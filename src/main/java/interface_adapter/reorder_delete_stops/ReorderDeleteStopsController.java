package interface_adapter.reorder_delete_stops;
import entity.ItineraryStop;
import use_case.reorder_delete_stops.InputBoundary;
import use_case.reorder_delete_stops.InputData;
import java.util.List;

public class ReorderDeleteStopsController {
    private final InputBoundary interactor;

    public ReorderDeleteStopsController(InputBoundary interactor) {
        this.interactor = interactor;
    }

    public void updateOrderedStops(List<ItineraryStop> orderedStops) {
        InputData input = new InputData(orderedStops);
        interactor.execute(input);
    }
}
