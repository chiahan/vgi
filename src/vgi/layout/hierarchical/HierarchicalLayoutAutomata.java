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
import java.util.Hashtable;
import java.util.List;
import javax.swing.SwingConstants;
import vgi.automata.Automata;
import vgi.automata.Group;
import vgi.automata.State;
import vgi.automata.StateGeometricData;
import vgi.automata.Transition;
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
    Hgraph graph;
            
    public HierarchicalLayoutAutomata(){}
    
    public void preprocessing(Automata automata_) {
        //table initialization
        stateTable = new Hashtable<State, Hvertex>();
        groupTable = new Hashtable<List<State>, Hgraph>();
        EdgeTable = new Hashtable<Transition, Hedge>();
        groupList = new ArrayList<List<State>>();
        
        //retrieve the list of group
        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata_.getAllStates());    
        while(!StateList.isEmpty()) {
            State s = StateList.get(0);
            List<State> g = automata_.getGroup(s);
            groupList.add(g);
            for (State ss: g) {
                StateList.remove(ss);
            }
        }
        
        //add vertices
        graph = new Hgraph();
        for (List<State> ls: groupList) {
            Hvertex v = graph.addVertex();
            if (ls.size() == 1) {
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
        
        //add edges
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
        
        //set up-left point, height, width
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
        layer = graph.layerAssignment_LPG();
    } 
     
    public void doLayout(Automata automata_){
        automata_.refresh();
        mxGraph _graph = automata_.jgraphAutomata.graph;
        projection=automata_.getProjection();
        
        //Sugiyama's framework
        preprocessing(automata_);
        cycleRemoval();
        layerAssignment();
        int i = 6; //break point for debugging.
    }
}
