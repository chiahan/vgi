package model;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;
import model.Automata2.Label;

/**
 * This class is used to store the information of automata.
 *
 * @author rasielchang
 */

public interface DataModelInterface {

/* default value */
    
    void initialize();
        
    void modifyDefaultLoopDir(int loopDir);
    
    void modifyDefaultLabelDist(float labelDist);
    
/* STATE in all */
    
    void addState(Point2D.Float point);
    
    void reAddState(int stateID);
    
    void deleteState(int stateID);
    
/* STATE in AUTOMATA */
    
    String getStateName(int stateID);
    
    void changeStateName(int stateID, String name);
    
    void setInitialState(int stateID, boolean finalState);
    
    void setFinalState(int stateID, boolean initialState);
    
    void removeInitialState(int stateID, boolean finalState);
    
    void removeFinalState(int stateID, boolean initialState);
    
    Vector<Integer> getAllStates();
    
    Vector<Integer> getSourcePortTransitions(int stateId);
    
    Vector<Integer> getTargetPortTransitions(int stateId);
    
    int getNumberOfStates();
    
    Vector<Integer> getInitialStates();
    
    Vector<Integer> getFinalStates();
    
    boolean isInitial(int stateID);
    
    boolean isFinal(int stateID);
    
/* STATE in GEOMETRY */
    
    Point2D.Float getStatePosition(int stateID);
    
    void setStatePosition(int stateID, Point2D.Float point);
    
    
    
/* STATE in DRAWING */    
    
    
    
    
    
/* TRANSITION in all */
    
    void addStraightTransition(int sourceStateID, int destinationStateID);
    
    void addPolyTransition(int sourceStateID, int destinationStateID, ArrayList<Point2D.Float> points);
    
    void addCurveTransition(int sourceStateID, int destinationStateID);
    
    void addBezierTransition(int sourceStateID, int destinationStateID, ArrayList<Point2D.Float> points);
    
    void addSplineTransition(int sourceStateID, int destinationStateID);
    
    void addTransition(int sourceStateID, int destinationStateID, String label);
    
    void reAddTransition(int transitionID);
            
    void deleteTransition(int transitionID);
    
/* TRANSITION in AUTOMATA */
    
    void changeTransitionLabel(int transitionID, String label);
    
    void changeTransitionLabel(int transitionID, String weight, String label);
    
    void setTransitionLabel(int transitionID, Label label, String text);
    
    Vector<Integer> getAllTransitions();
    
    int getTransitionSource(int transitionId);
    
    int getTransitionTarget(int transitionId);
    
    String getTransitionLabel(int transitionID);
		
		Automata2.RegExpBody getTransitionRegExpBody(int transitionID);
    
/* TRANSITION in GEOMETRY */
    

    
/* TRANSITION in DRAWING */
    
    int getLineType();
    
    void setLineType(int type);
    
    void setStraightTransition(int transitionID);
    
    void setPolyTransition(int transitionID, ArrayList<Point2D.Float> points);
    
    void setCurveTransition(int transitionID);
    
    void setBezierTransition(int transitionID, ArrayList<Point2D.Float> points);
    
    void setSplineTransition(int transitionID);
    
    int getTransitionLineType(int transitionID);
    
/* OTHERS */
    
    void registerObserver(DataModelObserver o);
    
    void removeObserver(DataModelObserver o);
    
    int getStateIDCounter();
    
    int getTransitionIDCounter();
    
    void newModel();
    
    Automata2.ValueType getValueType();
    
    void setValueType(Automata2.ValueType valueType);
    
    void notifyDataModelObservers();
    
    void setAutomata(Automata2 automata);
    
    void setGeometry(Geometry2 geometry);
    
    void setDrawing(Drawing2 drawing);
    
    void reset();
    
}
