package com.oms.serverapp;

import com.google.gson.Gson;
import com.oms.serverapp.payload.CustomerPayload;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.util.Helpers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ReportGenerator {

    private static final String URL_CUSTOMER = "http://localhost:8080/api/customer";
    private static final String URL_DEVICE = "http://localhost:8080/api/device";
    private static final String URL_FAILURE = "http://localhost:8080/api/failure";
    private static final String URL_REPORTS = "http://localhost:8080/api/report";

    public static void generateReports(int count) {

        List<Long> customers = new ArrayList<>(getIds(URL_CUSTOMER));
        List<Long> devices = new ArrayList<>(getIds(URL_DEVICE));
        List<Long> failures = new ArrayList<>(getIds(URL_FAILURE));

        var body = new HashMap<String, Object>() {{
            put("customer", customers.get(ThreadLocalRandom.current().nextInt(customers.size())));
            put("failure", failures.get(ThreadLocalRandom.current().nextInt(failures.size())));
            put("device", devices.get(ThreadLocalRandom.current().nextInt(devices.size())));
            put("date", new Date());
            put("location", "loc");
            put("description", "Description");
        }};
        Helpers.sendPostRequest(body, URL_REPORTS);
    }

    public static Set<Long> getIds(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            Set<Long> ids = new HashSet<>();
            Gson gson = new Gson();
            if (url.split("/")[4].equals("customer")) {
                CustomerPayload[] customers = gson.fromJson(response.body(), CustomerPayload[].class);
                for (CustomerPayload customer: customers) {
                    ids.add(customer.getId());
                }
            } else if (url.split("/")[4].equals("device")) {
                DevicePayload[] devices = gson.fromJson(response.body(), DevicePayload[].class);
                for (DevicePayload device: devices) {
                    ids.add(device.getId());
                }
            } else if (url.split("/")[4].equals("failure")) {
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
}