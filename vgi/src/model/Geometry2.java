package model;


import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Vector;
import debug.Debug;

/**
 * defines data structure related to geometric properties of automata
 * provides interfaces used for controlling several geometric aspects of automata
 * @author polar
 */
public class Geometry2 {

    private Fsmxml fsmxml;
    
    public Geometry2() {
        this.fsmxml = new Fsmxml();
    }

    public Geometry2(Fsmxml fsmxml) {
        this.fsmxml = fsmxml;
    }
    
    public void addState(String stateId  ){
        addState(0, stateId , new  StatesGeometricData ());
    }
    
    public void addState(int automatonID ,String stateId ){
        addState( automatonID , stateId , new  StatesGeometricData ());
    }
    
    public void addState(String stateId , String name , StatesGeometricData statesGeometricData){
        addState(0, stateId ,  statesGeometricData );
    }
    
    public void addState(int automatonID ,String stateId , StatesGeometricData statesGeometricData){
        State state = new State(stateId ,  statesGeometricData);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH.put(stateId ,state);
    }    
    
    public void deleteState(String stateId){
        deleteState(0 , stateId);
    }
    
    public void deleteState(int automatonID , String stateId){
        Hashtable hashTemp = this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH;
        hashTemp.remove(hashTemp.get(stateId));        
    }
          
    public void setAutomatonPosition( Point2D.Float p){
        setAutomatonPosition( 0 , p);        
    }
    
    public void setAutomatonPositionX( float x){
        setAutomatonPositionX( 0 , x);        
    }
    
    public void setAutomatonPositionY( float y){
        setAutomatonPositionY( 0 , y);        
    }
    
    public void setAutomatonPosition(int automataId , Point2D.Float p){
        setAutomatonPositionX(automataId , (float)p.getX());
        setAutomatonPositionY(automataId , (float)p.getY());
    }
    
    public void setAutomatonPositionX(int automatonId , float x){
        ((AutomatonGeometricData)(this.fsmxml.automatonV.get(automatonId).geometricData)).x = x;
    }
    
    public void setAutomatonPositionY(int automatonId , float y){
        ((AutomatonGeometricData)(this.fsmxml.automatonV.get(automatonId).geometricData)).y = y;
    }        
    
    public Point2D.Float getAutomatonPosition(){        
        return getAutomatonPosition(0);
    }
    
    public float getAutomatonPositionX(){
        return this.getAutomatonPositionX(0);
    }
    
    public float getAutomatonPositionY(){
        return this.getAutomatonPositionY(0) ;
    }
    
    public Point2D.Float getAutomatonPosition(int automataId){
        float x = getAutomatonPositionX(automataId);
        float y = getAutomatonPositionY(automataId);
        return(new Point2D.Float(x,y));
    }
    
    public float getAutomatonPositionX(int automataId){
        return ((AutomatonGeometricData)(this.fsmxml.automatonV.get(automataId).geometricData)).x;
    }
    
    public float getAutomatonPositionY(int automataId){
        return ((AutomatonGeometricData)(this.fsmxml.automatonV.get(automataId).geometricData)).y;
    }
        
    public void setStatePosition(String stateId , Point2D.Float p){
        setStatePosition(0 ,stateId , p);        
    }
    
    public void setStatePositionX(String stateId ,float x){
        this.setStatePositionY(0 , stateId , x);
    }
    
    public void setStatePositionY(String stateId ,float y){
        this.setStatePositionY(0 , stateId , y);
    }
    
    public void setStatePosition(int automatonId , String stateId , Point2D.Float p){
        setStatePositionX(automatonId , stateId ,(float)p.getX());
        setStatePositionY(automatonId , stateId ,(float)p.getY());
    }
    
    public void setStatePositionX(int automatonId , String stateId , float x){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.states.stateH.get(stateId).statesGeometricData.x = x;
    }
    
    public void setStatePositionY(int automatonId , String stateId , float y){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.states.stateH.get(stateId).statesGeometricData.y = y;
    }
    
    public Point2D.Float getStatePosition(String stateId){        
        return getStatePosition(0, stateId);
    }
    
    public float getStatePositionX(String stateId){
        return getStatePositionX(0, stateId);
    }
    
    public float getStatePositionY(String stateId){
        return getStatePositionY(0, stateId);
    }
           
    public Point2D.Float getStatePosition(int automatonId , String stateId){
        float x = getStatePositionX( automatonId , stateId);
        float y = getStatePositionY( automatonId , stateId);
        return (new Point2D.Float(x,y));               
    }
    
    public float getStatePositionX(int automatonId , String stateId ){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.states.stateH.get(stateId).statesGeometricData.x;
    }
    
    public float getStatePositionY(int automatonId , String stateId ){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.states.stateH.get(stateId).statesGeometricData.y;
    }
    
    public void setInitialState(String stateId){
        setInitialState(0 , stateId);
    }
    
    public void setInitialState(int automatonID , String stateId){
        Initial initial = new Initial(stateId , new InitialStatesGeometricData());
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH.put(stateId,initial);
    }
    
    public void removeInitialState(String initialId){
        removeInitialState(0 , initialId);
    }
    
    public void removeInitialState(int automatonID , String initialId){        
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH.remove(initialId);
    }
    
    public void setFinalState(String stateId){
        setFinalState(0 , stateId );
    }
    
    public void setFinalState(int automatonID , String stateId ){
        Final final_ = new Final(stateId  , new FinalStatesGeometricData());
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH.put(stateId, final_);
    }
    
    public void removeFinalState(String finalId){
        removeInitialState(0 , finalId);
    }
    
    public void removeFinalState(int automatonID , String finalId){        
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH.remove(finalId);
    }
    
    public void addTransition(String transitionId ){
        addTransition(0 , transitionId );
    }
    
    public void addTransition(int automatonID , String transitionId ){
        addTransition(automatonID , transitionId , new TransitionsGeometricData());
    }
    
    public void addTransition(String transitionId ,TransitionsGeometricData transitionsGeometricData){
        addTransition(0 , transitionId ,transitionsGeometricData);
    }
    
    public void addTransition(int automatonID , String transitionId , TransitionsGeometricData transitionsGeometricData){
        Transition transition = new Transition(transitionId , transitionsGeometricData);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.addTransition(transition);  
    }
    
    public void deleteTransition(String transitionId){
        deleteTransition(0 , transitionId);
    }
    
    public void deleteTransition(int automatonID , String transitionId){
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.removeTrasition(transitionId);
    }
    
    public void setTransitionType(String transitionId ,TransitionsGeometricData.TransitionTypeE type){
        setTransitionType(0, transitionId , type);
    }
    
    public void setTransitionType(int automatonId , String transitionId ,TransitionsGeometricData.TransitionTypeE type){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.transitionTypeE = type;
    }
    
    public TransitionsGeometricData.TransitionTypeE getTransitionType(String transitionId){
        return getTransitionType(0 , transitionId);
    }
    
    public TransitionsGeometricData.TransitionTypeE getTransitionType(int automatonId , String transitionId){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.transitionTypeE;
    }
    
    public void setTransitionLoopDir(String transitionId , int loopDir){
        setTransitionLoopDir( 0 , transitionId , loopDir);
    }
    
    public void setTransitionLoopDir(int automatonId , String transitionId , int loopDir){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.loopDir = loopDir;
    }
    
    public int getTransitionLoopDir( String transitionId ){
        return getTransitionLoopDir(0, transitionId );
    }
    
    public int getTransitionLoopDir(int automatonId , String transitionId ){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.loopDir;
    }
    
    public void setTransitionLabelPos(String transitionId , int labelPos){
        setTransitionLabelPos( 0 , transitionId , labelPos);
    }
            
    public void setTransitionLabelPos(int automatonId , String transitionId , int labelPos){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.labelPos = labelPos;
    }
    
    public float getTransitionLabelPos(String transitionId){
        return getTransitionLabelPos(0 , transitionId);
    }
    
    public float getTransitionLabelPos(int automatonId , String transitionId){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.labelPos;
    }
    
    public void setTransitionLabelDist( String transitionId , float labelDist){
        setTransitionLabelDist(0,  transitionId ,  labelDist);
    }
    
    public void setTransitionLabelDist(int automatonId , String transitionId , float labelDist){
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.labelDist = labelDist;
    }
    
    public float getTransitionLabelDist( String transitionId){
        return getTransitionLabelDist(0, transitionId);
    }
    
    public float getTransitionLabelDist(int automatonId , String transitionId){
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).transitionsGeometricData.labelDist;
    }    
    
    public static class Fsmxml {
        
        private Vector<Automaton> automatonV;
        
        public Fsmxml(Vector<Automaton> automatonV) {
            this.automatonV = automatonV;
        }       
        
        public Fsmxml(){
            this.automatonV = new Vector<Automaton>(); 
            this.automatonV.add( new Automaton());
        }

        public void setAutomaton(Vector<Automaton> automatonV) {
            this.automatonV = automatonV;
        }
        

        public Vector<Automaton> getAutomaton() {
            return automatonV;
        }        
        
    }
    
    public static class Automaton {
        
        private GeometricData geometricData;
        
        private AutomatonStruct automatonStruct;

        public Automaton(AutomatonStruct automatonStruct) {            
            this.automatonStruct = automatonStruct;
        }
        
        public Automaton(){
            this.automatonStruct = new AutomatonStruct();
        }

        public AutomatonStruct getAutomatonStruct() {
            return automatonStruct;
        }

        public void setAutomatonStruct(AutomatonStruct automatonStruct) {
            this.automatonStruct = automatonStruct;
        }

    }
    
    public static class AutomatonStruct{
        private States states;
        private Transitions transitions;

        public AutomatonStruct(States states, Transitions transitions) {
            this.states = states;
            this.transitions = transitions;
        }
        
        public AutomatonStruct(){
            states = new States();
            transitions = new Transitions();
        }

        public States getStates() {
            return states;
        }

        public void setStates(States states) {
            this.states = states;
        }

        public Transitions getTransitions() {
            return transitions;
        }

        public void setTransitions(Transitions transitions) {
            this.transitions = transitions;
        }               
                
    }
    
    public static class States{
        //private Vector<State> stateV;
        private Hashtable<String,State> stateH = new Hashtable();
                
        public Hashtable getStateH() {
            return stateH;
        }
        
        public State getStateH(int stateId){
            return (State) stateH.get(stateId);
        }        
        
        public void setStateH(Hashtable stateH) {
            this.stateH = stateH;
        }
                
        public void addStateH(String stateId ,State state){
            this.stateH.put(stateId, state);                        
        }               
        
        public void removeStateH(State state){
            this.stateH.remove(state);
        }
    }
    
    public static class State{
        private String id;
        private StatesGeometricData statesGeometricData;
        
        public State(String id , StatesGeometricData statesGeometricData) {
            this.id = id;            
            this.statesGeometricData = statesGeometricData;
        }                
            
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public StatesGeometricData getStatesGeometricData() {
            return statesGeometricData;
        }

        public void setStatesGeometricData(StatesGeometricData statesGeometricData) {
            this.statesGeometricData = statesGeometricData;
        }
        
        
                
    }
    
    public static class Transitions{
        //private Vector<Transition> transitionV;
        private Hashtable<String , Transition> transitionH = new Hashtable();    
        //private Vector<Initial> initialV;
        private Hashtable<String , Initial> initialH = new Hashtable();
        //private Vector<Final> finalV;
        private Hashtable<String , Final> finalH = new Hashtable();

        public Hashtable<String ,Final> getFinalH() {
            return finalH;
        }

        public void setFinalH(Hashtable<String ,Final> finalH) {
            this.finalH = finalH;
        }
        
        public void addFinalH(Final final_){
            this.finalH.put( final_.state , final_);
        }

        public Hashtable<String ,Initial> getInitialH() {
            return initialH;
        }

        public void setInitialH(Hashtable<String ,Initial> initialH) {
            this.initialH = initialH;
        }
        
        public void addInitialH(Initial initial){
            this.initialH.put(initial.state , initial);
        }

        public Hashtable<String , Transition> getTransitionH() {
            return transitionH;
        }

        public void setTransitionH(Hashtable<String , Transition>transitionH) {
            this.transitionH = transitionH;
        }
        
        public void addTransition( Transition transition) {
            this.transitionH.put(transition.id , transition);                        
        }
        
        public void removeTrasition(String transitionId){
            this.transitionH.remove(transitionId);
        }        
    }
    
    public static class Transition{                
        
        private String id;
        private TransitionsGeometricData transitionsGeometricData;

        public Transition(String id, TransitionsGeometricData transitionsGeometricData) {
            this.id = id;
            this.transitionsGeometricData = transitionsGeometricData;
        }                

        public TransitionsGeometricData gettransitionsGeometricData() {
            return transitionsGeometricData;
        }

        public void settransitionsGeometricData(TransitionsGeometricData transitionsGeometricData) {
            this.transitionsGeometricData = transitionsGeometricData;
        }
        
    }
 
    public static class InitialAndFinal{
        protected String state;
        protected String id;
        
        //protected Label label;
        protected GeometricData geometricData;

        public InitialAndFinal(String state ,GeometricData geometricData) {
            this.state = state;            
            this.geometricData = geometricData;
        }                

        public GeometricData getGeometricData() {
            return geometricData;
        }

        public void setGeometricData(GeometricData geometricData) {
            this.geometricData = geometricData;
        }
        
    }
    
    public static class Initial extends InitialAndFinal{

        public Initial(String state , InitialStatesGeometricData geometricData) {
            super(state ,geometricData);
        }
              
    }
    
    public static class Final extends InitialAndFinal{

        public Final(String state ,FinalStatesGeometricData geometricData) {
            super(state ,geometricData);
        }
    }
    
    public static class Label{
      
    }
                        
    public static class GeometricData{
        
    }
    
    public static class AutomatonGeometricData extends GeometricData{
        private float x;
        private float y;

        public AutomatonGeometricData(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
        
        
    }
    
    public static class StatesGeometricData extends GeometricData{
        private float x;
        private float y;

        public StatesGeometricData(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public StatesGeometricData() {
            this.x = 0; // FIX ME::
            this.y = 0;// FIX ME::
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
        
    }
    
    public static class TransitionsGeometricData extends GeometricData{
        public enum TransitionTypeE{EDGEL,EDGER,ARCL,ARCR,LOOP};
        private TransitionTypeE transitionTypeE;
        private int loopDir;
        private float labelPos;
        private float labelDist;

        public TransitionsGeometricData(TransitionTypeE transitionTypeE, int loopDir, float labelPos, float labelDist) {
            this.transitionTypeE = transitionTypeE;
            setLoopDir(loopDir);
            setLabelPos(labelPos);
            setLabelDist(labelDist);
        }

        public TransitionsGeometricData(){
            this.transitionTypeE = TransitionTypeE.ARCL;
            setLoopDir(90); //FIX ME:::
            setLabelPos(50);//FIX ME:::
            setLabelDist(2);//FIX ME:::
        }
        
        public float getLabelDist() {
            return labelDist;
        }

        public void setLabelDist(float labelDist) {
            if(labelDist >= 0)
                this.labelDist = labelDist;
            else
                Debug.echo("File Structure Error!!!!  LabelDist < 0 ");
            
        }

        public float getLabelPos() {
            return labelPos;
        }

        public void setLabelPos(float labelPos) {
            if(labelPos >=0 && labelPos <=100)
                this.labelPos = labelPos;
            else if(labelPos < 0 ){
                this.labelPos = 0;
                Debug.echo("File Structure Error!!!!  LabelPos < 0 ");
            }else{
                this.labelPos = 100;
                Debug.echo("File Structure Error!!!!  LoopPos > 100 ");
            }
        }

        public int getLoopDir() {
            return loopDir;
        }

        public void setLoopDir(int loopDir) {
            if(loopDir >= 0 && loopDir <=360)
                this.loopDir = loopDir;
            else if(loopDir <0){
                this.loopDir = 0;
                Debug.echo("File Structure Error!!!!  LoopDir < 0 ");
            }else{
                this.loopDir = 360;
                Debug.echo("File Structure Error!!!!  LoopDir > 360 ");
            }
        }

        public TransitionTypeE getTransitionTypeE() {
            return transitionTypeE;
        }

        public void setTransitionTypeE(TransitionTypeE transitionTypeE) {
            this.transitionTypeE = transitionTypeE;
        }
        
        
        
    }
    
    public static class InitialStatesGeometricData extends GeometricData{
        private int initialDir;
        private float labelPos;
        private float labelDist;

        public InitialStatesGeometricData(int initialDir, float labelPos, float labelDist) {
            setInitialDir(initialDir);
            setLabelPos(labelPos);
            setLabelDist(labelDist);
        }
        
        public InitialStatesGeometricData(){
            setInitialDir(180);//FIX ME::
            setLabelPos(50);//FIX ME::
            setLabelDist(2);//FIX ME::
        }

        public int getInitialDir() {
            return initialDir;
        }

        public void setInitialDir(int initialDir) {
            if(initialDir >=0 && initialDir <=360)
                this.initialDir = initialDir;
            else if(initialDir <0){
                Debug.echo("File Structure Error!!! initialDir <0 ");
                this.initialDir = 0;
            }else{
                Debug.echo("File Structure Error!!! initialDir > 360 ");
                this.initialDir = 360;
            }
                
        }

        public float getLabelDist() {
            return labelDist;
        }

        public void setLabelDist(float labelDist) {            
            
            if(labelDist >=0)
                this.labelDist = labelDist;
            else{
                Debug.echo("File Structure Error!!! labelDist <0 ");
                this.labelDist = 0;
            }
                
        }

        public float getLabelPos() {
            return labelPos;
        }

        public void setLabelPos(float labelPos) {
            this.labelPos = labelPos;
            
            if(labelPos >=0 && labelPos <=100)
                this.labelPos = labelPos;
            else if(labelPos <0){
                Debug.echo("File Structure Error!!! labelPos <0 ");
                this.labelPos = 0;
            }else{
                Debug.echo("File Structure Error!!! labelPos > 100 ");
                this.labelPos = 100;
            }
        }
        
        
        
    }
    
    public static class FinalStatesGeometricData extends GeometricData{
        private int finalDir;
        public enum FinalModeE{CIRCLE,ARROW};
        private FinalModeE finalModeE;
        private float labelPos;
        private float labelDist;

        public FinalStatesGeometricData(int finalDir, FinalModeE finalModeE , float labelPos, float labelDist) {
            setFinalDir(finalDir);
            this.finalModeE = finalModeE;
            setLabelPos(labelPos);
            setLabelDist(labelDist);
        }
        
        public FinalStatesGeometricData(){
            setFinalDir(180);//FIX ME:::
            this.finalModeE = FinalModeE.CIRCLE;//FIX ME:::
            setLabelPos(50);//FIX ME:::
            setLabelDist(2);//FIX ME:::
        }

        public int getFinalDir() {
            return finalDir;
        }

        public void setFinalDir(int finalDir) {
            if(finalDir >=0 && finalDir <=360)
                this.finalDir = finalDir;
            else if(finalDir <0){
                Debug.echo("File Structure Error!!! finalDir <0 ");
                this.finalDir = 0;
            }else{
                Debug.echo("File Structure Error!!! finalDir > 360 ");
                this.finalDir = 360;
            }
                
        }

        public float getLabelDist() {
            return labelDist;
        }

        public void setLabelDist(float labelDist) {            
            
            if(labelDist >=0)
                this.labelDist = labelDist;
            else{
                Debug.echo("File Structure Error!!! labelDist <0 ");
                this.labelDist = 0;
            }
                
        }

        public float getLabelPos() {
            return labelPos;
        }

        public void setLabelPos(float labelPos) {
            this.labelPos = labelPos;
            
            if(labelPos >=0 && labelPos <=100)
                this.labelPos = labelPos;
            else if(labelPos <0){
                Debug.echo("File Structure Error!!! labelPos <0 ");
                this.labelPos = 0;
            }else{
                Debug.echo("File Structure Error!!! labelPos > 100 ");
                this.labelPos = 100;
            }
        }
    }
}
