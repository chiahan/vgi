/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import vgi.automata.Automata;
import vgi.automata.Transition;
import vgi.geometrictools.Projection;
import vgi.layout.edgerouting.EdgeRoutingBranchingLayout;

/**
 *
 * @author x1213
 */
public class HierarchicalLayoutAutomata {
    Projection projection;
    public HierarchicalLayoutAutomata(){}
    
    public void doLayout(Automata automata_){
        mxGraph _graph = automata_.jgraphAutomata.graph;
        projection=automata_.getProjection();
        
        mxHierarchicalLayout layout = new mxHierarchicalLayout(_graph);
        layout.setOrientation(SwingConstants.NORTH);
        layout.setFineTuning(false);
        layout.setDisableEdgeStyle(false);
        layout.execute(_graph.getDefaultParent());
        
        //handleInitialFinal();
        //handleLoop();
        //handleEdgeControlPoint();
        /*EdgeRoutingLayout edgeRoute = new EdgeRoutingLayout(this.graph);
        edgeRoute.execute(this.graph.getDefaultParent());
        */
        //EdgeRoutingBranchingLayout layout2 = new EdgeRoutingBranchingLayout(_graph);
        //layout2.execute(this.graph.getDefaultParent());
        
        _graph.refresh();
        
        //modify control points
        List<Transition> mylist = automata_.getAllTransitions();
        for (Transition trans: mylist) {
            mxCell myobj = automata_.objToCell(trans);
            List<mxPoint> points = myobj.getGeometry().getPoints();
            List<Point2D> newPoints = getGeoFromLoc_vector(points);
            newPoints = adjustCtrPoints(newPoints);
            automata_.updateTransitionControlPoint(trans,newPoints);
        }
        
        _graph.refresh();
    }
    
    private List<Point2D> getGeoFromLoc_vector(List<mxPoint> list) {
        List<Point2D> newList = new ArrayList<Point2D>();
        for (int i = 0; i < list.size(); ++i) {
            Point2D p = new Point2D.Double(list.get(i).getX(), list.get(i).getY());
            p=projection.getGeoFromLoc(p);
            newList.add(p);
        }
        return newList;
    }
    
    private List<Point2D> adjustCtrPoints(List<Point2D> list) {
        List<Point2D> newList = new ArrayList<Point2D>();
        for (int i = 0; i < list.size()-1; ++i) {
            Point2D p = new Point2D.Double((list.get(i).getX()+list.get(i+1).getX())/2, (list.get(i).getY()+list.get(i+1).getY())/2);
            newList.add(p);
        }     
        return newList;
    }
}
