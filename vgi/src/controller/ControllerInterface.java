package controller;


import event.UIEvent;
import java.awt.geom.Point2D;
import model.Automata2.Label;
import org.jgraph.JGraph;

/**
 * This is the interface of controller.
 *
 *
 * @author rasielchang
 */
public interface ControllerInterface {

    /**
    * Use to get JGraph content.
    *
    * @return JGraph content
    *
    * @author rasielchang
    */
    JGraph getGraph();

    /**
    * Use to handle the event which is got from CanvasView.
    *
    * @param event contains informations about this event.
    *
    * @author rasielchang
    */
    void canvasActionPerformed(UIEvent event);

    /**
    * Use to handle the event which is got from Storage.
    *
    * @param event contains informations about this event.
    *
    * @author rasielchang
    */
    void storageActionPerformed(UIEvent event);

    /**
    * Use to check if data in DataModel or CanvasView have been changed.
    *
    * @return true means has been modified, otherwise has not.
    *
    * @author rasielchang
    */
    boolean isModify();

    /**
    * Use to save the xml file.
    *
    * @author rasielchang
    */
    void saveFile();

    /**
    * Use to handle the event of mouse click on the canvas.
    *
    * @param stateID indicates if it is clicked on a state, if it is, the value is
    *        the id of this state.
    *
    * @param transitionID indicates if it is clicked on a transition, if it is, the value is
    *        the id of this state.
    *
    * @param point indicates the coordinate on the canvas.
    *
    * @author rasielchang
    */
    void mouseClicked(int stateID, int transitionID, Point2D.Float point);

    /**
    * Use to set the label of a transition.
    *
    * @param transitionID indicates which transition is selected.
    *
    * @param Label is the new label of this transition.
    *
    * @param text is the string version of the param label.
    *
    * @author rasielchang
    */
    void setTransitionLabel(int transitionID, Label label, String text);
    
}
