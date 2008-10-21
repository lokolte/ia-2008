package othello;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;
/**
 * this is a window for popping up to create a new game. the user
 * can set what kind of game he wants, and then press cancel or start.
 */
class newgamewindow extends JFrame {

    reversi r;
    Choice[] cm = {new Choice(), new Choice()};
    Button start = new Button("start");
    Button cancel = new Button("cancel");
    TextField playerOneDepth = new TextField("4", 1);
    TextField playerTwoDepth = new TextField("4", 1);
    /**
     * create a new newgamewindow. It shown immediately. You can use this
     * dialog only once.
     * @param ir the applet that wants a new game.
     */
    public newgamewindow(reversi ir) {
        super("Nueva Jugada");
        r = ir;
        r.bview.wait = true;
        setLayout(new GridLayout(3, 4, 2, 2));
        cm[0].add("Minimax");
        cm[0].add("Alfabeta");
        cm[0].add("Aleatorio");
        cm[0].add("Humano");
        cm[1].add("Minimax");
        cm[1].add("Alfabeta");
        cm[1].add("Aleatorio");
        cm[1].add("Humano");
        add(new Label("Jugador 1 "));
        add(cm[0]);
        add(new Label("Jugador 2 "));
        add(cm[1]);
        add(new Label("Profundidad P1"));
        add(playerOneDepth);
        add(new Label("Profundidad P2"));
        add(playerTwoDepth);
        add(new Label(""));
        add(new Label(""));
        add(start);
        add(cancel);
        setSize(380, 120);
        setLocation(100, 100);
        setVisible(true);
    }

    /**
     * this method is called any time a button is pressed or an option selected.
     */
    public boolean action(Event ev, Object o) {
        if (ev.target == start) {
            r.newgame();
            r.bview.setplayers(
                    cm[0].getSelectedIndex() == 1,
                    cm[1].getSelectedIndex() == 0);
            r.bview.wait = false;
            dispose();
        } else if (ev.target == cancel) {
            r.bview.wait = false;
            dispose();
        }
        return true;
    }
}