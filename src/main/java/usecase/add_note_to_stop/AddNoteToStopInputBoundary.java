package usecase.add_note_to_stop;

/**
 * Input boundary for the AddNoteToStop use case.
 *
 * @since 1.0
 */

public interface AddNoteToStopInputBoundary {

    /**
     * Executes the AddNoteToStop use case.
     *
     * @param inputData the required data for adding a note to a stop
     * @since 1.0
     */

    void execute(AddNoteToStopInputData inputData);
}
