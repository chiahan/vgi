

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package drawcomp;

import com.mxgraph.model.mxCell;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class EditorPopupMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3132749140550242191L;

	public EditorPopupMenu(DrawPanel editor)
	{
		boolean selected = !editor.getGraphComponent().getGraph()
				.isSelectionEmpty();
                //edge or vertex????
                mxCell selectedCell=(mxCell)(editor.getGraphComponent().getGraph().getSelectionCell());
                
                boolean edgeSelected=false;
                boolean vertexSelected=false;
                
                if(selected){
                    edgeSelected=selectedCell.isEdge();
                    vertexSelected=selectedCell.isVertex();
                }
                
                //add(editor.bind(mxResources.get("undo"), new HistoryAction(true),
		//		"/com/mxgraph/examples/swing/images/undo.gif"));
                
                
                add(
				editor.bind("add State", editor.getAddStateAction(),
						""))
				.setEnabled(!selected);
                add(
				editor.bind("add Trans", editor.getAddTransAction(),
						""))
				.setEnabled(vertexSelected);
                add(
				editor.bind("add CtrlPt", editor.getAddCtrlPtAction(),
						""))
				.setEnabled(edgeSelected);
                
                
                
                addSeparator();
                
                
                /*add(
				editor.bind(mxResources.get("cut"), TransferHandler
						.getCutAction(),
						"/drawcomp/images/cut.gif"))
				.setEnabled(selected);
		add(
				editor.bind(mxResources.get("copy"), TransferHandler
						.getCopyAction(),
						"/drawcomp/images/copy.gif"))
				.setEnabled(selected);
		add(editor.bind(mxResources.get("paste"), TransferHandler
				.getPasteAction(),
				"/drawcomp/images/paste.gif"));

		addSeparator();

		add(
				editor.bind(mxResources.get("delete"), mxGraphActions
						.getDeleteAction(),
						"/drawcomp/images/delete.gif"))
				.setEnabled(selected);

		addSeparator();

		// Creates the format menu
		JMenu menu = (JMenu) add(new JMenu(mxResources.get("format")));

		//EditorMenuBar.populateFormatMenu(menu, editor);

		// Creates the shape menu
		menu = (JMenu) add(new JMenu(mxResources.get("shape")));

		//EditorMenuBar.populateShapeMenu(menu, editor);

		addSeparator();

		add(
				editor.bind(mxResources.get("edit"), mxGraphActions
						.getEditAction())).setEnabled(selected);

		addSeparator();

		add(editor.bind(mxResources.get("selectVertices"), mxGraphActions
				.getSelectVerticesAction()));
		add(editor.bind(mxResources.get("selectEdges"), mxGraphActions
				.getSelectEdgesAction()));

		addSeparator();

		add(editor.bind(mxResources.get("selectAll"), mxGraphActions
				.getSelectAllAction()));*/
	}

}
