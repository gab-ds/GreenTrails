#!/usr/bin/env python3
"""Helper CI/CD: legge il report JaCoCo (jacoco.xml) e stampa il coverage.

Utilizzo:
    python3 scripts/jacoco_coverage.py [path] [--markdownify] [--fail-below PCT] [--metric METRIC]

Il path è posizionale; di default punta a backend/target/site/jacoco/jacoco.xml
risolto rispetto alla posizione di questo script (funziona da root, da backend/ e in CI).

Con --markdownify l'output è in Markdown (tabelle ed emoji), pensato per gli step
summary e i commenti PR; senza, viene stampata una lista in testo semplice per
l'uso locale da CLI.
"""

import argparse
import sys
import xml.etree.ElementTree as etree
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_REPORT = REPO_ROOT / "backend" / "target" / "site" / "jacoco" / "jacoco.xml"

METRIC_ORDER = [
    ("INSTRUCTION", "Istruzioni"),
    ("BRANCH", "Rami"),
    ("LINE", "Linee"),
    ("COMPLEXITY", "Complessità"),
    ("METHOD", "Metodi"),
    ("CLASS", "Classi"),
]

ICONS = {
    "INSTRUCTION": "gear",
    "BRANCH": "diamond_shape_with_a_dot_inside",
    "LINE": "chart_with_upwards_trend",
    "COMPLEXITY": "spiral_note_pad",
    "METHOD": "hammer",
    "CLASS": "open_file_folder",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Stampa il coverage JaCoCo a partire dal report XML."
    )
    parser.add_argument(
        "path",
        nargs="?",
        type=Path,
        default=DEFAULT_REPORT,
        help="Percorso del report jacoco.xml (default: %(default)s)",
    )
    parser.add_argument(
        "--markdownify",
        action="store_true",
        help="Rende l'output in Markdown (tabelle ed emoji); senza, testo semplice per CLI.",
    )
    parser.add_argument(
        "--fail-below",
        type=float,
        metavar="PCT",
        help="Esce con codice 2 se la coverage della metrica è sotto PCT (gate CI).",
    )
    parser.add_argument(
        "--metric",
        choices=[m for m, _ in METRIC_ORDER],
        default="LINE",
        help="Metrica verificata con --fail-below (default: LINE).",
    )
    return parser.parse_args()


def load_report(path: Path) -> etree.Element:
    try:
        tree = etree.parse(path)
    except FileNotFoundError:
        print(f"Errore: report non trovato: {path}", file=sys.stderr)
        sys.exit(1)
    except etree.ParseError as exc:
        print(f"Errore: report XML malformato ({path}): {exc}", file=sys.stderr)
        sys.exit(1)
    root = tree.getroot()
    if root.tag != "report":
        print(f"Errore: '{path}' non è un report JaCoCo valido.", file=sys.stderr)
        sys.exit(1)
    return root


def counters_by_type(element: etree.Element) -> dict[str, tuple[int, int]]:
    return {
        t: (
            int(counter.get("missed", 0)),
            int(counter.get("covered", 0)),
        )
        for counter in element.findall("counter")
        if (t := counter.get("type")) is not None
    }


def fmt_cell(missed: int, covered: int) -> str:
    total = missed + covered
    if total == 0:
        return f"({covered}/{total})"
    return f"{covered / total * 100:.1f}% ({covered}/{total})"


def report_header(title: str, markdown: bool) -> str:
    if not markdown:
        return title
    return f"## {title} :bar_chart:"


def project_lines(counters: dict[str, tuple[int, int]], markdown: bool) -> list[str]:
    if markdown:
        lines = [
            "| Metrica | Coperto | Mancato | Totale | Percentuale |",
            "| --- | --- | --- | --- | --- |",
        ]
        for metric, label in METRIC_ORDER:
            missed, covered = counters.get(metric, (0, 0))
            lines.append(
                f"| :{ICONS[metric]}: {label} | {covered} | {missed} | "
                f"{missed + covered} | {fmt_cell(missed, covered)} |"
            )
        return lines
    lines = []
    for metric, label in METRIC_ORDER:
        missed, covered = counters.get(metric, (0, 0))
        lines.append(f"{label}: {fmt_cell(missed, covered)}")
    return lines


def package_lines(root: etree.Element, markdown: bool) -> list[str]:
    packages = []
    for package in root.findall("package"):
        counters = counters_by_type(package)
        packages.append(
            (
                package.get("name"),
                counters.get("LINE", (0, 0)),
                counters.get("BRANCH", (0, 0)),
            )
        )
    packages.sort(key=lambda item: item[0])

    if markdown:
        lines = [
            "| Package | Linee | Rami |",
            "| --- | --- | --- |",
        ]
        for name, line, branch in packages:
            lines.append(f"| `{name}` | {fmt_cell(*line)} | {fmt_cell(*branch)} |")
        return lines
    lines = []
    for name, line, branch in packages:
        lines.append(f"{name}: Linee {fmt_cell(*line)}, Rami {fmt_cell(*branch)}")
    return lines


def enforce_gate(
    counters: dict[str, tuple[int, int]], metric: str, min_pct: float
) -> None:
    missed, covered = counters.get(metric, (0, 0))
    total = missed + covered
    if total == 0:
        print(f"Errore: nessun contatore '{metric}' nel report.", file=sys.stderr)
        sys.exit(1)
    pct = covered / total * 100
    label = dict(METRIC_ORDER)[metric]
    if pct < min_pct:
        print(
            f"Errore: coverage {label} {pct:.1f}% inferiore alla soglia {min_pct:.1f}%.",
            file=sys.stderr,
        )
        sys.exit(2)


def main() -> None:
    args = parse_args()
    root = load_report(args.path)
    counters = counters_by_type(root)

    print(report_header("Riepilogo Coverage JaCoCo", args.markdownify))
    print()
    print("\n".join(project_lines(counters, args.markdownify)))
    print()
    print(report_header("Coverage per Package", args.markdownify))
    print()
    print("\n".join(package_lines(root, args.markdownify)))

    if args.fail_below is not None:
        enforce_gate(counters, args.metric, args.fail_below)


if __name__ == "__main__":
    main()
