package com.oms.serverapp.payload;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.util.RepairStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RepairRequest {

    private Long id;

    @NotNull(message = "ServiceTechnician cannot be null.")
    private Long serviceTechnician;

    @NotNull(message = "Repair date cannot be null.")
    private Date date;

    @NotNull(message = "Repair start time cannot be null.")
    private String start_time;

    @NotNull(message = "Repair end time cannot be null.")
    private String end_time;


    @NotNull(message = "Status cannot be null.")
    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @NotNull(message = "Report cannot be null.")
    private Long report;

    public RepairRequest() {
    }

    public RepairRequest(Repair repair) {
        this.id = repair.getId();
        this.serviceTechnician = repair.getServiceTechnician().getId();
        this.date = repair.getDate();
        this.start_time = repair.getStartTime();
        this.end_time = repair.getEndTime();
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

    public Long getServiceTechnician() {
        return serviceTechnician;
    }

    public void setServiceTechnician(Long serviceTechnician) {
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

    public Long getReport() {
        return report;
    }

    public void setReport(Long report) {
        this.report = report;
    }
}
