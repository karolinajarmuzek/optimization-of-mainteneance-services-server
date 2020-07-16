package com.oms.serverapp.payload;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.util.ReportStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ReportRequest {

    private Long id;

    @NotNull(message = "Customer cannot be null.")
    private Long customer;

    @NotNull(message = "Failure cannot be null.")
    private Long failure;

    @NotNull(message = "Device cannot be null.")
    private Long device;

    @NotNull(message = "Date of submission cannot be null.")
    private Date date;

    @NotBlank(message = "Location cannot be null")
    private String location;

    @NotBlank(message = "Description must be between 10 and 115 characters.")
    @Size(min = 10, max = 115, message = "Description must be between 10 and 200 characters.")
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private Long repair;

    public ReportRequest() {
    }

    public ReportRequest(Report report) {
        this.id = report.getId();
        this.customer = report.getCustomer().getId();
        this.failure = report.getFailure().getId();
        this.device = report.getDevice().getId();
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

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Long getRepair() {
        return repair;
    }

    public void setRepair(Long repair) {
        this.repair = repair;
    }
}
