/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cellview;

import java.util.Map;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

/**
 * @author cody_liu
 */
public class MyCellViewFactory extends DefaultCellViewFactory {

    public static final String VIEW_CLASS_KEY = "viewClassKey";

    public static final void setViewClass(Map map, String viewClass) {
        map.put(VIEW_CLASS_KEY, viewClass);
    }

    @Override
    public CellView createView(GraphModel model, Object cell) {
        CellView view = null;
        if (model.isPort(cell)) {
            view = createPortView(cell);
        } else if (model.isEdge(cell)) {
            view = createEdgeView(cell);
        } else {
            view = createStateView(cell);
        }
        return view;
    }

    protected VertexView createStateView(Object cell) {
        DefaultGraphCell gcell = (DefaultGraphCell) cell;
        String viewClass = (String) gcell.getAttributes().get(VIEW_CLASS_KEY);

        if (viewClass == null){
           return new VertexView(cell);
        }
        else{
             return new StateView(cell);
        }
    }

    protected EdgeView createEdgeView(Object cell) {
        return new EdgeView(cell);
    }

    protected PortView createPortView(Object cell) {
        return new MyPortView(cell);
    }
}
