/*
 * Created on Sep 2, 2004
 *
 */
package npuzzle.search.nodestore;

import java.util.List;

import npuzzle.datastructures.FIFOQueue;
import npuzzle.search.framework.Node;
import npuzzle.search.framework.NodeStore;

/**
 * @author Ravi Mohan
 * 
 */

public class FIFONodeStore implements NodeStore {

	FIFOQueue queue;

	public FIFONodeStore() {
		queue = new FIFOQueue();
	}

	public void add(Node anItem) {
		queue.add(anItem);

	}

	public Node remove() {
		return (Node) queue.remove();
	}

	public void add(List nodes) {
		queue.add(nodes);

	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public int size() {
		return queue.size();

	}

}