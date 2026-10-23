#!/usr/bin/env python3
"""Comprobaciones estáticas de la web de Fit Track (sin dependencias)."""
from __future__ import annotations

import hashlib
import json
import re
import sys
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit

ROOT = Path(__file__).resolve().parent.parent
DOCS = ROOT / "docs"
EXTERNAL_SCHEMES = {"http", "https", "mailto", "tel", "data", "javascript"}


class AuditParser(HTMLParser):
    def __init__(self, path: Path):
        super().__init__(convert_charrefs=True)
        self.path = path
        self.links: list[str] = []
        self.resources: list[tuple[str, str]] = []
        self.inline_styles: list[str] = []
        self.inline_scripts = 0
        self.has_csp = False
        self.title = ""
        self.description = ""
        self._in_title = False
        self._in_style = False
        self._in_script = False
        self._script_src = False

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        values = {key: (value or "") for key, value in attrs}
        if "style" in values:
            self.inline_styles.append(f"{tag} style attribute")
        if tag == "title":
            self._in_title = True
        elif tag == "style":
            self._in_style = True
            self.inline_styles.append("<style> block")
        elif tag == "script":
            self._in_script = True
            self._script_src = bool(values.get("src"))
            if not self._script_src:
                self.inline_scripts += 1
                self.inline_styles.append("inline <script>")
        elif tag == "meta":
            if values.get("http-equiv", "").lower() == "content-security-policy":
                self.has_csp = True
            if values.get("name") == "description":
                self.description = values.get("content", "")
        elif tag == "link":
            rel = values.get("rel", "").lower()
            if values.get("href") and rel in {
                "stylesheet",
                "icon",
                "apple-touch-icon",
                "manifest",
                "preload",
                "modulepreload",
            }:
                self.resources.append(("link", values["href"]))
        elif tag == "img":
            if values.get("src"):
                self.resources.append(("img", values["src"]))
        elif tag == "source":
            if values.get("src"):
                self.resources.append(("source", values["src"]))
        elif tag == "a":
            if values.get("href"):
                self.links.append(values["href"])
        if tag == "script" and values.get("src"):
            self.resources.append(("script", values["src"]))

    def handle_endtag(self, tag: str) -> None:
        if tag == "title":
            self._in_title = False
        elif tag == "style":
            self._in_style = False
        elif tag == "script":
            self._in_script = False
            self._script_src = False

    def handle_data(self, data: str) -> None:
        if self._in_title:
            self.title += data.strip()


def local_target(path: Path, href: str) -> Path | None:
    parts = urlsplit(href)
    if parts.scheme.lower() in EXTERNAL_SCHEMES or href.startswith("#") or href.startswith("//"):
        return None
    raw = unquote(parts.path)
    if not raw:
        return None
    candidate = (path.parent / raw).resolve()
    try:
        candidate.relative_to(DOCS.resolve())
    except ValueError:
        return candidate
    return candidate


def main() -> int:
    errors: list[str] = []
    warnings: list[str] = []
    html_files = sorted(DOCS.rglob("*.html"))
    if not html_files:
        errors.append("No se encontraron páginas HTML en docs/")

    for path in html_files:
        parser = AuditParser(path)
        parser.feed(path.read_text(encoding="utf-8"))
        rel = path.relative_to(ROOT)
        if not parser.title:
            errors.append(f"{rel}: falta <title>")
        if not parser.description:
            errors.append(f"{rel}: falta meta description")
        if not parser.has_csp:
            errors.append(f"{rel}: falta Content-Security-Policy")
        if parser.inline_styles:
            errors.append(f"{rel}: estilos/scripts inline bloqueados por CSP: {parser.inline_styles}")
        if "[COMPLETAR" in path.read_text(encoding="utf-8"):
            errors.append(f"{rel}: quedan placeholders [COMPLETAR]")
        for href in parser.links:
            target = local_target(path, href)
            if target and not target.exists():
                errors.append(f"{rel}: enlace roto {href}")
        for kind, src in parser.resources:
            if urlsplit(src).scheme.lower() in {"http", "https"} or src.startswith("//"):
                errors.append(f"{rel}: recurso externo ({kind}) {src}; la CSP lo bloqueará")
                continue
            target = local_target(path, src)
            if target and not target.exists():
                errors.append(f"{rel}: recurso roto ({kind}) {src}")
        if path.name == "404.html":
            continue

    release_path = DOCS / "release.json"
    try:
        release = json.loads(release_path.read_text(encoding="utf-8"))
    except Exception as exc:  # noqa: BLE001
        errors.append(f"docs/release.json no se puede leer: {exc}")
        release = {}

    if release:
        apk = DOCS / release.get("url", "").replace("./", "")
        if not apk.is_file():
            errors.append(f"release.json apunta a un APK inexistente: {apk}")
        else:
            data = apk.read_bytes()
            digest = hashlib.sha256(data).hexdigest()
            if digest != release.get("sha256"):
                errors.append("El SHA-256 de release.json no coincide con el APK")
            if len(data) != release.get("sizeBytes"):
                errors.append("El tamaño de release.json no coincide con el APK")
            checksum = apk.with_suffix(apk.suffix + ".sha256")
            if not checksum.is_file() or release["sha256"] not in checksum.read_text(encoding="utf-8"):
                errors.append("Falta o no coincide el archivo .sha256 del APK")
        for path in html_files:
            if 'data-download' not in path.read_text(encoding="utf-8"):
                continue
            text = path.read_text(encoding="utf-8")
            expected = release.get("url", "")
            for match in re.finditer(r'<a[^>]*data-download[^>]*href="([^"]+)"', text):
                if match.group(1) != expected:
                    warnings.append(
                        f"{path.relative_to(ROOT)}: el enlace de descarga estático no coincide con release.json"
                    )
            for match in re.finditer(r'<a[^>]*href="([^"]+)"[^>]*data-download', text):
                if match.group(1) != expected:
                    warnings.append(
                        f"{path.relative_to(ROOT)}: el enlace de descarga estático no coincide con release.json"
                    )

    sitemap = DOCS / "sitemap.xml"
    if not sitemap.is_file():
        errors.append("Falta docs/sitemap.xml")

    print(f"Páginas revisadas: {len(html_files)}")
    for warning in warnings:
        print(f"AVISO  {warning}")
    for error in errors:
        print(f"ERROR  {error}")
    if errors:
        return 1
    print("OK: enlaces, metadatos, CSP y descarga verificados.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
