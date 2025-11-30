package interfaceadapter.add_multiple_stops;

import usecase.add_stop.AddStopInputBoundary;
import usecase.add_stop.AddStopInputData;

public class AddStopController {
    // INTERFACE FOR INTERACTOR
    final AddStopInputBoundary addStopUseCaseInteractor;

    // THE DEPENCANCY INJECTION
    public AddStopController(AddStopInputBoundary addStopUseCaseInteractor) {
        this.addStopUseCaseInteractor = addStopUseCaseInteractor;
    }

    // UI CALLS EXECUTE
    public void execute(String cityName) {
        //PACKAGE RAW STRING
        AddStopInputData inputData = new AddStopInputData(cityName);

        //PASS TO INTERACTOR
        addStopUseCaseInteractor.execute(inputData);
    }
}