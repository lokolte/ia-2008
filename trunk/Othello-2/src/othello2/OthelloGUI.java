package othello2;

/**
 ******************************************************************************
 * The OthelloGUI presents the Othello game in a slick Swing based interface.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;

public class OthelloGUI extends JFrame implements OthelloConstants, Observer {

    VisualOthelloBoard othelloModel;
    OthelloMenuListener menuListener;
    private JMenuBar menuBar = new JMenuBar();
    private JMenuItem quitItem,  skipMove, aiConfigMenu,  webItem,  aboutItem, 
            newABvsAB, newABvsMM,newABvsRm,newMMvsMM,newMMvsRm, newRmvsRm,newHvsH,newHvsAB,newHvsMM,newHvsRm;
    private JCheckBoxMenuItem optionShowMoves,  optionLastMove;
    private OthelloGUI t_this;
    private JPanel statusBar;
    private JLabel statusText;
    private JProgressBar statusProgress;
    private DecimalFormat format = new DecimalFormat("00");
    private int aiDepth = STARTING_DEPTH;
    private int ProfundidadJugador1 = STARTING_DEPTH;
    private int ProfundidadJugador2 = STARTING_DEPTH;
    private String algoritmoJugador1="miniMax";
    private String algoritmoJugador2="alphaBeta";

    public OthelloGUI(String title) {
        setTitle(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setJMenuBar(menuBar);
        setLayout(new BorderLayout());
        othelloModel = new VisualOthelloBoard(new Othello(false, true, aiDepth,"miniMax"));
        othelloModel.setSize(600, 600);
        othelloModel.addMouseListener(new VisualOthelloMouseListener());

        buildMenuBar();
        buildStatusBar();
        super.add(othelloModel);
        t_this = this;
        othelloModel.getOthello().addObserver(this);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);


    }

    public void buildStatusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusText = new JLabel("Bienvenido a Othello.", JLabel.LEFT);
        statusBar.add(statusText, BorderLayout.WEST);
        statusProgress = new JProgressBar(0, 0);
        statusProgress.setIndeterminate(true);
        statusProgress.setIndeterminate(false);
        statusBar.add(statusProgress, BorderLayout.EAST);
        this.add(statusBar, BorderLayout.SOUTH);

    }

    public void buildMenuBar() {
        menuListener = new OthelloMenuListener();
        JMenu fileMenu = new JMenu("Game");

        fileMenu.setMnemonic('F');

                
        JMenu newItem = new JMenu("Nuevo");
        fileMenu.add(newItem);
        newABvsAB = newItem.add("AlfaBeta vs AlfaBeta");
        newABvsMM = newItem.add("AIFaBeta vs Minimax");
        newABvsRm = newItem.add("AIFaBeta vs Aleatorio");
        newMMvsMM = newItem.add("Minimax vs Minimax");
        newMMvsRm = newItem.add("Minimax vs Ramdom");
        newRmvsRm = newItem.add("Aleatorio vs Aleatorio");
        newHvsH = newItem.add("Humano vs Humano");
        newHvsAB = newItem.add("Humano vs AlfaBeta");
        newHvsMM = newItem.add("Humano vs Minimax");
        newHvsRm = newItem.add("Humano vs Aleatorio");
        
        //new0Humans.setEnabled( false );

        //skipMove = fileMenu.add("Skip Move");
        fileMenu.addSeparator();

        quitItem = fileMenu.add("Quit");

        newItem.setMnemonic('N');
        //skipMove.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
        quitItem.setAccelerator(KeyStroke.getKeyStroke('Q', Event.CTRL_MASK));



        menuBar.add(fileMenu);

        JMenu optionsMenu = new JMenu("Opciones");
        optionShowMoves = (JCheckBoxMenuItem) optionsMenu.add(new JCheckBoxMenuItem("Mostrar posible movimientos", false));
        optionLastMove = (JCheckBoxMenuItem) optionsMenu.add(new JCheckBoxMenuItem("Mostrar ultimo movimiento", false));
        optionsMenu.addSeparator();
        aiConfigMenu = optionsMenu.add("Profundidad de Busqueda");
        menuBar.add(optionsMenu);

        JMenu helpMenu = new JMenu("Ayuda");
        webItem = helpMenu.add("Visite el Homepage");
        helpMenu.addSeparator();
        aboutItem = helpMenu.add("Sobre Othello");
        menuBar.add(helpMenu);

        // Set Action Listeners
        optionShowMoves.addActionListener(menuListener);
        optionLastMove.addActionListener(menuListener);
        aboutItem.addActionListener(menuListener);
        aiConfigMenu.addActionListener(menuListener);
        newABvsAB.addActionListener(menuListener);
        newABvsMM.addActionListener(menuListener);
        newABvsRm.addActionListener(menuListener);
        newMMvsMM.addActionListener(menuListener);
        newMMvsRm.addActionListener(menuListener);
        newRmvsRm.addActionListener(menuListener);
        newHvsH.addActionListener(menuListener);
        newHvsAB.addActionListener(menuListener);
        newHvsMM.addActionListener(menuListener);
        newHvsRm.addActionListener(menuListener);
//        skipMove.addActionListener(menuListener);
        quitItem.addActionListener(menuListener);

    }

    public void update(Observable observerable, Object o) {
        Board b = othelloModel.getBoard();
        if (othelloModel.getOthello().isFinished()) {
            String winner = "";
            if (b.getCount(1) > b.getCount(2)) {
                winner = "Light Wins.";
            } else if (b.getCount(2) > b.getCount(1)) {
                winner = "Dark Wins.";
            } else {
                winner = "A Tie.";
            }
            winner += "\n\n";
            winner += "BLANCO: " + format.format(b.getCount(1)) + ", NEGRO: " + format.format(b.getCount(2));
            statusProgress.setIndeterminate(false);

            JOptionPane.showMessageDialog(null,
                    winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        } else {
            if (othelloModel.getOthello().nextIsAI()) {
                statusProgress.setIndeterminate(true);
            } else {
                statusProgress.setIndeterminate(false);
            }
        }
        othelloModel.repaint();
        statusText.setText("BLANCO: " + format.format(b.getCount(1)) + ", NEGRO: " + format.format(b.getCount(2)) +
                "\n BLANCO P/A: " + othelloModel.getOthello().getPlayer(LIGHT_PIECE).getDepth() + "/" + othelloModel.getOthello().getPlayer(LIGHT_PIECE).getAlgoritmo() +
                "\n NEGRO P/A: " + othelloModel.getOthello().getPlayer(DARK_PIECE).getDepth() + "/" + othelloModel.getOthello().getPlayer(DARK_PIECE).getAlgoritmo());
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
            System.exit(0);
        }
        super.processWindowEvent(e);
    }

    public static void center(JFrame frame) {
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 
     */
    public static void main(String[] args) throws Exception {
        OthelloGUI o = new OthelloGUI("Michael Farrelly's Othello");
        o.setSize(600, 600);
        center(o);
        o.setVisible(true);
    }

    class OthelloMenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == newABvsAB) {// AlfaBeta vs AlfaBeta
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newABvsMM) {// AlfaBeta vs Minimax
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newABvsRm) {// Alfabeta vs Aleatorio
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newMMvsMM) {// Minimax vs Minimax
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newMMvsRm) {// Minimax vs Aleatorio
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newRmvsRm) {// Aleatorio vs Aleatorio
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newHvsH) {// Humano vs Humano
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newHvsAB) {// Humano vs AlfaBeta
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newHvsMM) {// Humano vs Minimax
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == newHvsRm) {// Humano vs Aleatorio
            /**}else if (e.getSource() == newHvsAB) {
                othelloModel.resetGame(false, false, aiDepth,algoritmoJugador2);
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == new1Humans) {
                othelloModel.resetGame(false, true, ProfundidadJugador2,algoritmoJugador2);
                othelloModel.getOthello().addObserver(t_this);
            } else if (e.getSource() == new0Humans) {
                othelloModel.resetGame(true, true, ProfundidadJugador1, ProfundidadJugador2,algoritmoJugador1,algoritmoJugador2);
                othelloModel.getOthello().addObserver(t_this);**/
            } else if (e.getSource() == quitItem) {
                dispose();
                System.exit(0);
            } else if (e.getSource() == skipMove) {
                othelloModel.skipMove();
            } else if (e.getSource() == aiConfigMenu) {
                String input = JOptionPane.showInputDialog(null,
                        "Ingrese la Profundidad del Jugador (BLANCO) 1:", "AI-DEPTH", JOptionPane.INFORMATION_MESSAGE);
                ProfundidadJugador1 = new Integer(input).intValue();
                input = JOptionPane.showInputDialog(null,
                        "Ingrese la Profundidad del Jugador (NEGRO) 2", "AI-DEPTH", JOptionPane.INFORMATION_MESSAGE);
                ProfundidadJugador2 = new Integer(input).intValue();

                othelloModel.getOthello().getPlayer(LIGHT_PIECE).setDepth(ProfundidadJugador1);
                othelloModel.getOthello().getPlayer(DARK_PIECE).setDepth(ProfundidadJugador2);
            } else if (e.getSource() == optionShowMoves || e.getSource() == optionLastMove) {
                repaint();
            } else if (e.getSource() == aboutItem) {
                JOptionPane.showMessageDialog(null,
                        "Othello 2006\nMichael Farrelly\n<michael.farrelly@uleth.ca>", "About Othello", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class VisualOthelloMouseListener extends MouseAdapter {

        public void mouseReleased(MouseEvent e) {
            othelloModel.hit(e.getX(), e.getY());
        }
    }

    public class VisualOthelloBoard extends Component {

        private Othello othello;
        private int nextMove = Board.DARK_PIECE;

        public VisualOthelloBoard(Othello o) {
            this.othello = o;
            Thread t = new Thread(o);
            t.start();
        //othello.nextMove();
        }

        public void resetGame() {
            this.othello = new Othello(false, true, 4,"miniMax");
            Thread t = new Thread(othello);
            t.start();
            //othello.nextMove();
            repaint();
        }

        public void resetGame(boolean p1, boolean p2, int profundidad1, int profundidad2, String algo1, String algo2) {
            this.othello = new Othello(p1, p2, profundidad1, profundidad2,algo1,algo2);
            Thread t = new Thread(othello);
            t.start();
            //othello.nextMove();
            repaint();
        }
        //

        public void resetGame(boolean p1, boolean p2, int profundidad1,String algo2) {
            this.othello = new Othello(p1, p2, profundidad1,algo2);
            Thread t = new Thread(othello);
            t.start();
            //othello.nextMove();
            repaint();
        }

        public void skipMove() {
            this.othello.skipMove();
            repaint();
        }

        public Board getBoard() {
            return othello.getBoard();
        }

        public Othello getOthello() {
            return othello;
        }

        public void hit(int x, int y) {
            Board board = othello.getBoard();
            int l = x / (getWidth() / BOARD_LENGTH);
            int h = y / (getHeight() / BOARD_LENGTH);

            if (l < Board.BOARD_LENGTH && h < BOARD_LENGTH && l >= 0 && h >= 0 && board.getPosition(l, h) <= NO_PIECE) {
                //board.setPosition(l,h,nextMove);
                othello.move(l, h);

                repaint();

            }

        }

        public void paint(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Board board = othello.getBoard();

            int l = getWidth() / BOARD_LENGTH;
            int h = getHeight() / BOARD_LENGTH;

            // Draw Background -- White
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLACK);
            int[] moves = othello.moves;
            int m = 0;

            for (int j = 0; j < BOARD_LENGTH; j++) {
                for (int i = 0; i < BOARD_LENGTH; i++) {

                    // Draws square for position [i,j]
                    g.drawRect(i * l, j * h, l, h);
                    if (optionShowMoves.getState()) {
                        if (othello.moves[m] == Board.convertPosition(i, j)) {
                            g.setColor(Color.GREEN);
                            g.fillRect(i * l + 1, j * h + 1, l - 1, h - 1);
                            g.setColor(Color.BLACK);
                            m++;
                        }
                    }
                    if (optionLastMove.getState()) {
                        if (board.getLastMove() == Board.convertPosition(i, j)) {
                            g.setColor(Color.RED);
                            g.fillRect(i * l + 1, j * h + 1, l - 1, h - 1);
                            g.setColor(Color.BLACK);
                        }
                    }
                    switch (board.getPosition(i, j)) {
                        case DARK_PIECE:
                            g.fillOval(i * l + 2, j * h + 2, l - 4, h - 4);
                            break;
                        case LIGHT_PIECE:
                            g.drawOval(i * l + 2, j * h + 2, l - 4, h - 4);
                            break;
                    }
                }
            }
        }
    }
}
