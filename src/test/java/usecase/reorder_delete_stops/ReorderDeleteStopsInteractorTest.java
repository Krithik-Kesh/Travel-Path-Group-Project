package usecase.reorder_delete_stops;

import entity.ItineraryStop;
import entity.RouteInfo;
import data_access.RouteDataAccessInterface;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReorderDeleteStopsInteractorTest {

    private static class TestRouteDataAccessSuccess implements RouteDataAccessInterface {

        private List<ItineraryStop> lastStops = new ArrayList<>();
        private final RouteInfo routeInfoToReturn;

        TestRouteDataAccessSuccess(RouteInfo routeInfoToReturn) {
            this.routeInfoToReturn = routeInfoToReturn;
        }

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            this.lastStops = new ArrayList<>(stops);
            return routeInfoToReturn;
        }
    }

    private static class TestRouteDataAccessFailure implements RouteDataAccessInterface {

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            throw new IOException("Simulated failure");
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
        List<ItineraryStop> orderedStops = Arrays.asList(stop1, stop2);

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

