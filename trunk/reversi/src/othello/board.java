package othello;
import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * a board contains the current game situation: where the stones are, what color they have
 * and who should move. It also contains a history so moves can be undone.
 */
class board {

    /**
     * the array A contains the stones. You would expect A to be a 2-dimensional array, but
     * it turns out that it is faster to make it a one-dimensional array. A is larger than the
     * nulber of fields, because this prevents bounds tracking. If you start on a valid field,
     * and you start walking in any direction, you will first encounter a field with the value
     * OUT before you cross a border.<p>
     * because A is onedimensional, you just need one integer to indicate a field. 
     * So instead of an object move all methods dealing with moves just use int's
     * (note the huge speed improvement).
     */
    public int[] A;
    static int[] all;//all indices of visible fields.
    static int X = 8,  Y = 8;/*the size of the visible board. You can play on larger
    boards by changing these values. Note that the computer AI will be confused if 
    you do so. Try to keep X==Y (or modify the kinds code) and recompile all classes
    after you did, and retrain the player.*/

    static int N = X * Y;//the number of visible fields.
    static int BASE = X + 2;/*the number used in converting 1dimensional and
    twodimensional coordinates*/

    //the values A can have.
    public static int OUT = 88,  P1 = 0,  P2 = 1,  FREE = 3;
    /*the information needed to undo a move.*/
    //the moves already done.
    int[] move = new int[128];
    //the number of stones flipped in that move.
    int[] undoflips = new int[128];
    //the positions of the stones flipped in that move.
    int[][] undoflip = new int[128][24];
    int moves; //number of moves done.

    int[] can;//the number of flips if you move here,
    int posmoves;//the number of moves possible. if it is zero the current player must pass.
    int thisp;//the player that is currently moving. valid values are P1 and P2.
    int[] posmove = new int[N];/*the moves that are possible.*/

    int[] points = {0, 0};//the points for each player.
    int free;//the number of free fields. 
    int status; /*values:*/

    final static int RUNNING = 1,  FINISHED = 2;

    /*these fields are needed by the computer AI:*/
    static int[] kind;//the 'kind' of each field
    static int kinds;//the total number of different kinds.
    static int evalvectorlength;//the length of an evaluation vector.
    

    static {/*preparations for this class*/
        all = new int[N];
        int cur = 0;
        for (int j = 0; j < Y; j++) {
            for (int i = 0; i < X; i++) {
                all[cur++] = co(i, j);
            }
        }
        kind = new int[(X + 2) * (Y + 2)];
        int k = 0;
        for (int i = 0; i < X / 2; i++) {
            for (int j = 0; j <= i; j++) {
                kind[co(i, j)] = k;
                kind[co(X - 1 - i, j)] = k;
                kind[co(i, Y - 1 - j)] = k;
                kind[co(X - 1 - i, Y - 1 - j)] = k;
                kind[co(j, i)] = k;
                kind[co(j, X - 1 - i)] = k;
                kind[co(X - 1 - j, i)] = k;
                kind[co(X - 1 - j, Y - 1 - i)] = k;
                k++;
            }
        }
        kinds = k;
        evalvectorlength = kinds + 3;
    /*the kinds indicate the fields that are really different,
     */
    }


    /* help functions:*/
    static int co(int i, int j) {
        return (i + 1) + ((j + 1) * BASE);
    }
    //convert from 2dimensional field coordinates to 1dimensional field number.

    static int p1(int r) {
        return r % BASE - 1;
    }//get the x coordinate of a field number

    static int p2(int r) {
        return r / BASE - 1;
    }//get the y coordinate.

    static int opponent(int p) {
        return 1 - p;
    }//returns P1 if given P2 and vice versa.

    /**
     * returns an exact copy of this board.
     */
    public board copy() {
        board r = new board();
        r.moves = moves;
        r.posmoves = posmoves;
        r.thisp = thisp;
        r.A = samearray(A);
        r.points = samearray(points);
        r.move = samearray(move);
        r.posmove = samearray(posmove);
        r.can = samearray(can);
        return r;
    }
    /*make copy of given array*/

    int[] samearray(int[] a) {
        return (int[]) a.clone();
    }

    /*create a new board.*/
    public board() {
        A = new int[(X + 2) * (Y + 2)];
        can = new int[(X + 2) * (Y + 2)];
        for (int i = 0; i < A.length; i++) {
            A[i] = OUT;
        }
        clear();
    }

    /*info functions*/
    /**
     * get the player currently moving: P1 or P2.
     */
    public int getplayer() {
        return moves % 2;
    }

    /**
     * set the given field with the given value
     */
    public void set(int i, int value) {
        A[i] = value;
    }

    /**
     *get the value of the given field
     */
    public int get(int i) {
        return A[i];
    }

    /**
     * get the value of the field indicated in 2dimensional coordinates.
     */
    public int get(int i, int j) {
        return A[co(i, j)];
    }

    /**
     * resetup the board to the initial situation. Chooses randomly between
     * the two possible openings.
     */
    public void clear() {
        moves = 0;
        for (int i = 0; i < N; i++) {
            A[all[i]] = FREE;
        }
        if (2 * Math.random() > 1) {
            set(co(3, 3), P1);
            set(co(3, 4), P1);
            set(co(4, 3), P2);
            set(co(4, 4), P2);
        } else {
            set(co(3, 3), P2);
            set(co(3, 4), P1);
            set(co(4, 3), P1);
            set(co(4, 4), P2);
        }
        setcan();
        status = RUNNING;
    }

    /*sets the can array with the right values
    (and) status, free, etc... call this method after each
    change.*/
    void setcan() {
        points[P1] = points[P2] = posmoves = 0;
        free = 0;
        thisp = getplayer();
        for (int i = 0; i < N; i++) {
            if (A[all[i]] == P1) {
                points[P1]++;
                can[all[i]] = 0;
            } else if (A[all[i]] == P2) {
                points[P2]++;
                can[all[i]] = 0;
            } else {
                free++;
                can[all[i]] = flips(all[i]);
                if (can[all[i]] > 0) {
                    posmove[posmoves++] = all[i];
                }
            }
        }
        if (finished()) {
            status = FINISHED;
        } else {
            status = RUNNING;
        }
    }

    /**
     * do the given move on this board.
     */
    public void domove(int c) {
        /*do a move on this board.*/
        undoflips[moves] = 0;
        if (status == FINISHED) {
            return;
        }
        if (c != -1) {
            int otherp = opponent(thisp);
            int i, dir, d;
            A[c] = thisp;
            for (i = 0; i < 8; i++) {
                dir = around[i];
                if (count(otherp, c, dir) > 0) {
                    for (d = dir + c; A[d] == otherp; d += dir) {
                        A[d] = thisp;
                        undoflip[moves][undoflips[moves]++] = d;
                    }
                }
            }
        }
        move[moves++] = c;
        setcan();
    }

    //all directions you can flips stones in. Given as the difference in 
    //1dimensional field numbers.
    int[] around = {1, -1, BASE, -BASE, BASE + 1, BASE - 1, -BASE + 1, -BASE - 1};

    /**
     * calculate the number of stones that would flip if you put
     * a stone at the given position.
     */
    int flips(int c) {
        if (A[c] != FREE) {
            return 0;
        }
        int ret = 0;
        int otherp = opponent(thisp);
        for (int i = 0; i < 8; i++) {
            ret += count(otherp, c, around[i]);
        }
        return ret;
    }

    /**
     * count the flips that would happen if you put a 
     * stone at position c and looking in the direction dir.
     */
    int count(int otherp, int c, int dir) {
        int ret = 0;
        if (A[dir + c] != otherp) {
            return 0;
        }
        for (c = dir + c; A[c] == otherp; c += dir) {
            ret++;
        }
        if (A[c] == thisp) {
            return ret;
        }
        return 0;
    }

    /**
     * returns whether the current player is allowed to place a stone at c.
     */
    public boolean canmove(int c) {
        return can[c] > 0;
    }

    /**
     * returns true iff the game is finished.
     */
    public boolean finished() {
        return free == 0 || (posmoves == 0 && move[moves - 1] == -1);
    }

    /**
     * returns true if player 1 won the game.
     */
    public boolean P1wins() {
        return points[0] > points[1];
    }

    /**
     * returns true if player 2 won the game.
     */
    public boolean P2wins() {
        return points[0] < points[1];
    }

    /**
     * return the number of stones that would flip when playing c.
     */
    public int getflips(int c) {
        return can[c];
    }

    /**
     *return the number of visible fields that are not free.
     */
    public int getcoverage() {
        return points[0] + points[1];
    }

    /* for position evaluation */
    int getbalance() {
        return points[thisp] - points[1 - thisp];
    }

    /**
     *return the total number of stones that can be flipped
     */
    int getflipsum() {
        int f = 0;
        for (int i = 0; i < N; i++) {
            f += can[all[i]];
        }
        return f;
    }

    /**
     * return the number of moves that can be done.
     */
    int getmovecount() {
        return posmoves;
    }

    /**
     * returns a certain set of number that will be used
     * to evaluate the situation. Currently, it returns 13 numbers:
     * the first 10 numbers returns the number of counters in a certain
     * kind of field. There are ten kind of fields due to symmetries.
     * The kinds are in the kind table. This table looks like this <pre>
     *       0   1   3   6   6   3   1   0
     *       1   2   4   7   7   4   2   1
     *       3   4   5   8   8   5   4   3
     *       6   7   8   9   9   8   7   6
     *       6   7   8   9   9   8   7   6
     *       3   4   5   8   8   5   4   3
     *       1   2   4   7   7   4   2   1
     *       0   1   3   6   6   3   1   0</pre>
     * But you should not care about how this table looks: What is important
     * is that we value counters regarding their position, but using symmetries to
     * limit the number of situations.
     * the 11th number (that is out[10] or out[kinds])
     * return how many counters you are ahead (negative if behind)
     * the twelth number return the number of moves you can do
     * the last number tells you the number of counters you can flip in total.
     * <p>    
     * If you add a certain feature that you think is important for
     * winning chances, do it here and change evalvectorlength. The
     * genetictrain and weightvectorclasses will automatically adjust if you recompile them.
     * you must retrain before you use the applet, beacuse the standard weigth vector in the
     * player class will then be too short.
     * @param out an optional array you want the result in (reusing memory improves the speed)
     * You can make it null if you do not have such array.
     * @return returns an evalvector. It will be out if that was not null.
     */
    public int[] getevalvector(int[] out) {
        if (out == null) {
            out = new int[evalvectorlength];
        }
        for (int i = 0; i < kinds; i++) {
            out[i] = 0;
        }
        for (int i = 0; i < N; i++) {
            int f = A[all[i]];
            if (f == thisp) {
                out[kind[all[i]]]++;
            } else if (f == 1 - thisp) {
                out[kind[all[i]]]--;
            }
        }
        out[kinds] = getbalance();
        out[kinds + 1] = getmovecount();
        out[kinds + 2] = getflipsum();
        return out;
    }

    /**
     * undo the last move. The board remembers what move that was, so you do not
     * have to give it.
     */
    public void undomove() {
        moves--;
        int opp = opponent(getplayer());
        if (move[moves] != -1) {
            A[move[moves]] = FREE;
        }
        for (undoflips[moves]--; undoflips[moves] >= 0; undoflips[moves]--) {
            A[undoflip[moves][undoflips[moves]]] = opp;
        }
        setcan();
    }

    /**
     *returns a line of text describing the situation.
     */
    public String statusmessage() {
        if (finished()) {
            if (P1wins()) {
                return "RED wins with " + points[P1] + " against " + points[P2];
            }
            if (P2wins()) {
                return "GREEN wins with " + points[P2] + " against " + points[P1];
            }
            return "The game ended in DRAW";
        }
        if (thisp == P1) {
            return "RED to move:" + points[P1] + " (green: " + points[P2] + ")";
        }
        return "GREEN to move:" + points[P2] + " (red: " + points[P1] + ")";
    }
}