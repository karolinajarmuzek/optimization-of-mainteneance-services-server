package com.oms.serverapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name must be between 3 and 15 characters.")
    @Size(min = 3, max = 15)
    private String firstName;

    @NotBlank(message = "Customer surname must be between 3 and 15 characters.")
    @Size(min = 3, max = 15)
    private String lastName;

    @NotBlank(message = "Customer phone number must have 9 digits.")
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    Set<Report> reports;

    public Customer() {
    }

    public Customer(String firstName, String lastName, String phoneNumber, Set<Report> reports) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.reports = reports;
    }

    public Customer(Long id, String firstName, String lastName, String phoneNumber, Set<Report> reports) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.reports = reports;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
}
