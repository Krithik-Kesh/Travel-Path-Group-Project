package interfaceadapter;
import entity.ItineraryStop;
import entity.RouteInfo;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class IteneraryViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private RouteInfo routeInfo;
    private List<ItineraryStop> stops;
    private String error;

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public List<ItineraryStop> getStops() {
        return stops;
    }

    public String getError() {
        return error;
    }

    public void setRouteInfo(RouteInfo routeInfo) {
        this.routeInfo = routeInfo;
        firePropertyChanged();
    }

    public void setStops(List<ItineraryStop> stops) {
        this.stops = stops;
        firePropertyChanged();
    }

    public void setError(String error) {
        this.error = error;
        firePropertyChanged();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange("reorderDeleteStopsState", null, null);
    }
}


