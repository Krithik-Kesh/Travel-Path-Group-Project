package interfaceadapter.notes;

import usecase.add_note_to_stop.AddNoteToStopOutputBoundary;
import usecase.add_note_to_stop.AddNoteToStopOutputData;

/**
 * Presenter.
 * It updates the view model with success or failure information.
 *
 * @since 1.0
 */

public class AddNoteToStopPresenter implements AddNoteToStopOutputBoundary {
    private final NotesViewModel viewModel;

    public AddNoteToStopPresenter(NotesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(AddNoteToStopOutputData outputData) {
        viewModel.setErrorMessage("");
        viewModel.setCurrentNoteText(outputData.getNoteText());
    }

    @Override
    public void presentFailure(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    public NotesViewModel getViewModel() {
        return viewModel;
    }
}
