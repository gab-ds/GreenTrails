BACKEND := backend
FRONTEND := frontend

COV_REPORT := $(BACKEND)/target/site/jacoco/index.html
MUT_REPORT := $(BACKEND)/target/pit-reports/index.html

OPENJML_PROFILE ?= ricerca

# JMH
JMH_INCLUDE ?= it.greentrails.backend.benchmarks.*
JMH_FORKS ?= 1
JMH_WI ?= 3
JMH_W ?= 1s
JMH_I ?= 5
JMH_R ?= 1s
JMH_ARGS ?= $(JMH_INCLUDE) -f $(JMH_FORKS) -wi $(JMH_WI) -w $(JMH_W) -i $(JMH_I) -r $(JMH_R) -rf json -rff target/jmh-results.json
AMBER_JAR := third_party/AMBER/jmh-core-1.37-all.jar
AMBER_MODEL ?= oscnn
AMBER_HOST ?= localhost
AMBER_PORT ?= 50001
AMBER_ARGS ?= -hmodel $(AMBER_MODEL) -host $(AMBER_HOST) -hport $(AMBER_PORT)

.PHONY: test backend-test frontend-test coverage mutation verify checkstyle openjml
.PHONY: benchmark-jar benchmark benchmark-amber-setup benchmark-amber benchmark-service

default: test

test: backend-test frontend-test

backend-test:
	cd $(BACKEND) && ./mvnw test

frontend-test:
	cd $(FRONTEND) && bun run test

coverage:
	cd $(BACKEND) && ./mvnw verify
	@echo "Coverage report: $(COV_REPORT)"

mutation:
	cd $(BACKEND) && ./mvnw pitest:mutationCoverage
	@echo "Mutation report: $(MUT_REPORT)"

verify:
	cd $(BACKEND) && ./mvnw verify

checkstyle:
	cd $(BACKEND) && ./mvnw checkstyle:check

openjml:
	cd $(BACKEND) && ./mvnw verify -P openjml-$(OPENJML_PROFILE)

# ------------------------------------------------------------------
# Benchmarks (JMH)
# ------------------------------------------------------------------
benchmark-jar:
	@echo "Building benchmarks.jar (JMH vanilla $(shell sed -n 's/.*<jmh-core.version>\\(.*\\)<\\/jmh-core.version>/\\1/p' $(BACKEND)/pom.xml | head -1)) ..."
	cd $(BACKEND) && ./mvnw -Pbenchmark package -DskipTests

benchmark: benchmark-jar
	cd $(BACKEND) && java -jar target/benchmarks.jar $(JMH_ARGS)

benchmark-service:
	@echo "Starting jpt_service (AMBER dynamic halt, $(AMBER_MODEL)) ..."
	@docker rm -f jpt-service >/dev/null 2>&1 || true
	cd third_party/AMBER/jpt_service && docker build -t jpt-service . >/dev/null
	docker run -d --rm -p $(AMBER_PORT):5001 --name jpt-service jpt-service
	@echo "jpt_service running on port $(AMBER_PORT) (docker logs -f jpt-service)"

benchmark-amber-setup:
	@if [ ! -f "$(AMBER_JAR)" ]; then \
		echo "AMBER jar not found — run 'git submodule update --init --recursive'"; \
		exit 1; \
	fi
	mvn install:install-file -Dfile=$(AMBER_JAR) \
		-DgroupId=org.openjdk.jmh -DartifactId=jmh-core \
		-Dversion=1.37-extended -Dpackaging=jar

benchmark-amber: benchmark-amber-setup
	cd $(BACKEND) && ./mvnw -Pbenchmark,amber package -DskipTests
	cd $(BACKEND) && java -jar target/benchmarks.jar $(JMH_ARGS) $(AMBER_ARGS)
