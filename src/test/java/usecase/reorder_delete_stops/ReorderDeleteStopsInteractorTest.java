package usecase.reorder_delete_stops;

import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import interfaceadapter.reorder_delete_stops.RouteDataAccessInterface;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReorderDeleteStopsInteractorTest {

    private static class TestRouteDataAccessSuccess implements RouteDataAccessInterface {

        private List<ItineraryStop> lastStops = new ArrayList<>();
        private final RouteInfo routeInfoToReturn;
        private final List<ItineraryStop> storedStops = new ArrayList<>();
        private final List<Itinerary> savedItineraries = new ArrayList<>();
        private LocalDate startDate;

        TestRouteDataAccessSuccess(RouteInfo routeInfoToReturn) {
            this.routeInfoToReturn = routeInfoToReturn;
        }

        @Override
        public void addStop(ItineraryStop stop) {
            storedStops.add(stop);
        }

        @Override
        public List<ItineraryStop> getStops() {
            return new ArrayList<>(storedStops);
        }

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            this.lastStops = new ArrayList<>(stops);
            return routeInfoToReturn;
        }

        @Override
        public void saveItinerary(Itinerary itinerary) {
            savedItineraries.add(itinerary);
        }

        @Override
        public List<Itinerary> loadItineraries() {
            return new ArrayList<>(savedItineraries);
        }

        @Override
        public void setStartDate(LocalDate date) {
            this.startDate = date;
        }

        @Override
        public LocalDate getStartDate() {
            return startDate;
        }
    }

    private static class TestRouteDataAccessFailure implements RouteDataAccessInterface {

        private final List<ItineraryStop> storedStops = new ArrayList<>();
        private final List<Itinerary> savedItineraries = new ArrayList<>();
        private LocalDate startDate;

        @Override
        public void addStop(ItineraryStop stop) {
            storedStops.add(stop);
        }

        @Override
        public List<ItineraryStop> getStops() {
            return new ArrayList<>(storedStops);
        }

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            throw new IOException("Simulated failure");
        }

        @Override
        public void saveItinerary(Itinerary itinerary) {
            savedItineraries.add(itinerary);
        }

        @Override
        public List<Itinerary> loadItineraries() {
            return new ArrayList<>(savedItineraries);
        }

        @Override
        public void setStartDate(LocalDate date) {
            this.startDate = date;
        }

        @Override
        public LocalDate getStartDate() {
            return startDate;
        }
    }

    private static class TestPresenter implements OutputBoundary {

        boolean presentCalled = false;
        boolean errorCalled = false;
        OutputData lastOutput;
        String lastError;

        @Override
        public void present(OutputData outputData) {
            this.presentCalled = true;
            this.lastOutput = outputData;
        }

        @Override
        public void presentError(String message) {
            this.errorCalled = true;
            this.lastError = message;
        }
    }

    @Test
    void execute_success_callsRouteApiAndPresentsOutput() {
        ItineraryStop stop1 = new ItineraryStop("1", "Toronto", 43.65, -79.38, "");
        ItineraryStop stop2 = new ItineraryStop("2", "Montreal", 45.50, -73.57, "");
        List<ItineraryStop> orderedStops = List.of(stop1, stop2);

        RouteInfo routeInfo = new RouteInfo(550.0, 360.0, "Total: 550 km, 360 min");

        TestRouteDataAccessSuccess routeDataAccess = new TestRouteDataAccessSuccess(routeInfo);
        TestPresenter presenter = new TestPresenter();

        ReorderDeleteStopsInteractor interactor =
                new ReorderDeleteStopsInteractor(routeDataAccess, presenter);

        InputData input = new InputData(orderedStops);

        interactor.execute(input);

        assertEquals(orderedStops, routeDataAccess.lastStops);
        assertTrue(presenter.presentCalled);
        assertFalse(presenter.errorCalled);

        assertNotNull(presenter.lastOutput);
        assertEquals(orderedStops, presenter.lastOutput.getOrderedStops());
        assertSame(routeInfo, presenter.lastOutput.getRouteInfo());
    }

    @Test
    void execute_failure_callsPresentError() {
        ItineraryStop stop1 = new ItineraryStop("1", "Toronto", 43.65, -79.38, "");
        List<ItineraryStop> orderedStops = List.of(stop1);

        TestRouteDataAccessFailure routeDataAccess = new TestRouteDataAccessFailure();
        TestPresenter presenter = new TestPresenter();

        ReorderDeleteStopsInteractor interactor =
                new ReorderDeleteStopsInteractor(routeDataAccess, presenter);

        InputData input = new InputData(orderedStops);

        interactor.execute(input);

        assertTrue(presenter.errorCalled);
        assertFalse(presenter.presentCalled);

        assertNotNull(presenter.lastError);
        assertEquals("Unable to calculate route.", presenter.lastError);
    }
}