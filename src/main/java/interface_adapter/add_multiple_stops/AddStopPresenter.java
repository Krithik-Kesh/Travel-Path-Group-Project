package interface_adapter.add_multiple_stops;

import interface_adapter.IteneraryViewModel; //MATTIAS VIEW MODEL
import use_case.add_stop.AddStopOutputBoundary;
import use_case.add_stop.AddStopOutputData;

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