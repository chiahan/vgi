package vgi;


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

public class RotationAngle 
{

	protected double Angle;
        protected double cent_x;
        protected double cent_y;
	
        protected mxGraph Graph ;
	
        public RotationAngle(mxGraph graph, double central_x, double central_y)
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
           
           Object headNode = new mxCell();
           Object innerNode = new mxCell();
           // headNode's  x  coordinate
           double h_x = 1000;
           
            for (String sub : nodeID) {
              
                        Object[] in = Graph.getIncomingEdges(model.getCell(sub));
                
                        for(int i = 0; i< in.length;i++){
                    
                            String sourceID =  ((mxCell)in[i]).getTerminal(true).getId();
                    
                            if(!nodeID.contains(sourceID)){
                                Object source = model.getCell(sourceID);
                                if(((mxCell)source).getGeometry().getCenterX()<h_x){
                                    h_x = ((mxCell)source).getGeometry().getCenterX();
                                    headNode = source;
                                    innerNode = model.getCell(sub);
                                }
                            }
                        }
                     
                        Object[] out = Graph.getOutgoingEdges(model.getCell(sub));
                        
                        for(int i = 0; i< out.length;i++){
                        
                            String targetID =  ((mxCell)out[i]).getTerminal(false).getId();
               
                            if(!nodeID.contains(targetID)){
                                Object target = model.getCell(targetID);
                                 if(((mxCell)target).getGeometry().getCenterX()<h_x){
                                    h_x = ((mxCell)target).getGeometry().getCenterX();
                                    headNode = target;
                                    innerNode = model.getCell(sub);
                                }
                            }
                        }
                   }
            if((((mxCell)innerNode).getGeometry()!= null) && (((mxCell)headNode).getGeometry()!=null)){
            Object closestNode = new mxCell();
            double min_distance = 1000;
            double distance;
            for (String sub : nodeID) {
                Object node = model.getCell(sub);
                double x1 = ((mxCell)node).getGeometry().getCenterX();
                double y1 = ((mxCell)node).getGeometry().getCenterY();
                
                distance = min_distance;
                double x2 = ((mxCell)headNode).getGeometry().getCenterX();
                double y2 = ((mxCell)headNode).getGeometry().getCenterY();
                
                distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));     
             
                if(distance<min_distance){
                    min_distance = distance;
                    closestNode = node;
                }
            }
            
            double vector_x1 = ((mxCell)innerNode).getGeometry().getCenterX()-cent_x;
            double vector_y1 = ((mxCell)innerNode).getGeometry().getCenterY()-cent_y;
            double vector_x2 = ((mxCell)closestNode).getGeometry().getCenterX()-cent_x;
            double vector_y2 = ((mxCell)closestNode).getGeometry().getCenterY()-cent_y;
            
            double angle_x1 = Math.atan2(vector_y1,vector_x1);
            double angle_x2 = Math.atan2(vector_y2,vector_x2);
            
            if(angle_x2>angle_x1){
                 Angle = angle_x2 - angle_x1;
            }
            else if(angle_x2<angle_x1){
                 Angle = 2*Math.PI - (angle_x1-angle_x2);
            }
            }
            
            /*
            if(Angle>Math.PI)
                Angle = Angle - Math.PI*2;
            if(Angle<Math.PI*-1)
                Angle = Angle + Math.PI*2;
                * 
                */
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
