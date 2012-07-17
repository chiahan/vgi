/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.prefs.Preferences;
import vgi.SingleVertexEdgesLayout.Vector2DComparator;

/**
 *
 * @author JLiu
 */
public class WeightedVisibilityGraph {

	public static class Vertex {

		public Point2D location;
		public Set<Edge> edges;

		public Vertex() {
			this.location = null;
			this.edges = new LinkedHashSet<Edge>();
		}
	}  // End public static class Vertex

	public static class Edge {

		public Vertex source;
		public Vertex target;
		public double weight;

		public Edge(Vertex source, Vertex target, double weight) {
			this.source = source;
			this.target = target;
			this.weight = weight;
		}

		public Edge() {
			this(null, null, 0d);
		}
	}  // End public static class Edge
	protected Set<Vertex> vertices;
	protected Set<Edge> edges;

	public boolean contains(Vertex inVertex) {

		if ((inVertex == null)
				|| (inVertex.location == null)
				|| (inVertex.edges == null)) {
			return false;
		}

		return ((this.vertices.contains(inVertex))
				&& (this.edges.containsAll(inVertex.edges)));
	}  // End public boolean contains(Vertex inVertex)

	public boolean contains(Edge inEdge) {

		if ((inEdge == null)
				|| (inEdge.source == null)
				|| (inEdge.target == null)) {
			return false;
		}

		return ((this.edges.contains(inEdge))
				&& (this.vertices.contains(inEdge.source))
				&& (this.vertices.contains(inEdge.target)));
	}  // End public boolean contains(Edge ineEdge)

	public Set<Vertex> getVertices() {
		return this.vertices;
	}

	public Set<Edge> getEdges() {
		return this.edges;
	}

	public Vertex addVertex(double inX, double inY) {
		Vertex vertex = new Vertex();
		vertex.location = new Point2D.Double(inX, inY);
		this.vertices.add(vertex);
		return vertex;
	}  // End public Vertex addVertex(double inX, double inY)

	public Edge addEdge(
			Vertex inSource,
			Vertex inTarget,
			double inWeight) {

		if ((!(this.contains(inSource)))
				|| (!(this.contains(inTarget)))) {
			throw new IllegalArgumentException("The input argument 'inSource' or 'inTarget' is not a vertex of this graph.");
		}

		Edge edge = new Edge(inSource, inTarget, inWeight);
		this.edges.add(edge);
		inSource.edges.add(edge);
		if (inTarget != inSource) {
			inTarget.edges.add(edge);
		}

		return edge;
	}  // End public Edge addEdge(...)

	public Vertex removeVertex(Vertex inVertex) {

		if (!(this.contains(inVertex))) {
			throw new IllegalArgumentException("The input argument 'inVertex' is not a vertex of this graph.");
		}

		Iterator<Edge> iterateEdges = inVertex.edges.iterator();
		while (iterateEdges.hasNext()) {
			Edge edge = iterateEdges.next();
			this.removeEdge(edge);
			iterateEdges = inVertex.edges.iterator();
		}
		this.vertices.remove(inVertex);

		return inVertex;
	}  // End public Vertex removeVertex(Vertex inVertex)

	public Edge removeEdge(Edge inEdge) {

		if (!(this.contains(inEdge))) {
			throw new IllegalArgumentException("The input argument 'inEdge' is not an edge of this graph.");
		}

		this.edges.remove(inEdge);
		inEdge.source.edges.remove(inEdge);
		if (inEdge.target != inEdge.source) {
			inEdge.target.edges.remove(inEdge);
		}

		return inEdge;
	}  // End public Edge removeEdge(Edge inEdge)

	protected static class LineSegment {

		protected static double ERROR_MARGIN = 0.005d;
		double x1;
		double y1;
		double x2;
		double y2;

		public LineSegment(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		//
		// Based on information from http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
		//
		public static mxPoint intersection(
				double x1,
				double y1,
				double x2,
				double y2,
				double x3,
				double y3,
				double x4,
				double y4) {

			double denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

			//
			// If the line segments are not parallel
			//
			if (Math.abs(denominator) >= ERROR_MARGIN) {
				double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
				double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

				if ((ua >= (0.0d - ERROR_MARGIN))
						&& (ua <= (1.0d + ERROR_MARGIN))
						&& (ub >= (0.0d - ERROR_MARGIN))
						&& (ub <= (1.0d + ERROR_MARGIN))) {
					double x = x1 + ua * (x2 - x1);
					double y = y1 + ua * (y2 - y1);
					return new mxPoint(x, y);
				}

				return null;
			}  // End if (Math.abs(denominator) >= ERROR_MARGIN)

			//
			// If the line segments are parallel and not vertical.
			//
			if (Math.abs(x1 - x2) >= ERROR_MARGIN) {
				double yIntersect1 = y1 - (y2 - y1) / (x2 - x1) * x1;
				double yIntersect2 = y3 - (y4 - y3) / (x4 - x3) * x3;

				if (Math.abs(yIntersect1 - yIntersect2) >= ERROR_MARGIN) {
					return null;
				}

				double min1 = (x1 < x2) ? x1 : x2;
				double max1 = (x1 < x2) ? x2 : x1;
				double min2 = (x3 < x4) ? x3 : x4;
				double max2 = (x3 < x4) ? x4 : x3;
				double leftMin = (min1 < min2) ? min1 : min2;
				double leftMax = (min1 < min2) ? max1 : max2;
				double rightMin = (min1 < min2) ? min2 : min1;
				double rightMax = (min1 < min2) ? max2 : max1;
				if (leftMax < rightMin) {
					return null;
				}

				double x;
				if (leftMax < rightMax) {
					x = (leftMax + rightMin) / 2;
				} else {
					x = (rightMin + rightMax) / 2;
				}
				double y = (y2 - y1) / (x2 - x1) * x + yIntersect1;

				return new mxPoint(x, y);
			}  // End if (Math.abs(x1 - x2) >= ERROR_MARGIN)

			//
			// If the line segments are parallel and vertical.
			//
			if (Math.abs(x1 - x3) >= ERROR_MARGIN) {
				return null;
			}

			double min1 = (y1 < y2) ? y1 : y2;
			double max1 = (y1 < y2) ? y2 : y1;
			double min2 = (y3 < y4) ? y3 : y4;
			double max2 = (y3 < y4) ? y4 : y3;
			double leftMin = (min1 < min2) ? min1 : min2;
			double leftMax = (min1 < min2) ? max1 : max2;
			double rightMin = (min1 < min2) ? min2 : min1;
			double rightMax = (min1 < min2) ? max2 : max1;
			if (leftMax < rightMin) {
				return null;
			}

			double y;
			if (leftMax < rightMax) {
				y = (leftMax + rightMin) / 2;
			} else {
				y = (rightMin + rightMax) / 2;
			}

			return new mxPoint(x1, y);
		}  // End public static mxPoint intersection(...)
	}  // End protected static class LineSegment
	protected static final double VISIBILITY_GRAPH_VERTEX_WDITH = 5;
	protected static final double VISIBILITY_GRAPH_VERTEX_HEIGHT = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double MINIMUM_SPACING = 25;
	protected List<mxICell> roadblocks;
	protected Map<mxICell, Set<Vertex>> obstacleToVerticesMap;
	protected Map<mxICell, List<LineSegment>> hindranceToLineSegmentsMap;
	protected double costPerUnitLength = 1;
	protected double costPerEdgeCrossing = 500;
	protected double costPerSegment = 1;
	public Stopwatch rOther = new Stopwatch();
	public Stopwatch rIntersectEdges = new Stopwatch();
	public Stopwatch rAddVertices = new Stopwatch();
	public Stopwatch rAddEdges = new Stopwatch();
	public Stopwatch stOther = new Stopwatch();
	public Stopwatch stEdgeVectors = new Stopwatch();
	public Stopwatch stSortEdgeVectors = new Stopwatch();
	public Stopwatch stNewPositions = new Stopwatch();
	public Stopwatch stAddVertices = new Stopwatch();
	public Stopwatch stAddEdges = new Stopwatch();
	public Stopwatch eOther = new Stopwatch();
	public Stopwatch eIntersectRoadblocks = new Stopwatch();
	public Stopwatch eIntersectHindrances = new Stopwatch();
	public Stopwatch eAddEdges = new Stopwatch();

	public WeightedVisibilityGraph() {

		this.vertices = new LinkedHashSet<Vertex>();
		this.edges = new LinkedHashSet<Edge>();

		this.roadblocks = new LinkedList<mxICell>();
		this.obstacleToVerticesMap = new HashMap<mxICell, Set<Vertex>>();
		this.hindranceToLineSegmentsMap = new HashMap<mxICell, List<LineSegment>>();

		Preferences preferences = Preferences.userRoot().node(VGI.class.getName());
		String string = preferences.get(
				EdgeCostSettingsDialog.KEY_COST_PER_UNIT_LENGTH,
				EdgeCostSettingsDialog.VAL_DEFAULT_COST_PER_UNIT_LENGTH);
		this.costPerUnitLength = Double.valueOf(string);
		string = preferences.get(
				EdgeCostSettingsDialog.KEY_COST_PER_EDGE_CROSSING,
				EdgeCostSettingsDialog.VAL_DEFAULT_COST_PER_EDGE_CROSSING);
		this.costPerEdgeCrossing = Double.valueOf(string);
		string = preferences.get(
				EdgeCostSettingsDialog.KEY_COST_PER_SEGMENT,
				EdgeCostSettingsDialog.VAL_DEFAULT_COST_PER_SEGMENT);
		this.costPerSegment = Double.valueOf(string);
	}  // End public WeightedVisibilityGraph()

	public Map<mxICell, Set<Vertex>> getObstacleToVerticesMap() {
		return this.obstacleToVerticesMap;
	}

	protected void addEdgesForVertex(Vertex inVertex) {

		this.eOther.start();
		if (inVertex == null) {
			throw new IllegalArgumentException("Input 'vertex' is null.");
		}
		if (inVertex.location == null) {
			throw new IllegalArgumentException("Input 'vertex' has null location.");
		}

		Set<Vertex> vertices = this.getVertices();

		for (Vertex vertex : vertices) {

			if (vertex == inVertex) {
				continue;
			}
			if (vertex.location == null) {
				throw new IllegalStateException("The 'vertex' variable has null location.");
			}

			boolean isVisible = true;
			for (mxICell roadblock : this.roadblocks) {

				mxGeometry roadblockGeometry = roadblock.getGeometry();
				if (roadblockGeometry == null) {
					throw new IllegalStateException("The 'roadblock' variable has null geometry.");
				}
				this.eOther.stop();
				this.eIntersectRoadblocks.start();
				mxPoint point = roadblockGeometry.intersectLine(
						inVertex.location.getX(),
						inVertex.location.getY(),
						vertex.location.getX(),
						vertex.location.getY());
				this.eIntersectRoadblocks.stop();
				this.eOther.start();
				if (point != null) {
					isVisible = false;
					break;
				}

			}  // End for (mxICell roadblock : this.roadblocks)

			if (!isVisible) {
				continue;
			}

			double crossingNumber = 0.0d;

			for (mxICell cell : this.hindranceToLineSegmentsMap.keySet()) {

				List<LineSegment> lineSegments = this.hindranceToLineSegmentsMap.get(cell);
				for (LineSegment lineSegment : lineSegments) {

					this.eOther.stop();
					this.eIntersectHindrances.start();
					mxPoint intersection = LineSegment.intersection(
							inVertex.location.getX(),
							inVertex.location.getY(),
							vertex.location.getX(),
							vertex.location.getY(),
							lineSegment.x1,
							lineSegment.y1,
							lineSegment.x2,
							lineSegment.y2);
					this.eIntersectHindrances.stop();
					this.eOther.start();
					if (intersection == null) {
						continue;
					}
					if (((intersection.getX() == inVertex.location.getX()) && (intersection.getY() == inVertex.location.getY()))
							|| ((intersection.getX() == vertex.location.getX()) && (intersection.getY() == vertex.location.getY()))) {
						if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
								|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
							crossingNumber = crossingNumber + 0.25;
						} else {
							crossingNumber = crossingNumber + 0.5;
						}
					} else {
						if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
								|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
							crossingNumber = crossingNumber + 0.5;
						} else {
							crossingNumber = crossingNumber + 1;
						}
					}

				}  // End for (LineSegment lineSegment : lineSegments)

			}  // End for (mxICell cell : this.hindranceToLineSegmentsMap.keySet())

			double cost = Vector2D.length(
					inVertex.location.getX() - vertex.location.getX(),
					inVertex.location.getY() - vertex.location.getY())
					* this.costPerUnitLength
					+ crossingNumber * this.costPerEdgeCrossing
					+ this.costPerSegment;
			this.eOther.stop();
			this.eAddEdges.start();
			this.addEdge(inVertex, vertex, cost);
			this.eAddEdges.stop();
			this.eOther.start();

		}  // End for (Vertex vertex : vertices)

		this.eOther.stop();
	}  // End public void addEdgesForVertex(Vertex inVertex)

	public void addRoadblock(mxICell roadblock) {

		this.rOther.start();
		if ((roadblock == null) || (!(roadblock.isVertex()))) {
			throw new IllegalArgumentException("Input 'roadblock' is null or not a vertex.");
		}
		mxGeometry geometry = roadblock.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'roadblock' has null geometry.");
		}

		Set<Edge> edges = this.getEdges();
		Collection<Edge> edgesToRemove = new LinkedHashSet<Edge>();

		for (Edge edge : edges) {

			Vertex source = edge.source;
			if (source == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			if (source.location == null) {
				throw new IllegalStateException("The 'source' variable has null location.");
			}
			Vertex target = edge.target;
			if (target == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			if (target.location == null) {
				throw new IllegalStateException("The 'target' variable has null location.");
			}

			this.rOther.stop();
			this.rIntersectEdges.start();
			mxPoint point = geometry.intersectLine(
					source.location.getX(),
					source.location.getY(),
					target.location.getX(),
					target.location.getY());
			this.rIntersectEdges.stop();
			this.rOther.start();
			if (point != null) {
				edgesToRemove.add(edge);
			}

		}  // End for (Edge edge : edges)

		if (!(edgesToRemove.isEmpty())) {
			for (Edge edge : edgesToRemove) {
				this.removeEdge(edge);
			}
		}  // End if (!(edgesToRemove.isEmpty()))
		edgesToRemove = null;  // Collection<Edge> edgesToRemove = new LinkedHashSet<Edge>();

		this.roadblocks.add(roadblock);
		Set<Vertex> vertices = new LinkedHashSet<Vertex>();
		this.rOther.stop();
		this.rAddVertices.start();
		Vertex vertex = this.addVertex(
				geometry.getX() - MINIMUM_SPACING,
				geometry.getY() - MINIMUM_SPACING);
		this.rAddVertices.stop();
		this.rAddEdges.start();
		this.addEdgesForVertex(vertex);
		this.rAddEdges.stop();
		vertices.add(vertex);
		this.rAddVertices.start();
		vertex = this.addVertex(
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING,
				geometry.getY() - MINIMUM_SPACING);
		this.rAddVertices.stop();
		this.rAddEdges.start();
		this.addEdgesForVertex(vertex);
		this.rAddEdges.stop();
		vertices.add(vertex);
		this.rAddVertices.start();
		vertex = this.addVertex(
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING);
		this.rAddVertices.stop();
		this.rAddEdges.start();
		this.addEdgesForVertex(vertex);
		this.rAddEdges.stop();
		vertices.add(vertex);
		this.rAddVertices.start();
		vertex = this.addVertex(
				geometry.getX() - MINIMUM_SPACING,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING);
		this.rAddVertices.stop();
		this.rAddEdges.start();
		this.addEdgesForVertex(vertex);
		this.rAddEdges.stop();
		this.rOther.start();
		vertices.add(vertex);
		this.obstacleToVerticesMap.put(roadblock, vertices);
		vertices = null;  // Set<Vertex> vertices = new LinkedHashSet<Vertex>();
		this.rOther.stop();

	}  // End public void addRoadblock(mxICell roadblock)

	public void addHindrance(mxICell hindrance) {

		if ((hindrance == null) || (!(hindrance.isEdge()))) {
			throw new IllegalArgumentException("Input 'hindrance' is null or not an edge.");
		}
		mxGeometry geometry = hindrance.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'hindrance' has null geometry.");
		}
		mxICell source = hindrance.getTerminal(true);
		mxPoint sourcePoint = null;
		mxGeometry sourceGeometry = null;
		if (source == null) {
			sourcePoint = geometry.getSourcePoint();
		} else {
			sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			sourcePoint = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
		}
		mxICell target = hindrance.getTerminal(false);
		mxPoint targetPoint = null;
		mxGeometry targetGeometry = null;
		if (target == null) {
			targetPoint = geometry.getTargetPoint();
		} else {
			targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}
			targetPoint = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
		}
		if ((sourcePoint == null) || (targetPoint == null)) {
			throw new IllegalStateException("The 'sourcePoint' or 'targetPoint' variable is null.");
		}

		List<LineSegment> lineSegments = new LinkedList<LineSegment>();
		List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();
		mxPoint previousPoint = sourcePoint;
		Vector2D previousVector = null;
		List<mxPoint> allPoints = new LinkedList<mxPoint>();
		List<mxPoint> points = geometry.getPoints();
		if (points != null) {
			allPoints.addAll(points);
		}
		points = null;  // List<mxPoint> points = geometry.getPoints();
		allPoints.add(targetPoint);

		if (sourceGeometry != null) {
			double offsetLength = (sourceGeometry.getWidth() < sourceGeometry.getHeight())
					? sourceGeometry.getWidth() / 2
					: sourceGeometry.getHeight() / 2;
			mxPoint neighbourPoint = allPoints.get(0);
			Vector2D offsetVector = Vector2D.subtract(
					neighbourPoint.getX(),
					neighbourPoint.getY(),
					sourcePoint.getX(),
					sourcePoint.getY()).
					unitVector().
					scalarProduct(offsetLength);
			sourcePoint.setX(sourcePoint.getX() + offsetVector.getX());
			sourcePoint.setY(sourcePoint.getY() + offsetVector.getY());
		}  // End if (sourceGeometry != null)
		if (targetGeometry != null) {
			double offsetLength = (targetGeometry.getWidth() < targetGeometry.getHeight())
					? targetGeometry.getWidth() / 2
					: targetGeometry.getHeight() / 2;
			mxPoint neighbourPoint;
			if (allPoints.size() >= 2) {
				neighbourPoint = allPoints.get(allPoints.size() - 2);
			} else {
				neighbourPoint = sourcePoint;
			}
			Vector2D offsetVector = Vector2D.subtract(
					neighbourPoint.getX(),
					neighbourPoint.getY(),
					targetPoint.getX(),
					targetPoint.getY()).
					unitVector().
					scalarProduct(offsetLength);
			targetPoint.setX(targetPoint.getX() + offsetVector.getX());
			targetPoint.setY(targetPoint.getY() + offsetVector.getY());
		}  // End if (targetGeometry != null)

		for (mxPoint point : allPoints) {

			LineSegment lineSegment = new LineSegment(
					previousPoint.getX(),
					previousPoint.getY(),
					point.getX(),
					point.getY());
			lineSegments.add(lineSegment);
			lineSegment = null; // LineSegment lineSegment = new LineSegment(...)
			Vector2D vector = Vector2D.subtract(
					point.getX(),
					point.getY(),
					previousPoint.getX(),
					previousPoint.getY());
			if (previousVector != null) {
				Vector2D externalBisector = previousVector.unitVector().
						subtract(vector.unitVector()).
						unitVector().scalarProduct(MINIMUM_SPACING);
				newVerticesPositions.add(new mxPoint(
						previousPoint.getX() + externalBisector.getX(),
						previousPoint.getY() + externalBisector.getY()));
			}  // End if (previousVector != null)

			previousPoint = point;
			previousVector = vector;

		}  // End for (mxPoint point : allPoints)

		this.hindranceToLineSegmentsMap.put(hindrance, lineSegments);
		Set<Edge> edges = this.getEdges();

		for (Edge edge : edges) {

			Vertex sourceV = edge.source;
			if (sourceV == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			if (sourceV.location == null) {
				throw new IllegalStateException("The 'sourceV' variable has null location.");
			}
			Vertex targetV = edge.target;
			if (targetV == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			if (targetV.location == null) {
				throw new IllegalStateException("The 'targetV' variable has null location.");
			}
			double crossingNumber = 0.0d;

			for (LineSegment lineSegment : lineSegments) {

				mxPoint intersection = LineSegment.intersection(
						sourceV.location.getX(),
						sourceV.location.getY(),
						targetV.location.getX(),
						targetV.location.getY(),
						lineSegment.x1,
						lineSegment.y1,
						lineSegment.x2,
						lineSegment.y2);
				if (intersection == null) {
					continue;
				}
				if (((intersection.getX() == sourceV.location.getX()) && (intersection.getY() == sourceV.location.getY()))
						|| ((intersection.getX() == targetV.location.getX()) && (intersection.getY() == targetV.location.getY()))) {
					if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
							|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
						crossingNumber = crossingNumber + 0.25;
					} else {
						crossingNumber = crossingNumber + 0.5;
					}
				} else {
					if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
							|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
						crossingNumber = crossingNumber + 0.5;
					} else {
						crossingNumber = crossingNumber + 1;
					}
				}

			}  // End for (LineSegment lineSegment : lineSegments)

			if (crossingNumber > 0.0d) {
				edge.weight = edge.weight + this.costPerEdgeCrossing * crossingNumber;
			}

		}  // End for (Edge edge : edges)

		lineSegments = null;  // List<LineSegment> lineSegments = new LinkedList<LineSegment>();
		Set<Vertex> vertices = new LinkedHashSet<Vertex>();
		for (mxPoint position : newVerticesPositions) {

			Vertex vertex = this.addVertex(
					position.getX(),
					position.getY());
			this.addEdgesForVertex(vertex);
			vertices.add(vertex);

		}  // End for (mxPoint position : newVerticesPositions)

		if (!(vertices.isEmpty())) {
			this.obstacleToVerticesMap.put(hindrance, vertices);
		}
		vertices = null;  // Set<Vertex> vertices = new LinkedHashSet<Vertex>();
		newVerticesPositions = null;  // List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();

	}  // public void addHindrance(mxICell hindrance)

	public void addVerticesIntoOutOf(mxICell roadblock) {

		this.stOther.start();
		if ((roadblock == null) || (!(roadblock.isVertex()))) {
			throw new IllegalArgumentException("Input 'roadblock' is null or not a vertex.");
		}
		if (!(this.roadblocks.contains(roadblock))) {
			throw new IllegalArgumentException("Input 'roadblock' must be already added to this weighted visibility graph.");
		}
		mxGeometry geometry = roadblock.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'roadblock' has null geometry.");
		}

		Set<Vertex> vertices = this.obstacleToVerticesMap.get(roadblock);
		for (Vertex vertex : vertices) {
			this.removeVertex(vertex);
		}
		vertices.clear();

		this.roadblocks.remove(roadblock);
		Vertex vertex = this.addVertex(
				geometry.getCenterX(),
				geometry.getCenterY());
		this.addEdgesForVertex(vertex);
		vertices.add(vertex);
		this.roadblocks.add(roadblock);

//		int edgeCount = roadblock.getEdgeCount();
//		List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);
//		this.stOther.stop();
//		this.stEdgeVectors.start();
//
//		for (int index = 0; index < edgeCount; index++) {
//			mxICell edge = roadblock.getEdgeAt(index);
//			if ((edge == null) || (!(edge.isEdge()))) {
//				throw new IllegalStateException("The 'edge' variable is null or not an edge.");
//			}
//			mxGeometry edgeGeometry = edge.getGeometry();
//			if (edgeGeometry == null) {
//				throw new IllegalStateException("The 'edge' variable has null geometry.");
//			}
//			List<mxPoint> points = edgeGeometry.getPoints();
//			mxICell source = edge.getTerminal(true);
//			mxICell target = edge.getTerminal(false);
//			mxPoint point;
//			if (roadblock == source) {
//				if ((points == null) || (points.isEmpty())) {
//					if (target == null) {
//						point = edgeGeometry.getTargetPoint();
//					} else {
//						mxGeometry targetGeometry = target.getGeometry();
//						point = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
//					}
//				} else {
//					point = points.get(0);
//				}
//			} else if (roadblock == target) {
//				if ((points == null) || (points.isEmpty())) {
//					if (source == null) {
//						point = edgeGeometry.getSourcePoint();
//					} else {
//						mxGeometry sourceGeometry = source.getGeometry();
//						point = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
//					}
//				} else {
//					point = points.get(points.size() - 1);
//				}
//			} else {
//				throw new IllegalStateException("Edge does not connect to roadblock.");
//			}
//			edgeUnitVectorsList.add(Vector2D.subtract(point.getX(), point.getY(),
//					geometry.getCenterX(), geometry.getCenterY()).unitVector());
//		}  // End for (int index = 0; index < edgeCount; index++)
//
//		double x = geometry.getWidth() / 2 + MINIMUM_SPACING;
//		double y = geometry.getHeight() / 2 + MINIMUM_SPACING;
//		double radius = Math.sqrt(x * x + y * y);
//
//		this.stEdgeVectors.stop();
//		this.stSortEdgeVectors.start();
//		Collections.sort(edgeUnitVectorsList, new Vector2DComparator());
//		this.stSortEdgeVectors.stop();
//		this.stNewPositions.start();
//		List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();
//
//		for (int index = 0; index < edgeCount; index++) {
//
//			int nextIndex = (index + 1 >= edgeCount) ? (0) : (index + 1);
//			Vector2D currentVector = edgeUnitVectorsList.get(index);
//			Vector2D nextVector = edgeUnitVectorsList.get(nextIndex);
//			if (currentVector.isParallel(nextVector)) {
//				continue;
//			}
//			Vector2D rightUnitVector = currentVector.rotate90DegreesPositively();
//			if (currentVector.isAntiParallel(nextVector)) {
//				newVerticesPositions.add(new mxPoint(
//						geometry.getCenterX() + radius * rightUnitVector.getX(),
//						geometry.getCenterY() + radius * rightUnitVector.getY()));
//				continue;
//			}
//			Vector2D bisector = currentVector.add(nextVector);
//			//
//			// if the next vecotr points to the left of the current vector
//			//
//			if (nextVector.dotProduct(rightUnitVector) < 0) {
//				bisector = bisector.reverse();
//			}
//			double bisectorLength = bisector.length();
//			newVerticesPositions.add(new mxPoint(
//					geometry.getCenterX() + radius * bisector.getX() / bisectorLength,
//					geometry.getCenterY() + radius * bisector.getY() / bisectorLength));
//
//		}  // End for (int index = 0; index < edgeCount; index++)
//		this.stNewPositions.stop();
//		this.stOther.start();
//
//		edgeUnitVectorsList = null;  // List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);
//
//		Set<Vertex> vertices = new LinkedHashSet<Vertex>();
//		for (mxPoint position : newVerticesPositions) {
//
//			this.stOther.stop();
//			this.stAddVertices.start();
//			Vertex vertex = this.addVertex(
//					position.getX(),
//					position.getY());
//			this.stAddVertices.stop();
//			this.stAddEdges.start();
//			this.addEdgesForVertex(vertex);
//			this.stAddEdges.stop();
//			this.stOther.start();
//			vertices.add(vertex);
//
//		}  // End for (mxPoint position : newVerticesPositions)
//
//		if (!(vertices.isEmpty())) {
//			Set<Vertex> moreVertices = this.obstacleToVerticesMap.get(roadblock);
//			moreVertices.addAll(vertices);
////			this.obstacleToVerticesMap.put(roadblock, moreVertices);
//		}
//		vertices = null;  // Set<Vertex> vertices = new LinkedHashSet<Vertex>();
//		newVerticesPositions = null;  // List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();
		this.stOther.stop();

	}  // End public void addVerticesIntoOutOf(mxICell roadblock)

	public static class Cost {

		public double doubleValue;

		public Cost(double doubleValue) {
			this.doubleValue = doubleValue;
		}

		@Override
		public String toString() {
			return "" + doubleValue;
		}
	}  // End public static class Cost

	public static Collection<List<Vertex>> findShortestPaths(
			Collection<Vertex> inSourceVertices,
			Collection<Vertex> inTargetVertices) {
		return findShortestPaths(
				inSourceVertices,
				inTargetVertices,
				null,
				null);
	}  // End public static Collection<List<Vertex>> findShortestPaths(...)

	public static Collection<List<Vertex>> findShortestPaths(
			Collection<Vertex> inSourceVertices,
			Collection<Vertex> inTargetVertices,
			Double inOptionalMaxCostAllowed,
			Cost outOptionalCost) {

		if ((inSourceVertices == null)
				|| (inTargetVertices == null)) {
			throw new IllegalArgumentException("Invalid inputs to findShortestPath().");
		}

		if ((inSourceVertices.isEmpty()) || (inTargetVertices.isEmpty())) {
			return new LinkedHashSet<List<Vertex>>();
		}

		double minCostToTarget;
		if ((inOptionalMaxCostAllowed != null)
				&& (inOptionalMaxCostAllowed.doubleValue() >= 0)) {
			minCostToTarget = inOptionalMaxCostAllowed.doubleValue();
		} else {
			minCostToTarget = Double.POSITIVE_INFINITY;
		}
		List<Vertex> verticesToBeProcessed = new LinkedList<Vertex>();
		Map<Vertex, Double> vertexToCostMap = new HashMap<Vertex, Double>();
		double minCostToBeProcessed = 0.0d;
		boolean isPathFound = false;

		for (Vertex vertex : inSourceVertices) {
			verticesToBeProcessed.add(vertex);
			vertexToCostMap.put(vertex, minCostToBeProcessed);
		}  // End for (Vertex vertex : inSourceVertices)

		int numVerticesProcessed = 0;
		int numEdgesProcessed = 0;

		while (!(verticesToBeProcessed.isEmpty())) {

			Vertex vertex = verticesToBeProcessed.remove(0);
			Double cost = vertexToCostMap.get(vertex);
			if (cost == null) {
				throw new IllegalStateException("The cost variable cannot be null.");
			}
			numVerticesProcessed++;

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

			numEdgesProcessed += vertex.edges.size();

			for (Edge edge : vertex.edges) {

				Vertex source = edge.source;
				Vertex target = edge.target;
				Vertex neighbour;
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
				double weight = edge.weight;
				if (weight < 0) {
					throw new IllegalStateException("The weight of an edge cannot be negative for this shortest path algorithm.");
				}
				costToNeighbour = cost + weight;

				Double neighbourCost = vertexToCostMap.get(neighbour);
				if (neighbourCost == null) {
					vertexToCostMap.put(neighbour, costToNeighbour);

					ListIterator<Vertex> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
					while (listIterator.hasPrevious()) {
						Vertex aVertex = listIterator.previous();
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

				ListIterator<Vertex> listIterator = verticesToBeProcessed.listIterator(verticesToBeProcessed.size());
				while (listIterator.hasPrevious()) {

					Vertex aVertex = listIterator.previous();
					if (neighbourIndex == null) {
						if (aVertex == neighbour) {
							neighbourIndex = listIterator.nextIndex();
						}
						continue;
					}  // End if (neighbourIndex == null)
					if (vertexToCostMap.get(aVertex) <= costToNeighbour) {
						listIterator.next();
						break;
					}  // End if (vertexToCostMap.get(aVertex) <= costToNeighbour)

				}  // End while (listIterator.hasPrevious())

				if (neighbourIndex == null) {
					throw new IllegalStateException("A neighbour vertex whose minimum cost can be updated is not in the list of vertices to be processed.  This should not happen in this algorithm.");
				}
				int newIndex = listIterator.nextIndex();
				if (neighbourIndex != newIndex) {
					verticesToBeProcessed.remove(neighbourIndex.intValue());
					verticesToBeProcessed.add(newIndex, neighbour);
				}

			}  // End for (int index = 0; index < edgeCount; index++)

		}  // End while (!(verticesToBeProcessed.isEmpty()))

		verticesToBeProcessed = null;  // List<Vertex> verticesToBeProcessed = new LinkedList<Vertex>();

		if (!isPathFound) {
			vertexToCostMap = null;  // Map<Vertex, Double> vertexToCostMap = new HashMap<Vertex, Double>();
			return new LinkedHashSet<List<Vertex>>();
		}

		if (outOptionalCost != null) {
			outOptionalCost.doubleValue = minCostToTarget;
		}
		Set<List<Vertex>> outPaths = new LinkedHashSet<List<Vertex>>();

		for (Vertex vertex : inTargetVertices) {

			Double cost = vertexToCostMap.get(vertex);
			if ((cost == null) || (cost != minCostToTarget)) {
				continue;
			}

			List<Vertex> path = new LinkedList<Vertex>();
			path.add(0, vertex);
			List<List<Vertex>> pathsToBeProcessed = new LinkedList<List<Vertex>>();
			pathsToBeProcessed.add(path);
			path = null;  // List<Vertex> path = new LinkedList<Vertex>();

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

				for (Edge edge : vertex.edges) {

					Vertex source = edge.source;
					Vertex target = edge.target;
					Vertex neighbour;
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

					if ((path.contains(neighbour)) || (inTargetVertices.contains(neighbour))) {
						continue;
					}

					if (edge.weight < 0) {
						throw new IllegalStateException("The cost of an edge cannot be negative for this shortest path algorithm.");
					}

					Double neighbourCost = vertexToCostMap.get(neighbour);
					if ((neighbourCost == null)
							|| (Math.abs(cost - edge.weight - neighbourCost) >= 0.5d)) {
						continue;
					}

					List<Vertex> nextPath = new LinkedList<Vertex>(path);
					nextPath.add(0, neighbour);
					pathsToBeProcessed.add(nextPath);
					nextPath = null;  // List<Vertex> nextPath = new LinkedList<Vertex>(path);

				}  // End for (int index = 0; index < edgeCount; index++)

			}  // End while (!(pathsToBeProcessed.isEmpty()))

			pathsToBeProcessed = null;  // List<List<Vertex>> pathsToBeProcessed = new LinkedList<List<Vertex>>();

		}  // End for (Vertex vertex : inTargetVertices)

		vertexToCostMap = null;  // Map<Vertex, Double> vertexToCostMap = new HashMap<Vertex, Double>();

//		System.out.println("Dijkstra's algorithm finds " + outPaths.size() + " path(s) with cost " + minCostToTarget
//				+ " by processing " + numVerticesProcessed + " vertices and " + numEdgesProcessed + " edges.");
		return outPaths;
	}  // End public static Collection<List<Vertex>> findShortestPaths(...)

	public mxGraph toMxGraph() {

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		Map<Vertex, Object> oldToNewVerticesMap = new HashMap<Vertex, Object>();

		for (Vertex vertex : this.vertices) {
			Object object = graph.insertVertex(
					parent,
					null,
					null,
					vertex.location.getX() - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
					vertex.location.getY() - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
					VISIBILITY_GRAPH_VERTEX_WDITH,
					VISIBILITY_GRAPH_VERTEX_HEIGHT);
			oldToNewVerticesMap.put(vertex, object);
		}  // End for (Vertex vertex : this.vertices)

		for (Edge edge : this.edges) {
			graph.insertEdge(
					parent,
					null,
					edge.weight,
					oldToNewVerticesMap.get(edge.source),
					oldToNewVerticesMap.get(edge.target));
		}  // End for (Edge edge : this.edges)

		oldToNewVerticesMap = null;  // Map<Vertex, Object> oldToNewVerticesMap = new HashMap<Vertex, Object>();

		return graph;
	}  // End public mxGraph toMxGraph()
}  // End public class WeightedVisibilityGraph extends mxGraph implements Cloneable
