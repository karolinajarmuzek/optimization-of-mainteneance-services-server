package com.oms.serverapp.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "servicemen")
public class ServiceMan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ServiceMan name must be between 3 and 15 characters.")
    @Size(min = 3, max = 15)
    private String firstName;

    @NotBlank(message = "ServiceMan surname must be between 3 and 15 characters.")
    @Size(min = 3, max = 15)
    private String lastName;

    @NotBlank(message = "ServiceMan phone number must have 9 digits.")
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @NotBlank(message = "ServiceMan username must be between 6 and 30 characters.")
    @Size(min = 6, max = 30)
    private String username;

    @NotBlank(message = "ServiceMan password must be between 6 and 30 characters.")
    @Size(min = 6, max = 30)
    private String password;

    @NotBlank(message = "ServiceMan location cannot be blank")
    private String startLocalization;

    @NotNull(message = "ServiceMan experience must be between 1 and 10.")
    @Min(1)
    @Max(10)
    private int experience;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "skills_owned",
            joinColumns = @JoinColumn(name = "serviceman_id"),
            inverseJoinColumns = {@JoinColumn(name = "failure_id"),
                                    @JoinColumn(name = "device_id")}
    )
    Set<Skill> ownedSkills;

    @OneToMany(mappedBy = "serviceMan", cascade = CascadeType.ALL)
    Set<Repair> repairs;


    public ServiceMan() {
    }

    public ServiceMan(String firstName, String lastName, String phoneNumber, String username, String password, String startLocalization, int experience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.startLocalization = startLocalization;
        this.experience = experience;
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

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Set<Skill> getOwnedSkills() {
        return ownedSkills;
    }

    public void setOwnedSkills(Set<Skill> ownedSkills) {
        this.ownedSkills = ownedSkills;
    }
}
