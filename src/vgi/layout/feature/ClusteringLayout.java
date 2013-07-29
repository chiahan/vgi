package vgi.layout.feature;


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
import vgi.layout.edgerouting.Edge;
import vgi.layout.edgerouting.Node;
import vgi.layout.edgerouting.OptimizerModularity;
import vgi.geometrictools.RotationAngle;
import vgi.geometrictools.RotationInnerAngle;

public class ClusteringLayout extends mxGraphLayout
{

	/**
	 * Integer specifying the size of the radius. Default is 100.
	 */
	protected double radius;

	/**
	 * Boolean specifying if the circle should be moved to the top,
	 * left corner specified by x0 and y0. Default is false.
	 */
	protected boolean moveCircle = true;

	/**
	 * Integer specifying the left coordinate of the circle.
	 * Default is 0.
	 */
	protected double x0 = 0;

	/**
	 * Integer specifying the top coordinate of the circle.
	 * Default is 0.
	 */
	protected double y0 = 0;

	/**
	 * Specifies if all edge points of traversed edges should be removed.
	 * Default is true.
	 */
	protected boolean resetEdges = true;

	/**
	 *  Specifies if the STYLE_NOEDGESTYLE flag should be set on edges that are
	 * modified by the result. Default is true.
	 */
	protected boolean disableEdgeStyle = true;
        /**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public ClusteringLayout(mxGraph graph)
	{
		super(graph);
	}

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	

	/**
	 * @return the radius
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	/**
	 * @return the moveCircle
	 */
	public boolean isMoveCircle()
	{
		return moveCircle;
	}

	/**
	 * @param moveCircle the moveCircle to set
	 */
	public void setMoveCircle(boolean moveCircle)
	{
		this.moveCircle = moveCircle;
	}

	/**
	 * @return the x0
	 */
	public double getX0()
	{
		return x0;
	}

	/**
	 * @param x0 the x0 to set
	 */
	public void setX0(double x0)
	{
		this.x0 = x0;
	}

	/**
	 * @return the y0
	 */
	public double getY0()
	{
		return y0;
	}

	/**
	 * @param y0 the y0 to set
	 */
	public void setY0(double y0)
	{
		this.y0 = y0;
	}

	/**
	 * @return the resetEdges
	 */
	public boolean isResetEdges()
	{
		return resetEdges;
	}

	/**
	 * @param resetEdges the resetEdges to set
	 */
	public void setResetEdges(boolean resetEdges)
	{
		this.resetEdges = resetEdges;
	}

	/**
	 * @return the disableEdgeStyle
	 */
	public boolean isDisableEdgeStyle()
	{
		return disableEdgeStyle;
	}

	/**
	 * @param disableEdgeStyle the disableEdgeStyle to set
	 */
	public void setDisableEdgeStyle(boolean disableEdgeStyle)
	{
		this.disableEdgeStyle = disableEdgeStyle;
	}

        public static Map<String,Map<String,Double>> readGraph(mxGraph graph, Object parent) {
		
            
                Map<String,Map<String,Double>> result = new HashMap<String,Map<String,Double>>();
		
                // get the childs ID from the parent
                List<String> nodeID = new ArrayList<String>();
                mxGraphModel model = ((mxGraphModel)graph.getModel());
                ParentChildID(model, parent, nodeID);
                
                
                int childCount = 0;
                childCount = graph.getModel().getChildCount(graph.getDefaultParent());
            
                
//                System.out.print(" \n edgeCount : "+childCount+"\n");
                
                for (int i = 0; i < childCount; i++)
		{
                    mxCell child = null;//new Object();
                    child = (mxCell)graph.getModel().getChildAt(graph.getDefaultParent(), i);
                    
                    //if(((mxCell)child).isEdge())
                    if(child.isEdge() && child.getTerminal(true)!=null && child.getTerminal(false)!=null)
                        {
                            String source = ((mxCell)child).getTerminal(true).getId();
                            String target = ((mxCell)child).getTerminal(false).getId();
                           
                            if((nodeID.contains(source)) && (nodeID.contains(target)))
                            {  
                                double weight = 1;
                                
                                if(result.get(source) == null) 
                                result.put(source, new HashMap<String,Double>());
				
                                result.get(source).put(target, weight);
                            }
                       }
		}
                
                return result;
                
	}
        
        public static Map<String,Map<String,Double>> makeSymmetricGraph
			(Map<String,Map<String,Double>> graph) {
		Map<String,Map<String,Double>> result = new HashMap<String,Map<String,Double>>();
		for (String source : graph.keySet()) {
			for (String target : graph.get(source).keySet()) {
				double weight = graph.get(source).get(target);
				double revWeight = 0.0f;
				if (graph.get(target) != null && graph.get(target).get(source) != null) {
					revWeight = graph.get(target).get(source);
				}
				if (result.get(source) == null) result.put(source, new HashMap<String,Double>());
				result.get(source).put(target, weight+revWeight);
				if (result.get(target) == null) result.put(target, new HashMap<String,Double>());
				result.get(target).put(source, weight+revWeight);
			}
		}
		return result;
	}
        
        public static Map<String,Node> makeNodes(Map<String,Map<String,Double>> graph) {
		Map<String,Node> result = new HashMap<String,Node>();
		for (String nodeName : graph.keySet()) {
            double nodeWeight = 0.0;
            for (double edgeWeight : graph.get(nodeName).values()) {
                nodeWeight += edgeWeight;
            }
			result.put(nodeName, new Node(nodeName, nodeWeight));
		}
		return result;
	}
        
        public static List<Edge> makeEdges(Map<String,Map<String,Double>> graph, 
            Map<String,Node> nameToNode) {
        List<Edge> result = new ArrayList<Edge>();
        for (String sourceName : graph.keySet()) {
            for (String targetName : graph.get(sourceName).keySet()) {
                Node sourceNode = nameToNode.get(sourceName);
                Node targetNode = nameToNode.get(targetName);
                double weight = graph.get(sourceName).get(targetName);
                result.add( new Edge(sourceNode, targetNode, weight) );
            }
        }
        return result;
    }
        
        public static Map<Node,double[]> makeInitialPositions(List<Node> nodes, boolean is3d) {
        Map<Node,double[]> result = new HashMap<Node,double[]>();
		for (Node node : nodes) {
            double[] position = { Math.random() - 0.5,
                                  Math.random() - 0.5,
                                  is3d ? Math.random() - 0.5 : 0.0 };
            result.put(node, position);
		}
		return result;
	}
        
        private static void writePositions(Map<Node,double[]> nodeToPosition, 
            Map<Node,Integer>nodeToCluster, String filename) {
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(filename));
			for (Node node : nodeToPosition.keySet()) {
				double[] position = nodeToPosition.get(node);
                int cluster = nodeToCluster.get(node);
				file.write(node.name + " " + position[0] + " " + position[1] 
                                     + " " + position[2] + " " + cluster);
                file.write("\n");
			}
			file.close();
		} catch (IOException e) {
		      System.err.println("Exception while writing the graph:"); 
			  System.err.println(e);
			  System.exit(1);
		}
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
	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
	 */
	public void execute(Object parent)
	{
           
           mxGraphModel model = (mxGraphModel)graph.getModel();
           
            	model.beginUpdate();
		try
		{
			List<String> nodeIDinParent = new ArrayList<String>();
                        
                        Map<String,Map<String,Double>> featureGraph = readGraph(graph, parent);
                        featureGraph = makeSymmetricGraph(featureGraph);
                        Map<String,Node> nameToNode = makeNodes(featureGraph);
                        List<Node> nodes = new ArrayList<Node>(nameToNode.values());
                        List<Edge> edges = makeEdges(featureGraph,nameToNode);
                        Map<Node,double[]> nodeToPosition = makeInitialPositions(nodes, false);
	
                        new MinimizerBarnesHut(nodes, edges, 0.0, 1.0, 0.05).minimizeEnergy(nodeToPosition, 100);
                        
                        
                        Map<Node,Integer> nodeToCluster = new OptimizerModularity().execute(nodes, edges, false);
                   /*    
                        for (Node node : nodeToCluster.keySet()) {
                            System.out.print("\n"+" Node ID :" +node.name+"\n");
                            System.out.print("\n"+" Cluster :" +nodeToCluster.get(node) +"\n");
                        }
                    */
                        double scale = 30;
                        
                        double minX, maxX, minY, maxY;
                        minX = Float.MAX_VALUE; maxX = -Float.MAX_VALUE;
                        minY = Float.MAX_VALUE; maxY = -Float.MAX_VALUE;
                        for (Node node : nodeToPosition.keySet()) {
                            double[] position = nodeToPosition.get(node);
                            double diameter = Math.sqrt(node.weight);
                            minX = Math.min(minX, position[0] - diameter/2);
                            maxX = Math.max(maxX, position[0] + diameter/2);
                            minY = Math.min(minY, position[1] - diameter/2);
                            maxY = Math.max(maxY, position[1] + diameter/2);
                        }
                      
                        
                        
                        for(Node node :nodeToPosition.keySet()){
                     
                            int positionX = (int)Math.round((nodeToPosition.get(node)[0] - minX) * scale);
                            int positionY = (int)Math.round((nodeToPosition.get(node)[1] - minY) * scale);
                            //int diameter  = (int)Math.round(Math.sqrt(node.weight) * scale);
                            int diameter = (int)scale;
                            Object child = model.getCell(node.name);
                           
                            setVertexLocation(child, x0 + positionX-diameter/2 , y0 + positionY-diameter/2 );
                        }
                        
                        for(Node node :nodeToPosition.keySet()){
                            int diameter = (int)scale;
                            int dx ;
                            int dy ;
                            double distance;
                            int minlength = 50;
                            int maxlength = 200;
                            for(Node node2 :nodeToPosition.keySet()){
                                if(node != node2){
                                    int X1 = (int)Math.round((nodeToPosition.get(node)[0] - minX) * scale) - diameter/2;
                                    int Y1 = (int)Math.round((nodeToPosition.get(node)[1] - minY) * scale) - diameter/2;
                                    int X2 = (int)Math.round((nodeToPosition.get(node2)[0] - minX) * scale) - diameter/2;
                                    int Y2 = (int)Math.round((nodeToPosition.get(node2)[1] - minY) * scale) - diameter/2;
                                    
                                    dx = 0;
                                    dy = 0;
                                    
                                    dx = X2 - X1 ;
                                    dy = Y2 - Y1 ;
                                    distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                                    if(distance < minlength)
                                    {
                                   
                                        dx = (int)Math.round((minlength/2)*(dx/distance));
                                        dy = (int)Math.round((minlength/2)*(dy/distance));
                                        
                                        Object child = model.getCell(node.name);
                                        Object child2 = model.getCell(node2.name);
                                        
                                        setVertexLocation(child, x0 + X1 - dx , y0 + Y1 - dy );
                                        setVertexLocation(child2, x0 + X2 + dx , y0 + Y2 + dy );
                                    }
                                   
                                    
                                }
                            }
                        }
                        
                       
                        
                        double central_x = 0;
                        double central_y = 0;
                        
                        int childCount = model.getChildCount(parent);
                        int vertexCount = 0;
                        for (int i = 0; i < childCount; i++)
                        {
                            Object child = model.getChildAt(parent, i);
                        
                            if(((mxCell)child).isVertex()){
                                central_x = central_x + ((mxCell)child).getGeometry().getCenterX();
                                central_y = central_y + ((mxCell)child).getGeometry().getCenterY();
                                vertexCount++;
                            }
                         }
                        central_x = central_x / vertexCount;
                        central_y = central_y / vertexCount;
                        
                        RotationAngle rotate = new RotationAngle(graph, central_x, central_y);
                        rotate.execute(parent);
                        double angle = rotate.getAngle();
                        for (int i = 0; i < childCount; i++)
                        {
                            Object child = model.getChildAt(parent, i);
                        
                            if(((mxCell)child).isVertex()){
                            
                                double oldx = ((mxCell)child).getGeometry().getCenterX();
                                double oldy = ((mxCell)child).getGeometry().getCenterY();
                                
                                double vector_x = ((mxCell)child).getGeometry().getCenterX() - central_x;
                                double vector_y = ((mxCell)child).getGeometry().getCenterY() - central_y;
                                
                                double newx = (vector_x * Math.cos(angle) - vector_y * Math.sin(angle) + central_x);
                                double newy = (vector_x * Math.sin(angle) + vector_y * Math.cos(angle) + central_y);
                                
                                setVertexLocation(child, newx , newy );
                               
                            }
                        }
                        RotationInnerAngle rotate2 = new RotationInnerAngle(graph, central_x, central_y);
                        rotate2.execute(parent);
                        double angle2 = rotate2.getAngle();
                        for (int i = 0; i < childCount; i++)
                        {
                            Object child = model.getChildAt(parent, i);
                        
                            if(((mxCell)child).isVertex()){
                            
                                double oldx = ((mxCell)child).getGeometry().getCenterX();
                                double oldy = ((mxCell)child).getGeometry().getCenterY();
                                
                                double vector_x = ((mxCell)child).getGeometry().getCenterX() - central_x;
                                double vector_y = ((mxCell)child).getGeometry().getCenterY() - central_y;
                                
                                double newx = (vector_x * Math.cos(angle2) - vector_y * Math.sin(angle2) + central_x);
                                double newy = (vector_x * Math.sin(angle2) + vector_y * Math.cos(angle2) + central_y);
                                
                                setVertexLocation(child, newx , newy );
                               
                            }
                        }
                     
                       //(new GraphFrame(nodeToPosition, nodeToCluster)).setVisible(true);
                        
		}
		finally
		{
			model.endUpdate();
		}
	}



}
