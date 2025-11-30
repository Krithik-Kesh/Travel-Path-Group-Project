package use_case.get_previous_data;

public interface HistoryOutputBoundary {
    void presentHistory(HistoryOutput output);
    void prepareFailView(String error);
}