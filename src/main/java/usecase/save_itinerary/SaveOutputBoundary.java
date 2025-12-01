package use_case.save_itinerary;
import use_case.save_itinerary.SaveOutput;
public interface SaveOutputBoundary {

    void present(SaveOutput output);

    void prepareFailView(String error);
}
