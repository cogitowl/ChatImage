#!/usr/bin/env python3
"""Bundle verified Fabric / Quilt targets and installation instructions."""
import argparse
import json
import tempfile
import zipfile
from pathlib import Path

from collect_artifacts import ROOT, collect


def package(output, group):
    data = json.loads((ROOT / 'maintenance/targets.json').read_text())
    targets = [t for t in data['targets'] if
               group == 'all' or
               (group == 'legacy' and t['path'].startswith('fabric/')) or
               (group == '26.2' and t['target'] == '26.2')]
    if not targets:
        raise ValueError('No release targets selected')
    if any(t['status'] not in ('build-verified', 'runtime-smoke-verified') for t in targets):
        raise ValueError('Every selected target must pass its build before packaging')
    output.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='chatimage-release-') as temporary:
        stage = Path(temporary)
        for target in targets:
            artifact = collect(target, stage)
            with zipfile.ZipFile(artifact) as jar:
                metadata = json.loads(jar.read('fabric.mod.json'))
            versions = metadata['depends']['minecraft']
            versions = versions if isinstance(versions, list) else [versions]
            # Upstream 1.19.2–1.19.4 used tilde predicates. The maintained
            # matrix deliberately lists only their exact build targets.
            versions = [version.removeprefix('~') for version in versions]
            if set(versions) != set(target['game_versions']):
                raise ValueError(f'Minecraft metadata disagrees with target list: {artifact.name}')
        selected = dict(data, targets=targets)
        (stage / 'targets.json').write_text(json.dumps(selected, indent=2) + '\n')
        (stage / '安装说明.md').write_text((ROOT / 'CONTINUED.md').read_text())
        (stage / 'LICENSE').write_text((ROOT / 'modern/LICENSE').read_text())
        with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as bundle:
            for entry in sorted(stage.iterdir()):
                bundle.write(entry, entry.name)
        with zipfile.ZipFile(output) as bundle:
            if bundle.testzip() is not None:
                raise RuntimeError('Release archive failed its integrity check')
    print(f'{len(targets)} installable jars: {output}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--group', choices=['all', 'legacy', '26.2'], default='all')
    args = parser.parse_args()
    package(args.output, args.group)
