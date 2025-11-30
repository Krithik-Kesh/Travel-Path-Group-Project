package interfaceadapter.reorder_delete_stops;
import entity.ItineraryStop;
import entity.RouteInfo;
import java.io.IOException;
import java.util.List;
import entity.Itinerary;

public interface RouteDataAccessInterface {
    /**
     * Calls the Directions API when an ordered list of stops is given (in lat and long), and returns route information
     */
    void addStop(ItineraryStop stop);
    List<ItineraryStop> getStops();
    RouteInfo getRoute(List<ItineraryStop> stops) throws IOException;
    // SAVE ITINERARY
    void saveItinerary(Itinerary itinerary);
    //LOAD ITINERARY
    List<Itinerary> loadItineraries();
}

