package usecase.get_previous_data;

import entity.TravelRecord;
import java.util.List;

public class HistoryOutput {
    private final List<TravelRecord> records;
    private final String username;

    public HistoryOutput(String username, List<TravelRecord> records) {
        this.username = username;
        this.records = records;
    }

    public List<TravelRecord> getRecords() {
        return records;
    }

    public String getUsername() {
        return username;
    }
}
