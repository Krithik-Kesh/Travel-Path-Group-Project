package usecase.add_note_to_stop;

/**
 * Output data for the Add Note to Stop use case.
 * Stores the itinerary ID, stop ID, and the updated note text after the operation.
 *
 * @since 1.0
 */

public class AddNoteToStopOutputData {
    private final String itineraryId;
    private final String stopId;
    private final String noteText;

    public AddNoteToStopOutputData(String itineraryId, String stopId, String noteText) {
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
