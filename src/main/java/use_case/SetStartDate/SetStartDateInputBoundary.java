package use_case.SetStartDate;

public interface SetStartDateInputBoundary {
    /* Called my Controller, implemented by the Interactor
     */

    void execute(SetStartDateInputData inputData);
}