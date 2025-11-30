package use_case;

import entity.Itinerary;

public interface ItineraryRepository {
    Itinerary findById(String id);

    void save(Itinerary itinerary);
}
