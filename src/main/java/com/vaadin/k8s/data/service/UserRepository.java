package com.vaadin.k8s.data.service;

import com.vaadin.k8s.data.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import com.vaadin.k8s.data.Role;
import jakarta.persistence.Lob;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}