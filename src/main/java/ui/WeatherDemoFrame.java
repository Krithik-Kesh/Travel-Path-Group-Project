package ui;

import GeolocationsAPIs.GeocodingService;
import interfaceadapter.notes.AddNoteToStopController;
import interfaceadapter.notes.NotesViewModel;
import interfaceadapter.view_weather_adapt.ViewWeatherController;
import interfaceadapter.view_weather_adapt.WeatherViewModel;
import interfaceadapter.IteneraryViewModel;
import interfaceadapter.add_multiple_stops.AddStopController;
import interfaceadapter.set_start_date.SetStartDateController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import data_access.RouteDataAccess;
import entity.Itinerary;
import entity.ItineraryStop;
import entity.RouteInfo;
import usecase.ItineraryRepository;
import javax.swing.*;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.regex.Pattern;
/**
 *  3 page
 *  - login page
 *  - main weather + travel page
 *  - past history page
 */
public class WeatherDemoFrame extends JFrame implements PropertyChangeListener {

    private final GeocodingService geocodingService;
    private final ViewWeatherController weatherController;
    private final WeatherViewModel weatherViewModel;

    private final IteneraryViewModel itineraryViewModel;
    private final AddStopController addStopController;

    // for notes
    private final AddNoteToStopController addNoteController;
    private final NotesViewModel notesViewModel;
    private final ItineraryRepository itineraryRepository;
    private final String itineraryId;

    // Note
    private JTextArea noteArea;

    // get Mapbox Directions for data access
    private final RouteDataAccess routeDataAccess = new RouteDataAccess();

    // the order on layout：login / main / history
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Login page
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel loginErrorLabel;

    // Main page
    private JLabel welcomeLabel;
    private JTextField originField;         // 出发地
    private JTextField destinationField;
    private JTextArea currentWeatherArea;
    private JTextArea tipsArea;
    private JTextArea forecastArea;
    private JLabel errorLabel;
    private final java.util.Map<String, String> cityWeatherMap = new java.util.LinkedHashMap<>();

    // Travel Info
    private JLabel travelDistanceValueLabel;
    private JLabel travelTimeValueLabel;

    // Stops list
    private DefaultListModel<String> stopListModel;
    private JList<String> stopList;
    private JTextField stopField;

    // start date
    private JTextField startDateField;
    private String currentStartDate = "";

    // 如果你已经有 SetStartDateController，就保留；否则可以先注释掉
    private final SetStartDateController setStartDateController;

    // History page
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private JList<String> historyList;

    private String currentUser = "";
    private String mainDestination = null;

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

        super("TravelPath – Weather Demo");
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
        this.weatherViewModel.addPropertyChangeListener(this);
        this.itineraryViewModel.addPropertyChangeListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 520);
        setLocationRelativeTo(null);

        buildUi();
        setContentPane(cards);
    }

    /** put login main history together */
    private void buildUi() {
        cards.add(buildLoginPanel(), "login");
        cards.add(buildMainPanel(), "main");
        cards.add(buildHistoryPanel(), "history");

        cardLayout.show(cards, "login");
    }

    // Login page
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("TravelPath – Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);

        c.gridx = 0; c.gridy = 0;
        center.add(new JLabel("Username:"), c);
        c.gridx = 1;
        center.add(usernameField, c);

        c.gridx = 0; c.gridy = 1;
        center.add(new JLabel("Password:"), c);
        c.gridx = 1;
        center.add(passwordField, c);

        JButton loginButton = new JButton("Log in");
        loginButton.addActionListener(e -> onLogin());
        c.gridx = 1; c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        center.add(loginButton, c);

        panel.add(center, BorderLayout.CENTER);

        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setForeground(Color.RED);
        panel.add(loginErrorLabel, BorderLayout.SOUTH);

        return panel;
    }

    //  Main page
    private JPanel buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // top part: Welcome + origin/destination + stops
        JPanel top = new JPanel(new BorderLayout(5, 5));
        welcomeLabel = new JLabel("Welcome!", SwingConstants.LEFT);
        top.add(welcomeLabel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // First line：Origin + Destination + Get weather + Past history
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        searchPanel.add(new JLabel("Origin:"));
        originField = new JTextField(10);
        searchPanel.add(originField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField(10);
        searchPanel.add(destinationField);


        JButton getWeatherButton = new JButton("Get weather");
        getWeatherButton.addActionListener(e -> onGetWeather());
        searchPanel.add(getWeatherButton);

        JButton historyButton = new JButton("Past history");
        historyButton.addActionListener(e -> cardLayout.show(cards, "history"));
        searchPanel.add(historyButton);

        inputPanel.add(searchPanel);

        // second line ：Add stop
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JLabel startDateLabel = new JLabel("Start date (YYYY-MM-DD):");
        startDateField = new JTextField(10);
        datePanel.add(startDateLabel);
        datePanel.add(startDateField);
        inputPanel.add(datePanel);

        JPanel stopInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        stopInputPanel.add(new JLabel("Add stop:"));

        stopField = new JTextField(15);
        stopInputPanel.add(stopField);

        JButton addStopButton = new JButton("Add stop");
        addStopButton.addActionListener(e -> onAddStop());
        stopInputPanel.add(addStopButton);

        inputPanel.add(stopInputPanel);

        // third line：Stops list + Note editing
        stopListModel = new DefaultListModel<>();
        stopList = new JList<>(stopListModel);
        stopList.setVisibleRowCount(3);
        stopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stopList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onStopSelected();
            }
        });

        JPanel stopsPanel = new JPanel(new BorderLayout(5, 0));
        stopsPanel.add(new JLabel("Stops:"), BorderLayout.NORTH);
        stopsPanel.add(new JScrollPane(stopList), BorderLayout.CENTER);

        // Note editing
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);

        JButton saveNoteButton = new JButton("Save note");
        saveNoteButton.addActionListener(e -> onSaveNote());

        JButton removeStopButton = new JButton("Remove selected");
        removeStopButton.addActionListener(e -> onRemoveSelected());

        JPanel noteButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        noteButtons.add(saveNoteButton);
        noteButtons.add(removeStopButton);

        JPanel notePanel = new JPanel(new BorderLayout(5, 2));
        notePanel.add(new JLabel("Note for selected stop:"), BorderLayout.NORTH);
        notePanel.add(noteScroll, BorderLayout.CENTER);
        notePanel.add(noteButtons, BorderLayout.SOUTH);

        stopsPanel.add(notePanel, BorderLayout.SOUTH);

        inputPanel.add(stopsPanel);

        top.add(inputPanel, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        //Current / Tips / Forecast
        currentWeatherArea = new JTextArea(12, 26);
        currentWeatherArea.setEditable(false);
        tipsArea = new JTextArea(12, 26);
        tipsArea.setEditable(false);
        forecastArea = new JTextArea(12, 26);
        forecastArea.setEditable(false);

        JPanel center = new JPanel(new GridLayout(1, 3, 8, 0));
        center.add(wrapTextArea("Current weather", currentWeatherArea));
        center.add(wrapTextArea("Clothing tips", tipsArea));
        center.add(wrapTextArea("7-day forecast", forecastArea));

        panel.add(center, BorderLayout.CENTER);

        // Travel Info + What is wrong
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JPanel travelInfoPanel = buildTravelInfoPanel();
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(travelInfoPanel, BorderLayout.CENTER);
        bottom.add(errorLabel, BorderLayout.SOUTH);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel wrapTextArea(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTravelInfoPanel() {
        JPanel travelPanel = new JPanel(new GridLayout(2, 2, 8, 4));
        travelPanel.setBorder(BorderFactory.createTitledBorder("Travel Info"));

        JLabel distanceLabel = new JLabel("Total distance:");
        JLabel timeLabel = new JLabel("Total time:");

        travelDistanceValueLabel = new JLabel("0.0 km");
        travelTimeValueLabel = new JLabel("0 min");

        travelPanel.add(distanceLabel);
        travelPanel.add(travelDistanceValueLabel);
        travelPanel.add(timeLabel);
        travelPanel.add(travelTimeValueLabel);

        return travelPanel;
    }

    // History page
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Past weather history", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title, BorderLayout.NORTH);

        historyList = new JList<>(historyModel);
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);

        JButton backButton = new JButton("Back to main");
        backButton.addActionListener(e -> cardLayout.show(cards, "main"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(backButton);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // logic for app

    /** login button */
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            loginErrorLabel.setText("Please enter a username.");
            return;
        }

        int MIN_PASSWORD_LENGTH = 6;
        if (!checkForPassword(password, MIN_PASSWORD_LENGTH)) {
            loginErrorLabel.setText(
                    "Password must be at least " + MIN_PASSWORD_LENGTH +
                            " characters and contain upper, lower case letters and a digit."
            );
            return;
        }

        currentUser = username;
        welcomeLabel.setText("Welcome, " + currentUser + "!");
        loginErrorLabel.setText(" ");
        cardLayout.show(cards, "main");
    }

    /** Add stop：use AddStop use case */
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

        addStopController.execute(city);     // 走 use case
        stopField.setText("");
        errorLabel.setText("Adding stop: " + city + " ...");
    }

    /** Remove selected ：delete the chosen stop from ViewModel and update  */
    private void onRemoveSelected() {
        int index = stopList.getSelectedIndex();
        if (index < 0) {
            errorLabel.setText("Please select a stop to remove.");
            return;
        }

        java.util.List<ItineraryStop> currentStops = itineraryViewModel.getStops();
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

    /** when select stop，show its note */
    private void onStopSelected() {
        int index = stopList.getSelectedIndex();
        if (index < 0) {
            if (noteArea != null) {
                noteArea.setText("");
            }
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

    /** click on Save note：get AddNoteToStop use case，update UI and repo */
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

        try {
            // store the stops now into Itinerary than into repo
            Itinerary itinerary = itineraryRepository.findById(itineraryId);
            if (itinerary == null) {
                itinerary = new Itinerary(itineraryId, null, stops);
            } else {
                // give back the stop city list
                itinerary.getStops().clear();
                itinerary.getStops().addAll(stops);
            }
            itineraryRepository.save(itinerary);

            // use use case
            addNoteController.addOrUpdateNote(itineraryId, stop.getId(), noteText);

            // check error of Presenter / NotesViewModel
            if (notesViewModel.hasError()) {
                errorLabel.setText(notesViewModel.getErrorMessage());
                return;
            }

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

        // start date
        currentStartDate = "";
        if (!startDateText.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(startDateText); // 默认格式：YYYY-MM-DD

                currentStartDate = startDateText; //

                if (setStartDateController != null) {
                    // SetStartDate use case，save it to itinerary
                    setStartDateController.setStartDate(parsedDate);
                }
            } catch (DateTimeParseException e) {
                errorLabel.setText("Start date must be in format YYYY-MM-DD.");
                return; // the format of the date isnt correct show error
            }
        }

        mainDestination = dest;
        currentWeatherArea.setText("");

        // new search clear cityweather map
        cityWeatherMap.clear();

        try {
            //  geocode origin & destination
            GeocodingService.LatLon destCoords = geocodingService.geocode(dest);
            GeocodingService.LatLon originCoords = geocodingService.geocode(originCity);

            // show the weather of destination
            weatherController.viewWeather(destCoords.getLat(), destCoords.getLon(), dest);

            // show the weather of all the stops cities
            List<ItineraryStop> stops = itineraryViewModel.getStops();
            if (stops != null) {
                for (ItineraryStop s : stops) {
                    weatherController.viewWeather(
                            s.getLatitude(),
                            s.getLongitude(),
                            s.getName()
                    );
                }
            }

            // calculate route：Origin -> stops -> Destination
            java.util.List<ItineraryStop> routeStops = new java.util.ArrayList<>();

            routeStops.add(new ItineraryStop(
                    "origin", originCity,
                    originCoords.getLat(), originCoords.getLon(), ""
            ));
            if (stops != null && !stops.isEmpty()) {
                routeStops.addAll(stops);
            }
            routeStops.add(new ItineraryStop(
                    "destination", dest,
                    destCoords.getLat(), destCoords.getLon(), ""
            ));

            RouteInfo routeInfo = routeDataAccess.getRoute(routeStops);

            if (routeInfo.getDistance() <= 0.0) {
                updateTravelInfo("No route found", "No route found");
            } else {
                String distanceText = String.format("%.1f km", routeInfo.getDistance());
                String timeText = String.format("%.0f min", routeInfo.getDurationMinutes());
                updateTravelInfo(distanceText, timeText);
            }

        } catch (Exception ex) {
            errorLabel.setText("Geocoding or directions failed: " + ex.getMessage());
            updateTravelInfo("N/A", "N/A");
        }
    }


    //  ViewModel -> UI update

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object src = evt.getSource();

        if (src == weatherViewModel) {
            String name = evt.getPropertyName();

            // Success：present()  setForecastText trigger once
            // failed：presentError() setErrorMessage trigger once
            if ("forecastText".equals(name) || "errorMessage".equals(name)) {
                handleWeatherModelChange();
            }

        } else if (src == itineraryViewModel) {
            handleItineraryModelChange();
        }
    }

    /** WeatherViewModel update weather and history */
    private void handleWeatherModelChange() {
        String destination = weatherViewModel.getDestination();
        String currentText = weatherViewModel.getCurrentText();
        String tipsText = weatherViewModel.getTipsText();
        String forecastText = weatherViewModel.getForecastText();
        String error = weatherViewModel.getErrorMessage();

        // if there is error
        errorLabel.setText((error == null || error.isEmpty()) ? " " : error);
        if (error != null && !error.isEmpty()) {
            return;
        }

        if (destination == null || destination.isEmpty()
                || currentText == null || currentText.isEmpty()) {
            return;
        }

        // check if this is destination
        boolean isMain = destination.equalsIgnoreCase(mainDestination);

        // check if its stop，or stop's note
        boolean isStop = false;
        String noteForCity = "";
        java.util.List<ItineraryStop> stops = itineraryViewModel.getStops();
        if (stops != null) {
            for (ItineraryStop s : stops) {
                if (s.getName().equalsIgnoreCase(destination)) {
                    isStop = true;
                    if (s.getNotes() != null) {
                        noteForCity = s.getNotes();
                    }
                    break;
                }
            }
        }

        // update map，rebuild Current weather
        cityWeatherMap.put(destination, currentText);

        StringBuilder currentSb = new StringBuilder();
        for (java.util.Map.Entry<String, String> entry : cityWeatherMap.entrySet()) {
            currentSb.append(entry.getKey())
                    .append(":\n")
                    .append(entry.getValue())
                    .append("\n\n");
        }
        currentWeatherArea.setText(currentSb.toString());

        if (isMain) {
            destinationField.setText(destination);
            tipsArea.setText(tipsText != null ? tipsText : "");
            forecastArea.setText(forecastText != null ? forecastText : "");
        }

        // history line：weather + tip + note + start date

        String weatherSummary = currentText.replace('\n', ' ');

        // tip
        String tipSummary = "";
        if (tipsText != null && !tipsText.isEmpty()) {
            String[] lines = tipsText.split("\\R");
            if (lines.length > 0) {
                tipSummary = lines[0].replace("•", "").trim();
            }
        }

        String noteSummary = (noteForCity == null) ? "" : noteForCity.trim();

        String labelPrefix = "";
        if (isMain) {
            labelPrefix = "[Destination] ";
        } else if (isStop) {
            labelPrefix = "[Stop] ";
        }

        //  Add a start date symbol storing in the history if there is a start date
        String datePrefix = "";
        if (startDateField != null) {
            String startDateText = startDateField.getText().trim();
            if (!startDateText.isEmpty()) {
                datePrefix = "[Start " + startDateText + "] ";
            }
        }

        StringBuilder historyLine = new StringBuilder();
        historyLine.append(datePrefix)          // <<< 新加这一行
                .append(labelPrefix)
                .append(destination)
                .append(" — ")
                .append(weatherSummary);

        if (!tipSummary.isEmpty()) {
            historyLine.append(" | Tips: ").append(tipSummary);
        }
        if (!noteSummary.isEmpty()) {
            historyLine.append(" | Note: ").append(noteSummary);
        }

        String historyStr = historyLine.toString();
        // if same as last one dont add again
        int size = historyModel.getSize();
        if (size == 0 || !historyStr.equals(historyModel.getElementAt(size - 1))) {
            historyModel.addElement(historyStr);
        }
    }




    /**
     * when update Itenerary ViewModel: update stops list（with note ）
     */
    private void handleItineraryModelChange() {
        String err = itineraryViewModel.getError();
        if (err != null && !err.isEmpty()) {
            errorLabel.setText(err);
        }

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

    /** update Travel Info  */
    private void updateTravelInfo(String distanceText, String timeText) {
        if (travelDistanceValueLabel != null) {
            travelDistanceValueLabel.setText(distanceText);
        }
        if (travelTimeValueLabel != null) {
            travelTimeValueLabel.setText(timeText);
        }
    }

    // check if password follow the rules

    private static boolean checkForPassword(String str, int minLength) {
        if (str == null || str.length() < minLength) {
            return false;
        }
        boolean hasLow = Pattern.matches(".*[a-z].*", str);
        boolean hasUp = Pattern.matches(".*[A-Z].*", str);
        boolean hasDigit = Pattern.matches(".*\\d.*", str);
        return hasLow && hasUp && hasDigit;
    }
}
