package othello2;

/**
 ******************************************************************************
 * The Othello class controls the basic game of othello.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
import java.util.*;

public class Othello extends Observable implements OthelloConstants, Runnable {

    private Player light;
    private Player dark;
    private Board board;
    private Player nextPlayer;
    public int[] moves;
    private boolean finished = false;
    private String algoritmo = "miniMax";

    public Othello() {
        this(false,false,4,"miniMax");
    }

    public Othello(boolean ai1Controlled, boolean ai2Controlled, int aiDepth,String algo2) {
        board = new Board(true);
        dark = new Player(DARK_PIECE, ai1Controlled, aiDepth,algo2);
        light = new Player(LIGHT_PIECE, ai2Controlled, aiDepth,algoritmo);
        nextPlayer = dark;
        moves = Board.findMoves(board, nextPlayer.getID());
    }

    public Othello(boolean ai1Controlled, boolean ai2Controlled, int profundidad1, int profundidad2, String algo1, String algo2) {
        board = new Board(true);
        dark = new Player(DARK_PIECE, ai1Controlled, profundidad2, algo2);
        light = new Player(LIGHT_PIECE, ai2Controlled, profundidad1, algo1);
        nextPlayer = dark;
        moves = Board.findMoves(board, nextPlayer.getID());
    }

    public Board getBoard() {
        return board;
    }

    public boolean nextMove() {
        if (nextPlayer.isAI()) {

            MyDebug.println("ai[" + nextPlayer.getID() + "]::start.");
            MyDebug.println("ai[" + nextPlayer.getID() + "]::moves: ");
            MyDebug.print("\t");
            for (int i = 0; i < moves.length && moves[i] != -99; i++) {
                MyDebug.print(moves[i] + ", ");
            }
            MyDebug.print("\n");

            int nextmove = nextPlayer.move(board);
            if (nextmove != -1) {
                MyDebug.println("ai[" + nextPlayer.getID() + "]::nextmove = " + nextmove);
                int[] convert = Board.convertPosition(nextmove);
                MyDebug.println(board.toString());

                if (board.move(convert[0], convert[1], nextPlayer.getID())) {
                    skipMove();
                    MyDebug.println("ai[" + nextPlayer.getID() + "]::gen-your-moves.");
                    moves = Board.findMoves(board, nextPlayer.getID());
                    MyDebug.println("ai[" + nextPlayer.getID() + "]::done.");
                    setChanged();
                    notifyObservers();

                }
            } else {
                skipMove();
                moves = Board.findMoves(board, nextPlayer.getID());
            }
        } else {
            // wait for human to play.
        }
        return true;
    }

    public boolean move(int x, int y) {
        if (board.move(x, y, nextPlayer.getID())) {

            skipMove();
            moves = Board.findMoves(board, nextPlayer.getID());
            setChanged();
            notifyObservers();
            return true;
        }
        return false;
    }

    public void skipMove() {
        if (nextPlayer == dark) {
            nextPlayer = light;
        } else {
            nextPlayer = dark;
        }
    }

    public void run() {
        // run the game
        finished = false;
        while (!finished) {
            nextMove();
            if (board.getCount(FREE_SPACE) == 0) {
                setChanged();
                finished = true;
                notifyObservers();
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean nextIsAI() {
        return nextPlayer.isAI();
    }

    public Player getPlayer(int index) {
        switch (index) {
            case LIGHT_PIECE:
                return light;
            case DARK_PIECE:
            default:
                return dark;
        }
    }
}