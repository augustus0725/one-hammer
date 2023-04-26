#!/usr/bin/env bash

cd /opt/app || exit
java -Xms32m -Xmx512m -jar one-hammer-app-1.0-SNAPSHOT.jar --spring.profiles.active=${1}
