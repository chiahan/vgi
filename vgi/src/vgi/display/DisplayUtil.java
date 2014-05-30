/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.display;

import vgi.automata.Transition;
import vgi.automata.State;
import vgi.automata.Automata;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import vgi.automata.WeightedRegularExpression;

/**
 *
 * @author wkren
 */
public class DisplayUtil {
    private mxGraph graph;
    private Automata automata;
    //private Hashtable<mxCell, Object> cellTable;
    
//    public DisplayUtil(mxGraph graph, Automata automata, 
//                      Hashtable<mxCell, Object> cellTable) {
//        this.graph = graph;
//        this.automata = automata;
//        this.cellTable = cellTable;
//    }
    public DisplayUtil(mxGraph graph, Automata automata) {
        this.graph = graph;
        this.automata = automata;
    }
    
    public State cellToState(mxCell cell) {
       // return (State) getCellTable().get(cell);
        return (State)automata.cellToState(cell);
    }

    public Transition cellToTransition(mxCell cell) {
        //return (Transition) getCellTable().get(cell);
        return (Transition)automata.cellToState(cell);
    }
    
    public Transition IniFinToTransition(mxCell cell) {
        //return (Transition) getCellTable().get(cell);
        Transition a = null;
        return a;
    }
    
    public Object getInitialEdge(Object vertex) {
        Object[] edges = graph.getEdges(vertex);
        int length = edges.length;
        mxCell edge;
        for (int j = 0; j < length; j++) {
            edge = (mxCell) edges[j];
            if (edge.getTerminal(true) == null) {
                return edge;
            } 
        }
        return null;
    }
    
    public Object getFinalEdge(Object vertex) {
        Object[] edges = graph.getEdges(vertex);
        int length = edges.length;
        mxCell edge;
        for (int j = 0; j < length; j++) {
            edge = (mxCell) edges[j];
            if (edge.getTerminal(false) == null) {
                return edge;
            } 
        }
        return null;
    }
    
    private void updateInitialFinal(Object[] cells, Point2D offset) {
        DisplayUtil display = new DisplayUtil(graph, automata);
        mxCell vertex, edge;
        Object parent = graph.getDefaultParent();
        
        int length = cells.length;
        for (int i = 0; i < length; i++) {
            vertex = (mxCell) cells[i];
            Object edges[] = graph.getEdges(vertex);
            int count = edges.length;
            for (int j = 0; j < count; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                    display.showInitialFinal(parent, edge.getValue(), graph.getSelectionCell(), false);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);
                } else if (edge.getTerminal(false) == null) {
                    display.showInitialFinal(parent, edge.getValue(), graph.getSelectionCell(), true);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);
                }
            } // end edge
        } // end vertex
    }
    
    public void showInitialFinal(Object parent, Object weight, Object vertex, 
                               boolean vertexIsSource) {
        Object edge;
        WeightedRegularExpression.Atomic expression =
                WeightedRegularExpression.Atomic.createAtomic(getAutomata());
        expression.setSymbol(weight);
        Transition newTrans = new Transition();
        newTrans.setLabel(expression);

        Point2D p = createTerminalPoint((mxCell) vertex);
        mxPoint terminalPoint = new mxPoint(p.getX(), p.getY());;
        if (vertexIsSource) {
            edge = getGraph().insertEdge(parent, null, weight, vertex, null);

            newTrans.setSourceState(cellToState((mxCell) vertex));
            newTrans.setTargetState(null);
        } else {
            edge = getGraph().insertEdge(parent, null, weight, null, vertex);

            newTrans.setSourceState(null);
            newTrans.setTargetState(cellToState((mxCell) vertex));
        }

        Object[] cell = {edge};
        getGraph().setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cell);
        getGraph().setCellStyles("fontSize", String.valueOf(JgraphXInternalFrame.defaultFontSize), cell);
        getGraph().setCellStyles("textShape","default", cell);
        
        getGraph().getCellGeometry(edge).setTerminalPoint(terminalPoint, !vertexIsSource);
        ((mxCell) edge).getGeometry().setY(JgraphXInternalFrame.DEFAULT_LABEL_DISTANCE);

        //getCellTable().put((mxCell) edge, newTrans);
        automata.addTransition(newTrans,(mxCell)edge);
        graph.refresh();
    }
    
    private Point2D createTerminalPoint(mxCell vertex) {
        int counter = vertex.getEdgeCount();
        int i = 0, j = 0;
        Point2D p1 = new Point.Double(), p2 = new Point.Double(),
                p3 = new Point.Double(), p4 = new Point.Double();
        while (j < 4) {
            boolean flag = false;
            mxGeometry geo = vertex.getGeometry();
            switch (j) {
                case 0:
                    p3.setLocation(geo.getCenterX(), geo.getCenterY() - geo.getHeight() / 2);
                    p4.setLocation(geo.getCenterX(), geo.getCenterY() - geo.getHeight());
                    break;
                case 1:
                    p3.setLocation(geo.getCenterX(), geo.getCenterY() + geo.getHeight() / 2);
                    p4.setLocation(geo.getCenterX(), geo.getCenterY() + geo.getHeight());
                    break;
                case 2:
                    p3.setLocation(geo.getCenterX() - geo.getWidth() / 2, geo.getCenterY());
                    p4.setLocation(geo.getCenterX() - geo.getWidth(), geo.getCenterY());
                    break;
                case 3:
                    p3.setLocation(geo.getCenterX() + geo.getWidth() / 2, geo.getCenterY());
                    p4.setLocation(geo.getCenterX() + geo.getWidth(), geo.getCenterY());
                    break;
            }

            for (; i < counter; i++) {
                mxCell edge = (mxCell) vertex.getEdgeAt(i);
                p1 = getTerminalPoint(edge, true);
                p2 = getTerminalPoint(edge, false);

                if (isIntersect(p1, p2, p3, p4)) {
                    flag = true;
                    break;
                }
            }

            if ((i == counter) && !flag) {
                return p4;
            }

            j++;
        }
        return p4;
    }
    
    private Point2D getTerminalPoint(mxCell edge, boolean isSource) {
        mxCell cell = (mxCell) edge.getTerminal(isSource);
        Point2D p = new Point.Double();

        if (cell != null) {
            p.setLocation(cell.getGeometry().getCenterX(), cell.getGeometry().getCenterY());
        } else {
            p.setLocation(getGraph().getCellGeometry(edge).getTerminalPoint(isSource).getPoint());
        }

        return p;
    }
    
    private boolean isIntersect(Point2D p, Point2D q, Point2D r, Point2D s) {
        double rCrossPQ = cross(p, q, r);
        double sCrossPQ = cross(p, q, s);
        double pCrossRS = cross(r, s, p);
        double qCrossRS = cross(r, s, q);

        if ((rCrossPQ * sCrossPQ < 0) && (pCrossRS * qCrossRS < 0)) {
            return true;
        }
        if (rCrossPQ == 0) {
            return isIntersect(p, q, r);
        }
        if (sCrossPQ == 0) {
            return isIntersect(p, q, r);
        }
        if (pCrossRS == 0) {
            return isIntersect(r, s, p);
        }
        if (qCrossRS == 0) {
            return isIntersect(r, s, q);
        }

        return false;
    }

    private double cross(Point2D p, Point2D q, Point2D r) {
        return (q.getX() - p.getX()) * (r.getY() - p.getY())
                - (q.getY() - p.getY()) * (r.getX() - p.getX());
    }

    private boolean isIntersect(Point2D p, Point2D q, Point2D r) {
        return (cross(p, q, r) == 0 && dot(r, p, q) <= 0);
    }

    private double dot(Point2D p, Point2D q, Point2D r) {
        return (q.getX() - p.getX()) * (r.getX() - p.getX())
                - (q.getY() - p.getY()) * (r.getY() - p.getY());
    }

    /**
     * @return the graph
     */
    public mxGraph getGraph() {
        return graph;
    }

    /**
     * @return the automata
     */
    public Automata getAutomata() {
        return automata;
    }

    /**
     * @return the cellTable
     */
//    public Hashtable<mxCell, Object> getCellTable() {
//        return cellTable;
//    }
}
