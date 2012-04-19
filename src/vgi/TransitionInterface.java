package vgi;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface TransitionInterface {

	public class GeometricData {

		public Point2D.Double labelPosAndDist;
		public Point2D.Double labelOffset;
		public List<Point2D> controlPoints;

		public GeometricData() {
			this.labelPosAndDist = null;
			this.labelOffset = null;
			this.controlPoints = new ArrayList<Point2D>();
		}
	}  // End public class GeometricData

	public State getSourceState();

	public void setSourceState(State sourceState);

	public State getTargetState();

	public void setTargetState(State targetState);

	public WeightedRegularExpression getLabel();

	public void setLabel(WeightedRegularExpression label);

	public GeometricData getGeometricData();

	public void setGeometricData(GeometricData geometricData);
}  // End public interface TransitionInterface
