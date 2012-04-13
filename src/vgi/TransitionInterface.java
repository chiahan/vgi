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

		Point2D labelPosition;
		List<Point2D> controlPoints;

		public GeometricData() {
			this.labelPosition = null;
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
