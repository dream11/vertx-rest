#!/usr/bin/env bash

release_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
mvn versions:set -DnextSnapshot=true
bump_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

git config user.name github-actions
git config user.email github-actions@github.com

git add pom.xml
git commit -m "chore: release version $release_version and bump version to $bump_version"
git push origin HEAD:master