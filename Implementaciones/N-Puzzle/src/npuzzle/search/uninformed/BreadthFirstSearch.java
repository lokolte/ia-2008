package npuzzle.search.uninformed;

import java.util.List;

import npuzzle.search.framework.Metrics;
import npuzzle.search.framework.Problem;
import npuzzle.search.framework.QueueSearch;
import npuzzle.search.framework.Search;
import npuzzle.search.nodestore.FIFONodeStore;

/**
 * Artificial Intelligence A Modern Approach (2nd Edition): page 73.
 * 
 * Breadth-first search.
 */
public class BreadthFirstSearch implements Search {

	private final QueueSearch search;

	public BreadthFirstSearch(QueueSearch search) {
		this.search = search;
	}

	public List search(Problem p) {
		return search.search(p, new FIFONodeStore());
	}

	public Metrics getMetrics() {
		return search.getMetrics();
	}

}