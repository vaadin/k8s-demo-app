package com.vaadin.k8s;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.Cookie;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

public class CookieInfo extends Div {

    public CookieInfo() {
        setClassName("text-secondary");
        setSizeFull();
    }

    private static class Item {
        final String type;
        final String name;
        final String value;
        public Item(String type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
        public String getType() {
            return type;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
    }

    private List<Item> items = new ArrayList<>();
    private Grid<Item> grid = new Grid<>();

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        VaadinRequest req = VaadinService.getCurrentRequest();
        for (Cookie cookie : req.getCookies()) {
            items.add(new Item("C", cookie.getName(),
                    cookie.getValue() + ", domain:" + cookie.getDomain() + ", max-age:" + cookie.getMaxAge()));
        }
        Enumeration<String> headers = req.getHeaderNames();
        for (; headers.hasMoreElements();) {
            String type = headers.nextElement();
            items.add(new Item("H", type, req.getHeader(type)));
        }
        grid.addColumn(Item::getType).setFlexGrow(0).setWidth("50px");
        grid.addColumn(Item::getName).setFlexGrow(1).setWidth("150px");
        grid.addColumn(Item::getValue).setFlexGrow(4);
        grid.setItems(items);
        add(grid);
        grid.setSizeFull();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        removeAll();
        super.onDetach(detachEvent);
    }
}
