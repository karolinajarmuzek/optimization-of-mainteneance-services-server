package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.payload.*;
import com.oms.serverapp.util.Helpers;
import com.oms.serverapp.util.ReportStatus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.util.*;

public class RepairOptimization {

    private static final String URL_SERVICEMAN = "http://localhost:8080/api/serviceman";
    private static final String URL_REPORT = "http://localhost:8080/api/report";
    private static final String URL_SKILL = "http://localhost:8080/api/skill";
    private static final String URL_AUTHORIZE = "http://localhost:8080/api/auth/signin";
    private static final String URL_REPAIR = "http://localhost:8080/api/repair";

    private static String token;

    private static String firstSchedule = "7:50:00";

    public static void scheduler() {

        authorize();
        List<ServiceManPayload> servicemen = new ArrayList<>(getData(URL_SERVICEMAN, false));
        List<ReportResponse> reports = new ArrayList<>(getData(URL_REPORT, false));
        List<Long> skills = new ArrayList<>(getData(URL_SKILL, true));

        /*System.out.println("servicemen" + servicemen);
        System.out.println("reports" + reports);*/

        /*for (int i = 0; i< servicemen.size(); i++) {
            System.out.println("name " + servicemen.get(i).getFirstName()+ servicemen.get(i).getLastName());
        }*/

        String[] scheduleTime = firstSchedule.split(":");
        int schedule = (((Integer.parseInt(scheduleTime[0])-1) * 60 + Integer.parseInt(scheduleTime[1])) * 60 + Integer.parseInt(scheduleTime[2])) * 1000;

        List<ReportResponse> reportsToSchedule = new ArrayList<>();
        for (int i = 0; i < reports.size(); i++) {
            String[] reportTime = reports.get(i).getReportTime().split(":");
            int val = (((Integer.parseInt(reportTime[0])-1) * 60 + Integer.parseInt(reportTime[1])) * 60 + Integer.parseInt(reportTime[2])) * 1000;

            /*System.out.println("ms " + val + " , " + schedule + " , ");
            System.out.println(val <= schedule);*/
            if (val <= schedule) {
                reportsToSchedule.add(reports.get(i));
            }
        }

        System.out.println("Reports to schedule " + reportsToSchedule.size());


        /*for (int i = 0; i < 4; i++){
            int finalI = i;
            var body = new HashMap<String, Object>() {{
                put("serviceMan", servicemen.get(1));
                put("date", new Date());
                put("time", "8:00");
                put("status", ReportStatus.PENDING);
                put("report", reports.get(finalI));
            }};
            Helpers.sendPostRequest(body, URL_REPAIR, token);
        }*/
    }

    //helpers
    public static void authorize() {
        var body = new HashMap<String, Object>() {{
            put("username", "adminadmin");
            put("password", "adminadmin");
        }};

        HttpResponse<String> response = Helpers.sendPostRequest(body, URL_AUTHORIZE, token); //
        String token = response.body().replaceAll(",", "")
                .replace("\"tokenType\":\"Bearer\"", "")
                .replace("\"accessToken\":\"", "")
                .split("\"")[0]
                .split("\\{")[1];
        setToken(token);
    }

    //przenieść do helpers
    public static <T> Set<T> getData(String url, boolean onlyIds) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            Set<T> data = new HashSet<>();
            Gson gson = new Gson();
            if (url.split("/")[4].equals("serviceman")) {
                ServiceManPayload[] servicemen = gson.fromJson(response.body(), ServiceManPayload[].class);
                for (ServiceManPayload serviceman: servicemen) {
                    if (onlyIds) {
                        data.add((T) serviceman.getId());
                    } else {
                        data.add((T) serviceman);
                    }
                }
            } else if (url.split("/")[4].equals("report")) {
                ReportResponse[] reports = gson.fromJson(response.body(), ReportResponse[].class);
                for (ReportResponse report: reports) {
                    if (onlyIds) {
                        data.add((T) report.getId());
                    } else {
                        data.add((T) report);
                    }
                }
            } else if (url.split("/")[4].equals("skill")) {
                SkillPayload[] skills = gson.fromJson(response.body(), SkillPayload[].class);
                for (SkillPayload skill: skills) {
                    if (onlyIds) {
                        data.add((T) skill.getId());
                    } else {
                        data.add((T) skill);
                    }
                }
            }
            return data;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void api() {
        var body = new HashMap<String, Object>() {{
            put("locations", new double[][] {{9.70093,48.477473},{9.207916,49.153868},{37.573242,55.801281},{115.663757,38.106467}});
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

        /*Client client = ClientBuilder.newClient();
        Entity<String> payload = Entity.json("{\"locations\":[[9.70093,48.477473],[9.207916,49.153868],[37.573242,55.801281],[115.663757,38.106467]],\"metrics\":[\"distance\"],\"resolve_locations\":\"false\",\"units\":\"km\"}");
        Response response = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248655e1d2b2ab94d7caabb6d4171889110")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(payload);
*/
        System.out.println("status: " + response.statusCode());
        System.out.println("headers: " + response.headers());
        System.out.println("body:" + response.body());
    }



    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        RepairOptimization.token = token;
    }
}
