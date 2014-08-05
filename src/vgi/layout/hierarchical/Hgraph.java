package vgi.layout.hierarchical;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * The abstract data structure of the given automata
 * <p>
 *  <b>Hgraph</b> divides to 
 * </p>
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
    public List<List<Hvertex>> layerAssignment_LPA() {
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

    
    /*///////////////////////////////////////////////
     *    Vertex Ordering
     *///////////////////////////////////////////////
    
    /**
     * VertexOrdering procedure: 
     * Implementation of "An Efficient Implementation of Sugiyama's Algorithm
     * for Layered Graph Drawing"
     * ESK = abbreviations of authors' names
     * @param layer
     * @return layer (containing Hvertex and Hedge)
     */
    public List<List<Object>> vertexOrdering_ESK(List<List<Hvertex>> layer) {
        List<List<Object>> new_layer = new ArrayList<List<Object>>();  
        Map<Hvertex, Integer> layerMap = new HashMap<Hvertex, Integer>();

        //Initialize the new layering and mapping
        for (int i = 0; i < layer.size(); ++i) {
            List<Object> row = new ArrayList<Object>();        
            for (int j = 0; j < layer.get(i).size(); ++j) {
                row.add(layer.get(i).get(j));
                layerMap.put(layer.get(i).get(j), i);
            }
            new_layer.add(row);
        }
        
        //Add long edges into the layering
        for (int i = 0; i < inEdgeList.size(); ++i) {
            Hedge e = inEdgeList.get(i);
            int l_1 = layerMap.get(e.source());
            int l_2 = layerMap.get(e.target());
            if (l_2 <= l_1) {
                e = null;
                //program should never reach here
            }
            for (int j = l_1 + 1; j < l_2; ++j) {
                new_layer.get(j).add(e);
            }
        }
        
        //vertex ordering
        for (int i = 0; i < new_layer.size()-1; ++i) {
            List<Object> r = vertexOrdering_ESK_twoLayer(new_layer.get(i), new_layer.get(i+1));
            new_layer.set(i+1, r);
        }       
        edgePositionAdjusting(new_layer);
        new_layer = vertexOrderingReverse_ESK(new_layer);
        //vertex ordering again
        for (int i = 0; i < new_layer.size()-1; ++i) {
            List<Object> r = vertexOrdering_ESK_twoLayer(new_layer.get(i), new_layer.get(i+1));
            new_layer.set(i+1, r);
        }
        edgePositionAdjusting(new_layer);
        new_layer = vertexOrderingReverse_ESK(new_layer);
        for (int i = 0; i < new_layer.size()-1; ++i) {
            List<Object> r = vertexOrdering_ESK_twoLayer(new_layer.get(i), new_layer.get(i+1));
            new_layer.set(i+1, r);
        }
        edgePositionAdjusting(new_layer);
        new_layer = vertexOrderingReverse_ESK(new_layer);
        for (int i = 0; i < new_layer.size()-1; ++i) {
            List<Object> r = vertexOrdering_ESK_twoLayer(new_layer.get(i), new_layer.get(i+1));
            new_layer.set(i+1, r);
        }   
        new_layer = vertexOrderingReverse_ESK(new_layer);
        //edgePositionAdjusting(new_layer);
        //return the new layering
        return new_layer;
    }

    public void reverseAllEdge() {
        for (Hedge e : inEdgeList) {
            e.reverse();
        }
    }

    public List<List<Object>> vertexOrderingReverse_ESK(List<List<Object>> layer) {
        //reverse the layer
        List<List<Object>> new_layer = new ArrayList<List<Object>>();  
        for (int i = layer.size()-1; i >= 0; --i) {
            new_layer.add(layer.get(i));
        }  
        //vertex ordering
        reverseAllEdge();
        for (int i = 0; i < new_layer.size()-1; ++i) {
            List<Object> r = vertexOrdering_ESK_twoLayer(new_layer.get(i), new_layer.get(i+1));
            new_layer.set(i+1, r);
        }
        reverseAllEdge();
        //reverse the layer
        List<List<Object>> new_layer2 = new ArrayList<List<Object>>();  
        for (int i = new_layer.size()-1; i >= 0; --i) {
            new_layer2.add(new_layer.get(i));
        }  
        return new_layer2;
    }
    /**
     * Subroutine of vertexOrdering_ESK
     * Based on sec 3.1 of the paper
     * @param l1 //upper(previous) layer
     * @param l2 //current layer
     * @return the new l2 (original one is destroyed)
     */
    public List<Object> vertexOrdering_ESK_twoLayer(List<Object> l1, List<Object> l2) { 
        Map<Object, Double> measure = new HashMap<Object, Double>();
        Map<Object, Double> measure2 = new HashMap<Object, Double>();
        List<Object> newL2 = new ArrayList<Object>();
        
        //calculate measure of objects in l1
        for (int i = 0; i < l1.size(); ++i) {
             measure.put(l1.get(i), (double)i);
        } 
        /*
        for (int i = 0; i < l1.size(); ++i) {
            int j = i;
            while (true) {
                Object obj = l1.get(j);
                if (obj instanceof Hedge) {
                    if (j == 0 || (l1.get(j-1) instanceof Hedge)) {
                        break;
                    }
                    --j;
                }
                else {
                    break;
                }
            }
            double jj = j;
            measure.put(l1.get(i), jj);
        }*/
        
        //calculate measure of objects in l2 (by median heuristic)
        for (int i = 0; i < l2.size(); ++i) {     
            measure2.put(l2.get(i), median_Cal(l1,l2.get(i),measure));
        }
        
        //ordering l2 according to the measure calculated
        while (true) {
            double d_min = 10000000.0;
            Object obj_min = null;
            if (!l2.isEmpty()) {
                //retrieve the object of minimum measure
                for (int i = 0; i < l2.size(); ++i) {
                    if (d_min > measure2.get(l2.get(i))) {
                        d_min = measure2.get(l2.get(i));
                        obj_min = l2.get(i);
                    }
                }
                l2.remove(obj_min);
                newL2.add(obj_min);
            }
            else {
                break;
            }
        }
        
        //return the new l2
        return newL2;
    } 

    /**
     * Calculate measure of obj according to the measure in the previous layer r
     * according to the median heuristic.
     * @param r
     * @param obj
     * @param measure
     * @return measure value of obj
     */
    public double median_Cal(List<Object> r, Object obj, Map<Object, Double> measure) { 
        double m = 0.0;
        if (obj instanceof Hedge) {
            Hedge e = (Hedge)obj;
            if (r.contains(obj)) {
                m = measure.get(obj);
            }
            else {
                m = measure.get(e.source());
            }
        }
        else if (obj instanceof Hvertex) {
            List<Double> measure_list = new ArrayList<Double>();
            Hvertex v = (Hvertex)obj;
            for (int i = 0; i < v.inDeg(); ++i) {
                if (r.contains(v.inAdj(i))) {
                    measure_list.add(measure.get(v.inAdj(i))); 
                }
                else if (r.contains(v.inEdge(i))) {
                    measure_list.add(measure.get(v.inEdge(i))); 
                }
            }
            for (int i = 0; i < measure_list.size(); ++i) {
                m += measure_list.get(i);
            }
            m /= measure_list.size();
            if (measure_list.size() == 0) 
            {
                m = 0.0;
            }
        }
        
        return m;
    }

    /**
     * After calculating vertex ordering, 
     * we try to change the position of edge long edge of smallest length
     * by testing all possible position.
     * We choose the position yielding the smallest number of crossing.
     * @param layer
     */
    public void edgePositionAdjusting(List<List<Object>> layer) {
        Collection<Object> finished = new ArrayList<Object>();
        for (int i = 1; i < layer.size()-1; ++i) {
            for (int j = 0; j < layer.get(i).size(); ++j) {
                if (layer.get(i).get(j) instanceof Hedge && !finished.contains(layer.get(i).get(j))) {
                    Hedge ej = (Hedge)(layer.get(i).get(j));
                    if (layer.get(i-1).contains(ej.source()) && layer.get(i+1).contains(ej.target())) {
                        findBestPosition(layer, i, j);
                        --j;
                        finished.add(ej);
                    }
                }
            }
        }
    }
    
    public void findBestPosition(List<List<Object>> layer, int i, int j) {
        int best_pos = j;
        int best_crossing = 9999999;
        Hedge ej = (Hedge)(layer.get(i).get(j));
        layer.get(i).remove(j);
        for (int s = 0; s <= layer.get(i).size(); ++s) {
            layer.get(i).add(s, ej);
            int crossing = crossingCal(layer, i, s);
            if (crossing < best_crossing) {
                best_pos = s;
                best_crossing = crossing;
            }
            layer.get(i).remove(s);
        }
        layer.get(i).add(best_pos, ej);
    }
 
    public int crossingCal(List<List<Object>> layer, int i, int j) {
        Hedge ej = (Hedge)(layer.get(i).get(j));
        int crossing = 0;
        for (int s = 0; s < layer.get(i-1).size(); ++s) {
            Object obj = layer.get(i-1).get(s);
            if (obj instanceof Hedge) {
                Hedge e = (Hedge)obj;
                if (layer.get(i).contains(obj)) {
                    if ((layer.get(i).indexOf(obj)-j)*(s-layer.get(i-1).indexOf(ej.source())) < 0) {
                        ++crossing;
                    }
                }
                else {
                    if ((layer.get(i).indexOf(e.target())-j)*(s-layer.get(i-1).indexOf(ej.source())) < 0) {
                        ++crossing;
                    }
                }
            }
            else if (obj instanceof Hvertex) {
                Hvertex v = (Hvertex)obj;
                for (int ss = 0; ss < v.outDeg(); ++ss) {
                    if (layer.get(i).contains(v.outAdj(ss))) {
                        if ((layer.get(i).indexOf(v.outAdj(ss))-j)*(s-layer.get(i-1).indexOf(ej.source())) < 0) {
                            ++crossing;
                        }
                    }
                    else if (layer.get(i).contains(v.outEdge(ss))) {
                        if ((layer.get(i).indexOf(v.outEdge(ss))-j)*(s-layer.get(i-1).indexOf(ej.source())) < 0) {
                            ++crossing;
                        }
                    }
                }
            }
        }

        for (int s = 0; s < layer.get(i+1).size(); ++s) {
            Object obj = layer.get(i+1).get(s);
            if (obj instanceof Hedge) {
                Hedge e = (Hedge)obj;
                if (layer.get(i).contains(obj)) {
                    if ((layer.get(i).indexOf(obj)-j)*(s-layer.get(i+1).indexOf(ej.target())) < 0) {
                        ++crossing;
                    }
                }
                else {
                    if ((layer.get(i).indexOf(e.source())-j)*(s-layer.get(i+1).indexOf(ej.target())) < 0) {
                        ++crossing;
                    }
                }
            }
            else if (obj instanceof Hvertex) {
                Hvertex v = (Hvertex)obj;
                for (int ss = 0; ss < v.inDeg(); ++ss) {
                    if (layer.get(i).contains(v.inAdj(ss))) {
                        if ((layer.get(i).indexOf(v.inAdj(ss))-j)*(s-layer.get(i+1).indexOf(ej.target())) < 0) {
                            ++crossing;
                        }
                    }
                    else if (layer.get(i).contains(v.inEdge(ss))) {
                        if ((layer.get(i).indexOf(v.inEdge(ss))-j)*(s-layer.get(i+1).indexOf(ej.target())) < 0) {
                            ++crossing;
                        }
                    }
                }
            }
        }
        return crossing;
    }
    
    /*///////////////////////////////////////////////
     *   x-coordinate assignment
     *///////////////////////////////////////////////

     /**
     * xCoordinateAssignment procedure: 
     * Balanced version of xCoordinateAssignment_BK
     * @param layer
     * @return assignment (a map associating each object to an integer)
     */
    public Map<Object, Integer> xCoordinateAssignment_BK_balanced(List<List<Object>> layer) {
        Map<Object, Integer> assignment_ul = xCoordinateAssignment_BK(layer);
        
        List<List<Object>> layer2 = layerReversing(layer);
        Map<Object, Integer> assignment_ur = xCoordinateAssignment_BK(layer2);
        assignment_ur = assignmentReversing(assignment_ur);
        return assignmentAveraging(assignment_ul, assignment_ur);
    }
    
    public List<List<Object>> layerReversing(List<List<Object>> layer) {
        List<List<Object>> layer2 = new ArrayList<List<Object>>();
        for (int i = 0; i < layer.size(); ++i) {
            layer2.add(new ArrayList<Object>());
            for (int j = layer.get(i).size()-1; j>=0; --j) {
                layer2.get(i).add(layer.get(i).get(j));
            }
        }
        return layer2;
    }
    
    public Map<Object, Integer> assignmentReversing(Map<Object, Integer> assignment) {
        Map<Object, Integer> assignment2 = new HashMap<Object, Integer>();
        int max = 0; 
        Collection<Integer> s = assignment.values();
        for (Integer i : s) {
            if (max < i) {
                max = i;
            }
        } 
        for (Hvertex v: vertexList) {
            int a = assignment.get(v);
            assignment2.put(v, max-a);
        }
        for (Hedge e: inEdgeList) {
            if (assignment.containsKey(e)) {
                int a = assignment.get(e);
                assignment2.put(e, max-a);
            }
        }
        return assignment2;
    }

    public Map<Object, Integer> assignmentAveraging(Map<Object, Integer> a1, Map<Object, Integer> a2) {
        Map<Object, Integer> assignment = new HashMap<Object, Integer>();
        for (Hvertex v: vertexList) {
            assignment.put(v, (a1.get(v)+a2.get(v))/2);
        }
        for (Hedge e: inEdgeList) {
            if (a1.containsKey(e)) {
                assignment.put(e, (a1.get(e)+a2.get(e))/2);
            }
        }
        return assignment;
    } 

     /**
     * xCoordinateAssignment procedure: 
     * Implementation of "Fast and Simple Horizontal Coordinate Assignment"
     * BK = abbreviations of authors' names
     * @param layer
     * @return assignment (a map associating each object to an integer)
     */
    public Map<Object, Integer> xCoordinateAssignment_BK(List<List<Object>> layer) {
        Map<Object, Integer> assignment = new HashMap<Object, Integer>();
        Map<Object, Halignment> alignmentMap = new HashMap<Object, Halignment>();
        
        //initialize the alignment of the first layer
        for (int i = 0; i < layer.get(0).size(); ++i) {
            Halignment a = new Halignment();
            a.add_member(layer.get(0).get(i));
            alignmentMap.put(layer.get(0).get(i), a);
        }  
        for (int i = 0; i < layer.get(0).size()-1; ++i) { 
            alignmentMap.get(layer.get(0).get(i)).add_right(alignmentMap.get(layer.get(0).get(i+1)));
        }       
        for (int i = 1; i < layer.get(0).size(); ++i) { 
            alignmentMap.get(layer.get(0).get(i)).add_left(alignmentMap.get(layer.get(0).get(i-1)));
        } 
        //vertical alignment
        for (int i = 0; i < layer.size()-1; ++i) {
            verticalAlignment_twoLayer(layer.get(i), layer.get(i+1), alignmentMap);
        }
        //horizontal compaction
        List<Halignment> aList = new ArrayList<Halignment>();
        aList.addAll(alignmentMap.values());
        horizontalCompaction_LPA(aList);
        //set the assignment
        for (Hvertex v: vertexList) {
            assignment.put(v, alignmentMap.get(v).getXcoordinate());
        }
        for (Hedge e: inEdgeList) {
            if (alignmentMap.containsKey(e)) {
                assignment.put(e, alignmentMap.get(e).getXcoordinate()); 
            }         
        }
        return assignment;
    }

     /**
     * (Roughly) Alg2 of "Fast and Simple Horizontal Coordinate Assignment"
     * By default, directions are: up, left
     * The results are the alignments in the alignmentMap.
     * @param l1
     * @param l2 
     * @param alignmentMap
     */
    public void verticalAlignment_twoLayer(List<Object> l1, List<Object> l2, Map<Object, Halignment> alignmentMap) {
        //vertical alignment
        int i1 = 0;
        for (int i2 = 0; i2 < l2.size(); ++i2) {
            // m = the candidate in l1 that l2[i2] would like to align with.
            int m = median_index_Cal(l1, l2.get(i2));
            boolean conflict = false;
            if (m < i1) continue;
            else {
                //check if there is a conflict involving long edge
                for (int j = i1; j < m; ++j) {
                    if ((l1.get(j) instanceof Hedge) && l2.contains(l1.get(j))) {
                        conflict = true;
                        break;
                    }
                }
            }
            if (conflict) {
                continue;
            }
            //if there's no conflict, update alignment
            Halignment a = alignmentMap.get(l1.get(m));
            alignmentMap.put(l2.get(i2) , a);
            a.add_member(l2.get(i2));
            //update i1
            i1 = m+1;
            //if no further alignment can be made
            if (i1 >= l1.size()) {
                break;
            }
        }
        //create new alignments for un-aligned objs
        for (int i2 = 0; i2 < l2.size(); ++i2) {
            //if l2[i2] has not been aligned
            if (!alignmentMap.containsKey(l2.get(i2))) {
                Halignment a = new Halignment();
                a.add_member(l2.get(i2));
                alignmentMap.put(l2.get(i2), a);
            }
        }
        //updated left-right linkages of alignments
        for (int i2 = 0; i2 < l2.size()-1; ++i2) { 
            alignmentMap.get(l2.get(i2)).add_right(alignmentMap.get(l2.get(i2+1)));
        }       
        for (int i2 = 1; i2 < l2.size(); ++i2) { 
            alignmentMap.get(l2.get(i2)).add_left(alignmentMap.get(l2.get(i2-1)));
        }    
    }    

     /**
     * Return an index of obj in the previous layer
     * according to the median heuristic.
     * @param r
     * @param obj
     * @return index of obj
     */   
    public int median_index_Cal(List<Object> r, Object obj) { 
        int m = 0;
        if (obj instanceof Hedge) {
            Hedge e = (Hedge)obj;
            if (r.contains(obj)) {
                m = r.indexOf(obj);
            }
            else {
                m = r.indexOf(e.source());
            }
        }
        else if (obj instanceof Hvertex) {
            List<Integer> index_list = new ArrayList<Integer>();
            Hvertex v = (Hvertex)obj;
            for (int i = 0; i < v.inDeg(); ++i) {
                if (r.contains(v.inAdj(i))) {
                    index_list.add(r.indexOf(v.inAdj(i)));
                }
                else if (r.contains(v.inEdge(i))) {
                    index_list.add(r.indexOf(v.inEdge(i)));
                }
            }
            //m = index_list.get((index_list.size()-1)/2);
            m = median(index_list);
        }
        return m;
    }
    
    public int median(List<Integer> s) {
        if (s.size() == 0) {
            return -1;
        }
        List<Integer> ss = new ArrayList<Integer>();
        ss.addAll(s);
        //sorting
        int min = 999999;
        int ind = -1;
        for (int i = 0; i < ss.size(); ++i) {
            for (int j = i; j < ss.size(); ++j) {
                if (s.get(j) < min) {
                    min = s.get(i);
                    ind = i;
                }
            }
            ss.set(ind, ss.get(0));
            ss.set(i, min);
        }
        //return min
        if (ss.size() % 2 == 0) {
            return ss.get(ss.size()/2-1);
        }
        else {
            return ss.get(ss.size()/2);
        }
    }

      /**
     * (Roughly) Alg3 of "Fast and Simple Horizontal Coordinate Assignment"
     * The algorithm in essence is LPA.
     * We DO NOT implement the class-wise compaction procedure.
     * The resulting coordinates are saved in the alignments.
     * @param aList 
     */   
    public void horizontalCompaction_LPA(List<Halignment> aList) {
        List<Halignment> remainList = new ArrayList<Halignment>();
        List<Halignment> currentList = new ArrayList<Halignment>();
        remainList.addAll(aList);
        //Initial: remainList = all allignments.
        int x = 0; // x-coordinate
        while (!remainList.isEmpty()) {
            currentList.clear();
            //Construct the currentList (whose members have x-coordinate = x)
            for (int i = 0; i < remainList.size(); ++i) {
                boolean allLeftAdjHaveBeenAdded = true;
                for (int j = 0; j < remainList.size(); ++j) {
                    if (remainList.get(i).LeftAdjContains(remainList.get(j))) {
                        allLeftAdjHaveBeenAdded = false;
                        break;
                    }
                }
                if (allLeftAdjHaveBeenAdded) {
                    currentList.add(remainList.get(i));
                }
            }
            //set x-coordinate
            for (int i = 0; i < currentList.size(); ++i) {    
                currentList.get(i).setXcoordinate(x);
            }
            //update remainList
            remainList.removeAll(currentList);
            //update x
            ++x;
        }
    }
    
    /*///////////////////////////////////////////////
     *    Unused Vertex Ordering procedures
     *///////////////////////////////////////////////
    
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
  
    public int numVertex() {
        return vertexList.size();
    }
    
    public Hvertex getVertex(int i) {
        return vertexList.get(i);
    }
    public Point2D getUpLeftPoint() {
        return upleftPoint;
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