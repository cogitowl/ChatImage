#!/usr/bin/env python3
"""Stage installable jars only, excluding named development and smoke jars."""
import argparse
import hashlib
import json
import shutil
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def collect(target, output):
    project = ROOT / target['path']
    loader = target['loader']
    if target['path'].startswith('modern/'):
        folder = project / 'build/libs'
        suffix = f"+{target['target']}+{loader}"
        if loader == 'forge' and target['target'].startswith('1.'):
            suffix += '-srg'
    else:
        folder = ROOT / 'ChatImage-jar' / (ROOT / 'version.txt').read_text().strip()
        suffix = f"+{target['target']}+{loader}"
        # The upstream NeoForge 1.21 target is named 1.21.0 in its build properties.
    candidates = [p for p in folder.glob('ChatImage-*' + suffix + '.jar') if not p.name.startswith('ChatImage-smoke-')]
    if len(candidates) != 1:
        raise RuntimeError(f"Expected one installable jar for {loader} {target['target']}: {candidates}")
    source = candidates[0]
    metadata = 'fabric.mod.json' if loader in ('fabric', 'quilt') else ('META-INF/neoforge.mods.toml' if loader == 'neoforge' else 'META-INF/mods.toml')
    with zipfile.ZipFile(source) as archive:
        names = set(archive.namelist())
        required = {metadata, 'io/github/kituin/chatimage/ChatImage.class'}
        if not required <= names:
            raise RuntimeError(f'Incomplete release jar {source.name}: missing {required - names}')
        if any(n.startswith('io/github/cogitowl/chatimage/') for n in names):
            raise RuntimeError('Smoke code must not be included in a release jar')
    output.mkdir(parents=True, exist_ok=True)
    destination = output / source.name
    shutil.copy2(source, destination)
    digest = hashlib.sha256(destination.read_bytes()).hexdigest()
    destination.with_suffix('.jar.sha256').write_text(f'{digest}  {destination.name}\n')
    return destination

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--loader', required=True)
    parser.add_argument('--target', required=True)
    parser.add_argument('--output', type=Path, default=ROOT / 'dist')
    args = parser.parse_args()
    targets = json.loads((ROOT / 'maintenance/targets.json').read_text())['targets']
    target = next(t for t in targets if t['loader'] == args.loader and t['target'] == args.target)
    print(collect(target, args.output))
