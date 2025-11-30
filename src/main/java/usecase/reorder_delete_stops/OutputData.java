package usecase.reorder_delete_stops;
import entity.RouteInfo;
import entity.ItineraryStop;
import java.util.List;

public class OutputData {
    private final List<ItineraryStop> orderedStops;
    private final RouteInfo routeInfo;

    public OutputData(List<ItineraryStop> orderedStops, RouteInfo routeInfo) {
        this.orderedStops = orderedStops;
        this.routeInfo = routeInfo;
    }

    public List<ItineraryStop> getOrderedStops() {
        return orderedStops;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }
}
