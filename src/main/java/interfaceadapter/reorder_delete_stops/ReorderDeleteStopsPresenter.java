package interfaceadapter.reorder_delete_stops;
import interfaceadapter.IteneraryViewModel;
import usecase.reorder_delete_stops.OutputData;
import usecase.reorder_delete_stops.OutputBoundary;

public class ReorderDeleteStopsPresenter implements OutputBoundary {
    private final IteneraryViewModel viewModel;

    public ReorderDeleteStopsPresenter(IteneraryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(OutputData outputData) {
        viewModel.setRouteInfo(outputData.getRouteInfo());
        viewModel.setStops(outputData.getOrderedStops());
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
        viewModel.firePropertyChanged();
    }
}
