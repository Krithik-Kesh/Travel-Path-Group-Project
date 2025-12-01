package interfaceadapter.set_start_date;

import java.time.LocalDate;

/**
 * ViewModel for the SetStartDate use case.
 * Stores the UI state that should be displayed to the user.
 */
public class SetStartDateViewModel {

    private LocalDate startDate;
    private String message;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
