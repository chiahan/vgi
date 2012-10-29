package vgi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.mxgraph.layout.*;

import com.mxgraph.model.mxCellPath;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.model.mxCell;

public class EdgeRoutingLayout extends mxGraphLayout
{

    public enum CrossingType { none, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10 };
	/**
	 * Specifies the spacing between the edges. Default is 20.
	 */
	protected int spacing;

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public EdgeRoutingLayout(mxGraph graph)
	{
		this(graph, 30);
	}

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public EdgeRoutingLayout(mxGraph graph, int spacing)
	{
		super(graph);
		this.spacing = spacing;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
	 */
	public void execute(Object parent)
	{
		
		graph.getModel().beginUpdate();
		try
		{
                 
                mxIGraphModel model = graph.getModel();
		int childCount = model.getChildCount(parent);
           
                for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChildAt(parent, i);
                        if(((mxCell)child).isEdge()){
                            mxGraphView view = graph.getView();
                        Object src = view.getVisibleTerminal(child, true);
                        Object trg = view.getVisibleTerminal(child, false);
                        
                        int max_depth_to_go = 5;
                        List<mxPoint> points = new ArrayList<mxPoint>();
                        List<mxCell> ignore = new ArrayList<mxCell>();
                        
                        mxPoint source = new mxPoint(0,0);
                        mxPoint target = new mxPoint(0,0);
                        
                        double src_x, src_y, trg_x, trg_y;
                        src_x = (((mxCell)src).getGeometry().getCenterX());
                        src_y = (((mxCell)src).getGeometry().getCenterY());
                        trg_x = (((mxCell)trg).getGeometry().getCenterX());
                        trg_y = (((mxCell)trg).getGeometry().getCenterY());
                        
                       
                        for (int j = 0; j < childCount; j++)
                        {
                            Object node = model.getChildAt(parent, j);
                            if(((mxCell)node).isVertex()){
                            ignore.add((mxCell)node);
                            }
                        }
                        ignore.remove(src);
                        ignore.remove(trg);
               
                        Reroute(((mxCell)child), src_x, src_y, trg_x, trg_y,max_depth_to_go, ignore, points);
                        if (graph.isCellMovable(child))
                        {
                            setEdgePoints(child, points);
                        }
                        
                        }
                 }
                
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}

	public void route(mxCell edge) {
		if (!(edge.isEdge())) {
			return;
		}
		if (!(this.graph.isCellMovable(edge))) {
			return;
		}
		mxIGraphModel model = this.graph.getModel();
		if (model == null) {
			return;
		}
		mxICell source = edge.getSource();
		if ((source == null) || !(source instanceof mxCell)) {
			return;
		}
		mxGeometry sourceGeometry = source.getGeometry();
		if (sourceGeometry == null) {
			return;
		}
		mxICell target = edge.getTarget();
		if ((target == null) || !(target instanceof mxCell)) {
			return;
		}
		mxGeometry targetGeometry = target.getGeometry();
		if (targetGeometry == null) {
			return;
		}
		Object[] objectArray = this.graph.getChildVertices(this.graph.getDefaultParent());
		if (objectArray == null) {
			return;
		}
		List<mxCell> ignore = new ArrayList<mxCell>();
		for (int index = 0; index < objectArray.length; index++) {
			if (objectArray[index] instanceof mxCell) {
				ignore.add((mxCell) objectArray[index]);
			}
		}  // End for (int index = 0; index < objectArray.length; index++)
		ignore.remove((mxCell) source);
		ignore.remove((mxCell) target);
		List<mxPoint> points = new ArrayList<mxPoint>();

		Reroute(edge,
				sourceGeometry.getCenterX(),
				sourceGeometry.getCenterY(),
				targetGeometry.getCenterX(),
				targetGeometry.getCenterY(),
				5,
				ignore,
				points);

		model.beginUpdate();
		try {
			setEdgePoints(edge, points);
		} finally {
			model.endUpdate();
		}
	}  // End public void route(mxCell edge)

        public void Reroute(mxCell edge, double src_x, double src_y, double trg_x, double trg_y, int max_depth_to_go, List<mxCell> ignore, List<mxPoint> points){
        
       
            if(max_depth_to_go==0)
                return;
            
            double x1 = src_x;
            double y1 = src_y;
            double x2 = trg_x;
            double y2 = trg_y;
            
            mxPoint src = new mxPoint();
            mxPoint trg = new mxPoint();
            src.setX(x1);
            src.setY(y1);
            trg.setX(x2);
            trg.setY(y2);
            
            int epsilon = 20;
            
            mxIGraphModel model = graph.getModel();
           
            if(ignore.size()==0)
                return;
            /*
            System.out.print("size: "+ignore.size());
            System.out.print("srcx:  "+src.getX()+"    ");
            System.out.print("srcy:  "+src.getY());
            System.out.print("trgx:  "+trg.getX()+"    ");
            System.out.print("trgy:  "+trg.getY());
            System.out.print("getx:  "+ignore.get(0).getGeometry().getX()+"    ");
            System.out.print("gety:  "+ignore.get(0).getGeometry().getY());
            */
            for (int i = 0; i < ignore.size(); i++)
            {
                if(ignore.isEmpty())
                    return ;
                
                mxCell child = ignore.get(i);
            
                double width = ((mxCell)child).getGeometry().getWidth();
                double height = ((mxCell)child).getGeometry().getHeight();
                
                CrossingType crossing = Crosses(child, x1, y1, x2, y2);
           
                if(crossing!= CrossingType.none)
                {
                    i = i-1;  //when there's some kind of crossingType , it will remove one cross node so needed to minus and adjust the index
                   
                    mxPoint p = new mxPoint(0,0);
                    mxPoint p1 = new mxPoint(((mxCell)child).getGeometry().getX()-epsilon,((mxCell)child).getGeometry().getY()-epsilon);
                    mxPoint p2 = new mxPoint(((mxCell)child).getGeometry().getX()-epsilon,((mxCell)child).getGeometry().getY() + height + epsilon);
                    mxPoint p3 = new mxPoint(((mxCell)child).getGeometry().getX()+ width + epsilon,((mxCell)child).getGeometry().getY() + height + epsilon);
                    mxPoint p4 = new mxPoint(((mxCell)child).getGeometry().getX()+ width + epsilon,((mxCell)child).getGeometry().getY()-epsilon);;
                    
                    
                  
                    switch(crossing){
                        case t1:
                            p = p1;
                            break;
                        case t2:
                            p = p2;
                            break;
                        case t3:
                            p = p3;
                            break;
                        case t4:
                            p = p4;
                            break;
                        case t5:
                            p = MinCut(child, src, trg, p1, p3 );
                            break;
                        case t6:
                            p = MinCut(child, src, trg, p2, p4 );
                            break;
                        case t7:
                        {
                            if(x1<((mxCell)child).getGeometry().getX()+width/2)
                                p = MinCut(child, src, trg, p1, p2 );
                            else
                                p = MinCut(child, src, trg, p3, p4 );
                        }
                            break;
                        case t8:
                            p = MinCut(child, src, trg, p1, p3 );
                            break;
                        case t9:
                            p = MinCut(child, src, trg, p2, p4 );
                            break;
                        case t10:
                        {
                            if(y1<((mxCell)child).getGeometry().getY()+height/2)
                                p = MinCut(child, src, trg, p1, p4 );
                            else
                                p = MinCut(child, src, trg, p2, p3 );
                        }
                            break;
                            
                      
                    }
             //       ignore.add(child);
                    
                     ignore.remove(child);
                     
                     Reroute(edge, src_x, src_y, p.getX(), p.getY(),max_depth_to_go-1, ignore, points);
                   
                     points.add(p);
                     
                     Reroute(edge, p.getX(), p.getY(), trg_x, trg_y,max_depth_to_go-1, ignore, points);
                    
                    //Reroute(src, p, max_depth_to_go-1);
                    //Reroute(p, trg, max_depth_to_go-1);
                    
                //    points.add(p);
                }
                
            }
            return;
            
        }
        public mxPoint MinCut(mxCell node, mxPoint origin, mxPoint destination, mxPoint midpoint1, mxPoint midpoint2){
            
           double area1 = Area(node, origin, midpoint1) + Area(node, midpoint1, destination);
            double area2 = Area(node, origin, midpoint2) + Area(node, midpoint2, destination);
            
            if(area1<area2)
                return midpoint1;
            else 
                return midpoint2;
        
        
        }
        
        public double Area(mxCell node, mxPoint origin, mxPoint destination){
        
        double x1 = origin.getX();
        double y1 = origin.getY();
        double x2 = destination.getX();
        double y2 = destination.getY();
        
        double top = ((mxCell)node).getGeometry().getY();
        double bottom = ((mxCell)node).getGeometry().getY()+((mxCell)node).getGeometry().getHeight();
        double left = ((mxCell)node).getGeometry().getX();
        double right = ((mxCell)node).getGeometry().getX()+((mxCell)node).getGeometry().getWidth();
        
        CrossingType c = Crosses(node, x1, y1, x2, y2);
        
        double d1 = 0;
        double d2 = 0;
        
        switch(c){
        
            case none:
                return 0;
            case t1:
            {
                d1 = x1+((top-y1)/(y2-y1))*(x2-x1)-left;
                d2 = y1+((left-x1)/(x2-x1))*(y2-y1)-top;              
            }
                 break;
            case t2:
            {
                d1 = x1+((bottom-y1)/(y2-y1))*(x2-x1)-left;
                d2 = bottom-(y1+((left-x1)/(x2-x1))*(y2-y1));            
            }
                 break;
            case t3:
            {
                d1 = right-(x1+((bottom-y1)/(y2-y1))*(x2-x1));
                d2 = bottom+(y1+((right-x1)/(x2-x1))*(y2-y1));
            }
                 break;
            case t4:
            {
                d1 = right-(x1+((top-y1)/(y2-y1))*(x2-x1));
                d2 = y1+((right-x1)/(x2-x1))*(y2-y1)-top;
            }
                 break;
            case t5:
            case t9:
            {
                double d1a = x1+((top-y1)/(y2-y1))*(x2-x1)-left;
                double d2a = y1+((left-x1)/(x2-x1))*(y2-y1)-top; 
                double d1b = right-(x1+((bottom-y1)/(y2-y1))*(x2-x1));
                double d2b = bottom+(y1+((right-x1)/(x2-x1))*(y2-y1));
                
                if((d1a*d2a) < (d1b*d2b))
                {
                    d1 = d1a;
                    d2 = d2a;
                }
                else
                {
                    d1 = d1b;
                    d2 = d2b;
                }
            }
                break;
                
            case t6:
            case t8:
            {
                double d1a = x1+((bottom-y1)/(y2-y1))*(x2-x1)-left;
                double d2a = bottom-(y1+((left-x1)/(x2-x1))*(y2-y1));       
                double d1b = right-(x1+((top-y1)/(y2-y1))*(x2-x1));
                double d2b = y1+((right-x1)/(x2-x1))*(y2-y1)-top;
                
                if((d1a*d2a) < (d1b*d2b))
                {
                    d1 = d1a;
                    d2 = d2a;
                }
                else
                {
                    d1 = d1b;
                    d2 = d2b;
                }
            }
                break;
            case t7:
            case t10:
                System.err.print("Rerouting error");
            break;  
        }
            return (d1*d2)/2;
        
        }
        
        public CrossingType Crosses(Object node, double src_x, double src_y, double trg_x, double trg_y){
        
            double x1 = src_x ;
            double y1 = src_y ;
            double x2 = trg_x ;
            double y2 = trg_y ;
            
           
            
            double top = ((mxCell)node).getGeometry().getY();
            double bottom = ((mxCell)node).getGeometry().getY()+((mxCell)node).getGeometry().getHeight();
            double left = ((mxCell)node).getGeometry().getX();
            double right = ((mxCell)node).getGeometry().getX()+((mxCell)node).getGeometry().getWidth();
            
            
            if(y1==y2){
                if( (y1<top)||(y1>bottom))
                    return CrossingType.none;
                if( (x1<left)&&(x2<left))
                    return CrossingType.none;
                if( (x1>right)&&(x2>right))
                    return CrossingType.none;
             
                return CrossingType.t10;
            }
            
            if(x1==x2){
                if( (x1<left)||(x1>right))
                    return CrossingType.none;
                if( (y1<top)&&(y2<top))
                    return CrossingType.none;
                if( (y1>bottom)&&(y2>bottom))
                    return CrossingType.none;
                
                return CrossingType.t7;
            }
            
            double t = (top-y1)/(y2-y1);
                    
            boolean horni = (t>=0 && t<=1)&&(x1+t*(x2-x1)>=left && x1+t*(x2-x1)<=right);
          
            t = (bottom-y1)/(y2-y1);
            boolean dolni = (t>=0 && t<=1)&&(x1+t*(x2-x1)>=left && x1+t*(x2-x1)<=right);
          
            t = (left-x1)/(x2-x1);
            boolean leva = (t>=0 && t<=1)&&(y1+t*(y2-y1)>=top && y1+t*(y2-y1)<=bottom);
           
            t = (right-x1)/(x2-x1);
            boolean prava = (t>=0 && t<=1)&&(y1+t*(y2-y1)>=top && y1+t*(y2-y1)<=bottom);
           
            if(!horni && !dolni && !leva && !prava) 
                return CrossingType.none;
            
            if(horni && leva )
                return CrossingType.t1;
            if(dolni && leva )
                return CrossingType.t2;
            if(dolni && prava )
                return CrossingType.t3;
            if(horni && prava )
                return CrossingType.t4;
            
            if(horni && dolni){
            
                double x_h = x1+((top-y1)/(y2-y1))*(x2-x1);
                double x_d = x1+((bottom-y1)/(y2-y1))*(x2-x1);
                
                if(x_d<x_h)
                    return CrossingType.t5;
                if(x_d>x_h)
                    return CrossingType.t6;
            
            return CrossingType.t7;
            }
            
            if(leva && prava){
            
                double y_l = y1+((left-x1)/(x2-x1))*(y2-y1);
                double y_p = y1+((right-x1)/(x2-x1))*(y2-y1);
                
                if(y_l>y_p)
                    return CrossingType.t8;
                if(y_l<y_p)
                    return CrossingType.t9;
            
            return CrossingType.t10;
            }
            return CrossingType.none;
        }
	/**
	 * 
	 */
	protected void route(Object edge, double x, double y)
	{
		if (graph.isCellMovable(edge))
		{
			setEdgePoints(edge, Arrays
					.asList(new mxPoint[] { new mxPoint(x, y) }));
		}
	}

}
