/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.linear;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.*;

/**
 *
 * @author reng
 */
public class LinearLayout extends mxGraphLayout{
    
    int lineY=200;
    
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
    @Override
    public void execute(Object parent) {
        
        graph.getModel().beginUpdate();
            try{
            mxIGraphModel model = graph.getModel();
            int childCount = model.getChildCount(parent);
            
            HashMap<mxCell,Integer> vertexList=new HashMap<mxCell,Integer>();
            
            for(int i = 0; i < childCount; i++){
                Object child = model.getChildAt(parent, i);
                if(((mxCell)child).isVertex()){
                    mxCell cell=(mxCell)child;
                    
                    vertexList.put(cell, 0);
                    //System.out.println("id "+cell.getId());
                            
                }
            }
            
            for(int i = 0; i < childCount; i++){
                Object child = model.getChildAt(parent, i);
                 if(((mxCell)child).isVertex()){
                     
                        mxCell cell=(mxCell)child;
                        
                        Object[] edges=graph.getOutgoingEdges(child);
                        int edgeCount=edges.length;
                        
                        for(int j=0;j<edgeCount;++j){
                            mxCell edge=(mxCell)edges[j];
                            
                           // mxCell source=(mxCell)edge.getSource();
                            mxCell target=(mxCell)edge.getTarget();
                            // System.out.println("s: "+cell.getId()+"-> t: "+target.getId());
                            if(cell!=target && target!=null){
                                
                                Integer sourceWeight=vertexList.get(cell);
                                Integer targetWeight=vertexList.get(target);

                                
                                if(sourceWeight<targetWeight){
                                    Object[] targetOutgoEdges=graph.getOutgoingEdges(target);
                                    int targetEdgeCount=targetOutgoEdges.length;
                                    
                                    if(edgeCount<targetEdgeCount){
                                        vertexList.put(cell, targetWeight);
                                        vertexList.put(target,sourceWeight);
                                    }
                                    
                                }else if(sourceWeight==targetWeight){
                                    
                                    vertexList.put(cell,targetWeight+1);
                                   
                                    Object[] inComingEdges=graph.getIncomingEdges(cell);
                                    int sourceEdgeCount=inComingEdges.length;
                                    for(int k=0;k<sourceEdgeCount;++k){
                                        mxCell source=(mxCell) ((mxCell)inComingEdges[k]).getSource();
                                        if(source!=null){
                                        int oldWeight=vertexList.get(source);
                                        vertexList.put(source,(oldWeight-sourceWeight)+targetWeight+1);
                                        }
                                    }
                                   
                                }
                                //System.out.println(vertexList);
                                //System.out.println("s: "+cell.getId()+" "+vertexList.get(cell));
                                //System.out.println("t: "+target.getId()+" "+vertexList.get(target));
                            }
                        }
                 }
            }
            // sort vertex order with given weights
            ArrayList<Map.Entry<mxCell, Integer>> mapList=new ArrayList<Map.Entry<mxCell, Integer>>(vertexList.entrySet());
            Collections.sort(mapList, new Comparator<Map.Entry<mxCell, Integer>>(){
                public int compare(Map.Entry<mxCell, Integer> entry1,
                               Map.Entry<mxCell, Integer> entry2){
                    return (entry1.getValue().compareTo(entry2.getValue()));
                }
            });
            
            addedTopEdge=new ArrayList<mxCell>();
            addedBottomEdge=new ArrayList<mxCell>();
            
            vertexMapList=new ArrayList<mxCell>();
            for(Map.Entry<mxCell,Integer> entry:mapList){
                vertexMapList.add(entry.getKey());
            }
            
            int vertexNum=vertexMapList.size();
            for(int i = vertexNum-1; i>=0; i--){
                    mxCell cell=vertexMapList.get(i);
                    
                    mxGeometry geo=cell.getGeometry();
                    //System.out.println(cell.getId()+"-"+vertexList.get(cell)+"-"+(vertexNum-i)*geo.getWidth()*2);

                    setVertexLocation(cell,(vertexNum-i)*geo.getWidth()*2,lineY-geo.getHeight()/2);
            }
            for(int i = vertexNum-1; i>=0; i--){
                    mxCell cell=vertexMapList.get(i);
                    
                    // add edges following the sorted vertex order
                    // determine whether an edge goes from top or bottom
                    Object[] outGoingEdges=graph.getOutgoingEdges(cell);
                    int edgeCount=outGoingEdges.length;
                    for(int j=0;j<edgeCount;++j){
                        
                        mxCell edge=(mxCell)outGoingEdges[j];
                        mxCell target=(mxCell)edge.getTarget();
                        
                        if(target!=cell && target!=null){
                            if(edgeGoUp(edge)){
                                // set control point to top

                              //  System.out.println("go up!");
                                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
                                mxPoint controlPoint=new mxPoint();
                                controlPoint.setX((cell.getGeometry().getCenterX()+target.getGeometry().getCenterX())/2);
                                controlPoint.setY(2*cell.getGeometry().getHeight()*(-1)+lineY);
                                list.add(controlPoint);
                                
                            //    System.out.println(controlPoint);
                                
                                
                                edge.getGeometry().setPoints(list);
                                addedTopEdge.add(edge);

                            }else{
                                //set contol point to bottom

                               // System.out.println("go bottom!");
                                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
                                mxPoint controlPoint=new mxPoint();
                                controlPoint.setX((cell.getGeometry().getCenterX()+target.getGeometry().getCenterX())/2);
                                controlPoint.setY(2*cell.getGeometry().getHeight()+lineY);
                                list.add(controlPoint);
                                edge.getGeometry().setPoints(list);
                                
                              //  System.out.println(controlPoint);
                                
                                addedBottomEdge.add(edge);
                            }
                        }else{
                             
                             ArrayList<mxPoint> points = new ArrayList<mxPoint>();
                             mxPoint loopCtrlPt=new mxPoint();
                             loopCtrlPt.setX(cell.getGeometry().getCenterX());
                             loopCtrlPt.setY(cell.getGeometry().getCenterY()+cell.getGeometry().getHeight());
                             points.add(loopCtrlPt);
                             
                             //edge.getGeometry().setPoints(points);
                            
                        }
                    }
                    
                    
            
            
            }
           
            //update control point
            for(int i = 0; i < vertexNum; i++){
               
                        mxCell cell=vertexMapList.get(i);
                       // Object[] edges=graph.getOutgoingEdges(cell);
                        Object[] edges=graph.getEdges(cell);
                        
                        ArrayList<mxCell> edgeList=new ArrayList<mxCell>();
                        int len=edges.length;
                        for(int k=0;k<len;++k){
                            edgeList.add((mxCell)edges[k]);
                        }
                        /*Collections.sort(edgeList, new Comparator<mxCell>(){
                            @Override
                            public int compare(mxCell t, mxCell t1) {
                                 Integer sourceIndex=vertexMapList.indexOf((mxCell)t.getSource());
                                 Integer targetIndex=vertexMapList.indexOf((mxCell)t.getTarget());
                                 Integer sourceIndex1=vertexMapList.indexOf((mxCell)t1.getSource());
                                 Integer targetIndex1=vertexMapList.indexOf((mxCell)t1.getTarget());
                                 
                                /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                     return targetIndex.compareTo(targetIndex1)*(-1);
                                 else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                               /*  Integer length=Math.abs(sourceIndex-targetIndex);
                                 Integer length1=Math.abs(sourceIndex1-targetIndex1);
                                 return length.compareTo(length1);
                                        
                            }
                            
                        });*/
                        int topArcCount=0;
                        int bottomArcCount=0;
                            
                        for(int k=0;k<len;++k){
                            
                            mxCell edge=edgeList.get(k);
                            mxCell source=(mxCell)edge.getSource();
                            mxCell target=(mxCell)edge.getTarget();
                        
                           //System.out.println(source.getId()+" "+target.getId());
                               
                            if(target==source){
                                ArrayList<mxPoint> points = new ArrayList<mxPoint>();
                                mxPoint loopCtrlPt=new mxPoint();
                                loopCtrlPt.setX(source.getGeometry().getCenterX());
                                loopCtrlPt.setY(source.getGeometry().getCenterY()+source.getGeometry().getHeight());
                                points.add(loopCtrlPt);

                                edge.getGeometry().setPoints(points);
                                
                                System.out.println("loop"+loopCtrlPt);
                                
                            }else if(source==cell && target!=null){
                                int sourceIndex=vertexMapList.indexOf(source);
                                int targetIndex=vertexMapList.indexOf(target);
                                //System.out.println("arc test of "+sourceIndex+" -> "+targetIndex);
        
                                List<mxPoint> points=edge.getGeometry().getPoints();
                                mxPoint controlPoint=points.get(0);
                                if(Math.abs(sourceIndex-targetIndex)==1){ // for neighbors
                                    controlPoint.setY(lineY);

                                }
                                /*else{
                                    if(controlPoint.getY()>lineY){ 
                                        bottomArcCount++;
                                        controlPoint.setY(bottomArcCount*cell.getGeometry().getHeight()+lineY);
                                    }else{
                                        topArcCount++;
                                        controlPoint.setY(topArcCount*cell.getGeometry().getHeight()*(-1)+lineY);
                                    }
                                   
                                }
                                //System.out.println(controlPoint);
                                points.clear();
                                points.add(controlPoint);
                                edge.getGeometry().setPoints(points);
                                */
                                
                                
                                
                                
                            }
                        }
             }
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

        }finally{
            graph.getModel().endUpdate();
        }
    }
    private boolean edgeGoUp(mxCell edge){
        
        int sourceIndex=vertexMapList.indexOf((mxCell)edge.getSource());
        int targetIndex=vertexMapList.indexOf((mxCell)edge.getTarget());
        
        int a=sourceIndex;
        int b=targetIndex;
        if(targetIndex>sourceIndex){ a=targetIndex; b=sourceIndex;}
        
        
        //System.out.println("crossing of "+sourceIndex+" -> "+targetIndex);
        int topCrossing=0;
        for(mxCell topEdge:addedTopEdge){
            int sourceInd=vertexMapList.indexOf((mxCell)topEdge.getSource());
            int targetInd=vertexMapList.indexOf((mxCell)topEdge.getTarget());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd>sourceInd){ c=targetInd; d=sourceInd;}

            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if(c>a && d>b && a>d){
                topCrossing++;
               // System.out.println("top: "+sourceInd+"->"+targetInd);
            }
        }
        
        int bottomCrossing=0;
        for(mxCell bottomEdge:addedBottomEdge){
            int sourceInd=vertexMapList.indexOf((mxCell)bottomEdge.getSource());
            int targetInd=vertexMapList.indexOf((mxCell)bottomEdge.getTarget());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd>sourceInd){ c=targetInd; d=sourceInd;}

            
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if(c>a && d>b && a>d){
                bottomCrossing++;
              //  System.out.println("bottom: "+sourceInd+"->"+targetInd);
            }
        }
        //System.out.println("top "+topCrossing+" / bottom "+bottomCrossing);
        if(bottomCrossing<topCrossing) return false;
        else return true;
    }
    private void setEdgeCurveHeight(mxCell edge,boolean isTopEdge){
        int sourceInd=vertexMapList.indexOf(edge.getSource());
        int targetInd=vertexMapList.indexOf(edge.getTarget());
       // System.out.println("Height of "+sourceInd+" -> "+targetInd);
        
        List<mxPoint> points=edge.getGeometry().getPoints();
        mxPoint controlPoint=points.get(0);
        if(sourceInd==targetInd){  
            
          //  controlPoint.setY(height+edge.getSource().getGeometry().getHeight()*0.8);                    
            controlPoint.setX(edge.getSource().getGeometry().getCenterX());
            controlPoint.setY(edge.getSource().getGeometry().getCenterY()+edge.getSource().getGeometry().getHeight());
            System.out.println("loop"+controlPoint);
        
        }else{
         if(Math.abs(sourceInd-targetInd)==1){
             controlPoint.setY(lineY);
         }else{
    
            int a=sourceInd;
            int b=targetInd;
            if(targetInd>sourceInd){ a=targetInd; b=sourceInd;}

            if(isTopEdge){

                int index=addedTopEdge.indexOf(edge);
                int len=addedTopEdge.size();

                double height=lineY;

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
                        if(height>tmpHeight) height=tmpHeight;

                        //System.out.println("higher "+tmpSourceInd+" -> "+tmpTargetInd);
                       // System.out.println("height"+height);

                    }
                    //if(tmpSourceInd>targetInd) break;
                }
                controlPoint.setY(height-edge.getSource().getGeometry().getHeight()*0.8);                    


            }else{
                int index=addedBottomEdge.indexOf(edge);
                int len=addedBottomEdge.size();

                double height=lineY;

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
                        if(height<tmpHeight) height=tmpHeight;

                       // System.out.println("higher "+tmpSourceInd+" -> "+tmpTargetInd);
                       // System.out.println("height"+height);

                    }
                    
                }
                controlPoint.setY(height+edge.getSource().getGeometry().getHeight()*0.8);                    


            }
              
        
       }
         
     }        
            points.clear();
            points.add(controlPoint);
            edge.getGeometry().setPoints(points);
    }
    
    
}
