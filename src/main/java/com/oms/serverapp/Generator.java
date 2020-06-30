package com.oms.serverapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Generator {

    private static final String URL_SERVICEMAN = "http://localhost:8080/api/serviceman";

    private static final Set<String> names = new HashSet<>(Set.of("Adam", "Bartosz", "Cezary", "Damian", "Eryk", "Franciszek", "Grzegorz", "Henryk", "Ignacy", "Jacek", "Karol", "Lech", "Mateusz", "Norbert", "Oliwier", "Piotr", "Rafal", "Sebastian", "Tomasz", "Wojciech", "Zbigniew"));
    private static final Set<String> surnames = new HashSet<>(Set.of("Nowak", "Kowalski", "Wisniewski", "Wojcik", "Kowalczyk", "Kaminski", "Lewandowski", "Zielinski", "Szymanski", "Wozniak", "Dabrowski", "Kozlowski", "Jankowski"));
    private static Set<String> users = new HashSet<>();

    public static void generateData(){
        Integer serviceManCount = 2;

        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("DATA GENERATION STARTED");
        generateServiceMen(serviceManCount);
        System.out.println("\nDATA GENERATION FINISHED");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

    }

    public static void sendPostRequest(HashMap<String, String> body, String url) {

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

        System.out.println(response.body());
    }

    public static void generateServiceMen(Integer count) {
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
            var body = new HashMap<String, String>() {{
                put("firstName", finalName);
                put("lastName", finalSurname);
                put("phoneNumber", Integer.toString(random.nextInt(899999999) + 100000000));
                put("username", finalName.toLowerCase() + finalSurname.toLowerCase());
                put("password", finalName.toLowerCase() + finalSurname.toLowerCase());
                put("startLocalization", "loc");
                put("experience", Integer.toString(random.nextInt(10) + 1));
            }};

            sendPostRequest(body, URL_SERVICEMAN);
        }
    }


}
