#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "quality" ]; then
    ./gradlew checkstyle pmd findbugs lint
elif [ "$COMPONENT" == "build" ]; then
    ./gradlew build
else
    echo "Unknown module"
    exit 1
fi