#!/usr/bin/env bash

# Creates and starts an emulator.
android-update-sdk --components=sys-img-armeabi-v7a-android-22 --accept-licenses='android-sdk-license-[0-9a-f]{8}'
echo no | android create avd --force -n test -t android-22 --abi armeabi-v7a
emulator -avd test -no-audio -no-skin -no-window &

exit 0