package com.oms.serverapp.util;

public class RepairInfos {

    private final int profit;
    private final int repairTime;

    public RepairInfos(int profit, int repairTime) {
        this.profit = profit;
        this.repairTime = repairTime;
    }

    public int getProfit() {
        return profit;
    }

    public int getRepairTime() {
        return repairTime;
    }
}
