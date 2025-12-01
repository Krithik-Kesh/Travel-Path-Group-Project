package usecase.SetStartDate;

public interface SetStartDateInputBoundary {
    /* Called my Controller, implemented by the Interactor
     */

    void execute(SetStartDateInputData inputData);
}