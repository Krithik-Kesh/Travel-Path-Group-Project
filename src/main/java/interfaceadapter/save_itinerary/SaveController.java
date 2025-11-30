package interface_adapter.save_itinerary;

import use_case.save_itinerary.SaveInput;
import use_case.save_itinerary.SaveInputBoundary;

public class SaveController {

    private final SaveInputBoundary saveInteractor;

    public SaveController(SaveInputBoundary saveInteractor) {
        this.saveInteractor = saveInteractor;
    }

    /**
     * CONTROLLER PASSES RAW STRINGS
     */
    public void execute(String username, String weather, String clothing) {
        // CREATE INPUT WITH 3 STRINGS REQURIED BY CONSTRUCTOR
        SaveInput input = new SaveInput(username, weather, clothing);

        // PASS TO THE BUSINESS LOGIC
        saveInteractor.execute(input);
    }
}