package com.oms.serverapp.payload;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class RepairPayload {

    private Long id;
    private Long serviceMan;

    @NotNull(message = "Repair date cannot be null.")
    private Date date;

    @NotNull(message = "Repair time cannot be null.")
    private String time;

    @NotNull(message = "Repair status cannot be null.")
    private String status;

    private Long report;

    public RepairPayload() {
    }

    public RepairPayload(Long id, Long serviceMan, Date date, String time, String status, Long report) {
        this.id = id;
        this.serviceMan = serviceMan;
        this.date = date;
        this.time = time;
        this.status = status;
        this.report = report;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getReport() {
        return report;
    }

    public void setReport(Long report) {
        this.report = report;
    }
}
