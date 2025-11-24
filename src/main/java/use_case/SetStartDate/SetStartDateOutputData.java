package use_case.SetStartDate;

import java.time.LocalDate;

public class SetStartDateOutputData {
    /* Interactor packs all necessary data, then pass it to the Presenter.
     */

    private String username;
    private LocalDate startDate;
    private String itineraryId;

    public SetStartDateOutputData(String username, LocalDate startDate,  String itineraryId) {
        this.username = username;
        this.startDate = startDate;
        this.itineraryId = itineraryId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getItineraryId() {
        return itineraryId;
    }
}