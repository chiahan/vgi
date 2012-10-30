/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.edgerouting;

import vgi.layout.edgerouting.EdgeRoutingMinCross;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import vgi.display.JgraphXInternalFrame;
import vgi.display.VGI;

/**
 *
 * @author JLiu
 */
public class MeasureExecutionTimes {

	protected static final int NUM_MEASUREMENTS = 12;

	protected static double measure(
			VGI inVgi,
			String inFilePath,
			String inSourceName,
			String inTargetName,
			boolean inAddTransition,
			boolean inApplyRES) {

		inVgi.openFile(inFilePath);
		JgraphXInternalFrame frame = inVgi.getSelectedFrame();
		mxGraph graph = frame.graph;
		Object parent = graph.getDefaultParent();
		Object objects[] = graph.getChildVertices(parent);
		mxCell source = null;
		mxCell target = null;
		for (Object object : objects) {
			if (!(object instanceof mxCell)) {
				throw new IllegalStateException("A vertex is not of the type mxCell.");
			}
			mxCell vertex = (mxCell) object;
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The 'vertex' variable is not a vertex.");
			}
			Object value = vertex.getValue();
			if (!(value instanceof String)) {
				continue;
			}
			String string = (String) value;
			if (string.equals(inSourceName)) {
				source = vertex;
			} else if (string.equals(inTargetName)) {
				target = vertex;
			}
			if ((source != null) && (target != null)) {
				break;
			}
		}  // End for (Object object : objects)
		if ((source == null) || (target == null)) {
			throw new IllegalStateException("Missing source or target vertex.");
		}
		if (inAddTransition) {
			frame.addTransition(source, target);
		}
		mxCell edgeToRoute = null;
		objects = graph.getChildEdges(parent);
		for (Object object : objects) {
			if (!(object instanceof mxCell)) {
				throw new IllegalStateException("An edge is not of the type mxCell.");
			}
			mxCell edge = (mxCell) object;
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			if ((edge.getTerminal(true) == source)
					&& (edge.getTerminal(false) == target)) {
				edgeToRoute = edge;
				break;
			}
		}  // End for (Object object : objects)
		if (edgeToRoute == null) {
			throw new IllegalStateException("Missing the edge to route.");
		}
		EdgeRoutingMinCross edgeRoutingMinCross = new EdgeRoutingMinCross(graph);
		Stopwatch stopwatch = new Stopwatch();
		if (inApplyRES) {
			stopwatch.start();
			edgeRoutingMinCross.route(edgeToRoute);
			stopwatch.stop();
		} else {
			stopwatch.start();
			edgeRoutingMinCross.routeByWeightedVisibilityGraph(edgeToRoute);
			stopwatch.stop();
		}
		frame.modified = false;
		inVgi.closeSelectedFrame();

		return stopwatch.getElapsedMilliseconds();
	}  // End protected static double measure(...)

	protected static void logRun(
			VGI inVgi,
			String inFilePathWithoutExtension,
			String inSourceName,
			String inTargetName,
			boolean inAddTransition,
			int inTimesToRepeat) {

		PrintStream printStream = null;
		try {
			printStream = new PrintStream(new File(inFilePathWithoutExtension + ".txt"));
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}

		double wvgData[] = new double[inTimesToRepeat];
		double resData[] = new double[inTimesToRepeat];

		for (int index = 0; index < inTimesToRepeat; index++) {
			try {
				wvgData[index] = MeasureExecutionTimes.measure(
						inVgi,
						inFilePathWithoutExtension + ".xml",
						inSourceName,
						inTargetName,
						inAddTransition,
						false);
				resData[index] = MeasureExecutionTimes.measure(
						inVgi,
						inFilePathWithoutExtension + ".xml",
						inSourceName,
						inTargetName,
						inAddTransition,
						true);
			} catch (Exception exception) {
				wvgData = null;  // double wvgData[] = new double[inTimesToRepeat];
				resData = null;  // double resData[] = new double[inTimesToRepeat];
				printStream.close();
				exception.printStackTrace();
				return;
			}
		}  // End for (int index = 0; index < inTimesToRepeat; index++)

		double dataEntries[] = wvgData;
		double minimum = Double.POSITIVE_INFINITY;
		double maximum = Double.NEGATIVE_INFINITY;
		double totalDuration = 0;

		for (double currentDuration : dataEntries) {
			if (currentDuration < minimum) {
				minimum = currentDuration;
			}
			if (currentDuration > maximum) {
				maximum = currentDuration;
			}
			totalDuration = totalDuration + currentDuration;
		}  // End for (double currentDuration : dataEntries)

		printStream.println("Weighted Visibility Graph Execution Time in ms:");
		totalDuration = totalDuration - minimum - maximum;
		double average = totalDuration / (dataEntries.length - 2);
		double sumOfSquaredError = 0;

		for (double currentDuration : dataEntries) {
			if (currentDuration == minimum) {
				printStream.println(Math.round(currentDuration) + " min");
			} else if (currentDuration == maximum) {
				printStream.println(Math.round(currentDuration) + " max");
			} else {
				printStream.println(Math.round(currentDuration));
			}
			sumOfSquaredError = sumOfSquaredError
					+ (currentDuration - average) * (currentDuration - average);
		}  // End for (Long currentDuration : dataEntries)

		sumOfSquaredError = sumOfSquaredError
				- (minimum - average) * (minimum - average)
				- (maximum - average) * (maximum - average);
		printStream.println("Average:  " + Math.round(average));
		printStream.println("Sample standard deviation:  "
				+ Math.round(Math.sqrt(
				sumOfSquaredError / (dataEntries.length - 2 - 1))));

		dataEntries = resData;
		minimum = Double.POSITIVE_INFINITY;
		maximum = Double.NEGATIVE_INFINITY;
		totalDuration = 0;

		for (double currentDuration : dataEntries) {
			if (currentDuration < minimum) {
				minimum = currentDuration;
			}
			if (currentDuration > maximum) {
				maximum = currentDuration;
			}
			totalDuration = totalDuration + currentDuration;
		}  // End for (double currentDuration : dataEntries)

		printStream.println("Repeatedly Expanding Subgraph Execution Time in ms:");
		totalDuration = totalDuration - minimum - maximum;
		average = ((double) totalDuration) / (dataEntries.length - 2);
		sumOfSquaredError = 0;

		for (double currentDuration : dataEntries) {
			if (currentDuration == minimum) {
				printStream.println(Math.round(currentDuration) + " min");
			} else if (currentDuration == maximum) {
				printStream.println(Math.round(currentDuration) + " max");
			} else {
				printStream.println(Math.round(currentDuration));
			}
			sumOfSquaredError = sumOfSquaredError
					+ (currentDuration - average) * (currentDuration - average);
		}  // End for (Long currentDuration : dataEntries)

		sumOfSquaredError = sumOfSquaredError
				- (minimum - average) * (minimum - average)
				- (maximum - average) * (maximum - average);
		printStream.println("Average:  " + Math.round(average));
		printStream.println("Sample standard deviation:  "
				+ Math.round(Math.sqrt(
				sumOfSquaredError / (dataEntries.length - 2 - 1))));

		wvgData = null;  // double wvgData[] = new double[inTimesToRepeat];
		resData = null;  // double resData[] = new double[inTimesToRepeat];
		printStream.close();
	}  // End protected static void logRun(...)

	public static void main(String args[]) {
		VGI vgi = new VGI();
//		vgi.setVisible(true);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/1 crossing max", "s0", "s2", true, 10);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/1 crossing max", "s0", "s2", true, NUM_MEASUREMENTS);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/cycle test", "s4", "s1", false, 10);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/cycle test", "s4", "s1", false, NUM_MEASUREMENTS);
		File file = new File("test input/char-b/cycle test.txt");
		if (file.exists()) {
			file.renameTo(new File("test input/char-b/cycle test s4 to s1.txt"));
		}
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/cycle test", "s12", "s8", false, NUM_MEASUREMENTS);
		file = new File("test input/char-b/cycle test.txt");
		if (file.exists()) {
			file.renameTo(new File("test input/char-b/cycle test s12 to s8.txt"));
		}
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/5x5mesh", "s6", "s18", true, NUM_MEASUREMENTS);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/5x5", "s6", "s18", true, NUM_MEASUREMENTS);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/5x5+48", "s0", "s24", true, NUM_MEASUREMENTS);
		vgi.exitProgram();
	}  // End public static void main(String args[])
}  // End public class MeasureExecutionTimes
