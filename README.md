# k8s-demo-app

A Vaadin demo app to test Vaadin [cluster setup](https://github.com/vaadin/k8s-blue-green)

### Branches

There are 2 branches in this repo:
- [main](https://github.com/vaadin/k8s-demo-app/) It's a project downloaded from start.vaadin.com and modified to use [Kubernetes Kit](https://vaadin.com/docs/latest/tools/kubernetes) (session replication with Hazelcast and rolling update notifications), and to support stateless authentication by using Vaadin security helpers
- [plain-java](https://github.com/vaadin/k8s-demo-app/tree/plain-java) A simple vaadin application with the vaadin-cluster-support but not depending on spring stack, and without authentication.

## Running the application

The project is a standard Maven project. To run it in dev-mode, from the command line type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw  clean package -Pproduction` (Mac & Linux).

Since this application is thought to be package as a docker image and deployed in a docker hub, there is a script [build-app.sh](https://github.com/vaadin/k8s-demo-app/blob/stateless-auth/build-app.sh) for building 3 versions of the application and uploading to docker hub.

You need to change `USER` and `APP` to match your application, as well as provide the appropriate privilegies to the `docker push` command.

For a more detailed information on how you can customize and package your application read the [Application Setup](https://github.com/vaadin/k8s-blue-green#application-setup) section in the cluster setup repository.




## Kubernetes Kit demo on a local cluster

Requires a Vaadin subscription with Kubernetes Kit, Docker Desktop with Kubernetes enabled (or any local cluster
that can use local images), and port 80 free on localhost.

```bash
./build-app.sh all --local     # build images 1.0 (blue), 2.0 (green) and 3.0 (orange) without pushing
./k8s/demo.sh install          # Envoy Gateway, Hazelcast RBAC/service and the gateway
./k8s/demo.sh deploy 1         # 3 pods of version 1.0 at http://localhost (admin/admin)
```

Then show:

- **Scaling and load balancing:** `./k8s/demo.sh scale 1 5`, open private windows and compare `HostName` in the About view.
- **Failover:** open a person in Personas, change a field without saving, and `./k8s/demo.sh kill <HostName>` the pod shown
  in About. On the next interaction the session continues on another pod with the unsaved changes.
- **Rolling update:** `./k8s/demo.sh update 1 2`. New sessions get version 2.0, users on 1.0 get a notification and
  switch when they click it. `./k8s/demo.sh finish 1 2` removes 1.0.

`./k8s/demo.sh status` lists the pods and the route, `./k8s/demo.sh cleanup` removes everything.
