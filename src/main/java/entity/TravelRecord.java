package entity;
//travel record entities

public class TravelRecord {

    private final String username;
    private final String origin;
    private final String destination;
    private final String timeNeeded;
    private final String weatherSummary;
    private final String optimalPath;
    private final String clothingSuggestion;

    public TravelRecord(String username,
                        String origin,
                        String destination,
                        String timeNeeded,
                        String weatherSummary,
                        String optimalPath,
                        String clothingSuggestion) {
        this.username = username;
        this.origin = origin;
        this.destination = destination;
        this.timeNeeded = timeNeeded;
        this.weatherSummary = weatherSummary;
        this.optimalPath = optimalPath;
        this.clothingSuggestion = clothingSuggestion;
    }

    public String getUsername() {
        return username;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getTimeNeeded() {
        return timeNeeded;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public String getOptimalPath() {
        return optimalPath;
    }

    public String getClothingSuggestion() {
        return clothingSuggestion;
    }
}
