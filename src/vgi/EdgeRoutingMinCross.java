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

	public void routeByWeightedVisibilityGraph(mxCell edge) {

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
		Map<mxICell, List<mxICell>> oldToNewVerticesMap = new HashMap<mxICell, List<mxICell>>();
		mxGraph weightedVisibilityGraph = EdgeRoutingMinCross.buildWeightedVisibilityGraph(this.getGraph(), oldToNewVerticesMap);
		List<List<mxICell>> paths = EdgeRoutingMinCross.findShortestPaths(oldToNewVerticesMap.get(source), oldToNewVerticesMap.get(target));
		System.out.println(paths.size() + " path(s) is(are) found.");
		if ((paths == null) || (paths.isEmpty())) {
			return;
		}

		Iterator<List<mxICell>> iteratePaths = paths.iterator();
		while (iteratePaths.hasNext()) {

			mxGraph graph = this.getGraph();
			mxICell newEdge = (mxICell) graph.insertEdge(
					graph.getDefaultParent(),
					null,
					edge.getValue(),
					source,
					target,
					edge.getStyle());
			Object cells[] = {newEdge};
			graph.setCellStyles("strokeColor", mxUtils.hexString(Color.RED), cells);
			List<mxICell> path = iteratePaths.next();
			List<mxPoint> controlPoints = new LinkedList<mxPoint>();
			Iterator<mxICell> iterateVertices = path.iterator();
			while (iterateVertices.hasNext()) {
				mxICell vertex = iterateVertices.next();
				mxGeometry geometry = vertex.getGeometry();
				controlPoints.add(new mxPoint(geometry.getCenterX(), geometry.getCenterY()));
			}  // End while (iterateVertices.hasNext())
			super.setEdgePoints(newEdge, controlPoints);

		}  // End while (iteratePaths.hasNext())

//		Object cells[] = {edge};
//		this.getGraph().removeCells(cells);
	}  // End public void routeByWeightedVisibilityGraph(mxCell edge)
	protected static final double VISIBILITY_GRAPH_VERTEX_WDITH = 5;
	protected static final double VISIBILITY_GRAPH_VERTEX_HEIGHT = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double MINIMUM_SPACING = 9;

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

	protected static mxGraph buildWeightedVisibilityGraph(mxGraph inGraph) {
		return EdgeRoutingMinCross.buildWeightedVisibilityGraph(inGraph, null);
	}  // End protected static mxGraph buildWeightedVisibilityGraph(mxGraph inGraph)

	protected static mxGraph buildWeightedVisibilityGraph(
			mxGraph inGraph,
			Map<mxICell, List<mxICell>> outOptionalOldToNewVerticesMap) {

		mxGraph outGraph = new mxGraph();
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
			List<mxICell> newVertices = new LinkedList<mxICell>();

			Iterator<mxPoint> iteratePointsAroundRoadblock = pointsAroundRoadblock.iterator();
			while (iteratePointsAroundRoadblock.hasNext()) {
				mxPoint point = iteratePointsAroundRoadblock.next();
				Object object = outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX() - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
						point.getY() - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
						VISIBILITY_GRAPH_VERTEX_WDITH,
						VISIBILITY_GRAPH_VERTEX_HEIGHT);
				if ((outOptionalOldToNewVerticesMap != null)
						&& (object instanceof mxICell)) {
					newVertices.add((mxICell) object);
				}
			}  // End while (iteratePointsAroundRoadblock.hasNext())

			if (outOptionalOldToNewVerticesMap != null) {
				outOptionalOldToNewVerticesMap.put(vertex, newVertices);
			}
			newVertices = null;  // List<mxICell> newVertices = new LinkedList<mxICell>();

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
				mxGeometry sourceGeometry = source.getGeometry();
				mxGeometry targetGeometry = target.getGeometry();
				hindrancesList.add(new Hindrance(
						sourceGeometry.getCenterX(),
						sourceGeometry.getCenterY(),
						targetGeometry.getCenterX(),
						targetGeometry.getCenterY()));
				continue;
			}  // End if ((controlPoints == null) || (controlPoints.isEmpty()))
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
				if (Vector2D.isParallel(previousVectorX, previousVectorY, nextVectorX, nextVectorY)) {
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
				if (Vector2D.isAntiParallel(previousVectorX, previousVectorY, nextVectorX, nextVectorY)) {
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

		objects = outGraph.getChildVertices(parent);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell vertex1 = (mxICell) objects[index];
			if (!(vertex1.isVertex())) {
				throw new IllegalArgumentException("vertex1 is not a vertex.");
			}
			mxGeometry geometry1 = vertex1.getGeometry();
			if (geometry1 == null) {
				throw new IllegalStateException("vertex1 has null geometry.");
			}

			for (int index2 = index + 1; index2 < objects.length; index2++) {

				if (!(objects[index2] instanceof mxICell)) {
					throw new IllegalStateException("A vertex is not of the type mxICell.");
				}
				mxICell vertex2 = (mxICell) objects[index2];
				if (!(vertex2.isVertex())) {
					throw new IllegalArgumentException("vertex2 is not a vertex.");
				}
				mxGeometry geometry2 = vertex2.getGeometry();
				if (geometry2 == null) {
					throw new IllegalStateException("vertex2 has null geometry.");
				}

				boolean hitRoadblock = false;
				int roadblocksCount = roadblocksList.size();
				for (int index3 = 0; index3 < roadblocksCount; index3++) {
					mxRectangle rectangle = roadblocksList.get(index3);
					if (rectangle.intersectLine(
							geometry1.getCenterX(),
							geometry1.getCenterY(),
							geometry2.getCenterX(),
							geometry2.getCenterY()) != null) {
						hitRoadblock = true;
						break;
					}
				}  // End for (int index3 = 0; index3 < roadblocksCount; index3++)

				if (hitRoadblock) {
					continue;
				}

				List<mxPoint> endPointIntersectionsList = new ArrayList<mxPoint>();
				int cost = 0;
				int hindrancesCount = hindrancesList.size();

				for (int index3 = 0; index3 < hindrancesCount; index3++) {

					Hindrance hindrance = hindrancesList.get(index3);
					mxPoint intersection = mxUtils.intersection(
							geometry1.getCenterX(),
							geometry1.getCenterY(),
							geometry2.getCenterX(),
							geometry2.getCenterY(),
							hindrance.x1,
							hindrance.y1,
							hindrance.x2,
							hindrance.y2);
					if (intersection == null) {
						continue;
					}
					if (((intersection.getX() == hindrance.x1) && (intersection.getY() == hindrance.y1))
							|| ((intersection.getX() == hindrance.x2) && (intersection.getY() == hindrance.y2))) {
						if (!(endPointIntersectionsList.contains(intersection))) {
							endPointIntersectionsList.add(intersection);
							cost = cost + 1;
						}
					} else {
						cost = cost + 1;
					}

				}  // End for (int index3 = 0; index3 < hindrancesCount; index3++)

				outGraph.insertEdge(parent, null, cost, vertex1, vertex2);

			}  // End for (int index2 = index + 1; index2 < objects.length; index2++)

		}  // End for (int index = 0; index < objects.length; index++)

		hindrancesList = null;  // List<Hindrance> hindrancesList = new ArrayList<Hindrance>();
		roadblocksList = null;  // List<mxICell> roadblocksList = new LinkedList<mxICell>();

		HashMap<mxICell, mxICell> oldToNewVerticesMap = new HashMap<mxICell, mxICell>();
		objects = inGraph.getChildVertices(inGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			mxGeometry geometry = vertex.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("vertex has null geometry.");
			}
			mxICell newVertex = (mxICell) outGraph.insertVertex(
					parent,
					null,
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

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("An edge is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("edge is not an edge.");
			}
			mxGeometry geometry = edge.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("edge has null geometry.");
			}
			mxPoint terminalPoint = null;
			mxICell source = edge.getTerminal(true);
			if (source == null) {
				terminalPoint = geometry.getTerminalPoint(true);
			}
			mxICell target = edge.getTerminal(false);
			if (target == null) {
				terminalPoint = geometry.getTerminalPoint(false);
			}
			mxICell newEdge = (mxICell) outGraph.insertEdge(
					parent,
					null,
					edge.getValue(),
					oldToNewVerticesMap.get(source),
					oldToNewVerticesMap.get(target),
					edge.getStyle());
			mxGeometry newGeometry = newEdge.getGeometry();
			if (source == null) {
				newGeometry.setTerminalPoint(terminalPoint, true);
			} else if (target == null) {
				newGeometry.setTerminalPoint(terminalPoint, false);
			}
			newGeometry.setPoints(geometry.getPoints());

		}  // End for (int index = 0; index < objects.length; index++)

		oldToNewVerticesMap = null;  // HashMap<mxICell, mxICell> oldToNewVerticesMap = new HashMap<mxICell, mxICell>();

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

		List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);

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
			edgeUnitVectorsList.add(Vector2D.subtract(point.getX(), point.getY(),
					geometry.getCenterX(), geometry.getCenterY()).unitVector());
		}  // End for (int index = 0; index < edgeCount; index++)

		x = geometry.getWidth() / 2 + MINIMUM_SPACING;
		y = geometry.getHeight() / 2 + MINIMUM_SPACING;
		double radius = Math.sqrt(x * x + y * y);

		Collections.sort(edgeUnitVectorsList, new Vector2DComparator());

		for (int index = 0; index < edgeCount; index++) {

			int nextIndex = (index + 1 >= edgeCount) ? (0) : (index + 1);
			Vector2D currentVector = edgeUnitVectorsList.get(index);
			Vector2D nextVector = edgeUnitVectorsList.get(nextIndex);
			if (currentVector.isParallel(nextVector)) {
				continue;
			}
			Vector2D rightUnitVector = currentVector.rotate90DegreesPositively();
			if (currentVector.isAntiParallel(nextVector)) {
				outPoints.add(new mxPoint(
						geometry.getCenterX() + radius * rightUnitVector.getX(),
						geometry.getCenterY() + radius * rightUnitVector.getY()));
				continue;
			}
			Vector2D bisector = currentVector.add(nextVector);
			//
			// if the next vecotr points to the left of the current vector
			//
			if (nextVector.dotProduct(rightUnitVector) < 0) {
				bisector = bisector.reverse();
			}
			double bisectorLength = bisector.length();
			outPoints.add(new mxPoint(
					geometry.getCenterX() + radius * bisector.getX() / bisectorLength,
					geometry.getCenterY() + radius * bisector.getY() / bisectorLength));

		}  // End for (int index = 0; index < edgeCount; index++)

		edgeUnitVectorsList = null;  // List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);

		return outPoints;
	}  // End protected static List<mxPoint> findPointsAroundRoadblock(mxGraph inGraph, mxICell inVertex)

	protected static class Vector2DComparator implements Comparator<Vector2D> {

		@Override
		public int compare(Vector2D vector1, Vector2D vector2) {
			double angle1 = vector1.angle();
			if (angle1 < 0) {
				angle1 = 2 * Math.PI + angle1;
			}
			double angle2 = vector2.angle();
			if (angle2 < 0) {
				angle2 = 2 * Math.PI + angle2;
			}
			double difference = angle1 - angle2;
			if (difference < 0) {
				return -1;
			} else {
				return 1;
			}
		}  // End public int compare(Vector2D vector1, Vector2D vector2)
	}  // End protected static class Vector2DComparator implements Comparator<Vector2D>

	public static void testShortestPaths(mxGraph inGraph) {

		if (inGraph == null) {
			System.out.println("inGraph is null.");
			return;
		}

		mxGraph testGraph = new mxGraph();
		Object parent = testGraph.getDefaultParent();
		Object objects[] = inGraph.getChildVertices(inGraph.getDefaultParent());
		Map<mxICell, mxICell> oldToNewVertexMap = new HashMap<mxICell, mxICell>(objects.length);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException();
			}
			mxICell vertex = (mxICell) objects[index];
			mxGeometry geometry = vertex.getGeometry();
			mxICell testVertex = (mxICell) testGraph.insertVertex(
					parent,
					null,
					vertex.getValue(),
					geometry.getX(),
					geometry.getY(),
					geometry.getWidth(),
					geometry.getHeight(),
					vertex.getStyle(),
					geometry.isRelative());
			oldToNewVertexMap.put(vertex, testVertex);

		}  // End for (int index = 0; index < objects.length; index++)

		List<mxICell> sourceVertices = new LinkedList<mxICell>();
		List<mxICell> targetVertices = new LinkedList<mxICell>();
		objects = inGraph.getChildEdges(inGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException();
			}
			mxICell edge = (mxICell) objects[index];
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxGeometry geometry = edge.getGeometry();
			if (source == null) {
				if (target == null) {
					throw new IllegalStateException();
				}
				sourceVertices.add(oldToNewVertexMap.get(target));
				continue;
			} else if (target == null) {
				targetVertices.add(oldToNewVertexMap.get(source));
				continue;
			}
			Object object = edge.getValue();
			Object value = 0.0d;
			if (object instanceof WeightedRegularExpression.LeftMultiply) {
				value = ((WeightedRegularExpression.LeftMultiply) object).getWeightValue();
			} else if (object instanceof WeightedRegularExpression.RightMultiply) {
				value = ((WeightedRegularExpression.RightMultiply) object).getWeightValue();
			} else if (object instanceof Number) {
				value = object;
			}
			testGraph.insertEdge(
					parent,
					null,
					value,
					oldToNewVertexMap.get(source),
					oldToNewVertexMap.get(target));

		}  // End for (int index = 0; index < objects.length; index++)

		Cost cost = new Cost(Double.POSITIVE_INFINITY);
		List<List<mxICell>> paths = EdgeRoutingMinCross.findShortestPaths(
				sourceVertices,
				targetVertices,
				Double.POSITIVE_INFINITY,
				cost);
		System.out.println(paths.size() + " path(s) with cost " + cost + " is(are) found.");

		Iterator<List<mxICell>> iteratePaths = paths.iterator();
		while (iteratePaths.hasNext()) {

			List<mxICell> path = iteratePaths.next();
			System.out.print("This path goes through");

			Iterator<mxICell> iterateVertices = path.iterator();
			while (iterateVertices.hasNext()) {
				mxICell vertex = iterateVertices.next();
				System.out.print(" " + vertex.getValue().toString());
			}  // End while (iterateVertices.hasNext())
			System.out.println(".");

		}  // End while (iteratePaths.hasNext())

	}  // End public static void testShortestPaths(mxGraph inGraph)

	protected static class Cost {

		public double doubleValue;

		public Cost(double doubleValue) {
			this.doubleValue = doubleValue;
		}

		@Override
		public String toString() {
			return "" + doubleValue;
		}
	}  // End protected static class Cost

	public static List<List<mxICell>> findShortestPaths(
			List<mxICell> inSourceVertices,
			List<mxICell> inTargetVertices) {
		return EdgeRoutingMinCross.findShortestPaths(
				inSourceVertices,
				inTargetVertices,
				null,
				null);
	}  // End public static List<List<mxICell>> findShortestPaths(...)

	public static List<List<mxICell>> findShortestPaths(
			List<mxICell> inSourceVertices,
			List<mxICell> inTargetVertices,
			Double inOptionalMaxCostAllowed,
			Cost outOptionalCost) {

		if ((inSourceVertices == null)
				|| (inTargetVertices == null)) {
			throw new IllegalArgumentException("Invalid inputs to findShortestPath().");
		}

		if ((inSourceVertices.isEmpty()) || (inTargetVertices.isEmpty())) {
			return new LinkedList<List<mxICell>>();
		}

		double minCostToTarget;
		if ((inOptionalMaxCostAllowed != null)
				&& (inOptionalMaxCostAllowed.doubleValue() >= 0)) {
			minCostToTarget = inOptionalMaxCostAllowed.doubleValue();
		} else {
			minCostToTarget = Double.POSITIVE_INFINITY;
		}
		List<mxICell> verticesToBeProcessed = new LinkedList<mxICell>();
		Map<mxICell, Double> vertexToCostMap = new HashMap<mxICell, Double>();
		double minCostToBeProcessed = 0.0d;
		boolean isPathFound = false;

		Iterator<mxICell> iterateVertices = inSourceVertices.iterator();
		while (iterateVertices.hasNext()) {

			mxICell vertex = iterateVertices.next();
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The vertex variable is not a vertex.");
			}
			verticesToBeProcessed.add(vertex);
			vertexToCostMap.put(vertex, minCostToBeProcessed);

		}  // End while (iterateVertices.hasNext())

		while (!(verticesToBeProcessed.isEmpty())) {

			mxICell vertex = verticesToBeProcessed.remove(0);
			Double cost = vertexToCostMap.get(vertex);
			if (cost == null) {
				throw new IllegalStateException("The cost variable cannot be null.");
			}

			if (cost > minCostToTarget) {
				break;
			}

			if (cost > minCostToBeProcessed) {
				if (isPathFound) {
					break;
				}
				minCostToBeProcessed = cost;
			}  // End if (cost > minCostToBeProcessed)

			if (inTargetVertices.contains(vertex)) {
				isPathFound = true;
				if (cost < minCostToTarget) {
					minCostToTarget = cost;
				}
				continue;
			}  // End if (targetsToBeReached.contains(vertex))

			int edgeCount = vertex.getEdgeCount();
			for (int index = 0; index < edgeCount; index++) {

				mxICell edge = vertex.getEdgeAt(index);
				if (!(edge.isEdge())) {
					throw new IllegalStateException("The edge variable is not an edge.");
				}
				mxICell source = edge.getTerminal(true);
				mxICell target = edge.getTerminal(false);
				mxICell neighbour;
				if ((source == null) || (target == null)) {
					throw new IllegalStateException("An edge has null source or target vertex.");
				} else if (source == target) {
					continue;
				} else if (source == vertex) {
					neighbour = target;
				} else if (target == vertex) {
					neighbour = source;
				} else {
					throw new IllegalStateException("This edge is not connected to the vertex in question.");
				}

				double costToNeighbour;
				Object object = edge.getValue();
				if (object instanceof Number) {
					double weight = ((Number) object).doubleValue();
					if (weight < 0) {
						throw new IllegalStateException("The weight of an edge cannot be negative for this shortest path algorithm.");
					}
					costToNeighbour = cost + weight;
				} else {
					costToNeighbour = cost + 1;
				}

				Double neighbourCost = vertexToCostMap.get(neighbour);
				if (neighbourCost == null) {
					vertexToCostMap.put(neighbour, costToNeighbour);

					ListIterator<mxICell> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
					while (listIterator.hasPrevious()) {
						mxICell aVertex = listIterator.previous();
						if (vertexToCostMap.get(aVertex) <= costToNeighbour) {
							listIterator.next();
							break;
						}
					}  // End while (listIterator.hasPrevious())

					verticesToBeProcessed.add(listIterator.nextIndex(), neighbour);
					continue;
				}  // End if (neighbourCost == null)

				if (costToNeighbour >= neighbourCost) {
					continue;
				}

				vertexToCostMap.put(neighbour, costToNeighbour);
				Integer neighbourIndex = null;

				ListIterator<mxICell> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
				while (listIterator.hasPrevious()) {

					mxICell aVertex = listIterator.previous();
					if (neighbourIndex == null) {
						if (aVertex == neighbour) {
							neighbourIndex = listIterator.nextIndex();
						}
						continue;
					}  // End if (neighbourIndex == null)
					if (vertexToCostMap.get(aVertex) <= costToNeighbour) {
						listIterator.next();
						int newIndex = listIterator.nextIndex();
						if (neighbourIndex != newIndex) {
							verticesToBeProcessed.remove(neighbour);
							verticesToBeProcessed.add(newIndex, neighbour);
						}
						break;
					}  // End if (vertexToCostMap.get(aVertex) <= costToNeighbour)

				}  // End while (listIterator.hasPrevious())

			}  // End for (int index = 0; index < edgeCount; index++)

		}  // End while (!(verticesToBeProcessed.isEmpty()))

		verticesToBeProcessed = null;  // List<mxICell> verticesToBeProcessed = new LinkedList<mxICell>(inSourceVertices);

		if (!isPathFound) {
			vertexToCostMap = null;  // Map<mxICell, Double> vertexToCostMap = new HashMap<mxICell, Double>();
			return new LinkedList<List<mxICell>>();
		}

		if (outOptionalCost != null) {
			outOptionalCost.doubleValue = minCostToTarget;
		}
		List<List<mxICell>> outPaths = new LinkedList<List<mxICell>>();

		iterateVertices = inTargetVertices.iterator();
		while (iterateVertices.hasNext()) {

			mxICell vertex = iterateVertices.next();
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The vertex variable is not a vertex.");
			}
			Double cost = vertexToCostMap.get(vertex);
			if ((cost == null) || (cost != minCostToTarget)) {
				continue;
			}

			List<mxICell> path = new LinkedList<mxICell>();
			path.add(0, vertex);
			List<List<mxICell>> pathsToBeProcessed = new LinkedList<List<mxICell>>();
			pathsToBeProcessed.add(path);
			path = null;  // List<mxICell> path = new LinkedList<mxICell>();
			List<mxICell> visitedVertices = new LinkedList<mxICell>(inTargetVertices);

			while (!(pathsToBeProcessed.isEmpty())) {

				path = pathsToBeProcessed.remove(0);
				vertex = path.get(0);

				if (inSourceVertices.contains(vertex)) {
					outPaths.add(path);
					continue;
				}

				cost = vertexToCostMap.get(vertex);
				if (cost == null) {
					throw new IllegalStateException("A vertex in the path must have a minimum cost to reach.");
				}

				int edgeCount = vertex.getEdgeCount();
				for (int index = 0; index < edgeCount; index++) {

					mxICell edge = vertex.getEdgeAt(index);
					if (!(edge.isEdge())) {
						throw new IllegalStateException("The edge variable is not an edge.");
					}
					mxICell source = edge.getTerminal(true);
					mxICell target = edge.getTerminal(false);
					mxICell neighbour;
					if ((source == null) || (target == null)) {
						throw new IllegalStateException("An edge has null source or target vertex.");
					} else if (source == target) {
						continue;
					} else if (source == vertex) {
						neighbour = target;
					} else if (target == vertex) {
						neighbour = source;
					} else {
						throw new IllegalStateException("This edge is not connected to the vertex in question.");
					}

					if (visitedVertices.contains(neighbour)) {
						continue;
					}

					if (!(inSourceVertices.contains(neighbour))) {
						visitedVertices.add(neighbour);
					}
					double edgeCost;
					Object object = edge.getValue();
					if (object instanceof Number) {
						edgeCost = ((Number) object).doubleValue();
						if (edgeCost < 0) {
							throw new IllegalStateException("The cost of an edge cannot be negative for this shortest path algorithm.");
						}
					} else {
						edgeCost = 1;
					}

					Double neighbourCost = vertexToCostMap.get(neighbour);
					if ((neighbourCost == null)
							|| (neighbourCost != cost - edgeCost)) {
						continue;
					}

					List<mxICell> nextPath = new LinkedList<mxICell>(path);
					nextPath.add(0, neighbour);
					pathsToBeProcessed.add(nextPath);
					nextPath = null;  // List<mxICell> nextPath = new LinkedList<mxICell>(path);

				}  // End for (int index = 0; index < edgeCount; index++)

			}  // End while (!(pathsToBeProcessed.isEmpty()))

			visitedVertices = null;  // List<mxICell> visitedVertices = new LinkedList<mxICell>(inTargetVertices);
			pathsToBeProcessed = null;  // List<List<mxICell>> pathsToBeProcessed = new LinkedList<List<mxICell>>();

		}  // End while (iterateVertices.hasNext())

		vertexToCostMap = null;  // Map<mxICell, Double> vertexToCostMap = new HashMap<mxICell, Double>();

		return outPaths;
	}  // End public static List<List<mxICell>> findShortestPaths(...)

	public static List<List<mxICell>> findShortestPathsV1(
			List<mxICell> inSourceVertices,
			List<mxICell> inTargetVertices,
			Double inOptionalMaxCostAllowed,
			Cost outOptionalCost) {

		if ((inSourceVertices == null)
				|| (inTargetVertices == null)) {
			throw new IllegalArgumentException("Invalid inputs to findShortestPath().");
		}

		List<List<mxICell>> outPaths = new LinkedList<List<mxICell>>();
		List<mxICell> verticesToBeProcessed = new LinkedList<mxICell>(inSourceVertices);
		List<mxICell> targetsToBeReached = new LinkedList<mxICell>(inTargetVertices);
		Map<mxICell, Double> vertexToCostMap = new HashMap<mxICell, Double>();
		Map<mxICell, List<List<mxICell>>> vertexToPathsMap = new HashMap<mxICell, List<List<mxICell>>>();
		Map<mxICell, List<mxICell>> sourceToVisitedVerticesMap = new HashMap<mxICell, List<mxICell>>(inSourceVertices.size());
		double currentMinCost;
		if ((inOptionalMaxCostAllowed != null)
				&& (inOptionalMaxCostAllowed.doubleValue() >= 0)) {
			currentMinCost = inOptionalMaxCostAllowed.doubleValue();
		} else {
			currentMinCost = Double.POSITIVE_INFINITY;
		}

		Iterator<mxICell> iterateVertices = inSourceVertices.iterator();
		while (iterateVertices.hasNext()) {

			mxICell vertex = iterateVertices.next();
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The vertex variable is not a vertex.");
			}
			vertexToCostMap.put(vertex, 0.0d);
			List<mxICell> path = new LinkedList<mxICell>();
			path.add(vertex);
			List<List<mxICell>> paths = new LinkedList<List<mxICell>>();
			paths.add(path);
			path = null;  // List<mxICell> path = new LinkedList<mxICell>();
			vertexToPathsMap.put(vertex, paths);
			paths = null;  // List<List<mxICell>> paths = new LinkedList<List<mxICell>>();
			sourceToVisitedVerticesMap.put(vertex, new LinkedList<mxICell>(inSourceVertices));

		}  // End while (iterateVertices.hasNext())

		double currentCostLevel = vertexToCostMap.get(verticesToBeProcessed.get(0));

		while (!(verticesToBeProcessed.isEmpty())) {

			mxICell vertex = verticesToBeProcessed.remove(0);
			List<List<mxICell>> paths = vertexToPathsMap.get(vertex);
			if ((paths == null) || (paths.isEmpty())) {
				throw new IllegalStateException("A vertex to be processed cannot have no paths leading to it.");
			}
			double cost = vertexToCostMap.get(vertex);

			if (targetsToBeReached.contains(vertex)) {
				targetsToBeReached.remove(vertex);
				if (cost < currentMinCost) {
					currentMinCost = cost;
				}
				continue;
			}  // End if (targetsToBeReached.contains(vertex))
			if (cost > currentCostLevel) {
				if (targetsToBeReached.size() < inTargetVertices.size()) {
					break;
				}
				currentCostLevel = cost;
			}  // End if (cost > currentCostLevel)

			if (cost > currentMinCost) {
				continue;
			}

			int edgeCount = vertex.getEdgeCount();
			for (int index = 0; index < edgeCount; index++) {

				mxICell edge = vertex.getEdgeAt(index);
				if (!(edge.isEdge())) {
					throw new IllegalStateException("The edge variable is not an edge.");
				}
				mxICell source = edge.getTerminal(true);
				mxICell target = edge.getTerminal(false);
				if ((source == null) || (target == null)) {
					throw new IllegalStateException("An edge has null source or target vertex.");
				}
				if (source == target) {
					continue;
				}

				mxICell neighbour;
				if (source == vertex) {
					neighbour = target;
				} else if (target == vertex) {
					neighbour = source;
				} else {
					throw new IllegalStateException("This edge is not connected to the vertex in question.");
				}

				double costToNeighbour;
				Object object = edge.getValue();
				if (object instanceof Number) {
					double weight = ((Number) object).doubleValue();
					if (weight < 0) {
						throw new IllegalStateException("The weight of an edge cannot be negative for this shortest path algorithm.");
					}
					costToNeighbour = cost + weight;
				} else {
					costToNeighbour = cost + 1;
				}

				if (costToNeighbour > currentMinCost) {
					continue;
				}

				if (!(vertexToCostMap.containsKey(neighbour))) {
					ListIterator<mxICell> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
					while (listIterator.hasPrevious()) {
						mxICell aVertex = listIterator.previous();
						if (vertexToCostMap.get(aVertex) <= costToNeighbour) {
							listIterator.next();
							break;
						}
					}  // End while (listIterator.hasPrevious())

					verticesToBeProcessed.add(listIterator.nextIndex(), neighbour);
					vertexToCostMap.put(neighbour, costToNeighbour);

					List<List<mxICell>> neightbourPaths = new LinkedList<List<mxICell>>();
					Iterator<List<mxICell>> iteratePaths = paths.iterator();
					while (iteratePaths.hasNext()) {
						List<mxICell> path = iteratePaths.next();
						List<mxICell> visitedVertices = sourceToVisitedVerticesMap.get(path.get(0));
						if (visitedVertices.contains(neighbour)) {
							continue;
						}
						if (!(inTargetVertices.contains(neighbour))) {
							visitedVertices.add(neighbour);
						}
						List<mxICell> neighbourPath = new LinkedList<mxICell>(path);
						neighbourPath.add(neighbour);
						neightbourPaths.add(neighbourPath);
						neighbourPath = null;  // List<mxICell> neighbourPath = new LinkedList<mxICell>(path);
					}  // End while (iteratePaths.hasNext())
					vertexToPathsMap.put(neighbour, neightbourPaths);
					neightbourPaths = null;  // List<List<mxICell>> neightbourPaths = new LinkedList<List<mxICell>>();
					continue;
				}  // End if (!(vertexToCostMap.containsKey(neighbour)))

				double neighbourCost = vertexToCostMap.get(neighbour);
				if (neighbourCost < costToNeighbour) {
					continue;
				}

				List<List<mxICell>> neightbourPaths = vertexToPathsMap.get(neighbour);
				if (neighbourCost > costToNeighbour) {
					verticesToBeProcessed.remove(neighbour);

					ListIterator<mxICell> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
					while (listIterator.hasPrevious()) {
						mxICell aVertex = listIterator.previous();
						if (vertexToCostMap.get(aVertex) <= costToNeighbour) {
							listIterator.next();
							break;
						}
					}  // End while (listIterator.hasPrevious())

					verticesToBeProcessed.add(listIterator.nextIndex(), neighbour);
					vertexToCostMap.put(neighbour, costToNeighbour);
					neightbourPaths.clear();
				}  // End if (neighbourCost > costToNeighbour)

				Iterator<List<mxICell>> iteratePaths = paths.iterator();
				while (iteratePaths.hasNext()) {
					List<mxICell> path = iteratePaths.next();
					List<mxICell> visitedVertices = sourceToVisitedVerticesMap.get(path.get(0));
					if (visitedVertices.contains(neighbour)) {
						continue;
					}
					if (!(inTargetVertices.contains(neighbour))) {
						visitedVertices.add(neighbour);
					}
					List<mxICell> neighbourPath = new LinkedList<mxICell>(path);
					neighbourPath.add(neighbour);
					neightbourPaths.add(neighbourPath);
					neighbourPath = null;  // List<mxICell> neighbourPath = new LinkedList<mxICell>(path);
				}  // End while (iteratePaths.hasNext())

			}  // End for (int index = 0; index < edgeCount; index++)

		}  // End while (!(verticesToBeProcessed.isEmpty()))

		iterateVertices = inTargetVertices.iterator();
		while (iterateVertices.hasNext()) {
			mxICell target = iterateVertices.next();
			if ((!(vertexToCostMap.containsKey(target)))
					|| (verticesToBeProcessed.contains(target))) {
				continue;
			}
			double targetCost = vertexToCostMap.get(target);
			if (targetCost < currentMinCost) {
				currentMinCost = targetCost;
				outPaths.clear();
				outPaths.addAll(vertexToPathsMap.get(target));
			} else if (targetCost == currentMinCost) {
				outPaths.addAll(vertexToPathsMap.get(target));
			}
		}  // End while (iterateVertices.hasNext())

		vertexToPathsMap = null;  // Map<mxICell, List<List<mxICell>>> vertexToPathsMap = new HashMap<mxICell, List<List<mxICell>>>();
		vertexToCostMap = null;  // Map<mxICell, Double> vertexToCostMap = new HashMap<mxICell, Double>();
		targetsToBeReached = null;  // List<mxICell> targetsToBeReached = new LinkedList<mxICell>(inTargetVertices);
		verticesToBeProcessed = null;  // List<mxICell> verticesToBeProcessed = new LinkedList<mxICell>(inSourceVertices);

		if ((!(outPaths.isEmpty()))
				&& (outOptionalCost != null)) {
			outOptionalCost.doubleValue = currentMinCost;
		}
		return outPaths;
	}  // End public static List<List<mxICell>> findShortestPathsV1(...)

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

//		List<List<mxICell>> paths = findShortestPathsInDualGraph(dualGraph, planarSource, planarTarget);
		List<mxICell> sourceVertices = new LinkedList<mxICell>();
		List<mxICell> targetVertices = new LinkedList<mxICell>();
		objects = dualGraph.getChildVertices(dualGraph.getDefaultParent());
		count = objects.length;

		for (int index = 0; index < count; index++) {
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("The vertex variable is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The vertex variable is not a vertex.");
			}
			Object object = vertex.getValue();
			if (!(object instanceof Region)) {
				throw new IllegalStateException("The value of the vertex variable is not of the type Region.");
			}
			Region region = (Region) object;
			if (region.contains(planarSource)) {
				sourceVertices.add(vertex);
			}
			if (region.contains(planarTarget)) {
				targetVertices.add(vertex);
			}
		}  // End for (int index = 0; index < count; index++)
		List<List<mxICell>> paths = EdgeRoutingMinCross.findShortestPaths(sourceVertices, targetVertices);
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

	public static mxGraph planarize(mxGraph inGraph) {
		return EdgeRoutingMinCross.planarize(inGraph, null);
	}  // End public static mxGraph planarize(mxGraph inGraph)

	public static mxGraph planarize(mxGraph inGraph, mxICell inOptionalEdgeToIgnore) {

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
					null,
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
			if (edge == inOptionalEdgeToIgnore) {
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
		List<mxICell> edgesToBeProcessed = new LinkedList<mxICell>();
		objects = outGraph.getChildEdges(outGraph.getDefaultParent());

		for (int index = 0; index < objects.length; index++) {
			if (!(objects[index] instanceof mxICell)) {
				continue;
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				continue;
			}
			edgesToBeProcessed.add(edge);
		}  // End for (int index = 0; index < objects.length; index++)

		while (!(edgesToBeProcessed.isEmpty())) {

			mxICell edge = edgesToBeProcessed.remove(0);
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			if ((source == null)
					|| (target == null)
					|| (source == target)) {
				throw new IllegalStateException("An edge with null source, null target, or same source and target should not exist in our version of planar graphs.");
			}
			mxGeometry geometry = source.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("A vertex has null geometry.");
			}
			double x0 = geometry.getCenterX();
			double y0 = geometry.getCenterY();
			geometry = target.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("A vertex has null geometry.");
			}
			double x1 = geometry.getCenterX();
			double y1 = geometry.getCenterY();

			ListIterator<mxICell> listIterator = edgesToBeProcessed.listIterator();
			while (listIterator.hasNext()) {

				mxICell edge2 = listIterator.next();
				mxICell source2 = edge2.getTerminal(true);
				mxICell target2 = edge2.getTerminal(false);
				if ((source2 == null)
						|| (target2 == null)
						|| (source2 == target2)) {
					throw new IllegalStateException("An edge with null source, null target, or same source and target should not exist in our version of planar graphs.");
				}
				geometry = source2.getGeometry();
				if (geometry == null) {
					throw new IllegalStateException("A vertex has null geometry.");
				}
				double x2 = geometry.getCenterX();
				double y2 = geometry.getCenterY();
				geometry = target2.getGeometry();
				if (geometry == null) {
					throw new IllegalStateException("A vertex has null geometry.");
				}
				double x3 = geometry.getCenterX();
				double y3 = geometry.getCenterY();

				mxPoint point = mxUtils.intersection(x0, y0, x1, y1, x2, y2, x3, y3);
				if (point == null) {
					continue;
				}

				if (((point.getX() == x0) && (point.getY() == y0))
						|| ((point.getX() == x1) && (point.getY() == y1))
						|| ((point.getX() == x2) && (point.getY() == y2))
						|| ((point.getX() == x3) && (point.getY() == y3))) {
					continue;
				}

				Object[] edges = {edge, edge2};
				outGraph.removeCells(edges);
				Object parent = outGraph.getDefaultParent();
				Object dummyNode = outGraph.insertVertex(
						parent,
						null,
						null,
						point.getX(),
						point.getY(),
						0,
						0);
				mxICell newEdge = (mxICell) outGraph.insertEdge(parent, null, null, source, dummyNode);
				mxICell newEdge1 = (mxICell) outGraph.insertEdge(parent, null, null, dummyNode, target);
				mxICell newEdge2 = (mxICell) outGraph.insertEdge(parent, null, null, source2, dummyNode);
				mxICell newEdge3 = (mxICell) outGraph.insertEdge(parent, null, null, dummyNode, target2);
				edgesToBeProcessed.remove(listIterator.previousIndex());
				edgesToBeProcessed.add(newEdge);
				edgesToBeProcessed.add(newEdge1);
				edgesToBeProcessed.add(newEdge2);
				edgesToBeProcessed.add(newEdge3);
				break;
			}  // End while (listIterator.hasNext())

		}  // End while (!(edgesToBeProcessed.isEmpty()))

		edgesToBeProcessed = null;  // List<mxICell> edgesToBeProcessed = new LinkedList<mxICell>();

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
			List<Object> outList = new ArrayList<Object>();

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
				mxICell source = edgeDirection.edge.getTerminal(true);
				mxICell target = edgeDirection.edge.getTerminal(false);
				String sourceName = (source.getValue() != null) ? (source.getValue().toString()) : (source.getId());
				String targetName = (target.getValue() != null) ? (target.getValue().toString()) : (target.getId());
				if (edgeDirection.isSourceToTarget) {
					System.out.println("Going from " + sourceName + " to " + targetName + ".");
					edgeVisit.isTargetVisited = true;
				} else {
					System.out.println("Going from " + targetName + " to " + sourceName + ".");
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
