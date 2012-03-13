package vgi;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	public void addState(State state) {
		pmAllStates.add(state);
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
		transition.getSourceState().addTransition(transition);
		transition.getTargetState().addTransition(transition);
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
			if (state.getInitialWeight() != null) {
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
			if (state.getFinalWeight() != null) {
				arrayList.add(state);
			}
		}  // End while (iterateStates.hasNext())
		return arrayList;
	}
	private String pmName;
	private WritingData pmWritingData;
	private Weight pmWeight;
	private Alphabet pmAlphabet;
	private Alphabet pmOutputAlphabet;
	private List<State> pmAllStates;
	private List<Transition> pmAllTransitions;

	public Automata() {
		this.pmName = null;
		this.pmWritingData = null;
		this.pmWeight = null;
		this.pmAlphabet = null;
		this.pmOutputAlphabet = null;
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
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

	public static Automata accessible(Automata automata) {

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
		outputAutomaton.setName(automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, State> mapOldToNewStates = new HashMap<State, State>();

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (!(accessibleStates.contains(state))) {
				continue;
			}
			State newState = new State();
			newState.setName(state.getName());
			newState.setInitialWeight(state.getInitialWeight());
			newState.setFinalWeight(state.getFinalWeight());
			newState.setGeometricData(null);
			ArrayList arrayList = new ArrayList();
			arrayList.add(state);
			newState.setHistory(arrayList);
			arrayList = null;
			outputAutomaton.addState(newState);
			mapOldToNewStates.put(state, newState);
			newState = null;
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			if ((!(accessibleStates.contains(transition.getSourceState())))
					|| (!(accessibleStates.contains(transition.getTargetState())))) {
				continue;
			}
			Transition newTransition = new Transition();
			newTransition.setSourceState(mapOldToNewStates.get(transition.getSourceState()));
			newTransition.setTargetState(mapOldToNewStates.get(transition.getTargetState()));
			newTransition.setLabel(transition.getLabel());
			newTransition.setGeometricData(null);
			outputAutomaton.addTransition(newTransition);
			newTransition = null;
		}  // End while (iterateTransitions.hasNext())

		mapOldToNewStates = null;

		return outputAutomaton;
	}  // End public static Automata accessible(Automata automata)

	public static Automata coaccessible(Automata automata) {

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
		outputAutomaton.setName(automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, State> mapOldToNewStates = new HashMap<State, State>();

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			if (!(coaccessibleStates.contains(state))) {
				continue;
			}
			State newState = new State();
			newState.setName(state.getName());
			newState.setInitialWeight(state.getInitialWeight());
			newState.setFinalWeight(state.getFinalWeight());
			newState.setGeometricData(null);
			ArrayList arrayList = new ArrayList();
			arrayList.add(state);
			newState.setHistory(arrayList);
			arrayList = null;
			outputAutomaton.addState(newState);
			mapOldToNewStates.put(state, newState);
			newState = null;
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			if ((!(coaccessibleStates.contains(transition.getSourceState())))
					|| (!(coaccessibleStates.contains(transition.getTargetState())))) {
				continue;
			}
			Transition newTransition = new Transition();
			newTransition.setSourceState(mapOldToNewStates.get(transition.getSourceState()));
			newTransition.setTargetState(mapOldToNewStates.get(transition.getTargetState()));
			newTransition.setLabel(transition.getLabel());
			newTransition.setGeometricData(null);
			outputAutomaton.addTransition(newTransition);
			newTransition = null;
		}  // End while (iterateTransitions.hasNext())

		mapOldToNewStates = null;

		return outputAutomaton;
	}  // End public static Automata coaccessible(Automata automata)
}  // End public class Automata implements AutomataInterface
