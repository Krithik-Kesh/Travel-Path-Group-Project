package use_case.get_previous_data;

/**
 * Output boundary for the "get previous data" use case.
 * The presenter will implement this.
 */
public interface GetPreviousDataOutputBoundary {

    /**
     * Present the results of loading past travel records.
     *
     * @param outputData contains the user's past travel records
     */
    void present(GetPreviousDataOutputData outputData);
}
