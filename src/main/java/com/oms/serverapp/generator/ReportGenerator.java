package com.oms.serverapp.generator;

import com.google.gson.Gson;
import com.oms.serverapp.util.StreetsCreator;
import com.oms.serverapp.payload.CustomerPayload;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.util.Helpers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ReportGenerator {

    private static final String URL_CUSTOMER = "http://localhost:8080/api/customer";
    private static final String URL_DEVICE = "http://localhost:8080/api/device";
    private static final String URL_FAILURE = "http://localhost:8080/api/failure";
    private static final String URL_REPORTS = "http://localhost:8080/api/report";
    private static final String URL_AUTHORIZE = "http://localhost:8080/api/auth/signin";

    private static String token;

    public static void generateReports(int count) {

        authorize();

        List<Long> customers = new ArrayList<>(getIds(URL_CUSTOMER));
        List<Long> devices = new ArrayList<>(getIds(URL_DEVICE));
        List<Long> failures = new ArrayList<>(getIds(URL_FAILURE));

        StreetsCreator streetsCreator = new StreetsCreator();

        final Random random = new Random();

        for (int i = 0; i < count; i++) {
            String[] address = streetsCreator.generateAddress();

            int delta = 0;
            if (i < count/4) {
                delta = (6*60+50)*60*1000; //7:50 - 9:50
            } else if ( count/4 <= i && i < count*2/4) {
                delta = (8*60+50)*60*1000; //9:50 - 11:50
            } else if ( count*2/4 <= i && i < count*3/4) {
                delta = (10*60+50)*60*1000; //11:50 - 13:50
            }else if ( count*3/4 <= i && i < count) {
                delta = (4*60+50)*60*1000 ; //13:50 - 15:50 // 7:50??
            }
            Time time = new Time((random.nextInt(2*60*60*1000) + delta));
            var body = new HashMap<String, Object>() {{
                put("customer", customers.get(ThreadLocalRandom.current().nextInt(customers.size())));
                put("failure", failures.get(ThreadLocalRandom.current().nextInt(failures.size())));
                put("device", devices.get(ThreadLocalRandom.current().nextInt(devices.size())));
                put("reportDate", new Date());
                put("reportTime", time);
                put("address", address[0]);
                put("longitude", address[1]);
                put("latitude", address[2]);
                put("description", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin cursus egestas est non lacinia.");
            }};
            Helpers.sendPostRequest(body, URL_REPORTS, token);
        }
    }

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

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ReportGenerator.token = token;
    }
}
