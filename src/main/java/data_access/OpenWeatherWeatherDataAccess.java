package data_access;

import WeatheronmapAPI.OpenWeathermapApiCaller;
import WeatheronmapAPI.WeatherRequest;
import entity.DailyWeather;
import entity.WeatherData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

// Adapter that calls OpenWeather API and converts JSON
// into our domain entities WeatherData / DailyWeather

public class OpenWeatherWeatherDataAccess implements WeatherDataAccessInterface {

    private final OpenWeathermapApiCaller apiCaller;
    private final ZoneId zoneId;

    // apiCaller low-level API caller
    // zoneId: time zone for converting epoch seconds to LocalDate

    public OpenWeatherWeatherDataAccess(OpenWeathermapApiCaller apiCaller,
                                        ZoneId zoneId) {
        this.apiCaller = apiCaller;
        this.zoneId = zoneId;
    }

    // current weather (User Story 2)

    @Override
    public WeatherData getCurrentWeather(double lat, double lon) throws IOException {
        // exclude hour/ minute alerts to keep JSON smaller
        WeatherRequest request =
                new WeatherRequest(lat, lon, "metric", "minutely,hourly,alerts");

        String json = apiCaller.getJson(request);
        JSONObject root = new JSONObject(json);
        JSONObject current = root.getJSONObject("current");

        double temp = current.getDouble("temp");
        double windSpeed = current.optDouble("wind_speed", 0.0);
        // percip = precipitation
        double precip = extractPrecipitation(current);
        // d = description
        String d = extractDescription(current.getJSONArray("weather"));

        return new WeatherData(temp, d, windSpeed, precip);
    }

    // 7-day forecast (User Story 3)

    @Override
    public List<DailyWeather> getDailyForecast(double lat, double lon) throws IOException {
        // exclude current/hourly/alerts, keep daily
        WeatherRequest request =
                new WeatherRequest(lat, lon, "metric", "minutely,hourly,alerts,current");

        String json = apiCaller.getJson(request);
        JSONObject root = new JSONObject(json);
        JSONArray dailyArray = root.getJSONArray("daily");

        List<DailyWeather> result = new ArrayList<>();
        int days = Math.min(dailyArray.length(), 7);   // only first 7 days

        for (int i = 0; i < days; i++) {
            JSONObject day = dailyArray.getJSONObject(i);

            long epochSeconds = day.getLong("dt");
            LocalDate date = Instant.ofEpochSecond(epochSeconds)
                    .atZone(zoneId)
                    .toLocalDate();

            JSONObject tempObj = day.getJSONObject("temp");
            double min = tempObj.getDouble("min");
            double max = tempObj.getDouble("max");
            // d = description
            String d = extractDescription(day.getJSONArray("weather"));

            result.add(new DailyWeather(date, min, max, d));
        }

        return result;
    }

    // small JSON helpers

    private static String extractDescription(JSONArray weatherArray) {
        if (weatherArray.isEmpty()) {
            return "";
        }
        JSONObject first = weatherArray.getJSONObject(0);
        return first.optString("description", "");
    }

    private static double extractPrecipitation(JSONObject current) {
        double precip = 0.0;

        if (current.has("rain")) {
            JSONObject rain = current.getJSONObject("rain");
            // OpenWeather uses "1h" as mm in last hour
            precip += rain.optDouble("1h", 0.0);
        }
        if (current.has("snow")) {
            JSONObject snow = current.getJSONObject("snow");
            precip += snow.optDouble("1h", 0.0);
        }
        return precip;
    }
}
