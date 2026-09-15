#!/bin/sh
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading maven-wrapper.jar..."
    mkdir -p .mvn/wrapper
    which curl >/dev/null 2>&1 && curl -sSL "$WRAPPER_URL" -o "$WRAPPER_JAR" || \
    which wget >/dev/null 2>&1 && wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
fi

exec java -Dmaven.multiModuleProjectDirectory=. -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
