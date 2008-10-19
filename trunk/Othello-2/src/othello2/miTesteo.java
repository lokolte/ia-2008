package othello2;

/**
 ******************************************************************************
 * A test class for the SearchNode.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.03.26
 ****************************************************************************** 
 */
public class miTesteo {

    public static void main(String[] args) {
//		MyDebug.DEBUG = true;

        Board b = new Board(true);
        //testBestMove(b, 1);
        b.setPositions(
                new int[]{
        0, 0, 0, 0, 0, 0, 0, 0, 
        0, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 0, 0, 0, 0, 0, 
        0, 0, 0, 1, 2, 0, 0, 0, 
        0, 0, 0, 2, 2, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0,             
            
                });
        testBestMove(b, 2);
    }
    public static int testCount = 1;

    /**
     * Tests the best move of the board.
     * @param b the board
     * @param player the player to get the best move for.
     */
    public static void testBestMove(Board b, int player) {
        String algoritmo = "miniMa";
        System.err.println("Test " + testCount + " Start -----------------------");
        SearchTree t = new SearchTree(player, b);

        System.err.println("player[" + player + "]::moves: ");
        int[] moves = Board.findMoves(b, player);
        print_array(moves);

        System.err.println(b.toString());
        System.err.println("Best-Move: " + t.getBestMove(4, algoritmo));

        System.err.println("Test " + testCount + " End -----------------------");
        ++testCount;
    }

    public static void print_array(int[] array) {
        System.err.print("\t");
        for (int i = 0; i < array.length && array[i] != -99; i++) {
            System.err.print(array[i] + ", ");
        }
        System.err.print("\n");
    }
}