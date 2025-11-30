import entity.DailyWeather;
import entity.WeatherData;
import org.junit.jupiter.api.Test;
import data_access.WeatherDataAccessInterface;
import usecase.view_weather.InputData;
import usecase.view_weather.OutputData;
import usecase.view_weather.ViewWeatherInteractor;
import usecase.view_weather.ViewWeatherOutputBound;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for User Stories 2 & 3:
 *  - View current weather at destination (what to wear today)
 *  - View 7-day forecast for trip dates
 *
 * We do NOT call the real OpenWeather API here.
 * Instead, we use a fake WeatherDataAccessInterface that returns fixed data.
 */
class ViewWeatherInteractorTest {

    /**
     * Fake gateway returning fixed current weather & forecast.
     * It can be configured to throw IOException to test the error path.
     */
    private static class FakeWeatherGateway implements WeatherDataAccessInterface {
        private final boolean shouldThrow;

        FakeWeatherGateway(boolean shouldThrow) {
            this.shouldThrow = shouldThrow;
        }

        @Override
        public WeatherData getCurrentWeather(double lat, double lon) throws IOException {
            if (shouldThrow) {
                throw new IOException("boom current");
            }
            return new WeatherData(-3.0, "light snow", 5.0, 0.5);
        }

        @Override
        public List<DailyWeather> getDailyForecast(double lat, double lon) throws IOException {
            if (shouldThrow) {
                throw new IOException("boom forecast");
            }
            return Arrays.asList(
                    new DailyWeather(LocalDate.of(2025, 1, 1), -5, 0, "cold"),
                    new DailyWeather(LocalDate.of(2025, 1, 2), -4, 1, "cold"),
                    new DailyWeather(LocalDate.of(2025, 1, 3), -3, 2, "cold")
            );
        }
    }

    /**
     * Recording presenter for tests: remembers last output / error
     * and which method was called.
     */
    private static class RecordingPresenter implements ViewWeatherOutputBound {
        OutputData lastOutput;
        String lastError;
        boolean presentCalled;
        boolean errorCalled;

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
    void execute_success_populatesOutputAndNoError() {
        // given a gateway that succeeds
        FakeWeatherGateway gateway = new FakeWeatherGateway(false);
        RecordingPresenter presenter = new RecordingPresenter();
        ViewWeatherInteractor interactor = new ViewWeatherInteractor(gateway, presenter);

        InputData input = new InputData(43.65, -79.38, "Toronto");

        // when
        interactor.execute(input);

        // then
        assertTrue(presenter.presentCalled, "Presenter.present should be called");
        assertFalse(presenter.errorCalled, "Presenter.presentError must NOT be called");

        assertNotNull(presenter.lastOutput);
        assertEquals("Toronto", presenter.lastOutput.getDestinationLabel());
        assertNotNull(presenter.lastOutput.getCurrentWeather());
        assertEquals(3, presenter.lastOutput.getForecast().size());

        assertNotNull(presenter.lastOutput.getClothingSuggestionText());
        assertFalse(presenter.lastOutput.getClothingSuggestionText().isEmpty());
    }

    @Test
    void execute_whenGatewayThrows_callsPresenterError() {
        // given a gateway that throws IOException
        FakeWeatherGateway gateway = new FakeWeatherGateway(true);
        RecordingPresenter presenter = new RecordingPresenter();
        ViewWeatherInteractor interactor = new ViewWeatherInteractor(gateway, presenter);

        InputData input = new InputData(43.65, -79.38, "Toronto");

        // when
        interactor.execute(input);

        // then: error path
        assertTrue(presenter.errorCalled,
                "Error message should come from presenter");
        assertNotNull(presenter.lastError);
        assertFalse(presenter.lastError.isEmpty());
        assertFalse(presenter.presentCalled,
                "On error, normal present() should not be called");
    }
}
