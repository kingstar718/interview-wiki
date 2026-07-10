#!/usr/bin/env bash
# Patch the graph plugin's inline script to decodeURIComponent in getFullSlugFromUrl.
# Without this, graph nodes with Chinese-character slugs (e.g. 知识点索引) fail to
# match contentIndex.json keys, resulting in an isolated center node.
set -euo pipefail

GRAPH_DIST=".quartz/plugins/graph/dist/index.js"

if [ ! -f "$GRAPH_DIST" ]; then
  echo "[patch-graph-plugin] $GRAPH_DIST not found, skipping"
  exit 0
fi

# The minified code transforms:  function u(){var a=we(),o=Nu()
# into:                        function u(){var a=decodeURIComponent(we()),o=Nu()
#
# we()  = getFullSlugFromUrl()  — returns percent-encoded browser pathname
# u()   = the slug-resolving closure that calls we()
# Nu()  = getBasePath()
if grep -q 'function u(){var a=decodeURIComponent(we())' "$GRAPH_DIST"; then
  echo "[patch-graph-plugin] Already patched"
else
  sed -i 's/function u(){var a=we(),o=Nu()/function u(){var a=decodeURIComponent(we()),o=Nu()/' "$GRAPH_DIST"
  echo "[patch-graph-plugin] Patched"
fi
