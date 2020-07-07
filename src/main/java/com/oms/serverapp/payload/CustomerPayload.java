package com.oms.serverapp.payload;

import java.util.Set;

public class CustomerPayload {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<Long> reports;

    public CustomerPayload() {
    }

    public CustomerPayload(Long id, String firstName, String lastName, String phoneNumber, Set<Long> reports) {
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

    public Set<Long> getReports() {
        return reports;
    }

    public void setReports(Set<Long> reports) {
        this.reports = reports;
    }
}
