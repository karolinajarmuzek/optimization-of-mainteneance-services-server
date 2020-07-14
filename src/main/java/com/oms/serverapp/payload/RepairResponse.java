package com.oms.serverapp.payload;

import com.oms.serverapp.model.Repair;

import java.util.Date;

public class RepairResponse {

    private Long id;
    private Date date;
    private String time;
    private String status;
    private ReportResponse reportResponse;

    public RepairResponse() {
    }

    public RepairResponse(Repair repair, ReportResponse reportResponse) {
        this.id = repair.getId();
        this.date = repair.getDate();
        this.time = repair.getTime();
        this.status = repair.getStatus();
        this.reportResponse = reportResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ReportResponse getReportResponse() {
        return reportResponse;
    }

    public void setReportResponse(ReportResponse reportResponse) {
        this.reportResponse = reportResponse;
    }
}
