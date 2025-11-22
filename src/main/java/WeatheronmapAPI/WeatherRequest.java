package WeatheronmapAPI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//Holds and formats the query parameters for the OpenWeather One Call 3.0 API.

public class WeatherRequest {

    private final double lat;
    private final double lon;
    private final String units;        // eg "metric", "imperial"
    private final String excludeParts; // eg "minutely,hourly,alerts"

    public WeatherRequest(double lat, double lon) {
        this(lat, lon, "metric", "minutely,hourly,alerts");
    }

    public WeatherRequest(double lat, double lon, String units, String excludeParts) {
        this.lat = lat;
        this.lon = lon;
        this.units = units;
        this.excludeParts = excludeParts;
    }

    // conversion
    public String toString() {
        String encodedUnits = URLEncoder.encode(units, StandardCharsets.UTF_8);
        String encodedExclude = URLEncoder.encode(excludeParts, StandardCharsets.UTF_8);

        return "lat=" + lat +
                "&lon=" + lon +
                "&exclude=" + encodedExclude +
                "&units=" + encodedUnits;
    }

    /**
     * Returns the query string used by the OpenWeather API caller.
     * We just reuse the existing toString() implementation.
     */
    public String toQueryString() {
        return this.toString();
    }



    // optional
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getUnits() {
        return units;
    }

    public String getExcludeParts() {
        return excludeParts;
    }
}

