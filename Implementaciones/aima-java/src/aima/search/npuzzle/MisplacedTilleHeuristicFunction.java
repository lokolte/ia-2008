/*
 * Created on Sep 12, 2004
 *
 */
package aima.search.npuzzle;

import aima.basic.XYLocation;
import aima.search.framework.HeuristicFunction;

/**
 * @author Ravi Mohan
 * 
 */

public class MisplacedTilleHeuristicFunction implements HeuristicFunction {

	public int getHeuristicValue(Object state) {
		NPuzzleBoard board = (NPuzzleBoard) state;
		return getNumberOfMisplacedTiles(board);

	}

	private int getNumberOfMisplacedTiles(NPuzzleBoard board) {
		int numberOfMisplacedTiles = 0;
		
                int k = -1;
                
                for (int i=0; i<board.getTam(); i++) {
                    for (int j=0; j<board.getTam(); j++) {
                        if (!(board.getLocationOf(++k).equals(new XYLocation(i, j)))) {
                            numberOfMisplacedTiles++;
                        }
                    }
                    
                }
                
                /*
                if (!(board.getLocationOf(0).equals(new XYLocation(0, 0)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(1).equals(new XYLocation(0, 1)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(2).equals(new XYLocation(0, 2)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(3).equals(new XYLocation(1, 0)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(4).equals(new XYLocation(1, 1)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(5).equals(new XYLocation(1, 2)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(6).equals(new XYLocation(2, 0)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(7).equals(new XYLocation(2, 1)))) {
			numberOfMisplacedTiles++;
		}
		if (!(board.getLocationOf(8).equals(new XYLocation(2, 2)))) {
			numberOfMisplacedTiles++;
		}*/
		return numberOfMisplacedTiles;
	}

}