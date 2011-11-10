import java.awt.BorderLayout;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import storage.*;
import storage.MenuBar;

/**
* to start VGI 
* @author Junli Lu
*/
public class VGI {
        /**
         * start the program
         * @param args
         * @throws IOException IO exception
         * @throws SAXException SAX exception
         */
	public static void main(String[] args)
					throws IOException, SAXException {
		final Storage storage = Storage.getInstance();
		createShowGUI(storage);
	}
       /**
        * create and show the GUI
        * @param storage a place to store all items of UI
        * @author Junli Lu
        */
	private static void createShowGUI(Storage storage) {
		JFrame frame = new JFrame("VGI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MenuBar menu = new MenuBar();
                ToolBar toolbar = new ToolBar();
		TabbedPaneForGraph tabbedForGraph = storage.getTabbedForGraph();
                tabbedForGraph.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitPaneHorizontal.add(storage.getTabbedForGraph());
		JLabel userHintLabel = new JLabel("user hint goes here");
		JPanel midPanel = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel(new BorderLayout());

		midPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); //the direction order is up, left, down, and right
		midPanel.add(splitPaneHorizontal);
	
		statusPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		statusPanel.add(userHintLabel, BorderLayout.LINE_START);

		frame.setSize(850, 600);
		frame.setJMenuBar(menu.createMenuBar());
		frame.setLayout(new BorderLayout());
		frame.add(toolbar.getToolBar(), BorderLayout.PAGE_START);
		frame.add(midPanel, BorderLayout.CENTER);
		frame.add(statusPanel, BorderLayout.PAGE_END);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// storage saving
		storage.setMidPanel(midPanel);
		storage.setStatusPanel(statusPanel);
		storage.setSplitPaneHorizontal(splitPaneHorizontal);
		storage.setToolbar(toolbar);
		storage.setMenubar(menu);
		storage.setFrame(frame);
		storage.setUserHintLabel(userHintLabel);
                storage.ItemsEnableSetting("no file");
                frame.setVisible(true);
	}
} 

