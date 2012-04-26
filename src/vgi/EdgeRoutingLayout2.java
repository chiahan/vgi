/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.*;

/**
 *
 * @author JLiu
 */
public class EdgeRoutingLayout2 extends mxGraphLayout {

	public EdgeRoutingLayout2(mxGraph graph) {
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

	public double route(mxCell edge) {

		if (edge == null) {
			throw new IllegalArgumentException("Input 'edge' is null.");
		}
		if (!(edge.isEdge())) {
			throw new IllegalArgumentException("Input 'edge' is not an edge.");
		}
		mxICell source = edge.getSource();
		if (source == null) {
			return 0;
//			throw new IllegalArgumentException("Input 'edge' has no source vertex.");
		}
		mxGeometry sourceGeometry = source.getGeometry();
		if (sourceGeometry == null) {
			throw new IllegalArgumentException("Source vertex of input 'edge' has no position data.");
		}
		mxICell target = edge.getTarget();
		if (target == null) {
			return 0;
//			throw new IllegalArgumentException("Input 'edge' has no target vertex.");
		}
		mxGeometry targGeometry = target.getGeometry();
		if (targGeometry == null) {
			throw new IllegalArgumentException("Target vertex of input 'edge' has no position data.");
		}

		if (source.equals(target)) {
			//
			// Loop transition layout to be implemented later.
			//
			return 0;
		}

		List<mxPoint> controlPoints = new ArrayList<mxPoint>();
		double cost = this.route(
				sourceGeometry.getCenterX(),
				sourceGeometry.getCenterY(),
				targGeometry.getCenterX(),
				targGeometry.getCenterY(),
				controlPoints,
				0);
		super.setEdgePoints(edge, controlPoints);

		return cost;
	}  // End public double route(mxCell edge)

	public double route(
			double sourceX,
			double sourceY,
			double targetX,
			double targetY,
			List<mxPoint> controlPoints,
			double remainingCost) {

		if ((controlPoints == null) || !(controlPoints.isEmpty())) {
			throw new IllegalArgumentException("Input 'controlPoints' must be an empty ArrayList<mxPoint>.");
		}

		List<mxCell> obstacles = new ArrayList<mxCell>();
		Object[] cells = this.graph.getChildCells(this.graph.getDefaultParent());

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
			if ((geometry == null)
					|| ((geometry.getCenterX() == sourceX) && (geometry.getCenterY() == sourceY))
					|| ((geometry.getCenterX() == targetX) && (geometry.getCenterY() == targetY))) {
				continue;
			}
			mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, targetX, targetY);
			if (intersectPoint != null) {
				obstacles.add(cell);
			}
		}  // End for (int index = 0; index < cells.length; index++)

		double offsetX = targetX - sourceX;
		double offsetY = targetY - sourceY;

		if (obstacles.isEmpty()) {
			return Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		}

		double controlPointX = sourceX + offsetX / 2;
		double controlPointY = sourceY + offsetY / 2;
		if (remainingCost <= 0) {
			remainingCost = Math.sqrt(offsetX * offsetX + offsetY * offsetY) + obstacles.size() * 10000;
		}
		if (obstacles.size() > 1) {
			Collections.sort(obstacles, new ObstacleVerticesComparator(sourceX, sourceY));
		}
		mxCell obstacle = obstacles.get(0);

		while (obstacle != null) {
			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = geometry.getCenterX() - sourceX;
			offsetY = geometry.getCenterY() - sourceY;
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees couterclockwise and adjust its length to detourLength
			//
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourX = offsetY / offsetLength * detourLength;
			double detourY = -offsetX / offsetLength * detourLength;
			controlPointX = geometry.getCenterX() + detourX;
			controlPointY = geometry.getCenterY() + detourY;

			obstacle = (mxCell) (new mxGraphComponent(this.graph).getCellAt((int) controlPointX, (int) controlPointY));
		}  // End while (obstacle != null)

		double dx1 = controlPointX - sourceX;
		double dy1 = controlPointY - sourceY;
		double dx2 = targetX - controlPointX;
		double dy2 = targetY - controlPointY;
		double cost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
		if (cost > remainingCost) {
			offsetX = targetX - sourceX;
			offsetY = targetY - sourceY;
			return Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		}
		remainingCost = remainingCost - cost;

		List<mxPoint> firstHalfControlPoints = new ArrayList<mxPoint>();
		cost = this.route(
				sourceX,
				sourceY,
				controlPointX,
				controlPointY,
				firstHalfControlPoints,
				remainingCost);
		if (cost < remainingCost) {
			controlPoints.addAll(firstHalfControlPoints);
			remainingCost = remainingCost - cost;
		}
		controlPoints.add(new mxPoint(controlPointX, controlPointY));
		List<mxPoint> secondHalfControlPoints = new ArrayList<mxPoint>();
		double cost2 = this.route(
				controlPointX,
				controlPointY,
				targetX,
				targetY,
				secondHalfControlPoints,
				remainingCost);
		if (cost2 < remainingCost) {
			controlPoints.addAll(secondHalfControlPoints);
			cost = cost + cost2;
		}

		return cost;
	}  // End public double route(...)

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
}  // End public class EdgeRoutingLayout2 extends mxGraphLayout
