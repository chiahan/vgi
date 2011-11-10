//The icons used in this project are made by Freeiconsdownload from http://www.freeiconsdownload.com
package storage;

import event.UIEvent;
import event.UserEventConstants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * create the menu items
 * @param menuBar, the menu bar
 * @author jllu
 */
public class MenuBar {

    protected JMenuBar menuBar;

    /**
     * @return the main menu
     * @author jllu
     */
    public JMenuBar createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createLayoutMenu());
        menuBar.add(createAlgorithMenu());
        menuBar.add(createHelpMenu());

        return menuBar;
    }

    /**
     * @return the help menu
     * @author jllu
     */
    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Help");

        JMenuItem helpItem = new JMenuItem("Rational Expression Symbols");
        helpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/question.png")));
        helpItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String message = "\nSum = \" + \"\n"
                        + "Star = \" * \"\n"
                        + "Multiply = \" & \"\n"
                        + "Open Parentheses = \" ( \"\n"
                        + "Close Parentheses = \" ) \"\n"
                        + "Open Weight = \" { \"\n"
                        + "Close Weight = \" } \"\n"
                        + "ZeroSymbol = \" 0 \"\n"
                        + "OneSymbol = \" 1 \"\n"
                        + "Space = \"  \"\n\n";
                JOptionPane.showMessageDialog(null, message);
            }
        });
        menu.add(helpItem);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/about.png")));
        aboutItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String message = "using Vaucanson 1.2.93 library\n"
                        + "the icons used in this program are made by \n"
                        + "Freeiconsdownload from http://www.freeiconsdownload.com";
                JOptionPane.showMessageDialog(null, message);
            }
        });
        menu.add(aboutItem);

        return menu;
    }

    /**
     * @return the layout menu
     * @author jllu
     */
    private JMenu createLayoutMenu() {
        JMenu menu = new JMenu("Layout");
        menu.setMnemonic(KeyEvent.VK_L);

//		menu.add(createSimpleEventMenuItem("Apply Layered Layout", UserEventConstants.HIERARCHICAL_LAYOUT));
        menu.add(createSimpleEventMenuItem("Apply Layered Layout", UserEventConstants.HIERARCHICAL_LAYOUT2));
//		menu.add(createSimpleEventMenuItem("Apply Fast Organic", UserEventConstants.FAST_ORGANIC));
//		menu.add(createSimpleEventMenuItem("Apply Organic", UserEventConstants.ORGANIC));
//		menu.add(createSimpleEventMenuItem("Apply Self-Organizing", UserEventConstants.SELF_ORGANIZING));
        menu.add(createSimpleEventMenuItem("Apply Spring Layout", UserEventConstants.SPRING_LAYOUT));
        menu.add(createSimpleEventMenuItem("Apply Circle Layout", UserEventConstants.CIRCLE_LAYOUT));
//		menu.add(createSimpleEventMenuItem("Apply Circle Layout (w/o any processing)", UserEventConstants.CIRCLE_LAYOUT_WO_ALG));
        menu.add(createSimpleEventMenuItem("Apply Grid Layout", UserEventConstants.GRID_LAYOUT));
        //menu.add(createSimpleEventMenuItem("Apply Line Layout", UserEventConstants.LINE_LAYOUT));
//        menu.add(createSimpleEventMenuItem("Apply Layered Layout (Broken) !!", UserEventConstants.LAYERED_LAYOUT));
//        menu.add(createSimpleEventMenuItem("Apply Layered Layout with Leveling", UserEventConstants.LAYERED_LATOUT_WITH_LEVELING));
//        menu.add(createSimpleEventMenuItem("Apply GA Layered Layout", UserEventConstants.GA_LAYERED_DRAWING_LAYOUT));

        return menu;
    }

    /**
     * @return the file menu
     * @author jllu
     */
    private JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newFile = new JMenuItem("New");
        newFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/Forward.png")));
        newFile.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileNew();
            }
        });
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menu.add(newFile);

        JMenuItem openFile = new JMenuItem("Open...");
        openFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/open.png")));
        openFile.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileOpen();
            }
        });
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menu.add(openFile);

        JMenuItem closeFile = new JMenuItem("Close");
        closeFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/stop.png")));
        closeFile.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileCloseDialog(Storage.getInstance().getSelectedGraphIndex());
            }
        });
        menu.add(closeFile);

        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileSave(Storage.getInstance().getSelectedGraphIndex());
            }
        });
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menu.add(saveFile);

        JMenuItem saveFileAs = new JMenuItem("Save as...");
        saveFileAs.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileSaveAs(Storage.getInstance().getSelectedGraphIndex());
            }
        });
        saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        menu.add(saveFileAs);

        menu.add(new JSeparator());

        JMenuItem progExit = new JMenuItem("Exit");
        progExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/door.png")));
        progExit.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Storage.getInstance().fileExit();
            }
        });
        menu.add(progExit);
//
//		menu.add(new JSeparator());
//
//		JMenuItem test = new JMenuItem("test");
//		test.addActionListener(new java.awt.event.ActionListener() {
//
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				Storage storage = Storage.getInstance();
//				UIEvent event = new UIEvent(UserEventConstants.TEST_FUNCTION);
//				storage.eventHappen(event);
//			}
//		});
//		menu.add(test);

        return menu;
    }

    /**
     * @return the edit menu
     * @author jllu
     */
    private JMenu createEditMenu() {
        JMenu menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);

//		JMenuItem undo = createSimpleEventMenuItem("Undo", UserEventConstants.UNDO);
//		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
//		menu.add(undo);
//
//		JMenuItem redo = createSimpleEventMenuItem("Redo", UserEventConstants.UNDO);
//		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
//		menu.add(redo);

        JMenuItem delete = createSimpleEventMenuItem("Delete", UserEventConstants.DELETE_BUTTON);
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/close.png")));
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menu.add(delete);

        menu.addSeparator();

        JMenuItem setInitial = createSimpleEventMenuItem("Set as Initial State", UserEventConstants.SET_INITIAL_STATE);
        setInitial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_07.png")));
        menu.add(setInitial);

        JMenuItem setFinal = createSimpleEventMenuItem("Set as Final State", UserEventConstants.SET_FINAL_STATE);
        setFinal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_06.png")));
        menu.add(setFinal);

        menu.addSeparator();
        menu.add(createSimpleEventMenuItem("spring layout parameters", UserEventConstants.SPRING_LAYOUT_PARAMETERS));

//		item = new JMenuItem("Set Automaton Properties");
//		item.addActionListener(this); else if (orig.equals("Set Automaton Properties")) {
//			SetAutomaton setAutomaton = new SetAutomaton();
//			setAutomaton.Start();
//		}

        return menu;
    }

    /**
     * @return the view menu
     * @author jllu
     */
    private JMenu createViewMenu() {
        JMenu menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);

//		JMenuItem item = new JMenuItem("Refresh view");
//		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.SHIFT_MASK));
//		item.addActionListener(this);
//		menu.add(item);
//
//		item = new JMenuItem("Center Graph");
//		item.addActionListener(this);
//		menu.add(item);

        JMenuItem actualSize = createSimpleEventMenuItem("Actual Size", UserEventConstants.ACTUAL_SIZE);
        actualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoom.png")));
        actualSize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menu.add(actualSize);

        JMenuItem fitWindow = createSimpleEventMenuItem("Fit Window", UserEventConstants.FIT_WINDOW);
        fitWindow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/refresh.png")));
        fitWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menu.add(fitWindow);

        JMenuItem zoomIn = createSimpleEventMenuItem("Zoom in", UserEventConstants.ZOOM_IN);
        zoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoomin.png")));
        zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        menu.add(zoomIn);

        JMenuItem zoomOut = createSimpleEventMenuItem("Zoom out", UserEventConstants.ZOOM_OUT);
        zoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoomout.png")));
        zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        menu.add(zoomOut);

        menu.addSeparator();

        JCheckBoxMenuItem displayAutProp = new JCheckBoxMenuItem("Display Automata Properties");
        displayAutProp.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) evt.getSource();
                if (item.isSelected()) {
                    Storage.getInstance().addPropertiesView("automata");
                } else {
                    Storage.getInstance().removePropertiesView("automata");
                }
            }
        });
        displayAutProp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        menu.add(displayAutProp);

//		item = new JMenuItem("View State Properties");
//		item.addActionListener(this);
//		menubarList_.put("state", item);
        //menu.add(item);

//		item = new JMenuItem("View Transition Properties");
//		item.addActionListener(this);
//		menubarList_.put("transition", item);
        //menu.add(item);
// else if (orig.equals("View State Properties")) {
//			JMenuItem item = menubarList_.get("state");
//			if (item.getIcon() == null) {
//				storage.addPropertiesView("state");
//				item.setIcon(markImageIcon_);
//			} else {
//				storage.removePropertiesView("state");
//			}
//		} else if (orig.equals("View Transition Properties")) {
//			JMenuItem item = menubarList_.get("transition");
//			if (item.getIcon() == null) {
//				storage.addPropertiesView("transition");
//				item.setIcon(markImageIcon_);
//			} else {
//				storage.removePropertiesView("transition");
//			}
//		}
        menu.addSeparator();

        JCheckBoxMenuItem displayStatusBar = new JCheckBoxMenuItem("Display Status Bar");
        displayStatusBar.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) evt.getSource();
                if (item.isSelected()) {
                    Storage.getInstance().addStatusBarView();
                } else {
                    Storage.getInstance().removeStatusBarView();
                }
            }
        });
        displayStatusBar.setSelected(true);
        menu.add(displayStatusBar);

        return menu;
    }

    /**
     * @return the algorithm menu
     * @author jllu
     */
    private JMenu createAlgorithMenu() {

        JMenu menu = new JMenu("Algorithms");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.add(createBSubMenu());
        menu.add(createWSubMenu());
        menu.add(createTSubMenu());

        return menu;
    }

    /**
     * creates a simple menu button
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @param enabled : button can not be used if false
     * @return the menu button
     * @author polar
     */
    private JMenuItem createSimpleEventMenuItem(String name, final UserEventConstants eventType, boolean enabled) {
        JMenuItem menuItem = createSimpleEventMenuItem(name, eventType);
        if (enabled == false) {
            menuItem.setForeground(Color.GRAY);
            menuItem.setEnabled(false);
        }
        return menuItem;
    }

    /**
     * creates a simple menu button
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @return the menu button
     * @author polar
     */
    private JMenuItem createSimpleEventMenuItem(String name, final UserEventConstants eventType) {

        JMenuItem newItem = new JMenuItem(name);


        newItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UIEvent uievent = new UIEvent(eventType);
                Storage.getInstance().eventHappen(uievent);
            }
        });
        return newItem;
    }

    /**
     * creates a menu button that also asks user to select file when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @param enabled : button can not be used if false
     * @return the menu button
     * @author polar
     */
    private JMenuItem createFileDialogEventMenuItem(String name, final UserEventConstants eventType, boolean enabled) {
        JMenuItem newItem = createFileDialogEventMenuItem(name, eventType);
        if (enabled == false) {
            newItem.setForeground(Color.GRAY);
            newItem.setEnabled(false);
        }
        return newItem;
    }

    /**
     * creates a menu button that also asks user to select file when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @return the menu button
     * @author polar
     */
    private JMenuItem createFileDialogEventMenuItem(String name, final UserEventConstants eventType) {

        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Please select the second automata");
                int modalResult = fc.showOpenDialog(null);
                if (modalResult == 0) {
                    String selectedFile = fc.getSelectedFile().toString();
                    System.out.println("selected file: " + selectedFile);
                    UIEvent uievent = new UIEvent(eventType);
                    uievent.setsData(selectedFile);
                    Storage.getInstance().eventHappen(uievent);
                } else {
                    System.out.println("file selection cancled");
                }
            }
        });
        return newItem;
    }

    /**
     * creates a menu button that also asks user for integer input when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @return the menu button
     * @author polar
     */
    private JMenuItem createIntInputDialogEventMenuItem(String name, final UserEventConstants eventType) {

        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    String input = JOptionPane.showInputDialog(null, "please enter degree of power");
                    int degree = Integer.parseInt(input);
                    Storage storage = Storage.getInstance();
                    UIEvent uievent = new UIEvent(eventType);
                    uievent.setiData(degree);
                    storage.eventHappen(uievent);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "invalid input", "error", JOptionPane.WARNING_MESSAGE);
                    System.out.println(e.getMessage());
                }
            }
        });
        return newItem;
    }

    /**
     * creates a menu button that also asks user for string input when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @param enabled : button can not be used if false
     * @return the menu button
     * @author polar
     */
    private JMenuItem createStringInputDialogEventMenuItem(String name, final UserEventConstants eventType, boolean enabled) {
        JMenuItem newItem = createStringInputDialogEventMenuItem(name, eventType);
        if (enabled == false) {
            newItem.setForeground(Color.GRAY);
            newItem.setEnabled(false);
        }
        return newItem;
    }

    /**
     * creates a menu button that also asks user for string input when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @return the menu button
     * @author polar
     */
    private JMenuItem createStringInputDialogEventMenuItem(String name, final UserEventConstants eventType) {

        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    String userInput = JOptionPane.showInputDialog(null, "Please enter the input");
                    UIEvent uievent = new UIEvent(eventType);
                    uievent.setsData(userInput);
                    Storage.getInstance().eventHappen(uievent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "invalid input", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        return newItem;
    }

    /**
     * creates a menu button that also asks user for two string inputs when clicked
     * @param name : displayed name of button
     * @param eventType : action performed by button
     * @return the menu button
     * @author polar
     */
    private JMenuItem createTwoStringInputDialogEventMenuItem(String name, final UserEventConstants eventType) {

        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    String userInput1 = JOptionPane.showInputDialog(null, "Please enter the alphabet");
                    String userInput2 = JOptionPane.showInputDialog(null, "Please enter the regular expression");
                    UIEvent uievent = new UIEvent(eventType);
                    ArrayList<String> userInput = new ArrayList<String>();
                    userInput.add(userInput1);
                    userInput.add(userInput2);
                    uievent.setaData(userInput);
                    Storage.getInstance().eventHappen(uievent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "invalid input", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        return newItem;
    }

    /**
     * create Boolean automaton menu
     * @author jllu
     */
    private JMenu createBSubMenu() {

        JMenu menu = new JMenu("Boolean automaton");

        //** test and evaluation on automata

        menu.add(createFileDialogEventMenuItem("are equivalent", UserEventConstants.B_ARE_EQUIVALENT));
        menu.add(createStringInputDialogEventMenuItem("eval", UserEventConstants.B_EVAL));
        menu.add(createSimpleEventMenuItem("is ambiquous", UserEventConstants.B_IS_AMBIGUOUS));
        menu.add(createSimpleEventMenuItem("is complete", UserEventConstants.B_IS_COMPLETE));
        menu.add(createSimpleEventMenuItem("is deterministic", UserEventConstants.B_IS_DETERMINISTIC));
        menu.add(createSimpleEventMenuItem("is empty", UserEventConstants.B_IS_EMPTY));
        menu.add(createSimpleEventMenuItem("has succ comp", UserEventConstants.B_HAS_SUCC_COMP));
        menu.add(createSimpleEventMenuItem("is realtime", UserEventConstants.B_IS_REALTIME));
        menu.add(createSimpleEventMenuItem("is standard", UserEventConstants.B_IS_STANDARD));

        menu.add(new JSeparator());

        //** generic algorithms for automata

        menu.add(createSimpleEventMenuItem("accessible", UserEventConstants.B_ACCESSIBLE));
        menu.add(createSimpleEventMenuItem("eps removal", UserEventConstants.B_EPS_REMOVAL));
        menu.add(createSimpleEventMenuItem("eps removal-sp", UserEventConstants.B_EPS_REMOVAL_SP));
        menu.add(createSimpleEventMenuItem("co-accessible", UserEventConstants.B_CO_ACCESSIBLE));
        menu.add(createSimpleEventMenuItem("complete", UserEventConstants.B_COMPLETE));
		// BEGIN
		// Remove obsolete algorithm.  Use "concat of standard" instead of "concatenate".
//        menu.add(createFileDialogEventMenuItem("concatenate", UserEventConstants.B_CONCATENATE));
		// Remove obsolete algorithm.  Use "concat of standard" instead of "concatenate".
		// END
        menu.add(createIntInputDialogEventMenuItem("power", UserEventConstants.B_POWER));
        menu.add(createFileDialogEventMenuItem("product", UserEventConstants.B_PRODUCT));
        menu.add(createSimpleEventMenuItem("quotient", UserEventConstants.B_QUOTIENT));
        menu.add(createSimpleEventMenuItem("realtime", UserEventConstants.B_REALTIME));
        menu.add(createSimpleEventMenuItem("standardize", UserEventConstants.B_STANDARDIZE));
		// BEGIN
		// Remove obsolete algorithm.  Use "union" instead of "union of standard".
//        menu.add(createFileDialogEventMenuItem("union of standard", UserEventConstants.B_UNION_OF_STANDARD));
		// Remove obsolete algorithm.  Use "union" instead of "union of standard".
		// END
		// BEGIN
		// Fix incorrect B_CONCAT_OF_STANDARD behavior (which soon crashes VGI)
        menu.add(createFileDialogEventMenuItem("concat of standard", UserEventConstants.B_CONCAT_OF_STANDARD));
		// Fix incorrect B_CONCAT_OF_STANDARD behavior (which soon crashes VGI)
		// END
        menu.add(createSimpleEventMenuItem("star of standard", UserEventConstants.B_STAR_OF_STANDARD));
        menu.add(createFileDialogEventMenuItem("union", UserEventConstants.B_UNION));
        menu.add(createSimpleEventMenuItem("transpose", UserEventConstants.B_TRANSPOSE));
        menu.add(createSimpleEventMenuItem("trim", UserEventConstants.B_TRIM));

        menu.add(new JSeparator());

        //boolean automaton specific algorithms

        menu.add(createSimpleEventMenuItem("complement", UserEventConstants.B_COMPLEMENT));
        menu.add(createSimpleEventMenuItem("determinize", UserEventConstants.B_DETERMINIZE));
        menu.add(createSimpleEventMenuItem("minimize", UserEventConstants.B_MINIMIZE));
        menu.add(createSimpleEventMenuItem("minimize-moore", UserEventConstants.B_MINIMIZE_MOORE));

        menu.add(new JSeparator());

        //** conversion between automata and expressions

        menu.add(createSimpleEventMenuItem("aut to exp", UserEventConstants.B_AUT_TO_EXP));
        menu.add(createTwoStringInputDialogEventMenuItem("derived term", UserEventConstants.B_DERIVED_TERM));
        menu.add(createTwoStringInputDialogEventMenuItem("exp to aut", UserEventConstants.B_EXP_TO_AUT));
        menu.add(createTwoStringInputDialogEventMenuItem("expand", UserEventConstants.B_EXPAND));
        menu.add(createTwoStringInputDialogEventMenuItem("identity exp", UserEventConstants.B_IDENTITY_EXP));
        menu.add(createTwoStringInputDialogEventMenuItem("standard", UserEventConstants.B_STANDARD));
        menu.add(createTwoStringInputDialogEventMenuItem("thompson", UserEventConstants.B_THOMPSON));

        return menu;
    }

    /**
     * create weighted automata menu
     * @author jllu
     */
    private JMenu createWSubMenu() {
        JMenu menu = new JMenu("weighted automata");

        //test and evaluation on automata

        menu.add(createStringInputDialogEventMenuItem("eval", UserEventConstants.W_EVAL));
        menu.add(createSimpleEventMenuItem("is ambiguous", UserEventConstants.W_IS_AMBIGUOUS));
        menu.add(createSimpleEventMenuItem("is complete", UserEventConstants.W_IS_COMPLETE));
        menu.add(createSimpleEventMenuItem("is empty", UserEventConstants.W_IS_EMPTY));
        menu.add(createSimpleEventMenuItem("has succ comp", UserEventConstants.W_HAS_SUCC_COMP));
        menu.add(createSimpleEventMenuItem("is realtime", UserEventConstants.W_IS_REALTIME));
        menu.add(createSimpleEventMenuItem("is standard", UserEventConstants.W_IS_STANDARD));

        menu.add(new JSeparator());

        //** generic algorithms for automata

        menu.add(createSimpleEventMenuItem("accessible", UserEventConstants.W_ACCESSIBLE));
        menu.add(createSimpleEventMenuItem("eps removal", UserEventConstants.W_EPS_REMOVAL));
        menu.add(createSimpleEventMenuItem("eps removal-sp", UserEventConstants.W_EPS_REMOVAL_SP));
        menu.add(createSimpleEventMenuItem("co-accessible", UserEventConstants.W_CO_ACCESSIBLE));
        menu.add(createSimpleEventMenuItem("complete", UserEventConstants.W_COMPLETE));
		// BEGIN
		// Remove obsolete algorithm.  Use "concat of standard" instead of "concatenate".
//        menu.add(createSimpleEventMenuItem("concatenate", UserEventConstants.W_CONCATENATE));
		// Remove obsolete algorithm.  Use "concat of standard" instead of "concatenate".
		// END
        menu.add(createIntInputDialogEventMenuItem("power", UserEventConstants.W_POWER));
        menu.add(createFileDialogEventMenuItem("product", UserEventConstants.W_PRODUCT));
        menu.add(createSimpleEventMenuItem("quotient", UserEventConstants.W_QUOTIENT));
        menu.add(createSimpleEventMenuItem("realtime", UserEventConstants.W_REALTIME));
        menu.add(createSimpleEventMenuItem("standardize", UserEventConstants.W_STANDARDIZE));
		// BEGIN
		// Remove obsolete algorithm.  Use "union" instead of "union of standard".
//        menu.add(createFileDialogEventMenuItem("union of standasrd", UserEventConstants.W_UNION_OF_STANDARD));
		// Remove obsolete algorithm.  Use "union" instead of "union of standard".
		// END
        menu.add(createFileDialogEventMenuItem("concat of standard", UserEventConstants.W_CONCAT_OF_STANDARD));
        menu.add(createSimpleEventMenuItem("star of standard", UserEventConstants.W_STAR_OF_STANDARD));
        menu.add(createFileDialogEventMenuItem("union", UserEventConstants.W_UNION));
        menu.add(createSimpleEventMenuItem("transpose", UserEventConstants.W_TRANSPOSE));
        menu.add(createSimpleEventMenuItem("trim", UserEventConstants.W_TRIM));

        menu.add(new JSeparator());

        //** conversion between automata and expressions

        menu.add(createSimpleEventMenuItem("auto to exp", UserEventConstants.W_AUT_TO_EXP));
        menu.add(createTwoStringInputDialogEventMenuItem("derived term", UserEventConstants.W_DERIVED_TERM));
        menu.add(createTwoStringInputDialogEventMenuItem("exp to aut", UserEventConstants.W_EXP_TO_AUT));
        menu.add(createTwoStringInputDialogEventMenuItem("expand", UserEventConstants.W_EXPAND));
        menu.add(createTwoStringInputDialogEventMenuItem("identity exp", UserEventConstants.W_IDENTITY_EXP));
        menu.add(createTwoStringInputDialogEventMenuItem("standard", UserEventConstants.W_STANDARD));
        menu.add(createTwoStringInputDialogEventMenuItem("thompson", UserEventConstants.W_THOMPSON));

        return menu;
    }

    /**
     * create FMP Transducer menu
     * @author jllu
     */
    private JMenu createTSubMenu() {
        JMenu menu = new JMenu("FMP Transducer");

        //** tests and evaluation on tranducers

        menu.add(createSimpleEventMenuItem("is empty", UserEventConstants.T_IS_EMPTY));
        menu.add(createSimpleEventMenuItem("has succ comp", UserEventConstants.T_HAS_SUCC_COMP));
        menu.add(createSimpleEventMenuItem("is sub-normalized", UserEventConstants.T_IS_SUB_NORMALIZED));

        menu.add(new JSeparator());

        //** generic algorithm for transducers

        menu.add(createSimpleEventMenuItem("eps removal", UserEventConstants.T_EPS_REMOVAL));
        menu.add(createSimpleEventMenuItem("eps removal-sp", UserEventConstants.T_EPS_REMOVAL_SP));
        menu.add(createSimpleEventMenuItem("domain", UserEventConstants.T_DOMAIN));
        menu.add(createStringInputDialogEventMenuItem("eval", UserEventConstants.T_EVAL));
        menu.add(createFileDialogEventMenuItem("eval aut", UserEventConstants.T_EVAL_AUT));
        menu.add(createSimpleEventMenuItem("ltl to pair", UserEventConstants.T_LTL_TO_PAIR));
        menu.add(createSimpleEventMenuItem("image", UserEventConstants.T_IMAGE));
        menu.add(createSimpleEventMenuItem("transpose", UserEventConstants.T_TRANSPOSE));
        menu.add(createSimpleEventMenuItem("trim", UserEventConstants.T_TRIM));

        menu.add(new JSeparator());

        //** algorithms for transducers

        menu.add(createSimpleEventMenuItem("sub-normalize", UserEventConstants.T_SUB_NORMALIZE));
        menu.add(createSimpleEventMenuItem("composition cover", UserEventConstants.T_COMPOSITION_COVER));
        menu.add(createSimpleEventMenuItem("composition co-cover", UserEventConstants.T_COMPOSITION_CO_COVER));
        menu.add(createFileDialogEventMenuItem("compose", UserEventConstants.T_COMPOSE));
        menu.add(createFileDialogEventMenuItem("u-compose", UserEventConstants.T_U_COMPOSE));
//		menu.add(createFileDialogEventMenuItem("to rw", UserEventConstants.T_TO_RW));
        menu.add(createSimpleEventMenuItem("invert", UserEventConstants.T_INVERT));
        menu.add(createSimpleEventMenuItem("intersection", UserEventConstants.T_INTERSECTION));

        return menu;
    }

    /**
     * to enable or disable a items based on state
     * @param state, the description of state
     * @author jllu
     */
    public void ItemsEnableSetting(String state) {
        if (state.equals("no file")) {
            for (int i = 0; i < this.menuBar.getComponentCount() - 1; i++) {
                this.menuBar.getComponent(i).setEnabled(false);
            }
            JMenu menu = (JMenu) this.menuBar.getComponent(0);
            menu.setEnabled(true);
            for (int i = 2; i < 5; i++) {
                menu.getItem(i).setEnabled(false);
            }
        } else if (state == "has file") {
            for (int i = 0; i < this.menuBar.getComponentCount() - 1; i++) {
                this.menuBar.getComponent(i).setEnabled(true);
            }
            JMenu menu = (JMenu) this.menuBar.getComponent(0);
            for (int i = 2; i < 5; i++) {
                menu.getItem(i).setEnabled(true);
            }
        }
    }

	// BEGIN
	// Automatically update Algorithms menu items depending on current automata type.
    public void UpdateAlgorithmsMenuItems (int subitem) {
		JMenu menu = (JMenu) this.menuBar.getComponent(4);
		for (int i = 0; i < menu.getItemCount(); i++) {
			menu.getItem(i).setEnabled((i == subitem));
		}
	}
	// Automatically update Algorithms menu items depending on current automata type.
	// END
}
