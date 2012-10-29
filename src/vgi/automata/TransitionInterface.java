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

        public class DrawingData{
            
            public String strokeColor;
            public float strokeWidth;
            public String startArrow;
            public String endArrow;
                    
            public DrawingData(){
                this.strokeColor=null;
                this.strokeWidth=1;
                this.startArrow=null;
                this.endArrow=null;
            }
            
        }
        
	public State getSourceState();

	public void setSourceState(State sourceState);

	public State getTargetState();

	public void setTargetState(State targetState);

	public WeightedRegularExpression getLabel();

	public void setLabel(WeightedRegularExpression label);

	public GeometricData getGeometricData();

	public void setGeometricData(GeometricData geometricData);
        
        public DrawingData getDrawingData();
        
        public void setDrawingData(DrawingData drawingData);
}  // End public interface TransitionInterface
