package layout_algorithm;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import model.DataModelInterface;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import view.jgraph.JGraphController;
/**
 * please refer to the article, "crossing reduction in circular layouts*", edited by M. Baur and U. Brandes.
 * @author JLLu
 */
public class CircleLayout {
    /**
     * store nodes which are connected by a node
     */
    private HashMap transitionMap;
    /**
     * store the ID of the node in the graph
     */
    private HashMap stateMap;
    /**
     * jgraph controller
     */
    private JGraphController jgc;
    /**
     * the number of nodes in the graph
     */
    private int nodeSize;
    /**
     * the structure to map id on node
     */
    private HashMap<Integer,Node> nodeMap =null;
    /**
     * the list of ids of nodes which are not layouted
     */
    private Vector<Integer> availableNodeIDs = null;
    /**
     * the array to put ids of nodes which need to layout
     */
    private int[] layoutNodeIDs =null;
    /**
     * initialize these data structures
     * @param transitionMap store nodes which are connected by a node
     * @param stateMap store the ID of the node in the graph
     * @param jgc jgraph controller
     */
    public CircleLayout(HashMap transitionMap, HashMap stateMap,JGraphController jgc) {
        this.transitionMap = transitionMap;
        this.stateMap = stateMap;
        this.jgc = jgc;
        this.nodeSize = this.stateMap.size();
        this.layoutNodeIDs = new int[this.nodeSize];
        this.nodeMap = new HashMap<Integer,Node>();
        this.availableNodeIDs = new Vector<Integer>();
    }
    /**
     *  the main procedure to control running the circular layout
     *  @author Junli Lu
     */
    public void RunLayout(){
        this.InitialData();
        // put the first 2 elements
        if(this.nodeSize < 3)
            return;
        int id= this.availableNodeIDs.remove(0);
        this.nodeMap.get(id).isPlaced = true;
        this.nodeMap.get(id).position = 0;
        this.layoutNodeIDs[0] = id;
        id= this.availableNodeIDs.remove(1);
        this.nodeMap.get(id).isPlaced = true;
        this.nodeMap.get(id).position = 1;
        this.layoutNodeIDs[1] = id;
        int rPos = 2;
        int lPos = this.nodeSize-1;
        int times = 2;
        while(times < (this.nodeSize-1)){
            Node node = this.nodeMap.get(this.GetNodeIDWithLeastUnPlacedNeighbor());
            int rCrossing = 0;
            int lCrossing = 0;
            for(int i=0;i<node.connectNodes.size();i++) {
                if(this.nodeMap.get(node.connectNodes.get(i)).isPlaced) {
                    rCrossing += this.CountCrossingsWhenNodeAtRightEnd(this.nodeMap.get(node.connectNodes.get(i)), rPos);
                    lCrossing += this.CountCrossingsWhenNodeAtLeftEnd(this.nodeMap.get(node.connectNodes.get(i)), lPos);
                }
            }
            if(rCrossing <= lCrossing){
                this.layoutNodeIDs[rPos] = node.ID;
                this.nodeMap.get(node.ID).position = rPos;
                rPos++;
            }else{
                this.layoutNodeIDs[lPos] = node.ID;
                this.nodeMap.get(node.ID).position = lPos;
                lPos--;
            }
            times++;
        }
        Node node = this.nodeMap.get(this.GetNodeIDWithLeastUnPlacedNeighbor());
        this.layoutNodeIDs[rPos] = node.ID;
        node.position = rPos;
        //this.DrawResult();
        this.CircularSifting();
    }
    /**
     *  running the sifting: for each node in the current graph, it will be the number of crossing at every position in the circle, finally
     *  its position will changed to the one with the minimal crossing.
     *  @author Junli Lu
     */
    protected void CircularSifting(){
        Iterator it = this.nodeMap.keySet().iterator();
        while (it.hasNext()) {
            int min = 0;
            int tmp = 0;
            Node u = this.nodeMap.get(it.next());
            Node w = this.nodeMap.get(this.layoutNodeIDs[(u.position-1+this.nodeSize)% this.nodeSize]);
            int times = this.nodeSize -2;
            while(times > 0){
                Node v = this.nodeMap.get(this.layoutNodeIDs[(u.position+1)% this.nodeSize]);
                int c1 = 0;
                for(int i=0;i<u.connectNodes.size();i++) {
                    for(int j=0;j<v.connectNodes.size();j++) {
                        c1 += this.IsCrossing(this.nodeMap.get(u.connectNodes.get(i)),this.nodeMap.get(v.connectNodes.get(j)),
                                u,v);
                    }
                }
                // exchange u,v positon
                this.ExchangeNodes(u, v);
                int c2 = 0;
                for(int i=0;i<v.connectNodes.size();i++) {
                    for(int j=0;j<u.connectNodes.size();j++) {
                        c2 += this.IsCrossing(this.nodeMap.get(v.connectNodes.get(i)),this.nodeMap.get(u.connectNodes.get(j)),
                                v,u);
                    }
                }
                tmp = tmp - c1 + c2;
                if(tmp < min) {
                    min = tmp;
                    w = v;
                }
                times--;
            }
            this.LetWfollowU(u, w);
        }
        this.DrawResult();
    }
    /**
     * exchange two nodes' id in the layoutNodeIDs.
     * @param u a node
     * @param v a node
     * @author Junli Lu
     */
    protected void ExchangeNodes(Node u,Node v) {
        int tmp = u.position;
        u.position = v.position;
        v.position = tmp;
        this.layoutNodeIDs[u.position] = u.ID;
        this.layoutNodeIDs[v.position] = v.ID;
    }
    /**
     * let nodw w follow u, and change positions of all nodes
     * @param u a node
     * @param v a node
     * @author Junli Lu
     */
    protected void LetWfollowU(Node u,Node w){
        for(int i=(w.position+1)%this.nodeSize;i!=u.position;i =(i+1)%this.nodeSize) {
            this.nodeMap.get(this.layoutNodeIDs[i]).position = (i+1)%this.nodeSize;
        }
        u.position = (w.position+1)%this.nodeSize;
        // write to array
        Iterator it = this.nodeMap.keySet().iterator();
        while (it.hasNext()) {
            Node n = this.nodeMap.get(it.next());
            this.layoutNodeIDs[n.position] = n.ID;
        }
    }
    /**
     * @param u a node
     * @param v a node
     * @param x u's neighbor. a neighbor is a node which is connected by a edge.
     * @param y v's neighbor. a neighbor is a node which is connected by a edge.
     * @return to decide if there is a crossing between the edge of x and u and the edge of y and v. return 1 indicating yes, 0 indicating no
     * @author Junli Lu
     */
    protected int IsCrossing(Node x,Node y,Node u,Node v){
        if((x.ID == v.ID) || (y.ID == u.ID))
            return 0;
        int c1 = y.position - x.position;
        if(c1 < 0)
            c1 += this.nodeSize;
        int c2 = u.position - x.position;
        if(c2 < 0)
            c2 += this.nodeSize;
        if(c2 > c1)
            return 1;
        else
            return 0;
    }
    /**
     * @param node a node
     * @param rPos the right position of node
     * @return the number of crossing when the node is placed at the right end
     * @author Junli Lu
     */
    protected int CountCrossingsWhenNodeAtRightEnd(Node node,int rPos) {
        int pos = (node.position + 1) % this.nodeSize;
        int rCrossing = 0;
        while(pos != rPos){
            Node tmp = this.nodeMap.get(this.layoutNodeIDs[pos]);
            for(int j=0;j<tmp.connectNodes.size();j++) {
                if(this.nodeMap.get(tmp.connectNodes.get(j)).isPlaced == false)
                    rCrossing++;
            }
            pos = (pos+1) % this.nodeSize;

        }
        return rCrossing;
    }
    /**
     * @param node a node
     * @param lPos the left position of node
     * @return the number of crossing when the node is placed at the left end
     * @author Junli Lu
     */
    protected int CountCrossingsWhenNodeAtLeftEnd(Node node,int lPos) {
        int pos = (node.position - 1 + this.nodeSize) % this.nodeSize;
        int lCrossing = 0;
        while(pos != lPos) {
            Node tmp = this.nodeMap.get(this.layoutNodeIDs[pos]);
            for(int j=0;j<tmp.connectNodes.size();j++){
                if(this.nodeMap.get(tmp.connectNodes.get(j)).isPlaced == false)
                    lCrossing++;
            }
            pos = (pos-1+this.nodeSize) % this.nodeSize;
        }
        return lCrossing;
    }
    /**
     * @return the id of node which has the least number of unplaced neighbors
     * @author Junli Lu
     */
    protected int GetNodeIDWithLeastUnPlacedNeighbor() {
        int index = 0;
        int least = 9999;
        for(int i=0;i<this.availableNodeIDs.size();i++){
            int tmp = 0;
            Node node = this.nodeMap.get(this.availableNodeIDs.get(i));
            for(int j=0;j<node.connectNodes.size();j++) {
                if(this.nodeMap.get(node.connectNodes.get(j)).isPlaced == false)
                    tmp++;
            }
            if(tmp < least) {
                least = tmp;
                index = i;
            }
        }
        this.nodeMap.get(this.availableNodeIDs.get(index)).isPlaced = true;
        return this.availableNodeIDs.remove(index);
    }
    /**
     * set availableNodeIDs, nodeMap with nodes of the current graph
     * @author Junli Lu
     */
    protected void InitialData() {
        Iterator it = this.stateMap.keySet().iterator();
        while (it.hasNext()) {
            int id = Integer.parseInt(it.next().toString());
            Node node = new Node(id);
            node.connectNodes = this.GetConnectedNodes(id);
            this.availableNodeIDs.add(id);
            this.nodeMap.put(id, node);
        }
    }
    /**
     * draw the result after layout algorithm
     * @author Junli Lu
     */
    protected void DrawResult(){
        final double STATE_RADIUS = 12.5;
        final double RADIUS_FACTOR = 5;
        double radius = RADIUS_FACTOR * STATE_RADIUS * this.nodeSize / Math.PI;
        double step = Math.PI * 2 / this.nodeSize;
        double X = radius + 50;
        double Y = radius + 50;
        for (int i = 0; i<this.nodeSize; i++) {
            double angle = step * i;
            double x = X + radius * Math.sin(angle);
            double y = Y - radius * Math.cos(angle);
            this.jgc.setStatePosition(this.layoutNodeIDs[i],x,y);
        }
    }
    /**
     * draw at random
     * @author Junli Lu
     */
    protected void DrawRandomResult(){
        final double STATE_RADIUS = 12.5;
        final double RADIUS_FACTOR = 4.5;
        double radius = RADIUS_FACTOR * STATE_RADIUS * this.nodeSize / Math.PI;
        double step = Math.PI * 2 / this.nodeSize;
        double X = radius + 50;
        double Y = radius + 50;
        Iterator it = this.stateMap.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            int id = Integer.parseInt(it.next().toString());
            double angle = step * i;
            double x = X + radius * Math.sin(angle);
            double y = Y - radius * Math.cos(angle);
            this.jgc.setStatePosition(id,x,y);
            i++;
        }
    }
    /**
     * @param stateID the id of a node
     * @return the list of nodes which are connected to the node with stateID
     * @author Junli Lu
     */
    protected Vector<Integer> GetConnectedNodes(int stateID) {
        Vector<Integer> connectIDs = new Vector<Integer>();
        DefaultGraphCell centerState = (DefaultGraphCell) this.stateMap.get(stateID);
        centerState = (DefaultGraphCell) centerState.getChildAt(0);
        DefaultGraphCell sourceState;
        DefaultGraphCell targetState;
        DefaultEdge transition;

        Iterator idSets = this.transitionMap.keySet().iterator();
        while (idSets.hasNext()) {
            int transitionID = Integer.parseInt(idSets.next().toString());
            if (transitionID == -99) {
                continue;
            }
            transition = (DefaultEdge) this.transitionMap.get(transitionID);
            DefaultPort sourcePort = (DefaultPort) transition.getSource();
            DefaultPort targetPort = (DefaultPort) transition.getTarget();
            if(sourcePort == null || targetPort == null) {
                continue;
            }
            targetState = (DefaultGraphCell) targetPort.getParent();
            sourceState = (DefaultGraphCell) sourcePort.getParent();

            if (sourceState == centerState && sourceState != targetState) {
                connectIDs.add((Integer)targetState.getAttributes().get("id"));
            } else if (targetState == centerState && sourceState != targetState) {
                connectIDs.add((Integer)sourceState.getAttributes().get("id"));
            }
        }
        return connectIDs;
    }
    /**
     * info. of a node
     * @author Junli Lu
     */
    protected class Node {
        private int ID;
        private int position;
        private Vector<Integer> connectNodes = null;
        private boolean isPlaced;
        /**
         * the s-tructure of a node, used in circular layout
         * @param id the identity
         */
        public Node(int id) {
            this.ID = id;
            this.isPlaced = false;
        }
    }
}
