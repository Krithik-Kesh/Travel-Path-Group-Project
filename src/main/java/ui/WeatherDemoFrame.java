package ui;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.IOException;

import GeolocationsAPIs.GeocodingService;
import interfaceadapter.notes.AddNoteToStopController;
import interfaceadapter.notes.NotesViewModel;
import interfaceadapter.view_weather_adapt.ViewWeatherController;
import interfaceadapter.view_weather_adapt.WeatherViewModel;
import interfaceadapter.IteneraryViewModel;
import interfaceadapter.add_multiple_stops.AddStopController;
import interfaceadapter.set_start_date.SetStartDateController;
import data_access.RouteDataAccess;
import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import usecase.ItineraryRepository;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Refactored UI for a modern look
 */
public class WeatherDemoFrame extends JFrame implements PropertyChangeListener {

    // --- Dependencies ---
    private final GeocodingService geocodingService;
    private final ViewWeatherController weatherController;
    private final WeatherViewModel weatherViewModel;
    private final IteneraryViewModel itineraryViewModel;
    private final AddStopController addStopController;
    private final AddNoteToStopController addNoteController;
    private final NotesViewModel notesViewModel;
    private final ItineraryRepository itineraryRepository;
    private final String itineraryId;
    private final RouteDataAccess routeDataAccess = new RouteDataAccess();
    private final SetStartDateController setStartDateController;

    // --- UI Components ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Styling Constants
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private final Color BG_COLOR = new Color(245, 245, 250); // Light Gray-Blue

    // Login
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel loginErrorLabel;

    // Main Page Inputs
    private JLabel welcomeLabel;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField startDateField;
    private JTextField stopField;

    // Main Page Outputs
    private JTextArea currentWeatherArea;
    private JTextArea tipsArea;
    private JTextArea forecastArea;
    private JTextArea noteArea;
    private JLabel errorLabel;
    private final java.util.Map<String, String> cityWeatherMap = new java.util.LinkedHashMap<>();

    // Lists & Info
    private JLabel travelDistanceValueLabel;
    private JLabel travelTimeValueLabel;
    private DefaultListModel<String> stopListModel;
    private JList<String> stopList;

    // History
    private static final String HISTORY_FILE = "travel_history.json";
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private JList<String> historyList;

    private String currentUser = "";
    private String mainDestination = null;
    private String currentStartDate = "";

    public WeatherDemoFrame(GeocodingService geocoding,
                            ViewWeatherController weatherControl,
                            WeatherViewModel weatherView,
                            IteneraryViewModel itineraryView,
                            AddStopController addStopControl,
                            AddNoteToStopController addNoteControl,
                            NotesViewModel notesView,
                            ItineraryRepository itineraryRepo,
                            String itId,
                            SetStartDateController setStartDateControl) {

        super("TravelPath");
        // Dependencies assignment
        geocodingService = geocoding;
        weatherController = weatherControl;
        weatherViewModel = weatherView;
        itineraryViewModel = itineraryView;
        addStopController = addStopControl;
        addNoteController = addNoteControl;
        notesViewModel = notesView;
        itineraryRepository = itineraryRepo;
        itineraryId = itId;
        this.setStartDateController = setStartDateControl;

        // Listeners
        this.weatherViewModel.addPropertyChangeListener(this);
        this.itineraryViewModel.addPropertyChangeListener(this);

        // Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Slightly larger for better spacing
        setLocationRelativeTo(null);

        buildUi();
        setContentPane(cards);
    }

    private void buildUi() {
        cards.add(buildLoginPanel(), "login");
        cards.add(buildMainPanel(), "main");
        cards.add(buildHistoryPanel(), "history");
        cardLayout.show(cards, "login");
    }

    // --- LOGIN PANEL --- UI
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("TravelPath Login");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);

        // Inputs
        c.gridwidth = 1; c.gridy = 1;
        card.add(new JLabel("Username:"), c);
        usernameField = new JTextField(15);
        c.gridx = 1;
        card.add(usernameField, c);

        c.gridx = 0; c.gridy = 2;
        card.add(new JLabel("Password:"), c);
        passwordField = new JPasswordField(15);
        c.gridx = 1;
        card.add(passwordField, c);

        // Button
        JButton loginButton = createStyledButton("Log In");
        loginButton.addActionListener(e -> onLogin());
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(loginButton, c);

        // Error
        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setForeground(Color.RED);
        loginErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        c.gridy = 4;
        card.add(loginErrorLabel, c);

        panel.add(card);
        return panel;
    }

    // --- MAIN PANEL ---
    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header (Welcome + History Button)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        welcomeLabel = new JLabel("Welcome!", SwingConstants.LEFT);
        welcomeLabel.setFont(TITLE_FONT);

        JButton historyButton = createStyledButton("View History");
        historyButton.addActionListener(e -> cardLayout.show(cards, "history"));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(historyButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content (Split into Left Controls and Right Results)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 2 Columns
        contentPanel.setBackground(BG_COLOR);

        // --- LEFT COLUMN: INPUTS ---
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(BG_COLOR);

        // A. Trip Details Panel
        JPanel tripPanel = createSectionPanel("Trip Details");
        tripPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 1: Origin
        gbc.gridx = 0; gbc.gridy = 0;
        tripPanel.add(new JLabel("Origin:"), gbc);
        originField = new JTextField();
        gbc.gridx = 1;
        tripPanel.add(originField, gbc);

        // Row 2: Destination
        gbc.gridx = 0; gbc.gridy = 1;
        tripPanel.add(new JLabel("Destination:"), gbc);
        destinationField = new JTextField();
        gbc.gridx = 1;
        tripPanel.add(destinationField, gbc);

        // Row 3: Start Date
        gbc.gridx = 0; gbc.gridy = 2;
        tripPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        startDateField = new JTextField();
        gbc.gridx = 1;
        tripPanel.add(startDateField, gbc);

        // Row 4: Get Weather Button
        JButton getWeatherButton = createStyledButton("Get Forecast & Route");
        getWeatherButton.addActionListener(e -> onGetWeather());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        tripPanel.add(getWeatherButton, gbc);

        // B. Stops Panel
        JPanel stopsPanel = createSectionPanel("Manage Stops");
        stopsPanel.setLayout(new BorderLayout(5, 5));

        JPanel addStopSubPanel = new JPanel(new BorderLayout(5, 0));
        addStopSubPanel.setOpaque(false);
        stopField = new JTextField();
        JButton addStopButton = new JButton("+");
        addStopButton.addActionListener(e -> onAddStop());
        addStopSubPanel.add(stopField, BorderLayout.CENTER);
        addStopSubPanel.add(addStopButton, BorderLayout.EAST);
        addStopSubPanel.add(new JLabel("Add City: "), BorderLayout.WEST);

        stopListModel = new DefaultListModel<>();
        stopList = new JList<>(stopListModel);
        stopList.setVisibleRowCount(6);
        stopList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onStopSelected();
        });

        stopsPanel.add(addStopSubPanel, BorderLayout.NORTH);
        stopsPanel.add(new JScrollPane(stopList), BorderLayout.CENTER);

        JButton removeStopButton = new JButton("Remove Selected Stop");
        removeStopButton.addActionListener(e -> onRemoveSelected());
        stopsPanel.add(removeStopButton, BorderLayout.SOUTH);

        // C. Notes Panel
        JPanel notePanel = createSectionPanel("Notes");
        notePanel.setLayout(new BorderLayout(5, 5));
        noteArea = new JTextArea(4, 20);
        noteArea.setLineWrap(true);
        notePanel.add(new JScrollPane(noteArea), BorderLayout.CENTER);
        JButton saveNoteButton = new JButton("Save Note to Selected Stop");
        saveNoteButton.addActionListener(e -> onSaveNote());
        notePanel.add(saveNoteButton, BorderLayout.SOUTH);

        // Add sections to left column
        leftColumn.add(tripPanel);
        leftColumn.add(Box.createVerticalStrut(10));
        leftColumn.add(stopsPanel);
        leftColumn.add(Box.createVerticalStrut(10));
        leftColumn.add(notePanel);

        // --- RIGHT COLUMN: RESULTS ---
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(BG_COLOR);

        // D. Weather Display
        JPanel weatherPanel = createSectionPanel("Weather & Forecast");
        weatherPanel.setLayout(new GridLayout(3, 1, 5, 5));

        currentWeatherArea = createInfoArea();
        tipsArea = createInfoArea();
        forecastArea = createInfoArea();

        weatherPanel.add(wrapWithLabel("Current Weather:", currentWeatherArea));
        weatherPanel.add(wrapWithLabel("Clothing Tips:", tipsArea));
        weatherPanel.add(wrapWithLabel("7-Day Forecast:", forecastArea));

        // E. Travel Info Panel
        JPanel infoPanel = createSectionPanel("Travel Summary");
        infoPanel.setLayout(new GridLayout(2, 2, 10, 10));

        infoPanel.add(new JLabel("Total Distance:"));
        travelDistanceValueLabel = new JLabel("0.0 km");
        travelDistanceValueLabel.setFont(HEADER_FONT);
        infoPanel.add(travelDistanceValueLabel);

        infoPanel.add(new JLabel("Total Time:"));
        travelTimeValueLabel = new JLabel("0 min");
        travelTimeValueLabel.setFont(HEADER_FONT);
        infoPanel.add(travelTimeValueLabel);

        // Add sections to right column
        rightColumn.add(weatherPanel);
        rightColumn.add(Box.createVerticalStrut(10));
        rightColumn.add(infoPanel);

        // Add columns to content panel
        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Error Message Bar
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(errorLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // --- HISTORY PANEL ---
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Travel History", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.NORTH);

        historyList = new JList<>(historyModel);
        historyList.setFont(NORMAL_FONT);
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Dashboard");
        backButton.addActionListener(e -> cardLayout.show(cards, "main"));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BG_COLOR);
        btnPanel.add(backButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- HELPER METHODS FOR STYLING ---

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.BLACK); // Metal L&F sometimes overrides white text
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        HEADER_FONT,
                        PRIMARY_COLOR
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return p;
    }

    private JTextArea createInfoArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(NORMAL_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(250, 250, 250));
        return area;
    }

    private JPanel wrapWithLabel(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // --- LOGIC METHODS

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            loginErrorLabel.setText("Please enter a username.");
            return;
        }
        int MIN_PASSWORD_LENGTH = 6;
        if (!checkForPassword(password, MIN_PASSWORD_LENGTH)) {
            loginErrorLabel.setText("Password must be > " + MIN_PASSWORD_LENGTH + " chars, mixed case & digit.");
            return;
        }
        currentUser = username;
        welcomeLabel.setText("Welcome, " + currentUser + "!");
        loginErrorLabel.setText(" ");

        loadHistoryForCurrentUser();

        cardLayout.show(cards, "main");
    }

    private void onAddStop() {
        String city = stopField.getText().trim();
        if (city.isEmpty()) {
            errorLabel.setText("Please enter a stop city.");
            return;
        }
        if (addStopController == null) {
            errorLabel.setText("AddStopController not wired.");
            return;
        }
        addStopController.execute(city);
        stopField.setText("");
        errorLabel.setText("Adding stop: " + city + " ...");
    }

    private void onRemoveSelected() {
        int index = stopList.getSelectedIndex();
        if (index < 0) {
            errorLabel.setText("Please select a stop to remove.");
            return;
        }
        List<ItineraryStop> currentStops = itineraryViewModel.getStops();
        if (currentStops == null || index >= currentStops.size()) {
            errorLabel.setText("Invalid stop selection.");
            return;
        }
        ItineraryStop removed = currentStops.get(index);
        java.util.List<ItineraryStop> newStops = new java.util.ArrayList<>(currentStops);
        newStops.remove(index);
        itineraryViewModel.setStops(newStops);
        itineraryViewModel.setError("");
        errorLabel.setText("Removed stop: " + removed.getName());
    }

    private void onStopSelected() {
        int index = stopList.getSelectedIndex();
        if (index < 0) {
            if (noteArea != null) noteArea.setText("");
            return;
        }
        List<ItineraryStop> stops = itineraryViewModel.getStops();
        if (stops == null || index >= stops.size()) {
            noteArea.setText("");
            return;
        }
        ItineraryStop stop = stops.get(index);
        String notes = stop.getNotes();
        noteArea.setText(notes == null ? "" : notes);
    }

    private void onSaveNote() {
            int index = stopList.getSelectedIndex();
            if (index < 0) {
                errorLabel.setText("Please select a stop to save note.");
                return;
            }

            List<ItineraryStop> stops = itineraryViewModel.getStops();
            if (stops == null || index >= stops.size()) {
                errorLabel.setText("Invalid stop selection.");
                return;
            }

            String noteText = noteArea.getText().trim();
            if (noteText.isEmpty()) {
                errorLabel.setText("Note cannot be empty.");
                return;
            }

            ItineraryStop stop = stops.get(index);
            stop.setNotes(noteText);

            try {
                Itinerary itinerary = itineraryRepository.findById(itineraryId);
                if (itinerary == null) {
                    itinerary = new Itinerary(itineraryId, null, new java.util.ArrayList<>(stops));
                } else {
                    itinerary.getStops().clear();
                    itinerary.getStops().addAll(stops);
                }

                itineraryRepository.save(itinerary);

                notesViewModel.setErrorMessage("");
                notesViewModel.setCurrentNoteText(noteText);

                itineraryViewModel.setStops(new java.util.ArrayList<>(stops));
                errorLabel.setText("Saved note for: " + stop.getName());
            } catch (Exception e) {
                errorLabel.setText("Failed to save note: " + e.getMessage());
            }
        }


        private void onGetWeather() {
        String originCity = originField.getText().trim();
        String dest = destinationField.getText().trim();
        String startDateText = startDateField.getText().trim();

        if (originCity.isEmpty() || dest.isEmpty()) {
            errorLabel.setText("Please enter both origin and destination.");
            return;
        }
        currentStartDate = "";
        if (!startDateText.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(startDateText);
                currentStartDate = startDateText;
                if (setStartDateController != null) {
                    setStartDateController.setStartDate(parsedDate);
                }
            } catch (DateTimeParseException e) {
                errorLabel.setText("Start date must be in format YYYY-MM-DD.");
                return;
            }
        }
        mainDestination = dest;
        currentWeatherArea.setText("");
        cityWeatherMap.clear();
        try {
            GeocodingService.LatLon destCoords = geocodingService.geocode(dest);
            GeocodingService.LatLon originCoords = geocodingService.geocode(originCity);
            weatherController.viewWeather(destCoords.getLat(), destCoords.getLon(), dest);

            List<ItineraryStop> stops = itineraryViewModel.getStops();
            if (stops != null) {
                for (ItineraryStop s : stops) {
                    weatherController.viewWeather(s.getLatitude(), s.getLongitude(), s.getName());
                }
            }
            java.util.List<ItineraryStop> routeStops = new java.util.ArrayList<>();
            routeStops.add(new ItineraryStop("origin", originCity, originCoords.getLat(), originCoords.getLon(), ""));
            if (stops != null && !stops.isEmpty()) {
                routeStops.addAll(stops);
            }
            routeStops.add(new ItineraryStop("destination", dest, destCoords.getLat(), destCoords.getLon(), ""));

            RouteInfo routeInfo = routeDataAccess.getRoute(routeStops);
            if (routeInfo.getDistance() <= 0.0) {
                updateTravelInfo("No route found", "No route found");
            } else {
                String distanceText = String.format("%.1f km", routeInfo.getDistance());
                String timeText = String.format("%.0f min", routeInfo.getDurationMinutes());
                updateTravelInfo(distanceText, timeText);
            }
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
            updateTravelInfo("N/A", "N/A");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object src = evt.getSource();

        // 1. LISTEN FOR WEATHER UPDATES
        if (src == weatherViewModel) {
            String name = evt.getPropertyName();
            if ("forecastText".equals(name) || "errorMessage".equals(name)) {
                handleWeatherModelChange();
            }
        }

        // 2. LISTEN FOR ITINERARY UPDATES
        else if (src == itineraryViewModel) {
            // NOTE: Check if your ViewModel uses "itineraryState" or just "state"
            if ("itineraryState".equals(evt.getPropertyName())) {
                handleItineraryModelChange();
            }
            // Also update the list if "error" property changes, or any generic property
            // This ensures the list updates even if the property name varies slightly
            if (evt.getPropertyName() != null && evt.getPropertyName().contains("State")) {
                handleItineraryModelChange();
            }
        }
    }

    // --- Updates the text areas for weather ---
    private void handleWeatherModelChange() {
        String destination = weatherViewModel.getDestination();
        String currentText = weatherViewModel.getCurrentText();
        String tipsText = weatherViewModel.getTipsText();
        String forecastText = weatherViewModel.getForecastText();
        String error = weatherViewModel.getErrorMessage();

        errorLabel.setText((error == null || error.isEmpty()) ? " " : error);
        if (error != null && !error.isEmpty()) return;
        if (destination == null || destination.isEmpty() || currentText == null || currentText.isEmpty()) return;

        boolean isMain = destination.equalsIgnoreCase(mainDestination);
        boolean isStop = false;
        String noteForCity = "";
        List<ItineraryStop> stops = itineraryViewModel.getStops();
        if (stops != null) {
            for (ItineraryStop s : stops) {
                if (s.getName().equalsIgnoreCase(destination)) {
                    isStop = true;
                    if (s.getNotes() != null) noteForCity = s.getNotes();
                    break;
                }
            }
        }

        cityWeatherMap.put(destination, currentText);
        StringBuilder currentSb = new StringBuilder();
        for (java.util.Map.Entry<String, String> entry : cityWeatherMap.entrySet()) {
            currentSb.append(entry.getKey()).append(":\n").append(entry.getValue()).append("\n\n");
        }
        currentWeatherArea.setText(currentSb.toString());

        if (isMain) {
            destinationField.setText(destination);
            tipsArea.setText(tipsText != null ? tipsText : "");
            forecastArea.setText(forecastText != null ? forecastText : "");
        }

        // Logic for history list
        String weatherSummary = currentText.replace('\n', ' ');
        String tipSummary = "";
        if (tipsText != null && !tipsText.isEmpty()) {
            String[] lines = tipsText.split("\\R");
            if (lines.length > 0) tipSummary = lines[0].replace("•", "").trim();
        }
        String noteSummary = (noteForCity == null) ? "" : noteForCity.trim();
        String labelPrefix = isMain ? "[Destination] " : (isStop ? "[Stop] " : "");
        String datePrefix = "";
        if (startDateField != null) {
            String startDateText = startDateField.getText().trim();
            if (!startDateText.isEmpty()) datePrefix = "[Start " + startDateText + "] ";
        }

        StringBuilder historyLine = new StringBuilder();
        historyLine.append(datePrefix).append(labelPrefix).append(destination).append(" — ").append(weatherSummary);
        if (!tipSummary.isEmpty()) historyLine.append(" | Tips: ").append(tipSummary);
        if (!noteSummary.isEmpty()) historyLine.append(" | Note: ").append(noteSummary);

        String historyStr = historyLine.toString();
        int size = historyModel.getSize();
        if (size == 0 || !historyStr.equals(historyModel.getElementAt(size - 1))) {
            historyModel.addElement(historyStr);
            saveHistoryLine(historyStr);
        }

    }

    // --- RESTORED METHOD: Updates the Stops List ---
    private void handleItineraryModelChange() {
        String err = itineraryViewModel.getError();
        if (err != null && !err.isEmpty()) errorLabel.setText(err);

        stopListModel.clear();
        List<ItineraryStop> stops = itineraryViewModel.getStops();
        if (stops != null) {
            for (ItineraryStop s : stops) {
                String label = s.getName();
                String notes = s.getNotes();
                if (notes != null && !notes.isEmpty()) {
                    String snippet = notes.length() > 20 ? notes.substring(0, 20) + "..." : notes;
                    label = label + " — " + snippet;
                }
                stopListModel.addElement(label);
            }
        }
    }

    private void updateTravelInfo(String distanceText, String timeText) {
        if (travelDistanceValueLabel != null) travelDistanceValueLabel.setText(distanceText);
        if (travelTimeValueLabel != null) travelTimeValueLabel.setText(timeText);
    }

        private void loadHistoryForCurrentUser() {
            historyModel.clear();
            if (currentUser == null || currentUser.isEmpty()) {
                return;
            }

            JSONObject root = readHistoryRoot();
            if (!root.has(currentUser)) {
                return;
            }

            JSONArray arr = root.getJSONArray(currentUser);
            for (int i = 0; i < arr.length(); i++) {
                String line = arr.getString(i);
                historyModel.addElement(line);
            }
        }

        private void saveHistoryLine(String historyStr) {
            if (currentUser == null || currentUser.isEmpty()) {
                return;
            }

            try {
                JSONObject root = readHistoryRoot();
                JSONArray arr = root.optJSONArray(currentUser);
                if (arr == null) {
                    arr = new JSONArray();
                }
                arr.put(historyStr);
                root.put(currentUser, arr);

                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(HISTORY_FILE))) {
                    writer.write(root.toString(4));
                }
            } catch (IOException e) {
                System.err.println("Failed to write history: " + e.getMessage());
            }
        }

        private JSONObject readHistoryRoot() {
            Path path = Paths.get(HISTORY_FILE);
            if (!Files.exists(path)) {
                return new JSONObject();
            }
            try {
                String content = Files.readString(path);
                if (content == null || content.isBlank()) {
                    return new JSONObject();
                }
                return new JSONObject(content);
            } catch (IOException e) {
                System.err.println("Failed to read history: " + e.getMessage());
                return new JSONObject();
            }
        }



    private static boolean checkForPassword(String str, int minLength) {
        if (str == null || str.length() < minLength) return false;
        boolean hasLow = Pattern.matches(".*[a-z].*", str);
        boolean hasUp = Pattern.matches(".*[A-Z].*", str);
        boolean hasDigit = Pattern.matches(".*\\d.*", str);
        return hasLow && hasUp && hasDigit;
    }
}