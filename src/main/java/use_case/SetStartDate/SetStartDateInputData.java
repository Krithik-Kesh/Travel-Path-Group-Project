/* Reminder: Still need to do the UI and UI -> Controller step
 */

package use_case.SetStartDate;

import java.time.LocalDate;

public class SetStartDateInputData {
    /* This file contains a class stores inputs 1. username 2. start date
    and then pass them to the Interactor.
    */

    private final String username;
    private final LocalDate startDate;
    private final String itineraryId;

    public SetStartDateInputData(String username, LocalDate startDate, String itineraryId) {
        /* Receive two inputs, store them.
         */
        this.username = username;
        this.startDate = startDate;
        this.itineraryId = itineraryId;
    }

    public String getUsername() {
        /* Extract username.
         */
        return username;
    }

    public LocalDate getStartDate() {
        /* Extract itinerary start date.
         */
        return startDate;
    }

    public String getItineraryId() {
        // Get itinerary ID
        return itineraryId;
    }
}


