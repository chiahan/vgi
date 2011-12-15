/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawpackage;

/**
 *
 * @author reng
 */
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxConstants;

public class EdgePalette extends JPanel{
    
    protected JLabel selectedEntry = null;
    protected mxEventSource eventSource = new mxEventSource(this);
    
    public EdgePalette(){
            addMouseListener(new MouseListener(){

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				clearSelection();
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});
        
        
        
    }
    public void setPreferredWidth(int width){
		int cols = Math.max(1, width / 55);
		setPreferredSize(new Dimension(width,
				(getComponentCount() * 55 / cols) + 30));
		revalidate();
    }
    public void addEdgeTemplate(final String name, ImageIcon icon,
			String style, int width, int height, Object value){
		mxGeometry geometry = new mxGeometry(0, 0, width, height);
		geometry.setTerminalPoint(new mxPoint(0, height), true);
		geometry.setTerminalPoint(new mxPoint(width, 0), false);
		geometry.setRelative(true);

		mxCell cell = new mxCell(value, geometry, style);
		cell.setEdge(true);

		addTemplate(name, icon, cell);
    }
    public void addTemplate(final String name, ImageIcon icon, mxCell cell){
		
                mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(
				new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null){
			if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32){
				icon = new ImageIcon(icon.getImage().getScaledInstance(32, 32,
						0));
			}
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(50, 50));
		//entry.setBackground(EditorPalette.this.getBackground().brighter());
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 10));

		entry.setVerticalTextPosition(JLabel.BOTTOM);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);

		entry.setToolTipText(name);
		entry.setText(name);

		entry.addMouseListener(new MouseListener()
		{

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				setSelectionEntry(entry, t);
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});

		// Install the handler for dragging nodes into a graph
                //DragGestureListener dragGestureListener = new DragGestureListener()
		//{
			/**
			 * 
			 */
		//	public void dragGestureRecognized(DragGestureEvent e)
		//	{
		//		e.startDrag(null, mxConstants.EMPTY_IMAGE, new Point(),
		//						t, null);
		//	}

		//};

		//DragSource dragSource = new DragSource();
		//dragSource.createDefaultDragGestureRecognizer(entry,
		//		DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}
        public void setSelectionEntry(JLabel entry, mxGraphTransferable t){
		JLabel previous = selectedEntry;
		selectedEntry = entry;

		if (previous != null)
		{
			previous.setBorder(null);
			previous.setOpaque(false);
		}

		if (selectedEntry != null)
		{
			selectedEntry.setBorder(ShadowBorder.getSharedInstance());
			selectedEntry.setOpaque(true);
		}

		eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry",
				selectedEntry, "transferable", t, "previous", previous));
	}
        public void clearSelection()
	{
		setSelectionEntry(null, null);
	}
        public void addListener(String eventName, mxIEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

    
}
