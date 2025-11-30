package interfaceadapter.reorder_delete_stops;
import entity.ItineraryStop;
import usecase.reorder_delete_stops.InputBoundary;
import usecase.reorder_delete_stops.InputData;
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
