package com.oms.serverapp.payload;

import java.util.Set;

public class ServiceManPayload {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private String password;
    private String startLocalization;
    private Integer experience;
    private Set<Long> skills;
    private Set<Long> repairs;

    public ServiceManPayload() {
    }

    public ServiceManPayload(Long id, String firstName, String lastName, String phoneNumber, String username, String password, String startLocalization, Integer experience, Set<Long> skills, Set<Long> repairs) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.startLocalization = startLocalization;
        this.experience = experience;
        this.skills = skills;
        this.repairs = repairs;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStartLocalization() {
        return startLocalization;
    }

    public void setStartLocalization(String startLocalization) {
        this.startLocalization = startLocalization;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Set<Long> getSkills() {
        return skills;
    }

    public void setSkills(Set<Long> skills) {
        this.skills = skills;
    }

    public Set<Long> getRepairs() {
        return repairs;
    }

    public void setRepairs(Set<Long> repairs) {
        this.repairs = repairs;
    }
}
