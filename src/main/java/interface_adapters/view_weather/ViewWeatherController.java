package interface_adapters.view_weather;

import use_case.view_weather.InputData;
import use_case.view_weather.ViewWeatherInputBound;

// Controller for User Stories 2 & 3
//Called by the UI when user wants to view weather for some destination.

public class ViewWeatherController {

    private final ViewWeatherInputBound interactor;

    public ViewWeatherController(ViewWeatherInputBound interactor) {
        this.interactor = interactor;
    }

    public void viewWeather(double latitude,
                            double longitude,
                            String destinationLabel) {
        InputData input = new InputData(latitude, longitude, destinationLabel);
        interactor.execute(input);
    }
}
