/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author reng
 */
public class TransitionGeometricData {
    public Point2D.Double labelPosAndDist;
    public Point2D.Double labelOffset;
    public List<Point2D> controlPoints;

    public TransitionGeometricData() {
            this.labelPosAndDist = null;
            this.labelOffset = null;
            this.controlPoints = new ArrayList<Point2D>();
    }
    public void addControlPoint(Point2D point,boolean reverse){
        controlPoints.add(point);
        Collections.sort(controlPoints, new Comparator<Point2D>(){
                    @Override
                    public int compare(Point2D t, Point2D t1) {
                        int flag =Double.compare(t.getX(),t1.getX());
                        if (flag == 0) {
                            return Double.compare(t.getY(),t1.getY());
                        } else {
                            return flag;
                        }
                    }
                            
         });
       if(reverse) Collections.reverse(controlPoints);
    }
    public void addControlPoint(Point2D point,boolean reverse,int index){
        controlPoints.add(index,point);
        Collections.sort(controlPoints, new Comparator<Point2D>(){
                    @Override
                    public int compare(Point2D t, Point2D t1) {
                        int flag =Double.compare(t.getX(),t1.getX());
                        if (flag == 0) {
                            return Double.compare(t.getY(),t1.getY());
                        } else {
                            return flag;
                        }
                    }
                            
         });
       if(reverse) Collections.reverse(controlPoints);
    }
}
