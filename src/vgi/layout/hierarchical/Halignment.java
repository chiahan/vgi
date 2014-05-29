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
public class Halignment {
    
    public Halignment() {
        memberSet = new HashSet<Object>();
        left_adj = new HashSet<Halignment>();
        right_adj = new HashSet<Halignment>();
    }
    
    //merge h to this
    //h is abandoned after this operation
    public void merge(Halignment h) {
        for (Halignment g: h.left_adj) {
            g.right_adj.remove(h);
            g.right_adj.add(this);
            left_adj.add(g);
        }
        for (Halignment g: h.right_adj) {
            g.left_adj.remove(h);
            g.left_adj.add(this);
            right_adj.add(g);
        }
        memberSet.addAll(h.memberSet);
    }
    
    public void add_right(Halignment h) {
        right_adj.add(h);
    } 
    
    public void add_left(Halignment h) {
        left_adj.add(h);
    }     
    public void add_member(Object obj) {
        memberSet.add(obj);
    }   
    public void setXcoordinate(int x) {
        xCoordinate = x;
    }
    public int getXcoordinate() {
        return xCoordinate;
    }
    public int leftDeg() {
        return left_adj.size();
    }
    public boolean LeftAdjContains(Halignment h) {
        return left_adj.contains(h);
    }
   /*///////////////////////////////////////////////
    *    Members
    *///////////////////////////////////////////////   
    private Set<Object> memberSet;   // list of obj in this alignment
    private int xCoordinate;           // x-coordinate
    private Set<Halignment> left_adj; // allignments (immediately)left to this
    private Set<Halignment> right_adj;// allignments (immediately)right to this
}
