/*
 * Created on Sep 12, 2004
 *
 */
package npuzzle.search.framework;

/**
 * @author Ravi Mohan
 * 
 */

public class DefaultHeuristicFunction implements HeuristicFunction {

	public int getHeuristicValue(Object state) {
		throw new IllegalStateException(
				"Should not be depending on the DefaultHeuristicFunction.");
		// return 1;
	}

}