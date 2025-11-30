package usecase.save_itinerary;

public class SaveInput {
    private final String username;
    private final String weatherSummary;
    private final String clothingSuggestion;

    public SaveInput(String username, String weatherSummary, String clothingSuggestion) {
        this.username = username;
        this.weatherSummary = weatherSummary;
        this.clothingSuggestion = clothingSuggestion;
    }

    public String getUsername() { return username; }
    public String getWeatherSummary() { return weatherSummary; }
    public String getClothingSuggestion() { return clothingSuggestion; }
}
