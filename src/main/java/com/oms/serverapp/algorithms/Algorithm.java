package com.oms.serverapp.algorithms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.serverapp.OptimizationServices;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.util.RepairInfos;
import com.oms.serverapp.util.ReportStatus;
import com.oms.serverapp.util.ReportsLoader;
import com.oms.serverapp.util.ServiceTechnicianRepairInfos;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Algorithm {

    private static final boolean testing = true;                                                                // is it algorithms testing mode
    private static final boolean showMessages = false;                                                          // print messages
    private static final int shiftTime = 30;                                                                    // max repair time may be exceeded by this time [in min]

    private static ReportsLoader reportsLoader;                                                                 // reports details
    private static double[][] durationsInS;                                                                     // matrix of travel times
    private static int interval;                                                                                // interval during the day
    private static List<ServiceTechnician> serviceTechnicians;                                                  // list of service technicians
    private static Map<Long, ServiceTechnicianRepairInfos> serviceTechniciansRepairInfos = new HashMap<>();     // information about repairs for all service technicians
    private static int scheduleInterval;                                                                        // size of one schedule interval [in min]
    private static int maxRepairTime;                                                                           // max work time [in min]
    private static int totalProfit;                                                                             // total profit from all intervals

    private static int numberOfServiceTechnicians;                                                              // number of serviceTechnicians
    private static int numberOfReports;                                                                         // number of all reports

    Map<Integer, Report> reportsWithId = new HashMap<>();                                                       // cast each report to index (idx) for easy identification


    public Algorithm(int scheduleInterval, int maxRepairTime) {
        this.scheduleInterval = scheduleInterval;
        this.maxRepairTime = maxRepairTime;
        this.totalProfit = 0;
    }

    public void exec(String firstSchedule) {
        int intervals = maxRepairTime / scheduleInterval;
        if (testing) resetReportsStatuses();
        for (int i = 0; i < intervals; i++ ) {
            setInterval(i);
            prepare(firstSchedule);

            int index = 0;
            for (Report report : getReportsLoader().getReportsToSchedule()) {
                reportsWithId.put(index, report); //map reports to ids
                index++;
            }

            schedule();
        }
        System.out.println("Total profit " + getTotalProfit());
    }

    public static void prepare(String firstSchedule) {
        if (getInterval() == 0) loadServiceTechnicians();
        loadReports(firstSchedule);
        prepareTravelDurations();
    }

    public static void loadServiceTechnicians() {
        setServiceTechnicians(OptimizationServices.loadServiceTechnicians());
        ServiceTechnician admin = serviceTechnicians.stream().filter(serviceTechnician -> serviceTechnician.getFirstName().equals("Admin")).findFirst().orElse(null);
        serviceTechnicians.remove(admin); //remove admin
        serviceTechnicians.stream().forEach(serviceTechnician -> serviceTechniciansRepairInfos.put(serviceTechnician.getId(), new ServiceTechnicianRepairInfos()));
        numberOfServiceTechnicians = serviceTechniciansRepairInfos.keySet().size();
    }

    public static void loadReports(String firstSchedule) {
        // Load reports that were reported before the scheduling time
        String[] firstScheduleTime = firstSchedule.split(":");
        int scheduleTimeInMs = (((Integer.parseInt(firstScheduleTime[0])-1) * 60 + Integer.parseInt(firstScheduleTime[1])) * 60 + Integer.parseInt(firstScheduleTime[2])) * 1000 + interval * scheduleInterval * 60 * 1000;
        loadReportsToSchedule(scheduleTimeInMs);
    }

    public static void loadReportsToSchedule(int scheduleTimeInMs) {
        List<Report> reportsToSchedule = new ArrayList<>();
        Map<Report, Skill> reportSkillMap = new HashMap<>();

        List<Report> reports = OptimizationServices.loadReports();
        for (Report report: reports) {
            String[] reportTime = report.getReportTime().toString().split(":");
            int reportTimeInMs = (((Integer.parseInt(reportTime[0])-1) * 60 + Integer.parseInt(reportTime[1])) * 60 + Integer.parseInt(reportTime[2])) * 1000;

            if (reportTimeInMs <= scheduleTimeInMs && report.getStatus() == ReportStatus.REPORTED) {
                reportsToSchedule.add(report);
                Skill skill = OptimizationServices.getSkillByDeviceAndFailure(report.getDevice(), report.getFailure());
                reportSkillMap.put(report, skill);
            }
        }
        setReportsLoader(new ReportsLoader(reportsToSchedule, reportSkillMap));
        numberOfReports = reportsToSchedule.size();
    }

    public static void prepareTravelDurations() {
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
        double[][] durationsInS = getDurationsInS(locations, reportsLoader, interval);
        setDurationsInS(durationsInS);
    }

    public static double[][] getDurationsInS(double[][] locations, ReportsLoader reportsLoader, int interval) {
        // Get the time needed to travel between two points
        String durationMatrix = loadDurationMatrixFromAPI(locations);
        //String durationMatrix = "[[0.0,920.99,784.93,556.52,1505.31,776.42,1044.89],[1036.86,0.0,393.01,579.95,927.8,384.51,780.89],[796.56,367.63,0.0,306.22,908.66,90.56,868.72],[618.98,498.6,327.63,0.0,1039.16,319.12,932.76],[1688.28,1043.35,1045.72,1162.18,0.0,1002.39,1315.71],[851.05,422.11,117.08,335.32,858.62,0.0,923.21],[1055.21,688.27,869.17,908.97,1335.18,860.66,0.0]]";
        // Parse times to matrix
        double[][] durationsInS = new double[reportsLoader.getReportsToSchedule().size() + serviceTechnicians.size()][reportsLoader.getReportsToSchedule().size() + serviceTechnicians.size()];
        String[] durationMatrixStringSplit = durationMatrix.split("\\],\\[");
        for (int i = 0; i < durationMatrixStringSplit.length; i++) {
            String[] durations = durationMatrixStringSplit[i].replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            for (int j = 0; j < durations.length; j++) {
                durationsInS[i][j] = Double.parseDouble(durations[j]);
            }
        }
        return durationsInS;
    }

    public static String loadDurationMatrixFromAPI(double[][] locations){
        var body = new HashMap<String, Object>() {{
            put("locations", locations);
            put("metrics", new String[] {"duration"});
            put("resolve_locations", "false");
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openrouteservice.org/v2/matrix/driving-car"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader("Authorization", "5b3ce3597851110001cf6248655e1d2b2ab94d7caabb6d4171889110")
                .setHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println("body:" + response.body());

        Pattern p = Pattern.compile("\\[(\\[([0-9]+\\.[0-9]+.)+.)+");
        Matcher m = p.matcher(response.body());
        if (m.find())
        {
            return m.group(0);
        }

        return "";

    }

    //get profit and repair time
    public static RepairInfos getRepairInfos(Report report, int experienceOrServiceTechnicianIdx, boolean isExperience) {
        Skill skillNeeded = getReportsLoader().getReportSkillMap().get(report);
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

    public static void resetReportsStatuses() {
        List<Report> reports = OptimizationServices.loadReportsWithStatus(ReportStatus.ASSIGNED);
        for (Report report : reports) {
            report.setStatus(ReportStatus.REPORTED);
            OptimizationServices.updateReport(report);
        }
    }

    public static void updateReport(Report report) {
        OptimizationServices.updateReport(report);
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

    public static void setServiceTechniciansRepairInfos(Map<Long, ServiceTechnicianRepairInfos> serviceTechniciansRepairInfos) {
        Algorithm.serviceTechniciansRepairInfos = serviceTechniciansRepairInfos;
    }

    public static int getScheduleInterval() {
        return scheduleInterval;
    }

    public static void setScheduleInterval(int scheduleInterval) {
        Algorithm.scheduleInterval = scheduleInterval;
    }

    public static int getMaxRepairTime() {
        return maxRepairTime;
    }

    public static void setMaxRepairTime(int maxRepairTime) {
        Algorithm.maxRepairTime = maxRepairTime;
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

}