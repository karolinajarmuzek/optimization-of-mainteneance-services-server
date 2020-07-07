package com.oms.serverapp.payload;

import java.util.Date;

public class ReportPayload {

    private Long id;
    private Long customer;
    private Long failure;
    private Long device;
    private Date date;
    private String location;
    private String description;
    private String status;
    private Long repair;

    public ReportPayload() {
    }

    public ReportPayload(Long id, Long customer, Long failure, Long device, Date date, String location, String description, String status, Long repair) {
        this.id = id;
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.date = date;
        this.location = location;
        this.description = description;
        this.status = status;
        this.repair = repair;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }

    public Long getFailure() {
        return failure;
    }

    public void setFailure(Long failure) {
        this.failure = failure;
    }

    public Long getDevice() {
        return device;
    }

    public void setDevice(Long device) {
        this.device = device;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRepair() {
        return repair;
    }

    public void setRepair(Long repair) {
        this.repair = repair;
    }
}
