package csc207_group;

import ui.WeatherDemoApp;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherDemoApp().start();
        });
    }
}


