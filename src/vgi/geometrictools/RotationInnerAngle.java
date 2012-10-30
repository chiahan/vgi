package vgi.geometrictools;


import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.layout.*;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxCell;
import java.io.*;
import java.util.*;

public class RotationInnerAngle 
{

	protected double Angle;
        protected double cent_x;
        protected double cent_y;
	
        protected mxGraph Graph ;
	
        public RotationInnerAngle(mxGraph graph, double central_x, double central_y)
	{
            this.Graph = graph;
            cent_x= central_x;
            cent_y= central_y;
	}

	public double getAngle()
	{
		return Angle;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setAngle(double angle)
	{
		this.Angle = angle;
	}

	
        
   
	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
	 */
	public void execute(Object parent)
	{
           
            
            
           mxGraphModel model = (mxGraphModel)Graph.getModel();
           
           // nodeID record the cluster sub-nodes ID
           List<String> nodeID = new ArrayList<String>();
           ParentChildID(model, parent, nodeID);
           
           double longestDistance = 0;
           double distance;
           
           Object sourceNode = new mxCell();
           Object targetNode = new mxCell();
           
            for (String sub : nodeID) {
           
                Object node = model.getCell(sub);
                Object[] in = Graph.getIncomingEdges(model.getCell(sub));
                
                        for(int i = 0; i< in.length;i++){
                    
                            String sourceID =  ((mxCell)in[i]).getTerminal(true).getId();
                    
                            if(nodeID.contains(sourceID)){
                                Object source = model.getCell(sourceID);
                                
                                double x1 = ((mxCell)node).getGeometry().getCenterX();
                                double y1 = ((mxCell)node).getGeometry().getCenterY();
                                double x2 = ((mxCell)source).getGeometry().getCenterX();
                                double y2 = ((mxCell)source).getGeometry().getCenterY();
                                distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));  
                                
                                if(distance > longestDistance){
                                   longestDistance = distance ;
                                   sourceNode = source;
                                   targetNode = node;
                                }
                            }
                        }
                     
                        Object[] out = Graph.getOutgoingEdges(model.getCell(sub));
                        
                        for(int i = 0; i< out.length;i++){
                        
                            String targetID =  ((mxCell)out[i]).getTerminal(false).getId();
               
                            if(nodeID.contains(targetID)){
                               
                                Object target = model.getCell(targetID);
                                
                                double x1 = ((mxCell)node).getGeometry().getCenterX();
                                double y1 = ((mxCell)node).getGeometry().getCenterY();
                                double x2 = ((mxCell)target).getGeometry().getCenterX();
                                double y2 = ((mxCell)target).getGeometry().getCenterY();
                                distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));  
                                
                                if(distance > longestDistance){
                                   longestDistance = distance ;
                                   sourceNode = node;
                                   targetNode = target;
                                }
                        }
                   }
           }
            
            if(((mxCell)sourceNode).getGeometry()!=null && ((mxCell)targetNode).getGeometry()!=null){
            double vector_x = ((mxCell)sourceNode).getGeometry().getCenterX()-((mxCell)targetNode).getGeometry().getCenterX();
            double vector_y = ((mxCell)sourceNode).getGeometry().getCenterY()-((mxCell)targetNode).getGeometry().getCenterY();
            
            //Vector2D.angle(x, y) == Math.atan2(h_x, h_x)
            
            Angle = Math.atan2(vector_y,vector_x);
            
            if(Angle>= -1 * Math.PI/2 && Angle<Math.PI/2)
                Angle = -1 * Angle;
            else if(Angle>=Math.PI / 2 && Angle<Math.PI)
                Angle = Math.PI - Angle;
            else if(Angle>=Math.PI * -1 && Angle<Math.PI / 2 * -1)
                Angle = Math.PI - Angle;
            }
            else
                Angle = 0;
	}
        
        public static void ParentChildID(mxGraphModel model, Object parent, List<String> nodeID){
        
        
            int childCount = model.getChildCount(parent);

		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChildAt(parent, i);
                        
                        if(((mxCell)child).isVertex()){
                        
                            nodeID.add(((mxCell)child).getId());
                        
                        }
                }
        }



}
