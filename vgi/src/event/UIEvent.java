package event;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class is used to discribe a event.
 * 
 * @author rasielchang
 */
public class UIEvent {
    private UserEventConstants event;
    private Point2D.Float point;
    private int cellID;
    private int iData;
    private String sData;
    private float fData;
    private boolean cellType;
    private ArrayList aData;
    private boolean finish;
    private Object oData;
    private Vector[] vData;
    private boolean bData;
    
    /**
    * The constructor.
    *
    * @param event is the id.
    *
    * @author rasielchang
    */
    public UIEvent(UserEventConstants event) {        
        this.event = event;
        cellID = -1;
        finish = true;
        iData = -65536;
    }

    /**
    * Use to set event.
    *
    * @param event is the new event id.
    *
    * @author rasielchang
    */
    public void setEvent(UserEventConstants event) {
        this.event = event;
    }

    /**
    * Use to get event.
    *
    * @return event id
    *
    * @author rasielchang
    */
    public UserEventConstants getEvent() {
        return event;
    }

    /**
    * Use to set the coordinate on canvas of this event.
    *
    * @param point is the coordinate.
    *
    * @author rasielchang
    */
    public void setPoint(Point2D.Float point) {
        this.point = point;
    }

    /**
    * Use to get coordinate.
    *
    * @return the coordinate of this event.
    *
    * @author rasielchang
    */
    public Point2D.Float getPoint() {
        return point;
    }

    /**
    * Use to set the id whihc could be a state id or transition id.
    *
    * @param cellID is the id.
    *
    * @author rasielchang
    */
    public void setCellID(int cellID) {
        this.cellID = cellID;
    }

    /**
    * Use to get the state or transition id.
    *
    * @return the cell id.
    *
    * @author rasielchang
    */
    public int getCellID() {
        return cellID;
    }

    /**
    * Use to set the state id.
    *
    * @param stateID is the id.
    *
    * @author rasielchang
    */
    public void setStateID(int stateID) {
        cellID = stateID;
        cellType = false;
    }

    /**
    * Use to get the state id.
    *
    * @return the state id, -1 means it is not a state.
    *
    * @author rasielchang
    */
    public int getStateID() {
        if(cellType == false) {
            return cellID;
        }
        else {
            return -1;
        }           
    }

    /**
    * Use to set the transition id.
    *
    * @param transitionID is the id.
    *
    * @author rasielchang
    */
    public void setTransitionID(int transitionID) {
        cellID = transitionID;
        cellType = true;
    }

    /**
    * Use to get the transition id.
    *
    * @return the transition id, -1 means it is not a transition.
    *
    * @author rasielchang
    */
    public int getTransitionID() {
        if(cellType == true) {
            return cellID;
        }
        else {
            return -1;
        }
    }

    /**
    * Use to set the iData.
    *
    * @param data is the new iData.
    *
    * @author rasielchang
    */
    public void setiData(int data) {
        iData = data;
    }

    /**
    * Use to get the iData.
    *
    * @return the iData.
    *
    * @author rasielchang
    */
    public int getiData() {
        return iData;
    }

    /**
    * Use to set the sData.
    *
    * @param data is the new sData.
    *
    * @author rasielchang
    */
    public void setsData(String data) {
        sData = data;
    }

    /**
    * Use to get the sData.
    *
    * @return the sData.
    *
    * @author rasielchang
    */
    public String getsData() {
        return sData;
    }

    /**
    * Use to set the aData.
    *
    * @param data is the new aData.
    *
    * @author rasielchang
    */
    public void setaData(ArrayList data) {
        aData = data;
    }

    /**
    * Use to get the sData.
    *
    * @return the sData.
    *
    * @author rasielchang
    */
    public ArrayList getaData() {
        return aData;
    }

    /**
    * Use to set finish to true.
    *
    * @author rasielchang
    */
    public void finish() {
        finish = true;
    }

    /**
    * Use to set finish to false.
    *
    * @author rasielchang
    */
    public void notFinish() {
        finish = false;
    }

    /**
    * Use to get the finish.
    *
    * @return the finish.
    *
    * @author rasielchang
    */
    public boolean getFinish() {
        return finish;
    }

    /**
    * Use to set the fData.
    *
    * @param data is the new fData.
    *
    * @author rasielchang
    */
    public void setfData(float data) {
        fData = data;
    }

    /**
    * Use to get the fData.
    *
    * @return the fData.
    *
    * @author rasielchang
    */
    public float getfData() {
        return fData;        
    }

    /**
    * Use to set the oData.
    *
    * @param data is the new oData.
    *
    * @author rasielchang
    */
    public void setoData(Object data) {
        oData = data;
    }

    /**
    * Use to get the oData.
    *
    * @return the oData.
    *
    * @author rasielchang
    */
    public Object getoData() {
        return oData;
    }

    /**
    * Use to construct new vData.
    *
    * @param i is the size of the vector.
    *
    * @author rasielchang
    */
    public void newArrayVector(int i) {
        vData = new Vector[i];
    }

    /**
    * Use to set the vData.
    *
    * @param data is the new vData.
    *
    * @author rasielchang
    */
    public void setvData(Vector v,int i) {
        vData[i] = v;
    }

    /**
    * Use to get the vData.
    *
    * @return the vData.
    *
    * @author rasielchang
    */
    public Vector getvData(int i) {
        return vData[i];
    }

    /**
    * Use to set the bData.
    *
    * @param data is the new bData.
    *
    * @author rasielchang
    */
    public void setBooleanData(boolean data) {
        bData = data;
    }

    /**
    * Use to get the bData.
    *
    * @return the bData.
    *
    * @author rasielchang
    */
    public boolean getBooleanData() {
        return bData;
    }
}
