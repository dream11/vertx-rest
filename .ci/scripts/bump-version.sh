#!/usr/bin/env bash
set -e

release_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
mvn versions:set -DnextSnapshot=true
bump_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

git add pom.xml
git commit -m "chore: release version $release_version and bump version to $bump_version"
git push origin HEAD:master
