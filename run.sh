#!/bin/bash

set -e

mvn clean package

java --enable-preview -jar ./target/graphino-0.1.0-SNAPSHOT.jar