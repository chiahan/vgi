/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package view.router;

/*
 * Copyright (c) 2004-2005 France Telecom
 * Copyright (c) 2004 Gaudenz Alder
 * Copyright (c) 2005 David Benson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/* Some methods from the JGraphParallelRouter were used so I maintain the above copyright notice.
 * Additionally,
 * Copyright (c) 2006  Zygmunt Szpak
 * JGraphParallelFlexibleRouter v1.0
 */

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;
import org.jgraph.graph.Edge.Routing;
import org.jgraph.plaf.GraphUI;
import org.jgraph.plaf.basic.BasicGraphUI;
import org.jgraph.plaf.basic.BasicGraphUI.RootHandle;

/**
 * Routing algorithm which creates intermediate points for a line such that the
 * line is parallel to all other existing lines between the same source and
 * target. One of the parallel edges does not contain a routing algorithm and
 * can therefore accept additional control points and can be dragged.
 * The correct intermediate points for the routed lines are calculated from
 * the edge that doesn't have a routing algorithm applied to it.
 *
 * @author Zygmunt Szpak
 * @version 1.0 02/12/2006
 *
 */
public class JGraphParallelFlexibleRouter extends DefaultEdge.LoopRouting {

   /**
    * Bloody eclipse ;)
    */
   private static final long serialVersionUID = 1L;

   /**
    * Singleton to reach parallel flexible edge router
    */
   private static final JGraphParallelFlexibleRouter sharedInstance = new JGraphParallelFlexibleRouter();

   /**
    * Default model
    */
   private static GraphModel graphModel = new DefaultGraphModel();

   /**
    * Distance between each parallel edge
    */
   private static double edgeSeparation = 10.;

   /**
    * A reference to the GraphLayoutCache of the JGraph
    */
   private static GraphLayoutCache graphlayoutcache;

   /**
    * A reference to the GraphUI. This is needed to get to the GraphContext
    * that stores a copy of all relevant views needed for live-preview.
    */
   private static GraphUI graphUI;

   /**
    * Getter for singleton managing parallel edges
    *
    * @return JGraphParallelFlexibleRouter for parallel edges
    */
   public static JGraphParallelFlexibleRouter getSharedInstance() {
      return JGraphParallelFlexibleRouter.sharedInstance;
   }

   /**
    * Calc of intermediates points
    *
    * @param edge
    *            Edge for which routing is demanded
    */
   public List routeEdge(EdgeView edge) {
      List newPoints = new ArrayList();

      /*
       * A reference to the points for the edge that has been dragged or has
       * received additional control points
       */
      List movedEdgePoints = null;

      // Check presence of source/target nodes
      if ((null == edge.getSource()) || (null == edge.getTarget())
            || (null == edge.getSource().getParentView())
            || (null == edge.getTarget().getParentView())) {
         return null;
      }

      newPoints.add(edge.getSource());

      // Check presence of parallel edges
      Object[] edges = getParallelEdges(edge);
      if (edges == null) {
         return null;
      }


      /*
       * Sort the edges according to the graphModel. If the graphModel was not
       * set via the setter this routing will probably not work
       */
      edges = DefaultGraphModel.order(graphModel, edges);

      // Looking for position of edge that has been dragged or has received
      // additional control points
      int movedEdgePosition = 0;
      EdgeView movedEdgeView = null;
      for (int i = 0; i < edges.length; i++) {
         Object e = edges[i];
         // Check to see if this is the edge that has no routing set on it
         CellView cellView = (CellView) graphlayoutcache.getMapping(
               (DefaultEdge) e, false);
         Routing routing = GraphConstants.getRouting((cellView
               .getAllAttributes()));
         if (routing != JGraphParallelFlexibleRouter.getSharedInstance()) {
            movedEdgeView = (EdgeView) cellView;
            /*
             * Store the points of the non-routed edge. This is the edge
             * that received extra control points or was dragged
             */
            movedEdgePoints = GraphConstants.getPoints((cellView
                  .getAllAttributes()));
            movedEdgePosition = i + 1;

            /*
             * During live-preview the point list of the edge that received
             * extra control points or was dragged is not updated. The point
             * list is only updated *after* the mouse is released (i.e. the
             * end of the preview).
             *
             * This is because for live-preview, a copy of the point list is
             * made and only the copy is modified in place. For the routing
             * of the edges to work during a live-preview the copied point
             * list (the one that is modified in place) is needed.
             *
             * The GraphContext contains copies of all the relevant views
             * that are needed for live-preview and that are modifed in
             * place.
             *
             * The following lines of code are used to grab hold of the
             * GraphContext and to extract the relevant point list. The
             * GraphContext is stored in the RootHandle. To get to the
             * RootHandle one has to go through the
             * BasicGraphUI->CellHandle. If a custom implementation of the
             * GraphUI is written the following lines of code have
             * to be changed to reflect the correct GraphUI.  //FIXME
             *
             */

            BasicGraphUI bgui = (BasicGraphUI) graphUI;
            CellHandle rh = bgui.getHandle();
            if (rh instanceof RootHandle) {
               GraphContext gc = ((RootHandle) rh).getContext();

               // Find the copied EdgeView for this edge
               CellView contextView = gc.getMapping(movedEdgeView
                     .getCell(), false);

               if (contextView != null) {
                  if (contextView instanceof EdgeView) {
                     EdgeView contextEdgeView = (EdgeView) contextView;
                     List contextPointList = contextEdgeView.getPoints();
                     // Override the existing point list with the copied
                     // one
                     movedEdgePoints = contextPointList;
                  }
               }
            }
         }

      }

      // Check presence of at least one edge that does not have routing
      if (movedEdgePoints == null)
      {
         System.out.println("JGraphParallelFlexibleRouter ERROR: At least one edge between source and target must");
         System.out.println("                            have ROUTING_DEFAULT set on it. It is possible that");
         System.out.println("                            you did not enforce this during a deletion of a ");
         System.out.println("                            vertex or edge. ");
         return null;
      }

      // Looking for position of edge to determine which edge is being routed
      int position = 0;
      for (int i = 0; i < edges.length; i++) {
         Object e = edges[i];
         if (e == edge.getCell()) {
            position = i + 1;
         }
      }

      // Looking for position of source/target nodes (edge=>port=>vertex)
      VertexView nodeFrom = (VertexView) edge.getSource().getParentView();
      VertexView nodeTo = (VertexView) edge.getTarget().getParentView();
      Point2D from = AbstractCellView.getCenterPoint(nodeFrom);
      Point2D to = AbstractCellView.getCenterPoint(nodeTo);

      if (from != null && to != null) {

         Point2D edgeFrom = from;
         Point2D edgeTo = to;

         /*
          * The parallelOffset will make sure that each edge is correctly
          * spaced according to edgeSeperation
          */
         double parallelOffset = (position - movedEdgePosition)
               * edgeSeparation;

         // Remember that the edge that has DEFAULT_ROUTING must have at least 1 control point
         if (movedEdgePoints.size() > 2) {

            /*
             * To generate the correct control points for the current line
             * we work with the points contained in the non-routed edge that
             * contains the additional control points.
             *
             * We always take 3 points at a time, the first point will be
             * treated as the source, the second point will be treated as
             * the control point and the third point will be treated as the
             * target.
             */
            for (int i = 1; i < movedEdgePoints.size() - 1; i++) {

               // Source point
               Point2D srcPoint = null;

               // Target point
               Point2D trgtPoint = null;

               Point2D ctrlPoint = (Point2D) movedEdgePoints.get(i);

               /*
                * We are dealing with the PortView of the source
                */
               if (i == 1) {
                  PortView srcPortView = (PortView) movedEdgePoints
                        .get(0);
                  srcPoint = srcPortView.getLocation(movedEdgeView,
                        ctrlPoint);
                  edgeFrom.setLocation(srcPoint.getX(), srcPoint.getY());
               }
               // Otherwise it is just a normal Point2D
               else {
                  srcPoint = (Point2D) movedEdgePoints.get(i - 1);
                  edgeFrom.setLocation(srcPoint.getX(), srcPoint.getY());
               }

               /*
                * We are dealing with PortView of the target
                */
               if (i == movedEdgePoints.size() - 2) {
                  PortView trgtPortView = (PortView) movedEdgePoints
                        .get(movedEdgePoints.size() - 1);
                  trgtPoint = trgtPortView.getLocation(movedEdgeView,
                        ctrlPoint);
                  edgeTo.setLocation(trgtPoint.getX(), trgtPoint.getY());
               }
               // Otherwise it is just a normal Point2D
               else {
                  trgtPoint = (Point2D) movedEdgePoints.get(i + 1);
                  edgeTo.setLocation(trgtPoint.getX(), trgtPoint.getY());
               }

               // The gradient(m) of the line-segment from source to
               // control point
               double fromM;

               // Check to see if the line-segment is vertical
               if (Math.abs(ctrlPoint.getX() - edgeFrom.getX()) < 0.05) {
                  fromM = 99999;
               } else {
                  fromM = (ctrlPoint.getY() - edgeFrom.getY())
                        / (ctrlPoint.getX() - edgeFrom.getX());
               }

               // The gradient(m) of the line-segment from control point to
               // target
               double toM;

               // Check to see if the line-segment is vertical
               if (Math.abs((edgeTo.getX() - ctrlPoint.getX())) < 0.05) {
                  toM = 99999;
               } else {
                  toM = (edgeTo.getY() - ctrlPoint.getY())
                        / (edgeTo.getX() - ctrlPoint.getX());
               }

               /*
                * Now that we have the correct gradient for the line we
                * still need at least one correct point to represent the
                * line.
                *
                * fromX and fromY will contain the point that will go with
                * the fromM (gradient)
                */
               double fromY;
               double fromX;

               /*
                * We need to generate the fromX and fromY such that the
                * correct edge separation between the parallel lines is
                * maintained.
                *
                * The following lines of code achieve this by working out
                * the perpendicular vector
                */
               double dx = ctrlPoint.getX() - edgeFrom.getX();
               double dy = ctrlPoint.getY() - edgeFrom.getY();
               double perp_x = dy;
               double perp_y = -1 * dx;
               double len = Math.sqrt(perp_x * perp_x + perp_y * perp_y);
               perp_x = perp_x / len;
               perp_y = perp_y / len;
               double distance = parallelOffset;

               perp_x = perp_x * distance;
               perp_y = perp_y * distance;

               fromY = edgeFrom.getY() + perp_y;
               fromX = edgeFrom.getX() + perp_x;

               /*
                * The following checks to see if a straight line is formed
                * between the two line segment (source-controlPoint &&
                * controlPoint-target)
                *
                */
               // TODO This is incomplete, I only check for horizontal or
               // vertical.
               boolean straightLine = false;
               if (fromM == 0 && toM == 0) {
                  toM = 0.00001;
                  straightLine = true;
               }

               if (fromM == 99999 && toM == 99999) {
                  straightLine = true;
               }

               /*
                * The correct control point is determined by solving two
                * line equations The first line equation represents the
                * line from source-controlPoint The second line equation
                * represents the line from controlPoint-target
                */

               Point2D intersection = null;

               /*
                * A perpendicular vector also has to be worked out for the
                * second line segment controlPoint-target
                *
                */
               dx = edgeTo.getX() - ctrlPoint.getX();
               dy = edgeTo.getY() - ctrlPoint.getY();
               perp_x = dy;
               perp_y = -1 * dx;
               len = Math.sqrt(perp_x * perp_x + perp_y * perp_y);
               perp_x = perp_x / len;
               perp_y = perp_y / len;
               distance = parallelOffset;
               perp_x = perp_x * distance;
               perp_y = perp_y * distance;

               /*
                * There are two possible points of intersection, one of the
                * points of intersection however will result in the edges
                * overlapping. The following checks are used to ensure that
                * the correct point of intersection will be calculated
                */

               // Target is above and to the right of the source
               if (trgtPoint.getX() > ctrlPoint.getX()) {
                  if (trgtPoint.getY() < ctrlPoint.getY()) {
                     double toY2 = edgeTo.getY() + perp_y;
                     double toX2 = edgeTo.getX() + perp_x;
                     // First line segment of current line from source to
                     // control point
                     double routedLineFromYIntercept = -1 * fromM
                           * fromX + fromY;
                     // Second line segment of reference line from
                     // control point to target (below)
                     double routedLineToY2Intercept = -1 * toM * toX2
                           + toY2;
                     intersection = solve(fromM,
                           routedLineFromYIntercept, toM,
                           routedLineToY2Intercept);
                  } else {
                     double toY2 = edgeTo.getY() + perp_y;
                     double toX2 = edgeTo.getX() + perp_x;
                     // First line segment of current line from source to
                     // control point
                     double routedLineFromYIntercept = -1 * fromM
                           * fromX + fromY;
                     // Second line segment of reference line from
                     // control point to target (below)
                     double routedLineToY2Intercept = -1 * toM * toX2
                           + toY2;

                     // If we have a straight line we can rather solve
                     // for the intersection
                     // between a line that is perpendicular to the first
                     // line segment,
                     // and the second line segment.
                     if (straightLine) {
                        double perpendicularM = -1 / toM;
                        double perpendicularYIntercept = -1
                              * perpendicularM * ctrlPoint.getX()
                              + ctrlPoint.getY();
                        intersection = solve(fromM,
                              routedLineFromYIntercept,
                              perpendicularM, perpendicularYIntercept);
                     } else {
                        intersection = solve(fromM,
                              routedLineFromYIntercept, toM,
                              routedLineToY2Intercept);
                     }
                  }

               } else {
                  if (trgtPoint.getY() < ctrlPoint.getY()) {
                     double toY1 = edgeTo.getY() + perp_y;
                     double toX1 = edgeTo.getX() + perp_x;
                     // First line segment of current line from source to
                     // control point
                     double routedLineFromYIntercept = -1 * fromM
                           * fromX + fromY;
                     // Second line segment of reference line from
                     // control point to target (above)
                     double routedLineToY1Intercept = -1 * toM * toX1
                           + toY1;

                     // If we have a straight line we can rather solve
                     // for the intersection
                     // between a line that is perpendicular to the first
                     // line segment,
                     // and the second line segment.
                     if (straightLine) {
                        double perpendicularM = -1 / toM;
                        double perpendicularYIntercept = -1
                              * perpendicularM * ctrlPoint.getX()
                              + ctrlPoint.getY();
                        intersection = solve(fromM,
                              routedLineFromYIntercept,
                              perpendicularM, perpendicularYIntercept);
                     } else {
                        intersection = solve(fromM,
                              routedLineFromYIntercept, toM,
                              routedLineToY1Intercept);
                     }
                  } else {
                     double toY1 = edgeTo.getY() + perp_y;
                     double toX1 = edgeTo.getX() + perp_x;
                     // First line segment of current line from source to
                     // control point
                     double routedLineFromYIntercept = -1 * fromM
                           * fromX + fromY;
                     // Second line segment of reference line from
                     // control point to target )above
                     double routedLineToY1Intercept = -1 * toM * toX1
                           + toY1;

                     if (straightLine) {
                        double perpendicularM = -1 / toM;
                        double perpendicularYIntercept = -1
                              * perpendicularM * ctrlPoint.getX()
                              + ctrlPoint.getY();
                        intersection = solve(fromM,
                              routedLineFromYIntercept,
                              perpendicularM, perpendicularYIntercept);
                     } else {
                        intersection = solve(fromM,
                              routedLineFromYIntercept, toM,
                              routedLineToY1Intercept);
                     }
                  }

               }

               newPoints.add(edge.getAttributes().createPoint(
                     intersection.getX(), intersection.getY()));
            }

         }

      }
      newPoints.add(edge.getTarget());
      return newPoints;
   }

   // Helper method to find the intersection between two lines.
   private Point2D solve(double m1, double b1, double m2, double b2) {
      if (Math.abs(m1 - m2) < 0.05) {
         // TODO should cater for the possibility of a division by zero
      }
      double x = (b2 - b1) / (m1 - m2);
      double y = m1 * x + b1;

      // Debugging
      if ((int)x == 0 && (int)y == 0)
      {
         /*This should not happen but sometimes it does.
          *Seems to happen when there are a lot of control points
          *that are very close together with a similar gradient. Known Bug // FIXME
          */
         System.out.println("No intersection found");

      }

      return new Point((int) x, (int) y);

   }

   /**
    * Getter to obtain the distance between each parallel edge
    *
    * @return Distance
    */
   public static double getEdgeSeparation() {
      return JGraphParallelFlexibleRouter.edgeSeparation;
   }

   /**
    * Setter to define distance between each parallel edge
    *
    * @param edgeSeparation
    *            New distance
    */
   public static void setEdgeSeparation(double edgeSeparation) {
      JGraphParallelFlexibleRouter.edgeSeparation = edgeSeparation;
   }

   
   /**
    * Setter to define the Graph Model
    * @param myModel
    */
   public static void setGraphModel(GraphModel myModel) {
      JGraphParallelFlexibleRouter.graphModel = myModel;
   }

   /**
    * Setter to define the GraphLayoutCache (geometry of the graph)
    * @param GraphLayoutCache
    */
   public static void setGraphLayoutCache(GraphLayoutCache glc) {
      graphlayoutcache = glc;
   }

   /**
    * Setter to define the GraphUI. The GraphUI is used to get to the
    * RootHandle that contains the GraphContext. The GraphContext is needed to
    * extract the point lists that are updated during live-preview.
    *
    * @param theGraphUI
    *
    */
   public static void setGraphUI(GraphUI theGraphUI) {
      graphUI = theGraphUI;
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
      return DefaultGraphModel.getEdgesBetween(graphModel, edge.getSource()
            .getParentView().getCell(), edge.getTarget().getParentView()
            .getCell(), false);
   }

} 