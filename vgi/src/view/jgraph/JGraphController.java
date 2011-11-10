
package view.jgraph;
import layout_algorithm.ModifyStateLoop;
import event.UIEvent;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.plaf.basic.BasicGraphUI;
import view.main.CanvasView;
import event.UserEventConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.ProgressMonitor;
import layout_algorithm.CircleLayout;
import layout_algorithm.HierarchicalLayout2;
import model.Automata2.ValueType;
import model.TypeConstants;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.ParentMap;
import org.jgraph.graph.VertexView;
import view.cellview.MyCellViewFactory;
//import view.router.JGraphParallelFlexibleRouter;
import view.router.LoopRouter;
import view_algorithm.FisheyeAlgorithm;


/**
 * 1. This is the main class to controll JGraph library(www.jgraph.com)
 * 2. The demo for how to use this class is at view.demo
 */
public class JGraphController {

    //為了動態顯示新 Transition 的動畫
    public static int CURSOL_STATE_ID = -99;
    public static int TEMP_NEW_TRANSITION_ID = -99;
    public static ArrayList tempNewTransitionControlPoint = new ArrayList();
    public boolean functionTempNewTransition = false;
    public int functionTempNewTransitionSourceID;
    public int tempNewTransitionType = GraphConstants.STYLE_ORTHOGONAL;
    //為了動態顯示新 Transition 的動畫 END


    public JGraph graph;
    GraphModel model;
    public HashMap<Integer, DefaultGraphCell> stateMap = new HashMap();
    public HashMap<Integer, DefaultEdge> transitionMap = new HashMap();
    public ValueType valueType = null;    // id
    public double graphScale = 1;
    CanvasView View;

    
    public static float stateSize = 25;
    public double posSum = 0;
    private boolean fisheyeMode = false;    //Layout相關的
    public JGraphLayoutMorphingManager morpher = new JGraphLayoutMorphingManager();
    private int LOOP_PORT = 1;
    private int INITIAL_PORT = 2;
    private int FINAL_PORT = 3;
    public  boolean layerlayout =false;


    /*
     * @param graphPara : the controllered JGraph object
     *         viewPara : a view object to communicate with the upper level
     */
    public JGraphController(JGraph graphPara, CanvasView ViewPara) {

        View = ViewPara;
        model = new DefaultGraphModel();
        GraphLayoutCache jgraphview = new GraphLayoutCache(model, new DefaultCellViewFactory());
        graph = new JGraph(model, jgraphview);
        graph.getGraphLayoutCache().setFactory(new MyCellViewFactory()); //改變Graph的View，變成圓的

        //設定一些JGraph的屬性
        graph.setAntiAliased(true);
        graph.setUI(new UserInterface(this));
        graph.setSizeable(false);
        graph.setConnectable(false);
        graph.setDisconnectable(false);
    }

    /**
     * Set the graph scale
     * @param scale the graph's scale
     */
    public void setScale(double scale) {
        graph.setScale(scale, new Point2D.Float(0, 0));
        graphScale = scale;
    }

    /**
     * Get the graph scale
     * @return graph scale
     */
    public double getScale() {
        return graph.getScale();
    }

    /**
     * set the valueType which is an automata setting
     * @param vt valueType
     */
    public void setValueType(ValueType vt) {
        valueType = vt;
    }

    /**
     * Get the selected cells' ids
     * @return id list
     */
    public Vector getSelectedCellsID() {
        Vector states = new Vector();
        Vector transitions = new Vector();
        Vector result = new Vector();

        Object[] cells = graph.getSelectionCells();
        int cellsLength = cells.length;
        for (int i = 0; i < cellsLength; i++) {
            if (cells[i] instanceof DefaultEdge) {
                transitions.add(getTransistionID((DefaultEdge) cells[i]));
            } else if (cells[i] instanceof DefaultGraphCell) {
                DefaultGraphCell cell = (DefaultGraphCell) cells[i];
                int id = getStateID(cell);
                states.add(id);
            }
        }

        int statesNumber = 0;
        int transitionSize = 0;
        statesNumber = states.size();
        transitionSize = transitions.size();

        if (statesNumber == 0 && transitionSize == 0) {
            result.add(-1);
        } else if (statesNumber != 0 && transitionSize == 0) {
            result.add(-2);
            result.addAll(states);
        } else if (statesNumber == 0 && transitionSize != 0) {
            result.add(-3);
            result.addAll(transitions);
        } else if (statesNumber != 0 && transitionSize != 0) {
            result.add(-4);
            result.addAll(states);
            result.add(-4);
            result.addAll(transitions);
        }

        return result;
    }

    /**
     * Update jgraph in order to insure
     * @param graphInput the jGraph to be updated
     */
    public void updateGraph(JGraph graphInput) {
        graph = graphInput;
    }

    /**
     * Dynamic transition effect
     * @param sourceID the source state
     */
    public void setNewTransitionEffect(int sourceID) {
        if (sourceID > -1) {
            this.functionTempNewTransition = true;
            this.functionTempNewTransitionSourceID = sourceID;
        } else {
            this.functionTempNewTransition = false;
            this.functionTempNewTransitionSourceID = sourceID;
            //Remove New Transition Effect
            if (this.transitionMap.get(TEMP_NEW_TRANSITION_ID) != null) {
                this.removeTransition(TEMP_NEW_TRANSITION_ID);
            }
        }
    }
    /**
     * Set new transition line type
     * @param lineType Transition line type which can be looked up from JGraph manual
     */
    public void setNewTransitionLineType(int lineType) {
        tempNewTransitionType = lineType;
    }

    /**
     * convert Graph line type to jgraph style
     * @param type graph type
     * @return linetype jgraph type
     */
    public int toJGraphLineType(int type) {
        switch (type) {
            case TypeConstants.SPLINE_LINE:
                type = GraphConstants.STYLE_SPLINE;
                break;
            case TypeConstants.BEZIER_LINE:
                type = GraphConstants.STYLE_BEZIER;
                break;
            case TypeConstants.CURVE:
                type = GraphConstants.STYLE_BEZIER;
                break;
            case TypeConstants.POLY_LINE:
                type = GraphConstants.STYLE_ORTHOGONAL;
                break;
            case TypeConstants.STRAIGHT_LINE:
                type = GraphConstants.STYLE_ORTHOGONAL;
                break;
        }
        return type;
    }

    /**
     *  Our costome UI class extends basicGraphUI , please check JGRAPH for more detial
     */
    public class UserInterface extends BasicGraphUI {
        JGraphController JGC;

        public UserInterface(JGraphController JGCPara) {
            super();
            JGC = JGCPara;
        }

        /**
         * StartEditing 
         * @param cell the cell to be edited
         * @param event 
         * @return boolean whether ok ?
         */
        @Override
        protected boolean startEditing(Object cell, MouseEvent event) {

            UIEvent uievent = new UIEvent(UserEventConstants.CHANGE_TRANSITION_LABEL);
            uievent.setTransitionID(getTransistionID((DefaultGraphCell) cell));
            View.eventHappen(uievent);
            return true;
        }

        @Override
        public CellEditorListener createCellEditorListener() {
            return new MyCellEditorHandler();
        }

        /**
         * Editor Handler 
         */
        public class MyCellEditorHandler extends CellEditorHandler {

            @Override
            public void editingCanceled(ChangeEvent arg0) {
                super.editingCanceled(arg0);
            }

            @Override
            public void editingStopped(ChangeEvent arg0) {
                DefaultGraphCell cell = (DefaultGraphCell) graph.getEditingCell();
                int CellID = getCellID(cell);

                boolean isEdge;
                if (cell instanceof DefaultEdge) {
                    isEdge = true;
                } else {
                    isEdge = false;
                }
                String oldstring;
                if (isEdge) {
                    oldstring = transitionMap.get(CellID).toString();
                } else {
                    oldstring = stateMap.get(CellID).toString();
                }
                super.editingStopped(arg0);
                if (isEdge) {

                    // Get Transition type
                    int Transition_Type;
                    String newstring = transitionMap.get(CellID).toString();
                    DefaultEdge edge = (DefaultEdge) transitionMap.get(CellID);
                    
                } else {
                    setStateName(CellID, stateMap.get(CellID).toString(), true);
                }
            }
        }

        @Override
        protected MouseListener createMouseListener() {
            return new MyMouseListener();
        }
        public class MyMouseListener extends MouseHandler {

            public void setEvent(MouseEvent e) {
                Point2D.Float Pos = new Point2D.Float(e.getX(), e.getY());
                int stateID = -1, transitionID = -1;
                Object targetCell = (DefaultGraphCell) graph.getSelectionCellAt(e.getPoint());
                if (targetCell != null) {
                    if (targetCell instanceof DefaultEdge) {
                        transitionID = getTransistionID((DefaultEdge) targetCell);
                    } else if (targetCell instanceof DefaultGraphCell) {
                        stateID = getStateID((DefaultGraphCell) targetCell);
                    }
                }

                View.mouseClicked(stateID, transitionID, Pos);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                //Remove Cursor State
                super.mouseClicked(e);

                setEvent(e);
                updateLoopInitalFinal();

                if (View != null) {
                    if (fisheyeMode) {
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:
                                int height = graph.getHeight();
                                int width = graph.getWidth();
                                FisheyeAlgorithm.setHeight(height);
                                FisheyeAlgorithm.setWidth(width);

                                double offset = stateSize / 2;
                                Point2D focus = e.getPoint();

                                // recover state
                                JGC.recoverState();

                                for (int i = 0; stateMap.containsKey(i); i++) {
                                    DefaultGraphCell state = (DefaultGraphCell) stateMap.get(i);
                                    // point
                                    double stateX = GraphConstants.getBounds((state).getAttributes()).getX() + offset;
                                    double stateY = GraphConstants.getBounds((state).getAttributes()).getY() + offset;
                                    Point2D statePoint = new Point2D.Double(stateX, stateY);
                                    Point2D newPoint = FisheyeAlgorithm.fisheyePoint(focus, statePoint);
                                    // size
                                    double sizeX, sizeY;
                                    offset = FisheyeAlgorithm.getVertexSizeScaleFactor() * offset;
                                    if (focus.getX() < stateX) {
                                        sizeX = stateX + offset;
                                    } else {
                                        sizeX = stateX - offset;
                                    }
                                    if (focus.getY() < stateY) {
                                        sizeY = stateY + offset;
                                    } else {
                                        sizeY = stateY - offset;
                                    }

                                    Point2D sizePoint = new Point2D.Double(sizeX, sizeY);
                                    Point2D newSizePoint = FisheyeAlgorithm.fisheyePoint(focus, sizePoint);
                                    double newSize = FisheyeAlgorithm.getSgeom(newPoint, newSizePoint);
                                    JGC.setStatePosition(i, newPoint, newSize);
                                }
                                System.out.println("Left Botton clicked");
                                break;
                            case MouseEvent.BUTTON3:
                                // recover state
                                JGC.recoverState();
                                System.out.println("Right botton3 clicked");
                                break;
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                //System.out.println(model.getRootCount());
                super.mouseMoved(e);
                //bad fix me
                //functionTempNewTransition = false;
                if (functionTempNewTransition) {
                    //Add New Transition Effect
                    // Reset, Remove New Transition Effect
                    if (JGC.transitionMap.get(TEMP_NEW_TRANSITION_ID) != null) {
                        JGC.removeTransition(TEMP_NEW_TRANSITION_ID);
                    }

                    if (JGC.stateMap.get(CURSOL_STATE_ID) != null) {
                        JGC.removeState(CURSOL_STATE_ID);
                    }
                    DefaultGraphCell Source = (DefaultGraphCell) stateMap.get(functionTempNewTransitionSourceID);

                    //if ((Source != null) && !(Source instanceof DefaultEdge)) {
                    if (Source == graph.getFirstCellForLocation(e.getX(), e.getY())) {
                        // Loop
                        JGC.showNewTransition(e.getX(), e.getY(), tempNewTransitionType, true, e);
                    } else {
                        // Not loop
                        JGC.showNewTransition(e.getX(), e.getY(), tempNewTransitionType, false, e);
                    }

                }
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                //Compute the position hash
                Vector SelectedCellsID = JGC.getSelectedCellsID();
                switch ((Integer) SelectedCellsID.remove(0)) {
                    case -2:  //States
                        int size = stateMap.size();
                        int stateCount = 0;
                        int guessedStateID = 0;
                        // Get All State's Postions
                        double newPosTotal = 0;
                        while(stateCount < size - 1) {  // Compute the position hash value
                            DefaultGraphCell state = (DefaultGraphCell) stateMap.get(guessedStateID);
                            if (state != null) {
                                VertexView cellView = (VertexView) graph.getGraphLayoutCache().getMapping(state, false);
                                Rectangle2D rec2D = cellView.getBounds();
                                newPosTotal += rec2D.getX() + rec2D.getY();
                                stateCount++;
                            }                            
                            guessedStateID++;                            
                        }

                        /* Marked by Rasiel because it caused add transition bug.
                        for (int i = 0; i < size; i++) {  // Compute the position hash value
                            DefaultGraphCell state = (DefaultGraphCell) stateMap.get(i);
                            if (state != null) {
                                VertexView cellView = (VertexView) graph.getGraphLayoutCache().getMapping(state, false);
                                Rectangle2D rec2D = cellView.getBounds();
                                newPosTotal += rec2D.getX() + rec2D.getY();
                            }
                        }
                        */

                        posSum = newPosTotal;
                        break;
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                super.mouseReleased(e);
                if (JGC.stateMap.get(CURSOL_STATE_ID) != null) {
                    JGC.removeState(CURSOL_STATE_ID);
                }
                if (JGC.transitionMap.get(TEMP_NEW_TRANSITION_ID) != null) {
                    JGC.removeTransition(TEMP_NEW_TRANSITION_ID);
                }

                // If the state's position change , then return the UIEvent
                Vector SelectedCellsID = JGC.getSelectedCellsID();
                switch ((Integer) SelectedCellsID.remove(0)) {
                    case -1: // null
                        break;
                    case -2:  //States
                        int size = stateMap.size();
                        int stateCount = 0;
                        int guessedStateID = 0;
                        // Get All State's Postions
                        double newPosTotal = 0;
                        while(stateCount < size) {  // Compute the position hash value
                            DefaultGraphCell state = (DefaultGraphCell) stateMap.get(guessedStateID);
                            if(state != null) {
                                VertexView cellView = (VertexView) graph.getGraphLayoutCache().getMapping(state, false);
                                Rectangle2D rec2D = cellView.getBounds();
                                newPosTotal += rec2D.getX() + rec2D.getY();
                                stateCount++;
                            }                            
                            guessedStateID++;                            
                        }

                        /* Marked by Rasiel because it caused add transition bug.
                        for (int i = 0; i < size; i++) {  // Compute the position hash value
                            DefaultGraphCell state = (DefaultGraphCell) stateMap.get(i);
                            VertexView cellView = (VertexView) graph.getGraphLayoutCache().getMapping(state, false);
                            if(cellView == null)
                                continue;
                            Rectangle2D rec2D = cellView.getBounds();
                            newPosTotal += rec2D.getX() + rec2D.getY();
                        }
                        */

                        if (newPosTotal != posSum) {
                            updateStatePositiontoDataModel();
                        }
                        posSum = newPosTotal;
                        break;
                    case -3:  //Transitions
                        break;
                    case -4:  //States + Transitions
                        break;
                }
            }
        }// end class
    }

    public Point.Float getStatePosition(int id) {

        DefaultGraphCell cell = (DefaultGraphCell) stateMap.get(id);

        Rectangle2D rec2D = GraphConstants.getBounds(cell.getAttributes());
        float x = 0, y = 0;
        if (rec2D != null) {
            x = new Float(rec2D.getX());
            y = new Float(rec2D.getY());
        }
        Point.Float point = new Point.Float(x, y);
        return point;
    }

    /**
     * Get the selected State ID (圈選將狀態下 )
     * @return
     */
    public int getSelectedStateID() {
        DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
        if (cell == null) {
            return -1;
        }

        int id = getStateID(cell);
        if (cell instanceof DefaultEdge) {
            id = -1;
        }

        return id;
    }

    /**
     * Get selected Transition ID 
     */
    public int getSelectedTransitionID() {
        DefaultEdge cell = (DefaultEdge) graph.getSelectionCell();
        if (cell == null) {
            return -1;
        }

        int id = getTransistionID(cell);
        if (!(cell instanceof DefaultEdge)) {
            id = -1;
        }

        return id;
    }

    /**
     * Get Cell ID
     * @param cell the cell (jgraph object)
     * @return an cell ID integer
     */
    public int getCellID(DefaultGraphCell cell) {
        if (cell instanceof DefaultEdge) {
            return getTransistionID((DefaultEdge) cell);
        } else {
            return getStateID(cell);
        }

    }
    /**
     * Get JGraph Cell
     * @param cell JGraph 
     * @return State ID
     */
    public int getStateID(DefaultGraphCell cell) {
        if (cell == null) {
            return -1;
        }

        String x = cell.getAttributes().get("id").toString();
        //String x = (String) reverseStateMap.get(cell);
        if (x == null) {
            return -1;
        }

        int i = Integer.valueOf(x);
        return i;
    }

    /**
     * Get Transition ID
     * @param cell jGraph Cell
     * @return integer transition ID
     */
    public int getTransistionID(DefaultGraphCell cell) {
        if (cell == null) {
            return -1;
        }
        int i = Integer.valueOf(cell.getAttributes().get("id").toString());
        return i;
    }

    /**
     *
     * @param stateID stateID to be changed
     * @param initialState is this state a initial state ?
     * @param finalState is this state a final state?
     */
    public void changeStateStyle(int stateID, boolean initialState, boolean finalState) {
        DefaultGraphCell group = (DefaultGraphCell) stateMap.get(stateID);
        float mysize = Float.parseFloat(group.getAttributes().get("size").toString());
        changeStateStyle(stateID, initialState, finalState, mysize);
    }

    /**
     *
     * @param stateID stateID to be changed
     * @param initialState is this state a initial state ?
     * @param finalState is this state a final state?
     * @param mysize state size
     */
    public void changeStateStyle(int stateID, boolean initialState, boolean finalState, float mysize) {
        changeStateStyle(stateID, initialState, finalState, mysize, false);
    }

    /**
     * 
     * @param stateID stateID to be changed
     * @param initialState is this state a initial state ?
     * @param finalState is this state a final state?
     * @param mysize state size
     * @param recovery reset this state to be normal
     */
    public void changeStateStyle(int stateID, boolean initialState, boolean finalState, float mysize, boolean recovery) {

        DefaultGraphCell group = (DefaultGraphCell) stateMap.get(stateID);
        Rectangle2D rec2D = GraphConstants.getBounds(group.getAttributes());
        double x = rec2D.getX();
        double y = rec2D.getY();

        DefaultGraphCell state = (DefaultGraphCell) group.getChildAt(0);

        boolean bfinal = Boolean.parseBoolean(group.getAttributes().get("final").toString());
        boolean binitial = Boolean.parseBoolean(group.getAttributes().get("initial").toString());

        ModifyStateLoop msl = new ModifyStateLoop(stateSize, transitionMap, stateMap);
        Point2D.Double intercept = msl.FindLoopPortAlg(stateID);
        int[] regionDensityArray = msl.regionDensityArray;

        int inital_offset = 4;
        int final_offset = 0;
        int[] inital_priority = {4, 2, 0, 6, 3, 1, 5, 7};
        int[] final_priority = {0, 6, 2, 4, 5, 7, 1, 3};

        double[] x_offset = {3 * stateSize, 3 * stateSize, 1.5 * stateSize, 0, 0, 0, 1.5 * stateSize, 3 * stateSize};
        double[] y_offset = {1.5 * stateSize, 0, 0, 0, 1.5 * stateSize, 3 * stateSize, 3 * stateSize, 3 * stateSize};


        for (int i = 0; i < 8; i++) {
            if (regionDensityArray[inital_priority[i]] == 0) {
                inital_offset = inital_priority[i];
                break;
            }
        }
        for (int i = 0; i < 8; i++) {
            if (regionDensityArray[final_priority[i]] == 0 && final_priority[i] != inital_offset) {
                final_offset = final_priority[i];
                break;
            }
        }


        if (recovery) {
            bfinal = false;
            bfinal = false;
        } else {
            group.getAttributes().put("initial", false);
            group.getAttributes().put("final", false);
        }

        while (group.getChildren().size() > 1) {
            DefaultGraphCell cell = (DefaultGraphCell) group.getChildren().get(1);
            DefaultGraphCell[] temp = {cell};
            Object[] temp2 = graph.getDescendants(temp);
            model.remove(temp2);
        }


        DefaultPort loop_port = (DefaultPort) state.getChildAt(LOOP_PORT);
        DefaultPort port = (DefaultPort) state.getChildAt(INITIAL_PORT);

        Map nested = new Hashtable();
        nested.put(port, port.getAttributes());
        nested.put(loop_port, loop_port.getAttributes());
        graph.getGraphLayoutCache().edit(nested, null, null, null);

        if (initialState & finalState) {

            group.getAttributes().put("initial", true);
            group.getAttributes().put("final", true);

            GraphConstants.setBounds(group.getAttributes(), new Rectangle2D.Double(x, y, mysize * 3, mysize));
            GraphConstants.setBounds(state.getAttributes(), new Rectangle2D.Double(x + mysize, y + mysize, mysize, mysize));

            DefaultGraphCell mini_state = new DefaultGraphCell();
            GraphConstants.setBounds(mini_state.getAttributes(), new Rectangle2D.Double(x + x_offset[inital_offset], y + y_offset[inital_offset], 0, 0));
            mini_state.addPort();

            DefaultEdge initial = new DefaultEdge();
            initial.setSource(mini_state.getChildAt(0));
            initial.setTarget(state.getChildAt(INITIAL_PORT));
            GraphConstants.setLineEnd(initial.getAttributes(), GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(initial.getAttributes(), true);
            graph.getGraphLayoutCache().insert(initial);


            group.getAttributes().put("final", true);
            GraphConstants.setBounds(group.getAttributes(), new Rectangle2D.Double(x, y, mysize * 3, mysize));

            DefaultGraphCell final_mini_state = new DefaultGraphCell();
            GraphConstants.setBounds(final_mini_state.getAttributes(), new Rectangle2D.Double(x + x_offset[final_offset], y + y_offset[final_offset] + 1, 0, 0));

            final_mini_state.addPort();

            DefaultEdge final_edge = new DefaultEdge();
            final_edge.setSource(state.getChildAt(FINAL_PORT));
            final_edge.setTarget(final_mini_state.getChildAt(0));
            GraphConstants.setLineEnd(final_edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(final_edge.getAttributes(), true);
            graph.getGraphLayoutCache().insert(final_edge);

            ParentMap pm = new ParentMap();
            pm.addEntry(state, group);
            pm.addEntry(mini_state, group);
            pm.addEntry(initial, group);
            pm.addEntry(final_mini_state, group);
            pm.addEntry(final_edge, group);
            graph.getGraphLayoutCache().edit(null, null, pm, null);

        } else if (initialState) {
            group.getAttributes().put("initial", true);
            GraphConstants.setBounds(group.getAttributes(), new Rectangle2D.Double(x, y, mysize * 2, mysize));
            GraphConstants.setBounds(state.getAttributes(), new Rectangle2D.Double(x + mysize, y + mysize, mysize, mysize));

            DefaultGraphCell mini_state = new DefaultGraphCell();
            GraphConstants.setBounds(mini_state.getAttributes(), new Rectangle2D.Double(x + x_offset[inital_offset], y + y_offset[inital_offset], 0, 0));
            mini_state.addPort();

            DefaultEdge initial = new DefaultEdge();
            initial.setSource(mini_state.getChildAt(0));
            initial.setTarget(state.getChildAt(INITIAL_PORT));
            GraphConstants.setLineEnd(initial.getAttributes(), GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(initial.getAttributes(), true);
            graph.getGraphLayoutCache().insert(initial);

            ParentMap pm = new ParentMap();
            pm.addEntry(state, group);
            pm.addEntry(mini_state, group);
            pm.addEntry(initial, group);
            graph.getGraphLayoutCache().edit(null, null, pm, null);

        } else if (finalState) {
            group.getAttributes().put("final", true);
            GraphConstants.setBounds(group.getAttributes(), new Rectangle2D.Double(x, y, mysize * 2, mysize));
            GraphConstants.setBounds(state.getAttributes(), new Rectangle2D.Double(x + mysize, y + mysize, mysize, mysize));

            DefaultGraphCell final_mini_state = new DefaultGraphCell();
            GraphConstants.setBounds(final_mini_state.getAttributes(), new Rectangle2D.Double(x + x_offset[final_offset], y + y_offset[final_offset] + 1, 0, 0));

            final_mini_state.addPort();

            DefaultEdge final_edge = new DefaultEdge();
            final_edge.setSource(state.getChildAt(FINAL_PORT));
            final_edge.setTarget(final_mini_state.getChildAt(0));
            GraphConstants.setLineEnd(final_edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(final_edge.getAttributes(), true);
            graph.getGraphLayoutCache().insert(final_edge);

            ParentMap pm = new ParentMap();
            pm.addEntry(state, group);
            pm.addEntry(final_mini_state, group);
            pm.addEntry(final_edge, group);
            graph.getGraphLayoutCache().edit(null, null, pm, null);
        }

        graph.repaint();
    }

    /**
     * Return JGraph object
     * @return graph 
     */
    public JGraph getGraph() {
        return graph;
    }

    /**
     * Remove Transition by ID
     * @param transitionID transition ID to be removed
     */
    public void removeTransition(int transitionID) {
        DefaultGraphCell[] cell = {(DefaultGraphCell) transitionMap.get(transitionID)};
//        graph.getGraphLayoutCache().removeCells(cell);
        model.remove(cell);
    }

    /**
     * Remove State
     * @param stateID the state ID to be remove
     */
    public void removeState(int stateID) {
        DefaultGraphCell[] cell = {(DefaultGraphCell) stateMap.get(stateID)};
        DefaultGraphCell c = (DefaultGraphCell) stateMap.get(stateID);
        if (c != null) {
            while (c.getChildren().size() > 0) {
                DefaultGraphCell temp = (DefaultGraphCell) c.getChildren().get(0);
                DefaultGraphCell[] temp2 = {temp};
                Object[] temp3 = graph.getDescendants(temp2);
                model.remove(temp3);
            }


            stateMap.remove(stateID);
            model.remove(cell);
        }

    }

    /**
     * Insert transtion
     * @param transitionID transition ID
     * @param label transition label
     * @param sourceStateID the source StateID
     * @param destinationStateID the destination StateID
     */
    public void insertTransition(int transitionID, String label, int sourceStateID, int destinationStateID) {
        createTransition(sourceStateID, destinationStateID, transitionID, label, null);
    }

    /**
     * Create Transition
     * @param sourceID state sourceID
     * @param targetID state targetID
     * @param transitionID transition ID
     * @param edgeLabel transtion Label
     * @param e mouse Event mouseEvent
     * @return ok or not ok
     */
    public boolean createTransition(int sourceID, int targetID, int transitionID, String edgeLabel, MouseEvent e) {
        DefaultEdge edge = new DefaultEdge(edgeLabel);
        edge.getAttributes().put("id", transitionID);
        DefaultGraphCell sourceCell = (DefaultGraphCell) stateMap.get(sourceID);
        DefaultGraphCell targetCell = (DefaultGraphCell) stateMap.get(targetID);

        if (sourceCell == null || targetCell == null) { //void null cell
            return false;
        }

        if (sourceCell == targetCell) {  //loop
            DefaultPort loop_port = (DefaultPort) sourceCell.getChildAt(0).getChildAt(LOOP_PORT);

            ModifyStateLoop msl = new ModifyStateLoop(stateSize, transitionMap, stateMap);
            Point2D.Double intercept = msl.FindLoopPortAlg(sourceID);
            edge.getAttributes().put("loop", true);
            edge.getAttributes().put("angle", msl.angle);

            GraphConstants.setOffset(loop_port.getAttributes(), intercept);
            Map nested = new Hashtable();
            nested.put(loop_port, loop_port.getAttributes());
            graph.getGraphLayoutCache().edit(nested, null, null, null);

            edge.setSource(sourceCell.getChildAt(0).getChildAt(LOOP_PORT));
            edge.setTarget(targetCell.getChildAt(0).getChildAt(LOOP_PORT));

        } else {
            edge.setSource(sourceCell.getChildAt(0).getChildAt(0));
            edge.setTarget(targetCell.getChildAt(0).getChildAt(0));
            edge.getAttributes().put("loop", false);

        }
        GraphConstants.setRouting(edge.getAttributes(), LoopRouter.getSharedInstance());

        boolean bfinal = Boolean.parseBoolean(sourceCell.getAttributes().get("final").toString());
        boolean binitial = Boolean.parseBoolean(sourceCell.getAttributes().get("initial").toString());


        bfinal = Boolean.parseBoolean(targetCell.getAttributes().get("final").toString());
        binitial = Boolean.parseBoolean(targetCell.getAttributes().get("initial").toString());


        // Set appearance
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
        GraphConstants.setEndFill(edge.getAttributes(), true);

        //GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_BEZIER);
        // GraphConstants.setRouting(edge.getAttributes(), LoopRouter.getSharedInstance());

        if (e != null) {  //Add the control points of tempTransition
            List points = new ArrayList();

            double SourceX = GraphConstants.getBounds((sourceCell).getAttributes()).getX();
            double SourceY = GraphConstants.getBounds((sourceCell).getAttributes()).getY();
            double offsetX, offsetY;
            double controlPointNum = 10;

            offsetX = (e.getX() - SourceX) / controlPointNum;
            offsetY = (e.getY() - SourceY) / controlPointNum;

            points.add(edge.getAttributes().createPoint());
            points.add(edge.getAttributes().createPoint(SourceX + offsetX * 5, SourceY + offsetY * 8));
            points.add(edge.getAttributes().createPoint());
            GraphConstants.setPoints(edge.getAttributes(), points);
        }

        GraphConstants.setLabelPosition(edge.getAttributes(), new Point2D.Double(GraphConstants.PERMILLE / 2, -7));
        GraphConstants.setSelectable(edge.getAttributes(), true);
        graph.getGraphLayoutCache().insert(edge);
        if (edge.getSource() == null || edge.getSource() == null) {
            int x = 1;
        }
        transitionMap.put(transitionID, edge);

        return true;
    }

    /**
     * Set Transtion Style
     * @param transitionID transition ID
     * @param style transition Style
     */
    public void setTransitionStyle(int transitionID, int style) {
        DefaultEdge transtion = (DefaultEdge) transitionMap.get(transitionID);
        int jGraphStyle = toJGraphLineType(style);
        GraphConstants.setLineStyle(transtion.getAttributes(), jGraphStyle);

        Map nested = new Hashtable();
        nested.put(transtion, transtion.getAttributes());
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    /**
     * Set Transtion Conrol Point (Check JGraph Manual more for control point in detail)
     * @param transitionID transtion ID
     * @param list control points to be added
     */
    public void setTransitionControlPoint(int transitionID, ArrayList list) {
        DefaultEdge transtion = (DefaultEdge) transitionMap.get(transitionID);
        try {
            GraphConstants.setPoints(transtion.getAttributes(), list);
            Map nested = new Hashtable();
            nested.put(transtion, transtion.getAttributes());
            graph.getGraphLayoutCache().edit(nested);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     *  TempTranstion effect
     * @param list the control points
     */
    public void setNewTempTransitionControlPoint(ArrayList list) {
        tempNewTransitionControlPoint = list;
    }

    /**
     *
     * @param sourceID
     * @param targetID
     * @param transitionID
     * @param edgeLabel
     */
    public void insertStraightTransition(int sourceID, int targetID, int transitionID, String edgeLabel) {
        createTransition(sourceID, targetID, transitionID, edgeLabel, null);
    }
    /**
     *  Insert Poly Transtion
     *
     * @param sourceID source State ID
     * @param targetID target state ID
     * @param transitionID transtion ID
     * @param pointList control points
     * @param edgeLabel transtion label
     */
    public void insertPolyTransition(int sourceID, int targetID, int transitionID, ArrayList pointList, String edgeLabel) {
        //sourceID = 1;
        createCurvelTransition(sourceID, targetID, transitionID, GraphConstants.STYLE_ORTHOGONAL, pointList, edgeLabel, null);
    }
    /**
     *  Insert Poly Transtion
     *
     * @param sourceID source State ID
     * @param targetID target state ID
     * @param transitionID transtion ID
     * @param pointList control points
     * @param edgeLabel transtion label
     */
    public void insertCurveTransition(int sourceID, int targetID, int transitionID, ArrayList pointList, String edgeLabel) {
        createCurvelTransition(sourceID, targetID, transitionID, GraphConstants.STYLE_BEZIER, pointList, edgeLabel, null);
    }
    /**
     *  Insert Bezier Transtion
     *
     * @param sourceID source State ID
     * @param targetID target state ID
     * @param transitionID transtion ID
     * @param pointList control points
     * @param edgeLabel transtion label
     */
    public void insertBezierTransition(int sourceID, int targetID, int transitionID, ArrayList pointList, String edgeLabel) {
        createCurvelTransition(sourceID, targetID, transitionID, GraphConstants.STYLE_BEZIER, pointList, edgeLabel, null);
    }
    /**
     *  Insert Spline Transtion
     *
     * @param sourceID source State ID
     * @param targetID target state ID
     * @param transitionID transtion ID
     * @param pointList control points
     * @param edgeLabel transtion label
     */
    public void insertSplineTransition(int sourceID, int targetID, int transitionID, ArrayList pointList, String edgeLabel) {
        createCurvelTransition(sourceID, targetID, transitionID, GraphConstants.STYLE_SPLINE, pointList, edgeLabel, null);
    }

    /**
     * 
     * Insert Curve Transtion
     *
     * @param sourceID Source State ID
     * @param targetID Target State ID
     * @param transitionID Transition ID
     * @param lineType The transition type
     * @param pointList the transition point to be thoughed
     * @param edgeLabel the transition Label to be displayed
     * @param e Mouse Event
     */
    public void createCurvelTransition(int sourceID, int targetID, int transitionID, int lineType, ArrayList pointList, String edgeLabel, MouseEvent e) {

        DefaultEdge edge = new DefaultEdge(edgeLabel);
        DefaultGraphCell sourceCell = (DefaultGraphCell) stateMap.get(sourceID);
        DefaultGraphCell targetCell = (DefaultGraphCell) stateMap.get(targetID);


        edge.setSource(sourceCell.getChildAt(0));
        edge.setTarget(targetCell.getChildAt(0));

        // Set appearance
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        int jGraphStyle = toJGraphLineType(lineType);
        GraphConstants.setLineStyle(edge.getAttributes(), jGraphStyle);

        if (pointList.size() != 0 && lineType != TypeConstants.STRAIGHT_LINE) {
            GraphConstants.setPoints(edge.getAttributes(), pointList);
        } else {
            if (e != null && lineType != TypeConstants.STRAIGHT_LINE) {

                List points = new ArrayList();
                double SourceX = GraphConstants.getBounds((sourceCell).getAttributes()).getX();
                double SourceY = GraphConstants.getBounds((sourceCell).getAttributes()).getY();


                double offsetX, offsetY;
                double controlPointNum = 10;

                offsetX =
                        (e.getX() - SourceX) / controlPointNum;
                offsetY =
                        (e.getY() - SourceY) / controlPointNum;

                points.add(edge.getAttributes().createPoint());
                points.add(edge.getAttributes().createPoint(SourceX + offsetX * 5, SourceY + offsetY * 8));
                /*
                for (int i = 0; i < controlPointNum; i++) {
                points.add(edge.getAttributes().createPoint(SourceX +(offsetX * i ), SourceY +Math.log(offsetY * i )));
                }*/
                points.add(edge.getAttributes().createPoint());
                GraphConstants.setPoints(edge.getAttributes(), points);
            }

        }
        GraphConstants.setLabelPosition(edge.getAttributes(), new Point2D.Double(GraphConstants.PERMILLE / 2, -7));
        GraphConstants.setSelectable(edge.getAttributes(), true);
        graph.getGraphLayoutCache().insert(edge);
        transitionMap.put(transitionID, edge);

    }

    /**
     *
     * Display NewTemp Transtion Effect
     *
     * @param x x coordiantes
     * @param y y coordinates
     * @param lineType  integer line type
     * @param isLoop is this transitoin a loop ?
     * @param e mouseEvent
     */
    public void showNewTransition(double x, double y, int lineType, boolean isLoop, MouseEvent e) {

        tempNewTransitionType = lineType;
        DefaultGraphCell cell = (DefaultGraphCell) stateMap.get(functionTempNewTransitionSourceID);
        String sCellID = String.valueOf((Integer) cell.getAttributes().get("id"));
        int cellID = Integer.valueOf(sCellID);
        if (isLoop) {
            createTransition(cellID, cellID, TEMP_NEW_TRANSITION_ID, "", e);
        } else {
            //Add a Very small state  , a trick for add new transition
            createState(100, 300, CURSOL_STATE_ID, "", null);
            double x_offset, y_offset;

            Point2D.Float soruce = getStatePosition(cellID);
            if (soruce.x <= x) {
                x_offset = 15;
            } else {
                x_offset = -15;
            }

            if (soruce.y <= y) {
                y_offset = 15;
            } else {
                y_offset = -15;
            }

            setStatePosition(CURSOL_STATE_ID, new Point2D.Double(x / graph.getScale() - x_offset / graph.getScale(), y / graph.getScale() - y_offset / graph.getScale()), 0);
            createTransition(cellID, CURSOL_STATE_ID, TEMP_NEW_TRANSITION_ID, "", e);
        }
    }

    /**
     * Insert State 
     * @param stateID State ID 
     * @param statename State Name
     * @param point State coordinates
     */
    public void insertState(int stateID, String statename, Point2D.Float point) {
        System.out.println(graph.getScale());
        createState(point.x / graph.getScale(), point.y / graph.getScale(), stateID, statename, null);
    }

    /**
     * Create State
     * @param x coordinate
     * @param y coordiante
     * @param cellID State ID
     * @param cellname State Label
     * @param viewClass The state appearance
     * @return jgraph State
     */
    public DefaultGraphCell createState(double x, double y, int cellID, String cellname, String viewClass) {
        x = x - stateSize / 2;
        y = y - stateSize / 2;

        viewClass = "view.cellview.StateView";

        double w = stateSize; // width
        double h = stateSize; // height

        // For CursorID
        if (cellID == CURSOL_STATE_ID) {
            w = 10;
            h = 10;
        }

        DefaultGraphCell group = new DefaultGraphCell();
        group.getAttributes().put("size", stateSize);
        GraphConstants.setBounds(group.getAttributes(), new Rectangle2D.Double(x - w, y - h, 3 * w, 3 * h));
        group.getAttributes().put("state", true);
        group.getAttributes().put("id", cellID);
        group.getAttributes().put("initial", false);
        group.getAttributes().put("final", false);
        //group.addPort();
        graph.getGraphLayoutCache().insert(group);

        DefaultGraphCell state = new DefaultGraphCell(cellname);
        state.getAttributes().put("size", stateSize);
        GraphConstants.setBounds(state.getAttributes(), new Rectangle2D.Double(x, y, w, h));
        state.getAttributes().put("id", cellID);

        DefaultPort port = new DefaultPort();//no
        DefaultPort port2 = new DefaultPort();//loop
        DefaultPort port3 = new DefaultPort();//initial
        DefaultPort port4 = new DefaultPort();//fianl

        GraphConstants.setOffset(port2.getAttributes(), new Point2D.Double(0.5 * GraphConstants.PERMILLE, 0.6 * GraphConstants.PERMILLE));
        state.add(port);
        state.add(port2);
        state.add(port3);
        state.add(port4);

        MyCellViewFactory.setViewClass(state.getAttributes(), viewClass);
        GraphConstants.setBorderColor(state.getAttributes(), Color.black);
        graph.getGraphLayoutCache().insert(state);

        ParentMap pm = new ParentMap();
        pm.addEntry(state, group);
        graph.getGraphLayoutCache().edit(null, null, pm, null);
        stateMap.put(cellID, group);


        if (cellID == CURSOL_STATE_ID) {
            graph.getGraphLayoutCache().setVisible(state, false);
        }
        return group;
    }

    /**
     * Set Transtion Label
     * @param transitionID transtion ID
     * @param label Transition Label
     */
    public void setTransitionLabel(int transitionID, String label) {
        DefaultEdge cell = (DefaultEdge) transitionMap.get(transitionID);
        GraphConstants.setValue(cell.getAttributes(), label);
        GraphConstants.setLabelPosition(cell.getAttributes(), new Point2D.Double(GraphConstants.PERMILLE / 2, -7));
        Map nested = new Hashtable();
        nested.put(cell, cell.getAttributes());
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    /**
     * set Transtion Label
     * @param cell transtion object
     * @param label transtion label
     */
    public void setTransitionLabel(DefaultGraphCell cell, String label) {
        GraphConstants.setValue(cell.getAttributes(), label);
        GraphConstants.setLabelPosition(cell.getAttributes(), new Point2D.Double(GraphConstants.PERMILLE / 2, -7));
        Map nested = new Hashtable();
        nested.put(cell, cell.getAttributes());
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    /**
     * set State Name
     * @param stateID set State ID
     * @param name state state Label
     * @param sentEvent sent an UI Event to View
     */
    public void setStateName(int stateID, String name, boolean sentEvent) {
        DefaultGraphCell cell = (DefaultGraphCell) stateMap.get(stateID);
        GraphConstants.setValue(cell.getAttributes(), name);
        Map nested = new Hashtable();
        nested.put(cell, cell.getAttributes());
        graph.getGraphLayoutCache().edit(nested, null, null, null);

        if (sentEvent) {
            UIEvent event = new UIEvent(UserEventConstants.CHANGE_STATE_NAME);
            event.setStateID(stateID);
            event.setsData(name);
            View.eventHappen(event);
        }

    }

    /**
     *
     * @param stateID state ID
     * @param x x-coordiante
     * @param y y-coordiante
     */
    public void setStatePosition(int stateID, double x, double y) {
        setStatePosition(stateID, new Point2D.Double(x, y), stateSize);
    }

    /**
     * Set state position
     * @param stateID state ID
     * @param point state position
     * @param size  state size
     */
    public void setStatePosition(int stateID, Point2D point, double size) {

        DefaultGraphCell group = (DefaultGraphCell) stateMap.get(stateID);
        if (group != null) {
            group.getAttributes().put("size", size);
            Rectangle2D rec2D = GraphConstants.getBounds(group.getAttributes());
            rec2D.setRect(point.getX() - size, point.getY() - size, size * 3, size * 3);
            GraphConstants.setBounds(group.getAttributes(), rec2D);
            Map nested = new Hashtable();
            nested.put(group, group.getAttributes());
            graph.getGraphLayoutCache().edit(nested);

            DefaultGraphCell state = (DefaultGraphCell) group.getChildAt(0);
            state.getAttributes().put("size", size);


            rec2D = GraphConstants.getBounds(state.getAttributes());
            rec2D.setRect(point.getX(), point.getY(), size, size);
            GraphConstants.setBounds(state.getAttributes(), rec2D);
            nested = new Hashtable();
            nested.put(state, state.getAttributes());
            graph.getGraphLayoutCache().edit(nested);

            //這時侯才抓到正確的 x, y
            changeStateStyle(stateID, Boolean.parseBoolean(group.getAttributes().get("initial").toString()), Boolean.parseBoolean(group.getAttributes().get("final").toString()), Float.parseFloat(group.getAttributes().get("size").toString()));
        }
    }

    /**
     * show JGraph to canvas (check jGraph manual for more detail)
     */
    public void show() {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(graph));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 600);
        frame.setVisible(true);
    }

    /**
     * Set FisheyeMode on
     * @param flag
     */
    public void setFisheyeMode(boolean flag) {
        fisheyeMode = flag;
        if (flag) // backup state
        {
            FisheyeAlgorithm.setStateMap(stateMap);
        } else {
            stateSize = 25;
            recoverState();
        }
    }

    public void recoverState() {
        HashMap backupMap = FisheyeAlgorithm.getSateMap();
        for (int j = 0; backupMap.containsKey(j); j++) {
            Point2D point = (Point2D) backupMap.get(j);
            setStatePosition(j, point, stateSize);
        }

    }
    /**
     * Clean Transition Contorl Point
     */
    public void cleanTransitionContorlPoint() {
        this.layerlayout = false;
        //TODO remove control point
        Iterator idSets = transitionMap.keySet().iterator();
        while (idSets.hasNext()) {
            int transitionID = Integer.parseInt(idSets.next().toString());
            DefaultEdge edge = (DefaultEdge) transitionMap.get(transitionID);
            Map nested = new Hashtable();

            //GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
            GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
            ArrayList list = new ArrayList();
            list.add(edge.getAttributes().createPoint());
            list.add(edge.getAttributes().createPoint());
            GraphConstants.setPoints(edge.getAttributes(), list);
            nested.put(edge, edge.getAttributes());
            graph.getGraphLayoutCache().edit(nested, null, null, null);
        }

    }

    /**
     * Update the appeareance of the transitions of initial state and final state
     *
     */
    public void updateLoopInitalFinal() {

        //Update Inital , Final
        Iterator idSets = stateMap.keySet().iterator();
        while (idSets.hasNext()) {
            int stateID = Integer.parseInt(idSets.next().toString());
            DefaultGraphCell cell = (DefaultGraphCell) stateMap.get(stateID);
            changeStateStyle(stateID, Boolean.parseBoolean(cell.getAttributes().get("initial").toString()), Boolean.parseBoolean(cell.getAttributes().get("final").toString()), stateSize, true);
        }
        //Update Transition
        Iterator<DefaultEdge> transitions = transitionMap.values().iterator();
        while (transitions.hasNext()) {
            // Update the loop position
            Map nested = new Hashtable();
            DefaultEdge edge = transitions.next();

            // Update Loop
            if (edge.getSource() == edge.getTarget()) { // if this transition is loop , update the position
                GraphConstants.setRouting(edge.getAttributes(), LoopRouter.getSharedInstance());
                ModifyStateLoop msl = new ModifyStateLoop(stateSize, transitionMap, stateMap);
                DefaultPort port = (DefaultPort) edge.getSource();
                if (port != null) {
                    DefaultGraphCell cell = (DefaultGraphCell) port.getParent();
                    if (cell != null) {
                        int sourceID = getStateID(cell);
                        Point2D.Double intercept = msl.FindLoopPortAlg(sourceID);
                        edge.getAttributes().put("angle", msl.angle);
                        GraphConstants.setOffset(port.getAttributes(), intercept);
                        nested.put(edge, edge.getAttributes());
                        nested.put(port, port.getAttributes());
                        EdgeView edgeView = (EdgeView) graph.getGraphLayoutCache().getMapping(edge, false);
                        graph.getGraphLayoutCache().refresh(edgeView, false);

                    }

                }

            }
            graph.getGraphLayoutCache().edit(nested, null, null, null);
        }

    }

    /**
     * Update State Position to Data Model(MVC)
     */
    public void updateStatePositiontoDataModel() {
        if (stateMap.get(CURSOL_STATE_ID) != null) {
            removeState(CURSOL_STATE_ID);
        }

        if (transitionMap.get(TEMP_NEW_TRANSITION_ID) != null) {
            removeTransition(TEMP_NEW_TRANSITION_ID);
        }

        int size = model.getRootCount();
        Iterator idSets = stateMap.keySet().iterator();
        while (idSets.hasNext()) {
            int stateID = Integer.parseInt(idSets.next().toString());
            UIEvent event = new UIEvent(UserEventConstants.CHANGE_STATE_POSITION);
            event.setStateID((Integer) stateID);
            DefaultGraphCell group = (DefaultGraphCell) stateMap.get((Integer) stateID);
            VertexView cellView = (VertexView) graph.getGraphLayoutCache().getMapping(group.getChildAt(0), false);
            Rectangle2D rec2D = cellView.getBounds();
            Point2D.Float pos = new Point2D.Float((float) rec2D.getX(), (float) rec2D.getY());
            event.setPoint(pos);
            View.eventHappen(event);
        }

        updateLoopInitalFinal();

    }
/**
 * Running Hierarchical Layout
 */
    public void hierarchicalLayout2() {
        cleanTransitionContorlPoint();
        HierarchicalLayout2 layout = new HierarchicalLayout2(graph, stateMap, transitionMap, this);
        layout.run();
        //updateLoopInitalFinal();
    }
/**
 * Running Circular Layout
 */
    public void circleLayout() {
        cleanTransitionContorlPoint();
        CircleLayout circleLayout = new CircleLayout(transitionMap,stateMap,this);
        circleLayout.RunLayout();
        updateLoopInitalFinal();
    }

    /**
     * Set graph's scale to be 1 (normal scale)
     */
    public void actualSize() {
        graph.setScale(1);
        graphScale =1;
    }

    /**
     * Let Graph to fit in the window
     */
    public void fitWindow() {
        JGraphLayoutMorphingManager.fitViewport(graph);
    }
}
