#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "build" ]; then
    ./gradlew build
elif [ "$COMPONENT" == "coverage" ]; then
    ./gradlew jacocoTestReport coveralls
elif [ "$COMPONENT" == "quality" ]; then
    ./gradlew checkstyle pmd lint findbugs
elif [ "$COMPONENT" == "unittest" ]; then
    ./gradlew testDebugUnitTest testReleaseUnitTest
else
    echo "Unknown module"
    exit 1
fi