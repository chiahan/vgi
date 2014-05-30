/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.helperclass;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import vgi.automata.Automata;
import vgi.automata.State;
import vgi.automata.StateGeometricData;
import vgi.automata.Transition;
import vgi.layout.circular.CircularLayoutAutomata;
import vgi.layout.linear.LinearLayoutAutomata;

/**
 *
 * @author reng
 */
public class GroupReplacedAutomata {

    Automata originAutomata;
    Automata replaceAutomata;
    List<VertexGroup> groupList;
    List<State> replaceStatesForGroups;
    List<State> upPhase=null;
    List<State> downPhase=null;
    
    public GroupReplacedAutomata(Automata origin){
        originAutomata=origin;
        groupList=originAutomata.getGroupList();
        
    }
    public Automata createReplaceAutomata(){
        
        System.out.println("--- create GroupReplacedAutomata:");
        
        replaceAutomata=new Automata();
        replaceAutomata.setWeight(originAutomata.getWeight());
        replaceAutomata.setAlphabet(originAutomata.getAlphabet());
        replaceAutomata.setWritingData(originAutomata.getWritingData());
        
        // add states not in vertexgroups
        List<State> originStates=originAutomata.getAllStates();
        for(State originState:originStates){
            int vg=inSomeVertexGroup(originState);
            if(vg<0){
                // copy all data but transitions of the origin state
                State copyState=new State();
                copyState.setGeometricData(originState.getGeometricData());
                copyState.setDrawingData(originState.getDrawingData());
                copyState.setInitial(originState.getInitial());
                copyState.setFinal(originState.getFinal());
                copyState.setName(originState.getName());
                replaceAutomata.addState(copyState);
                
                System.out.println("copy state: "+copyState.getName());
             }
        }
        // add states representing vertexgroups
        createRepresentStatesForVertexGroups();
        for(State represent:replaceStatesForGroups){
            replaceAutomata.addState(represent);
            System.out.println("add replace state: "+represent.getName());
        }
        
        // add transitions
        List<Transition> originTransitions=originAutomata.getAllTransitions();
        for(Transition trans:originTransitions){
            
            State source=trans.getSourceState();
            State target=trans.getTargetState();
            
            int sourceVgIndex=inSomeVertexGroup(source);
            int targetVgIndex=inSomeVertexGroup(target);
            
            if(sourceVgIndex>=0) source=replaceStatesForGroups.get(sourceVgIndex);
            else source=getReplaceState(source);
            if(targetVgIndex>=0) target=replaceStatesForGroups.get(targetVgIndex);
            else target=getReplaceState(target);
            
            Transition newTransition=replaceAutomata.addTransition(source, target);
            newTransition.setLabel(trans.getLabel());
        }
        
        
        
        for(State st:replaceAutomata.getAllStates()){
                System.out.println(st.getName());
                List<Transition> trans=st.getTransitions();
                for(Transition tr:trans){
                    System.out.println("   "+tr.getSourceState().getName()+" -> "+tr.getTargetState().getName());
                }
        }
        return replaceAutomata;
    }
    public List<State> getRepresentStateList(){
        
        return replaceAutomata.getAllStates();
    }
    
    public void createRepresentStatesForVertexGroups(){
            
            int len=groupList.size();
            replaceStatesForGroups=new ArrayList<State>();
            
            for(int i=0;i<len;++i){
                VertexGroup vg=groupList.get(i);
                double[] bound=vg.getBound(); // x,y,w,h
                
//                State state=addState(new Point2D.Double(bound[0],bound[1]));
                State state=new State();
                state.setGeometricData(new StateGeometricData(bound[0],bound[1],bound[2],bound[3],"Rectangle"));
                state.setName("g"+(i+1));
//                state.getGeometricData().setWidth(bound[2]);
//                state.getGeometricData().setHeight(bound[3]);
                replaceStatesForGroups.add(state);
            }
           
    }
    /*
     * return the index of the belonging VertexGroup in groupList
     */
    public int inSomeVertexGroup(State state){
        
       
        int index=-1;
        int len=groupList.size();
        for(int i=0;i<len;++i){
            VertexGroup vg=groupList.get(i);
            List<State> statesInGroup=vg.getStateList();
//            for(State st:statesInGroup){
//                if(st.getName().equals(state.getName()) && st.getGeometricData()==state.getGeometricData()){
//                    index=i;
//                    break;
//                }
//            }
//            index=statesInGroup.indexOf(state);
            if(statesInGroup.contains(state)){
                index=i; break;
            }
        }
        
//        System.out.println("check if state is in some VertexGroup: "+state.getName()+" -> "+index);
        return index;
    }
    private State getReplaceState(State originState){
        
        for(State s:replaceAutomata.getAllStates()){
            if(s.getName().equals(originState.getName()))// && s.getGeometricData()==originState.getGeometricData())
                return s;
        }
        return null;
    }
    private State getOriginState(State replaceState){
        for(State s:originAutomata.getAllStates()){
            if(s.getName().equals(replaceState.getName())) return s;
        }
        return null;
    }
    
    public void expandStatesPositionToVertexGroups(){
        
        int len=groupList.size();
        for(int i=0;i<len;++i){
            
                VertexGroup vg=groupList.get(i);
                State representState=replaceStatesForGroups.get(i);
                
                double[] vgBound=vg.getBound();
                double stateX=representState.getGeometricData().getX();
                double stateY=representState.getGeometricData().getY();
                
                double offsetX=stateX-vgBound[0];
                double offsetY=stateY-vgBound[1];
                
                originAutomata.moveStatesWithoutUpdatingOuterTransitions(vg.getStateList(), new Point2D.Double(offsetX, offsetY));
        }
        List<State> originStates=originAutomata.getAllStates();
        for(State state:originStates){
            State replaceState=getReplaceState(state);
            if(replaceState==null) continue;
            else{
                originAutomata.moveState(state, replaceState.getGeometricData().getLocation());
            }
        }
        
        
        
//        // todo: handle transitions by re-routing edges inside VertexGroups
//        for(int i=0;i<len;++i){
//            
//                VertexGroup vg=groupList.get(i);
//                if(vg.getLayoutType()==VertexGroup.LINEAR_GROUP){
//                    
//                    LinearLayoutAutomata layout=new LinearLayoutAutomata();
//                    originAutomata.setSelectedStates(vg.getStateList());
//                    layout.adjustEdgeCurves(false);
//                }
//        }
        
    }
    public List<State> expandStatesToLinearLayout(List<State> vertexOrderList){
         // update locations of all states to the new locations
        expandStatesPositionToVertexGroups();
        
        
        ArrayList<State> expandList=new ArrayList<State>();
        
        int len=vertexOrderList.size();
        for(int i=0;i<len;++i){
            
            State stateInList=vertexOrderList.get(i);
            
            // if is a state replaced for some vertexGroup
            // add states in vertexGroup by order of x position
            int index=replaceStatesForGroups.indexOf(stateInList);
            if(index>=0){
                VertexGroup vg=groupList.get(index);
                
                List<State> statesInVG=vg.getStateList();
                if(vg.getLayoutType()==VertexGroup.LINEAR_GROUP){
                    Collections.sort(statesInVG, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=s1.getGeometricData().getX();
                            Double position2=s2.getGeometricData().getX();
                            return position1.compareTo(position2);
                        }
                            
                    });
                }else if(vg.getLayoutType()==VertexGroup.CIRCULAR_GROUP){
                    final Double centerx=stateInList.getGeometricData().getX();
                    final Double centery=stateInList.getGeometricData().getY();
                    
                    Collections.sort(statesInVG, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=Math.atan2(s1.getGeometricData().getY()-centery, s1.getGeometricData().getX()-centerx);
                            Double position2=Math.atan2(s2.getGeometricData().getY()-centery, s2.getGeometricData().getX()-centerx);
                            return position1.compareTo(position2);
                        }
                            
                    });
                    
                }
                
                System.out.println("state in group: "+statesInVG);
        
                for(State s:statesInVG) expandList.add(s);
                
            }else{
                expandList.add(getOriginState(stateInList));
            }
            
        }
        
        System.out.println("expand linear list: "+expandList);
        
        // sifting states to get less crossings
        for(VertexGroup g:groupList){
            if(g.getLayoutType()==VertexGroup.LINEAR_GROUP){
                siftStatesOrderInVertexGroup(g,expandList,false,false);
                // reroute edges
                LinearLayoutAutomata linearlayout=new LinearLayoutAutomata();
                linearlayout.adjustEdgeCurves(originAutomata,g.getStateList());
            }
            else{
                
                // set up two states orderings
                upPhase=new ArrayList<State>();
                downPhase=new ArrayList<State>();
                int num=expandList.size();
                for(int i=0;i<num;++i){
                    
                    State s=expandList.get(i);
                    double baseY=replaceStatesForGroups.get(groupList.indexOf(g)).getGeometricData().getY();
           
                    double y=s.getGeometricData().getY();
                    if(y>baseY) upPhase.add(s);
                    else if(y==baseY){
                        upPhase.add(s); downPhase.add(s);
                    }else downPhase.add(s);
                    
                }
                Collections.sort(upPhase, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=s1.getGeometricData().getX();
                            Double position2=s2.getGeometricData().getX();
                            return position1.compareTo(position2);
                        }
                            
                    });
                System.out.println("upPhase: "+upPhase);
                System.out.println("downPhase: "+downPhase);
                
                siftStatesOrderInVertexGroup(g,expandList,true,false);
                //reroute edges
                CircularLayoutAutomata circularlayout=new CircularLayoutAutomata();
                circularlayout.setRouteTwoPhase(false);
                circularlayout.adjustEdgeCurves(originAutomata, g.getStateList());
                
            }
        }
        System.out.println("after adjust linear list: "+expandList);
        
       
        
//        List<Transition> removedTransitions=removeInnerTransitions();
        
        
        return expandList;
    }
    
    /* 
     *  exchange neihboring states to minimize crossings
     *  circular: if the local layout is circular
     */
    private void siftStatesOrderInVertexGroup(VertexGroup vg,ArrayList<State> expandList,boolean localcircular,boolean globalcircular){
        //for(VertexGroup vg:groupList){
            List<State> stateInGroup=vg.getStateList();
            int len=stateInGroup.size();
            
            int start=expandList.indexOf(stateInGroup.get(0));
                
            for(int i=0;i<len;++i){
                State state1=stateInGroup.get(i);
                int pos1;//=expandList.indexOf(state1);
                   
//                int j=i;
//                int crossing=0,crossingAfterExchange=0;
                
                //while(crossing<=crossingAfterExchange){
                for(int j=1;j<len;++j){
                    
                    pos1=expandList.indexOf(state1);
                    
                    System.out.println(expandList);
                    
                    int index2=pos1+1;
                    if(localcircular) index2=(index2-start)%len+start;
                    
                    if(index2>=start+len){ 
                        System.out.println("pos1= "+pos1+" start= "+start+" index2= "+index2);
                        break;
                    }
                    
                    
                    State state2=expandList.get(index2);
                    
                    if(!stateInGroup.contains(state2)) continue;
                    int pos2=expandList.indexOf(state2);
                    
                    
                    double baseY;
                    boolean phase1=false, phase2=false;
                    int ind1=-1,ind2=-1;
                    
                   // for a global linear layout, only the local circular should be put into two phase
                   // for a global circualr, both local circular and linear should be put into two phase
                   
                    //if((!globalcircular && localcircular) || globalcircular){
                   if(upPhase!=null){    
                       //baseY=replaceStatesForGroups.get(inSomeVertexGroup(state1)).getGeometricData().getY();
                            phase1=upPhase.contains(state1);
                            phase2=upPhase.contains(state2);
                            ind1=(phase1)?upPhase.indexOf(state1):downPhase.indexOf(state1);
                            ind2=(phase2)?upPhase.indexOf(state2):downPhase.indexOf(state2);
                       
                   }
                   
                    System.out.println("deside to exchange: "+state1+" -- "+state2);
                    //int crossing=crossingsOfLinear(state1,expandList,circular)+crossingsOfLinear(state2,expandList,circular);
                    int crossing=crossingsBetweenTwoStates(state1,state2,expandList,localcircular,globalcircular);
                    System.out.println("        #crossing = "+crossing);  
//                expandList.remove(i+1);
//                expandList.add(i, state2);
                    expandList.set(pos1, state2);
                    expandList.set(pos2, state1);
                    if(upPhase!=null){
                            
                        if(phase1) upPhase.set(ind1,state2);
                        else downPhase.set(ind1,state2);
                            
                        if(phase2) upPhase.set(ind2,state1);
                        else downPhase.set(ind2,state1);
                    }
                    //int crossingAfterExchange=crossingsOfLinear(state1,expandList,circular)+crossingsOfLinear(state2,expandList,circular);
                    int crossingAfterExchange=crossingsBetweenTwoStates(state1,state2,expandList,localcircular,globalcircular);
                    System.out.println("        #crossing after exchange= "+crossingAfterExchange);
                
                    if(crossingAfterExchange>=crossing){
                        //exchange back and stop sifting
                        expandList.set(pos1, state1);
                        expandList.set(pos2, state2);
                        if(upPhase!=null){
                            
                            if(phase1) upPhase.set(ind1,state1);
                            else downPhase.set(ind1,state1);
                            
                            if(phase2) upPhase.set(ind2,state2);
                            else downPhase.set(ind2,state2);
                        }
                        System.out.println("    do not exchange: "+state1+" -- "+state2);  
                        break;
                        
                    }else{
                        System.out.println("    exchange: "+state1+" -- "+state2);  
                        
                        
                        Point2D tmp=state1.getGeometricData().getLocation();
                        state1.getGeometricData().setLocation(state2.getGeometricData().getLocation());
                        state2.getGeometricData().setLocation(tmp);
                        
                        
                    
                    }
                    
                }
                
            }
        //}
        
    }
    
    
    
    /* compute the crossings between two consecutive states
     * only the edges inserting to either states are considered
     * used for sifting
     * index of state1 < index of state2 in the expandList
     */
    public int crossingsBetweenTwoStates(State state1,State state2,List<State>expandList,boolean localcircular,boolean globalcircular){
        int cross=0;
        boolean phase1,phase2;
        List<State> phaseList;
        
        // if the two states aren't in the same phase
        if(localcircular){
           //double baseY=replaceStatesForGroups.get(inSomeVertexGroup(state1)).getGeometricData().getY();
           phase1=(upPhase.contains(state1));
           phase2=(upPhase.contains(state2));
           
           if(phase1!=phase2){
               cross=crossingsWithPhase(state1,phase1,expandList,globalcircular)+crossingsWithPhase(state2,phase2,expandList,globalcircular);
               return cross;
           }
           if(phase1 && phase2) phaseList=upPhase;
           else phaseList=downPhase;
           
        }else{
            
            phaseList=expandList;
        }        
        
        List<Transition> trans1=state1.getTransitions();
        List<Transition> trans2=state2.getTransitions();
        
        int index1=phaseList.indexOf(state1);
        int index2=phaseList.indexOf(state2);
        
        int n=expandList.size();
        
        for(Transition trans:trans1){
            int a=phaseList.indexOf(trans.getSourceState());
            int b=phaseList.indexOf(trans.getTargetState());
            
            System.out.println("    check crossing: "+trans.getSourceState()+" "+a+" -- "+trans.getTargetState()+" "+b);
            
            if(Math.abs(b-a)<=1) continue;
            
            int end1=(a==index1)?b:a;
            
            for(Transition checktrans:trans2){
                int c=phaseList.indexOf(checktrans.getSourceState());
                int d=phaseList.indexOf(checktrans.getTargetState());
                //System.out.println("        crossing: "+checktrans.getSourceState()+" "+c+" -- "+checktrans.getTargetState()+" "+d);
                
                if(c==-1 || d==-1) continue;
                if(Math.abs(c-d)<=1) continue;
                
                int end2=(c==index2)?d:c;
                if(!globalcircular){
                    if((end1>index2 && end2<index1)||(end1>index2 && end2>end1)){
                        System.out.println("        is crossing: "+checktrans.getSourceState()+" "+c+" -- "+checktrans.getTargetState()+" "+d);
                        cross++;
                    }
                }else{
                    if((end2>index1 && end2>end1 && end1>index2) || (end2<index1 && end1>index2)){
                            //(end1-index1)%n > (index2-index1)%n && (end2-index1)%n<0 && (end1-index1)%n<(end1-index1)%n){
                        System.out.println("        crossing: "+trans.getSourceState()+" "+c+" -- "+trans.getTargetState()+" "+d);
                        cross++;
                    } 
                }
            }
            
        }
        return cross;
    }
    public int crossingsWithPhase(State state,boolean phase,List<State> expandList,boolean globalcircular){
        
        int cross=0;
        
        int index=expandList.indexOf(state);
        int end;    
        List<Transition> transOfState=state.getTransitions();
        
        List<State> phaseList=(phase)?upPhase:downPhase;
        
        for(Transition trans:transOfState){
            
            
            int a=phaseList.indexOf(trans.getSourceState());
            int b=phaseList.indexOf(trans.getTargetState());
            
            if(a==-1 || b==-1) continue;
            
            System.out.println("    crossing with phase: "+trans.getSourceState()+" "+a+" -- "+trans.getTargetState()+" "+b);
            
            if(a>b){
                int tmp=a;a=b;b=tmp;
            }
            
            if(b-a==1) continue;
            
            end=(index==a)?b:a;
            
            // skip inGroup edges
            if(inSomeVertexGroup(phaseList.get(end))>=0){ 
                System.out.println("        Skip end in group!");
                continue;
            }
            
            
            
            // for states within a to b
            // if there's transitions with terminal outside a,b, there's a crossing
            for(int i=a+1;i<b;++i){
                System.out.println("        check: "+i);
            
                State stateIn=phaseList.get(i);
                List<Transition> trIn=stateIn.getIncomingTransitions();
                for(Transition tr:trIn){
                    
                    int c=phaseList.indexOf(tr.getSourceState());
                    if(c<0) continue;
                    if(c<a || c>b){
                        cross++;
                        System.out.println("             "+tr.getSourceState()+" "+c+"->"+stateIn+" "+i);
                    }
                    
                }
            
                List<Transition> trOut=stateIn.getOutgoingTransitions();
                for(Transition tr:trOut){
                    int c=expandList.indexOf(tr.getTargetState());
                    if(c<0) continue;
                    
                    if(c<a || c>b){
                            cross++;
                            System.out.println("             "+tr.getTargetState()+" "+c+"->"+stateIn+" "+i);
                           
                    }
                    
                }
            }
        
        }
        return cross;
    }
    
    public List<Transition> removeInnerTransitions(){
        System.out.println("remove Inner Transitions: ");
        List<Transition> originTransitions=originAutomata.getAllTransitions();
        ArrayList<Transition> removedTransitions=new ArrayList<Transition>();
        for(Transition tr:originTransitions){
            State source=tr.getSourceState();
            State target=tr.getTargetState();
            if(inSomeVertexGroup(source)>=0 && inSomeVertexGroup(target)>=0){
                removedTransitions.add(tr);
//                originTransitions.remove(tr);
//                source.getOutgoingTransitions().remove(tr);
//                target.getIncomingTransitions().remove(tr);
                source.removeTransition(tr);
                target.removeTransition(tr);
                System.out.println("    "+source.getName()+" -> "+target.getName());
            }
            
        }
        for(Transition tr:removedTransitions) originTransitions.remove(tr);
        
        return removedTransitions;
        
    }
    public void addInnerTransitions(){
        
        
    }

    public List<State> expandStatesToCircularLayout(List<State> vertexOrderList,Point2D center) {
        
        
        expandStatesPositionToVertexGroups();
        
        
        ArrayList<State> expandList=new ArrayList<State>();
        final Double centerx=center.getX();
        final Double centery=center.getY();
        
        int len=vertexOrderList.size();
        for(int i=0;i<len;++i){
            
            State stateInList=vertexOrderList.get(i);
            
            // if is a state replaced for some vertexGroup
            // add states in vertexGroup by order of x position
            int index=replaceStatesForGroups.indexOf(stateInList);
            if(index>=0){
                VertexGroup vg=groupList.get(index);
                
                List<State> statesInVG=vg.getStateList();
//                    final Double centerx=stateInList.getGeometricData().getX();
//                    final Double centery=stateInList.getGeometricData().getY();
                    
                    
                    // sort the states in groups by orientation
                    Collections.sort(statesInVG, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=Math.atan2(s1.getGeometricData().getY()-centery, s1.getGeometricData().getX()-centerx);
                            Double position2=Math.atan2(s2.getGeometricData().getY()-centery, s2.getGeometricData().getX()-centerx);
                            
//                            position1=-position1+Math.PI/2;
//                            if(position1<0) position1+=2*Math.PI;
//                            
//                            position2=-position2+Math.PI/2;
//                            if(position2<0) position2+=2*Math.PI;
                            
                            return position1.compareTo(position2);
                        }
                            
                    });
                    
                
                
                System.out.println("state in group: "+statesInVG);
        
                for(State s:statesInVG) expandList.add(s);
                
            }else{
                expandList.add(getOriginState(stateInList));
            }
            
        }
        Collections.sort(expandList, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=Math.atan2(s1.getGeometricData().getY()-centery, s1.getGeometricData().getX()-centerx);
                            Double position2=Math.atan2(s2.getGeometricData().getY()-centery, s2.getGeometricData().getX()-centerx);
                            
//                            position1=-position1+Math.PI/2;
//                            if(position1<0) position1+=2*Math.PI;
//                            
//                            position2=-position2+Math.PI/2;
//                            if(position2<0) position2+=2*Math.PI;
                            
                            return position1.compareTo(position2);
                        }
                            
        });
        
        System.out.println("expand circular list: "+expandList);
                // update locations of all states to the new locations
        // sifting states to get less crossings
        for(VertexGroup g:groupList){
//            if(g.getLayoutType()==VertexGroup.LINEAR_GROUP){
//                siftStatesOrderInVertexGroup(g,expandList,false);
//                // reroute edges
//                LinearLayoutAutomata linearlayout=new LinearLayoutAutomata();
//                linearlayout.adjustEdgeCurves(originAutomata,g.getStateList());
//            }
//            else{
                final double gcentery=replaceStatesForGroups.get(groupList.indexOf(g)).getGeometricData().getY();
                final double gcenterx=replaceStatesForGroups.get(groupList.indexOf(g)).getGeometricData().getX();
                double Radius=center.distance(gcenterx, gcentery);
                
                // set up two states orderings
                upPhase=new ArrayList<State>();
                downPhase=new ArrayList<State>();
                int num=expandList.size();
                for(int i=0;i<num;++i){
                    
                    State s=expandList.get(i);
                    if(inSomeVertexGroup(s)>-1){
                        double x=s.getGeometricData().getX();
                        double y=s.getGeometricData().getY();

                        double orientation=Math.atan2(y-center.getY(), x-center.getX());
                        double radius=center.distance(x, y);

                        if(radius>Radius) upPhase.add(s);
                        else if(radius<=Radius) downPhase.add(s);
                    }else{
                        upPhase.add(s);
                        downPhase.add(s);
                    }
                }
//                Collections.sort(downPhase, new Comparator<State>(){
//                        @Override
//                        public int compare(State s1, State s2) {
//                            Double position1=Math.atan2(s1.getGeometricData().getY()-gcentery, s1.getGeometricData().getX()-gcenterx);
//                            Double position2=Math.atan2(s2.getGeometricData().getY()-gcentery, s2.getGeometricData().getX()-gcenterx);
//                            
//                            position1=-position1+Math.PI/2;
//                            if(position1<0) position1+=2*Math.PI;
//                            
//                            position2=-position2+Math.PI/2;
//                            if(position2<0) position2+=2*Math.PI;
//                            
//                            return -position1.compareTo(position2);
//                        }
//                            
//                    });
                System.out.println("upPhase: "+upPhase);
                System.out.println("downPhase: "+downPhase);
                
                siftStatesOrderInVertexGroup(g,expandList,true,true);
                //reroute edges
                if(g.getLayoutType()==VertexGroup.CIRCULAR_GROUP){
                    
                    System.out.println("reroute circular layout");
                    CircularLayoutAutomata circularlayout=new CircularLayoutAutomata();
                    circularlayout.setRouteTwoPhase(false);
                    circularlayout.adjustEdgeCurves(originAutomata, g.getStateList());
                }else if(g.getLayoutType()==VertexGroup.LINEAR_GROUP){
                    
                    Collections.sort(g.getStateList(), new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=s1.getGeometricData().getX();
                            Double position2=s2.getGeometricData().getX();
                          
                            return position1.compareTo(position2);
                        }
                            
                    });
                    
                    System.out.println("reroute linear layout");
                    LinearLayoutAutomata linearlayout=new LinearLayoutAutomata();
                    linearlayout.adjustEdgeCurves(originAutomata,g.getStateList());
                }
//            
        }
        System.out.println("after adjust circular list: "+expandList);
        

        
//        List<Transition> removedTransitions=removeInnerTransitions();
        
        
        return expandList;
    }
}
