package com.vaadin.k8s.views.about;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.k8s.AppVersions;
import com.vaadin.k8s.CookieInfo;
import com.vaadin.k8s.VersionInfo;
import com.vaadin.k8s.views.MainLayout;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends Div {

    public AboutView(@Autowired AppVersions appVersions) {
        addClassNames("about-view", "flex", "flex-col", "h-full", "items-center", "justify-center", "p-l",
                "text-center", "box-border");

        add(new Paragraph("Some useful server information is displayed below"));
        Component versionInfo = new VersionInfo(appVersions);

        add(versionInfo);

        Component cookieView = new CookieInfo();
        add(cookieView);

    }

}
