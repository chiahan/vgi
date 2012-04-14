package vgi;


import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface StateInterface {

	public class GeometricData {

		Point2D location;
		Shape shape;

		public GeometricData() {
			this.location = null;
			this.shape = null;
		}
	}  // End public class GeometricData

	public String getName();

	public void setName(String name);

	public InitialFinalWeight getInitialWeight();

	public void setInitialWeight(InitialFinalWeight initialWeight);

	public InitialFinalWeight getFinalWeight();

	public void setFinalWeight(InitialFinalWeight finalWeight);

	public GeometricData getGeometricData();

	public void setGeometricData(GeometricData geometricData);

	public List<Transition> getTransitions();

	public List<Transition> getIncomingTransitions();

	public void setIncomingTransitions(List<Transition> transitions);

	public List<Transition> getLoopTransitions();

	public void setLoopTransitions(List<Transition> transitions);

	public List<Transition> getOutgoingTransitions();

	public void setOutgoingTransitions(List<Transition> transitions);

	public void addTransition(Transition transition);

	public List<State> getHistory();

	public void setHistory(List<State> states);
}  // End public interface StateInterface
