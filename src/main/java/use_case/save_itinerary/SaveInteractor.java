package use_case.save_itinerary;

import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import entity.TravelRecord;
import data_access.WeatherDataAccessInterface;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface;
import java.util.List;
import java.util.UUID;

public class SaveInteractor implements SaveInputBoundary {

    private final RouteDataAccessInterface routeData;
    private final SaveOutputBoundary presenter;

    public SaveInteractor(RouteDataAccessInterface routeData,
                          SaveOutputBoundary presenter) {
        this.routeData = routeData;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveInput input) {
        try {
            // 1. GET DATA: Grab the list of stops you added in memory
            List<ItineraryStop> stops = routeData.getStops();

            // 2. GET STATS: Recalculate distance to ensure it's accurate
            RouteInfo stats = routeData.getRoute(stops);

            // 3. CREATE TRAVEL RECORD: Convert raw stats into the Entity
            String origin = stops.isEmpty() ? "Unknown" : stops.get(0).getName();
            String dest = stops.isEmpty() ? "Unknown" : stops.get(stops.size() - 1).getName();

            TravelRecord record = new TravelRecord(
                    input.getUsername(),
                    origin,
                    dest,
                    stats.getDurationMinutes() + " mins",
                    "Current Weather: " + stats.getCurrent,
                    "Total Distance: " + stats.getDistance() + " km",
                    "Pack light (Placeholder)"
            );

            // 4. CREATE ITINERARY: PACKAGE + Record + ID together
            String uniqueID = UUID.randomUUID().toString();
            Itinerary itinerary = new Itinerary(uniqueID, record, stops);

            // 5. SAVE: SEND DATA TO DATAACCESS
            routeData.saveItinerary(itinerary);

            // 6. SUCCESS: TELL VIEW IT WORKED
            SaveOutput output = new SaveOutput(record);
            presenter.present(output);

        } catch (Exception e) {
            System.out.println("Error saving itinerary: " + e.getMessage());
        }
    }
}