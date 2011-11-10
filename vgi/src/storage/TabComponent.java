package storage;

import debug.Debug;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
* the structure of the tab component
* @author Junli Lu
*/
public class TabComponent extends JPanel 
{
    /**
     * the panel where the tab will adds
     */
    private final JTabbedPane pane_;
    /**
     * the name label of the tab
     */
    private JLabel nameLabel_;
    /**
     * the whole file name
     */
    private String fileAddress_;
    /**
     * the tab's type
     */
    private String type_;  
    /**
    * initialize
    * @param pane the panel where the tab will adds
    * @param filename the initial file name
    * @param fileAddress the whole file name
    * @param type the tab's type
    * @author Junli Lu
    */
    public TabComponent(final JTabbedPane pane,String type,String filename,String fileAddress)
    {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        fileAddress_ = fileAddress; //no file address at first
        type_ = type;
        pane_ = pane;
        if(type_ != "graph")
            nameLabel_ = new JLabel(type_);
        else
            nameLabel_ = new JLabel(filename);
        setOpaque(false);
        //make JLabel read the filename as the tab name
        add(nameLabel_);
        //add more space between the label and the button
        nameLabel_.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        //tab button
        JButton button = new TabButton();        
        add(button);
        //button.setLocation(new Point((int)(this.getWidth() - 17),(int)(this.getHeight() - 17)));
        //button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        //add more space to the upside of the component.
        //setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        //this.setSize(this.getWidth() - 20,this.getHeight());
        //Create the popup menu.
    }
    /**
    * @return fileAddress_
    * @author Junli Lu
    */
    public String getFileAddress() {
        return fileAddress_;
    }
    /**
    * set the fileAddress
    * @param fileAddress the file address
    * @author Junli Lu
    */
    public void setFileAddress(String fileAddress) {
        this.fileAddress_ = fileAddress;
    }
    /**
    * @return the file name
    * @author Junli Lu
    */
    public String getFilename() {
        return nameLabel_.getText().toString();
    }
    /**
    * set the filename
    * @param filename the file name
    * @author Junli Lu
    */
    public void setFilename(String filename) {
        nameLabel_.setText(filename);
    }
    /**
    * the structure of the tab button
    * @author Junli Lu
    */
    private class TabButton extends JButton implements ActionListener
    {
        /**
        * initialize the structure of the tab button
        * @author Junli Lu
        */
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            setRolloverEnabled(true);
            this.addMouseListener(new  MouseAdapter  (){
                public void mouseEntered(MouseEvent e) {
                    Component component = e.getComponent();
                    if (component instanceof AbstractButton) {
                        AbstractButton button = (AbstractButton) component;
                        button.setBorderPainted(true);
                    }
                }

                public void mouseExited(MouseEvent e) {
                    Component component = e.getComponent();
                    if (component instanceof AbstractButton) {
                        AbstractButton button = (AbstractButton) component;
                        button.setBorderPainted(false);
                    }
                }                
            });
            addActionListener(this);
        }
        /**
        * actionPerformed
        * @param e ActionEvent
        * @author Junli Lu
        */
        public void actionPerformed(ActionEvent e) {
            int index = pane_.indexOfTabComponent(TabComponent.this);
            Storage storage = Storage.getInstance();
            if (index != -1)
            {
                if(type_ == "graph"){
                    storage.fileCloseDialog(index);
                }
                else if(type_  == "automata"){
                    storage.removePropertiesView(nameLabel_.getText());
                }
            }
        }
       /**
        * paint this button as a cross
        * @param g Graphics
        * @author Junli Lu
        */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);

            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
}
