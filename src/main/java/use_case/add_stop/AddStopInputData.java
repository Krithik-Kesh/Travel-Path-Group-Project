package use_case.add_stop;

public class AddStopInputData {
    private final String cityInput;

    public AddStopInputData(String cityInput) {
        this.cityInput = cityInput;
    }

    public String getCityInput() {
        return cityInput;
    }
}
