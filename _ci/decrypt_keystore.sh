#!/usr/bin/env bash

# Script to decrypt signing keystore
# see https://docs.github.com/en/actions/security-guides/encrypted-secrets

set -eu

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

tmp_dir=$(mktemp -d -t ci-secrets.XXXXXX)
mkdir -p "$tmp_dir"
output_file="${1:-"$tmp_dir/playstore.keystore"}"
# convert potentially relative path to absolute
output_file="$(cd "$(dirname "$output_file")"; pwd)/$(basename "$output_file")"

# --batch to prevent interactive command --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt \
    --passphrase="$PLAYSTORE_SECRET_PASSPHRASE" \
    --output "$output_file" "$origin/playstore.keystore.gpg"

# output so that caller can retrieve generated output when not provided explicitly
echo "$output_file"
