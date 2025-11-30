package use_case.add_stop;

public interface AddStopOutputBoundary {
    void prepareSuccessView(AddStopOutputData outputData);
    void prepareFailView(String error);
}
