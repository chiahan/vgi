/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cellview;

/**
 * @author polar
 */
public class CellViewConstants {
	//state constants
	public static final int INITIAL_STATE = 0;
	public static final int NORMAL_STATE = 1;
	public static final int ACCEPTING_STATE = 2;
	//transition constants
	public static final int BEZIER_LINE = 3;
	public static final int ORTHOGONAL_LINE = 4;

	public static final int LABEL_CLOSE_TO_SOURCE_STATE = 5;
	public static final int LABEL_INBETWEEN_SOURCE_AND_TARGET = 6;
	public static final int LABEL_CLOSE_TO_TARGET_STATE = 7;

}
