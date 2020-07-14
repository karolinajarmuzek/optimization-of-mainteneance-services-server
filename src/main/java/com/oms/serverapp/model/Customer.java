package com.oms.serverapp.model;

import com.oms.serverapp.payload.CustomerPayload;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "customers", uniqueConstraints = {@UniqueConstraint(columnNames = {"firstName", "lastName"})})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank()
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters.")
    private String firstName;

    @NotBlank()
    @Size(min = 3, max = 15, message = "Surname must be between 3 and 15 characters.")
    private String lastName;

    @NotBlank()
    @Size(min = 9, max = 9, message = "Phone number must have 9 digits.")
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    Set<Report> reports;

    public Customer() {
    }

    public Customer(CustomerPayload customerPayload) {
        this.firstName = customerPayload.getFirstName();
        this.lastName = customerPayload.getLastName();
        this.phoneNumber = customerPayload.getPhoneNumber();
    }

    public Customer(Customer customer, CustomerPayload customerPayload) {
        this.id = customer.getId();
        this.firstName = customerPayload.getFirstName();
        this.lastName = customerPayload.getLastName();
        this.phoneNumber = customerPayload.getPhoneNumber();
        this.reports = customer.getReports();
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
