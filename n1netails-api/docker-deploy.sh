#!/bin/bash

# Extract version from pom.xml
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
IMAGE_NAME=n1netails-api
DOCKER_USER=shahidfo
REPO=n1netails-api

# Build image
docker build -t $IMAGE_NAME .

# Tag image with both version and latest
docker tag $IMAGE_NAME $DOCKER_USER/$REPO:latest
docker tag $IMAGE_NAME $DOCKER_USER/$REPO:$VERSION

# Push both tags
docker push $DOCKER_USER/$REPO:latest
docker push $DOCKER_USER/$REPO:$VERSION

echo "✅ Deployed version $VERSION to Docker Hub"