package com.oms.serverapp.util;

import com.oms.serverapp.model.Report;

public class ServiceManRepairInfos {
    private int repairsTime;
    private Report lastReport = null; //if null - begins from start localization

    public ServiceManRepairInfos() {
        this.repairsTime = 0;
    }

    public ServiceManRepairInfos(int repairsTime, Report lastReport) {
        this.repairsTime = repairsTime;
        this.lastReport = lastReport;
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
}
