#!/bin/sh

### Just an utility script that allows to deploy the app with different versions to Docker Hub
### It changes colors for each branch, packages the app, builds the docker image and deploys it.
### Usage: ./build-app.sh 1.0 

set -e

USER=vaadin
APP=k8s-demo-app
VERS=$1

# Package maven app
echo ""
echo ">> Packaging Maven Application ${APP} ..."
mvn package -Pproduction -B -q

# Restore back modified colors
[ -f ${CSSFILE}~ ] && mv ${CSSFILE}~ ${CSSFILE}

# Build docker image
echo ""
echo ">> Building Docker Image ${USER}/${APP}:${VERS} (You need privs to push to ${USER}/${APP})..."
docker build --tag ${USER}/${APP}:${VERS} .

# Publish docker image
echo ""
echo ">> Publishing Image in Docker Hub (You need privs to push to ${USER}/${APP}) ..."
docker push ${USER}/${APP}:${VERS}
