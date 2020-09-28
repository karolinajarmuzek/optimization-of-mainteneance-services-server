package com.oms.serverapp.util;

import java.util.Map;

public class SolutionDetails {
    private final int profit;
    private final Map<Long, ServiceTechnicianRepairInfos> serviceTechnicianRepairInfosMap;

    public SolutionDetails(int profit, Map<Long, ServiceTechnicianRepairInfos> serviceTechnicianRepairInfosMap) {
        this.profit = profit;
        this.serviceTechnicianRepairInfosMap = serviceTechnicianRepairInfosMap;
    }

    public int getProfit() {
        return profit;
    }

    public Map<Long, ServiceTechnicianRepairInfos> getServiceTechnicianRepairInfosMap() {
        return serviceTechnicianRepairInfosMap;
    }
}
