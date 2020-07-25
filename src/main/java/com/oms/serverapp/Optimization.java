package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.serverapp.model.*;
import com.oms.serverapp.payload.ReportResponse;
import com.oms.serverapp.repository.ReportRepository;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.repository.SkillRepository;
import com.oms.serverapp.service.ReportService;
import com.oms.serverapp.util.ReportStatus;
import com.oms.serverapp.util.ReportsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Optimization {

    private static ReportRepository reportRepository;
    private static ServiceManRepository serviceManRepository;
    private static SkillRepository skillRepository;
    private static String firstSchedule = "7:50:00";
    private static int scheduleInterval;// = 120; //min
    private static int maxRepairTime = 480; //min
    /*private static LocalTime nextSchedule = LocalTime.of(7, 50);//??
    private static LocalTime nextSchedule2 = LocalTime.of(7, 50);//??*/
    private static Map<ServiceMan, Integer> serviceManRepairTime = new HashMap<>();
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
        for (int i = 0; i < intervals; i++) {
            //scheduleInterval("7:50:00");
            scheduleInterval(i);
        }

    }

    public static void scheduleInterval(int interval) {

        String[] firstScheduleTime = firstSchedule.split(":");
        int scheduleTimeInMs = (((Integer.parseInt(firstScheduleTime[0])-1) * 60 + Integer.parseInt(firstScheduleTime[1])) * 60 + Integer.parseInt(firstScheduleTime[2])) * 1000 + interval * scheduleInterval * 60 * 1000;
        System.out.println("interval " + interval + " time " + new Time((long)scheduleTimeInMs));

        ReportsLoader reportsLoader = loadReportsToSchedule(scheduleTimeInMs);

        double[][] locations = new double[reportsLoader.getReportsToSchedule().size() + serviceMen.size()][2];
        for (int i = 0; i < reportsLoader.getReportsToSchedule().size(); i++) {
            locations[i][0] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLongitude());
            locations[i][1] = Double.parseDouble(reportsLoader.getReportsToSchedule().get(i).getLatitude());
        }
        for (int i = 0; i < serviceMen.size(); i++) {
            locations[i + reportsLoader.getReportsToSchedule().size()][0] = Double.parseDouble(serviceMen.get(i).getLatitude());
            locations[i + reportsLoader.getReportsToSchedule().size()][1] = Double.parseDouble(serviceMen.get(i).getLongitude());
        }
        //loadDurationMatrix(locations);

        /*for (Report report: reportsLoader.getReportsToSchedule()) {
            Skill skillNeeded = reportsLoader.getReportSkillMap().get(report);
            for (ServiceMan serviceMan: serviceMen) {
                if (serviceMan.getOwnedSkills().stream().filter(skill -> skill.getId().equals(skillNeeded.getId())).findFirst().isPresent() &&
                    serviceManRepairTime.get(serviceMan) < scheduleInterval * (interval + 1)) {
                    int timeToRepair = Math.round(skillNeeded.getMinRepairTime() + (float) (10 - serviceMan.getExperience()) / (10 - 1) * (skillNeeded.getMaxRepairTime() - skillNeeded.getMinRepairTime()));

                    int v1 = serviceManRepairTime.get(serviceMan) + timeToRepair;

                    if (serviceManRepairTime.get(serviceMan) + timeToRepair <= maxRepairTime) {
                        serviceManRepairTime.put(serviceMan, serviceManRepairTime.get(serviceMan) + timeToRepair);
                        report.setStatus(ReportStatus.ASSIGNED);
                        break;
                    }
                }
            }
        }*/
    }

    public static void loadDurationMatrix(double[][] locations){
        var body = new HashMap<String, Object>() {{
            put("locations", locations);//new double[][] {{9.70093,48.477473},{9.207916,49.153868},{37.573242,55.801281},{115.663757,38.106467}});
            put("metrics", new String[] {"distance"});
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

    }

    public static void loadServiceMen() {
        setServiceMen(serviceManRepository.findAll());
        serviceMen.stream().forEach(serviceMan -> serviceManRepairTime.put(serviceMan, 0));
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

    public static Map<ServiceMan, Integer> getServiceManRepairTime() {
        return serviceManRepairTime;
    }

    public static void setServiceManRepairTime(Map<ServiceMan, Integer> serviceManRepairTime) {
        Optimization.serviceManRepairTime = serviceManRepairTime;
    }

    public static List<ServiceMan> getServiceMen() {
        return serviceMen;
    }

    public static void setServiceMen(List<ServiceMan> serviceMen) {
        Optimization.serviceMen = serviceMen;
    }
}
