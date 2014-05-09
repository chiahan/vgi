/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import java.awt.geom.Point2D;
import java.util.*;
import vgi.layout.hierarchical.Hedge;
/**
 *
 * @author x1213
 */
public class Hvertex {
    public Hvertex() {
        subgraph = null;
        outAdjList = new ArrayList();
        inAdjList = new ArrayList();
        height = 0.0;
        width = 0.0;
        upleftPoint = null;
    }
   /*///////////////////////////////////////////////
    *    Setting
    *///////////////////////////////////////////////
    public void setUpleftPoint(Point2D p) {
        upleftPoint = p;
    }
    public void setHeight(double p) {
        height = p;
    }
    public void setWidth(double p) {
        width = p;
    }
    public void addEdge(Hedge e) {
        if (e.source() == this) {
            outAdjList.add(e);
        }
        else if (e.target() == this) {
            inAdjList.add(e);
        }
    } 
    public void setGraph(Hgraph g) {
        graph = g;
    }     
    public Hgraph createSubgraph() {
        subgraph = new Hgraph();
        subgraph.setParentVertex(this);
        return subgraph;
    }     
   /*///////////////////////////////////////////////
    *    Adjacency
    *///////////////////////////////////////////////
    public Hvertex inAdj(int i) {
        return inAdjList.get(i).getNext(this);
    }
    public Hvertex outAdj(int i) {
        return outAdjList.get(i).getNext(this);
    }
    public Hedge inEdge(int i) {
        return inAdjList.get(i);
    }
    public Hedge outEdge(int i) {
        return outAdjList.get(i);
    }
    public int inDeg() {
        return inAdjList.size();
    }
    public int outDeg() {
        return outAdjList.size();
    }
    public int inDegMulti() {
        int d = 0;
        for (int i = 0; i < inDeg(); ++i) {
            d += inAdjList.get(i).multiplicity();
        }
        return d;
    }    
    public int outDegMulti() {
        int d = 0;
        for (int i = 0; i < outDeg(); ++i) {
            d += outAdjList.get(i).multiplicity();
        }
        return d;
    }    
   /*///////////////////////////////////////////////
    *    Other
    *///////////////////////////////////////////////    
    public Hgraph subgraph() {
        return subgraph;
    }
    public Hgraph graph() {
        return graph;
    }
    boolean isGroup() {
        return subgraph != null;
    }
    public Hedge findEdge(Hvertex t) {
        for (Hedge e : outAdjList) {
            if (e.source() == t || e.target() == t) {
                return e;
            }
        }
        for (Hedge e : inAdjList) {
            if (e.source() == t || e.target() == t) {
                return e;
            }
        }
        return null;
    }
    public void edgeRemove(Hedge e) {
        outAdjList.remove(e);
        inAdjList.remove(e);
    }
   /*///////////////////////////////////////////////
    *    Members
    *///////////////////////////////////////////////   
    private List<Hedge> outAdjList; //The edges emanating from the vertex.
    private List<Hedge> inAdjList; //The edges pointing to the vertex.
    private Hgraph subgraph; // the subgraph represented by the vertex. Default = NULL
    private Hgraph graph; //the graph that the vertex is located.
    private double height; // Height
    private double width; // Width
    private Point2D upleftPoint; // up left
}
