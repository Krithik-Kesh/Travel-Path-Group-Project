package interfaceadapter.add_multiple_stops;

import interfaceadapter.IteneraryViewModel; //MATTIAS VIEW MODEL
import usecase.add_stop.AddStopOutputBoundary;
import usecase.add_stop.AddStopOutputData;

public class AddStopPresenter implements AddStopOutputBoundary {
    private final IteneraryViewModel viewModel;

    public AddStopPresenter(IteneraryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddStopOutputData response) {
        // UPDATE STATE
        viewModel.setStops(response.getStops());
        viewModel.setRouteInfo(response.getRouteInfo());
        viewModel.setError("");

        // UPDATE UI
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setError(error);
        viewModel.firePropertyChanged();
    }
}