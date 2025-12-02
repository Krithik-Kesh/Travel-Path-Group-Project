/* File description: Interface between Interactor and Presenter. This file
contains two methods, the functionality of each method will be explained
in their docstrings.

Additional note: Presenter implements this Boundary (Interface)
 */

package use_case.SetStartDate;

public interface  SetStartDateOutputBoundary {
    /* Interface, methods do not have implementation
     */

    // Called when itinerary start date is successfully saved
    void prepareSuccessView(SetStartDateOutputData outputData);

    // Called when itinerary start date is not successfully saved
    void prepareFailView(String errorMessage);
}
