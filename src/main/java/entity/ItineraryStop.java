package entity;
// 1

public class ItineraryStop {
    private final String id;
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
}
