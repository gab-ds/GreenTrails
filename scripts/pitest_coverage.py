#!/usr/bin/env python3
"""Helper CI/CD: legge il report PIT (mutations.xml) e stampa la mutation coverage.

Utilizzo:
    python3 scripts/pitest_coverage.py [path] [--markdownify] [--fail-below PCT]

Il path è posizionale; di default punta a backend/target/pit-reports/mutations.xml
risolto rispetto alla posizione di questo script (funziona da root, da backend/ e in CI).

Con --markdownify l'output è in Markdown (tabelle, emoji e sezioni collassabili),
pensato per gli step summary e i commenti PR; senza, viene stampata una lista in
testo semplice per l'uso locale da CLI.
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
        description="Stampa la mutation coverage (Pitest) a partire dal report XML."
    )
    parser.add_argument(
        "path",
        nargs="?",
        type=Path,
        default=DEFAULT_REPORT,
        help="Percorso del report mutations.xml (default: %(default)s)",
    )
    parser.add_argument(
        "--markdownify",
        action="store_true",
        help="Rende l'output in Markdown (tabelle, emoji e details); senza, testo semplice per CLI.",
    )
    parser.add_argument(
        "--fail-below",
        type=float,
        metavar="PCT",
        help="Esce con codice 2 se la mutation coverage è sotto PCT (gate CI).",
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


def status_lines(counts: Counter, markdown: bool) -> list[str]:
    entries = [
        (STATUS_LABELS.get(status, status), counts.get(status, 0))
        for status in STATUS_ORDER
        if counts.get(status)
    ]
    if markdown:
        lines = [
            "| Stato | Numero |",
            "| --- | --- |",
        ]
        for label, count in entries:
            lines.append(f"| {label} | {count} |")
        return lines
    return [f"{label}: {count}" for label, count in entries]


def survivors_lines(mutations: list[etree.Element], markdown: bool) -> list[str]:
    survivors = [m for m in mutations if m.get("status") == "SURVIVED"]
    if not survivors:
        return []
    rows = []
    for m in survivors:
        cls = m.findtext("mutatedClass") or "?"
        method = m.findtext("mutatedMethod") or "?"
        line = m.findtext("lineNumber") or "?"
        try:
            line_key = int(line)
        except ValueError:
            line_key = line
        rows.append((cls, line_key, line, f"{cls}::{method}"))
    rows.sort(key=lambda r: (r[0], r[1]))

    title = f"Mutazioni sopravvissute ({len(rows)}) — da controllare"
    if markdown:
        lines = [
            "<details>",
            f"<summary>:x: {title}</summary>",
            "",
            "| Classe :: Metodo | Riga |",
            "| --- | --- |",
        ]
        for _, _, line, label in rows:
            lines.append(f"| `{label}` | {line} |")
        lines.append("")
        lines.append("</details>")
        return lines
    lines = [title]
    for _, _, line, label in rows:
        lines.append(f"{label} riga {line}")
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

    markdown = args.markdownify
    icon = " :dart:" if markdown else ""
    if markdown:
        print(f"## Coverage mutazionale (Pitest){icon}")
    else:
        print("Coverage mutazionale (Pitest)")
    print()
    if markdown:
        coverage_line = (
            f"**Mutation coverage:** {fmt_pct(coverage)} "
            f"({detected} mutazioni rilevate su {viable} valide)"
        )
    else:
        coverage_line = (
            f"Mutation coverage: {fmt_pct(coverage)} "
            f"({detected} mutazioni rilevate su {viable} valide)"
        )
    if excluded:
        coverage_line += f" — escluse {excluded} non valide/non eseguite"
    print(coverage_line)
    print()
    print("\n".join(status_lines(counts, markdown)))
    survivors = survivors_lines(mutations, markdown)
    if survivors:
        print()
        print("\n".join(survivors))

    if args.fail_below is not None and coverage < args.fail_below:
        print(
            f"Errore: mutation coverage {coverage:.1f}% inferiore alla soglia "
            f"{args.fail_below:.1f}%.",
            file=sys.stderr,
        )
        sys.exit(2)


if __name__ == "__main__":
    main()
