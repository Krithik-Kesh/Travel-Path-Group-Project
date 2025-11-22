package entity;


/**
 * Entity representing the result of a route search.
 * distanceKm and durationMinutes are rounded for display.
 */
public class RouteInfo {

    private final double distance;
    private final double durationMinutes;
    private final String summary;

    public RouteInfo(double distanceKm,
                     double durationMinutes,
                     String summary) {
        this.distance = distanceKm;
        this.durationMinutes = durationMinutes;
        this.summary = summary;
    }

    public double getDistance() {
        return distance;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public String getSummary() {
        return summary;
    }
}
