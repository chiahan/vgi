/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.helperclass;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import vgi.automata.State;
import vgi.automata.Transition;

/**
 *
 * @author reng
 */


class DfsVertex{
        State state;
        int d;
        int f;
        State pi;
        int color;
        public DfsVertex(State v){
            state=v;
            color=0;
            pi=null;
        }
}
    
    ///// for strongly connected comp.
class SccVertex{
        List<State> stateList;
        List<SccEdge> edgeList=null; // connected to other sccVertex !!only outgoing edges!!
        String name;
        public SccVertex(List<State> states){
            stateList=states;
            name=new String();
            for(State state:stateList){
                name+=state.getName();
            }
        }
        public void addEdge(SccVertex s,SccVertex t){
            if(edgeList==null) edgeList=new ArrayList<SccEdge>();
            boolean found=false;
            for(SccEdge edge:edgeList){
                if(edge.source==s && edge.target==t){
                    edge.weight++;
                    found=true;
                    break;
                }
            }
            if(!found) edgeList.add(new SccEdge(s,t));
        }
}
class SccEdge{
        SccVertex source;
        SccVertex target;
        int weight; // number of edges connecting the two SccVertex
        public SccEdge(SccVertex s,SccVertex t){
            source=s; target=t;
            weight=1;
        }
       
}
class SccDfsVertex{
        SccVertex sccVertex;
        int d;
        int f;
        int color;
        public SccDfsVertex(SccVertex v){
            sccVertex=v;
            color=0;
            
        }
}

public class SccDfs{
    
    int vertexNum;
    List<State> allVertexList;
    
    int dfsTime=0;
    List<DfsVertex> dfsVertexList;
    List<State> dfsSortedList;
    
     // for strongly connected component
    List<DfsVertex> sccVertexList;
    private int sccTime;
    //List<State> sccStack;
    Stack<State> sccStack;
    List<SccVertex> sccList=null;
    List<SccDfsVertex> sccDfsVertexList;
    List<SccVertex> sccSortedList;
    
    public SccDfs(List<State> allStates)
    {
        allVertexList=allStates;
        vertexNum=allVertexList.size();
    }
    
    public List<State> getExpandedListWithSCC()
    {
            sccVertexList=new ArrayList<DfsVertex>();
            sccStack=new Stack<State>();

            for(int i=0;i<vertexNum;++i)
            {
                sccVertexList.add(new DfsVertex(allVertexList.get(i)));
            }
            sccTime=0;

            // find stonely connected components, saved in sccList
            for(int i=0;i<vertexNum;++i)
            {
                DfsVertex dvertex=sccVertexList.get(i);
                if(dvertex.color==0) sccVisit(dvertex);
            }

            // find eges connecting SccVertices and saved in each SccVertex
            for(SccVertex sccv:sccList)
            {
                findSccEdges(sccv);
            }

            // do DFS to sccList
            doSccAndDFS();

            // expand scc
            List<State> expandedList=expandSccSortedList();

            return expandedList;

    }
    
     private void sccVisit(DfsVertex dvertex)
     {
        sccTime++;
        dvertex.d=sccTime; 
        dvertex.f=sccTime; 
        dvertex.color=1;
        System.out.println("scc visit: "+dvertex.state.getName());
        System.out.println(dvertex.state.getName()+" d= "+dvertex.d);
        System.out.println(dvertex.state.getName()+" f= "+dvertex.f);
        
        sccStack.push(dvertex.state);
        
        
        List<Transition> outEdges=dvertex.state.getOutgoingTransitions();
        int edgeCount=outEdges.size();
        for(int i=0;i<edgeCount;++i){
            State destCell=outEdges.get(i).getTargetState();
            int ind=allVertexList.indexOf(destCell);
            if(ind>-1){
                DfsVertex destdVertex=sccVertexList.get(ind);
                
                if(destdVertex.color==0){
                    sccVisit(destdVertex);
                    dvertex.f=Math.min(dvertex.f,destdVertex.f);
                    System.out.println(dvertex.state.getName()+" f= "+dvertex.f);
        
                }else if(sccStack.contains(destdVertex.state)){
                    dvertex.f=Math.min(dvertex.f,destdVertex.d);
                    System.out.println(dvertex.state.getName()+" f= "+dvertex.f);
                }
            }
        }
        
        
        if(dvertex.f==dvertex.d){
            // get a stronely connected component
            State dest=null;
            List<State> sccStates=new ArrayList<State>();
            if(sccList==null) sccList=new ArrayList<SccVertex>();
            
            do{
                dest=sccStack.pop();
                System.out.println(dest.getName());
                sccStates.add(dest);
                
            }while(dest!=dvertex.state);
            SccVertex v=new SccVertex(sccStates);
            sccList.add(v);
            System.out.println("----a new scc vertex: "+v.name);
            
           
            
        }
        
    }
    private void findSccEdges(SccVertex vertex) {
        
        List<State> sccStates=vertex.stateList;
        for(State state:sccStates){
            List<Transition> transitions=state.getOutgoingTransitions();
            for(Transition tran:transitions){
                
                State target=tran.getTargetState();
                
                for(SccVertex v:sccList){
                    if(v.stateList.contains(target) && v!=vertex){
                        vertex.addEdge(vertex,v);
                        System.out.println("---- a new scc edge: "+vertex.name+" -> "+v.name);
                        break;
                    }
                }
               
            }
        }
    }
    
    private void doSccAndDFS(){
        
//       allVertexList=getAllVertices(automata_);
        int sccVertexNum=sccList.size();
        
       
        sccDfsVertexList=new ArrayList<SccDfsVertex>(sccVertexNum);
        sccSortedList=new ArrayList<SccVertex>(sccVertexNum);
        for(int i=0;i<sccVertexNum;++i){
            sccDfsVertexList.add(new SccDfsVertex(sccList.get(i)));
        }
        dfsTime=0;
        
        for(int i=0;i<sccVertexNum;++i){
            SccDfsVertex dvertex=sccDfsVertexList.get(i);
            if(dvertex.color==0) SccDfsVisit(dvertex);
        }
        
        List<SccVertex> invertedList=new ArrayList<SccVertex>();
        for(int i=0;i<sccVertexNum;++i){
            invertedList.add(sccSortedList.get(sccVertexNum-i-1));
        }
        sccSortedList=invertedList;
        
    }
    private void SccDfsVisit(SccDfsVertex dvertex){
        dfsTime++;
        dvertex.d=dfsTime;
        dvertex.color=1;
        System.out.println("dfs visit: "+dvertex.sccVertex.name);
        
        if(dvertex.sccVertex.edgeList!=null){
            List<SccEdge> outEdges=dvertex.sccVertex.edgeList;
            int edgeCount=outEdges.size();
            for(int i=0;i<edgeCount;++i){
                SccVertex destCell=outEdges.get(i).target;
                int ind=sccList.indexOf(destCell);
                if(ind>-1){
                    SccDfsVertex destdVertex=sccDfsVertexList.get(ind);

                    if(destdVertex.color==0){
                        SccDfsVisit(destdVertex);
                    }
                }
            }
        }
        dvertex.color=2;
        dfsTime++;
        dvertex.f=dfsTime;
        sccSortedList.add(dvertex.sccVertex);
        
        
        System.out.println(dvertex.sccVertex.name+"- ( "+dvertex.d+" , "+dvertex.f+" )");
        
    }
    
    private List<State> expandSccSortedList() {
        int len=sccList.size();
        List<State> expandedList=new ArrayList<State>();
        
        for(int i=0;i<len;++i){
            SccVertex v=sccSortedList.get(i);
            int num=v.stateList.size();
            for(int j=0;j<num;++j){
                expandedList.add(v.stateList.get(num-1-j));
            }
        }
       
        return expandedList;
    }
}


