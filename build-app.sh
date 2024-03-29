#!/bin/sh

### Just an utility script that allows to deploy the app with different versions to Docker Hub
### It changes colors for each branch, packages the app, builds the docker image and deploys it.
### Usage: ./build-app.sh 1.0 

set -e

USER=vaadin
APP=k8s-demo-app
VERS=$1
CSSFILE=frontend/themes/k8s-demo-app/styles.css

deploy() {
  echo ""
  echo ">> Changing colors for ${VERS} ($base, $primary) ..."
  sed -i~ \
    -e 's/--lumo-base-color:.*;/--lumo-base-color: '$base';/g' \
    -e 's/--lumo-primary-color:.*;/--lumo-primary-color: '$primary';/g' \
    ${CSSFILE}

  # Package maven app
  echo ""
  echo ">> Packaging Maven Application ${APP} ..."
  mvn package -Pproduction -B -q

  # Restore back modified colors
  [ -f ${CSSFILE}~ ] && mv ${CSSFILE}~ ${CSSFILE}

  # Build docker image
  echo ""
  echo ">> Building Docker Image ${USER}/${APP}:${VERS} ..."
  docker build --quiet --tag ${USER}/${APP}:${VERS} .

  # Publish docker image
  echo ""
  echo ">> Publishing Image in Docker Hub (You need privs to push to ${USER}/${APP}) ..."
  docker push --quiet ${USER}/${APP}:${VERS}
}

# Compute and set colors per each version
case ${VERS} in
  1.0) base="#233348" primary="#2a7fef" deploy;;
  2.0) base="#2c6d3b" primary="#194e19" deploy;;
  3.0) base="#926513" primary="#c2881f" deploy;;
  all) sh $0 1.0 && sh $0 2.0 && sh $0 3.0;;
  *) echo "Usage $0 <1.0|2.0|3.0|all>" && exit 1;;
esac
