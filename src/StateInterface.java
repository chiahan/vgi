
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

	public int getId();

	public void setId(int id);

	public String getName();

	public void setName(String name);

	public Object getInitialWeight();

	public void setInitialWeight(Object initialWeight);

	public Object getFinalWeight();

	public void setFinalWeight(Object finalWeight);

	public GeometricData getGeometricData();

	public void setGeometricData(GeometricData geometricData);

	public List<Transition> getTransitions();

	public void setTransitions(List<Transition> transitions);

}  // End public interface StateInterface
