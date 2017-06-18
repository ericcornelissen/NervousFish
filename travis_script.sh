#!/usr/bin/env bash

set -e

# Wait for an emulator if needed
if [ "$COMPONENT" == "androidtest" ] || [ "$COMPONENT" == "coveralls" ]; then
    android-wait-for-emulator
fi

# Start correct process given the $COMPONENT
if [ "$COMPONENT" == "androidtest" ]; then
    ./gradlew connectedDebugAndroidTest
elif [ "$COMPONENT" == "build" ]; then
    ./gradlew build
elif [ "$COMPONENT" == "coveralls" ]; then
    ./gradlew jacocoTestReport coveralls
elif [ "$COMPONENT" == "quality" ]; then
    ./gradlew checkstyle pmd lint findbugs
elif [ "$COMPONENT" == "unittest" ]; then
    ./gradlew testDebugUnitTest testReleaseUnitTest
else
    echo "Unknown component"
    exit 1
fi