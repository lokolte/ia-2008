package othello;
import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * a boardview is a canvas (a rectangle that can be displayed on the screen and painted on)
 * that can display a board. It can be used in two ways
 * <ol>
 * <li>by an applet. In this case the boardview listens to the mouse,
 * and lets the players do moves. It also starts an animation for fading in any
 * changes.
 * <li>By genetictrain. Animation is no longer needed, and it does not listen to the mouse. 
 * </ol>
 */
class boardview extends Canvas
        implements Runnable {

    board b;//the board that is viewed
    int fieldsize = 70;//size in pixels of a field.
    int SX, SY;//the total size in pixels of this canvas
    reversi top;//the applet that uses this boardview
    boolean wait = false;//if wait==true the mouse will be ignored
    player[] players = {null, null};

    public player[] getPlayers() {
        return players;
    }
    boolean[] computer = {false, true};
    //wether the computer plays that player.
    int[] oldboard; //a copy of the A table of the board.

    String statusbartext = "";//the text under the applet
    Color[] r2g = {Color.black,
        new Color(213, 43, 0),
        new Color(171, 85, 0),
        new Color(128, 128, 0),
        new Color(85, 171, 0),
        new Color(43, 213, 0),
        Color.white
    };//the colors for the morph
    int framenumber;//how far we are in morphing
    int frames = r2g.length;//the total number of frames
    Graphics g;
    Frame superframe;//only in non-applet mode

    /**
     * constructor for applet mode   
     */
    public boardview(board ib, reversi ir) {
        top = ir;
        b = ib;
        myresize(b.X, b.Y);
        players[0] = new player(b, 0, this,4,"miniMax");
        players[1] = new player(b, 1, this,4,"aleatorio");
        statusbartext = b.statusmessage();
    }

    /**
     * constructor in non-applet mode   
     */
    public boardview(board ib) {
        b = ib;
        wait = true;
        myresize(b.X, b.Y);
        display();
    }

    /**
     * creates a frame that shows this boardview. Used in nonapplet-mode.   
     */
    public void display() {

        superframe = new Frame("reversi");
        superframe.resize(SX + 20, SY + 20);
        superframe.add("Center", this);
        superframe.show();
    }

    /**
     * this method is called if the user clicked the mouse.
     * @param evt an object that contains details on the click.
     * @param x the horizontal position of the cursor
     * @param y the vertical position
     * @return true to indicate success
     */
    public boolean mouseDown(Event evt, int x, int y) {
        if (x >= SX || y >= SY || wait) {
            return true;
        }
        clicked(b.co(x / fieldsize, y / fieldsize));
        return true;
    }

    /**
     * do the given move on the board.
     * @param m the move to be done.
     */
    public void domove(int m) {
        highlightoff();//stops the morph

        b.domove(m);//do the move

        statusbartext = b.statusmessage();//set the message

        highlighton();//starts the morph

    }

    /**
     * undo the last move on the board.
     */
    public void undomove() {
        highlightoff();
        b.undomove();
        statusbartext = b.statusmessage();
        highlighton();
    }

    void clicked(int c) {//called if user clicked field c
        mensajeEstado("JUGADOR 0 -> P/A: "+this.getPlayers()[0].getMaxlevel()+"/"+this.getPlayers()[0].getAlgoritmo()+
                "    JUGADOR 1 -> P/A: "+this.getPlayers()[1].getMaxlevel()+"/"+this.getPlayers()[1].getAlgoritmo());
        if (!computer[b.getplayer()]) {
            if (b.posmoves == 0) {
                domove(-1);
            } else if (b.canmove(c)) {
                domove(c);
            }
        }
        if (computer[b.getplayer()]) {
            computermove();
        }
        
    }

    void computermove() {
        message("IA "+b.getplayer()+" esta calculando su proxima jugada...");
        wait = true;
        players[b.getplayer()].ask("Please do your move.");
    /*the computer player will start calculating, and call
    answer with its answer.*/
    }

    /**
     * you can indicate here whether computer players must be used.
     * @param a true if a computer player must be used for the first 
     * player. False if the user operates this player.
     * @param b same for second player.
     */
    public void setplayers(boolean a, boolean b) {
        computer[0] = a;
        computer[1] = b;
    }
    /*resize this object.*/

    void myresize(int x, int y) {
        SX = x * fieldsize;
        SY = y * fieldsize;
        resize(SX, SY + 20);//the 20 is room for the message under the screen.

    }
    boolean painting = false;

    /**
     * this method is called by the operating system if the frame must be redrawn, and by
     * the rest of the program if something changed on the board. It is synchronized 
     * because genetictrain calls this method very often, and we do not want these paints
     * to happen in parallel.
     */
    public void paint(Graphics newg) {
        synchronized (this) {
            if (painting) {
                return;
            }
            painting = true;
        }
        g = newg;
        for (int j = 0; j < b.Y; j++) {
            for (int i = 0; i < b.X; i++) {
                paintfield(i, j);
            }
        }
        paintstatusbar();
        painting = false;
    }

    /**
     * paints the text under the board.
     */
    void paintstatusbar() {
        g.setColor(Color.black);
        g.drawString(statusbartext, 10, SY + 10);
    }

    /**
     * this method determines if the stone on the given spot is changing color.
     * @return 0 if it keeps the same color, 1 if fading from red to green, 2
     * if it is fading from green to read..
     */
    int fading(int i, int j) {
        if (oldboard == null) {
            return 0;
        }
        g = getGraphics();
        int x = b.co(i, j);
        if (oldboard[x] == b.P1 && b.get(x) == b.P2) {
            return 1;
        }
        if (oldboard[x] == b.P2 && b.get(x) == b.P1) {
            return 2;
        }
        return 0;
    }/*0 means not, 1 r->g, 2 g->r*/

    Color back[] = {new Color(128, 128, 128), new Color(100, 100, 100)};
    Color c1 = new Color(255, 0, 0);
    Color c2 = new Color(0, 255, 0);

    /**
     * paint one field 
     */
    void paintfield(int i, int j) {
        paintback(i, j);
        paintnormalstone(i, j);
    }

    /**
     * is called every 150 milliseconds. It must fade the colors of the stones that 
     * recently changed.
     */
    void fade() {
        if (framenumber == -1) {
            return;
        }
        if (framenumber >= frames) {
            repaint();
            framenumber = -1;
            return;
        }
        for (int j = 0; j < b.Y; j++) {
            for (int i = 0; i < b.X; i++) {
                switch (fading(i, j)) {
                    case 0:
                        break;
                    case 1:
                        paintback(i, j);
                        paintstone(i, j, r2g[framenumber]);
                        break;
                    case 2:
                        paintback(i, j);
                        paintstone(i, j, r2g[frames - 1 - framenumber]);//r2g[frames-1-framenumber]

                        break;
                }
            }
        }
        framenumber++;
    }

    /**
     * paint the empty fields: just the background color. 
     */
    void paintback(int i, int j) {
        int si = i * fieldsize, sj = j * fieldsize;
        g.setColor(back[(i + j) % 2]);
        g.fillRect(si, sj, fieldsize, fieldsize);
    }

    /**
     * paint a stone in the given color.
     */
    void paintstone(int i, int j, Color c) {
        int si = i * fieldsize, sj = j * fieldsize;
        g.setColor(c);
        g.fillRect(si + 2, sj + 2, fieldsize - 4, fieldsize - 4);
    }

    /**
     * paint a field with a stone if any, or display the
     * number of flips for that field.
     */
    void paintnormalstone(int i, int j) {
        int v;
        boolean canmove;
        int flips;
        synchronized (this) {
            v = b.get(i, j);
            canmove = b.canmove(b.co(i, j));
            flips = b.getflips(b.co(i, j));
        }
        if (v == b.P1) {
            paintstone(i, j, c1);
        } else if (v == b.P2) {
            paintstone(i, j, c2);
        } else if (canmove) {
            int si = i * fieldsize, sj = j * fieldsize;
            g.setColor(Color.black);
            g.drawString("" + flips, si + fieldsize / 2, sj + fieldsize / 2);
        }
    }

    void message(String s) {
        top.message(s);
    }
    void mensajeEstado(String s) {
        top.mensajeEstado(s);
    }

    /**
     * this method is called by movers to return return the move they want to do.
     * @param s s is not used. It is a little joke: it can be used by callers to give
     * a comment to this function.
     * @param m the move to be done.
     */
    public void answer(String s, int m) {
        oldboard = (int[]) b.A.clone();
        domove(m);
        wait = false;
        message("");
    }

    /**
     * stops the animation (called when another move is already done).
     */
    void highlightoff() {
        oldboard = (int[]) b.A.clone();
        repaint();
    }

    /**
     * start the colorfade animation. called after something changed.
     */
    void highlighton() {
        repaint();
        framenumber = 0;
    }
    boolean keeponrunning;

    /**
     * is called if a new thread is created for the animation. 
     */
    public void start() {
        keeponrunning = true;
        new Thread(this).start();
    }

    /**
     * is called if someone wants the thread to stop.
     */
    public void stop() {
        keeponrunning = false;
    }

    /**
     * this method is the one that runs in its own thread to do the animation.
     * it calls fade() every 150 milliseconds.
     */
    public void run() {
        while (keeponrunning) {
            try {
                Thread.sleep(150);
            } catch (Exception e) {
            }
            fade();
        }
    }
}