package view.demo;

import org.jgraph.JGraph;
import view.jgraph.JGraphController;
import view.main.CanvasView;

/**
 * A demo for deleting state note.
 * @author 劉哲維
 */
public class DeleteDemo {

    public DeleteDemo() {
        //TODO Initial State
        JGraph graph = new JGraph();
        JGraphController JGController = new JGraphController(graph, null);

//Demo : Delete State

        JGController.createState(20, 100, 1, "s1", "StateView");
        JGController.createState(100, 20, 2, "s2", "StateView");
        JGController.createState(300, 270, 3, "s3", "StateView");
        JGController.createState(430, 470, 4, "s4", "StateView");
        JGController.createState(130, 470, 5, "s5", "StateView");


        JGController.removeState(1);
        JGController.removeState(2);
        JGController.removeState(3);

        JGController.createState(290, 470, 6, "s6", "StateView");
        JGController.createState(390, 270, 7, "s7", "StateView");

        //Demo : Delete Transition
        JGController.createTransition(6, 7, 1, "Yo Man", null);
        JGController.createTransition(5, 6, 2, "Opps", null);
        JGController.removeTransition(2);


        JGController.createTransition(6, 4, 3,  "Opps", null);

        //       JGController.removeState(4);
        //       JGController.removeState(5);
        JGController.show();


    // view.cellview.AcceptStateView



    }

    public static void main(String[] args) {
        DeleteDemo instance = new DeleteDemo();
    }
}




