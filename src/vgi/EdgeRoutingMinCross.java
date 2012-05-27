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
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author JLiu
 */
public class EdgeRoutingMinCross extends mxGraphLayout {

	public EdgeRoutingMinCross(mxGraph graph) {
		super(graph);
	}

	@Override
	public void execute(Object parent) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	protected static final double VISIBILITY_GRAPH_VERTEX_WDITH = 5;
	protected static final double VISIBILITY_GRAPH_VERTEX_HEIGHT = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double MINIMUM_SPACING = 9;

	protected static mxGraph buildWeightedVisibilityGraph(mxGraph inGraph) {

		mxGraph outGraph = inGraph;//new mxGraph();
		Object parent = outGraph.getDefaultParent();
		Object objects[] = inGraph.getChildVertices(inGraph.getDefaultParent());
		List<mxRectangle> roadblocksList = new ArrayList<mxRectangle>(objects.length);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			roadblocksList.add(vertex.getGeometry());

			List<mxPoint> pointsAroundRoadblock = EdgeRoutingMinCross.findPointsAroundRoadblock(inGraph, vertex);
			Iterator<mxPoint> iteratePointsAroundRoadblock = pointsAroundRoadblock.iterator();
			while (iteratePointsAroundRoadblock.hasNext()) {
				mxPoint point = iteratePointsAroundRoadblock.next();
				outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX() - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						point.getY() - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);
			}  // End while (iteratePointsAroundRoadblock.hasNext())

		}  // End for (int index = 0; index < objects.length; index++)

		List<Hindrance> hindrancesList = new ArrayList<Hindrance>();
		objects = inGraph.getChildEdges(inGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("An edge is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("edge is not an edge.");
			}
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxGeometry edgeGeometry = edge.getGeometry();
			mxPoint point = null;
			mxGeometry vertexGeometry = null;
			if (source == null) {
				if (target == null) {
					throw new IllegalStateException("edge has neither source nor target vertices.");
				}
				point = edgeGeometry.getSourcePoint();
				vertexGeometry = target.getGeometry();
			} else if (target == null) {
				point = edgeGeometry.getTargetPoint();
				vertexGeometry = source.getGeometry();
			}
			if ((point != null) && (vertexGeometry != null)) {
				hindrancesList.add(new Hindrance(vertexGeometry.getCenterX(), vertexGeometry.getCenterY(), point.getX(), point.getY()));
				double offsetX = point.getX() - vertexGeometry.getCenterX();
				double offsetY = point.getY() - vertexGeometry.getCenterY();
				double length = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
				offsetX = offsetX / length;
				offsetY = offsetY / length;
				double rightX = -offsetY;
				double rightY = offsetX;
				outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX() + (offsetX + rightX) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						point.getY() + (offsetY + rightY) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);
				outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX() + (offsetX - rightX) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						point.getY() + (offsetY - rightY) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);
				continue;
			}  // End if ((point != null) && (vertexGeometry != null))

			List<mxPoint> controlPoints = edge.getGeometry().getPoints();
			if ((controlPoints == null) || (controlPoints.isEmpty())) {
				continue;
			}
			mxGeometry geometry = source.getGeometry();
			mxPoint previousPoint = new mxPoint(geometry.getCenterX(), geometry.getCenterY());

			for (int pointIndex = 0; pointIndex < controlPoints.size(); pointIndex++) {

				mxPoint currentPoint = controlPoints.get(pointIndex);
				hindrancesList.add(new Hindrance(previousPoint.getX(), previousPoint.getY(), currentPoint.getX(), currentPoint.getY()));
				mxPoint nextPoint;
				if (pointIndex < controlPoints.size() - 1) {
					nextPoint = controlPoints.get(pointIndex + 1);
				} else {
					geometry = target.getGeometry();
					nextPoint = new mxPoint(geometry.getCenterX(), geometry.getCenterY());
					hindrancesList.add(new Hindrance(currentPoint.getX(), currentPoint.getY(), nextPoint.getX(), nextPoint.getY()));
				}
				double previousVectorX = previousPoint.getX() - currentPoint.getX();
				double previousVectorY = previousPoint.getY() - currentPoint.getY();
				double previousVectorLength = Math.sqrt(previousVectorX * previousVectorX + previousVectorY * previousVectorY);
				previousVectorX = previousVectorX / previousVectorLength;
				previousVectorY = previousVectorY / previousVectorLength;
				double nextVectorX = nextPoint.getX() - currentPoint.getX();
				double nextVectorY = nextPoint.getY() - currentPoint.getY();
				double nextVectorLength = Math.sqrt(nextVectorX * nextVectorX + nextVectorY * nextVectorY);
				nextVectorX = nextVectorX / nextVectorLength;
				nextVectorY = nextVectorY / nextVectorLength;
				previousPoint = currentPoint;
				if (Vector2D.areSameDirection(previousVectorX, previousVectorY, nextVectorX, nextVectorY)) {
					double offsetX = -previousVectorX;
					double offsetY = -previousVectorY;
					double rightX = -offsetY;
					double rightY = offsetX;
					outGraph.insertVertex(
							parent,
							null,
							null,
							currentPoint.getX() + (offsetX + rightX) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
							currentPoint.getY() + (offsetY + rightY) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
							VISIBILITY_GRAPH_VERTEX_WDITH,
							VISIBILITY_GRAPH_VERTEX_HEIGHT);
					outGraph.insertVertex(
							parent,
							null,
							null,
							currentPoint.getX() + (offsetX - rightX) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
							currentPoint.getY() + (offsetY - rightY) * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
							VISIBILITY_GRAPH_VERTEX_WDITH,
							VISIBILITY_GRAPH_VERTEX_HEIGHT);
					continue;
				}  // End if (Vector2D.areSameDirection(previousVectorX, previousVectorY, nextVectorX, nextVectorY))

				double bisectorX;
				double bisectorY;
				if (Vector2D.areOpposite(previousVectorX, previousVectorY, nextVectorX, nextVectorY)) {
					bisectorX = -nextVectorY;
					bisectorY = nextVectorX;
				} else {
					bisectorX = previousVectorX + nextVectorX;
					bisectorY = previousVectorY + nextVectorY;
					double bisectorLength = Math.sqrt(bisectorX * bisectorX + bisectorY * bisectorY);
					bisectorX = bisectorX / bisectorLength;
					bisectorY = bisectorY / bisectorLength;
				}
				outGraph.insertVertex(
						parent,
						null,
						null,
						currentPoint.getX() + bisectorX * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						currentPoint.getY() + bisectorY * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);
				outGraph.insertVertex(
						parent,
						null,
						null,
						currentPoint.getX() - bisectorX * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						currentPoint.getY() - bisectorY * MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);

			}  // End for (int pointIndex = 0; pointIndex < controlPoints.size(); pointIndex++)

		}  // End for (int index = 0; index < objects.length; index++)

		roadblocksList = null;  // List<mxICell> roadblocksList = new LinkedList<mxICell>();

		return outGraph;
	}  // End protected static mxGraph buildWeightedVisibilityGraph(mxGraph inGraph)

	protected static List<mxPoint> findPointsAroundRoadblock(mxGraph inGraph, mxICell inVertex) {

		if (inVertex == null) {
			throw new IllegalArgumentException("inVertex is null.");
		}
		if (!(inVertex.isVertex())) {
			throw new IllegalArgumentException("inVertex is not a vertex.");
		}
		mxGeometry geometry = inVertex.getGeometry();
		if (geometry == null) {
			throw new IllegalStateException("inVertex has null geometry.");
		}

		List<mxPoint> outPoints = new LinkedList<mxPoint>();
		mxGraphComponent graphComponent = new mxGraphComponent(inGraph);
		//
		// Add points for the 4 corners of the rectangle bounding inVertex.
		//
		double x = geometry.getX() + geometry.getWidth() + MINIMUM_SPACING;
		double y = geometry.getY() + geometry.getHeight() + MINIMUM_SPACING;
		if (graphComponent.getCellAt((int) x, (int) y) == null) {
			outPoints.add(new mxPoint(x, y));
		} else {  // End if (graphComponent.getCellAt((int) x, (int) y) == null)
			if (graphComponent.getCellAt((int) (x + MINIMUM_SPACING), (int) y) == null) {
				outPoints.add(new mxPoint(x + MINIMUM_SPACING, y));
			}
			if (graphComponent.getCellAt((int) x, (int) (y + MINIMUM_SPACING)) == null) {
				outPoints.add(new mxPoint(x, y + MINIMUM_SPACING));
			}
		}  // End else part of if (graphComponent.getCellAt((int) x, (int) y) == null)
		x = geometry.getX() - MINIMUM_SPACING;
		y = geometry.getY() + geometry.getHeight() + MINIMUM_SPACING;
		if (graphComponent.getCellAt((int) x, (int) y) == null) {
			outPoints.add(new mxPoint(x, y));
		} else {  // End if (graphComponent.getCellAt((int) x, (int) y) == null)
			if (graphComponent.getCellAt((int) (x - MINIMUM_SPACING), (int) y) == null) {
				outPoints.add(new mxPoint(x - MINIMUM_SPACING, y));
			}
			if (graphComponent.getCellAt((int) x, (int) (y + MINIMUM_SPACING)) == null) {
				outPoints.add(new mxPoint(x, y + MINIMUM_SPACING));
			}
		}  // End else part of if (graphComponent.getCellAt((int) x, (int) y) == null)
		x = geometry.getX() - MINIMUM_SPACING;
		y = geometry.getY() - MINIMUM_SPACING;
		if (graphComponent.getCellAt((int) x, (int) y) == null) {
			outPoints.add(new mxPoint(x, y));
		} else {  // End if (graphComponent.getCellAt((int) x, (int) y) == null)
			if (graphComponent.getCellAt((int) (x - MINIMUM_SPACING), (int) y) == null) {
				outPoints.add(new mxPoint(x - MINIMUM_SPACING, y));
			}
			if (graphComponent.getCellAt((int) x, (int) (y - MINIMUM_SPACING)) == null) {
				outPoints.add(new mxPoint(x, y - MINIMUM_SPACING));
			}
		}  // End else part of if (graphComponent.getCellAt((int) x, (int) y) == null)
		x = geometry.getX() + geometry.getWidth() + MINIMUM_SPACING;
		y = geometry.getY() - MINIMUM_SPACING;
		if (graphComponent.getCellAt((int) x, (int) y) == null) {
			outPoints.add(new mxPoint(x, y));
		} else {  // End if (graphComponent.getCellAt((int) x, (int) y) == null)
			if (graphComponent.getCellAt((int) (x + MINIMUM_SPACING), (int) y) == null) {
				outPoints.add(new mxPoint(x + MINIMUM_SPACING, y));
			}
			if (graphComponent.getCellAt((int) x, (int) (y - MINIMUM_SPACING)) == null) {
				outPoints.add(new mxPoint(x, y - MINIMUM_SPACING));
			}
		}  // End else part of if (graphComponent.getCellAt((int) x, (int) y) == null)
		graphComponent = null;  // mxGraphComponent graphComponent = new mxGraphComponent(inGraph);

		int edgeCount = inVertex.getEdgeCount();
		if (edgeCount < 2) {
			return outPoints;
		}

		List<mxPoint> edgeUnitVectorsList = new ArrayList<mxPoint>(edgeCount);

		for (int index = 0; index < edgeCount; index++) {
			mxICell edge = inVertex.getEdgeAt(index);
			if ((edge == null) || (!(edge.isEdge())) || (edge.getGeometry() == null)) {
				throw new IllegalStateException("Invalid edge.");
			}
			mxGeometry edgeGeometry = edge.getGeometry();
			List<mxPoint> points = edgeGeometry.getPoints();
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxPoint point;
			if (inVertex == source) {
				if ((points == null) || (points.isEmpty())) {
					if (target == null) {
						point = edgeGeometry.getTargetPoint();
					} else {
						mxGeometry targetGeometry = target.getGeometry();
						point = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
					}
				} else {
					point = points.get(0);
				}
			} else if (inVertex == target) {
				if ((points == null) || (points.isEmpty())) {
					if (source == null) {
						point = edgeGeometry.getSourcePoint();
					} else {
						mxGeometry sourceGeometry = source.getGeometry();
						point = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
					}
				} else {
					point = points.get(points.size() - 1);
				}
			} else {
				throw new IllegalStateException("Edge does not connect to inVertex.");
			}
			double offsetX = point.getX() - geometry.getCenterX();
			double offsetY = point.getY() - geometry.getCenterY();
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			edgeUnitVectorsList.add(new mxPoint(offsetX / offsetLength, offsetY / offsetLength));
		}  // End for (int index = 0; index < edgeCount; index++)

		x = geometry.getWidth() / 2 + MINIMUM_SPACING;
		y = geometry.getHeight() / 2 + MINIMUM_SPACING;
		double radius = Math.sqrt(x * x + y * y);

		Collections.sort(edgeUnitVectorsList, new UnitVectorComparator());

		for (int index = 0; index < edgeCount; index++) {

			int nextIndex = (index + 1 >= edgeCount) ? (0) : (index + 1);
			mxPoint currentVector = edgeUnitVectorsList.get(index);
			mxPoint nextVector = edgeUnitVectorsList.get(nextIndex);
			if (Vector2D.areSameDirection(currentVector.getX(), currentVector.getY(), nextVector.getX(), nextVector.getY())) {
				continue;
			}
			double rightUnitVectorX = -currentVector.getY();
			double rightUnitVectorY = currentVector.getX();
			if (Vector2D.areOpposite(currentVector.getX(), currentVector.getY(), nextVector.getX(), nextVector.getY())) {
				outPoints.add(new mxPoint(
						geometry.getCenterX() + radius * rightUnitVectorX,
						geometry.getCenterY() + radius * rightUnitVectorY));
				continue;
			}
			double bisectorX = currentVector.getX() + nextVector.getX();
			double bisectorY = currentVector.getY() + nextVector.getY();
			//
			// if the next vecotr points to the left of the current vector
			//
			if (Vector2D.dotProduct(nextVector.getX(), nextVector.getY(), rightUnitVectorX, rightUnitVectorY) < 0) {
				bisectorX = -bisectorX;
				bisectorY = -bisectorY;
			}
			double bisectorLength = Math.sqrt(bisectorX * bisectorX + bisectorY * bisectorY);
			outPoints.add(new mxPoint(
					geometry.getCenterX() + radius * bisectorX / bisectorLength,
					geometry.getCenterY() + radius * bisectorY / bisectorLength));

		}  // End for (int index = 0; index < edgeCount; index++)

		edgeUnitVectorsList = null;  // List<mxPoint> edgeUnitVectorsList = new ArrayList<mxPoint>();

		return outPoints;
	}  // End protected static List<mxPoint> findPointsAroundRoadblock(mxGraph inGraph, mxICell inVertex)

	protected static class UnitVectorComparator implements Comparator<mxPoint> {

		@Override
		public int compare(mxPoint unitVector1, mxPoint unitVector2) {
			double angle1 = Math.acos(unitVector1.getX());
			if (unitVector1.getY() < 0) {
				angle1 = angle1 + 2 * Math.PI;
			}
			double angle2 = Math.acos(unitVector2.getX());
			if (unitVector2.getY() < 0) {
				angle2 = angle2 + 2 * Math.PI;
			}
			double difference = angle1 - angle2;
			if (difference < 0) {
				return -1;
			} else {
				return 1;
			}
		}  // End public int compare(mxPoint unitVector1, mxPoint unitVector2)
	}  // End protected static class UnitVectorComparator implements Comparator<mxPoint>

	protected static class Vector2D {

		protected static final double SMALL_VALUE = 0.01;

		public static double dotProduct(double x1, double y1, double x2, double y2) {
			return (x1 * x2 + y1 * y2);
		}  // End public static double dotProduct(double x1, double y1, double x2, double y2)

		public static boolean areOpposite(double x1, double y1, double x2, double y2) {
			double lengthSquared1 = x1 * x1 + y1 * y1;
			double lengthSquared2 = x2 * x2 + y2 * y2;
			double dotProduct = Vector2D.dotProduct(x1, y1, x2, y2);
			return ((Math.abs(lengthSquared1 * lengthSquared2 - dotProduct * dotProduct) < SMALL_VALUE)
					&& (dotProduct < 0));
		}  // End public static boolean areOpposite(double x1, double y1, double x2, double y2)

		public static boolean arePerpendicular(double x1, double y1, double x2, double y2) {
			return (Math.abs(Vector2D.dotProduct(x1, y1, x2, y2)) < SMALL_VALUE);
		}  // End public static boolean arePerpendicular(double x1, double y1, double x2, double y2)

		public static boolean areSameDirection(double x1, double y1, double x2, double y2) {
			double lengthSquared1 = x1 * x1 + y1 * y1;
			double lengthSquared2 = x2 * x2 + y2 * y2;
			double dotProduct = Vector2D.dotProduct(x1, y1, x2, y2);
			return ((Math.abs(lengthSquared1 * lengthSquared2 - dotProduct * dotProduct) < SMALL_VALUE)
					&& (dotProduct > 0));
		}  // End public static boolean areSameDirection(double x1, double y1, double x2, double y2)
	}  // End protected static class Vector2D

	protected static class Hindrance {

		double x1;
		double y1;
		double x2;
		double y2;

		public Hindrance(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}  // End protected static class Hindrance

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
			throw new IllegalArgumentException("Source vertex of input 'edge' has null position data.");
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
		mxGraph planarGraph = EdgeRoutingMinCross.planarize(this.getGraph(), edge);
		mxGraph dualGraph = EdgeRoutingMinCross.buildDualGraph(planarGraph);

		mxICell planarSource = null;
		mxICell planarTarget = null;
		Object objects[] = planarGraph.getChildVertices(planarGraph.getDefaultParent());
		int count = objects.length;

		for (int index = 0; index < count; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("vertex is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("vertex is not vertex.");
			}
			Object object = vertex.getValue();
			if (object == sourceValue) {
				planarSource = (mxICell) vertex;
			}
			if (object == targetValue) {
				planarTarget = (mxICell) vertex;
			}
			if ((planarSource != null) && (planarTarget != null)) {
				break;
			}
		}  // End for (int index = 0; index < count; index++)

		if ((planarSource == null) || (planarTarget == null)) {
			throw new IllegalStateException("Cannot find the corresponding source and target vertices in the planar graph.");
		}

		List<List<mxICell>> paths = findShortestPathsInDualGraph(dualGraph, planarSource, planarTarget);
		if ((paths == null) || (paths.isEmpty())) {
			return;
		}

		List<List<mxPoint>> confirmedPathsList = new LinkedList<List<mxPoint>>();
		Iterator<List<mxICell>> iteratePaths = paths.iterator();
		while (iteratePaths.hasNext()) {

			List<mxICell> path = iteratePaths.next();
			if ((path != null) && (!(path.isEmpty()))) {
				System.out.print("Shortest path goes through");

				Iterator<mxICell> iterateVertices = path.iterator();
				while (iterateVertices.hasNext()) {
					mxICell vertex = iterateVertices.next();
					System.out.print(" {" + vertex.getValue().toString() + "}");
//					Region region = (Region) vertex.getValue();
//					System.out.print(" contains (10, 10)? " + region.contains(10, 10));
//					System.out.print(" contains (200, 200)? " + region.contains(200, 200));
//					System.out.println();
//
//					Iterator<mxICell> iterateRegionVertices = region.iterator();
//					while (iterateRegionVertices.hasNext()) {
//						mxICell regionVertex = iterateRegionVertices.next();
//						System.out.printf("Vertex: (%.1f, %.1f)", regionVertex.getGeometry().getCenterX(), regionVertex.getGeometry().getCenterY());
//						Point2D point2d = region.getPointInside(regionVertex);
//						System.out.printf(" Point Inside: (%.1f, %.1f)%n", point2d.getX(), point2d.getY());
//					}  // End while (iterateRegionVertices.hasNext())
				}  // End while (iterateVertices.hasNext())

				System.out.println();
				confirmedPathsList.addAll(EdgeRoutingMinCross.findPathsThroughRegions(planarSource, planarTarget, path));
			}  // End if ((path != null) && (!(path.isEmpty())))

		}  // End while (iteratePaths.hasNext())

		Iterator<List<mxPoint>> iterateConfirmedPaths = confirmedPathsList.iterator();
		while (iterateConfirmedPaths.hasNext()) {

			mxGraph graph = this.getGraph();
			mxICell newEdge = (mxICell) graph.insertEdge(
					graph.getDefaultParent(),
					edge.getId(),
					edge.getValue(),
					source,
					target,
					edge.getStyle());
			Object cells[] = {newEdge};
			graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
			List<mxPoint> controlPoints = iterateConfirmedPaths.next();
			super.setEdgePoints(newEdge, controlPoints);

		}  // End while (iterateConfirmedPaths.hasNext())

		confirmedPathsList = null;  // List<List<mxPoint>> confirmedPathsList = new LinkedList<List<mxPoint>>();
		Object cells[] = {edge};
		this.getGraph().removeCells(cells);
	}  // End public void route(mxCell edge)

	protected static class Crossing {

		mxCell edge1;
		mxCell edge2;
		public double x;
		public double y;

		public Crossing() {
			this.edge1 = null;
			this.edge2 = null;
			this.x = 0;
			this.y = 0;
		}
	}  // End protected static class Crossing

	public static mxGraph planarize(mxGraph inGraph) {
		return EdgeRoutingMinCross.planarize(inGraph, null);
	}  // End public static mxGraph planarize(mxGraph inGraph)

	public static mxGraph planarize(mxGraph inGraph, mxICell edgeToIgnore) {

		if (inGraph == null) {
			throw new IllegalArgumentException("Input inGraph is null.");
		}
		mxGraph outGraph = new mxGraph();
		HashMap<mxICell, mxICell> oldToNewVerticesMap = new HashMap<mxICell, mxICell>();
		Object objects[] = inGraph.getChildVertices(inGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("vertex is not mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("vertex is not vertex.");
			}
			mxGeometry geometry = vertex.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("vertex has null geometry.");
			}
			mxICell newVertex = (mxICell) outGraph.insertVertex(
					outGraph.getDefaultParent(),
					vertex.getId(),
					vertex.getValue(),
					geometry.getX(),
					geometry.getY(),
					geometry.getWidth(),
					geometry.getHeight(),
					vertex.getStyle(),
					geometry.isRelative());
			oldToNewVerticesMap.put(vertex, newVertex);
		}  // End for (int index = 0; index < objects.length; index++)

		objects = inGraph.getChildEdges(inGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {
			if (!(objects[index] instanceof mxCell)) {
				throw new IllegalStateException("edge is not mxCell.");
			}
			mxCell edge = (mxCell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("edge is not edge.");
			}
			if (edge == edgeToIgnore) {
				continue;
			}
			mxGeometry geometry = edge.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("edge has null geometry.");
			}
			mxICell source = edge.getSource();
			mxICell target = edge.getTarget();
			if ((source == null) && (target == null)) {
				throw new IllegalStateException("The source and target of an edge can not both be null.");
			}

			Object parent = outGraph.getDefaultParent();
			if (source == null) {
				mxPoint point = geometry.getSourcePoint();
				Object dummyNode = outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX(),
						point.getY(),
						0,
						0);
				outGraph.insertEdge(
						parent,
						null,
						null,
						dummyNode,
						oldToNewVerticesMap.get(target));
				continue;
			} else if (target == null) {
				mxPoint point = geometry.getTargetPoint();
				Object dummyNode = outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX(),
						point.getY(),
						0,
						0);
				outGraph.insertEdge(
						parent,
						null,
						null,
						oldToNewVerticesMap.get(source),
						dummyNode);
				continue;
			} else if (source.equals(target)) {
				List<mxPoint> controlPoints = geometry.getPoints();
				mxPoint point;
				if ((controlPoints == null) || (controlPoints.isEmpty())) {
					mxGeometry sourceGeometry = source.getGeometry();
					if (sourceGeometry == null) {
						throw new IllegalStateException("Source vertex has null geometry.");
					}
					point = new mxPoint(
							sourceGeometry.getCenterX() + sourceGeometry.getWidth(),
							sourceGeometry.getCenterY());
				} else {  // End if ((controlPoints == null) || (controlPoints.isEmpty()))
					point = controlPoints.get(0);
				}  // End else part of if ((controlPoints == null) || (controlPoints.isEmpty()))
				Object dummyNode = outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX(),
						point.getY(),
						0,
						0);
				point = null;  // point = new mxPoint(...);
				outGraph.insertEdge(
						parent,
						null,
						null,
						oldToNewVerticesMap.get(source),
						dummyNode);
				continue;
			}  // End if (source.equals(target))

			Object previousDummyNode = oldToNewVerticesMap.get(source);
			List<mxPoint> controlPoints = geometry.getPoints();
			if (controlPoints != null) {
				Iterator<mxPoint> iterateControlPoints = controlPoints.iterator();
				while (iterateControlPoints.hasNext()) {
					mxPoint point = iterateControlPoints.next();
					Object dummyNode = outGraph.insertVertex(
							parent,
							null,
							null,
							point.getX(),
							point.getY(),
							0,
							0);
					outGraph.insertEdge(
							parent,
							null,
							null,
							previousDummyNode,
							dummyNode);
					previousDummyNode = dummyNode;
				}  // End while (iterateControlPoints.hasNext())
			}  // End if (controlPoints != null)
			outGraph.insertEdge(
					parent,
					null,
					null,
					previousDummyNode,
					oldToNewVerticesMap.get(target));
		}  // End for (int index = 0; index < objects.length; index++)

		oldToNewVerticesMap = null;  // HashMap<mxICell, mxICell> oldToNewVerticesMap = new HashMap<mxICell, mxICell>();
		List<Crossing> crossingList = new ArrayList<Crossing>();
		objects = outGraph.getChildEdges(outGraph.getDefaultParent());

		for (int index = 0; index < objects.length - 1; index++) {
			if (!(objects[index] instanceof mxCell)) {
				continue;
			}
			mxCell edge = (mxCell) objects[index];
			if (!(edge.isEdge())) {
				continue;
			}
			mxICell source = edge.getSource();
			mxICell target = edge.getTarget();
			if ((source == null)
					|| (target == null)
					|| (source.equals(target))) {
				continue;
			}
			mxGeometry geometry = source.getGeometry();
			if (geometry == null) {
				continue;
			}
			double x0 = geometry.getCenterX();
			double y0 = geometry.getCenterY();
			geometry = target.getGeometry();
			if (geometry == null) {
				continue;
			}
			double x1 = geometry.getCenterX();
			double y1 = geometry.getCenterY();

			for (int index2 = index + 1; index2 < objects.length; index2++) {
				if (!(objects[index2] instanceof mxCell)) {
					continue;
				}
				mxCell edge2 = (mxCell) objects[index2];
				if (!(edge2.isEdge())) {
					continue;
				}
				mxICell source2 = edge2.getSource();
				mxICell target2 = edge2.getTarget();
				if ((source2 == null)
						|| (target2 == null)
						|| (source2.equals(target2))) {
					continue;
				}
				geometry = source2.getGeometry();
				if (geometry == null) {
					continue;
				}
				double x2 = geometry.getCenterX();
				double y2 = geometry.getCenterY();
				geometry = target2.getGeometry();
				if (geometry == null) {
					continue;
				}
				double x3 = geometry.getCenterX();
				double y3 = geometry.getCenterY();
				if (((x0 == x2) && (y0 == y2))
						|| ((x0 == x3) && (y0 == y3))
						|| ((x1 == x2) && (y1 == y2))
						|| ((x1 == x3) && (y1 == y3))) {
					continue;
				}
				mxPoint point = mxUtils.intersection(x0, y0, x1, y1, x2, y2, x3, y3);
				if (point == null) {
					continue;
				}
				Crossing crossing = new Crossing();
				crossing.edge1 = edge;
				crossing.edge2 = edge2;
				crossing.x = point.getX();
				crossing.y = point.getY();
				crossingList.add(crossing);
				crossing = null;  // Crossing crossing = new Crossing();
			}  // End for (int index2 = index + 1; index2 < objects.length; index2++)

		}  // End for (int index = 0; index < objects.length - 1; index++)

		Iterator<Crossing> iterateCrossings = crossingList.iterator();

		while (iterateCrossings.hasNext()) {
			Crossing crossing = iterateCrossings.next();
			if ((crossing.edge1 == null) || (crossing.edge2 == null)) {
				continue;
			}
			mxICell source = crossing.edge1.getSource();
			mxICell target = crossing.edge1.getTarget();
			mxICell source2 = crossing.edge2.getSource();
			mxICell target2 = crossing.edge2.getTarget();
			if ((source == null)
					|| (target == null)
					|| (source2 == null)
					|| (target2 == null)) {
				continue;
			}
			Object[] edges = {crossing.edge1, crossing.edge2};
			outGraph.removeCells(edges);
			Object parent = outGraph.getDefaultParent();
			Object dummyNode = outGraph.insertVertex(
					parent,
					null,
					null,
					crossing.x,
					crossing.y,
					0,
					0);
			outGraph.insertEdge(parent, null, null, source, dummyNode);
			outGraph.insertEdge(parent, null, null, dummyNode, target);
			outGraph.insertEdge(parent, null, null, source2, dummyNode);
			outGraph.insertEdge(parent, null, null, dummyNode, target2);
		}  // End while (iterateCrossings.hasNext())

		crossingList = null;  // List<Crossing> crossingList = new ArrayList<Crossing>();

		return outGraph;
	}  // End public static mxGraph planarize(mxGraph inGraph, mxICell edgeToIgnore)

	protected static class EdgeVisit {

		public boolean isSourceVisited;
		public boolean isTargetVisited;

		public EdgeVisit() {
			this.isSourceVisited = false;
			this.isTargetVisited = false;
		}
	}  // End protected static class EdgeVisit

	protected static class EdgeDirection {

		public mxICell edge;
		public boolean isSourceToTarget;

		public EdgeDirection() {
			this.edge = null;
			this.isSourceToTarget = false;
		}

		public EdgeDirection(mxICell edge, boolean isSourceToTarget) {
			this.edge = edge;
			this.isSourceToTarget = isSourceToTarget;
		}
	}  // End protected class EdgeDirection

	protected static class Region<E> extends ArrayList<E> {

		public Region() {
		}

		public Region(Collection<? extends E> c) {
			super(c);
		}

		public Region(int initialCapacity) {
			super(initialCapacity);
		}

		public boolean contains(double x, double y) {
			boolean bounded = this.getBounded();
			Path2D path2d = this.getPath2d();
			if (bounded) {
				return path2d.contains(x, y);
			}
			return !(path2d.contains(x, y));
		}

		public static List findBoundaryVertices(Region inRegion1, Region inRegion2) {

			int count = inRegion1.size();
			List outList = new ArrayList();

			for (int index = 0; index < count; index++) {
				Object object = inRegion1.get(index);
				int index2 = inRegion2.lastIndexOf(object);
				if (index2 < 0) {
					continue;
				}
				int nextIndex = (index + 1 >= count) ? 0 : (index + 1);
				int previousIndex2 = (index2 - 1 < 0) ? (inRegion2.size() - 1) : (index2 - 1);
				Object nextObject = inRegion1.get(nextIndex);
				if (nextObject.equals(inRegion2.get(previousIndex2))) {
					outList.add(object);
					do {
						outList.add(nextObject);
						nextIndex = (nextIndex + 1 >= count) ? 0 : (nextIndex + 1);
						previousIndex2 = (previousIndex2 - 1 < 0) ? (inRegion2.size() - 1) : (previousIndex2 - 1);
						nextObject = inRegion1.get(nextIndex);
					} while (nextObject.equals(inRegion2.get(previousIndex2)));
					break;
				}  // End if (nextObject.equals(inRegion2.get(previousIndex2)))
			}  // End for (int index = 0; index < count; index++)

			return outList;
		}  // End public static List findBoundaryVertices(Region region1, Region region2)
		private Boolean mBounded = null;

		private boolean getBounded() {

			if (this.mBounded != null) {
				return this.mBounded.booleanValue();
			}

			if (this.size() < 2) {
				return false;
			}
			Object object = this.get(0);
			if (!(object instanceof mxICell)) {
				throw new ClassCastException("This Region object is instanciated with a type that the getBounded() method does not support.");
			}
			mxICell vertex1 = (mxICell) object;
			object = this.get(1);
			if (!(object instanceof mxICell)) {
				throw new ClassCastException("This Region object is instanciated with a type that the getBounded() method does not support.");
			}
			mxICell vertex2 = (mxICell) object;
			Point2D point2d = this.getPointInside(vertex1, vertex2);
			Path2D path2d = this.getPath2d();
			this.mBounded = path2d.contains(point2d);

			return this.mBounded.booleanValue();
		}  // End private boolean getBounded()
		private Path2D mPath2d = null;

		public Path2D getPath2d() {

			if (this.mPath2d != null) {
				return this.mPath2d;
			}

			Iterator iterateVertices = this.iterator();
			if ((!iterateVertices.hasNext())) {
				return null;
			}
			Object object = iterateVertices.next();
			if (!(object instanceof mxICell)) {
				throw new ClassCastException("This Region object is instanciated with a type that the getPath2d() method does not support.");
			}
			mxICell vertex = (mxICell) object;
			mxGeometry geometry = vertex.getGeometry();
			Path2D path2d = new Path2D.Double();
			path2d.moveTo(geometry.getCenterX(), geometry.getCenterY());
			while (iterateVertices.hasNext()) {
				object = iterateVertices.next();
				if (!(object instanceof mxICell)) {
					throw new ClassCastException("This Region object is instanciated with a type that the getPath2d() method does not support.");
				}
				vertex = (mxICell) object;
				geometry = vertex.getGeometry();
				path2d.lineTo(geometry.getCenterX(), geometry.getCenterY());
			}  // End while (iterateVertices.hasNext())
			path2d.closePath();
			this.mPath2d = path2d;
			path2d = null;  // Path2D path2d = new Path2D.Double();

			return this.mPath2d;
		}  // End public Path2D.Double getPath2d()

		public Point2D getPointInside(mxICell inVertex) {

			if (this.size() < 2) {
				return null;
			}

			int index = this.indexOf(inVertex);
			if (index < 0) {
				throw new IllegalArgumentException("inVertex is not part of this region.");
			}
			mxGeometry geometry = inVertex.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("inVertex has null geometry.");
			}

			int nextIndex = (index + 1 >= this.size()) ? 0 : (index + 1);
			int previousIndex = (index - 1 < 0) ? (this.size() - 1) : (index - 1);
			Object object = this.get(previousIndex);
			if (!(object instanceof mxICell)) {
				throw new ClassCastException("This Region object is instanciated with a type that the getPointInside() method does not support.");
			}
			mxICell vertex = (mxICell) object;
			mxGeometry geometry1 = vertex.getGeometry();
			if (geometry1 == null) {
				throw new IllegalStateException("The previous vertex has null geometry.");
			}
			double inVectorX = geometry.getCenterX() - geometry1.getCenterX();
			double inVectorY = geometry.getCenterY() - geometry1.getCenterY();
			double inLengthSquared = inVectorX * inVectorX + inVectorY * inVectorY;
			object = this.get(nextIndex);
			if (!(object instanceof mxICell)) {
				throw new ClassCastException("This Region object is instanciated with a type that the getPointInside() method does not support.");
			}
			vertex = (mxICell) object;
			geometry1 = vertex.getGeometry();
			if (geometry1 == null) {
				throw new IllegalStateException("The next vertex has null geometry.");
			}
			double outVectorX = geometry1.getCenterX() - geometry.getCenterX();
			double outVectorY = geometry1.getCenterY() - geometry.getCenterY();
			double outLengthSquared = outVectorX * outVectorX + outVectorY * outVectorY;
			double offsetX = 0;
			double offsetY = 0;
			double offsetLengthSquared = 0;

			double dotProduct = inVectorX * outVectorX + inVectorY * outVectorY;
			if (dotProduct * dotProduct == inLengthSquared * outLengthSquared) {
				if (dotProduct < 0) {
					offsetX = inVectorX;
					offsetY = inVectorY;
					offsetLengthSquared = inLengthSquared;
				} else {
					//
					// Turn the incoming vector to the right (clockwise) 90 degrees.
					//
					offsetX = -inVectorY;
					offsetY = inVectorX;
					offsetLengthSquared = inLengthSquared;
				}
			} else {  // End if (dotProduct * dotProduct == inLengthSquared * outLengthSquared)
				offsetX = outVectorX * 10 / Math.sqrt(outLengthSquared) - inVectorX * 10 / Math.sqrt(inLengthSquared);
				offsetY = outVectorY * 10 / Math.sqrt(outLengthSquared) - inVectorY * 10 / Math.sqrt(inLengthSquared);
				offsetLengthSquared = offsetX * offsetX + offsetY * offsetY;
			}  // End else part of if (dotProduct * dotProduct == inLengthSquared * outLengthSquared)

			double width = geometry.getWidth();
			double height = geometry.getHeight();
			double outOffsetLength = 10;
			if ((width != 0) || (height != 0)) {
				outOffsetLength = (width > height) ? width : height;
			}
			offsetX = offsetX * outOffsetLength / Math.sqrt(offsetLengthSquared);
			offsetY = offsetY * outOffsetLength / Math.sqrt(offsetLengthSquared);

			if (this.contains(geometry.getCenterX() + offsetX, geometry.getCenterY() + offsetY)) {
				return new Point2D.Double(geometry.getCenterX() + offsetX, geometry.getCenterY() + offsetY);
			} else {
				return new Point2D.Double(geometry.getCenterX() - offsetX, geometry.getCenterY() - offsetY);
			}
		}  // End public Point2D getPointInside(mxICell vertex)

		public Point2D getPointInside(mxICell inVertex1, mxICell inVertex2) {

			int index1 = this.indexOf(inVertex1);
			if (index1 < 0) {
				throw new IllegalArgumentException("inVertex1 is not part of this region.");
			}
			int index2 = this.indexOf(inVertex2);
			if (index2 < 0) {
				throw new IllegalArgumentException("inVertex2 is not part of this region.");
			}

			int nextIndex = (index1 + 1 >= this.size()) ? 0 : (index1 + 1);
			int previousIndex = (index1 - 1 < 0) ? (this.size() - 1) : (index1 - 1);
			mxGeometry geometry1;
			mxGeometry geometry2;
			if (index2 == nextIndex) {
				geometry1 = inVertex1.getGeometry();
				geometry2 = inVertex2.getGeometry();
			} else if (index2 == previousIndex) {
				geometry1 = inVertex2.getGeometry();
				geometry2 = inVertex1.getGeometry();
			} else {
				throw new IllegalArgumentException("inVertex1 and inVertex2 are not part of the same edge.");
			}
			if (geometry1 == null) {
				throw new IllegalStateException("inVertex1 has null geometry.");
			}
			if (geometry2 == null) {
				throw new IllegalStateException("inVertex2 has null geometry.");
			}
			double offsetX = geometry2.getCenterX() - geometry1.getCenterX();
			double offsetY = geometry2.getCenterY() - geometry1.getCenterY();
			double length = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
			//
			// Turn the incoming vector to the right (clockwise) 90 degrees.
			//
			double rightVectorX = -offsetY * 5 / length;
			double rightVectorY = offsetX * 5 / length;

			return new Point2D.Double(
					geometry1.getCenterX() + offsetX / 2 + rightVectorX,
					geometry1.getCenterY() + offsetY / 2 + rightVectorY);
		}  // End public Point2D getPointInside(mxICell inVertex1, mxICell inVertex2)

		public boolean intersectsLine(Point2D lineStart, Point2D lineEnd) {
			return this.intersectsLine(
					lineStart.getX(),
					lineStart.getY(),
					lineEnd.getX(),
					lineEnd.getY());
		}  // End public boolean intersectsLine(Point2D lineStart, Point2D lineEnd)

		public boolean intersectsLine(
				double lineStartX,
				double lineStartY,
				double lineEndX,
				double lineEndY) {

			int count = this.size();

			for (int index = 0; index < count; index++) {

				Object object = this.get(index);
				if (!(object instanceof mxICell)) {
					throw new ClassCastException("This Region object is instanciated with a type that the intersectsLine() method does not support.");
				}
				mxICell vertex = (mxICell) object;
				mxGeometry geometry1 = vertex.getGeometry();
				if (geometry1 == null) {
					throw new IllegalStateException("The vertex has null geometry.");
				}
				int nextIndex = (index + 1 >= count) ? 0 : (index + 1);
				object = this.get(nextIndex);
				if (!(object instanceof mxICell)) {
					throw new ClassCastException("This Region object is instanciated with a type that the intersectsLine() method does not support.");
				}
				vertex = (mxICell) object;
				mxGeometry geometry2 = vertex.getGeometry();
				if (geometry2 == null) {
					throw new IllegalStateException("The vertex has null geometry.");
				}

				if (Line2D.linesIntersect(
						lineStartX,
						lineStartY,
						lineEndX,
						lineEndY,
						geometry1.getCenterX(),
						geometry1.getCenterY(),
						geometry2.getCenterX(),
						geometry2.getCenterY())) {
					return true;
				}

			}  // End for (int index = 0; index < count; index++)

			return false;
		}  // End public boolean intersectsLine(...)

		@Override
		public String toString() {

			String string = "";
			Iterator<E> iterator = this.iterator();
			while (true) {
				Object object = iterator.next();
				if (object instanceof mxICell) {
					mxICell cell = (mxICell) object;
					object = cell.getValue();
					if (object == null) {
						string = string + cell.getId();
					} else {
						string = string + object.toString();
					}
				} else {
					string = string + object.toString();
				}
				if (iterator.hasNext()) {
					string = string + ", ";
				} else {
					break;
				}
			}  // End while (true)

			return string;
		}  // End public String toString()
	}  // End protected static class Region<E> extends ArrayList<E>

	public static mxGraph buildDualGraph(mxGraph inGraph) {

		if (inGraph == null) {
			throw new IllegalArgumentException("Input inGraph is null.");
		}
		mxGraph outGraph = new mxGraph();
		Object objects[] = inGraph.getChildEdges(inGraph.getDefaultParent());
		int edgeCount = objects.length;
		HashMap<mxICell, EdgeVisit> edgeVisitMap = new HashMap<mxICell, EdgeVisit>(edgeCount);
		List<mxICell> edgeList = new LinkedList<mxICell>();

		//
		// Initialize all data structures required.
		//
		for (int index = 0; index < edgeCount; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("An edge is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The edge variable is not actually an edge.");
			}
			edgeVisitMap.put(edge, new EdgeVisit());
			edgeList.add(edge);
		}  // End for (int index = 0; index < edgeCount; index++)

		//
		// Find all regions of the planar graph.
		//
		List<Region<mxICell>> regionList = new ArrayList<Region<mxICell>>();
		while (!(edgeVisitMap.isEmpty())) {

			mxICell edge = edgeVisitMap.keySet().iterator().next();
			if (edge == null) {
				throw new IllegalStateException("The edge variable is null.");
			}
			EdgeVisit edgeVisit = edgeVisitMap.get(edge);
			if (edgeVisit == null) {
				throw new IllegalStateException("The edgeVisit variable is null.");
			}
			EdgeDirection startEdgeDirection;
			if (!(edgeVisit.isTargetVisited)) {
				startEdgeDirection = new EdgeDirection(edge, true);
			} else if (!(edgeVisit.isSourceVisited)) {
				startEdgeDirection = new EdgeDirection(edge, false);
			} else {
				edgeVisitMap.remove(edge);
				continue;
			}

			//
			// Find one region by traversing edges.
			//
			Region<mxICell> regionVertices = new Region<mxICell>();
			EdgeDirection edgeDirection = startEdgeDirection;
			do {
				if (edgeDirection.isSourceToTarget) {
					edgeVisit.isTargetVisited = true;
				} else {
					edgeVisit.isSourceVisited = true;
				}
				regionVertices.add(edgeDirection.edge.getTerminal(!(edgeDirection.isSourceToTarget)));
				edgeDirection = findNextEdgeDirection(edgeDirection);
				if ((edgeDirection == null)
						|| (edgeDirection.edge == null)) {
					throw new IllegalStateException("findNextEdgeDirection() returned invalid output.");
				}
				edgeVisit = edgeVisitMap.get(edgeDirection.edge);
			} while ((edgeDirection.edge != startEdgeDirection.edge)
					|| (edgeDirection.isSourceToTarget != startEdgeDirection.isSourceToTarget));

			regionList.add(regionVertices);
			regionVertices = null;  // List<mxICell> regionVertices = new ArrayList<mxICell>();

		} // End while (!(edgeVisitMap.isEmpty()))

		//
		// For each region found, add a vertex in the output dual graph.
		//
		Iterator<Region<mxICell>> iterateRegions = regionList.iterator();
		while (iterateRegions.hasNext()) {
			Region<mxICell> regionVertices = iterateRegions.next();
			Iterator<mxICell> iterateVertices = regionVertices.iterator();
			double sumX = 0;
			double sumY = 0;

			while (iterateVertices.hasNext()) {
				mxICell currentVertex = iterateVertices.next();
				mxGeometry geometry = currentVertex.getGeometry();
				if (geometry == null) {
					throw new IllegalStateException("currentVertex has null geometry.");
				}
				sumX = sumX + geometry.getCenterX();
				sumY = sumY + geometry.getCenterY();
			}  // End while (iterateVertices.hasNext())

			outGraph.insertVertex(
					outGraph.getDefaultParent(),
					null,
					regionVertices,
					sumX / regionVertices.size(),
					sumY / regionVertices.size(),
					30,
					30);
		}  // End while (iterateRegions.hasNext())

		//
		// For every pair of neighbouring regions, add an edge in the output dual graph.
		//
		while (!(edgeList.isEmpty())) {

			mxICell edge = edgeList.remove(0);
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxICell firstRegion = null;
			mxICell secondRegion = null;
			objects = outGraph.getChildVertices(outGraph.getDefaultParent());
			int verticesCount = objects.length;

			//
			// Find (at most two) regions that share an edge (i.e. contain the
			// same two vertices in consecutive order).
			//
			for (int index = 0; index < verticesCount; index++) {
				if (!(objects[index] instanceof mxICell)) {
					throw new IllegalStateException("A vertex is not of the type mxICell.");
				}
				mxICell outVertex = (mxICell) objects[index];
				if (!(outVertex.isVertex())) {
					throw new IllegalStateException("The outVertex variable is not actually a vertex.");
				}
				Object object = outVertex.getValue();
				if (!(object instanceof Region)) {
					throw new IllegalStateException("The value of outVertex is not the type Region.");
				}
				Region regionVertices = (Region) object;
				int position = regionVertices.indexOf(source);
				if (position < 0) {
					continue;
				}
				int previousPosition = (position - 1 < 0) ? (regionVertices.size() - 1) : (position - 1);
				int nextPosition = (position + 1 >= regionVertices.size()) ? (0) : (position + 1);
				if ((regionVertices.get(previousPosition) == target)
						|| (regionVertices.get(nextPosition) == target)) {
					if (firstRegion == null) {
						firstRegion = outVertex;
					} else if (secondRegion == null) {
						secondRegion = outVertex;
						break;
					}
				}  // End if ((regionVertices.get(previousPosition) == target)
				//	|| (regionVertices.get(nextPosition) == target))
			}  // End for (int index = 0; index < verticesCount; index++)

			if ((firstRegion != null) && (secondRegion != null)) {
				boolean alreadyHasEdge = false;
				for (int index1 = 0; index1 < firstRegion.getEdgeCount(); index1++) {
					for (int index2 = 0; index2 < secondRegion.getEdgeCount(); index2++) {
						if (firstRegion.getEdgeAt(index1) == secondRegion.getEdgeAt(index2)) {
							alreadyHasEdge = true;
							break;
						}
					}  // End for (int index2 = 0; index2 < secondRegion.getEdgeCount(); index2++)
				}  // End for (int index1 = 0; index1 < firstRegion.getEdgeCount(); index1++)
				if (!alreadyHasEdge) {
					outGraph.insertEdge(
							outGraph.getDefaultParent(),
							null,
							null,
							firstRegion,
							secondRegion);
				}  // End if (!alreadyHasEdge)
			}  // End if ((firstRegion != null) && (secondRegion != null))

			//
			// Remove all edges on the boundary between the two known regions
			// from the edge list.
			//
			mxICell boundaryEdge = edge;
			mxICell boundaryVertex = source;
			while (boundaryVertex.getEdgeCount() == 2) {
				int index = boundaryVertex.getEdgeIndex(boundaryEdge);
				if (index == 0) {
					boundaryEdge = boundaryVertex.getEdgeAt(1);
				} else {
					boundaryEdge = boundaryVertex.getEdgeAt(0);
				}
				if ((boundaryEdge == edge)
						|| (!(edgeList.contains(boundaryEdge)))) {
					break;
				}
				if (boundaryEdge.getTerminal(true) == boundaryVertex) {
					boundaryVertex = boundaryEdge.getTerminal(false);
				} else {
					boundaryVertex = boundaryEdge.getTerminal(true);
				}
				edgeList.remove(boundaryEdge);
			}  // End while (boundaryVertex.getEdgeCount() == 2)

			boundaryEdge = edge;
			boundaryVertex = target;
			while (boundaryVertex.getEdgeCount() == 2) {
				int index = boundaryVertex.getEdgeIndex(boundaryEdge);
				if (index == 0) {
					boundaryEdge = boundaryVertex.getEdgeAt(1);
				} else {
					boundaryEdge = boundaryVertex.getEdgeAt(0);
				}
				if ((boundaryEdge == edge)
						|| (!(edgeList.contains(boundaryEdge)))) {
					break;
				}
				if (boundaryEdge.getTerminal(true) == boundaryVertex) {
					boundaryVertex = boundaryEdge.getTerminal(false);
				} else {
					boundaryVertex = boundaryEdge.getTerminal(true);
				}
				edgeList.remove(boundaryEdge);
			}  // End while (boundaryVertex.getEdgeCount() == 2)

		}  // End while (!(edgeList.isEmpty()))

		return outGraph;
	}  // End public static mxGraph buildDualGraph(mxGraph inGraph)

	protected static EdgeDirection findNextEdgeDirection(EdgeDirection inEdgeDirection) {

		if ((inEdgeDirection == null)
				|| (inEdgeDirection.edge == null)) {
			throw new IllegalArgumentException("Invalid inputs to findNextEdgeDirection().");
		}

		mxICell currentVertex;
		mxICell previousVertex;
		if (inEdgeDirection.isSourceToTarget) {
			currentVertex = inEdgeDirection.edge.getTerminal(false);
			previousVertex = inEdgeDirection.edge.getTerminal(true);
		} else {
			currentVertex = inEdgeDirection.edge.getTerminal(true);
			previousVertex = inEdgeDirection.edge.getTerminal(false);
		}
		if ((currentVertex == null)
				|| (previousVertex == null)) {
			throw new IllegalStateException("inEdgeDirection.edge has a null vertex.");
		}
		int edgeCount = currentVertex.getEdgeCount();
		if (edgeCount == 0) {
			throw new IllegalStateException("currentVertex has 0 edges.");
		}
		EdgeDirection outEdgeDirection = new EdgeDirection();
		if (edgeCount == 1) {
			outEdgeDirection.edge = inEdgeDirection.edge;
			outEdgeDirection.isSourceToTarget = !(inEdgeDirection.isSourceToTarget);
			return outEdgeDirection;
		}

		mxGeometry previousGeometry = previousVertex.getGeometry();
		if (previousGeometry == null) {
			throw new IllegalStateException("previousVertex has null geometry.");
		}
		mxGeometry currentGeometry = currentVertex.getGeometry();
		if (currentGeometry == null) {
			throw new IllegalStateException("currentVertex has null geometry.");
		}
		double inOffsetX = currentGeometry.getCenterX() - previousGeometry.getCenterX();
		double inOffsetY = currentGeometry.getCenterY() - previousGeometry.getCenterY();
		//
		// Turn the incoming vector to the right (clockwise) 90 degrees.
		//
		double rightVectorX = -inOffsetY;
		double rightVectorY = inOffsetX;
		double currentAngle = -Math.PI - 1;

		for (int index = 0; index < edgeCount; index++) {

			mxICell edge = currentVertex.getEdgeAt(index);
			if ((edge == null) || !(edge.isEdge())) {
				throw new IllegalStateException("edge is null or is not an edge");
			}
			if (edge == inEdgeDirection.edge) {
				continue;
			}
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxICell neighbour = (currentVertex == source) ? target : source;

			if (edgeCount == 2) {
				outEdgeDirection.edge = edge;
				outEdgeDirection.isSourceToTarget = (currentVertex == source);
				return outEdgeDirection;
			}

			mxGeometry neighbourGeometry = neighbour.getGeometry();
			if (neighbourGeometry == null) {
				throw new IllegalStateException("neighbour has null geometry.");
			}
			double offsetX = neighbourGeometry.getCenterX() - currentGeometry.getCenterX();
			double offsetY = neighbourGeometry.getCenterY() - currentGeometry.getCenterY();
			//
			// Calculate the angle between the edge from previousVertex to
			// currentVertex and the edge from currentVertex to neighbour by dot
			// product.
			//
			double angle = Math.acos(
					(offsetX * inOffsetX + offsetY * inOffsetY)
					/ Math.sqrt(offsetX * offsetX + offsetY * offsetY)
					/ Math.sqrt(inOffsetX * inOffsetX + inOffsetY * inOffsetY));
			//
			// Make the angle negative if the edge from currentVertex to
			// neighbour is to the left of the edge (vector) from previousVertex
			// to currentVertex.
			//
			if ((offsetX * rightVectorX + offsetY * rightVectorY) < 0) {
				angle = -angle;
			}
			if (angle > currentAngle) {
				currentAngle = angle;
				outEdgeDirection.edge = edge;
				outEdgeDirection.isSourceToTarget = (currentVertex == source);
			}

		}  // End for (int index = 0; index < edgeCount; index++)

		return outEdgeDirection;
	}  // End protected EdgeDirection findNextEdgeDirection(EdgeDirection inEdgeDirection)

	protected static List<List<mxICell>> findShortestPathsInDualGraph(
			mxGraph inGraph,
			mxICell inSourceElement,
			mxICell inTargetElement) {

		if ((inGraph == null)
				|| (inSourceElement == null)
				|| (inTargetElement == null)) {
			throw new IllegalArgumentException("Invalid input to findShortestPath().");
		}

		HashMap<mxICell, List<mxICell>> sourceToVisitedVerticesMap = new HashMap<mxICell, List<mxICell>>();
		List<List<mxICell>> paths = new LinkedList<List<mxICell>>();
		List<List<mxICell>> outPaths = new LinkedList<List<mxICell>>();
		Object objects[] = inGraph.getChildVertices(inGraph.getDefaultParent());
		int count = objects.length;

		for (int index = 0; index < count; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("vertex is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("vertex is not vertex.");
			}
			Object object = vertex.getValue();
			if (!(object instanceof Region)) {
				throw new IllegalStateException("The value of vertex is not of the type Region.");
			}
			Region region = (Region) object;
			if (region.contains(inSourceElement)) {
				List<mxICell> path = new ArrayList<mxICell>();
				path.add(vertex);
				if (region.contains(inTargetElement)) {
					outPaths.add(path);
					path = null;  // List<mxICell> path = new ArrayList<mxICell>();
					continue;
				}
				paths.add(path);
				path = null;  // List<mxICell> path = new ArrayList<mxICell>();
				List<mxICell> visitedVertices = new LinkedList<mxICell>();
				visitedVertices.add(vertex);
				sourceToVisitedVerticesMap.put(vertex, visitedVertices);
				visitedVertices = null;  // List<mxICell> visitedVertices = new LinkedList<mxICell>();
			}  // End if (region.contains(inSourceElement))
		}  // End for (int index = 0; index < count; index++)

		objects = null;  // Object objects[] = inGraph.getChildVertices(inGraph.getDefaultParent());

		if (!(outPaths.isEmpty())) {
			sourceToVisitedVerticesMap = null;  // HashMap<mxICell, List<mxICell>> sourceToVisitedVerticesMap = new HashMap<mxICell, List<mxICell>>();
			paths = null;  // List<List<mxICell>> paths = new LinkedList<List<mxICell>>();
			return outPaths;
		}

		while (!(paths.isEmpty())) {

			List<List<mxICell>> nextPathsToTry = new LinkedList<List<mxICell>>();

			while (!(paths.isEmpty())) {

				List<mxICell> path = paths.remove(0);
				mxICell vertex = path.get(path.size() - 1);
				count = vertex.getEdgeCount();

				for (int index = 0; index < count; index++) {

					mxICell edge = vertex.getEdgeAt(index);
					mxICell nextVertex = edge.getTerminal(true);
					if (nextVertex == vertex) {
						nextVertex = edge.getTerminal(false);
						if (nextVertex == vertex) {
							continue;
						}
					}  // End if (nextVertex == vertex)

					List<mxICell> visitedVertices = sourceToVisitedVerticesMap.get(path.get(0));
					if (visitedVertices.contains(nextVertex)) {
						continue;
					}

					visitedVertices.add(nextVertex);
					List<mxICell> newPath = new ArrayList<mxICell>(path);
					newPath.add(nextVertex);

					Object object = nextVertex.getValue();
					if (!(object instanceof Region)) {
						throw new IllegalStateException("The value of vertex is not of the type Region.");
					}
					Region region = (Region) object;
					if (region.contains(inTargetElement)) {
						outPaths.add(newPath);
					} else {
						nextPathsToTry.add(newPath);
					}
					newPath = null;  // List<mxICell> newPath = new ArrayList<mxICell>(path);

				}  // End for (int index = 0; index < count; index++)

			}  // End while (!(paths.isEmpty()))

			if (!(outPaths.isEmpty())) {
				break;
			}

			paths = nextPathsToTry;
			nextPathsToTry = null;  // List<List<mxICell>> nextPathsToTry = new LinkedList<List<mxICell>>();

		}  // End while (!(paths.isEmpty()))

		if (outPaths.isEmpty()) {
			outPaths = null;
		}

		sourceToVisitedVerticesMap = null;  // HashMap<mxICell, List<mxICell>> sourceToVisitedVerticesMap = new HashMap<mxICell, List<mxICell>>();
		paths = null;  // List<List<mxICell>> paths = new LinkedList<List<mxICell>>();

		return outPaths;
	}  // End protected static List<List<mxICell>> findShortestPathsInDualGraph(...)

	protected static List<List<mxPoint>> findPathsThroughRegions(
			mxICell inSourceElement,
			mxICell inTargetElement,
			List<mxICell> inVerticesHoldingRegions) {

		if ((inSourceElement == null) || (inTargetElement == null)) {
			throw new IllegalArgumentException("At least one of the required inputs inSourceElement and inTargetElement is null.");
		}

		List<List<mxPoint>> outPaths = new LinkedList<List<mxPoint>>();

		if ((inVerticesHoldingRegions == null) || (inVerticesHoldingRegions.isEmpty())) {
			return outPaths;
		}

		int regionsCount = inVerticesHoldingRegions.size();

		for (int index = 0; index < regionsCount; index++) {

			mxICell vertexHoldingRegions = inVerticesHoldingRegions.get(index);
			Object object = vertexHoldingRegions.getValue();
			if (!(object instanceof Region)) {
				throw new IllegalStateException("The value of vertexHoldingRegions is not the type Region.");
			}
			Region region = (Region) object;
			Point2D sourcePoint = null;
			Point2D targetPoint = null;

			if (region.contains(inSourceElement)) {
				sourcePoint = region.getPointInside(inSourceElement);
			} else {  // End if (region.contains(inSourceElement))

				int previousIndex = (index - 1 < 0) ? (regionsCount - 1) : (index - 1);
				vertexHoldingRegions = inVerticesHoldingRegions.get(previousIndex);
				object = vertexHoldingRegions.getValue();
				if (!(object instanceof Region)) {
					throw new IllegalStateException("The value of vertexHoldingRegions is not the type Region.");
				}
				Region previousRegion = (Region) object;
				List boundaryObjects = Region.findBoundaryVertices(region, previousRegion);
				int middleIndex = boundaryObjects.size() / 2;
				if (middleIndex < 1) {
					throw new IllegalStateException("The two regions cannot share fewer than 2 vertices.");
				}
				object = boundaryObjects.get(middleIndex);
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("The vertex of a region is not the type mxICell.");
				}
				mxICell vertex1 = (mxICell) object;
				if (!(vertex1.isVertex())) {
					throw new IllegalStateException("vertex1 is not a vertex.");
				}
				object = boundaryObjects.get(middleIndex - 1);
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("The vertex of a region is not the type mxICell.");
				}
				mxICell vertex2 = (mxICell) object;
				if (!(vertex2.isVertex())) {
					throw new IllegalStateException("vertex2 is not a vertex.");
				}
				sourcePoint = region.getPointInside(vertex1, vertex2);

			}  // End else part of if (region.contains(inSourceElement))

			if (region.contains(inTargetElement)) {
				targetPoint = region.getPointInside(inTargetElement);
			} else {  // End if (region.contains(inTargetElement))

				int nextIndex = (index + 1 >= regionsCount) ? 0 : (index + 1);
				vertexHoldingRegions = inVerticesHoldingRegions.get(nextIndex);
				object = vertexHoldingRegions.getValue();
				if (!(object instanceof Region)) {
					throw new IllegalStateException("The value of vertexHoldingRegions is not the type Region.");
				}
				Region nextRegion = (Region) object;
				List boundaryObjects = Region.findBoundaryVertices(region, nextRegion);
				int middleIndex = boundaryObjects.size() / 2;
				if (middleIndex < 1) {
					throw new IllegalStateException("The two regions cannot share fewer than 2 vertices.");
				}
				object = boundaryObjects.get(middleIndex);
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("The vertex of a region is not the type mxICell.");
				}
				mxICell vertex1 = (mxICell) object;
				if (!(vertex1.isVertex())) {
					throw new IllegalStateException("vertex1 is not a vertex.");
				}
				object = boundaryObjects.get(middleIndex - 1);
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("The vertex of a region is not the type mxICell.");
				}
				mxICell vertex2 = (mxICell) object;
				if (!(vertex2.isVertex())) {
					throw new IllegalStateException("vertex2 is not a vertex.");
				}
				targetPoint = region.getPointInside(vertex1, vertex2);

			}  // End else part of if (region.contains(inTargetElement))

			List<List<mxPoint>> paths = EdgeRoutingMinCross.findPathsThroughOneRegion(sourcePoint, targetPoint, region);
			if (outPaths.isEmpty()) {
				outPaths.addAll(paths);
			} else {  // End if (outPaths.isEmpty())

				List<List<mxPoint>> nextOutPaths = new LinkedList<List<mxPoint>>();
				while (!(outPaths.isEmpty())) {
					List<mxPoint> outPath = outPaths.remove(0);
					Iterator<List<mxPoint>> iteratePaths = paths.iterator();
					while (iteratePaths.hasNext()) {
						List<mxPoint> path = iteratePaths.next();
						List<mxPoint> nextOutPath = new LinkedList<mxPoint>(outPath);
						nextOutPath.addAll(path);
						nextOutPaths.add(nextOutPath);
						nextOutPath = null;  // List<mxPoint> nextOutPath = new LinkedList<mxPoint>(outPath);
					}  // End while (iteratePaths.hasNext())
				}  // End while (!(outPaths.isEmpty()))

				outPaths = nextOutPaths;
				nextOutPaths = null;  // List<List<mxPoint>> nextOutPaths = new LinkedList<List<mxPoint>>();

			}  // End else part of if (outPaths.isEmpty())

			paths = null;  // List<List<mxPoint>> paths = findPathsThroughOneRegion(sourcePoint, targetPoint, region);

		}  // End for (int index = 0; index < regionsCount; index++)

		return outPaths;
	}  // End protected static List<List<mxPoint>> findPathsThroughRegions (...)

	protected static List<List<mxPoint>> findPathsThroughOneRegion(
			Point2D inSourcePoint,
			Point2D inTargetPoint,
			Region inRegion) {

		if ((inSourcePoint == null)
				|| (inTargetPoint == null)
				|| (inRegion == null)) {
			throw new IllegalArgumentException("At least one of the input argument to findPathsThroughOneRegion() is null.");
		}

		List<List<mxPoint>> outPaths = new LinkedList<List<mxPoint>>();
		List<List<mxPoint>> pathsToTry = new LinkedList<List<mxPoint>>();
		List<mxPoint> path = new LinkedList<mxPoint>();
		path.add(new mxPoint(inSourcePoint));
		pathsToTry.add(path);
		path = null;  // List<mxPoint> path = new LinkedList<mxPoint>();

		while (!(pathsToTry.isEmpty())) {

			List<List<mxPoint>> nextPathsToTry = new LinkedList<List<mxPoint>>();

			while (!(pathsToTry.isEmpty())) {

				path = pathsToTry.remove(0);
				mxPoint point = path.get(path.size() - 1);
				if (!(inRegion.intersectsLine(
						point.getX(),
						point.getY(),
						inTargetPoint.getX(),
						inTargetPoint.getY()))) {
					path.add(new mxPoint(inTargetPoint));
					outPaths.add(path);
				}  // End if (!(inRegion.intersectsLine(...)))

				Iterator iterateVertices = inRegion.iterator();
				while (iterateVertices.hasNext()) {

					Object object = iterateVertices.next();
					if (!(object instanceof mxICell)) {
						throw new IllegalStateException("Region vertex is not of the type mxICell.");
					}
					Point2D point2d = inRegion.getPointInside((mxICell) object);
					if (path.contains(new mxPoint(point2d))) {
						continue;
					}
					if (!(inRegion.intersectsLine(point.getX(), point.getY(), point2d.getX(), point2d.getY()))) {
						List<mxPoint> nextPath = new LinkedList<mxPoint>(path);
						nextPath.add(new mxPoint(point2d));
						nextPathsToTry.add(nextPath);
						if (inTargetPoint.equals(point2d)) {
							outPaths.add(nextPath);
						}
						nextPath = null;  // List<mxPoint> nextPath = new LinkedList<mxPoint>(path);
					}  // End if (!(inRegion.intersectsLine(point.getX(), point.getY(), point2d.getX(), point2d.getY())))

				}  // End while (iterateVertices.hasNext())

			}  // End while (!(pathsToTry.isEmpty()))

			if (!(outPaths.isEmpty())) {
				break;
			}

			pathsToTry = nextPathsToTry;
			nextPathsToTry = null;  // List<List<mxPoint>> nextPathsToTry = new LinkedList<List<mxPoint>>();

		}  // End while (!(pathsToTry.isEmpty()))

		return outPaths;
	}  // End protected static List<List<mxPoint>> findPathsThroughOneRegion(...)
}  // End public class EdgeRoutingMinCross extends mxGraphLayout
