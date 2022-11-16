package com.vaadin.k8s;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.dependency.NpmPackage;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "k8s-demo-app", variant = Lumo.DARK)
@PWA(name = "k8s-demo-app", shortName = "k8s-demo-app", offlineResources = {"images/logo.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@ComponentScan(basePackages = {"com.vaadin.k8s","com.vaadin.enterprise"})
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
