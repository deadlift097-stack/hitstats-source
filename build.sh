#!/usr/bin/env bash
# Needs: JDK 17+ (javac, jar) and Android build-tools (d8).  Run from this folder.
# Usage: ./build.sh /path/to/modhost-api.jar /path/to/android.jar
set -e
API="${1:?path to modhost-api.jar}"
ANDROID="${2:?path to android.jar (Android SDK platforms/android-XX/android.jar)}"
rm -rf out && mkdir -p out/classes out/dex
javac --release 8 -cp "$API:$ANDROID" -d out/classes src/hitstats/*.java
d8 --release --lib "$ANDROID" --classpath "$API" --output out/dex $(find out/classes -name '*.class')
cp out/dex/classes.dex out/classes.dex
cp res/modhost-mod.properties out/
( cd out && jar cf ../hitstats.jar classes.dex modhost-mod.properties )
echo "Built hitstats.jar -> copy it into the game's java_mods folder"
