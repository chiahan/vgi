/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package layout_algorithm;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * to put the sign of state loop in a space where less edges pass 
 * @author junli244
 */
public class ModifyStateLoop {

    /**
     * store nodes which are connected by a node
     */
    private HashMap transitionMap;
    /**
     * store the ID of the node in the graph
     */
    private HashMap stateMap;
    /*
     * the size of a state
     */
    private float stateSize;
    /*
     * the angle to put the loop sign
     */
    public double angle;
    /*
     * to store the region density of each of 8 directions for a node. there is 45 degrees between 2 directions
     */
    public int[] regionDensityArray;
    /**
     * @param transitionMap store nodes which are connected by a node
     * @param stateMap store the ID of the node in the graph
     * @param stateSize the size of a state
     * @author Junli Lu
     */
    public ModifyStateLoop(float stateSize, HashMap transitionMap, HashMap stateMap) {
        this.transitionMap = transitionMap;
        this.stateMap = stateMap;
        this.stateSize = stateSize;
        this.angle = 0;
        this.regionDensityArray = new int[8];
    }
   /**
     * @param centerID the id of the node which is counted for the port angle
     * @return the port angle
     * @author Junli Lu
     */
    public Point2D.Double FindLoopPortAlg(final int centerID) {
        Vector<DefaultGraphCell> connectNodeList = this.GetConnectedNodes(centerID);
        // 1st: sort all the connected nodes by their angle to center node
        Collections.sort(connectNodeList, new Comparator() {
            public int compare(Object state1, Object state2) {
                double result = CountAngleOfState((DefaultGraphCell) state1, (DefaultGraphCell) stateMap.get(centerID)) - CountAngleOfState((DefaultGraphCell) state2, (DefaultGraphCell) stateMap.get(centerID));
                if (result > 0) {
                    return 1;
                } else if (result == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        // 2nd: compare difference of angles between 2 nodes and find a max one
        double maxDifference = 0;
        if (connectNodeList.size() == 0) {
            this.angle = 3.14159/2;
            return new Point2D.Double(0.5 * GraphConstants.PERMILLE, 0);
        }
        DefaultGraphCell startState = connectNodeList.get(0);
        for (int i = 0; i < connectNodeList.size() - 1; i++) {
            double tmp1 = CountAngleOfState(connectNodeList.get(i), (DefaultGraphCell) this.stateMap.get(centerID));
            double tmp2 = CountAngleOfState(connectNodeList.get(i + 1), (DefaultGraphCell) this.stateMap.get(centerID));
            this.regionDensityArray[this.DecideRegionIndex(tmp1)]++;
            double tmp = tmp2 - tmp1;
            if (tmp > maxDifference) {
                maxDifference = tmp;
                startState = connectNodeList.get(i);
            }
        }
        double tmp1 = CountAngleOfState(connectNodeList.lastElement(), (DefaultGraphCell) this.stateMap.get(centerID));
        this.regionDensityArray[this.DecideRegionIndex(tmp1)]++;
        double tmp2 = (Math.PI * 2) - (CountAngleOfState(connectNodeList.lastElement(), (DefaultGraphCell) this.stateMap.get(centerID)) - CountAngleOfState(connectNodeList.firstElement(), (DefaultGraphCell) stateMap.get(centerID)));
        if (tmp2 > maxDifference) {
            maxDifference = tmp2;
            startState = connectNodeList.lastElement();
        }
        // 3rd: count angle and position of loop's port
        double finalAngle = CountAngleOfState(startState, (DefaultGraphCell) this.stateMap.get(centerID)) + maxDifference / 2;
        if (finalAngle > (Math.PI * 2)) {
            finalAngle -= (Math.PI * 2);
        }
        this.angle = finalAngle;
        this.regionDensityArray[this.DecideRegionIndex(this.angle)]++;
        return CountPortPosition((DefaultGraphCell) this.stateMap.get(centerID), finalAngle);
    }
   /**
     * @param statePoint the coordinate of the state
     * @param angle the angle that the control point rotate
     * @return the coordinate of the control point
     * @author Junli Lu
     */
    public Point2D.Double CountControlPointRotatePosition(Point2D.Double statePoint, double angle) {
        // Let statePoint'y product minus
        Point2D.Double tmpPoint = new Point2D.Double(statePoint.getX(), statePoint.getY() * (-1));
        // rotate point
        double x = tmpPoint.getX() * Math.cos(angle) + (tmpPoint.getY() * Math.sin(angle) * (-1));
        double y = tmpPoint.getX() * Math.sin(angle) + (tmpPoint.getY() * Math.cos(angle));
        // transform back
        Point2D.Double resultPoint = new Point2D.Double(x,-1*y);
        return resultPoint;
    }
   /**
     * @param angle
     * @return the index of the region where the angle belong
     * @author Junli Lu
     */
    protected int DecideRegionIndex(double angle) {
        double lowerBound = -1*(Math.PI/8);
        double upperBound = (Math.PI/8);
        int index = 0;
        for(int i = 0;i < 8;i++) {
            if((angle >= lowerBound) && (angle < upperBound)) {
                index = i;
                break;
            }
            lowerBound += (Math.PI/4);
            upperBound += (Math.PI/4);
        }
        return index;
    }
   /**
     * @param angle the angle of the port
     * @return the actual positon of the port
     * @author Junli Lu
     */
    protected Point2D.Double CountPortPosition(DefaultGraphCell state, double angle) {
        double stateRadius = Double.parseDouble(Float.toString(this.stateSize)) / 2;
        Point2D.Double centerPoint = new Point2D.Double(stateRadius, stateRadius);
        double x = centerPoint.getX() + (stateRadius * Math.cos(angle));
        double y = centerPoint.getY() - (stateRadius * Math.sin(angle));
        Point2D.Double result = new Point2D.Double((x / this.stateSize) * GraphConstants.PERMILLE, (y / this.stateSize) * GraphConstants.PERMILLE);
        return result;
    }
   /**
     * @param vector a vector
     * @return the cosine value of this vector
     * @author Junli Lu
     */
    protected double CountCosValueFromVector(Point2D vector) {
        double result = vector.getX() /
                Math.pow((Math.pow(vector.getX(), 2.0) + Math.pow(vector.getY(), 2.0)), 0.5);
        return result;
    }
   /**
     * @param state a node in graph
     * @param centerState the node we work in 
     * @return the angle between the two states
     *  @author Junli Lu
     */
    protected double CountAngleOfState(DefaultGraphCell state, DefaultGraphCell centerState) {
        final double center_x = GraphConstants.getBounds(centerState.getAttributes()).getX();
        final double center_y = GraphConstants.getBounds(centerState.getAttributes()).getY() * (-1);

        double angle;
        Rectangle2D point = GraphConstants.getBounds(state.getAttributes());
        if ((point.getY() * (-1) - center_y) >= 0) {
            angle = Math.acos(CountCosValueFromVector(new Point2D.Double(point.getX() - center_x, (point.getY() * (-1)) - center_y)));
        } else {
            angle = (Math.PI * 2) - Math.acos(CountCosValueFromVector(new Point2D.Double(point.getX() - center_x, (point.getY() * (-1)) - center_y)));
        }
        return angle;
    }
    /**
     * @param stateID the id of a node
     * @return the list of nodes which are connected to the node with stateID
     * @author Junli Lu
     */
    protected Vector<DefaultGraphCell> GetConnectedNodes(int stateID) {
        Vector<DefaultGraphCell> connectedNodes = new Vector<DefaultGraphCell>(); //juliu
        DefaultGraphCell centerState = (DefaultGraphCell) this.stateMap.get(stateID);
        centerState = (DefaultGraphCell) centerState.getChildAt(0);
        DefaultGraphCell sourceState;
        DefaultGraphCell targetState;
        DefaultEdge transition;

        Iterator idSets = this.transitionMap.keySet().iterator();
        while (idSets.hasNext()) {
            int transitionID = Integer.parseInt(idSets.next().toString());
            if (transitionID == -99) {
                continue;
            }
            transition = (DefaultEdge) this.transitionMap.get(transitionID);
            DefaultPort sourcePort = (DefaultPort) transition.getSource();
            DefaultPort targetPort = (DefaultPort) transition.getTarget();
						if(sourcePort == null || targetPort == null){
//							System.out.println("hello world");
							continue;
						}
						targetState = (DefaultGraphCell) targetPort.getParent();
            sourceState = (DefaultGraphCell) sourcePort.getParent();

            if (sourceState == centerState && sourceState != targetState) {
                connectedNodes.add(targetState);
            } else if (targetState == centerState && sourceState != targetState) {
                connectedNodes.add(sourceState);
            }
        }
        return connectedNodes;
    }
}
