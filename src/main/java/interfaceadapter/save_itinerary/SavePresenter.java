package interface_adapter.save_itinerary;
import interface_adapter.IteneraryViewModel;
import use_case.save_itinerary.SaveOutput;
import use_case.save_itinerary.SaveOutputBoundary;

public class SavePresenter implements SaveOutputBoundary {

    private final IteneraryViewModel viewModel;

    public SavePresenter(IteneraryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SaveOutput output) {
        // Success Logic
        System.out.println("Presenter: Save Successful for " + output.getRecord().getOrigin());

        viewModel.setError("");
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        // Failure Logic
        viewModel.setError(error);
        viewModel.firePropertyChanged();
    }
}