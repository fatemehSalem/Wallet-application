#!/bin/bash

# https://stackoverflow.com/questions/15895805/find-pom-in-subdirectories-and-execute-mvn-clean
find . -name "pom.xml" -exec mvn package -f '{}' \;