/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.geom.Point2D;

/**
 *
 * @author wkren
 */
public class State {
    public class Geometry {
        Point2D x;
        Point2D y;
    }
    Geometry geometriData;
    Drawing drawingData;
    
    int id;
    String name;
    boolean initialFlag;
    boolean finalFlag;
}
