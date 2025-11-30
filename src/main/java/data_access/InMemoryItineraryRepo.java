package data_access;

import entity.Itinerary;
import usecase.ItineraryRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryItineraryRepo implements ItineraryRepository {

    private final Map<String, Itinerary> data = new HashMap<>();

    @Override
    public Itinerary findById(String id) {
        return data.get(id);
    }

    @Override
    public void save(Itinerary itinerary) {
        data.put(itinerary.getId(), itinerary);
    }
}
