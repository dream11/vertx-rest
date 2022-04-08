#!/usr/bin/env bash

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "project_version: ${PROJECT_VERSION}"
if [[ ${PROJECT_VERSION} == *-SNAPSHOT ]]; then
  exit 0
else
  echo "Error: project version did not end with '-SNAPSHOT'"
  exit 1
fi
