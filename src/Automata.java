/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author wkren
 */
public class Automata implements AutomataInterface{

	/**
	 * @return the geometryData
	 */
//	public Geometry getGeometryData() {
//		return geometricData;
//	}

	/**
	 * @param geometryData the geometryData to set
	 */
//	public void setGeometryData(Point2D x, Point2D y) {
//		this.geometricData.x = x;
//		this.geometricData.y = y;
//	}

	/**
	 * @return the drawingData
	 */
//	public Drawing getDrawingData() {
//		return drawingData;
//	}

	/**
	 * @param drawingData the drawingData to set
	 */
//	public void setDrawingData(Drawing drawingData) {
//		this.drawingData = drawingData;
//	}

	/**
	 * @return the Type
	 */
	public Type getType() {
		return this.pmType;
	}

	public List<Object> getAlphabet() {
		return this.pmAlphabet;
	}

	public void setAlphabet(List<Object> alphabet) {
		this.pmAlphabet = alphabet;
	}

	public List<Object> getSecondAlphabet() {
		return this.pmSecondAlphabet;
	}

	public void setSecondAlphabet(List<Object> alphabet) {
		this.pmSecondAlphabet = alphabet;
	}

	/**
	 * @param valueype the valueType to set
	 */
//	public void setValueType(Type valueType) {
//		this.valueType = valueType;
//	}

	

	/**
	 * @return the states
	 */
	public List<State> getAllStates() {
		return this.pmAllStates;
	}

	public State getState(int index) {
		return this.pmAllStates.get(index);
	}

	/**
	 * @param states the states to set
	 */
	public void setAllStates(List<State> allStates) {
		this.pmAllStates = allStates;
	}

	public void addState(State s) {
		this.pmAllStates.add(s);
	}

	public void removeState(State state) {
		this.pmAllStates.remove(state);
	}

	/**
	 * @return the transitions
	 */
	public List<Transition> getAllTransitions() {
		return this.pmAllTransitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setAllTransitions(List<Transition> transitions) {
		this.pmAllTransitions = transitions;
	}

	public void addTransition(Transition t) {
		this.pmAllTransitions.add(t);
	}

	public void removeTransition(Transition transition) {
		this.pmAllTransitions.remove(transition);
	}

//	public class Geometry {
//
//		Point2D x;
//		Point2D y;
//	}
//	private Geometry geometricData;
//	private Drawing drawingData;
	private Type pmType;
	private List<Object> pmAlphabet;
	private List<Object> pmSecondAlphabet;
	private List<State> pmAllStates;
	private List<Transition> pmAllTransitions;

	public Automata() {
//		geometricData = new Geometry();
//		drawingData = new Drawing();
		this.pmType = new Type();
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
	}

	public Automata(Type type) {
		this.pmType = type;
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
	}

	public static void main(String args[]) {
		System.out.println("Staring from 'Automata Class'");
		System.out.println("Creating Monoid");
		Monoid monoid = new Monoid();
		System.out.println("Creating Semiring");
		Semiring semiring = new Semiring("1", "0",
				Semiring.SemiringType.NUMERICAL,
				Semiring.SemiringSet.B,
				Semiring.SemiringOperation.CLASSICAL,
				"1", null, null);
		System.out.println("Creating Type");
		Type type = new Type();
		System.out.println("Creating empty Automata");
		Automata automata = new Automata();
		System.out.println("Creating Automata with TYPE");
		Automata automataWithType = new Automata(type);
	}
}
