package usecase.save_itinerary;

import entity.TravelRecord;

public class SaveOutput {

    private final TravelRecord record;

    public SaveOutput(TravelRecord record) {
        this.record = record;
    }

    public TravelRecord getRecord() {
        return record;
    }
}
