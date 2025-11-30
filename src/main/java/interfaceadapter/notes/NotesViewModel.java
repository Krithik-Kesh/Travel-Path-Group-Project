package interfaceadapter.notes;

/**
 * View model for displaying and updating note information.
 * Stores the current note text and any error messages.
 *
 * @since 1.0
 */

public class NotesViewModel {
    private String currentNoteText = "";
    private String errorMessage = "";

    public String getCurrentNoteText() {
        return currentNoteText;
    }

    public void setCurrentNoteText(String currentNoteText) {
        this.currentNoteText = currentNoteText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Checks whether an error message exists.
     *
     * @return true if an error message is present, false otherwise
     * @since 1.0
     */

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
