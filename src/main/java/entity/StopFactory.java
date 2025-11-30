package entity;

import java.util.UUID;
    /**
     * CREATE NEW ITENERARY STOP
     */
public class StopFactory {
    public ItineraryStop create(String cityName, double latitude, double longitude) {
        // MAKES UNIQUE ID FOR STOPS
        String id = UUID.randomUUID().toString();
        return new ItineraryStop(id, cityName, latitude, longitude, "");
    }
}
