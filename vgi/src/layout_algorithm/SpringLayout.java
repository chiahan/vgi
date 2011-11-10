/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package layout_algorithm;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import model.DataModelInterface;
import storage.Storage;

/**
 * change graph layout according to spring algorithm
 * @author polar 
 */
public class SpringLayout {

    /** for debugging, ignore in final build */
	private static final boolean DEBUG = true;

    /** one of spring algorithm parameters */
	private static final float C1_DEFAULT = 40;

    /** one of spring algorithm parameters */
	private static final float C2_DEFAULT = 100;

    /** one of spring algorithm parameters */
	private static final float C3_DEFAULT = 20;

    /** one of spring algorithm parameters */
	private static final double C4_DEFAULT = 0.7;

    /** one of spring algorithm parameters */
	private static float C1 = C1_DEFAULT;

    /** one of spring algorithm parameters */
	private static float C2 = C2_DEFAULT;

    /** one of spring algorithm parameters */
	private static float C3 = C3_DEFAULT;

    /** one of spring algorithm parameters */
	private static double C4 = C4_DEFAULT;

    /** puts a limit on the allowed number of algorithm iterations */
	private static final int TRIAL_LIMIT = 1000;

    /** halting criterion */
	private static final double ENERYGY_LIMIT = 1;

    /** one of spring algorithm parameters */
	private static final double MASS = 50;
    
    /** contains tools required for graph manipulation */
	private DataModelInterface dmi;

    /** list of graph node ids */
	private Vector<Integer> stateIDs;

    /** hashmap between nodes and their respective connected neighbors */
	private Hashtable<Integer, Vector<Integer>> adjacencyTable = new Hashtable();

    /** data structure used by spring algorithm */
	private Hashtable<Integer, Point2D.Double> velocityTable = new Hashtable();
    
    /** data structure used by spring algorithm */
	private Hashtable<Integer, Point2D.Double> positionTable = new Hashtable();

    /**
     * constructor
     * @param dmi contains all necessary tools for graph manipulation
     * @author polar
     */
	public SpringLayout(DataModelInterface dmi) {
//		this.dmi = dmi;
//		stateIDs = dmi.getAllStates();
	}

    /**
     * pops a dialog to allow users to modify spring algorithm variables
     * @author polar
     */
	public static void modifyParameterDialog() {
		String input = "";
		//** C1
		input = JOptionPane.showInputDialog(
						"C1 (spring attraction) default: " + Double.toString(C1_DEFAULT),
						Double.toString(C1));
		try {
			float temp = Float.parseFloat(input);
			C1 = temp;
			System.out.println("C1 set to: " + Double.toString(C1));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while parsing value for C1, default value (" +
							Double.toString(C1) + ") used");
			C1 = C1_DEFAULT;
		}

		//** C2
		input = JOptionPane.showInputDialog(
						"C2 (spring repulsion) default: " + Double.toString(C2_DEFAULT),
						Double.toString(C2));
		try {
			float temp = Float.parseFloat(input);
			C2 = temp;
			System.out.println("C2 set to: " + Double.toString(C2));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while parsing value for C2, default value (" +
							Double.toString(C2) + ") used");
			C2 = C2_DEFAULT;
		}

		//** C3
		input = JOptionPane.showInputDialog(
						"C1 (node repulsion) default: " + Double.toString(C3_DEFAULT),
						Double.toString(C3));
		try {
			float temp = Float.parseFloat(input);
			C3 = temp;
			System.out.println("C3 set to: " + Double.toString(C3));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while parsing value for C3, default value (" +
							Double.toString(C3) + ") used");
			C3 = C3_DEFAULT;
		}
		
		//** C4
		input = JOptionPane.showInputDialog(
						"C4 (damping coefficient) (0, 1) default: " + Double.toString(C4_DEFAULT),
						Double.toString(C4));
		try {
			double temp = Double.parseDouble(input);
			C4 = temp;
			System.out.println("C4 set to: " + Double.toString(C4));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while parsing value for C4, default value (" +
							Double.toString(C4) + ") used");
			C4 = C4_DEFAULT;
		}
	}

    /**
     * executes spring algorithm to relocate graph nodes
     * @author polar
     */
	public void run() {
		System.out.println("SpringLayout started");

		dmi = Storage.getInstance().getController().getDataModel();
		stateIDs = dmi.getAllStates();

		adjacencyTable = buildAdjacencyTable();
		velocityTable = buildVelocityTable();
		positionTable = buildPositionTable();

		double energy = 2 * ENERYGY_LIMIT;
		int trialCount = 0;

		//** main loop
		//** checks if system is stable
		//** impose limit on the number of trials possible
		while (energy > ENERYGY_LIMIT && trialCount < TRIAL_LIMIT) {
			energy = 0;
			trialCount++;
			//double totalDisplacement = 0;
			double netForceX = 0;
			double netForceY = 0;

			//**** for each node in the graph ****
			for (int i = 0; i < stateIDs.size(); i++) {
				int currentNode = stateIDs.get(i);
				netForceX = 0;
				netForceY = 0;

				//** compute repulsive force from every other node
				for (int k = 0; k < stateIDs.size(); k++) {
					int otherNode = stateIDs.get(k);
					//exclude itself
					if (currentNode != otherNode) {
						double forceRepulX = repulsionX(currentNode, otherNode);
						double forceRepulY = repulsionY(currentNode, otherNode);
						netForceX = netForceX + forceRepulX;
						netForceY = netForceY + forceRepulY;
					}
				}

				//** compute attraction force from connected transitions
				Vector<Integer> connectedNodes = adjacencyTable.get(currentNode);
				for (int k = 0; k < connectedNodes.size(); k++) {
					int otherNode = connectedNodes.get(k);
					//exclude loops
					if (currentNode != otherNode) {
						double forceAttrX = attractionX(currentNode, otherNode);
						double forceAttrY = attractionY(currentNode, otherNode);
						netForceX = netForceX + forceAttrX;
						netForceY = netForceY + forceAttrY;
					}
				}

				//** compute node position change
//				double velX = (velocityTable.get(currentNode).getX() + netForceX) * C4;
//				double velY = (velocityTable.get(currentNode).getY() + netForceY) * C4;
				double velX = netForceX * C4;
				double velY = netForceY * C4;
				velocityTable.put(currentNode, new Point2D.Double(velX, velY));
				double posX = positionTable.get(currentNode).getX() + velX;
				double posY = positionTable.get(currentNode).getY() + velY;
				positionTable.put(currentNode, new Point2D.Double(posX, posY));

				energy = energy + MASS * (Math.pow(velX, 2) + Math.pow(velY, 2));

			}// end for each node
			if (DEBUG) {
				System.out.println("trial: " + trialCount);
				System.out.println("energy: " + energy);
			}
		}// end while energy is still high and #trial still under limit

		System.out.println("SpringLayout finished");
		if (trialCount >= TRIAL_LIMIT) {
			System.out.println("node displacement iteration reached maximum limit of " + Integer.toString(TRIAL_LIMIT));
		} else {
			System.out.println("node displacement iterations used: " + trialCount);
		}

		//** write in new state positions
		for (int i = 0; i < stateIDs.size(); i++) {
			int currentState = stateIDs.get(i);
			double posX = positionTable.get(currentState).getX();
			double posY = positionTable.get(currentState).getY();
			dmi.setStatePosition(currentState, new Point2D.Float((float) posX, (float) posY));
		}

		//** fix positioning
		CenterGraph centerGraphLayout = new CenterGraph(dmi);
		centerGraphLayout.run();
	} // end run

    /**
     * returns adjacency hash table
     * @return adjacency hash table
     * @author polar
     */
	private Hashtable<Integer, Vector<Integer>> buildAdjacencyTable() {
		Hashtable hashtable = new Hashtable();
		for (int i = 0; i < stateIDs.size(); i++) {
			int stateID = stateIDs.get(i);
			hashtable.put(stateID, getConnectedNodes(stateID));
		}
		return hashtable;
	}

    /**
     * return nodes connected to node with stateID
     * does not return itself in case of a loop
     * @param stateID the origin node
     * @return vector of IDs of nodes connected to input node
     * @author polar
     */
	private Vector<Integer> getConnectedNodes(int stateID) {
		Vector<Integer> connectedNodes = new Vector<Integer>();
		Vector<Integer> transIDs = dmi.getAllTransitions();
		int sourceState = 0;
		int targetState = 0;
		int transition = 0;
		for (int i = 0; i < transIDs.size(); i++) {
			transition = transIDs.get(i);
			sourceState = dmi.getTransitionSource(transition);
			targetState = dmi.getTransitionTarget(transition);
			if (sourceState == stateID && sourceState != targetState) {
				connectedNodes.add(targetState);
			}
			if (targetState == stateID && sourceState != targetState) {
				connectedNodes.add(sourceState);
			}
		}
		return connectedNodes;
	}

    /**
     * builds velocity table of every nodes used for spring algorithm
     * @return velocity hash table
     * @author polar
     */
	private Hashtable<Integer, Point2D.Double> buildVelocityTable() {
		Hashtable hashtable = new Hashtable();
		for (int i = 0; i < stateIDs.size(); i++) {
			int stateID = stateIDs.get(i);
			hashtable.put(stateID, new Point2D.Double(0, 0));
		}
		return hashtable;
	}

    /**
     * builds position table used for spring algorithm
     * @return position hash table
     * @author polar
     */
	private Hashtable<Integer, Point2D.Double> buildPositionTable() {
		Hashtable hashtable = new Hashtable();
		for (int i = 0; i < stateIDs.size(); i++) {
			int stateID = stateIDs.get(i);
			double posX = dmi.getStatePosition(stateID).getX();
			double posY = dmi.getStatePosition(stateID).getY();
			hashtable.put(stateID, new Point2D.Double(posX, posY));
		}
		return hashtable;
	}

    /**
     * @author polar
     * @return Coulomb repulsion along X-axis
     */
	private double repulsionX(int state1, int state2) {
		double distance = findDistance(state2, state1);
		double distanceX = findDistanceX(state2, state1);
//		double force = COULOMB_COEFFICIENT / Math.pow(distance, 2);
		double force = C3 / Math.sqrt(distance);
//		force = setMaxForce(force);
		double cosine = Math.abs(distanceX / distance);
		force = force * cosine;
		if (distanceX > 0) {
			force = force * -1;
		}
		return force;
	}

    /**
     * @author polar
     * @return Coulomb repulsion along Y-axis
     */
	private double repulsionY(int state1, int state2) {
		double distance = findDistance(state2, state1);
		double distanceY = findDistanceY(state2, state1);
//		double force = COULOMB_COEFFICIENT / Math.pow(distance, 2);
		double force = C3 / Math.sqrt(distance);
		//force = setMaxForce(force);
		double sine = Math.abs(distanceY / distance);
		force = force * sine;
		if (distanceY > 0) {
			force = force * -1;
		}
		return force;
	}

    /**
     * @author polar
     * @return Hooke attraction along X-axis
     */
	private double attractionX(int state1, int state2) {
		double distance = findDistance(state2, state1);
		double distanceX = findDistanceX(state2, state1);
//		double force = SPRING_COEFFCIENT * distance;
		double force = C1 * Math.log(distance / C2);
//		force = setMaxForce(force);
		double cosine = Math.abs(distanceX / distance);
		force = force * cosine;
		if (distanceX < 0) {
			force = force * -1;
		}
		return force;
	}

    /**
     * @author polar
     * @return Hooke attraction along Y-axis
     */
	private double attractionY(int state1, int state2) {
		double distance = findDistance(state2, state1);
		double distanceY = findDistanceY(state2, state1);
//		double force = SPRING_COEFFCIENT * distance;
		double force = C1 * Math.log(distance / C2);
		//force = setMaxForce(force);
		double sine = Math.abs(distanceY / distance);
		force = force * sine;
		if (distanceY < 0) {
			force = force * -1;
		}
		return force;
	}

    /**
     * @author polar
     * @return lowest allowed value
     */
	private double setMinValue(double input) {
		double output = input;
		double limit = 1;
		if (output >= 0 && output < limit) {
			output = limit;
		}
		if (output < 0 && output > (-1 * limit)) {
			output = -1 * limit;
		}
		return output;
	}

    /**
     * @author polar
     * @return distance between state1 and state2
     */
	private double findDistance(int state1, int state2) {
		Point2D.Double posState1 = positionTable.get(state1);
		Point2D.Double posState2 = positionTable.get(state2);
		double diffX = posState1.getX() - posState2.getX();
		double diffY = posState1.getY() - posState2.getY();
		double distance = Math.sqrt(diffX * diffX + diffY * diffY);
//		return distance;
		return setMinValue(distance);
	}

    /**
     * @author polar
     * @return distance between state1 and state2 along X-axis
     */
	private double findDistanceX(int state1, int state2) {
		Point2D.Double posState1 = positionTable.get(state1);
		Point2D.Double posState2 = positionTable.get(state2);
		double distance = posState1.getX() - posState2.getX();
//		return distance;
		return setMinValue(distance);
	}

    /**
     * @author polar
     * @return distance between state1 and state2 along Y-axis
     */
	private double findDistanceY(int state1, int state2) {
		Point2D.Double posState1 = positionTable.get(state1);
		Point2D.Double posState2 = positionTable.get(state2);
		double distance = posState1.getY() - posState2.getY();
//		return distance;
		return setMinValue(distance);
	}
}// SpringLayout2

