package GeolocationsAPIs;

import entity.ItineraryStop;
import entity.StopFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.json.JSONArray;

public class APICaller {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    public APICaller() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("GOOGLE_API_KEY");
    }

    /**
     GETS RAW JSON FROM GOOOGLE
     */
    public String getJson(String encodedLocation) throws IOException {
        String url = BASE_URL + encodedLocation + "&key=" + this.apiKey;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API Request Failed. Code: " + response.code());
            }

            if (response.body() == null) {
                return "{}";
            }
            // Returns the raw text (e.g., "{ 'results': ... }")
            return response.body().string();
        }
    }

    /**
     PARSES THE JSON INTO A ITENERARY STOP ENTITY
     */
    public ItineraryStop parseJsonToStop(String jsonString, String rawInput) {
        JSONObject obj = new JSONObject(jsonString);

        // SAFETY CHECK: WAS GOOGLE VALID
        if (!obj.has("status") || !obj.getString("status").equals("OK")) {
            throw new RuntimeException("API Error or No Results Found. Status: "
                    + (obj.has("status") ? obj.getString("status") : "Unknown"));
        }

        JSONArray results = obj.getJSONArray("results");
        JSONObject bestMatch = results.getJSONObject(0);
        JSONObject location = bestMatch.getJSONObject("geometry").getJSONObject("location");

        double lat = location.getDouble("lat");
        double lon = location.getDouble("lng");
        String formalName = bestMatch.getString("formatted_address");

        // MAKES ITENERARY STOP VIA STOPFACTORY ENTITY
        StopFactory factory = new StopFactory();
        return factory.create(formalName, lat, lon);
    }
}
