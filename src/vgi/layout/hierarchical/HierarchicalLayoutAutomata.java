/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.SwingConstants;
import vgi.automata.Automata;
import vgi.automata.Group;
import vgi.automata.State;
import vgi.automata.StateGeometricData;
import vgi.automata.Transition;
import vgi.automata.TransitionGeometricData;
import vgi.geometrictools.Projection;
import vgi.layout.edgerouting.EdgeRoutingBranchingLayout;

/**
 *
 * @author x1213
 */
public class HierarchicalLayoutAutomata {
    Projection projection;
    Hashtable<State, Hvertex> stateTable;
    Hashtable<List<State>, Hgraph> groupTable;
    Hashtable<Transition, Hedge> EdgeTable;
    List<List<State>> groupList;
    List<List<Hvertex>> layer;
    List<List<Object>> layer2;
    Map<Object, Integer> assignment;
    Hgraph graph;
    Automata automata;
            
    public HierarchicalLayoutAutomata(){}
    
    public void preprocessing(Automata automata_) {
        automata = automata_;
        
        // table initialization
        stateTable = new Hashtable<State, Hvertex>();
        groupTable = new Hashtable<List<State>, Hgraph>();
        EdgeTable = new Hashtable<Transition, Hedge>();
        groupList = new ArrayList<List<State>>();
        
        // retrieve the list of group
        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata_.getAllStates());    
        while (!StateList.isEmpty()) {
            State s = StateList.get(0);
            List<State> g = automata_.getGroup(s);
            groupList.add(g);
            for (State ss: g) {
                StateList.remove(ss);
            }
        }
        
        // add vertices
        graph = new Hgraph();
        for (List<State> ls: groupList) {
            Hvertex v = graph.addVertex();
            if (ls.size() == 1) {
                // 1 stands for "the group contains exactly 1 state"
                //so it is not a group.
                stateTable.put(ls.get(0), v);
            }
            else {
                Hgraph sg = v.createSubgraph();
                groupTable.put(ls, sg);
                for (State s: ls) {
                    v = sg.addVertex();
                    stateTable.put(s, v);
                }
            }
        }
        
        // add edges
        StateList.addAll(automata_.getAllStates());
        for (State s: StateList) {
            List<Transition> tl = s.getIncomingTransitions();
            for (Transition t: tl) {
                if (!StateList.contains(t.getSourceState())) {
                    continue;
                }
                State s2 = t.getSourceState();
                Hedge e = graph.addEdge(stateTable.get(s2), stateTable.get(s));
                EdgeTable.put(t, e);
            }
        }
        
        // set up-left point, height, width
        Rectangle r = automata_.computeBox(StateList);
        graph.setUpleftPoint(new Point2D.Double(r.getMinX(), r.getMaxY()));
        for (List<State> ls: groupList) {
            r = automata_.computeBox(ls);
            if (ls.size() == 1) {
                Hvertex v = stateTable.get(ls.get(0));
                v.setHeight(r.getHeight());
                v.setWidth(r.getWidth());
            }
            else {
                Hgraph sg = groupTable.get(ls);
                Hvertex v = sg.parentVertex();
                v.setHeight(r.getHeight());
                v.setWidth(r.getWidth());
                sg.setUpleftPoint(new Point2D.Double(r.getMinX(), r.getMaxY()));
                for (State ss: ls) {
                    v = stateTable.get(ss);
                    StateGeometricData sgd = ss.getGeometricData();
                    v.setHeight(sgd.getHeight());
                    v.setWidth(sgd.getWidth());
                    v.setUpleftPoint(new Point2D.Double(sgd.getX()-sgd.getWidth()/2, sgd.getY()+sgd.getHeight()/2));              
                }
            }
        }   
    }
    
    public void cycleRemoval() {
        graph.naiveCycleRemoval();
    }

    public void layerAssignment() {
        layer = graph.layerAssignment_LPA();
    }

    public void vertexOrdering() {
        layer2 = graph.vertexOrdering_ESK(layer);
        //graph.BaryCenterVertexOrdering(layer);
    }
    
    public void xCoordinationAssignment() {
        assignment = graph.xCoordinateAssignment_BK_balanced(layer2);
    }
    
    public void postProcessing() {
        //(x,y) in the drawing algorithm = (xPos[x],yPos[y]) in the drawing
        List<Double> xPos = new ArrayList<Double>();
        List<Double> yPos = new ArrayList<Double>();
        //width, height of each column, row. 
        List<Double> xWidth = new ArrayList<Double>();
        List<Double> yHeight = new ArrayList<Double>();
        //xLayer = x-version of layer
        //yAssignment = y-version of assignment
        List<List<Hvertex>> xLayer = new ArrayList<List<Hvertex>>();
        Map<Object, Integer> yAssignment = new HashMap<Object, Integer>();
        //leftmost, upmost points of the drawing
        double leftmost = graph.getUpLeftPoint().getX();
        double upmost = graph.getUpLeftPoint().getY();
        //max_x = number of columns - 1 = maximum value of x in assignment
        int max_x = 0;
        for (Integer i: assignment.values()) {
            if (i > max_x) {
                max_x = i;
            }
        }
        
        //get xLayer by assignment
        for (int i = 0; i < graph.numVertex(); ++i) {
            Hvertex v = graph.getVertex(i);
            int x = assignment.get(v);
            while (xLayer.size() < x+1) {
                xLayer.add(new ArrayList<Hvertex>());
            }
            xLayer.get(x).add(v);
        }
        
        //get yAssignment by layer2
        for (int i = 0; i < layer2.size(); ++i) {
            for (int j = 0; j < layer2.get(i).size(); ++j) {
                yAssignment.put(layer2.get(i).get(j), i);
            }
        }    
        
        
        //calculate yHeight
        for (int i = 0; i < layer.size(); ++i) {
            double yy = 0.0;
            for (int j = 0; j < layer.get(i).size(); ++j) {
                if (layer.get(i).get(j).getHeight() > yy) {
                    yy = layer.get(i).get(j).getHeight();
                }
            }
            yHeight.add(yy);
        }
        
        //calculare xWidth
        for (int i = 0; i < xLayer.size(); ++i) {
            double xx = 0.0;
            for (int j = 0; j < xLayer.get(i).size(); ++j) {
                if (xLayer.get(i).get(j).getWidth() > xx) {
                    xx = xLayer.get(i).get(j).getWidth();
                }
            }
            if (xx <= 0.1) {
                xx = 50.0;
            }
            xWidth.add(xx);
        }
        for (int i = xLayer.size(); i <= max_x; ++i) {
            xWidth.add(50.0);
        }
        
        //calculate yPos
        for (int i = 0; i < layer.size(); ++i) {
            if (i == 0) {
                yPos.add(upmost - 0.5*yHeight.get(0));
            }
            else {
                yPos.add(yPos.get(i-1) - 2.0*yHeight.get(i-1) - 2.0*yHeight.get(i));
            }
        }
        
        //calculate xPos
        for (int i = 0; i < xWidth.size(); ++i) {
            if (i == 0) {
                //xPos.add(leftmost + 0.7*xWidth.get(0));
                xPos.add(leftmost);
            }
            else {
                xPos.add(xPos.get(i-1) + 0.7*xWidth.get(i-1) + 0.7*xWidth.get(i));
            }
        }
        
        //draw states
        for (List<State> ls: groupList) {
            if (ls.size() == 1) {
                // for state (s) not in any group
                // we set its coordinate to (xx,yy)
                State s = ls.get(0);
                Hvertex v = stateTable.get(ls.get(0));
                double xx = xPos.get(assignment.get(v));
                double yy = yPos.get(yAssignment.get(v));
                s.getGeometricData().setX(xx);
                s.getGeometricData().setY(yy);
            }
            else {
                Hgraph sg = groupTable.get(ls);
                Hvertex v = sg.parentVertex();
                double refx = sg.getUpLeftPoint().getX() + v.getWidth()/2.0;
                double refy = sg.getUpLeftPoint().getY() - v.getHeight()/2.0;
                double xx = xPos.get(assignment.get(v));
                double yy = yPos.get(yAssignment.get(v));
                for (State ss: ls) {
                    //for state (ss) in a group (ls), we move it (xx-refx, yy-refy)
                    StateGeometricData sgd = ss.getGeometricData();
                    sgd.setX(xx - refx + sgd.getX());
                    sgd.setY(yy - refy + sgd.getY());
                }
            }
        } 
             
        //draw transitions
        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata.getAllStates());  
        for (State s1: StateList) {
            List<Transition> tl = s1.getIncomingTransitions();
            for (Transition t: tl) {
                if (!StateList.contains(t.getSourceState())) {
                    continue;
                }
                State s2 = t.getSourceState();
                Hedge e = EdgeTable.get(t);
                Hvertex v1 = stateTable.get(s1);
                Hvertex v2 = stateTable.get(s2);
                //Now: t=(s1,s2), and e be its corresponding Hedge
                //     v1, v2 =  corresponding Hvertex of s1, s2
                //case 1: completely in a group
                if (v1.graph() == v2.graph() && v1.graph() != graph) {
                    Hgraph sg = v1.graph();
                    Hvertex v = sg.parentVertex();
                    double refx = sg.getUpLeftPoint().getX() + v.getWidth()/2.0;
                    double refy = sg.getUpLeftPoint().getY() - v.getHeight()/2.0;
                    double xx = xPos.get(assignment.get(v));
                    double yy = yPos.get(yAssignment.get(v));
                    //shift the control points.
                    TransitionGeometricData tgd = t.getGeometricData();
                    for (int i = 0; i < tgd.controlPoints.size(); ++i) {
                        tgd.controlPoints.get(i).setLocation(
                                tgd.controlPoints.get(i).getX() + xx-refx,
                                tgd.controlPoints.get(i).getY() + yy-refy);
                    }
                }
                //case 2: otherwise
                else {
                    TransitionGeometricData tgd = t.getGeometricData();
                    tgd.controlPoints.clear();
                    Hvertex vv1 = v1;
                    Hvertex vv2 = v2;
                    if (vv1.graph() != graph) {
                        vv1 = v1.graph().parentVertex();
                    }
                    if (vv2.graph() != graph) {
                        vv2 = v2.graph().parentVertex();
                    }
                    Hedge ee = e;
                    if (e.parent() != null) {
                        ee = e.parent();
                    }
                    int x1 = assignment.get(vv1);
                    int x2 = assignment.get(vv2);
                    int y1 = yAssignment.get(vv1);
                    int y2 = yAssignment.get(vv2);
                    if (!assignment.containsKey(ee)) {
                        //it is not a long edge
                        continue;
                    }
                    int ex = assignment.get(ee);
                    
                    if (y1 > y2) {
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y2+1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y2+1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y1-1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y1-1)));
                    }
                    else {
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y2-1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y2-1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y1+1)));
                        tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), yPos.get(y1+1)));
                    }
                    /*
                    if (y1 > y2) {
                        tgd.controlPoints.add(new Point2D.Double((xPos.get(x2)+xPos.get(ex))/2.0, (yPos.get(y2)+yPos.get(y2+1))/2.0));
                        tgd.controlPoints.add(new Point2D.Double(0.3*xPos.get(x2)+0.7*xPos.get(ex), 0.3*yPos.get(y2)+0.7*yPos.get(y2+1)));
                        tgd.controlPoints.add(new Point2D.Double(0.05*xPos.get(x2)+0.95*xPos.get(ex), 0.05*yPos.get(y2)+0.95*yPos.get(y2+1)));
                        for (int yi = y2+2; yi < y1; ++yi) {
                            tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), (yPos.get(yi)+yPos.get(yi-1))/2.0));
                        }
                        tgd.controlPoints.add(new Point2D.Double(0.05*xPos.get(x1)+0.95*xPos.get(ex), 0.05*yPos.get(y1)+0.95*yPos.get(y1-1)));
                        tgd.controlPoints.add(new Point2D.Double(0.3*xPos.get(x1)+0.7*xPos.get(ex), 0.3*yPos.get(y1)+0.7*yPos.get(y1-1)));
                        tgd.controlPoints.add(new Point2D.Double((xPos.get(x1)+xPos.get(ex))/2.0, (yPos.get(y1)+yPos.get(y1-1))/2.0));
                    }
                    else {
                        tgd.controlPoints.add(new Point2D.Double((xPos.get(x2)+xPos.get(ex))/2.0, (yPos.get(y2)+yPos.get(y2-1))/2.0));
                        tgd.controlPoints.add(new Point2D.Double(0.3*xPos.get(x2)+0.7*xPos.get(ex), 0.3*yPos.get(y2)+0.7*yPos.get(y2-1)));
                        tgd.controlPoints.add(new Point2D.Double(0.05*xPos.get(x2)+0.95*xPos.get(ex), 0.05*yPos.get(y2)+0.95*yPos.get(y2-1)));
                        for (int yi = y2-2; yi > y1; --yi) {
                            tgd.controlPoints.add(new Point2D.Double(xPos.get(ex), (yPos.get(yi)+yPos.get(yi+1))/2.0));
                        }   
                        tgd.controlPoints.add(new Point2D.Double(0.05*xPos.get(x1)+0.95*xPos.get(ex), 0.05*yPos.get(y1)+0.95*yPos.get(y1+1)));
                        tgd.controlPoints.add(new Point2D.Double(0.3*xPos.get(x1)+0.7*xPos.get(ex), 0.3*yPos.get(y1)+0.7*yPos.get(y1+1)));
                        tgd.controlPoints.add(new Point2D.Double((xPos.get(x1)+xPos.get(ex))/2.0, (yPos.get(y1)+yPos.get(y1+1))/2.0));
                    }*/
                }
            }
        }
    }

    public void doLayout(Automata automata_){
        automata_.refresh();
        mxGraph _graph = automata_.jgraphAutomata.graph;
        projection = automata_.getProjection();
        
        //Sugiyama's framework
        preprocessing(automata_);
        cycleRemoval();
        layerAssignment();
        vertexOrdering();
        xCoordinationAssignment();
        postProcessing();
        automata_.refresh();

        int i = 6; //break point for debugging.
    }
}
