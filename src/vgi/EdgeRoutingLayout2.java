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

	public void route(mxCell edge) {

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

//		System.out.println("Routing edge from " + source.getValue().toString() + " to " + target.getValue().toString() + ".");
		List<mxPoint> controlPoints = new ArrayList<mxPoint>();
		this.route(
				sourceGeometry.getCenterX(),
				sourceGeometry.getCenterY(),
				targetGeometry.getCenterX(),
				targetGeometry.getCenterY(),
				controlPoints,
				0);
//		super.setEdgePoints(edge, controlPoints);
		mxCell[] cells = {edge};
		if (controlPoints.isEmpty()) {
			this.graph.setCellStyles("strokeColor", mxUtils.hexString(Color.BLACK), cells);
		} else {  // End if (controlPoints.isEmpty())
			this.graph.removeCells(cells);
			Object parent = this.graph.getDefaultParent();
			mxPoint point = controlPoints.get(0);
			mxCell vertex = (mxCell) this.graph.insertVertex(parent, null, null, point.getX(), point.getY(), 0, 0);
			this.graph.insertEdge(parent, null, "-", source, vertex, "strokeColor=0xFF0000");
			this.graph.insertEdge(parent, null, "-", vertex, target, "strokeColor=0xFF0000");
			if (controlPoints.size() > 1) {
				point = controlPoints.get(1);
				vertex = (mxCell) this.graph.insertVertex(parent, null, null, point.getX(), point.getY(), 0, 0);
				this.graph.insertEdge(parent, null, "-", source, vertex);
				this.graph.insertEdge(parent, null, "-", vertex, target);
			}
		}  // End else part of if (controlPoints.isEmpty())
	}  // End public void route(mxCell edge)

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

		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f\n", sourceX, sourceY, targetX, targetY);
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
					|| ((geometry.getCenterX() == targetX) && (geometry.getCenterY() == targetY))
					|| (geometry.getWidth() == 0)
					|| (geometry.getHeight() == 0)) {
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
		double controlPointX = targetX;
		double controlPointY = targetY;
		if (obstacles.size() > 1) {
			Collections.sort(obstacles, new ObstacleVerticesComparator(sourceX, sourceY));
		}
		mxCell obstacle = obstacles.get(0);

		while (obstacle != null) {

			mxGeometry geometry = obstacle.getGeometry();
			if (geometry == null) {
				throw new NullPointerException("An obstacle cell in edge routing has null geometry.  This should not be possible.  Something has gone terribly wrong.");
			}
			offsetX = controlPointX - sourceX;
			offsetY = controlPointY - sourceY;
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			double detourLength = (geometry.getHeight() > geometry.getWidth()) ? geometry.getHeight() : geometry.getWidth();
			//
			// Rotate the offset vector 90 degrees couterclockwise and adjust its length to detourLength
			//
			double detourX = offsetY * detourLength / offsetLength;
			double detourY = -offsetX * detourLength / offsetLength;
			controlPointX = geometry.getCenterX() + detourX;
			controlPointY = geometry.getCenterY() + detourY;

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
						|| (geometry.getWidth() == 0)
						|| (geometry.getHeight() == 0)) {
					continue;
				}
				if (geometry.contains(controlPointX, controlPointY)) {
					obstacle = cell;
					break;
				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		double dx1 = controlPointX - sourceX;
		double dy1 = controlPointY - sourceY;
		double dx2 = targetX - controlPointX;
		double dy2 = targetY - controlPointY;
		double leftCost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
//		if (cost > remainingCost) {
//			System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f, remaining cost: %.1f\n", sourceX, sourceY, targetX, targetY, cost, remainingCost);
//			offsetX = targetX - sourceX;
//			offsetY = targetY - sourceY;
//			return Math.sqrt(offsetX * offsetX + offsetY * offsetY);
//		}
//		remainingCost = remainingCost - cost;

//		List<mxPoint> leftControlPoints = new ArrayList<mxPoint>();
//		if (leftCost < remainingCost) {
//			leftCost = 0;
//			List<mxPoint> firstHalfControlPoints = new ArrayList<mxPoint>();
//			double leftCost1 = this.route(
//					sourceX,
//					sourceY,
//					controlPointX,
//					controlPointY,
//					firstHalfControlPoints,
//					remainingCost);
//			if (leftCost1 < remainingCost) {
//				leftControlPoints.addAll(firstHalfControlPoints);
//				leftCost = leftCost1;
////			remainingCost = remainingCost - cost;
//			}
//			leftControlPoints.add(new mxPoint(controlPointX, controlPointY));
//			List<mxPoint> secondHalfControlPoints = new ArrayList<mxPoint>();
//			double leftCost2 = this.route(
//					controlPointX,
//					controlPointY,
//					targetX,
//					targetY,
//					secondHalfControlPoints,
//					remainingCost - leftCost1);
//			if (leftCost2 < remainingCost - leftCost1) {
//				leftControlPoints.addAll(secondHalfControlPoints);
//				leftCost = leftCost + leftCost2;
//			}
//		}  // End if (leftCost < remainingCost)

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
						|| (geometry.getWidth() == 0)
						|| (geometry.getHeight() == 0)) {
					continue;
				}
				if (geometry.contains(rightControlPointX, rightControlPointY)) {
					obstacle = cell;
					break;
				}
			}  // End for (int index = 0; index < cells.length; index++)

		}  // End while (obstacle != null)

		dx1 = rightControlPointX - sourceX;
		dy1 = rightControlPointY - sourceY;
		dx2 = targetX - controlPointX;
		dy2 = targetY - controlPointY;
		double rightCost = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);

//		List<mxPoint> rightControlPoints = new ArrayList<mxPoint>();
//		if (rightCost < remainingCost) {
//			rightCost = 0;
//			List<mxPoint> firstHalfControlPoints = new ArrayList<mxPoint>();
//			double rightCost1 = this.route(
//					sourceX,
//					sourceY,
//					controlPointX,
//					controlPointY,
//					firstHalfControlPoints,
//					remainingCost);
//			if (rightCost1 < remainingCost) {
//				rightControlPoints.addAll(firstHalfControlPoints);
//				rightCost = rightCost1;
//			}
//			rightControlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
//			List<mxPoint> secondHalfControlPoints = new ArrayList<mxPoint>();
//			double rightCost2 = this.route(
//					controlPointX,
//					controlPointY,
//					targetX,
//					targetY,
//					secondHalfControlPoints,
//					remainingCost - rightCost1);
//			if (rightCost2 < remainingCost - rightCost1) {
//				rightControlPoints.addAll(secondHalfControlPoints);
//				rightCost = rightCost + rightCost2;
//			}
//		}  // End if (rightCost < remainingCost)

		double cost = straightCost;
//		if (leftCost < remainingCost) {
//			controlPoints.addAll(leftControlPoints);
//			cost = leftCost;
//		} else {
//			cost = remainingCost;
//		}
		if (leftCost < rightCost) {
//			controlPoints.addAll(leftControlPoints);
			controlPoints.add(new mxPoint(controlPointX, controlPointY));
			controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
			cost = leftCost;
		} else /*
		 * if (leftCost > rightCost)
		 */ {
//			controlPoints.addAll(rightControlPoints);
			controlPoints.add(new mxPoint(rightControlPointX, rightControlPointY));
			controlPoints.add(new mxPoint(controlPointX, controlPointY));
			cost = rightCost;
//		} else if (leftCost == rightCost) {
//			if (((int) leftCost) % 2 == 0) {
//				controlPoints.addAll(leftControlPoints);
//				cost = leftCost;
//			} else {
//				controlPoints.addAll(rightControlPoints);
//				cost = rightCost;
//			}
		}
//		leftControlPoints = null;  // List<mxPoint> leftControlPoints = new ArrayList<mxPoint>();
//		rightControlPoints = null;  // List<mxPoint> rightControlPoints = new ArrayList<mxPoint>();
		System.out.printf("sourceX: %.1f, sourceY: %.1f, targetX: %.1f, targetY: %.1f, cost: %.1f\n", sourceX, sourceY, targetX, targetY, cost);
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
