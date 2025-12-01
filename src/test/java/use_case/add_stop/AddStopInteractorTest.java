package use_case.add_stop;

import data_access.RouteDataAccessInterface;
import entity.ItineraryStop;
import entity.RouteInfo;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddStopInteractorTest extends TestCase {

    /* Simple test double for RouteDataAccessInterface. */
    private static class FakeRouteDataAccess implements RouteDataAccessInterface {

        final List<ItineraryStop> stops = new ArrayList<>();
        RouteInfo routeToReturn = new RouteInfo(10.0, 30.0, "Test route");
        boolean throwOnGetRoute = false;
        List<ItineraryStop> lastRouteStops;

        @Override
        public RouteInfo getRoute(List<ItineraryStop> stops) throws IOException {
            lastRouteStops = new ArrayList<>(stops);
            if (throwOnGetRoute) {
                throw new IOException("Boom");
            }
            return routeToReturn;
        }

        @Override
        public List<ItineraryStop> getStops() {
            return stops;
        }

        @Override
        public void addStop(ItineraryStop newStop) {
            stops.add(newStop);
        }
    }

    /* Presenter used to record which view method was called. */
    private static class TestPresenter implements AddStopOutputBoundary {

        AddStopOutputData lastSuccess;
        String lastError;

        @Override
        public void prepareSuccessView(AddStopOutputData outputData) {
            this.lastSuccess = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.lastError = error;
        }
    }

    public void testExecuteCallsFailViewOnError() {
        FakeRouteDataAccess dataAccess = new FakeRouteDataAccess();
        TestPresenter presenter = new TestPresenter();
        AddStopInteractor interactor = new AddStopInteractor(dataAccess, presenter);

        AddStopInputData input = new AddStopInputData("Toronto");
        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    public void testExecuteHandlesExceptionFromDataAccess() {
        FakeRouteDataAccess dataAccess = new FakeRouteDataAccess();
        dataAccess.throwOnGetRoute = true;

        TestPresenter presenter = new TestPresenter();
        AddStopInteractor interactor = new AddStopInteractor(dataAccess, presenter);

        AddStopInputData input = new AddStopInputData("Toronto");
        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.startsWith("Could not add stop:"));
    }
}


