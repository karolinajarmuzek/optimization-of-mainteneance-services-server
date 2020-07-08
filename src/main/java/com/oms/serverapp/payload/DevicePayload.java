package com.oms.serverapp.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public class DevicePayload {

    private Long id;

    @NotBlank()
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters.")
    private String name;

    @NotBlank()
    @Size(min = 3, max = 30, message = "Type must be between 3 and 30 characters.")
    private String type;

    private Set<Long> skills;
    private Set<Long> reports;

    public DevicePayload() {
    }

    public DevicePayload(Long id, String name, String type, Set<Long> skills, Set<Long> reports) {
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
