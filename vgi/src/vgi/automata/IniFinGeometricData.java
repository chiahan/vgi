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
public class IniFinGeometricData {
        public Double direction;
        public Double lengthRatio; // ratio to the width of state
        public Point2D.Double labelPosAndDist;
        public Point2D.Double labelOffset;
        public IniFinGeometricData(){
            labelPosAndDist=null;
            labelOffset=null;
            direction=null;
            lengthRatio=null;
        }
        public IniFinGeometricData(double d,double lr,Point2D.Double pd,Point2D.Double offset){
            direction=d; lengthRatio=lr;
            labelPosAndDist=pd;
            labelOffset=offset;
        }
    
}
