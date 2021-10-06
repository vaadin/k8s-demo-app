package com.vaadin.k8s;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Load properties from application.properties.
 *
 * This uses properties in Spring via Java configuration.
 */
@PropertySource(value = { "classpath:application.properties" })
@Configuration
public class AppVersions {

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.buildtime}")
    private String appBuildTime;

    @Value("${app.vaadin.version}")
    private String appVaadinVersion;

    @Value("${app.java.version}")
    private String appJavaVersion;

    public String getVersion() {
        return appVersion;
    }

    public String getBuildTime() {
        return appBuildTime;
    }

    public String getVaadinVersion() {
        return appVaadinVersion;
    }

    public String getJavaVersion() {
        return appJavaVersion;
    }

    public String getHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null) {
            hostname = "localhost";
        }
        return hostname.replaceFirst("^.*-", "");
    }

}