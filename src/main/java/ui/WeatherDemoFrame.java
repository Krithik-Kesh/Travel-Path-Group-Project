package ui;

import GeolocationsAPIs.GeocodingService;
import interface_adapter.view_weather_adapt.ViewWeatherController;
import interface_adapter.view_weather_adapt.WeatherViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// Simple Swing UI to demo User Stories 2 & 3:
// view current weather at destination
// view 7-day forecast + clothing tips

// This frame ONLY talks to: GeocodingService   (address -> lat/lon)
//ViewWeatherController (use case input)
// WeatherViewModel      (read-only; via PropertyChangeListener)

public class WeatherDemoFrame extends JFrame implements PropertyChangeListener {

    private final GeolocationsAPIs.GeocodingService geocodingService;
    private final ViewWeatherController controller;
    private final WeatherViewModel viewModel;

    // ---- UI widgets ----
    private final JTextField destinationField = new JTextField(20);
    private final JTextArea currentWeatherArea = new JTextArea(10, 30);
    private final JTextArea tipsArea = new JTextArea(10, 30);
    private final JTextArea forecastArea = new JTextArea(10, 30);
    private final JLabel errorLabel = new JLabel(" ");

    public WeatherDemoFrame(GeocodingService geocodingService,
                            ViewWeatherController controller,
                            WeatherViewModel viewModel) {
        super("TravelPath – Weather Demo (US2 & US3)");

        this.geocodingService = geocodingService;
        this.controller = controller;
        this.viewModel = viewModel;

        // 1. listen to ViewModel updates
        this.viewModel.addPropertyChangeListener(this);

        // 2. basic window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(900, 400));

        // 3. configure text areas
        configureTextArea(currentWeatherArea);
        configureTextArea(tipsArea);
        configureTextArea(forecastArea);

        // 4. layout
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterPanels(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // ---------- layout helpers ----------

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.add(new JLabel("Destination: "));
        top.add(destinationField);

        JButton getWeatherButton = new JButton("Get Weather");
        getWeatherButton.addActionListener(e -> onGetWeather());
        top.add(getWeatherButton);

        return top;
    }

    private JPanel buildCenterPanels() {
        JPanel center = new JPanel(new GridLayout(1, 3, 8, 8));

        center.add(wrapWithTitledPanel("Current weather", currentWeatherArea));
        center.add(wrapWithTitledPanel("Clothing tips", tipsArea));
        center.add(wrapWithTitledPanel("7-day forecast", forecastArea));

        return center;
    }

    private JPanel buildStatusBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        errorLabel.setForeground(Color.RED);
        bottom.add(errorLabel, BorderLayout.CENTER);
        return bottom;
    }

    private static void configureTextArea(JTextArea area) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private static JComponent wrapWithTitledPanel(String title, JComponent inner) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(inner), BorderLayout.CENTER);
        return panel;
    }

    // ---------- button action ----------

    /**
     * Called when user clicks "Get Weather".
     * - geocode the destination string
     * - call controller with dest + lat + lon
     */
    private void onGetWeather() {
        String dest = destinationField.getText().trim();
        if (dest.isEmpty()) {
            errorLabel.setText("Please enter a destination.");
            return;
        }

        errorLabel.setText(" ");   // clear old errors

        try {
            GeocodingService.LatLon coords = geocodingService.geocode(dest);
            // 调用你的 controller（保持你原来的接口）
            controller.viewWeather(
                    coords.getLat(),
                    coords.getLon(),
                    dest
            );
        } catch (Exception ex) {
            // 如果 geocoding 或后面的调用失败，在下方显示错误信息
            errorLabel.setText("Geocoding failed: " + ex.getMessage());
        }
    }

    // ---------- view model -> UI ----------

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();

        // 根据 ViewModel 的字段变化更新 UI
        switch (property) {
            case "currentText" -> currentWeatherArea.setText(viewModel.getCurrentText());
            case "tipsText" -> tipsArea.setText(viewModel.getTipsText());
            case "forecastText" -> forecastArea.setText(viewModel.getForecastText());
            case "errorMessage" -> errorLabel.setText(viewModel.getErrorMessage());
            case "destinationLabel" -> destinationField.setText(viewModel.getDestination());
            default -> {}
        }
    }
}
