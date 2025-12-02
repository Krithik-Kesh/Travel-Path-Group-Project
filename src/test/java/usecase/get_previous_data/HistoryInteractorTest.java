package usecase.get_previous_data;

import entity.Itinerary;
import entity.TravelRecord;
import entity.ItineraryStop;
import interfaceadapter.reorder_delete_stops.RouteDataAccessInterface;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryInteractorTest {

    // Mock data access: Success
    private static class TestRouteDataAccessSuccess implements RouteDataAccessInterface {

        private final List<Itinerary> itinerariesToReturn = new ArrayList<>();
        private LocalDate startDate;

        @Override
        public List<Itinerary> loadItineraries() {
            return new ArrayList<>(itinerariesToReturn);
        }

        // Unused but required methods
        @Override public void addStop(ItineraryStop stop) {}
        @Override public List<ItineraryStop> getStops() { return null; }
        @Override public entity.RouteInfo getRoute(List<ItineraryStop> stops) { return null; }
        @Override public void saveItinerary(Itinerary itinerary) {}
        @Override public void setStartDate(LocalDate date) { this.startDate = date; }
        @Override public LocalDate getStartDate() { return startDate; }
    }

    // Mock data access: Failure
    private static class TestRouteDataAccessFailure implements RouteDataAccessInterface {

        private LocalDate startDate;

        @Override
        public List<Itinerary> loadItineraries() {
            throw new RuntimeException("Simulated failure");
        }

        // Unused but required methods
        @Override public void addStop(ItineraryStop stop) {}
        @Override public List<ItineraryStop> getStops() { return null; }
        @Override public entity.RouteInfo getRoute(List<ItineraryStop> stops) { return null; }
        @Override public void saveItinerary(Itinerary itinerary) {}
        @Override public void setStartDate(LocalDate date) { this.startDate = date; }
        @Override public LocalDate getStartDate() { return startDate; }
    }

    private static class TestPresenter implements HistoryOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;

        HistoryOutput lastOutput;
        String lastError;

        @Override
        public void presentHistory(HistoryOutput output) {
            this.successCalled = true;
            this.lastOutput = output;
        }

        @Override
        public void prepareFailView(String message) {
            this.failCalled = true;
            this.lastError = message;
        }
    }

    // Test - Success
    @Test
    void execute_success_returnsUserHistory() {
        TestRouteDataAccessSuccess dataAccess = new TestRouteDataAccessSuccess();
        TestPresenter presenter = new TestPresenter();

        // Itinerary for another user
        TravelRecord bobRecord = new TravelRecord(
                "bob", "Toronto", "Trip B",
                "60 min", "Rain", "Path1", "Jacket"
        );
        Itinerary i1 = new Itinerary("id1", bobRecord, null);
        dataAccess.itinerariesToReturn.add(i1);

        // Itinerary for target user
        TravelRecord aliceRecord = new TravelRecord(
                "alice", "Toronto", "Trip A",
                "30 min", "Sunny", "Path2", "T-shirt"
        );
        Itinerary i2 = new Itinerary("id2", aliceRecord, null);
        dataAccess.itinerariesToReturn.add(i2);

        HistoryInteractor interactor = new HistoryInteractor(dataAccess, presenter);
        HistoryInput input = new HistoryInput("alice");

        interactor.execute(input);

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        assertNotNull(presenter.lastOutput);
        assertEquals("alice", presenter.lastOutput.getUsername());
        assertEquals(1, presenter.lastOutput.getRecords().size());
        // TravelRecord has getDestination(), not getTripName()
        assertEquals("Trip A",
                presenter.lastOutput.getRecords().get(0).getDestination());
    }

    // Test: Failure, no itinerary found
    @Test
    void execute_noHistory_callsFailView() {
        TestRouteDataAccessSuccess dataAccess = new TestRouteDataAccessSuccess();
        TestPresenter presenter = new TestPresenter();

        TravelRecord cRecord = new TravelRecord(
                "charlie", "Toronto", "Trip C",
                "45 min", "Windy", "Path3", "Hoodie"
        );
        Itinerary i1 = new Itinerary("id1", cRecord, null);
        dataAccess.itinerariesToReturn.add(i1);

        HistoryInteractor interactor = new HistoryInteractor(dataAccess, presenter);
        HistoryInput input = new HistoryInput("alice");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);

        assertEquals("No history found for user: alice", presenter.lastError);
    }

    // Test: DAO Exception
    void execute_failure_callsFailView() {
        TestRouteDataAccessFailure dataAccess = new TestRouteDataAccessFailure();
        TestPresenter presenter = new TestPresenter();

        HistoryInteractor interactor = new HistoryInteractor(dataAccess, presenter);
        HistoryInput input = new HistoryInput("alice");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);

        assertTrue(presenter.lastError.contains("Failed to load history"));
    }
}
