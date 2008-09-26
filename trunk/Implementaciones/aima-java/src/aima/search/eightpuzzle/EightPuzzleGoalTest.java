/*
 * Created on Sep 12, 2004
 *
 */
package aima.search.eightpuzzle;

import aima.search.framework.GoalTest;

/**
 * @author Ravi Mohan
 * 
 */

public class EightPuzzleGoalTest implements GoalTest {
	public static int tam = 4;
	EightPuzzleBoard goal = new EightPuzzleBoard(new int[] { 0, 1, 2, 4, 5, 6,
			3, 7, 9, 10, 12, 8, 13, 14, 11, 15 }, tam);

	public boolean isGoalState(Object state) {
		EightPuzzleBoard board = (EightPuzzleBoard) state;
		System.out.println(board.toString());
		return board.equals(goal);
	}
}