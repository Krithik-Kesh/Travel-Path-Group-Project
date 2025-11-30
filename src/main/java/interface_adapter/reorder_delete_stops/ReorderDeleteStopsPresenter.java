package interface_adapter.reorder_delete_stops;
import use_case.reorder_delete_stops.OutputData;
import use_case.reorder_delete_stops.OutputBoundary;

public class ReorderDeleteStopsPresenter implements OutputBoundary {
    private final RDStopsViewModel viewModel;

    public ReorderDeleteStopsPresenter(RDStopsViewModel viewModel) {
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
