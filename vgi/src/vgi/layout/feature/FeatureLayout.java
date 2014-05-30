/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.feature;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.*;
import javax.swing.SwingConstants;
import vgi.layout.circular.CircleLayout;

/**
 *
 * @author reng
 */
public class FeatureLayout extends mxGraphLayout{

    public FeatureLayout(mxGraph graph){
        super(graph);
    }
    @Override
    public void execute(Object parent) {
        this.graph.getModel().beginUpdate();
		try {
	
                    ClusterPreProcess clusterDetector = new ClusterPreProcess(this.graph);
                
                Map<mxCell, List<mxCell>> ClusterFeatureNode = new HashMap<mxCell, List<mxCell>>();
                Map<mxCell, List<mxCell>> CycleFeatureNode = new HashMap<mxCell, List<mxCell>>();
               
                // create a edges cell list of a graph used for check in clusteringlayout
                int childCount = this.graph.getModel().getChildCount(this.graph.getDefaultParent());

	            
                ClusterFeatureNode = clusterDetector.getFeatureNodeList();
                        
                List<mxCell> childs = new ArrayList<mxCell>();
                      
                // group the nodes in the same cluster : 
                        
                        Collection collection = ClusterFeatureNode.keySet();
                        Iterator iterator = collection.iterator();
                        while(iterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)iterator.next();
                            childs = ClusterFeatureNode.get(FeatureNode);
                            for(mxCell child : childs){
                       //         System.out.print("\n Child ID : "+child.getId()+"\n");
                                (FeatureNode).insert(child);
                            }
                        }
                        
                        iterator = collection.iterator();
                        
                        // if there's no feature node detect by clustering, then detect the cycle 
                        Iterator cycleIterator;
                        Collection cycleCollection;
                       
                        boolean isTreeOrHierarchical = true;
                        if(!iterator.hasNext()){
                                             
                           PreProcess cycleFilter = new PreProcess(this.graph);
                            CycleFeatureNode = cycleFilter.getFeatureNodeList();
                            cycleCollection = CycleFeatureNode.keySet();
                            cycleIterator = cycleCollection.iterator();
                            
                      
                            childs = new ArrayList<mxCell>();
                            
                            if(!cycleIterator.hasNext())
                                isTreeOrHierarchical = false;
                            
                            while(cycleIterator.hasNext()){
                           
                                    mxCell FeatureNode = (mxCell)cycleIterator.next();
                                    childs = CycleFeatureNode.get(FeatureNode);
                                    
                                    for(mxCell child : childs){
                                        (FeatureNode).insert(child);
                                    }
                             } // end of  (cycleIterator.hasNext())
                          
                        }
                   
              
                // execute the TreeLayout and ClusteringLayout : 
                if(isTreeOrHierarchical== true){        
                        TreeLayout treelayout = new TreeLayout(this.graph); 
                        treelayout.execute(graph.getDefaultParent());  
                       // treelayout.execute(cellTable.get("0"));
                }
                else if(isTreeOrHierarchical== false){
                       // this.doHierarchicalLayout();
                    mxHierarchicalLayout layout = new mxHierarchicalLayout(this.graph);
                    layout.setOrientation(SwingConstants.WEST);
                    layout.setFineTuning(false);
                    layout.setDisableEdgeStyle(false);
                    layout.execute(this.graph.getDefaultParent());
        
                }
             
                        
                        
                        iterator = collection.iterator();
                        
                          while(iterator.hasNext()) {
                              
                            ClusteringLayout clusterlayout = new ClusteringLayout(this.graph);
                            
                            mxCell FeatureNode = (mxCell)iterator.next();
                            
                            clusterlayout.setMoveCircle(true);
                            clusterlayout.setX0(FeatureNode.getGeometry().getX());
                            clusterlayout.setY0(FeatureNode.getGeometry().getY());
//                            System.out.print("\n X0 : "+FeatureNode.getGeometry().getX());
//                            System.out.print("\n Y0 : "+FeatureNode.getGeometry().getY());
                            clusterlayout.execute(FeatureNode);
                            
                        }
                          
                        
                        iterator = collection.iterator();
                        if(!iterator.hasNext()){
                           
                           cycleCollection = CycleFeatureNode.keySet();
                           cycleIterator = cycleCollection.iterator();
                            
                          while(cycleIterator.hasNext()) {
                              
                            CircleLayout circleLayout = new CircleLayout(this.graph);

                            mxCell FeatureNode = (mxCell)cycleIterator.next();
                            
                            circleLayout.setMoveCircle(true);
                            circleLayout.setX0(FeatureNode.getGeometry().getY());
                            circleLayout.setY0(FeatureNode.getGeometry().getX());
                     
                            circleLayout.setRadius(40);
                            circleLayout.execute(FeatureNode);
                        
                          }
                          
                          cycleIterator = cycleCollection.iterator();
                        
                          while(cycleIterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)cycleIterator.next();
                            this.graph.ungroupCells(new Object[]{FeatureNode});
                          }
                        }
                      
                        
                // ungroup the cluster :        
                          
                        iterator = collection.iterator();
                        while(iterator.hasNext()) {
                            mxCell FeatureNode = (mxCell)iterator.next();
                            this.graph.ungroupCells(new Object[]{FeatureNode});
                        }
                 childCount = this.graph.getModel().getChildCount(this.graph.getDefaultParent());

                 for(int i = 0; i < childCount-1; ++i){
               
                       Object child = graph.getModel().getChildAt(this.graph.getDefaultParent(), i);
                       if(((mxCell)child).isVertex()){
                            mxCell cell=(mxCell)child;
                            Object[] edges=graph.getEdges(cell);
                            
                            for(int j=0;j<edges.length;++j){
                                mxCell edge=(mxCell)edges[j];
                                mxCell source=(mxCell)edge.getSource();
                                mxCell target=(mxCell)edge.getTarget();
                
                                if(target==source){
                                    ArrayList<mxPoint> points = new ArrayList<mxPoint>();
                                    mxPoint loopCtrlPt=new mxPoint();
                                    loopCtrlPt.setX(source.getGeometry().getCenterX());
                                    loopCtrlPt.setY(source.getGeometry().getCenterY()+source.getGeometry().getHeight());
                                    points.add(loopCtrlPt);

                                    edge.getGeometry().setPoints(points);
                                }
                                
                            }
                        }
                 }       
                        
                        
//                 EdgeRoutingBranchingLayout layout = new EdgeRoutingBranchingLayout(this.graph);
//                 layout.execute(this.graph.getDefaultParent());
        
               } finally {
			this.graph.getModel().endUpdate();
		}
          
    }
    
}
