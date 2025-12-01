package interfaceadapter.set_start_date;

import usecase.SetStartDate.SetStartDateInputBoundary;
import usecase.SetStartDate.SetStartDateInputData;

import java.time.LocalDate;

public class SetStartDateController {

    private final SetStartDateInputBoundary interactor;
    private final String itineraryId;

    public SetStartDateController(SetStartDateInputBoundary interactor,
                                  String itineraryId) {
        this.interactor = interactor;
        this.itineraryId = itineraryId;
    }
    /**
     * Called by the UI when the user selects a start date.
     */
    public void setStartDate(LocalDate date) {
        SetStartDateInputData inputData =
                new SetStartDateInputData(date, itineraryId);
        interactor.execute(inputData);
    }
}
