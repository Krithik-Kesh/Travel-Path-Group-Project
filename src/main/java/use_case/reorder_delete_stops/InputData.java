package use_case.reorder_delete_stops;
import entity.ItineraryStop;
import java.util.List;

public class InputData {
    private final List<ItineraryStop> orderedStops;

    public InputData(List<ItineraryStop> orderedStops) {
        this.orderedStops = orderedStops;
    }
    public List<ItineraryStop> getOrderedStops() {
        return orderedStops;
    }
}