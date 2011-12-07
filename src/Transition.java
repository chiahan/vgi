/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wkren
 */
public class Transition {

    /**
     * @return the geometricData
     */
    public Geometry getGeometricData() {
        return geometricData;
    }

    /**
     * @param geometricData the geometricData to set
     */
    public void setGeometricData(Geometry geometricData) {
        this.geometricData = geometricData;
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
     * @return the initialFlag
     */
    public boolean isInitialFlag() {
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

    /**
     * @return the sourceID
     */
    public int getSourceID() {
        return sourceID;
    }

    /**
     * @param sourceID the sourceID to set
     */
    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    /**
     * @return the targetID
     */
    public int getTargetID() {
        return targetID;
    }

    /**
     * @param targetID the targetID to set
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    public enum TransitionType {
        EdgeL, EdgeR, ArcL, ArcR, Loop
    }
    public class Geometry {
        TransitionType transitionType;
        int loopDirection;
        int labelPosition;
        int labelDistance;
        int initialDirection;
        int finalDirection;
    }
    private Geometry geometricData;
    private Drawing drawingData;
    
    private boolean initialFlag;
    private boolean finalFlag;
    private int sourceID;
    private int targetID;
    private String label;
    
    public Transition() {
        geometricData = new Geometry();
        drawingData = new Drawing();
        initialFlag = false;
        finalFlag = false;
        sourceID = -1;
        targetID = -1;
        label = new String();
    }
}
