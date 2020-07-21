#!/bin/bash

find . -name "pom.xml" -exec mvn clean -f '{}' \;