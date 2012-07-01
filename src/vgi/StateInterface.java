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
		Point2D size;
                String shape;

		public GeometricData() {
			this.location = null;
                        this.size=null;
			this.shape = null;
		}
	}  // End public class GeometricData

        
        public class DrawingData{
            
               String fillColor;
               String strokeColor;
               float strokeWidth;
               
               public DrawingData(){
                   this.fillColor=null;
                   this.strokeColor=null;
                   this.strokeWidth=1;
               }
        }
        
        
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
        
        public DrawingData getDrawingData();
        
        public void setDrawingData(DrawingData drawingData);
        
        public String getShape();
        
        public void setShape(String shape);
        
}  // End public interface StateInterface
