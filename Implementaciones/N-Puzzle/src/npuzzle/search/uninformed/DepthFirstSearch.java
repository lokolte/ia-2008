package npuzzle.search.uninformed;

import java.util.List;

import npuzzle.search.framework.Metrics;
import npuzzle.search.framework.Problem;
import npuzzle.search.framework.QueueSearch;
import npuzzle.search.framework.Search;
import npuzzle.search.nodestore.LIFONodeStore;

/**
 * Artificial Intelligence A Modern Approach (2nd Edition): page 75.
 * 
 * Depth-first search.
 */
public class DepthFirstSearch implements Search {

	QueueSearch search;

	public DepthFirstSearch(QueueSearch search) {

		this.search = search;

	}

	public List search(Problem p) {

		return search.search(p, new LIFONodeStore());
	}

	public Metrics getMetrics() {
		return search.getMetrics();
	}

}