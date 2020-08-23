package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.payload.SkillPayload;
import com.oms.serverapp.util.Helpers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private static final String URL_SERVICETECHNICIAN = "http://localhost:8080/api/serviceTechnician";
    private static final String URL_CUSTOMER = "http://localhost:8080/api/customer";
    private static final String URL_DEVICE = "http://localhost:8080/api/device";
    private static final String URL_FAILURE = "http://localhost:8080/api/failure";
    private static final String URL_SKILL = "http://localhost:8080/api/skill";
    private static final String URL_AUTHORIZE = "http://localhost:8080/api/auth/signin";

    private enum UserType{SERVICETECHNICIAN, CUSTOMER, ADMIN}

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

    private static final Set<String> failures = new HashSet<>(Set.of("F1", "F2", "F3"));

    private static String token;

    public static void generateData(){
        int serviceTechniciansCount = 2;
        int customerCount = 5;

        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("DATA GENERATION STARTED");
        //generateUser(1, UserType.ADMIN);
        authorize();
        if (isTableEmpty(URL_DEVICE)) generateDevices();
        if (isTableEmpty(URL_FAILURE)) generateFailures();
        if (isTableEmpty(URL_SKILL)) generateSkills();
        //if (isTableEmpty(URL_SERVICETECHNICIAN))
        generateUser(serviceTechniciansCount, UserType.SERVICETECHNICIAN);
        if (isTableEmpty(URL_CUSTOMER)) generateUser(customerCount, UserType.CUSTOMER);
        System.out.println("DATA GENERATION FINISHED");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

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

    public static boolean isTableEmpty(String url) {
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
            Helpers.sendPostRequest(body, URL_DEVICE, token);
        }
    }

    public static void generateFailures() {
        for (String failure: failures) {
            var body = new HashMap<String, Object>() {{
                put("type", failure);
            }};

            Helpers.sendPostRequest(body, URL_FAILURE, token);
        }
    }

    public static void generateSkills() {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestDevices = HttpRequest.newBuilder()
                .uri(URI.create(URL_DEVICE))
                .GET()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
                .build();

        HttpRequest requestFailures = HttpRequest.newBuilder()
                .uri(URI.create(URL_FAILURE))
                .GET()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
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
                    Integer minRepairTime = ThreadLocalRandom.current().nextInt(3, 24) * 10; //30 - 240 [min]
                    Integer maxRepairTime;
                    do {
                        maxRepairTime = ThreadLocalRandom.current().nextInt(3, 24) * 10; //30 - 240 [min]
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

                    Helpers.sendPostRequest(body, URL_SKILL, token);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generateUser(int count, UserType userType) {
        StreetsCreator streetsCreator = new StreetsCreator();

        for (int i = 0; i < count; i++) {
            String name, surname;
            Random random = new Random();
            if (userType != UserType.ADMIN) {
                do {
                    name = new ArrayList<>(names).get(random.nextInt(names.size()));
                    surname = new ArrayList<>(surnames).get(random.nextInt(surnames.size()));
                } while (users.contains(name + " " + surname));
            }
            else {
                name = "Admin";
                surname = "Admin";
            }
            users.add(name + " " + surname);

            String finalName = name;
            String finalSurname = surname;

            var body = new HashMap<String, Object>() {{
                put("firstName", finalName);
                put("lastName", finalSurname);
                put("phoneNumber", Integer.toString(random.nextInt(899999999) + 100000000));
                if (userType != UserType.CUSTOMER) {
                    String[] address = streetsCreator.generateAddress();
                    put("username", finalName.toLowerCase() + finalSurname.toLowerCase());
                    put("password", finalName.toLowerCase() + finalSurname.toLowerCase());
                    put("startLocalization", address[0]);
                    put("longitude", address[1]);
                    put("latitude", address[2]);
                    put("experience", random.nextInt(10) + 1);
                    if (userType != UserType.ADMIN) {
                        SkillPayload[] skills = getSkills();
                        Set<Long> ownedSkills = new HashSet<>();
                        for (SkillPayload skill : skills) {
                            if (ThreadLocalRandom.current().nextInt(0, 3) <= 1) {
                                ownedSkills.add(skill.getId());
                            }
                        }
                        put("skills", ownedSkills);
                    }
                }
            }};
            Helpers.sendPostRequest(body, userType == UserType.CUSTOMER ? URL_CUSTOMER : URL_SERVICETECHNICIAN, token);
        }
    }

    public static SkillPayload[] getSkills() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestSkills = HttpRequest.newBuilder()
                .uri(URI.create(URL_SKILL))
                .GET()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
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

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Generator.token = token;
    }
}
