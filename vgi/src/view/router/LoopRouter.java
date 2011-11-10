package view.router;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import layout_algorithm.ModifyStateLoop;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.util.ParallelEdgeRouter;

/**
 * Routing algorithm which creates intermediate points for a line such that the
 * line is parallel to all other existing lines between the same source and
 * target. One of the parallel edges does not contain a routing algorithm and
 * can therefore accept additional control points and can be dragged.
 * The correct intermediate points for the routed lines are calculated from
 * the edge that doesn't have a routing algorithm applied to it.
 *
 * @author Zygmunt Szpak  ,  CW-LI
 * @version 1.0 02/12/2006
 *
 */
public class LoopRouter extends ParallelEdgeRouter {

    protected static final LoopRouter sharedInstance = new LoopRouter();

    public static LoopRouter getSharedInstance() {
        return LoopRouter.sharedInstance;
    }






    protected Object[] getParallelEdges(GraphLayoutCache cache, EdgeView edge) {

        GraphModel model = cache.getModel();
        Object[] roots = DefaultGraphModel.getRoots(model);
        if (roots.length == 0) {
            return null;
        }
        Object[] edges = DefaultGraphModel.getEdgesBetween(model, edge.getSource().getCell(), edge.getSource().getCell(), false);
        if (edges.length == 0) {
            return null;
        }

        return edges;
    }

    @Override
    public List route(GraphLayoutCache cache, EdgeView edge) {
        List newPoints = super.route(cache, edge);
        if (newPoints == null){
            return null;
        }
        //newPoints.add(0,  new Point2D.Double(10,10));
        return newPoints;
    }

    public List routeLoop(GraphLayoutCache cache, EdgeView edge) {

        List newPoints = new ArrayList();

        // newPoints.add(edge.getSource());

        if (edge.getSource() == null || edge.getTarget() == null || edge.getSource().getParentView() == null || edge.getTarget().getParentView() == null) {
            return null;
        }
        Object[] edges = getParallelEdges(cache, edge);

        // Find the position of the current edge that we are currently routing
        if (edges == null) {
            return null;
        }

        CellView sourceParent = (edge.getSource() != null) ? edge.getSource().getParentView() : edge.getSourceParentView();
        Point2D from = AbstractCellView.getCenterPoint(sourceParent);
        Rectangle2D rect = sourceParent.getBounds();
        double width = rect.getWidth();
        double height2 = rect.getHeight() / 2;
        double loopWidth = Math.min(20, Math.max(10, width / 8));
        double loopHeight = Math.min(30, Math.max(12, Math.max(
                loopWidth + 4, height2 / 2)));

        int position = 0;
        for (int i = 0; i < edges.length; i++) {
            Object e = edges[i];
            if (e == edge.getCell()) {
                position = i * 10;
            }
        }
        Point2D.Double point;
        DefaultEdge edgeCell = (DefaultEdge) edge.getCell();
        ModifyStateLoop msl = new ModifyStateLoop(0, null, null);
        DefaultGraphCell group = (DefaultGraphCell) edgeCell.getSource();
        double angle = Double.parseDouble(edgeCell.getAttributes().get("angle").toString()) - (3.14159 / 2);


        newPoints.add(edge.getSource());

        point = new Point2D.Double(0 - (loopWidth + position),
                (0 - height2 - (loopHeight * 1 + position)));

        point = msl.CountControlPointRotatePosition(point, angle);
        point = new Point2D.Double(from.getX() + point.getX(), from.getY() + point.getY());
        newPoints.add(edge.getAttributes().createPoint(point));


        point = new Point2D.Double(0,
                0 - height2 - 1.5 * loopHeight - position);
        point = msl.CountControlPointRotatePosition(point, angle);
        point = new Point2D.Double(from.getX() + point.getX(), from.getY() + point.getY());
        newPoints.add(edge.getAttributes().createPoint(point));

        point = new Point2D.Double(0 + (loopWidth + position),
                (0 - height2 - (loopHeight * 1 + position)));
        point = msl.CountControlPointRotatePosition(point, angle);
        point = new Point2D.Double(from.getX() + point.getX(), from.getY() + point.getY());
        newPoints.add(edge.getAttributes().createPoint(point));

        newPoints.add(edge.getTarget());

        return newPoints;


    }
}
