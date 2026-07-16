#!/usr/bin/env bash
# Ensure the graph plugin decodes the current-page slug before matching it against
# contentIndex.json keys. Without decoding, a Chinese-slug page's URL pathname stays
# percent-encoded (%E7%9F%A5...), never matches the decoded keys, and the sidebar
# (local) graph collapses to a single isolated node showing the encoded string.
# The expanded/global graph is unaffected (it renders the full index, not the URL).
#
# Target: the plugin's COMPILED inline browser script. quartz build bundles the graph
# client script from `dist/components/index.js` — verified by patching it and seeing
# `decodeURIComponent(window.location.pathname)` appear in public/static/scripts/.
# (The original script patched dist/index.js, which build never bundles, so it was a
# no-op — that's why the deployed graph stayed encoded. Do not repoint at src either:
# install-plugins compiles src->dist before this runs, so a src edit never reaches dist.)
#
# Anchor on `window.location.pathname` (a browser API literal that survives
# minification), not on minified variable names. Idempotent; fails loudly on drift.
set -euo pipefail

DIST=".quartz/plugins/graph/dist/components/index.js"

if [ ! -f "$DIST" ]; then
  echo "[patch-graph-plugin] $DIST not found — plugin not installed/built?" >&2
  exit 1
fi

SRC='=window.location.pathname'
DST='=decodeURIComponent(window.location.pathname)'

if grep -qF "$DST" "$DIST"; then
  echo "[patch-graph-plugin] Already patched"
  exit 0
fi

# The anchor must appear exactly once (inside the inlined getFullSlugFromUrl). If it's
# missing or duplicated, the plugin's shape changed — stop rather than ship broken.
count=$(grep -cF "$SRC" "$DIST" || true)
if [ "$count" != "1" ]; then
  echo "[patch-graph-plugin] Expected exactly 1 '$SRC', found $count — plugin changed, refusing to patch" >&2
  exit 1
fi

sed -i "s#${SRC}#${DST}#" "$DIST"

if ! grep -qF "$DST" "$DIST"; then
  echo "[patch-graph-plugin] Patch did not apply — aborting" >&2
  exit 1
fi
echo "[patch-graph-plugin] Patched dist/components/index.js to decode slug"
