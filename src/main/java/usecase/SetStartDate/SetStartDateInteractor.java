package usecase.SetStartDate;

import entity.Itinerary;
import usecase.ItineraryRepository;
import java.time.LocalDate;


public class SetStartDateInteractor implements SetStartDateInputBoundary {
    // Interactor

    private final ItineraryRepository itineraryRepo; //
    private final SetStartDateOutputBoundary presenter;

    public SetStartDateInteractor(ItineraryRepository itineraryRepo,
                                  SetStartDateOutputBoundary presenter) {
        this.itineraryRepo = itineraryRepo;
        this.presenter = presenter; // Defines dependency on the Presenter
    }

    @Override
    public void execute(SetStartDateInputData inputData) {

        String itineraryId = inputData.getItineraryId();
        LocalDate startDate = inputData.getStartDate();

        Itinerary itinerary = itineraryRepo.findById(itineraryId); // Load itinerary

        if (itinerary == null) { // Itinerary empty, false case, show error message
            presenter.prepareFailView(
                    "There is no itinerary" + itineraryId
            );
            return;
        }

        itinerary.setStartDate(startDate); // Update entity

        itineraryRepo.save(itinerary); // Save updated itinerary

        SetStartDateOutputData outputData = // Prepare output data
                new SetStartDateOutputData(itineraryId, startDate);

        presenter.prepareSuccessView(outputData);
    }
}
