package com.oms.serverapp.model;

import com.oms.serverapp.payload.ReportRequest;
import com.oms.serverapp.util.ReportStatus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer cannot be null.")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull(message = "Failure cannot be null.")
    @ManyToOne
    @JoinColumn(name = "failure_id")
    private Failure failure;

    @NotNull(message = "Device cannot be null.")
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @NotNull(message = "Date of submission cannot be null.")
    private Date reportDate;

    @NotNull(message = "Time of submission cannot be null.")
    private Time reportTime;

    @NotBlank(message = "Address cannot be null")
    private String address;

    @NotBlank(message = "Longitude cannot be null")
    private String longitude;

    @NotBlank(message = "Latitude cannot be null")
    private String latitude;

    @NotBlank(message = "Description must be between 10 and 115 characters.")
    @Size(min = 10, max = 115, message = "Description must be between 10 and 115 characters.")
    private String description;

    //@NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @OneToOne(mappedBy = "report")
    private Repair repair;

    public Report() {
    }

    public Report(ReportRequest reportRequest, Customer customer, Failure failure, Device device) {
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.reportDate = reportRequest.getReportDate();
        this.reportTime = reportRequest.getReportTime();
        this.address = reportRequest.getAddress();
        this.longitude = reportRequest.getLongitude();
        this.latitude = reportRequest.getLatitude();
        this.description = reportRequest.getDescription();
        this.status = reportRequest.getStatus();
    }

    public Report(Report report, ReportRequest reportRequest, Customer customer, Failure failure, Device device, Repair repair) {
        this.id = report.getId();
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.reportDate = reportRequest.getReportDate() != null ? reportRequest.getReportDate() : report.getReportDate();
        this.reportTime = reportRequest.getReportTime() != null ? reportRequest.getReportTime() : report.getReportTime();
        this.address = reportRequest.getAddress() != null ? reportRequest.getAddress() : report.getAddress();
        this.longitude = reportRequest.getLongitude() != null ? reportRequest.getLongitude() : report.getLongitude();
        this.latitude = reportRequest.getLatitude() != null ? reportRequest.getLatitude() : report.getLatitude();
        this.description = reportRequest.getDescription() != null ? reportRequest.getDescription() : report.getDescription();
        this.status = reportRequest.getStatus() != null ? reportRequest.getStatus() : report.getStatus();
    }

    public Report(Long id, Customer customer, Failure failure, Device device, Date date, Time time, String address, String longitude, String latitude, String description, ReportStatus reportStatus, Repair repair) {
        this.id = id;
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.reportDate = date;
        this.reportTime = time;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.status = reportStatus;
        this.repair = repair;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Time getReportTime() {
        return reportTime;
    }

    public void setReportTime(Time reportTime) {
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

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }
}
