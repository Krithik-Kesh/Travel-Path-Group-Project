package use_case.get_previous_data;

/**
 * Input boundary for the "get previous data" use case.
 */
public interface GetPreviousDataInputBoundary {

    /**
     * Execute the use case.
     *
     * @param inputData data about which user we are loading records for
     */
    void execute(GetPreviousDataInputData inputData);
}
