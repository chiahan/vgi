/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author wkren
 */
public class Automata {

    /**
     * @return the geometryData
     */
    public Geometry getGeometryData() {
        return geometricData;
    }

    /**
     * @param geometryData the geometryData to set
     */
    public void setGeometryData(Point2D x, Point2D y) {
        this.geometricData.x = x;
        this.geometricData.y = y;
    }

    /**
     * @return the drawingData
     */
    public Drawing getDrawingData() {
        return drawingData;
    }

    /**
     * @param drawingData the drawingData to set
     */
    public void setDrawingData(Drawing drawingData) {
        this.drawingData = drawingData;
    }

    /**
     * @return the valueType
     */
    public Type getValueType() {
        return valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the states
     */
    public ArrayList<State> getAllStates() {
        return states;
    }
    
    public State getState(int index) {
        return states.get(index);
    }

    /**
     * @param states the states to set
     */
    public void setStates(ArrayList<State> states) {
        this.states = states;
    }
    
    public void addState(State s) {
        states.add(s);
    }

    /**
     * @return the transitions
     */
    public ArrayList<Transition> getAllTransitions() {
        return transitions;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }
    
    public void addTransition(Transition t) {
        transitions.add(t);
    }
    
    public class Geometry {
        Point2D x;
        Point2D y;
    }
    private Geometry geometricData;
    private Drawing drawingData;
    private Type valueType;
    private ArrayList<State> states;
    private ArrayList<Transition> transitions;
    
    public Automata() {
        geometricData = new Geometry();
        drawingData = new Drawing();
        valueType = new Type();
        states = new ArrayList<State>();
        transitions = new ArrayList<Transition>();
    }
    
    public Automata(Type type) {
        this();
        this.valueType = type;
    }
    
    public static void main(String args[]) {
        System.out.println("Staring from 'Automata Class'");
        System.out.println("Creating Monoid");
        Monoid monoid = new Monoid("0", 1, "a", 0, null);
        System.out.println("Creating Semiring");
        Semiring semiring = new Semiring("1", "0",
                                         Semiring.SemiringType.NUMERICAL,
                                         Semiring.SemiringSet.B,
                                         Semiring.SemiringOperation.CLASSICAL,
                                         "1", null, null);
        System.out.println("Creating Type");
        Type type = new Type(semiring, monoid);
        System.out.println("Creating empty Automata");
        Automata automata = new Automata();
        System.out.println("Creating Automata with TYPE");
        Automata automataWithType = new Automata(type);
    }
}
