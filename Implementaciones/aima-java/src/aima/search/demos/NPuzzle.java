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

	private static String profundidad;
	private static Double tiempo;
	private static String[] movimientos;
	private static String nodosExpandidos;

	// static NPuzzleBoard random1 = new NPuzzleBoard(new int[] { 0, 2, 3, 4, 5,
	// 6, 7, 8, 9, 10, 12, 1, 13, 14, 11, 15 }, tam);
	private static NPuzzleBoard TableroAzar(int tam) {
		int[] miarray = new int[tam * tam];
		for (int i = 0; i < tam * tam; i++) {
			miarray[i] = i + 1;
		}
		miarray[tam * tam - 1] = 0;

		// NPuzzleBoard random1 = new NPuzzleBoard(miarray, tam);
		NPuzzleBoard random1 = new NPuzzleBoard(randomizar(miarray), tam);

		NPuzzleBoard nn = new NPuzzleBoard(new int[] { 4, 1, 3, 2, 8, 5, 0, 7,
				6 }, tam);

		return nn;
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
		int tam = 3;
		NPuzzleBoard random1 = TableroAzar(tam);
		System.out.println("\nIterative DLS -->");
		resolverPI(random1, tam);
		System.out.println("Profundidad: " + getProfundidad());
		System.out.println("Tiempo: " + getTiempo());
		System.out.println("Movimientos: " + getMovimientos().toString());
		System.out.println("Nodos Expandidos: " + getNodosExpandidos());

		System.out.println("\nA* Misplaced Tiles -->");
		resolverAstar_Misplaced(random1, tam);
		System.out.println("Profundidad: " + getProfundidad());
		System.out.println("Tiempo: " + getTiempo());
		System.out.println("Movimientos: " + getMovimientos().toString());
		System.out.println("Nodos Expandidos: " + getNodosExpandidos());

	}

	private static void eightPuzzleIDLSDemo(NPuzzleBoard random1, int tam) {
		System.out.println("Tablero Problema");
		System.out.println(random1.toString());
		System.out.println("Tablero Meta");
		NPuzzleGoalTest meta = new NPuzzleGoalTest();
		meta.generarMeta(tam);
		System.out.println(meta.getGoalBoard().toString());
		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), meta);
			Search search = new IterativeDeepeningSearch();
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void eightPuzzleAStarManhattanDemo(NPuzzleBoard random1,
			int tam) {
		System.out.println("Tablero Problema");
		System.out.println(random1.toString());
		System.out.println("Tablero Meta");
		NPuzzleGoalTest meta = new NPuzzleGoalTest();
		meta.generarMeta(tam);
		System.out.println(meta.getGoalBoard().toString());

		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), meta,
					new ManhattanHeuristicFunction());
			Search search = new AStarSearch(new GraphSearch());
			SearchAgent agent = new SearchAgent(problem, search);
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void eightPuzzleAStarDemo(NPuzzleBoard random1, int tam) {
		System.out.println("Tablero Problema");
		System.out.println(random1.toString());
		System.out.println("Tablero Meta");
		NPuzzleGoalTest meta = new NPuzzleGoalTest();
		meta.generarMeta(tam);
		System.out.println(meta.getGoalBoard().toString());

		try {
			Problem problem = new Problem(random1,
					new NPuzzleSuccessorFunction(), meta,
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
			if (key.equals("pathCost")) {
				profundidad = property;
			} else if (key.equals("nodesExpanded")) {
				nodosExpandidos = property;
			}
		}

	}

	private static void printActions(List actions) {
		movimientos = new String[actions.size()];
		for (int i = 0; i < actions.size(); i++) {
			String action = (String) actions.get(i);
			movimientos[i] = action;
			System.out.println(action);
		}
	}

	public static void resolverPI(NPuzzleBoard random1, int tam) {
		long inicio, fin;
		// NPuzzleBoard mm = TableroAzar(tam);
		System.out.println("--------------------------------------------");
		inicio = System.currentTimeMillis();
		// eightPuzzleAStarManhattanDemo(random1);
		eightPuzzleIDLSDemo(random1, tam);
		fin = System.currentTimeMillis();
		tiempo = (fin - inicio) / 1000.0;
	}

	public static void resolverAstar_Misplaced(NPuzzleBoard random1, int tam) {
		long inicio, fin;
		System.out.println("--------------------------------------------");
		inicio = System.currentTimeMillis();

		eightPuzzleAStarDemo(random1, tam);
		fin = System.currentTimeMillis();
		tiempo = (fin - inicio) / 1000.0;
	}

	public static String getProfundidad() {
		return profundidad;
	}

	public static void setProfundidad(String profundidad) {
		NPuzzle.profundidad = profundidad;
	}

	public static Double getTiempo() {
		return tiempo;
	}

	public static void setTiempo(Double tiempo) {
		NPuzzle.tiempo = tiempo;
	}

	public static String[] getMovimientos() {
		return movimientos;
	}

	public static void setMovimientos(String[] movimientos) {
		NPuzzle.movimientos = movimientos;
	}

	public static String getNodosExpandidos() {
		return nodosExpandidos;
	}

	public static void setNodosExpandidos(String nodosExpandidos) {
		NPuzzle.nodosExpandidos = nodosExpandidos;
	}

}
