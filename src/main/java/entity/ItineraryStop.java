package entity;
//  ITENERARY LISTS

import java.util.List;

public class ItineraryStop {
    private final String id;
    private final TravelRecord record;
    private String cityName = "";
    private final double latitude;
    private final double longitude;
    private String notes;
    public ItineraryStop(String id,
                         String name,
                         double latitude,
                         double longitude,
                         String notes) {
        this.id = id;
        this.cityName = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TravelRecord getRecord() {
        return record;
    }

    public ItineraryStop findStopById(String stopId) {
        for (ItineraryStop stop: ) {
            if (stop.getId().equals(stopId)) {
                return stop;
            }
        }
        return null;
    }
}
}
