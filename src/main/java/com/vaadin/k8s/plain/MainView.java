package com.vaadin.k8s.plain;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.k8s.ClusterSupport;

/**
 * The main view contains a button and a click listener.
 */
@Route
@RouteAlias("/hello")
@RouteAlias("/about")
@RouteAlias("/personas")
public class MainView extends VerticalLayout {

    public MainView() {
        H3 h3 = new H3("Demo App, version: " + (ClusterSupport.getCurrent() == null ? "no APP_VERSION defined"
                : ClusterSupport.getCurrent().getAppVersion()));
        Button button = new Button("Click me", event -> Notification.show("Clicked!"));
        add(h3, button);
    }
}
