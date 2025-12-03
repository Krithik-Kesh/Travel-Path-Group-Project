
---

# TravelPath – Smart Itinerary Planner

TravelPath is a desktop application designed to streamline the travel planning process. Users can build multi-city itineraries, calculate travel routes, check real-time weather, receive packing suggestions, and manage trip notes — all in one interface.

---

## Table of Contents

* [Team Information](#team-information)
* [App Introduction & Objectives](#app-introduction--objectives)
* [Main Features](#main-features)
* [Technology Stack](#technology-stack)
* [User Guide](#user-guide)
* [Visuals & Interface](#visuals--interface)
* [Documentation & Diagrams](#documentation--diagrams)
* [Future Plans](#future-plans)
* [References](#references)

---

## Team Information

| Name            | Role / Contributions                                                              |
|-----------------|-----------------------------------------------------------------------------------|
| Krithik         | Add Stop, Google Geolocation API, Route Calculation Logic                         |
| Steven (Yi Zhu) | Weather & Forecast, OpenWeatherMap API, Clothing/Packing Suggestions |
| Steven Zhan     | Notes per stop, Checkstyle Implementation, Presentation template                  |
| Ethan           | Save Itinerary, History Repository, JSON Persistence                              |
| Mattias         | Reorder & Delete Stops, UI Components                                             |
| Roger He        | Set Start Date, Data Logic                                                        |

---

## App Introduction & Objectives

Planning multi-city trips usually requires switching between several different tools. TravelPath solves this by integrating routing, weather data, and note-taking into a single clean interface.

**Objective:**
Build a compliant Java application that aggregates navigation, weather, and planning data into a unified Travel Record—incorporated with CLEAN Architecture Principles.

---

## Main Features

### Multi-Stop Routing

* Add unlimited stops (e.g., Toronto → Montreal → Vancouver)
* Automatically calculates total distance and estimated travel time

### Real-Time Weather

* Shows current weather conditions
* Includes 7-day forecasts for every stop

### Smart Packing Suggestions

* Provides clothing recommendations based on forecasted temperatures and conditions

### Itinerary Management

* Add or remove stops
* Attach notes to stops
* Set a trip start date

### Data Persistence

* Save itineraries locally in JSON format
* Load past trip histories

### Secure Login

* Enforces password strength validation

---

## Technology Stack

* **Language:** Java 17+
* **Architecture:** Clean Architecture (Entity, Use Case, Interface Adapter, View)
* **GUI:** Java Swing (GridBagLayout, CardLayout)
* **APIs:**

  * Google Maps Geocoding API
  * Mapbox Directions API
  * OpenWeatherMap API
* **Storage:** JSON (org.json)
* **Testing:** JUnit 5

---

## User Guide

### 1. Prerequisites

* JDK 17 or higher
* IntelliJ IDEA (recommended)
* API keys for Google Maps, Mapbox, and OpenWeather

### 2. Installation & Setup

**Clone the Repository:**

```bash
git clone https://github.com/Krithik-Kesh/Travel-Path.git
```

**Create a `.env` file at the project root:**

```
OPENWEATHER_API_KEY=your_key_here
GOOGLE_API_KEY=your_key_here
DIRECTIONS_TOKEN=your_mapbox_token_here
```

**Run the Application:**

* Open `src/main/java/csc207_group/Main.java`
* Run the `Main` method

### 3. How to Use

1. Log in using a username and a valid password
2. Enter an Origin and Destination
3. Add stops using the "Add City" field
4. Click **Get Forecast & Route** to generate route and weather data
5. Click **Save History** to store your itinerary locally

---

## Visuals & Interface

### Login Screen

<img width="2002" height="1394" alt="image" src="https://github.com/user-attachments/assets/56ca465f-9547-4a9a-b2ef-3dc1dd24569e" />


### Main Planning Dashboard

<img width="1998" height="1392" alt="image" src="https://github.com/user-attachments/assets/7660247c-14bb-42fa-88b1-3754a87c026f" />

---

## Documentation & Diagrams

* Presentation slides: https://docs.google.com/presentation/d/18HwkptpcBJpO5bKsqpVr571G4_BbCMb_lfy6TvBvByI/edit?usp=sharing

---

## Future Plans

* Add interactive map rendering using JxBrowser or Static Maps API
* Move from JSON storage to cloud DB (Firebase, MongoDB)
* Integrate flight search features (Skyscanner API)
* Export itineraries as PDF or send via email

---

## References
* Google Geocoding API Documentation
* Mapbox Directions API Documentation
* OpenWeatherMap API Documentation

---
