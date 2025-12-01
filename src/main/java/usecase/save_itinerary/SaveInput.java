package usecase.save_itinerary;

public class SaveInput {
    private final String username;
    private final String weatherSummary;
    private final String clothingSuggestion;
    private final String startDateInput;


    public SaveInput(String username, String weatherSummary, String clothingSuggestion, String startDateInput) {
        this.username = username;
        this.weatherSummary = weatherSummary;
        this.clothingSuggestion = clothingSuggestion;
        this.startDateInput = startDateInput;
    }

    public String getUsername() { return username; }
    public String getWeatherSummary() { return weatherSummary; }
    public String getClothingSuggestion() { return clothingSuggestion; }
    public String getStartDateInput() {return startDateInput;}
}
