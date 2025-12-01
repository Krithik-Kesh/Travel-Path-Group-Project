package GeolocationAPITest;
import entity.ItineraryStop;
import entity.StopFactory;

public class APICallerTest {
    public APICallerTest() { }

    public String getJson(String encodedLocation) {
        return "{}"; // dummy code
    }

    public ItineraryStop parseJsonToStop(String jsonString, String rawInput) {
        StopFactory factory = new StopFactory();
        return factory.create(rawInput, 43.0, -79.0);
    }
}



