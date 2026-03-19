#!/usr/bin/env bash
set -e

workspace_env_file="/workspaces/ProfPracInIT/.env"
bashrc_file="$HOME/.bashrc"
marker_begin="# >>> ProfPracInIT env >>>"
marker_end="# <<< ProfPracInIT env <<<"

if [ ! -f "$workspace_env_file" ]; then
  exit 0
fi

if grep -Fq "$marker_begin" "$bashrc_file"; then
  exit 0
fi

cat >> "$bashrc_file" <<EOF
$marker_begin
if [ -f "$workspace_env_file" ]; then
  set -a
  . "$workspace_env_file"
  set +a
fi
$marker_end
EOF
