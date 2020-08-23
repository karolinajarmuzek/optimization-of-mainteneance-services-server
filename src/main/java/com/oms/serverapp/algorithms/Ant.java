package com.oms.serverapp.algorithms;

import java.io.Serializable;
import java.util.Arrays;

// each ant represents the entire solution (it consists of the current number of service technicians)
public class Ant implements Serializable {
    protected int[][] trail;        // list of visited places for each service technician
    protected boolean[] visited;    // list of all places marked whether they have been visited (for all service technicians)
    protected int[] repairTimes;    // list of repair times (for each service technician)
    protected Integer profit;       // total profit

    public Ant(int serviceTechniciansCount, int reportsCount) {
        trail = new int[serviceTechniciansCount][reportsCount];
        visited = new boolean[serviceTechniciansCount + reportsCount];
        repairTimes = new int[serviceTechniciansCount];
        Arrays.fill(repairTimes, 0);
        for (int[] serviceTechnician: trail) {
            Arrays.fill(serviceTechnician, -1);
        }
        profit = 0;
    }

    protected void visitRepair(int serviceTechnician, int index, int report, int time, Integer repProfit) {
        trail[serviceTechnician][index + 1] = report;
        visited[report] = true;
        repairTimes[serviceTechnician] += time;
        profit += repProfit;
    }

    protected boolean isVisited(int i) {
        return visited[i];
    }

    protected void clear() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
        for (int i = 0; i < repairTimes.length; i++) {
            repairTimes[i] = 0;
        }
        profit = 0;
    }

    public int[] getRepairTimes() {
        return repairTimes;
    }
}
