/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.linear;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;
import vgi.automata.*;
import vgi.layout.helperclass.GroupReplacedAutomata;
import vgi.layout.helperclass.SccDfs;
import vgi.layout.hierarchical.Hvertex;

/**
 *
 * @author reng
 */
public class LinearLayoutAutomata
{

    Automata automata;
    double lineY;
    double upboundY=lineY;

    Rectangle bound;

    List<State> allVertexList;
    List<State> ChosenVertexList;
    int vertexNum;

    List<State> vertexMapList;
    List<Transition> addedTopEdge;
    List<Transition> addedBottomEdge;
    List<Integer> addedTopEdgeHigher = new ArrayList<Integer>();
    List<Integer> addedBottomEdgeHigher = new ArrayList<Integer>();
    
    
    List<State> upPhaseList;
    List<State> downPhaseList;

    List<List<State>> groupList;
    String[][] Group;
    double [][] GroupStructure;
    ////// if there's fixed vertex groups
    boolean useVertexGroup=false;

    /////

    public LinearLayoutAutomata() {}

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
        allVertexList = getSelectedVertices(automata_);
        vertexNum = allVertexList.size();
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);
        System.out.println("allVertexList: "+allVertexList);

        lineY = bound.getCenterY();

        if(allVertexList.size() == automata.getAllStates().size())
        {
            lineY=0;
            bound.x = -bound.width/2;
            if(bound.x<0) bound.x = 0;
        }
        upboundY = bound.getMaxY(); // ???

        System.out.println("lineY= "+lineY);

        //graph.getModel().beginUpdate();
        //    try{
        //make the group to one node
        ChosenVertexList = getSelectedVertices(automata_);
        System.out.println("ChosenVertexList:"+ChosenVertexList);

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
        for(int i=0; i<GID; i++)
        {
            System.out.println(GroupStructure[i][0]+" "+GroupStructure[i][1]+" "+GroupStructure[i][2]+" "+GroupStructure[i][3]+" "+GroupStructure[i][4]);
        }
        System.out.println("groupList.size"+groupList+" "+groupSize);
        System.out.println("ChosenVertexList:"+ChosenVertexList);

        SccDfs sccdfs = new SccDfs(ChosenVertexList);
        vertexMapList = sccdfs.getExpandedListWithSCC();
        System.out.println("vertexMapList:"+vertexMapList);


        setSortedVerticesLocation();



        adjustEdgeCurves(true);

        if(upboundY>bound.getMaxY())
            moveWholeBound();

        System.out.println("~~~There is group~~~");

    }

    public void NonGroupProcess(Automata automata_)
    {
        automata=automata_;
        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();

        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);

        lineY=bound.getCenterY();

        if(allVertexList.size()==automata.getAllStates().size())
        {
            lineY=0;
            bound.x=-bound.width/2;
            if(bound.x<0) bound.x=0;
        }
        upboundY=bound.getMaxY(); // ???

        System.out.println("lineY= "+lineY);

        SccDfs sccdfs=new SccDfs(allVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();

        setSortedVerticesLocation();

        adjustEdgeCurves(true);

        if(upboundY>bound.getMaxY()) moveWholeBound();

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


        /* Adjust the edge style in linear layout */
        adjustEdgeStyle(automata);
        automata.refresh();
        
    }

    // if there is vertexGroup
    public void doLayout(Automata automata_,GroupReplacedAutomata replaceAutomata)
    {

        Automata reautomata=replaceAutomata.createReplaceAutomata();
        reautomata.selectAllStates();
        automata=reautomata;

        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();

        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);

        lineY=bound.getCenterY();

        if(allVertexList.size()==automata.getAllStates().size())
        {
            lineY=0;
            bound.x=-bound.width/2;
        }
        upboundY=lineY; // ???
        System.out.println("lineY= "+lineY);

        SccDfs sccdfs=new SccDfs(allVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();
        setSortedVerticesLocation();

        vertexMapList=replaceAutomata.expandStatesToLinearLayout(vertexMapList);
        vertexNum=vertexMapList.size();

        automata=automata_;

        adjustEdgeCurves(true);

        if(upboundY>bound.getMaxY()) moveWholeBound();

    }

    private List<State> getSelectedVertices(Automata automata_)
    {

        return automata.getSelectedStates();
    }

    private void setSortedVerticesLocation()
    {
        List<State> sorted_vertexMapList = vertexMapList;
        List<State> return_vertexMapList = new ArrayList<State>(); //.add()
        double x = bound.x;
        double distance;
        int g_vertexNum = sorted_vertexMapList.size();

        State first_state = sorted_vertexMapList.get(0);
        return_vertexMapList.add(first_state);
        List<State> first_group  = new ArrayList<State>();
        boolean isGroup = false;
        int gid = 0;
        int first_gid = 0;
        //judge whether the first state is in a group
        for (List<State> state_list: groupList)
        {
            if(state_list.size() > 1)
            {
                if(first_state == state_list.get(0))
                {
                    first_gid = gid;
                    first_group = state_list;
                    isGroup = true;
                }
                gid = gid + 1;
            }
        }
        //assign first state(group) a new position
        if(isGroup) //first_state is a group
        {
            //StateGeometricData first_geodata = automata.getStateGeometricData(first_state);
            //first_geodata.setLocation(new Point2D.Double(x+first_geodata.getWidth()/2,lineY));
            Point2D first_geodata = new Point2D.Double(x+GroupStructure[first_gid][3]/2,lineY);

            double dis_x = first_geodata.getX() - (GroupStructure[first_gid][1]+GroupStructure[first_gid][3]/2);
            double dis_y = first_geodata.getY() - (GroupStructure[first_gid][2]+GroupStructure[first_gid][4]/2);
            System.out.println("GroupStructure_X:"+GroupStructure[first_gid][1]+" ,first_geo_x:"+first_geodata.getX()+" ,dis_x:"+dis_x);
            System.out.println("GroupStructure_Y:"+GroupStructure[first_gid][2]+" ,first_geo_y:"+first_geodata.getY()+" ,dis_y:"+dis_y);
            //automata.setStateGeometricData(first_state, first_geodata);
            return_vertexMapList.remove(return_vertexMapList.size()-1);
            Collections.sort(first_group, new Comparator<State>()
            {
                @Override
                public int compare(State s1, State s2)
                {
                    return Double.compare(automata.getStateGeometricData(s1).getX(), automata.getStateGeometricData(s2).getX());
                }
            });
            for(State state: first_group)
            {
                return_vertexMapList.add(state);
                StateGeometricData state_geodata = automata.getStateGeometricData(state);
                state_geodata.setLocation(new Point2D.Double(state_geodata.getX()+dis_x, state_geodata.getY()+dis_y));
                automata.setStateGeometricData(state, state_geodata);
            }

            //first_geodata=automata.getStateGeometricData(first_state);
            x=first_geodata.getX()+GroupStructure[first_gid][3]/2;
            //distance=GroupStructure[gid][3]/2;
            //distance between 2 nodes = max(node1_w, node1_h) + max(node2_w, node2_h) / 2
            if(GroupStructure[first_gid][3] > GroupStructure[first_gid][4]) distance=GroupStructure[first_gid][3] / 2;
            else distance=GroupStructure[first_gid][4] / 2;

        }
        else
        {
            StateGeometricData first_geodata = automata.getStateGeometricData(first_state);

            first_geodata.setLocation(new Point2D.Double(x+first_geodata.getWidth()/2,lineY));
            automata.setStateGeometricData(first_state, first_geodata);
            first_geodata=automata.getStateGeometricData(first_state);
            x=first_geodata.getX()+first_geodata.getWidth()/2;
            //distance=first_geodata.getWidth()/2;
            if(first_geodata.getWidth() > first_geodata.getHeight()) distance = first_geodata.getWidth() / 2;
            else distance = first_geodata.getHeight() / 2;
        }
        //assign each state a new position
        for(int i = 1; i<g_vertexNum; ++i)
        {
            //mxCell cell=vertexMapList.get(i);
            //mxGeometry geo=cell.getGeometry();
            State state=sorted_vertexMapList.get(i);
            return_vertexMapList.add(state);
            List<State> state_group  = new ArrayList<State>();
            isGroup = false;
            gid = 0;
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
            if(isGroup) //state is a group
            {
                //StateGeometricData geodata=automata.getStateGeometricData(state);
                //geodata.setLocation(new Point2D.Double(x+geodata.getWidth()/2+(distance+geodata.getWidth()/2)*0.3,lineY));
                if(GroupStructure[state_gid][3] > GroupStructure[state_gid][4]) distance = distance + GroupStructure[state_gid][3] / 2;
                else distance = distance + GroupStructure[state_gid][4] / 2;
                //Point2D state_geodata = new Point2D.Double(x+GroupStructure[state_gid][3]/2+(distance+GroupStructure[state_gid][3]/2)*0.3,lineY);
                Point2D state_geodata = new Point2D.Double(x+GroupStructure[state_gid][3]/2+distance,lineY);

                double dis_x = state_geodata.getX() - (GroupStructure[state_gid][1]+GroupStructure[state_gid][3]/2);
                double dis_y = state_geodata.getY() - (GroupStructure[state_gid][2]+GroupStructure[state_gid][4]/2);
                //automata.setStateGeometricData(state, geodata);
                return_vertexMapList.remove(return_vertexMapList.size()-1);
                Collections.sort(state_group, new Comparator<State>()
                {
                    @Override
                    public int compare(State s1, State s2)
                    {
                        return Double.compare(automata.getStateGeometricData(s1).getX(), automata.getStateGeometricData(s2).getX());
                    }
                });
                for(State g_state: state_group)
                {
                    return_vertexMapList.add(g_state);
                    StateGeometricData geodata = automata.getStateGeometricData(g_state);
                    geodata.setLocation(new Point2D.Double(geodata.getX()+dis_x,geodata.getY()+dis_y));
                    automata.setStateGeometricData(g_state, geodata);
                }

                //geodata=automata.getStateGeometricData(state);
                //x=geodata.getX()+geodata.getWidth()/2;
                x=state_geodata.getX()+GroupStructure[state_gid][3]/2;

                //distance=geodata.getWidth()/2;
                if(GroupStructure[state_gid][3] > GroupStructure[state_gid][4]) distance = GroupStructure[state_gid][3] / 2;
                else distance = GroupStructure[state_gid][4] / 2;
                //System.out.println("set location: "+state.getName()+" "+geodata.getX()+","+geodata.getY());
            }
            else
            {
                //StateGeometricData geodata=state.getGeometricData();
                StateGeometricData geodata=automata.getStateGeometricData(state);

                //setVertexLocation(state,x+distance*0.4,lineY-geodata.getHeight()/2);
                //                    automata.moveState(state, new Point2D.Double(x+distance*0.3,lineY));
                if(geodata.getWidth() > geodata.getHeight()) distance = distance + geodata.getWidth() / 2;
                else distance = distance + geodata.getHeight() /2;
                //geodata.setLocation(new Point2D.Double(x+geodata.getWidth()/2+(distance+geodata.getWidth()/2)*0.3,lineY));
                geodata.setLocation(new Point2D.Double(x+geodata.getWidth()/2+distance,lineY));
                automata.setStateGeometricData(state, geodata);
                //x=state.getGeometricData().getX()+state.getGeometricData().getWidth();
                geodata=automata.getStateGeometricData(state);
                x=geodata.getX()+geodata.getWidth()/2;
                //distance=geodata.getWidth()/2;
                if(geodata.getWidth() > geodata.getHeight()) distance = geodata.getWidth() / 2;
                else distance = geodata.getHeight() / 2;
                System.out.println("set location: "+state.getName()+" "+geodata.getX()+","+geodata.getY());
            }
        }
        vertexMapList = return_vertexMapList;
        //System.out.println("vertexMapList " + vertexMapList);
    }


    public void adjustEdgeCurves(boolean ignoreStateInGroups)
    {
        decideEdgesGoUpDown(ignoreStateInGroups);
        setEdgesCurveHeight(ignoreStateInGroups);

    }
    public void adjustEdgeCurves(Automata automata_,List<State> vertexList)
    {
        automata=automata_;
        vertexMapList=vertexList;
        vertexNum=vertexMapList.size();
        System.out.println("adjust linear layout edges: "+vertexMapList);

        bound=automata.computeBox(vertexMapList);
        System.out.println("box: "+bound);

        lineY=0;
        for(State s:vertexList)
        {
            lineY+=s.getGeometricData().getY();
        }
        lineY/=vertexNum;

        // sort state order in vertexList by x-coordinates
        Collections.sort(vertexList, new Comparator<State>()
        {
            @Override
            public int compare(State s1, State s2)
            {
                Double position1=s1.getGeometricData().getX();
                Double position2=s2.getGeometricData().getX();
                return position1.compareTo(position2);
            }

        });


        decideEdgesGoUpDown(false);
        setEdgesCurveHeight(false);

    }
    /*
     *  decide whether an edge goes up or down, choose the one with fewer crossings
     *
     *  addedTopEdge,addedBottomEdge are updated
     */
    private void decideEdgesGoUpDown(boolean ignoreStateInGroups)
    {
        addedTopEdge=new ArrayList<Transition>();
        addedBottomEdge=new ArrayList<Transition>();

        upPhaseList=new ArrayList<State>();
        downPhaseList=new ArrayList<State>();

        // if ignore state in groups, first put edges in groups into top/bottom list
//            if(ignoreStateInGroups){
        for(int i = 0; i<vertexNum; ++i)
        {
            State state=vertexMapList.get(i);
            int phase=0;

            upPhaseList.add(state);
            downPhaseList.add(state);
//                    }

            if(ignoreStateInGroups)
            {
                int id=state.getGroupID();
                if(id==-1) continue;

                List<Transition> outGoingEdges=state.getTransitions();
                int edgeCount=outGoingEdges.size();
                for(int j=0; j<edgeCount; ++j)
                {
                    Transition edge=outGoingEdges.get(j);


                    State target=edge.getTargetState();
                    if(target==state) target=edge.getSourceState();
                    if(target==state) continue; //loop


                    // if the other terminal is not in the same group
                    if(target.getGroupID()!=id)
                    {
                        // if one terminal point is above/below horizontal line, add to top/bottom directly.
                        int phaset=0;
                        if(target.getGeometricData().getY()>lineY) phaset=1;
                        else if(target.getGeometricData().getY()<lineY) phaset=-1;

                        if(phase>0 || phaset>0)
                        {
                            addedTopEdge.add(edge);
                        }
                        else if(phase<0 || phaset<0)
                        {
                            addedBottomEdge.add(edge);
                        }
                    }
                    else   // ingroup edges
                    {
                        List<Point2D> controlPoints=edge.getGeometricData().controlPoints;
                        System.out.println("edge in group :control points: "+controlPoints);
                        if(controlPoints.size()>0)
                        {
                            Point2D firstPoint=controlPoints.get(0);
                            if(firstPoint.getY()>lineY)
                            {
                                addedTopEdge.add(edge);
                                continue;
                            }
                            else
                            {
                                addedBottomEdge.add(edge);
                                continue;
                            }
                        }
                    }
                }
// if one terminal point is above/below horizontal line, add to top/bottom directly.

            }
        }


        for(int i = 0; i<vertexNum; ++i)
        {
            State state=vertexMapList.get(i);


            List<Transition> outGoingEdges=state.getOutgoingTransitions();
            int edgeCount=outGoingEdges.size();

            for(int j=0; j<edgeCount; ++j)
            {

                Transition edge=outGoingEdges.get(j);
                State target=edge.getTargetState();

                // edge in same group don't change
                //List<State> srcGroupList = automata.getGroup(edge.getSourceState());
                //if(srcGroupList.contains( target )) continue;

                // if both terminals of the edge are in vertexGroup,
                // add to top/bottom by existing control points!
                // TODO: problem happens when they are in different vertexGroup??

                if(addedTopEdge.contains(edge)|| addedBottomEdge.contains(edge)) continue;

                if(target!=null)
                {
                    int sourceIndex=vertexMapList.indexOf(edge.getSourceState());
                    int targetIndex=vertexMapList.indexOf(edge.getTargetState());

                    if(sourceIndex>=0 && targetIndex>=0)
                    {
                        if(edgeGoUp(edge))
                        {
                            addedTopEdge.add(edge);
                        }
                        else
                        {
                            addedBottomEdge.add(edge);
                        }
                    }
                }
            }
        }

        for(int i = 0; i<vertexNum; ++i)  //self-loop
        {
            State state=vertexMapList.get(i);
            List<Transition> loopEdges=state.getLoopTransitions();
            int loopCount=loopEdges.size();
            for(int j=0; j<loopCount; ++j)
            {
                Transition edge=loopEdges.get(j);
                // edge in same group don't change
                //List<State> srcGroupList = automata.getGroup(edge.getSourceState());
                //if(srcGroupList.size()>1) continue;
                if(edgeGoUp(edge))
                {
                    addedTopEdge.add(edge);
                }
                else
                {
                    addedBottomEdge.add(edge);
                }
            }
        }

    }
    /*
     * decide an edge to go up or down, depends on minimum crossings
     * if up return true, down return false
     */
    private boolean edgeGoUp(Transition edge)
    {


        int sourceIndex=vertexMapList.indexOf(edge.getSourceState());
        int targetIndex=vertexMapList.indexOf(edge.getTargetState());

        int a=sourceIndex;
        int b=targetIndex;
        if(targetIndex<sourceIndex)
        {
            a=targetIndex;
            b=sourceIndex;
        }

        System.out.println("crossing of "+edge.getSourceState().getName()+" -> "+edge.getTargetState().getName());
        System.out.println("            "+sourceIndex+" -> "+targetIndex);



        int topCrossing=0,bottomCrossing=0;

        // Check if there's a backward edge.
        // Since edges are determined from left to right, check only edges with (target <- source)
        if(sourceIndex>targetIndex && automata.hasBackwardTransition(edge))
        {

            System.out.println(" a backward edge!");
            Transition backedge = null;
            List<Transition> incomedges=edge.getSourceState().getIncomingTransitions();
            for(Transition tran:incomedges)
            {
                if(tran.getSourceState()==edge.getTargetState())
                {
                    backedge=tran;
                    break;
                }
            }
            if(backedge!=null)
            {
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
            if(targetInd<sourceInd)
            {
                c=targetInd;
                d=sourceInd;
            }
//            System.out.println("check  cross top: "+sourceInd+"->"+targetInd);
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex)
            if((c>a && c<b && d>b) ||(d>a && d<b && c<a))
            {
                topCrossing++;
//                System.out.println("  cross top: "+sourceInd+"->"+targetInd);
            }
        }

        for(Transition bottomEdge:addedBottomEdge)
        {
            int sourceInd=vertexMapList.indexOf(bottomEdge.getSourceState());
            int targetInd=vertexMapList.indexOf(bottomEdge.getTargetState());

            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd)
            {
                c=targetInd;
                d=sourceInd;
            }

//            System.out.println("check  cross bottom: "+sourceInd+"->"+targetInd);

            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex)
            if((c>a && c<b && d>b) ||(d>a && d<b && c<a))
            {
                bottomCrossing++;
//                System.out.println("cross bottom: "+sourceInd+"->"+targetInd);
            }
        }
        //if(edge.getSourceState() == edge.getTargetState()) //selfloop
        if(a == b)
        {
            System.out.println("selfloopUpDown"+edge);
            for(Transition topEdge:addedTopEdge)
            {
                int sourceInd=vertexMapList.indexOf(topEdge.getSourceState());
                int targetInd=vertexMapList.indexOf(topEdge.getTargetState());

                int c=sourceInd;
                int d=targetInd;
                if(targetInd<sourceInd)
                {
                    c=targetInd;
                    d=sourceInd;
                }
                //            System.out.println("check  cross top: "+sourceInd+"->"+targetInd);
                //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex)
                if(Math.abs(c-d)!=1 && ((a==c) ||(a==d)) )  //next to don't cause crossing
                {
                    topCrossing++;
                    //                System.out.println("  cross top: "+sourceInd+"->"+targetInd);
                }
            }
            for(Transition bottomEdge:addedBottomEdge)
            {
                int sourceInd=vertexMapList.indexOf(bottomEdge.getSourceState());
                int targetInd=vertexMapList.indexOf(bottomEdge.getTargetState());

                int c=sourceInd;
                int d=targetInd;
                if(targetInd<sourceInd)
                {
                    c=targetInd;
                    d=sourceInd;
                }
                //            System.out.println("check  cross top: "+sourceInd+"->"+targetInd);
                //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex)
                if(Math.abs(c-d)!=1 && ((a==c) ||(a==d)) )
                {
                    bottomCrossing++;
                    //                System.out.println("  cross top: "+sourceInd+"->"+targetInd);
                }
            }
            System.out.println("up:"+topCrossing+" bottom:"+bottomCrossing);
        }
        //System.out.println("top "+topCrossing+" / bottom "+bottomCrossing);

        automata.resetTransitionControlPoint(edge);

        if(bottomCrossing<topCrossing) return false;
        else return true;

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

                Integer length=Math.abs(sourceIndex-targetIndex);
                Integer length1=Math.abs(sourceIndex1-targetIndex1);
                return length.compareTo(length1);

            }

        });
        Collections.sort(addedBottomEdge, new Comparator<Transition>()
        {
            @Override
            public int compare(Transition t, Transition t1)
            {
                Integer sourceIndex=vertexMapList.indexOf(t.getSourceState());
                Integer targetIndex=vertexMapList.indexOf(t.getTargetState());
                Integer sourceIndex1=vertexMapList.indexOf(t1.getSourceState());
                Integer targetIndex1=vertexMapList.indexOf(t1.getTargetState());

                Integer length=Math.abs(sourceIndex-targetIndex);
                Integer length1=Math.abs(sourceIndex1-targetIndex1);
                return length.compareTo(length1);

            }

        });
        System.out.println("--------set edges curve height-----");
        ;
        for(Transition tr:addedTopEdge)
            System.out.println("now in top:  "+tr.getSourceState()+"->"+tr.getTargetState());

        for(Transition tr:addedBottomEdge)
            System.out.println("now in bottom:  "+tr.getSourceState()+"->"+tr.getTargetState());
        System.out.println("upPhase: "+upPhaseList);
        System.out.println("downPhase: "+downPhaseList);


        int len=addedTopEdge.size();
        for(int i=0; i<len; ++i)
        {
            Transition edge=addedTopEdge.get(i);

            // if ignore state in groups, only edge with one terminal outside group is considered
            // and reset edge curve height
            if(ignoreStateInGroups)
            {
                int sid=edge.getSourceState().getGroupID();
                int tid=edge.getTargetState().getGroupID();

                if(sid!=tid || (sid==-1 && tid==-1))
                {
                    setEdgeCurveHeight(edge,true);
                    System.out.println("!!!There contain loop"+sid+tid);
                }
            }
            else
            {
                setEdgeCurveHeight(edge,true);

            }
        }

        len=addedBottomEdge.size();
        for(int i=0; i<len; ++i)
        {
            Transition edge=addedBottomEdge.get(i);
            if(ignoreStateInGroups)
            {
                int sid=edge.getSourceState().getGroupID();
                int tid=edge.getTargetState().getGroupID();

                if(sid!=tid || (sid==-1 && tid==-1))
                    setEdgeCurveHeight(edge,false);
            }
            else
            {
                setEdgeCurveHeight(edge,false);
            }
        }
    }
    /*
    * adjust edges' curve height
    */
    private void setEdgeCurveHeight(Transition edge, boolean isTopEdge)
    {
        List<State> phaseList=(isTopEdge)?upPhaseList:downPhaseList;
        List<Transition> addedEdge=(isTopEdge)?addedTopEdge:addedBottomEdge;
        
        List<Point2D> points = new ArrayList<Point2D>();
        
        if(automata!=null) automata.resetTransitionControlPoint(edge);
        int sourceInd=phaseList.indexOf(edge.getSourceState());
        int targetInd=phaseList.indexOf(edge.getTargetState());
        int edgeInd = addedEdge.indexOf(edge); //0901


        double src_x = edge.getSourceState().getGeometricData().getX(); 
        double src_y = edge.getSourceState().getGeometricData().getY();
        double src_w = edge.getSourceState().getGeometricData().getWidth();
        double src_h = edge.getSourceState().getGeometricData().getHeight();

        double target_x = edge.getTargetState().getGeometricData().getX(); 
        double target_y = edge.getTargetState().getGeometricData().getY();
        double target_w = edge.getTargetState().getGeometricData().getWidth();
        double target_h = edge.getTargetState().getGeometricData().getHeight();

        double center_x = (src_x + target_x) / 2;
        double center_y = (src_y + target_y) / 2;
        
        Point2D controlPoint1 = null;
        Point2D controlPoint2 = null;
        if(Math.abs(sourceInd-targetInd)==1)//next to (out of group)
        {    
            int next_higher = 0;
            
            for(int i=0; i<edgeInd; ++i)
            {
                Transition tmp=addedEdge.get(i);
                int tmp_srcInd=phaseList.indexOf(tmp.getSourceState());
                int tmp_tgtInd=phaseList.indexOf(tmp.getTargetState());
                if(sourceInd==tmp_srcInd && targetInd==tmp_tgtInd)
                    next_higher++;
                if(sourceInd==tmp_tgtInd && targetInd==tmp_srcInd)
                    next_higher++;
            }
            if(automata.hasBackwardTransition(edge)) next_higher++;
            if(isTopEdge)
            {
                controlPoint1=new Point2D.Double();    
                controlPoint1.setLocation(center_x,lineY+(10*next_higher));
            }
            else
            {
                controlPoint1=new Point2D.Double();    
                controlPoint1.setLocation(center_x,lineY+((-10)*next_higher));
            }
            if(isTopEdge) addedTopEdgeHigher.add(0);
            else addedBottomEdgeHigher.add(0);
            points.clear();
          
            if(controlPoint1 != null){
                points.add(controlPoint1);
                if(controlPoint1.getY()>upboundY) upboundY=controlPoint1.getY();
              
                TransitionGeometricData tgd = automata.getTransitionGeometricData(edge);
                tgd.controlPoints = points;
                automata.setTransitionGeometricData(edge, tgd);
            }
        }
        else if(sourceInd == targetInd)
        {//self loop (out of group)
            Point2D controlPoint = new Point2D.Double();

            double selfloop_h;
            
            if(src_h>src_w) selfloop_h = src_h;
            else selfloop_h = src_w;
            
            int higher_level = 0;
            for(int i=0; i<edgeInd; ++i)
            {
                Transition tmp=addedEdge.get(i);
                int tmp_srcInd=phaseList.indexOf(tmp.getSourceState());
                int tmp_tgtInd=phaseList.indexOf(tmp.getTargetState());
                int tmpInd=addedEdge.indexOf(tmp); 
                if(tmp_srcInd==sourceInd&&tmp_tgtInd==targetInd)
                    higher_level++;
            } 
            double bonus = higher_level*10;
            
            if(isTopEdge) controlPoint.setLocation(center_x, src_y+selfloop_h+bonus);
            else controlPoint.setLocation(center_x, src_y-selfloop_h-bonus);
                
            if(isTopEdge) addedTopEdgeHigher.add(0);
            else addedBottomEdgeHigher.add(0);
            points.clear();
            if(controlPoint != null)
            {
                points.add(controlPoint);

                TransitionGeometricData tgd = automata.getTransitionGeometricData(edge);
                tgd.controlPoints = points;
                automata.setTransitionGeometricData(edge, tgd);
            }
        }
        else
        {
            Point2D controlPointM = new Point2D.Double();
            Point2D controlPointL = new Point2D.Double();
            Point2D controlPointR = new Point2D.Double();
            
            double sin_deg = Math.sin(45.0/180.0*Math.PI);  //sin 45
            double cos_deg = Math.cos(45.0/180.0*Math.PI);  //cos 45
            double radius =  Math.abs((src_x-target_x) / 2);
            int num = addedEdge.size();
            int higher_level = 0;
            for(int i=0; i<edgeInd; ++i)
            {
                Transition tmp=addedEdge.get(i);
                int tmp_srcInd=phaseList.indexOf(tmp.getSourceState());
                int tmp_tgtInd=phaseList.indexOf(tmp.getTargetState());
                int tmpInd=addedEdge.indexOf(tmp); 
                int a = sourceInd;
                int d = targetInd;
                int b = tmp_srcInd;
                int c = tmp_tgtInd;
                if(sourceInd>targetInd) {d = sourceInd; a = targetInd;}
                if(tmp_srcInd>tmp_tgtInd) {c = tmp_srcInd; b = tmp_tgtInd;}
                
                if(a<=b && c<=d)
                {    
                    if(isTopEdge)
                    {
                        int tmp_h_lv = addedTopEdgeHigher.get(i);
                        if(tmp_h_lv > higher_level) higher_level = addedTopEdgeHigher.get(i);
                    }
                    else
                    {
                        int tmp_h_lv = addedBottomEdgeHigher.get(i);
                        if(tmp_h_lv > higher_level) higher_level = addedBottomEdgeHigher.get(i);
                    }
                }

                if(tmp_srcInd==sourceInd&&tmp_tgtInd==targetInd)
                    higher_level++;
                if(tmp_srcInd==targetInd&&tmp_tgtInd==sourceInd)
                    higher_level++;
                

            }

            if(isTopEdge) addedTopEdgeHigher.add(higher_level);
            else addedBottomEdgeHigher.add(higher_level);
            
            double bonus = higher_level*10;
            if(isTopEdge)
            {
                controlPointM.setLocation(center_x, center_y + radius + bonus);
                controlPointL.setLocation(center_x+radius*cos_deg, center_y+radius*sin_deg + bonus);
                controlPointR.setLocation(center_x-radius*cos_deg, center_y+radius*sin_deg + bonus);
                if(controlPointM.getY() > upboundY) upboundY = controlPointM.getY();
            }
            else
            {
                controlPointM.setLocation(center_x, center_y - radius - bonus);
                controlPointL.setLocation(center_x+radius*cos_deg, center_y-radius*sin_deg - bonus);
                controlPointR.setLocation(center_x-radius*cos_deg, center_y-radius*sin_deg - bonus);
            }
            points.clear();
            if(controlPointM != null)
            {
                if(src_x<target_x)
                {
                    points.add(controlPointR);
                    points.add(controlPointM);
                    points.add(controlPointL);
                }
                else
                {
                    points.add(controlPointL);
                    points.add(controlPointM);
                    points.add(controlPointR);                    
                }
                TransitionGeometricData tgd = automata.getTransitionGeometricData(edge);
                tgd.controlPoints = points;
                automata.setTransitionGeometricData(edge, tgd);
            }
        }
    }

    public void setUseVertexGroup(boolean use)
    {
        useVertexGroup=use;
    }
    /*
     * Temporary Use:
     * move the whole graph if there are control points outside canvas
     * (with y<0)
     */
    private void moveWholeBound(){
        //graph.refresh();
        
        double offsetx=upboundY;//-bound.getMaxX()+50;
        
        automata.moveStates(vertexMapList, new Point2D.Double(0,-offsetx));
        System.out.println("move all vertices: "+offsetx);
    }


    /**
     * Adjust edge style corresponds to the linear layout: curve
     * @param automata_ 
     */
    public void adjustEdgeStyle(Automata automata_) {
        
        System.out.println("current: " + automata_);

        List<State> StateList = new ArrayList<State>();
        StateList.addAll(automata_.getAllStates());
        for (State s: StateList) {

            List<Transition> tl = s.getIncomingTransitions();

            
            for (Transition t: tl) {
                System.out.println("transition: " + t.getDrawingData().getShape());
                TransitionDrawingData td = t.getDrawingData();
                td.setShape("curve");
                automata_.setTransitionDrawingData(t, td);
                System.out.println("after transition: " + t.getDrawingData().getShape());
            }

        }

    }
}
