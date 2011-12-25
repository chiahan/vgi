/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wkren
 */
public class State implements StateInterface {

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.pmId;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
		this.pmId = id;
	}

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
	public GeometricData getGeometricData() {
		return this.pmGeometricData;
	}

	/**
	 * @param geometricData the geometriData to set
	 */
	@Override
	public void setGeometricData(GeometricData geometricData) {
		this.pmGeometricData = geometricData;
	}

	@Override
	public List<Transition> getTransitions() {
		return this.pmTransitions;
	}

	@Override
	public void setTransitions(List<Transition> transitions) {
		this.pmTransitions = transitions;
	}
	private String pmId;
	private String pmName;
	private Object pmInitialWeight;
	private Object pmFinalWeight;
	private List<Transition> pmTransitions;
	private GeometricData pmGeometricData;

	public State() {
		this.pmId = null;
		this.pmName = new String();
		this.pmInitialWeight = null;
		this.pmFinalWeight = null;
		this.pmTransitions = new ArrayList<Transition>();
		this.pmGeometricData = new GeometricData();
	}
}  // End public class State implements StateInterface
