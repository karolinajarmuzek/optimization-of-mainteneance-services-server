package com.oms.serverapp.model;

import com.oms.serverapp.payload.ServiceTechnicianPayload;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "servicetechnician", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"firstName", "lastName"}),
        @UniqueConstraint(columnNames = "username")
})
public class ServiceTechnician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "skills_owned",
            joinColumns = @JoinColumn(name = "servicetechnician_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    Set<Skill> ownedSkills;

    @OneToMany(mappedBy = "serviceTechnician", cascade = CascadeType.ALL)
    Set<Repair> repairs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "servicetechnician_roles",
            joinColumns = @JoinColumn(name = "servicetechnician_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public ServiceTechnician() {
    }

    public ServiceTechnician(ServiceTechnicianPayload serviceTechnicianPayload, String password, Set<Skill> skills, Set<Repair> repairs,Set<Role> roles) {
        this.firstName = serviceTechnicianPayload.getFirstName();
        this.lastName = serviceTechnicianPayload.getLastName();
        this.phoneNumber = serviceTechnicianPayload.getPhoneNumber();
        this.username = serviceTechnicianPayload.getUsername();
        this.password = password;
        this.startLocalization = serviceTechnicianPayload.getStartLocalization();
        this.longitude = serviceTechnicianPayload.getLongitude();
        this.latitude = serviceTechnicianPayload.getLatitude();
        this.experience = serviceTechnicianPayload.getExperience();
        this.ownedSkills = skills;
        this.repairs = repairs;
        this.roles = roles;
    }

    public ServiceTechnician(ServiceTechnician serviceTechnician, ServiceTechnicianPayload serviceTechnicianPayload, String password, Set<Skill> skills, Set<Repair> repairs, Set<Role> roles) {
        this.id = serviceTechnician.getId();
        this.firstName = serviceTechnicianPayload.getFirstName() != null ? serviceTechnicianPayload.getFirstName() : serviceTechnician.getFirstName();
        this.lastName = serviceTechnicianPayload.getLastName() != null ? serviceTechnicianPayload.getLastName() : serviceTechnician.getLastName();
        this.phoneNumber = serviceTechnicianPayload.getPhoneNumber() != null ? serviceTechnicianPayload.getPhoneNumber() : serviceTechnician.getPhoneNumber();
        this.username = serviceTechnicianPayload.getUsername() != null ? serviceTechnicianPayload.getUsername() : serviceTechnician.getUsername();
        this.password = password != null ? password : serviceTechnician.getPassword();
        this.startLocalization = serviceTechnicianPayload.getStartLocalization() != null ? serviceTechnicianPayload.getStartLocalization() : serviceTechnician.getStartLocalization();
        this.longitude = serviceTechnicianPayload.getLongitude() != null ? serviceTechnicianPayload.getLongitude() : serviceTechnician.getLongitude();
        this.latitude = serviceTechnicianPayload.getLatitude() != null ? serviceTechnicianPayload.getLatitude() : serviceTechnician.getLatitude();
        this.experience = serviceTechnicianPayload.getExperience() != null ? serviceTechnicianPayload.getExperience() : serviceTechnician.getExperience();
        this.ownedSkills = skills;
        this.repairs = repairs;
        this.roles = roles;
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

    public Set<Skill> getOwnedSkills() {
        return ownedSkills;
    }

    public void setOwnedSkills(Set<Skill> ownedSkills) {
        this.ownedSkills = ownedSkills;
    }

    public Set<Repair> getRepairs() {
        return repairs;
    }

    public void setRepairs(Set<Repair> repairs) {
        this.repairs = repairs;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
