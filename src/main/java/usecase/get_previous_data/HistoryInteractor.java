package usecase.get_previous_data;

import entity.Itinerary;
import entity.TravelRecord;
import interface_adapter.reorder_delete_stops.RouteDataAccessInterface;
import java.util.ArrayList;
import java.util.List;

public class HistoryInteractor implements HistoryInputBoundary {

    private final RouteDataAccessInterface dataAccess;
    private final HistoryOutputBoundary presenter;

    public HistoryInteractor(RouteDataAccessInterface dataAccess,
                             HistoryOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(HistoryInput input) {
        try {
            // LOAD THE HISTORY FROM THE JSON FILE
            List<Itinerary> allItineraries = dataAccess.loadItineraries();

            // FIND THE CURRENT USER'S STUFF
            List<TravelRecord> userRecords = new ArrayList<>();
            String targetUser = input.getUsername();

            for (Itinerary itinerary : allItineraries) {
                TravelRecord record = itinerary.getRecord();
                //IF IT EXISTS AND MATCHES THE USERS NAME
                if (record != null && record.getUsername().equals(targetUser)) {
                    userRecords.add(record);
                }
            }

            // HANDLE THE RESULTS
            if (userRecords.isEmpty()) {
                presenter.prepareFailView("No history found for user: " + targetUser);
            } else {
                HistoryOutput output = new HistoryOutput(targetUser, userRecords);
                presenter.presentHistory(output);
            }

        } catch (Exception e) {
            presenter.prepareFailView("Failed to load history: " + e.getMessage());
        }
    }
}