package controller;

import debug.Debug;
import event.UserEventConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.main.CanvasView;
import event.UIEvent;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.jgraph.JGraph;
import model.DataModelInterface;
import layout_algorithm.*;
import model.Automata2;
import model.Automata2.*;
import model.DataModel;
import model.Drawing2;
import storage.XMLReader;
import model.Geometry2;
import model.TypeConstants;
import storage.Storage;
import storage.XMLCreator;
import vjni.Vjni;

 /**
 * This class is used to control the VGI.
 *
 * @author rasielchang
 */
public class Controller implements ControllerInterface {

    Storage storage;
    DataModelInterface model;
    CanvasView view;
    Stack preEvent;
    Stack nextEvent;
    boolean modify;
    boolean finish;
    boolean isxml;
    CircleLayout circleLayout;
    GridLayout gridLayout;
    SpringLayout springLayout;
    double viewScale;
    ModeConstants currentMode = ModeConstants.SELECTION_MODE;
    LabelParser parser;
    Vjni vjni = new Vjni();

    /**
    * The constructor of controller.
    *
    * @author rasielchang
    */
    public Controller() {
        model = new DataModel();
        view = new CanvasView(this, model);
        preEvent = new Stack();
        nextEvent = new Stack();
        modify = true;
        isxml = false;
        viewScale = 1;
    }

    /**
    * The constructor of controller.
    *
    * @param storage is the main window of VGI.
    *
    * @param model is a interface of DataModel which is used to store the
    *        information of automata
    *
    * @author rasielchang
    */
    public Controller(Storage storage, DataModelInterface model) {
        this.storage = storage;
        this.model = model;
        view = new CanvasView(this, model);
        model.initialize();
        preEvent = new Stack();
        nextEvent = new Stack();
        modify = true;
        isxml = false;
        viewScale = 1;
    }

    /**
    * Use to get DataModel.
    *
    * @return the model which is controled by this controller.
    *
    * @author rasielchang
    */
    public DataModelInterface getDataModel() {
        return model;
    }

    /**
    * Use to get the JGraph.
    *
    * @return the JGraph which is in CanvasView.
    *
    * @author rasielchang
    */
    public JGraph getGraph() {
        JGraph graph = view.getGraph();
        return graph;
    }

    /**
    * Use to set the new mode of controller.
    *
    * @param mode indicates the next mode.
    *
    * @author rasielchang
    */
    public void nextMode(ModeConstants mode) {
        unFinishEventPerformed();
        if (currentMode == mode) {
            currentMode = ModeConstants.SELECTION_MODE;
            view.setFisheyeMode(false);
        } else {
            currentMode = mode;
            switch (mode) {
                case ADD_MODE:
                    view.setFisheyeMode(false);
                    break;
                case CONNECT_MODE:;
                    view.setFisheyeMode(false);
                    break;
                case FISHEYE_MODE:
                    view.setFisheyeMode(true);
                    break;
            }
        }
    }

    /**
    * NOT FINISH!!
    *
    * @author rasielchang
    */
    private void undo(UIEvent pre) {
         /* TODO

        switch (pre.getEvent()) {
            case ADD_STATE:
                model.deleteState(pre.getiData());
                break;
            case ADD_TRANSITION_FROM:
                view.virtualTransitionRemove();
                break;
            case ADD_TRANSITION_CONTROL_POINT:
                UIEvent prepre = (UIEvent) preEvent.peek();
                if (prepre.getEvent() == UserEventConstants.ADD_TRANSITION_CONTROL_POINT) {
                    view.virtualTransition(prepre.getiData(), model.getLineType(), prepre.getaData());
                } else {
                    ArrayList points = new ArrayList();
                    points.add(new Point2D.Double(0, 0));
                    points.add(new Point2D.Double(0, 0));
                    view.virtualTransition(prepre.getStateID(), model.getLineType(), points);
                }
                break;
            case ADD_TRANSITION_TO:
                model.deleteTransition(pre.getiData());// The transitionID is stored in iData.
                break;
            case CHANGE_STATE_NAME:
                String stateName = pre.getsData();
                pre.setsData(model.getStateName(pre.getStateID()));
                model.changeStateName(pre.getStateID(), stateName);
                break;
            case DELETE_BUTTON:
                Vector<Integer> statesID = pre.getvData(0);
                Vector<Integer> transitionsID = pre.getvData(1);
                for (int i = 0; i < statesID.size(); i++) {
                    model.reAddState(statesID.get(i));
                }
                for (int i = 0; i < transitionsID.size(); i++) {
                    model.reAddTransition(transitionsID.get(i));
                }
                break;
            case CHANGE_STATE_POSITION:
                Point2D.Float tmp = model.getStatePosition(pre.getStateID());
                model.setStatePosition(pre.getStateID(), pre.getPoint());
                pre.setPoint(tmp);
                break;
            case SET_INITIAL_STATE:
                statesID = pre.getvData(0);
                Vector<Boolean> isFinals = pre.getvData(1);
                Vector<UserEventConstants> ev = pre.getvData(2);
                for (int i = 0; i < statesID.size(); i++) {
                    if (ev.get(i) == UserEventConstants.SET_INITIAL_STATE) {
                        model.removeInitialState(statesID.get(i), isFinals.get(i));
                    } else {
                        model.setInitialState(statesID.get(i), isFinals.get(i));
                    }
                }
                break;
            case SET_FINAL_STATE:
                statesID = pre.getvData(0);
                Vector<Boolean> isInitials = pre.getvData(1);
                ev = pre.getvData(2);
                for (int i = 0; i < statesID.size(); i++) {
                    if (ev.get(i) == UserEventConstants.SET_FINAL_STATE) {
                        model.removeFinalState(statesID.get(i), isInitials.get(i));
                    } else {
                        model.setFinalState(statesID.get(i), isInitials.get(i));
                    }
                }
                break;
            default:
                Debug.echo("Controller_undo: This event can't undo.");
        }
        nextEvent.add(pre);

         */
    }

    /**
    * NOT FINISH!!
    *
    * @author rasielchang
    */
    private void redo(UIEvent next) {
        /* TODO
        switch (next.getEvent()) {
            case ADD_STATE:
                model.reAddState(next.getiData());
                break;
            case ADD_TRANSITION_FROM:
                view.virtualTransition(next.getStateID(), model.getLineType());
                break;
            case ADD_TRANSITION_CONTROL_POINT:
                view.virtualTransition(next.getiData(), model.getLineType(), next.getaData());
                break;
            case ADD_TRANSITION_TO:
                model.reAddTransition(next.getiData());
                break;
            case CHANGE_STATE_NAME:
                String stateName = next.getsData();
                next.setsData(model.getStateName(next.getStateID()));
                model.changeStateName(next.getStateID(), stateName);
                break;
            case DELETE_BUTTON:
                Vector<Integer> statesID = next.getvData(0);
                Vector<Integer> transitionsID = next.getvData(1);
                for (int i = 0; i < statesID.size(); i++) {
                    model.deleteState(statesID.get(i));
                }
                for (int i = 0; i < transitionsID.size(); i++) {
                    model.deleteTransition(transitionsID.get(i));
                }
                break;
            case CHANGE_STATE_POSITION:
                Point2D.Float tmp = model.getStatePosition(next.getStateID());
                model.setStatePosition(next.getStateID(), next.getPoint());
                next.setPoint(tmp);
                break;
            case SET_INITIAL_STATE:
                statesID = next.getvData(0);
                Vector<Boolean> isFinals = next.getvData(1);
                Vector<UserEventConstants> ev = next.getvData(2);
                for (int i = 0; i < statesID.size(); i++) {
                    if (ev.get(i) == UserEventConstants.SET_INITIAL_STATE) {
                        model.setInitialState(next.getStateID(), next.getBooleanData());
                    } else {
                        model.removeInitialState(next.getStateID(), next.getBooleanData());
                    }
                }
                break;
            case SET_FINAL_STATE:
                statesID = next.getvData(0);
                Vector<Boolean> isInitials = next.getvData(1);
                ev = next.getvData(2);
                for (int i = 0; i < statesID.size(); i++) {
                    if (ev.get(i) == UserEventConstants.SET_FINAL_STATE) {
                        model.setFinalState(next.getStateID(), next.getBooleanData());
                    } else {
                        model.removeFinalState(next.getStateID(), next.getBooleanData());
                    }
                }
                break;         
            default:
                Debug.echo("Controller_redo: This event can't redo.");
        }
        preEvent.add(next);
        */
    }

    /**
    * Use to handle the event which is got from CanvasView.
    *
    * @param event contains informations about this event.
    *
    * @author rasielchang
    */
    @SuppressWarnings("empty-statement")
    public void canvasActionPerformed(UIEvent event) {
        switch (event.getEvent()) {
            case ADD_STATE:
                Point2D.Float point = event.getPoint();
                model.addState(point);
                event.setiData(model.getStateIDCounter());
                break;
            case CHANGE_STATE_NAME:
                String stateName = event.getsData();
                event.setsData(model.getStateName(event.getStateID()));
                model.changeStateName(event.getStateID(), stateName);
                break;
            case CHANGE_STATE_POSITION:
                if (event.getCellID() == -1) {
                    Debug.echo("can't find the state");
                } else {
                    Point2D.Float tmp = model.getStatePosition(event.getStateID());
                    model.setStatePosition(event.getCellID(), event.getPoint());
                    event.setPoint(tmp);
                }
                break;
            case CHANGE_TRANSITION_LABEL:
                parser.startParse(event.getTransitionID());
                break;
            case CHANGE_TRANSITION_WEIGHT_LABEL:
                Vector<String> v = new Vector();
                v = event.getvData(0);
                model.changeTransitionLabel(event.getTransitionID(), v.get(0), v.get(1));
                break;            
            case ADD_TRANSITION_FROM:
                switch (model.getLineType()) {
                    case TypeConstants.STRAIGHT_LINE:
                        view.virtualTransition(event.getStateID(), TypeConstants.STRAIGHT_LINE);
                        break;
                    case TypeConstants.POLY_LINE:
                        view.virtualTransition(event.getStateID(), TypeConstants.STRAIGHT_LINE);
                        break;
                    case TypeConstants.CURVE:
                        view.virtualTransition(event.getStateID(), TypeConstants.CURVE);
                        break;
                    case TypeConstants.BEZIER_LINE:
                        view.virtualTransition(event.getStateID(), TypeConstants.CURVE);
                        break;
                }
                event.notFinish();
                break;
            case ADD_TRANSITION_CONTROL_POINT:
                UIEvent pre = (UIEvent) preEvent.peek();
                if (pre.getaData() == null) {
                    ArrayList points = new ArrayList();
                    int stateID = pre.getStateID();
                    points.add(new Point2D.Double(0, 0));
                    points.add(event.getPoint());
                    points.add(new Point2D.Double(0, 0));
                    event.setaData(points);
                    event.setiData(stateID);
                    view.virtualTransition(event.getiData(), model.getLineType(), event.getaData());
                } else {
                    ArrayList points = new ArrayList();
                    points = (ArrayList) pre.getaData().clone();
                    int stateID = pre.getiData();
                    event.setiData(stateID);
                    points.set(points.size() - 1, event.getPoint());
                    points.add(new Point2D.Double(0, 0));
                    event.setaData(points);
                    view.virtualTransition(event.getiData(), model.getLineType(), event.getaData());
                }
                event.notFinish();
                break;
            case ADD_TRANSITION_TO:
                pre = (UIEvent) preEvent.pop();
                event.setiData(model.getTransitionIDCounter());
                switch (model.getLineType()) {
                    case TypeConstants.STRAIGHT_LINE:
                        view.virtualTransitionRemove();
                        model.addStraightTransition(pre.getStateID(), event.getStateID());
                        break;
                    case TypeConstants.POLY_LINE:
                        if (pre.getStateID() == -1) {
                            event.setiData(pre.getiData());
                            event.setaData(pre.getaData());
                            event.getaData().remove(event.getaData().size() - 1);
                            event.getaData().remove(0);
                            model.addPolyTransition(event.getiData(), event.getStateID(), event.getaData());
                            while (((UIEvent) preEvent.pop()).getEvent() != UserEventConstants.ADD_TRANSITION_FROM);
                        } else {
                            ArrayList<Point2D> points = new ArrayList();
                            event.setaData(points);
                            model.addPolyTransition(pre.getStateID(), event.getStateID(), event.getaData());
                        }
                        view.virtualTransitionRemove();
                        break;
                    case TypeConstants.CURVE:
                        model.addCurveTransition(pre.getStateID(), event.getStateID());
                        view.virtualTransitionRemove();
                        break;
                    case TypeConstants.BEZIER_LINE:
                        if (pre.getStateID() == -1) {
                            event.setiData(pre.getiData());
                            event.setaData(pre.getaData());
                            event.getaData().remove(event.getaData().size() - 1);
                            event.getaData().remove(0);
                            model.addBezierTransition(event.getiData(), event.getStateID(), event.getaData());
                            while (((UIEvent) preEvent.pop()).getEvent() != UserEventConstants.ADD_TRANSITION_FROM);
                        } else {
                            ArrayList<Point2D> points = new ArrayList();
                            event.setaData(points);
                            model.addBezierTransition(pre.getStateID(), event.getStateID(), pre.getaData());
                        }
                        view.virtualTransitionRemove();
                        break;
                    case TypeConstants.SPLINE_LINE:
                        //這邊還需要去model新增transition
                        //這邊需要把虛擬的transition去除
                        break;
                }
                event.setiData(model.getTransitionIDCounter());
                event.finish();
                break;
            default:
                Debug.echo("Canvas Action Performed: no this kind of userEvent " + event.getEvent());
        }
        if (event.getEvent() != UserEventConstants.NOTHING && event.getEvent() != UserEventConstants.UNKNOW) {
            preEvent.push(event);
            Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
            modify = true;
        }
    }

    /**
    * Use to handle the event which is got from Storage.
    *
    * @param event indicates what event has happened.
    *
    * @author rasielchang
    */
    public void storageActionPerformed(UserEventConstants event) {
        switch (event) {
            case UNKNOW:
                System.out.println("test");
                break;
            default:
                System.out.println("No such event. (error dispatched from Controller.storageActionPerformed)");
        }
    }

    /**
    * Use to handle the event which is got from Storage.
    *
    * @param event contains informations about this event.
    *
    * @author rasielchang and polar
    */
    public void storageActionPerformed(UIEvent event) {
        String temp1, temp2, temp3, temp4;
        int tempInt1, tempInt2;
        ArrayList<String> tmpArray;
        unFinishEventPerformed();
        switch (event.getEvent()) {
            case ADD_STATE_BUTTON:
                nextMode(ModeConstants.ADD_MODE);
                break;
            case CONNECT_BUTTON:
                nextMode(ModeConstants.CONNECT_MODE);
                break;
            case FISHEYE_BUTTON:
                nextMode(ModeConstants.FISHEYE_MODE);
                break;
            case DELETE_BUTTON:
                if (view.isCellsSelected()) {
                    unFinishEventPerformed();
                    Vector transitions = view.getSelectedTransitionID();
                    Vector states = view.getSelectedStateID();
                    event.newArrayVector(2);
                    event.setvData(states, 0);
                    if (transitions != null) {
                        for (int i = 0; i < transitions.size(); i++) {
                            model.deleteTransition((Integer) transitions.get(i));
                        }
                    } else {
                        transitions = new Vector();
                    }
                    if (states != null) {
                        for (int i = 0; i < states.size(); i++) {
                            int stateID = (Integer) states.get(i);
                            Vector<Integer> source = model.getSourcePortTransitions(stateID);
                            if (source.isEmpty() == false) {
                                for (int j = 0; j < source.size(); j++) {
                                    model.deleteTransition(source.get(j));
                                    transitions.add(source.get(j));
                                }
                            }
                            Vector<Integer> destination = model.getTargetPortTransitions(stateID);
                            if (destination.isEmpty() == false) {
                                for (int j = 0; j < destination.size(); j++) {
                                    model.deleteTransition(destination.get(j));
                                    transitions.add(destination.get(j));
                                }
                            }
                            model.deleteState(stateID);
                        }
                    }
                    event.setvData(transitions, 1);
                    preEvent.push(event);
                    Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                    modify = true;
                } else {
                    event.setEvent(UserEventConstants.NOTHING);
                    Debug.echo("you have to select some cells first");
                }
                break;
            case SET_INITIAL_STATE:
                Vector states = view.getSelectedStateID();
                Vector<Boolean> isFinals = new Vector();
                Vector<UserEventConstants> ev = new Vector();
                event.newArrayVector(3);
                if (states != null) {
                    for (int i = 0; i < states.size(); i++) {
                        int stateID = (Integer) states.get(i);
                        isFinals.add(i, model.isFinal(stateID));
                        if (!(model.isInitial(stateID))) {
                            model.setInitialState(stateID, isFinals.get(i));
                            ev.add(i, UserEventConstants.SET_INITIAL_STATE);
                        } else {
                            model.removeInitialState(stateID, isFinals.get(i));
                            ev.add(i, UserEventConstants.REMOVE_INITIAL_STATE);
                        }
                    }
                    event.setvData(states, 0);
                    event.setvData(isFinals, 1);
                    event.setvData(ev, 2);
                    preEvent.push(event);
                    Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                    modify = true;
                } else {
                    Debug.echo("you have to select some states!");
                }
                break;
            case SET_FINAL_STATE:
                states = view.getSelectedStateID();
                Vector<Boolean> isInitials = new Vector();
                ev = new Vector();
                event.newArrayVector(3);
                if (states != null) {
                    for (int i = 0; i < states.size(); i++) {
                        int stateID = (Integer) states.get(i);
                        isInitials.add(i, model.isInitial(stateID));
                        if (!(model.isFinal(stateID))) {
                            model.setFinalState(stateID, isInitials.get(i));
                            ev.add(i, UserEventConstants.SET_FINAL_STATE);
                        } else {
                            model.removeFinalState(stateID, isInitials.get(i));
                            ev.add(i, UserEventConstants.REMOVE_FINAL_STATE);
                        }

                    }
                    event.setvData(states, 0);
                    event.setvData(isInitials, 1);
                    event.setvData(ev, 2);
                    preEvent.push(event);
                    Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                    modify = true;
                } else {
                    Debug.echo("you have to select some states!");
                }
                break;
            case SET_STRAIGHT_TRANSITION:
                unFinishEventPerformed();
                model.setLineType(TypeConstants.STRAIGHT_LINE);
                preEvent.push(event);
                Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                modify = true;
                break;
            case SET_POLY_TRANSITION:
                unFinishEventPerformed();
                model.setLineType(TypeConstants.POLY_LINE);
                preEvent.push(event);
                Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                modify = true;
                break;
            case SET_CURVE_TRANSITION:
                unFinishEventPerformed();
                model.setLineType(TypeConstants.CURVE);
                preEvent.push(event);
                Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                modify = true;
                break;
            case SET_BEZIER_TRANSITION:
                unFinishEventPerformed();
                model.setLineType(TypeConstants.BEZIER_LINE);
                preEvent.push(event);
                Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                modify = true;
                break;
            case SET_SPLINE_TRANSITION:
                unFinishEventPerformed();
                model.setLineType(TypeConstants.SPLINE_LINE);
                preEvent.push(event);
                Debug.echo("Controller: UserEvent " + event.getEvent() + " event has been pushed");
                modify = true;
                break;
            case CHANGE_TRANSITION_LINE_TYPE:
                //這個判斷還要再確定要不要實做，有可能不需經過controller來改而是用其他方法
                break;

            //** algorithms for boolean automata

            case B_ARE_EQUIVALENT:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.boolAreEquivalent(temp1, temp2)) {
                    JOptionPane.showMessageDialog(null, "automatons are equivalent");
                } else {
                    JOptionPane.showMessageDialog(null, "automatons are not equivalent");
                }
                resetStack();
                break;
            case B_EVAL:
                unFinishEventPerformed();
                if(event.getsData().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                } else if(event.getsData() != null) {
                    tempInt1 = vjni.boolEval(saveTmpFile(), event.getsData());
                    if (tempInt1 == 0) {
                        JOptionPane.showMessageDialog(null, "Input is rejected",
                                "Result", JOptionPane.INFORMATION_MESSAGE);
                    } else if (tempInt1 == 1) {
                        JOptionPane.showMessageDialog(null, "Input is accepted",
                                "Result", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Unknown error occured with boolean evaluate",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                    resetStack();
                }                
                break;
            case B_IS_AMBIGUOUS:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsAmbiquous(temp1)) {
                        JOptionPane.showMessageDialog(null, "automaton is ambiquous");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton is not ambiquous");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore it can be evaluated for ambiguity",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_IS_COMPLETE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsComplete(temp1)) {
                        JOptionPane.showMessageDialog(null, "automaton is complete");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton is not complete");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore it can be evaluated for completeness", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_IS_DETERMINISTIC:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsDeterministic(saveTmpFile())) {
                        JOptionPane.showMessageDialog(null, "automaton is deterministic");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton is not deterministic");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore it can be evaluated for determinism", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_IS_EMPTY:
                unFinishEventPerformed();
                if (vjni.boolIsEmpty(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is empty");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not empty");
                }
                resetStack();
                break;
            case B_HAS_SUCC_COMP:
                unFinishEventPerformed();
                if (vjni.boolHasSuccComp(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton has successful computations");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton does not have successful computations");
                }
                resetStack();
                break;
            case B_IS_REALTIME:
                unFinishEventPerformed();
                if (vjni.boolIsRealtime(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is realtime");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not realtime");
                }
                resetStack();
                break;
            case B_IS_STANDARD:
                unFinishEventPerformed();
                if (vjni.boolIsStandard(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is standard");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not standard");
                }
                resetStack();
                break;
            case B_ACCESSIBLE:
                unFinishEventPerformed();
                vjni.boolAccessible(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_EPS_REMOVAL:
                unFinishEventPerformed();
                vjni.boolEpsRemoval(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_EPS_REMOVAL_SP:
                unFinishEventPerformed();
                vjni.boolEpsRemovalSp(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_CO_ACCESSIBLE:
                unFinishEventPerformed();
                vjni.boolCoAccessible(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_COMPLETE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    vjni.boolComplete(temp1, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore it can be complete", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_CONCATENATE:
                unFinishEventPerformed();
                vjni.boolConcatenate(saveTmpFile(), event.getsData(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_POWER:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    vjni.boolPower(temp1, event.getiData(), "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore its power can be computed", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_PRODUCT:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.boolIsRealtime(temp1) && vjni.boolIsRealtime(temp2)) {
                    vjni.boolProduct(temp1, temp2, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be realtime" +
                            "\nbefore their product can be computed", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_QUOTIENT:
                unFinishEventPerformed();
                vjni.boolQuotient(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_REALTIME:
                unFinishEventPerformed();
                vjni.boolRealtime(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_STANDARDIZE:
                unFinishEventPerformed();
                vjni.boolStandardize(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_UNION_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.boolIsRealtime(temp1) && vjni.boolIsRealtime(temp2)) {
                    if (vjni.boolIsStandard(temp1) && vjni.boolIsStandard(temp2)) {
                        vjni.boolUnionOfStandard(temp1, event.getsData(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "both automatons need to be standard" +
                                "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be realtime" +
                            "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_CONCAT_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.boolIsRealtime(temp1) && vjni.boolIsRealtime(temp2)) {
                    if (vjni.boolIsStandard(temp1) && vjni.boolIsStandard(temp2)) {
                        vjni.boolConcatOfStandard(temp1, event.getsData(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "both automatons need to be standard" +
                                "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be realtime" +
                            "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_STAR_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsStandard(temp1)) {
                    vjni.boolStarOfStandard(temp1, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be standard" + "\nnefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_UNION:
                unFinishEventPerformed();
                vjni.boolUnion(saveTmpFile(), event.getsData(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_TRANSPOSE:
                unFinishEventPerformed();
                vjni.boolTranspose(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_TRIM:
                unFinishEventPerformed();
                vjni.boolTrim(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case B_COMPLEMENT:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsComplete(temp1)) {
                        vjni.boolComplement(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton needs to be complete" +
                                "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" + "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_DETERMINIZE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    vjni.boolDeterminize(temp1, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" + "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_MINIMIZE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsComplete(temp1)) {
                        vjni.boolMinimize(temp1, "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton needs to be complete" +
                                "\nbefore this operation can continue",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" + "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_MINIMIZE_MOORE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.boolIsRealtime(temp1)) {
                    if (vjni.boolIsComplete(temp1)) {
                        vjni.boolMinimizeMoore(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton needs to be complete" +
                                "\nbefore this operation can continue",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" + "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case B_AUT_TO_EXP:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                vjni.boolAutToExp(temp1, "/tmp/tmpTextFile.txt");
                try {
                    File tmpFile = new File("/tmp/tmpTextFile.txt");
                    int size = (int)tmpFile.length();
                    FileReader reader = new FileReader(tmpFile);
                    char buffer[] = new char[size];
                    reader.read(buffer);
                    String s = new String(buffer);
                    JOptionPane.showMessageDialog(null,
                            "The related regular expression is:\n" + s,
                            "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    System.err.println("controller: error happended on read tmp file!!");
                }
                resetStack();
                break;
            case B_DERIVED_TERM:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolDerivedTerm(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");                        
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }                
                break;
            case B_EXP_TO_AUT:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolExpToAut(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }                
                break;
            case B_EXPAND:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolExpand(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpTextFile.txt");
                        try {
                            File tmpFile = new File("/tmp/tmpTextFile.txt");
                            int size = (int)tmpFile.length();
                            FileReader reader = new FileReader(tmpFile);
                            char buffer[] = new char[size];
                            reader.read(buffer);
                            String s = new String(buffer);
                            JOptionPane.showMessageDialog(null,
                                    "The expanded regular expression is:\n" + s,
                                    "Result", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException e) {
                            System.err.println("controller: error happended on read tmp file!!");
                        }
                        resetStack();
                    }
                }
                break;
            case B_IDENTITY_EXP:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolIdentityExp(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpTextFile.txt");
                        try {
                            File tmpFile = new File("/tmp/tmpTextFile.txt");
                            int size = (int)tmpFile.length();
                            FileReader reader = new FileReader(tmpFile);
                            char buffer[] = new char[size];
                            reader.read(buffer);
                            String s = new String(buffer);
                            JOptionPane.showMessageDialog(null,
                                    "The expanded regular expression is:\n" + s,
                                    "Result", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException e) {
                            System.err.println("controller: error happended on read tmp file!!");
                        }
                        resetStack();
                    }
                }
                break;
            case B_STANDARD:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolStandard(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;
            case B_THOMPSON:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.boolThompson(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;

//*********************************************//
//*********** Weighted Automata ***************//
//*********************************************//

            case W_EVAL:
                unFinishEventPerformed();
                try {
                    tempInt1 = vjni.wEval(saveTmpFile(), event.getsData());
                    JOptionPane.showMessageDialog(null, "Result of evaluation is: " + Integer.toString(tempInt1),
                            "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                resetStack();
                break;
            case W_IS_AMBIGUOUS:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.wIsRealtime(temp1)) {
                    if (vjni.wIsAmbiguous(temp1)) {
                        JOptionPane.showMessageDialog(null, "automaton is ambiquous");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton is not ambiquous");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_IS_COMPLETE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.wIsRealtime(temp1)) {
                    if (vjni.wIsComplete(temp1)) {
                        JOptionPane.showMessageDialog(null, "automaton is complete");
                    } else {
                        JOptionPane.showMessageDialog(null, "automaton is not complete");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore this operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_IS_EMPTY:
                unFinishEventPerformed();
                if (vjni.wIsEmpty(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is empty");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not empty");
                }
                resetStack();
                break;
            case W_HAS_SUCC_COMP:
                unFinishEventPerformed();
                if (vjni.wHasSuccComp(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton has successful computations");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton does not have successful computations");
                }
                resetStack();
                break;
            case W_IS_REALTIME:
                unFinishEventPerformed();
                if (vjni.wIsRealtime(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is realtime");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not realtime");
                }
                resetStack();
                break;
            case W_IS_STANDARD:
                unFinishEventPerformed();
                if (vjni.wIsStandard(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is standard");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not standard");
                }
                resetStack();
                break;
            case W_ACCESSIBLE:
                unFinishEventPerformed();
                vjni.wAccessible(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_EPS_REMOVAL:
                unFinishEventPerformed();
                vjni.wEpsRemoval(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_EPS_REMOVAL_SP:
                unFinishEventPerformed();
                vjni.wEpsRemovalSp(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_CO_ACCESSIBLE:
                unFinishEventPerformed();
                vjni.wCoAccessible(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_COMPLETE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.wIsRealtime(temp1)) {
                    vjni.wComplete(temp1, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_CONCATENATE:
                unFinishEventPerformed();
                vjni.wConcatenate(saveTmpFile(), event.getsData(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_POWER:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.wIsRealtime(temp1)) {
                    vjni.wPower(temp1, event.getiData(), "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be realtime" +
                            "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_PRODUCT:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.wIsRealtime(temp1) && vjni.wIsRealtime(temp2)) {
                    vjni.wProduct(temp1, temp2, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "both automaton needs to be realtime" +
                            "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_QUOTIENT:
                unFinishEventPerformed();
                vjni.wQuotient(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_REALTIME:
                unFinishEventPerformed();
                vjni.wRealtime(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_STANDARDIZE:
                unFinishEventPerformed();
                vjni.wStandardize(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_UNION_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.wIsRealtime(temp1) && vjni.wIsRealtime(temp2)) {
                    if (vjni.wIsStandard(temp1) && vjni.wIsStandard(temp2)) {
                        vjni.wUnionOfStandard(temp1, event.getsData(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "both automatons need to be standard" + "\nbefore this operation can continue",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be realtime" + "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_CONCAT_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.wIsRealtime(temp1) && vjni.wIsRealtime(temp2)) {
                    if (vjni.wIsStandard(temp1) && vjni.wIsStandard(temp2)) {
                        vjni.wConcatOfStandard(temp1, event.getsData(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                    } else {
                        JOptionPane.showMessageDialog(null, "both automatons need to be standard" + "\nbefore this operation can continue",
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be realtime" + "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_STAR_OF_STANDARD:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                if (vjni.wIsStandard(temp1)) {
                    vjni.wStarOfStandard(temp1, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton needs to be standard" + "\nbefore this operation can continue",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case W_UNION:
                unFinishEventPerformed();
                vjni.wUnion(saveTmpFile(), event.getsData(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_TRANSPOSE:
                unFinishEventPerformed();
                vjni.wTranspose(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_TRIM:
                unFinishEventPerformed();
                vjni.wTrim(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case W_AUT_TO_EXP:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                vjni.wAutToExp(temp1, "/tmp/tmpTextFile.txt");
                try {
                    File tmpFile = new File("/tmp/tmpTextFile.txt");
                    int size = (int)tmpFile.length();
                    FileReader reader = new FileReader(tmpFile);
                    char buffer[] = new char[size];
                    reader.read(buffer);
                    String s = new String(buffer);
                    JOptionPane.showMessageDialog(null,
                            "The related regular expression is:\n" + s,
                            "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    System.err.println("controller: error happended on read tmp file!!");
                }                
                resetStack();
                break;
            case W_DERIVED_TERM:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wDerivedTerm(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        //vjni.boolMinimizeMoore(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;
            case W_EXP_TO_AUT:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wExpToAut(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;
            case W_EXPAND:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wExpand(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpTextFile.txt");
                        try {
                            File tmpFile = new File("/tmp/tmpTextFile.txt");
                            int size = (int)tmpFile.length();
                            FileReader reader = new FileReader(tmpFile);
                            char buffer[] = new char[size];
                            reader.read(buffer);
                            String s = new String(buffer);
                            JOptionPane.showMessageDialog(null,
                                    "The expanded regular expression is:\n" + s,
                                    "Result", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException e) {
                            System.err.println("controller: error happended on read tmp file!!");
                        }
                        resetStack();
                    }
                }
                break;
            case W_IDENTITY_EXP:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wIdentityExp(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpTextFile.txt");
                        try {
                            File tmpFile = new File("/tmp/tmpTextFile.txt");
                            int size = (int)tmpFile.length();
                            FileReader reader = new FileReader(tmpFile);
                            char buffer[] = new char[size];
                            reader.read(buffer);
                            String s = new String(buffer);
                            JOptionPane.showMessageDialog(null,
                                    "The expanded regular expression is:\n" + s,
                                    "Result", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException e) {
                            System.err.println("controller: error happended on read tmp file!!");
                        }
                        resetStack();
                    }
                }
                break;
            case W_STANDARD:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wStandard(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;
            case W_THOMPSON:
                unFinishEventPerformed();
                tmpArray = event.getaData();
                if(tmpArray.get(0) != null && tmpArray.get(1) != null) {
                    if(tmpArray.get(0).equals("") || tmpArray.get(1).equals("")) {
                        JOptionPane.showMessageDialog(null, "Please type something",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        vjni.wThompson(tmpArray.get(0), tmpArray.get(1), "/tmp/tmpfileforalgo1.xml");
                        loadTmpFile("/tmp/tmpfileforalgo1.xml");
                        resetStack();
                    }
                }
                break;
            case T_IS_EMPTY:
                unFinishEventPerformed();
                if (vjni.tIsEmpty(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is empty");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not empty");
                }
                resetStack();
                break;
            case T_HAS_SUCC_COMP:
                unFinishEventPerformed();
                if (vjni.tHasSuccComp(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton has successful computations");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton does not have successful computations");
                }
                resetStack();
                break;
            case T_IS_SUB_NORMALIZED:
                unFinishEventPerformed();
                if (vjni.tIsSubNormalized(saveTmpFile())) {
                    JOptionPane.showMessageDialog(null, "automaton is sub-normalized");
                } else {
                    JOptionPane.showMessageDialog(null, "automaton is not sub-normalized");
                }
                resetStack();
                break;
            case T_EPS_REMOVAL:
                unFinishEventPerformed();
                vjni.tEpsRemoval(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_EPS_REMOVAL_SP:
                unFinishEventPerformed();
                vjni.tEpsRemovalSp(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_DOMAIN:
                unFinishEventPerformed();
                vjni.tDomain(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_EVAL:
                unFinishEventPerformed();
                vjni.tEval(saveTmpFile(), event.getsData(), "/tmp/tmpTextFile.txt");
                try {
                    File tmpFile = new File("/tmp/tmpTextFile.txt");
                    int size = (int)tmpFile.length();
                    FileReader reader = new FileReader(tmpFile);
                    char buffer[] = new char[size];
                    reader.read(buffer);
                    String s = new String(buffer);
                    JOptionPane.showMessageDialog(null,
                            "The related regular expression is:\n" + s,
                            "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    System.err.println("controller: error happended on read tmp file!!");
                }
                resetStack();
                break;
            case T_EVAL_AUT:
                unFinishEventPerformed();
                vjni.tEvalAut(saveTmpFile(), event.getsData(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_LTL_TO_PAIR:
                unFinishEventPerformed();
                vjni.tLtlToPair(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_IMAGE:
                unFinishEventPerformed();
                vjni.tImage(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_TRANSPOSE:
                unFinishEventPerformed();
                vjni.tTranspose(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_TRIM:
                unFinishEventPerformed();
                vjni.tTrim(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_SUB_NORMALIZE:
                unFinishEventPerformed();
                vjni.tSubNormalize(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_COMPOSITION_COVER:
                unFinishEventPerformed();
                vjni.tCompositionCover(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_COMPOSITION_CO_COVER:
                unFinishEventPerformed();
                vjni.tCompositionCoCover(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_COMPOSE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                if (vjni.tIsSubNormalized(temp1) && vjni.tIsSubNormalized(temp2)) {
                    vjni.tCompose(temp1, temp2, "/tmp/tmpfileforalgo1.xml");
                    loadTmpFile("/tmp/tmpfileforalgo1.xml");
                } else {
                    JOptionPane.showMessageDialog(null, "both automatons need to be (sub-)normalized" +
                            "\nbefore operation can continue", "Error", JOptionPane.WARNING_MESSAGE);
                }
                resetStack();
                break;
            case T_U_COMPOSE:
                unFinishEventPerformed();
                temp1 = saveTmpFile();
                temp2 = event.getsData();
                vjni.tUCompose(temp1, temp2, "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_INVERT:
                unFinishEventPerformed();
                vjni.tInvert(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case T_INTERSECTION:
                unFinishEventPerformed();
                vjni.tIntersection(saveTmpFile(), "/tmp/tmpfileforalgo1.xml");
                loadTmpFile("/tmp/tmpfileforalgo1.xml");
                resetStack();
                break;
            case UNDO:
                if (preEvent.empty() == false) {
                    UIEvent pre = (UIEvent) preEvent.pop();
                    undo(pre);
                } else {
                    Debug.echo("Controller_storageActionPerformed: there isn't any event to be undo.");
                    //這邊應該要叫storage disable undo
                }
                break;
            case REDO:
                if (nextEvent.empty() == false) {
                    UIEvent next = (UIEvent) nextEvent.pop();
                    redo(next);
                } else {
                    Debug.echo("Controller_storageActionPerformed: there isn't any event to be redo.");
                    //這邊應該要叫storage disable redo
                }
                break;
            case SPRING_LAYOUT_PARAMETERS:
                unFinishEventPerformed();
                SpringLayout.modifyParameterDialog();
                view.updateLoopInitialFinal();
                break;
            case GRAPH_GENERATOR:
                unFinishEventPerformed();
                generateGraph();
                view.fitWindow();
                break;
            case TEST_FUNCTION:

                break;
            case CIRCLE_LAYOUT:
                unFinishEventPerformed();
                view.circleLayout();
                // commented by junli
//				view.cleanTransitionControlPoint();
                int iData = event.getiData();
//				if (iData == -65536) {
//					circleLayout();
//				} else {
//					circleLayout(iData);
//				}
		view.updateLoopInitialFinal();
                view.fitWindow();
                break;
            case CIRCLE_LAYOUT_WO_ALG:
                unFinishEventPerformed();
                CircleLayoutWithNoAlg circleLayoutNoAlg = new CircleLayoutWithNoAlg(model);
                circleLayoutNoAlg.run();
                view.updateLoopInitialFinal();
                view.fitWindow();
                break;
            case GRID_LAYOUT:
                unFinishEventPerformed();
                iData = event.getiData();
                if (iData == -65536) {
                    gridLayout();
                } else {
                    gridLayout(iData);
                }
                break;
            case SPRING_LAYOUT:
                unFinishEventPerformed();
                view.cleanTransitionControlPoint();
                springLayout();
                view.updateLoopInitialFinal();
                view.fitWindow();
                break;
            case LINE_LAYOUT:
                unFinishEventPerformed();

                break;
            case VALUETYPE_HAVE_BEEN_SET:
                unFinishEventPerformed();
                Automata2.ValueType vt = model.getValueType();
                parser = new LabelParser(model, vt);
                view.setLabelOption(vt);
                break;
            case ZOOM_IN:
                viewScale = viewScale * 4 / 3;
                view.zoom(viewScale);
                break;
            case ZOOM_OUT:
                viewScale = viewScale * 3 / 4;
                view.zoom(viewScale);
                break;
            case HIERARCHICAL_LAYOUT2:
                unFinishEventPerformed();
                view.hierarchicalLayout2();
                view.updateLoopInitialFinal();
                view.fitWindow();
                break;
            case ACTUAL_SIZE:
                unFinishEventPerformed();
                view.actualSize();
                break;
            case FIT_WINDOW:
                unFinishEventPerformed();
                view.fitWindow();
                break;
            case FAST_ORGANIC:
                unFinishEventPerformed();
                view.fastOrganic();
                break;
            case ORGANIC:
                unFinishEventPerformed();
                view.organic();
                break;
            case SELF_ORGANIZING:
                unFinishEventPerformed();
                view.selfOrganizing();
                break;
            default:
                Debug.echo("Storage Action Performed: no this kind of userEvent " + event.getEvent());
        }
    }

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
    public void mouseClicked(int stateID, int transitionID, Point2D.Float point) {
        UIEvent event = new UIEvent(UserEventConstants.UNKNOW);
        switch (currentMode) {
            case ADD_MODE:
                if (stateID == -1 && transitionID == -1) {
                    event.setEvent(UserEventConstants.ADD_STATE);
                    event.setPoint(point);
                }

                break;
            case CONNECT_MODE:
                if (preEvent.empty() == false) {
                    UIEvent pre = (UIEvent) preEvent.peek();
                    switch (pre.getEvent()) {
                        case ADD_TRANSITION_FROM:
                            if (stateID >= 0) {
                                event.setStateID(stateID);
                                event.setEvent(UserEventConstants.ADD_TRANSITION_TO);
                            } else if (model.getLineType() == TypeConstants.POLY_LINE ||
                                    model.getLineType() == TypeConstants.BEZIER_LINE) {
                                event.setEvent(UserEventConstants.ADD_TRANSITION_CONTROL_POINT);
                            }

                            break;
                        case ADD_TRANSITION_CONTROL_POINT:
                            if (stateID >= 0) {
                                event.setStateID(stateID);
                                event.setEvent(UserEventConstants.ADD_TRANSITION_TO);
                            } else {
                                event.setEvent(UserEventConstants.ADD_TRANSITION_CONTROL_POINT);
                            }

                            break;
                        default:
                            if (stateID >= 0) {
                                event.setStateID(stateID);
                                event.setEvent(UserEventConstants.ADD_TRANSITION_FROM);
                            }

                    }
                } else {
                    event.setEvent(UserEventConstants.ADD_TRANSITION_FROM);
                }

                break;
            default:

        }
        canvasActionPerformed(event);
    }

    /**
    * Use to check if data in DataModel or CanvasView have been changed.
    *
    * @return true means has been modified, otherwise has not.
    *
    * @author rasielchang
    */
    public boolean isModify() {
        return modify;
    }

    /**
    * Use to save the xml file.
    *
    * @author rasielchang
    */
    public void saveFile() {
        modify = false;
    }

    /**
    * Use to handle uncomplete event.
    *
    * @author rasielchang
    */
    private void unFinishEventPerformed() {
        if (preEvent.empty() == false) {
            UIEvent pre = (UIEvent) preEvent.peek();
            if (pre.getFinish() == false) {
                switch (pre.getEvent()) {
                    case ADD_TRANSITION_FROM:
                        view.virtualTransitionRemove();
                        preEvent.pop();
                        break;

                    case ADD_TRANSITION_CONTROL_POINT:
                        preEvent.pop();
                        unFinishEventPerformed();

                        break;

                    default:

                        Debug.echo("Controller: error in funcition unFinishEventPerformed");
                }

            }
        }
    }

    /**
    * Use to invoke the gridLayout.
    *
    * @author rasielchang
    */
    public void gridLayout() {
        gridLayout = new GridLayout(model);
        gridLayout.run();
    }

    /**
    * Use to invoke the grid layout.
    *
    * @param Data is a argument of grid layout.
    *
    * @author rasielchang
    */
    public void gridLayout(int Data) {
        gridLayout = new GridLayout(model);
        gridLayout.run(Data);
    }

    /**
    * Use to invoke the spring layout.
    *
    * @author rasielchang
    */
    public void springLayout() {
        springLayout = new SpringLayout(model);
        springLayout.run();
    }

    /**
    * Use to save temp file which is used for performing vaucanson's algorithm.
    *
    * @return the file path.
    *
    * @author rasielchang
    */
    public String saveTmpFile() {
        String path = "/tmp/tmpfileforalgo.xml";
        XMLCreator xMLCreator = new XMLCreator(model, path);
        xMLCreator.run();
        return path;
    }

    /**
    * Use to load temp file which is used for performing vaucanson's algorithm.
    *
    * @author rasielchang
    */
    public void loadTmpFile() {
//        BEGIN
//        Instead of replacing the automaton currently displayed, the output automaton from running algorithms will be opened in a new tab.
//        XMLReader xmlReader = new XMLReader();
//        xmlReader.parseXML("/tmp/tmpfileforalgo1.xml");
//        model.reset();
//        model.setAutomata(xmlReader.getAutomata());
//        model.setGeometry(new Geometry2());
//        model.setDrawing(new Drawing2());
//        openFile();
        this.storage.fileOpen("/tmp/tmpfileforalgo1.xml");
//        Instead of replacing the automaton currently displayed, the output automaton from running algorithms will be opened in a new tab.
//        END
    }

    /**
    * Use to load temp file which is used for performing vaucanson's algorithm.
    *
    * @param input indicates the file path.
    *
    * @author rasielchang
    */
    public void loadTmpFile(String input) {
//        BEGIN
//        Instead of replacing the automaton currently displayed, the output automaton from running algorithms will be opened in a new tab.
//        XMLReader xmlReader = new XMLReader();
//        xmlReader.parseXML(input);
//        model.reset();
//        model.setAutomata(xmlReader.getAutomata());
//        model.setGeometry(new Geometry2());
//        model.setDrawing(new Drawing2());
//        openFile();
        this.storage.fileOpen(input);
//        Instead of replacing the automaton currently displayed, the output automaton from running algorithms will be opened in a new tab.
//        END
    }

    /**
    * Use to load xml file.
    *
    * @author rasielchang
    */
    public void openFile() {
        model.notifyDataModelObservers();
        
        //assign initial position values to states
        CircleLayoutWithNoAlg circleLayoutNoAlg = new CircleLayoutWithNoAlg(model);
        circleLayoutNoAlg.run();

        modify = false;
        Automata2.ValueType vt = model.getValueType();
        parser = new LabelParser(model, vt);
        view.setLabelOption(vt);
        view.updateLoopInitialFinal();
    }

    /**
    * Use to reset the event stack.
    *
    * @author rasielchang
    */
    private void resetStack() {
        preEvent = new Stack();
        nextEvent = new Stack();
    }

    /**
    * Use to write string data into xml file.
    *
    * @param data is wrote into the file.
    *
    * @author rasielchang
    */
    public void writeIntoFile(String data) {
        try {
            File output = new File("/tmp/tmpfileforalgo1.xml");
            output.createNewFile();
            FileWriter fw = new FileWriter(output);
            fw.write(data);
            fw.close();
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

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
    public void setTransitionLabel(int transitionID, Label label, String text) {
        model.setTransitionLabel(transitionID, label, text);
    }

    public void generateGraph() {
        model.setLineType(TypeConstants.STRAIGHT_LINE);
        try {
            int nodeNumberDefault = 20;
            int nodeNumber = nodeNumberDefault;
            String nodeCountInput = JOptionPane.showInputDialog(
                    "Number of nodes:", Integer.toString(nodeNumberDefault));
            try {
                nodeNumber = Integer.parseInt(nodeCountInput);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                nodeNumber = nodeNumberDefault;
                String errorMessage = "invalid input for number of nodes, default value " + Integer.toString(nodeNumberDefault) + " assigned";
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (nodeNumber < 0) {
                String errorMessage = "number of nodes can not be a negative number\n, default value " + Integer.toString(nodeNumberDefault) + " assigned";
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                nodeNumber = nodeNumberDefault;
            }

            double densityDefault = 0.05;
            double density = densityDefault;
            String densityInput = JOptionPane.showInputDialog(
                    "Edge density:", Double.toString(densityDefault));
            try {
                density = Double.parseDouble(densityInput);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                density = densityDefault;
                String errorMessage = "invalid input for number of nodes, default value " + Double.toString(densityDefault) + " assigned";
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (density < 0 || density > 0.1) {
                String errorMessage = "density value out of range\ndefault value " + Double.toString(densityDefault) + " assigned";
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                density = densityDefault;
            }

            String[] alreadyConn = new String[(int) (nodeNumber * nodeNumber * density) + 1];
            for (int i = 0; i < alreadyConn.length; i++) {
                alreadyConn[i] = "99999999";
            }
            if (nodeNumber > 0 & density >= 0 & density <= 0.8) {
                model.newModel();
                for (int i = 0; i < nodeNumber; i++) {
                    model.addState(new Point2D.Float(0, 0));
                    model.setStatePosition(i, new Point2D.Float(((int) (Math.random() * 100000)) % 973, ((int) (Math.random() * 100000)) % 551));
                }
                boolean doesConn;
                for (int i = 0; i < nodeNumber * nodeNumber * density; i++) {
                    doesConn = false;
                    int sourceTemp = ((int) (Math.random() * 1000)) % nodeNumber;
                    int targetTemp = ((int) (Math.random() * 1000)) % nodeNumber;

                    //don't add self loop
                    if (sourceTemp == targetTemp) {
                        i--;
                        continue;
                    }

                    String str1 = "";
                    if (sourceTemp < 10) {
                        str1 = str1.concat("0");
                    }
                    if (sourceTemp < 100) {
                        str1 = str1.concat("0");
                    }
                    str1 = str1.concat(((Integer) sourceTemp).toString());
                    String str2 = "";
                    if (targetTemp < 10) {
                        str2 = str2.concat("0");
                    }
                    if (targetTemp < 100) {
                        str2 = str2.concat("0");
                    }
                    str2 = str2.concat(((Integer) targetTemp).toString());
                    str1 = str1.concat(str2);
                    for (int j = 0; j < i; j++) {
                        if (alreadyConn[j].equals(str1)) {
                            doesConn = true;
                            break;
                        }
                    }
                    if (doesConn == false) {
                        alreadyConn[i] = str1;
                        //model.addStraightTransition(sourceTemp, targetTemp);
                        model.addTransition(sourceTemp, targetTemp, " ");
                    } else {
                        i--;
                    }
                }
            } else {
                Debug.echo("Controller.storageActionPerformed: you'r input value must be right range.");
            }

            //removed isolated states
            Vector<Integer> stateIDs = model.getAllStates();
            Vector<Integer> targetTrans = null;
            Vector<Integer> sourceTrans = null;
            for (int i = 0; i < stateIDs.size(); i++) {
                int currentState = stateIDs.get(i);
                targetTrans = model.getSourcePortTransitions(currentState);
                sourceTrans = model.getTargetPortTransitions(currentState);
                if (targetTrans.size() == 0 && sourceTrans.size() == 0) {
                    model.deleteState(currentState);
                }
            }
            resetTransitionLabel();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Generate graph fail.");
        }
    }

    /**
    * Use to reset the label of a transition to identity.
    *
    * @author rasielchang
    */
    public void resetTransitionLabel() {
        Vector<Integer> transIDs = model.getAllTransitions();
        Automata2.TypedRegExp typedRegExp = new TypedRegExp(new Automata2.One());
        String labelValue = "1";
        Automata2.Label simpleLabel = new Label(typedRegExp, labelValue);
        for (int i = 0; i < transIDs.size(); i++) {
            int transitionID = transIDs.get(i);
            model.setTransitionLabel(transitionID, simpleLabel, labelValue);
        }
    }
}
