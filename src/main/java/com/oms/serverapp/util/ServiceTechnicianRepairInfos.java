package com.oms.serverapp.util;

import com.oms.serverapp.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ServiceTechnicianRepairInfos {
    private int repairsTime;
    private Report lastReport = null; //if null - begins from start localization
    private List<Report> assignedReports;

    public ServiceTechnicianRepairInfos() {
        this.repairsTime = 0;
        this.assignedReports = new ArrayList<>();
    }

    public int getRepairsTime() {
        return repairsTime;
    }

    public void setRepairsTime(int repairsTime) {
        this.repairsTime = repairsTime;
    }

    public Report getLastReport() {
        return lastReport;
    }

    public void setLastReport(Report lastReport) {
        this.lastReport = lastReport;
    }

    public List<Report> getAssignedReports() {
        return assignedReports;
    }

    public void setAssignedReports(List<Report> assignedReports) {
        this.assignedReports = assignedReports;
    }
}
