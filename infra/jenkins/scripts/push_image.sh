#!/usr/bin/env bash

set -x

NAME=`mvn -q -DforceStdout help:evaluate -Dexpression=project.name`
VERSION=`mvn -q -DforceStdout help:evaluate -Dexpression=project.version`

echo $DOCKERHUB_PASS | docker login -u $DOCKERHUB_USER --password-stdin

docker push $DOCKERHUB_USER/${NAME}:${VERSION}
docker push $DOCKERHUB_USER/${NAME}:latest
docker logout