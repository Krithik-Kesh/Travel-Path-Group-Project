/* Just some notes:

Work extends CSC207-Group-Project.model.Itinerary (so must bring it to the new repo)
 */

package use_case.SetStartDate;

import java.time.LocalDate;
import entity.Itinerary; // From the old repo, must tranfer to the new repo before PR
import data_access.ItineraryDataAccessInterface; /*  From the old repo, the DAO interface,
                                                     Interactor needs the itinerary data
                                                  */

public class SetStartDateInteractor implements SetStartDateInputBoundary {
    /* The Interactor: Controller needs this to call the use case. Controller call Boundary,
    Interactor implements Boundary.

    The Interactor contains two helper methods: 1. DAO; 2. Presenter. The functionality
    is described in their documentation
     */
    private final ItineraryDataAccessInterface itineraryDAO; // This DAO read and write Itinerary data, UC5 work
    private final SetStartDateOutputBoundary presenter; /* Presenter presents output of success and failure
                                                           of Start Date confirmation
                                                         */

    public SetStartDateInteractor(ItineraryDataAccessInterface itineraryDAO,
                                  SetStartDateOutputBoundary presenter){
        /* This method injects dependency into the Interactor
         */
        this.itineraryDAO = itineraryDAO;
        this.presenter = presenter;
    }

    @Override
    public void setStartDate(SetStartDateInputData inputData) {
        /* Executes the use case in the following steps:

        1) Get username, start date, and itinerary id
        2) Update itinerary start date in DAO
        3) Save updated DAO
        4) Send outputdata to Presenter
         */

        // Use helper in inputdata file
        String username = inputData.getUsername();
        LocalDate startDate = inputData.getStartDate();
        String itineraryId = inputData.getItineraryId();

        // Get itinerary data (Should have been done in User Story 5, now using a dummy helper)
        Itinerary itinerary = itineraryDAO.getItinerary(username, itineraryId); /* IMPORTANT: User Story 5 did not implement
                                                                    DAO, this is a dummy DAO and dummy helper
                                                                  */
        // Itinerary not found
        if (itinerary == null) {
            presenter.prepareFailView("Username" + username
                    + "with itinerary ID" + itineraryId + "is not found!");
            return ;
        }

        // Itinerary found: Set itinerary
        itinerary.setStartDate(startDate); // Must be done in UC5
        SetStartDateOutputData outputData = new SetStartDateOutputData(username, startDate, itineraryId);
        presenter.prepareSuccessView(outputData);

    }
}