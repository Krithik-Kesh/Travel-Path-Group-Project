package usecase.SetStartDate;

import entity.Itinerary;
import entity.TravelRecord;
import entity.ItineraryStop;
import usecase.ItineraryRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SetStartDateInteractorTest {

    // Fake Repository
    private static class TestItineraryRepository implements ItineraryRepository {

        Itinerary stored;   // returned by findById
        Itinerary saved;    // captured from save()

        @Override
        public Itinerary findById(String id) {
            return stored;
        }

        @Override
        public void save(Itinerary itinerary) {
            this.saved = itinerary;
        }
    }

    // Fake Presenter
    private static class TestPresenter implements SetStartDateOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;

        SetStartDateOutputData lastSuccessOutput;
        String lastError;

        @Override
        public void prepareSuccessView(SetStartDateOutputData outputData) {
            successCalled = true;
            lastSuccessOutput = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalled = true;
            lastError = errorMessage;
        }
    }

    // TEST 1 — Itinerary Not Found
    @Test
    void testFail_itineraryNotFound() {

        TestItineraryRepository repo = new TestItineraryRepository();
        TestPresenter presenter = new TestPresenter();

        SetStartDateInteractor interactor =
                new SetStartDateInteractor(repo, presenter);

        // Constructor: new SetStartDateInputData(LocalDate startDate, String itineraryId)
        SetStartDateInputData input =
                new SetStartDateInputData(LocalDate.of(2024, 12, 1), "IT100");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("There is no itineraryIT100", presenter.lastError);

        assertFalse(presenter.successCalled);
        assertNull(repo.saved);
    }


    // TEST 2 — Success Case
    @Test
    void testSuccess_updatesStartDate_andSaves_andCallsPresenter() {

        TestItineraryRepository repo = new TestItineraryRepository();
        TestPresenter presenter = new TestPresenter();

        // Create dummy TravelRecord for the Itinerary constructor
        TravelRecord dummyRecord = new TravelRecord(
                "username",
                "origin",
                "destination",
                "duration",
                "weather",
                "distance",
                "clothing"
        );

        // Create an existing itinerary with valid constructor
        Itinerary itinerary = new Itinerary(
                "IT100",
                dummyRecord,
                new ArrayList<ItineraryStop>()  // empty stops is OK
        );

        repo.stored = itinerary;

        SetStartDateInteractor interactor =
                new SetStartDateInteractor(repo, presenter);

        LocalDate newDate = LocalDate.of(2024, 12, 1);

        SetStartDateInputData input =
                new SetStartDateInputData(newDate, "IT100");

        interactor.execute(input);

        // --- Verify entity mutation ---
        assertEquals(newDate, itinerary.getStartDate());

        // --- Verify repository save ---
        assertNotNull(repo.saved);
        assertEquals("IT100", repo.saved.getId());
        assertEquals(newDate, repo.saved.getStartDate());

        // --- Verify presenter success ---
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        assertNotNull(presenter.lastSuccessOutput);
        assertEquals("IT100", presenter.lastSuccessOutput.getItineraryId());
        assertEquals(newDate, presenter.lastSuccessOutput.getStartDate());
    }
}
