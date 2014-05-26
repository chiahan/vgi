/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.hierarchical;

import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author x1213
 */
public class Hgraph {


    /**
     * Basic Constructor of Hgraph with
     * 1. vertexList
     * 2. inEdgeList
     * 3. outEdgeList
     * 4. parentVertex
     * 5. upleftPoint
     * 
     */
    public Hgraph() {
        vertexList = new ArrayList();
        inEdgeList = new ArrayList();
        outEdgeList = new ArrayList();
        parentVertex = null;  
        upleftPoint = null;
    }


   /*///////////////////////////////////////////////
    *    Setting
    *///////////////////////////////////////////////
    public void setUpleftPoint(Point2D p) {
        upleftPoint = p;
    }
    public void setParentVertex(Hvertex v) {
        parentVertex = v;
    }
    
    /**
     * addVertex method puts the incoming vertex into current graph, and add it
     * to the vertexList
     * @return vertex
     */
    public Hvertex addVertex() {
        Hvertex v = new Hvertex();
        v.setGraph(this);
        vertexList.add(v);
        return v;
    }

    /**
     * addEdge method basically get @param s and @param t as edge's source and
     * target, and stores itself as new data structure Hedge
     * @return 
     */
    public Hedge addEdge(Hvertex s, Hvertex t) {
        Hvertex ss, tt;
        if (s.graph() == this) {
            ss = s;
        }
        else {
            ss = s.graph().parentVertex();
        }
        if (t.graph() == this) {
            tt = t;
        }
        else {
            tt = t.graph().parentVertex();
        }
        
        // try to find an edge {s,t}
        // It will be find if it actually have an edge in the lower level
        Hedge e;
        if (s != ss) {
            e = s.graph().findEdge(s,t);
        }
        else {
            e = t.graph().findEdge(s,t);
        }
        if (e != null) {
            e.incrMultiplicity();
            return e;
        }
        
        // if not found, create one.
        // This is the case that the vertex stands for a group
        e = new Hedge(1);
        e.setSource(s);
        e.setTarget(t);
        
        // we add edge to s, t only when they are in the same subgraph.
        if (t.graph() == s.graph()) {
            s.addEdge(e);
            t.addEdge(e);
        }
        
        // add to edgelist
        if (s == ss && t == tt) {
            inEdgeList.add(e);
        }

        // s, t are all in groups
        else if (s.graph() != this &&  t.graph() != this){
            // in different groups
            if (s.graph() != t.graph()) {
                s.graph().outEdgeList.add(e);
                t.graph().outEdgeList.add(e);
            }
            // in the same group
            else {
                s.graph().inEdgeList.add(e);           
            }
        }
        else {
            // t is a vertex this graph, s in a group
            if (s.graph() != this) {
                s.graph().outEdgeList.add(e);
            }
            // s is a vertex this graph, t in a group
            if (t.graph() != this) {
                t.graph().outEdgeList.add(e);
            }
        }
        
        
        // if it involves some grouping, but not in the same group... 
        if ((s != ss || t != tt) && (s.graph() != t.graph())) {
            Hedge e2 = findEdge(ss,tt);
            // if not found, create one.
            if (e2 == null) {
                e2 = new Hedge(0);
                inEdgeList.add(e2);
                e2.setSource(ss);
                e2.setTarget(tt);  
                ss.addEdge(e2);
                tt.addEdge(e2);
            }
            // add e to e2
            e2.addSubedge(e);
            e.setParent(e2);
        }
        
        return e;
    }


    /*///////////////////////////////////////////////
     *    Layout algorithm
     *///////////////////////////////////////////////

    /**
     * An extremely simple algorithm: 
     * reverse all edges that contradict with the ordering in the vertexList.
     */
    public void naiveCycleRemoval() {
        for (Hedge e : inEdgeList) {
            Hvertex s = e.source();
            Hvertex t = e.target();
            if (vertexList.indexOf(s) > vertexList.indexOf(t)) {
                e.reverse();
            }
        }
    }

    /**
     * The longest path algorithm (see p.421 of that survey)
     * p.s. the implementation here is extremely simple that the time complexity is greatly sacrificed.
     * @return 
     */
    public List<List<Hvertex>> layerAssignment_LPG() {
        List<List<Hvertex>> layer = new ArrayList<List<Hvertex>>();
        List<Hvertex> remained = new ArrayList<Hvertex>();
        remained.addAll(vertexList);
        while (!remained.isEmpty()) {
            List<Hvertex> currentLayer = new ArrayList<Hvertex>();

            // find the vertices such that all their inadj have been assigned to the previous layers. 
            for (Hvertex v : remained) {
                boolean test = true;
                for (int i = 0; i < v.inDeg(); ++i) {
                    if (remained.contains(v.inAdj(i))) {
                        test = false;
                    }
                }
                if (test) {
                    currentLayer.add(v);
                }
            }

            // update: remained, layer
            layer.add(currentLayer);
            remained.removeAll(currentLayer);
        }
        return layer;
    }
    
    /**
     * BaryCenterVertexOrdering separates procedures
     * to two phases based on the paper
     * @param layer
     * @return layer
     */
    public List<List<Hvertex>> BaryCenterVertexOrdering(List<List<Hvertex>> layer) {
        
        vertexOrderingPhaseOne(layer, 0);
        vertexOrderingPhaseOne(layer, 1);
        
        vertexOrderingPhaseTwo(layer, 2);
        vertexOrderingPhaseTwo(layer, 1);
        
        return layer;
    }
    
    /**
     * This is the phase 1 of BC method
     * It do the first reordering without reverse order with same BC
     * Iteration will be 1 in this case
     * @param layer
     * @return layer that is modified
     */
    public void vertexOrderingPhaseOne(List<List<Hvertex>> layer, int ith) {

        // initialization
        int[][] MStar = getInterConMatrix(layer, ith);
        int crossNumStar = getCrossingNumber(MStar);
        double[] BCR = getRowBaryCenter(MStar);
        double[] BCC = getColumnBaryCenter(MStar);
        int[][] MR;
        int[][] MC;
        
        while (!(isBCRinOrder(BCR) && isBCCinOrder(BCC))) {
            // reorder rows
            MR = reorderInterConMatrixByRow(layer, BCR, ith);
            int crossNum = getCrossingNumber(MR);
            if (crossNum < crossNumStar) {
                crossNumStar = crossNum;
                MStar = MR;
                BCR = getRowBaryCenter(MR);
                BCC = getColumnBaryCenter(MR);
            }

            // reorder columns
            MC = reorderInterConMatrixByColumn(layer, BCC, ith);
            crossNum = getCrossingNumber(MC);
            if (crossNum < crossNumStar) {
                crossNumStar = crossNum;
                MStar = MC;
                BCR = getRowBaryCenter(MC);
                BCC = getColumnBaryCenter(MC);
            }
        }

    }
    
    /**
     * 
     * @param layer
     * @param prevM
     */
    public void vertexOrderingPhaseTwo(List<List<Hvertex>> layer, int ith) {
        int[][] MStar = getInterConMatrix(layer, ith);
        int crossNumStar = getCrossingNumber(MStar);
        double[] BCR = getRowBaryCenter(MStar);
        double[] BCC = getColumnBaryCenter(MStar);

        reverseRowOrder(layer, BCR, ith);
        reverseColumnOrder(layer, BCC, ith);


        vertexOrderingPhaseOne(layer, ith);
    }

    /*///////////////////////////////////////////////
     *    Others
     *///////////////////////////////////////////////
    /**
     * findEdge method based on given source and target vertex to find
     * corresponding edge.
     * @param s
     * @param t
     * @return 
     */
    public Hedge findEdge(Hvertex s, Hvertex t) {
        for (Hedge e : inEdgeList) {
            if (e.source() == s && e.target() == t || e.source() == t && e.target() == s) {
                return e;
            }
        }
        for (Hedge e : outEdgeList) {
            if (e.source() == s && e.target() == t || e.source() == t && e.target() == s) {
                return e;
            }
        }
        return null;
    }

    /**
     * 
     * @return 
     */
    public Hvertex parentVertex() {
        return parentVertex;
    }
    
    public int[][] getInterConMatrix(List<List<Hvertex>> layer, int ith) {
        int[][] M = new int[layer.get(ith).size()][layer.get(ith+1).size()];
        for (Hvertex s: layer.get(ith)) {
            for (int i=0; i<s.outDeg(); i++) {
                M[layer.get(ith).indexOf(s)][layer.get(ith+1).indexOf(s.outAdj(i))] = 1;
            }
        }

        // print out current result
        System.out.println("interconnection matrix:");
        printInterConMatrix(M);

        return M;
    }
    
    /**
     * index notation follow the papers ones
     * @param M
     * @return 
     */
    public int getCrossingNumber(int[][] M) {
        int crossNum = 0;
        for (int j = 0; j < M.length-1; j++) {
            for (int k = j+1; k < M.length; k++) {
                for (int a = 0; a < M[0].length-1; a++) {
                    for (int b = a+1; b < M[0].length; b++) {
                        if ((M[j][b] == 1) && (M[k][a] == 1)) {
                            crossNum += 1;
                        }
                    }
                }
            }
        }
        
        // print out current result
        System.out.println("Cross Num: " + crossNum);
        return crossNum;
    }
    
    public double[] getRowBaryCenter(int[][] M) {
        double[] BCR = new double[M.length];
        double numeBCR = 0.0;
        double denoBCR = 0.0;
        for (int l=0; l < BCR.length; l++) {
            for (int k=0; k<M[l].length; k++) {
                numeBCR += (double)(k+1)*M[l][k];
                denoBCR += (double)M[l][k];
            }
            BCR[l] = numeBCR/denoBCR;
            numeBCR = 0.0;
            denoBCR = 0.0;
            
        }

        // print out result
        printColumnBaryCenter(BCR);
        
        return BCR;
    }
    
    public double[] getColumnBaryCenter(int[][] M) {
        double[] BCC = new double[M[0].length];
        double numeBCC = 0.0;
        double denoBCC = 0.0;
        for (int j=0; j < BCC.length; j++) {
            for (int i=0; i < M.length; i++) {
                numeBCC += (double)(i+1)*M[i][j];
                denoBCC += (double)M[i][j];
            }
            BCC[j] = numeBCC/denoBCC;
            numeBCC = 0.0;
            denoBCC = 0.0;
        }
        
        // print out result
        printColumnBaryCenter(BCC);
        
        return BCC;
    }

    /**
     * reorder layer by rows based on the value in BCR
     * @param layer
     * @param BCR
     * @return 
     */
    public int[][] reorderInterConMatrixByRow(List<List<Hvertex>> layer, double[] BCR, int ith) {
        for (int i=0; i < BCR.length-1; i++) {
            for (int j=0; j < BCR.length-i-1; j++){
                if (BCR[j] > BCR[j+1]) {
                    swapBCR(BCR, j, j+1);
                    swapPrevLayer(layer, j, j+1, ith);
                }
            }
        }
        int[][] M = getInterConMatrix(layer, ith);
        return M;
    }
    
    /**
     * reorder layer by columns based on the value in BCC
     * @param layer
     * @param BCR
     * @return 
     */
    public int[][] reorderInterConMatrixByColumn(List<List<Hvertex>> layer, double[] BCC, int ith) {
        for (int i=0; i < BCC.length-1; i++) {
            for (int j=0; j < BCC.length-i-1; j++){
                if (BCC[j] > BCC[j+1]) {
                    swapBCC(BCC, j, j+1);
                    swapNextLayer(layer, j, j+1, ith);
                }
            }
        }
        int[][] M = getInterConMatrix(layer, ith);
        return M;
    }
    
    public void swapBCR(double[] BCR, int a, int b) {
        double tmp = BCR[a];
        BCR[a] = BCR[b];
        BCR[b] = tmp;
    }

    public void swapBCC(double[] BCC, int a, int b) {
        double tmp = BCC[a];
        BCC[a] = BCC[b];
        BCC[b] = tmp;
    }
    
    public void swapPrevLayer(List<List<Hvertex>> layer, int a, int b, int ith) {
        Hvertex tmp = layer.get(ith).get(a);
        layer.get(ith).set(a, layer.get(ith).get(b));
        layer.get(ith).set(b, tmp);
    }
    
    public void swapNextLayer(List<List<Hvertex>> layer, int a, int b, int ith) {
        Hvertex tmp = layer.get(ith+1).get(a);
        layer.get(ith+1).set(a, layer.get(ith+1).get(b));
        layer.get(ith+1).set(b, tmp);
    }
    
    public Boolean isBCRinOrder(double[] BCR) {
        for (int i = 0; i < BCR.length-1; i++) {
            if (BCR[i] > BCR[i+1]) {
                return false;
            }
        }
        return true;
    }
    
    public Boolean isBCCinOrder(double[] BCC) {
        for (int i = 0; i < BCC.length-1; i++) {
            if (BCC[i] > BCC[i+1]) {
                return false;
            }
        }
        return true;
    }
    
    public Boolean reverseRowOrder(List<List<Hvertex>> layer, double[] BCR, int ith) {
        for (int i = 0; i < BCR.length-1; i++) {
            if (BCR[i] == BCR[i+1]) {
                swapBCR(BCR, i, i+1);
                swapPrevLayer(layer, i, i+1, ith);
                return true;
            }
        }
        return false;
    }
    
    public Boolean reverseColumnOrder(List<List<Hvertex>> layer, double[] BCC, int ith) {
        for (int i = 0; i < BCC.length-1; i++) {
            if (BCC[i] == BCC[i+1]) {
                swapBCC(BCC, i, i+1);
                swapNextLayer(layer, i, i+1, ith);
                return true;
            }
        }
        return false;
    }
    
    public void printInterConMatrix(int[][] M) {
        for (int i=0; i < M.length; i++) {
            for (int j=0; j < M[0].length; j++) {
                System.out.print(M[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public void printRowBaryCenter(double[] BCR) {
        for (int i=0; i<BCR.length; i++) {
            System.out.println(BCR[i]);
        }
    }
    
    public void printColumnBaryCenter(double[] BCC) {
        for (int i=0; i<BCC.length; i++) {
            System.out.println(BCC[i]);
        }
    }

    /*///////////////////////////////////////////////
     *    Members
     *///////////////////////////////////////////////   
    private Point2D upleftPoint; // the most up-left point
    private List<Hvertex> vertexList; //set of all vertices in the graph
    private List<Hedge> inEdgeList; //edge within the graph
    private List<Hedge> outEdgeList; //edge linking to outside of the graph
    private Hvertex parentVertex; // the vertex representing this graph. If this graph is not a subgraph, it is set to null.
}
