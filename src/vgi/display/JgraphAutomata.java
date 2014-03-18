/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.display;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JFrame;
import vgi.automata.*;
import vgi.geometrictools.Projection;
import vgi.geometrictools.Vector2D;

/**
 *
 * @author reng
 */
public class JgraphAutomata {

    
    public mxGraph graph;
    public mxGraphComponent graphComponent;
    public Automata automata;
    
    Projection projection;
    mxRubberband rubberband;
    
    /* Class constant setup */
    public static final double DEFAULT_LABEL_DISTANCE = 15;
    static final float defaultFontSize = 13;

    /**
     * This constructor do the following procedure
     * 1. initial class variable mxGraph graph,
     * 2. assign class variable graphComponent by calling getGraphComponent()
     * 3. assign class variable automata with passed in automata
     * 4. call initGraph() method
     * @param automata_ 
     */
    public JgraphAutomata(Automata automata_) {


        graph = new mxGraph();
        graphComponent = getGraphComponent();   // if initGraph() setup
                                                // graphComponent, why not
                                                // kill this line?
        
        automata = automata_;
        
        
        initGraph();
        
        
        projection = automata_.getProjection();

        if (projection == null) {
            projection = new Projection();
        }
    }

    private void initGraph() {
        
        graph.setDisconnectOnMove(false);
        graph.setSplitEnabled(false);
        graph.setCellsDisconnectable(false);
        graph.setGridEnabled(false);
        
        //graph.setResetEdgesOnMove(true);
        
        graphComponent = getGraphComponent();    // why call getGraphComponent
                                                 // again? and what's is it?

        /* graphComponent setup */
        graphComponent.setConnectable(false);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setSwimlaneSelectionEnabled(true);
        graphComponent.setSize(800, 600);  // what's this 800, 600 for?

        rubberband = new mxRubberband(graphComponent);

        graph.getSelectionModel().addListener(mxEvent.CHANGE,
                new mxIEventListener() {    

            @Override
            public void invoke(Object sender, mxEventObject evt) {

                    if (sender instanceof mxGraphSelectionModel) {

                        automata.resetSelectedObjs();
                        for(Object cell:((mxGraphSelectionModel)sender).getCells()){
                            automata.addSelectedObj(automata.cellToObj((mxCell)cell));
                            //if(((mxCell)cell).isVertex())//{
                               // automata.addSelectedState((State)automata.cellToState((mxCell)cell));
                            //else if(((mxCell)cell).isEdge()){
//                                System.out.println("change of edge");
                                
                                // once an edge is selected update control points!
//                                mxCell edge=(mxCell)cell;
//                                mxGeometry geo=edge.getGeometry();
//                                List<mxPoint> points=geo.getPoints();
//                                
//                                Transition transition=(Transition)automata.cellToState(edge);
//                                if(transition==null) return;
//                                
//                                List<Point2D> newcpt=new ArrayList<Point2D>();
//                                for(mxPoint pt:points){
//                                    Point2D loc=automata.getProjection().getGeoFromLoc(new Point2D.Double(pt.getX(),pt.getY()));
//                                    newcpt.add(loc);
//                                }
//                                automata.updateTransitionControlPoint(transition, newcpt);
                            }
                            if (!automata.selectionEquivalenceChecking(((mxGraphSelectionModel)sender).getCells())) {
                                automata.updateSelectionJGraphAutomata();
                            }
                        }
                    }
            }
            
        );
        
        graph.addListener(mxEvent.RESIZE_CELLS, new mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells=(Object[])evt.getProperty("cells");
                Object[] bounds=(Object[])evt.getProperty("bounds");
                int len=cells.length;
                for(int i=0;i<len;++i){
                    mxCell cell=(mxCell)cells[i];
                    System.out.print("resize:");
                    if(cell.isVertex()){
                        mxRectangle rec=(mxRectangle)bounds[i];
                        
                        System.out.println(cell.getValue());
                        State state=(State)automata.cellToState(cell);
                        
                        StateGeometricData sgd=state.getGeometricData();
                        Vector2D scal=projection.getScalFromLoc(new Vector2D(rec.getWidth(),rec.getHeight()));
                        Point2D geo=projection.getGeoFromLoc(new Point2D.Double(rec.getCenterX(),rec.getCenterY()));
                        sgd.setWidth(scal.getX());
                        sgd.setHeight(scal.getY());
                        sgd.setX(geo.getX());
                        sgd.setY(geo.getY());
                        automata.setStateGeometricData(state,sgd);
                    }
                    
                }
            }
        });
        
        
    }
    
    public mxCell createVertex(State state) {
               
        StateGeometricData geodata=automata.getStateGeometricData(state);
        StateDrawingData drawingdata=automata.getStateDrawingData(state);
       
        Object parent = graph.getDefaultParent();
        //int id = cellTable.size();
        int id=graph.getChildCells(graph.getDefaultParent()).length;
        
        //double w=geodata.getWidth();
        //double h=geodata.getHeight();
        String shape=geodata.getShape();
        
        //\/\/\/\/ use the projection to get coordinate for screen
        Point2D loc=projection.getLocFromGeo(new Point2D.Double(geodata.getX(),geodata.getY()));
        Vector2D size=projection.getLocScalFromGeoScal(new Vector2D(geodata.getSize()));
                
        Object vertex = graph.insertVertex(parent, Integer.toString(id), state.getName(),
                loc.getX() - size.getX() / 2, loc.getY() - size.getY() / 2, size.getX(), size.getY(), "shape="+shape+";perimeter="+shape+"Perimeter;fontSize="+defaultFontSize+";");
        System.out.println("add state at "+geodata.getX()+" , "+geodata.getY());
        graph.setSelectionCell(vertex);
        
        graph.setCellStyles("fillColor", drawingdata.getFillColor());
        graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
        graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));

        ((mxCell) vertex).setConnectable(false);

        return (mxCell) vertex;
    }
    public mxCell createEdge(Transition transition){
        mxCell source = null, target = null;
//        Enumeration keys = cellTable.keys();
        State sourceState=transition.getSourceState();
        State targetState=transition.getTargetState();
        
       
//        while (keys.hasMoreElements()) {
//            mxCell keyCell = (mxCell) keys.nextElement();
//            if (cellTable.get(keyCell) == sourceState) source = keyCell;
//            if (cellTable.get(keyCell) == targetState) target = keyCell;
//            
//            if ((source != null) && (target != null)) break;
//        }
        source=automata.stateToCell(sourceState);
        target=automata.stateToCell(targetState);
        
        //System.out.println("jgraph transition:"+source.getValue()+"->"+target.getValue()+": "+transition.getLabel());
        
        mxCell edge = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
                                null, transition.getLabel(), source, target,
                                "shape=curve;fontSize="+defaultFontSize+";textShape=default;"));
        //cellTable.put(edge, transition);
        
        mxGeometry geometry = edge.getGeometry();
        if (geometry == null) {
            geometry = new mxGeometry();
            edge.setGeometry(geometry);
            
        }
        
        
        
        TransitionGeometricData geometricData = transition.getGeometricData();
        if (geometricData == null) {
            geometricData = new TransitionGeometricData();
        }
        if (geometricData.labelPosAndDist == null) {
            geometry.setX(0);
            geometry.setY(DEFAULT_LABEL_DISTANCE);
        } else {
            geometry.setX(geometricData.labelPosAndDist.getX());
            geometry.setY(geometricData.labelPosAndDist.getY());
        }
        if (geometricData.labelOffset == null) {
            geometry.setOffset(null);
        } else {
            geometry.setOffset(new mxPoint(geometricData.labelOffset));
        }
        if ((geometricData.controlPoints == null)
                || (geometricData.controlPoints.isEmpty())) {
            geometry.setPoints(new ArrayList<mxPoint>());
        } else {
            List<mxPoint> points = new ArrayList<mxPoint>();
            Iterator<Point2D> iterateControlPoints = geometricData.controlPoints.iterator();
            while (iterateControlPoints.hasNext()) {
                Point2D controlPoint = iterateControlPoints.next();
                
                //\/\/\/\/ use the projection to get coordinate for screen
                Point2D locControlPoint=projection.getLocFromGeo(controlPoint);
                
                points.add(new mxPoint(locControlPoint));
            }  // End while (iterateControlPoints.hasNext())
            geometry.setPoints(points);
            //System.out.println("jgraph pt: "+geometry.getPoints());
        }

        /*if(source==target){
            addControlPoint(edge,source.getGeometry().getCenterX()+source.getGeometry().getWidth(),
                                 source.getGeometry().getCenterY());
        }
        */
        graph.setSelectionCell(edge);
        TransitionDrawingData drawingdata = automata.getTransitionDrawingData(transition);
        if (drawingdata != null) {
            graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
            graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
            graph.setCellStyles("startArrow", drawingdata.getStartArrow());
            graph.setCellStyles("endArrow", drawingdata.getEndArrow());

        }
       
//        if (this.visibilityGraph != null) {
//                this.visibilityGraph.addHindrance(edge);
//        }
        
      //  graph.refresh();
        
        return edge;
        
    }

    /**
     * @return the graphComponent
     */
    private mxGraphComponent getGraphComponent() {

        if (graphComponent == null) {

            graphComponent = new mxGraphComponent(graph) {

                @Override
                protected void installDoubleClickHandler() {

                    graphControl.addMouseListener(new MouseAdapter() {

                        public void mouseReleased(MouseEvent e) {

                            /* Test for cuurent transition */
                            // System.out.println(((Transition)automata.cellToState(cell)).getLabel()

                            if (isEnabled()) {
                                
                                // check first mouse click
                                System.out.println("mouse release enable (this?)");

                                if (!e.isConsumed() && isEditEvent(e)) {

                                    mxCell cell = (mxCell)getCellAt(e.getX(), e.getY(), false);

                                    if (cell != null && getGraph().isCellEditable(cell)) {

                                        if (((mxCell) cell).isVertex()) {

                                            startEditingAtCell(cell, e);

                                        } else {

                                            mxCell source = (mxCell)cell.getSource();
                                            mxCell target = (mxCell)cell.getTarget();

                                            /* this condition handle the situation
                                             * while clicking edge double time
                                             */
                                            if (source != null && target != null) {
                                            
                                                ExpressionEditor editor =
                                                        new ExpressionEditor(
                                                                new JFrame(),
                                                                true,
                                                                ((Transition)automata.cellToState(cell)).getLabel());

                                                editor.setVisible(true);

                                                // set weight in automata!!
                                                System.out.println("set weight in automata (edge click) with " + automata.cellToState(cell));
                                                System.out.println("editor expression from double click" + editor.getExpression());
                                                automata.setTransitionLabel((Transition)automata.cellToState(cell), editor.getExpression());


                                            }
                                        }
                                    }

                                } else {
                                    // Other languages use focus traversal here, in Java
                                    // we explicitely stop editing after a click elsewhere
                                    stopEditing(!invokesStopCellEditing);
                                }
                            }
                        }
                    });
                }
            };
        }
        
        // test 201403181315
        System.out.println("return graphComponent");
        return graphComponent;
    }

    public void updateVertexGeometricData(mxCell vertex, StateGeometricData geodata){
        
        mxGeometry geometry=vertex.getGeometry();
        
        //\/\/\/\/ use the projection to get coordinate for screen
        Point2D loc=projection.getLocFromGeo(geodata.getLocation());
        Vector2D size=projection.getLocScalFromGeoScal(new Vector2D(geodata.getSize()));
        
        //geometry.setRect(geodata.getX()-geodata.getWidth()/2, geodata.getY()-geodata.getHeight()/2, geodata.getWidth(),geodata.getHeight());
        geometry.setRect(loc.getX()-size.getX()/2, loc.getY()-size.getY()/2, size.getX(),size.getY());
        
        vertex.setGeometry(geometry);
        graph.refresh();
    }
    public void updateVertexDrawingData(mxCell vertex, StateDrawingData drawingdata){
        graph.setSelectionCell(vertex);
        
        graph.setCellStyles("fillColor", drawingdata.getFillColor());
        graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
        graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));

    }

    public void updateEdgeDrawingData(mxCell edge, TransitionDrawingData drawingdata) {
        graph.setSelectionCell(edge);
        graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
        graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
        graph.setCellStyles("startArrow", drawingdata.getStartArrow());
        graph.setCellStyles("endArrow", drawingdata.getEndArrow());

    }
    
    
    public void addEdgeControlPoint(mxCell edge,Point2D point,boolean reverse){
        
        
        //\/\/\/\/ use the projection to get coordinate for screen
        Point2D loc=projection.getLocFromGeo(point);
        
        List<mxPoint> points =edge.getGeometry().getPoints();
        if(points==null) points=new ArrayList<mxPoint>();
        points.add(new mxPoint(loc));

        //to sort
        if (points.size() > 1) {
            Collections.sort(points, new Comparator<mxPoint>(){

                @Override
                public int compare(mxPoint t, mxPoint t1) {
                    int flag =Double.compare(t.getX(),t1.getX());
                    if (flag == 0) {
                            return Double.compare(t.getY(),t1.getY());
                    }else{
                            return flag;
                    }
                }
                
            });
            if(reverse) Collections.reverse(points);
        }
        edge.getGeometry().setPoints(points);
    }
    public void addEdgeControlPoint(mxCell edge,Point2D point,boolean reverse,int index){
        
        //\/\/\/\/ use the projection to get coordinate for screen
        Point2D loc=projection.getLocFromGeo(point);
        
        List<mxPoint> points =edge.getGeometry().getPoints();
        if(points==null) points=new ArrayList<mxPoint>();
        points.add(index,new mxPoint(loc));
        
        //to sort
//        if (points.size() > 1) {
//            Collections.sort(points, new Comparator<mxPoint>(){
//
//                @Override
//                public int compare(mxPoint t, mxPoint t1) {
//                    int flag =Double.compare(t.getX(),t1.getX());
//                    if (flag == 0) {
//                            return Double.compare(t.getY(),t1.getY());
//                    }else{
//                            return flag;
//                    }
//                }
//                
//            });
//            if(reverse) Collections.reverse(points);
//        }
        edge.getGeometry().setPoints(points);
//        System.out.println("add cPoints: "+points);
    }
    
    public void setEdgeControlPoint(mxCell edge,List<Point2D> points){
        List<mxPoint> points_=new ArrayList<mxPoint>();
        for(Point2D pt:points){
            //\/\/\/\/ use the projection to get coordinate for screen
            Point2D loc=projection.getLocFromGeo(pt);        
            points_.add(new mxPoint(loc));
        }
        edge.getGeometry().setPoints(points_);
        //System.out.println("setPoints: "+points_);
        graph.refresh();
    }
    
    
    public void resetControlPoint(mxCell cell) {
        
//        mxCell source=(mxCell)cell.getSource();
//        mxCell target=(mxCell)cell.getTarget();
        
//        List<mxPoint> points = new ArrayList<mxPoint>();

//
//        if(source==target){ //loop
//            mxPoint loopCtrlPt=new mxPoint();
//            loopCtrlPt.setX(source.getGeometry().getCenterX()+source.getGeometry().getWidth());
//            loopCtrlPt.setY(source.getGeometry().getCenterY());
//            points.add(loopCtrlPt);
//            
//            
//        }
        if(cell==null) return;
        if(cell.getGeometry()==null) return;
        if(cell.getGeometry().getPoints()==null) return;
        
        cell.getGeometry().getPoints().clear();
//            getGraphComponent().refresh();

         
        
    }

   
    public void deleteControlPoint(mxCell edge, Point2D point) {
        mxPoint mxpoint=new mxPoint(point);
        edge.getGeometry().getPoints().remove(mxpoint);
        
    }

    public void deleteCell(mxCell cell) {
         mxCell[] cells = {cell};
         graph.removeCells(cells);
    }
    
    
    public void moveCell(mxCell cell,Point2D point){
        
        //\/\/\/\/ use the projection to get coordinate for screen
        Point2D loc=projection.getLocFromGeo(point);
        
        cell.getGeometry().setX(loc.getX());
        cell.getGeometry().setY(loc.getY());
        graph.refresh();
    }
   
            
   public Rectangle getCellBound(mxCell edge){
       graph.refresh();
       if((graph.getView().getCellStates(new Object[]{edge})).length<1)
           return graph.getCellBounds(edge).getRectangle();
       
       mxCellState state=(graph.getView().getCellStates(new Object[]{edge}))[0];
       List<mxPoint> points=state.getAbsolutePoints();
       if(edge.getGeometry().getPoints().size()==2){
           Collections.sort(points, new Comparator<mxPoint>(){
                @Override
                public int compare(mxPoint t, mxPoint t1) {
                    int flag =Double.compare(t.getX(),t1.getX());
                    if (flag == 0) {
                            return Double.compare(t.getY(),t1.getY());
                    }else{
                            return flag;
                    }
                }
            });

           mxCurve curve=new mxCurve(points);
            //System.out.println("points size="+points.toString()+"--"+points.size());
          Rectangle rec=curve.getBounds().getRectangle();
          
          //\/\/\/\/ use the projection to get coordinate for screen
          Point2D loc=projection.getGeoFromLoc(rec.getLocation());
          Vector2D size=projection.getGeoScalFromLocScal(new Vector2D(rec.width,rec.height));
          
          //\/\/\/\ may be changed, why int?
          rec.x=(int)loc.getX(); rec.y=(int)loc.getY();
          rec.width=(int)size.getX(); rec.height=(int)size.getY();
          
          return rec;
          
       }else
            return graph.getCellBounds(edge).getRectangle();
   }

    public void updateEdgeGeometricData(mxCell edge, TransitionGeometricData geodata) {
        
        setEdgeControlPoint(edge,geodata.controlPoints);
        
        // TODO: other geodata
        
    }

    public void setEdgeLabel(mxCell edge,String label) {
        edge.setValue(label);
    }

    public mxCell addInitial(mxCell vertex,Initial initial,IniFinGeometricData geodata){
        // add a initial to the vertex with info. of lengthratio && direction
       System.out.println("setInitial in jGraph!"+vertex.getValue()); 
       mxCell ini = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
                                null, initial.getWeight().toString(), null, vertex,
                                "shape=curve;fontSize="+defaultFontSize+";textShape=default;strokeColor="+mxUtils.hexString(Color.RED)));
       ini.getGeometry().setY(JgraphXInternalFrame.DEFAULT_LABEL_DISTANCE);

       setIniFinGeometricData(vertex,geodata,true);
       return ini;
       
    }
    public mxCell addFinal(mxCell vertex,Final finall,IniFinGeometricData geodata){
        
       mxCell fin = (mxCell) (this.graph.insertEdge(graph.getDefaultParent(),
                                null, finall.getWeight().toString(), vertex, null,
                                "shape=curve;fontSize="+defaultFontSize+";textShape=default;strokeColor="+mxUtils.hexString(Color.RED)));
       fin.getGeometry().setY(JgraphXInternalFrame.DEFAULT_LABEL_DISTANCE);

       setIniFinGeometricData(vertex,geodata,false);
       return fin;
    }
    public void setIniFinLabe(mxCell vertex,Object label,boolean isInitial){
        if(isInitial){
            Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                    edge.setValue(label);
                } 
            } 
       }else{
           Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(false) == null) {
                    edge.setValue(label);
                } 
            } 
       }
        graph.refresh();
    }
    public void setIniFinGeometricData(mxCell vertex, IniFinGeometricData geodata,boolean isInitial) {
       
        // update the goemetric data of initial
       mxPoint terminalPoint=new mxPoint(computeInitialFinalTermPoint(vertex,geodata.direction,geodata.lengthRatio));
       
       // find the initial mxCell (may be changed)
       if(isInitial){
            Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                    edge.getGeometry().setTerminalPoint(terminalPoint, true);
//                    System.out.println("initial terminal pt: "+edge.getGeometry().getTerminalPoint(true));
                } 
            } 
       }else{
           Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(false) == null) {
                    edge.getGeometry().setTerminalPoint(terminalPoint, false);
//                    System.out.println("final terminal pt: "+edge.getGeometry().getTerminalPoint(false));
                } 
            } 
       }
        graph.refresh();
    }
    public Point2D computeInitialFinalTermPoint(mxCell vertex,double direction,double lengthRatio){
        
        Point2D stateSize=new Point2D.Double(vertex.getGeometry().getWidth(),vertex.getGeometry().getHeight());
        double x,y;
        double radius=Math.sqrt(stateSize.getX()*stateSize.getX()+stateSize.getY()*stateSize.getY())/2*(1+lengthRatio);
        x=vertex.getGeometry().getCenterX()+radius*Math.cos(direction);
        y=vertex.getGeometry().getCenterY()+radius*Math.sin(direction);
        
        return new Point2D.Double(x,y);
    }

    public void removeIniFin(mxCell vertex, boolean isIni) {
       
        Object[] edges = graph.getEdges(vertex);
        int length = edges.length;
        mxCell edge;
            
        if(isIni){
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                   graph.removeCells(new Object[]{edge});
                } 
            } 
       }else{
           
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(false) == null) {
                   graph.removeCells(new Object[]{edge});
                } 
            } 
       }
    }
    
    public void setIniFinDrawingData(mxCell vertex, TransitionDrawingData drawingdata, boolean isInitial) {
        if(isInitial){
            Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(true) == null) {
                    graph.setSelectionCell(edge);
                    graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
                    graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
                    graph.setCellStyles("startArrow", drawingdata.getStartArrow());
                    graph.setCellStyles("endArrow", drawingdata.getEndArrow());      
                } 
            } 
       }else{
           Object[] edges = graph.getEdges(vertex);
            int length = edges.length;
            mxCell edge;
            for (int j = 0; j < length; j++) {
                edge = (mxCell) edges[j];
                if (edge.getTerminal(false) == null) {
                    graph.setSelectionCell(edge);
                    graph.setCellStyles("strokeColor", drawingdata.getStrokeColor());
                    graph.setCellStyles("strokeWidth", String.valueOf(drawingdata.getStrokeWidth()));
                    graph.setCellStyles("startArrow", drawingdata.getStartArrow());
                    graph.setCellStyles("endArrow", drawingdata.getEndArrow());      
                } 
            } 
       }
        graph.refresh();
    }
    
    public void setSelectedCells(Object[] cells){
        graph.setSelectionCells(cells);
    }
    
    public void groupCells(Object[] cells){
       
//        mxCell group=(mxCell)graph.groupCells(null, DEFAULT_LABEL_DISTANCE, cells);
//        graph.setCellStyles("strokeWidth", "3", cells);
        
    }
    public void ungroupCells(Object[] cells){
        
//        graph.setCellStyles("strokeWidth","1",cells);
    }

}
