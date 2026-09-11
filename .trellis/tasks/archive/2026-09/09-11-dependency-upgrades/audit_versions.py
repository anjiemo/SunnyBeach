import concurrent.futures
import json
from pathlib import Path
import re
import tomllib
import urllib.request
import xml.etree.ElementTree as ET

ROOT = next(parent for parent in Path(__file__).resolve().parents
            if (parent / 'gradle/libs.versions.toml').is_file())
CATALOG = tomllib.loads((ROOT / 'gradle/libs.versions.toml').read_text(encoding='utf-8'))


def audit(item):
    key, coordinate = item
    group, artifact = coordinate.split(':')
    current = CATALOG['versions'][key]
    if group.startswith('com.github.') and group != 'com.github.bumptech.glide':
        repos = ['https://jitpack.io']
    elif group.startswith(('androidx.', 'com.android.', 'com.google.android.material')):
        repos = ['https://dl.google.com/dl/android/maven2', 'https://maven.aliyun.com/repository/google']
    else:
        repos = ['https://repo.maven.apache.org/maven2', 'https://maven.aliyun.com/repository/public']
    errors = []
    for repo in repos:
        url = f'{repo}/{group.replace(".", "/")}/{artifact}/maven-metadata.xml'
        try:
            request = urllib.request.Request(url, headers={'User-Agent': 'SunnyBeach-dependency-audit'})
            with urllib.request.urlopen(request, timeout=20) as response:
                root = ET.fromstring(response.read())
            versions = [v.text for v in root.findall('./versioning/versions/version') if v.text]
            stable = [v for v in versions if re.fullmatch(r'v?\d+(?:\.\d+)*', v)]
            latest = max(stable, key=lambda v: tuple(map(int, v.lstrip('v').split('.'))), default=None)
            return dict(key=key, coordinate=coordinate, current=current, stable=latest,
                        current_published=current in versions, recent=versions[-5:], source=url)
        except Exception as error:
            errors.append(f'{url}: {error}')
    return dict(key=key, coordinate=coordinate, current=current, errors=errors)


if __name__ == '__main__':
    coordinates = {}
    for library in CATALOG['libraries'].values():
        ref = library.get('version', {}).get('ref')
        if ref and ref not in coordinates:
            coordinates[ref] = library.get('module') or f'{library["group"]}:{library["name"]}'
    with concurrent.futures.ThreadPoolExecutor(max_workers=12) as pool:
        results = list(pool.map(audit, coordinates.items()))
    output = Path(__file__).with_name('versions-audit.json')
    output.write_text(json.dumps(results, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    for result in results:
        print(json.dumps(result, ensure_ascii=False))
