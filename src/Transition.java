/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wkren
 */
public class Transition {
    public enum TransitionType {
        EdgeL, EdgeR,
        ArcL, ArcR,
        Loop
    }
    public class Geometry {
        TransitionType transitionType;
        int loopDir;
        int labelPos;
        int labelDist;
    }
    Geometry geometriData;
    Drawing drawingData;
    
    boolean initialFlag;
    boolean finalFlag;
    int source;
    int target;
    String label;
}
