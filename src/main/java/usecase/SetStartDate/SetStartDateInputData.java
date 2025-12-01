package usecase.SetStartDate;

import java.time.LocalDate;

/**
 * Input Data for the SetStartDate use case.
 * Immutable data passed from Controller → Interactor.
 */
public class SetStartDateInputData {
    private final String itineraryId;
    private final LocalDate startDate;

    public SetStartDateInputData(LocalDate startDate, String itineraryId) {
        this.itineraryId = itineraryId;
        this.startDate = startDate;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
