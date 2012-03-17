package vgi;


import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
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
import java.io.File;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    /** Creates new form JgraphXInternalFrame */
    public JgraphXInternalFrame(JSplitPane infoSplitPane, mxGraph graph, 
                               Automata automata, String title) {
        super(automata.getName(),
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        initComponents();
        
        this.setTitle(title);
        
        this.graph = graph;
        graphComponent = getGraphComponent();
        graph.setDisconnectOnMove(false);
        graphComponent.setConnectable(false);
        graphOutline = new mxGraphOutline(graphComponent);
        graphComponent.getViewport().setBackground(Color.WHITE);
        this.automata = automata;
        this.infoSplitPane = infoSplitPane;
        if ((topPanel != null) && (bottomPanel != null)) {
            this.topPanel = (JPanel)infoSplitPane.getTopComponent();
            this.bottomPanel = (JPanel)infoSplitPane.getBottomComponent();
        }
        
        this.setBounds(xOffset*openFrameCount, yOffset*openFrameCount, 800, 600);
        openFrameCount++;
        this.getContentPane().add(graphComponent, java.awt.BorderLayout.CENTER);

        //cellTable = new Hashtable<Integer, mxCell>();
        cellTable = new Hashtable<mxCell,Object>();

        installRepaintListener();
        installListeners();
        installInternalFrameListeners();

        if (automata != null) {
            setupStates();
            setupTranitions();
            
            if (!(this.hasGeometricData)) {
                mxCircleLayout circleLayout = new mxCircleLayout(this.graph);
                circleLayout.execute(this.graph.getDefaultParent());
            }
            
            setupInitialFinal();
        }
		this.graph.refresh();
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
                maybeShowPopup(e);
                if (graphComponent.getCellEditor() != null)
                    graphComponent.getCellEditor().stopEditing(true);
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
                
                if (selected) {
                    edgeSelected = selectedCell.isEdge();
                    if (edgeSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(graph, selectedCell,
                                JgraphXInternalFrame.this.cellToTransition(selectedCell)));
                    }
                    
                    vertexSelected = selectedCell.isVertex();
                    if (vertexSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(graph, selectedCell, automata, 
                                JgraphXInternalFrame.this.cellToState(selectedCell)));

                        addTransitionFromMenuItem.setVisible(
                                (transitionFrom == null) ? true : false);
                        addTransitionToMenuItem.setVisible(
                                (transitionFrom == null) ? false : true);
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                        new AutomataPropertiesPanel(graph, automata));
                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                }
                addStateMenuItem.setVisible(!selected);
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected);
                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);
                setInitialMenuItem.setVisible(vertexSelected);
                setFinalMenuItem.setVisible(vertexSelected);

//                if (vertexSelected) {
//                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
//                            new state_properties(graph, selectedCell));
//
//                    addTransitionFromMenuItem.setVisible(
//                            (transitionFrom == null) ? true : false);
//                    addTransitionToMenuItem.setVisible(
//                            (transitionFrom == null) ? false : true);
//                } else {
//                    if (edgeSelected) {
//                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
//                                new edge_properties(graph, selectedCell));
//                    } else {
//                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
//                                new Automata_properties());
//                    }
//
//                    addTransitionFromMenuItem.setVisible(false);
//                    addTransitionToMenuItem.setVisible(false);
//                }
                JgraphXInternalFrame.this.validate();
                
                maybeShowPopup(e);

                graph.refresh();
                graph.repaint();
            }
            
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popMouseX = e.getX();
                    popMouseY = e.getY();
                    getGraphPopupMenu().show(getGraphComponent(), popMouseX, popMouseY);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e){
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
                                State selectedState = cellToState(selectedCell);
                                State.GeometricData geo=new State.GeometricData();
                                geo.location=new Point2D.Double(e.getX(),e.getY());
                                selectedState.setGeometricData(geo);
                               // System.out.println("update location");
                                setModified(true);
                            }       
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        mouseDragged(e);
                    }
                });
    }
    
    public void installInternalFrameListeners() {
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                boolean selected = !graph.isSelectionEmpty();
                mxCell selectedCell = (mxCell) graph.getSelectionCell();

                boolean edgeSelected = false;
                boolean vertexSelected = false;
                
                if (selected) {
                    if (selectedCell.isEdge()) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(graph, selectedCell,
                                JgraphXInternalFrame.this.cellToTransition(selectedCell)));
                    } else if (selectedCell.isVertex()) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(graph, selectedCell, automata,
                                JgraphXInternalFrame.this.cellToState(selectedCell)));
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                        new AutomataPropertiesPanel(graph, automata));
                }
                
                JgraphXInternalFrame.this.infoSplitPane.setBottomComponent(
                        graphOutline);
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
        Iterator<mxCell> cellIterator = this.initialFinalCells.iterator();
		while (cellIterator.hasNext()) {
			Object parent = this.graph.getDefaultParent();
			mxCell vertex = cellIterator.next();
			State state = this.cellToState(vertex);

			Object initialWeight = state.getInitialWeight();
			if (initialWeight != null) {
				setupInitialFinal(parent, initialWeight, vertex, false);
			}
            
			Object finalWeight = state.getFinalWeight();
			if (finalWeight != null) {
				setupInitialFinal(parent, finalWeight, vertex, true);
			}
		}  // End while (cellIterator.hasNext())
    }
    
    private void setupInitialFinal(Object parent, Object weight, Object vertex, boolean isSource) {
        Object edge;
        WeightedRegularExpression.Atomic expression = 
                WeightedRegularExpression.Atomic.createAtomic(automata);
        expression.setSymbol(weight);
        Transition newTrans = new Transition();
        newTrans.setLabel(expression);

        mxPoint terminalPoint;
        if (isSource) {
            Point2D p = createTerminalPoint((mxCell)vertex);
            terminalPoint = new mxPoint(p.getX(), p.getY());
            edge = this.graph.insertEdge(parent, null, expression, null, vertex);
            
            newTrans.setSourceState(null);
            newTrans.setTargetState(cellToState((mxCell)vertex));
        }
        else {
            Point2D p = createTerminalPoint((mxCell)vertex);
            terminalPoint = new mxPoint(p.getX(), p.getY());
            edge = this.graph.insertEdge(parent, null, expression, vertex, null);
            
            newTrans.setSourceState(cellToState((mxCell)vertex));
            newTrans.setTargetState(null);
        }

        Object[] cell = {edge};
        graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cell);
        graph.getCellGeometry(edge).setTerminalPoint(terminalPoint, isSource);
        ((mxCell)edge).getGeometry().setY(DEFAULT_LABEL_DISTANCE);
        
        cellTable.put((mxCell)edge, newTrans);
    }
    
    private Point2D createTerminalPoint(mxCell vertex) {
        int counter = vertex.getEdgeCount();
        int i = 0, j = 0;
        Point2D p1 = new Point(), p2 = new Point(),
                p3 = new Point(), p4 = new Point();
        while (j < 4) {
            boolean flag = false;
            mxGeometry geo = vertex.getGeometry();
            switch (j) {
                case 0:
                    p3.setLocation(geo.getCenterX(), geo.getCenterY() - geo.getHeight()/2);
                    p4.setLocation(geo.getCenterX(), geo.getCenterY() - geo.getHeight());
                    break;
                case 1:
                    p3.setLocation(geo.getCenterX(), geo.getCenterY() + geo.getHeight()/2);
                    p4.setLocation(geo.getCenterX(), geo.getCenterY() + geo.getHeight());
                    break;
                case 2:
                    p3.setLocation(geo.getCenterX() - geo.getWidth()/2, geo.getCenterY());
                    p4.setLocation(geo.getCenterX() - geo.getWidth(), geo.getCenterY());
                    break;
                case 3:
                    p3.setLocation(geo.getCenterX() + geo.getWidth()/2, geo.getCenterY());
                    p4.setLocation(geo.getCenterX() + geo.getWidth(), geo.getCenterY());
                    break;
            }
            
            for (; i < counter; i++) {
                mxCell edge = (mxCell) vertex.getEdgeAt(i);
                p1 = getTerminalPoint(edge, true);
                p2 = getTerminalPoint(edge, false);
                
                if (isIntersect (p1, p2, p3, p4)) {
                    flag = true;
                    break;
                }
            }
            
            if ((i == counter) && !flag)
                return p4;
                
            j++;
        }
        return p4;
    }
    
    private Point2D getTerminalPoint(mxCell edge, boolean isSource) {
        mxCell cell = (mxCell) edge.getTerminal(isSource);
        Point2D p = new Point();
        
        if (cell != null) {
            p.setLocation(cell.getGeometry().getCenterX(), cell.getGeometry().getCenterY());
        }
        else {
            p.setLocation(graph.getCellGeometry(edge).getTerminalPoint(isSource).getPoint());
        }
        
        return p;
    }
    
    private boolean isIntersect(Point2D p, Point2D q, Point2D r, Point2D s) {
        double rCrossPQ = cross(p, q, r);
        double sCrossPQ = cross(p, q, s);
        double pCrossRS = cross(r, s, p);
        double qCrossRS = cross(r, s, q);
        
        if ((rCrossPQ * sCrossPQ < 0) && (pCrossRS * qCrossRS < 0))
            return true;
        if (rCrossPQ == 0) return isIntersect(p, q, r); 
        if (sCrossPQ == 0) return isIntersect(p, q, r); 
        if (pCrossRS == 0) return isIntersect(r, s, p); 
        if (qCrossRS == 0) return isIntersect(r, s, q); 

        return false;
    }
    
    private double cross(Point2D p, Point2D q, Point2D r) {
        return (q.getX()-p.getX()) * (r.getY()-p.getY()) - 
               (q.getY()-p.getY()) * (r.getX()-p.getX());
    }
    
    private boolean isIntersect(Point2D p, Point2D q, Point2D r) {
        return (cross(p, q, r) == 0 && dot(r, p, q) <= 0);
    }
    
    private double dot(Point2D p, Point2D q, Point2D r) {
        return (q.getX()-p.getX()) * (r.getX()-p.getX()) - 
               (q.getY()-p.getY()) * (r.getY()-p.getY()); 
    }
    
    public void addState(double x, double y) {
        State newState = new State();
        State.GeometricData geo = new State.GeometricData();
        geo.location = new Point2D.Double(x, y);
        //geo.shape="elipse";
        newState.setGeometricData(geo);

        automata.addState(newState);
        mxCell vertex = createVertex(x, y, newState.getName());
        cellTable.put(vertex, newState);
        
        infoSplitPane.setTopComponent(
                new StatePropertiesPanel(graph, (mxCell)vertex, automata, newState));

        System.out.println("add state at" + x + "," + y);
        System.out.println("total states:" + automata.getAllStates().size());

        setModified(true);
    }

	public void addState(State state) {
        StateInterface.GeometricData geometricData = state.getGeometricData();
        double x = 0;
        double y = 0;
        if ((geometricData == null) || (geometricData.location == null)) {
            this.hasGeometricData = false;
        } else {
            x = geometricData.location.getX();
            y = geometricData.location.getY();
        }
        
        mxCell vertex = createVertex(x, y, state.getName());
        cellTable.put(vertex, state);

        if ((state.getInitialWeight() != null) || (state.getFinalWeight() != null)) {
            this.initialFinalCells.add(vertex);
        }
        
        infoSplitPane.setTopComponent(
                new StatePropertiesPanel(graph, (mxCell)vertex, automata, 
                JgraphXInternalFrame.this.cellToState(vertex)));
        System.out.println("add state at (" + x + "," + y + ").");
	}  // End public void addState(State state)
    
    private mxCell createVertex(double x, double y, String name) {
        Object parent = graph.getDefaultParent();
        int id = cellTable.size();
        Object vertex = graph.insertVertex(parent, Integer.toString(id), name,
                x - 25, y - 25, 50, 50, "shape=ellipse;perimeter=ellipsePerimeter;");
        graph.setSelectionCell(vertex);
        
        return (mxCell) vertex;
    }

    public void addTransition(mxCell source, mxCell target) {
        Object parent = graph.getDefaultParent();
        WeightedRegularExpression.Atomic expression = 
                WeightedRegularExpression.Atomic.createAtomic(automata);
        expression.setSymbol(expression.getAlphabet().allSymbols.get(0));
        Object e = graph.insertEdge(parent, null, expression, source, target, null);
        ArrayList<mxPoint> points = new ArrayList<mxPoint>();
        ((mxCell) e).getGeometry().setPoints(points);
		((mxCell) e).getGeometry().setY(DEFAULT_LABEL_DISTANCE);
        Transition newTrans = new Transition();
        cellTable.put((mxCell)e, newTrans);
        newTrans.setSourceState(cellToState(source));
        newTrans.setTargetState(cellToState(target));
        newTrans.setLabel(expression);
        automata.addTransition(newTrans);
    
        System.out.println("total trans:"+automata.getAllTransitions().size());
        
        setModified(true);
    }

	public void addTransition(Transition transition) {
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
            
            if ((source != null) && (target != null))
                break;
        }

        mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(), 
                                null, transition.getLabel(), source, target, 
                                "shape=curve"));
        cellTable.put(edge, transition);
        edge.getGeometry().setPoints(new ArrayList<mxPoint>());
        edge.getGeometry().setY(DEFAULT_LABEL_DISTANCE);
	}  // End public void addTransition(Transition transition)

    public void addControlPoint() {
        addControlPoint((mxCell) getGraphComponent().getCellAt(popMouseX, popMouseY),
                popMouseX, popMouseY);
    }
    
    public void addControlPoint(mxCell cell, int x, int y) {
        System.out.println("add Ctrl pt at" + x + "," + y);
        ArrayList<mxPoint> points = (ArrayList) cell.getGeometry().getPoints();
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
        
        setModified(true);
    }

	public void doCircleLayout() {
		mxCircleLayout layout = new mxCircleLayout(this.graph);
		layout.setResetEdges(true);
		layout.execute(this.graph.getDefaultParent());
	}  // End public void doCircleLayout()

	public void doHierarchicalLayout() {
		mxHierarchicalLayout layout = new mxHierarchicalLayout(this.graph);
		layout.execute(this.graph.getDefaultParent());
	}  // End public void doHierarchicalLayout()

	public Automata getAutomata() {
		return this.automata;
	}

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
                                    Object cell = getCellAt(e.getX(), e.getY(), false);

                                    if (cell != null && getGraph().isCellEditable(cell)) {
                                        if (((mxCell) cell).isVertex()) {
                                            startEditingAtCell(cell, e);
                                        } else {
                                            ExpressionEditor editor =
                                                    new ExpressionEditor(new JFrame(), true, (WeightedRegularExpression)((mxCell)cell).getValue());
                                            editor.setVisible(true);
                                            ((mxCell)cell).setValue(editor.getExpression());
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
    }

    public void deleteCell(mxCell cell) {
        if (cell != null) {
            cellTable.remove(cell);
            mxCell[] cells = {cell};
            graph.removeCells(cells);
        } else {
            System.out.println("Cell is empty");
        }
        graph.refresh();
    }

    
    public void setModified(boolean mod){
        modified=mod;
        
        String title=null;
        if(currentFile==null) title="untitled";
        else title=currentFile.getName();
            
        if(!modified){
            
            this.setTitle(title);
        }else{
            this.setTitle(title+"*");
        }
        
    }
    
    private State cellToState(mxCell cell) {
        return (State) cellTable.get(cell);
    }
    
    private Transition cellToTransition(mxCell cell) {
        return (Transition) cellTable.get(cell);
    }
    
    public static final double DEFAULT_LABEL_DISTANCE = 15;
    private static final long serialVersionUID = -6561623072112577140L;
    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;
    private mxGraphComponent graphComponent = null;
    private mxGraphOutline graphOutline;
    final mxGraph graph;
    protected boolean modified = false;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    //protected Hashtable<Integer, mxCell> cellTable;
    protected Hashtable<mxCell, Object> cellTable;
    protected mxCell transitionFrom, transitionTo;
    protected int popMouseX, popMouseY;
    protected JSplitPane infoSplitPane;
    private static JPanel topPanel = null, bottomPanel = null;
    protected Automata automata;
	private boolean hasGeometricData = true;
	private List<mxCell> initialFinalCells = new ArrayList<mxCell>();
    protected File currentFile = null;  
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        setInitialMenuItem = new javax.swing.JMenuItem();
        setFinalMenuItem = new javax.swing.JMenuItem();

        addStateMenuItem.setText("Add State");
        addStateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStateMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addStateMenuItem);

        addTransitionFromMenuItem.setText("Add Transition (From)");
        addTransitionFromMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransitionFromMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addTransitionFromMenuItem);

        addTransitionToMenuItem.setText("Add Transition (To)");
        addTransitionToMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransitionToMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addTransitionToMenuItem);

        addControlPointMenuItem.setText("Add Control Point");
        addControlPointMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addControlPointMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addControlPointMenuItem);

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

        setInitialMenuItem.setText("Set Initial State");
        graphPopupMenu.add(setInitialMenuItem);

        setFinalMenuItem.setText("Set Final State");
        graphPopupMenu.add(setFinalMenuItem);

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
        addState(popMouseX, popMouseY);
    }//GEN-LAST:event_addStateMenuItemActionPerformed

    private void addTransitionFromMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionFromMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = getGraphComponent().getCellAt(popMouseX, popMouseY);
        if (cell != null) {
            if (((mxCell) cell).isVertex()) {
                transitionFrom = (mxCell) cell;
            }
        }
    }//GEN-LAST:event_addTransitionFromMenuItemActionPerformed

    private void addTransitionToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionToMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = getGraphComponent().getCellAt(popMouseX, popMouseY);
        if (cell != null) {
            if (((mxCell) cell).isVertex()) {
                //System.out.println("add transition");
                if (transitionFrom != null) {
                    transitionTo = (mxCell) cell;
                    addTransition(transitionFrom, transitionTo);

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addControlPointMenuItem;
    private javax.swing.JMenuItem addStateMenuItem;
    private javax.swing.JMenuItem addTransitionFromMenuItem;
    private javax.swing.JMenuItem addTransitionToMenuItem;
    private javax.swing.JMenuItem cancelMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JPopupMenu graphPopupMenu;
    private javax.swing.JMenuItem setFinalMenuItem;
    private javax.swing.JMenuItem setInitialMenuItem;
    // End of variables declaration//GEN-END:variables
}
