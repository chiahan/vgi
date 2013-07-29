/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.helperclass;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import vgi.automata.State;
import vgi.automata.StateGeometricData;
/**
 *
 * @author reng
 */
public class VertexGroup {
    
    public static int LINEAR_GROUP=1;
    public static int CIRCULAR_GROUP=2;
    
    private List<State> stateList;
    private List<GroupEdge> edgeList=null; // connected to other sccVertex !!only outgoing edges!!
    private String name;
    private int layoutType;
    
    public VertexGroup(List<State> states){
            stateList=states;
            name=new String();
            for(State state:stateList){
                name+=state.getName();
            }
            System.out.println("create vertexGroup: "+name);
        }
    public void addEdge(VertexGroup s,VertexGroup t,State vs,State vt){
            if(edgeList==null) edgeList=new ArrayList<GroupEdge>();
            boolean found=false;
            for(GroupEdge edge:edgeList){
                if(edge.sourceGroup==s && edge.targetGroup==t){
                    
                    edge.addVertexLink(vs,vt);
                    found=true;
                    break;
                }
            }
            if(!found) edgeList.add(new GroupEdge(s,t,vs,vt));
        }
    public double[] getBound(){
           
           StateGeometricData geo=stateList.get(0).getGeometricData();
           double minx=geo.getX()-geo.getWidth()/2;   // first use as left-buttom corner
           double miny=geo.getY()-geo.getHeight()/2;
           double maxx=geo.getX()+geo.getWidth()/2;   // first use as right-top corner
           double maxy=geo.getY()+geo.getHeight()/2;
           
           int len=stateList.size();
           for(int i=1;i<len;++i){
               geo=stateList.get(i).getGeometricData();
               minx=Math.min(minx,geo.getX()-geo.getWidth()/2);
               miny=Math.min(miny,geo.getY()-geo.getHeight()/2);
               maxx=Math.max(maxx,geo.getX()+geo.getWidth()/2);
               maxy=Math.max(maxy,geo.getY()+geo.getHeight()/2);
           }
           double x=(minx+maxx)/2;
           double y=(miny+maxy)/2;
           double w=maxx-minx;
           double h=maxy-miny;
           
           return new double[]{x,y,w,h};
    }
    
    public List<GroupEdge> getEdgeList(){
        return edgeList;
    }
    public List<State> getStateList(){
        return stateList;
    }
    public void setLayoutType(int type){
        layoutType=type;
    }
    public int getLayoutType(){
        return layoutType;
    }
//    public State getRepresentState(){
//        State st=new State();
//        
//        
//        
//        return st;
//    }
}

