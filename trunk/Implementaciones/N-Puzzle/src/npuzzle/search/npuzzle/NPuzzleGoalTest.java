/*
 * Created on Sep 12, 2004
 *
 */
package npuzzle.search.npuzzle;

import npuzzle.search.framework.GoalTest;

/**
 * @author Ravi Mohan
 * 
 */

public class NPuzzleGoalTest implements GoalTest {

	NPuzzleBoard goal;

	public void generarMeta(int tam) {
		int[] miarray = new int[tam * tam];
		for (int i = 0; i < tam * tam; i++) {
			miarray[i] = i + 1;
		}
		miarray[tam * tam - 1] = 0;
		goal = new NPuzzleBoard(miarray, tam);
	}

	public boolean isGoalState(Object state) {
		NPuzzleBoard board = (NPuzzleBoard) state;
		// System.out.println(board.toString());
		return board.equals(goal);
	}

	public NPuzzleBoard getGoalBoard() {
		return goal;
	}
}