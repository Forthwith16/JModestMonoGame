package gamecore;

/**
 * Provides a number of useful constant values.
 * @author Dawn Nye
 */
public final class GlobalConstants
{
	/**
	 * Determines if two doubles are close enough to be considered equal.
	 * @param d1 The first double.
	 * @param d2 The second double.
	 * @return Returns true iff {@code d1} and {@code d2} are within {@code EPSILON} of each other.
	 */
	public static boolean CloseEnough(double d1, double d2)
	{return Math.abs(d1 - d2) < EPSILON;}
	
	/**
	 * If true, then inputs are allowed to come early or late by as much as INPUT_GRACE_TIME.
	 */
	public static final boolean ALLOW_INPUT_GRACE = true;
	
	/**
	 * When ALLOW_INPUT_GRACE is true, this specifies how early or late inputs are allowed to arrive.
	 * Inputs that arrive at or outside this boundary are not forgiven.
	 * This value is expressed in milliseconds.
	 */
	public static final long INPUT_GRACE_TIME = 105;
	
	/**
	 * The epsilon used to account for rounding errors in double calculations.
	 */
	public static final double EPSILON = 0.0000001;
	
	/**
	 * The width of a tetris grid.
	 */
	public static final int TETRIS_GRID_WIDTH = 10;
	
	/**
	 * The height of a tetris grid.
	 */
	public static final int TETRIS_GRID_HEIGHT = 20;
	
	/**
	 * The number of pixels per Tetris block.
	 */
	public static final double TETRIS_BLOCK_SIZE = 32.0;
	
	/**
	 * The number of pixels per half Tetris block.
	 */
	public static final double TETRIS_HALF_BLOCK_SIZE = 16.0;
	
	/**
	 * The number of lines per theme change.
	 */
	public static final int TETRIS_LINES_PER_THEME = 10;
	
	/**
	 * The maximum number of themes.
	 */
	public static final int TETRIS_MAX_THEME = 10;
}
