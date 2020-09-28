package com.oms.serverapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class DurationsMatrix {
    private int routesLimit = 3500;
    private int locationLimit = (int) Math.floor(Math.sqrt(routesLimit));

    private double[][] durations;
    private double[][] locations;

    public DurationsMatrix(int totalSize, double[][] allLocations) {
        durations = new double[totalSize][totalSize];
        locations = allLocations;
        loadDurationMatrix();
    }

    public void loadDurationMatrix() {
        if (locations.length > locationLimit) {
            int rowsPerRequest = (int) Math.floor(routesLimit / locations.length);
            int requests = (int) Math.ceil((float)locations.length / (float)rowsPerRequest);
            for (int i = 0; i < requests; i++) {
                int limit = rowsPerRequest;
                if (i == requests - 1) {
                    limit = locations.length - i * rowsPerRequest;
                }
                int[] destinations = new int[limit];
                for (int j = 0; j < limit; j++) {
                    destinations[j] = rowsPerRequest * i + j;
                }

                double[][] partialDurations = loadDurationMatrixFromAPI(locations, destinations, true);
                for (int row = 0; row < partialDurations.length; row++) {
                    for (int col = 0; col < partialDurations[row].length; col++) {
                        durations[row][rowsPerRequest * i + col] = partialDurations[row][col];
                    }
                }
            }
        } else {
            durations = loadDurationMatrixFromAPI(locations);
        }
    }

    public double[][] loadDurationMatrixFromAPI(double[][] locations) {
        return loadDurationMatrixFromAPI(locations, null, false);
    }

    public double[][] loadDurationMatrixFromAPI(double[][] locations, int[] destinations, boolean isPartial){
        var body = new HashMap<String, Object>() {{
            put("locations", locations);
            if(isPartial) put("destinations", destinations);
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

        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(response.body(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject.getAsJsonArray("durations"), double[][].class);
    }

    public double[][] getDurations() {
        return durations;
    }
}
