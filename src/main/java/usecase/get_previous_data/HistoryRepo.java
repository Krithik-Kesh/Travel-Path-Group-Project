package usecase.get_previous_data;

import entity.TravelRecord;

import java.util.List;

public interface HistoryRepo {

    List<TravelRecord> findByUser(String user);

    void save(String user, TravelRecord record);
}
