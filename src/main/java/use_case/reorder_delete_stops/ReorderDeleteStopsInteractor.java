package use_case.reorder_delete_stops;
import data_access.RouteDataAccessInterface;
import entity.ItineraryStop;
import entity.RouteInfo;
import java.io.IOException;
import java.util.List;

public class ReorderDeleteStopsInteractor implements InputBoundary {

    private final RouteDataAccessInterface routeDataAccess;
    private final OutputBoundary presenter;

    public ReorderDeleteStopsInteractor(RouteDataAccessInterface routeDataAccess,
                                  OutputBoundary presenter) {
        this.routeDataAccess = routeDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(InputData inputData) {
        List<ItineraryStop> stops = inputData.getOrderedStops();
        try {
            RouteInfo routeInfo = routeDataAccess.getRoute(stops);

            OutputData outputData = new OutputData(stops, routeInfo);

            presenter.present(outputData);
        } catch (IOException e) {
            presenter.presentError("Unable to calculate route.");
        }
    }
}
