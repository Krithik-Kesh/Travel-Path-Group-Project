package interfaceadapter.notes;

import usecase.add_note_to_stop.AddNoteToStopInputBoundary;
import usecase.add_note_to_stop.AddNoteToStopInputData;

/**
 * Controller.
 * It receives data from the UI and forwards it to the interactor.
 *
 * @since 1.0
 */

public class AddNoteToStopController {
    private final AddNoteToStopInputBoundary interactor;

    /**
     * Creates a controller for the AddNoteToStop use case.
     *
     * @param interactor the interactor that handles the business logic
     * @since 1.0
     */

    public AddNoteToStopController(AddNoteToStopInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Adds or updates a note for a specific stop in an itinerary.
     *
     * @param itineraryId the ID of the itinerary
     * @param stopId      the ID of the stop in the itinerary
     * @param noteText    the note text to be saved
     * @since 1.0
     */

    public void addOrUpdateNote(String itineraryId, String stopId, String noteText) {
        final AddNoteToStopInputData inputData =
                new AddNoteToStopInputData(itineraryId, stopId, noteText);
        interactor.execute(inputData);
    }
}
