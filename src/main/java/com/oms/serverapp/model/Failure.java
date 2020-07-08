package com.oms.serverapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "failures", uniqueConstraints = {@UniqueConstraint(columnNames = {"type"})})
public class Failure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Failure type must be between 3 and 30 characters. ")
    private String type;

    @OneToMany(mappedBy = "failure", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Skill> skills;

    @OneToMany(mappedBy = "failure", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Report> reports;

    public Failure() {
    }

    public Failure(String type, Set<Skill> skills, Set<Report> reports) {
        this.type = type;
        this.skills = skills;
        this.reports = reports;
    }

    public Failure(Long id, String type, Set<Skill> skills, Set<Report> reports) {
        this.id = id;
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
