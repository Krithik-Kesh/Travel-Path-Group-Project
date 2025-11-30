package data_access;
import entity.ItineraryStop;
import entity.RouteInfo;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RouteDataAccess implements RouteDataAccessInterface {
    private final OkHttpClient client = new OkHttpClient();
    private final String directionsToken;

    public RouteDataAccess() {
        Dotenv dotenv = Dotenv.load();
        this.directionsToken = dotenv.get("DIRECTIONS_TOKEN");
        if (this.directionsToken == null) {
            throw new IllegalStateException("DIRECTIONS_TOKEN is not found in .env");
        }
    }

    // 2. IMPLEMENTED ADD METHOD (Must be outside getRoute)
    @Override
    public void addStop(ItineraryStop stop) {
        this.stops.add(stop);
    }

    // 3. IMPLEMENTED GET METHOD (Must be outside getRoute)
    @Override
    public List<ItineraryStop> getStops() {
        return this.stops;
    }
    @Override
    public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
        if (stops == null || stops.size() < 2) {
            return new RouteInfo(0.0, 0.0, "Not enough stops for a route");
        }
        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < stops.size(); i++) {
            ItineraryStop stop = stops.get(i);
            if (i > 0) {
                coords.append(";");
            }
            coords.append(stop.getLongitude())
                    .append(",")
                    .append(stop.getLatitude());
        }

        String encodedCoords = URLEncoder.encode(coords.toString(), StandardCharsets.UTF_8);

        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/"
                + encodedCoords
                + "?access_token=" + directionsToken
                + "&overview=false";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Directions failed. Code: "
                        + response.code() + " URL: " + url);
            }
            if (response.body() == null) {
                throw new IOException("Directions returned empty body");
            }

            String jsonBody = response.body().string();
            JSONObject json = new JSONObject(jsonBody);
            JSONArray routes = json.getJSONArray("routes");
            if (routes.isEmpty()) {
                return new RouteInfo(0.0, 0.0, "No route found");
            }

            JSONObject route0 = routes.getJSONObject(0);
            double distanceMeters = route0.getDouble("distance");  // meters
            double durationSeconds = route0.getDouble("duration"); // seconds

            double distanceKm = distanceMeters / 1000.0;
            double durationMinutes = durationSeconds / 60.0;

            String summary = String.format("Total: %.1f km, %.0f min", distanceKm, durationMinutes);

            return new RouteInfo(distanceKm, durationMinutes, summary);
        }
    }
}

