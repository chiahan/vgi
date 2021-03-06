package vgi.display;

import vgi.layout.feature.PreProcess;
import vgi.layout.circular.CircleLayout;
import vgi.layout.linear.LinearLayout;
import vgi.layout.edgerouting.SingleVertexEdgesLayout;
import vgi.layout.edgerouting.WeightedVisibilityGraph;
import vgi.layout.edgerouting.EdgeRoutingBranchingLayout;
import vgi.layout.edgerouting.EdgePropertiesPanel;
import vgi.layout.edgerouting.EdgeRoutingMinCross;
import vgi.layout.edgerouting.EdgeRoutingLayout;
import vgi.layout.feature.ClusterPreProcess;
import vgi.layout.feature.TreeLayout;
import vgi.layout.feature.ClusteringLayout;
import vgi.automata.InitialFinalWeight;
import vgi.automata.Transition;
import vgi.automata.StateInterface;
import vgi.automata.State;
import vgi.automata.Automata;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.swing.handler.mxEdgeHandler;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import vgi.automata.WeightedRegularExpression;
import vgi.automata.*;
import vgi.automata.TransitionInterface.GeometricData;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/*
 * JgraphXInternalFrame.java
 *
 * Created on 2012/2/18, 下午 09:19:47
 */
/**
 *
 * @author wkren
 */
public class JgraphXInternalFrame extends javax.swing.JInternalFrame {

    protected static class StateValue {

        public String name;
        public List<State> history;

        @Override
        public String toString() {
            return this.name;
        }
    }  // End protected static class StateValue

    protected static class TransitionValue {

        public WeightedRegularExpression label;
        public List<Transition> history;

        @Override
        public String toString() {
            return this.label.toString();
        }
    }  // End protected static class TransitionValue

    /**
     * Creates new form JgraphXInternalFrame
     */
    public JgraphXInternalFrame(JSplitPane infoSplitPane, final mxGraph graph,
            Automata automata,String filename, VGI vgi_) {
        super(automata.getName(),
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable

        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);  
        
        initComponents();

        this.infoSplitPane = infoSplitPane;
        this.graph = graph;
        this.visibilityGraph = null;//new WeightedVisibilityGraph();
        this.automata = automata;
        //currentFile=file;
        setTitle(filename);
        
//        System.out.println("title:"+title);
        initGraph();
        installRepaintListener();
        installListeners();
        installInternalFrameListeners();

        if (automata != null) {
            initAutomata();
        }

        setupUndoRedo();
        vgi = vgi_;

    }

    private void initGraph() {
        graph.setDisconnectOnMove(false);
        graph.setSplitEnabled(false);
        graph.setCellsDisconnectable(false);
        graph.setGridEnabled(false);
        //graph.setResetEdgesOnMove(true);
        
        graphComponent = getGraphComponent();
        graphComponent.setConnectable(false);
        graphComponent.getViewport().setBackground(Color.WHITE);

        graphOutline = new mxGraphOutline(graphComponent);

        if ((topPanel != null) && (bottomPanel != null)) {
            this.topPanel = (JPanel) infoSplitPane.getTopComponent();
            this.bottomPanel = (JPanel) infoSplitPane.getBottomComponent();
        }

        this.setBounds(xOffset * openFrameCount, yOffset * openFrameCount, 800, 600);
        openFrameCount++;
        this.getContentPane().add(graphComponent, java.awt.BorderLayout.CENTER);

        //cellTable = new Hashtable<Integer, mxCell>();
        cellTable = new Hashtable<mxCell, Object>();
    }

    private void initAutomata() {
        setupStates();

		Iterator<mxCell> iterateCells = this.cellTable.keySet().iterator();
		while (iterateCells.hasNext()) {
			mxICell cell = iterateCells.next();
			if (!(cell.isVertex())) {
				continue;
			}
			if (this.visibilityGraph != null) {
				this.visibilityGraph.addRoadblock(cell);
			}
		}  // End while (iterateCells.hasNext())

		setupTranitions();

		Object[] edges = this.graph.getChildEdges(graph.getDefaultParent());
        for (int index = 0; index < edges.length; index++) {
            mxCell edge=(mxCell)edges[index];
            List<mxPoint> ptList=edge.getGeometry().getPoints();
            if ((ptList == null) || (ptList.isEmpty())) {
                resetControlPoint((mxCell)edges[index]);
            }
        }
        
        setupInitialFinal();
        if (!(this.hasGeometricData)) {
            this.doCircleLayout();
        }
        this.graph.refresh();
    }

    private void setupUndoRedo() {
        undoManager = new mxUndoManager();

        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        // Keeps the selection in sync with the command history
        mxIEventListener undoHandler = new mxIEventListener() {

            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableEdit.mxUndoableChange> changes =
                        ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));

                changedCell = graph.getSelectionCells();
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);
    }

    protected void installRepaintListener() {
        getGraphComponent().getGraph().addListener(mxEvent.REPAINT,
                new mxIEventListener() {

                    @Override
                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (getGraphComponent().getTripleBuffer() != null) ? ""
                                : " (unbuffered)";
                        mxRectangle dirty = (mxRectangle) evt.getProperty("region");
                    }
                });
    }

    protected void installListeners() {
        
        graph.addListener(mxEvent.MOVE_CELLS, new mxIEventListener() {

            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells = (Object[]) evt.getProperty("cells");
                Double dx = (Double) evt.getProperty("dx");
                Double dy = (Double) evt.getProperty("dy");

                //System.out.println("Cells moved: " + evt.getProperties());
                updateInitialFinal(cells, new Point2D.Double(dx, dy));
                updateControlPoint(cells,new Point2D.Double(dx,dy));
                setModified(true);
                
            }
        });
       graph.addListener(mxEvent.CHANGE, new mxIEventListener() {

            public void invoke(Object sender, mxEventObject evt) {
                setModified(true);
                
            }
        });
        // Installs mouse wheel listener for zooming
        MouseWheelListener wheelTracker = new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    JgraphXInternalFrame.this.formMouseWheelMoved(e);
                }
            }
        };

        // Handles mouse wheel events in the outline and graph component
        getGraphOutline().addMouseWheelListener(wheelTracker);
        getGraphComponent().addMouseWheelListener(wheelTracker);


        // Installs the popup menu in the graph component
        getGraphComponent().getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                isPopupTrigger = e.isPopupTrigger();
                if (graphComponent.getCellEditor() != null) {
                    graphComponent.getCellEditor().stopEditing(true);
                }
            }

            /**
             *
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean selected = !graph.isSelectionEmpty();
                mxCell selectedCell = (mxCell) graph.getSelectionCell();

                boolean edgeSelected = false;
                boolean vertexSelected = false;
                boolean controlPointSelected = false;
                
                boolean isloop=false;
                
                if (selected) {
                    DisplayUtil display = new DisplayUtil(graph, automata, cellTable);

                    edgeSelected = selectedCell.isEdge();
                    if (edgeSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell,
                                display.cellToTransition(selectedCell),
                                display, JgraphXInternalFrame.this));

                        mxEdgeHandler hand = (mxEdgeHandler) graphComponent.getSelectionCellsHandler().getHandler(selectedCell);
                        selectedHandlerIndex = hand.getIndex();
                        //System.out.println("handler index:"+selectedHandlerIndex);
                        if (selectedHandlerIndex > 0) {
                            controlPointSelected = true;
                        }


                        addTransitionFromMenuItem.setVisible(false);
                        addTransitionToMenuItem.setVisible(false);
                        
                        mxCell source=(mxCell)selectedCell.getSource();
                        mxCell target=(mxCell)selectedCell.getTarget();
                        if(source==target) isloop=true;
                        
                    }

                    vertexSelected = selectedCell.isVertex();
                    if (vertexSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(selectedCell,
                                JgraphXInternalFrame.this.cellToState(selectedCell),
                                display, JgraphXInternalFrame.this));

                        addTransitionFromMenuItem.setVisible(
                                (transitionFrom == null) ? true : false);
                        addTransitionToMenuItem.setVisible(
                                (transitionFrom == null) ? false : true);
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                            new AutomataPropertiesPanel(
                            new DisplayUtil(graph, automata, cellTable), JgraphXInternalFrame.this));
                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                    graph.setSelectionCell(null);
                }
                addStateMenuItem.setVisible(!selected);
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected && !isloop);
                deleteControlPointMenuItem.setVisible(controlPointSelected && !isloop);
                resetControlPointMenuItem.setVisible(edgeSelected);
                routeEdgeMenuItem.setVisible(edgeSelected);
                routeEdgeWVGMenuItem.setVisible(false);

                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);




                JgraphXInternalFrame.this.validate();

                maybeShowPopup(e);

                graph.refresh();
            }

            private void maybeShowPopup(MouseEvent e) {
                if (isPopupTrigger || e.isPopupTrigger()) {
                    isPopupTrigger = false;

                    Point pt = e.getPoint();
                    pt.x -= graphComponent.getHorizontalScrollBar().getValue();
                    pt.y -= graphComponent.getVerticalScrollBar().getValue();

                    //Point pt=graphComponent.getGraphHandler().convertPoint(e.getPoint());
                    Point ptc = SwingUtilities.convertPoint(graphComponent, e.getPoint(),
                            graphComponent.getGraphControl());
                    popPoint = graphComponent.getPointForEvent(e);

                    popMouseX = e.getX();
                    popMouseY = e.getY();
                   // System.out.println(e.getPoint().toString() + "pop up:" + pt.toString() + "  " + mxpt.toString());


                    getGraphPopupMenu().show(getGraphComponent(), pt.x, pt.y);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // Installs a mouse motion listener to display the mouse location
        getGraphComponent().getGraphControl().addMouseMotionListener(
                new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        mouseLocationChanged(e);
                        boolean selected = !graph.isSelectionEmpty();
                        mxCell selectedCell = (mxCell) graph.getSelectionCell();

                        boolean vertexSelected = false;

                        if (selected) {
                            vertexSelected = selectedCell.isVertex();
                            if (vertexSelected) {
                                //update position
                               /*
                                 * State selectedState =
                                 * cellToState(selectedCell);
                                 * if(selectedState!=null){ State.GeometricData
                                 * geo=new State.GeometricData();
                                 * geo.location=new
                                 * Point2D.Double(e.getX(),e.getY());
                                 * selectedState.setGeometricData(geo);
                                 * System.out.println("update location");
                                 * setModified(true); }
                                 */
                            }
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
//                        mouseDragged(e);
                    }
                });
    }

    public void installInternalFrameListeners() {
        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
				JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
						new AutomataPropertiesPanel(
						new DisplayUtil(graph, automata, cellTable), JgraphXInternalFrame.this));

				JgraphXInternalFrame.this.infoSplitPane.setBottomComponent(
						graphOutline);
				JgraphXInternalFrame.this.graph.clearSelection();
            }

            public void internalFrameClosing(InternalFrameEvent e) {
                JgraphXInternalFrame.this.infoSplitPane.setTopComponent(topPanel);
                JgraphXInternalFrame.this.infoSplitPane.setBottomComponent(bottomPanel);
            }
        });
    }

    protected void mouseLocationChanged(MouseEvent e) {
//        status(e.getX() + ", \n" + e.getY());
    }

    private void setupStates() {
        Iterator<State> iterator = this.automata.getAllStates().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next();
            this.addState(state);
        }  // End while (stateIterator.hasNext())
    }

    private void setupTranitions() {
        Iterator<Transition> iterator = this.automata.getAllTransitions().iterator();
        while (iterator.hasNext()) {
            Transition transition = iterator.next();
            this.addTransition(transition);
        }  // End while (transitionIterator.hasNext())
    }

    private void setupInitialFinal() {
        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);

        Object parent = this.graph.getDefaultParent();
        Iterator<mxCell> cellIterator = this.initialFinalCells.iterator();
        while (cellIterator.hasNext()) {
            mxCell vertex = cellIterator.next();
            mxGeometry geometry = vertex.getGeometry();
            if (geometry == null) {
                continue;
            }

            State state = this.cellToState(vertex);

            InitialFinalWeight initialFinalWeight = state.getInitialWeight();
            if (initialFinalWeight != null) {
                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
                if (geometricData == null) {
                    display.showInitialFinal(parent, initialFinalWeight, vertex, false);
                } else {  // End if (geometricData == null)
                    if (geometricData.offset == null) {
                        display.showInitialFinal(parent, initialFinalWeight, vertex, false);
                    } else {  // End if (geometricData.offset == null)
                        double x = geometry.getCenterX() + geometricData.offset.getX();
                        double y = geometry.getCenterY() + geometricData.offset.getY();
                        mxPoint point = new mxPoint(x, y);
                        mxCell edge = (mxCell) this.graph.insertEdge(parent, null, initialFinalWeight, null, vertex);
                        Object[] cells = {edge};
                        this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
        this.graph.setCellStyles("fontSize", String.valueOf(JgraphXInternalFrame.defaultFontSize), cells);
        this.graph.setCellStyles("textShape","default", cells);
                        mxGeometry edgeGeometry = edge.getGeometry();
                        if (edgeGeometry != null) {
                            edgeGeometry.setSourcePoint(point);
                            if (geometricData.labelPosAndDist == null) {
                                edgeGeometry.setY(DEFAULT_LABEL_DISTANCE);
                            } else {
                                edgeGeometry.setX(geometricData.labelPosAndDist.getX());
                                edgeGeometry.setY(geometricData.labelPosAndDist.getY());
                            }
                            if (geometricData.labelOffset == null) {
                                edgeGeometry.setOffset(null);
                            } else {
                                edgeGeometry.setOffset(new mxPoint(geometricData.labelOffset));
                            }
                        }  // End if (edgeGeometry != null)
                    }  // End else part of if (geometricData.offset == null)
                }  // End else part of if (geometricData == null)
            }  // End if (initialFinalWeight != null)

            initialFinalWeight = state.getFinalWeight();
            if (initialFinalWeight != null) {
                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
                if (geometricData == null) {
                    display.showInitialFinal(parent, initialFinalWeight, vertex, true);
                } else {  // End if (geometricData == null)
                    if (geometricData.offset == null) {
                        display.showInitialFinal(parent, initialFinalWeight, vertex, true);
                    } else {  // End if (geometricData.offset == null)
                        double x = geometry.getCenterX() + geometricData.offset.getX();
                        double y = geometry.getCenterY() + geometricData.offset.getY();
                        mxPoint point = new mxPoint(x, y);
                        mxCell edge = (mxCell) this.graph.insertEdge(parent, null, initialFinalWeight, vertex, null);
                        Object[] cells = {edge};
                        this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
        this.graph.setCellStyles("fontSize", String.valueOf(JgraphXInternalFrame.defaultFontSize), cells);
        this.graph.setCellStyles("textShape","default", cells);
                        mxGeometry edgeGeometry = edge.getGeometry();
                        if (edgeGeometry != null) {
                            edgeGeometry.setTargetPoint(point);
                            if (geometricData.labelPosAndDist == null) {
                                edgeGeometry.setY(DEFAULT_LABEL_DISTANCE);
                            } else {
                                edgeGeometry.setX(geometricData.labelPosAndDist.getX());
                                edgeGeometry.setY(geometricData.labelPosAndDist.getY());
                            }
                            if (geometricData.labelOffset == null) {
                                edgeGeometry.setOffset(null);
                            } else {
                                edgeGeometry.setOffset(new mxPoint(geometricData.labelOffset));
                            }
                        }  // End if (edgeGeometry != null)
                    }  // End else part of if (geometricData.offset == null)
                }  // End else part of if (geometricData == null)
            }  // End if (initialFinalWeight != null)
        }  // End while (cellIterator.hasNext())
    }

    private void updateInitialFinal(Object[] cells, Point2D offset) {
        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
        int length = cells.length;
        mxCell vertex, edge;
        for (int i = 0; i < length; i++) {
            vertex = (mxCell) cells[i];
            Object edges[] = graph.getEdges(vertex);
            int count = edges.length;
            for (int j = 0; j < count; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                    /*display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), false);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);*/
                    
                    
                    double dx=offset.getX();
                    double dy=offset.getY();
                    /*List<mxPoint> ptlist=edge.getGeometry().getPoints();
                    for(mxPoint pt:ptlist){
                           pt.setX(pt.getX()+dx);
                           pt.setY(pt.getY()+dy);
                    }
                    edge.getGeometry().setPoints(ptlist);*/
                    mxPoint tpt=edge.getGeometry().getTerminalPoint(true);
                    tpt.setX(tpt.getX()+dx);
                    tpt.setY(tpt.getY()+dy);
                    edge.getGeometry().setTerminalPoint(tpt, true);
                    
                } else if (edge.getTerminal(false) == null) {
                    /*display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), true);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);*/
                    double dx=offset.getX();
                    double dy=offset.getY();
                    mxPoint tpt=edge.getGeometry().getTerminalPoint(false);
                    tpt.setX(tpt.getX()+dx);
                    tpt.setY(tpt.getY()+dy);
                    edge.getGeometry().setTerminalPoint(tpt, false);
                    
                    
                }
            }
        }
    }
    
 
    private void updateControlPoint(Object[] cells, Point2D offset) {
        int length = cells.length;
        mxCell vertex, edge;
        double dx=offset.getX();
        double dy=offset.getY();
                    
        for (int i = 0; i < length; i++) {
        
            vertex = (mxCell) cells[i];
            
            mxPoint oldVertexPoint=new mxPoint();
            oldVertexPoint.setX(vertex.getGeometry().getCenterX()-dx);
            oldVertexPoint.setY(vertex.getGeometry().getCenterY()-dy);
                
            
            mxPoint newVertexPoint=new mxPoint();
            newVertexPoint.setX(vertex.getGeometry().getCenterX());
            newVertexPoint.setY(vertex.getGeometry().getCenterY());
            
            Object edges[] = graph.getEdges(vertex);
            int count = edges.length;
            
            
            for (int j = 0; j < count; j++) {
                edge = (mxCell) edges[j];
                
                mxCell source=(mxCell)edge.getSource();
                mxCell target=(mxCell)edge.getTarget();
                
                mxPoint targetPoint;
                if(target==vertex) 
                    targetPoint=new mxPoint(source.getGeometry().getCenterX(),source.getGeometry().getCenterY());
                else  targetPoint=new mxPoint(target.getGeometry().getCenterX(),target.getGeometry().getCenterY());
                
               // if(source==target){ // only loop
                    mxGeometry geo=edge.getGeometry();
                    if (geo != null){
                            // Resets the control points
                            List<mxPoint> points = geo.getPoints();
                            
                            if (points != null && !points.isEmpty()){

                                    List<mxPoint> ptlist=geo.getPoints();
                                    for(mxPoint pt:ptlist){
                                        
                                       /* double deltaX=targetPoint.getX()-oldVertexPoint.getX();
                                        double deltaY=targetPoint.getY()-oldVertexPoint.getY();
                                        System.out.println("deltaX "+deltaX+" deltaY "+deltaY);
                                        double newX,newY;
                                        if(Math.abs(deltaX)>0.01){
                                            newX=newVertexPoint.getX()+(targetPoint.getX()-newVertexPoint.getX())*(pt.getX()-oldVertexPoint.getX())/deltaX;
                                            System.out.println((targetPoint.getX()-newVertexPoint.getX())+" * "+(pt.getX()-oldVertexPoint.getX())+" / "+deltaX);
                                        
                                        }else
                                            newX=newVertexPoint.getX()+(targetPoint.getX()-newVertexPoint.getX())*(pt.getY()-oldVertexPoint.getY())/deltaY;
                                        
                                        if(Math.abs(deltaY)>0.001){
                                            newY=newVertexPoint.getY()+(targetPoint.getY()-newVertexPoint.getY())*(pt.getY()-oldVertexPoint.getY())/deltaY;
                                            System.out.println((targetPoint.getY()-newVertexPoint.getY())+" * "+(pt.getY()-oldVertexPoint.getY())+" / "+deltaY);
                                        
                                        }else
                                            newY=newVertexPoint.getY()+(targetPoint.getY()-newVertexPoint.getY())*(pt.getX()-oldVertexPoint.getX())/deltaX;
                                        
                                        pt.setX(newX);
                                        pt.setY(newY);
                                        */
                                        
                                        double a=targetPoint.getX()-oldVertexPoint.getX();
                                        double b=targetPoint.getY()-oldVertexPoint.getY();
                                        
                                        double theta=-Math.atan2(b, a);
                                        //theta=Math.toDegrees(theta);
                                        
                                        double len=Math.sqrt(a*a+b*b);
                                                
                                        double c1=pt.getX()-oldVertexPoint.getX();
                                        double c2=pt.getY()-oldVertexPoint.getY();
                                        
                                        double rc1=Math.cos(theta)*c1-Math.sin(theta)*c2;
                                        double rc2=Math.sin(theta)*c1+Math.cos(theta)*c2;
                                        
                                        double area=len*rc2;
                                     //   System.out.println("\narea: "+area+"\n theta: "+Math.toDegrees(theta) +" len: "+len+" c: "+rc1+" , "+rc2);
                                       
                                        double xc1=Math.cos(theta)*a-Math.sin(theta)*b;
                                     //   System.out.println(" X: "+xc1);
                                        double newa=targetPoint.getX()-newVertexPoint.getX();
                                        double newb=targetPoint.getY()-newVertexPoint.getY();
                                        
                                        
                                        double newtheta=-Math.atan2(newb, newa);
                                        
                                        double newlen=Math.sqrt(newa*newa+newb*newb);
                                        
                                        double newxc1=Math.cos(newtheta)*newa-Math.sin(newtheta)*newb;
                                       // System.out.println(" newX: "+newxc1);
                                        double newrc2=rc2;
                                        /*if(newlen>len)
                                            newrc2=area/(newlen*0.8);
                                        else
                                            newrc2=rc2/len*(newlen*1.2);*/
                                        double newrc1=rc1/xc1*newxc1;
                                       // System.out.println("new theta: "+Math.toDegrees(newtheta) +" len: "+newlen+" c: "+newrc1+" , "+newrc2);
                                        
                                        double newc1=Math.cos(-newtheta)*newrc1-Math.sin(-newtheta)*newrc2;
                                        double newc2=Math.sin(-newtheta)*newrc1+Math.cos(-newtheta)*newrc2;
                                        
                                        newc1=newc1+newVertexPoint.getX();
                                        newc2=newc2+newVertexPoint.getY();
                                        
                                        pt.setX(newc1);
                                        pt.setY(newc2);
                                        System.out.println("update control pt: "+pt);
                                    }
                                    
                                    edge.getGeometry().setPoints(ptlist);
                            }
                    }
               // }
            }
        }
        setModified(true);
        undoStack.push(STATUS_CHANGE);
    
    }   
    
    public void updateStateSize(Object[] cells){
        
        
    }

    public void addState(double x, double y) {
        State newState = new State();
        State.GeometricData geo = new State.GeometricData();
        geo.location = new Point2D.Double(x, y);
        geo.shape="ellipse";
        geo.size=new Point2D.Double(vertexWidth,vertexWidth);
        newState.setGeometricData(geo);
        
        automata.addState(newState);
        mxCell vertex = createVertex(x, y, newState.getName(),vertexWidth,vertexWidth,null);
        cellTable.put(vertex, newState);

        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
        infoSplitPane.setTopComponent(
                new StatePropertiesPanel((mxCell) vertex, newState, display, JgraphXInternalFrame.this));

//        System.out.println("add state at" + x + "," + y);
//        System.out.println("total states:" + automata.getAllStates().size());

        setModified(true);
        undoStack.push(STATUS_ADD);

		if (this.visibilityGraph != null) {
			this.visibilityGraph.addRoadblock(vertex);
		}
    }

    public void addState(State state) {
        StateInterface.GeometricData geometricData = state.getGeometricData();
        double x = 0;
        double y = 0;
        double w=0;
        double h=0;
        String shape=null;
        if ((geometricData == null) || (geometricData.location == null)) {
            this.hasGeometricData = false;
        } else {
            x = geometricData.location.getX();
            y = geometricData.location.getY();
            
            if(geometricData.size!=null){
                w=geometricData.size.getX();
                h=geometricData.size.getY();
//                System.out.println("get size:"+geometricData.size.toString());
            }
            shape=geometricData.shape;
        }

        mxCell vertex = createVertex(x, y, state.getName(),w,h,shape);
        cellTable.put(vertex, state);


        StateInterface.DrawingData drawingdata = state.getDrawingData();
        if (drawingdata != null) {
            graph.setCellStyles("fillColor", drawingdata.fillColor);
            graph.setCellStyles("strokeColor", drawingdata.strokeColor);
            graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.strokeWidth));

        }


        if ((state.getInitialWeight() != null) || (state.getFinalWeight() != null)) {
            this.initialFinalCells.add(vertex);
        }

//        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
//        infoSplitPane.setTopComponent(
//                new StatePropertiesPanel((mxCell) vertex, 
//                JgraphXInternalFrame.this.cellToState(vertex), display));
//        System.out.println("add state at (" + x + "," + y + ").");
    }  // End public void addState(State state)

    private mxCell createVertex(double x, double y, String name,double w,double h,String shape) {
        Object parent = graph.getDefaultParent();
        int id = cellTable.size();

        if(w<=0) w=vertexWidth;
        if(h<=0) h=vertexWidth;
        if(shape==null) shape="ellipse";
        
        //Object vertex = graph.insertVertex(parent, Integer.toString(id), name,
        //        x - w / 2, y - h / 2, w, h, "shape=ellipse;perimeter=ellipsePerimeter;");
        Object vertex = graph.insertVertex(parent, Integer.toString(id), name,
                x - w / 2, y - h / 2, w, h, "shape="+shape+";perimeter="+shape+"Perimeter;fontSize="+defaultFontSize+";");
        
        graph.setSelectionCell(vertex);
        ((mxCell) vertex).setConnectable(false);

        return (mxCell) vertex;
    }

    public void addTransition(mxCell source, mxCell target) {
        //Object parent = graph.getDefaultParent();
        WeightedRegularExpression.Atomic expression =
                WeightedRegularExpression.Atomic.createAtomic(automata);
        expression.setSymbol(expression.getAlphabet().allSymbols.get(0));
       
        /*Object e = graph.insertEdge(parent, null, expression, source, target, "shape=curve");
        ArrayList<mxPoint> points = new ArrayList<mxPoint>();
        ((mxCell) e).getGeometry().setPoints(points);
        ((mxCell) e).getGeometry().setY(DEFAULT_LABEL_DISTANCE);
        */
        Transition newTrans = new Transition();
       // cellTable.put((mxCell) e, newTrans);
        newTrans.setSourceState(cellToState(source));
        newTrans.setTargetState(cellToState(target));
        newTrans.setLabel(expression);
        
        if(source==target){
            TransitionInterface.GeometricData geometricData = newTrans.getGeometricData();
            geometricData.controlPoints.add(new Point2D.Double(source.getGeometry().getCenterX()+source.getGeometry().getWidth(),
                                 source.getGeometry().getCenterY()));
            
        }
        automata.addTransition(newTrans);

        mxCell edge = addTransition(newTrans);
        
//        System.out.println("total trans:" + automata.getAllTransitions().size());
		if (this.vgi.getRouteEdgeWhileAdding()) {
			EdgeRoutingMinCross edgeRoutingMinCross = new EdgeRoutingMinCross(this.graph);
			edgeRoutingMinCross.route(edge);
		}
        setModified(true);
        undoStack.push(STATUS_ADD);
    }

    public mxCell addTransition(Transition transition) {
        mxCell source = null, target = null;
        Enumeration keys = cellTable.keys();
        while (keys.hasMoreElements()) {
            mxCell keyCell = (mxCell) keys.nextElement();
            if (cellTable.get(keyCell) == transition.getSourceState()) {
                source = keyCell;
            }

            if (cellTable.get(keyCell) == transition.getTargetState()) {
                target = keyCell;
            }

            if ((source != null) && (target != null)) {
                break;
            }
        }

        mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
                null, transition.getLabel(), source, target,
                "shape=curve;fontSize="+defaultFontSize+";textShape=default;"));
        cellTable.put(edge, transition);
        mxGeometry geometry = edge.getGeometry();
        if (geometry == null) {
            geometry = new mxGeometry();
           // ArrayList<mxPoint> points = new ArrayList<mxPoint>();
           // geometry.setPoints(points);
            edge.setGeometry(geometry);
            
        }
        
        
        
        TransitionInterface.GeometricData geometricData = transition.getGeometricData();
        if (geometricData == null) {
            geometricData = new TransitionInterface.GeometricData();
        }
        if (geometricData.labelPosAndDist == null) {
            geometry.setX(0);
            geometry.setY(DEFAULT_LABEL_DISTANCE);
        } else {
            geometry.setX(geometricData.labelPosAndDist.getX());
            geometry.setY(geometricData.labelPosAndDist.getY());
        }
        if (geometricData.labelOffset == null) {
            geometry.setOffset(null);
        } else {
            geometry.setOffset(new mxPoint(geometricData.labelOffset));
        }
        if ((geometricData.controlPoints == null)
                || (geometricData.controlPoints.isEmpty())) {
            geometry.setPoints(new ArrayList<mxPoint>());
        } else {
            List<mxPoint> points = new ArrayList<mxPoint>();
            Iterator<Point2D> iterateControlPoints = geometricData.controlPoints.iterator();
            while (iterateControlPoints.hasNext()) {
                Point2D controlPoint = iterateControlPoints.next();
                points.add(new mxPoint(controlPoint));
            }  // End while (iterateControlPoints.hasNext())
            geometry.setPoints(points);
        }

        /*if(source==target){
            addControlPoint(edge,source.getGeometry().getCenterX()+source.getGeometry().getWidth(),
                                 source.getGeometry().getCenterY());
        }
        */
        graph.setSelectionCell(edge);
        TransitionInterface.DrawingData drawingdata = transition.getDrawingData();
        if (drawingdata != null) {
            graph.setCellStyles("strokeColor", drawingdata.strokeColor);
            graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.strokeWidth));
            graph.setCellStyles("startArrow", drawingdata.startArrow);
            graph.setCellStyles("endArrow", drawingdata.endArrow);

        }
       
		if (this.visibilityGraph != null) {
			this.visibilityGraph.addHindrance(edge);
		}
		return edge;
    }  // End public mxCell addTransition(Transition transition)

    public void addControlPoint() {
        addControlPoint((mxCell) getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY),
                popPoint.getX(), popPoint.getY());
        setModified(true);
        undoStack.push(STATUS_CHANGE);
    
    }

    public void addControlPoint(mxCell cell, double x, double y) {
//        System.out.println("add Ctrl pt at" + x + "," + y);
        List<mxPoint> points = cell.getGeometry().getPoints();
        if(points==null) points=new ArrayList<mxPoint>();
        points.add(new mxPoint(x, y));

        //to sort
        if (points.size() > 1) {
            Collections.sort(points, new CompareCtrlPoint());
            if (cell.getSource().getGeometry().getX()
                    > cell.getTarget().getGeometry().getX()) {
                Collections.reverse(points);
            }
        }


        cell.getGeometry().setPoints(points);
        getGraphComponent().refresh();

    }

    public void deleteControlPoint() {
        deleteControlPoint((mxCell) getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY),
                selectedHandlerIndex);
    }

    public void deleteControlPoint(mxCell cell, int index) {
        
//        System.out.println("delete Ctrl pt at" + index);
        List<mxPoint> points = cell.getGeometry().getPoints();
        points.remove(index - 1);

        cell.getGeometry().setPoints(points);
        getGraphComponent().refresh();

        setModified(true);
        undoStack.push(STATUS_CHANGE);
        
    }

    public void resetControlPoint() {

        resetControlPoint((mxCell) getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY));
        setModified(true);
        undoStack.push(STATUS_CHANGE);
    }

    public void resetControlPoint(mxCell cell) {
        
        mxCell source=(mxCell)cell.getSource();
        mxCell target=(mxCell)cell.getTarget();
        
        List<mxPoint> points = cell.getGeometry().getPoints();
        if (points == null) {
            points = new ArrayList<mxPoint>();
        } else {
            points.clear();
        }

        if(source==target){ //loop
            mxPoint loopCtrlPt=new mxPoint();
            loopCtrlPt.setX(source.getGeometry().getCenterX()+source.getGeometry().getWidth());
            loopCtrlPt.setY(source.getGeometry().getCenterY());
            points.add(loopCtrlPt);
            
            
        }
            cell.getGeometry().setPoints(points);
            getGraphComponent().refresh();

         
        
    }

    public void doCircleLayout() {
        mxCircleLayout layout = new mxCircleLayout(this.graph);
        layout.setResetEdges(true);
        layout.setMoveCircle(true);
        layout.setX0(100);
        layout.setY0(100);
        Object parent = this.graph.getDefaultParent();
        layout.execute(this.graph.getDefaultParent());
        Object objects[] = this.graph.getChildEdges(parent);
        for (Object object : objects) {
                if (!(object instanceof mxICell)) {
                        continue;
                }
                mxICell edge = (mxICell) object;
                if (!(edge.isEdge())) {
                        continue;
                }
                Object value = edge.getValue();
                if (!(value instanceof InitialFinalWeight)) {
                        continue;
                }
                InitialFinalWeight initialFinalWeight = (InitialFinalWeight) value;
                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
                double offsetX = 50;
                double offsetY = 0;
                if ((geometricData != null) && (geometricData.offset != null)) {
                        offsetX = geometricData.offset.x;
                        offsetY = geometricData.offset.y;
                }
                mxGeometry geometry = edge.getGeometry();
                if (geometry == null) {
                        continue;
                }
                mxICell source = edge.getTerminal(true);
                mxICell target = edge.getTerminal(false);
                if (source == null) {
                        if (target == null) {
                                continue;
                        }
                        mxGeometry vertexGeometry = target.getGeometry();
                        geometry.setSourcePoint(
                                        new mxPoint(
                                        vertexGeometry.getCenterX() + offsetX,
                                        vertexGeometry.getCenterY() + offsetY));
                } else if (target == null) {
                        mxGeometry vertexGeometry = source.getGeometry();
                        geometry.setTargetPoint(
                                        new mxPoint(
                                        vertexGeometry.getCenterX() + offsetX,
                                        vertexGeometry.getCenterY() + offsetY));
                }
        }  // End for (Object object : objects)
        SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(this.graph);
        singleVertexEdgesLayout.execute(this.graph.getDefaultParent());
    }  // End public void doCircleLayout()

    public void doHierarchicalLayout() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(this.graph);
        layout.setOrientation(SwingConstants.WEST);
        layout.setFineTuning(false);
        layout.setDisableEdgeStyle(false);
        layout.execute(this.graph.getDefaultParent());
        
        EdgeRoutingLayout edgeRoute = new EdgeRoutingLayout(this.graph);
        edgeRoute.execute(this.graph.getDefaultParent());
        
        EdgeRoutingBranchingLayout layout2 = new EdgeRoutingBranchingLayout(this.graph);
        layout2.execute(this.graph.getDefaultParent());
        
        
    }  // End public void doHierarchicalLayout()

    public void doFeatureLayout() {

      this.graph.getModel().beginUpdate();
		try {
	
                    ClusterPreProcess clusterDetector = new ClusterPreProcess(this.graph);
                
                Map<mxCell, List<mxCell>> ClusterFeatureNode = new HashMap<mxCell, List<mxCell>>();
                Map<mxCell, List<mxCell>> CycleFeatureNode = new HashMap<mxCell, List<mxCell>>();
               
                // create a edges cell list of a graph used for check in clusteringlayout
                int childCount = this.graph.getModel().getChildCount(this.graph.getDefaultParent());

	            
                ClusterFeatureNode = clusterDetector.getFeatureNodeList();
                        
                List<mxCell> childs = new ArrayList<mxCell>();
                      
                // group the nodes in the same cluster : 
                        
                        Collection collection = ClusterFeatureNode.keySet();
                        Iterator iterator = collection.iterator();
                        while(iterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)iterator.next();
                            childs = ClusterFeatureNode.get(FeatureNode);
                            for(mxCell child : childs){
                       //         System.out.print("\n Child ID : "+child.getId()+"\n");
                                (FeatureNode).insert(child);
                            }
                        }
                        
                        iterator = collection.iterator();
                        
                        // if there's no feature node detect by clustering, then detect the cycle 
                        Iterator cycleIterator;
                        Collection cycleCollection;
                       
                        boolean isTreeOrHierarchical = true;
                        if(!iterator.hasNext()){
                                             
                           PreProcess cycleFilter = new PreProcess(this.graph);
                            CycleFeatureNode = cycleFilter.getFeatureNodeList();
                            cycleCollection = CycleFeatureNode.keySet();
                            cycleIterator = cycleCollection.iterator();
                            
                      
                            childs = new ArrayList<mxCell>();
                            
                            if(!cycleIterator.hasNext())
                                isTreeOrHierarchical = false;
                            
                            while(cycleIterator.hasNext()){
                           
                                    mxCell FeatureNode = (mxCell)cycleIterator.next();
                                    childs = CycleFeatureNode.get(FeatureNode);
                                    
                                    for(mxCell child : childs){
                                        (FeatureNode).insert(child);
                                    }
                             } // end of  (cycleIterator.hasNext())
                          
                        }
                   
              
                // execute the TreeLayout and ClusteringLayout : 
                if(isTreeOrHierarchical== true){        
                        TreeLayout treelayout = new TreeLayout(this.graph); 
                        treelayout.execute(graph.getDefaultParent());  
                       // treelayout.execute(cellTable.get("0"));
                }
                else if(isTreeOrHierarchical== false){
                        this.doHierarchicalLayout();
                }
             
                        
                        
                        iterator = collection.iterator();
                        
                          while(iterator.hasNext()) {
                              
                            ClusteringLayout clusterlayout = new ClusteringLayout(this.graph);
                            
                            mxCell FeatureNode = (mxCell)iterator.next();
                            
                            clusterlayout.setMoveCircle(true);
                            clusterlayout.setX0(FeatureNode.getGeometry().getX());
                            clusterlayout.setY0(FeatureNode.getGeometry().getY());
//                            System.out.print("\n X0 : "+FeatureNode.getGeometry().getX());
//                            System.out.print("\n Y0 : "+FeatureNode.getGeometry().getY());
                            clusterlayout.execute(FeatureNode);
                            
                        }
                          
                        
                        iterator = collection.iterator();
                        if(!iterator.hasNext()){
                           
                           cycleCollection = CycleFeatureNode.keySet();
                           cycleIterator = cycleCollection.iterator();
                            
                          while(cycleIterator.hasNext()) {
                              
                            CircleLayout circleLayout = new CircleLayout(this.graph);

                            mxCell FeatureNode = (mxCell)cycleIterator.next();
                            
                            circleLayout.setMoveCircle(true);
                            circleLayout.setX0(FeatureNode.getGeometry().getY());
                            circleLayout.setY0(FeatureNode.getGeometry().getX());
                     
                            circleLayout.setRadius(40);
                            circleLayout.execute(FeatureNode);
                        
                          }
                          
                          cycleIterator = cycleCollection.iterator();
                        
                          while(cycleIterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)cycleIterator.next();
                            this.graph.ungroupCells(new Object[]{FeatureNode});
                          }
                        }
                      
                        
                // ungroup the cluster :        
                          
                        iterator = collection.iterator();
                        while(iterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)iterator.next();
                            this.graph.ungroupCells(new Object[]{FeatureNode});
                        }
                     
                 for(int i = 0; i < childCount-1; ++i){
               
                       Object child = graph.getModel().getChildAt(this.graph.getDefaultParent(), i);
                       if(((mxCell)child).isVertex()){
                            mxCell cell=(mxCell)child;
                            Object[] edges=graph.getEdges(cell);
                            
                            for(int j=0;j<edges.length;++j){
                                mxCell edge=(mxCell)edges[j];
                                mxCell source=(mxCell)edge.getSource();
                                mxCell target=(mxCell)edge.getTarget();
                
                                if(target==source){
                                    ArrayList<mxPoint> points = new ArrayList<mxPoint>();
                                    mxPoint loopCtrlPt=new mxPoint();
                                    loopCtrlPt.setX(source.getGeometry().getCenterX());
                                    loopCtrlPt.setY(source.getGeometry().getCenterY()+source.getGeometry().getHeight());
                                    points.add(loopCtrlPt);

                                    edge.getGeometry().setPoints(points);
                                }
                                
                            }
                        }
                 }       
                        
                        
                        //EdgeRoutingLayout edgeRoute = new EdgeRoutingLayout(this.graph);
                        //edgeRoute.execute(this.graph.getDefaultParent());
                 EdgeRoutingBranchingLayout layout = new EdgeRoutingBranchingLayout(this.graph);
                 layout.execute(this.graph.getDefaultParent());
        
               } finally {
			this.graph.getModel().endUpdate();
		}
                
        
        
    }

    public void routeAllEdges2008() {
        EdgeRoutingLayout layout = new EdgeRoutingLayout(this.graph);
        layout.execute(this.graph.getDefaultParent());
    }  // End public void routeAllEdges2008()

    public void routeAllEdgesBranching() {
        EdgeRoutingBranchingLayout layout = new EdgeRoutingBranchingLayout(this.graph);
        layout.execute(this.graph.getDefaultParent());
        
        
        SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(this.graph);
	singleVertexEdgesLayout.execute(null);
    }  // End public void routeAllEdgesBranching()

    
    public void doLinearLayout(){
        
        LinearLayout layout=new LinearLayout(this.graph);
        layout.execute(this.graph.getDefaultParent());
        graph.refresh();
        
        Object objects[] = this.graph.getChildEdges(graph.getDefaultParent());
        for (Object object : objects) {
                if (!(object instanceof mxICell)) {
                        continue;
                }
                mxICell edge = (mxICell) object;
                if (!(edge.isEdge())) {
                        continue;
                }
                Object value = edge.getValue();
                if (!(value instanceof InitialFinalWeight)) {
                        continue;
                }
                InitialFinalWeight initialFinalWeight = (InitialFinalWeight) value;
                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
                double offsetX = 50;
                double offsetY = 0;
                if ((geometricData != null) && (geometricData.offset != null)) {
                        offsetX = geometricData.offset.x;
                        offsetY = geometricData.offset.y;
                }
                mxGeometry geometry = edge.getGeometry();
                if (geometry == null) {
                        continue;
                }
                mxICell source = edge.getTerminal(true);
                mxICell target = edge.getTerminal(false);
                if (source == null) {
                        if (target == null) {
                                continue;
                        }
                        mxGeometry vertexGeometry = target.getGeometry();
                        geometry.setSourcePoint(
                                        new mxPoint(
                                        vertexGeometry.getCenterX() + offsetX,
                                        vertexGeometry.getCenterY() + offsetY));
                } else if (target == null) {
                        mxGeometry vertexGeometry = source.getGeometry();
                        geometry.setTargetPoint(
                                        new mxPoint(
                                        vertexGeometry.getCenterX() + offsetX,
                                        vertexGeometry.getCenterY() + offsetY));
                }
        }  // End for (Object object : objects)
        SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(this.graph);
        singleVertexEdgesLayout.execute(this.graph.getDefaultParent());
        
        /*Object[] allCell=graph.getChildCells(this.graph.getDefaultParent());
        for(Object cell:allCell){
            mxCell edge=(mxCell)cell;
            if(edge.isEdge()){
                System.out.println(edge.getGeometry().getPoints());
            }
            
        }*/
        //routeAllEdgesBranching();
    }
    
    
    public Automata getAutomata() {

        Automata automata = new Automata();
        automata.setName(this.automata.getName());
        automata.setWritingData(this.automata.getWritingData());
        automata.setWeight(this.automata.getWeight());
        automata.setAlphabet(this.automata.getAlphabet());
        automata.setOutputAlphabet(this.automata.getOutputAlphabet());
        HashMap<mxCell, State> cellToStateMap = new HashMap<mxCell, State>();

        Object[] vertices = this.graph.getChildVertices(graph.getDefaultParent());

        for (int index = 0; index < vertices.length; index++) {

            if (!(vertices[index] instanceof mxCell)) {
                continue;
            }

            mxCell vertex = (mxCell) vertices[index];
            State state = new State();
            mxGeometry geometry = vertex.getGeometry();
            Point2D point2d = new Point2D.Double();
            point2d.setLocation(geometry.getCenterX(), geometry.getCenterY());
            StateInterface.GeometricData geometricData = new StateInterface.GeometricData();
            geometricData.location = point2d;
            
            Point2D sizepoint2d = new Point2D.Double();
            sizepoint2d.setLocation(geometry.getWidth(),geometry.getHeight());
            geometricData.size=sizepoint2d;
            state.setGeometricData(geometricData);
            
            Map<String, Object> styleList = graph.getCellStyle(vertex);
            state.setShape((String)styleList.get("shape"));
            
            StateInterface.DrawingData drawingData = new StateInterface.DrawingData();
            drawingData.fillColor = (String) styleList.get("fillColor");
            drawingData.strokeColor = (String) styleList.get("strokeColor");
            String width = (String) styleList.get("strokeWidth");
            if (width != null) {
                drawingData.strokeWidth = Float.valueOf(width);
            }

            //System.out.println("style:"+drawingData.fillColor+" "+drawingData.strokeColor+" "+drawingData.strokeWidth);

            state.setDrawingData(drawingData);

            automata.addState(state);
            cellToStateMap.put(vertex, state);
            Object object = vertex.getValue();
            if (object instanceof String) {
                state.setName((String) object);
            }


        }  // End for (int index = 0; index < vertices.length; index++)

        Object[] edges = this.graph.getChildEdges(graph.getDefaultParent());

        for (int index = 0; index < edges.length; index++) {

            if (!(edges[index] instanceof mxCell)) {
                continue;
            }

            mxCell edge = (mxCell) edges[index];
            mxCell sourceVertex = null;
            mxCell targetVertex = null;
            Object object = edge.getSource();
            if (object instanceof mxCell) {
                sourceVertex = (mxCell) object;
            }
            object = edge.getTarget();
            if (object instanceof mxCell) {
                targetVertex = (mxCell) object;
            }

            if (sourceVertex == null) {
                if (targetVertex == null) {
                    throw new IllegalArgumentException("An edge can not have null for both source and target!  " + edge.toString());
                } else {
                    /*
                     * This edge is an arrow pointing to an initial state.
                     */
                    State state = cellToStateMap.get(targetVertex);
                    object = edge.getValue();
                    if (object instanceof InitialFinalWeight) {
                        InitialFinalWeight initialFinalWeight = (InitialFinalWeight) object;
                        mxGeometry edgeGeometry = edge.getGeometry();
                        if (edgeGeometry != null) {
                            InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
                            mxPoint point = edgeGeometry.getSourcePoint();
                            mxGeometry vertexGeometry = targetVertex.getGeometry();
                            if ((point != null) && (vertexGeometry != null)) {
                                geometricData.offset = new Point2D.Double(point.getX() - vertexGeometry.getCenterX(), point.getY() - vertexGeometry.getCenterY());
                            }
                            if ((edgeGeometry.getX() != 0) || (edgeGeometry.getY() != DEFAULT_LABEL_DISTANCE)) {
                                geometricData.labelPosAndDist = new Point2D.Double(edgeGeometry.getX(), edgeGeometry.getY());
                            }
                            mxPoint labelOffset = edgeGeometry.getOffset();
                            if (labelOffset != null) {
                                geometricData.labelOffset = new Point2D.Double(labelOffset.getX(), labelOffset.getY());
                            }
                            if ((geometricData.offset != null)
                                    || (geometricData.labelPosAndDist != null)
                                    || (geometricData.labelOffset != null)) {
                                initialFinalWeight.setGeometricData(geometricData);
                            }
                            geometricData = null;  // InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
                        }  // End if (edgeGeometry != null)
                        state.setInitialWeight(initialFinalWeight);
                    } else {
                        state.setInitialWeight(new InitialFinalWeight(true));
                    }
                    continue;
                }
            } else {
                if (targetVertex == null) {
                    /*
                     * This edge is an arrow pointing from a final state.
                     */
                    State state = cellToStateMap.get(sourceVertex);
                    object = edge.getValue();
                    if (object instanceof InitialFinalWeight) {
                        InitialFinalWeight initialFinalWeight = (InitialFinalWeight) object;
                        mxGeometry edgeGeometry = edge.getGeometry();
                        if (edgeGeometry != null) {
                            InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
                            mxPoint point = edgeGeometry.getTargetPoint();
                            mxGeometry vertexGeometry = sourceVertex.getGeometry();
                            if ((point != null) && (vertexGeometry != null)) {
                                geometricData.offset = new Point2D.Double(point.getX() - vertexGeometry.getCenterX(), point.getY() - vertexGeometry.getCenterY());
                            }
                            if ((edgeGeometry.getX() != 0) || (edgeGeometry.getY() != DEFAULT_LABEL_DISTANCE)) {
                                geometricData.labelPosAndDist = new Point2D.Double(edgeGeometry.getX(), edgeGeometry.getY());
                            }
                            mxPoint labelOffset = edgeGeometry.getOffset();
                            if (labelOffset != null) {
                                geometricData.labelOffset = new Point2D.Double(labelOffset.getX(), labelOffset.getY());
                            }
                            if ((geometricData.offset != null)
                                    || (geometricData.labelPosAndDist != null)
                                    || (geometricData.labelOffset != null)) {
                                initialFinalWeight.setGeometricData(geometricData);
                            }
                            geometricData = null;  // InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
                        }  // End if (edgeGeometry != null)
                        state.setFinalWeight(initialFinalWeight);
                    } else {
                        state.setFinalWeight(new InitialFinalWeight(true));
                    }
                    continue;
                }
            }

            Transition transition = new Transition();
            transition.setSourceState(cellToStateMap.get(sourceVertex));
            transition.setTargetState(cellToStateMap.get(targetVertex));
            object = edge.getValue();
            if (object instanceof WeightedRegularExpression) {
                transition.setLabel((WeightedRegularExpression) object);
            }
            mxGeometry geometry = edge.getGeometry();
            if (geometry != null) {
                TransitionInterface.GeometricData geometricData = new TransitionInterface.GeometricData();
                if ((geometry.getX() != 0) || (geometry.getY() != DEFAULT_LABEL_DISTANCE)) {
                    geometricData.labelPosAndDist = new Point2D.Double(geometry.getX(), geometry.getY());
                }
                mxPoint offset = geometry.getOffset();
                if (offset != null) {
                    geometricData.labelOffset = new Point2D.Double(offset.getX(), offset.getY());
                }
                List<mxPoint> points = geometry.getPoints();
                if ((points != null) && (!points.isEmpty())) {
                    Iterator<mxPoint> iteratePoints = points.iterator();
                    while (iteratePoints.hasNext()) {
                        mxPoint point = iteratePoints.next();
                        geometricData.controlPoints.add(new Point2D.Double(point.getX(), point.getY()));
                    }  // End while (iteratePoints.hasNext())
                }  // End if ((points != null) && (!points.isEmpty()))
                transition.setGeometricData(geometricData);
            }  // End if (geometry != null)

            TransitionInterface.DrawingData drawingData = new TransitionInterface.DrawingData();
            Map<String, Object> styleList = graph.getCellStyle(edge);
            drawingData.strokeColor = (String) styleList.get("strokeColor");
            String width = (String) styleList.get("strokeWidth");
            if (width != null) {
                drawingData.strokeWidth = Float.valueOf(width);
            }

            drawingData.startArrow = (String) styleList.get("startArrow");
            drawingData.endArrow = (String) styleList.get("endArrow");
//            System.out.println("style:" + drawingData.strokeColor + " " + drawingData.strokeWidth + " " + drawingData.startArrow + " " + drawingData.endArrow);

            transition.setDrawingData(drawingData);


            automata.addTransition(transition);

        }  // End for (int index = 0; index < edges.length; index++)

        return automata;
    }  // End public Automata getAutomata()

    /**
     * @return the graphOutline
     */
    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    /**
     * @return the graphComponent
     */
    public mxGraphComponent getGraphComponent() {
        if (graphComponent == null) {
            graphComponent = new mxGraphComponent(graph) {

                @Override
                protected void installDoubleClickHandler() {
                    graphControl.addMouseListener(new MouseAdapter() {

                        public void mouseReleased(MouseEvent e) {
                            if (isEnabled()) {
                                if (!e.isConsumed() && isEditEvent(e)) {
                                    mxCell cell = (mxCell)getCellAt(e.getX(), e.getY(), false);

                                    if (cell != null && getGraph().isCellEditable(cell)) {
                                        if (((mxCell) cell).isVertex()) {
                                            startEditingAtCell(cell, e);
                                        } else {
                                            mxCell source=(mxCell)cell.getSource();
                                            mxCell target=(mxCell)cell.getTarget();
                                            
                                            if(source!=null && target!=null){
                                            ExpressionEditor editor =
                                                    new ExpressionEditor(
                                                    new JFrame(), true,
                                                    (WeightedRegularExpression) ((mxCell) cell).getValue());
                                            editor.setVisible(true);
                                            ((mxCell) cell).setValue(editor.getExpression());
                                            }
                                        }
                                    }
                                } else {
                                    // Other languages use focus traversal here, in Java
                                    // we explicitely stop editing after a click elsewhere
                                    stopEditing(!invokesStopCellEditing);
                                }
                            }
                        }
                    });
                }
            };
        }

        return graphComponent;
    }

    /**
     * @return the graphPopupMenu
     */
    public javax.swing.JPopupMenu getGraphPopupMenu() {
        return graphPopupMenu;
    }

    public class CompareCtrlPoint implements Comparator {

        @Override
        public int compare(Object t, Object t1) {
            Integer tX = (int) ((mxPoint) t).getX();
            Integer tY = (int) ((mxPoint) t).getY();
            Integer t1X = (int) ((mxPoint) t1).getX();
            Integer t1Y = (int) ((mxPoint) t1).getY();

            int flag = tX.compareTo(t1X);
            if (flag == 0) {
                return tY.compareTo(t1Y);
            } else {
                return flag;
            }
        }
    }

    public void deleteSelectedCell() {
        mxCell selectedCell = (mxCell) graph.getSelectionCell();
        deleteCell(selectedCell);
        if (selectedCell != null) {
            undoStack.push(STATUS_DELETE);
        }

    }

    public void deleteCell(mxCell cell) {
        if (cell != null) {

            //remove state in automata
            if (cell.isVertex()) {

                State state = cellToState(cell);

                List<Transition> relatedTrans = state.getTransitions();
                for (Transition trans : relatedTrans) {
                    List<Transition> transList = automata.getAllTransitions();
                    transList.remove(trans);
                    automata.setAllTransitions(transList);
                }


                List<State> stateList = automata.getAllStates();
                stateList.remove(state);
                automata.setAllStates(stateList);
                //System.out.println("state list size: "+automata.getAllStates().size());
                //related transitions?



            } else if (cell.isEdge()) {
                Transition trans = cellToTransition(cell);
                if (trans == null) {
//                    System.out.println("can't find trans");
                }
                List<Transition> transList = automata.getAllTransitions();
                transList.remove(trans);
                automata.setAllTransitions(transList);
//                System.out.println("trans list size: " + automata.getAllTransitions().size());


            }


            cellTable.remove(cell);
            mxCell[] cells = {cell};
            graph.removeCells(cells);

//            System.out.println("cell table size: " + cellTable.size());


        } else {
//            System.out.println("Cell is empty");
        }
        graph.refresh();

    }

    public void setModified(boolean mod) {
        modified = mod;

        String title = null;
        if (currentFile == null) {
            title = "untitled";
        } else {
            title = currentFile.getName();
        }

        if (!modified) {

            this.setTitle(title);
        } else {
            this.setTitle(title + "*");
             graphComponent.refresh();
        }
        
       
        
    }

    private State cellToState(mxCell cell) {
        return (State) cellTable.get(cell);
    }

    private Transition cellToTransition(mxCell cell) {
        return (Transition) cellTable.get(cell);
    }

    public void undo() {

        undoManager.undo();
        updateUndoChangedCell();

    }

    public void redo() {

        undoManager.redo();
        updateRedoChangedCell();

    }

    public void updateUndoChangedCell() {

        //update cells in changedCell[]
        if (!undoStack.isEmpty()) {
            int stat = undoStack.pop();
            redoStack.push(stat);

//            System.out.println("stack pop:" + stat);

            for (Object cel : changedCell) {

                mxCell cell = (mxCell) cel;

                switch (stat) {
                    case STATUS_ADD: //so remove it!

                        deleteCell(cell);
                        break;

                    case STATUS_DELETE: //so add it again!
                        if (cell.isVertex()) {

                            State state = new State();
                            state.setName(cell.getValue().toString());
                            State.GeometricData geo = new State.GeometricData();
                            geo.location = new Point2D.Double(
                                    cell.getGeometry().getX(),
                                    cell.getGeometry().getY());
                            state.setGeometricData(geo);

                            cellTable.put(cell, state);
                            automata.addState(state);
                            //related transitions

                        } else {
                            Transition trans = new Transition();
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }
                            Transition.GeometricData geo = new GeometricData();
                            geo.controlPoints = apt;
                            trans.setGeometricData(geo);

                            trans.setLabel((WeightedRegularExpression) cell.getValue());
                            trans.setSourceState(cellToState((mxCell) cell.getSource()));
                            trans.setTargetState(cellToState((mxCell) cell.getTarget()));


                            automata.addTransition(trans);
                            cellTable.put(cell, trans);
                        }
                        break;

                    case STATUS_CHANGE:

                        if (((mxCell) cell).isVertex()) {
                            State state = cellToState(cell);
                            State.GeometricData geo = new State.GeometricData();
                            geo.location = new Point2D.Double(cell.getGeometry().getX(), cell.getGeometry().getY());
                            state.setGeometricData(geo);
                            state.setName((String) cell.getValue());

                        } else {

                            Transition trans = cellToTransition(cell);
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }

                            trans.getGeometricData().controlPoints = apt;

                            trans.setLabel((WeightedRegularExpression) cell.getValue());
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }

    public void updateRedoChangedCell() {

        if (!redoStack.isEmpty()) {
            int stat = redoStack.pop();
            undoStack.push(stat);

//            System.out.println("stack pop:" + stat);

            for (Object cel : changedCell) {

                mxCell cell = (mxCell) cel;

                switch (stat) {
                    case STATUS_DELETE: //so remove it!

                        deleteCell(cell);

                        break;

                    case STATUS_ADD: //so add it again!
                        if (cell.isVertex()) {

                            State state = new State();
                            state.setName(cell.getValue().toString());
                            State.GeometricData geo = new State.GeometricData();
                            geo.location = new Point2D.Double(cell.getGeometry().getX(), cell.getGeometry().getY());
                            state.setGeometricData(geo);

                            cellTable.put(cell, state);
                            automata.addState(state);
                            //related transitions



                        } else {
                            Transition trans = new Transition();
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }
                            Transition.GeometricData geo = new GeometricData();
                            geo.controlPoints = apt;
                            trans.setGeometricData(geo);

                            trans.setLabel((WeightedRegularExpression) cell.getValue());
                            trans.setSourceState(cellToState((mxCell) cell.getSource()));
                            trans.setTargetState(cellToState((mxCell) cell.getTarget()));


                            automata.addTransition(trans);
                            cellTable.put(cell, trans);
                        }

                        //undoStack.push(STATUS_ADD);
                        break;

                    case STATUS_CHANGE:

                        if (((mxCell) cell).isVertex()) {
                            State state = cellToState(cell);
                            State.GeometricData geo = new State.GeometricData();
                            geo.location = new Point2D.Double(
                                    cell.getGeometry().getX(),
                                    cell.getGeometry().getY());
                            state.setGeometricData(geo);

                            state.setName((String) cell.getValue());

                        } else {

                            Transition trans = cellToTransition(cell);
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }

                            trans.getGeometricData().controlPoints = apt;
                            trans.setLabel((WeightedRegularExpression) cell.getValue());
                        }
                        //undoStack.push(STATUS_CHANGE);
                        break;
                    default:
                        break;

                }


            }
        }


    }
    
    
    public void setCurrentFile(File file){
        currentFile=file;
        this.setTitle(currentFile.getName());
    }
    public File getCurrentFile(){
        return currentFile;
    }
    public static final double DEFAULT_LABEL_DISTANCE = 15;
    private static final long serialVersionUID = -6561623072112577140L;
    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;
    private mxGraphComponent graphComponent = null;
    private mxGraphOutline graphOutline;
    public final mxGraph graph;
    protected WeightedVisibilityGraph visibilityGraph;
    public boolean modified = false;
    protected boolean isPopupTrigger;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    //protected Hashtable<Integer, mxCell> cellTable;
    protected Hashtable<mxCell, Object> cellTable;
    protected mxCell transitionFrom, transitionTo;
    protected double popMouseX, popMouseY;
    protected mxPoint popPoint;
    
    protected JSplitPane infoSplitPane;
    private static JPanel topPanel = null, bottomPanel = null;
    protected Automata automata;
    private boolean hasGeometricData = true;
    private List<mxCell> initialFinalCells = new ArrayList<mxCell>();
    private File currentFile = null;
    ////undo
    protected mxUndoManager undoManager;
    protected mxIEventListener undoHandler = new mxIEventListener() {

        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };
    protected Object changedCell[] = new Object[0];
    protected Stack<Integer> undoStack = new Stack<Integer>();
    protected Stack<Integer> redoStack = new Stack<Integer>();
    static final int STATUS_ADD = 1;
    static final int STATUS_DELETE = 2;
    static final int STATUS_CHANGE = 3;
    ///
    static final float defaultFontSize=13;
    ////
    VGI vgi;
    ////
    int selectedHandlerIndex = 0;
    int vertexWidth = 50;
    ////

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        graphPopupMenu = new javax.swing.JPopupMenu();
        addStateMenuItem = new javax.swing.JMenuItem();
        addTransitionFromMenuItem = new javax.swing.JMenuItem();
        addTransitionToMenuItem = new javax.swing.JMenuItem();
        addControlPointMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        cancelMenuItem = new javax.swing.JMenuItem();
        deleteControlPointMenuItem = new javax.swing.JMenuItem();
        resetControlPointMenuItem = new javax.swing.JMenuItem();
        routeEdgeMenuItem = new javax.swing.JMenuItem();
        routeEdgeWVGMenuItem = new javax.swing.JMenuItem();

        addStateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/addstate.png"))); // NOI18N
        addStateMenuItem.setText("Add State");
        addStateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStateMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addStateMenuItem);

        addTransitionFromMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/addtransitionfrom.png"))); // NOI18N
        addTransitionFromMenuItem.setText("Add Transition (From)");
        addTransitionFromMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransitionFromMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addTransitionFromMenuItem);

        addTransitionToMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/addtransitionto.png"))); // NOI18N
        addTransitionToMenuItem.setText("Add Transition (To)");
        addTransitionToMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransitionToMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addTransitionToMenuItem);

        addControlPointMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/addcontrolpoint.png"))); // NOI18N
        addControlPointMenuItem.setText("Add Control Point");
        addControlPointMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addControlPointMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addControlPointMenuItem);

        deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/delete.png"))); // NOI18N
        deleteMenuItem.setText("Delete");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(deleteMenuItem);

        cancelMenuItem.setText("Cancel");
        cancelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(cancelMenuItem);

        deleteControlPointMenuItem.setText("Delete Control Point");
        deleteControlPointMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteControlPointMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(deleteControlPointMenuItem);

        resetControlPointMenuItem.setText("Reset Control Points");
        resetControlPointMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetControlPointMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(resetControlPointMenuItem);

        routeEdgeMenuItem.setText("Route Edge");
        routeEdgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeEdgeMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(routeEdgeMenuItem);

        routeEdgeWVGMenuItem.setText("Route Edge (WVG)");
        routeEdgeWVGMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeEdgeWVGMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(routeEdgeWVGMenuItem);

        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        // TODO add your handling code here:
        if (evt.getWheelRotation() < 0) {
            getGraphComponent().zoomIn();
        } else {
            getGraphComponent().zoomOut();
        }

//        status(mxResources.get("scale") + ": "
//                + (int) (100 * graphComponent.getGraph().getView().getScale())
//                + "%");
    }//GEN-LAST:event_formMouseWheelMoved

    private void addStateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStateMenuItemActionPerformed
        // TODO add your handling code here:
        
        //addState(popMouseX, popMouseY);
        addState(popPoint.getX(),popPoint.getY());
    }//GEN-LAST:event_addStateMenuItemActionPerformed

    private void addTransitionFromMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionFromMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY);
        if (cell != null) {
            if (((mxCell) cell).isVertex()) {
                transitionFrom = (mxCell) cell;
               // System.out.println("add transition from :"+((mxCell)cell).getId());
            }
        }
    }//GEN-LAST:event_addTransitionFromMenuItemActionPerformed

    private void addTransitionToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionToMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY);
        
        if (cell != null) {
            if (((mxCell) cell).isVertex()) {
                //System.out.println("add transition");
                if (transitionFrom != null) {
                    transitionTo = (mxCell) cell;
                    addTransition(transitionFrom, transitionTo);
                    // System.out.println("add transition to :"+((mxCell)cell).getId());
                    transitionFrom = null;
                    transitionTo = null;
                }
            }
        }
    }//GEN-LAST:event_addTransitionToMenuItemActionPerformed

    private void addControlPointMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addControlPointMenuItemActionPerformed
        // TODO add your handling code here:
        addControlPoint();
    }//GEN-LAST:event_addControlPointMenuItemActionPerformed

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        // TODO add your handling code here:
        deleteSelectedCell();
    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void cancelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelMenuItemActionPerformed
        // TODO add your handling code here:
        transitionFrom = null;
    }//GEN-LAST:event_cancelMenuItemActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
        if (modified) {

            int check = JOptionPane.showConfirmDialog(this, "Save before leaving?", "", JOptionPane.YES_NO_CANCEL_OPTION);
            if (check == JOptionPane.YES_OPTION) {

                if (currentFile == null) {
                    vgi.saveAs();

                } else {
                    vgi.save();

                }
                this.dispose();
            }else if(check==JOptionPane.NO_OPTION){
                this.dispose();
            }
        }else{
            this.dispose();
        }


    }//GEN-LAST:event_formInternalFrameClosing

    private void deleteControlPointMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteControlPointMenuItemActionPerformed
        deleteControlPoint();
    }//GEN-LAST:event_deleteControlPointMenuItemActionPerformed

    private void resetControlPointMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetControlPointMenuItemActionPerformed
        resetControlPoint();
    }//GEN-LAST:event_resetControlPointMenuItemActionPerformed

	private void routeEdgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeEdgeMenuItemActionPerformed
            mxCell cell = (mxCell) getGraphComponent().getCellAt((int) this.popMouseX, (int) this.popMouseY);
            if ((cell == null)) {
                return;
            }
            EdgeRoutingMinCross layout = new EdgeRoutingMinCross(this.graph);
            layout.route(cell);
	}//GEN-LAST:event_routeEdgeMenuItemActionPerformed

	private void routeEdgeWVGMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeEdgeWVGMenuItemActionPerformed
            mxCell cell = (mxCell) getGraphComponent().getCellAt((int) this.popMouseX, (int) this.popMouseY);
            if ((cell == null)) {
                return;
            }
            EdgeRoutingMinCross layout = new EdgeRoutingMinCross(this.graph);
            layout.routeByWeightedVisibilityGraph(cell);
	}//GEN-LAST:event_routeEdgeWVGMenuItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addControlPointMenuItem;
    private javax.swing.JMenuItem addStateMenuItem;
    private javax.swing.JMenuItem addTransitionFromMenuItem;
    private javax.swing.JMenuItem addTransitionToMenuItem;
    private javax.swing.JMenuItem cancelMenuItem;
    private javax.swing.JMenuItem deleteControlPointMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JPopupMenu graphPopupMenu;
    private javax.swing.JMenuItem resetControlPointMenuItem;
    private javax.swing.JMenuItem routeEdgeMenuItem;
    private javax.swing.JMenuItem routeEdgeWVGMenuItem;
    // End of variables declaration//GEN-END:variables
}
