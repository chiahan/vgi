
import com.mxgraph.model.mxCell;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface TransitionInterface {

	public mxCell GeometricData = null;

	public State getSourceState();

	public void setSourceState(State sourceState);

	public State getTargetState();

	public void setTargetState(State targetState);

	public String getLabel();

	public void setLabel(String label);

	public mxCell getGeometricData();

	public void setGeometricData(mxCell geometricData);

}  // End public interface TransitionInterface
