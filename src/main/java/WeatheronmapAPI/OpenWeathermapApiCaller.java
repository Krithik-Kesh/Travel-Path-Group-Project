package WeatheronmapAPI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OpenWeathermapApiCaller {
    // new client request
    private final OkHttpClient client = new OkHttpClient();
    // private api key variable
    private final String apiKey;
    //url
    private static final String BASE_URL = "https://api.openweathermap.org/data/3.0/onecall?";

    public OpenWeathermapApiCaller(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getJson(WeatherRequest weatherRequest) throws IOException {

        // CONSTRUCT THE FINAL URL:
        // BASE_URL + queryString + "&appid=API_KEY"
        String url = BASE_URL + weatherRequest.toString() + "&appid=" + this.apiKey;

        // MAIN REQUEST
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // IF REQUEST IS NOT 200
            if (!response.isSuccessful()) {
                throw new IOException("API Request Failed. Code: "
                        + response.code() + " URL: " + url);
            }

            // RETURN JSON BODY STRING
            if (response.body() == null) {
                return "{}"; // RETURN EMPTY JSON IF none
            }
            return response.body().string();
        }
    }
}
