package usecase.save_itinerary;

import entity.TravelRecord;
import junit.framework.TestCase;
import usecase.get_previous_data.HistoryRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SaveInteractorTest extends TestCase {


    private static class TestRepo implements HistoryRepo {

        private final Map<String, List<TravelRecord>> data = new HashMap<>();

        @Override
        public List<TravelRecord> findByUser(String user) {
            return data.getOrDefault(user, new ArrayList<>());
        }

        @Override
        public void save(String user, TravelRecord record) {
            data.computeIfAbsent(user, u -> new ArrayList<>()).add(record);
        }

        int count(String user) {
            return findByUser(user).size();
        }

        TravelRecord first(String user) {
            List<TravelRecord> list = findByUser(user);
            return list.isEmpty() ? null : list.get(0);
        }
    }


    private static class TestPresenter implements SaveOutputBoundary {

        private SaveOutput last;

        @Override
        public void present(SaveOutput output) {
            this.last = output;
        }

        SaveOutput getLast() {
            return last;
        }
    }

    public void testSaveAddsRecordToRepo() {
        TestRepo repo = new TestRepo();
        TestPresenter presenter = new TestPresenter();
        SaveInteractor interactor = new SaveInteractor(repo, presenter);

        TravelRecord record = new TravelRecord(
                "ethan",
                "Toronto",
                "Montreal",
                "5h",
                "Sunny",
                "By car",
                "T-shirt"
        );

        SaveInput input = new SaveInput(record);

        interactor.execute(input);

        assertEquals(1, repo.count("ethan"));
        TravelRecord stored = repo.first("ethan");
        assertNotNull(stored);
        assertEquals("ethan", stored.getUsername());

        SaveOutput out = presenter.getLast();
        assertNotNull(out);
        assertEquals(record, out.getRecord());
    }
}
