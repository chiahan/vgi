//package drawcomp;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reng
 */

import drawpackage.EditorPalette;
import drawpackage.EdgePalette;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Point;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class DrawPanel extends JPanel {
    
    private static final long serialVersionUID = -6561623072112577140L;
    
    protected mxGraphComponent graphComponent;
    protected JLabel statusBar;
    protected JPanel libraryPane;
    protected mxGraphOutline graphOutline;
    final mxGraph graph;
    
    protected boolean modified = false;

    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    
    
    protected Hashtable<String,mxCell> cellTable;
    protected mxCell trans_source,trans_target;
    protected int PopMouseX,PopMouseY;
    public DrawPanel(mxGraphComponent component){
    
        graphComponent=component;
        graph=graphComponent.getGraph();
        graphOutline = new mxGraphOutline(graphComponent);
        libraryPane = new JPanel();
        libraryPane.setSize(300, 300);
        
        
        cellTable=new Hashtable<String,mxCell>();
        
        graph.setCellsDisconnectable(false);
        graphComponent.setConnectable(false);
        
        
     /*   JPanel eButtons=new JPanel();
        JButton edgeB=new JButton("edge");
        edgeB.setIcon(new ImageIcon(DrawComp.class.getResource("/drawcomp/images/straight.png")));
        //edgeB.setSize(80, 80);
        JButton loopB=new JButton("loop");
        loopB.setIcon(new ImageIcon(DrawComp.class.getResource("/drawcomp/images/vertical.png")));
        
        eButtons.add(edgeB);
        eButtons.add(loopB);
        
        JSplitPane inner_up = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				libraryPane, eButtons);
	*/
        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				libraryPane, graphOutline);
	inner.setDividerLocation(400);
	inner.setResizeWeight(1);
	inner.setDividerSize(6);
	inner.setBorder(null);

        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
				graphComponent);
	outer.setOneTouchExpandable(true);
	outer.setDividerLocation(200);
	outer.setDividerSize(6);
	outer.setBorder(null);
        
        statusBar = createStatusBar();

        installRepaintListener();

        setLayout(new BorderLayout());
        add(outer, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        
        installHandlers();
	installListeners();
    }
    
    protected JLabel createStatusBar()
	{
		JLabel statusBar = new JLabel(mxResources.get("ready"));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		return statusBar;
	}
    protected void installRepaintListener()
	{
		graphComponent.getGraph().addListener(mxEvent.REPAINT,
                        new mxIEventListener(){
					public void invoke(Object source, mxEventObject evt)
					{
						String buffer = (graphComponent.getTripleBuffer() != null) ? ""
								: " (unbuffered)";
						mxRectangle dirty = (mxRectangle) evt.getProperty("region");

						if (dirty == null){
							status("Repaint all" + buffer);
						}else{
							status("Repaint: x=" + (int) (dirty.getX()) + " y="
									+ (int) (dirty.getY()) + " \nw="
									+ (int) (dirty.getWidth()) + " h="
									+ (int) (dirty.getHeight()) + buffer);
						}
                                                
                                                 graph.repaint(graph.getGraphBounds());
					}
				});
	}
    public void status(String msg)
	{
		statusBar.setText(msg);
	}
    protected void installHandlers()
	{
		//rubberband = new mxRubberband(graphComponent);
		//keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}
    protected void installListeners()
	{
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener()
		{
			/**
			 * 
			 */
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getSource() instanceof mxGraphOutline
						|| e.isControlDown())
				{
					DrawPanel.this.mouseWheelMoved(e);
				}
			}

		};

		// Handles mouse wheel events in the outline and graph component
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		
		// Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
                                        PopMouseX=e.getX();
                                        PopMouseY=e.getY();
				}
			}
                        
                        //--------------click to add state & transition
                        public void mouseClicked(MouseEvent e)
                        {
                            Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                            if(e.getButton()==MouseEvent.BUTTON1){
                          
				if (cell != null){
                                    if(((mxCell)cell).isVertex()){
                                    
					//System.out.println("add transition");
                                        if(trans_source!=null){
                                            
                                            trans_target=(mxCell)cell;
                                            addTrans(trans_source,trans_target);
                                            
                                            trans_source=null;
                                            trans_target=null;
                                            System.out.println("Target!");
                                        }
                                    }    
				}
                            }
                            if(e.isPopupTrigger()){
                                
                                showGraphPopupMenu(e);
                                
                                if (cell != null && ((mxCell)cell).isEdge()){
                                    PopMouseX=e.getX();
                                    PopMouseY=e.getY();
                                }
                                
                            }
                        }

		});

		// Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(
				new MouseMotionListener()
				{

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
					 */
					public void mouseDragged(MouseEvent e)
					{
						mouseLocationChanged(e);
					}

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
					 */
					public void mouseMoved(MouseEvent e)
					{
						mouseDragged(e);
					}

				});
	}
    
    protected void mouseWheelMoved(MouseWheelEvent e)
    {
		if (e.getWheelRotation() < 0){
			graphComponent.zoomIn();
		}else{
			graphComponent.zoomOut();
		}

		status(mxResources.get("scale") + ": "
				+ (int) (100 * graphComponent.getGraph().getView().getScale())
				+ "%");
	}
    protected void mouseLocationChanged(MouseEvent e)
    {
		status(e.getX() + ", \n" + e.getY());
    }
    public EditorPalette insertShapePalette(String title)
    {
		final EditorPalette palette = new EditorPalette();
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		libraryPane.add(title, scrollPane);

		// Updates the widths of the palettes if the container size changes
		libraryPane.addComponentListener(new ComponentAdapter()
		{
			/**
			 * 
			 */
			public void componentResized(ComponentEvent e)
			{
				int w = scrollPane.getWidth()
						- scrollPane.getVerticalScrollBar().getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
    }
    public EdgePalette insertEdgePalette(String title)
    {
		final EdgePalette palette = new EdgePalette();
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		libraryPane.add(title, scrollPane);

		// Updates the widths of the palettes if the container size changes
		libraryPane.addComponentListener(new ComponentAdapter()
		{
			/**
			 * 
			 */
			public void componentResized(ComponentEvent e)
			{
				int w = scrollPane.getWidth()
						- scrollPane.getVerticalScrollBar().getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
    }
    
    public mxGraphComponent getGraphComponent()
    {
		return graphComponent;
    }
    
    public Action bind(String name, final Action action)
    {
		return bind(name, action, null);
    }
    
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl)
    {
		return new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				DrawPanel.class.getResource(iconUrl)) : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(getGraphComponent(), e
						.getID(), e.getActionCommand()));
			}
		};
    }
    protected void showGraphPopupMenu(MouseEvent e)
    {
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(DrawPanel.this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
    }
    public JFrame createFrame()
    {
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setJMenuBar(menuBar);
		frame.setSize(870, 640);
		frame.setVisible(true);
		// Updates the frame title
		//updateTitle();

		return frame;
    }

    public void addState(int x,int y)
    {
        Object parent = graph.getDefaultParent();
        String id = String.valueOf(cellTable.size());
        Object newv=graph.insertVertex(parent,id,"",x-25,y-25,50,50,"shape=ellipse;perimeter=ellipsePerimeter;");
        cellTable.put((String)id, (mxCell)newv);
        System.out.println("add state at"+x+","+y);
        
    }
    public void addTrans(mxCell source,mxCell target)
    {
        Object parent = graph.getDefaultParent();
        Object e=graph.insertEdge(parent, null, "", source, target,"shape=curve");
        ArrayList<mxPoint> points=new ArrayList<mxPoint>();
        
        //System.out.println("source pt"+((mxCell)e).getGeometry().);
        //System.out.println("target pt"+((mxCell)e).getGeometry().getTargetPoint());
        
        //points.add(((mxCell)e).getGeometry().getSourcePoint());
        //points.add(((mxCell)e).getGeometry().getTargetPoint());
        
        ((mxCell)e).getGeometry().setPoints(points);
        
    }
    public void addControlPt()
    {
            addControlPt((mxCell)graphComponent.getCellAt(PopMouseX,PopMouseY),PopMouseX,PopMouseY);    
    }
    public void addControlPt(mxCell cell,int x,int y)
    {
        
        System.out.println("add Ctrl pt at"+x+","+y);
        ArrayList<mxPoint> points=(ArrayList)cell.getGeometry().getPoints();
        //ArrayList<mxPoint> points1=new ArrayList<mxPoint>();
        
        points.add(new mxPoint(x,y)); 
        
        //to sort
        if(points.size()>1){
            Collections.sort(points,new CompareCtrlPt());
            if(cell.getSource().getGeometry().getX()>cell.getTarget().getGeometry().getX())
                Collections.reverse(points);
        }
      
              
        cell.getGeometry().setPoints(points);
        graphComponent.refresh();
        
       // graph.repaint(graph.getGraphBounds());
       
    }
    public class CompareCtrlPt implements Comparator{

        @Override
        public int compare(Object t, Object t1) {
            //throw new UnsupportedOperationException("Not supported yet.");
            
            Integer tX=(int)((mxPoint)t).getX();
            Integer tY=(int)((mxPoint)t).getY();
            Integer t1X=(int)((mxPoint)t1).getX();
            Integer t1Y=(int)((mxPoint)t1).getY();
            
            int flag=tX.compareTo(t1X);
            if(flag==0) return tY.compareTo(t1Y);
            else return flag;
           
        }
        
        
    } 

    class addStateAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //throw new UnsupportedOperationException("Not supported yet.");
            addState(PopMouseX,PopMouseY);
        }
        
        
    }
    class addTransAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //throw new UnsupportedOperationException("Not supported yet.");
            trans_source=(mxCell)graphComponent.getCellAt(PopMouseX,PopMouseY);
            System.out.println("source");
        }
        
        
    }
    class addCtrlPtAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //throw new UnsupportedOperationException("Not supported yet.");
            addControlPt();
        }
        
        
    }
    public Action getAddStateAction(){
        return (new addStateAction());
    }
    public Action getAddTransAction(){
        return (new addTransAction());
    }
    public Action getAddCtrlPtAction(){
        return (new addCtrlPtAction());
    }
    
    void paintAutomata(Automata automata) {
        graph.getModel().beginUpdate();

        try {
            Object parent = graph.getDefaultParent();

            List<State> stateList = automata.getAllStates();
            int size = stateList.size();
            for (int i = 0; i < size; i++) {
                State state = stateList.get(i);
                String id = state.getId();
                String name = state.getName();
                Point2D location = state.getGeometricData().location;
                
                //x,y???
                Object vertex = graph.insertVertex(parent, Integer.toString(i), 
                        name, location.getX(), location.getY(), 50, 50, 
                        "shape=ellipse");
                cellTable.put((String) id, (mxCell) vertex);
            }

            List<Transition> translist=automata.getAllTransitions();
            for(int i=0;i<translist.size();++i){
                
                String sourceId=translist.get(i).getSourceState().getId();
                String targetId=translist.get(i).getTargetState().getId();
                
                mxCell source=cellTable.get(sourceId);
                mxCell target=cellTable.get(targetId);
                
                Object e2=graph.insertEdge(parent, null, "", source, target);
            }

        } finally {
            graph.getModel().endUpdate();
        }
        graphComponent = new mxGraphComponent(graph);
    }
    
    
     public static void main(String[] args) {
        // TODO code application logic here
    
        mxGraphComponent comp=new mxGraphComponent(new mxGraph());
        DrawPanel drawpanel=new DrawPanel(comp);
        drawpanel.createFrame();
    
    }
}

