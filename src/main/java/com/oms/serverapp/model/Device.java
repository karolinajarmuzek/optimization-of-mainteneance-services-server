package com.oms.serverapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Device name must be between 3 and 30 characters.")
    @Size(min = 3, max = 30)
    private String name;

    @NotBlank(message = "Device name must be between 3 and 30 characters.")
    @Size(min = 3, max = 30)
    private String type;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Skill> skills;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    Set<Report> reports;

    public Device() {
    }

    public Device(String name, String type, Set<Skill> skills, Set<Report> reports) {
        this.name = name;
        this.type = type;
        this.skills = skills;
        this.reports = reports;
    }

    public Device(Long id, String name, String type, Set<Skill> skills, Set<Report> reports) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.skills = skills;
        this.reports = reports;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
}
