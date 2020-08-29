package com.oms.serverapp.algorithms;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.util.RepairInfos;
import com.oms.serverapp.util.ReportStatus;
import java.util.*;

public class GreedyAlgorithm extends Algorithm {

    private List<Report> reportsSorted;
    private List<Integer> reportsSortedIds;
    private Map<Long, Integer> serviceTechnicianIdToIntegerMap;


    public GreedyAlgorithm(int scheduleInterval, int maxRepairTime) {
        super(scheduleInterval, maxRepairTime);
    }

    @Override
    void schedule() {
        int profitFromInterval = 0;
        Map<Long, Boolean> firstAssigment = new HashMap<>();

        for (ServiceTechnician serviceTechnician: getServiceTechnicians()) {
            if (!serviceTechnician.getFirstName().equals("Admin"))
                firstAssigment.put(serviceTechnician.getId(), true);
        }

        sortReports();
        mapServiceTechnicians();

        for (Integer reportIdx: reportsSortedIds) {
            Report report = getReportsWithId().get(reportIdx);
            Skill skillNeeded = getReportsLoader().getReportSkillMap().get(report);

            List<ServiceTechnician> serviceTechniciansSorted = new ArrayList<>(skillNeeded.getServiceTechnician());
            // list of serviceTechnicians sorted based on serviceTechnician experience
            serviceTechniciansSorted.sort(new Comparator<ServiceTechnician>() {
                @Override
                public int compare(ServiceTechnician s1, ServiceTechnician s2) {
                    return s2.getExperience().compareTo(s1.getExperience());
                }
            });

            for (ServiceTechnician serviceTechnician : serviceTechniciansSorted) {
                int previousRepairsTime = getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getRepairsTime();
                if (previousRepairsTime < (getScheduleInterval() * (getInterval() + 1))) {
                    // get repair infos
                    RepairInfos repairInfos = getRepairInfos(report, serviceTechnician.getExperience(), true);

                    // Get time needed to travel between two locations based on report or serviceTechnician location
                    int destinationLocationIdx = reportIdx + getNumberOfServiceTechnicians();
                    int actualLocationIdx;
                    Report lastReport = getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getLastReport();
                    if (lastReport == null ||  firstAssigment.get(serviceTechnician.getId())) {
                        actualLocationIdx = serviceTechnicianIdToIntegerMap.get(serviceTechnician.getId());
                    } else {
                        int previousReportIdx = -1;
                        for (Map.Entry<Integer, Report> entry : getReportsWithId().entrySet()) {
                            if (entry.getValue().getId() == getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getLastReport().getId()) {
                                previousReportIdx = entry.getKey();
                                break;
                            }
                        }

                        actualLocationIdx =  previousReportIdx + getNumberOfServiceTechnicians();
                    }

                    int travelTime = (int) getDurationsInS()[actualLocationIdx][destinationLocationIdx] / 60;

                    // Time needed for repair and travel
                    int totalTime = repairInfos.getRepairTime() + travelTime;

                    // Check if serviceTechnician can handle the repair
                    int maxTime;
                    if ((getScheduleInterval() * (getInterval() + 1) * 1.3) < 480) {
                        maxTime = (int) (getScheduleInterval() * (getInterval() + 1) * 1.3);
                    } else {
                        maxTime = getMaxRepairTime() + getShiftTime();
                    }


                    //check if fixing report will not exceed max time for current schedule
                    if ((previousRepairsTime + totalTime <= maxTime) || (firstAssigment.get(serviceTechnician.getId()) && (previousRepairsTime + totalTime <= getMaxRepairTime() + getShiftTime()))) {
                        getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).setRepairsTime(previousRepairsTime + totalTime);
                        getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).setLastReport(report);
                        getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getAssignedReports().add(report);
                        report.setStatus(ReportStatus.ASSIGNED);
                        updateReport(report);
                        firstAssigment.put(serviceTechnician.getId(), false);

                        // TO DO
                        // if (!isTesting()) -> create repair

                        profitFromInterval += skillNeeded.getProfit();
                        if (isShowMessages()) System.out.println("Service technician " + serviceTechnician.getId() + " new rep " +  report.getId() + " total time " + totalTime);
                        break;
                    }
                }
            }
        }
        if (isShowMessages()) System.out.println("Interval " + getInterval() + " profit " + profitFromInterval);
        setTotalProfit(getTotalProfit() + profitFromInterval);
    }

    private void sortReports() {
        // list of reports sorted based on profit
        reportsSorted = new ArrayList<>(getReportsLoader().getReportsToSchedule());
        reportsSorted.sort(new Comparator<Report>() {
            @Override
            public int compare(Report r1, Report r2) {
                Skill s1 = getReportsLoader().getReportSkillMap().get(r1);
                Skill s2 = getReportsLoader().getReportSkillMap().get(r2);
                Integer v1 = s1.getProfit() / (s1.getMaxRepairTime() / 60  + 1);
                Integer v2 = s2.getProfit() / (s2.getMaxRepairTime() / 60 + 1);
                return v2.compareTo(v1);
            }
        });

        reportsSortedIds = new ArrayList<>();
        for (Report report : reportsSorted) {
            for (Map.Entry<Integer, Report> entry : getReportsWithId().entrySet()) {
                if (entry.getValue().getId() == report.getId()) {
                    int reportIdxOnInitialList = entry.getKey();
                    reportsSortedIds.add(reportIdxOnInitialList);
                }
            }
        }
    }

    private void mapServiceTechnicians() {
        serviceTechnicianIdToIntegerMap = new HashMap<>();
        int tempIdx = 0;
        for (ServiceTechnician serviceTechnician : getServiceTechnicians()) {
            serviceTechnicianIdToIntegerMap.put(serviceTechnician.getId(), tempIdx);
            tempIdx++;
        }
    }

}
