#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out grafos/*.java
java -cp out grafos.Main test
