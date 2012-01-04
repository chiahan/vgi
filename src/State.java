/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mxgraph.model.mxCell;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wkren
 */
public class State implements StateInterface {

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.pmName;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.pmName = name;
	}

	/**
	 * @return the initial weight
	 */
	@Override
	public Object getInitialWeight() {
		return this.pmInitialWeight;
	}

	/**
	 * @param initialFlag the initial weight to set
	 */
	@Override
	public void setInitialWeight(Object initialWeight) {
		this.pmInitialWeight = initialWeight;
	}

	/**
	 * @return the final weight
	 */
	@Override
	public Object getFinalWeight() {
		return this.pmFinalWeight;
	}

	/**
	 * @param finalFlag the finalFlag to set
	 */
	@Override
	public void setFinalWeight(Object finalWeight) {
		this.pmFinalWeight = finalWeight;
	}

	/**
	 * @return the geometricData
	 */
	@Override
	public mxCell getGeometricData() {
		return this.pmGeometricData;
	}

	/**
	 * @param geometricData the geometriData to set
	 */
	@Override
	public void setGeometricData(mxCell geometricData) {
		this.pmGeometricData = geometricData;
	}

	@Override
	public List<Transition> getTransitions() {
		ArrayList<Transition> arrayList = new ArrayList<Transition>();
		arrayList.addAll(this.pmIncomingTransitions);
		arrayList.addAll(this.pmOutgoingTransitions);
		return arrayList;
	}

	@Override
	public List<Transition> getIncomingTransitions() {
		return this.pmIncomingTransitions;
	}

	@Override
	public void setIncomingTransitions(List<Transition> transitions) {
		this.pmOutgoingTransitions = transitions;
	}

	@Override
	public List<Transition> getOutgoingTransitions() {
		return this.pmOutgoingTransitions;
	}

	@Override
	public void setOutgoingTransitions(List<Transition> transitions) {
		this.pmOutgoingTransitions = transitions;
	}

	@Override
	public void addTransition(Transition transition) {
		if (transition.getSourceState().equals(this)) {
			this.pmOutgoingTransitions.add(transition);
		} else if (transition.getTargetState().equals(this)) {
			this.pmIncomingTransitions.add(transition);
		} else {
			throw new IllegalArgumentException("Can not add a transition to a state which is neither the source nor the target of the transition.");
		}
	}  // End public void addTransition(Transition transition)
	private String pmName;
	private Object pmInitialWeight;
	private Object pmFinalWeight;
	private List<Transition> pmIncomingTransitions;
	private List<Transition> pmOutgoingTransitions;
	private mxCell pmGeometricData;

	public State() {
		this.pmName = new String();
		this.pmInitialWeight = null;
		this.pmFinalWeight = null;
		this.pmIncomingTransitions = new ArrayList<Transition>();
		this.pmOutgoingTransitions = new ArrayList<Transition>();
		this.pmGeometricData = null;
	}
        
        public State(mxCell geometricData) {
            this.pmName = new String();
            this.pmInitialWeight = null;
            this.pmFinalWeight = null;
            this.pmIncomingTransitions = new ArrayList<Transition>();
            this.pmOutgoingTransitions = new ArrayList<Transition>();
            this.pmGeometricData = geometricData;
        }
}  // End public class State implements StateInterface
