package data_access;

import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import entity.TravelRecord;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RouteDataAccess implements RouteDataAccessInterface {
    private final OkHttpClient client = new OkHttpClient();
    private final String directionsToken;

    // 1. MEMORY: The list where stops live while the app runs
    private final List<ItineraryStop> stops = new ArrayList<>();

    // 2. STORAGE: The file where data is saved permanently
    private static final String JSON_FILE_PATH = "saved_itineraries.json";

    public RouteDataAccess() {
        Dotenv dotenv = Dotenv.load();
        this.directionsToken = dotenv.get("DIRECTIONS_TOKEN");

    }

    // ADDING THE ITENERARY STOPS TOGETHER
    @Override
    public void addStop(ItineraryStop stop) {
        this.stops.add(stop);
    }

    //GET STOP CALL METHOD
    @Override
    public List<ItineraryStop> getStops() {
        return this.stops;
    }

    @Override
    public void saveItinerary(Itinerary itinerary) {
        // ITENERARY WRAPPER TO JSON
        JSONObject json = new JSONObject();
        json.put("id", itinerary.getId());

        // TRAVEL RECORD TO JSON
        JSONObject recordJson = new JSONObject();
        TravelRecord record = itinerary.getRecord();
        if (record != null) {
            recordJson.put("username", record.getUsername());
            recordJson.put("origin", record.getOrigin());
            recordJson.put("destination", record.getDestination());
            recordJson.put("timeNeeded", record.getTimeNeeded());
            recordJson.put("weatherSummary", record.getWeatherSummary());
            recordJson.put("optimalPath", record.getOptimalPath());
            recordJson.put("clothingSuggestion", record.getClothingSuggestion());
        }
        json.put("travelRecord", recordJson);

        // CONVERT LIST OF STOPS TO JSON ARRAY
        JSONArray stopsJson = new JSONArray();
        for (ItineraryStop stop : itinerary.getStops()) {
            JSONObject stopJson = new JSONObject();
            stopJson.put("id", stop.getId());
            stopJson.put("name", stop.getName());
            stopJson.put("latitude", stop.getLatitude());
            stopJson.put("longitude", stop.getLongitude());
            stopJson.put("notes", stop.getNotes());
            stopsJson.put(stopJson);
        }
        json.put("stops", stopsJson);

        //SAVE TO JSON FILE
        saveToJSONFile(json);
    }

    private void saveToJSONFile(JSONObject newItinerary) {
        File file = new File(JSON_FILE_PATH);
        JSONArray allItineraries;

        try {
            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                allItineraries = new JSONArray(content);
            } else {
                allItineraries = new JSONArray();
            }

            allItineraries.put(newItinerary);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(allItineraries.toString(4));
            }
            System.out.println("Itinerary saved to " + JSON_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Could not save to file. " + e.getMessage());
        }
    }

    //LOADITENERARY METHOD AFTER SAVED TO DB
    public List<Itinerary> loadItineraries() {
        List<Itinerary> loadedItineraries = new ArrayList<>();
        File file = new File(JSON_FILE_PATH);

        if (!file.exists()) {
            return loadedItineraries; // Return empty list if no file
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String id = json.getString("id");

                // RECONSTRUCT THE TRAVEL RECORD
                JSONObject rJson = json.getJSONObject("travelRecord");
                TravelRecord record = new TravelRecord(
                        rJson.getString("username"),
                        rJson.getString("origin"),
                        rJson.getString("destination"),
                        rJson.getString("timeNeeded"),
                        rJson.getString("weatherSummary"),
                        rJson.getString("optimalPath"),
                        rJson.getString("clothingSuggestion")
                );

                // RECONSTRUCT THE STOP LISTS
                List<ItineraryStop> stopList = new ArrayList<>();
                JSONArray sArray = json.getJSONArray("stops");
                for (int j = 0; j < sArray.length(); j++) {
                    JSONObject sJson = sArray.getJSONObject(j);
                    ItineraryStop stop = new ItineraryStop(
                            sJson.getString("id"),
                            sJson.getString("name"),
                            sJson.getDouble("latitude"),
                            sJson.getDouble("longitude"),
                            sJson.getString("notes")
                    );
                    stopList.add(stop);
                }

                loadedItineraries.add(new Itinerary(id, record, stopList));
            }
            System.out.println("DAO: Loaded " + loadedItineraries.size() + " itineraries.");

        } catch (Exception e) {
            System.err.println("DAO Error Loading File: " + e.getMessage());
        }
        return loadedItineraries;
    }

    // API Calculation (Use Case 4)

    @Override
    public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
        if (stops == null || stops.size() < 2) {
            return new RouteInfo(0.0, 0.0, "Not enough stops for a route");
        }

        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < stops.size(); i++) {
            ItineraryStop stop = stops.get(i);
            if (i > 0) coords.append(";");
            coords.append(stop.getLongitude()).append(",").append(stop.getLatitude());
        }

        String encodedCoords = URLEncoder.encode(coords.toString(), StandardCharsets.UTF_8);
        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/"
                + encodedCoords
                + "?access_token=" + directionsToken
                + "&overview=false";

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("API Failed: " + response.code());
            if (response.body() == null) throw new IOException("Empty Body");

            JSONObject json = new JSONObject(response.body().string());
            JSONArray routes = json.getJSONArray("routes");

            if (routes.isEmpty()) return new RouteInfo(0.0, 0.0, "No route found");

            JSONObject route0 = routes.getJSONObject(0);
            double km = route0.getDouble("distance") / 1000.0;
            double min = route0.getDouble("duration") / 60.0;
            String summary = String.format("Total: %.1f km, %.0f min", km, min);

            return new RouteInfo(km, min, summary);
        }
    }
}