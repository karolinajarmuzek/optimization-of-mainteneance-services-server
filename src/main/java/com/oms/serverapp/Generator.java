package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.payload.SkillPayload;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private static final String URL_SERVICEMAN = "http://localhost:8080/api/serviceman";
    private static final String URL_CUSTOMER = "http://localhost:8080/api/customer";
    private static final String URL_DEVICE = "http://localhost:8080/api/device";
    private static final String URL_FAILURE = "http://localhost:8080/api/failure";
    private static final String URL_SKILL = "http://localhost:8080/api/skill";

    private enum UserType{SERVICEMAN, CUSTOMER}

    private static final Set<String> names = new HashSet<>(Set.of("Adam", "Bartosz", "Cezary", "Damian", "Eryk", "Franciszek", "Grzegorz", "Henryk", "Ignacy", "Jacek", "Karol", "Lech", "Mateusz", "Norbert", "Oliwier", "Piotr", "Rafal", "Sebastian", "Tomasz", "Wojciech", "Zbigniew"));
    private static final Set<String> surnames = new HashSet<>(Set.of("Nowak", "Kowalski", "Wisniewski", "Wojcik", "Kowalczyk", "Kaminski", "Lewandowski", "Zielinski", "Szymanski", "Wozniak", "Dabrowski", "Kozlowski", "Jankowski"));
    private static Set<String> users = new HashSet<>();

    private static final Map<String, String> devices = new HashMap<String,String>(){
        {
            put("Bosch Condens GC7000iW", "kociol kondensacyjny");
            put("Bosch Condens GC9000iW", "kociol kondensacyjny");
            put("Bosch Condens GC2300iW", "kociol kondensacyjny");
            put("Cerapur Midi", "kociol kondensacyjny");
            put("Cerapur Acu Smart", "kociol kondensacyjny");
            put("Suprapur KBR 65-98", "kociol kondensacyjny");
            put("HT Earth", "pompa ciepla");
            put("HT Air 40-60 kW", "pompa ciepla");
            put("HT AIR", "pompa ciepla");
            put("WARMTEC WRM06+", "kurtyna powietrzna");
            put("WARMTEC WRMS12+", "kurtyna powietrzna");
        }};

    private static final List<String> failures = new ArrayList<>(Arrays.asList("F1", "F2", "F3"));

    public static void generateData(){
        int serviceManCount = 2;
        int customerCount = 2;

        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("DATA GENERATION STARTED");
        if (isTableEmpty(URL_DEVICE)) generateDevices();
        if (isTableEmpty(URL_FAILURE)) generateFailures();
        if (isTableEmpty(URL_SKILL)) generateSkills();
        if (isTableEmpty(URL_SERVICEMAN)) generateUser(serviceManCount, UserType.SERVICEMAN);
        if (isTableEmpty(URL_CUSTOMER)) generateUser(customerCount, UserType.CUSTOMER);
        System.out.println("DATA GENERATION FINISHED");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

    }

    public static boolean isTableEmpty(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.body().length() > 2) return false;
            else return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void generateDevices() {
        for (Map.Entry<String, String> device : devices.entrySet()) {
            var body = new HashMap<String, Object>() {{
                put("name", device.getKey());
                put("type", device.getValue());
            }};

            sendPostRequest(body, URL_DEVICE);
        }
    }

    public static void generateFailures() {
        for (String failure: failures) {
            var body = new HashMap<String, Object>() {{
                put("type", failure);
            }};

            sendPostRequest(body, URL_FAILURE);
        }
    }

    public static void generateSkills() {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestDevices = HttpRequest.newBuilder()
                .uri(URI.create(URL_DEVICE))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        HttpRequest requestFailures = HttpRequest.newBuilder()
                .uri(URI.create(URL_FAILURE))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseDevices = null;
        HttpResponse<String> responseFailures = null;
        try {
            responseDevices = client.send(requestDevices,
                    HttpResponse.BodyHandlers.ofString());
            responseFailures = client.send(requestFailures,
                    HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            DevicePayload[] devices = gson.fromJson(responseDevices.body(), DevicePayload[].class);
            FailurePayload[] failures = gson.fromJson(responseFailures.body(), FailurePayload[].class);

            for (int i = 0; i < devices.length; i++) {
                for (int j = 0; j < failures.length; j++) {
                    Integer profit = ThreadLocalRandom.current().nextInt(5, 51) * 10; // 50-500
                    Integer minRepairTime = ThreadLocalRandom.current().nextInt(3, 48) * 10; //30 - 480 [min]
                    Integer maxRepairTime;
                    do {
                        maxRepairTime = ThreadLocalRandom.current().nextInt(3, 48) * 10; //30 - 480 [min]
                    } while (maxRepairTime < minRepairTime);

                    Integer finalMaxRepairTime = maxRepairTime;
                    int finalI = i;
                    int finalJ = j;
                    var body = new HashMap<String, Object>() {{
                        put("device", devices[finalI].getId());
                        put("failure", failures[finalJ].getId());
                        put("profit", profit.toString());
                        put("minRepairTime", minRepairTime);
                        put("maxRepairTime", finalMaxRepairTime);
                    }};

                    sendPostRequest(body, URL_SKILL);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generateUser(int count, UserType userType) {
        for (int i = 0; i < count; i++) {
            String name, surname;
            Random random = new Random();
            do {
                name = new ArrayList<>(names).get(random.nextInt(names.size()));
                surname = new ArrayList<>(surnames).get(random.nextInt(surnames.size()));
            } while (users.contains(name + " " + surname));
            users.add(name + " " + surname);

            String finalName = name;
            String finalSurname = surname;

            var body = new HashMap<String, Object>() {{
                put("firstName", finalName);
                put("lastName", finalSurname);
                put("phoneNumber", Integer.toString(random.nextInt(899999999) + 100000000));
                if (userType == UserType.SERVICEMAN) {
                    put("username", finalName.toLowerCase() + finalSurname.toLowerCase());
                    put("password", finalName.toLowerCase() + finalSurname.toLowerCase());
                    put("startLocalization", "loc");
                    put("experience", random.nextInt(10) + 1);
                    SkillPayload[] skills = getSkills();
                    Set<Long> ownedSkills = new HashSet<>();
                    for (SkillPayload skill: skills) {
                        if (ThreadLocalRandom.current().nextInt(0, 2) == 0) {
                            ownedSkills.add(skill.getId());
                        }
                    }
                    put("skills", ownedSkills);
                }
            }};
            sendPostRequest(body, userType == UserType.SERVICEMAN ? URL_SERVICEMAN : URL_CUSTOMER);
        }
    }

    public static SkillPayload[] getSkills() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestSkills = HttpRequest.newBuilder()
                .uri(URI.create(URL_SKILL))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseSkills = null;
        try {
            responseSkills = client.send(requestSkills,
                    HttpResponse.BodyHandlers.ofString());


            Gson gson = new Gson();
            SkillPayload[] skills = gson.fromJson(responseSkills.body(), SkillPayload[].class);
            return skills;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPostRequest(HashMap<String, Object> body, String url) {

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
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader("Content-Type", "application/json")
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
    }
}
