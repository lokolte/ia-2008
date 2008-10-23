package othello;

import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * this class is a computer player, that calculates what move to do
 * in a given situation.
 */
class player implements Runnable {
    /*general variables*/

    board realb, b;
    int side;
    int maxlevel;
    String algoritmo = "Minimax";
    double tiempoTotal = 0;
    double cantidadJugada = 0;

    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public int getMaxlevel() {
        return maxlevel;
    }

    public void setMaxlevel(int maxlevel) {
        this.maxlevel = maxlevel;
    }
    /*The Artificial intelligence used here is not very complictated
    I use a alphabeta algorithm to search a few moves deep. Then
    I do a simple evaluation with a number of adjustable parameters.
    To find good values for these parameters I used a genetic algorithm.
    This genetic algorithm is not used while your playing: to slow. */
    /*evaluation variables*/
    /**
     * The string W is the evaluation string used to evaluate situations. It was 
     * calculate with the genetictrain class, and then inserted in this sourcecode.
     */
    public String W = "8 0 -7 2 -8 2 0 -2 1 -6 2 3 1 -2 1 1 -2 0 -1 0 1 -2 -1 4 3 5 53 best";
    int[] weight = new weightvector(W).a;
    int offset;
    /*if you used genetictrain to calculate a new weightvector,
    change the line String W to hold a line of the new output file.
    typically, pick the first or last line. */
    /*for use in a parallel thread (applet mode)*/
    boardview bv;
    long time;

    /**
     * determines how far the computer looks ahead, higher value plays
     * better, but takes more time. I think even values are better, because
     * that gives the opponent the last move, but that is personal.*/
    public void setstrength(int l) {
        if (l > 6) {
            l = 6;
        }
        if (l < 1) {
            l = 1;
        }
        maxlevel = l;
    }

    /**
     * set the weight vector this player should use.
     */
    public void setweight(int[] w) {
        weight = w;
    }

    /**
     * create a player.
     * @param ib the board that is used. 
     * @param iside the side the player takes: board.P1 or board.P2
     */
    public player(board ib, int iside) {
        this(ib, iside, null, 4, "Minimax");
    }

    /**
     * create a player for applet mode: the player calculates in a separate thread,
     * and wait 4 seconds before answering.
     * @param ib the board that is used. 
     * @param iside the side the player takes: board.P1 or board.P2
     * @param ibv the boardview it must answer to.
     */
    public player(board ib, int iside, boardview ibv, int profundidad, String algoritmo) {
        realb = ib;
        side = iside;
        bv = ibv;
        setstrength(profundidad);
        this.algoritmo = algoritmo;

    }

    /**
     * the method you can call if you want this player to think of a move in a 
     * separate thread. This function returns immediately, so the caller can do something
     * different. The answers will be given after 4 seconds or longer to the 
     * boardview by the method answer.
     * @param s a string that is not used. It can be used for comment by a caller. Do 
     * not take it too seriously: it is an inside joke to include comment in extra parameters.
     */
    public void ask(String s) {
        new Thread(this).start();
    }

    /*for timing*/
    void timestart() {
        time = System.currentTimeMillis();
    }

    int gettime() {
        return (int) (System.currentTimeMillis() - time);
    }

    /**
     * the method that will run in a separate thread. Do not call this function yourself,
     * but use ask.
     */
    public void run() {
        timestart();
        int move = obtenerMejorMovimiento();
        while (gettime() < 700) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
        bv.answer("Well, this is my move:", move);
    }

    void setoffset() {
        /*determines which part of the eval vector,the first or the last, to use.*/
        if (b.getcoverage() < weight[weight.length - 1]) {
            offset = 0;
        } else {
            offset = out.length;
        }
    }
    int[] out;/*this vector can be used again and 
    again to get an evalvector in. this saves memory. */


    int simplescore() {
        int score = 0;
        out = b.getevalvector(out);
        for (int i = 0; i < out.length; i++) {
            score += weight[offset + i] * out[i];
        }
        return score;
    }

    int obtenerMejorMovimiento() {
        int mejor;
        long inicio, fin;
        if (this.algoritmo.compareTo("Minimax") == 0) {
            inicio = System.currentTimeMillis();
            mejor = miniMaxCaller();
            fin = System.currentTimeMillis();
            this.tiempoTotal = this.tiempoTotal + (fin - inicio) / 1000.0;
            this.cantidadJugada++;
            System.err.println("-- MINIMAX -- " + "\n Tiempo Total: "+this.tiempoTotal+"\n Cant. Mov.: "+this.cantidadJugada+"\n Promedio Tiempo: "+this.tiempoTotal/this.cantidadJugada);
            return mejor;
        } else if (this.algoritmo.compareTo("Alfabeta") == 0) {
            inicio = System.currentTimeMillis();
            mejor = alphaBetaCaller();
            fin = System.currentTimeMillis();
            this.tiempoTotal = this.tiempoTotal + (fin - inicio) / 1000.0;
            this.cantidadJugada++;
            System.err.println("-- ALPHABETA -- " + "\n Tiempo Total: "+this.tiempoTotal+"\n Cant. Mov.: "+this.cantidadJugada+"\n Promedio Tiempo: "+this.tiempoTotal/this.cantidadJugada);
            return mejor;
        } else { //ALEATORIO

            inicio = System.currentTimeMillis();
            b = realb.copy();
            mejor = this.siguienteMovimientoAleatorio(b.posmove);
            fin = System.currentTimeMillis();
            this.tiempoTotal = this.tiempoTotal + (fin - inicio) / 1000.0;
            this.cantidadJugada++;
            System.err.println("-- ALEATORIO -- " + "\n Tiempo Total: "+this.tiempoTotal+"\n Cant. Mov.: "+this.cantidadJugada+"\n Promedio Tiempo: "+this.tiempoTotal/this.cantidadJugada);
            return mejor;
        }
    }

    /**
     * this functions returns the move that this player thinks is best. Use this function
     * if you just want the best move, no extra threads and no delays.
     * @return a move
     */
    public int miniMaxCaller() {
        b = realb.copy();
        if (b.posmoves == 0) {
            return -1;
        }
        setoffset();
        int s, best = -1, alpha = -100000;
        int k = b.posmoves;
        for (int i = 0; i < k; i++) {
            b.domove(b.posmove[i]);

            s = -miniMax(-100000, -alpha, 1);
            b.undomove();
            if (s > alpha) {
                alpha = s;
                best = i;
            }
        }
        return b.posmove[best];
    }

    /*returns how good the current situation looks.*/
    int miniMax(int alpha, int beta, int level) {
        if (level >= maxlevel || b.posmoves == 0) {
            return simplescore();
        }
        int s;
        /*try all moves and return the estimate
        we get when doing the best move*/
        for (int i = 0; i < b.posmoves; i++) {
            b.domove(b.posmove[i]);
            s = -miniMax(-beta, -alpha, level + 1);
            b.undomove();
        /*            
        if (s > beta) {
        return s;
        }
        if (s > alpha) {
        alpha = s;
        }
         */
        }
        return alpha;
    }

    /**
     * this functions returns the move that this player thinks is best. Use this function
     * if you just want the best move, no extra threads and no delays.
     * @return a move
     */
    public int alphaBetaCaller() {
        b = realb.copy();
        if (b.posmoves == 0) {
            return -1;
        }
        setoffset();
        int s, best = -1, alpha = -100000;
        int k = b.posmoves;
        for (int i = 0; i < k; i++) {
            b.domove(b.posmove[i]);

            s = -alphaBeta(-100000, -alpha, 1);
            b.undomove();
            if (s > alpha) {
                alpha = s;
                best = i;
            }
        }
        return b.posmove[best];
    }

    /*returns how good the current situation looks.*/
    int alphaBeta(int alpha, int beta, int level) {
        if (level >= maxlevel || b.posmoves == 0) {
            return simplescore();
        }
        int s;
        /*try all moves and return the estimate
        we get when doing the best move*/
        for (int i = 0; i < b.posmoves; i++) {
            b.domove(b.posmove[i]);
            s = -alphaBeta(-beta, -alpha, level + 1);
            b.undomove();
            if (s > beta) {
                return s;
            }
            if (s > alpha) {
                alpha = s;
            }
        }
        return alpha;
    }
    public static int siguienteMovimientoAleatorio(int[] moves){
        int cantidad=0;
        for (int i = 0; i < moves.length && moves[i] != 0; i++) {
            cantidad++;
        }
        if (cantidad==0)
            return -1;

        int[] movimientos=new int[cantidad];
        cantidad=0;
        for (int i = 0; i < moves.length && moves[i] != 0; i++) {
            movimientos[cantidad++]=moves[i];
        }
        
        int x = (int) (movimientos.length * Math.random());
        return movimientos[x];
        
    }
    
}