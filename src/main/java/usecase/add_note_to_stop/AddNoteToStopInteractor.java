package usecase.add_note_to_stop;

import entity.Itinerary;
import entity.ItineraryStop;
import usecase.ItineraryRepository;

/**
 * Interactor for adding a note to a stop in an itinerary.
 *
 * @since 1.0
 */

public class AddNoteToStopInteractor implements AddNoteToStopInputBoundary {
    private final ItineraryRepository itineraryRepository;
    private final AddNoteToStopOutputBoundary presenter;

    public AddNoteToStopInteractor(ItineraryRepository itineraryRepository,
                                   AddNoteToStopOutputBoundary presenter) {
        this.itineraryRepository = itineraryRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddNoteToStopInputData inputData) {
        final Itinerary itinerary = itineraryRepository.findById(inputData.getItineraryId());
        ItineraryStop stop = null;
        if (itinerary != null) {
            stop = itinerary.findStopById(inputData.getStopId());
        }

        if (itinerary == null) {
            presenter.presentFailure("Itinerary not found: " + inputData.getItineraryId());
        }
        else if (stop == null) {
            presenter.presentFailure("Stop not found: " + inputData.getStopId());
        }
        else {
            stop.setNotes(inputData.getNoteText());
            itineraryRepository.save(itinerary);

            final AddNoteToStopOutputData outputData = new AddNoteToStopOutputData(
                    itinerary.getId(),
                    stop.getId(),
                    stop.getNotes()
            );
            presenter.presentSuccess(outputData);
        }
    }
}
