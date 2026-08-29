#!/bin/bash
#
# energy_measure.sh — Energy consumption measurement script for GreenTrails
#
# Run on the worker VM (192.168.2.10) after playbook execution.
# Measures system-wide energy via EnergiBridge (RAPL MSR passthrough).
#
# Usage: GT_BACKEND_JAR=/path/to/jar GT_WARMUP=180 ./energy_measure.sh
#
# Environment variables:
#   GT_BACKEND_JAR  — Path to Spring Boot JAR (default: /opt/greentrails/backend.jar)
#   GT_WARMUP       — Warm-up time in seconds (default: 180)
#   GT_MEASURE      — Measurement time in seconds (default: 300)
#   GT_COOLDOWN     — Cooldown time in seconds (default: 180)
#   GT_REPEATS      — Number of measurement repetitions (default: 3)
#   GT_RESULTS_DIR  — Output directory for CSVs (default: /tmp/energy_results/<run-id>)
#   GT_RUN_ID       — Run identifier (default: UTC timestamp)
#   GT_DB_NAME      — MySQL database name (default: greentrails)
#   GT_DB_URL       — JDBC URL (default: jdbc:mysql://localhost:3306/greentrails)
#   GT_DB_USER      — MySQL user (default: greentrails)
#   GT_DB_PASS      — MySQL password (required)
#   GT_JMETER_HOST  — Hostname/IP exposed by the Proxmox port-forward (required for load tier)
#   GT_JMETER_PORT  — Exposed backend port for JMeter (default: 8080)
#   GT_JMETER_PLAN  — JMeter plan path on the JMeter host (default: load_test.jmx)
#   GT_JMETER_DURATION — Load-plan duration in seconds (default: full load tier)
#   GT_JMETER_RESULTFILE — Result path on the external JMeter host
#                          (default: load_test_<run-id>.csv)
#
set -euo pipefail

BACKEND_JAR="${GT_BACKEND_JAR:-/opt/greentrails/backend.jar}"
WARMUP="${GT_WARMUP:-180}"        # 3 minutes
MEASURE="${GT_MEASURE:-300}"      # 5 minutes
COOLDOWN="${GT_COOLDOWN:-180}"    # 3 minutes
REPEATS="${GT_REPEATS:-3}"
DB_NAME="${GT_DB_NAME:-greentrails}"
DB_URL="${GT_DB_URL:-jdbc:mysql://localhost:3306/${DB_NAME}}"
DB_USER="${GT_DB_USER:-greentrails}"
DB_PASS="${GT_DB_PASS:?GT_DB_PASS is required}"
MYSQL_SERVICE="${GT_MYSQL_SERVICE:-mysql}"
JMETER_HOST="${GT_JMETER_HOST:-}"
JMETER_PORT="${GT_JMETER_PORT:-8080}"
JMETER_PLAN="${GT_JMETER_PLAN:-load_test.jmx}"
JMETER_DURATION="${GT_JMETER_DURATION:-$((REPEATS * (WARMUP + MEASURE) + (REPEATS - 1) * COOLDOWN))}"
RUN_ID="${GT_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
JMETER_RESULTFILE="${GT_JMETER_RESULTFILE:-load_test_${RUN_ID}.csv}"
RESULTS_DIR="${GT_RESULTS_DIR:-/tmp/energy_results/${RUN_ID}}"
MYSQL_STARTED=0
BACKEND_PID=""

stop_backend() {
    if [ -n "$BACKEND_PID" ]; then
        kill "$BACKEND_PID" 2>/dev/null || true
        wait "$BACKEND_PID" 2>/dev/null || true
        BACKEND_PID=""
    fi
}

stop_mysql() {
    if [ "$MYSQL_STARTED" -eq 1 ]; then
        systemctl stop "$MYSQL_SERVICE"
        MYSQL_STARTED=0
    fi
}

cleanup() {
    exit_code=$?
    trap - EXIT INT TERM
    set +e
    stop_backend
    stop_mysql
    exit "$exit_code"
}

trap cleanup EXIT INT TERM

start_mysql() {
    systemctl start "$MYSQL_SERVICE"
    MYSQL_STARTED=1
    systemctl is-active --quiet "$MYSQL_SERVICE"
}

wait_for_backend() {
    for _ in $(seq 1 30); do
        if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            return 0
        fi
        sleep 2
    done
    return 1
}

start_backend() {
    SPRING_PROFILES_ACTIVE=dev SPRING_JPA_SHOW_SQL=false \
        DB_URL="$DB_URL" DB_USER="$DB_USER" DB_PASS="$DB_PASS" \
        java -jar "$BACKEND_JAR" &
    BACKEND_PID=$!
    if ! wait_for_backend; then
        echo "Backend did not become healthy at http://localhost:8080/actuator/health." >&2
        curl -sS http://localhost:8080/actuator/health >&2 || true
        return 1
    fi
}

mkdir -p "$RESULTS_DIR"

{
    echo "run_id=$RUN_ID"
    echo "started_at=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    echo "worker=$(hostname)"
    echo "backend_jar=$BACKEND_JAR"
    echo "backend_sha256=$(sha256sum "$BACKEND_JAR" | awk '{print $1}')"
    echo "db_name=$DB_NAME"
    echo "jmeter_host=$JMETER_HOST"
    echo "jmeter_port=$JMETER_PORT"
    echo "jmeter_plan=$JMETER_PLAN"
    echo "jmeter_duration=$JMETER_DURATION"
    echo "jmeter_resultfile=$JMETER_RESULTFILE"
    echo "warmup=$WARMUP"
    echo "measure=$MEASURE"
    echo "cooldown=$COOLDOWN"
    echo "repeats=$REPEATS"
} > "$RESULTS_DIR/metadata.txt"

echo "Ensuring MySQL is disabled outside energy measurement tiers..."
systemctl stop "$MYSQL_SERVICE"
systemctl disable "$MYSQL_SERVICE" > /dev/null 2>&1 || true

echo "=== GreenTrails Energy Measurement ==="
echo "Backend JAR: $BACKEND_JAR"
echo "Results directory: $RESULTS_DIR"
echo "Timing: ${WARMUP}s warm-up, ${MEASURE}s measure, ${COOLDOWN}s cooldown"
echo ""

# ─── Tier 1: Baseline (no app) ───────────────────────────────────────────────
echo "=== TIER 1: Baseline (no app running) ==="
for i in $(seq 1 "$REPEATS"); do
    echo "[baseline $i/$REPEATS] warm-up ${WARMUP}s..."
    sleep "$WARMUP"
    echo "[baseline $i/$REPEATS] measuring ${MEASURE}s..."
    energibridge --summary -i 200 -o "${RESULTS_DIR}/energy_baseline_${i}.csv" -- sleep "$MEASURE"
    if [ "$i" -lt "$REPEATS" ]; then
        echo "[baseline] cooldown ${COOLDOWN}s..."
        sleep "$COOLDOWN"
    fi
done
echo ""

# ─── Tier 2: Backend idle (started, no requests) ─────────────────────────────
echo "=== TIER 2: Backend idle ==="
echo "[idle] Starting MySQL for energy measurement..."
start_mysql
echo "[idle] Waiting for backend to start..."
start_backend
echo "[idle] Backend is ready."

for i in $(seq 1 "$REPEATS"); do
    echo "[idle $i/$REPEATS] warm-up ${WARMUP}s..."
    sleep "$WARMUP"
    echo "[idle $i/$REPEATS] measuring ${MEASURE}s..."
    energibridge --summary -i 200 -o "${RESULTS_DIR}/energy_idle_${i}.csv" -- sleep "$MEASURE"
    if [ "$i" -lt "$REPEATS" ]; then
        echo "[idle] cooldown ${COOLDOWN}s..."
        sleep "$COOLDOWN"
    fi
done

stop_backend
echo ""

# ─── Tier 3: Backend under JMeter load ───────────────────────────────────────
echo "=== TIER 3: Backend under JMeter load ==="
echo "[load] Waiting for backend to start..."
start_backend
echo "[load] Backend is ready."

if [ -z "$JMETER_HOST" ]; then
    echo "GT_JMETER_HOST must identify the Proxmox port-forward address." >&2
    exit 1
fi

echo ">>> Start JMeter on the external load-generator host with:"
echo ">>>   jmeter -n -t $JMETER_PLAN -Jhost=$JMETER_HOST -Jport=$JMETER_PORT -Jduration=$JMETER_DURATION -Jresultfile=$JMETER_RESULTFILE"
echo ">>> Keep the result file on the JMeter host; it is not stored on this worker automatically."
echo ">>> Press ENTER after JMeter has started..."
read -r

for i in $(seq 1 "$REPEATS"); do
    echo "[load $i/$REPEATS] warm-up ${WARMUP}s..."
    sleep "$WARMUP"
    echo "[load $i/$REPEATS] measuring ${MEASURE}s..."
    energibridge --summary -i 200 -o "${RESULTS_DIR}/energy_load_${i}.csv" -- sleep "$MEASURE"
    if [ "$i" -lt "$REPEATS" ]; then
        echo "[load] cooldown ${COOLDOWN}s..."
        sleep "$COOLDOWN"
    fi
done

stop_backend
stop_mysql
echo ""

echo "=== Measurement complete ==="
echo "Results saved to: $RESULTS_DIR/"
ls -la "$RESULTS_DIR/"
echo ""
echo "Copy results to your machine:"
echo "  scp -r root@$(hostname -I | awk '{print $1}'):${RESULTS_DIR} ./results/"
