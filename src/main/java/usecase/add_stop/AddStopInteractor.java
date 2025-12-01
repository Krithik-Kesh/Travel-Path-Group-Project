package use_case.add_stop;

import GeolocationsAPIs.APICaller;
import entity.ItineraryStop;
import entity.RouteInfo;
import entity.StopFactory;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface; // OUR SHARED INTERFACE


public class AddStopInteractor implements AddStopInputBoundary {
    final RouteDataAccessInterface dataAccess;
    final AddStopOutputBoundary presenter;
    private final StopFactory stopFactory;

    public AddStopInteractor(RouteDataAccessInterface dataAccess,
                             AddStopOutputBoundary presenter, StopFactory stopFactory) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.stopFactory = stopFactory;
    }

    @Override
    public void execute(AddStopInputData inputData) {
        APICaller apiCaller = new APICaller();
        try {
            // GET COORDS FROM GOOGLE API
            String json = apiCaller.getJson(inputData.getCityInput());
            ItineraryStop newStop = apiCaller.parseJsonToStop(json, inputData.getCityInput());

            // ADD TO LIST
            dataAccess.addStop(newStop);

            // RECALCULATE ROUTE USING MPBOX
            RouteInfo newInfo = dataAccess.getRoute(dataAccess.getStops());

            // NEW ROUTE
            AddStopOutputData output = new AddStopOutputData(dataAccess.getStops(), newInfo);
            presenter.prepareSuccessView(output);

        } catch (Exception e) {
            presenter.prepareFailView("Could not add stop: " + e.getMessage());
        }
    }
}
