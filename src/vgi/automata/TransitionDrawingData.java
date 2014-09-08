package vgi.automata;

/**
 * Records the transition basic drawing attributes
 * 
 * <p>
 *  <b>TransitionDrawingData</b> record the following attributes of the given
 * edge.
 *  <ol>
 *      <li>stroke color</li>
 *      <li>stroke width</li>
 *      <li>start arrow style</li>
 *      <li>end arrow style</li>
 *      <li>edge style</li>
 *  </ol>
 * and it has corresponding setters and getters.
 * </p>
 * @author reng
 */
public class TransitionDrawingData {


    private String strokeColor;
    private float strokeWidth;
    private String startArrow;
    private String endArrow;
    
    private String edgeStyle; // edge style, shape is added for different 
    private String shape;     // layout algorithms

    public TransitionDrawingData() {

        this.strokeColor = null;
        this.strokeWidth = -1;
        this.startArrow = null;
        this.endArrow = null;
        this.edgeStyle = null;
        this.shape = null;

    }


    public TransitionDrawingData(String strokeColor_,
                                float strokeWidth_,
                                String startArrow_,
                                String endArrow_,
                                String edgeStyle_,
                                String shape_) {

        this.strokeColor = strokeColor_;
        this.strokeWidth = strokeWidth_;
        this.startArrow = startArrow_;
        this.endArrow = endArrow_;
        this.edgeStyle = edgeStyle_;
        this.shape = shape_;
        
    }
    
    public void setStrokeColor(String strokeColor_) {

        strokeColor = strokeColor_;
        
    }

    public String getStrokeColor() {

        return strokeColor;

    }

    public void setStrokeWidth(float strokeWidth_) {

        strokeWidth = strokeWidth_;

    }

    public float getStrokeWidth() {

        return strokeWidth;

    }

    public void setStartArrow(String startArrow_) {

        startArrow = startArrow_;

    }

    public String getStartArrow() {

        return startArrow;

    }

    public void setEndArrow(String endArrow_) {

         endArrow = endArrow_;

    }

    public String getEndArrow() {

        return endArrow;

    }

    public void setEdgeStyle(String edgeStyle_) {

        edgeStyle = edgeStyle_;

    }

    public String getEdgeStyle() {

        return edgeStyle;

    }

    public void setShape(String shape_) {

        shape = shape_;

    }

    public String getShape() {

        return shape;

    }
    
}
