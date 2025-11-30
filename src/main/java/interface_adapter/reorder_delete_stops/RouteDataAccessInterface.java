package interface_adapter.reorder_delete_stops;
import entity.ItineraryStop;
import entity.RouteInfo;
import java.io.IOException;
import java.util.List;

public interface RouteDataAccessInterface {
    /**
     * Calls the Directions API when an ordered list of stops is given (in lat and long), and returns route information
     */
    RouteInfo getRoute(List<ItineraryStop> stops) throws IOException;

    List<ItineraryStop> getStops();

    void addStop(ItineraryStop newStop);
}
