package use_case.view_weather;
// presenter implement the function, and interactor use it
public interface ViewWeatherOutputBound {
    void present(OutputData outputData);
    void presentError(String message);

}
