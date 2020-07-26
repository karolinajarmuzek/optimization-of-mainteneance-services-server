package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.serverapp.model.*;
import com.oms.serverapp.repository.ReportRepository;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.repository.SkillRepository;
import com.oms.serverapp.util.ReportStatus;
import com.oms.serverapp.util.ReportsLoader;
import com.oms.serverapp.util.ServiceManRepairInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Optimization {

    private static ReportRepository reportRepository;
    private static ServiceManRepository serviceManRepository;
    private static SkillRepository skillRepository;
    private static String firstSchedule = "7:50:00";
    private static int scheduleInterval; //min
    private static int maxRepairTime = 480; //min
    //serviceManRepairInfos is Map where:
    // key is serviceman id
    // value is ServiceManRepairInfos object which contains:
    //// serviceman time spend for all repairs
    //// serviceman last report location (if 0 -> begins from start location)
    private static Map<Long, ServiceManRepairInfos> serviceManRepairInfos = new HashMap<>();
    private static List<ServiceMan> serviceMen = new ArrayList<>();

    @Autowired
    public Optimization(ReportRepository reportRepository, ServiceManRepository serviceManRepository, SkillRepository skillRepository) {
        this.reportRepository = reportRepository;
        this.serviceManRepository =  serviceManRepository;
        this.skillRepository = skillRepository;
    }

    public static void schedule() {
        int intervals = 4;
        loadServiceMen();
        setScheduleInterval(maxRepairTime / intervals);
        //for (int i = 0; i < intervals; i++) {
            for (int i = 0; i < 2; i++) {
            scheduleInterval(i);
        }
    }

    public static void scheduleInterval(int interval) {

        // Load reports that were reported before the scheduling time
        String[] firstScheduleTime = firstSchedule.split(":");
        int scheduleTimeInMs = (((Integer.parseInt(firstScheduleTime[0])-1) * 60 + Integer.parseInt(firstScheduleTime[1])) * 60 + Integer.parseInt(firstScheduleTime[2])) * 1000 + interval * scheduleInterval * 60 * 1000;
        ReportsLoader reportsLoader = loadReportsToSchedule(scheduleTimeInMs);

        // Create array with all locations (reports locations and serviceman locations - previous report location or start location)
        double[][] locations = new double[reportsLoader.getReportsToSchedule().size() + serviceMen.size()][2];
        // Reports locations
        for (int i = 0; i < reportsLoader.getReportsToSchedule().size(); i++) {
            locations[i][0] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLongitude());
            locations[i][1] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLatitude());
        }
        for (int i = 0; i < serviceMen.size(); i++) {
            // If serviceman starts from startLocation - the first repair of the day
            if (serviceManRepairInfos.get(serviceMen.get(i).getId()).getLastReport() == null) {
                locations[i + reportsLoader.getReportsToSchedule().size()][0] = Double.parseDouble(serviceMen.get(i).getLongitude());
                locations[i + reportsLoader.getReportsToSchedule().size()][1] = Double.parseDouble(serviceMen.get(i).getLatitude());
            }
            // Last repair location in the previous scheduling
            else {
                locations[i + reportsLoader.getReportsToSchedule().size()][0] = Double.parseDouble(serviceManRepairInfos.get(serviceMen.get(i).getId()).getLastReport().getLongitude());
                locations[i + reportsLoader.getReportsToSchedule().size()][1] = Double.parseDouble(serviceManRepairInfos.get(serviceMen.get(i).getId()).getLastReport().getLatitude());
            }
        }

        // Get the time needed to travel between two points
        double[][] durationsInS = getDurationsInS(locations, reportsLoader);

        greedyAlgorithm(reportsLoader, durationsInS, interval);

    }

    public static void greedyAlgorithm(ReportsLoader reportsLoader, double[][] durationsInS, int interval) {
        int profitFromInterval = 0;

        for (Report report: reportsLoader.getReportsToSchedule()) {
            Skill skillNeeded = reportsLoader.getReportSkillMap().get(report);
            List<ServiceMan> serviceMenSorted = new ArrayList<>(skillNeeded.getServiceMen());
            // Sort list od servicemen by serviceman experience
            serviceMenSorted.sort(new Comparator<ServiceMan>() {
                @Override
                public int compare(ServiceMan s1, ServiceMan s2) {
                    return s2.getExperience().compareTo(s1.getExperience());
                }
            });
            for (ServiceMan serviceMan: serviceMenSorted) {
                if (serviceManRepairInfos.get(serviceMan.getId()).getRepairsTime() < (scheduleInterval * (interval + 1))) {
                    // Count time needed to repair
                    int repairTime = Math.round(skillNeeded.getMinRepairTime() + (float) (10 - serviceMan.getExperience()) / (10 - 1) * (skillNeeded.getMaxRepairTime() - skillNeeded.getMinRepairTime()) );

                    // Get time needed to travel between two locations based on report or serviceman location
                    int index = 0;
                    if (serviceManRepairInfos.get(serviceMan.getId()).getLastReport() == null || reportsLoader.getReportsToSchedule().indexOf(serviceManRepairInfos.get(serviceMan.getId()).getLastReport()) == -1) {
                        index = reportsLoader.getReportsToSchedule().size() + serviceMen.indexOf(serviceMan);
                    } else {
                        index = Math.toIntExact(reportsLoader.getReportsToSchedule().indexOf(serviceManRepairInfos.get(serviceMan.getId()).getLastReport()));
                    }
                    int travelTime = (int) durationsInS[Math.toIntExact(reportsLoader.getReportsToSchedule().indexOf(report))][index] / 60;

                    // Time needed for repair and travel
                    int totalTime = repairTime + travelTime;

                    // Check if serviceman can handle the repair
                    if (serviceManRepairInfos.get(serviceMan.getId()).getRepairsTime() + totalTime <= maxRepairTime) {
                        serviceManRepairInfos.put(serviceMan.getId(), new ServiceManRepairInfos(serviceManRepairInfos.get(serviceMan.getId()).getRepairsTime() + totalTime, report));
                        report.setStatus(ReportStatus.ASSIGNED);
                        profitFromInterval += skillNeeded.getProfit();
                        break;
                    }
                }
            }
        }

        System.out.println("interval " + interval + " profit " + profitFromInterval);
    }

    public static double[][] getDurationsInS(double[][] locations, ReportsLoader reportsLoader) {
        // Get the time needed to travel between two points
        String durationMatrix = loadDurationMatrix(locations);

        // Parse times to matrix
        double[][] durationsInS = new double[reportsLoader.getReportsToSchedule().size() + serviceMen.size()][reportsLoader.getReportsToSchedule().size() + serviceMen.size()];
        String[] durationMatrixStringSplit = durationMatrix.split("\\],\\[");
        for (int i = 0; i < durationMatrixStringSplit.length; i++) {
            String[] durations = durationMatrixStringSplit[i].replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            for (int j = 0; j < durations.length; j++) {
                durationsInS[i][j] = Double.parseDouble(durations[j]);
            }
        }
        return durationsInS;
    }

    public static String loadDurationMatrix(double[][] locations){
        var body = new HashMap<String, Object>() {{
            put("locations", locations);
            put("metrics", new String[] {"duration"});
            put("resolve_locations", "false");
            put("units", "km");
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

        System.out.println("status: " + response.statusCode());
        System.out.println("headers: " + response.headers());
        System.out.println("body:" + response.body());

        Pattern p = Pattern.compile("\\[(\\[([0-9]+\\.[0-9]+.)+.)+");
        Matcher m = p.matcher(response.body());
        if (m.find())
        {
            return m.group(0);
        }

        return "";

    }

    public static void loadServiceMen() {
        setServiceMen(serviceManRepository.findAll());
        serviceMen.stream().forEach(serviceMan -> serviceManRepairInfos.put(serviceMan.getId(), new ServiceManRepairInfos()));
    }

    public static ReportsLoader loadReportsToSchedule(int scheduleTimeInMs) {
        List<Report> reportsToSchedule = new ArrayList<>();
        Map<Report, Skill> reportSkillMap = new HashMap<>();

        List<Report> reports = reportRepository.findAll();
        for (Report report: reports) {
            String[] reportTime = report.getReportTime().toString().split(":");
            int reportTimeInMs = (((Integer.parseInt(reportTime[0])-1) * 60 + Integer.parseInt(reportTime[1])) * 60 + Integer.parseInt(reportTime[2])) * 1000;

            if (reportTimeInMs <= scheduleTimeInMs && report.getStatus() == ReportStatus.REPORTED) {
                reportsToSchedule.add(report);
                Skill skill = skillRepository.findByDeviceAndFailure(report.getDevice(), report.getFailure());
                reportSkillMap.put(report, skill);
            }
        }
        return new ReportsLoader(reportsToSchedule, reportSkillMap);
    }


    public static ReportRepository getReportRepository() {
        return reportRepository;
    }

    public static void setReportRepository(ReportRepository reportRepository) {
        Optimization.reportRepository = reportRepository;
    }

    public static ServiceManRepository getServiceManRepository() {
        return serviceManRepository;
    }

    public static void setServiceManRepository(ServiceManRepository serviceManRepository) {
        Optimization.serviceManRepository = serviceManRepository;
    }

    public static SkillRepository getSkillRepository() {
        return skillRepository;
    }

    public static void setSkillRepository(SkillRepository skillRepository) {
        Optimization.skillRepository = skillRepository;
    }

    public static int getScheduleInterval() {
        return scheduleInterval;
    }

    public static void setScheduleInterval(int scheduleInterval) {
        Optimization.scheduleInterval = scheduleInterval;
    }

    public static int getMaxRepairTime() {
        return maxRepairTime;
    }

    public static void setMaxRepairTime(int maxRepairTime) {
        Optimization.maxRepairTime = maxRepairTime;
    }

    public static String getFirstSchedule() {
        return firstSchedule;
    }

    public static void setFirstSchedule(String firstSchedule) {
        Optimization.firstSchedule = firstSchedule;
    }

    public static Map<Long, ServiceManRepairInfos> getServiceManRepairInfos() {
        return serviceManRepairInfos;
    }

    public static void setServiceManRepairInfos(Map<Long, ServiceManRepairInfos> serviceManRepairInfos) {
        Optimization.serviceManRepairInfos = serviceManRepairInfos;
    }

    public static List<ServiceMan> getServiceMen() {
        return serviceMen;
    }

    public static void setServiceMen(List<ServiceMan> serviceMen) {
        Optimization.serviceMen = serviceMen;
    }
}
