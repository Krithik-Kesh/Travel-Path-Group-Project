package usecase.SetStartDate;

import entity.Itinerary;
import usecase.ItineraryRepository;
import java.time.LocalDate;

/**
 * Interactor for the SetStartDate use case.
 */
public class SetStartDateInteractor implements SetStartDateInputBoundary {

    private final ItineraryRepository itineraryRepo;
    private final SetStartDateOutputBoundary presenter;

    public SetStartDateInteractor(ItineraryRepository itineraryRepo,
                                  SetStartDateOutputBoundary presenter) {
        this.itineraryRepo = itineraryRepo;
        this.presenter = presenter;
    }

    @Override
    public void execute(SetStartDateInputData inputData) {

        String itineraryId = inputData.getItineraryId();
        LocalDate startDate = inputData.getStartDate();

        // Step 1: Load itinerary
        Itinerary itinerary = itineraryRepo.findById(itineraryId);

        if (itinerary == null) {
            presenter.prepareFailView(
                    "Itinerary with ID '" + itineraryId + "' was not found."
            );
            return;
        }

        // Step 2: Update entity
        itinerary.setStartDate(startDate);

        // Step 3: Save updated itinerary
        itineraryRepo.save(itinerary);

        // Step 4: Prepare output data
        SetStartDateOutputData outputData =
                new SetStartDateOutputData(itineraryId, startDate);

        presenter.prepareSuccessView(outputData);
    }
}
