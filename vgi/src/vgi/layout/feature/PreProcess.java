package vgi.layout.feature;


import com.mxgraph.model.*;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import java.util.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;


public  class PreProcess
{

    /**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public PreProcess(mxGraph graph)
	{
		CycleDetect(graph);
                
	}

	protected Map<mxCell, List<mxCell>> CycleFeatureNode = new HashMap<mxCell, List<mxCell>>();
	
        
        public void CycleDetect(mxGraph graph) {
                
            CycleDetector<String, DefaultEdge> cycleDetector;
                
            DefaultDirectedGraph<String, DefaultEdge> g;

            g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

            
            
           int childCount = graph.getModel().getChildCount(graph.getDefaultParent());
            
            Object parent = graph.getDefaultParent();
	    
            for (int i = 0; i < childCount; i++)
            {
		Object cell = graph.getModel().getChildAt(parent, i);

		if (((mxCell)cell).isVertex()){
                    g.addVertex(((mxCell)cell).getId());
                }
            }    
            
            
            for (int i = 0; i < childCount; i++)
            {
		Object cell = graph.getModel().getChildAt(parent, i);

                if(((mxCell)cell).isEdge()){
                
                    if((((mxCell)cell).getTerminal(true)!=null)  && (((mxCell)cell).getTerminal(false)!=null))
                    {
                        String preId = ((mxCell)cell).getTerminal(true).getId();
                        String succId = ((mxCell)cell).getTerminal(false).getId();
                    
                        g.addEdge(preId, succId);
                    }
                }
            }    
   
//            System.out.println(g.toString());

            cycleDetector = new CycleDetector<String, DefaultEdge>(g);
   
            if (cycleDetector.detectCycles()) {
         
                Iterator<String> iterator;
                Set<String> cycleVertices;
                Set<String> subCycle;
                mxGraphModel model = (mxGraphModel)graph.getModel();
                String cycle;

//                System.out.println("Cycles detected.");

                cycleVertices = cycleDetector.findCycles();
        
                int FeatureNodeIndex = 0;
                int nodeHide = 0;
                /**
                 * cycle count 
                 */
                while (! cycleVertices.isEmpty()) {
              
//                    System.out.println("Cycle:");

         
                    iterator = cycleVertices.iterator();
                    cycle = iterator.next();

         
                    subCycle = cycleDetector.findCyclesContainingVertex(cycle);
                    
                    /**
                     * add a feature node to the graph and remember to set up its incoming and outgoing edge
                     * +2  because id starts from 2 
                     */
                    String id = String.valueOf(graph.getModel().getChildCount(graph.getDefaultParent())+2);
                    Object newv= new Object();
              
                    newv = graph.insertVertex(parent,id,"",100,100,250,250,"shape=ellipse;perimeter=ellipsePerimeter;");
                    List<mxCell> ChildNode = new ArrayList<mxCell>();
                    
                    for (String sub : subCycle) {
              
                        
                        
                        Object[] in = graph.getIncomingEdges(model.getCell(sub));
                
                        for(int i = 0; i< in.length;i++){
                    
                            String sourceID =  ((mxCell)in[i]).getTerminal(true).getId();
                        /**
                         *  insert edges from all the source to nodes in cycle 
                         */
                            if(!subCycle.contains(sourceID)){
                                Object source = graph.getModel().getChildAt(parent, Integer.parseInt(sourceID)-2);
                                graph.insertEdge(parent, null, "", source, newv, "");
                            }
                        }
                        
                        
                     
                        Object[] out = graph.getOutgoingEdges(model.getCell(sub));
                        
                        for(int i = 0; i< out.length;i++){
                            if((((mxCell)out[i]).getTerminal(false))!=null){
                            String targetID =  ((mxCell)out[i]).getTerminal(false).getId();
                        
                        /**
                         *  insert edges from nodes in cycle to the target not include in these group
                         */
                            if(!subCycle.contains(targetID)){
                                Object target = graph.getModel().getChildAt(parent, Integer.parseInt(targetID)-2);
                                graph.insertEdge(parent, null, "", newv, target, "");
                            }
                            }
                        }
               //            ((mxCell)newv).insert((mxCell)graph.getModel().getChildAt(parent, Integer.parseInt(sub)-2));
                    
                        ChildNode.add((mxCell)graph.getModel().getChildAt(parent, Integer.parseInt(sub)-2));
                        
                        
                   }
                        CycleFeatureNode.put((mxCell)newv, ChildNode);
                        
                        
                        
                        /**
                         * Test file :
                       
                        List<mxCell> testlist = new ArrayList<mxCell>();
                        testlist = CycleFeatureNode.get(newv);
                        System.err.print("Feature NOde Id : "+((mxCell)newv).getId()+"\n");
                        for(mxCell cell : testlist)
                            System.out.print("Child Node : "+cell.getId()+"\n");
                         * 
                         */
                        
                        
                        newv = null;
                        ChildNode = null;
                    /*
                    for(int i = 0; i< cellTable.size();i++)
               //         ((mxCell)newv).insert(cellTable.get(i));
                    * 
                    */
                    for (String sub : subCycle) {
                      cycleVertices.remove(sub);
                   }
                   
                  //  vertex.add((mxCell)newv);
         }
      }

      // No cycles.  Just output properly ordered vertices.
      else {
         String v;
         TopologicalOrderIterator<String, DefaultEdge> orderIterator;

         orderIterator =
            new TopologicalOrderIterator<String, DefaultEdge>(g);
//         System.out.println("\nOrdering:");
         while (orderIterator.hasNext()) {
            v = orderIterator.next();
//            System.out.println(v);
         }
      }
           
   }
    
	
	public Map<mxCell, List<mxCell>> getFeatureNodeList(){
        
            return CycleFeatureNode;
        }

}
