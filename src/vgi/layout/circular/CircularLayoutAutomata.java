/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.circular;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;
import vgi.automata.*;
import vgi.layout.helperclass.GroupReplacedAutomata;
import vgi.layout.helperclass.SccDfs;

/**
 *
 * @author reng
 */

public class CircularLayoutAutomata {
    
    Automata automata;
//    double centerX,centerY;
    Point2D center;
    double radius;
    double theta;
//    double boundY;
    Rectangle bound;

    List<State> allVertexList;

    int vertexNum;
    List<State> vertexMapList;
    List<State> ChosenVertexList;
    List<Transition> addedTopEdge;
    List<Transition> addedBottomEdge;
    
    List<List<State>> groupList;
    
    double [][] GroupStructure;
    
    boolean routeTwoPhase=true;
    
    
    public CircularLayoutAutomata(){}
    
    public void GroupProcess(Automata automata_)
    {
        groupList = new ArrayList<List<State>>();
        System.out.println("~~~There is group~~~");
        //retrieve the list of group
        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata_.getAllStates());    
        while(!StateList.isEmpty()) 
        {
            State s = StateList.get(0);
            List<State> g = automata_.getGroup(s);
            groupList.add(g);
            for (State ss: g) 
            {
                StateList.remove(ss);
            }
        }
        
        automata = automata_;
        
        /*begin to change*/
        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();
    
        /*
         * compute bounding box of selected states
         */
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);
        

        center=new Point2D.Double(bound.getCenterX(),bound.getCenterY());  
         

        
        //graph.getModel().beginUpdate();
        //    try{
        
        ChosenVertexList = getSelectedVertices(automata_);
        //System.out.println("ChosenVertexList:"+ChosenVertexList);
        
        int groupSize = 0;
        for (List<State> state: groupList) 
        {
            if(state.size() > 1)//if there is a group
            {
               ChosenVertexList.removeAll(state);
               ChosenVertexList.add(state.get(0));
               groupSize++;
               
            }
        }
        int GID = 0;
        GroupStructure = new double[groupSize][5];
        for (List<State> state: groupList) 
        {
            if(state.size() > 1)//if there is a group
            {
               GroupStructure[GID][0] = (double)GID+1;
               Rectangle boundtmp;
               boundtmp = automata.computeBox(state);
               
               GroupStructure[GID][1] = boundtmp.getX();
               GroupStructure[GID][2] = boundtmp.getY();
               GroupStructure[GID][3] = boundtmp.getWidth();
               GroupStructure[GID][4] = boundtmp.getHeight();
               GID++;
            }
        }
        for(int i=0;i<GID;i++)
        {
            System.out.println(GroupStructure[i][0]+" "+GroupStructure[i][1]+" "+GroupStructure[i][2]+" "+GroupStructure[i][3]+" "+GroupStructure[i][4]);
        }
        //System.out.println("groupList.size"+groupList+" "+groupSize);
        System.out.println("ChosenVertexList:"+ChosenVertexList);
        
        System.out.println("allVertexList:"+allVertexList);    
        SccDfs sccdfs=new SccDfs(ChosenVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();
        System.out.println("vertexMapList:"+vertexMapList);
            //vertexMapList=this.sortVertices(automata_);
        radius =group_findProperRadius();
        theta=group_findProperTheta();//Math.PI*2/vertexNum;
        System.out.println("center= "+center.getX()+" , "+center.getY()+" radius= "+radius);
        setSortedVerticesLocation();
            
        adjustEdgeCurves(true);    

  
        System.out.println("~~~There is group~~~");
        
    }
    
    public void NonGroupProcess(Automata automata_)
    {
        automata=automata_;
        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();
    
        /*
         * compute bounding box of selected states
         */
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);

        center=new Point2D.Double(bound.getCenterX(),bound.getCenterY());  
        radius =findProperRadius();
        theta=findProperTheta();//Math.PI*2/vertexNum;
         
        System.out.println("center= "+center.getX()+" , "+center.getY()+" radius= "+radius);
        
        //graph.getModel().beginUpdate();
        //    try{
            
            
        SccDfs sccdfs=new SccDfs(allVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();
        
            //vertexMapList=this.sortVertices(automata_);
        setSortedVerticesLocation();
        System.out.println("vertexMapList:"+vertexMapList);
        adjustEdgeCurves(true);    

    }
    
    public void doLayout(Automata automata_)
    {
        automata=automata_;
        
        boolean HaveGroup = false;
        
        groupList = new ArrayList<List<State>>();
        
        //retrieve the list of group
        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata_.getAllStates());    
        while(!StateList.isEmpty()) 
        {
            State s = StateList.get(0);
            List<State> g = automata_.getGroup(s);
            groupList.add(g);
            for (State ss: g) 
            {
                StateList.remove(ss);
            }
        }
        //decide whether own group
        for (List<State> state: groupList) 
        {
            if (state.size() == 1) 
            {
                HaveGroup = false;
            }
            else
            {
                HaveGroup = true;
                break;
            }
        }

        if(HaveGroup == true)
        {
            GroupProcess(automata);
        }
        else
        {
            NonGroupProcess(automata);
        }
//        vertexNum=automata.getAllStates().size();
//        allVertexList=getAllVertices(automata_);

        
            
//            if(boundY<0) moveWholeBound();
            
       // }finally{
           // graph.getModel().endUpdate();
       // }
    }
    
    public void doLayout(Automata automata_,GroupReplacedAutomata replaceAutomata){
        
        Automata reautomata=replaceAutomata.createReplaceAutomata();
        reautomata.selectAllStates();
        automata=reautomata;

        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();
        
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);
        
        center=new Point2D.Double(bound.getCenterX(),bound.getCenterY());  
        
        radius =findProperRadius();
        theta = findProperTheta();//Math.PI * 2 / vertexNum;
  
        System.out.println("center= "+center.getX()+" , "+center.getY()+" radius= "+radius);
       
        SccDfs sccdfs=new SccDfs(allVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();

        setSortedVerticesLocation();
            
//        replaceAutomata.expandStatesToVertexGroups();
        vertexMapList=replaceAutomata.expandStatesToCircularLayout(vertexMapList,center);
        
        vertexNum=vertexMapList.size();
        
        automata=automata_;
        
//        decideEdgesGoUpDown();
//        setEdgesCurveHeight();
//        setRouteTwoPhase(false);
        adjustEdgeCurves(true);  
        
    }
    private double findProperRadius(){
        
//        double maxStateSize = 0;
//        for (State state : allVertexList) {
//            double tmp = automata.getStateGeometricData(state).getWidth();
//            if (tmp > maxStateSize) {
//                maxStateSize = tmp;
//            }
//            tmp = automata.getStateGeometricData(state).getHeight();
//            if (tmp > maxStateSize) {
//                maxStateSize = tmp;
//            }
//        }
//         System.out.println("maxState= " + maxStateSize);
//       double rad=Math.max(bound.height - maxStateSize / 2, bound.width - maxStateSize / 2)/ 2;
//       rad=Math.max(maxStateSize/2,rad)+20;
//       if(center.getY()+rad>bound.y) center.setLocation(center.getX(), bound.y-rad);
//       if(center.getY()-rad<bound.x) center.setLocation(bound.x+rad,center.getY());
        
        
        double sum=0;
        for(State state:allVertexList){
            sum+=automata.getStateGeometricData(state).getWidth();
        }
        double rad=sum/Math.PI;
        if(center.getY()+rad>bound.y+bound.getHeight()) 
        {
            center.setLocation(center.getX(), bound.y+bound.getHeight()-rad);
        }
        if(center.getX()-rad<bound.x) 
        {
            center.setLocation(bound.x+rad,center.getY());
        }
        System.out.println("center_x:"+center.getX()+"center_y:"+center.getY());
         
        return rad;
        
    }
    private double findProperTheta(){
        
        //find the min width state
        double minWidth=automata.getStateGeometricData(allVertexList.get(0)).getWidth();
        for(State state:allVertexList){
            double t=automata.getStateGeometricData(state).getWidth();
            if(t<minWidth) minWidth=t;
        }
        
        double partition=0;
        for(State state:allVertexList){
            double t=automata.getStateGeometricData(state).getWidth();
            partition+=t/minWidth;
        }
        
        return 2*Math.PI/partition;
    }
    private double group_findProperRadius(){
        double sum=0;
        for(State state:vertexMapList){
            boolean isGroup = false;
            int gid = 0;
            int state_gid = 0;
            for (List<State> state_list: groupList) {
                if(state_list.size() > 1) {                 
                    if(state == state_list.get(0)) {
                        state_gid = gid;
                        isGroup = true;
                    }
                    gid = gid + 1;
                }
            }     
            if(isGroup) sum+=GroupStructure[state_gid][3];
            else sum+=automata.getStateGeometricData(state).getWidth();
        }
        double rad=sum/Math.PI;
        if(center.getY()+rad>bound.y+bound.getHeight()) center.setLocation(center.getX(), bound.y+bound.getHeight()-rad);
        if(center.getX()-rad<bound.x) center.setLocation(bound.x+rad,center.getY());

        System.out.println("center_x:"+center.getX()+"center_y:"+center.getY());
         
        return rad;       
    }
    private double group_findProperTheta(){
    
        //find the min width state
        State first_state = vertexMapList.get(0);
        boolean isGroup = false;
        int gid = 0;
        int state_gid = 0;
        for (List<State> state_list: groupList) {
            if(state_list.size() > 1) {                 
                if(first_state == state_list.get(0)) {
                    state_gid = gid;
                    isGroup = true;
                }
                gid = gid + 1;
            }
        }    
        double minWidth;
        if(isGroup) minWidth = GroupStructure[state_gid][3];
        else  minWidth=automata.getStateGeometricData(vertexMapList.get(0)).getWidth();
        //double minWidth=automata.getStateGeometricData(allVertexList.get(0)).getWidth();
        for(State state:vertexMapList){
            isGroup = false;
            gid = 0;
            state_gid = 0;
            for (List<State> state_list: groupList) {
                if(state_list.size() > 1) {                 
                    if(state == state_list.get(0)) {
                        state_gid = gid;
                        isGroup = true;
                    }
                    gid = gid + 1;
                }
            }    
            double t;
            if(isGroup) t=GroupStructure[state_gid][3];
            else t=automata.getStateGeometricData(state).getWidth();
            if(t<minWidth) minWidth=t;
        }
        
        double partition=0;
        for(State state:vertexMapList){
            //double t=automata.getStateGeometricData(state).getWidth();
                        isGroup = false;
            gid = 0;
            state_gid = 0;
            for (List<State> state_list: groupList) {
                if(state_list.size() > 1) {                 
                    if(state == state_list.get(0)) {
                        state_gid = gid;
                        isGroup = true;
                    }
                    gid = gid + 1;
                }
            }    
            double t;
            if(isGroup) t=GroupStructure[state_gid][3];
            else t=automata.getStateGeometricData(state).getWidth();
            partition+=t/minWidth;
        }
        
        return 2*Math.PI/partition;
    }
    
    private List<State> getSelectedVertices(Automata automata_){

        //vertexNum=automata.getAllStates().size();
//        return automata.getAllStates();
        
          // only deal with selected states
          return automata.getSelectedStates();
    }

//    private List<State> sortVertices(Automata automata_){
//        
//        // do DFS
//        dfsSortedList=new ArrayList<State>();
//        DFS(automata_);
//        
//        return dfsSortedList;
//    }

    private void setSortedVerticesLocation(){
         List<State> sorted_vertexMapList = vertexMapList;
         List<State> return_vertexMapList = new ArrayList<State>(); 
         int g_vertexNum = sorted_vertexMapList.size();
         //find the min width state
         double minWidth=automata.getStateGeometricData(vertexMapList.get(0)).getWidth();
         double tmptheta=0;
         for(State state:vertexMapList){
             //judge whether the state is in a group
             boolean isGroup = false;
             int gid = 0;
             int state_gid = 0;
             for (List<State> state_list: groupList) {
                 if(state_list.size() > 1) {                 
                     if(state == state_list.get(0)) {
                         state_gid = gid;
                         isGroup = true;
                     }
                     gid = gid + 1;
                 }
             }     
             double t;
             if(isGroup) {
                 t=GroupStructure[state_gid][3];
             }
             else {
                 t=automata.getStateGeometricData(state).getWidth();
             }
             if(t<minWidth) minWidth=t;
         }   

         //assign each state a new position
         for(int i = 0; i<g_vertexNum; ++i) {
                State state=vertexMapList.get(i);
                List<State> state_group  = new ArrayList<State>();
                boolean isGroup = false;
                int gid = 0;
                int state_gid = 0;
                //judge whether the state is in a group
                for (List<State> state_list: groupList)
                {
                    if(state_list.size() > 1)
                    {
                        if(state == state_list.get(0))
                        {
                            state_gid = gid;
                            state_group = state_list;
                            isGroup = true;
                        }
                        gid = gid + 1;
                    }
                }     
                double tt;
                double newx;
                double newy;
                if(isGroup) { 
                    //Point2D state_geodata = new Point2D.Double(x+GroupStructure[state_gid][3]/2+(distance+GroupStructure[state_gid][3]/2)*0.3,lineY);
                    tt = GroupStructure[state_gid][3]/minWidth*theta;
                    tmptheta += tt/2;
                    newx=center.getX()+radius*Math.cos(tmptheta);
                    newy=center.getY()+radius*Math.sin(tmptheta);
                    Point2D state_geodata = new Point2D.Double(newx, newy);
                 
                    double dis_x = state_geodata.getX() - (GroupStructure[state_gid][1]+GroupStructure[state_gid][3]/2);
                    double dis_y = state_geodata.getY() - (GroupStructure[state_gid][2]+GroupStructure[state_gid][4]/2); 
                    //automata.setStateGeometricData(state, geodata);
                    for(State g_state: state_group)
                    {                    
                        return_vertexMapList.add(g_state);
                        StateGeometricData geodata = automata.getStateGeometricData(g_state);
                        geodata.setLocation(new Point2D.Double(geodata.getX()+dis_x,geodata.getY()+dis_y));
                        automata.setStateGeometricData(g_state, geodata);
                    }
                    tmptheta+=tt/2;
                    
                }
                else {
                    return_vertexMapList.add(state);
                    StateGeometricData geodata=automata.getStateGeometricData(state);
                          
                    tt=geodata.getWidth()/minWidth*theta;            
                    tmptheta += tt/2;
                    newx=center.getX()+radius*Math.cos(tmptheta);
                    newy=center.getY()+radius*Math.sin(tmptheta);
                    
                    geodata.setLocation(new Point2D.Double(newx,newy));
                    automata.setStateGeometricData(state, geodata);
                    tmptheta+=tt/2;
                    
                    geodata=automata.getStateGeometricData(state);
                    System.out.println("set location: "+state.getName()+" "+geodata.getX()+","+geodata.getY()+" theta= "+tmptheta);
                }
                
         }
         vertexMapList = return_vertexMapList;
    }
    
    public void adjustEdgeCurves(boolean ignoreStateInGroups){
         if(routeTwoPhase){
             decideEdgesGoUpDown(ignoreStateInGroups);
             setEdgesCurveHeight(ignoreStateInGroups);
        }else{
             //reset all the control points since we only route inward
            for(State state:vertexMapList){
                List<Transition> trans=state.getOutgoingTransitions();
                for(Transition tr:trans) automata.resetTransitionControlPoint(tr);
            }
            // maybe to some edge bundling...
            
        }
//        System.out.println("finish adjusting edges!");
    }
    public void adjustEdgeCurves(Automata automata_,List<State> vertexList){
        automata=automata_;
        vertexMapList=vertexList; 
        vertexNum=vertexMapList.size();
        System.out.println("adjust linear layout edges: "+vertexMapList);
        
        if(routeTwoPhase){
            decideEdgesGoUpDown(false);
            setEdgesCurveHeight(false);
        }else{   
            
            //reset all the control points since we only route inward
            for(State state:vertexMapList){
                List<Transition> trans=state.getOutgoingTransitions();
                for(Transition tr:trans) automata.resetTransitionControlPoint(tr);
            }
            // maybe to some edge bundling...
            
        }
        
    }
    
    
    /*
     *  decide whether an edge goes up or down, choose the one with fewer crossings
     *  
     *  addedTopEdge,addedBottomEdge are updated
     */
    private void decideEdgesGoUpDown(boolean ignoreStateInGroups){
            addedTopEdge=new ArrayList<Transition>();
            addedBottomEdge=new ArrayList<Transition>();
            
            
//            System.out.println("vertexMapList= "+vertexMapList+"vertexNum= "+vertexNum);
            
            // check if the states are from vertex groups
            // add to Top/Bottom directly by computing the radius
            if(ignoreStateInGroups){
                 for(int i = 0; i<vertexNum; ++i){
                    State state=vertexMapList.get(i);
                    int id=state.getGroupID();
//                    boolean stateInGroup=(id>-1);
//                    System.out.println(state+"id "+id);
                    
                    if(id==-1) continue;
                    
                    int phase=0;
                    double r=center.distance(state.getGeometricData().getLocation());
                    if(r>radius) phase=1;
                    else if(r<radius) phase=-1;
                    
                    
                    List<Transition> outGoingEdges=state.getTransitions();
                    int edgeCount=outGoingEdges.size();
                    for(int j=0;j<edgeCount;++j){
                         Transition edge=outGoingEdges.get(j);
                                 
                         State target=edge.getTargetState();
                         if(target==state) target=edge.getSourceState();
                         if(target==state){
                             addedTopEdge.add(edge);
                             continue;
                         } //loop
                         
//                         System.out.println("check: "+state+"->"+target);
                         
                         // if the other terminal is not in the same group
                         if(target.getGroupID()!=id){
                            // if one terminal point is above/below horizontal line, add to top/bottom directly.
                            int phaset=0;
                            if(target.getGroupID()!=-1){
                                double r_=center.distance(target.getGeometricData().getLocation());
                            
                                if(r_>radius) phaset=1;
                                else if(r_<radius) phaset=-1;
                            }
                            
                             System.out.println("phase of "+state+" : "+phase+" "+target+" : "+phaset);
                            if(phase>0 || phaset>0){
                                addedTopEdge.add(edge);
                            }else if(phase<0 || phaset<0){
                                addedBottomEdge.add(edge);
                            }
                            
                         }
                    }
                 }
                            
           }
//            for(Transition tr:addedTopEdge) System.out.println("top: "+tr.getSourceState()+"->"+tr.getTargetState());
//            for(Transition tr:addedBottomEdge) System.out.println("bottom: "+tr.getSourceState()+"->"+tr.getTargetState());
            
            
            for(int i = 0; i<vertexNum; ++i){
                    State state=vertexMapList.get(i);
                    
                   
                    List<Transition> outGoingEdges=state.getOutgoingTransitions();
                    int edgeCount=outGoingEdges.size();
                    for(int j=0;j<edgeCount;++j){
                        
//                        mxCell edge=(mxCell)outGoingEdges[j];
//                        mxCell target=(mxCell)edge.getTarget();
//                        mxCell source=(mxCell)edge.getSource();
//                        
                        Transition edge=outGoingEdges.get(j);
                        
                        if(addedTopEdge.contains(edge) || addedBottomEdge.contains(edge))continue;
                        
                        
                        
                        State target=edge.getTargetState();
                        State source=edge.getSourceState();
                        
                        //if two end are in same group
                        if(source.getGroupID()==target.getGroupID() && source.getGroupID()!=-1) continue;
                        
                        //if(target!=cell && target!=null){
                        if(target!=null && source!=null){
                            if(edgeGoUp(edge)){
                                addedTopEdge.add(edge);
                            }else{
                                addedBottomEdge.add(edge);
                            }
                        }
                    }
                    
                    List<Transition> loopEdges=state.getLoopTransitions();
                    int loopCount=loopEdges.size();
                    for(int j=0;j<loopCount;++j){
                        
//                        mxCell edge=(mxCell)outGoingEdges[j];
//                        mxCell target=(mxCell)edge.getTarget();
//                        mxCell source=(mxCell)edge.getSource();
//                        
                        Transition edge=loopEdges.get(j);
                        if(edgeGoUp(edge)){
                                addedTopEdge.add(edge);
                        }else{
                                addedBottomEdge.add(edge);
                        }
                        
                    }
            }
            
            for(Transition tr:addedTopEdge) System.out.println("!!!!top: "+tr.getSourceState()+"->"+tr.getTargetState());
            for(Transition tr:addedBottomEdge) System.out.println("bottom: "+tr.getSourceState()+"->"+tr.getTargetState());
            

    }
    /*
     * decide an edge to go up or down, depends on minimum crossings
     * if up return true, down return false
     */
    private boolean edgeGoUp(Transition edge){
        
        int sourceIndex=vertexMapList.indexOf(edge.getSourceState());
        int targetIndex=vertexMapList.indexOf(edge.getTargetState());
        
        int a=sourceIndex;
        int b=targetIndex;
        if(targetIndex<sourceIndex){ a=targetIndex; b=sourceIndex;}
        
        System.out.println("crossing of "+sourceIndex+" -> "+targetIndex);
        
        
        // if the orientation between the two vertices > 3PI/4, route it inside the circle
        double theta1=Math.atan2(edge.getSourceState().getGeometricData().getY()-center.getY(),edge.getSourceState().getGeometricData().getX()-center.getX())+2*Math.PI;
        double theta2=Math.atan2(edge.getTargetState().getGeometricData().getY()-center.getY(),edge.getTargetState().getGeometricData().getX()-center.getX())+2*Math.PI;
        if(Math.min(Math.abs(theta1-theta2),2*Math.PI-Math.abs(theta1-theta2))>Math.PI/2){    
            return false;
        }
        
        int topCrossing=0,bottomCrossing=0;
        
        // Check if there's a backward edge.
        // Since edges are determined from left to right, check only edges with (target <- source)
        // if there's a backward edge, add 3 to weight
        if(sourceIndex>targetIndex && automata.hasBackwardTransition(edge)){
            
            System.out.println(" a backward edge!");
            Transition backedge = null;
            List<Transition> incomedges=edge.getSourceState().getIncomingTransitions();
            for(Transition tran:incomedges){
                if(tran.getSourceState()==edge.getTargetState()){
                    backedge=tran;break;
                }
            }
            if(backedge!=null){
                if(addedTopEdge.contains(backedge))  topCrossing+=3;
                else bottomCrossing+=3;
             }
        }
        
        for(Transition topEdge:addedTopEdge)
        {
            int sourceInd=vertexMapList.indexOf(topEdge.getSourceState());
            int targetInd=vertexMapList.indexOf(topEdge.getTargetState());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}

//            if(c<a && d<b && a<d){
            if(checkTwoEdgeCrossing(a,b,c,d))
            {
                topCrossing++;
                System.out.println("    top: "+sourceInd+"->"+targetInd);
            }
        }
        
        for(Transition bottomEdge:addedBottomEdge)
        {
            int sourceInd=vertexMapList.indexOf(bottomEdge.getSourceState());
            int targetInd=vertexMapList.indexOf(bottomEdge.getTargetState());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}

            
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if(checkTwoEdgeCrossing(a,b,c,d)){
                bottomCrossing++;
                System.out.println("    bottom: "+sourceInd+"->"+targetInd);
            }
        }
        System.out.println("top "+topCrossing+" / bottom "+bottomCrossing);
        if(bottomCrossing<=topCrossing) return false;
        else return true;
        
    }
    
    // assume source < target
    private boolean checkTwoEdgeCrossing(int asource,int atarget,int bsource,int btarget){
        
//        if(bsource<asource)
//            if((bsource-asource)*(btarget-asource)<0 && 
//                (bsource+vertexNum-atarget)*(btarget-atarget)<0) return true;
//        else
//            if((bsource-asource)*(btarget-asource)<0 && 
//            (bsource-atarget)*(btarget-atarget)<0) return true;
        
        if(bsource<asource && btarget>asource && btarget<atarget) return true;
        if(bsource>asource && bsource<atarget && btarget>atarget) return true;
        
        return false;
    }
    
    
    private void setEdgesCurveHeight(boolean ignoreStateInGroups)
    {
        
        Collections.sort(addedTopEdge, new Comparator<Transition>()
        {
                    @Override
                    public int compare(Transition t, Transition t1) 
                    {
                            Integer sourceIndex=vertexMapList.indexOf(t.getSourceState());
                            Integer targetIndex=vertexMapList.indexOf(t.getTargetState());
                            Integer sourceIndex1=vertexMapList.indexOf(t1.getSourceState());
                            Integer targetIndex1=vertexMapList.indexOf(t1.getTargetState());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
            Collections.sort(addedBottomEdge, new Comparator<Transition>(){
                    @Override
                    public int compare(Transition t, Transition t1) {
                            Integer sourceIndex=vertexMapList.indexOf(t.getSourceState());
                            Integer targetIndex=vertexMapList.indexOf(t.getTargetState());
                            Integer sourceIndex1=vertexMapList.indexOf(t1.getSourceState());
                            Integer targetIndex1=vertexMapList.indexOf(t1.getTargetState());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
                int len=addedTopEdge.size();
                /*!!!here*/
                for(int i=0;i<len;++i)
                {
                    setEdgeCurveHeight(addedTopEdge.get(i),true);
                    System.out.println("~~~Annaaddtop:"+addedTopEdge.get(i));
                }

                len=addedBottomEdge.size();
                for(int i=0;i<len;++i){
                    setEdgeCurveHeight(addedBottomEdge.get(i),false);
                }
            
    }
    /*
     * adjust edges' curve height
     * avoid edges having same source/target from crossing each other
     * 
     * TODO: keep all given y coordinates > 0
     */
    private void setEdgeCurveHeight(Transition edge,boolean isTopEdge)
    {
        
        //for test
        int flag = 0;
        
        int sourceInd=vertexMapList.indexOf(edge.getSourceState());
        int targetInd=vertexMapList.indexOf(edge.getTargetState());
        System.out.println("Height of "+edge.getSourceState()+" "+sourceInd+" -> "+edge.getTargetState()+" "+targetInd);
        
        List<Point2D> points=new ArrayList<Point2D>();
        Point2D controlPoint1=null;
        Point2D controlPoint2=null;
        
        
        if(sourceInd==-1 || targetInd==-1) return;
        //controlPoint.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(targetInd).getGeometry().getCenterX())/2);
        
        State source=edge.getSourceState();//vertexMapList.get(a);
        State target=edge.getTargetState();//vertexMapList.get(b);
            
        double theta1=Math.atan2(source.getGeometricData().getY()-center.getY(),source.getGeometricData().getX()-center.getX())+2*Math.PI;
        double theta2=Math.atan2(target.getGeometricData().getY()-center.getY(),target.getGeometricData().getX()-center.getX())+2*Math.PI;
        
        if(Math.min(Math.abs(theta1-theta2),2*Math.PI-Math.abs(theta1-theta2))>Math.PI/2){    
            automata.resetTransitionControlPoint(edge);
            return;
        }
        
      if(sourceInd==targetInd){  //loop
            controlPoint1=new Point2D.Double();
            
            controlPoint1.setLocation(
                    center.getX()+Math.cos(theta1)*radius*1.2,
                    center.getY()+Math.sin(theta1)*radius*1.2
                    );
            System.out.println("loop"+controlPoint1);
//            points.add(controlPoint1);
            
      }
      
      /*here!!*/
      else
      {
         if(Math.abs(sourceInd-targetInd)==1 || Math.abs(sourceInd-targetInd)==vertexNum-1){
             
            System.out.println("neighbor: "+sourceInd+" , "+targetInd);
            automata.resetTransitionControlPoint(edge);
             // if there is edge from target-source
             if(automata.hasBackwardTransition(edge))
             {
                controlPoint1=new Point2D.Double();
                 
                double midtheta=(theta1+theta2)/2;
                if(Math.max(theta1,theta2)>Math.PI*5/2 && Math.min(theta2,theta1)<Math.PI*3/2){
                    midtheta+=Math.PI;
                }
                if(targetInd>sourceInd)
                { 
                    controlPoint1.setLocation(
                            center.getX()+Math.cos(midtheta)*radius*1.05,
                            center.getY()+Math.sin(midtheta)*radius*1.05);
                }
                else
                {
                    controlPoint1.setLocation(
                            center.getX()+Math.cos(midtheta)*radius*0.95,
                            center.getY()+Math.sin(midtheta)*radius*0.95);
                }
//                System.out.println(controlPoint1);
//                points.add(controlPoint1);
                 
                
             }
//                controlPoint1.setLocation();
             
         }
         else
         {
            
            
            
            if(!isTopEdge)
            { //keep bottom edges as straight lines
                automata.resetTransitionControlPoint(edge);
                return;
            }
            int a=sourceInd;
            int b=targetInd;
            
            if(targetInd>sourceInd)
            { 
                a=targetInd; 
                b=sourceInd;
//                State tmp=source;
//                source=target;
//                target=tmp;
            }

            
            double midtheta=(theta1+theta2)/2;
            boolean reverse=false;
            if((a-b)>Math.abs((a+vertexNum/2)%vertexNum-(b+vertexNum/2)%vertexNum)){
                midtheta=Math.PI+midtheta;
                reverse=true;
            }
            if(reverse)
            {
                a=(a+vertexNum/2)%vertexNum;
                b=(b+vertexNum/2)%vertexNum;
                int t=Math.max(a,b);
                b=Math.min(a,b); a=t;
            }
            System.out.println("    theta1= "+theta1+" theta2= "+theta2+" midtheta= "+midtheta+" reverse= "+reverse);
            
                    
            controlPoint1=new Point2D.Double();
//            controlPoint1.setLocation(
//                    center.getX()-Math.cos(midtheta)*radius,
//                    center.getY()-Math.sin(midtheta)*radius);
////            
           

//                int index=addedTopEdge.indexOf(edge);
                int len=addedTopEdge.size();

                //double height=lineY;
                
                // find the one has largest height between source and target
                int middleInd=0;
                double middleHeight=0;//radius;
                for(int i=b+1;i<a;++i)
                {
//                    double tmpHeight=vertexMapList.get(i).getGeometricData().getLocation().distance(center)+
//                            vertexMapList.get(i).getGeometricData().getHeight()/2;
                    double tmpHeight=vertexMapList.get(i).getGeometricData().getHeight()/2;
      
                    if(middleHeight<tmpHeight)
                    { 
                        middleInd=i;
                        middleHeight=tmpHeight;
                    }
                }
                
                
                double height=middleHeight;//
                System.out.println("    height= "+height);
                
                double higher=0;
                double tmpAnglea=0;
                double tmpAngleb=0;
                
                for(int i=0;i<len;++i)
                {
                    
                    Transition tmp=addedTopEdge.get(i);
                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSourceState());
                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTargetState());
//                    System.out.println("     top child "+tmpSourceInd+" -> "+tmpTargetInd);

                    int c=tmpSourceInd;
                    int d=tmpTargetInd;
                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}
                    
                    if(reverse)
                    {
                        c=(c+vertexNum/2)%vertexNum;
                        d=(d+vertexNum/2)%vertexNum;
                        int t=Math.max(c,d);
                        d=Math.min(c,d); c=t;
                       
                    }
                    
//                    System.out.println("     ["+a+" , "+b+"]"+"--["+c+" , "+d+"]");
                    if(c-d==1) continue;
                            
                    if((a==c && b==d))continue;
                    
                      if(a>=c && b<=d)
                      { 
//                              (reverse && (a+n2)%vertexNum<=(c+n2)%vertexNum && (b+n2)%vertexNum>=(d+n2)%vertexNum)){
                        if(tmp.getGeometricData().controlPoints.size()>1)
                        {
                            
                            Point2D point=tmp.getGeometricData().controlPoints.get(0);
                            
//                            double tmpHeight=point.distance(center);
                            double tmpHeight=point.distance(vertexMapList.get(tmpSourceInd).getGeometricData().getLocation());
                            //ßSystem.out.println("Anna!!!"+vertexMapList.get(tmpSourceInd));
                            System.out.println("---- tmpHeight= "+tmpHeight);
                            if(height<=tmpHeight){
                                    height=tmpHeight*Math.pow((a-b)/(c-d),higher*.1+1); higher++;
//                                    tmpCurveWidth=Math.abs(distsource*Math.cos(the));
                            }
                            
                            Point2D stateLocation=vertexMapList.get(tmpSourceInd).getGeometricData().getLocation();
                            double tmpa=Math.atan2(point.getY()-stateLocation.getY(),point.getX()-stateLocation.getX())+Math.PI*2;
                            
                            Point2D farpoint=tmp.getGeometricData().controlPoints.get(1);
                            stateLocation=vertexMapList.get(tmpTargetInd).getGeometricData().getLocation();
                            double tmpb=Math.atan2(farpoint.getY()-stateLocation.getY(),farpoint.getX()-stateLocation.getX())+Math.PI*2;
                            
                            if(tmpSourceInd<tmpTargetInd)
                            {
                                double t=tmpa; tmpa=tmpb; tmpb=t;
                            }
                                 
                            if(a==c) tmpAnglea=tmpa;
                            if(b==d) tmpAngleb=tmpb;
                            
                        }
                            
                            
                            

                        
                        System.out.println("        higher "+tmpSourceInd+" -> "+tmpTargetInd);
                        System.out.println("        height "+height);
                        System.out.println("        angle "+tmpAnglea+"  "+tmpAngleb);
                        

                      }
                }
//                double heightScale=automata.getStateGeometricData(vertexMapList.get(middleInd)).getHeight()/2;
//                
//                //int middleInd=(sourceInd+targetInd)/2;
//                //double middleHeight=vertexMapList.get(middleInd).getGeometry().getHeight();
//                //double height=vertexMapList.get(middleInd).getGeometricData().getLocation().distance(center)+heightScale;
//                
//                double height=middleHeight-radius*Math.cos((a-b)*theta/2);
////                System.out.println("    height= "+height);
//                
//                double higher=0;
//                double tmpCurveWidth=0;
//                
//                for(int i=0;i<len;++i){
//                    
//                    Transition tmp=addedTopEdge.get(i);
//                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSourceState());
//                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTargetState());
//                    System.out.println("     top child "+tmpSourceInd+" -> "+tmpTargetInd);
//
//                    int c=tmpSourceInd;
//                    int d=tmpTargetInd;
//                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}
//
//                    //System.out.println("["+a+" , "+b+"]"+"--["+c+" , "+d+"]");
//                    if(c-d==1) continue;
//                            
//                    if(!(a==c && b==d)){
//                       int n2=vertexNum/2;
//                      if((!reverse && a>=c && b<=d)|| 
//                              (reverse && (a+n2)%vertexNum<=(d+n2)%vertexNum && (b+n2)%vertexNum>=(c+n2)%vertexNum)){
//                        if(tmp.getGeometricData().controlPoints.size()>0){
//                            
//                            Point2D point=tmp.getGeometricData().controlPoints.get(0);
//                            double distsource=point.distance(source.getGeometricData().getLocation());
//                            double disttarget=point.distance(target.getGeometricData().getLocation());
//                            
//                            double tmpHeight;
//                            
//                            if(distsource<disttarget){
//                                double the=Math.atan2(point.getY()-source.getGeometricData().getY(),
//                                        point.getX()-source.getGeometricData().getX());
//                                the-=theta1;
//                                tmpHeight=Math.abs(distsource*Math.sin(the));
//                                System.out.println("    tmpheight="+tmpHeight+" the= "+the);
//
//                                if(height<tmpHeight){
//                                    height=tmpHeight; higher++;
//                                    tmpCurveWidth=Math.abs(distsource*Math.cos(the));
//                                }
//                            }else{
//                                double the=Math.atan2(point.getY()-target.getGeometricData().getY(),
//                                        point.getX()-target.getGeometricData().getX());
//                                the-=theta2;
//                                tmpHeight=Math.abs(disttarget*Math.sin(the));
//                                
//                                System.out.println("    tmpheight="+tmpHeight+" the= "+the);
//
//                                if(height<tmpHeight){
//                                    height=tmpHeight; higher++;
//                                    tmpCurveWidth=Math.abs(distsource*Math.cos(the));
//                                }
//                            }
//                            
//                            
//
//                        }
//                        System.out.println("    higher "+tmpSourceInd+" -> "+tmpTargetInd);
//                        System.out.println("    height"+height);
//
//                      }
//                    }
                    //if(tmpSourceInd>targetInd) break;
//                }
                
//                double h_=height;//+radius*Math.cos((a-b)*theta/2);
//                double a_=tmpCurveWidth;
////                if(a_==0) a_=Math.min(Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
////                                        automata.getStateGeometricData(edge.getSourceState()).getX()),
////                                       Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
////                                        automata.getStateGeometricData(edge.getTargetState()).getX()));
//                
//                double A_=source.getGeometricData().getLocation().distance(target.getGeometricData().getLocation());
//                A_/=2;
//                
                
//                if(a_==0){
                    //if(a-b>2 && a-b!=vertexNum-2){
                        
//                        double phi=(Math.PI-Math.abs(theta1-theta2))/2;
//                        if(reverse) phi=(Math.abs(theta2-theta1)-Math.PI)/2;
//                        
//                        //double dist=
//                        //a_=radius*(Math.sin((a-b)/2*theta)-Math.sin(((a-b)/2-1)*theta));
//                        a_=2*radius*Math.sin(theta/2)*(Math.cos(theta/2+phi)); a_=Math.abs(a_);
//                        h_=2*radius*Math.sin(theta/2)*(Math.sin(theta/2+phi)); h_=Math.abs(h_);
//                        
//                        System.out.println("chord= "+2*radius*Math.sin(theta/2));
//                        double H_=Math.pow(A_/a_,1)*h_+Math.abs(radius*Math.sin(phi));
//                        
//                        System.out.println("A= "+A_+" a="+a_+" h= "+h_+" H_= "+H_);
//                
//                        if(H_>2*radius) H_=2*radius;
//                        
//                        controlPoint1.setLocation(
//                                center.getX()+(H_*1)*Math.cos(midtheta),
//                                center.getY()+(H_*1)*Math.sin(midtheta)
//                                );
                       
                          
//                    }else{
//                        h_+=radius*Math.cos((2)*theta/2);
//                         controlPoint1.setLocation(
//                                center.getX()+(h_*1.2)*Math.cos(theta1),
//                                center.getY()+(h_*1.2)*Math.sin(theta1));
//                         controlPoint2=new Point2D.Double();
//                         controlPoint2.setLocation(
//                                center.getX()+(h_*1.2)*Math.cos(theta2),
//                                center.getY()+(h_*1.2)*Math.sin(theta2));
//                    }
//                }else{
//                    double H_=h_*Math.pow(A_/a_,1.1)+radius*Math.cos((a-b)*theta/2);
//                    
//                    System.out.println("A= "+A_+" a="+a_+" h= "+h_+" H_= "+H_);
//                    if(H_>2*radius) H_=2*radius;
//                        
//                    controlPoint1.setLocation(
//                        center.getX()+(H_)*Math.cos(midtheta),
//                        center.getY()+(H_)*Math.sin(midtheta)
//                        );                    
//                    
//                }
                
//                if(height==middleHeight){
//                double phi=(theta1+theta2)/2-Math.PI/2;
                //if(reverse) phi=(Math.abs(theta2-theta1)-Math.PI)/2;
//                double phi=(theta1+theta2)/2;
//                if(theta1>theta2){
//                    if(theta1>=5*Math.PI/2 && theta2<=3*Math.PI/2+theta) phi+=Math.PI;
//                }else{
//                    if(theta2>=5*Math.PI/2 && theta1<=3*Math.PI/2+theta) phi+=Math.PI;
//                }
//                
//                double t1=theta1-phi;//theta1-(Math.PI-phi)/2;
//                if(t1>Math.PI*3) t1-=2*Math.PI;
                
//                double t2=theta2+phi;//theta1-(Math.PI-phi)/2;
//                if(t2>Math.PI*3) t2-=2*Math.PI;
                
//                if(theta1>5*Math.PI/2 && theta2<3*Math.PI/2) t1+=Math.PI;
//                double t2=t1;//theta2-(Math.PI-phi)/2;
//                        height=height+radius;
//                controlPoint1.setLocation(
//                                center.getX()+(height*1.15)*Math.cos(theta1),
//                                center.getY()+(height*1.15)*Math.sin(theta1));
//                         controlPoint2=new Point2D.Double();
//                         controlPoint2.setLocation(
//                                center.getX()+(height*1.15)*Math.cos(theta2),
//                                center.getY()+(height*1.15)*Math.sin(theta2));
                //if(tmpCurveAngle==0){
                    
//                    if(tmpAnglea<tmpAngleb){
//                        double t=tmpAnglea;
//                        tmpAnglea=tmpAngleb; tmpAngleb=t;
//                    }
                if(tmpAnglea==0 && tmpAngleb==0){
                     double phi=(theta1+theta2)/2;
                    if(theta1>theta2){
                        if(theta1>=5*Math.PI/2 && theta2<=3*Math.PI/2+theta) phi+=Math.PI;
                    }else{
                        if(theta2>=5*Math.PI/2 && theta1<=3*Math.PI/2+theta) phi+=Math.PI;
                    }
                    tmpAnglea=tmpAngleb=phi;
                }
                     if(tmpAnglea==0) tmpAnglea=tmpAngleb;
                     if(tmpAngleb==0) tmpAngleb=tmpAnglea;
                     higher=0;
                     
                     if(reverse){
                         double t=tmpAngleb;
                         tmpAngleb=tmpAnglea; tmpAnglea=t;
                     }
                //}
                System.out.println("    angle= "+tmpAnglea+" "+tmpAngleb+" height= "+(height*1.5));
//                if(!reverse){  
                
                //!!!here
                if(sourceInd>targetInd)
                {
                    System.out.println("Anna~~source>target~~Anna");
                    
                    //System.out.println(source.getGeometricData().getX());//center
                    //System.out.println(source.getGeometricData().getY());
                    //System.out.println(source.getGeometricData().getHeight());
                    //System.out.println(source.getGeometricData().getWidth());
                    double TopRange = source.getGeometricData().getY()+source.getGeometricData().getHeight()/2;
                    double ButtomRange = source.getGeometricData().getY()-source.getGeometricData().getHeight()/2;
                    double LeftRange = source.getGeometricData().getX()-source.getGeometricData().getWidth()/2;
                    double RightRange = source.getGeometricData().getX()+source.getGeometricData().getWidth()/2;
                    //System.out.println(source.getGeometricData().getX()+source.getGeometricData().getWidth()/2);
                    //System.out.println(source.getGeometricData().getY()+source.getGeometricData().getHeight()/2);
                    controlPoint1.setLocation(
                                source.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAnglea),
                                source.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAnglea));
                    controlPoint2=new Point2D.Double();
                    controlPoint2.setLocation(
                                target.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAngleb),
                                target.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAngleb));
                    
                    
                    flag = 3;
                    System.out.println("Anna~~source>target~~Anna");
                    //controlPoint1.setLocation(
                                //source.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAnglea),
                                //source.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAnglea));
                    //controlPoint2=new Point2D.Double();
                    //controlPoint2.setLocation(
                                //target.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAngleb),
                                //target.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAngleb));
                }
                else
                {
                     System.out.println("Anna~~target>source~~Anna");
                    System.out.println(source.getGeometricData().getX());
                    System.out.println(source.getGeometricData().getY());
                    System.out.println(source.getGeometricData().getHeight());
                    System.out.println(source.getGeometricData().getWidth());
                    System.out.println("Anna~~target>source~~Anna");
                    controlPoint1.setLocation(
                                source.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAngleb),
                                source.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAngleb));
                    
                    controlPoint2=new Point2D.Double();
                    controlPoint2.setLocation(
                                target.getGeometricData().getX()+(height*1.5)*Math.cos(tmpAnglea),
                                target.getGeometricData().getY()+(height*1.5)*Math.sin(tmpAnglea));
                
                }
//                }
                
                    
                
//        
       }
//         
     }        
//            points.clear();
        //if(flag == 3)//for test now just source>target
        //{
            //control point1 close to source
            if(controlPoint1!=null)
            {
                points.add(controlPoint1);
                    //if(controlPoint2!=null) points.add(controlPoint2);
                //System.out.println(controlPoint1);

                //edge.getGeometry().setPoints(points);
                TransitionGeometricData tgd=automata.getTransitionGeometricData(edge);
                tgd.controlPoints=points;
                automata.setTransitionGeometricData(edge, tgd);
            }
            
            //control point2 close to target
            if(controlPoint2!=null)
            {
                points.add(controlPoint2);
                    //if(controlPoint2!=null) points.add(controlPoint2);
                //System.out.println(controlPoint1);

                //edge.getGeometry().setPoints(points);
                TransitionGeometricData tgd=automata.getTransitionGeometricData(edge);
                tgd.controlPoints=points;
                automata.setTransitionGeometricData(edge, tgd);
            }
        //}

            
            
//             placeLableToBound(edge);
        
        
            
    }
    
    
    
    /*
     *  add a third control point at the top of the curve
     */
    private void placeLableToBound(Transition edge){
        
    }
    /*
     * Temporary Use:
     * move the whole graph if there are control points outside canvas
     * (with y<0)
     */
    private void moveWholeBound(){
       
    } 
    
    public void setRouteTwoPhase(boolean t){
        routeTwoPhase=t;
    }
    
}

