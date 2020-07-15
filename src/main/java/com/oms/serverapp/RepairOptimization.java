package com.oms.serverapp;

import com.google.gson.Gson;
import com.oms.serverapp.payload.CustomerPayload;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.util.Helpers;
import com.oms.serverapp.util.ReportStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class RepairOptimization {

    private static final String URL_SERVICEMAN = "http://localhost:8080/api/serviceman";
    private static final String URL_REPORT = "http://localhost:8080/api/report";
    private static final String URL_SKILL = "http://localhost:8080/api/skill";
    private static final String URL_AUTHORIZE = "http://localhost:8080/api/auth/signin";
    private static final String URL_REPAIR = "http://localhost:8080/api/repair";

    private static String token;

    public static void scheduler() {

        authorize();
        List<Long> servicemen = new ArrayList<>(getIds(URL_SERVICEMAN));
        List<Long> reports = new ArrayList<>(getIds(URL_REPORT));
        List<Long> skills = new ArrayList<>(getIds(URL_SKILL));

        System.out.println("servicemen" + servicemen);
        System.out.println("reports" + reports);
        for (int i = 0; i < 4; i++){
            int finalI = i;
            var body = new HashMap<String, Object>() {{
                put("serviceMan", servicemen.get(1));
                put("date", new Date());
                put("time", "8:00");
                put("status", ReportStatus.PENDING);
                put("report", reports.get(finalI));
            }};
            Helpers.sendPostRequest(body, URL_REPAIR, token);
        }
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
    public static Set<Long> getIds(String url) {
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

            Set<Long> ids = new HashSet<>();
            Gson gson = new Gson();
            if (url.split("/")[4].equals("serviceman")) {
                CustomerPayload[] customers = gson.fromJson(response.body(), CustomerPayload[].class);
                for (CustomerPayload customer: customers) {
                    ids.add(customer.getId());
                }
            } else if (url.split("/")[4].equals("report")) {
                DevicePayload[] devices = gson.fromJson(response.body(), DevicePayload[].class);
                for (DevicePayload device: devices) {
                    ids.add(device.getId());
                }
            } else if (url.split("/")[4].equals("skill")) {
                FailurePayload[] failures = gson.fromJson(response.body(), FailurePayload[].class);
                for (FailurePayload failure: failures) {
                    ids.add(failure.getId());
                }
            }
            return ids;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        RepairOptimization.token = token;
    }
}
