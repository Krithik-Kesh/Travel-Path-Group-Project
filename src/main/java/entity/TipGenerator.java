package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure-domain class: given weather, generates packing tips.
 */
public final class TipGenerator {

    private TipGenerator() {
    }

    public static List<String> generate(WeatherData weather) {
        List<String> tips = new ArrayList<>();
        double temp = weather.getTemperature();
        double precipitation = weather.getPrecipitation();
        double wind = weather.getWindSpeed();

        if (temp <= 0) {
            tips.add("Please Bring a winter coat.");
            tips.add("Please Pack gloves, scarf, and a warm hat.");
        } else if (temp <= 10) {
            tips.add("Please Bring a medium-weight jacket or sweater.");
            tips.add("Consider layering (t-shirt + hoodie).");
        } else if (temp <= 20) {
            tips.add("Light jacket or long sleeves should be enough for this trip.");
        } else {
            tips.add("T-shirts and lighter clothing are enough for this trip.");
        }

        if (precipitation > 0.1) {
            tips.add("Pack an umbrella or rain jacket.");
        }

        if (wind > 8.0) {
            tips.add("It may be windy — bring a windbreaker.");
        }

        return tips;
    }
}
