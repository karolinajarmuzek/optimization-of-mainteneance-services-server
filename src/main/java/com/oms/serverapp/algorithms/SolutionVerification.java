package com.oms.serverapp.algorithms;

import com.oms.serverapp.OptimizationServices;
import com.oms.serverapp.model.*;
import com.oms.serverapp.util.DurationsMatrix;

import java.util.*;

public class SolutionVerification {
    private static int scheduleMinTime;
    private static int scheduleMaxTime;
    private static Map<ServiceTechnician, List<Report>> serviceTechnicianReportsMap;
    private static int totalProfit;
    private static Map<Integer, Object> indexToObjectMap;
    private static double[][] durationsInS;
    private static Map<Long, Integer> sparePartCountMap;

    private static Map<ServiceTechnician, List<Integer>> serviceTechnicianReportIdxMap = new HashMap<>();
    private static Map<Long, Integer> serviceTechnicianIdToIdxMap = new HashMap<>();
    private static Map<Long, Integer> repairTimes;
    private static String message;
    private static Boolean testing;

    public SolutionVerification(int scheduleMinTime, int scheduleMaxTime, Map<ServiceTechnician, List<Report>> serviceTechnicianReportsMap, int totalProfit, Map<Long, Integer> repairTimes, Map<Long, Integer> sparePartCountMap, Boolean testing) {
        this.scheduleMinTime = scheduleMinTime;
        this.scheduleMaxTime = scheduleMaxTime;
        this.serviceTechnicianReportsMap = serviceTechnicianReportsMap;
        this.totalProfit = totalProfit;
        this.repairTimes = repairTimes;
        this.indexToObjectMap = new HashMap<>();
        this.sparePartCountMap = sparePartCountMap;
        this.testing = testing;
    }

    public static boolean isSolutionCorrect() {
        boolean isCorrect = true;
        int profit = 0;
        Map<Long, Integer> sparePartUsedMap = new HashMap<>();

        List<SparePart> spareParts = OptimizationServices.loadSpareParts();
        for (SparePart sparePart : spareParts) {
            sparePartUsedMap.put(sparePart.getId(), sparePart.getQuantity());
        }

        prepareDurations();

        for (ServiceTechnician serviceTechnician : serviceTechnicianReportsMap.keySet()) {
            int repairTimeCalculated = 0;
            for (int i = 0; i < serviceTechnicianReportsMap.get(serviceTechnician).size(); i++) {
                Report report = serviceTechnicianReportsMap.get(serviceTechnician).get(i);
                Skill skill = OptimizationServices.getSkillByDeviceAndFailure(report.getDevice(), report.getFailure());
                int repairTime = Math.round(skill.getMinRepairTime() + (float) (10 - serviceTechnician.getExperience()) / (10 - 1) * (skill.getMaxRepairTime() - skill.getMinRepairTime()));
                int travelTime;
                if (i == 0) {
                    Integer serviceTechnicianIdx = serviceTechnicianIdToIdxMap.get(serviceTechnician.getId());
                    travelTime = (int) getDurationsInS()[serviceTechnicianIdx][serviceTechnicianReportIdxMap.get(serviceTechnician).get(i)] / 60;
                } else {
                    travelTime = (int) getDurationsInS()[serviceTechnicianReportIdxMap.get(serviceTechnician).get(i - 1)][serviceTechnicianReportIdxMap.get(serviceTechnician).get(i)] / 60;
                }
                int totalTime = repairTime + travelTime;
                profit += skill.getProfit();
                repairTimeCalculated += totalTime;
                if (testing) {
                    for (SparePartNeeded sparePartNeeded : skill.getSparePartsNeeded()) {
                        Long sparePartId = sparePartNeeded.getSparePart().getId();
                        sparePartUsedMap.put(sparePartId, sparePartUsedMap.get(sparePartId) - sparePartNeeded.getQuantity());
                    }
                }
            }
            int repairTimeProvided = repairTimes.get(serviceTechnician.getId());
            if (repairTimeProvided != repairTimeCalculated) {
                isCorrect = false;
                message = "The calculated time for serviceTechnicianId: " + serviceTechnician.getId() + " does not match the given time. Expected time: " + repairTimeCalculated + ", given time: " + repairTimeProvided;
                System.out.println(message);
                break;
            }  else if (repairTimeProvided < scheduleMinTime) {
                isCorrect = false;
                message = "The minimum time was not met. Minimal time: " + scheduleMinTime + ", actual time: " + repairTimeCalculated ;
                System.out.println(message);
                break;
            } else if (repairTimeProvided > scheduleMaxTime) {
                isCorrect = false;
                message = "The maximum time has been exceeded. Maximum time: " + scheduleMaxTime + ", actual time: " + repairTimeCalculated ;
                System.out.println(message);
                break;
            }
        }

        if (isCorrect && (profit != totalProfit)) {
            isCorrect = false;
            message = "Total profit is not correct. Expected total profit: " + profit + ", given profit: " + totalProfit;
        }

        for (Map.Entry<Long, Integer> entry : sparePartUsedMap.entrySet()) {
            if (entry.getValue() != sparePartCountMap.get(entry.getKey())) {
                isCorrect = false;
                message = "Spare part " + entry.getKey() + " quantity does not match. Expected quantity: " + entry.getValue() + ", given quantity " + sparePartCountMap.get(entry.getKey());
            }
        }

        return isCorrect;
    }


    private static void prepareDurations() {
        int numberOfServiceTechnicians = serviceTechnicianReportsMap.size();
        int numberOfReports = 0;

        for (ServiceTechnician serviceTechnician : serviceTechnicianReportsMap.keySet()) {
            numberOfReports += serviceTechnicianReportsMap.get(serviceTechnician).size();
        }

        double[][] locations = new double[numberOfServiceTechnicians + numberOfReports][2];

        int index = 0;
        for (ServiceTechnician serviceTechnician : serviceTechnicianReportsMap.keySet()) {
            indexToObjectMap.put(index, serviceTechnician);
            locations[index][0] = Double.parseDouble(serviceTechnician.getLongitude());
            locations[index][1] = Double.parseDouble(serviceTechnician.getLatitude());
            serviceTechnicianIdToIdxMap.put(serviceTechnician.getId(), index);
            index++;
        }

        for (ServiceTechnician serviceTechnician : serviceTechnicianReportsMap.keySet()) {
            List<Integer> reportIdx = new ArrayList<>();
            for (Report report : serviceTechnicianReportsMap.get(serviceTechnician)) {
                indexToObjectMap.put(index, report);
                locations[index][0] = Double.parseDouble(report.getLongitude());
                locations[index][1] = Double.parseDouble(report.getLatitude());
                reportIdx.add(index);
                index++;
            }
            serviceTechnicianReportIdxMap.put(serviceTechnician, reportIdx);
        }

        // Get the time needed to travel between two points
        DurationsMatrix durationsMatrix = new DurationsMatrix(index, locations);
        setDurationsInS(durationsMatrix.getDurations());

    }


    public static double[][] getDurationsInS() {
        return durationsInS;
    }

    public static void setDurationsInS(double[][] durationsInS) {
        SolutionVerification.durationsInS = durationsInS;
    }

    public static String getMessage() {
        return message;
    }
}
