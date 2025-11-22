package use_case.get_previous_data;

import entities.TravelRecord;
import java.util.List;

/**
 * Interactor for the "get previous data" use case.
 */
public class GetPreviousDataInteractor implements GetPreviousDataInputBoundary {

    private final TravelHistoryDataAccessInterface travelHistoryDataAccess;
    private final GetPreviousDataOutputBoundary presenter;

    public GetPreviousDataInteractor(TravelHistoryDataAccessInterface travelHistoryDataAccess,
                                     GetPreviousDataOutputBoundary presenter) {
        this.travelHistoryDataAccess = travelHistoryDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetPreviousDataInputData inputData) {
        // 1. Load all past travel records for this user.
        String username = inputData.getUsername();
        List<TravelRecord> records = travelHistoryDataAccess.getTravelRecordsForUser(username);

        // 2. Wrap them in an output data object.
        GetPreviousDataOutputData outputData = new GetPreviousDataOutputData(records);

        // 3. Ask the presenter to update the view.
        presenter.present(outputData);
    }
}
