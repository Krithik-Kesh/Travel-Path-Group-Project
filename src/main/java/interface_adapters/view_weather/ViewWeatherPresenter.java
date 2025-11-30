package interface_adapters.view_weather;

import entity.DailyWeather;
import entity.WeatherData;
import use_case.view_weather.OutputData;
import use_case.view_weather.ViewWeatherOutputBound;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ViewWeatherPresenter implements ViewWeatherOutputBound {

    private final WeatherViewModel viewModel;

    public ViewWeatherPresenter(WeatherViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(OutputData Data) {
        // From output data get the current weather and the forecast data for future
        WeatherData current = Data.getCurrentWeather();
        List<DailyWeather> forecast = Data.getForecast();

        //Put all the features of the weather into a String
        String currentText = String.format(
                "%.1f°C, %s%nWind: %.1f m/s%nPrecipitation: %.1f mm",
                current.getTemperature(),
                current.getDescription(),
                current.getWindSpeed(),
                current.getPrecipitation()
        );

        // Put the 7 days forecast into a String
        DateTimeFormatter df = DateTimeFormatter.ofPattern("EEE MMM d");

        String forecastText = forecast.stream()
                .map(d -> String.format("%s   min %.1f°C / max %.1f°C   %s",
                        df.format(d.getDate()),
                        d.getMinTemperature(),
                        d.getMaxTemperature(),
                        d.getDescription()))
                .collect(Collectors.joining(System.lineSeparator()));

        // Give the data to viewmodel

        viewModel.setErrorMessage("");
        viewModel.setDestinationLabel(Data.getDestinationLabel());
        viewModel.setCurrentText(currentText);
        viewModel.setTipsText(Data.getClothingSuggestionText());
        viewModel.setForecastText(forecastText);
    }


    //When error occur, what presenter will do
    @Override
    public void presentError(String message) {
        viewModel.setErrorMessage(message);
        viewModel.setCurrentText("");
        viewModel.setTipsText("");
        viewModel.setForecastText("");
    }
}
