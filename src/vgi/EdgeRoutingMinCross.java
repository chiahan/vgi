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
		mxGraph planarGraph = planarize(this.getGraph());
		mxGraph dualGraph = buildDualGraph(planarGraph);

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

		List<mxICell> path = findShortestPath(dualGraph, planarSource, planarTarget);
		if (!(path.isEmpty())) {
			System.out.print("Shortest path goes through");

			Iterator<mxICell> iterateVertices = path.iterator();
			while (iterateVertices.hasNext()) {
				mxICell vertex = iterateVertices.next();
				System.out.print(" {" + vertex.getValue().toString() + "}");
			}  // End while (iterateVertices.hasNext())

			System.out.println();
		}  // End if (!(path.isEmpty()))
//		List<mxPoint> controlPoints = new ArrayList<mxPoint>();
//		super.setEdgePoints(edge, controlPoints);
	}  // End public void route(mxCell edge)

	protected static List<mxICell> findShortestPath(mxGraph inGraph, mxICell inSource, mxICell inTarget) {

		if ((inGraph == null)
				|| (inSource == null)
				|| (inTarget == null)) {
			throw new IllegalArgumentException("Invalid input to findShortestPath().");
		}

		List<mxICell> verticesToBeVisited = new LinkedList<mxICell>();
		List<List<mxICell>> paths = new LinkedList<List<mxICell>>();
		List<List<mxICell>> completePaths = new LinkedList<List<mxICell>>();
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
			if (region.contains(inSource)) {
				List<mxICell> path = new ArrayList<mxICell>();
				path.add(vertex);
				paths.add(path);
				if (region.contains(inTarget)) {
					completePaths.add(path);
				}
			} else {  // End if (region.contains(inSource))
				verticesToBeVisited.add(vertex);
			}
		}  // End for (int index = 0; index < count; index++)

		Iterator<List<mxICell>> iteratePaths = completePaths.iterator();
		while (iteratePaths.hasNext()) {
			List<mxICell> path = iteratePaths.next();
			return path;
		}  // End while (iteratePaths.hasNext())

		while (!(verticesToBeVisited.isEmpty())) {

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

				if (!(verticesToBeVisited.contains(nextVertex))) {
					continue;
				}

				List<mxICell> newPath = new ArrayList<mxICell>(path);
				newPath.add(nextVertex);
				paths.add(newPath);

				Object object = nextVertex.getValue();
				if (!(object instanceof Region)) {
					throw new IllegalStateException("The value of vertex is not of the type Region.");
				}
				Region region = (Region) object;
				if (region.contains(inTarget)) {
					completePaths.add(newPath);
				}
				verticesToBeVisited.remove(nextVertex);

			}  // End for (int index = 0; index < count; index++)

			paths.remove(path);

			iteratePaths = completePaths.iterator();
			while (iteratePaths.hasNext()) {
				path = iteratePaths.next();
				return path;
			}  // End while (iteratePaths.hasNext())

		}  // End while (!(verticesToBeVisited.isEmpty()))

		return null;
	}  // End protected static List<mxICell> findShortestPath(mxGraph inGraph, mxICell inSource, mxICell inTarget)

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
	}  // End public static mxGraph planarize(mxGraph inGraph)

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
					throw new IllegalStateException("The value of outVertex is not the type List.");
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
}  // End public class EdgeRoutingMinCross extends mxGraphLayout
