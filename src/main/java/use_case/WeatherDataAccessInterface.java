package use_case;

import entity.WeatherData;
import entity.DailyWeather;

import java.io.IOException;
import java.util.List;

public interface WeatherDataAccessInterface {

    WeatherData getCurrentWeather(double lat, double lon) throws IOException;

    List<DailyWeather> getDailyForecast(double lat, double lon) throws IOException;
}
