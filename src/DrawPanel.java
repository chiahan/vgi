

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reng
 */
import java.util.*;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;

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


import drawpackage.*;


public class DrawPanel extends JPanel {
    
    private static final long serialVersionUID = -6561623072112577140L;
    
    protected mxGraphComponent graphComponent;
    protected JLabel statusBar;
    protected JPanel libraryPane;
    protected mxGraphOutline graphOutline;
    
    protected boolean modified = false;

    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
   
    final mxGraph graph;
    
    public DrawPanel(mxGraphComponent component){
    
        graphComponent=component;
        graph=graphComponent.getGraph();
        graphOutline = new mxGraphOutline(graphComponent);
        libraryPane = new JPanel();
        libraryPane.setSize(300, 300);
        
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
					}
				});
	}
    public void status(String msg)
	{
		statusBar.setText(msg);
	}
    protected void installHandlers()
	{
		rubberband = new mxRubberband(graphComponent);
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

		// Installs the popup menu in the outline
		graphOutline.addMouseListener(new MouseAdapter()
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
					//showOutlinePopupMenu(e);
				}
			}

		});

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
					//showGraphPopupMenu(e);
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
   // public JFrame createFrame()
  //  {
	//	JFrame frame = new JFrame();
	//	frame.getContentPane().add(this);
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setJMenuBar(menuBar);
	//	frame.setSize(870, 640);
	//	frame.setVisible(true);
		// Updates the frame title
		//updateTitle();

	//	return frame;
    //}
    
    void paintAut(Automata aut){
            
          /*  Object parent = graph.getDefaultParent();
             
            ArrayList<State> statelist=aut.getAllStates();
            for(int i=0;i<statelist.size();++i){
                int id=statelist.get(i).getId();
                String name=(statelist.get(i)).getName();
                
                Point2D x=(statelist.get(i)).getGeometriData().x;
                Point2D y=(statelist.get(i)).getGeometriData().y;
                
                //x,y???
                graph.insertVertex(parent,Integer.toString(i),name,x.getX(),y.getY(),50,50,"shape=ellipse");
            
            }
            
            
            ArrayList<Transition> translist=aut.getAllTransitions();
            for(int i=0;i<translist.size();++i){
                
                
            }
            
            */
            
   }
    
}

