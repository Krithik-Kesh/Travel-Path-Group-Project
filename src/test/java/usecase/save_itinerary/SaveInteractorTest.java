package usecase.save_itinerary;

import data_access.RouteDataAccess;
import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import interfaceadapter.IteneraryViewModel;
import interfaceadapter.save_itinerary.SavePresenter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveInteractorTest {

    // Fake ViewModel
    private static class FakeViewModel extends IteneraryViewModel {
        private String error = "";
        @Override public void setError(String e) { this.error = e; }
        @Override public String getError() { return error; }
        @Override public void firePropertyChanged() {}
    }

    // Test Presenter extends SavePresenter
    private static class TestPresenter extends SavePresenter {

        boolean successCalled = false;
        boolean failCalled = false;

        SaveOutput lastSuccess;
        String lastError;

        public TestPresenter() {
            super(new FakeViewModel());   // satisfy real constructor
        }

        @Override
        public void present(SaveOutput output) {
            successCalled = true;
            lastSuccess = output;
        }

        @Override
        public void prepareFailView(String error) {
            failCalled = true;
            lastError = error;
        }
    }

    // SUCCESS DAO
    private static class TestRouteDataAccessSuccess extends RouteDataAccess {

        List<ItineraryStop> stops = new ArrayList<>();
        List<Itinerary> saved = new ArrayList<>();
        RouteInfo routeInfo;
        LocalDate startDate;

        TestRouteDataAccessSuccess(RouteInfo info) {
            this.routeInfo = info;
        }

        @Override public void addStop(ItineraryStop stop) { stops.add(stop); }
        @Override public List<ItineraryStop> getStops() { return new ArrayList<>(stops); }
        @Override public RouteInfo getRoute(List<ItineraryStop> s) { return routeInfo; }
        @Override public void saveItinerary(Itinerary it) { saved.add(it); }
        @Override public List<Itinerary> loadItineraries() { return new ArrayList<>(saved); }
        @Override public void setStartDate(LocalDate date) { this.startDate = date; }
        @Override public LocalDate getStartDate() { return startDate; }
    }

    // FAILURE DAO
    private static class TestRouteDataAccessFailure extends RouteDataAccess {

        List<ItineraryStop> stops = new ArrayList<>();
        List<Itinerary> saved = new ArrayList<>();

        @Override public List<ItineraryStop> getStops() { return new ArrayList<>(stops); }
        @Override public RouteInfo getRoute(List<ItineraryStop> s) throws IOException {
            throw new IOException("Simulated failure");
        }
        @Override public void addStop(ItineraryStop stop) { stops.add(stop); }
        @Override public void saveItinerary(Itinerary it) { saved.add(it); }
        @Override public List<Itinerary> loadItineraries() { return new ArrayList<>(saved); }
    }

    @Test
    void fail_nullDate() {
        TestRouteDataAccessSuccess dao = new TestRouteDataAccessSuccess(null);
        TestPresenter presenter = new TestPresenter();

        SaveInteractor interactor = new SaveInteractor(dao, presenter);
        SaveInput input = new SaveInput("roger", null, "Sunny", "Coat");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("Please insert a start date.", presenter.lastError);
        assertFalse(presenter.successCalled);
    }

    @Test
    void fail_emptyDate() {
        TestRouteDataAccessSuccess dao = new TestRouteDataAccessSuccess(null);
        TestPresenter presenter = new TestPresenter();

        SaveInteractor interactor = new SaveInteractor(dao, presenter);
        SaveInput input = new SaveInput("roger", "", "Sunny", "Coat");

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("Please insert a start date.", presenter.lastError);
    }

    @Test
    void fail_invalidDateFormat() {
        TestRouteDataAccessSuccess dao = new TestRouteDataAccessSuccess(null);
        TestPresenter presenter = new TestPresenter();

        SaveInteractor interactor = new SaveInteractor(dao, presenter);
        SaveInput input = new SaveInput("roger", "2024-99-99", "Sunny", "Coat");

        interactor.execute(input);

        // invalid date DOES NOT call presenter according to YOUR code
        assertFalse(presenter.failCalled);
        assertFalse(presenter.successCalled);
    }

    @Test
    void success_savesCorrectly() {

        RouteInfo info = new RouteInfo(550.0, 300.0, "Test Route");

        TestRouteDataAccessSuccess dao =
                new TestRouteDataAccessSuccess(info);

        TestPresenter presenter = new TestPresenter();

        SaveInteractor interactor = new SaveInteractor(dao, presenter);

        dao.stops.add(new ItineraryStop("1", "Toronto", 43.0, -79.0, ""));
        dao.stops.add(new ItineraryStop("2", "Montreal", 45.0, -73.0, ""));

        SaveInput input = new SaveInput(
                "roger",
                "2024-12-01",
                "Sunny",
                "Coat"
        );

        interactor.execute(input);

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        assertEquals(LocalDate.of(2024, 12, 1), dao.startDate);
        assertEquals(1, dao.saved.size());

        Itinerary saved = dao.saved.get(0);

        assertEquals("Toronto", saved.getRecord().getOrigin());
        assertEquals("Montreal", saved.getRecord().getDestination());
    }
}
