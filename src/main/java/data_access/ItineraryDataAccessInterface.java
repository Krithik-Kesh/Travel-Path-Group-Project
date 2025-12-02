package data_access;

import entity.Itinerary;

public interface ItineraryDataAccessInterface {
    // interface which defines methods used by Use Case SetStartDate
    // and define methods used in remaining Itinerary related Use Cases


    // Get itinerary by calling it with username and the itinerary ID
    Itinerary getItinerary(String username, String itineraryId);
    // Return corresponding itinerary if found, null o.w.

    // Save corresponding itinerary if found
    void save(String username, String itineraryId, Itinerary itinerary);

}
