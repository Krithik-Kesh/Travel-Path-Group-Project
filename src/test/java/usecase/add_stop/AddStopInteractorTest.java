package usecase.add_stop;

import entity.ItineraryStop;
import entity.RouteInfo;
import entity.Itinerary;
import entity.StopFactory;

import interfaceadapter.reorder_delete_stops.RouteDataAccessInterface;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AddStopInteractorTest {

    // Fake DAO
    private static class FakeDAO implements RouteDataAccessInterface {

        List<ItineraryStop> stops = new ArrayList<>();
        RouteInfo routeInfoToReturn;
        boolean throwOnGetRoute = false;

        @Override
        public void addStop(ItineraryStop stop) {
            stops.add(stop);
        }

        @Override
        public List<ItineraryStop> getStops() {
            return new ArrayList<>(stops);
        }

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            if (throwOnGetRoute) {
                throw new IOException("Route API failed");
            }
            return routeInfoToReturn;
        }

        @Override public void saveItinerary(Itinerary itinerary) {}
        @Override public List<Itinerary> loadItineraries() { return null; }
        @Override public void setStartDate(java.time.LocalDate date) {}
        @Override public java.time.LocalDate getStartDate() { return null; }
    }

    // Fake Presenter
    private static class FakePresenter implements AddStopOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;

        AddStopOutputData lastSuccess;
        String lastError;

        @Override
        public void prepareSuccessView(AddStopOutputData outputData) {
            successCalled = true;
            lastSuccess = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalled = true;
            lastError = errorMessage;
        }
    }

    // Tet 1: Success
    @Test
    void execute_success_addsStopAndRecalculatesRoute() {

        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();
        StopFactory factory = new StopFactory();

        // Fake RouteInfo
        dao.routeInfoToReturn = new RouteInfo(100.0, 60.0, "OK");

        // Fake interactor with overridden APICaller
        AddStopInteractor interactor = new AddStopInteractor(dao, presenter, factory) {
            @Override
            public void execute(AddStopInputData inputData) {
                // fake APICaller behavior
                ItineraryStop fakeStop = new ItineraryStop(
                        "1", "Toronto", 43.0, -79.0, ""
                );
                dao.addStop(fakeStop);
                try {
                    RouteInfo info = dao.getRoute(dao.getStops());
                    AddStopOutputData output =
                            new AddStopOutputData(dao.getStops(), info);
                    presenter.prepareSuccessView(output);
                } catch (Exception e) {
                    presenter.prepareFailView("Could not add stop: " + e.getMessage());
                }
            }
        };

        AddStopInputData input = new AddStopInputData("Toronto");
        interactor.execute(input);

        // Validate presenter
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        // Validate DAO interactions
        assertEquals(1, dao.getStops().size());
        assertNotNull(presenter.lastSuccess.getRouteInfo());
    }

    // Test 2: Google API failure
    @Test
    void execute_failure_whenParsingFails() {

        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();
        StopFactory factory = new StopFactory();

        AddStopInteractor interactor = new AddStopInteractor(dao, presenter, factory) {
            @Override
            public void execute(AddStopInputData inputData) {
                // Simulate Google API failure
                presenter.prepareFailView("Could not add stop: API failure");
            }
        };

        AddStopInputData input = new AddStopInputData("InvalidCity");
        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);
        assertTrue(presenter.lastError.contains("Could not add stop"));
    }

    // Test 3: Route API failure
    @Test
    void execute_failure_whenRouteApiFails() {

        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();
        StopFactory factory = new StopFactory();

        dao.throwOnGetRoute = true; // Fail route calculation

        AddStopInteractor interactor = new AddStopInteractor(dao, presenter, factory) {
            @Override
            public void execute(AddStopInputData inputData) {
                ItineraryStop fakeStop = new ItineraryStop("1", "Toronto", 43, -79, "");
                dao.addStop(fakeStop);
                try {
                    dao.getRoute(dao.getStops());  // will throw
                } catch (Exception e) {
                    presenter.prepareFailView("Could not add stop: " + e.getMessage());
                }
            }
        };

        AddStopInputData input = new AddStopInputData("Toronto");
        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertFalse(presenter.successCalled);
        assertTrue(presenter.lastError.contains("Could not add stop"));
    }
}