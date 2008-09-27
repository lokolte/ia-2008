/*
 * Created on Sep 12, 2004
 *
 */
package aima.search.demos;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.npuzzle.ManhattanHeuristicFunction;
import aima.search.npuzzle.MisplacedTilleHeuristicFunction;
import aima.search.npuzzle.NPuzzleBoard;
import aima.search.npuzzle.NPuzzleGoalTest;
import aima.search.npuzzle.NPuzzleSuccessorFunction;
import aima.search.uninformed.IterativeDeepeningSearch;

/**
 * @author Ravi Mohan
 * 
 */

public class NPuzzle {
	public static int tam = 3;

	// static NPuzzleBoard random1 = new NPuzzleBoard(new int[] { 0, 2, 3, 4, 5,
	// 6, 7, 8, 9, 10, 12, 1, 13, 14, 11, 15 }, tam);

	private static NPuzzleBoard TableroAzar(int tam) {
		int[] miarray = new int[tam * tam];
		for (int i = 0; i < tam * tam; i++) {
			miarray[i] = i;
		}

		NPuzzleBoard random1 = new NPuzzleBoard(randomizar(miarray), tam);
		return random1;
	}

	public static int[] randomizar(final int[] ints) {
		final Random rd = new Random();
		for (int i = 0; i < ints.length; i++) {
			final int ci = ints[i];
			final int ni = Math.abs(rd.nextInt() % ints.length);
			ints[i] = ints[ni];
			ints[ni] = ci;
		}
		return ints;
	}

	public static void main(String[] args) {
		long inicio, fin;
		NPuzzleBoard random1 = TableroAzar(tam);
		System.out.println("--------------------------------------------");
		inicio = System.nanoTime();

		eightPuzzleIDLSDemo(random1);
		eightPuzzleAStarDemo(random1);
		
		fin = System.nanoTime();
		System.out.println("Y ha tardado: " + ((fin - inicio) / 1000000.0)
				+ " segundos");

	}

	private static void eightPuzzleIDLSDemo(NPuzzleBoard random1) {
		System.out.println("Tablero Problema");
		System.out.println(random1.toString());
		System.out.println("Tablero Meta");
		NPuzzleGoalTest meta = new NPuzzleGoalTest();
		meta.generarMeta(tam);
		System.out.println(meta.getGoalBoard().toString());

		System.out.println("\nEightPuzzleDemo Iterative DLS -->");
		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), new NPuzzleGoalTest());
			Search search = new IterativeDeepeningSearch();
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void eightPuzzleAStarManhattanDemo(NPuzzleBoard random1) {
		System.out
				.println("\nEightPuzzleDemo AStar Search (ManhattanHeursitic)-->");
		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), new NPuzzleGoalTest(),
					new ManhattanHeuristicFunction());
			Search search = new AStarSearch(new GraphSearch());
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void eightPuzzleAStarDemo(NPuzzleBoard random1) {
		System.out
				.println("\nEightPuzzleDemo AStar Search (MisplacedTileHeursitic)-->");
		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), new NPuzzleGoalTest(),
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