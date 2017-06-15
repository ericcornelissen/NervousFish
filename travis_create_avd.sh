#!/usr/bin/env bash

# Creates and starts an emulator.
android-update-sdk --components=sys-img-armeabi-v7a-android-16 --accept-licenses='android-sdk-license-[0-9a-f]{8}'
echo no | android create avd --force -n test -t android-16 --abi armeabi-v7a --skin QVGA
emulator -avd test -no-audio -no-skin -netfast -no-window &

exit 0