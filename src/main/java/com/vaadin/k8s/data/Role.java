package com.vaadin.k8s.data;

public enum Role {
    USER("user"), ADMIN("admin");

    private String roleName;

    private Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
