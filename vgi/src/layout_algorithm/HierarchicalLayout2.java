package layout_algorithm;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import view.jgraph.JGraphController;

/**
 * change graph layout according to Sugiyama's 4 step algorithm
 * crossing reduction implemented using global sifting crossing reduction
 * @author cody, polar
 */
public class HierarchicalLayout2 {

    /** stores tools required for graph manipulation */
    private JGraph graph;

    /** hashmap between integer id value and jgraph node object */
    private HashMap<Integer, DefaultGraphCell> stateMap;

    /** hashmap between integer id value and jgraph transition object */
    private HashMap<Integer, DefaultEdge> transitionMap;

    /** hashmap between two implementation of graph nodes */
    private HashMap<DefaultGraphCell, Node> nodeMap = new HashMap<DefaultGraphCell, Node>();
    
    /** hashmap between jgraph object to list of dummy nodes */
    private HashMap<DefaultEdge, List> dummyListMap = new HashMap<DefaultEdge, List>();

    /** defines size of graph node size */
    private int stateSize;

    private int width = 90000;

    /** stores tools required for graph manipulation */
    private JGraphController jgc;
    
    /**
     * outer list stores list of horizontal layers,
     * inner list stores nodes in the same horizontal layer
     */
    private LinkedList<LinkedList> layerList;

    private HashMap<Object, Double> xCoordinate = new HashMap(); // store children barycenter

    /** offset between neighboring nodes */
    private double offset = 100;

    /** layerList used for crossing reduction */
    private LinkedList<LinkedList<Node>> layerListCR;

    /** for debugging, ignore in final build */
    private static final boolean DEBUG = false;

    /** hashmap between node the block it resides in */
    private Hashtable<Node, Block> nodeToBlockH;

    /** hashmap between an integer id and the block it represents */
    private Hashtable<Integer, Block> idToBlockH;

    /** min distance between graph nodes */
    private static final double X_POS_MARGIN = .5;

    //For X-coordinate , Vertical aligment
    private HashSet<String> segmentMarked; // <parent, child>

    private double minimumSeparation = 1;

    // For horizontal compaction in X-Coordinate, using Fast and Simple

    /**
     * Some Constants
     */
    private class Node {

        //Common
        private boolean dummy;
        private double xCoordinate;
        private Point2D.Double dummyPoint;
        private DefaultGraphCell state = null;
        private LinkedList<Node> sourceList;
        private LinkedList<Node> targetList;
        // data member added by polar
        private boolean visited = false;
        private Vector<Node> nPlus = new Vector<Node>();
        private Vector<Node> nMinus = new Vector<Node>();
        private Vector<Integer> iPlus = new Vector<Integer>();
        private Vector<Integer> iMinus = new Vector<Integer>();
        // For X-Coordinate , Vertical alignment
        private Node align = this;
        private Node root = this;
        private int order;
        private int level;
        // For horizontal compaction in X-Coordinate, using Fast and Simple
        private Node sink = this;
        private double shift = Double.MAX_VALUE;
        private double x = -1;// -1 means undefined
        private double upperLeftX = -1;// -1 means undefined
        private double upperRightX = -1;// -1 means undefined
        private double lowerLeftX = -1;// -1 means undefined
        private double lowerRightX = -1;// -1 means undefined

        public Node(boolean dummy) {
            this.dummy = dummy;
            this.sourceList = new LinkedList<Node>();
            this.targetList = new LinkedList<Node>();
            this.dummyPoint = new Point2D.Double();
        }
    } // end of class Node

    /**
     * a block consists of a group of nodes
     */
    private class Block {

        Vector<Node> nodes = new Vector<Node>();
        int piValue = 0;
        int id = 0;

        public Node getUpper() {
            return nodes.get(0);
        }

        public Node getLower() {
            return nodes.get(nodes.size() - 1);
        }

        public Vector<Node> getNPlus() {
            return this.getLower().nPlus;
        }

        public Vector<Node> getNMinus() {
            return this.getUpper().nMinus;
        }

        public Vector<Integer> getIPlus() {
            return this.getLower().iPlus;
        }

        public Vector<Integer> getIMinus() {
            return this.getUpper().iMinus;
        }

        //returns null if no node at input layer is found
        public Node getNodeAtLayer(int inputLayer) {
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                int layer = node.level;
                if (layer == inputLayer) {
                    return node;
                }
            }
            return null;
        }
    } // end of class Block

    /**
     * constructor
     * @param graph graph to be modified
     * @param stateMap hashmap containing graph nodes
     * @param transitionMap hashmap containing node transition
     * @param jgc contains tools for graph manipulation
     */
    public HierarchicalLayout2(JGraph graph, HashMap stateMap, HashMap transitionMap, JGraphController jgc) {
        this.graph = graph;
        this.stateMap = stateMap;
        this.stateSize = stateMap.size();
        this.transitionMap = (HashMap<Integer, DefaultEdge>) transitionMap.clone();
        this.jgc = jgc;
        this.layerList = new LinkedList();
        jgc.layerlayout = true;
    }

    private int getRealIndex(int x) {
        if (x <= 0) {
            throw new Error("Paper Index should not larger than 1");
        }
        return x - 1;
    }

     /**
     * This algorithm is based on
     * U. Brandes and B. Köpf, “Fast and Simple Horizontal Coordinate Assignment,” Graph Drawing, 2002, page. 33-36.
     * Algorithm 1 : Preprocessing (mark type 1 conflicts)
     */
    private void preprocessingXC() {

        segmentMarked = new HashSet();

        for (int i = 1; i < layerListCR.size() - 2; i++) {
            int k0 = 0, l = 1;
            for (int l1 = 1; l1 <= layerListCR.get(i + 1).size(); l1++) {
                Node node = (Node) layerListCR.get(i + 1).get(l1 - 1);
                int k1 = layerListCR.get(i).size();
                if (!node.dummy && l1 != layerListCR.get(i + 1).size()) {
                    continue;
                }

                boolean inner_segment = false;
                Iterator<Node> it = node.sourceList.iterator();
                int pos = 0;
                while (it.hasNext()) {
                    pos++;
                    Node neighborNode = it.next();
                    if (neighborNode.dummy) {
                        inner_segment = true;
                        k1 = pos;
                        break;
                    }
                }
                if (l1 == layerListCR.get(i + 1).size() || inner_segment) {
                    while (l <= l1) {
                        Node otherNode = layerListCR.get(i + 1).get(l - 1);
                        Iterator<Node> it2 = otherNode.sourceList.iterator();
                        while (it2.hasNext()) {
                            Node neighborNode = it2.next();
                            int neighborPos = layerListCR.get(i).indexOf(neighborNode);
                            if (neighborPos == -1) {
                                throw new Error("NeighborPos should not be -1");
                            }
                            if (neighborPos < k0 || neighborPos > k1) {
//                                segmentMarked.add(neighborNode.hashCode() * 100000 + otherNode.hashCode());
                            }
                        }
                        l++;
                    }
                    k0 = k1;
                }
            }
        }
    }

    private void sortNeighborsList(Node node, int level) {
        if (level > 0) {
            LinkedList<Node> list = node.sourceList;
            LinkedList<Node> newList = new LinkedList();

            Iterator<Node> it = list.iterator();
            while (it.hasNext()) {
                Node neighborNode = it.next();
                neighborNode.order = layerListCR.get(level - 1).indexOf(neighborNode) + 1;

                int addPos = 0;
                while (addPos != 0 && newList.get(addPos).order < neighborNode.order) {
                    addPos++;
                }
                newList.add(addPos, neighborNode);
            }
            node.sourceList = newList;
        }
    }

    /**
     * This algorithm is based on
     * U. Brandes and B. Köpf, “Fast and Simple Horizontal Coordinate Assignment,” Graph Drawing, 2002, page. 33-36.
     * Algorithm 2 : Vertical alignment
     */
    private void verticalAlignment() {


        //initialize
        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> list = layerListCR.get(i);
            Iterator<Node> it = list.iterator();
            while (it.hasNext()) {
                Node node = it.next();
                node.align = node;
                node.root = node;
            }

        }

        for (int i = 0; i < layerListCR.size(); i++) {
            int r = 0;
            for (int k = 0; k < layerListCR.get(i).size(); k++) {
                Node node = layerListCR.get(i).get(k);
                if (node.sourceList.size() == 0) {
                    continue;
                }
                sortNeighborsList(node, i);
                LinkedList<Node> neighbors = node.sourceList;
                LinkedList<Node> median = new LinkedList<Node>();
                if (neighbors.size() % 2 == 1) {
                    median.add(neighbors.get((neighbors.size() - 1) / 2));
                } else {
                    median.add(neighbors.get(neighbors.size() / 2));
                    median.add(neighbors.get((neighbors.size() / 2) - 1));
                }
                for (int p = 0; p < median.size(); p++) {
                    if (node.align == node) {
                        Node uNode = median.get(p);

                        boolean isMarked = segmentMarked.contains(Integer.toString(uNode.hashCode()) +
                                "," + Integer.toString(node.hashCode()));
                        if ((!isMarked) && r < uNode.order) {
                            uNode.align = node;
                            node.root = uNode.root;
                            node.align = node.root;
                            r = uNode.order;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < layerListCR.size(); i++) {
            for (int k = 0; k < layerListCR.get(i).size(); k++) {
                Node node = layerListCR.get(i).get(k);
                if (!node.dummy) {
                    System.out.print(" state:" + node.state.toString());
                } else {
                    System.out.print(" state: (dummy, level: " + node.level + " , order: " + node.order + ")");
                }
                if (!node.align.dummy) {
                    System.out.print(" align:" + node.align.state.toString());
                } else {
                    System.out.print(" align: (dummy , level: " + node.align.level + " , order: " + node.align.order + ")");
                }
                if (!node.root.dummy) {
                    System.out.println(" root:" + node.root.state.toString());
                } else {
                    System.out.println(" root: (dummy , level:" + node.root.level + " , order: " + node.root.order + ")");
                }
            }
        }
    }

    /**
     * executes Sugiyama's 4 step graph layout algorithm
     */
    public void run() {
        clean();
        cycleRemoval();

        layerAssignment();

        buildLayerListCR();
        //barycenterXR();

        globalSiftingXR();

        //preprocessingXC();
        //verticalAlignment();
        horizontalCoordinateAssignment();

        //horizontalDeCompaction();	// used with barycenter XO only
        //rerverse edge, initial/final edge
        draw();

    }// end of run

    /**
     * Clean the cell attribute during this layout
     */
    private void clean() {
        Iterator<DefaultGraphCell> states = stateMap.values().iterator();
        while (states.hasNext()) {
            states.next();
        }

        Iterator<DefaultEdge> transitons = transitionMap.values().iterator();
        while (transitons.hasNext()) {
            DefaultEdge transition = transitons.next();
            if (transition.getAttributes() == null) {
                continue;
            }

            transition.getAttributes().put("stage1Done", Boolean.FALSE);

            Map nested = new Hashtable();
            nested.put(transition, transition.getAttributes());
            graph.getGraphLayoutCache().edit(nested, null, null, null);
        }

    }

    /**
     * STAGE 1 : Cycle removal
     * Compute the outgoing and the incoming degree of a state
     */
    private void cycleRemoval() {
        Iterator stateidSets = stateMap.keySet().iterator();
        while (stateidSets.hasNext()) {
            DefaultGraphCell group = (DefaultGraphCell) stateMap.get(Integer.parseInt(stateidSets.next().toString()));
            DefaultGraphCell state = (DefaultGraphCell) group.getFirstChild();
            DefaultPort port = (DefaultPort) state.getFirstChild();
            Iterator edges = port.getEdges().iterator();
            Set<DefaultEdge> imcomingSet = new HashSet<DefaultEdge>();
            Set<DefaultEdge> outgoingSet = new HashSet<DefaultEdge>();

            while (edges.hasNext()) { // Count the in/out edges
                DefaultEdge edge = (DefaultEdge) edges.next();
                if (edge.getAttributes().get("stage1Done") == Boolean.FALSE) {
                    edge.getAttributes().put("stage1Done", Boolean.TRUE);
                    Map nested = new Hashtable();
                    nested.put(edge, edge.getAttributes());
                    graph.getGraphLayoutCache().edit(nested, null, null, null);
                } else {
                    continue;
                }

                if (((DefaultPort) edge.getSource()).getParent() == state && edge.getTarget() != state) {
                    outgoingSet.add(edge);
                } else if (((DefaultPort) edge.getTarget()).getParent() == state && edge.getSource() != state) {
                    imcomingSet.add(edge);
                }

            }

            //Record the outgoing , imcoming degress
            Map statenested = new Hashtable();
            statenested.put(state, state.getAttributes());
            graph.getGraphLayoutCache().edit(statenested, null, null, null);

            if (imcomingSet.size() == 0 && outgoingSet.size() == 0) {
                // Nothing need to do
            } else if (imcomingSet.size() >= outgoingSet.size()) {
                // Let the outgoingSet to be inverse
                edges = outgoingSet.iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    edge.getAttributes().put("reversed", Boolean.TRUE);
                    DefaultPort temp = (DefaultPort) edge.getSource();
                    edge.setSource((DefaultPort) edge.getTarget());
                    edge.setTarget(temp);
                    Map nested = new Hashtable();
                    nested.put(edge, edge.getAttributes());
                    graph.getGraphLayoutCache().edit(nested, null, null, null);
                }

                edges = imcomingSet.iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    edge.getAttributes().put("reversed", Boolean.FALSE);
                    Map nested = new Hashtable();
                    nested.put(edge, edge.getAttributes());
                    graph.getGraphLayoutCache().edit(nested, null, null, null);
                }

            } else {
                //Let the imcomginSet to be inverse
                edges = imcomingSet.iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    edge.getAttributes().put("reversed", Boolean.TRUE);
                    DefaultPort temp = (DefaultPort) edge.getSource();
                    edge.setSource((DefaultPort) edge.getTarget());
                    edge.setTarget(temp);
                    Map nested = new Hashtable();
                    nested.put(edge, edge.getAttributes());
                    graph.getGraphLayoutCache().edit(nested, null, null, null);
                }

                edges = outgoingSet.iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    edge.getAttributes().put("reversed", Boolean.FALSE);
                    Map nested = new Hashtable();
                    nested.put(edge, edge.getAttributes());
                    graph.getGraphLayoutCache().edit(nested, null, null, null);
                }

            }
        }
    } // end cycleRemoval

    /**
     * STAGE 2 : Layer Assignment , Using a modfied Coffman-Graham Layering (1972)
     * reference:http://www.cs.usyd.edu.au/%7Evisual/comp5048/L4_comp5048_2009_layering_1.pdf
     */
    private void layerAssignment() {



        /*
         * Phase 1 : Assign Labels
         */
        Map nested = new Hashtable();
        // initial the pi of v
        Iterator stateidSets = stateMap.keySet().iterator();
        while (stateidSets.hasNext()) {
            DefaultGraphCell state = (DefaultGraphCell) ((DefaultGraphCell) stateMap.get(Integer.parseInt(stateidSets.next().toString()))).getFirstChild();
            state.getAttributes().put("label", stateSize + 1); // initial , set the Max value
            nested.put(state, state.getAttributes());
        }

        graph.getGraphLayoutCache().edit(nested, null, null, null);


        for (int i = 0; i < stateSize; i++) { //Compute the N state 's pi(v)
            stateidSets = stateMap.keySet().iterator();
            while (stateidSets.hasNext()) {
                DefaultGraphCell state = (DefaultGraphCell) ((DefaultGraphCell) stateMap.get(Integer.parseInt(stateidSets.next().toString()))).getFirstChild();
                Iterator edges = ((DefaultPort) state.getFirstChild()).getEdges().iterator();
                int mini_set = 0;
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    if (((DefaultPort) edge.getTarget()).getParent() == state) {
                        //@FIXME , Maybe it's not good enough
                        mini_set += (Integer) ((DefaultGraphCell) ((DefaultPort) edge.getTarget()).getParent()).getAttributes().get("label");
                    }
                }
                state.getAttributes().put("mini_set", mini_set);
            }

            // find the minium one
            stateidSets = stateMap.keySet().iterator();
            int global_mini_set = 999999999;
            int mini_id = -1;
            while (stateidSets.hasNext()) {
                int stateID = (Integer) stateidSets.next();
                DefaultGraphCell state = (DefaultGraphCell) ((DefaultGraphCell) stateMap.get(stateID)).getFirstChild();
                if ((Integer) state.getAttributes().get("mini_set") < global_mini_set && (Integer) state.getAttributes().get("label") == stateSize + 1) {
                    global_mini_set = (Integer) state.getAttributes().get("mini_set");
                    mini_id =
                            stateID;
                }

            }
            // Let the mini's one 's pi(v) be i
            ((DefaultGraphCell) stateMap.get(mini_id).getFirstChild()).getAttributes().put("label", i);
        }

        stateidSets = stateMap.keySet().iterator();
        while (stateidSets.hasNext()) {
            int stateID = (Integer) stateidSets.next();
            DefaultGraphCell state = (DefaultGraphCell) ((DefaultGraphCell) stateMap.get(stateID)).getFirstChild();
            //System.out.println((stateID) + " node with a lable of  " + state.getAttributes().get("label"));
        }

        /*
         * Phase 2 : Assign Layers
         */
        //Initial
        int k = 0;
        HashSet<TreeNode> U = new HashSet();  // U : Put the all state processed , and this come from Waldo & Ludo's version
        HashSet<TreeNode> L1toLk_1 = new HashSet();  // L1_to_LK_1 , and this come from Waldo & Ludo's version

        LinkedList<DefaultGraphCell> now_layer = new LinkedList();
        layerList.addFirst(now_layer);
        while (U.size() < stateSize) {
            
            int max_label = -1;
            DefaultGraphCell max_state = null;

            int max_nowlayer = -1;
            DefaultGraphCell max_state_ToPutInSameLayer = null;

            int max_bad_state_label = -1;
            DefaultGraphCell max_bad_state = null;

            // Choose u in (V - U) such taht every vertex in { u | (u,v) in E } is in U
            // and label(u) is maximized

            stateidSets = stateMap.keySet().iterator();
            while (stateidSets.hasNext()) {
                int stateID = (Integer) stateidSets.next();
                DefaultGraphCell state = (DefaultGraphCell) ((DefaultGraphCell) stateMap.get(stateID)).getFirstChild();
                if (U.contains(state)) { // U : the state processed 
                    continue;
                }

                //Check the label is bigger or equal to max_label

                int label = (Integer) state.getAttributes().get("label");

                //Are  all target state from this state in  U ?
                if (state.toString().compareTo("s28") == 0) {
                    int x = 1;
                }
                
                boolean needToputInNextLayer = false;
                Iterator edges = ((DefaultPort) state.getFirstChild()).getEdges().iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    if (((DefaultPort) edge.getSource()).getParent() != state) {
                        continue;
                    }
                    if (now_layer.contains((DefaultGraphCell) ((DefaultPort) edge.getTarget()).getParent())) {
                        needToputInNextLayer = true;
                        break;
                    }
                }
                boolean allTargetInLower = true;
                edges = ((DefaultPort) state.getFirstChild()).getEdges().iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
                    if (((DefaultPort) edge.getSource()).getParent() != state) {
                        continue;
                    }
                    if (!L1toLk_1.contains(((DefaultPort) edge.getTarget()).getParent())) {
                        allTargetInLower = false;
                        break;
                    }
                }

                if (allTargetInLower && (label > max_label)) {
                    max_label = label;
                    max_state = state;
                }
                if (allTargetInLower && (!needToputInNextLayer) && (label > max_nowlayer)) {
                    max_nowlayer = label;
                    max_state_ToPutInSameLayer = state;
                }
            }
            // Put to now layer
            System.out.println(max_state_ToPutInSameLayer + " " + max_state + " " + max_bad_state);

            if (now_layer.size() <= this.width && max_state_ToPutInSameLayer != null) {
                now_layer.add(max_state_ToPutInSameLayer);
            } else { // Add
                L1toLk_1.addAll(now_layer);
                now_layer = new LinkedList<DefaultGraphCell>();
                if (max_state_ToPutInSameLayer != null) {
                    now_layer.add(max_state_ToPutInSameLayer);
                } else if(max_state !=null) {
                    now_layer.add(max_state);
                }else{
                    ;
                    /*
                    try {
                    throw new Exception("No max_state");
                    } catch (Exception ex) {
                        Logger.getLogger(HierarchicalLayout2.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                }
                layerList.addFirst(now_layer);
            }
            if (max_state_ToPutInSameLayer != null) {
                max_state_ToPutInSameLayer.getAttributes().put("layer", layerList.size() - 1);
                U.add(max_state_ToPutInSameLayer);
            } else if(max_state != null) {
                max_state.getAttributes().put("layer", layerList.size() - 1);
                U.add(max_state);
            } else{
                /*
                try {
                    throw new Exception("No max_state");
                } catch (Exception ex) {
                    Logger.getLogger(HierarchicalLayout2.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
        }


        //Create Dummy nodes. In latter , we can replace the dummyState with control point
        Iterator<Integer> transitions = ((HashMap) transitionMap.clone()).keySet().iterator();
        while (transitions.hasNext()) {
            DefaultEdge edge = transitionMap.get(transitions.next());
            //If ( Layer of soruce - layer of targer ) is larger than one , insert dummy state
            if (edge.getSource() == null || edge.getTarget() == null) {
                continue;
            }

            DefaultGraphCell source = (DefaultGraphCell) ((DefaultPort) edge.getSource()).getParent();
            DefaultGraphCell target = (DefaultGraphCell) ((DefaultPort) edge.getTarget()).getParent();
            int source_layer = (Integer) source.getAttributes().get("layer");
            int taget_layer = (Integer) target.getAttributes().get("layer");
            LinkedList dummyList = new LinkedList();
            for (int j = source_layer - 1; j > taget_layer; j--) {
                Point2D.Double dummyPoint = new Point.Double(100, 100);
                dummyList.add(dummyPoint);
            }

            edge.getAttributes().put("dummyList", dummyList);
            nested = new Hashtable();
            nested.put(edge, edge.getAttributes());
            graph.getGraphLayoutCache().edit(nested, null, null, null);
        }

    } // end layerAssignment

    private void test() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i <
                    nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                LinkedList<Node> targetList = node.targetList;
                int top = layer;
                for (int j = 0; j <
                        targetList.size(); j++) {
                    Node nextNode = targetList.get(j);
                    while (nextNode.dummy) {
                        if (nextNode.dummyPoint.y < layer) {
                            System.out.println("hello world");
                        }

                        nextNode = nextNode.targetList.get(0);
                    }

                }
            }
        }
    }

    /** build data structure to be used for crossing reduction algorithm */
    private void buildLayerListCR() {
        // initiate layerListCR with real states
        layerListCR = new LinkedList<LinkedList<Node>>();
        nodeMap.clear();
        for (int layer = 0; layer < layerList.size(); layer++) {
            layerListCR.add(new LinkedList<Node>());
            for (int j = 0; j < layerList.get(layer).size(); j++) {
                DefaultGraphCell state = (DefaultGraphCell) layerList.get(layer).get(j);
                Node node = new Node(false); //not a dummy state
                node.state = state;
                node.level = layer;
                nodeMap.put(state, node);
                layerListCR.get(layer).add(node);
            }
        }

        // link nodes in layerListCR, add dummy nodes for long edges
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            for (int j = 0; j < layerListCR.get(layer).size(); j++) {
                Node node = layerListCR.get(layer).get(j);
                // neglect dummy nodes
                if (node.dummy) {
                    continue;
                }
                DefaultGraphCell state = node.state;
                Iterator edges = ((DefaultPort) state.getFirstChild()).getEdges().iterator();
                while (edges.hasNext()) {
                    DefaultEdge edge = (DefaultEdge) edges.next();
//					DefaultGraphCell sourceCell = (DefaultGraphCell) ((DefaultPort) edge.getSource()).getParent();
                    DefaultGraphCell targetCell = (DefaultGraphCell) ((DefaultPort) edge.getTarget()).getParent();
                    Node targetNode = nodeMap.get(targetCell);

                    // neglect transitions connected TO current node
                    if (targetCell == state) {
                        continue;
                    }

                    //connect short edges
                    if (targetNode.level == layer + 1) {
                        if (!node.targetList.contains(targetNode)) {
                            node.targetList.add(targetNode);
                            targetNode.sourceList.add(node);
                        }
                        //build dummy nodes and connect long edges
                    } else if (targetNode.level > layer + 1) {
                        Vector<Point2D.Double> dummyList = new Vector<Point2D.Double>();
                        int curLayer = layer + 1;
                        Node lastNode = node;
                        while (curLayer < targetNode.level) {
                            Node newNode = new Node(true); //dummy node
                            dummyList.add(newNode.dummyPoint);
                            newNode.level = curLayer;
                            layerListCR.get(curLayer).add(newNode);
                            lastNode.targetList.add(newNode);
                            newNode.sourceList.add(lastNode);
                            if (curLayer == targetNode.level - 1) {
                                newNode.targetList.add(targetNode);
                                targetNode.sourceList.add(newNode);
                            }
                            curLayer++;
                            lastNode = newNode;
                        }
                        dummyListMap.put(edge, dummyList);
                    } else {
                        System.out.println("invalid node connection encountered");
                    }
                }
//                LinkedList<Node> test = node.sourceList;
//                int count = 0;
//                for (int t = 0; t < test.size(); t++) {
//                    count++;
//                    System.out.println("Number " + count + " :");
//                    Node tmp = test.get(t);
//                    System.out.println("Debug soure node level: " +
//                            Integer.toString(tmp.level) + "order: " +
//                            Integer.toString(tmp.order));
//                    System.out.println("Debug target node level: " +
//                            Integer.toString(node.level) + "order: " +
//                            Integer.toString(node.order));
//                }
            }
        } // end "link nodes in layerListCR, add dummy nodes for long edges"
    } // end buildLayerListCR

    /** count number of dummy nodes */
    private void countDummyNodes() {
        int dummy = 0;
        int notDummy = 0;
        int maxChain = 1;
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                if (node.dummy) {
                    dummy++;
                    Node nextNode = node.targetList.get(0);
                    int chain = 1;
                    while (nextNode.dummy) {
                        chain++;
                        nextNode = nextNode.targetList.get(0);
                    }
                    if (chain > maxChain) {
                        maxChain = chain;
                    }
                } else {
                    notDummy++;
                }
            }
        }
        System.out.println("dummy: " + Integer.toString(dummy) +
                "    not dummy: " + Integer.toString(notDummy) +
                "    max chain: " + Integer.toString(maxChain));
    }

    /** executes global sifting crossing reduction */
    private void globalSiftingXR() {
        Vector<Block> beta = buildBlocks();
        for (int repeat = 0; repeat < 10; repeat++) {
            //block index is decided when beta is built
            for (int i = 0; i < beta.size(); i++) {
                Block curBlock = idToBlockH.get(i);
                siftingStep(beta, curBlock);
            }
        }
        orderLayerListCR(); //by pi value
//		assignPiValue();	//x-coord decided by pi values
//		xCoordCompaction();	//nodes are aligned to the left
    }

    /** sifting step in global sifting crossing reduction */
    private void siftingStep(Vector<Block> beta, Block blockA) {
        // create new ordering beta with blockA put to front
        beta.remove(blockA);
        beta.add(0, blockA);
        updatePiValue(beta);

//		sortAdjacencies(beta);
        sortLinkedList();

        int chi = 0;
        int chiStar = 0;
        int pStar = 0;
        //possibility of off-by-one error
        for (int p = 1; p < beta.size(); p++) {
            chi = chi + siftingSwap(beta, blockA, beta.get(p));
            if (chi < chiStar) {
                chiStar = chi;
                pStar = p;
            }
            //add code block below to allow randomization
//			else if (chi == chiStar) {
//				Random gen = new Random();
//				int num = gen.nextInt(1000);
//				if (num > 500) {
//					chiStar = chi;
//					pStar = p;
//				}
//			}
        }
        // new ordering beta with blockA placed after beta(p*)
        beta.remove(blockA);
        beta.add(pStar, blockA);
        updatePiValue(beta);
    }

    /** update pi value */
    private void updatePiValue(Vector<Block> beta) {
        for (int i = 0; i < beta.size(); i++) {
            Block block = beta.get(i);
            block.piValue = i;
        }
    }

    /** sort ordering of targetList and sourceList of all nodes by pi values */
    private void sortLinkedList() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> currentLayer = layerListCR.get(layer);
            for (int j = 0; j < currentLayer.size(); j++) {
                Node node = currentLayer.get(j);
                //sourceList
                Hashtable<Integer, Node> piToNodeH = new Hashtable<Integer, Node>();
                Vector<Integer> piArray = new Vector<Integer>();
                for (int k = 0; k < node.sourceList.size(); k++) {
                    Node linkedNode = node.sourceList.get(k);
                    Block linkedBlock = nodeToBlockH.get(linkedNode);
                    piToNodeH.put(linkedBlock.piValue, linkedNode);
                    piArray.add(linkedBlock.piValue);
                }
                Collections.sort(piArray);
                LinkedList newOrdering = new LinkedList();
                for (int k = 0; k < piArray.size(); k++) {
                    Node linkedNode = piToNodeH.get(piArray.get(k));
                    newOrdering.add(linkedNode);
                }
                node.sourceList = newOrdering;

                //targetList
                piToNodeH = new Hashtable<Integer, Node>();
                piArray = new Vector<Integer>();
                for (int k = 0; k < node.targetList.size(); k++) {
                    Node linkedNode = node.targetList.get(k);
                    Block linkedBlock = nodeToBlockH.get(linkedNode);
                    piToNodeH.put(linkedBlock.piValue, linkedNode);
                    piArray.add(linkedBlock.piValue);
                }
                Collections.sort(piArray);
                newOrdering = new LinkedList();
                for (int k = 0; k < piArray.size(); k++) {
                    Node linkedNode = piToNodeH.get(piArray.get(k));
                    newOrdering.add(linkedNode);
                }
                node.targetList = newOrdering;
            }
        }
    }

    /**
     * swap entries incident to common neighbors
     * @param a node a
     * @param b node b
     * @param sign sign
     */
    private void updateLinkedList(Node a, Node b, Sign sign) {
        int i = 0;
        int j = 0;
        LinkedList<Node> x = new LinkedList<Node>();
        LinkedList<Node> y = new LinkedList<Node>();
        if (sign == Sign.PLUS) { //x and y connect to some node(s)
            x = a.targetList;
            y = b.targetList;
        } else { // x and y connected from some node(s)
            x = a.sourceList;
            y = b.sourceList;
        }
        int r = x.size();
        int s = y.size();

        while (i < r && j < s) {
            int piXi = nodeToBlockH.get(x.get(i)).piValue;
            int piYj = nodeToBlockH.get(y.get(j)).piValue;
            if (piXi < piYj) {
                i++;
            } else if (piXi > piYj) {
                j++;
            } else {
                Node z = x.get(i);
                if (z != y.get(j)) {
                    System.out.println("x[i] != y[j]");
                }
                //x and y connect to same node z
                if (sign == Sign.PLUS) {
                    int index = z.sourceList.indexOf(b);
                    z.sourceList.set(index, a);
                    z.sourceList.set(index - 1, b);
                } else {
                    int index = z.targetList.indexOf(b);
                    z.targetList.set(index, a);
                    z.targetList.set(index - 1, b);
                }
                i++;
                j++;
            }
        }
    }

    /**
     * build N/I adjacency arrays
     * @param beta list of blocks
     */
    private void sortAdjacencies(Vector<Block> beta) {
        //clear N+/- and I+/- arrays for all nodes
        for (int i = 0; i < beta.size(); i++) {
            Block block = beta.get(i);
            block.piValue = i;
            for (int j = 0; j < block.nodes.size(); j++) {
                Node node = block.nodes.get(j);
                node.nMinus = new Vector<Node>();
//				node.iMinus = new Vector<Integer>();
                node.nPlus = new Vector<Node>();
//				node.iPlus = new Vector<Integer>();
            }

        }

        //build N+/- array for nodes in every block
        for (int i = 0; i < beta.size(); i++) {
            Block block = beta.get(i);
            //build N+/- array for every node in block
            for (int j = 0; j < block.nodes.size(); j++) {
                Node node = block.nodes.get(j);
                // N+ array
                LinkedList<Node> targetList = node.targetList;
                for (int k = 0; k < targetList.size(); k++) {
                    Node targetNode = targetList.get(k);
                    node.nPlus.add(targetNode);
                }
// N- array

                LinkedList<Node> sourceList = node.sourceList;
                for (int k = 0; k < sourceList.size(); k++) {
                    Node sourceNode = sourceList.get(k);
                    node.nMinus.add(sourceNode);
                }

            }
            //sort N+ array for lower, also init I+
            Node lower = block.getLower();
            Hashtable<Integer, Node> piToNode = new Hashtable<Integer, Node>();
            Vector<Integer> nodeOrdering = new Vector<Integer>();
            for (int j = 0; j < lower.nPlus.size(); j++) {
                Node connectedNode = lower.nPlus.get(j);
                int pi = nodeToBlockH.get(connectedNode).piValue;
                piToNode.put(pi, connectedNode);
                nodeOrdering.add(pi);
            }

            Collections.sort(nodeOrdering);
            lower.nPlus = new Vector<Node>();
            lower.iPlus = new Vector<Integer>();
            for (int j = 0; j < nodeOrdering.size(); j++) {
                int pi = nodeOrdering.get(j);
                Node connectedNode = piToNode.get(pi);
                lower.nPlus.add(connectedNode);
                lower.iPlus.add(0);
            }
//sort N- array for upper, also init I-

            Node upper = block.getUpper();
            piToNode = new Hashtable<Integer, Node>();
            nodeOrdering = new Vector<Integer>();
            for (int j = 0; j < upper.nMinus.size(); j++) {
                Node connectedNode = upper.nMinus.get(j);
                int pi = nodeToBlockH.get(connectedNode).piValue;
                piToNode.put(pi, connectedNode);
                nodeOrdering.add(pi);
            }

            Collections.sort(nodeOrdering);
            upper.nMinus = new Vector<Node>();
            upper.iMinus = new Vector<Integer>();
            for (int j = 0; j < nodeOrdering.size(); j++) {
                int pi = nodeOrdering.get(j);
                Node connectedNode = piToNode.get(pi);
                upper.nMinus.add(connectedNode);
                upper.iMinus.add(0);
            }

        }
        //build I+/- array for outer nodes, assume inner nodes don't need it
        for (int i = 0; i < beta.size(); i++) {
            Block block = beta.get(i);
            // I- array
            Node lower = block.getLower();
            for (int index = 0; index < lower.nPlus.size(); index++) {
                Node connectedNode = lower.nPlus.get(index);
                int location = connectedNode.nMinus.indexOf(lower);
                connectedNode.iMinus.set(location, index);
            }
// I+ array

            Node upper = block.getUpper();
            for (int index = 0; index < upper.nMinus.size(); index++) {
                Node connectedNode = upper.nMinus.get(index);
                int location = connectedNode.nPlus.indexOf(upper);
                connectedNode.iPlus.set(location, index);
            }

        }
    } // end sortAdjacencies

    /**
     * sifting swap step in global sifting crossing reduction
     * @param beta list of blocks
     * @param blockA block A
     * @param blockB block B
     * @return delta value
     */
    private int siftingSwap(Vector<Block> beta, Block blockA, Block blockB) {
        //using 4 if statements to replace the set structure L
        int upperA = blockA.getUpper().level;
        int lowerA = blockA.getLower().level;
        int upperB = blockB.getUpper().level;
        int lowerB = blockB.getLower().level;
        int delta = 0;

        //phi(upperA) in levels(B) :: phi(upperA), -
        if (upperA >= upperB && upperA <= lowerB) {
            Node a = blockA.getUpper();
            Node b = blockB.getNodeAtLayer(upperA);
//			delta = delta + uswap(a.nMinus, b.nMinus);
//			updateAdjacencies(a.nMinus, a.iMinus, b.nMinus, b.iMinus, Sign.MINUS);
            delta += uswapNew(a, b, Sign.MINUS);
            updateLinkedList(a, b, Sign.MINUS);
        }

        //phi(lowerA) in levels(B) :: phi(lowerA), +
        if (lowerA >= upperB && lowerA <= lowerB) {
            Node a = blockA.getLower();
            Node b = blockB.getNodeAtLayer(lowerA);
//			delta = delta + uswap(a.nPlus, b.nPlus);
//			updateAdjacencies(a.nPlus, a.iPlus, b.nPlus, b.iPlus, Sign.PLUS);
            delta += uswapNew(a, b, Sign.PLUS);
            updateLinkedList(a, b, Sign.PLUS);
        }

        //phi(upperB) in levels(B) :: phi(upperB), -
        if (upperB >= upperA && upperB <= lowerA && upperB != upperA) {
            Node a = blockA.getNodeAtLayer(upperB);
            Node b = blockB.getUpper();
//			delta = delta + uswap(a.nMinus, b.nMinus);
//			updateAdjacencies(a.nMinus, a.iMinus, b.nMinus, b.iMinus, Sign.MINUS);
            delta += uswapNew(a, b, Sign.MINUS);
            updateLinkedList(a, b, Sign.MINUS);
        }

        //phi(lowerB) in levels(B) :: phi(lowerB), +
        if (lowerB >= upperA && lowerB <= lowerA && lowerB != lowerA) {
            Node a = blockA.getNodeAtLayer(lowerB);
            Node b = blockB.getLower();
//			delta = delta + uswap(a.nPlus, b.nPlus);
//			updateAdjacencies(a.nPlus, a.iPlus, b.nPlus, b.iPlus, Sign.PLUS);
            delta += uswapNew(a, b, Sign.PLUS);
            updateLinkedList(a, b, Sign.PLUS);
        }

        //swap positions of A and B in beta, they should be consecutive blocks
        int index = beta.indexOf(blockA);
        beta.set(index, blockB);
        beta.set(index + 1, blockA);
        if (blockA.piValue != blockB.piValue - 1) {
            System.out.println("unmatched pi value for adjacent blocks");
        }
        blockA.piValue++;
        blockB.piValue--;

        return delta;
    }

    /**
     * uswap step in global sifting crossing reduction
     * @param a node a
     * @param b node b
     * @param sign sign
     * @return c value
     */
    private int uswapNew(Node a, Node b, Sign sign) {
        LinkedList<Node> x = new LinkedList<Node>();
        LinkedList<Node> y = new LinkedList<Node>();
        if (sign == Sign.PLUS) {
            x = a.targetList;
            y = b.targetList;
        } else {
            x = a.sourceList;
            y = b.sourceList;
        }
        int r = x.size();
        int s = y.size();
        int c = 0;
        int i = 0;
        int j = 0;
        while (i < r && j < s) {
            int piBlockXi = nodeToBlockH.get(x.get(i)).piValue;
            int piBlockYj = nodeToBlockH.get(y.get(j)).piValue;
            if (piBlockXi < piBlockYj) {
                c = c + (s - j);
                i++;

            } else if (piBlockXi > piBlockYj) {
                c = c - (r - i);
                j++;

            } else {
                c = c + (s - j) - (r - i);
                i++;
                j++;
            }
        }
        return c;
    }


//    private int uswap(Vector<Node> x, Vector<Node> y) {
//        int r = x.size();
//        int s = y.size();
//        int c = 0;
//        int i = 0;
//        int j = 0;
//        while (i < r && j < s) {
//            int piBlockXi = nodeToBlockH.get(x.get(i)).piValue;
//            int piBlockYj = nodeToBlockH.get(y.get(j)).piValue;
//            if (piBlockXi < piBlockYj) {
//                c = c + (s - j);
//                i++;
//
//            } else if (piBlockXi > piBlockYj) {
//                c = c - (r - i);
//                j++;
//
//            } else {
//                c = c + (s - j) - (r - i);
//                i++;
//                j++;
//            }
//        }
//        return c;
//    }

//    private void updateAdjacencies(Vector<Node> x, Vector<Integer> Ia,
//            Vector<Node> y, Vector<Integer> Ib, Sign sign) {
//        int r = x.size();
//        int s = y.size();
//        int i = 0;
//        int j = 0;
//        while (i < r && j < s) {
//            int piBlockXi = nodeToBlockH.get(x.get(i)).piValue;
//            int piBlockYj = nodeToBlockH.get(y.get(j)).piValue;
//            if (piBlockXi < piBlockYj) {
//                i++;
//            } else if (piBlockXi > piBlockYj) {
//                j++;
//            } else {
//                Node z = x.get(i);
//                Vector<Node> Nz = null;
//                Vector<Integer> Iz = null;
//                //signs are reversed
//                switch (sign) {
//                    case PLUS:
//                        Nz = z.nMinus;
//                        Iz = z.iMinus;
//                        break;
//                    case MINUS:
//                        Nz = z.nPlus;
//                        Iz = z.iPlus;
//                        break;
//                }
//                //swap entries in N-d(z) and I-d(z)
//
//                int iai = Ia.get(i);
//                int ibj = Ib.get(j);
//                if (iai >= Nz.size() || ibj >= Nz.size()) {
//                    System.out.println("Nz bound error");
//                    i++;
//                    j++;
//                    continue;
//                }
//
//                Node tempNode = Nz.get(iai);
//                Nz.set(iai, Nz.get(ibj));
//                Nz.set(ibj, tempNode);
//                int tempInt = Iz.get(iai);
//                Iz.set(iai, Iz.get(ibj));
//                Iz.set(ibj, tempInt);
//
//                Ia.set(i, iai + 1);
//                Ib.set(j, ibj - 1);
//                i++;
//                j++;
//            }
//        } // end while (i < r && j < s)
//    } // end updateAdjacencies

    /** direction of transition, up or down */
    private enum Sign {

        PLUS, MINUS
    }

    /** set visited flag of all nodes to false */
    private void clearVisited() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                node.visited = false;
            }
        }
    }

    /** initialize blocks */
    private Vector<Block> buildBlocks() {
        clearVisited();
        nodeToBlockH = new Hashtable<Node, Block>();
        idToBlockH = new Hashtable<Integer, Block>();
        Vector<Block> beta = new Vector<Block>();
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                if (node.visited) {
                    continue;
                } else {
                    node.visited = true;
                }

                Block block = new Block();
                if (node.dummy) {
                    //add connected dummy nodes as one block
                    block.nodes.add(node);
                    nodeToBlockH.put(node, block);
                    Node nextNode = (Node) node.targetList.get(0);
                    while (nextNode.dummy) {
                        nextNode.visited = true;
                        block.nodes.add(nextNode);
                        nodeToBlockH.put(nextNode, block);
                        nextNode = (Node) nextNode.targetList.get(0);
                    }
                } else {
                    block.nodes.add(node);
                    nodeToBlockH.put(node, block);
                }

                beta.add(block);
                int index = beta.indexOf(block);
                block.id = index;
                block.piValue = index;
                idToBlockH.put(index, block);
            }
        }
        return beta;
    } // end buildBlocks

    /** order nodes in each layer of layerListCR by increasing pi value
     * used for global sifting postprocessing
     */
    private void orderLayerListCR() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> currentLayer = layerListCR.get(layer);
            Hashtable<Integer, Node> piToNodeH = new Hashtable<Integer, Node>();
            Vector<Integer> piOrdering = new Vector<Integer>();
            for (int i = 0; i < currentLayer.size(); i++) {
                Node node = currentLayer.get(i);
                int pi = nodeToBlockH.get(node).piValue;
                piToNodeH.put(pi, node);
                piOrdering.add(pi);
            }

            Collections.sort(piOrdering);
            LinkedList<Node> newOrdering = new LinkedList<Node>();
            for (int i = 0; i < piOrdering.size(); i++) {
                int pi = piOrdering.get(i);
                Node node = piToNodeH.get(pi);
                newOrdering.add(node);
            }
            layerListCR.set(layer, newOrdering);
        }
    }

    /** barycenter crossing reduction */
    private void barycenterXR() {
        // init first level with position
        LinkedList<Node> seedLayer = (LinkedList) layerListCR.get(0);
        Iterator<Node> nodes = seedLayer.iterator();
        double xPos = 0;
        while (nodes.hasNext()) {
            Node node = nodes.next();
            node.xCoordinate = xPos++;
        }
//barycenter DOWN, UP scan repeated N times followed by a DOWN scan

        if (layerListCR.size() > 1) {
            for (int repeat = 0; repeat < 10; repeat++) {
                for (int j = 1; j < layerListCR.size(); j++) {
                    barycenterSweep(j, Direction.DOWN);
                }

                shuffleLayerListCR();
                for (int j = layerListCR.size() - 2; j >= 0; j--) {
                    barycenterSweep(j, Direction.UP);
                }
                shuffleLayerListCR();
            }

            for (int j = 1; j < layerListCR.size(); j++) {
                barycenterSweep(j, Direction.DOWN);
            }
        }

    } // end barycenterXR

    /** inner data type for direction of transitions */
    private enum Direction {

        UP, DOWN
    }

    /**
     * execute barycenter on one layer
     * @param layer layer of nodes to work on
     * @param dir direction of sweep
     */
    private void barycenterSweep(int layer, Direction dir) {
        HashSet<Double> existPos = new HashSet<Double>();
        LinkedList<Node> nodeLayer = layerListCR.get(layer);
        for (int i = 0; i <
                nodeLayer.size(); i++) {
            Node node = nodeLayer.get(i);
            double xPos = 0;

            //build list of connected nodes
            LinkedList<Node> sourceList = null;
            switch (dir) {
                case DOWN:
                    sourceList = node.sourceList;
                    break;

                case UP:
                    sourceList = node.targetList;
                    break;

            }

            //find barycenter of current node

            for (int j = 0; j < sourceList.size(); j++) {
                Node source = sourceList.get(j);
                xPos += source.xCoordinate;
            }

            if (sourceList.size() == 0) {
                xPos = 1;
            } else {
                xPos = xPos / sourceList.size();
            }

            while (existPos.contains(xPos)) {
                //increment by one destroys XR
                xPos = xPos + 0.01;
            }

            assignPseudoPosition(node, xPos, layer);
            existPos.add(xPos);
        }

    }

    /**
     * assign temporary position for nodes
     * @param node node to be assigned position
     * @param xPos position along x-axis
     * @param layer layer of to be assigned to
     */
    private void assignPseudoPosition(Node node, double xPos, int layer) {
        node.xCoordinate = xPos;
        if (node.dummy) {
            //"JGraphController.stateSize / 2" aligns dummy node with real nodes
            node.dummyPoint.x = offset + xPos * 100 + JGraphController.stateSize / 2;
            node.dummyPoint.y = offset + layer * 100 + JGraphController.stateSize / 2;
        }

    }

    /** assign position according to pi value from global sifting */
    private void assignPiValue() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                Block block = nodeToBlockH.get(node);
                int blockPos = block.piValue;
                assignPseudoPosition(node, blockPos, layer);
            }
        }
    }

    /** asign position to nodes according to ordering of layerListCR */
    private void xCoordCompaction() {
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                assignPseudoPosition(node, i, layer);
            }
        }
    }

    /**
     * STAGE 4 : Coordinate assignment
     */
    private void xCoordinateAssignment() {
        // Find the layer with the most nodes -> maxLayer
        int maxLayer = 0, max = 0;
        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> list = layerListCR.get(i);
            if (list.size() > max) {
                max = list.size();
                maxLayer = i;
            }

        }

        //assign horizontal position to nodes in maxLayer
        int pos = 1;
        List maxLayerList = layerListCR.get(maxLayer);
        for (int i = 0; i < maxLayerList.size(); i++) {
            Node node = (Node) maxLayerList.get(i);
            if (node.dummy) {
                node.dummyPoint.x = offset + i * offset + JGraphController.stateSize / 2;
                node.dummyPoint.y = offset + maxLayer * offset + JGraphController.stateSize / 2;
                node.xCoordinate = i;
            } else {
                node.xCoordinate = i;
            }

        }

        //assign horizontal position, UP SCAN
        for (int i = maxLayer - 1; i >= 0; i--) {
            HashSet<Double> existPos = new HashSet<Double>();
            List list = layerListCR.get(i);
            HashSet s = new HashSet();
            for (int j = 0; j < list.size(); j++) {
                Node node = (Node) list.get(j);
                List<Node> targetList = node.targetList;
                double xPos = 0;
                for (int k = 0; k < targetList.size(); k++) {
                    xPos += ((Node) targetList.get(k)).xCoordinate;
                }

                if (targetList.size() == 0) {
                    xPos = 1;
                } else {
                    xPos = (xPos / targetList.size());
                    xPos = xPos - xPos % 1;
                }

                while (existPos.contains(xPos)) {
                    int step = 1;
                    /*if (Math.random() > 0.5) {
                    step = -1;
                    }*/
                    xPos = xPos + step;
                }

                existPos.add(xPos);
                node.xCoordinate = xPos;
                if (node.dummy) {
                    node.dummyPoint.x = offset + xPos * 100 + JGraphController.stateSize / 2;
                    node.dummyPoint.y = offset + i * 100 + JGraphController.stateSize / 2;
                }

            }
        }

        //assign horizontal position, DOWN SCAN
        for (int i = maxLayer + 1; i < layerListCR.size(); i++) {
            HashSet<Double> existPos = new HashSet<Double>();
            List list = layerListCR.get(i);
            HashSet s = new HashSet();
            for (int j = 0; j < list.size(); j++) {
                Node node = (Node) list.get(j);
                List<Node> sourceList = node.sourceList;
                double xPos = 0;
                for (int k = 0; k < sourceList.size(); k++) {
                    xPos += ((Node) sourceList.get(k)).xCoordinate;
                }

                if (sourceList.size() == 0) {
                    xPos = 1;
                } else {
                    xPos = (xPos / sourceList.size());
                    xPos = xPos - xPos % 1;
                }

                int step = 1;
                if (Math.random() > 0.5) {
                    step = -1;
                }

                while (existPos.contains(xPos)) {

                    xPos = xPos + step;
                    if (xPos < 0) {
                        xPos = Math.random() * max;
                    }

                }
                existPos.add(xPos);
                node.xCoordinate = xPos;
                if (node.dummy) {
                    node.dummyPoint.x = offset + xPos * 100 + JGraphController.stateSize / 2;
                    node.dummyPoint.y = offset + i * 100 + JGraphController.stateSize / 2;
                }

            }
        }
    } // end xCoordinateAssignment

//    private void horizontalDeCompaction() {
//        alignInnerSegments();
//        sortLayerListCR();
//
//        for (int layer = 0; layer < layerListCR.size(); layer++) {
//            LinkedList<Node> nodeLayer = layerListCR.get(layer);
//            for (int i = 0; i < nodeLayer.size() - 1; i++) {
//                Node node = nodeLayer.get(i);
//                Node nextNode = nodeLayer.get(i + 1);
//                Double difference = nextNode.xCoordinate - node.xCoordinate;
//                if (difference < X_POS_MARGIN) {
//                    moveNodesRight(nextNode.xCoordinate, X_POS_MARGIN - difference);
//                }
////				System.out.println("hello world");
//
//            }
//        }
//    }

//    private void alignInnerSegments() {
//        //unmark all nodes
//        for (int layer = 0; layer <
//                layerListCR.size(); layer++) {
//            LinkedList<Node> nodeLayer = layerListCR.get(layer);
//            for (int i = 0; i < nodeLayer.size(); i++) {
//                Node node = nodeLayer.get(i);
//                node.visited = false;
//            }
//
//        }
//        //straighten inner segments
//        for (int layer = 0; layer < layerListCR.size(); layer++) {
//            LinkedList<Node> nodeLayer = layerListCR.get(layer);
//            for (int i = 0; i < nodeLayer.size(); i++) {
//                Node node = nodeLayer.get(i);
//                if (node.dummy && !node.visited) {
//                    //obtain average xPos of inner segments
//                    Double xPos = 0.0;
//                    int count = 1;
//                    xPos += node.xCoordinate;
//                    Node nextNode = node.targetList.get(0);
//                    while (nextNode.dummy) {
//                        count++;
//                        xPos += nextNode.xCoordinate;
//                        nextNode = nextNode.targetList.get(0);
//                    }
//
//                    xPos = xPos / count;
//                    //assign average xPos to inner segments
//                    if (count > 1) {
//                        assignPseudoPosition(node, xPos, layer);
//                        int currentLayer = layer;
//                        nextNode = node.targetList.get(0);
//                        while (nextNode.dummy) {
//                            currentLayer++;
//                            assignPseudoPosition(nextNode, xPos, currentLayer);
//                            nextNode = nextNode.targetList.get(0);
//                        }
//
//                    }
//                }
//            }
//        }
//    }

//    //move all nodes with xPos >= breakpoint to the right by increment amount
//    private void moveNodesRight(Double breakPoint, Double increment) {
//        for (int layer = 0; layer < layerListCR.size(); layer++) {
//            LinkedList<Node> nodeLayer = layerListCR.get(layer);
//            for (int i = 0; i < nodeLayer.size(); i++) {
//                Node node = nodeLayer.get(i);
//                if (node.xCoordinate >= breakPoint) {
//                    Double newPos = node.xCoordinate + increment;
//                    assignPseudoPosition(node, newPos, layer);
//                }
//
//            }
//        }
//    }
//    //position nodes according to their relative positioning

//    private void lazyCoodinateAssignment() {
//        //** create layerListCR ordered by xPosition value
////		for (int i = 0; i < layerListCR.size(); i++) {
////			Collections.sort(layerListCR.get(i));
////		}
//        LinkedList<LinkedList<Node>> newLayerList = new LinkedList<LinkedList<Node>>();
//        for (int layer = 0; layer < layerListCR.size(); layer++) {
//            LinkedList<Node> oldLayer = layerListCR.get(layer);
//            HashMap<Double, Node> mapping = new HashMap<Double, Node>();
//            LinkedList<Double> ordering = new LinkedList<Double>();
//            for (int i = 0; i < oldLayer.size(); i++) {
//                //assume no xPos collision
//                double xPos = oldLayer.get(i).xCoordinate;
//                mapping.put(xPos, oldLayer.get(i));
//                ordering.add(xPos);
//            }
//
//            Collections.sort(ordering);
//            LinkedList<Node> newLayer = new LinkedList<Node>();
//            for (int i = 0; i < ordering.size(); i++) {
//                double xPos = ordering.get(i);
//                Node node = mapping.get(xPos);
//                newLayer.add(node);
//            }
//
//            newLayerList.add(newLayer);
//        }
//
//        layerListCR = newLayerList;
//
//        //** assign position
//        for (int layer = 0; layer < layerListCR.size(); layer++) {
//            LinkedList<Node> nodeLayer = layerListCR.get(layer);
//            for (int nodeRank = 0; nodeRank < nodeLayer.size(); nodeRank++) {
//                Node node = (Node) nodeLayer.get(nodeRank);
//                assignPseudoPosition(node, nodeRank, layer);
//            }
//
//        }
//    }

    /** random shuffle on link ordering of nodes in each node layer in layerListCR */
    private void shuffleLayerListCR() {
        Random generator = new Random();
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            LinkedList<Node> newLayer = new LinkedList<Node>();
            while (nodeLayer.size() > 0) {
                int roll = generator.nextInt(nodeLayer.size());
                Node node = nodeLayer.get(roll);
                nodeLayer.remove(roll);
                newLayer.add(node);
            }

            layerListCR.set(layer, newLayer);
        }

    }

    /** sort nodes in each layer ascending order of xCoordinate value */
    private void sortLayerListCR() {
        //create layerListCR out of layerList
        for (int layer = 0; layer < layerListCR.size(); layer++) {
            LinkedList<Node> nodeLayer = layerListCR.get(layer);
            Vector<Double> xPosV = new Vector<Double>();
            Hashtable<Double, Node> xPosToNodeH = new Hashtable<Double, Node>();

            //create vector of xCoordinate positions
            for (int i = 0; i < nodeLayer.size(); i++) {
                Node node = nodeLayer.get(i);
                xPosV.add(node.xCoordinate);
                xPosToNodeH.put(node.xCoordinate, node);
            }

            Collections.sort(xPosV);
            //create new node list sorted by xPos
            LinkedList<Node> newNodeLayer = new LinkedList<Node>();
            for (int i = 0; i < xPosV.size(); i++) {
                Double xPos = xPosV.get(i);
                Node node = xPosToNodeH.get(xPos);
                newNodeLayer.add(node);
            }

            layerListCR.set(layer, newNodeLayer);
        }

    }

    /** update dummy node position */
    private void updateDummyNodePosition() {
        Iterator<DefaultEdge> transitions = dummyListMap.keySet().iterator();
        while (transitions.hasNext()) {
            DefaultEdge edge = transitions.next();
            List dummy = dummyListMap.get(edge);

            dummy.add(dummy.size(), new Point2D.Double());
            dummy.add(0, new Point2D.Double());

            GraphConstants.setPoints(edge.getAttributes(), dummy);
            Map nested = new Hashtable();
            nested.put(edge, edge.getAttributes());
            graph.getGraphLayoutCache().edit(nested, null, null, null);
        }

    }

    /** update graph to reflect data changes */
    private void draw() {

        updateDummyNodePosition();


        // Let the reversed trantion to be back
        Iterator<DefaultEdge> edges = transitionMap.values().iterator();
        while (edges.hasNext()) {
            DefaultEdge edge = (DefaultEdge) edges.next();

            if (edge.getSource() == null || edge.getTarget() == null) {
                continue;
            }

            Boolean reversed = (Boolean) edge.getAttributes().get("reversed");
            if (reversed == Boolean.TRUE) {

                List points = dummyListMap.get(edge);
                if (points != null && points.size() > 2) {
                    Iterator<Point2D> pp = points.iterator();
                    List revserPoints = new ArrayList();

                    while (pp.hasNext()) {
                        revserPoints.add(0, pp.next());
                    }

                    GraphConstants.setPoints(edge.getAttributes(), revserPoints);
                }

                DefaultPort temp = (DefaultPort) edge.getSource();
                edge.setSource(((DefaultPort) edge.getTarget()));
                edge.setTarget(temp);

                Map nested = new Hashtable();
                nested.put(edge, edge.getAttributes());
                graph.getGraphLayoutCache().edit(nested);


            }

        }
        /*
         * Draw the layered layout graph
         */
        double height = 0;
        double level = 0;
        for (int i = 0; i < layerListCR.size(); i++) { //for each
            LinkedList list = (LinkedList) layerListCR.get(i);//from the last layer
            for (int j = 0; j < list.size(); j++) {
                Node node = (Node) list.get(j);
                if (!node.dummy) {
                    DefaultGraphCell state = (DefaultGraphCell) (node.state);
                    //(Integer) state.getAttributes().get("id")
                    jgc.setStatePosition((Integer) state.getAttributes().get("id"), offset + offset * node.xCoordinate, offset + i * offset);
                }

            }
        }
    } // end draw

//    /**
//     * returns width
//     * @return width
//     */
//    private int getWidth() {
//        return width;
//    }
//
//    /**
//     * set width
//     */
//    private void setWidth(int width) {
//        this.width = width;
//    }
//
//    private double getPosHash(int x, int y) {
//        return 10000 * x + y;
//    }

    private void placeBlock(Node v) {
        if (v.x == -1) {
            v.x = 0;
            Node w = v;
            do {
                int layer = w.level;
                LinkedList<Node> nodes = layerListCR.get(layer);
                int posW = nodes.indexOf(w);
                if (posW > 0) {
                    Node u = nodes.get(posW - 1).root;
                    placeBlock(u);
                    if (v.sink == v) {
                        v.sink = u.sink;
                    }

                    if (v.sink != u.sink) {
                        if (v.x - u.x - minimumSeparation < u.sink.shift) {
                            u.sink.shift = v.x - u.x - minimumSeparation;
                        }

                    } else {
                        if (u.x + minimumSeparation > v.x) {
                            v.x = u.x + minimumSeparation;
                        }

                    }

                }
                w = w.align;
            } while (w != v);
        }

    }

    /** reduce space between nodes on the same horizontal layer */
    private void horizontalCompaction() {
        // Root coordinates relative to sink
        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> layer = layerListCR.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Node v = layer.get(j);
                if (v.root == v) {
                    placeBlock(v);
                }
            }
        }

        // Absolute coordinates
        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> layer = layerListCR.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Node v = layer.get(j);
                v.x = v.root.x;
                if (v.root.sink.shift < Double.MAX_VALUE) {
                    v.x = v.x + v.root.sink.shift;
                }

            }
        }
    }

    /** initilization */
    private void preprocessing() {
        segmentMarked = new HashSet();
        for (int i = 1; i < layerListCR.size() - 2; i++) {
            int k0 = -1;
            int l = 0;
            int liplus1 = layerListCR.get(i + 1).size();
            for (int l1 = 0; l1 < liplus1; l1++) {
                boolean isInner = false;
                Node parent = null;
                Node node = layerListCR.get(i + 1).get(l1);
                if (node.dummy) {
                    Iterator<Node> it = node.sourceList.iterator();
//                    LinkedList<Node> test = node.sourceList;
//                    for (int t = 0; t < test.size(); t++) {
//                        Node tmp = test.get(t);
//                        System.out.println("test: " + "level: " +
//                                Integer.toString(tmp.level) + "order: " +
//                                Integer.toString(tmp.order) +
//                                "," + "level: " +
//                                Integer.toString(node.level) + "order: " +
//                                Integer.toString(node.order));
//                    }
                    while (it.hasNext()) {
                        parent = it.next();
                        if (parent.dummy) {
                            isInner = true;
//                            System.out.println("inner: " + "level: " +
//                                    Integer.toString(parent.level) + "order: " +
//                                    Integer.toString(parent.order) +
//                                    "," + "level: " +
//                                    Integer.toString(node.level) + "order: " +
//                                    Integer.toString(node.order));
                        }
                        if (isInner) {
                            break;
                        }
                    }
                }
                if (l1 == liplus1 || isInner) {
                    int k1 = layerListCR.get(i).size();
                    if (isInner) {
                        int layer = parent.level;
                        k1 = layerListCR.get(layer).indexOf(parent);
                    }
                    while (l <= l1) {
                        Node vl = layerListCR.get(i + 1).get(l);
                        Iterator<Node> it = vl.sourceList.iterator();
                        while (it.hasNext()) {
                            parent = it.next();
                            int k = layerListCR.get(parent.level).indexOf(parent);
                            if (k < k0 || k > k1) {
                                segmentMarked.add(Integer.toString(parent.hashCode()) +
                                        "," + Integer.toString(vl.hashCode()));
                                System.out.println("Marked: " + "level: " +
                                        Integer.toString(parent.level) + "order: " +
                                        Integer.toString(parent.order) +
                                        "," + "level: " +
                                        Integer.toString(vl.level) + "order: " +
                                        Integer.toString(vl.order));
                            }
                        }
                        l++;
                    }
                    k0 = k1;
                }
            }
        }
    }

    /** assign actual x-axis position  to nodes */
    private void horizontalCoordinateAssignment() {
        // Upper left
        //preprocessingXC();
        preprocessing();
        verticalAlignment();

        horizontalCompaction();

        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> layer = layerListCR.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Node v = layer.get(j);
                v.upperLeftX = v.x;
                v.x = -1;
            }

        }

        // Lower left
        // cody
//        horizontalCompaction();
//        for (int i = 0; i <
//                layerListCR.size(); i++) {
//            LinkedList<Node> layer = layerListCR.get(i);
//            for (int j = 0; j <
//                    layer.size(); j++) {
//                Node v = layer.get(j);
//                v.lowerLeftX = v.x;
//                v.x = -1;
//            }
//
//        }

        // Upper right

//        verticalAlignment();
//
//        for (int i = 0; i <
//                layerListCR.size(); i++) {
//            LinkedList<Node> layer = layerListCR.get(i);
//            for (int j = 0; j <
//                    layer.size() / 2; j++) {
//                Node v = layer.get(j);
//                layer.set(j, layer.get(layer.size() - j - 1));
//                layer.set(layer.size() - j - 1, v);
//            }
//
//        } // Revers layerList in layerListCR
        // cody

//        horizontalCompaction();
//        for (int i = 0; i <
//                layerListCR.size(); i++) {
//            LinkedList<Node> layer = layerListCR.get(i);
//            for (int j = 0; j <
//                    layer.size(); j++) {
//                Node v = layer.get(j);
//                v.upperRightX = v.x;
//                v.x = -1;
//            }
//
//        }

        // Lower right
        // cody
//        horizontalCompaction();
//        for (int i = 0; i <
//                layerListCR.size(); i++) {
//            LinkedList<Node> layer = layerListCR.get(i);
//            for (int j = 0; j <
//                    layer.size(); j++) {
//                Node v = layer.get(j);
//                v.lowerRightX = v.x;
//                v.x = -1;
//            }
//
//        }


        // Assign X
        for (int i = 0; i < layerListCR.size(); i++) {
            LinkedList<Node> layer = layerListCR.get(i);
            for (int j = 0; j < layer.size(); j++) {
                int avg = 4;
                Node v = layer.get(j);
                int l = v.level;
                if (v.upperLeftX == -1) {
                    v.upperLeftX = 0;
                }

                if (v.upperRightX == -1) {
                    v.upperRightX = 0;
                    avg--;
                }

                if (v.lowerLeftX == -1) {
                    v.lowerLeftX = 0;
                    avg--;
                }
                if (v.lowerLeftX == -1) {
                    v.lowerLeftX = 0;
                }

                if (v.lowerRightX == -1) {
                    v.lowerRightX = 0;
                    avg--;
                }

                if (avg == 0) {
                    System.out.println("Hierarchical Layout Error: X is zero!!");
                    avg = 1;
                }
                v.x = (v.upperLeftX + v.upperRightX +
                        v.lowerLeftX + v.lowerRightX) / avg;
                assignPseudoPosition(v, v.x, l);
            }
        }
    }
} // end class HierarchicalLayout2

