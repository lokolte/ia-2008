/*
 * Created on Sep 12, 2004
 *
 */
package npuzzle.search.npuzzle;

import java.util.ArrayList;
import java.util.List;

import npuzzle.search.framework.Successor;
import npuzzle.search.framework.SuccessorFunction;

/**
 * @author Ravi Mohan
 * 
 */

public class NPuzzleSuccessorFunction implements SuccessorFunction {

	public List getSuccessors(Object state) {
		NPuzzleBoard board = (NPuzzleBoard) state;
		List<Successor> successors = new ArrayList<Successor>();
		if (board.canMoveGap(NPuzzleBoard.UP)) {
			NPuzzleBoard newBoard = copyOf(board);
			newBoard.moveGapUp();
			successors.add(new Successor(NPuzzleBoard.UP, newBoard));
		}
		if (board.canMoveGap(NPuzzleBoard.DOWN)) {
			NPuzzleBoard newBoard = copyOf(board);
			newBoard.moveGapDown();
			successors.add(new Successor(NPuzzleBoard.DOWN, newBoard));
		}
		if (board.canMoveGap(NPuzzleBoard.LEFT)) {
			NPuzzleBoard newBoard = copyOf(board);
			newBoard.moveGapLeft();
			successors.add(new Successor(NPuzzleBoard.LEFT, newBoard));
		}
		if (board.canMoveGap(NPuzzleBoard.RIGHT)) {
			NPuzzleBoard newBoard = copyOf(board);
			newBoard.moveGapRight();
			successors.add(new Successor(NPuzzleBoard.RIGHT, newBoard));
		}
		return successors;
	}

	private NPuzzleBoard copyOf(NPuzzleBoard board) {
		NPuzzleBoard newBoard = new NPuzzleBoard(board.tam);
		newBoard.setBoard(board.getPositions());
		return newBoard;
	}

}