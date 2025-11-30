package use_case.save_itinerary;

import entity.TravelRecord;


public class SaveInput {

    private final TravelRecord record;

    public SaveInput(TravelRecord record) {
        this.record = record;
    }

    public TravelRecord getRecord() {
        return record;
    }
}
