package usecase.add_note_to_stop;

/**
 * Output boundary for the AddNoteToStop use case.
 *
 * @since 1.0
 */

public interface AddNoteToStopOutputBoundary {

    /**
     * Presents the successful result of adding a note to a stop.
     *
     * @param outputData the data returned after successfully adding the note
     * @since 1.0
     */

    void presentSuccess(AddNoteToStopOutputData outputData);

    /**
     * Presents an error message when the use case fails.
     *
     * @param errorMessage the message describing the failure
     * @since 1.0
     */

    void presentFailure(String errorMessage);
}
