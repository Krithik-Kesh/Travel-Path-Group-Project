package use_case.get_previous_data;

import entities.TravelRecord;
import java.util.List;

/**
 * Output data for the "get previous data" use case.
 */
public class GetPreviousDataOutputData {

    private final List<TravelRecord> travelRecords;

    public GetPreviousDataOutputData(List<TravelRecord> travelRecords) {
        this.travelRecords = travelRecords;
    }

    public List<TravelRecord> getTravelRecords() {
        return travelRecords;
    }

    /**
     * Convenience method so the presenter can quickly check
     * whether there is any data.
     */
    public boolean hasRecords() {
        return !travelRecords.isEmpty();
    }
}
