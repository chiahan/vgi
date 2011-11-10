/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package view.router;

    /*
    * @(#)HelloWorld.java 3.3 23-APR-04
    *
    * Copyright (c) 2001-2004, Gaudenz Alder All rights reserved.
    *
    * This library is free software; you can redistribute it and/or
    * modify it under the terms of the GNU Lesser General Public
    * License as published by the Free Software Foundation; either
    * version 2.1 of the License, or (at your option) any later version.
    *
    * This library is distributed in the hope that it will be useful,
    * but WITHOUT ANY WARRANTY; without even the implied warranty of
    * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    * Lesser General Public License for more details.
    *
    * You should have received a copy of the GNU Lesser General Public
    * License along with this library; if not, write to the Free Software
    * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    *
    */
    
    import java.awt.Color;
    import java.awt.Point;
    import java.awt.event.MouseEvent;
    import java.awt.geom.Point2D;
    import java.awt.geom.Rectangle2D;
    import java.util.ArrayList;
    import java.util.Hashtable;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Map;

    import javax.swing.BorderFactory;
    import javax.swing.JFrame;
    import javax.swing.JScrollPane;

    import org.jgraph.JGraph;
    import org.jgraph.event.GraphModelEvent;
    import org.jgraph.event.GraphModelListener;
    import org.jgraph.event.GraphModelEvent.GraphModelChange;
    import org.jgraph.graph.AttributeMap;
    import org.jgraph.graph.BasicMarqueeHandler;
    import org.jgraph.graph.CellView;
    import org.jgraph.graph.ConnectionSet;
    import org.jgraph.graph.DefaultCellViewFactory;
    import org.jgraph.graph.DefaultEdge;
    import org.jgraph.graph.DefaultGraphCell;
    import org.jgraph.graph.DefaultGraphModel;
    import org.jgraph.graph.Edge;
    import org.jgraph.graph.EdgeView;
    import org.jgraph.graph.GraphConstants;
    import org.jgraph.graph.GraphLayoutCache;
    import org.jgraph.graph.GraphModel;
    import org.jgraph.graph.Port;
    import org.jgraph.graph.PortView;
    import org.jgraph.graph.ConnectionSet.Connection;
    import org.jgraph.graph.Edge.Routing;


    /** Hello World application to demonstrate the JGraphParallelFlexibleRouter v1.0
    * Known Issues:
    *
    *              (1)
    *             If the edge seperation is very small it is important to lower the mouse tolerance.
    *             Sometimes even to 0. This is because the graph.getSelectionCellAt(p) can return
    *             different results for the same point p if the edges are very close. This will result
    *             in the user not being able to drag or add points to the selected edge sometimes.
    *             Refer to the MyMarqueeHandler for more information.
    *
    * There may be some other bugs. If you find any or have any suggestions please post them on the forum.
    *
    * @author Zygmunt Szpak (c) 3/12/2006
    * @version for JGraphParallelFlexibleRouter 1.0
    * */

    public class Router  implements GraphModelListener  {
       JGraph graph;

       Router()
       {
          // Construct Model and Graph
          GraphModel model = new DefaultGraphModel();
          GraphLayoutCache view = new GraphLayoutCache(model,
                new DefaultCellViewFactory());
           graph = new JGraph(model,view);

             // Control-drag should clone selection
             graph.setCloneable(true);

             graph.setConnectable(true);

             // Enable edit without final RETURN keystroke
             graph.setInvokesStopCellEditing(true);

             // When over a cell, jump to its default port (we only have one, anyway)
             graph.setJumpToDefaultPort(true);

             // This makes the lines smoother
             graph.setAntiAliased(true);

             // The graph model listener is needed to ensure that when an edge
             // is disconnected from a target or source, one of the remaining
             // parallel edges has its routing set to DEFAULT_ROUTING
             graph.getModel().addGraphModelListener(this);

             // Uncomment this to see that edge picking is more consistent
             //graph.setTolerance(0);

             // A custom marquee handler used to toggle routing between JGraphParallelFlexibleRouter and
             // ROUTING_DEFAULT. Toggling is like a radio button in a button group, only one edge should
             // have ROUTING_DEFAULT set on it and that is the edge that the user clicks on.
             graph.setMarqueeHandler(new MyMarqueeHandler());

             // Insert all three cells in one call, so we need an array to store them
             DefaultGraphCell[] cells = new DefaultGraphCell[6];

             // Create Hello Vertex
             cells[0] = createVertex("Floating", 20, 20, 60, 20, null, false);

             // Create World Vertex
             cells[1] = createVertex("Floating", 140, 140, 40, 20, Color.ORANGE, true);

             // Create Edge
             DefaultEdge edge = new DefaultEdge();
             // Fetch the ports from the new vertices, and connect them with the edge
             edge.setSource(cells[0].getChildAt(0));
             edge.setTarget(cells[1].getChildAt(0));
             cells[2] = edge;

             DefaultEdge edge2 = new DefaultEdge();
             // Fetch the ports from the new vertices, and connect them with the edge
             edge2.setSource(cells[0].getChildAt(0));
             edge2.setTarget(cells[1].getChildAt(0));
             cells[3] = edge2;

             DefaultEdge edge3 = new DefaultEdge();
             // Fetch the ports from the new vertices, and connect them with the edge
             edge3.setSource(cells[0].getChildAt(0));
             edge3.setTarget(cells[1].getChildAt(0));
             cells[4] = edge3;

             DefaultEdge edge4 = new DefaultEdge();
             // Fetch the ports from the new vertices, and connect them with the edge
             edge4.setSource(cells[0].getChildAt(0));
             edge4.setTarget(cells[1].getChildAt(0));
             cells[5] = edge4;



             // The graph model ensures that the way the edges are
             // drawn will follow the order in which you inserted them
             // into the model.
             JGraphParallelFlexibleRouter.setGraphModel(graph.getModel());
             // The distance between the parallel edges.
             JGraphParallelFlexibleRouter.setEdgeSeparation(5.0);
             JGraphParallelFlexibleRouter.setGraphLayoutCache(graph.getGraphLayoutCache());
             // The UI is needed to extract the GraphContext so that the live-preview
             // is drawn correctly.
             JGraphParallelFlexibleRouter.setGraphUI(graph.getUI());

             AttributeMap map = new AttributeMap();
             AttributeMap map2 = new AttributeMap();
             Map nested = new Hashtable();
             Map nested2 = new Hashtable();

             GraphConstants.setRouting(map, JGraphParallelFlexibleRouter
                   .getSharedInstance());
             nested.put(edge2, map);
             nested.put(edge3, map);
             nested.put(edge4, map);

             // The line that has DEFAULT_ROUTING set on it
             // must contain at least one point besides source and target.
             List pointList = new ArrayList();
             pointList.add(edge.getSource());
             Point2D midpoint = new Point((20+140)/2,(20+140)/2);
             pointList.add(midpoint);
             pointList.add(edge.getTarget());
             GraphConstants.setPoints(map2, pointList);
             GraphConstants.setRouting(map2, GraphConstants.ROUTING_DEFAULT);
             nested2.put(edge, map2);

             // Insert the cells via the cache, so they get selected
             graph.getGraphLayoutCache().insert(cells);

             // First we need to add the line which has DEFAULT_ROUTING set on it
             // Because the JGraphParallelFlexibleRouter requires that at least one edge
             // has ROUTING_DEFAULT set on it.
             graph.getGraphLayoutCache().edit(nested2, null, null, null);

             // Then we can add the remaining lines that use JGraphParallelFlexibleRouter
             graph.getGraphLayoutCache().edit(nested, null, null, null);


             graph.clearSelection();
       }

       /**
        * Graph Model change event
        * This is used to make sure that when an edge is disconnected from the target or source
        * At least one of the remaining parallel edges will have ROUTING_DEFAULT set on it.
        */
       public void graphChanged(GraphModelEvent e) {

          // Grab hold of what changed in the event
          GraphModelChange change = e.getChange();

          // This is confusing... you can read the forums about when
          // to use getConnectionSet() or getPreviousConnectionSet()
          // but I'm sure you will get just as confused as I did.
          // What we want to do here essentially is to find out who
          // the edge was connected to once it becomes disconnected
          // so that at one of the remaining parallel edges
          // between its source and target can have its routing changed
          // to ROUTING_DEFAULT.
          // I tried NUMEROUS different ways of grabbing hold of this
          // information but everytime I got NULL.
          // This is the only way that I manage to retrieve the relevant
          // information.
          // If there is a correct way of doing this please post it on the
          // forum.
          ConnectionSet changedConnectionSet = change.getConnectionSet();

          // Almost everything here can at some time be null by the looks of things
          if (changedConnectionSet != null)
          {
                // A post on the forum suggested that iterating over the connections
                // Should give me what I want,
                Iterator changedConnectionSetIterator = changedConnectionSet.connections();
                while (changedConnectionSetIterator.hasNext())
                {
                   Connection connection = (Connection) changedConnectionSetIterator.next();
                   Edge connectionEdge = (Edge) connection.getEdge();
                   if (connectionEdge != null)
                   {
                      // Using getSource or getTarget on connectionEdge results in NULL
                      // but sometimes I can retrieve the port that the edge was connected to
                      Port port = (Port) connection.getPort();

                      // But then again this port could be null ;)
                      if (port!=null)
                      {
                         // Use the port to find what edges are connected to it
                         Iterator edgesOfPortIterator = port.edges();
                         while(edgesOfPortIterator.hasNext())
                         {
                            Edge portEdge = (Edge) edgesOfPortIterator.next();
                            // We want an edge that is connected to both a source and target
                            if (portEdge.getSource() != null && portEdge.getTarget() != null)
                            {
                               // Now we need the cellView of this edge
                               CellView cellView = (CellView) graph.getGraphLayoutCache().getMapping(
                                     (DefaultEdge) portEdge, false);

                               if (cellView != null)
                               {
                                  //   Check presence of parallel edges
                                  Object[] edges = getParallelEdges((EdgeView) (cellView));

                                  // Now set one edge to have ROUTING_DEFAULT
                                  if (edges.length >= 1)
                                  {
                                     Object edg = edges[0];
                                     CellView otherCellView = (CellView) graph.getGraphLayoutCache().getMapping(
                                           (DefaultEdge) edg, false);
                                     // Change the routing of the current cell to default routing
                                     Hashtable nested = new Hashtable();
                                     AttributeMap map = otherCellView.getAllAttributes();
                                     // Make sure that whatever points the edge contained will not be lost
                                     List pointList = ((EdgeView)otherCellView).getPoints();
                                     GraphConstants.setRouting(map,GraphConstants.ROUTING_DEFAULT);
                                     GraphConstants.setPoints(map,pointList);
                                     nested.put(otherCellView.getCell(),map);
                                     graph.getGraphLayoutCache().edit(nested, null, null,
                                           null);
                                  }

                               }
                            }
                         }
                      }
                   }
                  }
             }
          }

       /**
        * Getter to obtain the list of parallel edges
        *
        * @param edge
        *            Edge on which one wants to know parallel edges
        * @return Object[] Array of parallel edges (include edge passed on
        *         argument)
        */
       private Object[] getParallelEdges(EdgeView edge) {
          // FIXME: The model is stored in the cells only in the default
          // implementations. Otherwise we must use the real model here.
          return DefaultGraphModel.getEdgesBetween(graph.getModel(), edge.getSource()
                .getParentView().getCell(), edge.getTarget().getParentView()
                .getCell(), false);
       }



       public DefaultGraphCell createVertex(String name, double x,
             double y, double w, double h, Color bg, boolean raised) {

          // Create vertex with the given name
          DefaultGraphCell cell = new DefaultGraphCell(name);

          // Set bounds
          GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
                x, y, w, h));

          // Set fill color
          if (bg != null) {
             GraphConstants.setGradientColor(cell.getAttributes(), Color.orange);
             GraphConstants.setOpaque(cell.getAttributes(), true);
          }

          // Set raised border
          if (raised)
             GraphConstants.setBorder(cell.getAttributes(), BorderFactory
                   .createRaisedBevelBorder());
          else
             // Set black border
             GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

          // Add a Floating Port
           cell.addPort();

          // Add some fixed ports
          // Right hand side
          //cell.addPort(new Point(GraphConstants.PERMILLE,(int) ((int) (GraphConstants.PERMILLE) * 0.25)));
          //cell.addPort(new Point(GraphConstants.PERMILLE,(int) ((int) (GraphConstants.PERMILLE) * 0.5)));
          //cell.addPort(new Point(GraphConstants.PERMILLE,(int) ((int) (GraphConstants.PERMILLE) * 0.75)));

          // Left hand side
          //cell.addPort(new Point(0,(int) ((int) (GraphConstants.PERMILLE) * 0.25)));
          //cell.addPort(new Point(0,(int) ((int) (GraphConstants.PERMILLE) * 0.5)));
          //cell.addPort(new Point(0,(int) ((int) (GraphConstants.PERMILLE) * 0.75)));

          return cell;
       }

       public JGraph getGraphComponent()
       {
          return graph;
       }


       //
       // Custom MarqueeHandler (ripped and hacked apart from GraphEd ;) )

       // MarqueeHandler that Toggles between Routing
       public class MyMarqueeHandler extends BasicMarqueeHandler {

          // Holds the Start and the Current Point
          protected Point2D start, current;

          // Holds the First and the Current Port
          protected PortView port, firstPort;

          // Override to Gain Control for Toggling between Routing
          public boolean isForceMarqueeEvent(MouseEvent e) {

             Point2D p = graph.fromScreen(new Point(e.getPoint()));

             // Find Cell in Model Coordinates
             Object visuallySelectedCell = graph.getSelectionCellAt(p);
             Object cell = graph.getFirstCellForLocation(p.getX(), p.getY());

             if (visuallySelectedCell != cell)
             {
                // If this happens it means that the mouse tolerance is too high.
                // It is important that the edge that was selected is the one that
                // has the routing removed, otherwise the user will try to move the
                // edge that is selected but will not be able to.  //TODO
                System.out.println("There is confusion between what edge was selected.");
                System.out.println("Lower the mouse tolerance or increase edge separation.");
                cell = visuallySelectedCell;
             }

             CellView selectedCellView = graph.getGraphLayoutCache().getMapping(cell,false);

             // If this is an edge then we need to ensure that it can
             // accept control points (by changing any routing to ROUTING_DEFAULT)
             // and if it contains any parallel edges
             // the parallel edges must have the JGraphParallelFlexibleRouter
             // set.
             if (selectedCellView instanceof EdgeView)
             {
                Routing routing = GraphConstants.getRouting((selectedCellView
                      .getAllAttributes()));
                if (routing != GraphConstants.ROUTING_DEFAULT)
                {
                   // Change the routing of the current cell to default routing
                   Hashtable nested = new Hashtable();
                   AttributeMap map = selectedCellView.getAllAttributes();
                   // Make sure that whatever points the edge contained will not be lost
                   List pointList = ((EdgeView)selectedCellView).getPoints();
                   GraphConstants.setRouting(map,GraphConstants.ROUTING_DEFAULT);
                   GraphConstants.setPoints(map,pointList);
                   nested.put(selectedCellView.getCell(),map);
                   graph.getGraphLayoutCache().edit(nested, null, null,
                         null);
                }

                // Check presence of source/target nodes to ensure that this edge is connected
                if ((null == ((EdgeView)selectedCellView).getSource()) || (null == ((EdgeView)selectedCellView).getTarget())
                      || (null == ((EdgeView)selectedCellView).getSource().getParentView())
                      || (null == ((EdgeView)selectedCellView).getTarget().getParentView())) {
                   // This edge is not connected. It therefore has no parallel edges so we can stop here
                   return super.isForceMarqueeEvent(e);
                }

                // Check presence of parallel edges (it is possible that thi)
                Object[] edges = getParallelEdges((EdgeView) (selectedCellView));

                /*
                 * Sort the edges according to the graphModel. If the graphModel was not
                 * set via the setter this routing will probably not work
                 * This is useful in debugging to confirm that the edge that is
                 * visually selected is also the one that was found using
                 * graph.getFirstCellForLocation(p.getX(), p.getY());
                 *
                 */
                //edges = DefaultGraphModel.order(graph.getModel(), edges); //FIXME

                for (int i = 0; i < edges.length; i++) {
                   Object edg = edges[i];
                   CellView otherCellView = (CellView) graph.getGraphLayoutCache().getMapping(
                         (DefaultEdge) edg, false);

                   if (selectedCellView.getCell() != otherCellView.getCell())
                   {
                      // Change routing of remaining parallel edges to JGraphParallelFlexibleRouter
                      Hashtable nested2 = new Hashtable();
                      AttributeMap map2 = otherCellView.getAllAttributes();
                      GraphConstants.setRouting(map2,JGraphParallelFlexibleRouter.getSharedInstance());
                      nested2.put(otherCellView.getCell(),map2);
                      graph.getGraphLayoutCache().edit(nested2, null, null,
                            null);
                   }
                }
             }

             return super.isForceMarqueeEvent(e);
          }


          /**
           * Getter to obtain the list of parallel edges
           *
           * @param edge
           *            Edge on which one wants to know parallel edges
           * @return Object[] Array of parallel edges (include edge passed on
           *         argument)
           */
          private Object[] getParallelEdges(EdgeView edge) {
             // FIXME: The model is stored in the cells only in the default
             // implementations. Otherwise we must use the real model here.
             return DefaultGraphModel.getEdgesBetween(graph.getModel(), edge.getSource()
                   .getParentView().getCell(), edge.getTarget().getParentView()
                   .getCell(), false);
          }


       } // End of Editor.MyMarqueeHandler



       public static void main(String[] args) {
          Router hr = new Router();

          // Show in Frame
          JFrame frame = new JFrame();
          frame.getContentPane().add(new JScrollPane(hr.getGraphComponent()));
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.pack();
          frame.setVisible(true);
       }


    }
