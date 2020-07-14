package com.oms.serverapp.payload;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.util.Status;

import java.util.Date;

public class ReportResponse {
    private Long id;
    private CustomerPayload customerPayload;
    private FailurePayload failurePayload;
    private DevicePayload devicePayload;
    private Date date;
    private String location;
    private String description;
    private Status status;
    private Long repair;

    public ReportResponse() {
    }

    public ReportResponse(Report report, CustomerPayload customerPayload, FailurePayload failurePayload, DevicePayload devicePayload) {
        this.id = report.getId();
        this.customerPayload = customerPayload;
        this.failurePayload = failurePayload;
        this.devicePayload = devicePayload;
        this.date = report.getDate();
        this.location = report.getLocation();
        this.description = report.getDescription();
        this.status = report.getStatus();
        if (report.getRepair() != null) {
            this.repair = report.getRepair().getId();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerPayload getCustomerPayload() {
        return customerPayload;
    }

    public void setCustomerPayload(CustomerPayload customerPayload) {
        this.customerPayload = customerPayload;
    }

    public FailurePayload getFailurePayload() {
        return failurePayload;
    }

    public void setFailurePayload(FailurePayload failurePayload) {
        this.failurePayload = failurePayload;
    }

    public DevicePayload getDevicePayload() {
        return devicePayload;
    }

    public void setDevicePayload(DevicePayload devicePayload) {
        this.devicePayload = devicePayload;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getRepair() {
        return repair;
    }

    public void setRepair(Long repair) {
        this.repair = repair;
    }
}
