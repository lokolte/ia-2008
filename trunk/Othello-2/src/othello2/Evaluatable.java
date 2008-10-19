package othello2;
/**
 ******************************************************************************
 * The wrapper for items in the SearchNode.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.03.26
 ****************************************************************************** 
 */
import java.util.*;

public interface Evaluatable
{
	/**
	 * Computes the "value" of the board. Higher values are prefered.
	 * @param player the player to compute for.
	 * @return the computed "value."
	 */
	int staticEvaluation( int player );
	
	/**
	 * Generates the children of this Evaluatable object.
	 * @param player the player to generate against.
	 * @return the list of children
	 */
	ArrayList generate( int player );
}
	