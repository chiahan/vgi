/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author x1213
 */
public class Hgraph {
    public Hgraph() {
        vertexList = new ArrayList();
        inEdgeList = new ArrayList();
        outEdgeList = new ArrayList();
        parentVertex = null;  
        upleftPoint = null;
    }
   /*///////////////////////////////////////////////
    *    Setting
    *///////////////////////////////////////////////
    public void setUpleftPoint(Point2D p) {
        upleftPoint = p;
    }
    public void setParentVertex(Hvertex v) {
        parentVertex = v;
    }
    public Hvertex addVertex() {
        Hvertex v = new Hvertex();
        v.setGraph(this);
        vertexList.add(v);
        return v;
    }
    public Hedge addEdge(Hvertex s, Hvertex t) {
        Hvertex ss, tt;
        if (s.graph() == this) {
            ss = s;
        }
        else {
            ss = s.graph().parentVertex();
        }
        if (t.graph() == this) {
            tt = t;
        }
        else {
            tt = t.graph().parentVertex();
        }
        
        //try to find an edge {s,t}
        Hedge e;
        if (s != ss) {
            e = s.graph().findEdge(s,t);
        }
        else {
            e = t.graph().findEdge(s,t);
        }
        if (e != null) {
            e.incrMultiplicity();
            return e;
        }
        
        //if not found, create one.
        e = new Hedge(1);
        e.setSource(s);
        e.setTarget(t);
        
        //we add edge to s,t only when they are in the same subgraph.
        if (t.graph() == s.graph()) {
            s.addEdge(e);
            t.addEdge(e);
        }
        
        //add to edgelist
        if (s == ss && t == tt) {
            inEdgeList.add(e);
        }
        //s,t are all in groups
        else if (s.graph() != this &&  t.graph() != this){
            //in different groups
            if (s.graph() != t.graph()) {
                s.graph().outEdgeList.add(e);
                t.graph().outEdgeList.add(e);
            }
            //in the same group
            else {
                s.graph().inEdgeList.add(e);           
            }
        }
        else {
            //t is a vertex this graph, s in a group
            if (s.graph() != this) {
                s.graph().outEdgeList.add(e);
            }
            //s is a vertex this graph, t in a group
            if (t.graph() != this) {
                t.graph().outEdgeList.add(e);
            }
        }
        
        
        //if it involves some grouping, but not in the same group... 
        if ((s != ss || t != tt) && (s.graph() != t.graph())) {
            Hedge e2 = findEdge(ss,tt);
            //if not found, create one.
            if (e2 == null) {
                e2 = new Hedge(0);
                inEdgeList.add(e2);
                e2.setSource(ss);
                e2.setTarget(tt);  
                ss.addEdge(e2);
                tt.addEdge(e2);
            }
            //add e to e2
            e2.addSubedge(e);
            e.setParent(e2);
        }
        
        return e;
    } 
   /*///////////////////////////////////////////////
    *    Layout algorithm
    *///////////////////////////////////////////////
    //An extremely simple algorithm: 
    //  reverse all edges that contradict with the ordering in the vertexList.
    public void naiveCycleRemoval() {
        for (Hedge e : inEdgeList) {
            Hvertex s = e.source();
            Hvertex t = e.target();
            if (vertexList.indexOf(s) > vertexList.indexOf(t)) {
                e.reverse();
            }
        }
    }
    //The longest path algorithm (see p.421 of that survey)
    //p.s. the implementation here is extremely simple that the time complexity is greatly sacrificed.
    public List<List<Hvertex>> layerAssignment_LPG() {
        List<List<Hvertex>> layer = new ArrayList<List<Hvertex>>();
        List<Hvertex> remained = new ArrayList<Hvertex>();
        remained.addAll(vertexList);
        while (!remained.isEmpty()) {
            List<Hvertex> currentLayer = new ArrayList<Hvertex>();
            //find the vertices such that all their inadj have been assigned to the previous layers. 
            for (Hvertex v : remained) {
                boolean test = true;
                for (int i = 0; i < v.inDeg(); ++i) {
                    if (remained.contains(v.inAdj(i))) {
                        test = false;
                        continue;
                    }
                }
                if (test) {
                    currentLayer.add(v);
                }
            }
            //update: remained, layer
            layer.add(currentLayer);
            remained.removeAll(currentLayer);
        }
        return layer;
    }
   /*///////////////////////////////////////////////
    *    Others
    *///////////////////////////////////////////////
    public Hedge findEdge(Hvertex s, Hvertex t) {
        for (Hedge e : inEdgeList) {
            if (e.source() == s && e.target() == t || e.source() == t && e.target() == s) {
                return e;
            }
        }
        for (Hedge e : outEdgeList) {
            if (e.source() == s && e.target() == t || e.source() == t && e.target() == s) {
                return e;
            }
        }
        return null;
    }
    public Hvertex parentVertex() {
        return parentVertex;
    }
   /*///////////////////////////////////////////////
    *    Members
    *///////////////////////////////////////////////   
    private Point2D upleftPoint; // the most up-left point
    private List<Hvertex> vertexList; //set of all vertices in the graph
    private List<Hedge> inEdgeList; //edge within the graph
    private List<Hedge> outEdgeList; //edge linking to outside of the graph
    private Hvertex parentVertex; // the vertex representing this graph. If this graph is not a subgraph, it is set to null.
}
