# k8s-demo-app

A Vaadin demo app to test Vaadin [cluster setup](https://github.com/vaadin/k8s-blue-green)

### Branches

There are 3 branches in this repo:
- [main](https://github.com/vaadin/k8s-demo-app/) It's a project downloaded from start.vaadin.com and modified to include the vaadin-cluster-support dependency to make the app aware of cluster update events.
- [stateless-auth](https://github.com/vaadin/k8s-demo-app/tree/stateless-auth) which is the same application, but implementing stateless auth, which means that the user is always logged in when swithching blue/green
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



