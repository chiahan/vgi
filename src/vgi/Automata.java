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
		String name = state.getName();
		if (name == null) {
			state.setName("s" + Integer.toString(counter++));
		}
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
		State sourceState = transition.getSourceState();
		sourceState.addTransition(transition);
		State targetState = transition.getTargetState();
		if (!(targetState.equals(sourceState))) {
			targetState.addTransition(transition);
		}
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
	private int counter;

	public Automata() {
		this.pmName = null;
		this.pmWritingData = null;
		this.pmWeight = null;
		this.pmAlphabet = null;
		this.pmOutputAlphabet = null;
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
		this.counter = 0;
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
			optionalHistory.transitionsMap = new HashMap<Transition, Transition>();
		}

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
				optionalHistory.transitionsMap.put(transition, newTransition);
				optionalHistory.transitionsMap.put(newTransition, transition);
			}
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
			optionalHistory.transitionsMap = new HashMap<Transition, Transition>();
		}

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
				optionalHistory.transitionsMap.put(transition, newTransition);
				optionalHistory.transitionsMap.put(newTransition, transition);
			}
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
			optionalHistory.transitionsMap = new HashMap<Transition, Transition>();
		}

		//
		// Copy all the states from the original automaton to the new automaton.
		// Use history of states and a HashMap to connect the original and the new states.
		//
		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			State newState = new State();
			newState.setName(state.getName());
			newState.setInitialWeight(state.getInitialWeight());
			newState.setFinalWeight(state.getFinalWeight());
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
					optionalHistory.transitionsMap.put(transition, newTransition);
					optionalHistory.transitionsMap.put(newTransition, transition);
				}
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
					optionalHistory.transitionsMap.put(transition, newTransition);
					optionalHistory.transitionsMap.put(newTransition, transition);
				}
				newTransition = null;  // Transition newTransition = new Transition();
			}  // End while (iterateTransitions.hasNext())

			for (int index = 0; index < epsilonClosure.size(); index++) {
				State closureState = epsilonClosure.get(index);

				//
				// Make the new state final if its epsilon closure has a final state.
				//
				if ((closureState.getFinalWeight() != null)
						&& (newState.getFinalWeight() == null)) {
					newState.setFinalWeight(closureState.getFinalWeight());
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
			optionalHistory.transitionsMap = null;
		}

		int count1 = firstInput.getAllStates().size();
		int count2 = secondInput.getAllStates().size();
		for (int index2 = 0; index2 < count2; index2++) {

			for (int index1 = 0; index1 < count1; index1++) {

				State state1 = firstInput.getAllStates().get(index1);
				stateToInt1.put(state1, index1);
				State state2 = secondInput.getAllStates().get(index2);
				stateToInt2.put(state2, index2);
				State newState = new State();
				InitialFinalWeight initialWeight1 = state1.getInitialWeight();
				InitialFinalWeight initialWeight2 = state2.getInitialWeight();
				InitialFinalWeight finalWeight1 = state1.getFinalWeight();
				InitialFinalWeight finalWeight2 = state2.getFinalWeight();
				if ((initialWeight1 != null) && (initialWeight2 != null)) {
					InitialFinalWeight newWeightValue = new InitialFinalWeight();
					newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, initialWeight1.getValue(), initialWeight2.getValue()));
					newState.setInitialWeight(newWeightValue);
					newWeightValue = null;  // InitialFinalWeight newWeightValue = new InitialFinalWeight();
				}  // End if ((initialWeight1 != null) && (initialWeight2 != null))
				if ((finalWeight1 != null) && (finalWeight2 != null)) {
					InitialFinalWeight newWeightValue = new InitialFinalWeight();
					newWeightValue.setValue(multiplyWeights(outputAutomaton.getWeight().semiring, finalWeight1.getValue(), finalWeight2.getValue()));
					newState.setFinalWeight(newWeightValue);
					newWeightValue = null;  // InitialFinalWeight newWeightValue = new InitialFinalWeight();
				}  // End if ((finalWeight1 != null) && (finalWeight2 != null))
				newState.setGeometricData(null);
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
					newTransition.setGeometricData(null);
					outputAutomaton.addTransition(newTransition);
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
				if ((Boolean.class.isInstance(weight1))
						&& (((Boolean) weight1) == true)
						&& (Boolean.class.isInstance(weight2))
						&& (((Boolean) weight2) == true)) {
					return new Boolean(true);
				}
				break;
			case Z_INTEGER:
				if ((Integer.class.isInstance(weight1))
						&& (Integer.class.isInstance(weight2))) {
					return new Integer(((Integer) weight1) * ((Integer) weight2));
				}
				break;
			case Q_RATIONAL:
			case R_REAL:
				if ((Double.class.isInstance(weight1))
						&& (Double.class.isInstance(weight2))) {
					return new Double(((Double) weight1) * ((Double) weight2));
				}
				break;
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				if ((Integer.class.isInstance(weight1))
						&& (Integer.class.isInstance(weight2))) {
					return new Integer(((Integer) weight1) + ((Integer) weight2));
				}
				break;
		}  // End switch (semiring)

		throw new IllegalArgumentException();
	}  // End protected static Object productOfWeights()
}  // End public class Automata implements AutomataInterface
