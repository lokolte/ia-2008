package othello;
import java.awt.*;
import java.util.*;
import java.io.*;
/** 
 * reversi.java is an applet for playing the reversi othello board game against the computer.
 * Designed by 
 * <a href="http://www.bluering.nl">bluering software development </a>. 
 * You can send questions about this source to 
 * <a href="mailto:sieuwert@bluering.nl">sieuwert@bluering.nl</a>
 * this applet can be freely used for whatever you want if you first ask permission from
 * the author.
 * <h2>project overview</h2>
 * This applet consists of several classes.We have put all the classes in one file 
 * for easy downloading. To get the right 
 * javadoc documentation, we had to make all classes and many methods public. You
 * might want to put each class in a separate file with the filename equal to the class 
 * name. You can also make all classes except reversi non-public (just remove the word public)
 * to avoid errors. The drawback is that you will not see those classes in the documentation.
 * <p>the javadoc files contain much of the comment, but not all of it. Check the sourcecode
 * for the last details. The list of all classes is:
 * <dl>
 *<dt>reversi</dt><dd>the toplevel applet class. It contains and handles the buttons. </dd>
 *<dt>newgamewindow</dt><dd>the window that allows you to set up a new game. It appears when 
 * you press new game</dd>
 *<dt>boardview</dt><dd> the class that paints the pieces. It also handles the mouse clicks.</dd>
 *<dt>board </dt><dd>The class containing the state of the board. This class knows the rules
 * of the game. Designers say it contains the game logic. It is in some way the most pure, and basic
 * class, where all others rely on.</dd>
 *<dt>player </dt><dd>the functions to calculate what move to do. This class contains
 * the minimax algorithm with the alpha-beta heuristic. With these techniques a search is
 * done to find the best position it can reach. The other classes are needed for estimating the
 * winning value of positions.</dd>
 *<dt>weightvector</dt><dd>This class contains the adjustable parameters that are needed for
 * determining the values of positions.</dd>
 *<dt>genetictrain </dt><dd>This class contains the genetic algorithm that can search the best
 * weight vector. If you want to search a good weight vector, you must run this class, and
 * in a few minutes it will select good weight vectors by letting them play against each other.</dd>
 *<dt>file</dt><dd>This is just a utility class for reading and writing files in an easy way.</dd>
 * </dl>
 * <p>
 */
public class reversi extends java.applet.Applet {

    /**
     * the interface components in this applet. slabel means status label.
     */
    Label slabel = new Label("por favor, haga un movimiento");
    Label estado=new Label ("Estado Inicial");
    board b = new board();
    boardview bview = new boardview(b, this);
    Button newgame = new Button("Nueva Jugada"),
            exit = new Button("Salir"),
            undo = new Button("Deshacer movimiento");

    /**
     * constructor for running this applet as an application
     */
    public reversi() {
    }

    /**
     * the init method is called when the applet is loaded by the browser. 
     */
    public void init() {
        setBackground(Color.white);//you can set the background color of the applet in this line
        //resize(bview.SX+30,bview.SY+100); //this line not needed in applets, they cannot resize

        Panel buttonpanel = new Panel();//panel to keep buttons together

        buttonpanel.setLayout(new GridLayout(1, 3));//add three buttons beside each other

        buttonpanel.add(newgame);
        buttonpanel.add(undo);
        buttonpanel.add(exit);
        Panel superpanel = new Panel();
        superpanel.setLayout(new GridLayout(3, 1));
        superpanel.add(slabel);
        superpanel.add(estado);
        superpanel.add(buttonpanel);
        setLayout(new BorderLayout());
        add("North", bview);
        add("South", superpanel);
    }

    /**the start method of the applet. It is called any time the window re-appears on the
     * screen. All we do here is start the animation.
     */
    public void start() {
        bview.start();
    }

    /**
     * here we stop the animation
     */
    public void stop() {
        bview.stop();
    }

    /**
     * the action method is called when a button is pressed. It first checks whether
     * bview might be busy. In that case, it does nothing. Else it undoes a move or 
     * launches a new game window.
     * @return true to indicate success
     */
    public boolean action(Event ev, Object O) {
        if (bview.wait) {
            return true;
        }
        if (ev.target == newgame) {
            new newgamewindow(this);
        } else if (ev.target == undo) {
            undo();
        } else if (ev.target == exit) {
            System.exit(0);
        }
        return true;
    }

      
    /**
     * this method is called by newgamewindow.  
     */
    public void newgame() {
        b.clear();//b.clear() restores the opening position on the board.

        bview.repaint();//this updates the screen

    }

    /**
     * this method undoes a move, if possible.
     */
    public void undo() {
        if (b.moves != 0) {
            bview.undomove();
        }
    }

    /**
     * sets a given text in the message label.
     * @param s the message to display.
     */
    public void message(String s) {
        slabel.setText(s);//set a message in the status label.

    }
    public void mensajeEstado(String s) {
        estado.setText(s);//set a message in the status label.

    }

    /**
     * this method is quick & dirty trick for running this applet as
     * an application. It is not used when a browser runs this class
     * as an applet.
     */
    public static void main(String[] ps) {
        Frame f = new Frame("reversi");
        reversi r = new reversi();
        f.resize(565, 700);
        f.add("Center", r);
        r.init();
        r.start();
        f.show();
    }
}
