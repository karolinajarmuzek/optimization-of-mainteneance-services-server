package com.oms.serverapp.model;

import com.oms.serverapp.payload.RepairRequest;
import com.oms.serverapp.util.RepairStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "repairs")
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servicetechnician_id")
    ServiceTechnician serviceTechnician;

    @NotNull(message = "Repair date cannot be null.")
    private Date date;

    @NotNull(message = "Repair time cannot be null.")
    private String time; ///????

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @OneToOne
    @JoinColumn(name = "report_id", referencedColumnName = "id")
    private Report report;

    public Repair() {
    }

    public Repair(RepairRequest repairRequest, ServiceTechnician serviceTechnician, Report report) {
        if (serviceTechnician != null) {
            this.serviceTechnician = serviceTechnician;
        }
        this.date = repairRequest.getDate();
        this.time = repairRequest.getTime();
        this.status = repairRequest.getStatus(); //Status.REPORTED ??
        if (report != null) { //report should not be null!
            this.report = report;
        }
    }

    public Repair(Repair repair, RepairRequest repairRequest, ServiceTechnician serviceTechnician, Report report) {
        this.id = repair.getId();
        if (serviceTechnician != null) {
            this.serviceTechnician = serviceTechnician;
        }
        this.date = repairRequest.getDate() != null ? repairRequest.getDate() : repair.getDate();
        this.date = repairRequest.getDate() != null ? repairRequest.getDate() : repair.getDate();
        this.time = repairRequest.getTime() != null ? repairRequest.getTime() : repair.getTime();
        this.status = repairRequest.getStatus() != null ? repairRequest.getStatus() : repair.getStatus();
        if (report != null) {
            this.report = report;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceTechnician getServiceTechnician() {
        return serviceTechnician;
    }

    public void setServiceTechnician(ServiceTechnician serviceTechnician) {
        this.serviceTechnician = serviceTechnician;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
