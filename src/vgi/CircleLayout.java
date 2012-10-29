package vgi;



import com.mxgraph.layout.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class CircleLayout extends mxGraphLayout
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
        private boolean[] marked;        // marked[v] = has vertex v been marked?
        private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
        private boolean[] onStack;  
        public List<Object> vertices;
        public Map< Object, Integer > featureNode;
        public int DFSCount = 0;
	public CircleLayout(mxGraph graph)
	{
		this(graph, 100);
	}

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public CircleLayout(mxGraph graph, double radius)
	{
		super(graph);
		this.radius = radius;
	}

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

	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
	 */
	public void execute(Object parent)
	{
		mxIGraphModel model = graph.getModel();

		// Moves the vertices to build a circle. Makes sure the
		// radius is large enough for the vertices to not
		// overlap
		model.beginUpdate();
		try
		{
			// Gets all vertices inside the parent and finds
			// the maximum dimension of the largest vertex
			double max = 0;
			Double top = null;
			Double left = null;
			vertices = new ArrayList<Object>();
                        featureNode = new HashMap<Object, Integer>();
			int childCount = model.getChildCount(parent);

			for (int i = 0; i < childCount; i++)
			{
				Object cell = model.getChildAt(parent, i);

				if (!isVertexIgnored(cell))
				{
					vertices.add(cell);
					mxRectangle bounds = getVertexBounds(cell);

					if (top == null)
					{
						top = bounds.getY();
					}
					else
					{
						top = Math.min(top, bounds.getY());
					}

					if (left == null)
					{
						left = bounds.getX();
					}
					else
					{
						left = Math.min(left, bounds.getX());
					}

					max = Math.max(max, Math.max(bounds.getWidth(), bounds
							.getHeight()));
				}
				else if (!isEdgeIgnored(cell))
				{
					if (isResetEdges())
					{
						graph.resetEdge(cell);
					}
	
					if (isDisableEdgeStyle())
					{
						setEdgeStyleEnabled(cell, false);
					}
				}
			}

			int vertexCount = vertices.size();
                        if(vertexCount>0){ 
                        
                            double r = Math.max(vertexCount * max / Math.PI, radius);

                            // Moves the circle to the specified origin
                            if (moveCircle)
                            {
                                    top = x0;
                                    left = y0;
                            }
                            marked  = new boolean[vertexCount];
                            onStack = new boolean[vertexCount];
                            edgeTo  = new int[vertexCount];


                            for (int v = 0; v < vertexCount; v++){
                                marked[v] = false;
                                onStack[v]= false;
                                featureNode.put(vertices.get(v),v);
                            }


                            dfs(graph, parent, 0);

                            int x = DFSCount;


                            for(int i = 0 ; i< vertexCount ; i++){
                                x = edgeTo[x];
                            }
                            circle(vertices.toArray(), r, left.doubleValue(), top.doubleValue());
                        }
		}
		finally
		{
			model.endUpdate();
		}
	}

	/**
	 * Executes the circular layout for the specified array
	 * of vertices and the given radius.
	 */
	public void circle(Object[] vertices, double r, double left, double top)
	{
		int vertexCount = vertices.length;
                
		double phi = 2 * Math.PI / vertexCount;
               
                Object nodeToPosition = new Object();
                int traverseIndex = DFSCount ;
                nodeToPosition = vertices[traverseIndex];
		for (int i = 0; i < vertexCount; i++)
		{
                    
			if (isVertexMovable(nodeToPosition))
			{
                     
                            double x = 0;
                            double y = 0;
                            x = left + r + r * Math.sin( i * phi);
                            y = top + r + r * Math.cos( i * phi);
                           
                            setVertexLocation(nodeToPosition,
						left + r + r * Math.sin( i * phi), top + r + r
								* Math.cos( i * phi));
			}
                        traverseIndex = edgeTo[traverseIndex] ;
                        nodeToPosition = vertices[traverseIndex];
		}
	}
        
        
        private void dfs(mxGraph graph, Object parent, int v) {
        
            
        
        onStack[v] = true;
        marked[v] = true;
        int w = 0;
        
        Object preNode = new Object();
        
        //use iterator to find the object by number in vertices : v
        
        for(Object nodeInMap : featureNode.keySet()){
            if(featureNode.get(nodeInMap) == v){
                preNode = nodeInMap;
            }
        }
        
        // use outgoingEdges to get the next node 
        
        Object[] out = graph.getOutgoingEdges(preNode);
        /*
        System.out.print(" out  :"+((mxCell)out[0]).getId()+"\n");
        System.out.print(" source ID :"+((mxCell)preNode).getId()+"\n");
        */
        for(int outgoing = 0; outgoing< out.length;outgoing++){
            
            String targetID =  ((mxCell)out[outgoing]).getTerminal(false).getId();
           
            Object target = new Object();
            
            for(Object nodeInMap : featureNode.keySet()){
                if(((mxCell)nodeInMap).getId()==targetID){
                    target = nodeInMap;
                }
            }
            
            if(featureNode.containsKey(target)){
     
                w = featureNode.get(target);
                if(!marked[w]){
                    edgeTo[w] = v;
                    DFSCount = DFSCount + 1;
                 //   System.out.print("\n"+"edgeTo[ "+w+" ] : "+ v +"\n");
                    dfs(graph, parent, w);
                }     
                else if(onStack[w]){
                 
                    if(DFSCount == featureNode.size()-1)
                    {    
                        DFSCount = v;
                        edgeTo[w] = v;
                    }
                    break;
                }
            }
       }
        onStack[v] = false;
        
    }

}
