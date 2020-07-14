package com.oms.serverapp.model;

import com.oms.serverapp.payload.RepairRequest;

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
    @JoinColumn(name = "serviceman_id")
    ServiceMan serviceMan;

    @NotNull(message = "Repair date cannot be null.")
    private Date date;

    @NotNull(message = "Repair time cannot be null.")
    private String time; ///????

    @NotNull(message = "Repair status cannot be null.")
    private String status;

    @OneToOne
    @JoinColumn(name = "report_id", referencedColumnName = "id")
    private Report report;

    public Repair() {
    }

    public Repair(RepairRequest repairRequest, ServiceMan serviceMan, Report report) {
        if (serviceMan != null) {
            this.serviceMan = serviceMan;
        }
        this.date = repairRequest.getDate();
        this.time = repairRequest.getTime();
        this.status = repairRequest.getStatus(); //Status.REPORTED ??
        if (report != null) { //report should not be null!
            this.report = report;
        }
    }

    public Repair(Repair repair, RepairRequest repairRequest, ServiceMan serviceMan, Report report) {
        this.id = repair.getId();
        if (serviceMan != null) {
            this.serviceMan = serviceMan;
        }
        this.date = repairRequest.getDate();
        this.time = repairRequest.getTime();
        this.status = repairRequest.getStatus(); //Status.REPORTED ??
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

    public ServiceMan getServiceMan() {
        return serviceMan;
    }

    public void setServiceMan(ServiceMan serviceMan) {
        this.serviceMan = serviceMan;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
