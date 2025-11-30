package use_case.get_previous_data;

import entity.TravelRecord;

import java.util.List;

public class HistoryInteractor implements HistoryInputBoundary {

    private final HistoryRepo repo;
    private final HistoryOutputBoundary presenter;

    public HistoryInteractor(HistoryRepo repo, HistoryOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }
//
    @Override
    public void execute(HistoryInput input) {
        String user = input.getUser();

        List<TravelRecord> records = repo.findByUser(user);

        HistoryOutput output = new HistoryOutput(records);
        presenter.present(output);
    }
}
