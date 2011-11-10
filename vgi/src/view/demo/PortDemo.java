package view.demo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.jgraph.JGraph;
import view.jgraph.JGraphController;
import view.main.CanvasView;

/**
 * A demo for using JGraphController
 * @author 劉哲維(Cody Liu)
 */
public class PortDemo {

    public PortDemo() {
        //TODO Initial State
        JGraph graph = new JGraph();
        JGraphController JGController = new JGraphController(graph, null);

//Demo : Delete State

        JGController.createState(20, 100, 1, "sx1", "StateView");
        JGController.createState(100, 20, 2, "s2", "StateView");
        JGController.createState(300, 270, 3, "s3", "StateView");
        JGController.createState(430, 470, 4, "s4", "StateView");
        JGController.createState(130, 470, 5, "s5", "StateView");
        JGController.createState(290, 470, 6, "s6", "AcceptStateView");
        JGController.createState(430, 470, 7, "s7", "StateView");

        //Demo : Delete Transition
        JGController.createTransition(6, 7, 1, "Yo Man", null);
        JGController.createTransition(5, 6, 2, "Opps", null);


//Demo of new TransitionEffect
/*
        //Turn off the effect
        JGController.setNewTransitionEffect(-1);
        //Set  the Control points
        ArrayList list = new ArrayList();
        list.add(new Point2D.Double(0, 0));
        list.add(new Point2D.Double(300, 300));
        list.add(new Point2D.Double(400, 400));
        list.add(new Point2D.Double(0, 0));
        JGController.setNewTempTransitionControlPoint(list);
        JGController.setNewTransitionLineType(101);
        JGController.setNewTransitionEffect(6);
         */


//Demo of insert a Bezier Transition

        //Demo of Bezier Transition
        ArrayList list2 = new ArrayList();
        list2.add(new Point2D.Double(0, 0));
        list2.add(new Point2D.Double(300, 150));
        list2.add(new Point2D.Double(300, 250));
        list2.add(new Point2D.Double(0, 0));
        JGController.insertPolyTransition(1, 5, 3, list2, "yoyo");

        ArrayList list3 = new ArrayList();
        list3.add(new Point2D.Double(100, 250));
        list3.add(new Point2D.Double(500, 250));
        list3.add(new Point2D.Double(0, 250));
        JGController.setTransitionControlPoint(3, list3);
        JGController.show();
    }

    public static void main(String[] args) {
        PortDemo instance = new PortDemo();
    }
}




