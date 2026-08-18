#!/usr/bin/env python3
"""Helper CI/CD: legge il report JaCoCo (jacoco.xml) e stampa il coverage in markdown.

Utilizzo:
    python3 scripts/jacoco_coverage.py [path] [--emojify]

Il path è posizionale; di default punta a backend/target/site/jacoco/jacoco.xml
risolto rispetto alla posizione di questo script (funziona da root, da backend/ e in CI).
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
        description="Stampa il coverage JaCoCo in markdown a partire dal report XML."
    )
    parser.add_argument(
        "path",
        nargs="?",
        type=Path,
        default=DEFAULT_REPORT,
        help="Percorso del report jacoco.xml (default: %(default)s)",
    )
    parser.add_argument(
        "--emojify",
        action="store_true",
        help="Abilita le emoji GitHub-style nelle intestazioni e nelle etichette.",
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
        return f" ({covered}/{total})"
    return f"{covered / total * 100:.1f}% ({covered}/{total})"


def report_header(title: str, emojify: bool) -> str:
    icon = " :bar_chart:" if emojify else ""
    return f"## {title}{icon}"


def project_table(counters: dict[str, tuple[int, int]], emojify: bool) -> list[str]:
    lines = [
        "| Metrica | Coperto | Mancato | Totale | Percentuale |",
        "| --- | --- | --- | --- | --- |",
    ]
    for metric, label in METRIC_ORDER:
        missed, covered = counters.get(metric, (0, 0))
        icon = f"{ICONS[metric]} " if emojify else ""
        lines.append(
            f"| {icon}{label} | {covered} | {missed} | {missed + covered} | "
            f"{fmt_cell(missed, covered)} |"
        )
    return lines


def package_table(root: etree.Element) -> list[str]:
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

    lines = [
        "| Package | Linee | Rami |",
        "| --- | --- | --- |",
    ]
    for name, line, branch in packages:
        lines.append(f"| `{name}` | {fmt_cell(*line)} | {fmt_cell(*branch)} |")
    return lines


def main() -> None:
    args = parse_args()
    root = load_report(args.path)
    counters = counters_by_type(root)

    print(report_header("Riepilogo Coverage JaCoCo", args.emojify))
    print()
    print("\n".join(project_table(counters, args.emojify)))
    print()
    print(report_header("Coverage per Package", args.emojify))
    print()
    print("\n".join(package_table(root)))


if __name__ == "__main__":
    main()
