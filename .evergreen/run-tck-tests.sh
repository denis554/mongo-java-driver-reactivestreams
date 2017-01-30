#!/bin/bash

set -o xtrace   # Write all commands first to stderr
set -o errexit  # Exit the script with error if any of the commands fail

export JAVA_HOME="/opt/java/jdk8"

############################################
#            Main Program                  #
############################################


echo "Running TCK tests"

./gradlew -version
./gradlew --stacktrace --info tckTest
