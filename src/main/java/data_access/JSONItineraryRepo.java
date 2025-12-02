package data_access;

import entity.Itinerary;
import entity.ItineraryStop;
import entity.TravelRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import usecase.ItineraryRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * File-backed JSON database for itineraries.
 */
public class JSONItineraryRepo implements ItineraryRepository {

    private final Path filePath;

    public JSONItineraryRepo(String fileName) {
        this.filePath = Path.of(fileName);
    }

    @Override
    public Itinerary findById(String id) {
        for (Itinerary it : loadAll()) {
            if (it.getId().equals(id)) {
                return it;
            }
        }
        return null;
    }

    @Override
    public void save(Itinerary itinerary) {
        List<Itinerary> all = loadAll();
        boolean replaced = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(itinerary.getId())) {
                all.set(i, itinerary);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            all.add(itinerary);
        }

        writeAll(all);
    }

    private List<Itinerary> loadAll() {
        List<Itinerary> result = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return result;
        }

        try {
            String content = Files.readString(filePath);
            if (content.isBlank()) {
                return result;
            }

            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                result.add(fromJson(json));
            }
        } catch (IOException e) {
            System.err.println("Failed to read itineraries: " + e.getMessage());
        }

        return result;
    }

    private void writeAll(List<Itinerary> itineraries) {
        JSONArray array = new JSONArray();
        for (Itinerary it : itineraries) {
            array.put(toJson(it));
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(array.toString(4));
        } catch (IOException e) {
            System.err.println("Failed to write itineraries: " + e.getMessage());
        }
    }

    private JSONObject toJson(Itinerary itinerary) {
        JSONObject json = new JSONObject();
        json.put("id", itinerary.getId());

        TravelRecord record = itinerary.getRecord();
        if (record != null) {
            JSONObject r = new JSONObject();
            r.put("username", record.getUsername());
            r.put("origin", record.getOrigin());
            r.put("destination", record.getDestination());
            r.put("timeNeeded", record.getTimeNeeded());
            r.put("weatherSummary", record.getWeatherSummary());
            r.put("optimalPath", record.getOptimalPath());
            r.put("clothingSuggestion", record.getClothingSuggestion());
            json.put("record", r);
        }

        JSONArray stopsArray = new JSONArray();
        for (ItineraryStop stop : itinerary.getStops()) {
            JSONObject s = new JSONObject();
            s.put("id", stop.getId());
            s.put("cityName", stop.getName());
            s.put("latitude", stop.getLatitude());
            s.put("longitude", stop.getLongitude());
            s.put("notes", stop.getNotes());
            stopsArray.put(s);
        }
        json.put("stops", stopsArray);

        if (itinerary.getStartDate() != null) {
            json.put("startDate", itinerary.getStartDate().toString());
        }

        return json;
    }

    private Itinerary fromJson(JSONObject json) {
        String id = json.getString("id");

        TravelRecord record = null;
        if (json.has("record")) {
            JSONObject r = json.getJSONObject("record");
            record = new TravelRecord(
                    r.getString("username"),
                    r.getString("origin"),
                    r.getString("destination"),
                    r.getString("timeNeeded"),
                    r.getString("weatherSummary"),
                    r.getString("optimalPath"),
                    r.getString("clothingSuggestion")
            );
        }

        List<ItineraryStop> stops = new ArrayList<>();
        JSONArray stopsArray = json.optJSONArray("stops");
        if (stopsArray != null) {
            for (int i = 0; i < stopsArray.length(); i++) {
                JSONObject s = stopsArray.getJSONObject(i);
                String cityName = s.optString("cityName", "");
                ItineraryStop stop = new ItineraryStop(
                        s.getString("id"),
                        cityName,
                        s.getDouble("latitude"),
                        s.getDouble("longitude"),
                        s.optString("notes", "")
                );
                stops.add(stop);
            }
        }

        Itinerary itinerary = new Itinerary(id, record, stops);

        if (json.has("startDate")) {
            itinerary.setStartDate(LocalDate.parse(json.getString("startDate")));
        }

        return itinerary;
    }
}



