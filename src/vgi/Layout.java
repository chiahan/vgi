/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author JLiu
 */
public class Layout {

	public static Automata keepTheSame(Automata automata, AutomataHistory history) {

		if ((automata == null)
				|| (automata.getWritingData() == null)
				|| (automata.getWeight() == null)
				|| (automata.getAlphabet() == null)
				|| (history == null)
				|| (history.newToOldStatesMap == null)
				|| (history.newToOldStatesMap.isEmpty())) {
			throw new IllegalArgumentException("Invalid automata or history inputs to the layout algorithm.");
		}

		Automata outputAutomaton = new Automata();
		outputAutomaton.setName(automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, State> inToOutStatesMap = new HashMap<State, State>();

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			State outState = new State();
			outState.setName(state.getName());
			outState.setInitialWeight(state.getInitialWeight());
			outState.setFinalWeight(state.getFinalWeight());
			List<State> oldStatesList = history.newToOldStatesMap.get(state);
			if ((oldStatesList != null) && !(oldStatesList.isEmpty())) {
				State oldState = oldStatesList.get(0);
				outState.setGeometricData(oldState.getGeometricData());
			} else {
				outState.setGeometricData(null);
			}
			outputAutomaton.addState(outState);
			inToOutStatesMap.put(state, outState);
			outState = null;  // State outState = new State();
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			Transition outTransition = new Transition();
			outTransition.setSourceState(inToOutStatesMap.get(transition.getSourceState()));
			outTransition.setTargetState(inToOutStatesMap.get(transition.getTargetState()));
			outTransition.setLabel(transition.getLabel());
			outTransition.setGeometricData(null);
			if ((history.newToOldTransitionsMap != null) && !(history.newToOldTransitionsMap.isEmpty())) {
				List<Transition> oldTransitionsList = history.newToOldTransitionsMap.get(transition);
				if ((oldTransitionsList != null) && !(oldTransitionsList.isEmpty())) {
					Transition oldTransition = oldTransitionsList.get(0);
					if (oldTransition != null) {
						outTransition.setGeometricData(oldTransition.getGeometricData());
					}
				}  // End if ((oldTransitionsList != null) && !(oldTransitionsList.isEmpty()))
			}  // End if ((history.newToOldTransitionsMap != null) && !(history.newToOldTransitionsMap.isEmpty()))
			outputAutomaton.addTransition(outTransition);
			outTransition = null;  // Transition outTransition = new Transition();
		}  // End while (iterateTransitions.hasNext())

		inToOutStatesMap = null;  // HashMap<State, State> inToOutStatesMap = new HashMap<State, State>();

		return outputAutomaton;
	}  // End public static Automata keepTheSame(Automata automata, AutomataHistory history)

	public static Automata productGrid(Automata automata, AutomataHistory history) {

		if ((automata == null)
				|| (automata.getWritingData() == null)
				|| (automata.getWeight() == null)
				|| (automata.getAlphabet() == null)
				|| (history == null)
				|| (history.newToOldStatesMap == null)
				|| (history.newToOldStatesMap.isEmpty())) {
			throw new IllegalArgumentException("Invalid automata or history inputs to the layout algorithm.");
		}

		Automata outputAutomaton = new Automata();
		outputAutomaton.setName(automata.getName());
		outputAutomaton.setWritingData(automata.getWritingData());
		outputAutomaton.setWeight(automata.getWeight());
		outputAutomaton.setAlphabet(automata.getAlphabet());
		outputAutomaton.setOutputAlphabet(automata.getOutputAlphabet());
		HashMap<State, State> inToOutStatesMap = new HashMap<State, State>();

		Iterator<State> iterateStates = automata.getAllStates().iterator();
		while (iterateStates.hasNext()) {
			State state = iterateStates.next();
			State outState = new State();
			outState.setName(state.getName());
			outState.setInitialWeight(state.getInitialWeight());
			outState.setFinalWeight(state.getFinalWeight());
			List<State> oldStatesList = history.newToOldStatesMap.get(state);
			if ((oldStatesList == null) || (oldStatesList.size() < 2)) {
				throw new IllegalArgumentException("Automata history is missing data for this state, " + state.toString());
			}
			State oldState = oldStatesList.get(0);
			StateInterface.GeometricData geometricData = oldState.getGeometricData();
			if ((geometricData == null) || (geometricData.location == null)) {
				throw new IllegalArgumentException("A state from the first input automaton is missing geometric data.");
			}
			Point2D.Double location = new Point2D.Double();
			location.x = geometricData.location.getX();
			oldState = oldStatesList.get(1);
			geometricData = oldState.getGeometricData();
			if ((geometricData == null) || (geometricData.location == null)) {
				throw new IllegalArgumentException("A state from the second input automaton is missing geometric data.");
			}
			location.y = geometricData.location.getY();
			geometricData = new StateInterface.GeometricData();
			geometricData.location = location;
			outState.setGeometricData(geometricData);
			geometricData = null;  // geometricData = new StateInterface.GeometricData();
			location = null;  // Point2D.Double location = new Point2D.Double();
			outputAutomaton.addState(outState);
			inToOutStatesMap.put(state, outState);
			outState = null;  // State outState = new State();
		}  // End while (iterateStates.hasNext())

		Iterator<Transition> iterateTransitions = automata.getAllTransitions().iterator();
		while (iterateTransitions.hasNext()) {
			Transition transition = iterateTransitions.next();
			Transition outTransition = new Transition();
			outTransition.setSourceState(inToOutStatesMap.get(transition.getSourceState()));
			outTransition.setTargetState(inToOutStatesMap.get(transition.getTargetState()));
			outTransition.setLabel(transition.getLabel());
			outTransition.setGeometricData(null);
			outputAutomaton.addTransition(outTransition);
			outTransition = null;  // Transition outTransition = new Transition();
		}  // End while (iterateTransitions.hasNext())

		inToOutStatesMap = null;  // HashMap<State, State> inToOutStatesMap = new HashMap<State, State>();

		return outputAutomaton;
	}  // End public static Automata productGrid(Automata automata, AutomataHistory history)
}  // End public class Layout
