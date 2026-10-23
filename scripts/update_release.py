#!/usr/bin/env python3
"""Copia un APK firmado a docs/downloads y actualiza docs/release.json.

Uso:
  python3 scripts/update_release.py --apk app/build/outputs/apk/release/app-release.apk \
      --version 1.1.0 --version-code 2

El SHA-256 y el tamaño se calculan del archivo real. El certificado de firma se
conserva del release.json existente salvo que se pase --cert-sha256.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
import shutil
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DOCS = ROOT / "docs"
DOWNLOAD_DIR = DOCS / "downloads"
RELEASE_FILE = DOCS / "release.json"
VERSION_RE = re.compile(r"^\d+\.\d+(?:\.\d+)?$")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", required=True, type=Path)
    parser.add_argument("--version", required=True)
    parser.add_argument("--version-code", required=True, type=int)
    parser.add_argument("--published-at", default=date.today().isoformat())
    parser.add_argument("--cert-sha256", default=None)
    parser.add_argument("--changelog", action="append", default=None)
    parser.add_argument(
        "--update-html",
        action="store_true",
        help="actualiza los valores estáticos de las páginas (fallback sin JS)",
    )
    return parser.parse_args()


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def format_spanish_date(iso: str) -> str:
    months = [
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
    ]
    try:
        year, month, day = (int(part) for part in iso.split("-"))
        return f"{day} de {months[month - 1]} de {year}"
    except (ValueError, IndexError):
        return iso


def update_static_html(release: dict) -> None:
    """Mantiene el fallback sin JavaScript alineado con release.json."""
    url = release["url"]
    size = f'{release["sizeBytes"] / 1000 / 1000:.1f}'.replace(".", ",") + " MB"
    spans = {
        "version": f'v{release["version"]}',
        "version-plain": release["version"],
        "code": str(release["versionCode"]),
        "size": size,
        "android": f'{release["minAndroid"]}+',
        "date": format_spanish_date(release["publishedAt"]),
        "sha": release["sha256"],
        "fingerprint": release.get("signingCertificateSha256", ""),
        "file": release["fileName"],
    }
    for path in DOCS.glob("*.html"):
        text = path.read_text(encoding="utf-8")
        updated = re.sub(r'href="\./downloads/fit-track-[^"]+\.apk"', f'href="{url}"', text)
        updated = re.sub(
            r'href="\./downloads/fit-track-[^"]+\.apk\.sha256"',
            f'href="{url}.sha256"',
            updated,
        )
        updated = re.sub(
            r'download="fit-track-[^"]+\.apk"',
            f'download="{release["fileName"]}"',
            updated,
        )
        for key, value in spans.items():
            updated = re.sub(
                rf'(data-release-{re.escape(key)}(?![-a-z])[^>]*>)[^<]*',
                rf'\g<1>{value}',
                updated,
            )
        if updated != text:
            path.write_text(updated, encoding="utf-8")


def main() -> None:
    args = parse_args()
    if not VERSION_RE.match(args.version):
        raise SystemExit(f"Versión no válida: {args.version!r}")
    if not args.apk.is_file():
        raise SystemExit(f"No existe el APK: {args.apk}")

    DOWNLOAD_DIR.mkdir(parents=True, exist_ok=True)
    file_name = f"fit-track-{args.version}.apk"
    destination = DOWNLOAD_DIR / file_name
    if args.apk.resolve() != destination.resolve():
        shutil.copy2(args.apk, destination)

    checksum = sha256(destination)
    size = destination.stat().st_size
    checksum_file = destination.with_suffix(destination.suffix + ".sha256")
    checksum_file.write_text(f"{checksum}  {file_name}\n", encoding="utf-8")

    existing = {}
    if RELEASE_FILE.exists():
        existing = json.loads(RELEASE_FILE.read_text(encoding="utf-8"))

    changelog = args.changelog
    if not changelog and existing.get("version") != args.version:
        changelog = [
            f'Versión {args.version} firmada y publicada el {format_spanish_date(args.published_at)}.',
            "Consulta las notas de la release en GitHub para el detalle de cambios.",
        ]

    release = {
        "app": "Fit Track",
        "packageName": existing.get("packageName", "com.pausiar.fit"),
        "version": args.version,
        "versionCode": args.version_code,
        "publishedAt": args.published_at,
        "fileName": file_name,
        "url": f"./downloads/{file_name}",
        "sizeBytes": size,
        "sha256": checksum,
        "minSdk": existing.get("minSdk", 26),
        "targetSdk": existing.get("targetSdk", 34),
        "minAndroid": existing.get("minAndroid", "8.0"),
        "signingCertificateSha256": args.cert_sha256
        or existing.get("signingCertificateSha256"),
        "changelog": changelog or existing.get("changelog", []),
    }
    RELEASE_FILE.write_text(json.dumps(release, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    if args.update_html:
        update_static_html(release)

    print(f"Publicado {destination.relative_to(ROOT)}")
    print(f"  versión: {args.version} (código {args.version_code})")
    print(f"  tamaño:  {size} bytes")
    print(f"  sha256:  {checksum}")
    print(f"  metadata: {RELEASE_FILE.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
