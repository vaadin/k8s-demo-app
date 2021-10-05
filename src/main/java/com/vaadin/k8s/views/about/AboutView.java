package com.vaadin.k8s.views.about;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.k8s.AppVersions;
import com.vaadin.k8s.ClusterSupport;
import com.vaadin.k8s.CookieInfo;
import com.vaadin.k8s.VersionInfo;
import com.vaadin.k8s.views.MainLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends Div {

    public AboutView(@Autowired AppVersions appVersions) {
        addClassNames("about-view", "flex", "flex-col", "h-full", "items-center", "justify-center", "p-l",
                "text-center", "box-border");

        Div wrapper = new Div();
        wrapper.addClassNames("box-border");
        wrapper.setWidth("176px");
        wrapper.setHeight("176px");
        Style wrapperStyle = wrapper.getStyle();
        wrapperStyle.set("padding-top", "34px");
        wrapperStyle.set("border-radius", "100px");
        wrapperStyle.set("background", "var(--lumo-shade-10pct)");

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("150px");
        wrapper.add(img);
        add(wrapper);

        add(new H2("About"));
        add(new Paragraph("Some useful server information is displayed below"));

        Component versionInfo = new VersionInfo(appVersions);
        add(versionInfo);

        Component cookieView = new CookieInfo();
        add(cookieView);

    }

}
