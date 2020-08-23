package com.oms.serverapp.payload;

import com.oms.serverapp.model.ServiceTechnician;

import javax.validation.constraints.*;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceTechnicianPayload {

    private Long id;

    @NotBlank(message = "Name must be between 3 and 15 characters.")
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters.")
    private String firstName;

    @NotBlank(message = "Surname must be between 3 and 15 characters.")
    @Size(min = 3, max = 15, message = "Surname must be between 3 and 15 characters.")
    private String lastName;

    @NotBlank(message = "Phone number must have 9 digits.")
    @Size(min = 9, max = 9, message = "Phone number must have 9 digits.")
    private String phoneNumber;

    @NotBlank(message = "Username must be between 6 and 30 characters.")
    @Size(min = 6, max = 30, message = "Username must be between 6 and 30 characters.")

    private String username;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotBlank(message = "Address cannot be null")
    private String startLocalization;

    @NotBlank(message = "Longitude cannot be null")
    private String longitude;

    @NotBlank(message = "Latitude cannot be null")
    private String latitude;

    @NotNull(message = "Experience must be between 1 and 10.")
    @Min(value = 1, message = "Experience must be between 1 and 10.")
    @Max(value = 10, message = "Experience must be between 1 and 10.")
    private Integer experience;

    private Set<Long> skills;
    private Set<Long> repairs;

    public ServiceTechnicianPayload() {
    }

    public ServiceTechnicianPayload(ServiceTechnician serviceTechnician) {
        this.id = serviceTechnician.getId();
        this.firstName = serviceTechnician.getFirstName();
        this.lastName = serviceTechnician.getLastName();
        this.phoneNumber = serviceTechnician.getPhoneNumber();
        this.username = serviceTechnician.getUsername();
        this.password = serviceTechnician.getPassword();
        this.startLocalization = serviceTechnician.getStartLocalization();
        this.longitude = serviceTechnician.getLongitude();
        this.latitude =  serviceTechnician.getLatitude();
        this.experience = serviceTechnician.getExperience();
        this.skills = serviceTechnician.getOwnedSkills().stream().map(skill -> skill.getId()).collect(Collectors.toSet());
        this.repairs = serviceTechnician.getRepairs().stream().map(repair -> repair.getId()).collect(Collectors.toSet());
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
