package use_case.get_previous_data;

import entity.TravelRecord;

import java.util.List;

public class HistoryOutput {

    private final List<TravelRecord> records;

    public HistoryOutput(List<TravelRecord> records) {
        this.records = records;
    }
//
    public List<TravelRecord> getRecords() {
        return records;
    }

    public boolean hasRecords() {
        return !records.isEmpty();
    }
}
