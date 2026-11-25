#!/usr/bin/env python3
"""Rewrites version references after a release, for the Sync workflow.

Reads RELEASE_VERSION and NEXT_SNAPSHOT from the environment and updates
pom.xml, README.md, docs/antora.yml, and any Antora page that carries the
same "Latest release" / "Latest snapshot" dependency snippets as README.md.

CHANGELOG.md is intentionally left untouched: its release section and fresh
"[Unreleased]" heading are written before the tag is published, and arrive on
develop via the merge that precedes this script, not by bumping a version
string.
"""
import os
import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]


def replace_status_table_rows(text, release_version, next_snapshot):
    """Updates "| Latest release | x |" and "| Current development version | x |" table rows."""
    text = re.sub(r"(?mi)^(\|\s*Latest release\s*\|\s*)[^|\s]+", rf"\g<1>{release_version}", text)
    text = re.sub(
        r"(?mi)^(\|\s*Current development version\s*\|\s*)[^|\s]+", rf"\g<1>{next_snapshot}", text
    )
    return text


def replace_dependency_snippets(text, release_version, next_snapshot):
    """Replaces the <version> tag following a "Latest release"/"Latest snapshot" marker line."""
    lines = text.splitlines(keepends=True)
    pending = None
    for i, line in enumerate(lines):
        lower = line.strip().lower()
        if lower.startswith("latest release"):
            pending = release_version
        elif lower.startswith("latest snapshot"):
            pending = next_snapshot
        elif pending and "<version>" in line:
            lines[i] = re.sub(r"(<version>)[^<]*(</version>)", rf"\g<1>{pending}\g<2>", line)
            pending = None
    return "".join(lines)


def _project_version_span(text):
    """Returns the (start, end) span of the project's own <version> value in pom.xml.

    That is the first <version> that is a direct child of <project>: the <parent> block is
    blanked out first, and the search stops at the first dependencies/build/properties/etc.
    section, so a dependency's or plugin's <version> can never match.
    """
    search = text
    parent = re.search(r"<parent>.*?</parent>", text, flags=re.DOTALL)
    if parent:
        s, e = parent.span()
        search = text[:s] + " " * (e - s) + text[e:]
    boundary = re.search(
        r"<(dependencies|dependencyManagement|build|profiles|modules|properties)>", search
    )
    scope = search[: boundary.start()] if boundary else search
    m = re.search(r"<version>([^<]*)</version>", scope)
    if not m:
        sys.exit("pom.xml: could not find the project's own <version> element to bump")
    return m.span(1)


def read_project_version():
    text = (REPO_ROOT / "pom.xml").read_text()
    s, e = _project_version_span(text)
    return text[s:e]


def update_pom(next_snapshot):
    path = REPO_ROOT / "pom.xml"
    text = path.read_text()
    s, e = _project_version_span(text)
    path.write_text(text[:s] + next_snapshot + text[e:])


def _version_key(version):
    """Sort key for X.Y.Z[-SNAPSHOT]; a snapshot sorts below its release."""
    base, _, suffix = version.partition("-")
    return tuple(int(p) for p in base.split(".")) + (0 if suffix else 1,)


def choose_next_snapshot(candidate, develop_version):
    """Never move develop backwards: keep develop's snapshot if it's already ahead.

    E.g. a 1.7.1 patch release computes 1.8.0-SNAPSHOT, but if develop is already on
    1.9.0-SNAPSHOT that is kept.
    """
    if develop_version and develop_version.endswith("-SNAPSHOT"):
        try:
            if _version_key(develop_version) > _version_key(candidate):
                return develop_version
        except ValueError:
            pass
    return candidate


def update_readme(release_version, next_snapshot):
    # README.md here uses "Latest release:" / "Latest snapshot:" headers followed by a
    # fenced dependency block, not a markdown table -- replace_dependency_snippets already
    # matches on the marker line + following <version> tag regardless of table vs. plain text.
    path = REPO_ROOT / "README.md"
    text = path.read_text()
    text = replace_dependency_snippets(text, release_version, next_snapshot)
    text = replace_status_table_rows(text, release_version, next_snapshot)
    path.write_text(text)


def update_antora_component_version(release_version):
    path = REPO_ROOT / "docs" / "antora.yml"
    if not path.exists():
        return
    text = path.read_text()
    updated, count = re.subn(r"(?m)^version:.*$", f"version: {release_version}", text, count=1)
    if count == 1:
        path.write_text(updated)


def update_antora_pages(release_version, next_snapshot):
    pages_dir = REPO_ROOT / "docs" / "modules" / "ROOT" / "pages"
    if not pages_dir.exists():
        return
    for path in sorted(pages_dir.glob("*.adoc")):
        text = path.read_text()
        if "<version>" not in text:
            continue
        updated = replace_dependency_snippets(text, release_version, next_snapshot)
        if updated != text:
            path.write_text(updated)


def main():
    release_version = os.environ["RELEASE_VERSION"]
    # DEVELOP_VERSION is develop's own version read before the release was merged in; the
    # merge itself replaces pom.xml's version with the release version.
    next_snapshot = choose_next_snapshot(
        os.environ["NEXT_SNAPSHOT"], os.environ.get("DEVELOP_VERSION", "")
    )

    update_pom(next_snapshot)
    update_readme(release_version, next_snapshot)
    update_antora_component_version(release_version)
    update_antora_pages(release_version, next_snapshot)

    print(f"Release {release_version}; development version set to {next_snapshot}")
    output = os.environ.get("GITHUB_OUTPUT")
    if output:
        with open(output, "a") as f:
            f.write(f"next_snapshot={next_snapshot}\n")


if __name__ == "__main__":
    main()
