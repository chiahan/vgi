package vgi.layout.feature;


import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.model.mxGraphModel;
import java.util.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import com.mxgraph.model.mxCell;
import vgi.layout.edgerouting.Edge;
import vgi.layout.edgerouting.Node;
import vgi.layout.edgerouting.OptimizerModularity;

public  class ClusterPreProcess
{

    /**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public ClusterPreProcess(mxGraph graph)
	{
		ClusterDetect(graph);
                
	}

	protected Map<mxCell, List<mxCell>> ClusterFeatureNode = new HashMap<mxCell, List<mxCell>>();
        // count the nubmer of nodes in the specified cluster
	protected Map<Integer, Integer> NodeNumberInCluster = new HashMap<Integer, Integer>(); 
        protected Map<Integer, List<String>> ClusterNodes = new HashMap<Integer, List<String>>();
        protected List<Integer> Grouped = new ArrayList<Integer>();
        
        public void ClusterDetect(mxGraph graph) {
           
            ClusteringLayout layout = new ClusteringLayout(graph);
            Map<String,Map<String,Double>> featureGraph = layout.readGraph(graph, graph.getDefaultParent());
            featureGraph = layout.makeSymmetricGraph(featureGraph);
            Map<String,Node> nameToNode = layout.makeNodes(featureGraph);
                        
            List<Node> nodes = new ArrayList<Node>(nameToNode.values());
            List<Edge> edges = layout.makeEdges(featureGraph,nameToNode);
            Map<Node,double[]> nodeToPosition = layout.makeInitialPositions(nodes, false);
	
            new MinimizerBarnesHut(nodes, edges, 0.0, 1.0, 0.05).minimizeEnergy(nodeToPosition, 100);
            Map<Node,Integer> nodeToCluster = new OptimizerModularity().execute(nodes, edges, false);
            
            
            // count the nodes for each cluster
            for (Node node : nodeToCluster.keySet()) {
       
                int cluster = nodeToCluster.get(node);
                int count = 1;
                
                if(NodeNumberInCluster.containsKey(cluster)){
                    count = NodeNumberInCluster.get(cluster)+1;
                }
                NodeNumberInCluster.put(cluster, count);
                
                if(!ClusterNodes.containsKey(cluster)){
                ClusterNodes.put(cluster, new ArrayList<String>());
                }
                ClusterNodes.get(cluster).add(node.name);
                       
//                System.out.print("\n"+" Node ID :" +node.name+"\n");
//                System.out.print("\n"+" Cluster :" +nodeToCluster.get(node) +"\n");
            }
            
            
            Map<Integer, Map<Integer, Integer>> BetweenEdges = new HashMap<Integer, Map<Integer, Integer>>();
            boolean isFeature = true;
            
            while(isFeature == true){
            
            
            BetweenEdges = IntraEdges(graph, nodeToCluster);
            
            boolean isbreak = false;
            isFeature = false;
            
                for(Integer cluster1 : BetweenEdges.keySet()){
                    
                    if(isbreak == true){
                       break;
                    }
                    
                    for(Integer cluster2 : BetweenEdges.get(cluster1).keySet()){
                        if(cluster1 == cluster2)
                            continue;
                
                        int edgesBetween = BetweenEdges.get(cluster1).get(cluster2);
                        int smallerCluster = 0;
                        if(NodeNumberInCluster.get(cluster1)<=NodeNumberInCluster.get(cluster2))
                            smallerCluster = cluster1;
                        else 
                            smallerCluster = cluster2;
                        
                        double edgesNodeRatio = (double)edgesBetween/NodeNumberInCluster.get(smallerCluster);
                        
                        
                        if((NodeNumberInCluster.get(smallerCluster) == 2) || (NodeNumberInCluster.get(smallerCluster) == 3) ){
                           if(edgesNodeRatio >= 1)
                               isFeature = true;
                        }
                        else if((NodeNumberInCluster.get(smallerCluster) == 4) || (NodeNumberInCluster.get(smallerCluster) == 5) ){
                            if(edgesNodeRatio >= 0.75)
                                isFeature = true;
                        }
                        else if((NodeNumberInCluster.get(smallerCluster) >=6) ){
                            if(edgesNodeRatio >= 0.5)
                                isFeature = true;
                        }
                        
                        // assign all the nodes in cluster2 to cluster1 
                        if(isFeature){
//                            System.out.print("\n edgesNodeRatio : "+edgesNodeRatio+"\n");
                            isbreak = true;
                            for(String nodeId : ClusterNodes.get(cluster2)){
                            
                                for (Node node : nodeToCluster.keySet()) {
                                    
                                    if(node.name == nodeId)
                                        nodeToCluster.put(node, cluster1);
                                }
                            
                            }
                         
                         Grouped.add(cluster1);   
                         
                         if(Grouped.contains(cluster2))
                             Grouped.remove(cluster2);
                            
                         NodeNumberInCluster = new HashMap<Integer, Integer>();
                         ClusterNodes = new HashMap<Integer, List<String>>();
                        
                         for (Node node : nodeToCluster.keySet()) {
       
                            int cluster = nodeToCluster.get(node);
                            int count = 1;
                
                            if(NodeNumberInCluster.containsKey(cluster)){
                                count = NodeNumberInCluster.get(cluster)+1;
                            }
                            NodeNumberInCluster.put(cluster, count);
                
                            if(!ClusterNodes.containsKey(cluster)){
                            ClusterNodes.put(cluster, new ArrayList<String>());
                            }
                            ClusterNodes.get(cluster).add(node.name);
                       
                            
                         }
                         break;
                    }
                }
              
            }
           
            
            
            
            /*
            for (Node node : nodeToCluster.keySet()) {
                System.out.print("\n"+" Node ID :" +node.name+"\n");
                System.out.print("\n"+" Cluster :" +nodeToCluster.get(node) +"\n");
            }
            
            /*
              for (Integer cluster : BetweenEdges.keySet()) {
                System.out.print("\n cluster :" +cluster +"\n");
                for (Integer cluster2 : BetweenEdges.get(cluster).keySet()) {
                    System.out.print(" to cluster "+cluster2 +" :"+" intraEdges "+BetweenEdges.get(cluster).get(cluster2)+"\n");
                }*/
            }
            PutToGroupNode(graph, Grouped);
           
        }
    
	public Map<Integer, Map<Integer, Integer>> IntraEdges(mxGraph graph, Map<Node,Integer> nodeToCluster){
            //  record the edges between different clusters
            Map<Integer, Map<Integer, Integer>> edges = new HashMap<Integer, Map<Integer, Integer>>();
            //  record the mxGraph ID mapping to their own cluster
            Map<String, Integer> IDToCluster = new HashMap<String, Integer>();
            
            //  map the Node ID to the cluster
            for(Node node : nodeToCluster.keySet()){
                IDToCluster.put(node.name, nodeToCluster.get(node));
            }
            
         
            
            mxIGraphModel model = graph.getModel();
                
                int childCount = model.getChildCount(graph.getDefaultParent());

		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChildAt(graph.getDefaultParent(), i);

                        int cluster1 = 0;
                        int cluster2 = 0;
                        
                        
			if(((mxCell)child).isEdge()){
                            
                            String source = ((mxCell)child).getTerminal(true).getId();
                            String target = ((mxCell)child).getTerminal(false).getId();
                            
                          
                            cluster1 = IDToCluster.get(source);
                            cluster2 = IDToCluster.get(target);
                            
                            //  if there's no clusterkey in the edges , then put it into there 
                            if(!(edges.containsKey(cluster1))){
                                edges.put(cluster1, new HashMap<Integer, Integer>());
                                edges.get(cluster1).put(cluster1, 0);
                            }
                            if(!(edges.containsKey(cluster2))){
                                edges.put(cluster2, new HashMap<Integer, Integer>());
                                edges.get(cluster2).put(cluster2, 0);
                            }
                            
                            
                            
                            
                            if(!(cluster1 == cluster2)){
                                int intraCount = 1;
                                if(edges.containsKey(cluster1) && edges.get(cluster1).containsKey(cluster2)){
                                
                                    intraCount = edges.get(cluster1).get(cluster2);
                                    intraCount = intraCount + 1;
                                    
                                    edges.get(cluster1).put(cluster2, intraCount);
                                    edges.get(cluster2).put(cluster1, intraCount);
                                
                                }
                                else if(edges.containsKey(cluster1) && !(edges.get(cluster1).containsKey(cluster2))){
                                    edges.get(cluster1).put(cluster2, intraCount);
                                    edges.get(cluster2).put(cluster1, intraCount);
                                }
                           }
                            else{
                                int intraCount = 0;
                               
                                    intraCount = edges.get(cluster1).get(cluster1);
                                    intraCount = intraCount + 1;
                                    
                                    edges.get(cluster1).put(cluster1, intraCount);
                                    
                            } 
                        }
                        
                }
            
            return edges;
        }
	
        
        public void PutToGroupNode(mxGraph graph, List<Integer> Grouped){
        
        
            
            if (!Grouped.isEmpty()) {
         
                Iterator<Integer> iterator ;
                Set<String> cycleVertices;
                Set<String> subCycle;
                Object parent = graph.getDefaultParent();
                
                mxGraphModel model = (mxGraphModel)graph.getModel();
                
                iterator = Grouped.iterator();
                
                int cluster = 0;
                int FeatureNodeIndex = 0;
                int nodeHide = 0;
                /**
                 * cycle count 
                 */
                while ( iterator.hasNext() ) {
              
//                    System.out.println("Cluster :");

                    cluster = iterator.next();

         
                    /**
                     * add a feature node to the graph and remember to set up its incoming and outgoing edge
                     * +2  because id starts from 2 
                     */
                   
                    String id = String.valueOf(graph.getModel().getChildCount(graph.getDefaultParent())+2);
                    Object newv= new Object();
                  
                    newv = graph.insertVertex(parent,id,"",100,100,350,350,"shape=ellipse;perimeter=ellipsePerimeter;");
                    List<mxCell> ChildNode = new ArrayList<mxCell>();
                    
                    for (String sub : ClusterNodes.get(cluster)) {
              
                        Object[] in = graph.getIncomingEdges(model.getCell(sub));
                
                        for(int i = 0; i< in.length;i++){
                    
                            String sourceID =  ((mxCell)in[i]).getTerminal(true).getId();
                        /**
                         *  insert edges from all the source to nodes in cycle 
                         */
                            if(!ClusterNodes.get(cluster).contains(sourceID)){
                                Object source = model.getCell(sourceID);
                                graph.insertEdge(parent, null, "", source, newv, "");
                            }
                        }
                        
                        
                        
                        
                        
                        
                        
                        Object[] out = graph.getOutgoingEdges(model.getCell(sub));
                        
                        for(int i = 0; i< out.length;i++){
                        
                            String targetID =  ((mxCell)out[i]).getTerminal(false).getId();
                        
                        /**
                         *  insert edges from nodes in cycle to the target not include in these group
                         */
                            if(!ClusterNodes.get(cluster).contains(targetID)){
                                Object target = model.getCell(targetID);
                                graph.insertEdge(parent, null, "", newv, target, "");
                            }
                        }
               //            ((mxCell)newv).insert((mxCell)graph.getModel().getChildAt(parent, Integer.parseInt(sub)-2));
                    
                        ChildNode.add((mxCell)model.getCell(sub));
                        
                        
                   }
                        ClusterFeatureNode.put((mxCell)newv, ChildNode);
                        
        
                }
            }
        }
        
        public Map<mxCell, List<mxCell>> getFeatureNodeList(){
        
            return ClusterFeatureNode;
        }
}
