package com.vaadin.k8s;


import com.vaadin.k8s.AppVersions;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class VersionInfo extends Div {


    private Span version;
    private Span buildTime;
    private Span vaadinVersion;
    private Span nodeName;

    public VersionInfo(AppVersions versions) {
        version = new Span(versions.getVersion());
        buildTime = new Span(versions.getBuildTime());
        vaadinVersion = new Span("Vaadin "+versions.getVaadinVersion());
        nodeName = new Span("Node " + versions.getHostname());
        setClassName("text-secondary");
        buildTime.getStyle().set("padding","10px");
        nodeName.getStyle().set("padding","10px");
        add(version, buildTime, vaadinVersion, nodeName);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }
}
