/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import java.util.*;

/**
 *
 * @author x1213
 */
public class Hedge {
    public Hedge(int m) {
        multiplicity = m; 
        // 0 means there is going to be sub-edges.
        // 1 means otherwise.
        // See public int multiplicity() and public void incrMultiplicity().
        subEdgeList = new ArrayList();
        parent = null;
    }
   /*///////////////////////////////////////////////
    *   Set
    *///////////////////////////////////////////////  
    public void setParent(Hedge e) {
        parent = e;
    }
    public void setSource(Hvertex v) {
        source = v;
    }
    public void setTarget(Hvertex v) {
        target = v;
    }  
    public void addSubedge(Hedge e) {
        subEdgeList.add(e);
    }
    public void incrMultiplicity() {
        ++multiplicity;
    }    
   /*///////////////////////////////////////////////
    *    Get
    *///////////////////////////////////////////////   
    public int multiplicity() {
        if (multiplicity == 0) {
            return subEdgeList.size();
        }
        return multiplicity;
    }
    public Hvertex getNext(Hvertex v) {
        if (v == source) {
            return target;
        }
        else if (v == target) {
            return source;
        }
        else {
            return null;
        }
    }
    public Hvertex source() {
        return source;
    }
    public Hvertex target() {
        return target;
    }
   /*///////////////////////////////////////////////
    *   Others
    */////////////////////////////////////////////// 
    public void reverse() {
        Hvertex t = source;
        Hvertex s = target;
        source = s;
        target = t;
        t.edgeRemove(this);
        s.edgeRemove(this);
        t.addEdge(this);
        s.addEdge(this);
    }
   /*///////////////////////////////////////////////
    *    Members
    *///////////////////////////////////////////////   
    private List<Hedge> subEdgeList; //The edges in the lower level hierarchy that corresponds to this edge.
    private Hvertex source; // The source vertex.
    private Hvertex target; //The target vertex.
    // if none of the s,t is representing a group, it records the number of edges between s,t.
    // Otherwise, it is set to 0.
    private int multiplicity; 
    // Parent edge. (if u in group A in graph G, v is directly in graph G, then (G,v) is the parent of (u,v))
    // default = null
    private Hedge parent; 

}
