package com.oms.serverapp.payload;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.util.RepairStatus;

import java.text.SimpleDateFormat;

public class RepairResponse {

    private Long id;
    private String date;
    private String startTime;
    private String endTime;
    private RepairStatus status;
    private ReportResponse reportResponse;

    public RepairResponse() {
    }

    public RepairResponse(Repair repair, ReportResponse reportResponse) {
        this.id = repair.getId();
        this.date = new SimpleDateFormat("dd.MM.yyyy").format(repair.getDate());
        this.startTime = repair.getStartTime();
        this.endTime = repair.getEndTime();
        this.status = repair.getStatus();
        this.reportResponse = reportResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public ReportResponse getReportResponse() {
        return reportResponse;
    }

    public void setReportResponse(ReportResponse reportResponse) {
        this.reportResponse = reportResponse;
    }
}
