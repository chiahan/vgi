package vgi.automata;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wkren
 */
public class LinearGroup implements StateInterface 
{

	/**
	 * @return the name
	 */
	@Override
	public String getName() 
        {
		return this.pmName;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) 
        {
		this.pmName = name;
	}

	/**
	 * @return the initial weight
	 */
//	@Override
//	public InitialFinalWeight getInitialWeight() {
//		return this.pmInitialWeight;
//	}
//
//	/**
//	 * @param initialFlag the initial weight to set
//	 */
//	@Override
//	public void setInitialWeight(InitialFinalWeight initialWeight) {
//		this.pmInitialWeight = initialWeight;
//	}
//
//	/**
//	 * @return the final weight
//	 */
//	@Override
//	public InitialFinalWeight getFinalWeight() {
//		return this.pmFinalWeight;
//	}
//
//	/**
//	 * @param finalFlag the finalFlag to set
//	 */
//	@Override
//	public void setFinalWeight(InitialFinalWeight finalWeight) {
//		this.pmFinalWeight = finalWeight;
//	}

	/**
	 * @return the geometricData
	 */
	@Override
	public StateGeometricData getGeometricData() {
		return this.pmGeometricData;
	}

	/**
	 * @param egeometricData the geometriData to set
	 */
	@Override
	public void setGeometricData(StateGeometricData geometricData) 
        {
		this.pmGeometricData = geometricData;
	}

	@Override
	public List<Transition> getTransitions() 
        {
		ArrayList<Transition> arrayList = new ArrayList<Transition>();
		arrayList.addAll(this.pmIncomingTransitions);
		arrayList.addAll(this.pmLoopTransitions);
		arrayList.addAll(this.pmOutgoingTransitions);
		return arrayList;
	}

	@Override
	public List<Transition> getIncomingTransitions() 
        {
		return this.pmIncomingTransitions;
	}

	@Override
	public void setIncomingTransitions(List<Transition> transitions) 
        {
		this.pmOutgoingTransitions = transitions;
	}

	@Override
	public List<Transition> getLoopTransitions() 
        {
		return this.pmLoopTransitions;
	}

	@Override
	public void setLoopTransitions(List<Transition> transitions) 
        {
		this.pmLoopTransitions = transitions;
	}

	@Override
	public List<Transition> getOutgoingTransitions() 
        {
		return this.pmOutgoingTransitions;
	}

	@Override
	public void setOutgoingTransitions(List<Transition> transitions) 
        {
		this.pmOutgoingTransitions = transitions;
	}

	@Override
	public void addTransition(Transition transition) 
        {
		if (transition.getSourceState().equals(this)) 
                {
			if (transition.getTargetState().equals(this)) 
                        {
				this.pmLoopTransitions.add(transition);
			} 
                        else 
                        {
				this.pmOutgoingTransitions.add(transition);
			}
		} 
                else if (transition.getTargetState().equals(this)) 
                {
			this.pmIncomingTransitions.add(transition);
		} 
                else 
                {
			throw new IllegalArgumentException("Can not add a transition to a state which is neither the source nor the target of the transition.");
		}
	}  // End public void addTransition(Transition transition)

	@Override
	public List<State> getHistory() 
        {
		return this.pmHistoryStates;
	}

	@Override
	public void setHistory(List<State> states) 
        {
		this.pmHistoryStates = states;
	}

	@Override
	public String toString() 
        {
		return pmName;
	}
	private String pmName;
	//private InitialFinalWeight pmInitialWeight;
	//private InitialFinalWeight pmFinalWeight;
	private List<Transition> pmIncomingTransitions;
	private List<Transition> pmLoopTransitions;
	private List<Transition> pmOutgoingTransitions;
	//private GeometricData pmGeometricData;
	private List<State> pmHistoryStates;
       // private DrawingData pmDrawingData;
        private StateDrawingData pmDrawingData;
        private StateGeometricData pmGeometricData;
        
        private Initial initial;
        private Final finall;
        
        private int pmGroupID;
        
	public LinearGroup() 
        {
		this.pmName = null;
		//this.pmInitialWeight = null;
		//this.pmFinalWeight = null;
		this.pmIncomingTransitions = new ArrayList<Transition>();
		this.pmLoopTransitions = new ArrayList<Transition>();
		this.pmOutgoingTransitions = new ArrayList<Transition>();
		this.pmGeometricData = new StateGeometricData();
                this.pmDrawingData=new StateDrawingData();
                
                this.initial=null;  // null if state has no initial
                this.finall=null; 
                this.pmGroupID=-1;
                
	}

    @Override
    public StateDrawingData getDrawingData() 
    {
        return pmDrawingData;
    }

    @Override
    public void setDrawingData(StateDrawingData drawingData) 
    {
        pmDrawingData=drawingData;
    }

    @Override
    public String getShape() 
    {
        return pmGeometricData.getShape();
    }

    @Override
    public void setShape(String shape_) 
    {
        pmGeometricData.setShape(shape_);
    }
    
    public Initial getInitial()
    {
        return initial;
    }
    public void setInitial(Initial ini)
    {
        initial=ini;
    }
    public Final getFinal()
    {
        return finall;
    }
    public void setFinal(Final fin)
    {
        finall=fin;
    }
    
    @Override
    
    public boolean removeTransition(Transition transition)
    {
        if(!pmIncomingTransitions.remove(transition))
            if(!pmOutgoingTransitions.remove(transition))
                return pmLoopTransitions.remove(transition);
        return true;
    }
        
    public int getGroupID()
    {
        return pmGroupID;
    }
    public void setGroupID(int i)
    {
        pmGroupID=i;
    }
}  // End public class State implements StateInterface
