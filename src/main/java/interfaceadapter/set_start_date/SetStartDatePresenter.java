package interfaceadapter.set_start_date;

import usecase.SetStartDate.SetStartDateOutputBoundary;
import usecase.SetStartDate.SetStartDateOutputData;

/**
 * Presenter for the SetStartDate use case.
 * Converts output data into ViewModel updates.
 */
public class SetStartDatePresenter implements SetStartDateOutputBoundary {

    private final SetStartDateViewModel viewModel;

    public SetStartDatePresenter(SetStartDateViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void presentSuccess(SetStartDateOutputData outputData) {
        viewModel.setStartDate(outputData.getStartDate());
        viewModel.setMessage("Start date saved successfully.");
    }

    public void presentFailure(String errorMessage) {
        viewModel.setMessage(errorMessage);
    }

    @Override
    public void prepareSuccessView(SetStartDateOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage) {

    }
}
