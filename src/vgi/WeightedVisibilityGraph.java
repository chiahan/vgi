/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.util.*;

/**
 *
 * @author JLiu
 */
public class WeightedVisibilityGraph extends mxGraph {

	protected static final double VISIBILITY_GRAPH_VERTEX_WDITH = 5;
	protected static final double VISIBILITY_GRAPH_VERTEX_HEIGHT = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double MINIMUM_SPACING = VISIBILITY_GRAPH_VERTEX_WDITH;

	protected static class LineSegment {

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
	}  // End protected static class LineSegment
	protected List<mxICell> roadblocks;
//	protected List<mxICell> hindrances;
	protected Map<mxICell, List<mxICell>> obstacleToVerticesMap;
	protected Map<mxICell, List<LineSegment>> hindranceToLineSegmentsMap;

	protected final void inititializeMembers() {
		this.roadblocks = new LinkedList<mxICell>();
//		this.hindrances = new LinkedList<mxICell>();
		this.obstacleToVerticesMap = new HashMap<mxICell, List<mxICell>>();
		this.hindranceToLineSegmentsMap = new HashMap<mxICell, List<LineSegment>>();
	}  // End protected final void inititializeMembers()

	public WeightedVisibilityGraph(mxIGraphModel model, mxStylesheet stylesheet) {
		super(model, stylesheet);
		inititializeMembers();
	}

	public WeightedVisibilityGraph(mxStylesheet stylesheet) {
		super(stylesheet);
		inititializeMembers();
	}

	public WeightedVisibilityGraph(mxIGraphModel model) {
		super(model);
		inititializeMembers();
	}

	public WeightedVisibilityGraph() {
		super();
		inititializeMembers();
	}

	protected void addEdgesForVertex(mxICell vertex) {

		if (vertex == null) {
			throw new IllegalArgumentException("Input 'vertex' is null.");
		}
		mxGeometry geometry = vertex.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'vertex' has null geometry.");
		}

		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildVertices(parent);

		for (int index = 0; index < objects.length; index++) {

			if (objects[index] == vertex) {
				continue;
			}
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell anotherVertex = (mxICell) objects[index];
			mxGeometry anotherGeometry = anotherVertex.getGeometry();
			if (anotherGeometry == null) {
				throw new IllegalStateException("The 'anotherVertex' variable has null geometry.");
			}

			boolean isVisible = true;
			Iterator<mxICell> iterateCells = this.roadblocks.iterator();
			while (iterateCells.hasNext()) {

				mxICell roadblock = iterateCells.next();
				mxGeometry roadblockGeometry = roadblock.getGeometry();
				if (roadblockGeometry == null) {
					throw new IllegalStateException("The 'roadblock' variable has null geometry.");
				}
				mxPoint point = roadblockGeometry.intersectLine(
						geometry.getCenterX(),
						geometry.getCenterY(),
						anotherGeometry.getCenterX(),
						anotherGeometry.getCenterY());
				if (point != null) {
					isVisible = false;
					break;
				}

			}  // End while (iterateCells.hasNext())

			if (!isVisible) {
				continue;
			}

			List<mxPoint> endPointIntersectionsList = new ArrayList<mxPoint>();
			int cost = 0;
			iterateCells = this.hindranceToLineSegmentsMap.keySet().iterator();
			while (iterateCells.hasNext()) {

				mxICell cell = iterateCells.next();
				List<LineSegment> lineSegments = this.hindranceToLineSegmentsMap.get(cell);
				Iterator<LineSegment> iterateLineSegments = lineSegments.iterator();
				while (iterateLineSegments.hasNext()) {

					LineSegment lineSegment = iterateLineSegments.next();
					mxPoint intersection = mxUtils.intersection(
							geometry.getCenterX(),
							geometry.getCenterY(),
							anotherGeometry.getCenterX(),
							anotherGeometry.getCenterY(),
							lineSegment.x1,
							lineSegment.y1,
							lineSegment.x2,
							lineSegment.y2);
					if (intersection == null) {
						continue;
					}
					if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
							|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
						if (!(endPointIntersectionsList.contains(intersection))) {
							endPointIntersectionsList.add(intersection);
							cost = cost + 1;
						}
					} else {
						cost = cost + 1;
					}

				}  // End while (iterateLineSegments.hasNext())

			}  // End while (iterateCells.hasNext())

			this.insertEdge(
					parent,
					null,
					cost,
					vertex,
					anotherVertex);

		}  // End for (int index = 0; index < objects.length; index++)

	}  // End public void addEdgesForVertex(mxICell vertex)

	public void addRoadblock(mxICell roadblock) {

		if ((roadblock == null) || (!(roadblock.isVertex()))) {
			throw new IllegalArgumentException("Input 'roadblock' is null or not a vertex.");
		}
		mxGeometry geometry = roadblock.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'roadblock' has null geometry.");
		}

		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildEdges(parent);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			mxICell source = edge.getTerminal(true);
			if (source == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			mxGeometry sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			mxICell target = edge.getTerminal(false);
			if (target == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			mxGeometry targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}

			mxPoint point = geometry.intersectLine(
					sourceGeometry.getCenterX(),
					sourceGeometry.getCenterY(),
					targetGeometry.getCenterX(),
					targetGeometry.getCenterY());
			if (point != null) {
				Object cells[] = {edge};
				this.removeCells(cells);
			}

		}  // End for (int index = 0; index < objects.length; index++)

		this.roadblocks.add(roadblock);
		mxICell vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);

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
		if (source == null) {
			throw new IllegalStateException("The 'edge' variable has null source.");
		}
		mxGeometry sourceGeometry = source.getGeometry();
		if (sourceGeometry == null) {
			throw new IllegalStateException("The 'source' variable has null geometry.");
		}
		mxICell target = hindrance.getTerminal(false);
		if (target == null) {
			throw new IllegalStateException("The 'edge' variable has null target.");
		}
		mxGeometry targetGeometry = target.getGeometry();
		if (targetGeometry == null) {
			throw new IllegalStateException("The 'target' variable has null geometry.");
		}

		mxPoint previousPoint = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
		List<LineSegment> lineSegments = new LinkedList<LineSegment>();
		List<mxPoint> points = geometry.getPoints();

		if ((points != null) && (!(points.isEmpty()))) {
			Iterator<mxPoint> iteratePoints = points.iterator();
			while (iteratePoints.hasNext()) {

				mxPoint point = iteratePoints.next();
				LineSegment lineSegment = new LineSegment(
						previousPoint.getX(),
						previousPoint.getY(),
						point.getX(),
						point.getY());
				lineSegments.add(lineSegment);
				previousPoint = point;

			}  // End while (iteratePoints.hasNext())
		}  // End if ((points != null) && (!(points.isEmpty())))

		LineSegment lineSegment = new LineSegment(
				previousPoint.getX(),
				previousPoint.getY(),
				targetGeometry.getCenterX(),
				targetGeometry.getCenterY());
		lineSegments.add(lineSegment);
		this.hindranceToLineSegmentsMap.put(hindrance, lineSegments);

		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildEdges(parent);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			source = edge.getTerminal(true);
			if (source == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			target = edge.getTerminal(false);
			if (target == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}
			Object object = edge.getValue();
			if (!(object instanceof Number)) {
				throw new IllegalStateException("The 'edge' variable's value is not of the type Number.");
			}
			double cost = ((Number) object).doubleValue();

			Iterator<LineSegment> iterateLineSegments = lineSegments.iterator();
			while (iterateLineSegments.hasNext()) {

				lineSegment = iterateLineSegments.next();
				mxPoint point = mxUtils.intersection(
						sourceGeometry.getCenterX(),
						sourceGeometry.getCenterY(),
						targetGeometry.getCenterX(),
						targetGeometry.getCenterY(),
						lineSegment.x1,
						lineSegment.y1,
						lineSegment.x2,
						lineSegment.y2);
				if (point != null) {
					cost = cost + 1;
				}

			}  // End while (iterateLineSegments.hasNext())

			if (cost != ((Number) object).doubleValue()) {
				edge.setValue(cost);
			}

		}  // End for (int index = 0; index < objects.length; index++)

	}  // public void addHindrance(mxICell hindrance)
}  // End public class WeightedVisibilityGraph extends mxGraph
