package entity;


public class WeatherData {
    private final double temp;
    private final String description;
    private final double windSpeed;
    private final double precipitation;

    public WeatherData(double temp,
                       String description,
                       double windSpeed,
                       double precipitation) {
        this.temp = temp;
        this.description = description;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
    }

    public double getTemperature() {
        return temp;
    }

    public String getDescription() {
        return description;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getPrecipitation() {
        return precipitation;
    }
}

