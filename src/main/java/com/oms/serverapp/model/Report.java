package com.oms.serverapp.model;

import com.oms.serverapp.payload.ReportRequest;
import com.oms.serverapp.util.ReportStatus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private Date date;

    @NotBlank(message = "Location cannot be null")
    private String location;

    @NotBlank(message = "Description must be between 10 and 200c haracters.")
    @Size(min = 10, max = 200, message = "Description must be between 10 and 200 characters.")
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
        this.date = reportRequest.getDate();
        this.location = reportRequest.getLocation();
        this.description = reportRequest.getDescription();
        this.status = reportRequest.getStatus();
    }

    public Report(Report report, ReportRequest reportRequest, Customer customer, Failure failure, Device device, Repair repair) {
        this.id = report.getId();
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.date = reportRequest.getDate() != null ? reportRequest.getDate() : report.getDate();
        this.location = reportRequest.getLocation() != null ? reportRequest.getLocation() : report.getLocation();
        this.description = reportRequest.getDescription() != null ? reportRequest.getDescription() : report.getDescription();
        this.status = reportRequest.getStatus() != null ? reportRequest.getStatus() : report.getStatus();
    }

    public Report(Long id, Customer customer, Failure failure, Device device, Date date, String location, String description, ReportStatus reportStatus, Repair repair) {
        this.id = id;
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.date = date;
        this.location = location;
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

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }
}
