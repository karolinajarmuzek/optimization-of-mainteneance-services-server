package com.oms.serverapp.payload;

import com.oms.serverapp.model.ServiceMan;

import javax.validation.constraints.*;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceManPayload {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters.")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 15, message = "Surname must be between 3 and 15 characters.")
    private String lastName;

    @NotBlank
    @Size(min = 9, max = 9, message = "Phone number must have 9 digits.")
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 30, message = "Username must be between 6 and 30 characters.")
    private String username;

    @NotBlank
    //@Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters.")
    private String password;

    @NotBlank(message = "Location cannot be blank.")
    private String startLocalization;

    @NotNull()
    @Min(value = 1, message = "Experience must be between 1 and 10.")
    @Max(value = 10, message = "Experience must be between 1 and 10.")
    private Integer experience;

    private Set<Long> skills;
    private Set<Long> repairs;

    public ServiceManPayload() {
    }

    public ServiceManPayload(ServiceMan serviceMan) {
        this.id = serviceMan.getId();
        this.firstName = serviceMan.getFirstName();
        this.lastName = serviceMan.getLastName();
        this.phoneNumber = serviceMan.getPhoneNumber();
        this.username = serviceMan.getUsername();
        this.password = serviceMan.getPassword();
        this.startLocalization = serviceMan.getStartLocalization();
        this.experience = serviceMan.getExperience();
        this.skills = serviceMan.getOwnedSkills().stream().map(skill -> skill.getId()).collect(Collectors.toSet());
        this.repairs = serviceMan.getRepairs().stream().map(repair -> repair.getId()).collect(Collectors.toSet());
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
