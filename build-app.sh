#!/bin/sh
set -e

USER=vaadin
APP=k8s-demo-app
VERS=1.0

echo ">> Building Maven Application $APP ..."
mvn package -Pproduction -B -q

echo ""
echo ">> Building Docker Image $USER/$APP:$VERS (You need privs to push to $USER/$APP)..."
docker build --tag $USER/$APP:$VERS .

echo ""
echo ">> Pushing Image to Docker Hub (You need privs to push to $USER/$APP) ..."
docker push $USER/$APP:$VERS