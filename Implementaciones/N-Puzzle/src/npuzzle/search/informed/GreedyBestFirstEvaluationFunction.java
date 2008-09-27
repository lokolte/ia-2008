package npuzzle.search.informed;

import npuzzle.search.framework.EvaluationFunction;
import npuzzle.search.framework.Node;
import npuzzle.search.framework.Problem;

/**
 * @author Ciaran O'Reilly
 * 
 */
public class GreedyBestFirstEvaluationFunction implements EvaluationFunction {

	public GreedyBestFirstEvaluationFunction() {
	}

	public Double getValue(Problem p, Node n) {
		// f(n) = h(n)
		return new Double(p.getHeuristicFunction().getHeuristicValue(
				n.getState()));
	}
}
