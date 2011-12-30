
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
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
			this.location = new Point2D.Double();
			this.shape = new Ellipse2D.Double();
		}
	}  // End public class GeometricData

	public String getName();

	public void setName(String name);

	public Object getInitialWeight();

	public void setInitialWeight(Object initialWeight);

	public Object getFinalWeight();

	public void setFinalWeight(Object finalWeight);

	public GeometricData getGeometricData();

	public void setGeometricData(GeometricData geometricData);

	public List<Transition> getTransitions();

	public List<Transition> getIncomingTransitions();

	public void setIncomingTransitions(List<Transition> transitions);

	public List<Transition> getOutgoingTransitions();

	public void setOutgoingTransitions(List<Transition> transitions);

	public void addTransition(Transition transition);
}  // End public interface StateInterface