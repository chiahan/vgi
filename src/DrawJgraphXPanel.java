
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
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DrawJgraphXPanel.java
 *
 * Created on 2011/12/25, 上午 11:07:25
 */
/**
 *
 * @author reng, wkren
 */
public class DrawJgraphXPanel extends javax.swing.JPanel {

    /** Creates new form DrawJgraphXPanel */
    public DrawJgraphXPanel(mxGraphComponent component, Automata automata) {
        initComponents();

        graphComponent = component;
        graph = graphComponent.getGraph();
        graph.setDisconnectOnMove(false);
        graphComponent.setConnectable(false);
        graphOutline = new mxGraphOutline(graphComponent);
        graphComponent.getViewport().setBackground(Color.WHITE);
        this.automata = automata;

        cellTable = new Hashtable<Integer, mxCell>();

        infoTabbedPane.remove(graphOutlinePanel);
        infoTabbedPane.add(graphOutline, 0);
        infoTabbedPane.setTitleAt(0, "graph outline");
        infoTabbedPane.setSelectedIndex(0);
        innerSplitPane.setTopComponent(new Automata_properties());
        mainSplitPane.setRightComponent(graphComponent);

        installRepaintListener();
        installListeners();
    }
    private static final long serialVersionUID = -6561623072112577140L;
    protected mxGraphComponent graphComponent;
    protected mxGraphOutline graphOutline;
    final mxGraph graph;
    protected boolean modified = false;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    protected Hashtable<Integer, mxCell> cellTable;
    protected mxCell transitionFrom, transitionTo;
    protected int popMouseX, popMouseY;
    protected Automata automata;

    protected void installRepaintListener() {
        graphComponent.getGraph().addListener(mxEvent.REPAINT,
                new mxIEventListener() {

                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (graphComponent.getTripleBuffer() != null) ? ""
                                : " (unbuffered)";
                        mxRectangle dirty = (mxRectangle) evt.getProperty("region");

                        if (dirty == null) {
                            status("Repaint all" + buffer);
                        } else {
                            status("Repaint: x=" + (int) (dirty.getX()) + " y="
                                    + (int) (dirty.getY()) + " \nw="
                                    + (int) (dirty.getWidth()) + " h="
                                    + (int) (dirty.getHeight()) + buffer);
                        }
                    }
                });
    }

    public void status(String msg) {
//        statusBar.setText(msg);
    }

    protected void installListeners() {
        // Installs mouse wheel listener for zooming
        MouseWheelListener wheelTracker = new MouseWheelListener() {

            /**
             * 
             */
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    DrawJgraphXPanel.this.formMouseWheelMoved(e);
                }
            }
        };

        // Handles mouse wheel events in the outline and graph component
        graphOutline.addMouseWheelListener(wheelTracker);
        graphComponent.addMouseWheelListener(wheelTracker);


        // Installs the popup menu in the graph component
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            /**
             * 
             */
            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                boolean selected = !graph.isSelectionEmpty();

                mxCell selectedCell = (mxCell) graph.getSelectionCell();

                boolean edgeSelected = false;
                boolean vertexSelected = false;

                if (selected) {
                    edgeSelected = selectedCell.isEdge();
                    vertexSelected = selectedCell.isVertex();
                }
                addStateMenuItem.setVisible(!selected);
                deleteMenuItem.setVisible(selected);
                addControlPointMenuItem.setVisible(edgeSelected);
                cancelMenuItem.setVisible((transitionFrom == null) ? false : true);

                if (vertexSelected) {
                    DrawJgraphXPanel.this.innerSplitPane.setTopComponent(
                            new state_properties(graph, selectedCell));

                    addTransitionFromMenuItem.setVisible(
                            (transitionFrom == null) ? true : false);
                    addTransitionToMenuItem.setVisible(
                            (transitionFrom == null) ? false : true);
                } else {
                    if (edgeSelected) {
                        DrawJgraphXPanel.this.innerSplitPane.setTopComponent(
                                new edge_properties(graph, selectedCell));
                    } else {
                        DrawJgraphXPanel.this.innerSplitPane.setTopComponent(
                                new Automata_properties());
                    }

                    addTransitionFromMenuItem.setVisible(false);
                    addTransitionToMenuItem.setVisible(false);
                }

                if (e.isPopupTrigger()) {
                    popMouseX = e.getX();
                    popMouseY = e.getY();
                    graphPopupMenu.show(graphComponent, popMouseX, popMouseY);
                }
                DrawJgraphXPanel.this.validate();
            }

            /**
             * 
             */
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popMouseX = e.getX();
                    popMouseY = e.getY();
                    graphPopupMenu.show(graphComponent, popMouseX, popMouseY);
                }

                graph.refresh();
                graph.repaint();
            }
        });

        // Installs a mouse motion listener to display the mouse location
        graphComponent.getGraphControl().addMouseMotionListener(
                new MouseMotionListener() {

                    /*
                     * (non-Javadoc)
                     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
                     */
                    public void mouseDragged(MouseEvent e) {
                        mouseLocationChanged(e);
                    }

                    /*
                     * (non-Javadoc)
                     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
                     */
                    public void mouseMoved(MouseEvent e) {
                        mouseDragged(e);
                    }
                });
    }

    public void addTransition(mxCell source, mxCell target) {
        Object parent = graph.getDefaultParent();
        Object e = graph.insertEdge(parent, null, "", source, target, "shape=curve");
        ArrayList<mxPoint> points = new ArrayList<mxPoint>();
        ((mxCell) e).getGeometry().setPoints(points);
//        automata.addTransition(
//                new Transition((mxCell)e, 
//                automata.getState(source), 
//                automata.getState(target)));
    }

    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", \n" + e.getY());
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void addState(int x, int y) {
        Object parent = graph.getDefaultParent();
        int id = cellTable.size();
        Object newVertex = graph.insertVertex(parent, Integer.toString(id), "",
                x - 25, y - 25, 50, 50, "shape=ellipse;perimeter=ellipsePerimeter;");
        cellTable.put((Integer) id, (mxCell) newVertex);
//        automata.addState(new State((mxCell) newVertex));

        System.out.println("add state at" + x + "," + y);
    }

    public void addControlPoint() {
        addControlPoint((mxCell) graphComponent.getCellAt(popMouseX, popMouseY),
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
        graphComponent.refresh();
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

    public void setShowPropert(boolean flag) {
        this.innerSplitPane.getTopComponent().setVisible(flag);
        this.innerSplitPane.resetToPreferredSizes();
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
        mainSplitPane = new javax.swing.JSplitPane();
        innerSplitPane = new javax.swing.JSplitPane();
        libPanel = new javax.swing.JPanel();
        infoTabbedPane = new javax.swing.JTabbedPane();
        graphOutlinePanel = new javax.swing.JPanel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();

        addStateMenuItem.setText("Add State");
        addStateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStateMenuItemActionPerformed(evt);
            }
        });
        graphPopupMenu.add(addStateMenuItem);

        addTransitionFromMenuItem.setText("Add Transition(from)");
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
        setLayout(new java.awt.BorderLayout());

        mainSplitPane.setDividerSize(20);

        innerSplitPane.setDividerLocation(300);
        innerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        innerSplitPane.setResizeWeight(0.5);

        libPanel.setPreferredSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout libPanelLayout = new javax.swing.GroupLayout(libPanel);
        libPanel.setLayout(libPanelLayout);
        libPanelLayout.setHorizontalGroup(
            libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        libPanelLayout.setVerticalGroup(
            libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );

        innerSplitPane.setTopComponent(libPanel);

        javax.swing.GroupLayout graphOutlinePanelLayout = new javax.swing.GroupLayout(graphOutlinePanel);
        graphOutlinePanel.setLayout(graphOutlinePanelLayout);
        graphOutlinePanelLayout.setHorizontalGroup(
            graphOutlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );
        graphOutlinePanelLayout.setVerticalGroup(
            graphOutlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );

        infoTabbedPane.addTab("grahp outline", graphOutlinePanel);

        outputTextArea.setColumns(20);
        outputTextArea.setRows(5);
        outputScrollPane.setViewportView(outputTextArea);

        infoTabbedPane.addTab("output info", outputScrollPane);

        innerSplitPane.setBottomComponent(infoTabbedPane);

        mainSplitPane.setLeftComponent(innerSplitPane);

        add(mainSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        // TODO add your handling code here:
        if (evt.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }

        status(mxResources.get("scale") + ": "
                + (int) (100 * graphComponent.getGraph().getView().getScale())
                + "%");
    }//GEN-LAST:event_formMouseWheelMoved

    private void addStateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStateMenuItemActionPerformed
        // TODO add your handling code here:
        addState(popMouseX, popMouseY);
    }//GEN-LAST:event_addStateMenuItemActionPerformed

    private void addTransitionFromMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionFromMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = graphComponent.getCellAt(popMouseX, popMouseY);
        if (cell != null) {
            if (((mxCell) cell).isVertex()) {
                transitionFrom = (mxCell) cell;
            }
        }
    }//GEN-LAST:event_addTransitionFromMenuItemActionPerformed

    private void addTransitionToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTransitionToMenuItemActionPerformed
        // TODO add your handling code here:
        Object cell = graphComponent.getCellAt(popMouseX, popMouseY);
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
    private javax.swing.JPanel graphOutlinePanel;
    private javax.swing.JPopupMenu graphPopupMenu;
    private javax.swing.JTabbedPane infoTabbedPane;
    private javax.swing.JSplitPane innerSplitPane;
    private javax.swing.JPanel libPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JTextArea outputTextArea;
    // End of variables declaration//GEN-END:variables
}
