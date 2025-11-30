package use_case.save_itinerary;

import entity.TravelRecord;
import use_case.get_previous_data.HistoryRepo;


public class SaveInteractor implements SaveInputBoundary {

    private final HistoryRepo repo;
    private final SaveOutputBoundary presenter;

    public SaveInteractor(HistoryRepo repo, SaveOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveInput input) {
        TravelRecord record = input.getRecord();


        String user = record.getUsername();


        repo.save(user, record);


        SaveOutput output = new SaveOutput(record);
        presenter.present(output);
    }
}
