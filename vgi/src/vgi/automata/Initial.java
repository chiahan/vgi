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
public class Initial {
    
    Object weight;
    public IniFinGeometricData geodata;
    public TransitionDrawingData drawdata;
                
    Initial(){
        geodata=new IniFinGeometricData();
        geodata.direction=Math.PI;
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
