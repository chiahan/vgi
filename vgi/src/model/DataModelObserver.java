package model;

import event.UserEventConstants;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class is used to nodify the observers of DataModel, please reffer to
 * MVC design pattern.
 *
 * @author rasielchang
 */

public interface DataModelObserver {
    
    /**
     * Use to add state.          
     * 
     * @author rasielchang
     */
    void updateDataModel(int stateID, String stateName, Point2D.Float point);

    /**
     * Use to add transition.
     *
     * @author rasielchang
     */
    void updateDataModel(int transitionID, int sourceStateID, int destinationStateID, String label, int lineType);
    
    /**
     * Use to add transition with some data.
     *
     * @author rasielchang
     */
    void updateDataModel(int transitionID, int sourceStateID, int destinationStateID, String label, int lineType, ArrayList points);    

    /**
     * Use to do one kind of UserEvent on the given cell without other data, the UserEvent is defined in
     * UserEventConstants.
     *
     * @author rasielchang
     */
    void updateDataModel(UserEventConstants event, int cellID);    

    /**
     * Use to do one kind of UserEvent on the given cell with Point2D's data, the UserEvent is defined in
     * UserEventConstants.
     *
     * @author rasielchang
     */
    void updateDataModel(UserEventConstants event, int cellID, Point2D.Float point);    

    /**
     * Use to do one kind of UserEvent on the given cell with String data, the UserEvent is defined in
     * UserEventConstants.
     *
     * @author rasielchang
     */
    void updateDataModel(UserEventConstants event, int cellID, String data);

    /**
     * Use to do one kind of UserEvent on the given cell with ArrayList data, the UserEvent is defined in
     * UserEventConstants.
     *
     * @author rasielchang
     */
    void updateDataModel(UserEventConstants event, int cellID, ArrayList data);    

    /**
     * Use to do one kind of UserEvent on the given cell with boolean data, the UserEvent is defined in
     * UserEventConstants.
     *
     * @author rasielchang
     */
    void updateDataModel(UserEventConstants event, int cellID, boolean data);
    
}
