#!/bin/sh
mkdir -p build
find . -name "*.java" > sources.txt
javac -classpath lib/junit-jupiter-api-5.0.0.jar:lib/apiguardian-api-1.0.0.jar -d build @sources.txt
mkdir -p package
jar cf package/FlashText.jar -C build .
java -jar lib/junit-platform-console-standalone-1.0.0.jar -classpath lib/junit-jupiter-api-5.0.0.jar:lib/apiguardian-api-1.0.0.jar:lib/junit-jupiter-engine-5.0.0.jar:package/FlashText.jar --scan-class-path
