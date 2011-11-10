package storage;

import model.DataModel;
import model.DataModelInterface;
import java.awt.Component;
import java.io.File;
import javax.swing.*;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import controller.Controller;
import debug.Debug;
import event.UIEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Drawing2;
import model.Geometry2;
import org.jgraph.JGraph;

/**
* the structure of the tab component
* @param frame_, the frame
* @param toolbar_, the tool bar
* @param menubar_, the menu bar
* @param tabbedForGraph_, the tab panel for graphs
* @param tabbedForProperty_, the tab panel for property panel
* @param automatonPanel, the automata's property panel
* @param splitPaneHorizontal_, the split panel used for spliting the graph and the property panels
* @param userHintLabel_, used for user hint
* @param statusPanel_, used for using status
* @param midPanel, the panel in the middle of the frame
* @param controllerList_, the list of controllers
* @param fc_, the chooser for file operations
* @param nameFactory_, generating names
* @param propertyView_, map the component on its name
* @author Junli Lu
*/
public class Storage {
        /**
         * the only instance used in the UI
         */
	private static Storage storage = new Storage();
        /**
         * the frame
         */
	protected JFrame frame_ = null;
        /**
         * the tool bar
         */
	protected ToolBar toolbar_ = null;
        /**
         * the menu bar
         */
	protected MenuBar menubar_ = null;
        /**
         * the tab panel for graphs
         */
	protected TabbedPaneForGraph tabbedForGraph_ = null;
        /**
         * he tab panel for property panel
         */
	protected JTabbedPane tabbedForProperty_ = null;
        /**
         * the automata's property panel
         */
        protected AutomatonPanel automatonPanel =null;
        /**
         * the split panel used for spliting the graph and the property panels
         */
	protected JSplitPane splitPaneHorizontal_ = null;
        /**
         * used for user hint
         */
	protected JLabel userHintLabel_ = null;
        /**
         * used for using status
         */
	protected JPanel statusPanel_ = null;
        /**
         * the panel in the middle of the frame
         */
	protected JPanel midPanel = null;
        /**
         * the list of controllers
         */
	protected List<Controller> controllerList_ = null;
        /**
         * the chooser for file operations
         */
	protected JFileChooser fc_;
        /**
         * generate names
         */
	protected LabelFactory nameFactory_ = null;
        /**
         * map the component on its name
         */
	protected Hashtable propertyView_ = null;
        /**
        * initialize
        * @author Junli Lu
        */
	private Storage() {
		Debug.echo("Storage Constructor.........");
		//fc_ = new JFileChooser();
		fc_ = new JFileChooser(System.getProperties().getProperty("user.dir"));
		nameFactory_ = new LabelFactory("Automata_");
                tabbedForGraph_ = new TabbedPaneForGraph();
		tabbedForProperty_ = new JTabbedPane();
		propertyView_ = new Hashtable();
		controllerList_ = new ArrayList();
                automatonPanel = new AutomatonPanel();
	}
        /**
        * @return the storage
        * @author Junli Lu
        */
	public static Storage getInstance() {
		return storage;
	}
        /**
        * create a controller
        * @param filename the file name
        * @param model the DataModel
        * @return the index of the controller
        * @author Junli Lu
        */
	public int controllerCreate(String filename, DataModel model) {
		Controller controller = new Controller(this, model);
		if (filename == null) {
			filename = createFileName();
		}
		JGraph graph = controller.getGraph();
		tabbedForGraph_.add(new JScrollPane(graph));
		tabbedForGraph_.setTabComponentAt(tabbedForGraph_.getTabCount() - 1,
						new TabComponent(tabbedForGraph_, "graph", filename, null));
		controllerList_.add(controller);
		tabbedForGraph_.setSelectedIndex(tabbedForGraph_.getTabCount() - 1);
		Debug.echo("controllerCreate at index " + (controllerList_.size() - 1));
                this.ItemsEnableSetting("has file");
		return controllerList_.size() - 1;
	}
        /**
        * create a controller
        * @param filename the file name
        * @return the index of the controller
        * @author Junli Lu
        */
	public int controllerCreate(String filename) {
		DataModel model = new DataModel();
		Controller controller = new Controller(this, model);
		if (filename == null) {
			filename = createFileName();
		}
		JGraph graph = controller.getGraph();
		tabbedForGraph_.add(new JScrollPane(graph));
		tabbedForGraph_.setTabComponentAt(tabbedForGraph_.getTabCount() - 1,
						new TabComponent(tabbedForGraph_, "graph", filename, null));
		controllerList_.add(controller);
		tabbedForGraph_.setSelectedIndex(tabbedForGraph_.getTabCount() - 1);
		Debug.echo("controllerCreate at index " + (controllerList_.size() - 1));
                this.ItemsEnableSetting("has file");
		return controllerList_.size() - 1;
	}
        /**
        * add a property view on the frame
        * @param type the property's type
        * @author Junli Lu
        */
	public void addPropertiesView(String type) {
            try{
                Component component = null;
                if (type.equals("automata")) {
                    automatonPanel.ShowAutomaton(this.getSelectedGraphIndex());
                    component = new JScrollPane(automatonPanel);
                }
                tabbedForProperty_.add(component);
                TabComponent tab = new TabComponent(tabbedForProperty_, type, "", "");
                tabbedForProperty_.setTabComponentAt(tabbedForProperty_.getTabCount() - 1,tab);
                tabbedForProperty_.setSelectedIndex(tabbedForProperty_.getTabCount() - 1);
                propertyView_.put(type, component);
                if (tabbedForProperty_.getTabCount() == 1){ //when first add property tab
                    tabbedForProperty_.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                    splitPaneHorizontal_.add(tabbedForProperty_);
                    splitPaneHorizontal_.setDividerLocation(0.7);
                    splitPaneHorizontal_.setContinuousLayout(true);
                    splitPaneHorizontal_.setResizeWeight(1.0);
                }
            }
            catch (Exception e) {
                return;
            }
	}
        /**
        * remove a property view on the frame
        * @param type the property's type
        * @author Junli Lu
        */
	public void removePropertiesView(String type) {
            try{
                Component component = (Component) propertyView_.get(type);
                tabbedForProperty_.remove(component);
                if (tabbedForProperty_.getTabCount() == 0) {
                        this.splitPaneHorizontal_.remove(this.tabbedForProperty_);
                }
            }catch (Exception e) {
                return;
            }
	}
        /**
        * add the status info on the frame
        * @author Junli Lu
        */
	public void addStatusBarView() {
		Debug.echo("addStatusBarView.....");
		Dimension d1 = statusPanel_.getSize();
		// adjust the frame size ,and the border size of objects in frame is 4
		int borderSize = 0;
		frame_.setSize(frame_.getWidth(), (int) (frame_.getHeight() + d1.getHeight() + borderSize));
		frame_.add(statusPanel_, BorderLayout.PAGE_END);
	}
        /**
        * remove the status info on the frame
        * @author Junli Lu
        */
	public void removeStatusBarView() {
		Debug.echo("removeStatusBarView.....");
		Dimension d1 = statusPanel_.getSize();
		frame_.remove(statusPanel_);
		// adjust the frame size ,and the border size of objects in frame is 4
		int borderSize = 0;//
		frame_.setSize(frame_.getWidth(), (int) (frame_.getHeight() - d1.getHeight() - borderSize));
	}
        /**
        * to notify the controller a UI event
        * @param uievent the event from UI
        * @author Junli Lu
        */
	public void eventHappen(UIEvent uievent) {
		Debug.echo("Storage: eventHappen from UIevent " + uievent.getEvent());
		controllerList_.get(tabbedForGraph_.getSelectedIndex()).storageActionPerformed(uievent);
	}
        /**
        * new a file
        * @author Junli Lu
        */
	public void fileNew() {
		setUserHint("fileNew");
		SetAutomaton setAutomaton = new SetAutomaton();
		setAutomaton.Start();
	}
        /**
        * open a file
        * @author Junli Lu
        */
	public void fileOpen() {
		setUserHint("fileOpen");
		fc_.addChoosableFileFilter(new XmlFileFilter());
		fc_.setDialogTitle("Open Vaucanson XML file");
		int ret = fc_.showOpenDialog(frame_);
		if (ret == JFileChooser.APPROVE_OPTION) {
			String fileAddress = fc_.getSelectedFile().getAbsolutePath();
			String filename = fc_.getSelectedFile().getName();
			// read xml to parser
			XMLReader xmlReader = new XMLReader();
			xmlReader.parseXML(fileAddress);
			// create a new controller
			DataModel dataModel = new DataModel(xmlReader.getAutomata(), new Geometry2(), new Drawing2());
			controllerCreate(filename, dataModel);

			//TODO run a layout
			//UIEvent uievent = new UIEvent(-1,UserEventConstants.LAYERED_LATOUT_WITH_LEVELING);
			controllerList_.get(tabbedForGraph_.getSelectedIndex()).openFile();

			//** adjust state position according to fsmxml
			if (xmlReader.containsGeoInfo()) {
				try {
					DataModelInterface dmi = controllerList_.get(tabbedForGraph_.getSelectedIndex()).getDataModel();
					Vector<Integer> stateIDs = dmi.getAllStates();
					Hashtable<Integer, Point2D.Float> statePositions = xmlReader.getStatePositionH();
					for (int i = 0; i < stateIDs.size(); i++) {
						int stateID = stateIDs.get(i);
						Point2D.Float position = statePositions.get(stateID);
						if (position != null) {
							dmi.setStatePosition(stateID, position);
						}
					}
				} catch (Exception e) {
					System.out.println("error loading state positions");
					System.out.println(e.getMessage());
				}
			}
		}
	}
        /**
        * buggy, used only for debugging efficiency
        * @param file
        * @author 
        */
	public void fileOpen(String file) {
		setUserHint("fileOpen");
		// read xml to parser
		XMLReader xmlReader = new XMLReader();
		xmlReader.parseXML(file);
		// create a new controller
		DataModel dataModel = new DataModel(xmlReader.getAutomata(), new Geometry2(), new Drawing2());
		controllerCreate("Output", dataModel);

		//TODO run a layout
		//UIEvent uievent = new UIEvent(-1,UserEventConstants.LAYERED_LATOUT_WITH_LEVELING);
		controllerList_.get(tabbedForGraph_.getSelectedIndex()).openFile();

		// BEGIN
		// Workaround the issue of weird layout after running algorithms
		//** adjust state position according to fsmxml
//		if (xmlReader.containsGeoInfo()) {
//			try {
//				DataModelInterface dmi = controllerList_.get(tabbedForGraph_.getSelectedIndex()).getDataModel();
//				Vector<Integer> stateIDs = dmi.getAllStates();
//				Hashtable<Integer, Point2D.Float> statePositions = xmlReader.getStatePositionH();
//				for (int i = 0; i < stateIDs.size(); i++) {
//					int stateID = stateIDs.get(i);
//					Point2D.Float position = statePositions.get(stateID);
//					if (position != null) {
//						dmi.setStatePosition(stateID, position);
//					}
//				}
//			} catch (Exception e) {
//				System.out.println("error loading state positions");
//				System.out.println(e.getMessage());
//			}
//		}
		// Workaround the issue of weird layout after running algorithms
		// END
	}
        /**
        * save a file
        * @param index the index of the file
        * @author Junli Lu
        */
	public void fileSave(int index) {
		setUserHint("Save file");
		TabComponent tabComponent = (TabComponent) tabbedForGraph_.getTabComponentAt(index);
		String fileAddress = tabComponent.getFileAddress();
		if (fileAddress == null) //save file at first time
		{
			fileSaveAs(index);
		} else {
			Controller controller = controllerList_.get(index);
			DataModelInterface model = controller.getDataModel();
			//create an instance
			XMLCreator xMLCreator = new XMLCreator(model, fileAddress);
			//Save the XML on absolute
			xMLCreator.run();
		}
	}
        /**
        * save as a new file
        * @param index the index of the file
        * @author Junli Lu
        */
	public void fileSaveAs(int index) {
		setUserHint("fileSaveAs");
		fc_.addChoosableFileFilter(new XmlFileFilter());
		fc_.setDialogTitle("Save Vaucanson XML file to...");
		int ret = fc_.showSaveDialog(frame_);
		if (ret == JFileChooser.APPROVE_OPTION) {
			//if file address or name deosn't end with .xml, add it on.
			String fileAddress = fc_.getSelectedFile().getAbsolutePath();
			if (!fileAddress.contains(".xml")) {
				fileAddress += ".xml";
			}
			String filename = fc_.getSelectedFile().getName();
			if (!filename.contains(".xml")) {
				filename += ".xml";
			}
			Debug.echo("file address: " + fileAddress + "file name: " + filename);
			TabComponent tabComponent = (TabComponent) tabbedForGraph_.getTabComponentAt(index);
			tabComponent.setFilename(filename);
			tabComponent.setFileAddress(fileAddress);
			Controller controller = controllerList_.get(index);
			DataModelInterface model = controller.getDataModel();
			//create an instance
			XMLCreator xMLCreator = new XMLCreator(model, fileAddress);
			xMLCreator.run();
		}
	}
        /**
        * close a file
        * @param index the index of the file
        * @author Junli Lu
        */
	public void fileCloseDialog(int index) {
		setUserHint("Close file");
		TabComponent tabComponent = (TabComponent) tabbedForGraph_.getTabComponentAt(index);
		String filename = tabComponent.getFilename();
		String userHint = null;
		Object[] options = {"Save", "Discard", "Cancel"};
		int n = JOptionPane.showOptionDialog(frame_,
						"File " + filename + " is modified. Save it ?",
						"Question",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]); //set the default indexfsdfdsfsd
		if (n == JOptionPane.YES_OPTION) {
			userHint = "save file";
                        fileSave(index);
			removeFile(index);
		} else if (n == JOptionPane.NO_OPTION) {
			userHint = "close without saving";
			removeFile(index);
		} else {
			userHint = "canel saving file";
		}
		storage.setUserHint(userHint);
	}
        /**
        * remove a file
        * @param index the index of the file
        * @author Junli Lu
        */
	protected void removeFile(int index) {
            if (index != -1) {
                String name = tabbedForGraph_.getTitleAt(index);
                nameFactory_.delete_element(name);
                tabbedForGraph_.remove(index);
                controllerList_.remove(index);
                if(this.tabbedForGraph_.getTabCount() == 0){
                    this.tabbedForProperty_.removeAll();
                    this.splitPaneHorizontal_.remove(this.tabbedForProperty_);
                    this.ItemsEnableSetting("no file");
                }
            }

	}
        /**
        * close the frame
        * @author Junli Lu
        */
	public void fileExit() {
		System.exit(0);
	}
        /**
        * enable items based on description
        * @param description, to show the current UI situaition
        * @author Junli Lu
        */
        public void ItemsEnableSetting(String description){
            this.menubar_.ItemsEnableSetting(description);
            this.toolbar_.ItemsEnableSetting(description);
        }
        /**
        * @return generate a new file name
        * @author Junli Lu
        */
	protected String createFileName() {
		String filename = nameFactory_.get_new_label();
		return filename;
	}
        /**
        * @param i the index of the controller
        * @return the controller with index i
        * @author Junli Lu
        */
	public Controller getController(int i) {
		if (i >= controllerList_.size() || i < 0) {
			return null;
		}
		return controllerList_.get(i);
	}
        /**
        * @return the index of the selected graph
        * @author Junli Lu
        */
   	public int getSelectedGraphIndex() {
		return tabbedForGraph_.getSelectedIndex();
	}
        /**
        * @return the controller of the selected graph
        * @author Junli Lu
        */
	public Controller getController() {
		int idx = tabbedForGraph_.getSelectedIndex();
		return getController(idx);
	}
        /**
        * set controller c at index i of controllerList_
        * @param c the controller
        * @param i the index
        * @return the index of the controller
        * @author Junli Lu
        */
	public int setController(Controller c, int i) {
		Debug.echo("setController");

		controllerList_.set(i, c);
		return i;
	}
        /**
         * set the menu
         * @param menubar the menu
         */
	public void setMenubar(MenuBar menubar) {
		menubar_ = menubar;
	}
        /**
         *
         * @return the AutomatonPanel
         */
	public MenuBar getMenubar() {
		return menubar_;
	}
        /**
         * set the frame
         * @param frame
         */
	public void setFrame(JFrame frame) {
		frame_ = frame;
	}
        /**
         * @return the frame
         */
	public JFrame getFrame() {
		return frame_;
	}
        /**
         * @return the tab panel for grpahs
         */
	public TabbedPaneForGraph getTabbedForGraph() {
		return tabbedForGraph_;
	}
        /**
         * @return the size of the list of controllers
         */
	public int graphCountGet() {
		return controllerList_.size();
	}
        /**
         * @return the user hint label
         */
	public JLabel getUserHintLabel() {
		return userHintLabel_;
	}
        /**
         * set the user hint info.
         * @param userHintLabel user hint info.
         */
	public void setUserHintLabel(JLabel userHintLabel) {
		this.userHintLabel_ = userHintLabel;
	}
        /**
         * set the user hint text
         * @param text user hint text
         */
	public void setUserHint(String text) {
		this.userHintLabel_.setText(text);
	}
        /**
         * @return the tab panel for properties
         */
	public JTabbedPane getTabbedForProperty() {
		return tabbedForProperty_;
	}
        /**
         * set the tab panel which is for properties
         * @param tabbedForProperty_ tab panel for properties
         */
	public void settabbedForProperty_(JTabbedPane tabbedForProperty_) {
		this.tabbedForProperty_ = tabbedForProperty_;
	}
        /**
         * set the tab panel which is for graphs
         * @param tabbedForGraph_ tab panel for graphs
         */
	public void setTabbedForGraph(TabbedPaneForGraph tabbedForGraph_) {
		this.tabbedForGraph_ = tabbedForGraph_;
	}
        /**
         * @return the panel to put the status label
         */
	public JPanel getStatusPanel() {
		return statusPanel_;
	}
        /**
         * set statusPanel
         * @param statusPanel_ the panel to put the status hint
         */
	public void setStatusPanel(JPanel statusPanel_) {
		this.statusPanel_ = statusPanel_;
	}
        /**
         * @return the panel in the middle of the frame
         */
	public JPanel getMidPanel() {
		return midPanel;
	}
        /**
         * set midPanel
         * @param midPanel the panel in the middle of the frame
         */
	public void setMidPanel(JPanel midPanel) {
		this.midPanel = midPanel;
	}
        /**
         * @return the split panel used for spliting the graph and the property panels
         */
	public JSplitPane getSplitPaneHorizontal() {
		return splitPaneHorizontal_;
	}
        /**
         * set splitPaneHorizontal
         * @param splitPaneHorizontal the split panel used for spliting the graph and the property panels
         */
	public void setSplitPaneHorizontal(JSplitPane splitPaneHorizontal) {
		this.splitPaneHorizontal_ = splitPaneHorizontal;
	}
        /**
         * set tool bar
         * @param toolBar tool bar
         */
	public void setToolbar(ToolBar toolBar) {
		this.toolbar_ = toolBar;
	}
        /**
         * @return the automata's property panel
         */
  	public AutomatonPanel getAutomatonPanel() {
		return this.automatonPanel;
	}
}

