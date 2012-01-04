
import com.mxgraph.model.mxCell;

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
	public String getLabel() {
		return this.pmLabel;
	}

	/**
	 * @param label the label to set
	 */
	@Override
	public void setLabel(String label) {
		this.pmLabel = label;
	}

	/**
	 * @return the geometricData
	 */
	@Override
	public mxCell getGeometricData() {
		return this.pmGeometricData;
	}

	/**
	 * @param geometricData the geometricData to set
	 */
	@Override
	public void setGeometricData(mxCell geometricData) {
		this.pmGeometricData = geometricData;
	}

	private State pmSourceState;
	private State pmTargetState;
	private String pmLabel;
	private mxCell pmGeometricData;

	public Transition() {
		this.pmSourceState = null;
		this.pmTargetState = null;
		this.pmLabel = null;
		this.pmGeometricData = null;
	}
        
        public Transition(mxCell geometricData, State source, State target) {
            this.pmSourceState = source;
            this.pmTargetState = target;
            this.pmLabel = null;
            this.pmGeometricData = geometricData;
        }
}  // End public class Transition implements TransitionInterface
