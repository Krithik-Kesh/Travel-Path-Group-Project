package usecase;

import entity.Itinerary;

/**
 * Repository interface for accessing and saving itinerary objects.
 *
 * @since 1.0
 */

public interface ItineraryRepository {
    /**
     * Finds an itinerary by its unique identifier.
     *
     * @param id the identifier of the itinerary to retrieve
     * @return the itinerary object associated with the given id, or null if not found
     * @since 1.0
     */

    Itinerary findById(String id);

    /**
     * Saves the given itinerary to the repository.
     *
     * @param itinerary the itinerary object to save
     * @since 1.0
     */

    void save(Itinerary itinerary);
}
