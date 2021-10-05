package com.vaadin.k8s;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;

public class CookieInfo extends Div {

    public CookieInfo() {
        setClassName("text-secondary");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        for (Cookie cookie : VaadinService.getCurrentRequest().getCookies()) {
            final Span cName = new Span(cookie.getName());
            final Span cContent = new Span(cookie.getValue());
            final Span cDomain = new Span(""+ cookie.getDomain());
            final Span cAge = new Span(""+ cookie.getMaxAge());
            Div div = new Div(cName, new Span(" = '"),cContent,
                    new Span("', domain: "),cDomain,
                    new Span(", max-age: "),cAge);
            div.setClassName("cookie-info");
            add(div);
        }

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        removeAll();
        super.onDetach(detachEvent);
    }
}
