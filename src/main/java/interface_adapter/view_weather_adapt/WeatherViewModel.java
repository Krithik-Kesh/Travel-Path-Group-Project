package interface_adapter.view_weather_adapt;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class WeatherViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    // Destination: the destination for our travel app
    // currentText: the current weather text for this destination
    // tipsText: the tips that generate from the tipGenerator
    // forecastText: the forecast String that have the 7 days forecast weather in
    // errorMessage: potential errorMessage that could exist if there is something wrong
    // like api failure

    private String destination = "";
    private String currentText = "";
    private String tipsText = "";
    private String forecastText = "";
    private String errorMessage = "";

    // add listener so that when we change the data in ViewModel it automatic update that
    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }
    // delete listener
    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }

    public String getDestination() {
        return destination;
    }

    // setDestinationLabel will store the old value and update the new one for viewModel

    public void setDestinationLabel(String destinationLabel) {
        String old = this.destination;
        this.destination = destinationLabel;
        support.firePropertyChange("destinationLabel", old, destinationLabel);
    }

    public String getCurrentText() {
        return currentText;
    }

    public void setCurrentText(String currentText) {
        String old = this.currentText;
        this.currentText = currentText;
        support.firePropertyChange("currentText", old, currentText);
    }

    public String getTipsText() {
        return tipsText;
    }

    public void setTipsText(String tipsText) {
        String old = this.tipsText;
        this.tipsText = tipsText;
        support.firePropertyChange("tipsText", old, tipsText);
    }

    public String getForecastText() {
        return forecastText;
    }

    public void setForecastText(String forecastText) {
        String old = this.forecastText;
        this.forecastText = forecastText;
        support.firePropertyChange("forecastText", old, forecastText);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String old = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", old, errorMessage);
    }
}
