/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import vgi.automata.Transition;
import vgi.automata.TransitionInterface;
import vgi.automata.StateInterface;
import vgi.automata.State;
import vgi.automata.Automata;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
			outState.setInitial(state.getInitial());
			outState.setFinal(state.getFinal());
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
//			outState.setInitialWeight(state.getInitialWeight());
//			outState.setFinalWeight(state.getFinalWeight());
                        outState.setInitial(state.getInitial());
			outState.setFinal(state.getFinal());
			List<State> oldStatesList = history.newToOldStatesMap.get(state);
			if ((oldStatesList == null) || (oldStatesList.size() < 2)) {
				throw new IllegalArgumentException("Automata history is missing data for this state, " + state.toString());
			}
			State oldState = oldStatesList.get(0);
			StateGeometricData geometricData = oldState.getGeometricData();
			if ((geometricData == null) || (geometricData.getLocation() == null)) {
				throw new IllegalArgumentException("A state from the first input automaton is missing geometric data.");
			}
			Point2D.Double location = new Point2D.Double();
			location.x = geometricData.getLocation().getX();
			oldState = oldStatesList.get(1);
			geometricData = oldState.getGeometricData();
			if ((geometricData == null) || (geometricData.getLocation() == null)) {
				throw new IllegalArgumentException("A state from the second input automaton is missing geometric data.");
			}
			location.y = geometricData.getLocation().getY();
			geometricData = new StateGeometricData();
			geometricData.setLocation(location);
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
			State sourceState = transition.getSourceState();
			State targetState = transition.getTargetState();
			outTransition.setSourceState(inToOutStatesMap.get(sourceState));
			outTransition.setTargetState(inToOutStatesMap.get(targetState));
			outTransition.setLabel(transition.getLabel());
			outTransition.setGeometricData(null);
			outputAutomaton.addTransition(outTransition);

			if (history.newToOldTransitionsMap == null) {
				outTransition = null;  // Transition outTransition = new Transition();
				continue;
			}
			List<Transition> oldTransitionsList = history.newToOldTransitionsMap.get(transition);
			if ((oldTransitionsList == null) || (oldTransitionsList.size() < 2)) {
				outTransition = null;  // Transition outTransition = new Transition();
				continue;
			}

			List<State> oldSourceStates = history.newToOldStatesMap.get(sourceState);
			List<State> oldTargetStates = history.newToOldStatesMap.get(targetState);
			Transition oldTransition;
			double oldToNewOffsetX;
			double oldToNewOffsetY;
			if (oldSourceStates.get(1) == oldTargetStates.get(1)) {
				//
				// Vertical or loop transitions
				//
				oldTransition = oldTransitionsList.get(0);
				oldToNewOffsetX = outTransition.getSourceState().getGeometricData().getLocation().getX() - oldSourceStates.get(0).getGeometricData().getLocation().getX();
				oldToNewOffsetY = outTransition.getSourceState().getGeometricData().getLocation().getY() - oldSourceStates.get(0).getGeometricData().getLocation().getY();
			} else if (oldSourceStates.get(0) == oldTargetStates.get(0)) {
				//
				// Horizontal transitions
				//
				oldTransition = oldTransitionsList.get(1);
				oldToNewOffsetX = outTransition.getSourceState().getGeometricData().getLocation().getX() - oldSourceStates.get(1).getGeometricData().getLocation().getX();
				oldToNewOffsetY = outTransition.getSourceState().getGeometricData().getLocation().getY() - oldSourceStates.get(1).getGeometricData().getLocation().getY();
			} else {
				//
				// Diagonal transitions
				//
				outTransition = null;  // Transition outTransition = new Transition();
				continue;
			}

			TransitionGeometricData oldGeometricData = oldTransition.getGeometricData();
			TransitionGeometricData geometricData = new TransitionGeometricData();
			geometricData.labelPosAndDist = oldGeometricData.labelPosAndDist;
			geometricData.labelOffset = oldGeometricData.labelOffset;
			geometricData.controlPoints = new ArrayList<Point2D>();
			Iterator<Point2D> iteratePoints = oldGeometricData.controlPoints.iterator();
			while (iteratePoints.hasNext()) {
				Point2D oldPoint = iteratePoints.next();
				geometricData.controlPoints.add(new Point2D.Double(oldPoint.getX() + oldToNewOffsetX, oldPoint.getY() + oldToNewOffsetY));
			}  // End while (iteratePoints.hasNext())
			outTransition.setGeometricData(geometricData);
			geometricData = null;  // TransitionInterface.GeometricData geometricData = new TransitionInterface.GeometricData();

			outTransition = null;  // Transition outTransition = new Transition();
		}  // End while (iterateTransitions.hasNext())

		inToOutStatesMap = null;  // HashMap<State, State> inToOutStatesMap = new HashMap<State, State>();

		return outputAutomaton;
	}  // End public static Automata productGrid(Automata automata, AutomataHistory history)
}  // End public class Layout
