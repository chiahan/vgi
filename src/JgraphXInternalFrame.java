
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.*;
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
    public JgraphXInternalFrame(JSplitPane infoSplitPane,
                               mxGraphComponent component, Automata automata) {
        super(automata.getName(),
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        initComponents();
        
        graphComponent = component;
        graph = graphComponent.getGraph();
        graph.setDisconnectOnMove(false);
        graphComponent.setConnectable(false);
        graphOutline = new mxGraphOutline(getGraphComponent());
        graphComponent.getViewport().setBackground(Color.WHITE);
        this.automata = automata;
        this.infoSplitPane = infoSplitPane;
        if ((topPanel != null) && (bottomPanel != null)) {
            this.topPanel = (JPanel)infoSplitPane.getTopComponent();
            this.bottomPanel = (JPanel)infoSplitPane.getBottomComponent();
        }
        
        this.setBounds(xOffset*openFrameCount, yOffset*openFrameCount, 800, 600);
        openFrameCount++;
        this.getContentPane().add(graphComponent);

        //cellTable = new Hashtable<Integer, mxCell>();
        cellTable = new Hashtable<mxCell,State>();

        installRepaintListener();
        installListeners();
        installInternalFrameListeners();

		if (this.automata == null) {
			return;
		}

		Iterator<State> stateIterator = this.automata.getAllStates().iterator();
		while (stateIterator.hasNext()) {
			State state = stateIterator.next();
			this.addState(state);
		}  // End while (stateIterator.hasNext())

		Iterator<Transition> transitionIterator = this.automata.getAllTransitions().iterator();
		while (transitionIterator.hasNext()) {
			Transition transition = transitionIterator.next();
			this.addTransition(transition);
		}  // End while (transitionIterator.hasNext())
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
                                new edge_properties(graph, selectedCell));
                    }
                    
                    vertexSelected = selectedCell.isVertex();
                    if (vertexSelected) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new state_properties(graph, selectedCell));

                        addTransitionFromMenuItem.setVisible(
                                (transitionFrom == null) ? true : false);
                        addTransitionToMenuItem.setVisible(
                                (transitionFrom == null) ? false : true);
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                        new Automata_properties());
                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                }
                addStateMenuItem.setVisible(!selected);
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected);
                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);

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

                if (e.isPopupTrigger()) {
                    popMouseX = e.getX();
                    popMouseY = e.getY();
                    getGraphPopupMenu().show(getGraphComponent(), popMouseX, popMouseY);
                }
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

        });

        // Installs a mouse motion listener to display the mouse location
        getGraphComponent().getGraphControl().addMouseMotionListener(
                new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        mouseLocationChanged(e);
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
                                new edge_properties(graph, selectedCell));
                    } else if (selectedCell.isVertex()) {
                        JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                                new state_properties(graph, selectedCell));
                    }
                } else {
                    JgraphXInternalFrame.this.infoSplitPane.setTopComponent(
                        new Automata_properties());
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
    
    public void addState(int x, int y) {
        Object parent = graph.getDefaultParent();
        int id = cellTable.size();
        Object newVertex = graph.insertVertex(parent, Integer.toString(id), "",
                x - 25, y - 25, 50, 50, "shape=ellipse;perimeter=ellipsePerimeter;");
        //cellTable.put((Integer) id, (mxCell)newVertex);
        graph.setSelectionCell(newVertex);
        infoSplitPane.setTopComponent(new state_properties(graph, (mxCell)newVertex));
        
        State newState=new State();
        State.GeometricData geo=new State.GeometricData();
        geo.location=new Point2D.Double(x, y);;
        //geo.shape="elipse";
        newState.setGeometricData(geo);
        
        automata.addState(newState);
        cellTable.put((mxCell)newVertex,newState);
        
//        automata.addState(new State((mxCell) newVertex));

        System.out.println("add state at" + x + "," + y);
        System.out.println("total states:"+automata.getAllStates().size());
    
    }

	public void addState(State state) {
		Object parent = this.graph.getDefaultParent();
		Integer id = new Integer(cellTable.size());
		Point2D point2d = state.getGeometricData().location;
		mxCell vertex = (mxCell) (this.graph.insertVertex(parent, Integer.toString(id), "", point2d.getX(), point2d.getY(), 50, 50, "shape=ellipse;perimeter=ellipsePerimeter;"));
		
                cellTable.put(vertex, state);
		
                
                graph.setSelectionCell(vertex);
		infoSplitPane.setTopComponent(new state_properties(graph, (mxCell)vertex));
		Object initialWeight = state.getInitialWeight();
		if (initialWeight != null) {
			double x = vertex.getGeometry().getCenterX();
			double y = vertex.getGeometry().getY() - 30;
			mxCell hiddenVertex = (mxCell) (this.graph.insertVertex(parent, null, "", x, y, 0, 0));
			this.graph.insertEdge(parent, null, initialWeight, hiddenVertex, vertex);
		}
		Object finalWeight = state.getInitialWeight();
		if (finalWeight != null) {
			double x = vertex.getGeometry().getCenterX();
			double y = vertex.getGeometry().getY() + vertex.getGeometry().getHeight() + 30;
			mxCell hiddenVertex = (mxCell) (this.graph.insertVertex(parent, null, "", x, y, 0, 0));
			this.graph.insertEdge(parent, null, finalWeight, vertex, hiddenVertex);
		}
		System.out.println("add state at (" + point2d.getX() + "," + point2d.getY() + ").");
	}  // End public void addState(State state)

    public void addTransition(mxCell source, mxCell target) {
        Object parent = graph.getDefaultParent();
        Object e = graph.insertEdge(parent, null, "", source, target, null);
        ArrayList<mxPoint> points = new ArrayList<mxPoint>();
        ((mxCell) e).getGeometry().setPoints(points);
//        automata.addTransition(
//                new Transition((mxCell)e, 
//                automata.getState(source), 
//                automata.getState(target)));

        Transition newTrans=new Transition();
        Transition.GeometricData geo=new Transition.GeometricData();
        newTrans.setGeometricData(geo);
    
        newTrans.setSourceState(cellTable.get(source));
        newTrans.setTargetState(cellTable.get(target));
        
        automata.addTransition(newTrans);
    
        System.out.println("total trans:"+automata.getAllTransitions().size());
    
    }

	public void addTransition(Transition transition) {
		List<State> states = this.automata.getAllStates();
		//int index = states.indexOf(transition.getSourceState());
		//mxCell source = this.cellTable.get(index);
		//index = states.indexOf(transition.getTargetState());
		//mxCell target = this.cellTable.get(index);
                
                mxCell source = null,target=null;
                Enumeration keys=cellTable.keys();
                while(keys.hasMoreElements()){
                    mxCell keyCell=(mxCell)keys.nextElement();
                    if(cellTable.get(keyCell)==transition.getSourceState()){
                        source=keyCell;
                        break;
                    }
                    
                }
                keys=cellTable.keys();
                while(keys.hasMoreElements()){
                    mxCell keyCell=(mxCell)keys.nextElement();
                    if(cellTable.get(keyCell)==transition.getTargetState()){
                        target=keyCell;
                        break;
                    }
                }
                
		mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(), null, transition.getLabel(), source, target, "shape=curve"));
		edge.getGeometry().setPoints(new ArrayList<mxPoint>());
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

    private static final long serialVersionUID = -6561623072112577140L;
    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;
    private mxGraphComponent graphComponent;
    private mxGraphOutline graphOutline;
    final mxGraph graph;
    protected boolean modified = false;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    //protected Hashtable<Integer, mxCell> cellTable;
    protected Hashtable<mxCell,State> cellTable;
    protected mxCell transitionFrom, transitionTo;
    protected int popMouseX, popMouseY;
    protected JSplitPane infoSplitPane;
    private static JPanel topPanel = null, bottomPanel = null;
    protected Automata automata;
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

        addStateMenuItem.setText("Add State");
        addStateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStateMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addStateMenuItem);

        addTransitionFromMenuItem.setText("Add Transition(From)");
        addTransitionFromMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransitionFromMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addTransitionFromMenuItem);

        addTransitionToMenuItem.setText("Add Transition(To)");
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
    // End of variables declaration//GEN-END:variables
}
