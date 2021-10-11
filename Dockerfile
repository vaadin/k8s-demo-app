## Create the war file by running mvn package
FROM jetty
COPY target/*.war /var/lib/jetty/webapps/ROOT.war

RUN java -jar $JETTY_HOME/start.jar --add-to-startd=jmx,stats
ENTRYPOINT [ "java", "-jar", "/usr/local/jetty/start.jar" ]

## docker run -e APP_VERSION=2.4 -it --rm -p 8080:8080 --name k8s-demo-app vaadin/k8s-demo-app:0.0 --adfa-asdfs=asfdaf
## docker run -it --rm --entrypoint /bin/bash --name my-app vaadin/k8s-demo-app:0.0