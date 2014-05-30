/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.edgerouting;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author JLiu
 */
public class EdgeRoutingBranchingLayout extends mxGraphLayout {

	public EdgeRoutingBranchingLayout(mxGraph graph) {
		super(graph);
	}

	@Override
	public void execute(Object parent) {
		Object[] objectArray = this.graph.getChildEdges(parent);
		for (int index = 0; index < objectArray.length; index++) {
			if (!(objectArray[index] instanceof mxCell)) {
				continue;
			}
			mxCell edge = (mxCell) objectArray[index];
			if (!(edge.isEdge())) {
				continue;
			}
			this.route(edge);
		}  // End for (int index = 0; index < objectArray.length; index++)
	}  // End public void execute(Object parent)

	public void route(mxCell edge) {
		this.route(edge, false);
	}  // End public void route(mxCell edge)

	public void route(mxCell edge, boolean oneStepOnly) {

		if (edge == null) {
			throw new IllegalArgumentException("Input 'edge' is null.");
		}
		if (!(edge.isEdge())) {
			throw new IllegalArgumentException("Input 'edge' is not an edge.");
		}
		mxICell source = edge.getSource();
		if (source == null) {
			return;
//			throw new IllegalArgumentException("Input 'edge' has no source vertex.");
		}
		mxGeometry sourceGeometry = source.getGeometry();
		if (sourceGeometry == null) {
			throw new IllegalArgumentException("Source vertex of input 'edge' has no position data.");
		}
		mxICell target = edge.getTarget();
		if (target == null) {
			return;
//			throw new IllegalArgumentException("Input 'edge' has no target vertex.");
		}
		mxGeometry targetGeometry = target.getGeometry();
		if (targetGeometry == null) {
			throw new IllegalArgumentException("Target vertex of input 'edge' has no position data.");
		}

		if (source.equals(target)) {
			//
			// Loop transition layout to be implemented later.
			//
			return;
		}

		Object sourceValue = source.getValue();
		Object targetValue = target.getValue();
		if ((sourceValue != null) && (targetValue != null)) {
//			System.out.println("Routing edge from " + sourceValue.toString() + " to " + targetValue.toString() + ".");
		}
		List<mxPoint> controlPoints = new ArrayList<mxPoint>();
		this.route(
				sourceGeometry.getCenterX(),
				sourceGeometry.getCenterY(),
				targetGeometry.getCenterX(),
				targetGeometry.getCenterY(),
				controlPoints,
				0,
				oneStepOnly);
		if (oneStepOnly) {
			mxCell[] cells = {edge};
			if (controlPoints.isEmpty()) {
				this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.BLACK), cells);
			} else {  // End if (controlPoints.isEmpty())
				this.graph.removeCells(cells);
			}  // End else part of if (controlPoints.isEmpty())
		} else {
			super.setEdgePoints(edge, controlPoints);
		}
	}  // End public void route(mxCell edge, boolean oneStepOnly)

	protected double route(
			double sourceX,
			double sourceY,
			double targetX,
			double targetY,
			List<mxPoint> controlPoints,
			double remainingCost,
			boolean oneStepOnly) {

		if ((controlPoints == null) || !(controlPoints.isEmpty())) {
			throw new IllegalArgumentException("Input 'controlPoints' must be an empty ArrayList<mxPoint>.");
		}

//		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, remainingCost: %.1f\n", sourceX, sourceY, targetX, targetY, remainingCost);
		mxCell source = null;
		mxCell target = null;
		List<mxCell> obstacles = new ArrayList<mxCell>();
		Object[] cells = this.graph.getChildCells(this.graph.getDefaultParent());
		//
		// Find all the obstacles between the source and the target.
		// Since we are enumerating all the mxCells, find and remember the source and the target.as well.
		//
		for (int index = 0; index < cells.length; index++) {
			if (!(cells[index] instanceof mxCell)) {
				continue;
			}
			mxCell cell = (mxCell) cells[index];
			if (cell.isEdge()) {
				//
				// Edge crossing detection to be implemented later.
				//
				continue;
			}
			mxGeometry geometry = cell.getGeometry();
			if (geometry == null) {
				continue;
			}
			if ((geometry.getCenterX() == sourceX) && (geometry.getCenterY() == sourceY)) {
				source = cell;
				continue;
			}
			if ((geometry.getCenterX() == targetX) && (geometry.getCenterY() == targetY)) {
				target = cell;
				continue;
			}
			if ((geometry.getWidth() == 0) && (geometry.getHeight() == 0)) {
				continue;
			}
			mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, targetX, targetY);
			if (intersectPoint != null) {
				obstacles.add(cell);
			}
		}  // End for (int index = 0; index < cells.length; index++)

		double offsetX = targetX - sourceX;
		double offsetY = targetY - sourceY;
		double straightCost = Math.sqrt(offsetX * offsetX + offsetY * offsetY) + obstacles.size() * 1000;

		if (obstacles.isEmpty()) {
//			System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f\n", sourceX, sourceY, targetX, targetY, straightCost);
			return straightCost;
		}

		if (remainingCost <= 0) {
			remainingCost = straightCost;
		}
		//
		// The list of obstacles should be in the order of hitting the obstacle when going out from the source.
		//
		EdgeRoutingBranchingLayout.sort(obstacles, new ObstacleVerticesComparator(sourceX, sourceY));
		//
		// Rotate the offset vector 90 degrees couterclockwise to get a test vecotr.
		// Any vector projected onto the test vector (by dot product) points to the left of the
		// offset vector if and only if the projection (dot product) is positive.
		//
		double testVectorX = offsetY;
		double testVectorY = -offsetX;
		//
		// Find a control point to the left of the vector from source to target.
		//
		Route route = new Route();
		route.controlPointX = targetX;
		route.controlPointY = targetY;
		mxCell obstacle = obstacles.get(0);

		while (obstacle != null) {

			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = route.controlPointX - sourceX;
			offsetY = route.controlPointY - sourceY;
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees couterclockwise and adjust its length to detourLength
			//
			double detourX = offsetY * detourLength / offsetLength;
			double detourY = -offsetX * detourLength / offsetLength;
			route.controlPointX = geometry.getCenterX() + detourX;
			route.controlPointY = geometry.getCenterY() + detourY;

			offsetX = route.controlPointX - sourceX;
			offsetY = route.controlPointY - sourceY;
			double projection = offsetX * testVectorX + offsetY * testVectorY;
			//
			// If the left control point has been adjusted so much that it is no longer
			// left of the vector from source to target, stop looking for a left control
			// point.
			//
			if (projection < 0) {
				route = null;
				break;
			}

			obstacle = null;
			for (int index = 0; index < cells.length; index++) {
				if (!(cells[index] instanceof mxCell)) {
					continue;
				}
				mxCell cell = (mxCell) cells[index];
				if (cell.isEdge()) {
					//
					// Edge crossing detection to be implemented later.
					//
					continue;
				}
				geometry = cell.getGeometry();
				if ((geometry == null)
						|| ((geometry.getCenterX() == sourceX) && (geometry.getCenterY() == sourceY))
						|| ((geometry.getCenterX() == targetX) && (geometry.getCenterY() == targetY))
						|| ((geometry.getWidth() == 0) && (geometry.getHeight() == 0))) {
					continue;
				}
				mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, route.controlPointX, route.controlPointY);
				if (intersectPoint != null) {
					obstacle = cell;
					break;
				}
//				if (geometry.contains(route.controlPointX, route.controlPointY)) {
//					obstacle = cell;
//					break;
//				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		ArrayList<Route> routeList = new ArrayList<Route>();
		if (route != null) {
			double dx1 = route.controlPointX - sourceX;
			double dy1 = route.controlPointY - sourceY;
			double dx2 = targetX - route.controlPointX;
			double dy2 = targetY - route.controlPointY;
			route.cost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
			if (route.cost < remainingCost) {
				routeList.add(route);
			}
		}  // End if (route != null)

		//
		// Find a control point to the right of the vector from source to target.
		//
		route = new Route();
		route.controlPointX = targetX;
		route.controlPointY = targetY;
		obstacle = obstacles.get(0);

		while (obstacle != null) {

			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = route.controlPointX - sourceX;
			offsetY = route.controlPointY - sourceY;
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees clockwise and adjust its length to detourLength
			//
			double detourX = -offsetY * detourLength / offsetLength;
			double detourY = offsetX * detourLength / offsetLength;
			route.controlPointX = geometry.getCenterX() + detourX;
			route.controlPointY = geometry.getCenterY() + detourY;

			offsetX = route.controlPointX - sourceX;
			offsetY = route.controlPointY - sourceY;
			double projection = offsetX * testVectorX + offsetY * testVectorY;
			//
			// If the right control point has been adjusted so much that it is no longer
			// right of the vector from source to target, stop looking for a right control
			// point.
			//
			if (projection > 0) {
				route = null;
				break;
			}

			obstacle = null;
			for (int index = 0; index < cells.length; index++) {
				if (!(cells[index] instanceof mxCell)) {
					continue;
				}
				mxCell cell = (mxCell) cells[index];
				if (cell.isEdge()) {
					//
					// Edge crossing detection to be implemented later.
					//
					continue;
				}
				geometry = cell.getGeometry();
				if ((geometry == null)
						|| ((geometry.getCenterX() == sourceX) && (geometry.getCenterY() == sourceY))
						|| ((geometry.getCenterX() == targetX) && (geometry.getCenterY() == targetY))
						|| ((geometry.getWidth() == 0) && (geometry.getHeight() == 0))) {
					continue;
				}
				mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, route.controlPointX, route.controlPointY);
				if (intersectPoint != null) {
					obstacle = cell;
					break;
				}
//				if (geometry.contains(route.controlPointX, route.controlPointY)) {
//					obstacle = cell;
//					break;
//				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		if (route != null) {
			double dx1 = route.controlPointX - sourceX;
			double dy1 = route.controlPointY - sourceY;
			double dx2 = targetX - route.controlPointX;
			double dy2 = targetY - route.controlPointY;
			route.cost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
			if (route.cost < remainingCost) {
				routeList.add(route);
			}
		}  // End if (route != null)

		//
		// The list of routes should be in the order of increasing costs.
		//
		EdgeRoutingBranchingLayout.sort(routeList, new RouteComparator());

		if (oneStepOnly) {

			Object parent = this.graph.getDefaultParent();
			mxCell vertex;
			Iterator<Route> iterateRoutes = routeList.iterator();
			if (iterateRoutes.hasNext()) {
				route = iterateRoutes.next();
				route.controlPoints.add(new mxPoint(route.controlPointX, route.controlPointY));
				if (source == null) {
					source = (mxCell) this.graph.insertVertex(parent, null, null, sourceX, sourceY, 0, 0);
				}
				if (target == null) {
					target = (mxCell) this.graph.insertVertex(parent, null, null, targetX, targetY, 0, 0);
				}
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, route.controlPointX, route.controlPointY, 0, 0);
				this.graph.insertEdge(parent, null, "", source, vertex, "strokeColor=0xFF0000");
				this.graph.insertEdge(parent, null, "", vertex, target, "strokeColor=0xFF0000");

				while (iterateRoutes.hasNext()) {
					route = iterateRoutes.next();
					vertex = (mxCell) this.graph.insertVertex(parent, null, null, route.controlPointX, route.controlPointY, 0, 0);
					this.graph.insertEdge(parent, null, "", source, vertex);
					this.graph.insertEdge(parent, null, "", vertex, target);
				}  // End while (iterateRoutes.hasNext())
			}  // End if (iterateRoutes.hasNext())

		} else {  // End if (oneStepOnly)

			if (!(routeList.isEmpty())) {
				//
				// Recursively route the route with the lowest possible cost if
				// its cost is not updated (which means it has not been fully
				// routed).
				//
				double currentRemainingCost = remainingCost;

				while (!(routeList.get(0).isCostUpdated)) {

					route = routeList.get(0);
					ArrayList<mxPoint> tempControlPoints = new ArrayList<mxPoint>();
//					double cost1 = this.route(
//							sourceX,
//							sourceY,
//							route.controlPointX,
//							route.controlPointY,
//							tempControlPoints,
//							currentRemainingCost,
//							false);
//					route.controlPoints.addAll(tempControlPoints);
//					tempControlPoints.clear();
					offsetX = route.controlPointX - sourceX;
					offsetY = route.controlPointY - sourceY;
					double cost1 = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
					route.controlPoints.add(new mxPoint(route.controlPointX, route.controlPointY));
					double cost2 = 0;
					if (cost1 < currentRemainingCost) {
						cost2 = this.route(
								route.controlPointX,
								route.controlPointY,
								targetX,
								targetY,
								tempControlPoints,
								currentRemainingCost - cost1,
								false);
						route.controlPoints.addAll(tempControlPoints);
					}  // End if (cost1 < remainingCost)
					tempControlPoints = null;  // ArrayList<mxPoint> tempControlPoints = new ArrayList<mxPoint>();
					route.cost = cost1 + cost2;
					route.isCostUpdated = true;
					//
					// Updaing current remaining cost to the lowest update cost
					// helps to prune the branching search tree.
					//
					if (route.cost < currentRemainingCost) {
						currentRemainingCost = route.cost;
					}
					EdgeRoutingBranchingLayout.sort(routeList, new RouteComparator());

				}  // End while (!(routeList.get(0).isCostUpdated))

			}  // End if (!(routeList.isEmpty()))

		}  // End else part of if (oneStepOnly)

		double cost = straightCost;
		if (!(routeList.isEmpty())) {
			route = routeList.get(0);
			if (route.cost < remainingCost) {
				controlPoints.addAll(route.controlPoints);
				cost = route.cost;
			}
		}  // End if (!(routeList.isEmpty()))
//		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f\n", sourceX, sourceY, targetX, targetY, cost);
		return cost;
	}  // End protected double route(...)

	protected static <T> void sort(List<T> list, Comparator<? super T> c) {
		if (list == null) {
			return;
		}
		if (list.size() == 2) {
			T first = list.get(0);
			if (c.compare(first, list.get(1)) > 0) {
				list.remove(first);
				list.add(first);
			}
		} else if (list.size() > 2) {
			Collections.sort(list, c);
		}
	}  // End protected static <T> void sort(List<T> list, Comparator<? super T> c)

	protected static class ObstacleVerticesComparator implements Comparator<mxCell> {

		protected double sourceX;
		protected double sourceY;

		public ObstacleVerticesComparator(double sourceX, double sourceY) {
			this.sourceX = sourceX;
			this.sourceY = sourceY;
		}

		@Override
		public int compare(mxCell o1, mxCell o2) {

			mxGeometry geometry1 = o1.getGeometry();
			if (geometry1 == null) {
				throw new IllegalArgumentException("The first mxCell has null geomtry.");
			}
			mxGeometry geometry2 = o2.getGeometry();
			if (geometry1 == null) {
				throw new IllegalArgumentException("The second mxCell has null geomtry.");
			}

			double offsetX1 = geometry1.getCenterX() - this.sourceX;
			double offsetY1 = geometry1.getCenterY() - this.sourceY;
			double offsetX2 = geometry2.getCenterX() - this.sourceX;
			double offsetY2 = geometry2.getCenterY() - this.sourceY;

			return (int) (offsetX1 * offsetX1 + offsetY1 * offsetY1 - offsetX2 * offsetX2 - offsetY2 * offsetY2);
		}  // End public int compare(mxCell o1, mxCell o2)
	}  // End protected static class ObstacleVerticesComparator implements Comparator<mxCell>

	protected static class Route {

		public double controlPointX;
		public double controlPointY;
		public List<mxPoint> controlPoints;
		public double cost;
		public boolean isCostUpdated;

		public Route() {
			this.controlPointX = 0;
			this.controlPointY = 0;
			this.controlPoints = new ArrayList<mxPoint>();
			this.cost = 0;
			this.isCostUpdated = false;
		}
	}  // End protected static class Route

	protected static class RouteComparator implements Comparator<Route> {

		@Override
		public int compare(Route o1, Route o2) {
			return (int) (o1.cost - o2.cost);
		}  // End public int compare(mxCell o1, mxCell o2)
	}  // End protected static class RouteComparator implements Comparator<Route>
}  // End public class EdgeRoutingBranchingLayout extends mxGraphLayout
