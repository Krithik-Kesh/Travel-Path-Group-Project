package csc207_group;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;

// --- DATA ACCESS IMPORTS ---
import data_access.RouteDataAccess;
import data_access.InMemoryHistoryRepo; // Use your actual HistoryRepo implementation
import interface_adapter.view_weather_adapt.WeatherViewModel;
import use_case.get_previous_data.HistoryRepo;
import data_access.OpenWeatherWeatherDataAccess;
import data_access.WeatherDataAccessInterface;
import WeatheronmapAPI.OpenWeathermapApiCaller;
import GeolocationsAPIs.APICaller;
import GeolocationsAPIs.GeocodingService;
import io.github.cdimascio.dotenv.Dotenv;

//VIEW MODEL IMPORTS
import interface_adapter.IteneraryViewModel;

//ADD STOP IMPORTS
import interface_adapter.add_multiple_stops.AddStopController;
import interface_adapter.add_multiple_stops.AddStopPresenter;
import use_case.add_stop.AddStopInteractor;
import entity.StopFactory;

//SAVE ITINERARY IMPORTS
import interface_adapter.save_itinerary.SaveController;
import interface_adapter.save_itinerary.SavePresenter;
import use_case.save_itinerary.SaveInteractor;

// --- VIEW WEATHER IMPORTS ---
import interface_adapter.view_weather_adapt.ViewWeatherController;
import interface_adapter.view_weather_adapt.ViewWeatherPresenter;
import use_case.view_weather.ViewWeatherInteractor;

// --- VIEW IMPORTS ---
//import view.ItineraryView; // THIS IS THE FRAME ONE YOU NEED TO MAKE IT STEVEN

public class Main {
    public static void main(String[] args) {
        // Load Env Variables
        Dotenv dotenv = Dotenv.load();
        String openWeatherKey = dotenv.get("OPENWEATHER_API_KEY");
        if (openWeatherKey == null) {
            System.err.println("Missing OPENWEATHER_API_KEY in .env");
        }

        // 1. SETUP THE WINDOW
        JFrame application = new JFrame("Travel Path Builder");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setSize(1000, 800);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        // 2. CREATE SHARED DATA ACCESS OBJECTS

        // A. Route Data Access (Holds the list of stops & Mapbox logic)
        RouteDataAccess routeDataAccess = new RouteDataAccess();

        // B. History Repo (For saving final trips)
        // If RouteDataAccess implements HistoryRepo, you can reuse it.
        // Otherwise, create the separate repo.
        HistoryRepo historyRepo = new InMemoryHistoryRepo();

        // C. Weather Data Access (For View Weather use case)
        OpenWeathermapApiCaller weatherApiCaller = new OpenWeathermapApiCaller(openWeatherKey);
        WeatherDataAccessInterface weatherGateway =
                new OpenWeatherWeatherDataAccess(weatherApiCaller, ZoneId.systemDefault());

        // D. Geocoding Service (For searching cities)
        GeocodingService geocodingService = new GeocodingService(new APICaller());


        // 3. CREATE VIEW MODELS (The State)
        IteneraryViewModel itineraryViewModel = new IteneraryViewModel();
        WeatherViewModel weatherViewModel = new WeatherViewModel();


        // 4. WIRED UP USE CASES

        // --- Use Case: Add Stop ---
        AddStopPresenter addStopPresenter = new AddStopPresenter(itineraryViewModel);
        StopFactory stopFactory = new StopFactory();
        AddStopInteractor addStopInteractor = new AddStopInteractor(
                routeDataAccess,
                addStopPresenter,
                stopFactory
        );
        AddStopController addStopController = new AddStopController(addStopInteractor);

        // --- Use Case: Save Itinerary ---
        SavePresenter savePresenter = new SavePresenter(itineraryViewModel);
        SaveInteractor saveInteractor = new SaveInteractor(
                routeDataAccess,
                savePresenter
        );
        SaveController saveController = new SaveController(saveInteractor);

        // --- Use Case: View Weather ---
        ViewWeatherPresenter weatherPresenter = new ViewWeatherPresenter(weatherViewModel);
        ViewWeatherInteractor weatherInteractor = new ViewWeatherInteractor(weatherGateway, weatherPresenter);
        ViewWeatherController weatherController = new ViewWeatherController(weatherInteractor);

        /* STEVEN THIS IS THE CODE USED TO CONNECT MAIN TO UI
        // 5. CREATE THE UI (The View)
        // You likely need to update your ItineraryView constructor to accept all these controllers
        ItineraryView itineraryView = new ItineraryView(
                addStopController,
                saveController,
                weatherController, // Pass this if the view has a "Check Weather" button
                itineraryViewModel,
                weatherViewModel,
                geocodingService   // If the view uses this directly for auto-complete
        );

        views.add(itineraryView, itineraryView.viewName);

        // 6. LAUNCH
        cardLayout.show(views, itineraryView.viewName);
        application.setVisible(true);
        */
    }
}