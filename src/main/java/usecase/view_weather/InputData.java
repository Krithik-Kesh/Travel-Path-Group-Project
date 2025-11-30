package usecase.view_weather;

// Data pass from controller to this use case.

public class InputData {
    private final double latitude;
    private final double longitude;
    private final String destinationLabel;

    public InputData(double lat,
                                double lon,
                                String destinationLabel) {
        this.latitude = lat;
        this.longitude = lon;
        this.destinationLabel = destinationLabel;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }
}

