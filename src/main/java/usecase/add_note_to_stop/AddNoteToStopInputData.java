package usecase.add_note_to_stop;

/**
 * Input data for the Add Note to Stop use case.
 * Contains the itinerary ID, stop ID, and the note text provided by the user.
 *
 * @since 1.0
 */

public class AddNoteToStopInputData {
    private final String itineraryId;
    private final String stopId;
    private final String noteText;

    public AddNoteToStopInputData(String itineraryId, String stopId, String noteText) {
        this.itineraryId = itineraryId;
        this.stopId = stopId;
        this.noteText = noteText;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public String getStopId() {
        return stopId;
    }

    public String getNoteText() {
        return noteText;
    }
}
