package data_access;

import entity.TravelRecord;
import usecase.get_previous_data.HistoryRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 //Very simple in-memory history repo.
 //Shared by: save_itinerary + get_previous_data.

public class InMemoryHistoryRepo implements HistoryRepo {

    private final Map<String, List<TravelRecord>> data = new HashMap<>();

    @Override
    public List<TravelRecord> findByUser(String user) {
        return data.getOrDefault(user, new ArrayList<>());
    }

    @Override
    public void save(String user, TravelRecord record) {
        data.computeIfAbsent(user, u -> new ArrayList<>()).add(record);
    }
}
