package usecase.view_weather;

import entity.DailyWeather;
import entity.TipGenerator;
import entity.WeatherData;
import data_access.WeatherDataAccessInterface;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//Use case interactor for User Story 3:
// It will get current weather and 7-day forecast by lat and lon
//generate packing tips

public class ViewWeatherInteractor implements ViewWeatherInputBound {

    private final WeatherDataAccessInterface weatherGateway;
    private final ViewWeatherOutputBound presenter;

    public ViewWeatherInteractor(WeatherDataAccessInterface weatherGateway,
                                 ViewWeatherOutputBound presenter) {
        this.weatherGateway = weatherGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(InputData inputData) {
        try {
            WeatherData current = weatherGateway.getCurrentWeather(
                    inputData.getLatitude(), inputData.getLongitude());
            List<DailyWeather> forecast = weatherGateway.getDailyForecast(
                    inputData.getLatitude(), inputData.getLongitude());

            String tipsText = TipGenerator.generate(current)
                    .stream()
                    .map(t -> "• " + t)
                    .collect(Collectors.joining("\n"));

            OutputData output = new OutputData(
                    inputData.getDestinationLabel(),
                    current,
                    forecast,
                    tipsText
            );
            presenter.present(output);
        } catch (IOException e) {
            presenter.presentError("Unable to load weather data. Please try again later.");
        }
    }
}
