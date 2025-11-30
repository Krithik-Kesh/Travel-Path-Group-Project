package use_case.SetStartDate;

import java.time.LocalDate;

/**
 * Output data for the SetStartDate use case.
 * Immutable data passed from Interactor → Presenter.
 */
public class SetStartDateOutputData {

    private final String itineraryId;
    private final LocalDate startDate;

    public SetStartDateOutputData(String itineraryId, LocalDate startDate) {
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
