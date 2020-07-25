package com.oms.serverapp.payload;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.util.ReportStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportResponse {
    private Long id;
    private CustomerPayload customerPayload;
    private FailurePayload failurePayload;
    private DevicePayload devicePayload;
    private String reportDate;
    private String reportTime;
    private String address;
    private String longitude;
    private String latitude;
    private String description;
    private ReportStatus status;
    private Long repair;

    public ReportResponse() {
    }

    public ReportResponse(Report report, CustomerPayload customerPayload, FailurePayload failurePayload, DevicePayload devicePayload) {
        this.id = report.getId();
        this.customerPayload = customerPayload;
        this.failurePayload = failurePayload;
        this.devicePayload = devicePayload;
        this.reportDate = new SimpleDateFormat("dd.MM.yyyy").format(report.getReportDate());
        this.reportTime = report.getReportTime().toString();
        this.address = report.getAddress();
        this.longitude = report.getLongitude();
        this.latitude = report.getLatitude();
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

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
