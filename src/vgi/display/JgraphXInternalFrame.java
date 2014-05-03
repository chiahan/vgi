package vgi.display;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.handler.mxEdgeHandler;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import vgi.automata.*;
import vgi.geometrictools.Vector2D;
import vgi.layout.circular.CircularLayoutAutomata;
import vgi.layout.edgerouting.*;
import vgi.layout.feature.FeatureLayout;
import vgi.layout.helperclass.GroupReplacedAutomata;
import vgi.layout.hierarchical.HierarchicalLayoutAutomata;
import vgi.layout.linear.LinearLayoutAutomata;

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
        this.visibilityGraph = null;//new WeightedVisibilityGraph();
        this.automata = automata;
        
        this.graph=automata.jgraphAutomata.graph;
        this.graphComponent=automata.jgraphAutomata.graphComponent;
        

        setTitle(filename);

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
//        graph.setDisconnectOnMove(false);
//        graph.setSplitEnabled(false);
//        graph.setCellsDisconnectable(false);
//        graph.setGridEnabled(false);
//        //graph.setResetEdgesOnMove(true);
//        
//        graphComponent = getGraphComponent();
//        graphComponent.setConnectable(false);
//        graphComponent.getViewport().setBackground(Color.WHITE);
        
        graphOutline = new mxGraphOutline(graphComponent);

        if ((topPanel != null) && (bottomPanel != null)) {
            this.topPanel = (JPanel) infoSplitPane.getTopComponent();
            this.bottomPanel = (JPanel) infoSplitPane.getBottomComponent();
        }

        this.setBounds(xOffset * openFrameCount, yOffset * openFrameCount, 800, 600);
        openFrameCount++;
        this.getContentPane().add(graphComponent, java.awt.BorderLayout.CENTER);

        //cellTable = new Hashtable<Integer, mxCell>();
        //cellTable = new Hashtable<mxCell, Object>();
    }

    private void initAutomata() {
                setupStates();

//             
//		Iterator<mxCell> iterateCells = this.cellTable.keySet().iterator();
//		while (iterateCells.hasNext()) {
//			mxICell cell = iterateCells.next();
//			if (!(cell.isVertex())) {
//				continue;
//			}
//			if (this.visibilityGraph != null) {
//				this.visibilityGraph.addRoadblock(cell);
//			}
//		}  // End while (iterateCells.hasNext())
                Object[] edges = this.graph.getChildEdges(graph.getDefaultParent());
		for (int index = 0; index < edges.length; index++) {
                    if (this.visibilityGraph != null) {
				this.visibilityGraph.addRoadblock((mxCell)edges[index]);
			}
		}  // End while (iterateCells.hasNext())

		//setupTranitions();

		//Object[] edges = this.graph.getChildEdges(graph.getDefaultParent());
//        for (int index = 0; index < edges.length; index++) {
//            mxCell edge=(mxCell)edges[index];
//            List<mxPoint> ptList=edge.getGeometry().getPoints();
//            if ((ptList == null) || (ptList.isEmpty())) {
//               // resetControlPoint(cellToTransition((mxCell)edges[index]));
//            }
//        }
        
//        setupInitialFinal();
        if (!(this.hasGeometricData)) {
            System.out.println("The file has no geometric data, use circular layout!");
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
                //mxCell[] cells = (mxCell[])evt.getProperty("cells");
                
                Double dx = (Double) evt.getProperty("dx");
                Double dy = (Double) evt.getProperty("dy");

                //System.out.println("Cells moved: " + evt.getProperties());
                //updateInitialFinal(cells, new Point2D.Double(dx, dy));
                //updateControlPoint(cells,new Point2D.Double(dx,dy));
                //setModified(true);
                System.out.println("move cells");
                int length = cells.length;
                mxCell vertex;
                if(length==1){
                    vertex = (mxCell)cells[0];
                    Point2D pt=new Point2D.Double(vertex.getGeometry().getCenterX(),vertex.getGeometry().getCenterY());
                    Point2D loc=automata.getProjection().getGeoFromLoc(pt);
                    if(vertex.isVertex()) automata.moveState((State)automata.cellToState(vertex),loc);
                }else{
                    List<State> states=new ArrayList<State>();
                    for(Object cell:cells){
                        vertex=(mxCell)cell;
                        if(vertex.isVertex()) states.add((State)automata.cellToState(vertex));
                    }
                    Vector2D offset=automata.getProjection().getGeoVecFromLocVec(new Vector2D(dx,dy));
                    automata.moveStates(states,new Point2D.Double(offset.getX(),offset.getY()));
                }
                
                
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

            //@Override
            public void mousePressed(MouseEvent e) {

                isPopupTrigger = e.isPopupTrigger();
                if (graphComponent.getCellEditor() != null) {
                    graphComponent.getCellEditor().stopEditing(true);
                }
                // graph.refresh();
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
                boolean isIni = false;
                boolean isFin = false;
                
                if (selected)
                {

                    DisplayUtil display = new DisplayUtil(graph, automata);
                    mxCell source=(mxCell)selectedCell.getSource();
                mxCell target=(mxCell)selectedCell.getTarget();
                if(source==target) isloop=true;
                        
                if(source==null) isIni=true;
                if(target == null) isFin = true;
                    edgeSelected = selectedCell.isEdge();
                    if (edgeSelected) 
                    {
                       // System.out.println("push edge");
                        if(isIni == true)
                        {
                            JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell,
                                display.IniFinToTransition(selectedCell),
                                display, JgraphXInternalFrame.this));
                        }
                        else if(isFin == true)
                        {
                            JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell,
                                display.IniFinToTransition(selectedCell),
                                display, JgraphXInternalFrame.this));
                        }
                        else
                        {
                            JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell,
                                display.cellToTransition(selectedCell),
                                display, JgraphXInternalFrame.this));
                        }
                        
                       

                        mxEdgeHandler hand = (mxEdgeHandler) graphComponent.getSelectionCellsHandler().getHandler(selectedCell);
                        selectedHandlerIndex = hand.getIndex();
                        //System.out.println("handler index:"+selectedHandlerIndex);
                        if (selectedHandlerIndex > 0) {
                            controlPointSelected = true;
                        }


                        addTransitionFromMenuItem.setVisible(false);
                        addTransitionToMenuItem.setVisible(false);
                        
                        
                        
//                        //\/\/\/\/\/\/\/\/\/\/\/\\
//                        // update contorl point
//                        //\/\/\/\/\/\/\/\/\/\/\/\\
//                        mxCell edge=(mxCell)selectedCell;
//                        mxGeometry geo=edge.getGeometry();
//                        List<mxPoint> points=geo.getPoints();
//
//                        Transition transition=(Transition)automata.cellToState(edge);
//                        if(transition==null){ // for initial or final
//                           
//                           mxPoint termpoint_=(isIni)?geo.getTerminalPoint(true):geo.getTerminalPoint(false);
//                           Point2D termpoint=automata.getProjection().getGeoFromLoc(new Point2D.Double(termpoint_.getX(),termpoint_.getY()));
//                           
//                           State state=(isIni)?(State)automata.cellToState((mxCell)edge.getTarget()):(State)automata.cellToState((mxCell)edge.getSource());
//                           automata.updateIniFinGeometricData(state, termpoint, isIni);
//                           
//                           return;
//                        }
//                        // for transitions
//                        List<Point2D> newcpt=new ArrayList<Point2D>();
//                        for(mxPoint pt:points){
//                            Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(pt.getX(),pt.getY()));
//                            newcpt.add(loc);
//                        }
//                        automata.updateTransitionControlPoint(transition, newcpt);
//                        
                    }

                    vertexSelected = selectedCell.isVertex();
                    if (vertexSelected) {
                        System.out.println("push vertex");
//                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
//                                new StatePropertiesPanel(selectedCell,
//                                JgraphXInternalFrame.this.cellToState(selectedCell),
//                                display, JgraphXInternalFrame.this));
                       
                        
                   
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(selectedCell,
                                cellToState(selectedCell),
                                display, JgraphXInternalFrame.this));
                        
                        addTransitionFromMenuItem.setVisible(
                                (transitionFrom == null) ? true : false);
                        addTransitionToMenuItem.setVisible(
                                (transitionFrom == null) ? false : true);
                    }
                    
                } else {
                    
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                            new AutomataPropertiesPanel(
                            new DisplayUtil(graph, automata), JgraphXInternalFrame.this));
                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                    graph.setSelectionCell(null);
                }
                addStateMenuItem.setVisible(!selected);
                //addStateMenuItem.setVisible(false); ellie
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected && !isloop);
                deleteControlPointMenuItem.setVisible(controlPointSelected && !isloop);
                resetControlPointMenuItem.setVisible(edgeSelected);
                routeEdgeMenuItem.setVisible(edgeSelected);
                routeEdgeWVGMenuItem.setVisible(false);

                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);
                
                applyLinearLayoutMenuItem.setVisible(selected);
                groupMenuItem.setVisible(selected);
                UngroupMenuItem.setVisible(selected);
                copyMenuItem.setVisible(selected);

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
                
                else if(!e.isPopupTrigger() && addStateFlag)//ellie
                {
                    addState(e.getX(),e.getY());
                    addStateFlag = false;
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // Installs a mouse motion listener to display the mouse location
//        getGraphComponent().getGraphControl().addMouseMotionListener(
//                new MouseMotionListener() {
//
//                    @Override
//                    public void mouseDragged(MouseEvent e) {
//                        mouseLocationChanged(e);
//                        boolean selected = !graph.isSelectionEmpty();
//                        mxCell selectedCell = (mxCell) graph.getSelectionCell();
//
//                        boolean vertexSelected = false;
//
//                        if (selected) {
//                            vertexSelected = selectedCell.isVertex();
//                            if (vertexSelected) {
//                                //update position
//                               /*
//                                 * State selectedState =
//                                 * cellToState(selectedCell);
//                                 * if(selectedState!=null){ State.GeometricData
//                                 * geo=new State.GeometricData();
//                                 * geo.location=new
//                                 * Point2D.Double(e.getX(),e.getY());
//                                 * selectedState.setGeometricData(geo);
//                                 * System.out.println("update location");
//                                 * setModified(true); }
//                                 */
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void mouseMoved(MouseEvent e) {
////                        mouseDragged(e);
//                    }
//                });
    }

    public void installInternalFrameListeners() {
        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
				JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
						new AutomataPropertiesPanel(
						new DisplayUtil(graph, automata), JgraphXInternalFrame.this));

				JgraphXInternalFrame.this.infoSplitPane.setBottomComponent(
						graphOutline);
				JgraphXInternalFrame.this.graph.clearSelection();
            }

            @Override
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
//        Iterator<Transition> iterator = this.automata.getAllTransitions().iterator();
//        while (iterator.hasNext()) {
//            Transition transition = iterator.next();
//            this.addTransition(transition);
//        }  // End while (transitionIterator.hasNext())
    }

    private void setupInitialFinal() {
//        DisplayUtil display = new DisplayUtil(graph, automata);
//
//        Object parent = this.graph.getDefaultParent();
//        Iterator<mxCell> cellIterator = this.initialFinalCells.iterator();
//        while (cellIterator.hasNext()) {
//            mxCell vertex = cellIterator.next();
//            mxGeometry geometry = vertex.getGeometry();
//            if (geometry == null) {
//                continue;
//            }
//
//            State state = this.cellToState(vertex);
//
//            InitialFinalWeight initialFinalWeight = state.getInitialWeight();
//            if (initialFinalWeight != null) {
//                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
//                if (geometricData == null) {
//                    display.showInitialFinal(parent, initialFinalWeight, vertex, false);
//                } else {  // End if (geometricData == null)
//                    if (geometricData.offset == null) {
//                        display.showInitialFinal(parent, initialFinalWeight, vertex, false);
//                    } else {  // End if (geometricData.offset == null)
//                        double x = geometry.getCenterX() + geometricData.offset.getX();
//                        double y = geometry.getCenterY() + geometricData.offset.getY();
//                        mxPoint point = new mxPoint(x, y);
//                        mxCell edge = (mxCell) this.graph.insertEdge(parent, null, initialFinalWeight, null, vertex);
//                        Object[] cells = {edge};
//                        this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
//        this.graph.setCellStyles("fontSize", String.valueOf(JgraphXInternalFrame.defaultFontSize), cells);
//        this.graph.setCellStyles("textShape","default", cells);
//                        mxGeometry edgeGeometry = edge.getGeometry();
//                        if (edgeGeometry != null) {
//                            edgeGeometry.setSourcePoint(point);
//                            if (geometricData.labelPosAndDist == null) {
//                                edgeGeometry.setY(DEFAULT_LABEL_DISTANCE);
//                            } else {
//                                edgeGeometry.setX(geometricData.labelPosAndDist.getX());
//                                edgeGeometry.setY(geometricData.labelPosAndDist.getY());
//                            }
//                            if (geometricData.labelOffset == null) {
//                                edgeGeometry.setOffset(null);
//                            } else {
//                                edgeGeometry.setOffset(new mxPoint(geometricData.labelOffset));
//                            }
//                        }  // End if (edgeGeometry != null)
//                    }  // End else part of if (geometricData.offset == null)
//                }  // End else part of if (geometricData == null)
//            }  // End if (initialFinalWeight != null)
//
//            initialFinalWeight = state.getFinalWeight();
//            if (initialFinalWeight != null) {
//                InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
//                if (geometricData == null) {
//                    display.showInitialFinal(parent, initialFinalWeight, vertex, true);
//                } else {  // End if (geometricData == null)
//                    if (geometricData.offset == null) {
//                        display.showInitialFinal(parent, initialFinalWeight, vertex, true);
//                    } else {  // End if (geometricData.offset == null)
//                        double x = geometry.getCenterX() + geometricData.offset.getX();
//                        double y = geometry.getCenterY() + geometricData.offset.getY();
//                        mxPoint point = new mxPoint(x, y);
//                        mxCell edge = (mxCell) this.graph.insertEdge(parent, null, initialFinalWeight, vertex, null);
//                        Object[] cells = {edge};
//                        this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
//        this.graph.setCellStyles("fontSize", String.valueOf(JgraphXInternalFrame.defaultFontSize), cells);
//        this.graph.setCellStyles("textShape","default", cells);
//                        mxGeometry edgeGeometry = edge.getGeometry();
//                        if (edgeGeometry != null) {
//                            edgeGeometry.setTargetPoint(point);
//                            if (geometricData.labelPosAndDist == null) {
//                                edgeGeometry.setY(DEFAULT_LABEL_DISTANCE);
//                            } else {
//                                edgeGeometry.setX(geometricData.labelPosAndDist.getX());
//                                edgeGeometry.setY(geometricData.labelPosAndDist.getY());
//                            }
//                            if (geometricData.labelOffset == null) {
//                                edgeGeometry.setOffset(null);
//                            } else {
//                                edgeGeometry.setOffset(new mxPoint(geometricData.labelOffset));
//                            }
//                        }  // End if (edgeGeometry != null)
//                    }  // End else part of if (geometricData.offset == null)
//                }  // End else part of if (geometricData == null)
//            }  // End if (initialFinalWeight != null)
//        }  // End while (cellIterator.hasNext())
    }

    private void updateInitialFinal(Object[] cells, Point2D offset) {
//        DisplayUtil display = new DisplayUtil(graph, automata);
//        int length = cells.length;
//        mxCell vertex, edge;
//        for (int i = 0; i < length; i++) {
//            vertex = (mxCell) cells[i];
//            Object edges[] = graph.getEdges(vertex);
//            int count = edges.length;
//            for (int j = 0; j < count; j++) {
//                edge = (mxCell) edges[j];
//                if (edge.getTerminal(true) == null) {
//                    /*display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), false);
//                    Object removeEdge[] = {edge};
//                    graph.removeCells(removeEdge);*/
//                    
//                    
//                    double dx=offset.getX();
//                    double dy=offset.getY();
//                    /*List<mxPoint> ptlist=edge.getGeometry().getPoints();
//                    for(mxPoint pt:ptlist){
//                           pt.setX(pt.getX()+dx);
//                           pt.setY(pt.getY()+dy);
//                    }
//                    edge.getGeometry().setPoints(ptlist);*/
//                    mxPoint tpt=edge.getGeometry().getTerminalPoint(true);
//                    tpt.setX(tpt.getX()+dx);
//                    tpt.setY(tpt.getY()+dy);
//                    edge.getGeometry().setTerminalPoint(tpt, true);
//                    
//                } else if (edge.getTerminal(false) == null) {
//                    /*display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), true);
//                    Object removeEdge[] = {edge};
//                    graph.removeCells(removeEdge);*/
//                    double dx=offset.getX();
//                    double dy=offset.getY();
//                    mxPoint tpt=edge.getGeometry().getTerminalPoint(false);
//                    tpt.setX(tpt.getX()+dx);
//                    tpt.setY(tpt.getY()+dy);
//                    edge.getGeometry().setTerminalPoint(tpt, false);
//                    
//                    
//                }
//            }
//        }
    }
    
 /*  cells contain states already moved by the vector offset
  * 
  */
    private void updateControlPoint(Object[] cells, Point2D offset) {
//        int length = cells.length;
//        mxCell vertex, edge;
//        double dx=offset.getX();
//        double dy=offset.getY();
//                    
//        for (int i = 0; i < length; i++) {
//        
//            vertex = (mxCell)cells[i];
//            Point2D pt=new Point2D.Double(vertex.getGeometry().getCenterX(),vertex.getGeometry().getCenterY());
//            if(vertex.isVertex()) automata.moveState((State)automata.cellToState(vertex),pt);
//            
            
            // edges contains all transitions connected to vertex
//            Object edges[] = graph.getEdges(vertex);
//            int count = edges.length;
//            
//            
//            for (int j = 0; j < count; j++) {
//                edge = (mxCell) edges[j];
//                
//                mxCell source=(mxCell)edge.getSource();
//                mxCell target=(mxCell)edge.getTarget();
//                
//                // initial and final are processed seperately
//                if(source==null || target==null) continue;
//                // process loops
//                if(source==target){
//                    mxGeometry geo=edge.getGeometry();
//                    List<mxPoint> points = geo.getPoints();
//                    for(mxPoint pt:points){
//                                pt.setX(pt.getX()+dx);
//                                pt.setY(pt.getY()+dy);
//                    }
//                    edge.getGeometry().setPoints(points);
//                }else{
//            
//                    // oldVertexPoint is the center of vertex before translation
//                    Point2D oldVertexPoint=new Point2D.Double(vertex.getGeometry().getCenterX()-dx,
//                                                        vertex.getGeometry().getCenterY()-dy);
//            
//                    // newVertexPoint is the center of vertex
//                    Point2D newVertexPoint=new Point2D.Double(vertex.getGeometry().getCenterX(),
//                                                       vertex.getGeometry().getCenterY());
//          
//                    // endPoint is the center of the other end of transition
//                    Point2D endPoint;
//                    if(target==vertex) 
//                        endPoint=new Point2D.Double(source.getGeometry().getCenterX(),source.getGeometry().getCenterY());
//                    else  endPoint=new Point2D.Double(target.getGeometry().getCenterX(),target.getGeometry().getCenterY());
//
//                    mxGeometry geo=edge.getGeometry();
//                    if (geo != null){
//                            // Resets the control points
//                            List<mxPoint> points = geo.getPoints();
//                            
//                            if (points != null && !points.isEmpty()){
//
//                                    List<mxPoint> ptlist=geo.getPoints();
//                                    for(mxPoint pt:ptlist){
//                                        
//                                        Point2D point=new Point2D.Double(pt.getX(),pt.getY());
//                                        Point2D newpt=moveControlPointWithTwoEnd(point,oldVertexPoint,newVertexPoint,endPoint);
//                                        
//                                        pt.setX(newpt.getX());
//                                        pt.setY(newpt.getY());
//                                      //  System.out.println("update control pt: "+pt);
//                                    }
//                                    
//                                    edge.getGeometry().setPoints(ptlist);
//                            }
//                    }
//                }
////            }
//        }
//        setModified(true);
//        undoStack.push(STATUS_CHANGE);
//    
    }   
 
                
    public void updateAllGDData(){
        automata.refresh();
        graph.refresh();
    }
  
    public void resetAllStatesDrawingData(){
        
        
        
    }
    
//    public void applyStateDrawingData(mxCell cell, StateDrawingData drawingdata){
//        
//        mxCell[] cells=new mxCell[]{cell};
//        
//        graph.setCellStyles("fillColor", drawingdata.getFillColor(),cells);
//        graph.setCellStyles("strokeColor", drawingdata.getStrokeColor(),cells);
//        graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()),cells);
//
//    }
//    public void applyStateGeometricData(mxCell cell, StateGeometricData gd){
//        
//        mxCell[] cells=new mxCell[]{cell};
//        
//        graph.setCellStyles("shape", gd.getShape(),cells);
//        mxGeometry geo=cell.getGeometry();
//        geo.setWidth(gd.getWidth());
//        geo.setHeight(gd.getHeight());
//        
//        // TODO: location is not yet synchronized in StateGeometricData while moving
//        //geo.setX(gd.getX());
//        //geo.setY(gd.getY());
//        cell.setGeometry(geo);
//        
//    }
//    public void applyTransitionDrawingData(mxCell cell,TransitionDrawingData dd){
//        mxCell[] cells=new mxCell[]{cell};
//        
//        graph.setCellStyles("strokeColor", dd.getStrokeColor(),cells);
//        graph.setCellStyles("strokeWidth", String.valueOf(dd.getStrokeWidth()),cells);
//        graph.setCellStyles("startArrow", dd.getStartArrow(),cells);
//        graph.setCellStyles("endArrow", dd.getEndArrow(),cells);
//        
//
//    }
    public void updateStateDrawingData(State state,StateDrawingData drawingdata){
//        State state=cellToState(cell);
        StateDrawingData sdd=state.getDrawingData();
        
        if(drawingdata.getFillColor()!=null) sdd.setFillColor(drawingdata.getFillColor());
        if(drawingdata.getStrokeColor()!=null) sdd.setStrokeColor(drawingdata.getStrokeColor());
        if(drawingdata.getStrokeWidth()>0) sdd.setStrokeWidth(drawingdata.getStrokeWidth());
        
//        state.setDrawingData(sdd);
//        applyStateDrawingData(cell,automata.getStateDrawingData(state));
        automata.setStateDrawingData(state, sdd);
    }
    
    /*
     *  add states at left mouse click
     */
    public void addState(double x, double y) {
        
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(x,y));
        State newState=automata.addState(loc);
        
//        mxCell vertex = createVertex(newState);
        mxCell vertex=automata.stateToCell(newState);
//        cellTable.put(vertex, newState);
//
        DisplayUtil display = new DisplayUtil(graph, automata);
        infoSplitPane.setTopComponent(
                new StatePropertiesPanel((mxCell) vertex, newState, display, JgraphXInternalFrame.this));
//
////        System.out.println("add state at" + x + "," + y);
////        System.out.println("total states:" + automata.getAllStates().size());
//
        setModified(true);
        undoStack.push(STATUS_ADD);

		if (this.visibilityGraph != null) {
			this.visibilityGraph.addRoadblock(vertex);
		}
       automata.refresh();
    }
    /*
     *  add states when reading automata from files
     */
    public void addState(State state) {
        StateGeometricData geometricData = state.getGeometricData();
        if ((geometricData == null) || (geometricData.getLocation() == null)) {
            this.hasGeometricData = false;
        }
//        mxCell vertex = createVertex(state);
//        cellTable.put(vertex, state);
 
        mxCell vertex=automata.stateToCell(state);
        if ((state.getInitial() != null) || (state.getFinal() != null)) {
            this.initialFinalCells.add(vertex);
        }

//        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
//        infoSplitPane.setTopComponent(
//                new StatePropertiesPanel((mxCell) vertex, 
//                JgraphXInternalFrame.this.cellToState(vertex), display));
//        System.out.println("add state at (" + x + "," + y + ").");
        automata.refresh();
    }  // End public void addState(State state)


//    private mxCell createVertex(State state) {
//        
//        StateGeometricData geodata=automata.getStateGeometricData(state);
//        StateDrawingData drawingdata=automata.getStateDrawingData(state);
//       
//        Object parent = graph.getDefaultParent();
//        int id = cellTable.size();
//
//        double w=geodata.getWidth();
//        double h=geodata.getHeight();
//        String shape=geodata.getShape();
//        
//        if(w<=0) w=vertexWidth;
//        if(h<=0) h=vertexWidth;
//        if(shape==null) shape="ellipse";
//        
//        Object vertex = graph.insertVertex(parent, Integer.toString(id), state.getName(),
//                geodata.getX() - w / 2, geodata.getY() - h / 2, w, h, "shape="+shape+";perimeter="+shape+"Perimeter;fontSize="+defaultFontSize+";");
//        
//        graph.setSelectionCell(vertex);
//        
//        graph.setCellStyles("fillColor", drawingdata.getFillColor());
//        graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
//        graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
//
//        ((mxCell) vertex).setConnectable(false);
//
//        return (mxCell) vertex;
//    }
    /*
     * add transitions at right mouse click
     */
    public void addTransition(State source, State target) {

        Transition transition=automata.addTransition(source,target);
        
//        mxCell edge=createEdge(transition);
//        cellTable.put(edge, transition);
        mxCell edge=automata.transitionToCell(transition);
        
//        System.out.println("total trans:" + automata.getAllTransitions().size());
        if (this.vgi.getRouteEdgeWhileAdding()) {
                EdgeRoutingMinCross edgeRoutingMinCross = new EdgeRoutingMinCross(this.graph);
                edgeRoutingMinCross.route(edge);
        }
        setModified(true);
        automata.refresh();
        undoStack.push(STATUS_ADD);
    }
    /*
     * add transitions when reading automata from file
     */
    public mxCell addTransition(Transition transition) {
       
        
//        mxCell edge=createEdge(transition);
//        cellTable.put(edge, transition);
//        
//        return edge;
        return null;
        
    }  // End public mxCell addTransition(Transition transition)

    
//    public mxCell createEdge(Transition transition){
//        mxCell source = null, target = null;
//        Enumeration keys = cellTable.keys();
//        State sourceState=transition.getSourceState();
//        State targetState=transition.getTargetState();
////        while (keys.hasMoreElements()) {
////            mxCell keyCell = (mxCell) keys.nextElement();
////            if (cellTable.get(keyCell) == sourceState) source = keyCell;
////            if (cellTable.get(keyCell) == targetState) target = keyCell;
////            
////            if ((source != null) && (target != null)) break;
////        }
//        source=automata.stateToCell(sourceState);
//        target=automata.stateToCell(targetState);
//        mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
//                                null, transition.getLabel(), source, target,
//                                "shape=curve;fontSize="+defaultFontSize+";textShape=default;"));
//        //cellTable.put(edge, transition);
//        
//        mxGeometry geometry = edge.getGeometry();
//        if (geometry == null) {
//            geometry = new mxGeometry();
//            edge.setGeometry(geometry);
//            
//        }
//        
//        
//        
//        TransitionGeometricData geometricData = transition.getGeometricData();
//        if (geometricData == null) {
//            geometricData = new TransitionGeometricData();
//        }
//        if (geometricData.labelPosAndDist == null) {
//            geometry.setX(0);
//            geometry.setY(DEFAULT_LABEL_DISTANCE);
//        } else {
//            geometry.setX(geometricData.labelPosAndDist.getX());
//            geometry.setY(geometricData.labelPosAndDist.getY());
//        }
//        if (geometricData.labelOffset == null) {
//            geometry.setOffset(null);
//        } else {
//            geometry.setOffset(new mxPoint(geometricData.labelOffset));
//        }
//        if ((geometricData.controlPoints == null)
//                || (geometricData.controlPoints.isEmpty())) {
//            geometry.setPoints(new ArrayList<mxPoint>());
//        } else {
//            List<mxPoint> points = new ArrayList<mxPoint>();
//            Iterator<Point2D> iterateControlPoints = geometricData.controlPoints.iterator();
//            while (iterateControlPoints.hasNext()) {
//                Point2D controlPoint = iterateControlPoints.next();
//                points.add(new mxPoint(controlPoint));
//            }  // End while (iterateControlPoints.hasNext())
//            geometry.setPoints(points);
//        }
//
//        /*if(source==target){
//            addControlPoint(edge,source.getGeometry().getCenterX()+source.getGeometry().getWidth(),
//                                 source.getGeometry().getCenterY());
//        }
//        */
//        graph.setSelectionCell(edge);
//        TransitionDrawingData drawingdata = automata.getTransitionDrawingData(transition);
//        if (drawingdata != null) {
//            graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
//            graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
//            graph.setCellStyles("startArrow", drawingdata.getStartArrow());
//            graph.setCellStyles("endArrow", drawingdata.getEndArrow());
//
//        }
//       
//        if (this.visibilityGraph != null) {
//                this.visibilityGraph.addHindrance(edge);
//        }
//        
//        return edge;
//        
//    }
    
    public void addControlPoint() {
        
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(popMouseX,popMouseY));
        
        addControlPoint((Transition)automata.getStateAt(loc),
                popPoint.getX(), popPoint.getY());
        setModified(true);
        undoStack.push(STATUS_CHANGE);
    
    }

    private void addControlPoint(Transition transition, double x, double y) {
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(x,y));
        automata.addTransitionControlPoint(transition, loc);

    }

    public void deleteControlPoint() {
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(popMouseX,popMouseY));
        deleteControlPoint((Transition)automata.getStateAt(loc),
                selectedHandlerIndex);
    }

    private void deleteControlPoint(Transition transition, int index) {
        
        // TODO: change to deleteControlPoint(mxCell cell,Point2D point), not depending on jgraph
        
        mxPoint mxpoint=automata.transitionToCell(transition).getGeometry().getPoints().get(index-1);
        automata.deleteTransitionControlPoint(transition, new Point2D.Double(mxpoint.getX(),mxpoint.getY()));
        
        getGraphComponent().refresh();

        setModified(true);
        undoStack.push(STATUS_CHANGE);
        
    }

    public void resetControlPoint() {
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(popMouseX,popMouseY));
        resetControlPoint((Transition)automata.getStateAt(loc));
        setModified(true);
        undoStack.push(STATUS_CHANGE);
    }

    private void resetControlPoint(Transition transition) {
        automata.resetTransitionControlPoint(transition);

    }
    
    // TODO: 
    private void handleInitialFinal(){
        Object objects[] = this.graph.getChildEdges(this.graph.getDefaultParent());
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
    }
    /*
     * TODO: layout should be called with parameter "automata"
     *       something like CircleLayout.execute(automata)
     *       and handle geometric/drawing data of automata structure, as well as of jgraphautomata
     */
    public void doCircleLayout() {
//        mxCircleLayout layout = new mxCircleLayout(this.graph);
//        layout.setResetEdges(true);
//        layout.setMoveCircle(true);
//        layout.setX0(100);
//        layout.setY0(100);
//        Object parent = this.graph.getDefaultParent();
//        layout.execute(this.graph.getDefaultParent());
//        
////        handleInitialFinal();
//        
//        SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(this.graph);
//        singleVertexEdgesLayout.execute(this.graph.getDefaultParent());
//        
//        automata.refresh();
        
        long startTime = System.nanoTime();
        
        if(automata.getSelectedStates().isEmpty()) automata.selectAllStates();
        
        CircularLayoutAutomata layout=new CircularLayoutAutomata();
        
        automata.getGroupList();
        if(automata.selectedStatesContainVertexGroup()){
            GroupReplacedAutomata replaceAutomata=new GroupReplacedAutomata(automata);
//            Automata reautomata=replaceAutomata.createReplaceAutomata();
//            reautomata.selectAllStates();
            layout.doLayout(automata,replaceAutomata);
            //System.out.println("finish circular layout!");
//            automata=reautomata;
            //replaceAutomata.expandStatesToVertexGroups();
        }
        else{
            if(automata.getSelectedStates().isEmpty()) automata.selectAllStates();
            layout.doLayout(automata);
        }
//        System.out.println("finish circular layout!"); 
        automata.refresh();
        this.graph.clearSelection();
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("+++++++++    circular layout time = "+duration+" ns "+
                 " #s= "+automata.getAllStates().size()+"#t= "+automata.getAllTransitions().size()+"+++++++++++++++");
        
        
    }  // End public void doCircleLayout()

    public void doHierarchicalLayout() {
        
        HierarchicalLayoutAutomata layout = new HierarchicalLayoutAutomata();
        layout.doLayout(automata);
        this.graph.refresh();
        /*
        mxHierarchicalLayout layout = new mxHierarchicalLayout(this.graph);
        layout.setOrientation(SwingConstants.NORTH);
        layout.setFineTuning(false);
        layout.setDisableEdgeStyle(false);
        layout.execute(this.graph.getDefaultParent());
        
        handleInitialFinal();
        handleLoop();
        * */
        //handleEdgeControlPoint();
        /*EdgeRoutingLayout edgeRoute = new EdgeRoutingLayout(this.graph);
        edgeRoute.execute(this.graph.getDefaultParent());
        */
        //EdgeRoutingBranchingLayout layout2 = new EdgeRoutingBranchingLayout(this.graph);
        //layout2.execute(this.graph.getDefaultParent());
        
        
    }  // End public void doHierarchicalLayout()

    public void doFeatureLayout() {

                 FeatureLayout featurelayout=new FeatureLayout(this.graph);
                 featurelayout.execute(this.graph.getDefaultParent());
                 
                 handleInitialFinal();
                 handleLoop();
        
                 EdgeRoutingBranchingLayout layout = new EdgeRoutingBranchingLayout(this.graph);
                 layout.execute(this.graph.getDefaultParent());       
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
//        
//        LinearLayout layout=new LinearLayout(this.graph);
//        layout.execute(this.graph.getDefaultParent());
//        automata.getGroupList();
        long startTime = System.nanoTime();
        
        if(automata.getSelectedStates().isEmpty()) automata.selectAllStates();
        
        LinearLayoutAutomata layout=new LinearLayoutAutomata();
        
        automata.getGroupList();
        if(automata.selectedStatesContainVertexGroup()){
            GroupReplacedAutomata replaceAutomata=new GroupReplacedAutomata(automata);
//            Automata reautomata=replaceAutomata.createReplaceAutomata();
//            reautomata.selectAllStates();
            layout.doLayout(automata,replaceAutomata);
//            automata=reautomata;
            //replaceAutomata.expandStatesToVertexGroups();
        }
        else{
            if(automata.getSelectedStates().isEmpty()) automata.selectAllStates();
            layout.doLayout(automata);
        }
          
        automata.refresh();
        this.graph.clearSelection();
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("+++++++++    layout time = "+duration+" ns "+
                " #s= "+automata.getAllStates().size()+"#t= "+automata.getAllTransitions().size()+"+++++++++++++++");
        //SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(this.graph);
        //singleVertexEdgesLayout.execute(this.graph.getDefaultParent());
        
    }
    private void handleLoop(){
        
        Object objects[] = this.graph.getChildEdges(this.graph.getDefaultParent());
        for(Object object:objects){
            mxCell cell=(mxCell)object;
            if(cell.getTarget()==cell.getSource()){
                this.graph.resetEdge(cell);
//                System.out.println("reset edge: "+cell.getSource().getId()+"->"+cell.getTarget().getId());
            }
        }
        this.graph.refresh();
    }
    /*
     * TODO: this is for temporary use after hierachical layout
     */
    private void handleEdgeControlPoint(){
//        Object objects[] = this.graph.getChildEdges(this.graph.getDefaultParent());
//        for(Object object:objects){
//            mxCell cell=(mxCell)object;
//            this.graph.resetEdge(cell);
//            
//        }
//        this.graph.refresh();
    }
    /*
     *  TODO: this is used for now to get an automata synchronized to the one on graph (when saving files),
     *        since not all modifications on graph are synchronized to Automata structure
     */
    public Automata getAutomata() {
            return automata;
//        Automata automata = new Automata();
//        automata.setName(this.automata.getName());
//        automata.setWritingData(this.automata.getWritingData());
//        automata.setWeight(this.automata.getWeight());
//        automata.setAlphabet(this.automata.getAlphabet());
//        automata.setOutputAlphabet(this.automata.getOutputAlphabet());
//        HashMap<mxCell, State> cellToStateMap = new HashMap<mxCell, State>();
//
//        Object[] vertices = this.graph.getChildVertices(graph.getDefaultParent());
//
//        for (int index = 0; index < vertices.length; index++) {
//
//            if (!(vertices[index] instanceof mxCell)) {
//                continue;
//            }
//
//            mxCell vertex = (mxCell) vertices[index];
//            State state = new State();
//            mxGeometry geometry = vertex.getGeometry();
//            Point2D point2d = new Point2D.Double();
//            point2d.setLocation(geometry.getCenterX(), geometry.getCenterY());
//            StateGeometricData geometricData = new StateGeometricData();
//            geometricData.setLocation(point2d);
//            
//            Point2D sizepoint2d = new Point2D.Double();
//            sizepoint2d.setLocation(geometry.getWidth(),geometry.getHeight());
//            geometricData.setSize(sizepoint2d);
//            state.setGeometricData(geometricData);
//            
//            Map<String, Object> styleList = graph.getCellStyle(vertex);
//            state.setShape((String)styleList.get("shape"));
//            
//            //StateInterface.DrawingData drawingData = new StateInterface.DrawingData();
//            //drawingData.fillColor = (String) styleList.get("fillColor");
//            //drawingData.strokeColor = (String) styleList.get("strokeColor");
////            String width = (String) styleList.get("strokeWidth");
////            if (width != null) {
////                drawingData.strokeWidth = Float.valueOf(width);
////            }
//            StateDrawingData drawingData=new StateDrawingData();
//            drawingData.setFillColor((String) styleList.get("fillColor"));
//            drawingData.setStrokeColor((String) styleList.get("strokeColor"));
//            String width = (String) styleList.get("strokeWidth");
//            if (width != null) {
//                drawingData.setStrokeWidth(Float.valueOf(width));
//            }
//            
//            
//            //System.out.println("style:"+drawingData.fillColor+" "+drawingData.strokeColor+" "+drawingData.strokeWidth);
//
//            state.setDrawingData(drawingData);
//
//            automata.addState(state);
//            cellToStateMap.put(vertex, state);
//            Object object = vertex.getValue();
//            if (object instanceof String) {
//                state.setName((String) object);
//            }
//
//
//        }  // End for (int index = 0; index < vertices.length; index++)
//
//        Object[] edges = this.graph.getChildEdges(graph.getDefaultParent());
//
//        for (int index = 0; index < edges.length; index++) {
//
//            if (!(edges[index] instanceof mxCell)) {
//                System.out.println("not an edge!");
//                continue;
//            }
//
//            mxCell edge = (mxCell) edges[index];
//            mxCell sourceVertex = null;
//            mxCell targetVertex = null;
//            Object object = edge.getSource();
//            if (object instanceof mxCell) {
//                sourceVertex = (mxCell) object;
//            }
//            object = edge.getTarget();
//            if (object instanceof mxCell) {
//                targetVertex = (mxCell) object;
//            }
//
//            if (sourceVertex == null) {
//                if (targetVertex == null) {
//                    throw new IllegalArgumentException("An edge can not have null for both source and target!  " + edge.toString());
//                } else {
//                    /*
//                     * This edge is an arrow pointing to an initial state.
//                     */
//                    State state = cellToStateMap.get(targetVertex);
//                    object = edge.getValue();
//                    if (object instanceof InitialFinalWeight) {
//                        InitialFinalWeight initialFinalWeight = (InitialFinalWeight) object;
//                        mxGeometry edgeGeometry = edge.getGeometry();
//                        if (edgeGeometry != null) {
//                            InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
//                            mxPoint point = edgeGeometry.getSourcePoint();
//                            mxGeometry vertexGeometry = targetVertex.getGeometry();
//                            if ((point != null) && (vertexGeometry != null)) {
//                                geometricData.offset = new Point2D.Double(point.getX() - vertexGeometry.getCenterX(), point.getY() - vertexGeometry.getCenterY());
//                            }
//                            if ((edgeGeometry.getX() != 0) || (edgeGeometry.getY() != DEFAULT_LABEL_DISTANCE)) {
//                                geometricData.labelPosAndDist = new Point2D.Double(edgeGeometry.getX(), edgeGeometry.getY());
//                            }
//                            mxPoint labelOffset = edgeGeometry.getOffset();
//                            if (labelOffset != null) {
//                                geometricData.labelOffset = new Point2D.Double(labelOffset.getX(), labelOffset.getY());
//                            }
//                            if ((geometricData.offset != null)
//                                    || (geometricData.labelPosAndDist != null)
//                                    || (geometricData.labelOffset != null)) {
//                                initialFinalWeight.setGeometricData(geometricData);
//                            }
//                            geometricData = null;  // InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
//                        }  // End if (edgeGeometry != null)
//                        state.setInitialWeight(initialFinalWeight);
//                        
//                    } else {
//                        state.setInitialWeight(new InitialFinalWeight(true));
//                    }
//                    continue;
//                }
//            } else {
//                if (targetVertex == null) {
//                    /*
//                     * This edge is an arrow pointing from a final state.
//                     */
//                    State state = cellToStateMap.get(sourceVertex);
//                    object = edge.getValue();
//                    if (object instanceof InitialFinalWeight) {
//                        InitialFinalWeight initialFinalWeight = (InitialFinalWeight) object;
//                        mxGeometry edgeGeometry = edge.getGeometry();
//                        if (edgeGeometry != null) {
//                            InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
//                            mxPoint point = edgeGeometry.getTargetPoint();
//                            mxGeometry vertexGeometry = sourceVertex.getGeometry();
//                            if ((point != null) && (vertexGeometry != null)) {
//                                geometricData.offset = new Point2D.Double(point.getX() - vertexGeometry.getCenterX(), point.getY() - vertexGeometry.getCenterY());
//                            }
//                            if ((edgeGeometry.getX() != 0) || (edgeGeometry.getY() != DEFAULT_LABEL_DISTANCE)) {
//                                geometricData.labelPosAndDist = new Point2D.Double(edgeGeometry.getX(), edgeGeometry.getY());
//                            }
//                            mxPoint labelOffset = edgeGeometry.getOffset();
//                            if (labelOffset != null) {
//                                geometricData.labelOffset = new Point2D.Double(labelOffset.getX(), labelOffset.getY());
//                            }
//                            if ((geometricData.offset != null)
//                                    || (geometricData.labelPosAndDist != null)
//                                    || (geometricData.labelOffset != null)) {
//                                initialFinalWeight.setGeometricData(geometricData);
//                            }
//                            geometricData = null;  // InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
//                        }  // End if (edgeGeometry != null)
//                        state.setFinalWeight(initialFinalWeight);
//                    } else {
//                        state.setFinalWeight(new InitialFinalWeight(true));
//                    }
//                    continue;
//                }
//            }
//
//            Transition transition = new Transition();
//            transition.setSourceState(cellToStateMap.get(sourceVertex));
//            transition.setTargetState(cellToStateMap.get(targetVertex));
//            object = edge.getValue();
//            if (object instanceof WeightedRegularExpression) {
//                transition.setLabel((WeightedRegularExpression) object);
//            }
//            mxGeometry geometry = edge.getGeometry();
//            if (geometry != null) {
//                TransitionGeometricData geometricData = new TransitionGeometricData();
//                if ((geometry.getX() != 0) || (geometry.getY() != DEFAULT_LABEL_DISTANCE)) {
//                    geometricData.labelPosAndDist = new Point2D.Double(geometry.getX(), geometry.getY());
//                }
//                mxPoint offset = geometry.getOffset();
//                if (offset != null) {
//                    geometricData.labelOffset = new Point2D.Double(offset.getX(), offset.getY());
//                }
//                List<mxPoint> points = geometry.getPoints();
//                if ((points != null) && (!points.isEmpty())) {
//                    Iterator<mxPoint> iteratePoints = points.iterator();
//                    while (iteratePoints.hasNext()) {
//                        mxPoint point = iteratePoints.next();
//                        geometricData.controlPoints.add(new Point2D.Double(point.getX(), point.getY()));
//                        
//                    }  // End while (iteratePoints.hasNext())
//                }  // End if ((points != null) && (!points.isEmpty()))
//                transition.setGeometricData(geometricData);
//            }  // End if (geometry != null)
//
//            TransitionDrawingData drawingData = new TransitionDrawingData();
//            Map<String, Object> styleList = graph.getCellStyle(edge);
//            drawingData.setStrokeColor((String) styleList.get("strokeColor"));
//            String width = (String) styleList.get("strokeWidth");
//            if (width != null) {
//                drawingData.setStrokeWidth(Float.valueOf(width));
//            }
//
//            drawingData.setStartArrow ((String) styleList.get("startArrow"));
//            drawingData.setEndArrow((String) styleList.get("endArrow"));
////            System.out.println("style:" + drawingData.strokeColor + " " + drawingData.strokeWidth + " " + drawingData.startArrow + " " + drawingData.endArrow);
//
//            transition.setDrawingData(drawingData);
//
//            //System.out.println("add transition: "+transition.getSourceState().getName()+" -> "+transition.getTargetState().getName());
//            automata.addTransition(transition);
//
//        }  // End for (int index = 0; index < edges.length; index++)
//        
//        
//        return automata;
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
//        if (graphComponent == null) {
//            graphComponent = new mxGraphComponent(graph) {
//
//                @Override
//                protected void installDoubleClickHandler() {
//                    graphControl.addMouseListener(new MouseAdapter() {
//
//                        public void mouseReleased(MouseEvent e) {
//                            if (isEnabled()) {
//                                if (!e.isConsumed() && isEditEvent(e)) {
//                                    mxCell cell = (mxCell)getCellAt(e.getX(), e.getY(), false);
//
//                                    if (cell != null && getGraph().isCellEditable(cell)) {
//                                        if (((mxCell) cell).isVertex()) {
//                                            startEditingAtCell(cell, e);
//                                        } else {
//                                            mxCell source=(mxCell)cell.getSource();
//                                            mxCell target=(mxCell)cell.getTarget();
//                                            
//                                            if(source!=null && target!=null){
////                                            ExpressionEditor editor =
////                                                    new ExpressionEditor(
////                                                    new JFrame(), true,
////                                                    (WeightedRegularExpression) ((mxCell) cell).getValue());
//                                            ExpressionEditor editor =
//                                                    new ExpressionEditor(
//                                                    new JFrame(), true,
//                                                    ((Transition)automata.cellToState(cell)).getLabel());
//                                            editor.setVisible(true);
//                                            //((mxCell) cell).setValue(editor.getExpression());
//                                            
//                                            // set weight in automata!!
//                                            automata.setTransitionLabel((Transition)automata.cellToState(cell), editor.getExpression());
//                                            
//                                            
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    // Other languages use focus traversal here, in Java
//                                    // we explicitely stop editing after a click elsewhere
//                                    stopEditing(!invokesStopCellEditing);
//                                }
//                            }
//                        }
//                    });
//                }
//            };
//        }

        return automata.jgraphAutomata.graphComponent;
    }

    /**
     * @return the graphPopupMenu
     */
    public javax.swing.JPopupMenu getGraphPopupMenu() {
        return graphPopupMenu;
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
                    automata.deleteState(cellToState(cell));
            }else{
                automata.deleteTransition(cellToTransition(cell));
            }
        }
        
       
//                State state = cellToState(cell);
//
//                List<Transition> relatedTrans = state.getTransitions();
//                for (Transition trans : relatedTrans) {
//                    List<Transition> transList = automata.getAllTransitions();
//                    transList.remove(trans);
//                    automata.setAllTransitions(transList);
//                }
//
//
//                List<State> stateList = automata.getAllStates();
//                stateList.remove(state);
//                automata.setAllStates(stateList);
//                //System.out.println("state list size: "+automata.getAllStates().size());
//                //related transitions?
//
//
//
//            } else if (cell.isEdge()) {
//                Transition trans = cellToTransition(cell);
//                if (trans == null) {
////                    System.out.println("can't find trans");
//                }
//                List<Transition> transList = automata.getAllTransitions();
//                transList.remove(trans);
//                automata.setAllTransitions(transList);
////                System.out.println("trans list size: " + automata.getAllTransitions().size());
//
//
//            }
//
//
//            //cellTable.remove(cell);
//            mxCell[] cells = {cell};
//            graph.removeCells(cells);
//
////            System.out.println("cell table size: " + cellTable.size());
//
//
//        } else {
////            System.out.println("Cell is empty");
//        }
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
        //return (State) cellTable.get(cell);
        return (State)automata.cellToState(cell);
    }

    private Transition cellToTransition(mxCell cell) {
        return (Transition)automata.cellToState(cell);
    }

    public void undo() {

        undoManager.undo();
        //updateUndoChangedCell();

    }

    public void redo() {

        undoManager.redo();
        //updateRedoChangedCell();

    }

//    public void updateUndoChangedCell() {
//
//        //update cells in changedCell[]
//        if (!undoStack.isEmpty()) {
//            int stat = undoStack.pop();
//            redoStack.push(stat);
//
////            System.out.println("stack pop:" + stat);
//
//            for (Object cel : changedCell) {
//
//                mxCell cell = (mxCell) cel;
//
//                switch (stat) {
//                    case STATUS_ADD: //so remove it!
//
//                        deleteCell(cell);
//                        break;
//
//                    case STATUS_DELETE: //so add it again!
//                        if (cell.isVertex()) {
//
//                            State state = new State();
//                            state.setName(cell.getValue().toString());
//                            StateGeometricData geo = new StateGeometricData();
//                            geo.setLocation(new Point2D.Double(
//                                    cell.getGeometry().getX(),
//                                    cell.getGeometry().getY()));
//                            state.setGeometricData(geo);
//
//                            cellTable.put(cell, state);
//                            automata.addState(state);
//                            //related transitions
//
//                        } else {
//                            Transition trans = new Transition();
//                            List<mxPoint> jpt = cell.getGeometry().getPoints();
//                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
//                            for (mxPoint pt : jpt) {
//                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
//                            }
//                            TransitionGeometricData geo = new TransitionGeometricData();
//                            geo.controlPoints = apt;
//                            trans.setGeometricData(geo);
//
//                            trans.setLabel((WeightedRegularExpression) cell.getValue());
//                            trans.setSourceState(cellToState((mxCell) cell.getSource()));
//                            trans.setTargetState(cellToState((mxCell) cell.getTarget()));
//
//
//                            automata.addTransition(trans);
//                            cellTable.put(cell, trans);
//                        }
//                        break;
//
//                    case STATUS_CHANGE:
//
//                        if (((mxCell) cell).isVertex()) {
//                            State state = cellToState(cell);
//                            
//                            Point2D newgeo=state.getGeometricData().getLocation();
//                            
//                            StateGeometricData geo = new StateGeometricData();
//                            
//                            
//                            geo.setLocation(new Point2D.Double(cell.getGeometry().getX(), cell.getGeometry().getY()));
//                            state.setGeometricData(geo);
//                            state.setName((String) cell.getValue());
//                            
//                            
//                            // update control pts of edges, loops, initial/finals
//                            Point2D offset=new Point2D.Double(newgeo.getX()-geo.getX(),
//                                                       newgeo.getY()-geo.getY());
//                            updateControlPoint(new mxCell[]{cell},offset);
//                            
//                            
//                        } else {
//
//                            Transition trans = cellToTransition(cell);
//                            mxGeometry geo=cell.getGeometry();
//                            if(geo!=null){
//                                List<mxPoint> jpt = geo.getPoints();
//                                if(!jpt.isEmpty()){
//                                    ArrayList<Point2D> apt = new ArrayList<Point2D>();
//                                
//                                    for (mxPoint pt : jpt) {
//                                        apt.add(new Point2D.Double(pt.getX(), pt.getY()));
//                                    }
//                                
//                                    trans.getGeometricData().controlPoints = apt;
//                                }
//                            }
//                            trans.setLabel((WeightedRegularExpression) cell.getValue());
//                        }
//                        break;
//                    default:
//                        break;
//
//                }
//            }
//        }
//    }
//
//    public void updateRedoChangedCell() {
//
//        if (!redoStack.isEmpty()) {
//            int stat = redoStack.pop();
//            undoStack.push(stat);
//
////            System.out.println("stack pop:" + stat);
//
//            for (Object cel : changedCell) {
//
//                mxCell cell = (mxCell) cel;
//
//                switch (stat) {
//                    case STATUS_DELETE: //so remove it!
//
//                        deleteCell(cell);
//
//                        break;
//
//                    case STATUS_ADD: //so add it again!
//                        if (cell.isVertex()) {
//
//                            State state = new State();
//                            state.setName(cell.getValue().toString());
//                            StateGeometricData geo = new StateGeometricData();
//                            geo.setLocation(new Point2D.Double(cell.getGeometry().getX(), cell.getGeometry().getY()));
//                            state.setGeometricData(geo);
//
//                            cellTable.put(cell, state);
//                            automata.addState(state);
//                            //related transitions
//
//
//
//                        } else {
//                            Transition trans = new Transition();
//                            List<mxPoint> jpt = cell.getGeometry().getPoints();
//                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
//                            for (mxPoint pt : jpt) {
//                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
//                            }
//                            TransitionGeometricData geo = new TransitionGeometricData();
//                            geo.controlPoints = apt;
//                            trans.setGeometricData(geo);
//
//                            trans.setLabel((WeightedRegularExpression) cell.getValue());
//                            trans.setSourceState(cellToState((mxCell) cell.getSource()));
//                            trans.setTargetState(cellToState((mxCell) cell.getTarget()));
//
//
//                            automata.addTransition(trans);
//                            cellTable.put(cell, trans);
//                        }
//
//                        //undoStack.push(STATUS_ADD);
//                        break;
//
//                    case STATUS_CHANGE:
//
//                        if (((mxCell) cell).isVertex()) {
//                            State state = cellToState(cell);
//                            
//                            Point2D newgeo=state.getGeometricData().getLocation();
//                           
//                            StateGeometricData geo = new StateGeometricData();
//                            
//                            
//                            geo.setLocation (new Point2D.Double(
//                                    cell.getGeometry().getX(),
//                                    cell.getGeometry().getY()));
//                            state.setGeometricData(geo);
//
//                            state.setName((String) cell.getValue());
//
//                             // update control pts of edges, loops, initial/finals
//                            //Point2D offset=new Point2D.Double(newgeo.getX()-geo.location.getX(),
//                            //                           newgeo.getY()-geo.location.getY());
//                            //updateControlPoint(new Object[]{cell},offset);
//                            
//                            
//                        } else {
//
//                            Transition trans = cellToTransition(cell);
//                            List<mxPoint> jpt = cell.getGeometry().getPoints();
//                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
//                            for (mxPoint pt : jpt) {
//                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
//                            }
//
//                            trans.getGeometricData().controlPoints = apt;
//                            trans.setLabel((WeightedRegularExpression) cell.getValue());
//                        }
//                        //undoStack.push(STATUS_CHANGE);
//                        break;
//                    default:
//                        break;
//
//                }
//
//
//            }
//        }
//
//
//    }
    
    
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
    //protected Hashtable<mxCell, Object> cellTable;
    //protected mxCell transitionFrom, transitionTo;
    protected State transitionFrom,transitionTo;
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
    static public VGI vgi;
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
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        applyLinearLayoutMenuItem = new javax.swing.JMenuItem();
        groupMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        UngroupMenuItem = new javax.swing.JMenuItem();

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
        graphPopupMenu.add(jSeparator1);

        applyLinearLayoutMenuItem.setText("Apply Linear Layout");
        applyLinearLayoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyLinearLayoutMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(applyLinearLayoutMenuItem);

        groupMenuItem.setText("Fixed Group Layout");
        groupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(groupMenuItem);

        copyMenuItem.setText("copy");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(copyMenuItem);

        UngroupMenuItem.setLabel("Ungroup");
        UngroupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UngroupMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(UngroupMenuItem);

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
//        Object cell = getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY);
//   
//        if (cell != null) {
//            if (((mxCell) cell).isVertex()) {
//                transitionFrom = (mxCell) cell;
//               // System.out.println("add transition from :"+((mxCell)cell).getId());
//            }
//        }
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(popMouseX,popMouseY));
        Object source=automata.getStateAt(loc);
        if(source!=null){
            if(source instanceof State){
                transitionFrom=(State)source;
            }
        }
    }//GEN-LAST:event_addTransitionFromMenuItemActionPerformed

    private void addTransitionToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionToMenuItemActionPerformed
        // TODO add your handling code here:
//        Object cell = getGraphComponent().getCellAt((int) popMouseX, (int) popMouseY);
//        
//        if (cell != null) {
//            if (((mxCell) cell).isVertex()) {
//                //System.out.println("add transition");
//                if (transitionFrom != null) {
//                    transitionTo = (mxCell) cell;
//                    addTransition(transitionFrom, transitionTo);
//                    // System.out.println("add transition to :"+((mxCell)cell).getId());
//                    transitionFrom = null;
//                    transitionTo = null;
//                }
//            }
//        }
        Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(popMouseX,popMouseY));
        Object target=automata.getStateAt(loc);
        if(target!=null){
            if(target instanceof State){
                if(transitionFrom!=null){
                    transitionTo=(State)target;
                    addTransition(transitionFrom,transitionTo);
                    transitionFrom=transitionTo=null;
                    
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

    private void applyLinearLayoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyLinearLayoutMenuItemActionPerformed
        if(automata.getSelectedStates().size()>1){
            this.doLinearLayout();
            automata.setSelectedStates(automata.getSelectedStates());
        }
    }//GEN-LAST:event_applyLinearLayoutMenuItemActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        
        List<State> selectedStates=automata.getSelectedStates();
        List<State> savethem=new ArrayList<State>();
        for(State st:selectedStates){
            savethem.add(st);
        }
        automata.copyStates(savethem);
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void groupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupMenuItemActionPerformed
        automata.groupObjects(automata.getSelectedObjs());
        /*
        if(automata.getSelectedStates().size()>1){
//            automata.setSelectedStates(automata.getSelectedStates());
            automata.groupVertices(automata.getSelectedStates());
//            this.graph.clearSelection();
//            automata.setSelectedStates(automata.getSelectedStates());

//           System.out.println("goup menu Item Action Performed!!");
        }*/
        
    }//GEN-LAST:event_groupMenuItemActionPerformed

    private void UngroupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UngroupMenuItemActionPerformed
        automata.unGroupObjects(automata.getSelectedObjs());
    }//GEN-LAST:event_UngroupMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem UngroupMenuItem;
    private javax.swing.JMenuItem addControlPointMenuItem;
    private javax.swing.JMenuItem addStateMenuItem;
    private javax.swing.JMenuItem addTransitionFromMenuItem;
    private javax.swing.JMenuItem addTransitionToMenuItem;
    private javax.swing.JMenuItem applyLinearLayoutMenuItem;
    private javax.swing.JMenuItem cancelMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem deleteControlPointMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JPopupMenu graphPopupMenu;
    private javax.swing.JMenuItem groupMenuItem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem resetControlPointMenuItem;
    private javax.swing.JMenuItem routeEdgeMenuItem;
    private javax.swing.JMenuItem routeEdgeWVGMenuItem;
    // End of variables declaration//GEN-END:variables
    public boolean addStateFlag; //ellie
    void randomGenerateStates() {
        automata.randomGenerateStates(10);
        automata.refresh();
    }

    void exportImage() throws IOException {
        
        Dimension d = graphComponent.getGraphControl().getSize();
        BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        graphComponent.getGraphControl().paint(g);
        
        //java.util.Date now = new java.util.Date(); 
        
        final File outputfile = new File("result_image/test_"+System.nanoTime()+".png");
        ImageIO.write(image, "png", outputfile);
        
    }
}
