package ui;

import GeolocationsAPIs.APICaller;
import GeolocationsAPIs.GeocodingService;
import WeatheronmapAPI.OpenWeathermapApiCaller;
import data_access.OpenWeatherWeatherDataAccess;
import interface_adapters.view_weather.ViewWeatherController;
import interface_adapters.view_weather.ViewWeatherPresenter;
import interface_adapters.view_weather.WeatherViewModel;
import data_access.WeatherDataAccessInterface;
import usecase.view_weather.ViewWeatherInputBound;
import usecase.view_weather.ViewWeatherInteractor;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.time.ZoneId;

public class WeatherDemoApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 载入 .env 里的 API keys
            Dotenv dotenv = Dotenv.load();
            String openWeatherKey = dotenv.get("OPENWEATHER_API_KEY");
            if (openWeatherKey == null) {
                System.err.println("Missing OPENWEATHER_API_KEY in .env");
            }

            // 2. 实例化 OpenWeather 调用器 + data access
            OpenWeathermapApiCaller weatherApiCaller =
                    new OpenWeathermapApiCaller(openWeatherKey);
            WeatherDataAccessInterface weatherGateway =
                    new OpenWeatherWeatherDataAccess(weatherApiCaller, ZoneId.systemDefault());

            // 3. Geocoding service (Google)
            GeocodingService geocodingService =
                    new GeocodingService(new APICaller());

            // 4. ViewModel + Presenter + Interactor + Controller
            WeatherViewModel viewModel = new WeatherViewModel();
            ViewWeatherPresenter presenter = new ViewWeatherPresenter(viewModel);
            ViewWeatherInputBound interactor =
                    new ViewWeatherInteractor(weatherGateway, presenter);
            ViewWeatherController controller =
                    new ViewWeatherController(interactor);

            // 5. UI Frame
            WeatherDemoFrame frame = new WeatherDemoFrame(
                    geocodingService, controller, viewModel);
            frame.setVisible(true);
        });
    }
}
