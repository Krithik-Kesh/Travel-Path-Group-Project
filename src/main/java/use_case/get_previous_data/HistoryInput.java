package use_case.get_previous_data;
//
public class HistoryInput {

    private final String user;

    public HistoryInput(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
