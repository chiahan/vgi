/*
 * This class is used to store the constant which is used to discribe some 
 * properties of state or transition.
 * 
 */
package model;

/**
 * This class is used to discribe the state, transition and line type.
 *
 * @author rasielchang
 */
public class TypeConstants {

/* state constants 0~100 */
    
    public static final int INITIAL_STATE = 0;
    public static final int NORMAL_STATE = 1;
    public static final int FINAL_STATE = 2;
    
/* transition constants 101~300 */
    
    public static final int POLY_LINE = 101;
    public static final int BEZIER_LINE = 102;
    public static final int SPLINE_LINE = 103;
    public static final int STRAIGHT_LINE = 104;
    public static final int CURVE = 105;
    
/* others 301~ */
    
    public static final int LABEL_CLOSE_TO_SOURCE_STATE = 301;
    public static final int LABEL_INBETWEEN_SOURCE_AND_TARGET = 302;
    public static final int LABEL_CLOSE_TO_TARGET_STATE = 303;
    
}