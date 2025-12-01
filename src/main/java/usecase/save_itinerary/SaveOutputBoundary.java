package usecase.save_itinerary;
import usecase.save_itinerary.SaveOutput;
public interface SaveOutputBoundary {

    void present(SaveOutput output);

    void prepareFailView(String error);
}
