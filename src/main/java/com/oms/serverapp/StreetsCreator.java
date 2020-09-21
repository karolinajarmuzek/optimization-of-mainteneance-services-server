package com.oms.serverapp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class StreetsCreator {

    private static String fileName = "src\\main\\java\\com\\oms\\serverapp\\util\\streets.txt";
    private ArrayList<String> streets;

    public StreetsCreator() {
        this.streets = getStreet();
    }

    private static ArrayList<String> getStreet() {
        ArrayList<String> result = new ArrayList<>();

        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String[] generateAddress() {
        Random rand = new Random();
        double[] coordinates;
        String randomAddress = "";
        do {
            randomAddress = streets.get(rand.nextInt(streets.size())) + " " + (rand.nextInt(99) + 1) + " Poznań";
            coordinates = getCoordinates(randomAddress);
        } while (coordinates == null);
        String longitude = String.valueOf(coordinates[0]);
        String latitude = String.valueOf(coordinates[1]);
        return new String[]{randomAddress, longitude, latitude};
    }

    public static double[] getCoordinates(String address) {
        String normalizedAddress = Normalizer.normalize(address, Normalizer.Form.NFD)
                .replaceAll("ł", "l")
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openrouteservice.org/geocode/search?api_key=5b3ce3597851110001cf6248655e1d2b2ab94d7caabb6d4171889110&text=" + normalizedAddress))
                .GET()
                .setHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(response.body(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonArray("features").get(0).getAsJsonObject();
        float confidence = jsonObject.getAsJsonObject("properties").getAsJsonPrimitive("confidence").getAsFloat();

        if (confidence < 0.8) return null;

        double[] coordinates = gson.fromJson(jsonObject.getAsJsonObject("geometry").getAsJsonArray("coordinates"), double[].class);

        return coordinates;
    }
}
