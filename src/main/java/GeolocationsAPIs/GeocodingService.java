package GeolocationsAPIs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

// Uses UserLocation + APICaller to turn a free-form address
// into latitude/longitude via Google Geocoding API.

public class GeocodingService {

    private final APICaller apiCaller;

    public GeocodingService(APICaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    public LatLon geocode(String rawLocation) throws IOException {
        UserLocation userLocation = new UserLocation(rawLocation);
        String encoded = userLocation.encodedLocation;

        String json = apiCaller.getJson(encoded);

        JSONObject root = new JSONObject(json);
        String status = root.optString("status", "");
        if (!"OK".equals(status)) {
            throw new IOException("Geocoding failed: status=" + status);
        }

        JSONArray results = root.getJSONArray("results");
        if (results.isEmpty()) {
            throw new IOException("Geocoding returned no results.");
        }

        JSONObject location = results.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location");

        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");

        return new LatLon(lat, lng);
    }


    public static class LatLon {
        private final double lat;
        private final double lon;

        public LatLon(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }
}
