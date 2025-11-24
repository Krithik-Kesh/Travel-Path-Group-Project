/* File Description: Interface between Controller and Interactor.
 */

package use_case.SetStartDate;

public interface SetStartDateInputBoundary {
    /* Called my Controller, implemented by the Interactor
     */

    void setStartDate(SetStartDateInputData inputData);
}