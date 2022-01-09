#!/usr/bin/env bash

set -euo pipefail

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

cd "$origin"

./gradlew wordle-compose-mosaic:installDist
./wordle-compose-mosaic/build/install/wordle-compose-mosaic/bin/wordle-compose-mosaic
