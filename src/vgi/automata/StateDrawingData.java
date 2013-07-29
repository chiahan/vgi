/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

/**
 *
 * @author reng
 */
public class StateDrawingData {
    private String fillColor;
    private String strokeColor;
    private float strokeWidth;
    private float fontSize;
    private String fontColor;
    
    public StateDrawingData(){
        this.fillColor=null;
        this.strokeColor=null;
        this.strokeWidth=-1;
        this.fontSize=-1;
        this.fontColor=null;
    }
    public StateDrawingData(String fc,String sc,float w){
        this.fillColor=fc;
        this.strokeColor=sc;
        this.strokeWidth=w;
    }
    public void setFillColor(String fc){
        fillColor=fc;
    }
    public String getFillColor(){
        return fillColor;
    }
    public void setStrokeColor(String sc){
        strokeColor=sc;
    }
    public String getStrokeColor(){
        return strokeColor;
    }
    public void setStrokeWidth(float w){
        strokeWidth=w;
    }
    public float getStrokeWidth(){
        return strokeWidth;
    }
    public float getFontSize(){
        return fontSize;
    }
    public void setFontSize(float s){
        fontSize=s;
    }
    public String getFontColor(){
        return fontColor;
    }
    public void setFontColor(String fc){
        fontColor=fc;
    }
    
}
