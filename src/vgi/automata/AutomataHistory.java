/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import vgi.automata.Transition;
import vgi.automata.State;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author JLiu
 */
public class AutomataHistory {

	public HashMap<State, List<State>> newToOldStatesMap;
	public HashMap<State, List<State>> oldToNewStatesMap;
	public HashMap<Transition, List<Transition>> newToOldTransitionsMap;
	public HashMap<Transition, List<Transition>> oldToNewTransitionsMap;
}  // End public class AutomataHistory
