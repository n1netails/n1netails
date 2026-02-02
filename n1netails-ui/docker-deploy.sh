#!/bin/bash

# Extract version from pom.xml
set -e

cd "$(dirname "$0")"

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | tr -d '\r')

if [ -z "$VERSION" ]; then
  echo "❌ ERROR: Maven version not resolved"
  exit 1
fi

IMAGE_NAME=n1netails-ui
DOCKER_USER=shahidfo
REPO=n1netails-ui

# Build image
docker build -t $IMAGE_NAME .

# Tag image with both version and latest
docker tag $IMAGE_NAME $DOCKER_USER/$REPO:latest
docker tag $IMAGE_NAME $DOCKER_USER/$REPO:$VERSION

# Push both tags
docker push $DOCKER_USER/$REPO:latest
docker push $DOCKER_USER/$REPO:$VERSION

echo "✅ Deployed version $VERSION to Docker Hub"