/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
			System.out.println("Routing edge from " + sourceValue.toString() + " to " + targetValue.toString() + ".");
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

		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f\n", sourceX, sourceY, targetX, targetY);
		mxCell source = null;
		mxCell target = null;
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
		double straightCost = Math.sqrt(offsetX * offsetX + offsetY * offsetY) + obstacles.size() * 10000;

		if (obstacles.isEmpty()) {
			System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f\n", sourceX, sourceY, targetX, targetY, straightCost);
			return straightCost;
		}

		if (remainingCost <= 0) {
			remainingCost = straightCost;
		}
		double leftControlPointX = targetX;
		double leftControlPointY = targetY;
		if (obstacles.size() > 1) {
			Collections.sort(obstacles, new ObstacleVerticesComparator(sourceX, sourceY));
		}
		mxCell obstacle = obstacles.get(0);

		while (obstacle != null) {

			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = leftControlPointX - sourceX;
			offsetY = leftControlPointY - sourceY;
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees couterclockwise and adjust its length to detourLength
			//
			double detourX = offsetY * detourLength / offsetLength;
			double detourY = -offsetX * detourLength / offsetLength;
			leftControlPointX = geometry.getCenterX() + detourX;
			leftControlPointY = geometry.getCenterY() + detourY;

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
				mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, leftControlPointX, leftControlPointY);
				if (intersectPoint != null) {
					obstacle = cell;
					break;
				}
//				if (geometry.contains(controlPointX, controlPointY)) {
//					obstacle = cell;
//					break;
//				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		double dx1 = leftControlPointX - sourceX;
		double dy1 = leftControlPointY - sourceY;
		double dx2 = targetX - leftControlPointX;
		double dy2 = targetY - leftControlPointY;
		double leftCost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);

		double rightControlPointX = targetX;
		double rightControlPointY = targetY;
		obstacle = obstacles.get(0);

		while (obstacle != null) {

			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = rightControlPointX - sourceX;
			offsetY = rightControlPointY - sourceY;
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees clockwise and adjust its length to detourLength
			//
			double detourX = -offsetY * detourLength / offsetLength;
			double detourY = offsetX * detourLength / offsetLength;
			rightControlPointX = geometry.getCenterX() + detourX;
			rightControlPointY = geometry.getCenterY() + detourY;

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
				mxPoint intersectPoint = geometry.intersectLine(sourceX, sourceY, rightControlPointX, rightControlPointY);
				if (intersectPoint != null) {
					obstacle = cell;
					break;
				}
//				if (geometry.contains(rightControlPointX, rightControlPointY)) {
//					obstacle = cell;
//					break;
//				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		dx1 = rightControlPointX - sourceX;
		dy1 = rightControlPointY - sourceY;
		dx2 = targetX - rightControlPointX;
		dy2 = targetY - rightControlPointY;
		double rightCost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);

		List<mxPoint> leftControlPoints = new ArrayList<mxPoint>();
		List<mxPoint> rightControlPoints = new ArrayList<mxPoint>();
		List<mxPoint> firstHalfControlPoints;
		List<mxPoint> secondHalfControlPoints;

		if (oneStepOnly) {

			Object parent = this.graph.getDefaultParent();
			if (source == null) {
				source = (mxCell) this.graph.insertVertex(parent, null, null, sourceX, sourceY, 0, 0);
			}
			if (target == null) {
				target = (mxCell) this.graph.insertVertex(parent, null, null, targetX, targetY, 0, 0);
			}
			mxCell vertex;
			if (leftCost < rightCost) {
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, leftControlPointX, leftControlPointY, 0, 0);
				this.graph.insertEdge(parent, null, "", source, vertex, "strokeColor=0xFF0000");
				this.graph.insertEdge(parent, null, "", vertex, target, "strokeColor=0xFF0000");
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, rightControlPointX, rightControlPointY, 0, 0);
			} else {
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, rightControlPointX, rightControlPointY, 0, 0);
				this.graph.insertEdge(parent, null, "", source, vertex, "strokeColor=0xFF0000");
				this.graph.insertEdge(parent, null, "", vertex, target, "strokeColor=0xFF0000");
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, leftControlPointX, leftControlPointY, 0, 0);
			}
			this.graph.insertEdge(parent, null, "", source, vertex);
			this.graph.insertEdge(parent, null, "", vertex, target);

		} else {  // End if (oneStepOnly)

			if (leftCost < remainingCost) {
				if (rightCost < remainingCost) {
					if (leftCost < rightCost) {
						leftCost = this.routeWithOneControlPoint(
								sourceX,
								sourceY,
								targetX,
								targetY,
								leftControlPoints,
								remainingCost,
								leftControlPointX,
								leftControlPointY);
						if (rightCost < leftCost) {
							rightCost = this.routeWithOneControlPoint(
									sourceX,
									sourceY,
									targetX,
									targetY,
									rightControlPoints,
									remainingCost,
									rightControlPointX,
									rightControlPointY);
						}  // End if (rightCost < leftCost)
					} else {  // End if (leftCost < rightCost)
						rightCost = this.routeWithOneControlPoint(
								sourceX,
								sourceY,
								targetX,
								targetY,
								rightControlPoints,
								remainingCost,
								rightControlPointX,
								rightControlPointY);
						if (leftCost < rightCost) {
							leftCost = this.routeWithOneControlPoint(
									sourceX,
									sourceY,
									targetX,
									targetY,
									leftControlPoints,
									remainingCost,
									leftControlPointX,
									leftControlPointY);
						}  // End if (leftCost < rightCost)
					}  // End else part of if (leftCost < rightCost)
				} else {  // End if (rightCost < remainingCost)
					// rightCost > remainingCost so no need to consider right path
					leftCost = this.routeWithOneControlPoint(
							sourceX,
							sourceY,
							targetX,
							targetY,
							leftControlPoints,
							remainingCost,
							leftControlPointX,
							leftControlPointY);
				}  // End else part of if (rightCost < remainingCost)
			} else {  // End if (leftCost < remainingCost)
				// leftCost > remainingCost so no need to consider left path
				if (rightCost < remainingCost) {
					rightCost = this.routeWithOneControlPoint(
							sourceX,
							sourceY,
							targetX,
							targetY,
							rightControlPoints,
							remainingCost,
							rightControlPointX,
							rightControlPointY);
				}  // End if (rightCost < remainingCost)
			}  // End else part of if (leftCost < remainingCost)

		}  // End else part of if (oneStepOnly)

		double cost = straightCost;
		if (leftCost < remainingCost) {
			if (rightCost < remainingCost) {
				if (leftCost < rightCost) {
					if (oneStepOnly) {
						controlPoints.add(new mxPoint(leftControlPointX, leftControlPointY));
						controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
					} else {
						controlPoints.addAll(leftControlPoints);
					}
					cost = leftCost;
				} else {  // End if (leftCost < rightCost)
					if (oneStepOnly) {
						controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
						controlPoints.add(new mxPoint(leftControlPointX, leftControlPointY));
					} else {
						controlPoints.addAll(rightControlPoints);
					}
					cost = rightCost;
				}  // End else part of if (leftCost < rightCost)
			} else {  // End if (rightCost < remainingCost)
				if (oneStepOnly) {
					controlPoints.add(new mxPoint(leftControlPointX, leftControlPointY));
					controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
				} else {
					controlPoints.addAll(leftControlPoints);
				}
			}  // End else part of if (rightCost < remainingCost)
		} else {  // End if (leftCost < remainingCost)
			if (rightCost < remainingCost) {
				if (oneStepOnly) {
					controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
					controlPoints.add(new mxPoint(leftControlPointX, leftControlPointY));
				} else {
					controlPoints.addAll(rightControlPoints);
				}
				cost = rightCost;
			}  // End if (rightCost < remainingCost)
		}  // End else part of if (leftCost < remainingCost)
		leftControlPoints = null;  // List<mxPoint> leftControlPoints = new ArrayList<mxPoint>();
		rightControlPoints = null;  // List<mxPoint> rightControlPoints = new ArrayList<mxPoint>();
		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f\n", sourceX, sourceY, targetX, targetY, cost);
		return cost;
	}  // End protected double route(...)

	protected double routeWithOneControlPoint(
			double sourceX,
			double sourceY,
			double targetX,
			double targetY,
			List<mxPoint> controlPoints,
			double remainingCost,
			double controlPointX,
			double controlPointY) {

		double cost = 0;
		List<mxPoint> tempControlPoints = new ArrayList<mxPoint>();
		double cost1 = this.route(
				sourceX,
				sourceY,
				controlPointX,
				controlPointY,
				tempControlPoints,
				remainingCost,
				false);
		if (cost1 < remainingCost) {
			controlPoints.addAll(tempControlPoints);
			cost = cost + cost1;
		}
		controlPoints.add(new mxPoint(controlPointX, controlPointY));
		tempControlPoints.clear();
		double cost2 = this.route(
				controlPointX,
				controlPointY,
				targetX,
				targetY,
				tempControlPoints,
				remainingCost - cost1,
				false);
		if (cost2 < remainingCost - cost1) {
			controlPoints.addAll(tempControlPoints);
			cost = cost + cost2;
		}
		return cost;
	}  // End protected double routeWithOneControlPoint(...)

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
}  // End public class EdgeRoutingBranchingLayout extends mxGraphLayout
