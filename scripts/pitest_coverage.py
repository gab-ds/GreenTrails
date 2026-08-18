#!/usr/bin/env python3
"""Helper CI/CD: legge il report PIT (mutations.xml) e stampa la mutation coverage in markdown.

Utilizzo:
    python3 scripts/pitest_coverage.py [path] [--emojify]

Il path è posizionale; di default punta a backend/target/pit-reports/mutations.xml
risolto rispetto alla posizione di questo script (funziona da root, da backend/ e in CI).
"""

import argparse
import sys
import xml.etree.ElementTree as etree
from collections import Counter
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_REPORT = REPO_ROOT / "backend" / "target" / "pit-reports" / "mutations.xml"

STATUS_ORDER = [
    "KILLED",
    "SURVIVED",
    "NO_COVERAGE",
    "TIMED_OUT",
    "MEMORY_ERROR",
    "NON_VIABLE",
    "RUN_ERROR",
]

STATUS_LABELS = {
    "KILLED": "Uccise",
    "SURVIVED": "Sopravvissute",
    "NO_COVERAGE": "Senza copertura",
    "TIMED_OUT": "Timeout",
    "MEMORY_ERROR": "Errore di memoria",
    "NON_VIABLE": "Non vitali",
    "RUN_ERROR": "Errore di esecuzione",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Stampa la mutation coverage (Pitest) in markdown a partire dal report XML."
    )
    parser.add_argument(
        "path",
        nargs="?",
        type=Path,
        default=DEFAULT_REPORT,
        help="Percorso del report mutations.xml (default: %(default)s)",
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
    if root.tag != "mutations":
        print(f"Errore: '{path}' non è un report Pitest valido.", file=sys.stderr)
        sys.exit(1)
    return root


def fmt_pct(value: float) -> str:
    return f"{value:.1f}%"


def status_table(counts: Counter) -> list[str]:
    lines = [
        "| Stato | Numero |",
        "| --- | --- |",
    ]
    for status in STATUS_ORDER:
        count = counts.get(status, 0)
        if count:
            lines.append(f"| {STATUS_LABELS.get(status, status)} | {count} |")
    return lines


def survivors_table(mutations: list[etree.Element], emojify: bool) -> list[str]:
    survivors = [m for m in mutations if m.get("status") == "SURVIVED"]
    if not survivors:
        return []
    icon = " :white_check_mark:" if emojify else ""
    lines = [f"### Mutazioni sopravvissute{icon}", ""]
    if emojify:
        lines.append(":x: Da controllare:")
        lines.append("")
    lines.append("| Classe :: Metodo | Riga |")
    lines.append("| --- | --- |")
    for m in survivors:
        cls = m.findtext("mutatedClass") or "?"
        method = m.findtext("mutatedMethod") or "?"
        line = m.findtext("lineNumber") or "?"
        lines.append(f"| `{cls}::{method}` | {line} |")
    return lines


def main() -> None:
    args = parse_args()
    root = load_report(args.path)
    mutations = root.findall("mutation")

    counts = Counter(m.get("status") for m in mutations)
    detected = sum(1 for m in mutations if m.get("detected") == "true")
    excluded = counts.get("NON_VIABLE", 0) + counts.get("RUN_ERROR", 0)
    viable = len(mutations) - excluded
    coverage = detected / viable * 100 if viable else 0.0

    icon = " :dart:" if args.emojify else ""
    print(f"## Coverage mutazionale (Pitest){icon}")
    print()
    coverage_line = (
        f"**Mutation coverage:** {fmt_pct(coverage)} "
        f"({detected} mutazioni rilevate su {viable} valide)"
    )
    if excluded:
        coverage_line += f" — escluse {excluded} non valide/non eseguite"
    print(coverage_line)
    print()
    print("\n".join(status_table(counts)))
    survivors = survivors_table(mutations, args.emojify)
    if survivors:
        print()
        print("\n".join(survivors))


if __name__ == "__main__":
    main()
