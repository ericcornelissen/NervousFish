#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "build" ]; then
    ./gradlew build
elif [ "$COMPONENT" == "coverage" ]; then
    android-wait-for-emulator
    ./gradlew jacocoTestReport coveralls
elif [ "$COMPONENT" == "quality" ]; then
    ./gradlew checkstyle pmd lint findbugs
elif [ "$COMPONENT" == "unittest" ]; then
    ./gradlew testDebugUnitTest testReleaseUnitTest
else
    echo "Unknown component"
    exit 1
fi