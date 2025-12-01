package interfaceadapter.set_start_date;

import usecase.SetStartDate.SetStartDateInputBoundary;
import usecase.SetStartDate.SetStartDateInputData;

import java.time.LocalDate;

public class SetStartDateController {

    private final SetStartDateInputBoundary interactor;

    public SetStartDateController(SetStartDateInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called by the UI when the user selects a start date.
     *
     * @param date the start date selected by the user
     */
    public void setStartDate(LocalDate date) {
        SetStartDateInputData inputData = new SetStartDateInputData(date);
        interactor.execute(inputData);
    }
}
