/*
 * Created on Sep 12, 2004
 *
 */
package aima.search.demos;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import aima.search.eightpuzzle.EightPuzzleBoard;
import aima.search.eightpuzzle.EightPuzzleGoalTest;
import aima.search.eightpuzzle.EightPuzzleSuccessorFunction;
import aima.search.eightpuzzle.MisplacedTilleHeuristicFunction;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.uninformed.IterativeDeepeningSearch;

/**
 * @author Ravi Mohan
 * 
 */

public class NPuzzle {
	public static int tam = 4;
	// public static EightPuzzleBoard boardWithThreeMoveSolution = new
	// EightPuzzleBoard(
	// new int[] { 1, 2, 5, 3, 4, 0, 6, 7, 8 });;

	static EightPuzzleBoard random1 = new EightPuzzleBoard(new int[] { 1, 2, 3,
			4, 5, 6, 7, 8, 9, 10, 12, 0, 13, 14, 11, 15 }, tam);

	// static EightPuzzleBoard extreme = new EightPuzzleBoard(new int[] { 0, 8,
	// 7,$
	// 6, 5, 4, 3, 2, 1 });

	public static void main(String[] args) {
		eightPuzzleIDLSDemo();
	}

	private static void eightPuzzleIDLSDemo() {
		System.out.println("\nEightPuzzleDemo Iterative DLS -->");
		System.out.println(random1.toString());
		try {
			Problem problem = new Problem(random1,
					new EightPuzzleSuccessorFunction(),
					new EightPuzzleGoalTest());
			Search search = new IterativeDeepeningSearch();
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void eightPuzzleAStarDemo() {
		System.out
				.println("\nEightPuzzleDemo AStar Search (MisplacedTileHeursitic)-->");
		try {
			Problem problem = new Problem(random1,
					new EightPuzzleSuccessorFunction(),
					new EightPuzzleGoalTest(),
					new MisplacedTilleHeuristicFunction());
			Search search = new AStarSearch(new GraphSearch());
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void printInstrumentation(Properties properties) {
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			System.out.println(key + " : " + property);
		}

	}

	private static void printActions(List actions) {
		for (int i = 0; i < actions.size(); i++) {
			String action = (String) actions.get(i);
			System.out.println(action);
		}
	}

}