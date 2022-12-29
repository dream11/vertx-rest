#!/usr/bin/env bash
set -e

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "PROJECT_VERSION: ${PROJECT_VERSION}"
if [[ ${PROJECT_VERSION} == *-SNAPSHOT ]]; then
  exit 0
else
  echo "Error: project version must be suffixed with '-SNAPSHOT'"
  exit 1
fi
