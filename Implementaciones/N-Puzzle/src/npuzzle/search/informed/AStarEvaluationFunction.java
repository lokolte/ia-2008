package npuzzle.search.informed;

import npuzzle.search.framework.EvaluationFunction;
import npuzzle.search.framework.Node;
import npuzzle.search.framework.Problem;

/**
 * @author Ciaran O'Reilly
 * 
 */
public class AStarEvaluationFunction implements EvaluationFunction {

	public AStarEvaluationFunction() {
	}

	public Double getValue(Problem p, Node n) {
		// f(n) = g(n) + h(n)
		return new Double(n.getPathCost()
				+ p.getHeuristicFunction().getHeuristicValue(n.getState()));
	}
}
