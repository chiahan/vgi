
import java.awt.geom.Point2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wkren
 */
public class Geometry {
    private Point2D location;

    /**
     * @return the location
     */
    public Point2D getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Point2D location) {
        this.location = location;
    }
    
    /**
     * @param location the location to set
     */
    public void setLocation(int x, int y) {
        this.location.setLocation(x, y);
    }
}
