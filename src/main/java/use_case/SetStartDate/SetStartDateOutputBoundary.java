package use_case.SetStartDate;

/**
 * Output Boundary for the SetStartDate use case.
 * Implemented by the Presenter.
 *
 * Provides two methods:
 *  - prepareSuccessView: called when the start date is successfully updated.
 *  - prepareFailView: called when the use case encounters an error.
 */
public interface SetStartDateOutputBoundary {

    void prepareSuccessView(SetStartDateOutputData outputData);

    void prepareFailView(String errorMessage);
}
