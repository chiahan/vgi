package vgi.automata;

import vgi.automata.State;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wkren
 */
public class Transition implements TransitionInterface {

	/**
	 * @return the source state
	 */
	@Override
	public State getSourceState() {
		return this.pmSourceState;
	}

	/**
	 * @param sourceState the source state to set
	 */
	@Override
	public void setSourceState(State sourceState) {
		this.pmSourceState = sourceState;
	}

	/**
	 * @return the target state
	 */
	@Override
	public State getTargetState() {
		return this.pmTargetState;
	}

	/**
	 * @param targetState the target state to set
	 */
	@Override
	public void setTargetState(State targetState) {
		this.pmTargetState = targetState;
	}

	/**
	 * @return the label
	 */
	@Override
	public WeightedRegularExpression getLabel() {
		return this.pmLabel;
	}

	/**
	 * @param label the label to set
	 */
	@Override
	public void setLabel(WeightedRegularExpression label) {
		this.pmLabel = label;
	}

	/**
	 * @return the geometricData
	 */
	@Override
	public GeometricData getGeometricData() {
		return this.pmGeometricData;
	}

	/**
	 * @param geometricData the geometricData to set
	 */
	@Override
	public void setGeometricData(GeometricData geometricData) {
		this.pmGeometricData = geometricData;
	}
    
    @Override
    public String toString() {
        return pmLabel + " : '" + pmSourceState + "' to '" + pmTargetState + "'";
    }
    
	private State pmSourceState;
	private State pmTargetState;
	private WeightedRegularExpression pmLabel;
	private GeometricData pmGeometricData;
        private DrawingData pmDrawingData;

	public Transition() {
		this.pmSourceState = null;
		this.pmTargetState = null;
		this.pmLabel = null;
		this.pmGeometricData = new GeometricData();
	}

    @Override
    public DrawingData getDrawingData() {
        return pmDrawingData;
    }

    @Override
    public void setDrawingData(DrawingData drawingData) {
        pmDrawingData=drawingData;
    }
}  // End public class Transition implements TransitionInterface
