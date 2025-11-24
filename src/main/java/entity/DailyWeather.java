package entity;

// src/main/java/entity/DailyWeather

import java.time.LocalDate;

public class DailyWeather {

    private final LocalDate date;
    private final double minTemp;
    private final double maxTemp;
    private final String description;

    public DailyWeather(LocalDate date,
                        double minTemp,
                        double maxTemp,
                        String description) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getMinTemperature() {
        return minTemp;
    }

    public double getMaxTemperature() {
        return maxTemp;
    }

    public String getDescription() {
        return description;
    }
}
