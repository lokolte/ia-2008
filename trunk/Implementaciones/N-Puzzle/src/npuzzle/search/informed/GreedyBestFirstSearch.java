/*
 * Created on Sep 12, 2004
 *
 */
package npuzzle.search.informed;

import npuzzle.search.framework.BestFirstSearch;
import npuzzle.search.framework.QueueSearch;

/**
 * @author Ravi Mohan
 * 
 */

public class GreedyBestFirstSearch extends BestFirstSearch {

	public GreedyBestFirstSearch(QueueSearch search) {
		super(search, new GreedyBestFirstEvaluationFunction());
	}
}