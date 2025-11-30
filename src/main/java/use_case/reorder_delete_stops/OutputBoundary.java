package use_case.reorder_delete_stops;

public interface OutputBoundary {
    void present(OutputData outputData);
    void presentError(String message);
}
