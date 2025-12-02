package usecase.add_note_to_stop;

import entity.Itinerary;
import entity.ItineraryStop;
import usecase.ItineraryRepository;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AddNoteToStopInteractorTest {

    // Mock Itinerary: Success
    private static class TestRepoSuccess implements ItineraryRepository {

        Map<String, Itinerary> stored = new HashMap<>();

        @Override
        public Itinerary findById(String id) {
            return stored.get(id);
        }

        @Override
        public void save(Itinerary itinerary) {
            stored.put(itinerary.getId(), itinerary);
        }
    }

    // Mock Itinerary: Failure
    private static class TestRepoFailure implements ItineraryRepository {

        @Override
        public Itinerary findById(String id) {
            throw new RuntimeException("Simulated failure");
        }

        @Override
        public void save(Itinerary itinerary) {}
    }

    // Mock Presenter
    private static class TestPresenter implements AddNoteToStopOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;

        AddNoteToStopOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(AddNoteToStopOutputData outputData) {
            this.successCalled = true;
            this.lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.failCalled = true;
            this.lastError = errorMessage;
        }
    }

    // Test: Itinerary not found
    @Test
    void execute_itineraryNotFound_callsFail() {
        TestRepoSuccess repo = new TestRepoSuccess();
        TestPresenter presenter = new TestPresenter();

        AddNoteToStopInteractor interactor =
                new AddNoteToStopInteractor(repo, presenter);

        AddNoteToStopInputData input =
                new AddNoteToStopInputData("missing-itinerary", "stop1", "hello");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);
        assertEquals("Itinerary not found: missing-itinerary", presenter.lastError);
    }

    // Test: Itinerary not found
    @Test
    void execute_stopNotFound_callsFail() {
        TestRepoSuccess repo = new TestRepoSuccess();
        TestPresenter presenter = new TestPresenter();

        // Build itinerary without the requested stop
        ItineraryStop s1 = new ItineraryStop("A", "Toronto", 1, 1, "");
        List<ItineraryStop> stops = new ArrayList<>();
        stops.add(s1);

        Itinerary itinerary = new Itinerary("i1", null, stops);
        repo.save(itinerary);

        AddNoteToStopInteractor interactor =
                new AddNoteToStopInteractor(repo, presenter);

        AddNoteToStopInputData input =
                new AddNoteToStopInputData("i1", "missing-stop", "note");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);
        assertEquals("Stop not found: missing-stop", presenter.lastError);
    }

    // Test: Success
    @Test
    void execute_success_savesNoteAndCallsSuccess() {
        TestRepoSuccess repo = new TestRepoSuccess();
        TestPresenter presenter = new TestPresenter();

        // Build itinerary with a stop
        ItineraryStop stop = new ItineraryStop("s1", "Toronto", 1, 1, "");
        List<ItineraryStop> stops = new ArrayList<>();
        stops.add(stop);

        Itinerary itinerary = new Itinerary("trip123", null, stops);
        repo.save(itinerary);

        AddNoteToStopInteractor interactor =
                new AddNoteToStopInteractor(repo, presenter);

        AddNoteToStopInputData input =
                new AddNoteToStopInputData("trip123", "s1", "My new note");

        interactor.execute(input);

        // Validate presenter success
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        // Validate output data
        assertNotNull(presenter.lastSuccess);
        assertEquals("trip123", presenter.lastSuccess.getItineraryId());
        assertEquals("s1", presenter.lastSuccess.getStopId());
        assertEquals("My new note", presenter.lastSuccess.getNoteText());

        // Validate note was actually written into the stop
        Itinerary saved = repo.findById("trip123");
        ItineraryStop savedStop = saved.findStopById("s1");

        assertEquals("My new note", savedStop.getNotes());
    }
}