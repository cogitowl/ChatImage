#!/usr/bin/env python3
"""Print the checked-in build matrix; never infer compatibility from a version range."""
import argparse
import json
from pathlib import Path


def matrix(loader=None):
    data = json.loads(Path(__file__).with_name('targets.json').read_text())
    return {'include': [t for t in data['targets'] if loader is None or t['loader'] == loader]}


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--loader', choices=['fabric', 'forge', 'quilt', 'neoforge'])
    args = parser.parse_args()
    print(json.dumps(matrix(args.loader), separators=(',', ':')))
