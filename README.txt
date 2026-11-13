HitStats for Mod Host (untested: no javac/d8 was available when this was written)
1. Install JDK 17+ and the Android SDK (build-tools for d8, platforms for android.jar).
2. ./build.sh modhost-api.jar /path/to/android.jar
3. Put hitstats.jar in the java_mods folder, set "attackEvent": true in the Mod Host config, restart the game.
Output goes to mod_host.log. The settings menu shows "HitStats" if menuEnabled is on.

No computer? Build it on GitHub (works from a phone):
1. Make a new GitHub repo and upload everything in this folder, plus modhost-api.jar in the repo root (keep the .github folder).
2. Open the Actions tab, run "Build HitStats jar", then download the "hitstats-jar" artifact. Unzip it to get hitstats.jar.
