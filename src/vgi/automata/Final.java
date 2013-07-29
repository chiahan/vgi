/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import java.awt.geom.Point2D;

/**
 *
 * @author reng
 */
public class Final {
    Object weight;
    public IniFinGeometricData geodata;
    public TransitionDrawingData drawdata;
    Final(){
        geodata=new IniFinGeometricData();
        geodata.direction=Double.valueOf(0);
        geodata.lengthRatio=0.5;
        drawdata=new TransitionDrawingData();
    }
    public Object getWeight(){
        return weight;
    }
    public void setWeight(Object ex){
        weight=ex;
    }
}
