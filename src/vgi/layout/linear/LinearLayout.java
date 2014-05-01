/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.linear;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxCurveLabelShape;
import com.mxgraph.shape.mxCurveShape;
import com.mxgraph.shape.mxDefaultTextShape;
import com.mxgraph.shape.mxITextShape;
import com.mxgraph.util.mxCurve;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.util.*;

/**
 *
 * @author reng
 */
public class LinearLayout extends mxGraphLayout{
    
    int lineY=200;
    double boundY=lineY;
     //
     //  for DFS
     //
    int dfsTime=0;
    List<mxCell> allVertexList;
    List<DfsVertex> dfsVertexList;
    List<mxCell> dfsSortedList;
    //
    int vertexNum;
    List<mxCell> vertexMapList;
    List<mxCell> addedTopEdge;
    List<mxCell> addedBottomEdge;
    
    public LinearLayout(mxGraph graph){
        super(graph);
    }
    public class WeightVertex{
        mxCell vertex;
        int weight;
        
        public WeightVertex(mxCell cell,int w){
            vertex=cell; weight=w;
        }
        public int getWieght(){return weight;}
        public void setWeight(int w){weight=w;}
        
        public mxCell getVertex(){return vertex;}
        
    }
    
    public class CompareVertex implements Comparator {
        
        @Override
        public int compare(Object t, Object t1) {
            
            Integer w1 = ((WeightVertex)t).getWieght();
            Integer w2 = ((WeightVertex)t1).getWieght();
            
            int flag = w2.compareTo(w1);
            return flag;
            
        }
    }
    
    public class DfsVertex{
        mxCell vertex;
        int d;
        int f;
        mxCell pi;
        int color;
        public DfsVertex(mxCell v){
            vertex=v;
            color=0;
            pi=null;
        }
    }
    
    
    @Override
    public void execute(Object parent) {
        
        graph.getModel().beginUpdate();
            try{

            vertexMapList=this.sortVertices(graph);
            setSortedVerticesLocation();
            
            decideEdgesGoUpDown();
            
            
            Collections.sort(addedTopEdge, new Comparator<mxCell>(){
                    @Override
                    public int compare(mxCell t, mxCell t1) {
                            Integer sourceIndex=vertexMapList.indexOf((mxCell)t.getSource());
                            Integer targetIndex=vertexMapList.indexOf((mxCell)t.getTarget());
                            Integer sourceIndex1=vertexMapList.indexOf((mxCell)t1.getSource());
                            Integer targetIndex1=vertexMapList.indexOf((mxCell)t1.getTarget());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
            Collections.sort(addedBottomEdge, new Comparator<mxCell>(){
                    @Override
                    public int compare(mxCell t, mxCell t1) {
                            Integer sourceIndex=vertexMapList.indexOf((mxCell)t.getSource());
                            Integer targetIndex=vertexMapList.indexOf((mxCell)t.getTarget());
                            Integer sourceIndex1=vertexMapList.indexOf((mxCell)t1.getSource());
                            Integer targetIndex1=vertexMapList.indexOf((mxCell)t1.getTarget());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
            
            int len=addedTopEdge.size();
            for(int i=0;i<len;++i){
                setEdgeCurveHeight(addedTopEdge.get(i),true);
            }
             
            len=addedBottomEdge.size();
            for(int i=0;i<len;++i){
                setEdgeCurveHeight(addedBottomEdge.get(i),false);
            }
            
            if(boundY<0) moveWholeBound();
            
        }finally{
            graph.getModel().endUpdate();
        }
    }
    private List<mxCell> getAllVertices(mxGraph graph){
        List<mxCell> vertexList=new ArrayList<mxCell>();
        
         mxIGraphModel model = graph.getModel();
         Object parent=graph.getDefaultParent();
         
         
         Object[] oList=graph.getChildVertices(parent);
         vertexNum =oList.length;
         
         for(int i = 0; i < vertexNum; i++){
                mxCell cell=(mxCell)oList[i];
                vertexList.add(cell);
                
         }
        
        return vertexList;
        
    }
    private List<mxCell> sortVertices(mxGraph graph){
        
        // do DFS
        dfsSortedList=new ArrayList<mxCell>();
        DFS(graph);
        
        return dfsSortedList;
    }
    private void DFS(mxGraph graph){
        
        allVertexList=getAllVertices(graph);
        
       dfsVertexList=new ArrayList<DfsVertex>();
        for(int i=0;i<vertexNum;++i){
            dfsVertexList.add(new DfsVertex(allVertexList.get(i)));
        }
        dfsTime=0;
        
        for(int i=0;i<vertexNum;++i){
            DfsVertex dvertex=dfsVertexList.get(i);
            if(dvertex.color==0) DfsVisit(graph,dvertex);
        }
        
        List<mxCell> invertedList=new ArrayList<mxCell>();
        for(int i=0;i<vertexNum;++i){
            invertedList.add(dfsSortedList.get(vertexNum-i-1));
        }
        dfsSortedList=invertedList;
        
    }
    private void DfsVisit(mxGraph graph,DfsVertex dvertex){
        dfsTime++;
        dvertex.d=dfsTime;
        dvertex.color=1;
        
        Object[] outEdges=graph.getOutgoingEdges(dvertex.vertex);
        int edgeCount=outEdges.length;
        for(int i=0;i<edgeCount;++i){
            mxCell destCell=(mxCell)((mxCell)outEdges[i]).getTarget();
            int ind=allVertexList.indexOf(destCell);
            if(ind>-1){
            DfsVertex destdVertex=dfsVertexList.get(ind);
            
            if(destdVertex.color==0){
                destdVertex.pi=dvertex.vertex;
                DfsVisit(graph,destdVertex);
            }
            }
        }
        
        dvertex.color=2;
        dfsTime++;
        dvertex.f=dfsTime;
        dfsSortedList.add(dvertex.vertex);
        
        
        System.out.println(dvertex.vertex.getValue()+"- ( "+dvertex.d+" , "+dvertex.f+" )");
        
    }
    private void setSortedVerticesLocation(){
         double x=0;
         for(int i = 0; i<vertexNum; ++i){
                    mxCell cell=vertexMapList.get(i);
                    mxGeometry geo=cell.getGeometry();
                    
                    double distance=geo.getWidth();
                    
                    if(i>0){
                        mxCell lastCell=vertexMapList.get(i-1);
                        distance+=lastCell.getGeometry().getWidth();
                    }else distance*=2;
                          
                    //System.out.println(cell.getId()+"-"+vertexList.get(cell)+"-"+x+" + "+distance);
                    
                    setVertexLocation(cell,x+distance*0.4,lineY-geo.getHeight()/2);
                    x=cell.getGeometry().getX()+cell.getGeometry().getWidth();
         }
    }
    /*
     *  decide whether an edge goes up or down, choose the one with fewer crossings
     *  
     *  addedTopEdge,addedBottomEdge are updated
     */
    private void decideEdgesGoUpDown(){
            addedTopEdge=new ArrayList<mxCell>();
            addedBottomEdge=new ArrayList<mxCell>();
            
            for(int i = 0; i<vertexNum; ++i){
                    mxCell cell=vertexMapList.get(i);
                    
                    // add edges following the sorted vertex order
                    // determine whether an edge goes from top or bottom
//                    Object[] outGoingEdges=graph.getOutgoingEdges(cell);
//                    int edgeCount=outGoingEdges.length;
//                    for(int j=0;j<edgeCount;++j){
//                        
//                        mxCell edge=(mxCell)outGoingEdges[j];
//                        mxCell target=(mxCell)edge.getTarget();
//                        
//                        //if(target!=cell && target!=null){
//                        if(target!=null){
//                            if(edgeGoUp(edge)){
//                                addedTopEdge.add(edge);
//                            }else{
//                                addedBottomEdge.add(edge);
//                            }
//                        }
//                    }
                    
                    Object[] outGoingEdges=graph.getAllEdges(new Object[]{cell});
                    int edgeCount=outGoingEdges.length;
                    for(int j=0;j<edgeCount;++j){
                        
                        mxCell edge=(mxCell)outGoingEdges[j];
                        mxCell target=(mxCell)edge.getTarget();
                        mxCell source=(mxCell)edge.getSource();
                        
                        //if(target!=cell && target!=null){
                        if(target!=null && source!=null){
                            if(edgeGoUp(edge)){
                                addedTopEdge.add(edge);
                            }else{
                                addedBottomEdge.add(edge);
                            }
                        }
                    }
            }

    }
    /*
     * decide an edge to go up or down, depends on minimum crossings
     * if up return true, down return false
     */
    private boolean edgeGoUp(mxCell edge){
        
        int sourceIndex=vertexMapList.indexOf((mxCell)edge.getSource());
        int targetIndex=vertexMapList.indexOf((mxCell)edge.getTarget());
        
        int a=sourceIndex;
        int b=targetIndex;
        if(targetIndex<sourceIndex){ a=targetIndex; b=sourceIndex;}
        
        
        System.out.println("crossing of "+sourceIndex+" -> "+targetIndex);
        int topCrossing=0;
        for(mxCell topEdge:addedTopEdge){
            int sourceInd=vertexMapList.indexOf((mxCell)topEdge.getSource());
            int targetInd=vertexMapList.indexOf((mxCell)topEdge.getTarget());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}

            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if(c<a && d<b && a<d){
                topCrossing++;
                //System.out.println("top: "+sourceInd+"->"+targetInd);
            }
        }
        
        int bottomCrossing=0;
        for(mxCell bottomEdge:addedBottomEdge){
            int sourceInd=vertexMapList.indexOf((mxCell)bottomEdge.getSource());
            int targetInd=vertexMapList.indexOf((mxCell)bottomEdge.getTarget());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}

            
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if(c<a && d<b && a<d){
                bottomCrossing++;
                //System.out.println("bottom: "+sourceInd+"->"+targetInd);
            }
        }
        //System.out.println("top "+topCrossing+" / bottom "+bottomCrossing);
        if(bottomCrossing<topCrossing) return false;
        else return true;
    }
    /*
     * adjust edges' curve height
     * avoid edges having same source/target from crossing each other
     * 
     * TODO: keep all given y coordinates > 0
     */
    private void setEdgeCurveHeight(mxCell edge,boolean isTopEdge){
        int sourceInd=vertexMapList.indexOf(edge.getSource());
        int targetInd=vertexMapList.indexOf(edge.getTarget());
        System.out.println("Height of "+sourceInd+" -> "+targetInd);
        
        List<mxPoint> points=new ArrayList<mxPoint>();
        mxPoint controlPoint1=null;
        mxPoint controlPoint2=null;
        
        
        //controlPoint.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(targetInd).getGeometry().getCenterX())/2);
        
        if(sourceInd==targetInd){  //loop
            controlPoint1=new mxPoint();
            controlPoint1.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(targetInd).getGeometry().getCenterX())/2);
        
          //  controlPoint.setY(height+edge.getSource().getGeometry().getHeight()*0.8);                    
         //   controlPoint.setX(edge.getSource().getGeometry().getCenterX());
            controlPoint1.setY(edge.getSource().getGeometry().getCenterY()+edge.getSource().getGeometry().getHeight());
             System.out.println("loop"+controlPoint1);
        
        }else{
         if(Math.abs(sourceInd-targetInd)==1){
             
             controlPoint1=new mxPoint();
            
             controlPoint1.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(targetInd).getGeometry().getCenterX())/2);
             controlPoint1.setY(lineY);
             
         }else{
             
            controlPoint1=new mxPoint();
            controlPoint2=new mxPoint();
            
            controlPoint1.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(Math.min(sourceInd, targetInd) +1).getGeometry().getCenterX())/2);
            controlPoint2.setX((vertexMapList.get(targetInd).getGeometry().getCenterX()+vertexMapList.get(Math.max(sourceInd,targetInd)-1).getGeometry().getCenterX())/2);
                    
            int a=sourceInd;
            int b=targetInd;
            if(targetInd>sourceInd){ a=targetInd; b=sourceInd;}

            if(isTopEdge){

                int index=addedTopEdge.indexOf(edge);
                int len=addedTopEdge.size();

                //double height=lineY;
                
                // find the one has largest height between source and target
                int middleInd=0;
                double middleHeight=lineY;
                for(int i=b+1;i<a;++i){
                    double tmpHeight=vertexMapList.get(i).getGeometry().getY();
                    if(middleHeight>tmpHeight){ 
                        middleInd=i;
                        middleHeight=tmpHeight;
                    }
                }
                double heightScale=vertexMapList.get(middleInd).getGeometry().getHeight()/3;
                
                //int middleInd=(sourceInd+targetInd)/2;
                //double middleHeight=vertexMapList.get(middleInd).getGeometry().getHeight();
                double height=vertexMapList.get(middleInd).getGeometry().getY();
                //System.out.println("middle "+middleInd+"  "+height);
                double higher=0;
                for(int i=0;i<len;++i){
                    
                    mxCell tmp=addedTopEdge.get(i);
                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSource());
                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTarget());
                    //System.out.println("top child "+tmpSourceInd+" -> "+tmpTargetInd);

                    int c=tmpSourceInd;
                    int d=tmpTargetInd;
                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}

                    //System.out.println("["+a+" , "+b+"]"+"--["+c+" , "+d+"]");


                    if(a>=c && b<=d && !(a==c && b==d)){

                        double tmpHeight=tmp.getGeometry().getPoints().get(0).getY();
                        if(height>tmpHeight){
                            height=tmpHeight; higher++;
                        }

                        //System.out.println("higher "+tmpSourceInd+" -> "+tmpTargetInd);
                        //System.out.println("height"+height);

                    }
                    //if(tmpSourceInd>targetInd) break;
                }
                controlPoint1.setY(height-heightScale*(1+higher/8));                    
                controlPoint2.setY(height-heightScale*(1+higher/8));                    

                //System.out.println("higher "+higher+" "+controlPoint.getY());
                
            }
            else{
                int index=addedBottomEdge.indexOf(edge);
                int len=addedBottomEdge.size();

                //double height=lineY;
                //int middleInd=(sourceInd+targetInd)/2;
                //double middleHeight=vertexMapList.get(middleInd).getGeometry().getHeight();
                
                // find the one has largest height between source and target
                int middleInd=0;
                double middleHeight=lineY;
                for(int i=b+1;i<a;++i){
                    double tmpHeight=vertexMapList.get(i).getGeometry().getY();
                    if(middleHeight>tmpHeight){ 
                        middleInd=i;
                        middleHeight=tmpHeight;
                    }
                }
                double heightScale=vertexMapList.get(middleInd).getGeometry().getHeight()/3;
                
                double height=vertexMapList.get(middleInd).getGeometry().getY()
                                    +vertexMapList.get(middleInd).getGeometry().getHeight();
                System.out.println("middle "+middleInd+"  "+height);
                double higher=0;
                for(int i=0;i<len;++i){
                    
                    mxCell tmp=addedBottomEdge.get(i);
                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSource());
                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTarget());

                   // System.out.println("bottom child "+tmpSourceInd+" -> "+tmpTargetInd);

                    int c=tmpSourceInd;
                    int d=tmpTargetInd;
                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}
                    //System.out.println("["+a+" , "+b+"]"+"--["+c+" , "+d+"]");


                    if(a>=c && b<=d && !(a==c && b==d)){

                        double tmpHeight=tmp.getGeometry().getPoints().get(0).getY();
                        if(height<tmpHeight){
                            height=tmpHeight; higher++;
                        }

                        //System.out.println("higher "+tmpSourceInd+" -> "+tmpTargetInd);
                        //System.out.println("height"+height);

                    }
                    
                 }
                 controlPoint1.setY(height+heightScale*(1+higher/8));                    
                 controlPoint2.setY(height+heightScale*(1+higher/8));                    


            }
              
        
       }
         
     }        
            points.clear();
            points.add(controlPoint1);
            if(controlPoint2!=null) points.add(controlPoint2);
            System.out.println(controlPoint1);
            
            edge.getGeometry().setPoints(points);
            
             placeLableToBound(edge);
            
            
            
    }
    /*
     *  add a third control point at the top of the curve
     */
    private void placeLableToBound(mxCell edge){
        graph.refresh();
        mxCellState state=(graph.getView().getCellStates(new Object[]{edge}))[0];
        List<mxPoint> points=state.getAbsolutePoints();
        if(edge.getGeometry().getPoints().size()==2){
        
            mxCurve curve=new mxCurve(points);
            //System.out.println("points size="+points.toString()+"--"+points.size());
            mxRectangle rec2=curve.getBounds();
        

            mxPoint pt1=edge.getGeometry().getPoints().get(0);
            double centerx=rec2.getCenterX();//pt1.getX()+pt2.getX())/2;
            double centery=(pt1.getY()<lineY)?rec2.getY():rec2.getY()+rec2.getHeight();//+(double)rec.getHeight()*((pt1.getY()<lineY)? -1 :1);
            
            edge.getGeometry().getPoints().add(1,new mxPoint(centerx,centery));
            if(pt1.getY()>lineY) edge.getGeometry().setY(edge.getGeometry().getY()*-1);
            //else edge.getGeometry().setY(edge.getGeometry().getY());
            
            // get the heighest y
            if(boundY>centery) boundY=centery;
        }
        
        
    }
    /*
     * Temporary Use:
     * move the whole graph if there are control points outside canvas
     * (with y<0)
     */
    private void moveWholeBound(){
        graph.refresh();
        System.out.println("move all vertices: "+-boundY);
        
        boundY*=-1;
        boundY+=30;
                
        List<mxCell> allvertices=this.getAllVertices(graph);
        for(mxCell vertex:allvertices){
            double newh=vertex.getGeometry().getY()+boundY;
            vertex.getGeometry().setY(newh);
        }
        Object[] alledges=graph.getChildEdges(graph.getDefaultParent());
        for(Object oedge:alledges){
            mxCell edge=(mxCell)oedge;
            List<mxPoint> cpts=edge.getGeometry().getPoints();
            for(mxPoint pt:cpts){
                double newh=pt.getY()+boundY;
                pt.setY(newh);
            }
            edge.getGeometry().setPoints(cpts);
        }
    } 
}
