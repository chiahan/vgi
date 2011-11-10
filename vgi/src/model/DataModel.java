package model;

import debug.Debug;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;
import event.UserEventConstants;
import model.Automata2.Label;
import model.Automata2.One;
import model.Automata2.TypedRegExp;
import model.Automata2.ValueType.MonoidTypeE;

/**
 * This class is used to store the information of automata.
 *
 * @author rasielchang
 */
public class DataModel implements DataModelInterface {
    Automata2 automata;
    Geometry2 geometry;
    Drawing2 drawing;
    Automata2 deletedAutomata;
    Geometry2 deletedGeometry;
    Drawing2 deletedDrawing;
    int stateIDCounter;
    int transitionIDCounter;
    ArrayList dataModelObservers = new ArrayList();

    String transitionLabel;    
    int lineType;
    
    Geometry2.TransitionsGeometricData.TransitionTypeE transitionType;
    Automata2.ValueType valueType;
    int defaultLoopDir;
    float defaultLabelDist;
        
    /**
     * The constructor, it will new an automaton.
     *
     * @author rasielchang
     */
    public DataModel() {
        automata = new Automata2();
        geometry = new Geometry2();
        drawing = new Drawing2();
    }

    /**
     * The constructor, it will open an automaton from given variable.
     *
     * @param automata store the defination, states and transitions of this automaton.
     *
     * @param geometry store the geometry information of this automaton.
     *
     * @param drawing store the drawing information of this automaton.
     *
     * @author rasielchang
     */
    public DataModel(Automata2 automata, Geometry2 geometry, Drawing2 drawing) {
        this.automata = automata;
        this.geometry = geometry;
        this.drawing = drawing;
    }
    
    public void initialize() {
        deletedAutomata = new Automata2();
        deletedGeometry = new Geometry2();
        deletedDrawing = new Drawing2();
        stateIDCounter = -1;
        transitionIDCounter = -1;                
        lineType = TypeConstants.STRAIGHT_LINE;
//        lineType = TypeConstants.CURVE;
        
        valueType = new Automata2.ValueType();
        
    }
    
    public void modifyDefaultLabelDist(float labelDist) {
        defaultLabelDist = labelDist;
    }
    
    public void modifyDefaultLoopDir(int loopDir) {
        defaultLoopDir = loopDir;
    }
    
    
/* notifier */
    
    public void notifyDataModelObservers() {
        Vector<Integer> states = getAllStates();
        Vector<Integer> transitions = getAllTransitions();
        Vector<Integer> initialStates = getInitialStates();
        Vector<Integer> finalStates = getFinalStates();
        int s = -1, t = -1, tmp = -1;
        int stateID;
        int transitionID;
        int sourceStateID;
        int destinationStateID;
        String label;
        for(int i = 0; i < states.size(); i++) {
            stateID = states.get(i);
            tmp = stateID;
            if(s < tmp) {
                s = tmp;
            }
            geometry.addState(((Integer)stateID).toString());
            drawing.addState(((Integer)stateID).toString());
            notifyDataModelObservers(stateID, "s" + stateID, new Point2D.Float(0, 0));// the state name should be fix because now we don't store it.            
        }
        stateIDCounter = s;
        for(int i = 0; i < transitions.size(); i++) {
            transitionID = transitions.get(i);
            tmp = transitionID;
            if(t < tmp) {
                t = tmp;
            }
            geometry.addTransition(((Integer)transitionID).toString());
            drawing.addTransition(((Integer)transitionID).toString());
            sourceStateID = getTransitionSource(transitions.get(i));
            destinationStateID = getTransitionTarget(transitions.get(i));
            label = getTransitionLabel(transitionID);
            notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, label, TypeConstants.STRAIGHT_LINE);
        }
        transitionIDCounter = t;
        for(int i = 0; i < initialStates.size(); i++) {
            notifyDataModelObservers(UserEventConstants.SET_INITIAL_STATE, initialStates.get(i), isFinal(initialStates.get(i)));
        }
        for(int i = 0; i < finalStates.size(); i++) {
            notifyDataModelObservers(UserEventConstants.SET_FINAL_STATE, finalStates.get(i), isInitial(finalStates.get(i)));
        }
    }
        
    public void notifyDataModelObservers(int stateID, String stateName, Point2D.Float point){
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(stateID, stateName, point);
        }
        int i = 0;
    }// add state
    
    public void notifyDataModelObservers(int transitionID, int sourceStateID, int destinationStateID, String label, int type) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(transitionID, sourceStateID, destinationStateID, label, type);
        }
    }// add transition
    
    public void notifyDataModelObservers(int transitionID, int sourceStateID,
            int destinationStateID, String label, int type, ArrayList points) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(transitionID, sourceStateID,
                    destinationStateID, label, type, points);
        }
    }//add transition with some data
    
    public void notifyDataModelObservers(UserEventConstants event, int cellID) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(event, cellID);
        }
    }

    public void notifyDataModelObservers(UserEventConstants event, int cellID, Point2D.Float point) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(event, cellID, point);
        }
    }
    
    public void notifyDataModelObservers(UserEventConstants event, int cellID, String data) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(event, cellID, data);
        }
    }
    
    public void notifyDataModelObservers(UserEventConstants event, int cellID, ArrayList data) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(event, cellID, data);
        }
    }
    
    public void notifyDataModelObservers(UserEventConstants event, int cellID, boolean data) {
        for(int i = 0; i < dataModelObservers.size(); i++) {
            DataModelObserver dataModelObserver = (DataModelObserver)dataModelObservers.get(i);
            dataModelObserver.updateDataModel(event, cellID, data);
        }
    }
              
    
    
/* STATE in all */
    
    public void addState(Point2D.Float point) {
        int stateID = createStateID();
        String stateName = "s" + stateID;
        automata.addState(((Integer)stateID).toString(), stateName);
        geometry.addState(((Integer)stateID).toString());
        geometry.setStatePosition(((Integer)stateID).toString(), point);        
        notifyDataModelObservers(stateID, stateName, point);
        Debug.echo("DataModel: New state successful");
    }
    
    public void reAddState(int stateID) {
        String stateID_ = ((Integer)stateID).toString();
        String stateName = deletedAutomata.getStateName(stateID_);
        Point2D.Float point = deletedGeometry.getStatePosition(stateID_);
        
        automata.addState(stateID_, stateName);
        geometry.addState(stateID_);
        geometry.setStatePosition(stateID_, point);
        deletedAutomata.deleteState(stateID_);
        deletedGeometry.deleteState(stateID_);
        notifyDataModelObservers(stateID, stateName, point);
        Debug.echo("DataModel: New state successful");
    }

    public void deleteState(int stateID) {
        String stateID_ = ((Integer)stateID).toString();
        
        deletedAutomata.addState(stateID_, automata.getStateName(stateID_));
        deletedGeometry.addState(stateID_);
        deletedGeometry.setStatePosition(stateID_, geometry.getStatePosition(stateID_));
        automata.deleteState(((Integer)stateID).toString());
        geometry.deleteState(((Integer)stateID).toString());
        notifyDataModelObservers(UserEventConstants.DELETE_STATE, stateID);
        Debug.echo("DataModel: Delete state successful");
    }    

/* STATE in AUTOMATA */
    
    public String getStateName(int stateID) {
        return automata.getStateName(((Integer)stateID).toString());
    }
    
    public void changeStateName(int stateID, String name) {
        automata.changeStateName(((Integer)stateID).toString(), name);
        notifyDataModelObservers(UserEventConstants.CHANGE_STATE_NAME, stateID, name);
        Debug.echo("DataModel: change state name successful");
    }
    
    public void setInitialState(int stateID, boolean finalState) {
        automata.setInitialState(((Integer)stateID).toString());
        notifyDataModelObservers(UserEventConstants.SET_INITIAL_STATE, stateID, finalState);
        Debug.echo("DataModel: set to initial state!");              
    }
    
    public void removeInitialState(int stateID, boolean finalState) {
        automata.removeInitialState(((Integer)stateID).toString());
        notifyDataModelObservers(UserEventConstants.REMOVE_INITIAL_STATE, stateID, finalState);
        Debug.echo("DataModel: remove initial state!");
    }
    
    public void setFinalState(int stateID_, boolean initialState) {
        String stateID = ((Integer)stateID_).toString();
        automata.setFinalState(stateID);
        notifyDataModelObservers(UserEventConstants.SET_FINAL_STATE, stateID_, initialState);
        Debug.echo("DataModel: set to final state!");              
    }
    
    public void removeFinalState(int stateID, boolean initialState) {
        automata.removeFinalState(((Integer)stateID).toString());
        notifyDataModelObservers(UserEventConstants.REMOVE_FINAL_STATE, stateID, initialState);
        Debug.echo("DataModel: remove final state!");
    }
    
    public Vector<Integer> getAllStates() {
        Vector<String> s = automata.getAllStates();
        Vector<Integer> t = new Vector<Integer>();
        for(int i = 0; i < s.size(); i++) {
            t.add(Integer.parseInt(s.get(i)));
        }
        return t;
    }
    
    public Vector<Integer> getSourcePortTransitions(int stateId) {
        Vector<String> s = automata.getSourcePortTransitions(((Integer)stateId).toString());
        Vector<Integer> t = new Vector<Integer>();
        for(int i = 0; i < s.size(); i++) {
            t.add(Integer.parseInt(s.get(i)));
        }
        return t;
    }
    
    public Vector<Integer> getTargetPortTransitions(int stateId) {
        Vector<String> s = automata.getTargetPortTransitions(((Integer)stateId).toString());
        Vector<Integer> t = new Vector<Integer>();
                for(int i = 0; i < s.size(); i++) {
            t.add(Integer.parseInt(s.get(i)));
        }
        return t;
    }
    
    public int getNumberOfStates() {
        return automata.getNumberOfStates();
    }
    
    public Vector getInitialStates() {
        Vector<String> sis = automata.getAllInitialTransitionsStateId();
        Vector<Integer> is = new Vector();
        for(int i = 0; i < sis.size(); i++) {
            is.add(Integer.parseInt(sis.get(i)));
        }
        return is;
    }
    
    public Vector<Integer> getFinalStates() {
        Vector<String> sfs = automata.getAllFinalTransitionsStateId();
        Vector<Integer> fs = new Vector();
        for(int i = 0; i < sfs.size(); i++) {
            fs.add(Integer.parseInt(sfs.get(i)));
        }
        return fs;
    }
    
    public Automata2.ValueType getValueType(){//add by mysterious.
        return automata.getValueType();
    }
    
    public void setValueType(Automata2.ValueType valueType){//add by mysterious.
        automata.setValueType(valueType);
        if(valueType.getMonoidType() == MonoidTypeE.FREE) {
            transitionLabel = valueType.getOneSymbol();
        } else if(valueType.getMonoidType() == MonoidTypeE.PRODUCT) {
            transitionLabel = "(" + valueType.getOneSymbol() + ", " + valueType.getOneSymbol() + ")";
        }        
    }    
    
    public boolean isInitial(int stateID) {
        Vector<Integer> is = getInitialStates();
        boolean res = false;
        for(int i = 0; i < is.size(); i++) {
            if(stateID == is.get(i)) {
                res = true;
            }
        }
        return res;
    }
    
    public boolean isFinal(int stateID) {
        Vector<Integer> fs = getFinalStates();
        boolean res = false;
        for(int i = 0; i < fs.size(); i++) {
            if(stateID == fs.get(i)) {
                res = true;
            }
        }
        return res;
    }
    
/* STATE in GEOMETRY */
    
    public Point2D.Float getStatePosition(int stateID) {
        return geometry.getStatePosition(((Integer)stateID).toString());
    }
    
    public void setStatePosition(int stateID, Point2D.Float point) {
        geometry.setStatePosition(((Integer)stateID).toString(), point);
        notifyDataModelObservers(UserEventConstants.CHANGE_STATE_POSITION, stateID, point);
				System.out.println("DataModel: changed state( " + Integer.toString(stateID) +
								" )'s position successfully to " + Double.toString(point.getX()) + ", " +
								Double.toString(point.getY()));
//        Debug.echo("DataModel: changed state's position successful");
    }
    
/* STATE in DRAWING */
    
    
    
/* TRANSITION in all */    
    
    public void addStraightTransition(int sourceStateID, int destinationStateID) {
	Label label = new Label(new TypedRegExp(new One()));
        int transitionID = createTransitionID();        
        automata.addTransition(((Integer)transitionID).toString(), ((Integer)sourceStateID).toString(), ((Integer)destinationStateID).toString(), transitionLabel);
//				automata.changeTransitionLabel(((Integer)transitionID).toString(), transitionLabel);
        automata.changeTransitionLabel(((Integer)transitionID).toString(), label);
//        geometry.setTransitionLabelDist(transitionID, defaultLabelDist);        
        drawing.addTransition(((Integer)transitionID).toString());
        drawing.setTransitionLineType(((Integer)transitionID).toString(), TypeConstants.STRAIGHT_LINE);
        if(sourceStateID == destinationStateID) {
//            geometry.setTransitionType(transitionID,  Geometry2.TransitionsGeometricData.TransitionTypeE.LOOP);
//            geometry.setTransitionLoopDir(((Integer)transitionID).toString(), defaultLoopDir);
//這邊要寫入一些drawing的資料
        }
        else {
//            geometry.setTransitionType(transitionID,  TransitionTypeE);
//這邊要寫入一些drawing的資料
        }              
//        geometry.setTransitionLabelPos(transitionID, );//left or right or middle
        Debug.echo("DataModel: New transition successful");
        notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, transitionLabel, TypeConstants.STRAIGHT_LINE);
    }
    
    public void addPolyTransition(int sourceStateID, int destinationStateID,
            ArrayList<Point2D.Float> points) {
        int transitionID = createTransitionID();
        automata.addTransition(((Integer)transitionID).toString(), 
                ((Integer)sourceStateID).toString(), ((Integer)destinationStateID).toString()
                , transitionLabel);
        drawing.addTransition(((Integer)transitionID).toString());
        drawing.setTransitionLineType(((Integer)transitionID).toString(), TypeConstants.POLY_LINE);
        Vector A = new Vector();
        for(int i = 0; i < points.size(); i++) {
            A.add(points.get(i));
        }
        drawing.setTransitionControlPoints(((Integer)transitionID).toString(), A);
        if(sourceStateID == destinationStateID) {
            //這邊要寫入一些資料    
        }
        else {
            //這邊要寫入一些資料
        }
        Debug.echo("DataModel: New transition successful");
        notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, transitionLabel, TypeConstants.POLY_LINE, points);
    }
    
    public void addCurveTransition(int sourceStateID, int destinationStateID) {
        int transitionID = createTransitionID();
        automata.addTransition(((Integer)transitionID).toString(), ((Integer)sourceStateID).toString(), ((Integer)destinationStateID).toString(), transitionLabel);
        drawing.addTransition(((Integer)transitionID).toString());
        drawing.setTransitionLineType(((Integer)transitionID).toString(), TypeConstants.CURVE);
        if(sourceStateID == destinationStateID) {
            //這邊要寫入一些資料    
        }
        else {
            //這邊要寫入一些資料
        }
        Debug.echo("DataModel: New transition successful");
        ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
        points.add(new Point2D.Float(0, 0));
        points.add(new Point2D.Float(0, 0));
        notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, transitionLabel, TypeConstants.CURVE, points);
    }
    
    public void addBezierTransition(int sourceStateID, int destinationStateID, ArrayList<Point2D.Float> points) {
        int transitionID = createTransitionID();
        Vector A = new Vector();
        for(int i = 0; i < points.size(); i++) {
            A.add(points.get(i));
        }
        automata.addTransition(((Integer)transitionID).toString(), ((Integer)sourceStateID).toString(), ((Integer)destinationStateID).toString(), transitionLabel);
        drawing.addTransition(((Integer)transitionID).toString());
        drawing.setTransitionLineType(((Integer)transitionID).toString(), TypeConstants.BEZIER_LINE);
        drawing.setTransitionControlPoints(((Integer)transitionID).toString(), A);
        if(sourceStateID == destinationStateID) {
            //這邊要寫入一些資料    
        }
        else {
            //這邊要寫入一些資料
        }
        Debug.echo("DataModel: New transition successful");
        notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, transitionLabel, TypeConstants.BEZIER_LINE, points);
    }
    
    public void addSplineTransition(int sourceStateID, int destinationStateID) {
        int transitionID = createTransitionID();
        automata.addTransition(((Integer)transitionID).toString(), ((Integer)sourceStateID).toString(), ((Integer)destinationStateID).toString(), transitionLabel);
        //這邊要寫入一些grawing的資料
        if(sourceStateID == destinationStateID) {
            //這邊要寫入一些資料    
        }
        else {
            //這邊要寫入一些資料
        }
        Debug.echo("DataModel: New transition successful");
        notifyDataModelObservers(transitionID, sourceStateID, destinationStateID, transitionLabel, TypeConstants.SPLINE_LINE);
    }
    
    public void addTransition(int sourceStateID_, int destinationStateID_, String label) {
        String sourceStateID = ((Integer)sourceStateID_).toString();
        String destinationStateID = ((Integer)destinationStateID_).toString();
        int transitionID = createTransitionID();
        automata.addTransition(((Integer)transitionID).toString(), sourceStateID, destinationStateID, label);
        drawing.addTransition(((Integer)transitionID).toString());
        drawing.setTransitionLineType(((Integer)transitionID).toString(), lineType);
        if(lineType == TypeConstants.POLY_LINE | lineType == TypeConstants.BEZIER_LINE) {
            
        }
        if(sourceStateID_ == destinationStateID_) {           
//            geometry.setTransitionType(transitionID,  Geometry2.TransitionsGeometricData.TransitionTypeE.LOOP);
//            geometry.setTransitionLoopDir(((Integer)transitionID).toString(), defaultLoopDir);
        }
        else {
//            geometry.setTransitionType(transitionID,  TransitionTypeE);
        }        
      
//        geometry.setTransitionLabelPos(transitionID, );//left or right or middle
        Debug.echo("DataModel: New transition successful");
        notifyDataModelObservers(transitionIDCounter, sourceStateID_, destinationStateID_, label, lineType);
    }
    
    public void reAddTransition(int transitionID) {
        String transitionID_ = ((Integer)transitionID).toString();
        String sourceStateID = deletedAutomata.getTransitionSource(transitionID_);
        String destinationStateID = deletedAutomata.getTransitionTarget(transitionID_);
        String label = deletedAutomata.getTransitionLabel(transitionID_);
        int transitionLineType = deletedDrawing.getTransitionLineType(transitionID_);
        
        automata.addTransition(transitionID_, sourceStateID, destinationStateID, label);
        drawing.addTransition(transitionID_);
        drawing.setTransitionLineType(transitionID_, transitionLineType);
        if(deletedDrawing.getTransitionControlPoints(transitionID_) != null) {
            drawing.setTransitionControlPoints(transitionID_, deletedDrawing.getTransitionControlPoints(transitionID_));
        }
        deletedAutomata.deleteTransition(((Integer)transitionID).toString());
        deletedDrawing.removeTransition(transitionID_);
        Debug.echo("DataModel: readd transition successful");
        if(transitionLineType == TypeConstants.POLY_LINE | transitionLineType == TypeConstants.BEZIER_LINE) {
            ArrayList points = new ArrayList();
//            points.add(new Point2D.Double(0, 0));
            if(drawing.getTransitionControlPoints(transitionID_) != null) {
                for(int i = 0; i < drawing.getTransitionControlPoints(transitionID_).size(); i++) {
                    points.add(drawing.getTransitionControlPoints(transitionID_).get(i));
                }
            }
//            points.add(new Point2D.Double(0, 0));
            notifyDataModelObservers(transitionID, Integer.parseInt(sourceStateID), Integer.parseInt(destinationStateID), label, transitionLineType, points);
        }
        else {
            notifyDataModelObservers(transitionID, Integer.parseInt(sourceStateID), Integer.parseInt(destinationStateID), label, transitionLineType);
        }
        
    }
    
    public void deleteTransition(int transitionID) {
        String transitionID_ = ((Integer)transitionID).toString();
        String sourceStateID = automata.getTransitionSource(transitionID_);
        String destinationStateID = automata.getTransitionTarget(transitionID_);
        String label = automata.getTransitionLabel(transitionID_);
        int transitionLineType = getTransitionLineType(transitionID);
        
        deletedAutomata.addTransition(transitionID_, sourceStateID, destinationStateID, label);
        deletedDrawing.addTransition(transitionID_);
        deletedDrawing.setTransitionLineType(transitionID_, transitionLineType);
        if(drawing.getTransitionControlPoints(transitionID_) != null) {
            deletedDrawing.setTransitionControlPoints(transitionID_, drawing.getTransitionControlPoints(transitionID_));
        }
        automata.deleteTransition(((Integer)transitionID).toString());
        drawing.removeTransition(transitionID_);
        Debug.echo("DataModel: Delete transition successful");
        notifyDataModelObservers(UserEventConstants.DELETE_TRANSITION, transitionID);
    }       
    
/* TRANSITION in AUTOMATA */    
    
    public void changeTransitionLabel(int transitionID_, String label) {
        String transitionID = ((Integer)transitionID_).toString();
        automata.changeTransitionLabel(transitionID, label);
        Debug.echo("DataModel: change transition label success!");
    }
    
    public void changeTransitionLabel(int transitionID_, String weight, String label) {
        String transitionID = ((Integer)transitionID_).toString();
        automata.changeTransitionLabel(transitionID, label, weight);
        Debug.echo("DataModel: change transition label success!");
    }
    
    public void setTransitionLabel(int transitionID_, Label label, String text) {
        String transitionID = ((Integer)transitionID_).toString();
//        automata.changeTransitionLabel(transitionID, text);
        automata.changeTransitionLabel(transitionID, label);
        notifyDataModelObservers(UserEventConstants.CHANGE_TRANSITION_LABEL, transitionID_, text);
    }
    
    public Vector<Integer> getAllTransitions() {
        Vector<String> s = automata.getAllTransitions();
        Vector<Integer> t = new Vector<Integer>();
            for(int i = 0; i < s.size(); i++) {
                t.add(Integer.parseInt(s.get(i)));
            }
        return t;
    }
    
    public int getTransitionSource(int transitionId) {
         return Integer.parseInt(automata.getTransitionSource(((Integer)transitionId).toString()));
    }
    
    public int getTransitionTarget(int transitionId) {
        return Integer.parseInt(automata.getTransitionTarget(((Integer)transitionId).toString()));
    }
    
    public String getTransitionLabel(int transitionID) {
        return automata.getTransitionLabel(((Integer)transitionID).toString());               
    }

		public Automata2.RegExpBody getTransitionRegExpBody(int transitionID) {
			return automata.getTransitionRegExpBody(transitionID);
		}
    
/* TRANSITION in GEOMETRY */
    
    
    
/* TRANSITION in DRAWING */
    
    public int getLineType() {
        return lineType;
    }
    
    public void setLineType(int type) {
        lineType = type;
    }
    
    public void setStraightTransition(int transitionID) {
        drawing.setTransitionType(((Integer)transitionID).toString(), TypeConstants.STRAIGHT_LINE);
        notifyDataModelObservers(UserEventConstants.SET_STRAIGHT_TRANSITION, transitionID);
    }
    
    public void setPolyTransition(int transitionID, ArrayList<Point2D.Float> points) {
        Vector A = new Vector();
        for(int i = 0; i < points.size(); i++) {
            A.add(points.get(i));
        }
        drawing.setTransitionType(((Integer)transitionID).toString(), TypeConstants.POLY_LINE);
        drawing.setTransitionControlPoints(((Integer)transitionID).toString(), A);
        notifyDataModelObservers(UserEventConstants.SET_POLY_TRANSITION, transitionID, points);
    }
    
    public void setCurveTransition(int transitionID) {
        drawing.setTransitionType(((Integer)transitionID).toString(), TypeConstants.CURVE);
        notifyDataModelObservers(UserEventConstants.SET_CURVE_TRANSITION, transitionID);
    }
    
    public void setBezierTransition(int transitionID, ArrayList<Point2D.Float> points) {
        Vector A = new Vector();
        for(int i = 0; i < points.size(); i++) {
            A.add(points.get(i));
        }
        drawing.setTransitionType(((Integer)transitionID).toString(), TypeConstants.BEZIER_LINE);
        drawing.setTransitionControlPoints(((Integer)transitionID).toString(), A);
        notifyDataModelObservers(UserEventConstants.SET_BEZIER_TRANSITION, transitionID, points);
    }
    
    public void setSplineTransition(int transitionID) {
        //這邊需要去grawing裡修改
        notifyDataModelObservers(UserEventConstants.SET_SPLINE_TRANSITION, transitionID);
    }
    
    public int getTransitionLineType(int transitionID) {
        return drawing.getTransitionLineType(((Integer)transitionID).toString());
    }
    
/* OTHERS */
    
    public void registerObserver(DataModelObserver o) {
        dataModelObservers.add(o);
    }
    
    public void removeObserver(DataModelObserver o) {
        int i = dataModelObservers.indexOf(o);
        if(i >= 0) {
            dataModelObservers.remove(i);
        }
    }
    
    private int createStateID() {
        stateIDCounter = stateIDCounter + 1;
        return stateIDCounter;
    }
    
    private int createTransitionID() {
        transitionIDCounter = transitionIDCounter + 1;
        return transitionIDCounter;
    }
    
    public int getStateIDCounter() {
        return stateIDCounter;
    }
    
    public int getTransitionIDCounter() {
        return transitionIDCounter;
    }
    
    public void newModel() {
        deletedAutomata = automata;
        deletedGeometry = geometry;
        deletedDrawing = drawing;
        automata = new Automata2();
        geometry = new Geometry2();
        drawing = new Drawing2();
    }
    
    public void setAutomata(Automata2 automata) {
        this.automata = automata;
    }
    
    public void setGeometry(Geometry2 geometry) {
        this.geometry = geometry;
    }
    
    public void setDrawing(Drawing2 drawing) {
        this.drawing = drawing;
    }
    
    public void reset() {
        Vector<Integer> states = getAllStates();
        Vector<Integer> transitions = getAllTransitions();
        int stateID;
        int transitionID;
        int sourceStateID;
        int destinationStateID;
        String label;
        for(int i = 0; i < states.size(); i++) {
            stateID = states.get(i);
            automata.deleteState(((Integer)stateID).toString());
            geometry.deleteState(((Integer)stateID).toString());
            drawing.removeState(((Integer)stateID).toString());
            notifyDataModelObservers(UserEventConstants.DELETE_STATE, stateID);
        }
        for(int i = 0; i < transitions.size(); i++) {
            transitionID = transitions.get(i);
            automata.deleteTransition(((Integer)transitionID).toString());
            geometry.deleteTransition(((Integer)transitionID).toString());
            drawing.removeTransition(((Integer)transitionID).toString());
            notifyDataModelObservers(UserEventConstants.DELETE_TRANSITION, transitionID);
        }
    }
    
}
