package usecase.add_stop;

public interface AddStopOutputBoundary {
    void prepareSuccessView(AddStopOutputData outputData);
    void prepareFailView(String error);
}
