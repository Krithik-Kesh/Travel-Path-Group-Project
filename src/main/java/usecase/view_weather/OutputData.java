package usecase.view_weather;

import entity.DailyWeather;
import entity.WeatherData;

import java.util.List;

// Data sent to the presenter from this use case

public record OutputData(String destinationLabel, WeatherData currentWeather, List<DailyWeather> forecast,
                         String clothingSuggestionText) {
}
