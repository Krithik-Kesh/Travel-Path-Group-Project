package use_case.get_previous_data;

public class HistoryInput {
    private final String username;

    public HistoryInput(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
