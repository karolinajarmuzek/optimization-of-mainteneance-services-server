package com.oms.serverapp.algorithms;

import com.oms.serverapp.OptimizationServices;
import com.oms.serverapp.model.*;
import com.oms.serverapp.util.*;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Algorithm {

    private static boolean testing;                                                                             // is it algorithms testing mode
    private static final boolean showMessages = false;                                                          // whether to print messages
    private static final int shiftTime = 30;                                                                    // max repair time may be exceeded by this time [in min]

    private static ReportsLoader reportsLoader;                                                                 // reports details
    private static double[][] durationsInS;                                                                     // matrix of travel times
    private static int interval;                                                                                // interval during the day
    private static List<ServiceTechnician> serviceTechnicians;                                                  // list of service technicians
    private static Map<Long, ServiceTechnicianRepairInfos> serviceTechniciansRepairInfos = new HashMap<>();     // information about repairs for all service technicians
    private static int scheduleInterval;                                                                        // size of one schedule interval [in min]
    private static int maxRepairTime;                                                                           // max work time [in min]
    private static int totalProfit;                                                                             // total profit from all intervals
    private static Map<Long, Integer> sparePartCountMap;                                                        // the number of available spare parts of a given type (spare part id)

    private static int numberOfServiceTechnicians;                                                              // number of serviceTechnicians
    private static int numberOfReports;                                                                         // number of all reports

    private static String firstSchedule;                                                                        // first schedule time
    private static String startTimeOfWork;                                                                      // time when service technicians start work

    private static Map<Integer, Report> reportsWithId = new HashMap<>();                                        // cast each report to index (idx) for easy identification

    private static double[][] allDurations;                                                                     // matrix with all durations (for testing purposes)

    public Algorithm(int scheduleInterval, int maxRepairTime, boolean isTesting, String firstSchedule, String startTimeOfWork, double[][] allDurations) {
        this.scheduleInterval = scheduleInterval;
        this.maxRepairTime = maxRepairTime;
        this.totalProfit = 0;
        this.testing = isTesting;
        this.firstSchedule = firstSchedule;
        this.startTimeOfWork = startTimeOfWork;
        if (allDurations != null) this.allDurations = allDurations;
    }

    public void exec() {
        loadSpareParts();
        if (testing) {
           runInTestingMode();
        } else {
            runInNormalMode();
        }
        System.out.println("Total profit " + getTotalProfit());
        for (Map.Entry<Long, ServiceTechnicianRepairInfos> entry : getServiceTechniciansRepairInfos().entrySet()) {
            System.out.println("ServiceTechnician Id " + entry.getKey() + " time " + getServiceTechniciansRepairInfos().get(entry.getKey()).getRepairsTime());
            for (Report report : entry.getValue().getAssignedReports()) {
                System.out.println("reportId " + report.getId());
            }
        }
        checkSolution();
    }

    public SolutionDetails tests() {
        loadSpareParts();
        runInTestingMode();
        Boolean isSolutionCorrect = checkSolution();
        if (isSolutionCorrect) {
            return new SolutionDetails(getTotalProfit(), getServiceTechniciansRepairInfos());
        } else {
            return null;
        }
    }

    private void runInTestingMode() {
        int intervals = maxRepairTime / scheduleInterval;
        resetReportsStatuses();
        for (int i = 0; i < intervals; i++) {
            reportsWithId = new HashMap<>();
            setInterval(i);
            prepare(firstSchedule);

            int index = 0;
            for (Report report : getReportsLoader().getReportsToSchedule()) {
                reportsWithId.put(index, report); //map reports to ids
                index++;
            }
            schedule();
        }
    }

    private void runInNormalMode() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        int startTime = timeInMs(firstSchedule);
        int time = timeInMs(formatter.format(date));
        int interval = (int) Math.floor((time - startTime) / (scheduleInterval * 60 * 1000));
        loadServiceTechnicians();
        fillServiceTechnicianRepairInfos(startTimeOfWork);
        setInterval(interval);
        prepare(firstSchedule);
        int index = 0;
        for (Report report : getReportsLoader().getReportsToSchedule()) {
            reportsWithId.put(index, report); //map reports to ids
            index++;
        }
        schedule();
    }

    private static void prepare(String firstSchedule) {
        if (getInterval() == 0 || !isTesting()) loadServiceTechnicians();
        loadReports(firstSchedule);
        if (testing && allDurations != null) prepareTravelDurationsTesting();
        else prepareTravelDurations();
    }

    private static void loadServiceTechnicians() {
        setServiceTechnicians(OptimizationServices.loadServiceTechnicians());
        ServiceTechnician admin = serviceTechnicians.stream().filter(serviceTechnician -> serviceTechnician.getFirstName().equals("Admin")).findFirst().orElse(null);
        serviceTechnicians.remove(admin); //remove admin
        serviceTechnicians.stream().forEach(serviceTechnician -> serviceTechniciansRepairInfos.put(serviceTechnician.getId(), new ServiceTechnicianRepairInfos()));
        numberOfServiceTechnicians = serviceTechniciansRepairInfos.keySet().size();
    }

    private static void loadReports(String firstSchedule) {
        // Load reports that were reported before the scheduling time
        int scheduleTimeInMs;
        if (testing) {
            scheduleTimeInMs = timeInMs(firstSchedule) + interval * scheduleInterval * 60 * 1000;
        } else {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            scheduleTimeInMs = timeInMs(formatter.format(date));
        }
        loadReportsToSchedule(scheduleTimeInMs);
    }

    private static void loadReportsToSchedule(int scheduleTimeInMs) {
        List<Report> reportsToSchedule = new ArrayList<>();
        Map<Report, Skill> reportSkillMap = new HashMap<>();

        List<Report> reports = OptimizationServices.loadReports();
        for (Report report: reports) {
            int reportTimeInMs = timeInMs(report.getReportTime().toString());

            if (reportTimeInMs <= scheduleTimeInMs && report.getStatus() == ReportStatus.REPORTED) {
                reportsToSchedule.add(report);
                Skill skill = OptimizationServices.getSkillByDeviceAndFailure(report.getDevice(), report.getFailure());
                reportSkillMap.put(report, skill);
            }
        }
        setReportsLoader(new ReportsLoader(reportsToSchedule, reportSkillMap));
        numberOfReports = reportsToSchedule.size();
    }

    private static void loadSpareParts() {
        List<SparePart> spareParts = OptimizationServices.loadSpareParts();
        sparePartCountMap = new HashMap<>();
        for (SparePart sparePart : spareParts) {
            sparePartCountMap.put(sparePart.getId(), sparePart.getQuantity());
        }
    }

    private static void fillServiceTechnicianRepairInfos(String startTimeOfWork) {
        for (ServiceTechnician serviceTechnician : serviceTechnicians) {
            Repair lastRepair = OptimizationServices.getRepairByServiceTechnician(serviceTechnician.getId());
            List<Repair> repairsNotFinished = OptimizationServices.getRepairsNotFinished(serviceTechnician);
            for (Repair repair: repairsNotFinished) {
                if (lastRepair != null && repair.getId() == lastRepair.getId()) repair.setStatus(RepairStatus.REPAIRING);
                else repair.setStatus(RepairStatus.FINISHED);
                Report report = repair.getReport();
                report.setStatus(ReportStatus.FINISHED);
                OptimizationServices.updateRepair(repair);
                OptimizationServices.updateReport(report);
            }
            int timeInMin = 0;
            if (lastRepair != null) {
                Skill skillNeeded = OptimizationServices.getSkillByDeviceAndFailure(lastRepair.getReport().getDevice(), lastRepair.getReport().getFailure());
                RepairInfos repairInfos = getRepairInfos(skillNeeded, serviceTechnician.getExperience(), true);

                String[] time = lastRepair.getTime().split(":");
                String[] startTime = startTimeOfWork.split(":");
                timeInMin = Integer.parseInt(time[0]) * 60 +Integer.parseInt(time[1]) - (Integer.parseInt(startTime[0]) * 60 +Integer.parseInt(startTime[1])) + repairInfos.getRepairTime();
            }
            serviceTechniciansRepairInfos.get(serviceTechnician.getId()).setLastReport(lastRepair != null ? lastRepair.getReport() : null);
            serviceTechniciansRepairInfos.get(serviceTechnician.getId()).setRepairsTime(timeInMin);
        }
    }

    private static void prepareTravelDurations() {
        // Create array with all locations (reports locations and serviceTechnician locations - previous report location or start location)
        double[][] locations = new double[reportsLoader.getReportsToSchedule().size() + serviceTechnicians.size()][2];
        for (int i = 0; i < serviceTechnicians.size(); i++) {
            // If serviceTechnician starts from startLocation - the first repair of the day
            if (serviceTechniciansRepairInfos.get(serviceTechnicians.get(i).getId()).getLastReport() == null) {
                locations[i][0] = Double.parseDouble(serviceTechnicians.get(i).getLongitude());
                locations[i][1] = Double.parseDouble(serviceTechnicians.get(i).getLatitude());
            }
            // Last repair location in the previous scheduling
            else {
                locations[i][0] = Double.parseDouble(serviceTechniciansRepairInfos.get(serviceTechnicians.get(i).getId()).getLastReport().getLongitude());
                locations[i][1] = Double.parseDouble(serviceTechniciansRepairInfos.get(serviceTechnicians.get(i).getId()).getLastReport().getLatitude());
            }
        }
        // Reports locations
        for (int i = 0; i < reportsLoader.getReportsToSchedule().size(); i++) {
            locations[i + serviceTechnicians.size()][0] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLongitude());
            locations[i + serviceTechnicians.size()][1] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLatitude());
        }
        // Get the time needed to travel between two points
        DurationsMatrix durationsMatrix = new DurationsMatrix(serviceTechnicians.size() + reportsLoader.getReportsToSchedule().size(), locations);
        setDurationsInS(durationsMatrix.getDurations());
    }

    private static void prepareTravelDurationsTesting() {
        List<Report> allReports = OptimizationServices.loadReports();
        int actualSize = reportsLoader.getReportsToSchedule().size() + serviceTechnicians.size();

        Map<Long, Integer> serviceTechniciansMap = new HashMap<>();
        Map<Long, Integer> reportsMap = new HashMap<>();
        int temp = 0;
        for (ServiceTechnician serviceTechnician : serviceTechnicians) {
            serviceTechniciansMap.put(serviceTechnician.getId(), temp);
            temp++;
        }
        for (Report report : allReports) {
            reportsMap.put(report.getId(), temp);
            temp++;
        }

        int[] ids = new int[actualSize];
        temp = 0;
        for (ServiceTechnician serviceTechnician : serviceTechnicians) {
            if (serviceTechniciansRepairInfos.get(serviceTechnician.getId()).getLastReport() == null) {
                ids[temp] = serviceTechniciansMap.get(serviceTechnician.getId());
            } else {
                ids[temp] = reportsMap.get(serviceTechniciansRepairInfos.get(serviceTechnician.getId()).getLastReport().getId());
            }
            temp++;
        }
        for (Report report : reportsLoader.getReportsToSchedule()) {
            ids[temp] = reportsMap.get(report.getId());
            temp++;
        }
        double[][] durations = new double[actualSize][actualSize];
        for (int i = 0; i < actualSize; i++) {
            for (int j = 0; j < actualSize; j++) {
                durations[i][j] = allDurations[ids[i]][ids[j]];
            }
        }
        setDurationsInS(durations);
    }

    //get profit and repair time
    public static RepairInfos getRepairInfos(Skill skillNeeded, int experienceOrServiceTechnicianIdx, boolean isExperience) {
        int profit = skillNeeded.getProfit();
        int minRepairTime = skillNeeded.getMinRepairTime();
        int maxRepairTime = skillNeeded.getMaxRepairTime();
        int experience;
        if (isExperience) experience = experienceOrServiceTechnicianIdx;
        else experience = getServiceTechnicians().get(experienceOrServiceTechnicianIdx).getExperience();
        int repairTime = Math.round(minRepairTime + (float) (10 - experience) / (10 - 1) * (maxRepairTime - minRepairTime));
        RepairInfos repairInfos = new RepairInfos(profit, repairTime);
        return repairInfos;
    }

    private static void resetReportsStatuses() {
        List<Report> reports = OptimizationServices.loadReportsWithStatus(ReportStatus.ASSIGNED);
        for (Report report : reports) {
            report.setStatus(ReportStatus.REPORTED);
            OptimizationServices.updateReport(report);
        }
    }

    public static void updateReport(Report report) {
        OptimizationServices.updateReport(report);
    }

    private static Boolean checkSolution() {
        Map<ServiceTechnician, List<Report>> serviceTechnicianListMap = new HashMap<>();
        Map<Long, Integer> repairTimes = new HashMap<>();
        for (ServiceTechnician serviceTechnician : serviceTechnicians) {
            if (!serviceTechnician.getFirstName().equals("Admin")) {
                serviceTechnicianListMap.put(serviceTechnician, getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getAssignedReports());
                repairTimes.put(serviceTechnician.getId(), getServiceTechniciansRepairInfos().get(serviceTechnician.getId()).getRepairsTime());
            }

        }
        SolutionVerification solutionVerification = new SolutionVerification(getMaxRepairTime() - scheduleInterval, getMaxRepairTime() + getShiftTime(), serviceTechnicianListMap, getTotalProfit(), repairTimes, getSparePartCountMap(), testing, allDurations);
        boolean isSolutionCorrect = solutionVerification.isSolutionCorrect();
        System.out.println("Is solution correct: " + isSolutionCorrect);
        if (!isSolutionCorrect) System.out.println(solutionVerification.getMessage());
        return isSolutionCorrect;

    }

    public void addRepair(int previousRepairsTime, ServiceTechnician serviceTechnician, Report report) {
        String[] startTime = getStartTimeOfWork().split(":");

        String time = "";
        int hours = previousRepairsTime / 60 + Integer.parseInt(startTime[0]);
        if (String.valueOf(hours).length() == 1) {
            time = "0" + hours + ":";
        } else {
            time = hours + ":";
        }
        int minutes = previousRepairsTime % 60 + Integer.parseInt(startTime[1]);
        if (String.valueOf(minutes).length() == 1) {
            if (minutes == 0)
                time = time + minutes + "0";
            else
                time =  time + "0" + minutes;
        } else {
            time += minutes;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hours, minutes, 0 );
        Date date = calendar.getTime();
        RepairStatus repairStatus = RepairStatus.PENDING;
        if (previousRepairsTime == 0)  repairStatus = RepairStatus.REPAIRING; // if it is a first repair - set status to REPAIRING
        Repair repair = new Repair(serviceTechnician, date, time, report, repairStatus);
        OptimizationServices.addRepair(repair);
        for (SparePartNeeded sparePartNeeded : getReportsLoader().getReportSkillMap().get(report).getSparePartsNeeded()) {
            SparePart sparePart = OptimizationServices.getSparePartById(sparePartNeeded.getSparePart().getId());
            sparePart.setQuantity(sparePart.getQuantity() - sparePartNeeded.getQuantity());
            OptimizationServices.updateSparePart(sparePart);
        }
    }

    private static Integer timeInMs(String time) {
        String[] timeParts = time.split(":");
        return (((Integer.parseInt(timeParts[0]) - 1) * 60 + Integer.parseInt(timeParts[1])) * 60 + Integer.parseInt(timeParts[2])) * 1000;
    }

    abstract void schedule();

    public static ReportsLoader getReportsLoader() {
        return reportsLoader;
    }

    public static void setReportsLoader(ReportsLoader reportsLoader) {
        Algorithm.reportsLoader = reportsLoader;
    }

    public static double[][] getDurationsInS() {
        return durationsInS;
    }

    public static void setDurationsInS(double[][] durationsInS) {
        Algorithm.durationsInS = durationsInS;
    }

    public static int getInterval() {
        return interval;
    }

    public static void setInterval(int interval) {
        Algorithm.interval = interval;
    }

    public static List<ServiceTechnician> getServiceTechnicians() {
        return serviceTechnicians;
    }

    public static void setServiceTechnicians(List<ServiceTechnician> serviceTechnicians) {
        Algorithm.serviceTechnicians = serviceTechnicians;
    }

    public static Map<Long, ServiceTechnicianRepairInfos> getServiceTechniciansRepairInfos() {
        return serviceTechniciansRepairInfos;
    }

    public static int getScheduleInterval() {
        return scheduleInterval;
    }
    public static int getMaxRepairTime() {
        return maxRepairTime;
    }

    public static int getTotalProfit() {
        return totalProfit;
    }

    public static void setTotalProfit(int totalProfit) {
        Algorithm.totalProfit = totalProfit;
    }

    public static boolean isTesting() {
        return testing;
    }

    public static boolean isShowMessages() {
        return showMessages;
    }

    public static int getShiftTime() {
        return shiftTime;
    }

    public int getNumberOfServiceTechnicians() {
        return numberOfServiceTechnicians;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public static Map<Integer, Report> getReportsWithId() {
        return reportsWithId;
    }

    public static Map<Long, Integer> getSparePartCountMap() {
        return sparePartCountMap;
    }

    public static String getStartTimeOfWork() {
        return startTimeOfWork;
    }
}
