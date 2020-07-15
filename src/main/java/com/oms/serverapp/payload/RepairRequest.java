package com.oms.serverapp.payload;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.util.RepairStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RepairRequest {

    private Long id;
    private Long serviceMan;
    private Date date;
    private String time;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    private Long report;

    public RepairRequest() {
    }

    public RepairRequest(Repair repair) {
        this.id = repair.getId();
        this.serviceMan = repair.getServiceMan().getId();
        this.date = repair.getDate();
        this.time = repair.getTime();
        this.status = repair.getStatus();
        if (repair.getReport() != null) { // when throw ex -> remove this
            this.report = repair.getReport().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceMan() {
        return serviceMan;
    }

    public void setServiceMan(Long serviceMan) {
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

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public Long getReport() {
        return report;
    }

    public void setReport(Long report) {
        this.report = report;
    }
}
