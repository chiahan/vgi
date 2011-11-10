//modified by polar 2009/04/21

package model;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Vector;

/**
 * defines data structure related to graphical properties of automata
 * provides interfaces used for controlling several graphical aspects of automata
 * @author polar
 */
public class Drawing2 {

	public void setAutomaton(Vector<Automaton> automatonV) {
		this.fsmxml.automatonV = automatonV;
	}

	public Vector<Automaton> getAutomaton() {
		return this.fsmxml.automatonV;
	}

	public AutomatonStruct getAutomatonStruct() {
		return this.fsmxml.automatonV.get(0).automatonStruct;
	}

	public void setAutomatonStruct(AutomatonStruct automatonStruct) {
		this.fsmxml.automatonV.get(0).automatonStruct = automatonStruct;
	}

	//**************************************************************************
	//********************  State related handlers   ***************************
	//**************************************************************************
	public Hashtable getStateH() {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH;
	}

	public void setStateH(Hashtable stateH) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH = stateH;
	}

	public State getState(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id);
	}

	public void addState(String id) {
		State state = new State(id);
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.put(id, state);
	}

	public void addState(String id, State state) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.put(id, state);
	}

	public void removeState(State state) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.remove(state.id);
	}

	public void removeState(String stateID) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.remove(stateID);
	}

	public StateDrawingData getStateDrawingData(String stateID) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(stateID).stateDrawingData;
	}

	public void setStateDrawingData(String stateID, StateDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(stateID)
				.stateDrawingData = drawingData; }

	public int getStateSize(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.size;
	}
	
	public void setStateSize(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.size = para;
	}
		
	public int getStateVisualType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.visualType;
	}
	
	public void setStateVisualType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.visualType = para;
	}
		
	public int getStateType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.stateType;
	}
	
	public void setStateType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.stateType = para;
	}		
	
	public int getStateTextColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.textColor;
	}
	
	public void setStateTextColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.textColor = para;
	}		
	
	public int getStateBGColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.bgColor;
	}
	
	public void setStateBGColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.bgColor = para;
	}

	public int getStateBorderColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.borderColor;
	}

	public void setStateBorderColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.states.stateH.get(id).stateDrawingData
				.borderColor = para;
	}

	//**************************************************************************
	//*********************  global state related handlers  ********************
	//**************************************************************************
	public StateDrawingData getGeneralStateDrawingData(String stateID) {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData;
	}

	public void setGeneralStateDrawingData(String stateID, StateDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData = drawingData;
	}

	public int getGeneralStateSize() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.size;
	}

	public void setGeneralStateSize(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.size = para;
	}

	public int getGeneralStateVisualType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.visualType;
	}

	public void setGeneralStateVisualType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.visualType = para;
	}

	public int getGeneralStateType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.stateType;
	}

	public void setGeneralStateType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.stateType = para;
	}

	public int getGeneralStateTextColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.textColor;
	}

	public void setGeneralStateTextColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.textColor = para;
	}

	public int getGeneralStateBGColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.bgColor;
	}

	public void setGeneralStateBGColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.bgColor = para;
	}

	public int getGeneralStateBorderColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.borderColor;
	}

	public void setGeneralStateBorderColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.stateDrawingData
				.borderColor = para;
	}

	//**************************************************************************
	//**********************  Transition related handerls   ********************
	//**************************************************************************
	public Hashtable getTransitionH() {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH;
	}

	public void setTransitionH(Hashtable transH) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH = transH;
	}

	public void addTransition(String id) {
		Transition trans = new Transition(id);
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.put(id, trans);
	}

	public void addTransition(String id, Transition trans) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.put(id, trans);
	}

	public void removeTransition(Transition trans) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.remove(trans.id);
	}

	public void removeTransition(String id) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.remove(id);
	}

	public Transition getTransition(String transID) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(transID);
	}

	public void setTransition(String transID, Transition trans) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.put(transID, trans);
	}

	public TransitionDrawingData getTransitionDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id)
				.transitionDrawingData;
	}

	public void setTransitionDrawingData(String id, TransitionDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id)
				.transitionDrawingData = drawingData;
	}

	public int getTransitionType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.transitionType; }

	public void setTransitionType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.transitionType = para; }

	public Vector<Point2D.Float> getTransitionControlPoints(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.controlPoints; }

	public void setTransitionControlPoints(String id, Vector<Point2D.Float> para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.controlPoints = para; }

	public void addTransitionControlPoint(String id, Point2D.Float point) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.controlPoints.add(point); }

	public int getTransitionLineType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineType; }

	public void setTransitionLineType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineType = para; }

	public int getTransitionLineWidth(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineWidth; }

	public void setTransitionLineWidth(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineWidth = para; }

	public int getTransitionLineColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineColor; }

	public void setTransitionLineColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.lineColor = para; }

	public int getTransitionArrowType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.arrowType; }

	public void setTransitionArrowType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.arrowType = para; }

	public int getTransitionArrowSize(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.arrowSize; }

	public void setTransitionArrowSize(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(id).transitionDrawingData
				.arrowSize = para; }

	//**************************************************************************
	//*******************  global Transition related handlers  *****************
	//**************************************************************************
	public TransitionDrawingData getGeneralTransitionDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData;
	}

	public void setGeneralTransitionDrawingData(String id, TransitionDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData = drawingData;
	}

	public int getGeneralTransitionType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.transitionType; }

	public void setGeneralTransitionType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.transitionType = para; }

	public int getGeneralTransitionLineType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineType; }

	public void setGeneralTransitionLineType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineType = para; }

	public int getGeneralTransitionLineWidth() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineWidth; }

	public void setGeneralTransitionLineWidth(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineWidth = para; }

	public int getGeneralTransitionLineColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineColor; }

	public void setGeneralTransitionLineColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.lineColor = para; }

	public int getGeneralTransitionArrowType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.arrowType; }

	public void setGeneralTransitionArrowType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.arrowType = para; }

	public int getGeneralTransitionArrowSize() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.arrowSize; }

	public void setGeneralTransitionArrowSize(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.transitionDrawingData
				.arrowSize = para; }

	//**************************************************************************
	//**********************  Initial related handlers  ************************
	//**************************************************************************
	public void addInitial(String id) {
		Initial initial = new Initial(id);
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.put(id, initial);
	}

	public void addInitial(String id, Initial initial) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.put(id, initial);
	}

	public void removeInitial(Initial initial) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.remove(initial.id);
	}

	public void removeInitial(String id) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.remove(id);
	}

	public Initial getInitial(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id);
	}

	public void setInitial(String id, Initial initial) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.put(id, initial);
	}

	public InitialDrawingData getInitialDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id)
				.initialDrawingData;
	}

	public void setInitialDrawingData(String id, InitialDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id)
				.initialDrawingData = drawingData;
	}

	public int getInitialLineType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineType; }

	public void setInitialLineType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineType = para; }

	public int getInitialLineWidth(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineWidth; }

	public void setInitialLineWidth(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineWidth = para; }

	public int getInitialLineColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineColor; }

	public void setInitialLineColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.lineColor = para; }

	public int getInitialArrowType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.arrowType; }

	public void setInitialArrowType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.arrowType = para; }

	public int getInitialArrowSize(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.arrowSize; }

	public void setInitialArrowSize(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.initialH.get(id).initialDrawingData
				.arrowSize = para; }

	//**************************************************************************
	//********************  global Initial related handlers  *******************
	//**************************************************************************
	public InitialDrawingData getGeneralInitialDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData;
	}

	public void setGeneralInitialDrawingData(String id, InitialDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData = drawingData;
	}

	public int getGeneralInitialLineType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineType; }

	public void setGeneralInitialLineType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineType = para; }

	public int getGeneralInitialLineWidth() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineWidth; }

	public void setGeneralInitialLineWidth(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineWidth = para; }

	public int getGeneralInitialLineColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineColor; }

	public void setGeneralInitialLineColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.lineColor = para; }

	public int getGeneralInitialArrowType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.arrowType; }

	public void setGeneralInitialArrowType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.arrowType = para; }

	public int getGeneralInitialArrowSize() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.arrowSize; }

	public void setGeneralInitialArrowSize(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.initialDrawingData
				.arrowSize = para; }


	//**************************************************************************
	//************************  Final related handlers  ************************
	//**************************************************************************
	public void addFinal(String finalID, Final final_) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.put(finalID, final_);
	}

	public void removeFinal(Final final_) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.remove(final_.id);
	}

	public void removeFinal(String finalID) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.remove(finalID);
	}

	public Final getFinal(String finalID) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(finalID);
	}

	public void setFinal(String finalID, Final final_) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.put(finalID, final_);
	}

	public FinalDrawingData getFinalDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id)
				.finalDrawingData;
	}

	public void setFinalDrawingData(String id, FinalDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id)
				.finalDrawingData = drawingData;
	}

	public int getFinalLineType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineType; }

	public void setFinalLineType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineType = para; }

	public int getFinalLineWidth(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineWidth; }

	public void setFinalLineWidth(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineWidth = para; }

	public int getFinalLineColor(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineColor; }

	public void setFinalLineColor(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.lineColor = para; }

	public int getFinalArrowType(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.arrowType; }

	public void setFinalArrowType(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.arrowType = para; }

	public int getFinalArrowSize(String id) {
		return this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.arrowSize; }

	public void setFinalArrowSize(String id, int para) {
		this.fsmxml.automatonV.get(0).automatonStruct.transitions.finalH.get(id).finalDrawingData
				.arrowSize = para; }

	//**************************************************************************
	//********************  global Final related handlers  *********************
	//**************************************************************************
	public FinalDrawingData getGeneralFinalDrawingData(String id) {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData;
	}

	public void setGeneralFinalDrawingData(String id, FinalDrawingData drawingData) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData = drawingData;
	}

	public int getGeneralFinalLineType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineType; }

	public void setGeneralFinalLineType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineType = para; }

	public int getGeneralFinalLineWidth() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineWidth; }

	public void setGeneralFinalLineWidth(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineWidth = para; }

	public int getGeneralFinalLineColor() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineColor; }

	public void setGeneralFinalLineColor(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.lineColor = para; }

	public int getGeneralFinalArrowType() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.arrowType; }

	public void setGeneralFinalArrowType(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.arrowType = para; }

	public int getGeneralFinalArrowSize() {
		return this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.arrowSize; }

	public void setGeneralFinalArrowSize(int para) {
		this.fsmxml.automatonV.get(0).generalDrawingData.finalDrawingData
				.arrowSize = para; }

	//**************************************************************************
	//**********************   end of handler functions   **********************
	//**************************************************************************
	private Fsmxml fsmxml;

	public Drawing2() {
		this.fsmxml = new Fsmxml();
	}

	public Drawing2(Fsmxml fsmxml) {
		this.fsmxml = fsmxml;
	}

	public static class Fsmxml {

		private Vector<Automaton> automatonV;

		public Fsmxml() {
			this.automatonV = new Vector<Automaton>();
			this.automatonV.add(new Automaton());
		}
	}

	public static class Automaton {

		private DrawingData drawingData;
		private GeneralDrawingData generalDrawingData;
		private AutomatonStruct automatonStruct;

		public Automaton() {
			this.automatonStruct = new AutomatonStruct();
		}
	}

	//TODO obsolete class?
	public static class DrawingData {
	}

	public static class StateDrawingData extends DrawingData {

		private int size;			// 0:small, 1:normal (default), 2:big
		private int stateType;		// 0:normal, 1:initial, 2:final, 3:initial+final
		private int visualType;		// 0:circle, 1:diamond, 2:square, 3:monmo
		private int textColor;		// 0:black, 1:red, 2:green, 3:blue
		private int bgColor;		// 0:black, 1:red, 2:green, 3:blue
		private int borderColor;	// 0:black, 1:red, 2:green, 3:blue

		public StateDrawingData() {
			size = 1; stateType = 0; visualType = 0; textColor = 0;
			bgColor = 0; borderColor = 0;
		}

		public StateDrawingData(int size, int stateT, int visT, int txtC, int bgC, int borC) {
			this.size = size;
			this.stateType = stateT;
			this.visualType = visT;
			this.textColor = txtC;
			this.bgColor = bgC;
			this.bgColor = borC;
		}
	}

	public static class TransitionDrawingData extends DrawingData {

		private int transitionType;	// 0:straight line, 1:polyline, 2:bezier, 3:spline
		private Vector<Point2D.Float> controlPoints;
		private int lineType;		// 0:normal, 1:dashed, 2:dots
		private int lineWidth;		// any positive integer value; default 1?
		private int lineColor;		// 0:black, 1:red, 2:green, 3:blue
		private int arrowType;		// 0:normal, 1:simple
		private int arrowSize;		// 0:small, 1:normal <default>, 2:big, 3:huge

		public TransitionDrawingData() {
			transitionType = 0;
			//controlPoints = new Vector();
			controlPoints = new Vector();
			lineType = 0;
			lineWidth = 1;
			lineColor = 0;
			arrowType = 0;
			arrowSize = 0;
		}

		public TransitionDrawingData(int transT, Vector<Point2D.Float> ctrlP,
				int lineT, int lineW, int lineC, int arrT, int arrS) {

			this.transitionType = transT;
			this.controlPoints = ctrlP;
			this.lineType = lineT;
			this.lineWidth = lineW;
			this.lineColor = lineC;
			this.arrowType = arrT;
			this.arrowSize = arrS;
		}
	}

	public static class InitialDrawingData extends DrawingData {

		private int lineType;		// 0:normal, 1:dashed, 2:dots
		private int lineWidth;		// any positive integer value
		private int lineColor;		// 0:black, 1:red, 2:green, 3:blue
		private int arrowType;		// 0:normal, 1:simple
		private int arrowSize;		// 0:small, 1:normal <default>, 2:big, 3:huge

		public InitialDrawingData() {

			lineType = 0;
			lineWidth = 1;
			lineColor = 0;
			arrowType = 0;
			arrowSize = 0;
		}

		public InitialDrawingData(int lineT, int lineW, int lineC, int arrT, int arrS) {

			this.lineType = lineT;
			this.lineWidth = lineW;
			this.lineColor = lineC;
			this.arrowType = arrT;
			this.arrowSize = arrS;
		}
	}

	public static class FinalDrawingData extends DrawingData {

		private int lineType;		// 0:normal, 1:dashed, 2:dots
		private int lineWidth;		// any positive integer value
		private int lineColor;		// 0:black, 1:red, 2:green, 3:blue
		private int arrowType;		// 0:normal, 1:simple
		private int arrowSize;		// 0:small, 1:normal <default>, 2:big, 3:huge

		public FinalDrawingData() {

			lineType = 0;
			lineWidth = 1;
			lineColor = 0;
			arrowType = 0;
			arrowSize = 0;
		}

		public FinalDrawingData(int lineT, int lineW, int lineC, int arrT, int arrS) {

			this.lineType = lineT;
			this.lineWidth = lineW;
			this.lineColor = lineC;
			this.arrowType = arrT;
			this.arrowSize = arrS;
		}
	}

	// stores global drawing data
	public static class GeneralDrawingData {

		private StateDrawingData stateDrawingData;
		private TransitionDrawingData transitionDrawingData;
		private InitialDrawingData initialDrawingData;
		private FinalDrawingData finalDrawingData;

		public GeneralDrawingData() {
			stateDrawingData = new StateDrawingData();
			transitionDrawingData = new TransitionDrawingData();
			initialDrawingData = new InitialDrawingData();
			finalDrawingData = new FinalDrawingData();
		}

		public GeneralDrawingData(StateDrawingData stateDD, TransitionDrawingData transDD,
				InitialDrawingData initialDD, FinalDrawingData finalDD) {

			this.stateDrawingData = stateDD;
			this.transitionDrawingData = transDD;
			this.initialDrawingData = initialDD;
			this.finalDrawingData = finalDD;
		}
	}

	public static class AutomatonStruct {

		private States states;
		private Transitions transitions;

		public AutomatonStruct() {
			states = new States();
			transitions = new Transitions();
		}

		public AutomatonStruct(States states, Transitions transitions) {
			this.states = states;
			this.transitions = transitions;
		}
	}

	public static class States {

		private Hashtable<String, State> stateH = new Hashtable();
	}

	public static class State {

		private String id;
		private StateDrawingData stateDrawingData;

		public State(String id) {
			this.id = id;
			stateDrawingData = new StateDrawingData();
		}

		public State(String id, StateDrawingData drawingData) {
			this.id = id;
			this.stateDrawingData = drawingData;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	//TODO obsolete class?
	public static class Label {
	}

	public static class Transitions {

		private Hashtable<String, Transition>	transitionH = new Hashtable();
		private Hashtable<String, Initial>		initialH = new Hashtable();
		private Hashtable<String, Final>		finalH = new Hashtable();
	}

	public static class Transition {

		private String id;
		private TransitionDrawingData transitionDrawingData;

		public Transition(String id) {
			this.id = id;
			transitionDrawingData = new TransitionDrawingData();
		}

		public Transition(String id, TransitionDrawingData drawingData) {
			this.id = id;
			this.transitionDrawingData = drawingData;
		}
	}

	public static class Initial {

		private String id;
		private InitialDrawingData initialDrawingData;

		public Initial(String id) {
			this.id = id;
			initialDrawingData = new InitialDrawingData();
		}

		public Initial(String id, InitialDrawingData drawingData) {
			this.id = id;
			this.initialDrawingData = drawingData;
		}
	}

	public static class Final {

		private String id;
		private FinalDrawingData finalDrawingData;

		public Final(String id) {
			this.id = id;
			finalDrawingData = new FinalDrawingData();
		}

		public Final(String id, FinalDrawingData drawingData) {
			this.id = id;
			this.finalDrawingData = drawingData;
		}
	}
}
