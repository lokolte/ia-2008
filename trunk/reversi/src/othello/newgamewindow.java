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
    Choice eleccionA = new Choice();
    Choice eleccionB = new Choice();
    
    Button start = new Button("Iniciar");
    Button cancel = new Button("Cancelar");
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
        eleccionA.add("Minimax");
        eleccionA.add("Alfabeta");
        eleccionA.add("Aleatorio");
        eleccionA.add("Humano");
        eleccionB.add("Minimax");
        eleccionB.add("Alfabeta");
        eleccionB.add("Aleatorio");
        eleccionB.add("Humano");
        add(new Label("Jugador 1 "));
        add(eleccionA);
        add(new Label("Jugador 2 "));
        add(eleccionB);
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
                    eleccionA.getSelectedItem(),eleccionB.getSelectedItem(),
                    playerOneDepth.getText(),playerTwoDepth.getText());
            r.bview.players[0].setAlgoritmo(eleccionA.getSelectedItem());
            r.bview.players[1].setAlgoritmo(eleccionB.getSelectedItem());
            r.bview.players[0].setMaxlevel(Integer.parseInt(playerOneDepth.getText()));
            r.bview.players[1].setMaxlevel(Integer.parseInt(playerTwoDepth.getText()));
            r.bview.wait = false;
            dispose();
        } else if (ev.target == cancel) {

            r.bview.wait = false;
            dispose();
        }
        return true;
    }
}