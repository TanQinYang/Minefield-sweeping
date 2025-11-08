#!/usr/bin/env bash
# Optional: format your Java files if google-java-format is available
set -euo pipefail
if ! command -v google-java-format >/dev/null 2>&1; then
  echo "google-java-format not found. Install it or remove this step."
  exit 0
fi
find . -maxdepth 1 -name '*.java' -print0 | xargs -0 google-java-format -i
echo "Formatted."
