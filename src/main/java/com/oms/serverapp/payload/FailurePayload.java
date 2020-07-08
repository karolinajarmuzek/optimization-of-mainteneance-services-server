package com.oms.serverapp.payload;

import java.util.Set;

public class FailurePayload {

    private Long id;
    private String type;
    private Set<Long> skills;
    private Set<Long> reports;

    public FailurePayload() {
    }

    public FailurePayload(Long id, String type, Set<Long> skills, Set<Long> reports) {
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

    public Set<Long> getSkills() {
        return skills;
    }

    public void setSkills(Set<Long> skills) {
        this.skills = skills;
    }

    public Set<Long> getReports() {
        return reports;
    }

    public void setReports(Set<Long> reports) {
        this.reports = reports;
    }
}
