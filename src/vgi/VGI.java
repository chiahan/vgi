package vgi;

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vgi.FsmXmlInterface.FsmXmlException;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/*
 * VGI.java
 *
 * Created on Nov 30, 2011, 1:08:01 PM
 */
/**
 *
 * @author JLiu
 */
public class VGI extends javax.swing.JFrame {

	private VGI pmVGI;
	private File pmLastFolderForOpenFile;
	private File pmLastFolderForSaveFile;
	private TAFKit pmTAFKit;
	private TAFKitInterface.AutomataType pmAutomataType;

	private class AlgorithmMenuItemActionListener implements java.awt.event.ActionListener {

		public TAFKitInterface.VcsnAlgorithm vcsnAlgorithm;

		public AlgorithmMenuItemActionListener(TAFKitInterface.VcsnAlgorithm vcsnAlgorithm) {
			if (vcsnAlgorithm == null) {
				throw new IllegalArgumentException("Input argument vcsnAlgorithm cannot be null!");
			}
			this.vcsnAlgorithm = vcsnAlgorithm;
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {

			ArrayList<Object> inputs = new ArrayList<Object>();
			int automataCount = 0;

			for (int index = 0; index < this.vcsnAlgorithm.inputsInfo.size(); index++) {
				TAFKitInterface.VcsnAlgorithm.IoInfo inputInfo = this.vcsnAlgorithm.inputsInfo.get(index);
				switch (inputInfo.type) {

					case AUTOMATON:
						automataCount++;
						if (automataCount == 1) {
							JInternalFrame frame = mainDesktopPane.getSelectedFrame();
							if (JgraphXInternalFrame.class.isInstance(frame)) {
								inputs.add(((JgraphXInternalFrame) frame).getAutomata());
							}
						} else {  // End if (automataCount == 1)
							JFileChooser fileChooser = new JFileChooser(pmLastFolderForOpenFile);
							fileChooser.setDialogTitle("Please choose a FSM XML file");
							fileChooser.setFileFilter(FsmXmlInterface.fileNameExtensionFilter);
							fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							fileChooser.setMultiSelectionEnabled(false);
							int returnValue = fileChooser.showOpenDialog(pmVGI);
							if (returnValue != JFileChooser.APPROVE_OPTION) {
								return;
							}
							pmLastFolderForOpenFile = fileChooser.getCurrentDirectory();
							inputs.add(fileChooser.getSelectedFile());
						}  // End else part of if (automataCount == 1)
						break;

					case BOOLEAN:
						throw new IllegalArgumentException("The " + (index + 1) + "th argument is boolean, but no argument should be boolean.");

					case INTEGER:
						String string = JOptionPane.showInputDialog("Please input an integer.");
						if ((string == null) || (string.isEmpty())) {
							return;
						}
						inputs.add(Integer.decode(string));
						break;

					case REGULAR_EXPRESSION:
						string = JOptionPane.showInputDialog("Please input a regular expression.");
						if ((string == null) || (string.isEmpty())) {
							return;
						}
						inputs.add(string);
						break;

					case TEXT:
						throw new IllegalArgumentException("The " + (index + 1) + "th argument is text, but no argument should be text.");

					case WEIGHT:
						string = JOptionPane.showInputDialog("Please input a weight value.");
						if ((string == null) || (string.isEmpty())) {
							return;
						}
						inputs.add(string);
						break;

					case WORD:
						string = JOptionPane.showInputDialog("Please input a word.");
						if ((string == null) || (string.isEmpty())) {
							return;
						}
						inputs.add(string);
						break;

					default:
						break;

				}  // End switch (inputInfo.type)
			}  // End for (int index = 0; index < this.vcsnAlgorithm.inputsInfo.size(); index++)

			List<Object> outputs;
			try {
				outputs = pmTAFKit.runVcsnAlgorithm(pmAutomataType, this.vcsnAlgorithm, inputs);
			} catch (Exception exception) {
				exception.printStackTrace();
                                JOptionPane.showMessageDialog(
                                        pmVGI,
					exception.getMessage(),
					null,
					JOptionPane.WARNING_MESSAGE);
				return;
			}

			for (int index = 0; index < this.vcsnAlgorithm.outputsInfo.size(); index++) {

				TAFKitInterface.VcsnAlgorithm.IoInfo outputInfo = this.vcsnAlgorithm.outputsInfo.get(index);
				Object object = outputs.get(index);

				switch (outputInfo.type) {

					case AUTOMATON:
						if (Automata.class.isInstance(object)) {
							createInternalFrame((Automata) object, "Output of \"" + this.vcsnAlgorithm.name);
						} else {
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" should be an automaton, but the returned data does not have the right type.",
									null,
									JOptionPane.WARNING_MESSAGE);
						}
						break;

					case BOOLEAN:
						if (object instanceof Boolean) {
							Boolean bool = (Boolean) object;
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + bool,
									null,
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
						}
						break;

					case INTEGER:
						if (object instanceof Integer) {
							Integer integer = (Integer) object;
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + integer,
									null,
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
						}
						break;

					case REGULAR_EXPRESSION:
						if (object instanceof String) {
							String string = (String) object;
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
									null,
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							throw new IllegalArgumentException("The " + (index + 1) + "th output should be a regular expression, but it is not!");
						}
						break;

					case TEXT:
						if (object instanceof String) {
							String string = (String) object;
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
									null,
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							throw new IllegalArgumentException("The " + (index + 1) + "th output should be text, but it is not!");
						}
						break;

					case WEIGHT:
						throw new UnsupportedOperationException("The " + (index + 1) + "th output should be a weight, but VGI does not support weight outputs yet!");

					case WORD:
						if (object instanceof String) {
							String string = (String) object;
							JOptionPane.showMessageDialog(
									pmVGI,
									"Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
									null,
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							throw new IllegalArgumentException("The " + (index + 1) + "th output should be a word, but it is not!");
						}
						break;

					default:
						throw new IllegalArgumentException("The " + (index + 1) + "th output does not have a recognizable type.");

				}  // End switch (outputInfo.type)

			}  // End for (int index = 0; index < this.vcsnAlgorithm.outputsInfo.size(); index++)

		}  // End public void actionPerformed(ActionEvent actionEvent)
	}  // End private class AlgorithmMenuItemActionListener implements java.awt.event.ActionListener

	private void createInternalFrame(JgraphXInternalFrame frame) {
		frame.setVisible(true);
		mainDesktopPane.add(frame);
                //mainDesktopPane.setSelectedFrame(frame);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}

		mxGraphOutline graphOutline = frame.getGraphOutline();
		graphOutline.setMinimumSize(new Dimension(298, 284));
		infoSplitPane.setBottomComponent(graphOutline);

		this.validate();
	}

	private void createInternalFrame(Automata automata) {
		JgraphXInternalFrame frame =
				new JgraphXInternalFrame(infoSplitPane, new mxGraph(),
				automata, "untitled",this);
		createInternalFrame(frame);
	}

	private void createInternalFrame(Automata automata, String filename) {
		JgraphXInternalFrame frame =
				new JgraphXInternalFrame(infoSplitPane, new mxGraph(),
				automata, filename,this);
		createInternalFrame(frame);
	}

	/**
	 * Creates new form VGI
	 */
	public VGI() {
		this.pmVGI = this;
		String defaultFolderPath = System.getProperty("user.dir");
		this.pmLastFolderForOpenFile = new File(defaultFolderPath);
		this.pmLastFolderForSaveFile = new File(defaultFolderPath);
		this.pmAutomataType = null;
		initComponents();
		this.editMenu.setVisible(false);
		this.viewMenu.setVisible(false);

		Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
		String string = preferences.get("TAF-Kit Path", defaultFolderPath);
		try {
			this.pmTAFKit = new TAFKit(new File(string));
		} catch (IllegalArgumentException illegalArgumentException) {
			this.pmTAFKit = null;
		}
		if (this.pmTAFKit != null) {
			this.currentSettingMenuItem.setText("Current setting:  " + this.pmTAFKit.getTafKitPath().getAbsolutePath());
//			this.updateAlgorithmMenuItems();
		}
                
                
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                setSize(screenSize.width, screenSize.height);
                
                
	}  // End public VGI()
        
        
        

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        fitWindowButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        actualSizeButton = new javax.swing.JButton();
        toolBarSeparator2 = new javax.swing.JToolBar.Separator();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        mainSplitPane = new javax.swing.JSplitPane();
        infoSplitPane = new javax.swing.JSplitPane();
        infoPanel = new javax.swing.JPanel();
        outlinePanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        mainDesktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        fileMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        deleteMenuItem = new javax.swing.JMenuItem();
        editMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        setAsInitialStateMenuItem = new javax.swing.JMenuItem();
        setAsFinalStateMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        fitWindowMenuItem = new javax.swing.JMenuItem();
        zoomInMenuItem = new javax.swing.JMenuItem();
        zoomOutMenuItem = new javax.swing.JMenuItem();
        actualSizeMenuItem = new javax.swing.JMenuItem();
        viewMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        showPropertiesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showStatusBarCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        layoutMenu = new javax.swing.JMenu();
        circleMenuItem = new javax.swing.JMenuItem();
        hierarchicalMenuItem = new javax.swing.JMenuItem();
        featureMenuItem = new javax.swing.JMenuItem();
        routeAllEdges2008MenuItem = new javax.swing.JMenuItem();
        routeAllEdgesBranchingMenuItem = new javax.swing.JMenuItem();
        planarizeMenuItem = new javax.swing.JMenuItem();
        buildDualGraphMenuItem = new javax.swing.JMenuItem();
        buildWeightedVisibilityGraphMenuItem = new javax.swing.JMenuItem();
        testShortestPathsMenuItem = new javax.swing.JMenuItem();
        singleVertexEdgesLayoutMenuItem = new javax.swing.JMenuItem();
        showWeightedVisibilityGraphMenuItem = new javax.swing.JMenuItem();
        edgeCostSettingsMenuItem = new javax.swing.JMenuItem();
        algorithmsMenu = new javax.swing.JMenu();
        setTAFKitPathMenuItem = new javax.swing.JMenuItem();
        currentSettingMenuItem = new javax.swing.JMenuItem();
        algorithmsMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        mergeSimilarTransitionsMenuItem = new javax.swing.JMenuItem();
        accessibleMenuItem = new javax.swing.JMenuItem();
        coaccessibleMenuItem = new javax.swing.JMenuItem();
        removeEpsilonTransitionsMenuItem = new javax.swing.JMenuItem();
        productMenuItem = new javax.swing.JMenuItem();
        algorithmsMenuSeparator2 = new javax.swing.JPopupMenu.Separator();
        helpMenu = new javax.swing.JMenu();
        rationalExpressionSymbolsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VGI");
        setMinimumSize(new java.awt.Dimension(1110, 710));

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        fitWindowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/fit-32.png"))); // NOI18N
        fitWindowButton.setFocusable(false);
        fitWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fitWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fitWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(fitWindowButton);

        zoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/zoomin-32.png"))); // NOI18N
        zoomInButton.setFocusable(false);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInButtonActionPerformed(evt);
            }
        });
        toolBar.add(zoomInButton);

        zoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/zoomout-32.png"))); // NOI18N
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutButtonActionPerformed(evt);
            }
        });
        toolBar.add(zoomOutButton);

        actualSizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/actual-32.png"))); // NOI18N
        actualSizeButton.setFocusable(false);
        actualSizeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        actualSizeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        actualSizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualSizeButtonActionPerformed(evt);
            }
        });
        toolBar.add(actualSizeButton);
        toolBar.add(toolBarSeparator2);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/undo-32.png"))); // NOI18N
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        toolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/redo-32.png"))); // NOI18N
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        toolBar.add(redoButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);

        mainSplitPane.setOneTouchExpandable(true);

        infoSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        infoSplitPane.setResizeWeight(1.0);
        infoSplitPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        infoSplitPane.setMaximumSize(new java.awt.Dimension(300, 600));
        infoSplitPane.setMinimumSize(new java.awt.Dimension(300, 600));
        infoSplitPane.setPreferredSize(new java.awt.Dimension(300, 600));

        infoPanel.setMinimumSize(new java.awt.Dimension(300, 400));
        infoPanel.setPreferredSize(new java.awt.Dimension(300, 400));
        infoSplitPane.setTopComponent(infoPanel);

        outlinePanel.setDoubleBuffered(false);
        outlinePanel.setPreferredSize(new java.awt.Dimension(298, 284));
        infoSplitPane.setBottomComponent(outlinePanel);

        mainSplitPane.setLeftComponent(infoSplitPane);

        mainPanel.setLayout(new java.awt.BorderLayout());

        statusLabel.setText("status:");
        mainPanel.add(statusLabel, java.awt.BorderLayout.SOUTH);
        mainPanel.add(mainDesktopPane, java.awt.BorderLayout.CENTER);

        mainSplitPane.setRightComponent(mainPanel);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/Document.png"))); // NOI18N
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/Folder.png"))); // NOI18N
        openMenuItem.setText("Open...");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/Close.png"))); // NOI18N
        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/save.png"))); // NOI18N
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/saveas.png"))); // NOI18N
        saveAsMenuItem.setText("Save As...");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(fileMenuSeparator1);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/Standby.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/delete.png"))); // NOI18N
        deleteMenuItem.setText("Delete");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);
        editMenu.add(editMenuSeparator1);

        setAsInitialStateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_07.png"))); // NOI18N
        setAsInitialStateMenuItem.setText("Set As Initial State");
        setAsInitialStateMenuItem.setEnabled(false);
        editMenu.add(setAsInitialStateMenuItem);

        setAsFinalStateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_06.png"))); // NOI18N
        setAsFinalStateMenuItem.setText("Set As Final State");
        setAsFinalStateMenuItem.setEnabled(false);
        editMenu.add(setAsFinalStateMenuItem);

        menuBar.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        fitWindowMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/fit-16.png"))); // NOI18N
        fitWindowMenuItem.setText("Fit Window");
        viewMenu.add(fitWindowMenuItem);

        zoomInMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/zoomin-16.png"))); // NOI18N
        zoomInMenuItem.setText("Zoom In");
        viewMenu.add(zoomInMenuItem);

        zoomOutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/zoomout-16.png"))); // NOI18N
        zoomOutMenuItem.setText("Zoom Out");
        viewMenu.add(zoomOutMenuItem);

        actualSizeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/actual-16.png"))); // NOI18N
        actualSizeMenuItem.setText("Actual Size");
        viewMenu.add(actualSizeMenuItem);
        viewMenu.add(viewMenuSeparator1);

        showPropertiesCheckBoxMenuItem.setSelected(true);
        showPropertiesCheckBoxMenuItem.setText("Display Automata Properties");
        showPropertiesCheckBoxMenuItem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                showPropertiesCheckBoxMenuItemStateChanged(evt);
            }
        });
        viewMenu.add(showPropertiesCheckBoxMenuItem);

        showStatusBarCheckBoxMenuItem.setSelected(true);
        showStatusBarCheckBoxMenuItem.setText("Display Status Bar");
        showStatusBarCheckBoxMenuItem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                showStatusBarCheckBoxMenuItemStateChanged(evt);
            }
        });
        viewMenu.add(showStatusBarCheckBoxMenuItem);

        menuBar.add(viewMenu);

        layoutMenu.setMnemonic('L');
        layoutMenu.setText("Layout");

        circleMenuItem.setText("Circle");
        circleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                circleMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(circleMenuItem);

        hierarchicalMenuItem.setText("Hierarchical");
        hierarchicalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hierarchicalMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(hierarchicalMenuItem);

        featureMenuItem.setText("feature");
        featureMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                featureMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(featureMenuItem);

        routeAllEdges2008MenuItem.setText("Route All Edges (2008)");
        routeAllEdges2008MenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeAllEdges2008MenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(routeAllEdges2008MenuItem);

        routeAllEdgesBranchingMenuItem.setText("Route All Edges (Branching)");
        routeAllEdgesBranchingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeAllEdgesBranchingMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(routeAllEdgesBranchingMenuItem);

        planarizeMenuItem.setText("Planarize");
        planarizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planarizeMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(planarizeMenuItem);

        buildDualGraphMenuItem.setText("Build Dual Graph");
        buildDualGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildDualGraphMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(buildDualGraphMenuItem);

        buildWeightedVisibilityGraphMenuItem.setText("Build Weighted Visibility Graph");
        buildWeightedVisibilityGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildWeightedVisibilityGraphMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(buildWeightedVisibilityGraphMenuItem);

        testShortestPathsMenuItem.setText("Test Shortest Paths");
        testShortestPathsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testShortestPathsMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(testShortestPathsMenuItem);

        singleVertexEdgesLayoutMenuItem.setText("Single Vertex Edges Layout");
        singleVertexEdgesLayoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleVertexEdgesLayoutMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(singleVertexEdgesLayoutMenuItem);

        showWeightedVisibilityGraphMenuItem.setText("Show Weighted Visibility Graph");
        showWeightedVisibilityGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showWeightedVisibilityGraphMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(showWeightedVisibilityGraphMenuItem);

        edgeCostSettingsMenuItem.setText("Edge Cost Settings...");
        edgeCostSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeCostSettingsMenuItemActionPerformed(evt);
            }
        });
        layoutMenu.add(edgeCostSettingsMenuItem);

        menuBar.add(layoutMenu);

        algorithmsMenu.setMnemonic('A');
        algorithmsMenu.setText("Algorithms");
        algorithmsMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                algorithmsMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        setTAFKitPathMenuItem.setText("Set TAF-Kit Path...");
        setTAFKitPathMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTAFKitPathMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(setTAFKitPathMenuItem);

        currentSettingMenuItem.setText("Current setting:  N/A");
        currentSettingMenuItem.setEnabled(false);
        algorithmsMenu.add(currentSettingMenuItem);
        algorithmsMenu.add(algorithmsMenuSeparator1);

        mergeSimilarTransitionsMenuItem.setText("Merge Similar Transitions");
        mergeSimilarTransitionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeSimilarTransitionsMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(mergeSimilarTransitionsMenuItem);

        accessibleMenuItem.setText("Accessible");
        accessibleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accessibleMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(accessibleMenuItem);

        coaccessibleMenuItem.setText("Coaccessible");
        coaccessibleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coaccessibleMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(coaccessibleMenuItem);

        removeEpsilonTransitionsMenuItem.setText("Remove Epsilon Transitions");
        removeEpsilonTransitionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEpsilonTransitionsMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(removeEpsilonTransitionsMenuItem);

        productMenuItem.setText("Product");
        productMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(productMenuItem);
        algorithmsMenu.add(algorithmsMenuSeparator2);

        menuBar.add(algorithmsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        rationalExpressionSymbolsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/question.png"))); // NOI18N
        rationalExpressionSymbolsMenuItem.setText("Rational Expression Symbols");
        rationalExpressionSymbolsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rationalExpressionSymbolsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(rationalExpressionSymbolsMenuItem);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/newicons/info.png"))); // NOI18N
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setTAFKitPathMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTAFKitPathMenuItemActionPerformed

		JFileChooser tafKitPathChooser = new JFileChooser(System.getProperty("user.dir"));
		if (pmTAFKit != null) {
			tafKitPathChooser.setCurrentDirectory(pmTAFKit.getTafKitPath());
		}
		tafKitPathChooser.setDialogTitle(this.setTAFKitPathMenuItem.getText());
		tafKitPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		tafKitPathChooser.setMultiSelectionEnabled(false);
		TAFKit tafKit = null;

		while (true) {

			int returnValue = tafKitPathChooser.showOpenDialog(this);
			if (returnValue != JFileChooser.APPROVE_OPTION) {
				break;
			}

			try {
				tafKit = new TAFKit(tafKitPathChooser.getSelectedFile());
				break;
			} catch (IllegalArgumentException illegalArgumentException) {
				returnValue = JOptionPane.showConfirmDialog(
						this,
						illegalArgumentException.getMessage() + "\nDo you want to try setting TAF-Kit path again?",
						"Invalid TAF-Kit path",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (returnValue != JOptionPane.YES_OPTION) {
					break;
				}
			}

		}  // End while (true)

		if (tafKit != null) {
			this.currentSettingMenuItem.setText("Current setting:  " + tafKit.getTafKitPath().getAbsolutePath());
			this.pmTAFKit = tafKit;
//			this.updateAlgorithmMenuItems();
			Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
			preferences.put("TAF-Kit Path", tafKit.getTafKitPath().getAbsolutePath());
		}  // End if (pmTAFKit != null)

    }//GEN-LAST:event_setTAFKitPathMenuItemActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
		// TODO add your handling code here:
		CreateAutomataDialog createDialog = new CreateAutomataDialog(this, true);
		createDialog.setVisible(true);
		if (createDialog.isCreated()) {
			Automata automata = new Automata();
			automata.setAlphabet(createDialog.getAlphabet());
			automata.setWeight(createDialog.getWeight());
			automata.setWritingData(createDialog.getWritingData());
//			System.out.println("Create automata from 'NewMenuItem'");

			/*
			 * Create draw panel for new automata
			 */
			this.createInternalFrame(automata);
//			System.out.println("adding DrawPanel is done! ");
		}
    }//GEN-LAST:event_newMenuItemActionPerformed

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed

		JFileChooser fileChooser = new JFileChooser(this.pmLastFolderForOpenFile);
		fileChooser.setFileFilter(FsmXmlInterface.fileNameExtensionFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int returnValue = fileChooser.showOpenDialog(this);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		this.pmLastFolderForOpenFile = fileChooser.getCurrentDirectory();

		FsmXml fsmXml = new FsmXml();
		List<Automata> automataList = null;
		try {
			automataList = fsmXml.read(fileChooser.getSelectedFile());

		} catch (Exception e) {
			//e.printStackTrace();
                        int returnValue_ = JOptionPane.showConfirmDialog(
						this,
						e.getMessage(),
						"Fail to open",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
		}
		if ((automataList != null) && (automataList.size() > 0)) {
			Automata automata = automataList.get(0);
//			this.pmAutomataType = new TAFKitInterface.AutomataType(automata);
//			this.updateAlgorithmMenuItems();
			this.createInternalFrame(automata, fileChooser.getSelectedFile().getName());
			JgraphXInternalFrame fr = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
			fr.setCurrentFile(fileChooser.getSelectedFile());
		}
	}//GEN-LAST:event_openMenuItemActionPerformed

    public void openFile(String filename){
//        System.out.println("open: "+filename+"...");
        File file=new File(filename);
        if(file.exists()){
            FsmXml fsmXml = new FsmXml();
            List<Automata> automataList = null;
            try {
                    automataList = fsmXml.read(file);

            } catch (Exception e) {
                    int returnValue_ = JOptionPane.showConfirmDialog(
						this,
						e.getMessage(),
						"Fail to open",
                                                JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
            }
            if ((automataList != null) && (automataList.size() > 0)) {
                    Automata automata = automataList.get(0);
//                    this.pmAutomataType = new TAFKitInterface.AutomataType(automata);
//                    this.updateAlgorithmMenuItems();
                    this.createInternalFrame(automata, file.getName());
                    //JgraphXInternalFrame fr = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
                    JgraphXInternalFrame fr = (JgraphXInternalFrame) mainDesktopPane.getComponent(0);
                    
                    if(fr!=null){ 
                        fr.setCurrentFile(file);
                        mainDesktopPane.setSelectedFrame(fr);
                        //System.out.println(fr.getTitle());
                    }else{
                        //System.out.println(mainDesktopPane.getComponentCount()+"frame empty!");
                        
                    }
            }       
        }else{
//            System.out.println(filename+" doesn't exist!!");
        }
        
        
    }
     
    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			((JgraphXInternalFrame) frame).getGraphComponent().zoomIn();
		}
    }//GEN-LAST:event_zoomInButtonActionPerformed

    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			((JgraphXInternalFrame) frame).getGraphComponent().zoomOut();
		}
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    private void fitWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitWindowButtonActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			mxGraphComponent graphComponent = ((JgraphXInternalFrame) frame).getGraphComponent();
			graphComponent.zoomActual();
			mxRectangle graphBounds = graphComponent.getGraph().getGraphBounds();
			Rectangle viewRect = graphComponent.getViewport().getVisibleRect();
			double xScale = viewRect.getWidth() * 0.95 / graphBounds.getWidth();
			double yScale = viewRect.getHeight() * 0.95 / graphBounds.getHeight();
                        if (xScale < yScale) {
				graphComponent.zoomTo(xScale, true);
			} else {
				graphComponent.zoomTo(yScale, true);
			}
		}  // End if (frame instanceof JgraphXInternalFrame)
    }//GEN-LAST:event_fitWindowButtonActionPerformed

    private void actualSizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualSizeButtonActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			((JgraphXInternalFrame) frame).getGraphComponent().zoomActual();
		}
    }//GEN-LAST:event_actualSizeButtonActionPerformed

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
		// TODO add your handling code here:
		((JgraphXInternalFrame) mainPanel.getComponent(0)).deleteSelectedCell();
    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void showPropertiesCheckBoxMenuItemStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_showPropertiesCheckBoxMenuItemStateChanged
		this.infoSplitPane.getTopComponent().
				setVisible(showPropertiesCheckBoxMenuItem.getState());
    }//GEN-LAST:event_showPropertiesCheckBoxMenuItemStateChanged

    private void showStatusBarCheckBoxMenuItemStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_showStatusBarCheckBoxMenuItemStateChanged
		// TODO add your handling code here:
		this.statusLabel.setVisible(showStatusBarCheckBoxMenuItem.getState());
    }//GEN-LAST:event_showStatusBarCheckBoxMenuItemStateChanged

    private void rationalExpressionSymbolsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rationalExpressionSymbolsMenuItemActionPerformed
		// TODO add your handling code here:
		JOptionPane.showMessageDialog(this, "Under Construction");
    }//GEN-LAST:event_rationalExpressionSymbolsMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
		// TODO add your handling code here:
		JOptionPane.showMessageDialog(this, "Under Construction");
    }//GEN-LAST:event_aboutMenuItemActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed

		//JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();

		//if (selected.currentFile == null/* newlyCreatedFile*/) {
		/*	this.saveAsMenuItemActionPerformed(evt);
		} else {
			FsmXml fsmXml = new FsmXml();
			try {
				ArrayList<Automata> currentAutomata = new ArrayList<Automata>();
				currentAutomata.add(selected.getAutomata());
				fsmXml.write(currentAutomata, selected.currentFile);
				selected.setModified(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
                save();

	}//GEN-LAST:event_saveMenuItemActionPerformed

        public void save(){
            JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();

		if (selected.getCurrentFile() == null/*
				 * newlyCreatedFile
				 */) {
			saveAs();
		} else {
			FsmXml fsmXml = new FsmXml();
			try {
				ArrayList<Automata> currentAutomata = new ArrayList<Automata>();
				currentAutomata.add(selected.getAutomata());
				fsmXml.write(currentAutomata, selected.getCurrentFile());
				selected.setModified(false);
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(
						this,
						e.getMessage(),
						"Fail to save",
                                                JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
			}
		}

        }
        
        public void saveAs(){
            
            JFileChooser fileChooser = new JFileChooser(this.pmLastFolderForSaveFile);
		fileChooser.setFileFilter(FsmXmlInterface.fileNameExtensionFilter);
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		this.pmLastFolderForSaveFile = fileChooser.getCurrentDirectory();
		File file = this.getFileTobeSavedWithExtensionAppended(fileChooser);

		FsmXml fsmXml = new FsmXml();
		try {

			JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
			ArrayList<Automata> currentAutomata = new ArrayList<Automata>();
			currentAutomata.add(selected.getAutomata());
			fsmXml.write(currentAutomata, file);

			selected.setCurrentFile(file);
			selected.setTitle(fileChooser.getSelectedFile().getName());
			selected.setModified(false);
		} catch (Exception e) {
			e.printStackTrace();
                        JOptionPane.showConfirmDialog(
						this,
						e.getMessage(),
						"Fail to save",
                                                JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
		}
            
            
        }
        
        
	private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed

                saveAs();
		/*JFileChooser fileChooser = new JFileChooser(this.pmLastFolderForSaveFile);
		fileChooser.setFileFilter(FsmXmlInterface.fileNameExtensionFilter);
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		this.pmLastFolderForSaveFile = fileChooser.getCurrentDirectory();
		File file = this.getFileTobeSavedWithExtensionAppended(fileChooser);

		FsmXml fsmXml = new FsmXml();
		try {

			JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
			ArrayList<Automata> currentAutomata = new ArrayList<Automata>();
			currentAutomata.add(selected.getAutomata());
			fsmXml.write(currentAutomata, file);

			selected.currentFile = file;
			selected.setTitle(fileChooser.getSelectedFile().getName());
			selected.setModified(false);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}//GEN-LAST:event_saveAsMenuItemActionPerformed

	private void circleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circleMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			((JgraphXInternalFrame) frame).doCircleLayout();
		}
	}//GEN-LAST:event_circleMenuItemActionPerformed

	private void hierarchicalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hierarchicalMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			((JgraphXInternalFrame) frame).doHierarchicalLayout();
		}
	}//GEN-LAST:event_hierarchicalMenuItemActionPerformed

	private void accessibleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accessibleMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			Automata inputAutomaton = ((JgraphXInternalFrame) frame).getAutomata();
			AutomataHistory history = new AutomataHistory();
			Automata outputAutomaton = Automata.accessible(inputAutomaton, history);
			outputAutomaton = Layout.keepTheSame(outputAutomaton, history);
			this.createInternalFrame(outputAutomaton, "Accessible of " + frame.getTitle());
		}
	}//GEN-LAST:event_accessibleMenuItemActionPerformed

	private void coaccessibleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coaccessibleMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			Automata inputAutomaton = ((JgraphXInternalFrame) frame).getAutomata();
			AutomataHistory history = new AutomataHistory();
			Automata outputAutomaton = Automata.coaccessible(inputAutomaton, history);
			outputAutomaton = Layout.keepTheSame(outputAutomaton, history);
			this.createInternalFrame(outputAutomaton, "Coccessible of " + frame.getTitle());
		}
	}//GEN-LAST:event_coaccessibleMenuItemActionPerformed

	private void removeEpsilonTransitionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEpsilonTransitionsMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			Automata inputAutomaton = ((JgraphXInternalFrame) frame).getAutomata();
			AutomataHistory history = new AutomataHistory();
			Automata outputAutomaton = Automata.removeEpsilonTransitions(inputAutomaton, history);
			outputAutomaton = Layout.keepTheSame(outputAutomaton, history);
			this.createInternalFrame(outputAutomaton, "Removed epsilon transitions of " + frame.getTitle());
		}
	}//GEN-LAST:event_removeEpsilonTransitionsMenuItemActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
		JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
		if (selected != null) {
			selected.redo();
		}
    }//GEN-LAST:event_redoButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
		JgraphXInternalFrame selected = (JgraphXInternalFrame) mainDesktopPane.getSelectedFrame();
		if (selected != null) {
			selected.undo();
		}
    }//GEN-LAST:event_undoButtonActionPerformed

	private void productMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (!(JgraphXInternalFrame.class.isInstance(frame))) {
			return;
		}
		Automata firstInput = ((JgraphXInternalFrame) frame).getAutomata();
		JFileChooser fileChooser = new JFileChooser(pmLastFolderForOpenFile);
		fileChooser.setDialogTitle("Please choose a FSM XML file");
		fileChooser.setFileFilter(FsmXmlInterface.fileNameExtensionFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int returnValue = fileChooser.showOpenDialog(pmVGI);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		pmLastFolderForOpenFile = fileChooser.getCurrentDirectory();
		File file = fileChooser.getSelectedFile();
		List<Automata> automataList = null;
		try {
			FsmXml fsmXml = new FsmXml();
			automataList = fsmXml.read(file);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(VGI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(VGI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FsmXmlException ex) {
			Logger.getLogger(VGI.class.getName()).log(Level.SEVERE, null, ex);
		}
		if ((automataList == null) || (automataList.isEmpty())) {
			return;
		}
		try {
			AutomataHistory history = new AutomataHistory();
			Automata outputAutomaton = Automata.product(firstInput, automataList.get(0), history);
			outputAutomaton = Layout.productGrid(outputAutomaton, history);
			this.createInternalFrame(outputAutomaton, "Product automaton of " + frame.getTitle() + " and " + file.getName());
		} catch (IllegalArgumentException illegalArgumentException) {
			JOptionPane.showMessageDialog(this, illegalArgumentException.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
		}
	}//GEN-LAST:event_productMenuItemActionPerformed

    private void featureMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_featureMenuItemActionPerformed
        // TODO add your handling code here:
        JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (JgraphXInternalFrame.class.isInstance(frame)) {
			((JgraphXInternalFrame) frame).doFeatureLayout();
		}
    }//GEN-LAST:event_featureMenuItemActionPerformed

	private void mergeSimilarTransitionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeSimilarTransitionsMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JInternalFrame) {
			Automata inputAutomaton = ((JgraphXInternalFrame) frame).getAutomata();
			AutomataHistory history = new AutomataHistory();
			Automata outputAutomaton = Automata.mergeSimilarTransitions(inputAutomaton, history);
			outputAutomaton = Layout.keepTheSame(outputAutomaton, history);
			this.createInternalFrame(outputAutomaton, frame.getTitle());
		}  // End if (frame instanceof JInternalFrame)
	}//GEN-LAST:event_mergeSimilarTransitionsMenuItemActionPerformed

	private void routeAllEdges2008MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeAllEdges2008MenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			((JgraphXInternalFrame) frame).routeAllEdges2008();
		}
	}//GEN-LAST:event_routeAllEdges2008MenuItemActionPerformed

	private void routeAllEdgesBranchingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeAllEdgesBranchingMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			((JgraphXInternalFrame) frame).routeAllEdgesBranching();
		}
	}//GEN-LAST:event_routeAllEdgesBranchingMenuItemActionPerformed

	private void planarizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planarizeMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			mxGraph graph = EdgeRoutingMinCross.planarize(((JgraphXInternalFrame) frame).graph);
			JgraphXInternalFrame newFrame = new JgraphXInternalFrame(
					this.infoSplitPane,
					graph,
					new Automata(),
					"Planarized " + frame.getTitle(),
					this);
			this.createInternalFrame(newFrame);
		}  // End if (frame instanceof JgraphXInternalFrame)
	}//GEN-LAST:event_planarizeMenuItemActionPerformed

	private void buildDualGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildDualGraphMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			mxGraph graph = EdgeRoutingMinCross.buildDualGraph(((JgraphXInternalFrame) frame).graph);
			JgraphXInternalFrame newFrame = new JgraphXInternalFrame(
					this.infoSplitPane,
					graph,
					new Automata(),
					"Dual Graph of " + frame.getTitle(),
					this);
			this.createInternalFrame(newFrame);
		}  // End if (frame instanceof JgraphXInternalFrame)
	}//GEN-LAST:event_buildDualGraphMenuItemActionPerformed

	private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame == null) {
			return;
		}
		frame.doDefaultCloseAction();
	}//GEN-LAST:event_closeMenuItemActionPerformed

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		WindowEvent windowClosing = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		this.dispatchEvent(windowClosing);
	}//GEN-LAST:event_exitMenuItemActionPerformed

	private void buildWeightedVisibilityGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildWeightedVisibilityGraphMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			mxGraph graph = ((JgraphXInternalFrame) frame).graph;
			WeightedVisibilityGraph weightedVisibilityGraph = new WeightedVisibilityGraph();
			Object parent = graph.getDefaultParent();
			Object objects[] = graph.getChildVertices(parent);
			for (Object object : objects) {
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("A vertex is not of the type mxICell.");
				}
				mxICell roadblock = (mxICell) object;
				weightedVisibilityGraph.addRoadblock(roadblock);
			}  // End for (Object object : objects)
			objects = graph.getChildEdges(parent);
			for (Object object : objects) {
				if (!(object instanceof mxICell)) {
					throw new IllegalStateException("An edge is not of the type mxICell.");
				}
				mxICell hindrance = (mxICell) object;
				weightedVisibilityGraph.addHindrance(hindrance);
			}  // End for (Object object : objects)
			graph = weightedVisibilityGraph.toMxGraph();
			JgraphXInternalFrame newFrame = new JgraphXInternalFrame(
					this.infoSplitPane,
					graph,
					new Automata(),
					"Weighted Visibility Graph of " + frame.getTitle(),
					this);
			this.createInternalFrame(newFrame);
		}  // End if (frame instanceof JgraphXInternalFrame)
	}//GEN-LAST:event_buildWeightedVisibilityGraphMenuItemActionPerformed

	private void testShortestPathsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testShortestPathsMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			EdgeRoutingMinCross.testShortestPaths(((JgraphXInternalFrame) frame).graph);
		}  // End if (frame instanceof JgraphXInternalFrame)
	}//GEN-LAST:event_testShortestPathsMenuItemActionPerformed

	private void singleVertexEdgesLayoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleVertexEdgesLayoutMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			SingleVertexEdgesLayout singleVertexEdgesLayout = new SingleVertexEdgesLayout(((JgraphXInternalFrame) frame).graph);
			singleVertexEdgesLayout.execute(null);
		}  // End if (frame instanceof JgraphXInternalFrame)
	}//GEN-LAST:event_singleVertexEdgesLayoutMenuItemActionPerformed

	private void showWeightedVisibilityGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showWeightedVisibilityGraphMenuItemActionPerformed
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (!(frame instanceof JgraphXInternalFrame)) {
			return;
		}
		if (((JgraphXInternalFrame) frame).visibilityGraph == null) {
			return;
		}
		mxGraph graph = ((JgraphXInternalFrame) frame).visibilityGraph.toMxGraph();
		if (graph == null) {
			return;
		}
		JgraphXInternalFrame newFrame = new JgraphXInternalFrame(
				this.infoSplitPane,
				graph,
				new Automata(),
				"Weighted Visibility Graph of " + frame.getTitle(),
				this);
		this.createInternalFrame(newFrame);
	}//GEN-LAST:event_showWeightedVisibilityGraphMenuItemActionPerformed

	private void algorithmsMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_algorithmsMenuMenuSelected
		this.updateAlgorithmMenuItems();
	}//GEN-LAST:event_algorithmsMenuMenuSelected

	private void edgeCostSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeCostSettingsMenuItemActionPerformed
		EdgeCostSettingsDialog edgeCostSettingsDialog = new EdgeCostSettingsDialog(this, true);
		edgeCostSettingsDialog.setVisible(true);
	}//GEN-LAST:event_edgeCostSettingsMenuItemActionPerformed

	void closeSelectedFrame() {
		this.closeMenuItemActionPerformed(null);
	}

	void exitProgram() {
		this.exitMenuItemActionPerformed(null);
	}

	JgraphXInternalFrame getSelectedFrame() {
		JInternalFrame frame = this.mainDesktopPane.getSelectedFrame();
		if (frame instanceof JgraphXInternalFrame) {
			return (JgraphXInternalFrame) frame;
		}
		return null;
	}  // End JgraphXInternalFrame getSelectedFrame()

	//private static File getFileTobeSavedWithExtensionAppended(JFileChooser fileChooser) {
        public static File getFileTobeSavedWithExtensionAppended(JFileChooser fileChooser) {

		if (fileChooser.getDialogType() != JFileChooser.SAVE_DIALOG) {
			throw new RuntimeException("Input of getFileTobeSavedWithExtensionAppended() should be a JFileChooser used to show a \"Save File\" dialog.");
		}

		FileFilter fileFilter = fileChooser.getFileFilter();
		File file = fileChooser.getSelectedFile();
		if ((fileFilter.accept(file))
				|| (!(FileNameExtensionFilter.class.isAssignableFrom(fileFilter.getClass())))) {
			return file;
		}

		FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter) fileFilter;

		return new File(file.getAbsolutePath() + "." + (fileNameExtensionFilter.getExtensions())[0]);

	}  // End private static File getFileTobeSavedWithExtensionAppended(JFileChooser fileChooser)

	protected void restoreDefaultAlgorithmMenuItems() {
		int count = this.algorithmsMenu.getMenuComponentCount();
		if (count <= 0) {
			return;
		}
		Component component = this.algorithmsMenu.getMenuComponent(count - 1);
		if (component != this.algorithmsMenuSeparator2) {
			this.algorithmsMenu.removeAll();
			this.algorithmsMenu.add(this.setTAFKitPathMenuItem);
			this.algorithmsMenu.add(this.currentSettingMenuItem);
			this.algorithmsMenu.add(this.algorithmsMenuSeparator1);
			this.algorithmsMenu.add(this.mergeSimilarTransitionsMenuItem);
			this.algorithmsMenu.add(this.accessibleMenuItem);
			this.algorithmsMenu.add(this.coaccessibleMenuItem);
			this.algorithmsMenu.add(this.removeEpsilonTransitionsMenuItem);
			this.algorithmsMenu.add(this.productMenuItem);
			this.algorithmsMenu.add(this.algorithmsMenuSeparator2);
		}  // End if (component != this.algorithmsMenuSeparator2)
	}  // End protected void restoreDefaultAlgorithmMenuItems()

	protected void updateAlgorithmMenuItems() {

		JInternalFrame tempFrame = this.mainDesktopPane.getSelectedFrame();
		if (tempFrame == null) {
			this.restoreDefaultAlgorithmMenuItems();
			this.pmAutomataType = null;
			return;
		}
		if (!(tempFrame instanceof JgraphXInternalFrame)) {
			return;
		}
		JgraphXInternalFrame frame = (JgraphXInternalFrame) tempFrame;
		if (frame.automata == null) {
			return;
		}
		TAFKitInterface.AutomataType automataType = new TAFKitInterface.AutomataType(frame.automata);
		if (automataType.equals(this.pmAutomataType)) {
			return;
		}
		if ((this.pmTAFKit == null)) {
			return;
		}

		List<TAFKitInterface.VcsnAlgorithm> vcsnAlgorithms = null;

		try {
			vcsnAlgorithms = pmTAFKit.listVcsnAlgorithms(automataType);
		} catch (FileNotFoundException fileNotFoundException) {
			JOptionPane.showMessageDialog(
					this,
					"TAF-Kit currently does not support this type of automata, \"" + automataType.toExecutableFileName() + "\".",
					null,
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (TAFKitInterface.TAFKitException tafKitException) {
			JOptionPane.showMessageDialog(
					this,
					tafKitException.getMessage(),
					null,
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		this.restoreDefaultAlgorithmMenuItems();
		JMenu submenu = null;

		for (int index = 0; index < vcsnAlgorithms.size(); index++) {
			TAFKitInterface.VcsnAlgorithm vcsnAlgorithm = vcsnAlgorithms.get(index);
			JMenuItem menuItem;
			if (vcsnAlgorithm.name.equalsIgnoreCase("category")) {
				submenu = new JMenu(vcsnAlgorithm.description);
				this.algorithmsMenu.add(submenu);
			} else {
				menuItem = new JMenuItem(vcsnAlgorithm.name);
				AlgorithmMenuItemActionListener algorithmMenuItemActionListener = new AlgorithmMenuItemActionListener(vcsnAlgorithm);
				menuItem.addActionListener(algorithmMenuItemActionListener);
				submenu.add(menuItem);
			}
		}  // End for (int index = 0; index < vcsnAlgorithms.size(); index ++)

		this.pmAutomataType = automataType;
	}  // End protected void updateAlgorithmMenuItems()

        public void showErrorDialog(String errorString){
            
            JOptionPane.showMessageDialog(
			this,
                        errorString,
                        null,
			JOptionPane.WARNING_MESSAGE);
        }
	/**
	 * @param args the command line arguments
	 */
	public static void main(final String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/*
		 * Create and display the form
		 */
               // FsmXml fsmXml = new FsmXml();
               // fsmXml.read(System.in);
                //final String[] filename=args;
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
                                VGI vgi=new VGI();
                                boolean standardInputError=false;
                                String errorString=null;
                                    
                                if(args.length>0){
                                    String filename=args[0];
                                    if(filename!=null){
                                        if(filename.charAt(0)=='-'){
                                            FsmXml fsmXml=new FsmXml();
                                            List<Automata> automataList = null;
           
                                            try{
                                                        automataList=fsmXml.read(System.in);
                                                        //System.out.println(System.in.toString());
                                                    
                                            }catch(FsmXmlException ex){
                                                Logger.getLogger(VGI.class.getName()).log(Level.SEVERE, null, ex);
                                                standardInputError=true;
                                                errorString=ex.getMessage();
                                            }
                                            
                                            if ((automataList != null) && (automataList.size() > 0)) {
       
                                                Automata automata = automataList.get(0);
//                                                vgi.pmAutomataType = new TAFKitInterface.AutomataType(automata);
//                                                vgi.updateAlgorithmMenuItems();
                                                vgi.createInternalFrame(automata, "");
                                            }
                                            
                                        }else{
                                            vgi.openFile(filename);
                                        }
                                    }
                                }
				vgi.setVisible(true);
                                if(standardInputError) vgi.showErrorDialog(errorString);
                                
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem accessibleMenuItem;
    private javax.swing.JButton actualSizeButton;
    private javax.swing.JMenuItem actualSizeMenuItem;
    private javax.swing.JMenu algorithmsMenu;
    private javax.swing.JPopupMenu.Separator algorithmsMenuSeparator1;
    private javax.swing.JPopupMenu.Separator algorithmsMenuSeparator2;
    private javax.swing.JMenuItem buildDualGraphMenuItem;
    private javax.swing.JMenuItem buildWeightedVisibilityGraphMenuItem;
    private javax.swing.JMenuItem circleMenuItem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem coaccessibleMenuItem;
    private javax.swing.JMenuItem currentSettingMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem edgeCostSettingsMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JPopupMenu.Separator editMenuSeparator1;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem featureMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPopupMenu.Separator fileMenuSeparator1;
    private javax.swing.JButton fitWindowButton;
    private javax.swing.JMenuItem fitWindowMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem hierarchicalMenuItem;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JSplitPane infoSplitPane;
    private javax.swing.JMenu layoutMenu;
    private javax.swing.JDesktopPane mainDesktopPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mergeSimilarTransitionsMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JPanel outlinePanel;
    private javax.swing.JMenuItem planarizeMenuItem;
    private javax.swing.JMenuItem productMenuItem;
    private javax.swing.JMenuItem rationalExpressionSymbolsMenuItem;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenuItem removeEpsilonTransitionsMenuItem;
    private javax.swing.JMenuItem routeAllEdges2008MenuItem;
    private javax.swing.JMenuItem routeAllEdgesBranchingMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem setAsFinalStateMenuItem;
    private javax.swing.JMenuItem setAsInitialStateMenuItem;
    private javax.swing.JMenuItem setTAFKitPathMenuItem;
    private javax.swing.JCheckBoxMenuItem showPropertiesCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem showStatusBarCheckBoxMenuItem;
    private javax.swing.JMenuItem showWeightedVisibilityGraphMenuItem;
    private javax.swing.JMenuItem singleVertexEdgesLayoutMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JMenuItem testShortestPathsMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToolBar.Separator toolBarSeparator2;
    private javax.swing.JButton undoButton;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPopupMenu.Separator viewMenuSeparator1;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JMenuItem zoomInMenuItem;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JMenuItem zoomOutMenuItem;
    // End of variables declaration//GEN-END:variables
}
