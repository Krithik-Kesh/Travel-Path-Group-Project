package entity;

// src/main/java/entity/DailyWeather.java

import java.time.LocalDate;

public class DailyWeather {

    private final LocalDate date;
    private final double minTemperature;
    private final double maxTemperature;
    private final String description;

    public DailyWeather(LocalDate date,
                        double minTemperature,
                        double maxTemperature,
                        String description) {
        this.date = date;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public String getDescription() {
        return description;
    }
}
