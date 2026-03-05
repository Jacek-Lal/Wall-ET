#!/usr/bin/env bash

set -x

NAME=`mvn -q -DforceStdout help:evaluate -Dexpression=project.name`
VERSION=`mvn -q -DforceStdout help:evaluate -Dexpression=project.version`

docker build -t $DOCKERHUB_USER/${NAME}:${VERSION} -t $DOCKERHUB_USER/${NAME}:latest .
