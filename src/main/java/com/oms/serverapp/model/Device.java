package com.oms.serverapp.model;

import com.oms.serverapp.payload.DevicePayload;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "devices", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "type"})})
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be between 3 and 30 characters.")
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters.")
    private String name;

    @NotBlank(message = "Type must be between 3 and 30 characters.")
    @Size(min = 3, max = 30, message = "Type must be between 3 and 30 characters.")
    private String type;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Skill> skills;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    Set<Report> reports;

    public Device() {
    }

    public Device(DevicePayload devicePayload) {
        this.name = devicePayload.getName();
        this.type = devicePayload.getType();
    }

    public Device(Device device, DevicePayload devicePayload) {
        this.id = device.getId();
        this.name = devicePayload.getName() != null ? devicePayload.getName() : device.getName();
        this.type = devicePayload.getType() != null ? devicePayload.getType() : device.getType();
        this.skills = device.getSkills();
        this.reports = device.getReports();
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
