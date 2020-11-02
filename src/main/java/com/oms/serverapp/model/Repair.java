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

    @NotNull(message = "Repair start time cannot be null.")
    private String start_time;

    @NotNull(message = "Repair end time cannot be null.")
    private String end_time;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @OneToOne
    @JoinColumn(name = "report_id", referencedColumnName = "id")
    private Report report;

    public Repair() {
    }

    public Repair(ServiceTechnician serviceTechnician, Date date, String startTime, String endTime, Report report, RepairStatus repairStatus) {
        this.serviceTechnician = serviceTechnician;
        this.date = date;
        this.start_time = startTime;
        this.end_time = endTime;
        this.report = report;
        this.status = repairStatus;
    }

    public Repair(RepairRequest repairRequest, ServiceTechnician serviceTechnician, Report report) {
        if (serviceTechnician != null) {
            this.serviceTechnician = serviceTechnician;
        }
        this.date = repairRequest.getDate();
        this.start_time = repairRequest.getStartTime();
        this.end_time = repairRequest.getEndTime();
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
        this.start_time = repairRequest.getStartTime() != null ? repairRequest.getStartTime() : repair.getStartTime();
        this.end_time = repairRequest.getEndTime() != null ? repairRequest.getEndTime() : repair.getEndTime();
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

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
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
