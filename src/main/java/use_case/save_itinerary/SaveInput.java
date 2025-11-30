package use_case.save_itinerary;

public class SaveInput {
    private final String username;

    public SaveInput(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
