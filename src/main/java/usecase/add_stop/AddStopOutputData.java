package usecase.add_stop;

import entity.ItineraryStop;
import entity.RouteInfo;
import java.util.List;

public class AddStopOutputData {
    private final List<ItineraryStop> stops;
    private final RouteInfo routeInfo;

    public AddStopOutputData(List<ItineraryStop> stops, RouteInfo routeInfo) {
        this.stops = stops;
        this.routeInfo = routeInfo;
    }

    public List<ItineraryStop> getStops() { return stops; }
    public RouteInfo getRouteInfo() { return routeInfo; }
}
