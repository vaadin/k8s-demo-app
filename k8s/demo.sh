#!/bin/sh

### Drives the Kubernetes Kit demo on a local cluster (Docker Desktop, kind, ...).
### Images are built with ../build-app.sh <1.0|2.0|3.0> [--local].
###
### Usage:
###   ./demo.sh install              Install Envoy Gateway, Hazelcast RBAC/service and the gateway
###   ./demo.sh deploy <v>           Deploy version <v> (1, 2 or 3) and route all traffic to it
###   ./demo.sh scale <v> <n>        Scale version <v> to <n> pods
###   ./demo.sh kill [pod]           Delete a pod (full name or the HostName shown in About, or a random one)
###   ./demo.sh update <old> <new>   Deploy <new> next to <old>: new sessions go to <new>,
###                                  users on <old> get the "new version" notification
###   ./demo.sh finish <old> <new>   Route everything to <new> and remove <old>
###   ./demo.sh status               Show app pods, their version and the current route
###   ./demo.sh cleanup              Remove everything created by this script

set -e

SCRIPT=$(basename "$0")
cd "$(dirname "$0")"

ENVOY_GATEWAY_VERSION=v1.6.0
APP=k8s-demo-app

# Applies the HTTPRoute. Arguments: <version> [<previous version>]
# With a previous version, both stay routable (existing sessions stick to their
# pod through the vaadin-session cookie) and requests get the X-AppUpdate header
# that makes Kubernetes Kit show the update notification to users on <previous>.
route() {
  new=$1
  old=$2
  {
    cat <<EOF
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
  name: ${APP}
spec:
  parentRefs:
    - name: public-gateway
  rules:
    - matches:
        - path:
            type: PathPrefix
            value: /
      backendRefs:
        - name: ${APP}-v${new}
          port: 80
          weight: 100
EOF
    if [ -n "$old" ]; then
      cat <<EOF
        - name: ${APP}-v${old}
          port: 80
          weight: 1
      filters:
        - type: RequestHeaderModifier
          requestHeaderModifier:
            set:
              - name: X-AppUpdate
                value: "${new}.0"
EOF
    fi
    cat <<EOF
      sessionPersistence:
        sessionName: vaadin-session
        type: Cookie
EOF
  } | kubectl apply -f -
}

wait_ready() {
  kubectl rollout status deployment/${APP}-v$1 --timeout=300s
}

url() {
  echo ""
  echo ">> App available at http://localhost (admin/admin or user/user)"
}

case $1 in
  install)
    echo ">> Installing Envoy Gateway ${ENVOY_GATEWAY_VERSION} ..."
    kubectl apply --server-side -f https://github.com/envoyproxy/gateway/releases/download/${ENVOY_GATEWAY_VERSION}/install.yaml
    kubectl wait --timeout=300s -n envoy-gateway-system deployment/envoy-gateway --for=condition=Available
    echo ">> Creating Hazelcast RBAC/service and the gateway ..."
    kubectl apply -f hazelcast.yaml -f gateway.yaml
    kubectl wait --timeout=300s gateway/public-gateway --for=condition=Programmed
    ;;
  deploy)
    kubectl apply -f app-v$2.yaml
    route $2
    wait_ready $2
    url
    ;;
  scale)
    kubectl scale deployment/${APP}-v$2 --replicas=$3
    wait_ready $2
    ;;
  kill)
    if [ -n "$2" ]; then
      # Accepts the full pod name or the HostName suffix shown in the About view
      pod=$(kubectl get pods -l app=${APP} -o name | grep -- "$2\$" | head -1)
      [ -z "$pod" ] && echo "No pod matching $2" && exit 1
    else
      pod=$(kubectl get pods -l app=${APP} -o name | awk 'BEGIN{srand()} {p[NR]=$0} END{print p[int(rand()*NR)+1]}')
    fi
    echo ">> Deleting ${pod} ..."
    kubectl delete pod ${pod#pod/} --wait=false
    ;;
  update)
    kubectl apply -f app-v$3.yaml
    wait_ready $3
    route $3 $2
    url
    ;;
  finish)
    route $3
    kubectl delete -f app-v$2.yaml --ignore-not-found
    ;;
  status)
    kubectl get pods -l app=${APP} -L version -o wide
    echo ""
    kubectl get httproute ${APP} -o jsonpath='{range .spec.rules[0].backendRefs[*]}{.name}{" weight="}{.weight}{"\n"}{end}' 2>/dev/null || true
    ;;
  cleanup)
    kubectl delete httproute ${APP} --ignore-not-found
    kubectl delete -f app-v1.yaml -f app-v2.yaml -f app-v3.yaml --ignore-not-found
    kubectl delete -f gateway.yaml -f hazelcast.yaml --ignore-not-found
    ;;
  *)
    sed -n 's/^### \{0,1\}//p' "${SCRIPT}"
    exit 1
    ;;
esac
