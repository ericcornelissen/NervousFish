#!/usr/bin/env bash

set -e

# Starting emulators is very costly, only done for components that need it
if [ "$COMPONENT" == "androidtest" ] || [ "$COMPONENT" == "coveralls" ]; then
    ./gradle/scripts/travis_create_avd.sh &
fi

exit 0