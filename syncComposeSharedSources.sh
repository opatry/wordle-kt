#!/usr/bin/env bash

set -euo pipefail

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

# TODO would be better to symlink or use Gradle top copy as generated sources

src_dir="$origin/wordle-compose-desktop/src/main"
output_dir="$origin/wordle-compose-android/src/main"
ui_package_dir="net/opatry/game/wordle/ui"
data_package_dir="net/opatry/game/wordle/data"

mkdir -p "$output_dir/java/$ui_package_dir"
mkdir -p "$output_dir/java/$data_package_dir"
mkdir -p "$output_dir/res/drawable"

cp -R "$src_dir/java/$ui_package_dir"/* "$output_dir/java/$ui_package_dir"
cp -R "$src_dir/java/$data_package_dir"/* "$output_dir/java/$data_package_dir"
cp "$src_dir/java/net/opatry/game/wordle/WordleStats.kt" "$output_dir/java/net/opatry/game/wordle"
rm -f "$output_dir/java/$ui_package_dir/compose/wordleComposeDesktop.kt"

cp "$src_dir/resources/"ic_*.xml "$output_dir/res/drawable"
