package use_case.get_previous_data;

import entities.TravelRecord;
import java.util.List;

/**
 * Data access interface for loading a user's past travel records.
 */
public interface TravelHistoryDataAccessInterface {

    /**
     * Return all past travel records for the given user.
     *
     * @param username the user whose records we want
     * @return a list of that user's past travel records
     */
    List<TravelRecord> getTravelRecordsForUser(String username);
}
