package use_case.save_itinerary;

import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import entity.TravelRecord;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

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

        //DATE CHECK IS FIRST (FAIL FAST IS USED IF INVALID)
        String dateString = input.getStartDateInput();
        if (dateString == null || dateString.isEmpty()) {
            System.out.println("Error: Please insert a start date.");
            presenter.prepareFailView("Please insert a start date.");
            return;
        }

        //DATE PARSING
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dateString);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }
        try {
            // GET DATA: GRAB THE LISTS OF STOPS IN THE MEMORY
            List<ItineraryStop> stops = routeData.getStops();

            // GET STATS: RECALCUALTE DISTANCE
            RouteInfo stats = routeData.getRoute(stops);

            // CREATE TRAVEL RECORD: CONVERT THE RAW STRINGS INTO ENTITIES
            // IF THE ORIGIN AND DESTINATION HAS NOTHING ITS JUST GOING TO BE RETURNED AS UNKNOWN
            String origin = stops.isEmpty() ? "Unknown" : stops.get(0).getName();
            String dest = stops.isEmpty() ? "Unknown" : stops.get(stops.size() - 1).getName();

            TravelRecord record = new TravelRecord(
                    input.getUsername(),
                    origin,
                    dest,
                    stats.getDurationMinutes() + " mins",
                    "Current Weather: " + input.getWeatherSummary(),
                    "Total Distance: " + stats.getDistance() + " km",
                    "Clothing Tips: " + input.getClothingSuggestion());

            // 4. CREATE ITINERARY: PACKAGE + RECORD + ID TOGETHER
            String uniqueID = UUID.randomUUID().toString();
            Itinerary itinerary = new Itinerary(uniqueID, record, stops);

            // 5. SET DATE
            itinerary.setStartDate(parsedDate);
            routeData.setStartDate(parsedDate);

            // SAVE: SEND DATA TO DATAACCESS
            routeData.saveItinerary(itinerary);

            // SUCCESS: TELL VIEW IT WORKED
            SaveOutput output = new SaveOutput(record);
            presenter.present(output);

        } catch (Exception e) {
            System.out.println("Error saving itinerary: " + e.getMessage());
        }
    }
}