/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

/**
 *
 * @author reng
 */
public class TransitionDrawingData {
    private String strokeColor;
    private float strokeWidth;
    private String startArrow;
    private String endArrow;

    public TransitionDrawingData(){
        this.strokeColor=null;
        this.strokeWidth=-1;
        this.startArrow=null;
        this.endArrow=null;
    }
    public TransitionDrawingData(String strokeColor_,float strokeWidth_,String startArrow_,String endArrow_){
        this.strokeColor=strokeColor_;
        this.strokeWidth=strokeWidth_;
        this.startArrow=startArrow_;
        this.endArrow=endArrow_;
        
    }
    
    public void setStrokeColor(String strokeColor_){
        strokeColor=strokeColor_;
    }
    public String getStrokeColor(){
        return strokeColor;
    }
    public void setStrokeWidth(float strokeWidth_){
        strokeWidth=strokeWidth_;
    }
    public float getStrokeWidth(){
        return strokeWidth;
    }
    public void setStartArrow(String startArrow_){
        startArrow=startArrow_;
    }
    public String getStartArrow(){
        return startArrow;
    }
    public void setEndArrow(String endArrow_){
        endArrow=endArrow_;
    }
    public String getEndArrow(){
        return endArrow;
    }
    
}
