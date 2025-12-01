package csc207_group;

import GeolocationsAPIs.APICaller;
import GeolocationsAPIs.GeocodingService;
import WeatheronmapAPI.OpenWeathermapApiCaller;

import data_access.InMemoryItineraryRepo;
import data_access.OpenWeatherWeatherDataAccess;
import data_access.RouteDataAccess;
import data_access.WeatherDataAccessInterface;

import entity.StopFactory;

import interfaceadapter.IteneraryViewModel;
import interfaceadapter.add_multiple_stops.AddStopController;
import interfaceadapter.add_multiple_stops.AddStopPresenter;
import interfaceadapter.notes.AddNoteToStopController;
import interfaceadapter.notes.AddNoteToStopPresenter;
import interfaceadapter.notes.NotesViewModel;
import interfaceadapter.view_weather_adapt.ViewWeatherController;
import interfaceadapter.view_weather_adapt.ViewWeatherPresenter;
import interfaceadapter.view_weather_adapt.WeatherViewModel;

import io.github.cdimascio.dotenv.Dotenv;

import ui.WeatherDemoFrame;
import usecase.ItineraryRepository;
import usecase.add_note_to_stop.AddNoteToStopInputBoundary;
import usecase.add_note_to_stop.AddNoteToStopInteractor;
import usecase.add_note_to_stop.AddNoteToStopOutputBoundary;
import usecase.add_stop.AddStopInputBoundary;
import usecase.add_stop.AddStopInteractor;
import usecase.view_weather.ViewWeatherInputBound;
import usecase.view_weather.ViewWeatherInteractor;

import javax.swing.*;
import java.time.ZoneId;

public class Main {

    public static void main(String[] args) {
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

            ItineraryRepository itineraryRepository = new InMemoryItineraryRepo();
            String itineraryId = "demo-itinerary";

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
                    itineraryId
            );
            frame.setVisible(true);
        });
    }
}

