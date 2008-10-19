package othello2;

/**
 ******************************************************************************
 * The Player class is just a container class for player related information.
 * For AI Players, it will also decide which move to take next.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
public class Player implements OthelloConstants {

    private int colorDesignation;
    private boolean ai;
    private int depth;

    public int getDepth() {
        return depth;
    }
    private String algoritmo = "miniMax"; //por defecto es el MiniMax


    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public Player(int color, String algoritmo) {
        this(color, false, 4, algoritmo);
    }

    public Player(int color, boolean ai, int depth, String algoritmo) {
        colorDesignation = color;
        this.ai = ai;
        this.depth = depth;
        this.algoritmo = algoritmo;
    }

    public int getID() {
        return colorDesignation;
    }

    public boolean isAI() {
        return ai;
    }

    public int move(Board board) {
        SearchTree t = new SearchTree(getID(), board);
        //t.generateTree( 4 );
        int move;
        if (this.algoritmo.compareTo("aleatorio") == 0) {
            int[] arrayMov = Board.findMoves(board, getID());
            int x = (int) (arrayMov.length * Math.random());
            move = arrayMov[x];
            //move=0;
        } else {
            move = t.getBestMove(depth, algoritmo);

        }
        return move;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
