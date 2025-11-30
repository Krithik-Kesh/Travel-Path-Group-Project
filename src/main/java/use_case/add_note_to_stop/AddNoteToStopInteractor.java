package use_case.add_note_to_stop;

import entity.Itinerary;
import entity.ItineraryStop;
import use_case.ItineraryRepository;
// Interactor.
public class AddNoteToStopInteractor implements AddNoteToStopInputBoundary{
    private final ItineraryRepository itineraryRepository;
    private final AddNoteToStopOutputBoundary presenter;

    public AddNoteToStopInteractor(ItineraryRepository itineraryRepository,
                                   AddNoteToStopOutputBoundary presenter) {
        this.itineraryRepository = itineraryRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddNoteToStopInputData inputData) {
        Itinerary itinerary = itineraryRepository.findById(inputData.getItineraryId());
        if (itinerary == null) {
            presenter.presentFailure("Itinerary not found: " + inputData.getItineraryId());
            return;
        }

        ItineraryStop stop = itinerary.findStopById(inputData.getStopId());
        if (stop == null) {
            presenter.presentFailure("Stop not found: " + inputData.getStopId());
            return;
        }

        stop.setNotes(inputData.getNoteText());

        itineraryRepository.save(itinerary);

        AddNoteToStopOutputData outputData =
                new AddNoteToStopOutputData(
                        itinerary.getId(),
                        stop.getId(),
                        stop.getNotes()
                );
        presenter.presentSuccess(outputData);
    }
}
