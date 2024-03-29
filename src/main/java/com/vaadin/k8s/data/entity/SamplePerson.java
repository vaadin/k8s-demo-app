package com.vaadin.k8s.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.vaadin.k8s.data.AbstractEntity;
import java.time.LocalDate;

@Entity
public class SamplePerson extends AbstractEntity {

    @NotEmpty
    @Size(min = 2, max = 20)
    private String firstName;
    private String lastName;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "[0-9\\-\\. \\+\\(\\)]{9,16}", message = "Enter a valid phone number")
    private String phone;
    @NotNull
    private LocalDate dateOfBirth;
    @NotEmpty
    private String occupation;
    private boolean important;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getOccupation() {
        return occupation;
    }
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    public boolean isImportant() {
        return important;
    }
    public void setImportant(boolean important) {
        this.important = important;
    }

}
