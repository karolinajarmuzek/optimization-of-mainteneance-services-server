package com.oms.serverapp.model;

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

    @NotBlank(message = "Report cannot be null")
    private String location;

    @NotBlank()
    @Size(min = 10, max = 200, message = "Description must be between 10 and 200characters.")
    private String description;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    @OneToOne
    @JoinColumn(name = "repair_id", referencedColumnName = "id")
    private Repair repair;

    public Report() {
    }

    public Report(Customer customer, Failure failure, Device device, Date date, String location, String description, String status, Repair repair) {
        this.customer = customer;
        this.failure = failure;
        this.device = device;
        this.date = date;
        this.location = location;
        this.description = description;
        this.status = status;
        this.repair = repair;
    }

    public Report(Long id, Customer customer, Failure failure, Device device, Date date, String location, String description, String status, Repair repair) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }
}
