package view.main;


import controller.ControllerInterface;
import debug.Debug;
import event.UserEventConstants;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import model.DataModelInterface;
import model.DataModelObserver;
import org.jgraph.JGraph;
import view.cellview.CellViewConstants;
import view.jgraph.JGraphController;
import event.UIEvent;
import java.util.Vector;
import model.Automata2;
import model.Automata2.Label;
import model.TypeConstants;


/**
 * This class is used to display the canvas.
 * 
 * @author rasielchang
 */
public class CanvasView implements DataModelObserver {

    DataModelInterface model;
    ControllerInterface controller;
    JGraphController jgraphController;
    JGraph graph;

    /**
     * The constructor.
     *
     * @param controller 
     *
     * @param model
     *
     * @author rasielchang
     */
    public CanvasView(ControllerInterface controller, DataModelInterface model) {
        this.controller = controller;
        this.model = model;
        graph = new JGraph();
        jgraphController = new JGraphController(graph,this);
        model.registerObserver((DataModelObserver) this);
    /**/
    }
    
    public JGraph getGraph() {
        graph = jgraphController.getGraph();
        return graph;
    }

    public void updateDataModel(int stateID, String stateName, Point2D.Float point) {
        if(model != null) {
            jgraphController.insertState(stateID, stateName, point);
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }

    public void updateDataModel(int transitionID, int sourceStateID, int destinationStateID, String label, int type) {
        if(model != null) {
            switch(type) {
                case TypeConstants.STRAIGHT_LINE:
                    jgraphController.insertStraightTransition(sourceStateID, destinationStateID, transitionID, label);
                break;               
                case TypeConstants.CURVE:
//                    jgraphController.insertCurveTransition(sourceStateID, destinationStateID, transitionID, pointList, label);
                    //弧線應該不用給點吧
                break;
                case TypeConstants.SPLINE_LINE:
                    //這邊要去呼叫jgraph加入spline transition的函式
                break;
            }           
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }// add transition
    
    public void updateDataModel(int transitionID, int sourceStateID, int destinationStateID, String label, int type, ArrayList points) {
        if(model != null) {
            switch(type) {
                case TypeConstants.CURVE:
                    jgraphController.insertCurveTransition(sourceStateID, destinationStateID, transitionID, points, label);
                break;
                case TypeConstants.POLY_LINE:
                    points.add(0, new Point2D.Double(0, 0));
                    points.add(new Point2D.Double(0, 0));
                    jgraphController.insertPolyTransition(sourceStateID, destinationStateID, transitionID, points, label);
                break;
                case TypeConstants.BEZIER_LINE:
                    points.add(0, new Point2D.Double(0, 0));
                    points.add(new Point2D.Double(0, 0));
                    jgraphController.insertBezierTransition(sourceStateID, destinationStateID, transitionID, points, label);
                break;
            }
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }

    public void updateDataModel(UserEventConstants event, int cellID) {
        if (model != null) {
            switch(event) {
                case DELETE_STATE:
                    jgraphController.removeState(cellID);
                break;
                case DELETE_TRANSITION:
                    jgraphController.removeTransition(cellID);
                break;
                case SET_STRAIGHT_TRANSITION:
                    jgraphController.setTransitionStyle(cellID, TypeConstants.STRAIGHT_LINE);
                break;
                case SET_CURVE_TRANSITION:
                    jgraphController.setTransitionStyle(cellID, TypeConstants.CURVE);
                break;
                default:
                    Debug.echo("CanvasView: no this event");
            }
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }

    public void updateDataModel(UserEventConstants event, int cellID, Point2D.Float point) {
        if (model != null) {
            switch(event) {
                case CHANGE_STATE_POSITION:
                    jgraphController.setStatePosition(cellID, point.getX(), point.getY());
                break;

            }
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }

    public void updateDataModel(UserEventConstants event, int cellID, String data) {
        if (model != null) {
            switch(event) {
                case CHANGE_STATE_NAME:
                    jgraphController.setStateName(cellID, data, false);
                break;
                case CHANGE_TRANSITION_LABEL:
                    jgraphController.setTransitionLabel(cellID, data);
                break;
                    
            }
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }
    
    public void updateDataModel(UserEventConstants event, int cellID, ArrayList data) {
        if (model != null) {
            switch(event) {
                case SET_POLY_TRANSITION:
                    data.add(0, new Point2D.Double(0, 0));
                    data.add(new Point2D.Double(0, 0));
                    jgraphController.setTransitionStyle(cellID, TypeConstants.POLY_LINE);
                    jgraphController.setTransitionControlPoint(cellID, data);
                break;
                case SET_BEZIER_TRANSITION:
                    data.add(0, new Point2D.Double(0, 0));
                    data.add(new Point2D.Double(0, 0));
                    jgraphController.setTransitionStyle(cellID, TypeConstants.BEZIER_LINE);
                    jgraphController.setTransitionControlPoint(cellID, data);
                break;
            }
        }
        else {
            Debug.echo("Model doesn't exist");
        }
    }
    
    public void updateDataModel(UserEventConstants event, int cellID, boolean data) {
        if(model != null) {
            switch(event) {
                case SET_INITIAL_STATE:
                    jgraphController.changeStateStyle(cellID, true, data);
                break;
                case SET_FINAL_STATE:
                    jgraphController.changeStateStyle(cellID, data, true);
                break;
                case REMOVE_INITIAL_STATE:
                    jgraphController.changeStateStyle(cellID, false, data);
                break;
                case REMOVE_FINAL_STATE:
                    jgraphController.changeStateStyle(cellID, data, false);
                break;
                default:
                    Debug.echo("CanvasView: no this event");
            }
        }
    }

    /**
     * Use to pass the event to the controller.
     *
     * @event is passed from JGraphController
     *
     * @author rasielchang
     */
    public void eventHappen(UIEvent event) {
        controller.canvasActionPerformed(event);
    }

 
    public Vector getSelectedStateID() {
        Vector cells = jgraphController.getSelectedCellsID();
        switch((Integer)cells.remove(0)) {
            case -1:
                return null;
            case -2:
                return cells;
            case -4:
                int index = cells.indexOf(-4);
                cells.setSize(index);
                return cells;
            default:
                return null;
        }
    }

    
    public Vector getSelectedTransitionID() {
        Vector cells = jgraphController.getSelectedCellsID();
        switch((Integer)cells.remove(0)) {
            case -1:
                return null;
            case -3:
                return cells;
            case -4:
                int index = cells.indexOf(-4);
                Vector transitions = new Vector();
                int temp;
                for(int i = index + 1; i < cells.size(); i++) {
                    temp = (Integer)cells.get(i);
                    transitions.add(temp);
                }
                return transitions;
            default:
                return null;
        }
    }
    
    public boolean isCellsSelected() {
        Vector cells = jgraphController.getSelectedCellsID();
        if((Integer)cells.get(0) == -1) {
            return false;
        }
        else {
            return true;
        }
    }
    
    public void moveStatePosition(int stateID) {
        /*jgraph will handle this, just have to get the position at the end*/
    }// End of CanvasView.moveStatePosition
    
    public void virtualTransitionRemove() {
        jgraphController.setNewTransitionEffect(-1);
    }
    
    public void virtualTransition(int stateID, int type) {
        jgraphController.setNewTransitionLineType(type);
        jgraphController.setNewTransitionEffect(stateID);
    }
    
    public void virtualTransition(int stateID, int type, ArrayList points) {
        jgraphController.setNewTempTransitionControlPoint(points);
        jgraphController.setNewTransitionLineType(type);
        jgraphController.setNewTransitionEffect(stateID);
    }

    public void setFisheyeMode(boolean flag) {
	jgraphController.setFisheyeMode(flag);
    }
    
    public void setLabelOption(Automata2.ValueType vt) {
        jgraphController.setValueType(vt);
    }
    
    public void zoom(double data) {
        jgraphController.setScale(data);
    }
    
    public void mouseClicked(int stateID, int transitionID, Point2D.Float point) {
        controller.mouseClicked(stateID, transitionID, point);
    }

    public void hierarchicalLayout2() {
        jgraphController.hierarchicalLayout2();
    }
    
    public void actualSize() {
        jgraphController.actualSize();
    }
    
    public void fitWindow() {
        jgraphController.fitWindow();
    }
    
    public void springLayout() {
        //jgraphController.springLayout();
    }
    
    public void circleLayout() {
        jgraphController.circleLayout();
    }
    
    public void testLayout() {
//        jgraphController.testLayout();
    }
    
    public void fastOrganic() {
        //jgraphController.fastOrganic();
    }
    
    public void organic() {
        //jgraphController.organic();
    }
    
    public void selfOrganizing() {
//        jgraphController.selfOrganizing();
    }
    
    public void setTrasitionLabel(int transitionID, Label label, String text) {
        controller.setTransitionLabel(transitionID, label, text);
    }
    
    public void cleanTransitionControlPoint() {
        jgraphController.cleanTransitionContorlPoint();
    }
    
    public void updateLoopInitialFinal() {
        jgraphController.updateLoopInitalFinal();
    }
}

              
