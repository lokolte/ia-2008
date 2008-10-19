package othello2;

/**
 ******************************************************************************
 * The OthelloConstants interface holds all the constants.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.08
 ****************************************************************************** 
 */
public interface OthelloConstants {

    public final static int BOARD_LENGTH = 8;
    public final static int BOARD_POSITIONS = 64;
    public final static int LIGHT_PIECE = 1;
    public final static int DARK_PIECE = 2;
    public final static int LIGHT_MOVEABLE = -1;
    public final static int DARK_MOVEABLE = -2;
    public final static int BOTH_MOVEABLE = -3;
    public final static int NO_PIECE = 0;
    public final static int NOT_POSSIBLE = -99;
    public final static int FREE_SPACE = 0;
    public final static int STARTING_DEPTH = 4;
    public final static String miniMaxAlgoritm = "miniMax";
    public final static String alphaBetaAlgoritm = "alphaBeta";
}
