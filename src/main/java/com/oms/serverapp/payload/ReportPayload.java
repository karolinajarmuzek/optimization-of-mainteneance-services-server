package com.oms.serverapp.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ReportPayload {

    private Long id;

    @NotNull(message = "Customer cannot be null.")
    private Long customer;

    @NotNull(message = "Failure cannot be null.")
    private Long failure;

    @NotNull(message = "Device cannot be null.")
    private Long device;

    @NotNull(message = "Date of submission cannot be null.")
    private Date date;

    @NotBlank(message = "Report cannot be null")
    private String location;

    @NotBlank()
    @Size(min = 10, max = 200, message = "Description must be between 10 and 200characters.")
    private String description;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private Long repair;

    public ReportPayload() {
    }

    public ReportPayload(Long id, Long customer, Long failure, Long device, Date date, String location, String description, String status) {
        this.id = id;
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.date = date;
        this.location = location;
        this.description = description;
        this.status = status;
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
