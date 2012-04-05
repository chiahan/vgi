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
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import vgi.TransitionInterface.GeometricData;

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
            Automata automata, String title) {
        super(automata.getName(),
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable

        initComponents();

        this.infoSplitPane = infoSplitPane;
        this.graph = graph;
        this.automata = automata;
        setTitle(title);

        initGraph();
        installRepaintListener();
        installListeners();
        installInternalFrameListeners();

        if (automata != null) {
            initAutomata();
        }

        setupUndoRedo();
    }

    private void initGraph() {
        graph.setDisconnectOnMove(false);
        graph.setSplitEnabled(false);
        graph.setCellsDisconnectable(false);
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
        setupTranitions();

        if (!(this.hasGeometricData)) {
            mxCircleLayout circleLayout = new mxCircleLayout(this.graph);
            circleLayout.execute(this.graph.getDefaultParent());
        }

        setupInitialFinal();

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

//                System.out.println("Cells moved: " + cells.length + " " + dx + " " + dy);
                updateInitialFinal(cells, new Point2D.Double(dx, dy));
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
                maybeShowPopup(e);
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

                if (selected) {
                    DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
                    
                    edgeSelected = selectedCell.isEdge();
                    if (edgeSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell, 
                                                   display.cellToTransition(selectedCell), 
                                                   display));
                    }

                    vertexSelected = selectedCell.isVertex();
                    if (vertexSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(selectedCell, 
                                JgraphXInternalFrame.this.cellToState(selectedCell), 
                                display)
                                );

                        addTransitionFromMenuItem.setVisible(
                                (transitionFrom == null) ? true : false);
                        addTransitionToMenuItem.setVisible(
                                (transitionFrom == null) ? false : true);
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                            new AutomataPropertiesPanel(
                                new DisplayUtil(graph, automata, cellTable)));
                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                    graph.setSelectionCell(null);
                }
                addStateMenuItem.setVisible(!selected);
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected);
                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);
                setInitialMenuItem.setVisible(vertexSelected);
                setFinalMenuItem.setVisible(vertexSelected);

                JgraphXInternalFrame.this.validate();

                maybeShowPopup(e);

                graph.refresh();
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popMouseX = e.getX();
                    popMouseY = e.getY();
                    getGraphPopupMenu().show(getGraphComponent(), popMouseX, popMouseY);
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
                boolean selected = !graph.isSelectionEmpty();
                mxCell selectedCell = (mxCell) graph.getSelectionCell();

                if (selected) {
                    DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
                    
                    if (selectedCell.isEdge()) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new EdgePropertiesPanel(selectedCell,
                                                   display.cellToTransition(selectedCell), 
                                                   display));
                    } else if (selectedCell.isVertex()) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new StatePropertiesPanel(selectedCell,
                                JgraphXInternalFrame.this.cellToState(selectedCell), 
                                display));
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                            new AutomataPropertiesPanel(
                                new DisplayUtil(graph, automata, cellTable)));
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
        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
        
        Object parent = this.graph.getDefaultParent();
        Iterator<mxCell> cellIterator = this.initialFinalCells.iterator();
        while (cellIterator.hasNext()) {
            mxCell vertex = cellIterator.next();
            State state = this.cellToState(vertex);

            Object initialWeight = state.getInitialWeight();
            if (initialWeight != null) {
                display.showInitialFinal(parent, initialWeight, vertex, false);
            }

            Object finalWeight = state.getFinalWeight();
            if (finalWeight != null) {
                display.showInitialFinal(parent, finalWeight, vertex, true);
            }
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
                    display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), false);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);
                } else if (edge.getTerminal(false) == null) {
                    display.showInitialFinal(graph.getDefaultParent(), edge.getValue(), graph.getSelectionCell(), true);
                    Object removeEdge[] = {edge};
                    graph.removeCells(removeEdge);
                }
            }
        }
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

        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
        infoSplitPane.setTopComponent(
                new StatePropertiesPanel((mxCell) vertex, newState, display));

        System.out.println("add state at" + x + "," + y);
        System.out.println("total states:" + automata.getAllStates().size());

        setModified(true);
        undoStack.push(STATUS_ADD);


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

//        DisplayUtil display = new DisplayUtil(graph, automata, cellTable);
//        infoSplitPane.setTopComponent(
//                new StatePropertiesPanel((mxCell) vertex, 
//                JgraphXInternalFrame.this.cellToState(vertex), display));
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
        Object e = graph.insertEdge(parent, null, expression, source, target, "shape=curve");
        ArrayList<mxPoint> points = new ArrayList<mxPoint>();
        ((mxCell) e).getGeometry().setPoints(points);
        ((mxCell) e).getGeometry().setY(DEFAULT_LABEL_DISTANCE);

        Transition newTrans = new Transition();
        cellTable.put((mxCell) e, newTrans);
        newTrans.setSourceState(cellToState(source));
        newTrans.setTargetState(cellToState(target));
        newTrans.setLabel(expression);
        automata.addTransition(newTrans);

        System.out.println("total trans:" + automata.getAllTransitions().size());

        setModified(true);
        undoStack.push(STATUS_ADD);
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

            if ((source != null) && (target != null)) {
                break;
            }
        }

        mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
                null, transition.getLabel(), source, target,
                "shape=curve"));
        cellTable.put(edge, transition);
		if ((transition.getGeometricData() == null)
				|| (transition.getGeometricData().controlPoints == null)
				|| (transition.getGeometricData().controlPoints.isEmpty())) {
			edge.getGeometry().setPoints(new ArrayList<mxPoint>());
		} else {
			List<mxPoint> points = new ArrayList<mxPoint>();
			Iterator<Point2D> iterateControlPoints = transition.getGeometricData().controlPoints.iterator();
			while (iterateControlPoints.hasNext()) {
				Point2D controlPoint = iterateControlPoints.next();
				points.add(new mxPoint(controlPoint));
			}  // End while (iterateControlPoints.hasNext())
			edge.getGeometry().setPoints(points);
		}
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
        undoStack.push(STATUS_CHANGE);
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
			state.setGeometricData(geometricData);
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
					state.setInitialWeight(edge.getValue());
					continue;
				}
			} else {
				if (targetVertex == null) {
					/*
					 * This edge is an arrow pointing from a final state.
					 */
					State state = cellToStateMap.get(sourceVertex);
					state.setFinalWeight(edge.getValue());
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
			List<mxPoint> points = edge.getGeometry().getPoints();
			if ((points != null) && (!points.isEmpty())) {
				List<Point2D> controlPoints = transition.getGeometricData().controlPoints;
				Iterator<mxPoint> iteratePoints = points.iterator();
				while (iteratePoints.hasNext()) {
					mxPoint point = iteratePoints.next();
					controlPoints.add(new Point2D.Double(point.getX(), point.getY()));
				}  // End while (iteratePoints.hasNext())
			}  // End if ((points != null) && (!points.isEmpty()))
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
                                    Object cell = getCellAt(e.getX(), e.getY(), false);

                                    if (cell != null && getGraph().isCellEditable(cell)) {
                                        if (((mxCell) cell).isVertex()) {
                                            startEditingAtCell(cell, e);
                                        } else {
                                            ExpressionEditor editor =
                                                    new ExpressionEditor(
                                                    new JFrame(), true, 
                                                    (WeightedRegularExpression) ((mxCell) cell).getValue());
                                            editor.setVisible(true);
                                            ((mxCell) cell).setValue(editor.getExpression());
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
                
                List<Transition> relatedTrans=state.getTransitions();
                for(Transition trans : relatedTrans){
                    List<Transition> transList=automata.getAllTransitions();
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
                if(trans==null) System.out.println("can't find trans");
                List<Transition> transList = automata.getAllTransitions();
                transList.remove(trans);
                automata.setAllTransitions(transList);
                System.out.println("trans list size: "+automata.getAllTransitions().size());
                
                
            }


            cellTable.remove(cell);
            mxCell[] cells = {cell};
            graph.removeCells(cells);

            System.out.println("cell table size: "+cellTable.size());


        } else {
            System.out.println("Cell is empty");
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

            System.out.println("stack pop:" + stat);

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
                            cellTable.put(cell,trans);
                        }
                        break;

                    case STATUS_CHANGE:

                        if (((mxCell) cell).isVertex()) {
                            State state = cellToState(cell);
                            State.GeometricData geo = new State.GeometricData();
                            geo.location = new Point2D.Double(cell.getGeometry().getX(), cell.getGeometry().getY());
                            state.setGeometricData(geo);
                            state.setName((String)cell.getValue());

                        } else {

                            Transition trans = cellToTransition(cell);
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }

                            trans.getGeometricData().controlPoints = apt;
                            
                            trans.setLabel((WeightedRegularExpression)cell.getValue());
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

            System.out.println("stack pop:" + stat);

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
                            cellTable.put(cell,trans);
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

                            state.setName((String)cell.getValue());
                            
                        } else {

                            Transition trans = cellToTransition(cell);
                            List<mxPoint> jpt = cell.getGeometry().getPoints();
                            ArrayList<Point2D> apt = new ArrayList<Point2D>();
                            for (mxPoint pt : jpt) {
                                apt.add(new Point2D.Double(pt.getX(), pt.getY()));
                            }

                            trans.getGeometricData().controlPoints = apt;
                            trans.setLabel((WeightedRegularExpression)cell.getValue());
                        }
                        //undoStack.push(STATUS_CHANGE);
                        break;
                    default:
                        break;

                }


            }
        }


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
