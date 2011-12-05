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
    public class Geometry {
        Point2D x;
        Point2D y;
    }
    Geometry geometryData;
    Drawing drawingData;
    Type valueType;
    ArrayList<State> states;
    ArrayList<Transition> transitions;
    
    public Automata() {
        geometryData = new Geometry();
        drawingData = new Drawing();
        valueType = new Type();
        states = new ArrayList<State>();
        transitions = new ArrayList<Transition>();
    }
}
