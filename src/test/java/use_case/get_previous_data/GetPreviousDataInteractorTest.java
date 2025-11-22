package use_case.get_previous_data;

import entities.TravelRecord;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for GetPreviousDataInteractor.
 */
public class GetPreviousDataInteractorTest extends TestCase {

    /**
     * Simple in-memory implementation of the data access interface.
     * Used only for testing.
     */
    private static class InMemoryTravelHistoryDAO implements TravelHistoryDataAccessInterface {

        private final Map<String, List<TravelRecord>> recordsByUser = new HashMap<>();

        void addRecord(String username, TravelRecord record) {
            recordsByUser
                    .computeIfAbsent(username, k -> new ArrayList<>())
                    .add(record);
        }

        @Override
        public List<TravelRecord> getTravelRecordsForUser(String username) {
            return recordsByUser.getOrDefault(username, new ArrayList<>());
        }
    }

    /**
     * Presenter that just remembers the last output data it received.
     * This lets the test assert on what the interactor sends to the presenter.
     */
    private static class RecordingPresenter implements GetPreviousDataOutputBoundary {

        private GetPreviousDataOutputData lastOutputData;

        @Override
        public void present(GetPreviousDataOutputData outputData) {
            this.lastOutputData = outputData;
        }

        GetPreviousDataOutputData getLastOutputData() {
            return lastOutputData;
        }
    }

    public void testExecuteLoadsRecordsForUser() {
        // Arrange: set up fake DAO with some records.
        InMemoryTravelHistoryDAO dao = new InMemoryTravelHistoryDAO();

        TravelRecord record1 = new TravelRecord(
                "ethan",
                "Toronto",
                "Montreal",
                "5h",
                "Sunny",
                "Train",
                "T-shirt");
        TravelRecord record2 = new TravelRecord(
                "ethan",
                "Toronto",
                "New York",
                "8h",
                "Cloudy",
                "Bus",
                "Jacket");

        // Add two records for ethan.
        dao.addRecord("ethan", record1);
        dao.addRecord("ethan", record2);

        // Add a record for a different user to make sure it is ignored.
        TravelRecord otherRecord = new TravelRecord(
                "otherUser",
                "Paris",
                "London",
                "3h",
                "Rainy",
                "Train",
                "Coat");
        dao.addRecord("otherUser", otherRecord);

        // Set up presenter and interactor.
        RecordingPresenter presenter = new RecordingPresenter();
        GetPreviousDataInteractor interactor =
                new GetPreviousDataInteractor(dao, presenter);

        // Input says we are loading data for "ethan".
        GetPreviousDataInputData inputData =
                new GetPreviousDataInputData("ethan");

        // Act: run the use case.
        interactor.execute(inputData);

        // Assert: presenter received the correct output data.
        GetPreviousDataOutputData output = presenter.getLastOutputData();
        assertNotNull(output);
        assertTrue(output.hasRecords());
        assertEquals(2, output.getTravelRecords().size());

        // All returned records should belong to "ethan".
        for (TravelRecord r : output.getTravelRecords()) {
            assertEquals("ethan", r.getUsername());
        }
    }

    public void testExecuteWhenUserHasNoRecords() {
        // Arrange: DAO has no records for this user.
        InMemoryTravelHistoryDAO dao = new InMemoryTravelHistoryDAO();
        RecordingPresenter presenter = new RecordingPresenter();
        GetPreviousDataInteractor interactor =
                new GetPreviousDataInteractor(dao, presenter);

        // Input for a user that has no records.
        GetPreviousDataInputData inputData =
                new GetPreviousDataInputData("noHistoryUser");

        // Act
        interactor.execute(inputData);

        // Assert
        GetPreviousDataOutputData output = presenter.getLastOutputData();
        assertNotNull(output);
        assertFalse(output.hasRecords());
        assertEquals(0, output.getTravelRecords().size());
    }


}
