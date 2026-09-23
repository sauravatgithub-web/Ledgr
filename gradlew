#!/bin/sh

#
# Gradle start-up script for POSIX generated for Money Tracker.
# Open the project in Android Studio to sync; Studio can also regenerate the wrapper jar.
#

die () {
    echo
    echo "$*"
    echo
    exit 1
}

APP_HOME=$( cd "${0%/*}" && pwd -P ) || exit

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
    die "ERROR: gradle-wrapper.jar is missing.

Open this folder in Android Studio (File → Open → ~/projects/money-tracker).
Android Studio will download the Gradle wrapper on first sync.

Or install Gradle and run:  gradle wrapper --gradle-version 8.9"
fi

# Determine Java
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD=$JAVA_HOME/bin/java
else
    JAVACMD=java
fi

exec "$JAVACMD" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
