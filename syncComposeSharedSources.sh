#!/usr/bin/env bash

set -euo pipefail

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

# TODO would be better to symlink or use Gradle top copy as generated sources

src_dir="$origin/wordle-compose-desktop/src/main/java"
output_dir="$origin/wordle-compose-android/src/main/java"
ui_package_dir="net/opatry/game/wordle/ui"
data_package_dir="net/opatry/game/wordle/data"

mkdir -p "$output_dir/$ui_package_dir"
mkdir -p "$output_dir/$data_package_dir"

cp -R "$src_dir/$ui_package_dir"/* "$output_dir/$ui_package_dir"
cp -R "$src_dir/$data_package_dir"/* "$output_dir/$data_package_dir"
cp "$src_dir/net/opatry/game/wordle/WordleStats.kt" "$output_dir/net/opatry/game/wordle"
rm -f "$output_dir/$ui_package_dir/compose/wordleComposeDesktop.kt"
