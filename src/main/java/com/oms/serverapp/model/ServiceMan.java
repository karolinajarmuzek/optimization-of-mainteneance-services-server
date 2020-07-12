package com.oms.serverapp.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "servicemen", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"firstName", "lastName"}),
        @UniqueConstraint(columnNames = "username")
})
public class ServiceMan {

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

    @NotBlank()
    @Size(min = 6, max = 30, message = "Username must be between 6 and 30 characters.")
    private String username;

    @NotBlank()
    //@Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters.")
    private String password;

    @NotBlank(message = "Location cannot be blank.")
    private String startLocalization;

    @NotNull()
    @Min(value = 1, message = "Experience must be between 1 and 10.")
    @Max(value = 10, message = "Experience must be between 1 and 10.")
    private Integer experience;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "skills_owned",
            joinColumns = @JoinColumn(name = "serviceman_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    Set<Skill> ownedSkills;

    @OneToMany(mappedBy = "serviceMan", cascade = CascadeType.ALL)
    Set<Repair> repairs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "serviveman_roles",
            joinColumns = @JoinColumn(name = "serviceman_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public ServiceMan() {
    }

    public ServiceMan(String firstName, String lastName, String phoneNumber, String username, String password, String startLocalization, Integer experience, Set<Skill> ownedSkills, Set<Repair> repairs) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.startLocalization = startLocalization;
        this.experience = experience;
        this.ownedSkills = ownedSkills;
        this.repairs = repairs;
    }

    public ServiceMan(Long id, String firstName, String lastName, String phoneNumber, String username, String password, String startLocalization, Integer experience, Set<Skill> ownedSkills, Set<Repair> repairs) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.startLocalization = startLocalization;
        this.experience = experience;
        this.ownedSkills = ownedSkills;
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
