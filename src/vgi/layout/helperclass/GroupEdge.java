/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.helperclass;

import java.util.ArrayList;
import java.util.List;
import vgi.automata.State;

/**
 *
 * @author reng
 */
public class GroupEdge{
    VertexGroup sourceGroup;
    VertexGroup targetGroup;
    List<State> sourceVertices; // actual connected states in order, length of list = weight
    List<State> targetVertices;
    int weight; 
    public GroupEdge(VertexGroup s,VertexGroup t,State vs,State vt){
        sourceGroup=s; targetGroup=t;
        addVertexLink(vs,vt);
    }
    public void addVertexLink(State s,State t){
        if(sourceVertices==null) sourceVertices=new ArrayList<State>();
        if(targetVertices==null) targetVertices=new ArrayList<State>();
        sourceVertices.add(s);
        targetVertices.add(t);
        weight++;
    }
}