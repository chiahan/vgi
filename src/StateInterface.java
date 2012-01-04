
import com.mxgraph.model.mxCell;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface StateInterface {

	public mxCell geometricData = null;

	public String getName();

	public void setName(String name);

	public Object getInitialWeight();

	public void setInitialWeight(Object initialWeight);

	public Object getFinalWeight();

	public void setFinalWeight(Object finalWeight);

	public mxCell getGeometricData();

	public void setGeometricData(mxCell geometricData);

	public List<Transition> getTransitions();

	public List<Transition> getIncomingTransitions();

	public void setIncomingTransitions(List<Transition> transitions);

	public List<Transition> getOutgoingTransitions();

	public void setOutgoingTransitions(List<Transition> transitions);

	public void addTransition(Transition transition);
}  // End public interface StateInterface
