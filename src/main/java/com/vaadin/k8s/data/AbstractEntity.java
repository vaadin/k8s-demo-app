package com.vaadin.k8s.data;

import java.io.Serializable;

import jakarta.annotation.Nonnull;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


// Serializable: entities held by views are part of the session replicated by Kubernetes Kit
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue
    @Nonnull
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }
        AbstractEntity other = (AbstractEntity) obj;

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }
}
