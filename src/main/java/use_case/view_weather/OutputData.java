package use_case.view_weather;

import entity.DailyWeather;
import entity.WeatherData;

import java.util.List;

public class OutputData {

    private final String destinationLabel;
    private final WeatherData currentWeather;
    private final List<DailyWeather> forecast;
    private final String clothingSuggestionText;

    public OutputData(String destinationLabel,
                      WeatherData currentWeather,
                      List<DailyWeather> forecast,
                      String clothingSuggestionText) {
        this.destinationLabel = destinationLabel;
        this.currentWeather = currentWeather;
        this.forecast = forecast;
        this.clothingSuggestionText = clothingSuggestionText;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public WeatherData getCurrentWeather() {
        return currentWeather;
    }

    public List<DailyWeather> getForecast() {
        return forecast;
    }

    public String getClothingSuggestionText() {
        return clothingSuggestionText;
    }

    public String destinationLabel() {
        return destinationLabel;
    }

    public WeatherData currentWeather() {
        return currentWeather;
    }

    public List<DailyWeather> forecast() {
        return forecast;
    }

    public String clothingSuggestionText() {
        return clothingSuggestionText;
    }
}



