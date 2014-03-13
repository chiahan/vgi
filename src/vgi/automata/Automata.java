package vgi.automata;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import com.mxgraph.model.mxCell;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;
import vgi.display.JgraphAutomata;
import vgi.display.JgraphXInternalFrame;
import vgi.fsmxml.TAFKitInterface;
import vgi.geometrictools.Projection;
import vgi.geometrictools.Vector2D;
import vgi.layout.helperclass.GroupEdge;
import vgi.layout.helperclass.GroupReplacedAutomata;
import vgi.layout.helperclass.VertexGroup;
import vgi.layout.linear.LinearLayoutAutomata;

public class Automata implements AutomataInterface {

	@Override
	public String getName() {
		return this.pmName;
	}

	@Override
	public void setName(String name) {
		this.pmName = name;
	}

	/**
	 * @return the Type
	 */
	@Override
	public WritingData getWritingData() {
		return this.pmWritingData;
	}

	@Override
	public void setWritingData(WritingData writingData) {
		this.pmWritingData = writingData;
	}

	@Override
	public Weight getWeight() {
		return this.pmWeight;
	}

	@Override
	public void setWeight(Weight weight) {
		this.pmWeight = weight;
	}

	@Override
	public Alphabet getAlphabet() {
		return this.pmAlphabet;
	}

	@Override
	public void setAlphabet(Alphabet alphabet) {
		this.pmAlphabet = alphabet;
	}

	@Override
	public Alphabet getOutputAlphabet() {
		return this.pmOutputAlphabet;
	}

	@Override
	public void setOutputAlphabet(Alphabet alphabet) {
		this.pmOutputAlphabet = alphabet;
	}

	/**
	 * @return the states
	 */
	@Override
	public List<State> getAllStates() {
		return this.pmAllStates;
	}

	/**
	 * @param states the states to set
	 */
	@Override
	public void setAllStates(List<State> allStates) {
		this.pmAllStates = allStates;
	}

	@Override
	public void addState(State state) { // private
		pmAllStates.add(state);
		String name = state.getName();
		if (name == null) {
			state.setName("s" + Integer.toString(counter));
		}
                counter++;
               createJgraphState(state);
               
	}   
       private State addState(String name,StateGeometricData geoData,StateDrawingData drawData){ // private
		State state=new State();
                state.setGeometricData(geoData);
                state.setDrawingData(drawData);
            
                pmAllStates.add(state);
		//String name = state.getName();
		if (name == null) {
			state.setName("s" + Integer.toString(counter));
		}else
                    state.setName(name);
                counter++;
                
                createJgraphState(state);
                
                
                
                return state;
                
	}
        public State addState(){
            // TODO 
            State state=null;
            
            return state;
        }
        public State addState(Point2D location){
            
            System.out.println("add state in automata: "+location);
            //State state=new State();
            StateGeometricData geoData=new StateGeometricData();
            //geoData.setLocation(location);
            geoData.setX(location.getX());
            geoData.setY(location.getY());
            
            StateDrawingData drawData=new StateDrawingData();
            
            State state=addState(null,geoData,drawData);
            
            return state;
        }
        private void createJgraphState(State state){
            mxCell cell=jgraphAutomata.createVertex(state);
            cellTable.put(state, cell);
        }
        
	/**
	 * @return the transitions
	 */
	@Override
	public List<Transition> getAllTransitions() {
		return this.pmAllTransitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	@Override
	public void setAllTransitions(List<Transition> transitions) {
		this.pmAllTransitions = transitions;
	}

	@Override
	public void addTransition(Transition transition) {
		pmAllTransitions.add(transition);
                
                State source=transition.getSourceState();
                State target=transition.getTargetState();
                source.addTransition(transition);
                if(!target.equals(source)) target.addTransition(transition);
        
                //addTransition(transition.getSourceState(),transition.getTargetState(),transition.getGeometricData(),transition.getDrawingData());
                createJgraphTransition(transition);
                
                System.out.println("add transition: "+transition.getSourceState().getName()+"->"+transition.getTargetState().getName());
               
	}
        //
        // TODO: for initial/final in DisplayUtil
        //
        public void addTransition(Transition transition, mxCell edge){
            pmAllTransitions.add(transition);
            cellTable.put(transition, edge);
        }
        private Transition addTransition(State source,State target,TransitionGeometricData geo,TransitionDrawingData draw){
            
            Transition transition=new Transition();
            transition.setSourceState(source);
            transition.setTargetState(target);
            transition.setDrawingData(draw);
            transition.setGeometricData(geo);
            
            
            WeightedRegularExpression.Atomic expression = WeightedRegularExpression.Atomic.createAtomic(this);
            expression.setSymbol(expression.getAlphabet().allSymbols.get(0));
            transition.setLabel(expression);
        
            pmAllTransitions.add(transition);
		
            source.addTransition(transition);
            if(!target.equals(source)) target.addTransition(transition);
            
            createJgraphTransition(transition);
            
            return transition;
        }
        public Transition addTransition(State source,State target){
            
            return addTransition(source,target,new TransitionGeometricData(),new TransitionDrawingData());
            
        }
        private void createJgraphTransition(Transition transition){
            mxCell edge=jgraphAutomata.createEdge(transition);
            cellTable.put(transition, edge);
            
        }
	@Override
	public List<State> getInitialStates() {
		ArrayList<State> arrayList = new ArrayList<State>();
		if (this.pmAllStates == null) {
			return arrayList;
		}
		Iterator<State> iterateStates = this.pmAllStates.iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (state.getInitial() != null) {
				arrayList.add(state);
			}
		}  // End while (iterateStates.hasNext())
		return arrayList;
	}

	@Override
	public List<State> getFinalStates() {
		ArrayList<State> arrayList = new ArrayList<State>();
		if (this.pmAllStates == null) {
			return arrayList;
		}
		Iterator<State> iterateStates = this.pmAllStates.iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (state.getFinal() != null) {
				arrayList.add(state);
			}
		}  // End while (iterateStates.hasNext())
		return arrayList;
	}
        
        /*
           method about celltable
         */
        public mxCell stateToCell(State state){
            return cellTable.get(state);
        }
        public mxCell transitionToCell(Transition transition) {
            
            return cellTable.get(transition);
        }
        public mxCell objToCell(Object obj) {
            
            return cellTable.get(obj);
        }
        public void addToCellTable(Object obj, mxCell cell) {
            cellTable.put(obj, cell);
        }
        public Object cellToObj(mxCell cell){
            Enumeration em=cellTable.keys();
            while(em.hasMoreElements()){
                Object key=em.nextElement();
                mxCell nextCell=cellTable.get(key);
                if(nextCell.equals(cell)){
                    return key;
                }
            }
            System.out.println("cellToState: not found!"+cell.getValue()+" id="+cell.getId());  
            return null;          
        }
        /*
         * return state or transition or null
         */
        public Object cellToState(mxCell cell){
            return cellToObj(cell);
        }
        
        
	private String pmName;
	private WritingData pmWritingData;
	private Weight pmWeight;
	private Alphabet pmAlphabet;
	private Alphabet pmOutputAlphabet;
	private List<State> pmAllStates;
	private List<Transition> pmAllTransitions;
	private int counter;
        // a global drawing date for all states in the automata
        private StateDrawingData pmStateDrawingData;
        private StateGeometricData pmStateGeometricData;
        private TransitionDrawingData pmTransitionDrawingData;
        
        private IniFinGeometricData pmInitialGeometricData;
        private IniFinGeometricData pmFinalGeometricData;
        private TransitionDrawingData pmInitialDrawingData;
        private TransitionDrawingData pmFinalDrawingData;
        
        
        private final JgraphXInternalFrame jgraphXInternalFrame;
        
        public JgraphAutomata jgraphAutomata;
        protected Hashtable<Object, mxCell> cellTable;
        
        Projection projection;
        
        List<State> selectedStates;
        
        List<VertexGroup> groupList;
        
        //selected objects and grouping 
        List<Object> selectedObjs;
        Group group;
        
	public Automata() {
		this.pmName = null;
		this.pmWritingData = null;
		this.pmWeight = null;
		this.pmAlphabet = null;
		this.pmOutputAlphabet = null;
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
		this.counter = 0;
                
                this.pmStateDrawingData=new StateDrawingData();
                this.pmStateGeometricData=new StateGeometricData();
                this.pmTransitionDrawingData=new TransitionDrawingData();
                this.pmInitialDrawingData=new TransitionDrawingData();
                this.pmFinalDrawingData=new TransitionDrawingData();
                this.pmInitialGeometricData=new IniFinGeometricData();
                this.pmFinalGeometricData=new IniFinGeometricData();
                
                
                group = new Group();
                selectedObjs = null;
                        
                jgraphXInternalFrame=null;
                jgraphAutomata=new JgraphAutomata(this);
                
                cellTable=new Hashtable<Object,mxCell>();
                
                projection=new Projection();
                Rectangle rec=jgraphAutomata.graphComponent.getBounds();
//                System.out.println("jgraph bound"+rec+"center"+rec.getCenterX()+" "+rec.getCenterY());
                projection.setCenter(new Point2D.Double(rec.getCenterX(),rec.getCenterY()));
                
	}

	public Automata(Weight weight) {
		this();
		this.pmWeight = weight;
	}

	public static void main(String args[]) {
		System.out.println("Staring from 'Automata Class'");
		System.out.println("Creating Weight");
		Weight weight = new Weight();
		System.out.println("Creating empty Automata");
		Automata automata = new Automata();
		System.out.println("Creating Automata with Weight");
		Automata automataWithWeight = new Automata(weight);
	}

	public static Automata mergeSimilarTransitions(Automata automata, AutomataHistory optionalHistory) {

		Automata outputAutomaton = new Automata();
		outputAutomaton.setName(automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();
		if (optionalHistory != null) {
			optionalHistory.newToOldStatesMap = new HashMap<State, List<State>>();
			optionalHistory.oldToNewStatesMap = mapOldToNewStates;
			optionalHistory.newToOldTransitionsMap = new HashMap<Transition, List<Transition>>();
			optionalHistory.oldToNewTransitionsMap = new HashMap<Transition, List<Transition>>();
		}  // End if (optionalHistory != null)

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			State newState = new State();
			newState.setName(state.getName());
//			newState.setInitialWeight(state.getInitialWeight());
//			newState.setFinalWeight(state.getFinalWeight());
			automata.setInitialWeight(newState,state.getInitial().getWeight());
			automata.setFinalWeight(newState,state.getFinal().getWeight());
			
                        newState.setGeometricData(state.getGeometricData());
			outputAutomaton.addState(newState);
			ArrayList<State> arrayList = new ArrayList<State>();
			arrayList.add(newState);
			mapOldToNewStates.put(state, arrayList);
			arrayList = null;  // ArrayList<State> arrayList = new ArrayList<State>();
			if (optionalHistory != null) {
				arrayList = new ArrayList<State>();
				arrayList.add(state);
				optionalHistory.newToOldStatesMap.put(newState, arrayList);
				arrayList = null;  // arrayList = new ArrayList<State>();
			}  // End if (optionalHistory != null)
			newState = null;  // State newState = new State();
		}  // End while (iterateStates.hasNext())

		iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {

			State state = iterateStates.next();
			List<Transition> loopAndOutgoingTransitions = new ArrayList<Transition>();
			loopAndOutgoingTransitions.addAll(state.getLoopTransitions());
			loopAndOutgoingTransitions.addAll(state.getOutgoingTransitions());
			int transitionsCount = loopAndOutgoingTransitions.size();
			List<State> addedTargetStates = new ArrayList<State>();

			for (int index = 0; index < transitionsCount; index++) {

				Transition transition = loopAndOutgoingTransitions.get(index);
				State targetState = transition.getTargetState();
				if (addedTargetStates.contains(targetState)) {
					continue;
				}

				addedTargetStates.add(targetState);
				WeightedRegularExpression newLabel = transition.getLabel();
				List<Transition> oldTransitionsList = new ArrayList<Transition>();
				if (optionalHistory != null) {
					oldTransitionsList.add(transition);
				}

				for (int index2 = index + 1; index2 < transitionsCount; index2++) {
					Transition transition2 = loopAndOutgoingTransitions.get(index2);
					State targetState2 = transition2.getTargetState();
					if (!targetState.equals(targetState2)) {
						continue;
					}

					WeightedRegularExpression.Sum sum = new WeightedRegularExpression.Sum();
					sum.setAlphabet(newLabel.getAlphabet());
					sum.setWeight(newLabel.getWeight());
					sum.setWritingData(newLabel.getWritingData());
					sum.setLeftExpression(newLabel);
					sum.setRightExpression(transition2.getLabel());
					newLabel = sum;
					if (optionalHistory != null) {
						oldTransitionsList.add(transition2);
					}
				}  // End for (int index2 = index + 1; index2 < transitionsCount; index2++)

				Transition newTransition = new Transition();
				newTransition.setSourceState(mapOldToNewStates.get(transition.getSourceState()).get(0));
				newTransition.setTargetState(mapOldToNewStates.get(transition.getTargetState()).get(0));
				newTransition.setLabel(newLabel);
				newTransition.setGeometricData(null);
				outputAutomaton.addTransition(newTransition);
				if (optionalHistory != null) {
					optionalHistory.newToOldTransitionsMap.put(newTransition, oldTransitionsList);
					Iterator<Transition> iterateOldTransitions = oldTransitionsList.iterator();
					while (iterateOldTransitions.hasNext()) {
						Transition oldTransition = iterateOldTransitions.next();
						List<Transition> list = new ArrayList();
						list.add(newTransition);
						optionalHistory.oldToNewTransitionsMap.put(oldTransition, list);
						list = null;  // List<Transition> list = new ArrayList();
					}  // End while (iterateTransitions.hasNext())
				}  // End if (optionalHistory != null)
				newTransition = null;  // Transition newTransition = new Transition();

				oldTransitionsList = null;  // List<Transition> oldTransitionsList = new ArrayList<Transition>();

			}  // End for (int index = 0; index < count; index++)

			addedTargetStates = null;  // List<State> addedTargetStates = new ArrayList<State>();
			loopAndOutgoingTransitions = null;  // List<Transition> loopAndOutgoingTransitions = new ArrayList<Transition>();

		}  // End while (iterateStates.hasNext())

		mapOldToNewStates = null;  // HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();

		return outputAutomaton;
	}  // End public static Automata mergeSimilarTransitions(Automata automata, AutomataHistory optionalHistory)

	public static Automata accessible(Automata automata, AutomataHistory optionalHistory) {

		//
		// Add initial states to the list of accessible states because all initial states are accessible by definition.
		//
		List<State> accessibleStates = automata.getInitialStates();

		//
		// Go forward from known accessible states to new accessible states until no new accessible state can be found.
		//
		for (int index = 0; index < accessibleStates.size(); index++) {
			State state = accessibleStates.get(index);
			Iterator<Transition> iterateTransitions = state.getOutgoingTransitions().iterator();
			while (iterateTransitions.hasNext()) {
				Transition transition = iterateTransitions.next();
				State anotherState = transition.getTargetState();
				if (!(accessibleStates.contains(anotherState))) {
					accessibleStates.add(anotherState);
				}
			}  // End while (iterateTransitions.hasNext())
		}  // End for (int index = 0; index < accessibleStates.size(); index++)

		//
		// Build a new automaton containing only the accessibles states of the original automaton.
		//
		Automata outputAutomaton = new Automata();
		outputAutomaton.setName("Accessible of " + automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();
		if (optionalHistory != null) {
			optionalHistory.newToOldStatesMap = new HashMap<State, List<State>>();
			optionalHistory.oldToNewStatesMap = mapOldToNewStates;
			optionalHistory.newToOldTransitionsMap = new HashMap<Transition, List<Transition>>();
			optionalHistory.oldToNewTransitionsMap = new HashMap<Transition, List<Transition>>();
		}  // End if (optionalHistory != null)

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (!(accessibleStates.contains(state))) {
				continue;
			}
			State newState = new State();
			newState.setName(state.getName());
//			newState.setInitialWeight(state.getInitialWeight());
//			newState.setFinalWeight(state.getFinalWeight());
                        automata.setInitialWeight(newState,state.getInitial().getWeight());
			automata.setFinalWeight(newState,state.getFinal().getWeight());
			
			newState.setGeometricData(new StateGeometricData());
			outputAutomaton.addState(newState);
			ArrayList<State> arrayList = new ArrayList<State>();
			arrayList.add(newState);
			mapOldToNewStates.put(state, arrayList);
			arrayList = null;  // ArrayList<State> arrayList = new ArrayList<State>();
			if (optionalHistory != null) {
				arrayList = new ArrayList<State>();
				arrayList.add(state);
				optionalHistory.newToOldStatesMap.put(newState, arrayList);
				arrayList = null;  // arrayList = new ArrayList<State>();
			}  // End if (optionalHistory != null)
			newState = null;  // State newState = new State();
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			if ((!(accessibleStates.contains(transition.getSourceState())))
					|| (!(accessibleStates.contains(transition.getTargetState())))) {
				continue;
			}
			Transition newTransition = new Transition();
			newTransition.setSourceState(mapOldToNewStates.get(transition.getSourceState()).get(0));
			newTransition.setTargetState(mapOldToNewStates.get(transition.getTargetState()).get(0));
			newTransition.setLabel(transition.getLabel());
			newTransition.setGeometricData(null);
			outputAutomaton.addTransition(newTransition);
			if (optionalHistory != null) {
				List<Transition> list = new ArrayList();
				list.add(transition);
				optionalHistory.newToOldTransitionsMap.put(newTransition, list);
				list = null;  // List<Transition> list = new ArrayList();
				list = new ArrayList();
				list.add(newTransition);
				optionalHistory.oldToNewTransitionsMap.put(transition, list);
				list = null;  // list = new ArrayList();
			}  // End if (optionalHistory != null)
			newTransition = null;  // Transition newTransition = new Transition();
		}  // End while (iterateTransitions.hasNext())

		mapOldToNewStates = null;  // HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();

		return outputAutomaton;  // Automata outputAutomaton = new Automata();
	}  // End public static Automata accessible(Automata automata, AutomataHistory optionalHistory)

	public static Automata coaccessible(Automata automata, AutomataHistory optionalHistory) {

		//
		// Add final states to the list of coaccessible states because all final states are coaccessible by definition.
		//
		List<State> coaccessibleStates = automata.getFinalStates();

		//
		// Go backward from known coaccessible states to new coaccessible states until no new coaccessible state can be found.
		//
		for (int index = 0; index < coaccessibleStates.size(); index++) {
			State state = coaccessibleStates.get(index);
			Iterator<Transition> iterateTransitions = state.getIncomingTransitions().iterator();
			while (iterateTransitions.hasNext()) {
				Transition transition = iterateTransitions.next();
				State anotherState = transition.getSourceState();
				if (!(coaccessibleStates.contains(anotherState))) {
					coaccessibleStates.add(anotherState);
				}
			}  // End while (iterateTransitions.hasNext())
		}  // End for (int index = 0; index < coaccessibleStates.size(); index++)

		//
		// Build a new automaton containing only the coaccessibles states of the original automaton.
		//
		Automata outputAutomaton = new Automata();
		outputAutomaton.setName("Coccessible of " + automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();
		if (optionalHistory != null) {
			optionalHistory.newToOldStatesMap = new HashMap<State, List<State>>();
			optionalHistory.oldToNewStatesMap = mapOldToNewStates;
			optionalHistory.newToOldTransitionsMap = new HashMap<Transition, List<Transition>>();
			optionalHistory.oldToNewTransitionsMap = new HashMap<Transition, List<Transition>>();
		}  // End if (optionalHistory != null)

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (!(coaccessibleStates.contains(state))) {
				continue;
			}
			State newState = new State();
			newState.setName(state.getName());
//			newState.setInitialWeight(state.getInitialWeight());
//			newState.setFinalWeight(state.getFinalWeight());
                        automata.setInitialWeight(newState,state.getInitial().getWeight());
			automata.setFinalWeight(newState,state.getFinal().getWeight());
			
			newState.setGeometricData(null);
			outputAutomaton.addState(newState);
			ArrayList<State> arrayList = new ArrayList<State>();
			arrayList.add(newState);
			mapOldToNewStates.put(state, arrayList);
			arrayList = null;  // ArrayList<State> arrayList = new ArrayList<State>();
			if (optionalHistory != null) {
				arrayList = new ArrayList<State>();
				arrayList.add(state);
				optionalHistory.newToOldStatesMap.put(newState, arrayList);
				arrayList = null;  // arrayList = new ArrayList<State>();
			}  // End if (optionalHistory != null)
			newState = null;  // State newState = new State();
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			if ((!(coaccessibleStates.contains(transition.getSourceState())))
					|| (!(coaccessibleStates.contains(transition.getTargetState())))) {
				continue;
			}
			Transition newTransition = new Transition();
			newTransition.setSourceState(mapOldToNewStates.get(transition.getSourceState()).get(0));
			newTransition.setTargetState(mapOldToNewStates.get(transition.getTargetState()).get(0));
			newTransition.setLabel(transition.getLabel());
			newTransition.setGeometricData(null);
			outputAutomaton.addTransition(newTransition);
			if (optionalHistory != null) {
				List<Transition> list = new ArrayList();
				list.add(transition);
				optionalHistory.newToOldTransitionsMap.put(newTransition, list);
				list = null;  // List<Transition> list = new ArrayList();
				list = new ArrayList();
				list.add(newTransition);
				optionalHistory.oldToNewTransitionsMap.put(transition, list);
				list = null;  // list = new ArrayList();
			}  // End if (optionalHistory != null)
			newTransition = null;  // Transition newTransition = new Transition();
		}  // End while (iterateTransitions.hasNext())

		mapOldToNewStates = null;  // HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();

		return outputAutomaton;  // Automata outputAutomaton = new Automata();
	}  // End public static Automata coaccessible(Automata automata, AutomataHistory optionalHistory)

	public static Automata removeEpsilonTransitions(Automata automata, AutomataHistory optionalHistory) {

		Automata outputAutomaton = new Automata();
		outputAutomaton.setName("Removed epsilon transitions of " + automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, List<State>> mapNewToOldStates = new HashMap<State, List<State>>();
		HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();
		if (optionalHistory != null) {
			optionalHistory.newToOldStatesMap = mapNewToOldStates;
			optionalHistory.oldToNewStatesMap = mapOldToNewStates;
			optionalHistory.newToOldTransitionsMap = new HashMap<Transition, List<Transition>>();
			optionalHistory.oldToNewTransitionsMap = new HashMap<Transition, List<Transition>>();
		}  // End if (optionalHistory != null)

		//
		// Copy all the states from the original automaton to the new automaton.
		// Use history of states and a HashMap to connect the original and the new states.
		//
		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			State newState = new State();
			newState.setName(state.getName());
//			newState.setInitialWeight(state.getInitialWeight());
//			newState.setFinalWeight(state.getFinalWeight());
                        automata.setInitialWeight(newState,state.getInitial().getWeight());
			automata.setFinalWeight(newState,state.getFinal().getWeight());
			
			newState.setGeometricData(null);
			outputAutomaton.addState(newState);
			ArrayList<State> arrayList = new ArrayList<State>();
			arrayList.add(state);
			mapNewToOldStates.put(newState, arrayList);
			arrayList = null;  // ArrayList<State> arrayList = new ArrayList<State>();
			arrayList = new ArrayList<State>();
			arrayList.add(newState);
			mapOldToNewStates.put(state, arrayList);
			arrayList = null;  // arrayList = new ArrayList<State>();
			newState = null;  // State newState = new State();
		}  // End while (iterateStates.hasNext())

		//
		// Go through every state in the new automaton.
		//
		iterateStates = outputAutomaton.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State newState = iterateStates.next();
			State state = mapNewToOldStates.get(newState).get(0);

			//
			// Add all the non-epsilon loop transitions to the new automaton.
			//
			Iterator<Transition> iterateTransitions = state.getLoopTransitions().iterator();
			while (iterateTransitions.hasNext()) {
				Transition transition = iterateTransitions.next();
				if (WeightedRegularExpression.One.class.isInstance(transition.getLabel())) {
					continue;
				}
				Transition newTransition = new Transition();
				newTransition.setSourceState(newState);
				newTransition.setTargetState(newState);
				newTransition.setLabel(transition.getLabel());
				newTransition.setGeometricData(null);
				outputAutomaton.addTransition(newTransition);
				if (optionalHistory != null) {
					List<Transition> list = new ArrayList();
					list.add(transition);
					optionalHistory.newToOldTransitionsMap.put(newTransition, list);
					list = null;  // List<Transition> list = new ArrayList();
					list = new ArrayList();
					list.add(newTransition);
					optionalHistory.oldToNewTransitionsMap.put(transition, list);
					list = null;  // list = new ArrayList();
				}  // End if (optionalHistory != null)
				newTransition = null;  // Transition newTransition = new Transition();
			}  // End while (iterateTransitions.hasNext())

			ArrayList<State> epsilonClosure = new ArrayList<State>();

			//
			// Add all the non-epsilon outgoing transitions to the new automaton
			// and record all states reachable by epsilon transitions in the
			// epsilon closure.
			//
			iterateTransitions = state.getOutgoingTransitions().iterator();
			while (iterateTransitions.hasNext()) {
				Transition transition = iterateTransitions.next();
				State targetState = transition.getTargetState();
				if (WeightedRegularExpression.One.class.isInstance(transition.getLabel())) {
					if (!(epsilonClosure.contains(targetState))) {
						epsilonClosure.add(targetState);
					}
					continue;
				}
				Transition newTransition = new Transition();
				newTransition.setSourceState(newState);
				newTransition.setTargetState(mapOldToNewStates.get(targetState).get(0));
				newTransition.setLabel(transition.getLabel());
				newTransition.setGeometricData(null);
				outputAutomaton.addTransition(newTransition);
				if (optionalHistory != null) {
					List<Transition> list = new ArrayList();
					list.add(transition);
					optionalHistory.newToOldTransitionsMap.put(newTransition, list);
					list = null;  // List<Transition> list = new ArrayList();
					list = new ArrayList();
					list.add(newTransition);
					optionalHistory.oldToNewTransitionsMap.put(transition, list);
					list = null;  // list = new ArrayList();
				}  // End if (optionalHistory != null)
				newTransition = null;  // Transition newTransition = new Transition();
			}  // End while (iterateTransitions.hasNext())

			for (int index = 0; index < epsilonClosure.size(); index++) {
				State closureState = epsilonClosure.get(index);

				//
				// Make the new state final if its epsilon closure has a final state.
				//
				if ((closureState.getFinal() != null)
						&& (newState.getFinal() == null)) {
//					newState.setFinalWeight(closureState.getFinalWeight());
                                        outputAutomaton.setFinalWeight(newState, closureState.getFinal().getWeight());
				}

				//
				// Add all the non-epsilon loop transitions to the new automaton.
				//
				iterateTransitions = closureState.getLoopTransitions().iterator();
				while (iterateTransitions.hasNext()) {
					Transition transition = iterateTransitions.next();
					if (WeightedRegularExpression.One.class.isInstance(transition.getLabel())) {
						continue;
					}
					Transition newTransition = new Transition();
					newTransition.setSourceState(newState);
					newTransition.setTargetState(mapOldToNewStates.get(closureState).get(0));
					newTransition.setLabel(transition.getLabel());
					newTransition.setGeometricData(null);
					outputAutomaton.addTransition(newTransition);
					newTransition = null;  // Transition newTransition = new Transition();
				}  // End while (iterateTransitions.hasNext())

				//
				// Add all the non-epsilon outgoing transitions to the new automaton
				// and record all states reachable by epsilon transitions in the
				// epsilon closure.
				//
				iterateTransitions = closureState.getOutgoingTransitions().iterator();
				while (iterateTransitions.hasNext()) {
					Transition transition = iterateTransitions.next();
					State targetState = transition.getTargetState();
					if (WeightedRegularExpression.One.class.isInstance(transition.getLabel())) {
						if (!(epsilonClosure.contains(targetState))) {
							epsilonClosure.add(targetState);
						}
						continue;
					}
					Transition newTransition = new Transition();
					newTransition.setSourceState(newState);
					newTransition.setTargetState(mapOldToNewStates.get(targetState).get(0));
					newTransition.setLabel(transition.getLabel());
					newTransition.setGeometricData(null);
					outputAutomaton.addTransition(newTransition);
					newTransition = null;  // Transition newTransition = new Transition();
				}  // End while (iterateTransitions.hasNext())

			}  // End for (int index = 0; index < epsilonClosure.size(); index++)

			epsilonClosure = null;  // ArrayList<State> epsilonClosure = new ArrayList<State>();

		}  // End while (iterateStates.hasNext())

		mapNewToOldStates = null;  // HashMap<State, List<State>> mapNewToOldStates = new HashMap<State, List<State>>();
		mapOldToNewStates = null;  // HashMap<State, List<State>> mapOldToNewStates = new HashMap<State, List<State>>();

		return outputAutomaton;  // Automata outputAutomaton = new Automata();
	}  // End public static Automata removeEpsilonTransitions(Automata automata, AutomataHistory optionalHistory)

	public static Automata product(Automata firstInput, Automata secondInput, AutomataHistory optionalHistory) {

		if ((firstInput == null)
				|| (secondInput == null)
				|| (firstInput.getWeight().semiring != secondInput.getWeight().semiring)
				|| (firstInput.getAlphabet().dataType != secondInput.getAlphabet().dataType)
				|| (firstInput.getOutputAlphabet() != null)
				|| (secondInput.getOutputAlphabet() != null)) {
			throw new IllegalArgumentException("The input automata are not compatible (different weight semiring, different alphabet data type, transducer, etc.) so their product is undefined.");
		}

		Automata outputAutomaton = new Automata();
		outputAutomaton.setName("Product of " + firstInput.getName() + " and " + secondInput.getName());
		outputAutomaton.setWritingData(firstInput.getWritingData());
		outputAutomaton.setWeight(firstInput.getWeight());
		outputAutomaton.setAlphabet(firstInput.getAlphabet());
		outputAutomaton.setOutputAlphabet(firstInput.getOutputAlphabet());
		HashMap<State, Integer> stateToInt1 = new HashMap<State, Integer>();
		HashMap<State, Integer> stateToInt2 = new HashMap<State, Integer>();
		HashMap<Integer, State> intToNewState = new HashMap<Integer, State>();
		HashMap<State, List<State>> mapNewToOldStates = new HashMap<State, List<State>>();
		if (optionalHistory != null) {
			optionalHistory.newToOldStatesMap = mapNewToOldStates;
			optionalHistory.oldToNewStatesMap = new HashMap<State, List<State>>();
			optionalHistory.newToOldTransitionsMap = new HashMap<Transition, List<Transition>>();
			optionalHistory.oldToNewTransitionsMap = new HashMap<Transition, List<Transition>>();
		}  // End if (optionalHistory != null)v

		int count1 = firstInput.getAllStates().size();
		int count2 = secondInput.getAllStates().size();
		for (int index2 = 0; index2 < count2; index2++) {

			for (int index1 = 0; index1 < count1; index1++) {

				State state1 = firstInput.getAllStates().get(index1);
				stateToInt1.put(state1, index1);
				State state2 = secondInput.getAllStates().get(index2);
				stateToInt2.put(state2, index2);
				State newState = new State();
//				InitialFinalWeight initialWeight1 = state1.getInitialWeight();
//				InitialFinalWeight initialWeight2 = state2.getInitialWeight();
//				InitialFinalWeight finalWeight1 = state1.getFinalWeight();
//				InitialFinalWeight finalWeight2 = state2.getFinalWeight();
                                
                                Initial initialWeight1 = state1.getInitial();
				Initial initialWeight2 = state2.getInitial();
				Final finalWeight1 = state1.getFinal();
				Final finalWeight2 = state2.getFinal();

				if ((initialWeight1 != null) && (initialWeight2 != null)) {
					InitialFinalWeight newWeightValue = new InitialFinalWeight();
//					newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, initialWeight1.getValue(), initialWeight2.getValue()));
//					newState.setInitialWeight(newWeightValue);
					newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, initialWeight1.getWeight(), initialWeight2.getWeight()));
					outputAutomaton.setInitialWeight(newState, newWeightValue.toString());
                                        
                                        newWeightValue = null;  // InitialFinalWeight newWeightValue = new InitialFinalWeight();
				}  // End if ((initialWeight1 != null) && (initialWeight2 != null))
				if ((finalWeight1 != null) && (finalWeight2 != null)) {
					InitialFinalWeight newWeightValue = new InitialFinalWeight();
//					newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, finalWeight1.getValue(), finalWeight2.getValue()));
//					newState.setFinalWeight(newWeightValue);
					
                                        newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, finalWeight1.getWeight(), finalWeight2.getWeight()));
					outputAutomaton.setFinalWeight(newState, newWeightValue.toString())
                                                
                                                ;
                                        newWeightValue = null;  // InitialFinalWeight newWeightValue = new InitialFinalWeight();
				}  // End if ((finalWeight1 != null) && (finalWeight2 != null))
				newState.setGeometricData(new StateGeometricData());
				ArrayList<State> arrayList = new ArrayList<State>();
				arrayList.add(state1);
				arrayList.add(state2);
				mapNewToOldStates.put(newState, arrayList);
				arrayList = null;  // ArrayList<State> arrayList = new ArrayList<State>();
				outputAutomaton.addState(newState);
				intToNewState.put(index2 * count1 + index1, newState);
				if (optionalHistory != null) {
					List<State> list = optionalHistory.oldToNewStatesMap.get(state1);
					if (list == null) {
						list = new ArrayList<State>();
						optionalHistory.oldToNewStatesMap.put(state1, list);
					}
					list.add(newState);
					list = optionalHistory.oldToNewStatesMap.get(state2);
					if (list == null) {
						list = new ArrayList<State>();
						optionalHistory.oldToNewStatesMap.put(state2, list);
					}
					list.add(newState);
				}  // End if (optionalHistory != null)
				newState = null;  // State newState = new State();

			}  // End for (int index1 = 0; index1 < count1; index1++)

		}  // End for (int index2 = 0; index2 < count2; index2++)

		Iterator<State> iterateStates = outputAutomaton.getAllStates().iterator();
		while (iterateStates.hasNext()) {

			State newState = iterateStates.next();
			State state1 = mapNewToOldStates.get(newState).get(0);
			State state2 = mapNewToOldStates.get(newState).get(1);

			ArrayList<Transition> allOutgoingTransitions1 = new ArrayList<Transition>();
			allOutgoingTransitions1.addAll(state1.getLoopTransitions());
			allOutgoingTransitions1.addAll(state1.getOutgoingTransitions());

			Iterator<Transition> iterateTransitions1 = allOutgoingTransitions1.iterator();
			while (iterateTransitions1.hasNext()) {

				Transition transition1 = iterateTransitions1.next();
				ArrayList<Transition> allOutgoingTransitions2 = new ArrayList<Transition>();
				allOutgoingTransitions2.addAll(state2.getLoopTransitions());
				allOutgoingTransitions2.addAll(state2.getOutgoingTransitions());

				Iterator<Transition> iterateTransitions2 = allOutgoingTransitions2.iterator();
				while (iterateTransitions2.hasNext()) {

					Transition transition2 = iterateTransitions2.next();
					LabelData labelData1 = getLabelData(transition1.getLabel());
					LabelData labelData2 = getLabelData(transition2.getLabel());
					WeightedRegularExpression newLabel;

					if (!(labelData1.symbol.toString().equals(labelData2.symbol.toString()))) {
						continue;
					}

					if (labelData1.weightValue == null) {
						if (labelData2.weightValue == null) {
							newLabel = new WeightedRegularExpression.Atomic(labelData1.symbol);
						} else {
							WeightedRegularExpression.Atomic atomic = new WeightedRegularExpression.Atomic(labelData1.symbol);
							newLabel = new WeightedRegularExpression.LeftMultiply(labelData2.weightValue, atomic);
							atomic = null;
						}
					} else {  // End if (labelData1.weightValue == null)
						if (labelData2.weightValue == null) {
							WeightedRegularExpression.Atomic atomic = new WeightedRegularExpression.Atomic(labelData1.symbol);
							newLabel = new WeightedRegularExpression.LeftMultiply(labelData1.weightValue, atomic);
							atomic = null;
						} else {
							WeightedRegularExpression.Atomic atomic = new WeightedRegularExpression.Atomic(labelData1.symbol);
							Object newWeightValue = multiplyWeights(outputAutomaton.getWeight().semiring, labelData1.weightValue, labelData2.weightValue);
							newLabel = new WeightedRegularExpression.LeftMultiply(newWeightValue, atomic);
							atomic = null;
						}
					}  // End else part of if (labelData1.weightValue == null)

					newLabel.setWritingData(outputAutomaton.getWritingData());
					newLabel.setWeight(outputAutomaton.getWeight());
					newLabel.setAlphabet(outputAutomaton.getAlphabet());

					Transition newTransition = new Transition();
					newTransition.setSourceState(newState);
					int index1 = stateToInt1.get(transition1.getTargetState());
					int index2 = stateToInt2.get(transition2.getTargetState());
					State newTargetState = intToNewState.get(index2 * count1 + index1);
					newTransition.setTargetState(newTargetState);
					newTransition.setLabel(newLabel);
					newTransition.setGeometricData(new TransitionGeometricData());
                                        //outputAutomaton.addTransition(newTransition);
                                        outputAutomaton.addTransition(newState, newTargetState);
                                        System.out.println("product trans: "+newState.getName()+"->"+newTargetState.getName());
                                        
                                        
                                        
					if (optionalHistory != null) {
						List<Transition> list = new ArrayList<Transition>();
						list.add(transition1);
						list.add(transition2);
						optionalHistory.newToOldTransitionsMap.put(newTransition, list);
						list = null;  // List<Transition> list = new ArrayList<Transition>();
						list = optionalHistory.oldToNewTransitionsMap.get(transition1);
						if (list == null) {
							list = new ArrayList<Transition>();
							optionalHistory.oldToNewTransitionsMap.put(transition1, list);
						}
						list.add(newTransition);
						list = optionalHistory.oldToNewTransitionsMap.get(transition2);
						if (list == null) {
							list = new ArrayList<Transition>();
							optionalHistory.oldToNewTransitionsMap.put(transition2, list);
						}
						list.add(newTransition);
					}  // End if (optionalHistory != null)
					newTransition = null;  // Transition newTransition = new Transition();
					newLabel = null;  // newLabel = new WeightedRegularExpression

				}  // End while (iterateTransitions2.hasNext())

				allOutgoingTransitions2 = null;  // ArrayList<Transition> allOutgoingTransitions2 = new ArrayList<Transition>();

			}  // End while (iterateTransitions1.hasNext())

			allOutgoingTransitions1 = null;  // ArrayList<Transition> allOutgoingTransitions1 = new ArrayList<Transition>();

		} // End while (iterateStates.hasNext())

		stateToInt1 = null;  // HashMap<State, Integer> stateToInt1 = new HashMap<State, Integer>();
		stateToInt2 = null;  // HashMap<State, Integer> stateToInt2 = new HashMap<State, Integer>();
		intToNewState = null;  // HashMap<Integer, State> intToNewState = new HashMap<Integer, State>();
		mapNewToOldStates = null;  // HashMap<State, List<State>> mapNewToOldStates = new HashMap<State, List<State>>();

		return outputAutomaton;
	}  // End public static Automata product(Automata firstInput, Automata secondInput, AutomataHistory optionalHistory)
    /*
     *  get global sate drawing data for the automata,
     *  for undefined attributes, use those defined in vgi default
     */
    @Override
    public StateDrawingData getAutomataStateDrawingData(){
        StateDrawingData dsdd=jgraphXInternalFrame.vgi.defaultStateDrawingData;
        //System.out.println("default dd: "+dsdd.getFillColor()+" "+dsdd.getStrokeColor()+" "+dsdd.getStrokeWidth());
            String fillColor=pmStateDrawingData.getFillColor();
            if(fillColor==null) fillColor=dsdd.getFillColor();

            String strokeColor=pmStateDrawingData.getStrokeColor();
            if(strokeColor==null) strokeColor=dsdd.getStrokeColor();

            float strokeWidth=pmStateDrawingData.getStrokeWidth();
            if(strokeWidth<0) strokeWidth=dsdd.getStrokeWidth();
            
            return new StateDrawingData(fillColor,strokeColor,strokeWidth);

    }
    /*
     *  set global state drawing data for the automata,
     *  only non-null attributes can be applied 
     */    
    @Override
        public void setAutomataStateDrawingData(StateDrawingData drawingData){
            if(drawingData.getFillColor()!=null) pmStateDrawingData.setFillColor(drawingData.getFillColor());
            if(drawingData.getStrokeColor()!=null) pmStateDrawingData.setStrokeColor(drawingData.getStrokeColor());
            if(drawingData.getStrokeWidth()>0) pmStateDrawingData.setStrokeWidth(drawingData.getStrokeWidth());
            //System.out.println("automata set sdd: "+pmStateDrawingData.getFillColor()+" "+pmStateDrawingData.getStrokeColor()+" "+pmStateDrawingData.getStrokeWidth());
            //pmStateDrawingData=drawingData;
        }
     /*
     *  get global state geometric data for the automata,
     *  for undefined attributes, use those defined in vgi default
     */
    @Override
    public StateGeometricData getAutomataStateGeometricData() {
        
        StateGeometricData dsgd=jgraphXInternalFrame.vgi.defaultStateGeometricData;
        
        Point2D size=pmStateGeometricData.getSize();
        if(size==null) size=dsgd.getSize();
        
        Point2D location=pmStateGeometricData.getLocation();
        if(location==null) location=dsgd.getLocation();
        if(location==null) location=new Point2D.Double(0,0);
//
//        double x=pmStateGeometricData.getX();
//        if(x<0) x=dsgd.getX();
//        
//        double y=pmStateGeometricData.getY();
//        if(y<0) y=dsgd.getY();
//        
//        double w=pmStateGeometricData.getWidth();
//        if(w<0) w=dsgd.getWidth();
//        
//        double h=pmStateGeometricData.getHeight();
//        if(h<0) h=dsgd.getHeight();
        
        String shape=pmStateGeometricData.getShape();
        if(shape==null) shape=dsgd.getShape();
       // System.out.println("automata geo: size "+size+" loc "+location+" shape "+shape);
        
        //return new StateGeometricData(location,size,shape);
        return new StateGeometricData(location.getX(),location.getY(),size.getX(),size.getY(),shape);
    }
    /*
     *  set global state geometric data for the automata,
     *  only non-null attributes can be applied 
     */
    @Override
    public void setAutomataStateGeometircData(StateGeometricData geoData) {
        if(geoData.getShape()!=null) pmStateGeometricData.setShape(geoData.getShape());
        if(geoData.getX()>0) pmStateGeometricData.setX(geoData.getX());
        if(geoData.getY()>0) pmStateGeometricData.setY(geoData.getY());
        if(geoData.getWidth()>0) pmStateGeometricData.setWidth(geoData.getWidth());
        if(geoData.getHeight()>0) pmStateGeometricData.setHeight(geoData.getHeight());
        
       // pmStateGeometricData=geoData;
    }
    /*
     *  get global transition drawing data for the automata,
     *  for undefined attributes, use those defined in vgi default
     */
    @Override
    public TransitionDrawingData getAutomataTransitionDrawingData() {
       TransitionDrawingData dtdd=jgraphXInternalFrame.vgi.defaultTransitionDrawingData; 
       
       String strokeColor=pmTransitionDrawingData.getStrokeColor();
       if(strokeColor==null) strokeColor=dtdd.getStrokeColor();
       
       float strokeWidth=pmTransitionDrawingData.getStrokeWidth();
       if(strokeWidth<0) strokeWidth=dtdd.getStrokeWidth();
       
       String startArrow=pmTransitionDrawingData.getStartArrow();
       if(startArrow==null) startArrow=dtdd.getStartArrow();
       
       String endArrow=pmTransitionDrawingData.getEndArrow();
       if(endArrow==null) endArrow=dtdd.getEndArrow();
        
       return new TransitionDrawingData(strokeColor,strokeWidth,startArrow,endArrow);
    }
    /*
     *  set global transition drawing data for the automata,
     *  only non-null attributes can be applied 
     */
    @Override
    public void setAutomataTransitionDrawingData(TransitionDrawingData drawingData) {
        //pmTransitionDrawingData=drawingData;
        
        if(drawingData.getStrokeColor()!=null) pmTransitionDrawingData.setStrokeColor(drawingData.getStrokeColor());
        if(drawingData.getStrokeWidth()>0) pmTransitionDrawingData.setStrokeWidth(drawingData.getStrokeWidth());
        
        if(drawingData.getStartArrow()!=null) pmTransitionDrawingData.setStartArrow(drawingData.getStartArrow());
        if(drawingData.getEndArrow()!=null) pmTransitionDrawingData.setEndArrow(drawingData.getEndArrow());
        
    }

    /*
     *  get single state drawing data
     *  for undefined attributes, use those defined in automata
     */
    public StateDrawingData getStateDrawingData(State state){
        
        StateDrawingData stateDD;
        stateDD=state.getDrawingData();
        
        StateDrawingData aDD=getAutomataStateDrawingData();
            
        String fillColor;
        if(stateDD.getFillColor()==null){
            fillColor=aDD.getFillColor();
        }else
            fillColor=stateDD.getFillColor();

        String strokeColor;
        if(stateDD.getStrokeColor()==null){
            strokeColor=aDD.getStrokeColor();
        }else
            strokeColor=stateDD.getStrokeColor();

        float strokeWidth;
        if(stateDD.getStrokeWidth()==-1){
            strokeWidth=aDD.getStrokeWidth();
        }else
            strokeWidth=stateDD.getStrokeWidth();


        return new StateDrawingData(fillColor,strokeColor,strokeWidth);
      
    }
    public void setStateDrawingData(State state,StateDrawingData drawingdata){
        state.setDrawingData(drawingdata);
        jgraphAutomata.updateVertexDrawingData(stateToCell(state), getStateDrawingData(state));
    }
    /*
     *  get single state geometric data
     *  for undefined attributes, use those defined in automata
     */
    public StateGeometricData getStateGeometricData(State state){
        
        StateGeometricData dsgd=this.getAutomataStateGeometricData();
        StateGeometricData sgd = state.getGeometricData();
//        System.out.println("state geodata: "+sgd.getLocation());
        
        double x,y,w,h;
        if(sgd.getLocation()!=null){
            x=sgd.getX();
            y=sgd.getY();
        }else{
            x=dsgd.getX();
            y=dsgd.getY();
        }
        
        if(sgd.getSize()!=null){
            w=sgd.getWidth();
            h=sgd.getHeight();
        }else{
            w=dsgd.getWidth();
            h=dsgd.getHeight();
        }
        
        String shape=sgd.getShape();
        if(shape==null) shape=dsgd.getShape();
        
//        System.out.println("compare with default: "+x+","+y);
        
            
        return new StateGeometricData(x,y,w,h,shape);
    }
    public void setStateGeometricData(State state,StateGeometricData geodata){
        state.setGeometricData(geodata);
        jgraphAutomata.updateVertexGeometricData(stateToCell(state),getStateGeometricData(state));
    }
    
    /*
     *  get single state drawing data
     *  for undefined attributes, use those defined in automata
     */
    public TransitionDrawingData getTransitionDrawingData(Transition transition){
        
        TransitionDrawingData tdd=transition.getDrawingData();
        TransitionDrawingData atdd=getAutomataTransitionDrawingData();
            
        String strokeColor=tdd.getStrokeColor();
        if(strokeColor==null) strokeColor=atdd.getStrokeColor();
        
        float strokeWidth=tdd.getStrokeWidth();
        if(strokeWidth<0) strokeWidth=atdd.getStrokeWidth();
        
        String startArrow=tdd.getStartArrow();
        if(startArrow==null) startArrow=atdd.getStartArrow();
        
        String endArrow=tdd.getEndArrow();
        if(endArrow==null) endArrow=atdd.getEndArrow();
        
        return new TransitionDrawingData(strokeColor,strokeWidth,startArrow,endArrow);
      
    }
    public TransitionGeometricData getTransitionGeometricData(Transition transition){
        TransitionGeometricData tgd=transition.getGeometricData();
        
        // TODO: compare with geodata in automata
        
        return tgd;
    }
    public void setTransitionDrawingData(Transition transition,TransitionDrawingData drawingdata){
        transition.setDrawingData(drawingdata);
        jgraphAutomata.updateEdgeDrawingData(transitionToCell(transition),getTransitionDrawingData(transition));
    }
    public void setTransitionGeometricData(Transition transition, TransitionGeometricData geodata){
        transition.setGeometricData(geodata);
        jgraphAutomata.updateEdgeGeometricData(transitionToCell(transition),getTransitionGeometricData(transition));
    }
    
    public void addTransitionControlPoint(Transition transition,Point2D point){
        
//        boolean reverse=!(transition.getSourceState().getGeometricData().getX()<transition.getTargetState().getGeometricData().getX());
//        transition.getGeometricData().addControlPoint(point, reverse);
//        
//        mxCell edge=transitionToCell(transition);
//        jgraphAutomata.addEdgeControlPoint(edge, point,reverse);
        addTransitionControlPoint(transition,point,transition.getGeometricData().controlPoints.size());
        
    }
    public void addTransitionControlPoint(Transition transition,Point2D point,int index){
        
        boolean reverse=!(transition.getSourceState().getGeometricData().getX()<transition.getTargetState().getGeometricData().getX());
        transition.getGeometricData().addControlPoint(point, reverse,index);
        
        mxCell edge=transitionToCell(transition);
        jgraphAutomata.addEdgeControlPoint(edge, point,reverse,index);
    }
    public void deleteTransitionControlPoint(Transition transition,Point2D point){
        
        transition.getGeometricData().controlPoints.remove(point);
        
        jgraphAutomata.deleteControlPoint(transitionToCell(transition),point);
        
    }
    public void resetTransitionControlPoint(Transition transition){
        
        if(transition.getGeometricData()!=null){
            transition.getGeometricData().controlPoints.clear();
             jgraphAutomata.resetControlPoint(transitionToCell(transition));
        }
        // LOOP??
        
       
    }
    public void updateTransitionControlPoint(Transition transition,List<Point2D> points){
        System.out.println("update control point: "+points);    
        resetTransitionControlPoint(transition);
        for(Point2D point:points) 
            addTransitionControlPoint(transition,point,transition.getGeometricData().controlPoints.size());
        
    }
    public void setTransitionLabel(Transition transition,WeightedRegularExpression label){
        transition.setLabel(label);
        jgraphAutomata.setEdgeLabel(transitionToCell(transition),label.toString());
    }
    
    
    public void deleteState(State state){
        List<Transition> transitions=state.getTransitions();
        for(Transition trans:transitions) deleteTransition(trans);
        
        pmAllStates.remove(state);
        jgraphAutomata.deleteCell(stateToCell(state));
    }
    public void deleteTransition(Transition transition){
        State source=transition.getSourceState();
        State target=transition.getTargetState();
//        
//        System.out.println("remove: "+source.getTransitions().remove(transition));
//        System.out.println("remove: "+target.getTransitions().remove(transition));
////        
        System.out.println("remove transition: "+transition.getSourceState()+"->"+transition.getTargetState());
        pmAllTransitions.remove(transition);
        jgraphAutomata.deleteCell(transitionToCell(transition));
        
    }

    /*
     *  get state or transtition at the location(in automata coordinate)
     */
    public Object getStateAt(Point2D location){
        Point2D loc=projection.getLocFromGeo(location);
        Object cell=jgraphAutomata.graphComponent.getCellAt((int)loc.getX(), (int)loc.getY());
        return cellToState((mxCell)cell);
       
    }
    
    /*
     * point: the new location
     */
    public void moveState(State state, Point2D point){
        
        Point2D oldpoint=state.getGeometricData().getLocation();
        //offset.setLocation(offset.getX()-point.getX(), offset.getY()-point.getY());
        
        // 1. move state
        state.getGeometricData().setLocation(point);
        // 2. move state in JgraphAutomata
        StateGeometricData geodata=getStateGeometricData(state);
        Point2D leftTopPoint=new Point2D.Double(point.getX()-geodata.getWidth()/2,point.getY()+geodata.getHeight()/2);
        jgraphAutomata.moveCell(stateToCell(state), leftTopPoint);
       
        // 3. move connecting transitions' control points
        List<Transition> transitions=state.getTransitions();
        for(Transition trans:transitions){
            List<Point2D> newpoints=new ArrayList<Point2D>();
            List<Point2D> points=trans.getGeometricData().controlPoints;
                
            if(trans.getSourceState()==trans.getTargetState()){
                // if is loop, simply translate control points 
                for(Point2D pt:points){
                    Point2D newpt=new Point2D.Double(pt.getX()-oldpoint.getX()+point.getX(),
                                                        pt.getY()-oldpoint.getY()+point.getY());
                    newpoints.add(newpt);
                }
            }else{
                for(Point2D pt:points){
                    State source=trans.getSourceState();
                    if(source==state) source=trans.getTargetState();
                    Point2D newpt=computeControlPointAfterMoving(pt,point,oldpoint,source.getGeometricData().getLocation());
                    newpoints.add(newpt);

                }
            }
            trans.getGeometricData().controlPoints=newpoints;
            // 4. move connecting transitions' control point in JgraphAutomata
            mxCell transition=transitionToCell(trans);
            jgraphAutomata.setEdgeControlPoint(transition, newpoints);
      
        }
        // if there's initial/final
        // 5. move initial/final
        // THERE'S NO NEED for length/direction!
        
        // 6. move initial/final in JgraphAutomata
        // recompute the absolute location for Jgraph
        Initial ini=state.getInitial();
        if(ini!=null) 
            this.setIniFinGeometricData(state,ini.geodata,true);
        
        Final fin=state.getFinal();
        if(fin!=null) 
            this.setIniFinGeometricData(state,fin.geodata,false);
        
        
    }
    
   
    public void moveStates(List<State> states, Point2D offset){
        
        // TODO:
        // control points of inner transitions shouldn't be changed
        //                of outer transitions should be changed
        List<Transition> innerTransitions=new ArrayList<Transition>();
        List<Transition> outerTransitions=new ArrayList<Transition>();
        
        for(State state:states){
            Point2D newPos=state.getGeometricData().getLocation();
            newPos.setLocation(newPos.getX()+offset.getX(),newPos.getY()+offset.getY());
            // 1. move state
            state.getGeometricData().setLocation(newPos);
            // 2. move state in JgraphAutomata
            StateGeometricData geodata=getStateGeometricData(state);
            Point2D leftTopPoint=new Point2D.Double(newPos.getX()-geodata.getWidth()/2,newPos.getY()+geodata.getHeight()/2);
            jgraphAutomata.moveCell(stateToCell(state), leftTopPoint);
        
            // 3. update initial/final in JgraphAutomata
            Initial ini=state.getInitial();
            if(ini!=null) 
                this.setIniFinGeometricData(state,ini.geodata,true);
        
            Final fin=state.getFinal();
            if(fin!=null) 
                this.setIniFinGeometricData(state,fin.geodata,false);
            
            // 4. put transitions into categories
            List<Transition> transitions=state.getTransitions();
            for(Transition trans:transitions){
                
                if(innerTransitions.contains(trans) || outerTransitions.contains(trans)) continue;
                
                State target=trans.getTargetState();
                State source=trans.getSourceState();
                if((states.contains(target) && states.contains(source)))
                    innerTransitions.add(trans);
                else 
                   outerTransitions.add(trans);
            }
        }
        
        // 5. update innerTransitions
        for(Transition trans:innerTransitions){
            
//            System.out.println("update inner transition: "+trans.getSourceState()+"->"+trans.getTargetState());
            
            List<Point2D> points=trans.getGeometricData().controlPoints;
            List<Point2D> newpoints=new ArrayList<Point2D>();
            for(Point2D pt:points){
                Point2D newpt=new Point2D.Double(pt.getX()+offset.getX(),pt.getY()+offset.getY());
                newpoints.add(newpt);
            }
            trans.getGeometricData().controlPoints=newpoints;
            // move connecting transitions' control point in JgraphAutomata
            mxCell transition=transitionToCell(trans);
            jgraphAutomata.setEdgeControlPoint(transition, newpoints);
      
        }
        
        // 6. update outerTransitions
        for(Transition trans:outerTransitions){
            
//            System.out.println("update outer transition: "+trans.getSourceState()+"->"+trans.getTargetState());
            
            State state;
            if(states.contains(trans.getSourceState())) state=trans.getSourceState();
            else state=trans.getTargetState();
            Point2D point=state.getGeometricData().getLocation();
            Point2D oldpoint=new Point2D.Double(point.getX()-offset.getX(),point.getY()-offset.getY());
            
            List<Point2D> points=trans.getGeometricData().controlPoints;
            List<Point2D> newpoints=new ArrayList<Point2D>();
            for(Point2D pt:points){
                State source=trans.getSourceState();
                if(source==state) source=trans.getTargetState();
                Point2D newpt=computeControlPointAfterMoving(pt,point,oldpoint,source.getGeometricData().getLocation());
                newpoints.add(newpt);
            }
            trans.getGeometricData().controlPoints=newpoints;
            // 4. move connecting transitions' control point in JgraphAutomata
            mxCell transition=transitionToCell(trans);
            jgraphAutomata.setEdgeControlPoint(transition, newpoints);
      
        }
        
        
        jgraphAutomata.graph.refresh();
        
        
    }
    public void moveStatesWithoutUpdatingOuterTransitions(List<State> states,Point2D offset){
        
        // TODO:
        // control points of inner transitions shouldn't be changed
        //                of outer transitions should be changed
        List<Transition> innerTransitions=new ArrayList<Transition>();
        List<Transition> outerTransitions=new ArrayList<Transition>();
        
        for(State state:states){
            Point2D newPos=state.getGeometricData().getLocation();
            newPos.setLocation(newPos.getX()+offset.getX(),newPos.getY()+offset.getY());
            // 1. move state
            state.getGeometricData().setLocation(newPos);
            // 2. move state in JgraphAutomata
            StateGeometricData geodata=getStateGeometricData(state);
            Point2D leftTopPoint=new Point2D.Double(newPos.getX()-geodata.getWidth()/2,newPos.getY()-geodata.getHeight()/2);
            jgraphAutomata.moveCell(stateToCell(state), leftTopPoint);
        
            // 3. update initial/final in JgraphAutomata
            Initial ini=state.getInitial();
            if(ini!=null) 
                this.setIniFinGeometricData(state,ini.geodata,true);
        
            Final fin=state.getFinal();
            if(fin!=null) 
                this.setIniFinGeometricData(state,fin.geodata,false);
            
            // 4. put transitions into categories
            List<Transition> transitions=state.getTransitions();
            for(Transition trans:transitions){
                
                if(innerTransitions.contains(trans) || outerTransitions.contains(trans)) continue;
                
                State target=trans.getTargetState();
                State source=trans.getSourceState();
                if((states.contains(target) && states.contains(source)))
                    innerTransitions.add(trans);
                else 
                   outerTransitions.add(trans);
            }
        }
        
        // 5. update innerTransitions
        for(Transition trans:innerTransitions){
            
//            System.out.println("update inner transition: "+trans.getSourceState()+"->"+trans.getTargetState());
            
            List<Point2D> points=trans.getGeometricData().controlPoints;
            List<Point2D> newpoints=new ArrayList<Point2D>();
            for(Point2D pt:points){
                Point2D newpt=new Point2D.Double(pt.getX()+offset.getX(),pt.getY()+offset.getY());
                newpoints.add(newpt);
            }
            trans.getGeometricData().controlPoints=newpoints;
            // move connecting transitions' control point in JgraphAutomata
            mxCell transition=transitionToCell(trans);
            jgraphAutomata.setEdgeControlPoint(transition, newpoints);
      
        }
    }
    
    public void copyStates(List<State> states){
        int len=states.size();
        System.out.println("size of copying states= "+len);
        
        // copy the states and the inner transitions
        List<State> copyStates=new ArrayList<State>();
        for(State state:states){
            
            State copystate=new State();
        
            copystate.setGeometricData(state.getGeometricData());
            copystate.setDrawingData(state.getDrawingData());
            copystate.setName(state.getName()+"'");
            System.out.println("copy state: "+copystate.getName());
            this.addState(copystate);
            
            if(state.getInitial()!=null){
                this.setInitialWeight(copystate,state.getInitial().weight);
                this.setIniFinGeometricData(copystate, state.getInitial().geodata, true);
            }
            if(state.getFinal()!=null){
                this.setFinalWeight(copystate,state.getFinal().weight);
                this.setIniFinGeometricData(copystate, state.getFinal().geodata, false);
            }
            
            copyStates.add(copystate);
        }
        
        System.out.println("size of states= "+len);
        for(int i=0;i<len;++i){
            System.out.println("get state: "+i);
        
            State source=states.get(i);
            System.out.println("get transitions from "+source.getName());
            List<Transition> outgoTransitions=source.getOutgoingTransitions();
            
            for(Transition tran:outgoTransitions){
                
                State target=tran.getTargetState();
                if(states.contains(target)){
                    
                    Transition copytran=new Transition();
                    
                    State copysource=copyStates.get(i);
                    State copytarget=copyStates.get(states.indexOf(target));
                    
                    copytran.setSourceState(copysource);
                    copytran.setTargetState(copytarget);
                    System.out.println("copy transition: "+copysource.getName()+"->"+copytarget.getName());
                    
                    copytran.setDrawingData(tran.getDrawingData());
                    copytran.setGeometricData(tran.getGeometricData());
                    
                    copytran.setLabel(tran.getLabel());
                    
                    this.addTransition(copytran);
                    
                    copysource.addTransition(copytran);
                    copytarget.addTransition(copytran);
                }
            }
            
            // handle the self loop
            List<Transition> loopTransitions = source.getLoopTransitions();

            for (Transition tran : loopTransitions) {
                
                State target = tran.getTargetState();

                if(states.contains(target)){
                    
                    Transition copytran = new Transition();
                    
                    State copysource = copyStates.get(i);
                    State copytarget = copyStates.get(states.indexOf(target));
                    
                    copytran.setSourceState(copysource);
                    copytran.setTargetState(copytarget);
                    System.out.println("copy transition: "+copysource.getName()+"->"+copytarget.getName());
                    
                    copytran.setDrawingData(tran.getDrawingData());
                    copytran.setGeometricData(tran.getGeometricData());
                    
                    copytran.setLabel(tran.getLabel());
                    
                    this.addTransition(copytran);
                    
                    copysource.addTransition(copytran);
                    copytarget.addTransition(copytran);
                }
            }
        }
        
        moveStates(copyStates,new Point2D.Double(50,50));
        
        // update selectedStates to the copies
        this.setSelectedStates(copyStates);

        
    }
    
   /*
 *  Old selection methods
 * /
 */
    
    public List<State> getSelectedStates(){
        /*
        System.out.print("\nselected: ");
        for(State state:selectedStates){
            System.out.print(state.getName()+" ");
        }
        System.out.println();
        return selectedStates;
        */
        List<State> stateList = new ArrayList<State>();
        for (Object obj: selectedObjs) {
            if (obj instanceof State) {
                stateList.add((State)obj);
            }
        }
        return stateList;
    }
    public void setSelectedStates(List<State> sss){
        /*
        selectedStates=sss;
        List<mxCell> cells=new ArrayList<mxCell>();
        for(State state:selectedStates){
            cells.add(stateToCell(state));
        }
        jgraphAutomata.setSelectedCells(cells.toArray());*/
        resetSelectedObjs();
        for(State obj: sss){
           addSelectedObj((Object)obj);
        }
        updateSelectionJGraphAutomata();
    } 
    public void addSelectedState(State state){
        /*
        if(selectedStates==null) selectedStates=new ArrayList<State>();
        if(state!=null) selectedStates.add(state);*/
    }
    public void resetSelectedStates(){
//        System.out.println("Reset selected States!");
        //if(selectedStates!=null) selectedStates.clear();
    }
    public void selectAllStates(){
        /*
        System.out.println("Select ALL States!");
        selectedStates.addAll(this.pmAllStates);*/
//        selectedStates=this.pmAllStates;
        selectAllObjs();
    }
    
/*
 *  New selection methods
 * /
 */
    public void selectedObjsExistenceChecking() {
        List<Object> objList = group.retrieveAllSelectedObjs();
        for (Object obj: objList) {
            if (objToCell(obj) == null) {
                group.removeObj(obj);
            }
        }
    }
    public boolean selectionEquivalenceChecking(Object[] cells) {
        //the corresponding obj list of cells
        List<Object> objList = new ArrayList<Object>();
        for (Object cell:cells){
            objList.add(cellToObj((mxCell)cell));
        }
        //equivalence checking
        if (!selectedObjs.containsAll(objList)) return false;
        if (!objList.containsAll(selectedObjs)) return false;
        return true;
    }
    public void updateSelectionJGraphAutomata() {
        if (selectedObjs == null) return;
        List<mxCell> cells = new ArrayList<mxCell>();
        for(Object obj:selectedObjs){
            cells.add(objToCell(obj));
        }
        jgraphAutomata.setSelectedCells(cells.toArray());
    }
    public List<Object> getSelectedObjs(){
        return selectedObjs;
    }
    public void setSelectedObjs(List<Object> sss){
        selectedObjs = group.retrieveSelectedObjs(sss);
        updateSelectionJGraphAutomata();
    } 
    public void addSelectedObj(Object obj){
        if(selectedObjs==null) selectedObjs=new ArrayList<Object>();
        if(obj != null && !selectedObjs.contains(obj)) selectedObjs.add(obj);
        selectedObjs = group.retrieveSelectedObjs(selectedObjs);
        //updateSelectionJGraphAutomata();
    }
    public void resetSelectedObjs(){
//        System.out.println("Reset selected States!");
        if(selectedObjs != null) selectedObjs.clear();
         //updateSelectionJGraphAutomata();
    }
    public void selectAllObjs(){
        System.out.println("Select ALL States!");
        selectedObjs.addAll(this.pmAllStates);
        selectedObjs = group.retrieveSelectedObjs(selectedObjs);
         //updateSelectionJGraphAutomata();
//        selectedStates=this.pmAllStates;
    }
    
      private Point2D computeControlPointAfterMoving(Point2D pt,Point2D newVertexPoint,Point2D oldVertexPoint,Point2D endPoint){
        
        
        double a=oldVertexPoint.getX()-endPoint.getX();
        double b=oldVertexPoint.getY()-endPoint.getY();

        double theta=Math.atan2(b, a);
        
        double len=Math.sqrt(a*a+b*b);

        double c1=pt.getX()-endPoint.getX();
        double c2=pt.getY()-endPoint.getY();


        double newa=newVertexPoint.getX()-endPoint.getX();
        double newb=newVertexPoint.getY()-endPoint.getY();


        double newtheta=Math.atan2(newb, newa);

        double newlen=Math.sqrt(newa*newa+newb*newb);

        double delta=newtheta-theta;
        double newc1=(Math.cos(delta)*c1-Math.sin(delta)*c2)*newlen/len;
        double newc2=(Math.sin(delta)*c1+Math.cos(delta)*c2)*newlen/len;

        newc1=newc1+endPoint.getX();
        newc2=newc2+endPoint.getY();
        
        //newc.add(new Vector2D(newVertexPoint));
        //return new Point2D.Double(newc.getX(),newc.getY());
        return new Point2D.Double(newc1,newc2);
        
    }  
//    private Point2D computeControlPointAfterMoving(Point2D pt,Point2D newVertexPoint,Point2D oldVertexPoint,Point2D endPoint){
//        
//        
//        double a=endPoint.getX()-oldVertexPoint.getX();
//        double b=endPoint.getY()-oldVertexPoint.getY();
//
//        double theta=Math.atan2(b, a);
//        //theta=Math.toDegrees(theta);
//
//        double len=Math.sqrt(a*a+b*b);
//
//        double c1=pt.getX()-oldVertexPoint.getX();
//        double c2=pt.getY()-oldVertexPoint.getY();
//
//        double rc1=Math.cos(theta)*c1-Math.sin(theta)*c2;
//        double rc2=Math.sin(theta)*c1+Math.cos(theta)*c2;
//
//        double area=len*rc2;
//        //   System.out.println("\narea: "+area+"\n theta: "+Math.toDegrees(theta) +" len: "+len+" c: "+rc1+" , "+rc2);
//
//        double xc1=Math.cos(theta)*a-Math.sin(theta)*b;
//        //   System.out.println(" X: "+xc1);
//        double newa=endPoint.getX()-newVertexPoint.getX();
//        double newb=endPoint.getY()-newVertexPoint.getY();
//
//
//        double newtheta=Math.atan2(newb, newa);
//
//        double newlen=Math.sqrt(newa*newa+newb*newb);
//
//        double newxc1=Math.cos(newtheta)*newa-Math.sin(newtheta)*newb;
//        // System.out.println(" newX: "+newxc1);
//        double newrc2=rc2;
//        /*if(newlen>len)
//            newrc2=area/(newlen*0.8);
//        else
//            newrc2=rc2/len*(newlen*1.2);*/
//        double newrc1=rc1/len*newlen;
//        // System.out.println("new theta: "+Math.toDegrees(newtheta) +" len: "+newlen+" c: "+newrc1+" , "+newrc2);
//
//        double newc1=Math.cos(-newtheta)*newrc1-Math.sin(-newtheta)*newrc2;
//        double newc2=Math.sin(-newtheta)*newrc1+Math.cos(-newtheta)*newrc2;
//
//        newc1=newc1+newVertexPoint.getX();
//        newc2=newc2+newVertexPoint.getY();
//        
//        //newc.add(new Vector2D(newVertexPoint));
//        //return new Point2D.Double(newc.getX(),newc.getY());
//        return new Point2D.Double(newc1,newc2);
//        
//    }

    public Projection getProjection() {
        return projection;
    }
    public void setProjection(Projection p){
        projection=p;
        // update all things in jgraphAutomata!!
        refresh();
    }
    
    /*
     * refresh all geometric/draw data in jgraphAutomata
     */
    public void refresh(){
        for(State state:pmAllStates){
//            System.out.println("update: "+state);
            mxCell vertex=stateToCell(state);
            jgraphAutomata.updateVertexDrawingData(vertex, getStateDrawingData(state));
            jgraphAutomata.updateVertexGeometricData(vertex, getStateGeometricData(state));
            if(state.getInitial()!=null) jgraphAutomata.setIniFinGeometricData(vertex,getIniFinGeometricData(state, true), true);
            if(state.getFinal()!=null) jgraphAutomata.setIniFinGeometricData(vertex,getIniFinGeometricData(state, false), false);
        
        }
        for(Transition tran:pmAllTransitions){
//            System.out.println("update: "+tran.getSourceState()+"->"+tran.getTargetState());
            jgraphAutomata.updateEdgeDrawingData(transitionToCell(tran), getTransitionDrawingData(tran));
            jgraphAutomata.updateEdgeGeometricData(transitionToCell(tran), getTransitionGeometricData(tran));
        }
        jgraphAutomata.graph.refresh();
    }

          
            
            
	protected static class LabelData {

		Object symbol;
		Object weightValue;
	}

	protected static LabelData getLabelData(WeightedRegularExpression label) {
		LabelData output = new LabelData();

		if (WeightedRegularExpression.Atomic.class.isInstance(label)) {

			output.symbol = ((WeightedRegularExpression.Atomic) label).getSymbol();
			output.weightValue = null;

		} else if (WeightedRegularExpression.LeftMultiply.class.isInstance(label)) {

			WeightedRegularExpression.LeftMultiply leftMultiply = (WeightedRegularExpression.LeftMultiply) label;
			if (WeightedRegularExpression.Atomic.class.isInstance(leftMultiply.getExpression())) {
				output.symbol = ((WeightedRegularExpression.Atomic) (leftMultiply.getExpression())).getSymbol();
				output.weightValue = leftMultiply.getWeightValue();
			} else {
				throw new IllegalArgumentException("Input automata to the product algorithm must have no epsilon transitions, and their labels must have only one symbol (with one optional weight).");
			}

		} else if (WeightedRegularExpression.RightMultiply.class.isInstance(label)) {

			WeightedRegularExpression.RightMultiply righttMultiply = (WeightedRegularExpression.RightMultiply) label;
			if (WeightedRegularExpression.Atomic.class.isInstance(righttMultiply.getExpression())) {
				output.symbol = ((WeightedRegularExpression.Atomic) (righttMultiply.getExpression())).getSymbol();
				output.weightValue = righttMultiply.getWeightValue();
			} else {
				throw new IllegalArgumentException("Input automata to the product algorithm must have no epsilon transitions, and their labels must have only one symbol (with one optional weight).");
			}

		} else {
			throw new IllegalArgumentException("Input automata to the product algorithm must have no epsilon transitions and, their labels must have only one symbol (with one optional weight).");
		}

		return output;
	}  // End protected static LabelData getLabelData(WeightedRegularExpression label)

	protected static Object multiplyWeights(
			TAFKitInterface.AutomataType.Semiring semiring,
			Object weight1,
			Object weight2) {

		switch (semiring) {
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
                            if (weight1 instanceof String) {
                                weight1 = Boolean.valueOf((String) weight1);
                            }
                            if (weight2 instanceof String) {
                                weight2 = Boolean.valueOf((String) weight2);
                            }
				if ((Boolean.class.isInstance(weight1))
						&& (((Boolean) weight1) == true)
						&& (Boolean.class.isInstance(weight2))
						&& (((Boolean) weight2) == true)) {
					return new Boolean(true);
				}
				break;
			case Z_INTEGER:
                            if (weight1 instanceof String) {
                                weight1 = Integer.valueOf((String) weight1);
                            }
                            if (weight2 instanceof String) {
                                weight2 = Integer.valueOf((String) weight2);
                            }
				if ((Integer.class.isInstance(weight1))
						&& (Integer.class.isInstance(weight2))) {
					return new Integer(((Integer) weight1) * ((Integer) weight2));
				}
				break;
			case Q_RATIONAL:
				if ((weight1 instanceof String)
					&& (weight2 instanceof String)) {
					return weight1 + " * " + weight2;
				}
                            break;
			case R_REAL:
                            if (weight1 instanceof String) {
                                weight1 = Double.valueOf((String) weight1);
                            }
                            if (weight2 instanceof String) {
                                weight2 = Double.valueOf((String) weight2);
                            }
				if ((Double.class.isInstance(weight1))
						&& (Double.class.isInstance(weight2))) {
					return new Double(((Double) weight1) * ((Double) weight2));
				}
				break;
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
                            if (weight1 instanceof String) {
                                weight1 = Integer.valueOf((String) weight1);
                            }
                            if (weight2 instanceof String) {
                                weight2 = Integer.valueOf((String) weight2);
                            }
				if ((Integer.class.isInstance(weight1))
						&& (Integer.class.isInstance(weight2))) {
					return new Integer(((Integer) weight1) + ((Integer) weight2));
				}
				break;
		}  // End switch (semiring)

		throw new IllegalArgumentException("multiplyWeights() failed.");
	}  // End protected static Object productOfWeights()
        
        
        public Rectangle getTransitionBound(Transition trans){
            mxCell edge=transitionToCell(trans);
            Rectangle rec=jgraphAutomata.getCellBound(edge);
            System.out.println(rec);
            return rec;
            
        }
        
        public boolean hasBackwardTransition(Transition tr){
            State source=tr.getSourceState();
            State target=tr.getTargetState();
            boolean hasbackward=false;
            List<Transition> transitions=source.getIncomingTransitions();
            for(Transition trans:transitions){
                if(trans.getSourceState()==target){
                    hasbackward=true;
                    break;
                }
            }
            return hasbackward; 
        }
   
        public Initial getInitial(State state){
            return state.getInitial();
        }
        public void setInitialWeight(State state,Object expression){
            
//            System.out.println("set initial! "+state.getName());
            Initial i=state.getInitial();
            if(i==null){
                i=new Initial();
                state.setInitial(i);
                // deal with jGraph
                i.setWeight(expression);
                mxCell Ini;
                Ini = jgraphAutomata.addInitial(stateToCell(state),i,getIniFinGeometricData(state,true));
                addToCellTable(i, Ini);
            }else{
                i.setWeight(expression);
                jgraphAutomata.setIniFinLabe(stateToCell(state), expression, true);
            }
        }
        public void removeInitial(State state){
            state.setInitial(null);
            jgraphAutomata.removeIniFin(stateToCell(state),true);
        }
        public Final getFinal(State state){
            return state.getFinal();
        }
        public void setFinalWeight(State state,Object expression){
            
            Final f=state.getFinal();
            
            if(f==null){
                f=new Final();
                state.setFinal(f);
                f.setWeight(expression);
            
                // deal with jGraph
                mxCell Fin;
                Fin = jgraphAutomata.addFinal(stateToCell(state), f,getIniFinGeometricData(state,false));
                addToCellTable(f, Fin);
            }
            else{
                f.setWeight(expression);
                jgraphAutomata.setIniFinLabe(stateToCell(state), expression, false);
            }
        }
        public void removeFinal(State state){
            state.setFinal(null);
            jgraphAutomata.removeIniFin(stateToCell(state),false);
        }
        
        public void setIniFinGeometricData(State state,IniFinGeometricData geodata,boolean isIni){
            if(isIni) state.getInitial().geodata=geodata;
            else state.getFinal().geodata=geodata;
            // deal with jGraph
            jgraphAutomata.setIniFinGeometricData(stateToCell(state),getIniFinGeometricData(state,isIni),isIni);
            
        }
        public void setIniFinDrawingData(State state,TransitionDrawingData tdd,boolean isIni){
            if(isIni) state.getInitial().drawdata=tdd;
            else state.getFinal().drawdata=tdd;
            
            jgraphAutomata.setIniFinDrawingData(stateToCell(state),tdd,isIni);
        }
        public void updateIniFinGeometricData(State state,Point2D termpoint,boolean isIni){
            StateGeometricData sgd=getStateGeometricData(state);
            IniFinGeometricData ifgd=(isIni)?state.getInitial().geodata:state.getFinal().geodata;

            double length=Math.sqrt((termpoint.getX()-sgd.getX())*(termpoint.getX()-sgd.getX())+(termpoint.getY()-sgd.getY())*((termpoint.getY()-sgd.getY())));
            double lengthRatio=length/(Math.sqrt(sgd.getWidth()*sgd.getWidth()+sgd.getHeight()*sgd.getHeight())/2)-1;
            double direction=Math.atan2(termpoint.getY()-sgd.getY(),termpoint.getX()-sgd.getX());
            ifgd.direction=-direction;
            ifgd.lengthRatio=lengthRatio;

            setIniFinGeometricData(state,ifgd,isIni);
        }
        public IniFinGeometricData getIniFinGeometricData(State state, boolean isIni){
            
            // TODO: compare with default style!
            if(isIni){
                
                if(state.getInitial()==null) return null;
                
                IniFinGeometricData igd=new IniFinGeometricData();
                IniFinGeometricData sigd=state.getInitial().geodata;
                IniFinGeometricData aigd=getAutomataIniFinGeometricData(true);
                
                //if(sigd==null) return aigd;
                
                
                if(sigd.direction==null) igd.direction=aigd.direction;
                else igd.direction=sigd.direction;
                
                if(sigd.lengthRatio==null) igd.lengthRatio=aigd.lengthRatio;
                else igd.lengthRatio=sigd.lengthRatio;
                
                if(sigd.labelOffset==null) igd.labelOffset=aigd.labelOffset;
                else igd.labelOffset=sigd.labelOffset;
                
                if(sigd.labelPosAndDist==null) igd.labelPosAndDist=aigd.labelPosAndDist;
                else igd.labelPosAndDist=sigd.labelPosAndDist;
                
                return igd;
            }else{
                
                if(state.getFinal()==null) return null;
                IniFinGeometricData fgd=new IniFinGeometricData();
                IniFinGeometricData sfgd=state.getFinal().geodata;
                IniFinGeometricData afgd=getAutomataIniFinGeometricData(false);
                
                //if(sfgd==null) return afgd;
                
                if(sfgd.direction==null) fgd.direction=afgd.direction;
                else fgd.direction=sfgd.direction;
                
                if(sfgd.lengthRatio==null) fgd.lengthRatio=afgd.lengthRatio;
                else fgd.lengthRatio=sfgd.lengthRatio;
                
                if(sfgd.labelOffset==null) fgd.labelOffset=afgd.labelOffset;
                else fgd.labelOffset=sfgd.labelOffset;
                
                if(sfgd.labelPosAndDist==null) fgd.labelPosAndDist=afgd.labelPosAndDist;
                else fgd.labelPosAndDist=sfgd.labelPosAndDist;
                
                return fgd;
            }
        }
        public IniFinGeometricData getAutomataIniFinGeometricData(boolean isIni){
            
            IniFinGeometricData fgd=new IniFinGeometricData();
            IniFinGeometricData afgd;
            IniFinGeometricData ffgd;
                        
            if(isIni){
                afgd=this.pmInitialGeometricData;
                ffgd=jgraphXInternalFrame.vgi.defaultInitialGeometricData;
            }else{
                afgd=this.pmFinalGeometricData;
                ffgd=jgraphXInternalFrame.vgi.defaultFinalGeometricData;
            }
            if(afgd.direction!=null) fgd.direction=afgd.direction;
            else fgd.direction=ffgd.direction;

            if(afgd.lengthRatio!=null) fgd.lengthRatio=afgd.lengthRatio;
            else fgd.lengthRatio=ffgd.lengthRatio;

            if(afgd.labelOffset!=null) fgd.labelOffset=afgd.labelOffset;
            else fgd.labelOffset=ffgd.labelOffset;

            if(afgd.labelPosAndDist!=null) fgd.labelPosAndDist=afgd.labelPosAndDist;
            else fgd.labelPosAndDist=ffgd.labelPosAndDist;

            return fgd;
           
        }
        public TransitionDrawingData getIniFinDrawingData(State state, boolean isIni){
            
            if(isIni){
                
                if(state.getInitial()==null) return null;

                TransitionDrawingData tdd=state.getInitial().drawdata;
                TransitionDrawingData atdd=getAutomataIniFinDrawingData(true);
            
                String strokeColor=tdd.getStrokeColor();
                if(strokeColor==null) strokeColor=atdd.getStrokeColor();

                float strokeWidth=tdd.getStrokeWidth();
                if(strokeWidth<0) strokeWidth=atdd.getStrokeWidth();

                String startArrow=tdd.getStartArrow();
                if(startArrow==null) startArrow=atdd.getStartArrow();

                String endArrow=tdd.getEndArrow();
                if(endArrow==null) endArrow=atdd.getEndArrow();

                return new TransitionDrawingData(strokeColor,strokeWidth,startArrow,endArrow);
              
            }else{
                
                if(state.getFinal()==null) return null;
                 
                TransitionDrawingData tdd=state.getInitial().drawdata;
                TransitionDrawingData atdd=getAutomataIniFinDrawingData(false);
            
                String strokeColor=tdd.getStrokeColor();
                if(strokeColor==null) strokeColor=atdd.getStrokeColor();

                float strokeWidth=tdd.getStrokeWidth();
                if(strokeWidth<0) strokeWidth=atdd.getStrokeWidth();

                String startArrow=tdd.getStartArrow();
                if(startArrow==null) startArrow=atdd.getStartArrow();

                String endArrow=tdd.getEndArrow();
                if(endArrow==null) endArrow=atdd.getEndArrow();

                return new TransitionDrawingData(strokeColor,strokeWidth,startArrow,endArrow);
            }
        }
        public TransitionDrawingData getAutomataIniFinDrawingData(boolean isIni){
            if(isIni)
                return new TransitionDrawingData("#FF0000",2,"none","open");
            else
                return new TransitionDrawingData("#FF0000",2,"open","none");
            
        }
        public Rectangle computeBox(List<State> states){
            
            if(states.isEmpty()) return null;
            
            double left,top;
            double right=0,bottom=0;
            StateGeometricData sgd=this.getStateGeometricData(states.get(0));
            left=sgd.getX()-sgd.getWidth()/2; top=sgd.getY()-sgd.getHeight()/2;
            right=left+sgd.getWidth()/2; bottom=top+sgd.getHeight()/2;

            int len=states.size();

            for(int i=0;i<len;++i){
                State state=states.get(i);
                sgd=this.getStateGeometricData(state);

    //            System.out.println("box: "+left+" "+right+" "+bottom+" "+top);
    //            System.out.println("add state: "+sgd.getX()+" "+sgd.getY()+" "+sgd.getWidth()+" "+sgd.getHeight());

                left=Math.min(left,sgd.getX()-sgd.getWidth()/2);
                top=Math.min(top,sgd.getY()-sgd.getHeight()/2);

                right=Math.max(right,sgd.getX()+sgd.getWidth()/2);
                bottom=Math.max(bottom,sgd.getY()+sgd.getHeight()/2);

            }
        
            return new Rectangle((int)left,(int)top,(int)(right-left),(int)(bottom-top));
        }
        
        
       /*
        * new group methods
        * 
        */ 
        public void groupObjects(List<Object> objs){
            group.groupObjs(objs);
            setSelectedObjs(group.retrieveSelectedObjs(objs));
        }
        public void unGroupObjects(List<Object> objs){
            group.unGroupObjs(objs);
            setSelectedObjs(group.retrieveSelectedObjs(objs));
        }    
        /*
         * put states into a layout group, 
         * when applying global layouts, local layouts are preserved
         */
        public void groupVertices(List<State> states){
            
            
            // TODO: check if the selected state is already in some vertex group
            
            
            int len=states.size();
//            Object[] cells=new Object[len];
            List<State> statesToAdd=new ArrayList<State>();
            for(int i=0;i<len;++i){
//                cells[i]=stateToCell(states.get(i));
                statesToAdd.add(states.get(i));
            }
            
            
            if(groupList==null) groupList=new ArrayList<VertexGroup>();
            VertexGroup vg=new VertexGroup(statesToAdd);
            groupList.add(vg);
            
            System.out.println("create vertexGroup: "+vg.getStateList());
            
            int style=VertexGroup.LINEAR_GROUP;
            double y=statesToAdd.get(0).getGeometricData().getY();
            // add 3 to stroke width to represent a group, for now 
            for(State state:statesToAdd){
                StateDrawingData dd=state.getDrawingData();
                dd.setStrokeWidth(getStateDrawingData(state).getStrokeWidth()+3);
                setStateDrawingData(state,dd);
                
                // set groupID
                state.setGroupID(groupList.size()-1);
                
                // check group layout style
                if(state.getGeometricData().getY()!=y) style=VertexGroup.CIRCULAR_GROUP;
            }
            
            // set vertexgroup style
            vg.setLayoutType(style);
            
            
//            jgraphAutomata.groupCells(cells); // do nothing for now
            jgraphAutomata.graph.clearSelection();
            setSelectedStates(states);
        }
        
        public void ungroupVertices(List<State> states){
            
            // find the vertexGroup and delete it!
            if(groupList==null) return;
            for(VertexGroup vgit:groupList){
                if(vgit.getStateList()==states){
                   groupList.remove(vgit); break;
                }
            }
                        
            //remove 3 stroke width 
            for(State state:states){
                StateDrawingData dd=state.getDrawingData();
                dd.setStrokeWidth(dd.getStrokeWidth()-3);
                setStateDrawingData(state,dd);
                
                // reset groupID
                state.setGroupID(-1);
            }
//            jgraphAutomata.ungroupCells(cells); // do nothing for now
            
            
        }
        
//        public List<State> getReplaceStateList(){
//            GroupReplacedAutomata replaceAutomata=new GroupReplacedAutomata(this);
//            List<State> replaceList=replaceAutomata.getRepresentStateList();
//            System.out.println("--- in group:");
//            for(State st:replaceList){
//                System.out.println(st.getName());
//                List<Transition> trans=st.getTransitions();
//                for(Transition tr:trans){
//                    System.out.println("   "+tr.getSourceState().getName()+" -> "+tr.getTargetState().getName());
//                }
//            }
//            return replaceList;
//        }
        public boolean selectedStatesContainVertexGroup() {
           return false;
           //automatically set to false
           //2014/1/18
           
           
//            if(groupList==null) return false;
//            else return true;
            /*
            boolean thereisgroup=false;
            boolean thereisnongroup=false;
            for(State state:selectedStates){
                if(state.getGroupID()>-1) thereisgroup=true;
                if(state.getGroupID()==-1) thereisnongroup=true;
            }
            if(thereisgroup && thereisnongroup)
                return true;
            
            return false;
            */
        }
        public List<VertexGroup> getGroupList(){
            if(groupList!=null)
                for(VertexGroup vg:groupList){
                    System.out.println("group: "+vg.getStateList());
                }
            return groupList;
        }
        
        /*
         * generate states randomly for experiments
         */
        public void randomGenerateStates(int num){
            for(int i=0;i<num;++i){
                addState(new Point2D.Double((i)*50,0));
            }
            int edgeNum=(int)(Math.random()*(double)num*(num-1)/4)+num;
            int stateNum=pmAllStates.size();
            for(int i=0;i<edgeNum;++i){
                State s;State t;
                
                    s=pmAllStates.get((int)(Math.random()*(double)stateNum));
                    t=pmAllStates.get((int)(Math.random()*(double)stateNum));
                    if(t==s) t=pmAllStates.get((int)(Math.random()*(double)stateNum));
                    
                    boolean exist=false;
                    for(Transition tr: pmAllTransitions){
                        if(tr.getSourceState()==s && tr.getTargetState()==t){
                            exist=true;break;
                    }
                }
                if(!exist)
                    addTransition(s,t);
            
            }
            // check if there are non-connected states
            for(State st:pmAllStates){
                if(st.getTransitions().size()==0){
                    State t=pmAllStates.get((int)(Math.random()*(double)stateNum));
                    if(t==st) t=pmAllStates.get((int)(Math.random()*(double)stateNum));
                    addTransition(st,t);
                }
            }
        }
        
}  // End public class Automata implements AutomataInterface
