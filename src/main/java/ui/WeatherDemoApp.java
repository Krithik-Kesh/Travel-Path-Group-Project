package ui;

import GeolocationsAPIs.APICaller;
import GeolocationsAPIs.GeocodingService;
import WeatheronmapAPI.OpenWeathermapApiCaller;
import data_access.JSONItineraryRepo;
import data_access.OpenWeatherWeatherDataAccess;
import data_access.RouteDataAccess;
import data_access.WeatherDataAccessInterface;
import entity.StopFactory;
import entity.Itinerary;
import interfaceadapter.IteneraryViewModel;
import interfaceadapter.add_multiple_stops.AddStopController;
import interfaceadapter.add_multiple_stops.AddStopPresenter;
import interfaceadapter.notes.AddNoteToStopController;
import interfaceadapter.notes.AddNoteToStopPresenter;
import interfaceadapter.notes.NotesViewModel;
import interfaceadapter.set_start_date.SetStartDateController;
import interfaceadapter.set_start_date.SetStartDatePresenter;
import interfaceadapter.set_start_date.SetStartDateViewModel;
import interfaceadapter.view_weather_adapt.ViewWeatherController;
import interfaceadapter.view_weather_adapt.ViewWeatherPresenter;
import interfaceadapter.view_weather_adapt.WeatherViewModel;
import io.github.cdimascio.dotenv.Dotenv;
import usecase.ItineraryRepository;
import usecase.SetStartDate.SetStartDateInputBoundary;
import usecase.SetStartDate.SetStartDateInteractor;
import usecase.SetStartDate.SetStartDateOutputBoundary;
import usecase.add_note_to_stop.AddNoteToStopInputBoundary;
import usecase.add_note_to_stop.AddNoteToStopInteractor;
import usecase.add_note_to_stop.AddNoteToStopOutputBoundary;
import usecase.add_stop.AddStopInputBoundary;
import usecase.add_stop.AddStopInteractor;
import usecase.view_weather.ViewWeatherInputBound;
import usecase.view_weather.ViewWeatherInteractor;
import javax.swing.*;
import java.time.ZoneId;

public class WeatherDemoApp {
    public static void start() {
        SwingUtilities.invokeLater(() -> {

            // from env to get weatherapi key
            Dotenv dotenv = Dotenv.load();
            String openWeatherKey = dotenv.get("OPENWEATHER_API_KEY");
            if (openWeatherKey == null) {
                System.err.println("Missing OPENWEATHER_API_KEY in .env");
            }

            // weather data access
            OpenWeathermapApiCaller weatherApiCaller =
                    new OpenWeathermapApiCaller(openWeatherKey);
            WeatherDataAccessInterface weatherGateway =
                    new OpenWeatherWeatherDataAccess(weatherApiCaller, ZoneId.systemDefault());

            // Geocoding service
            GeocodingService geocodingService =
                    new GeocodingService(new APICaller());

            // Weather use case
            WeatherViewModel weatherViewModel = new WeatherViewModel();
            ViewWeatherPresenter weatherPresenter = new ViewWeatherPresenter(weatherViewModel);
            ViewWeatherInputBound weatherInteractor =
                    new ViewWeatherInteractor(weatherGateway, weatherPresenter);
            ViewWeatherController weatherController =
                    new ViewWeatherController(weatherInteractor);

            // route and itinerary + AddStop use case
            RouteDataAccess routeDataAccess = new RouteDataAccess();
            IteneraryViewModel itineraryViewModel = new IteneraryViewModel();
            StopFactory stopFactory = new StopFactory();

            AddStopPresenter addStopPresenter =
                    new AddStopPresenter(itineraryViewModel);

            AddStopInputBoundary addStopInteractor =
                    new AddStopInteractor(routeDataAccess, addStopPresenter, stopFactory);

            AddStopController addStopController =
                    new AddStopController(addStopInteractor);

            //add Notes
            NotesViewModel notesViewModel = new NotesViewModel();
            AddNoteToStopOutputBoundary notePresenter =
                    new AddNoteToStopPresenter(notesViewModel);

            ItineraryRepository itineraryRepository = new JSONItineraryRepo("itineraries.json");
            String itineraryId = "demo-itinerary";

            // loads saved stops whenever the app starts

            Itinerary existing = itineraryRepository.findById(itineraryId);
            if (existing != null) {
                itineraryViewModel.setStops(existing.getStops());
            }

            SetStartDateViewModel setStartDateViewModel = new SetStartDateViewModel();
            SetStartDateOutputBoundary setStartDatePresenter =
                    new SetStartDatePresenter(setStartDateViewModel);
            SetStartDateInputBoundary setStartDateInteractor =
                    new SetStartDateInteractor(itineraryRepository, setStartDatePresenter);
            SetStartDateController setStartDateController =
                    new SetStartDateController(setStartDateInteractor, itineraryId);

            AddNoteToStopInputBoundary noteInteractor =
                    new AddNoteToStopInteractor(itineraryRepository, notePresenter);
            AddNoteToStopController addNoteController =
                    new AddNoteToStopController(noteInteractor);

            //UI Frame
            WeatherDemoFrame frame = new WeatherDemoFrame(
                    geocodingService,
                    weatherController,
                    weatherViewModel,
                    itineraryViewModel,
                    addStopController,
                    addNoteController,
                    notesViewModel,
                    itineraryRepository,
                    itineraryId,
                    setStartDateController
            );
            frame.setVisible(true);
        });
    }
}
