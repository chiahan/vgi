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

    /**
     * @return the geometriData
     */
    public Geometry getGeometriData() {
        return geometricData;
    }

    /**
     * @param geometriData the geometriData to set
     */
    public void setGeometriData(Point2D x, Point2D y) {
        this.geometricData.x = x;
        this.geometricData.y = y;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the initialFlag
     */
    public boolean isInitial() {
        return initialFlag;
    }

    /**
     * @param initialFlag the initialFlag to set
     */
    public void setInitial(boolean initialFlag) {
        this.initialFlag = initialFlag;
    }

    /**
     * @return the finalFlag
     */
    public boolean isFinal() {
        return finalFlag;
    }

    /**
     * @param finalFlag the finalFlag to set
     */
    public void setFinal(boolean finalFlag) {
        this.finalFlag = finalFlag;
    }
    public class Geometry {
        Point2D x;
        Point2D y;
    }
    private Geometry geometricData;
    Drawing drawingData;
    
    private int id;
    private String name;
    private boolean initialFlag;
    private boolean finalFlag;
    
    public State() {
        id = -1;
        drawingData = new Drawing();
        initialFlag = false;
        finalFlag = false;
        name = new String();
    }
}
