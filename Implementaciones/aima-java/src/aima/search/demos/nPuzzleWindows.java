package aima.search.demos;

/*
 * Created on 13/05/2005
 *
 * TODO Clase generadora de los paneles de Calculadoras
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author Silvano Christian Gomez, Juan Marcelo Ferreira Aranda, Guido Casco
 * 
 * TODO Esta clase es un Panel con todo el formato de calculadora, contiene dos
 * subclases privada para manejar los eventos del raton y del teclado. La misma
 * clase calculadora se encarga de manejar algunos de los eventos producidos por
 * el teclado
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import aima.search.npuzzle.NPuzzleBoard;

public class nPuzzleWindows extends JPanel implements KeyListener,
		ComponentListener {

	private int partida[], paso;
	private int progreso;
	private int memoria[];
	private int tamanho = 3;// Maneja el tamaï¿½o del problema Puzzle
	private final BorderLayout esquema_principal;
	private final JPanel panelCentro, panelDisplay;// Paneles
	private final JPanel JPsetN;
	final JPanel JPjugadas, JPbotoncitos, JPbotoncitos2;
	private final JPanel JPpath, JPnodes, JPsecuencias;
	private int[] posiciones;
	private int xpos;
	private int ypos;
	private int white_array_pos_prv;
	// del
	// contenedor principal
	private final JPanel panelNumerico, panelMain; // Subpaneles del
	// panelCentro
	private String[] secuencias;
	private JButton botones_numericos[];// Botones
	private JButton b_hash, b_memoria, b_evaluarPI, b_evaluarAstar;
	private final JButton b_setN;
	final JButton b_secuencias, b_salir;
	// para el Subpanel panelCentro
	private final JLabel etiqueta;
	private final JTextField display_profundidad, display_time;
	private final JTextField display_n;
	final JTextField display_nodos;
	private JLabel JLsetN, JLpath, JLnodes, JLsecuencias, JLtime;
	final JTextArea display_secuencias;
	final JLabel JLempty;
	private boolean resp_resultado;
	final boolean display_push = false;

	// Constructor Generar con tamaï¿½o del Puzzle como parametro
	public nPuzzleWindows() {
		super();

		esquema_principal = new BorderLayout(5, 10);// Este es el equema
		// principal donde se dibuja
		// todos los components
		secuencias = null;
		this.setLayout(esquema_principal);
		// Se crea el objeto que se encargara de manejar todos los eventos del
		// programa
		ManejadorEventoBotones manejador = new ManejadorEventoBotones(this);
		this.addKeyListener(this);// EL objeto de la clase Calculadora se
		// encarga de manejar los eventos tipo
		// KeyListener
		/*
		 * Panel Central para operaciones principales, numerico, y tecla de
		 * borrar, evaluar, ayuda, etc
		 */
		panelCentro = new JPanel();
		panelCentro.setLayout(new BorderLayout(3, 3));

		// Panel para evaluar, ayuda, retroceder y borrar
		panelMain = new JPanel();
		panelMain.setLayout(new GridLayout(1, 4));
		// Crear botones para evaluar, ayuda, retroceder y borrar
		etiqueta = new JLabel();
		partida = new int[tamanho * tamanho];
		crear_botones_principales();

		// Panel principal de Botones numericos y operaciones principales
		panelNumerico = new JPanel();
		panelNumerico.setLayout(new GridLayout(tamanho, tamanho, 2, 2));
		// Crear y agregar botones numericos
		botones_numericos = new JButton[tamanho * tamanho];
		crear_botones_numericos(tamanho);
		for (int i = 0; i < tamanho * tamanho; i++) {
			panelNumerico.add(botones_numericos[i]);
		}

		// Agregamos los paneles numericos y principal al panel central

		/*
		 * Panel Izquierdo para funciones y caracteres especiales
		 */

		JLabel JLempty2 = new JLabel();
		JLempty = new JLabel(" ");
		// Se agraga la etiqueta del dato Profundidad
		JPpath = new JPanel();
		JPpath.setLayout(new GridLayout(1, 3));
		JLpath = new JLabel();
		JLpath = new JLabel("Profundidad");
		JLpath.setHorizontalAlignment(SwingConstants.LEFT);
		JLpath.setFont(new Font("Serif", Font.PLAIN, 18));
		display_profundidad = new JTextField("0", 60);
		display_profundidad.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_profundidad.setHorizontalAlignment(SwingConstants.LEFT);
		display_profundidad.setEditable(false);
		display_profundidad.setBackground(new Color(255, 255, 255));
		display_profundidad.addKeyListener(this);
		JPpath.add(JLpath);
		JPpath.add(display_profundidad);
		JPpath.add(JLempty);
		// Se agraga la etiqueta del dato Nodos Expandidos
		JPnodes = new JPanel();
		JPnodes.setLayout(new GridLayout(1, 3));
		JLnodes = new JLabel();
		JLnodes = new JLabel("Nodos expandidos");
		JLnodes.setHorizontalAlignment(SwingConstants.LEFT);
		JLnodes.setFont(new Font("Serif", Font.PLAIN, 18));
		display_nodos = new JTextField("0", 60);
		display_nodos.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_nodos.setHorizontalAlignment(SwingConstants.LEFT);
		display_nodos.setEditable(false);
		display_nodos.setBackground(new Color(255, 255, 255));
		display_nodos.addKeyListener(this);
		JPnodes.add(JLnodes);
		JPnodes.add(display_nodos);
		JPnodes.add(JLempty2);
		// Se agraga la etiqueta del dato secuencia del Juego con el boton de
		// animacion
		JPsecuencias = new JPanel();
		JPsecuencias.setLayout(new GridLayout(1, 3));
		JLsecuencias = new JLabel();
		JLsecuencias = new JLabel("Secuencia de Jugadas");
		JLsecuencias.setHorizontalAlignment(SwingConstants.LEFT);
		JLsecuencias.setFont(new Font("Serif", Font.PLAIN, 18));
		display_secuencias = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(display_secuencias);
		display_secuencias.setEditable(false);
		display_secuencias.setText("");
		display_secuencias.setFont(new Font("Monospaced", Font.PLAIN, 12));
		display_secuencias.setBackground(new Color(255, 255, 255));
		display_secuencias.setEditable(true);
		display_secuencias.addKeyListener(this);
		JPsecuencias.add(JLsecuencias);
		JPsecuencias.add(scrollPane);
		// Botones de jugada y finalizacion del programa
		JPjugadas = new JPanel();
		JPjugadas.setLayout(new GridLayout(1, 3));
		JPbotoncitos = new JPanel();
		JPbotoncitos.setLayout(new GridLayout(1, 2));

		b_secuencias = new JButton("Animar");
		b_secuencias
				.setToolTipText("Juega el puzzle resuelto por el algoritmo");
		b_secuencias.setFont(new Font("Arial", Font.BOLD, 12));
		b_secuencias.setMnemonic(8);
		b_secuencias.setForeground(new Color(255, 0, 0));

		JPbotoncitos.add(new JLabel(""));
		JPbotoncitos.add(b_secuencias);
		JPsecuencias.add(JPbotoncitos);
		// Armamos el panel donde se insertara el tamaï¿½o de N
		JPsetN = new JPanel();
		JPsetN.setLayout(new GridLayout(1, 3));
		JLsetN = new JLabel();
		JLsetN = new JLabel("Tamaño del Puzzle");
		JLsetN.setHorizontalAlignment(SwingConstants.LEFT);
		JLsetN.setFont(new Font("Serif", Font.PLAIN, 18));
		display_n = new JTextField("", 60);
		display_n.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_n.setHorizontalAlignment(SwingConstants.RIGHT);
		display_n.setEditable(true);
		display_n.setBackground(new Color(255, 255, 255));
		display_n.setText("" + tamanho);
		display_n.addKeyListener(this);

		b_setN = new JButton("Cambiar");
		b_setN.setToolTipText("Fija el valor de N y recalcula la matriz");
		b_setN.setFont(new Font("Arial", Font.BOLD, 12));
		b_setN.setMnemonic(8);
		b_setN.setForeground(new Color(255, 0, 0));
		JPsetN.add(JLsetN);
		JPsetN.add(display_n);
		JPsetN.add(b_setN);
		// Creamos el display del tiempo utilizado para el proceso y el boton de
		// Terminacion del programa
		JLtime = new JLabel();
		JLtime = new JLabel("Tiempo empleado");
		JLtime.setHorizontalAlignment(SwingConstants.LEFT);
		JLtime.setFont(new Font("Serif", Font.PLAIN, 18));
		display_time = new JTextField("0", 60);
		display_time.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_time.setHorizontalAlignment(SwingConstants.LEFT);
		display_time.setEditable(false);
		display_time.setBackground(new Color(255, 255, 255));
		display_time.addKeyListener(this);

		JPbotoncitos2 = new JPanel();
		JPbotoncitos2.setLayout(new GridLayout(1, 2));
		b_salir = new JButton("Salir");
		b_salir.setToolTipText("Juega el puzzle resuelto por el algoritmo");
		b_salir.setFont(new Font("Arial", Font.BOLD, 12));
		b_salir.setMnemonic(8);
		b_salir.setForeground(new Color(255, 0, 0));
		JPbotoncitos2.add(new JLabel(""));
		JPbotoncitos2.add(b_salir);
		JPjugadas.add(JLtime);
		JPjugadas.add(display_time);
		JPjugadas.add(JPbotoncitos2);
		// Crear Panel de Display
		panelDisplay = new JPanel();
		panelDisplay.setLayout(new GridLayout(5, 1, 1, 1));
		panelDisplay.setBackground(new Color(255, 255, 255));
		panelDisplay.add(JPsetN);
		panelDisplay.add(JPpath);
		panelDisplay.add(JPnodes);
		panelDisplay.add(JPsecuencias);
		panelDisplay.add(JPjugadas);
		/*
		 * Agregamos el panel central, panel izquierdo y Caja de Texto del
		 * display_profundidad al Contenedor principal
		 */
		panelCentro.add(panelMain, BorderLayout.SOUTH);
		panelCentro.add(panelNumerico, BorderLayout.CENTER);
		this.add(panelCentro, BorderLayout.CENTER);
		this.add(panelDisplay, BorderLayout.SOUTH);
		/*
		 * Agregamos los manejadores de eventos para todos los botones definidos
		 */

		b_evaluarPI.addMouseListener(manejador);
		b_evaluarPI.addMouseMotionListener(manejador);
		b_evaluarPI.addActionListener(manejador);
		b_evaluarPI.addKeyListener(this);
		b_evaluarAstar.addMouseListener(manejador);
		b_evaluarAstar.addMouseMotionListener(manejador);
		b_evaluarAstar.addActionListener(manejador);
		b_evaluarAstar.addKeyListener(this);
		b_hash.addMouseListener(manejador);
		b_hash.addMouseMotionListener(manejador);
		b_hash.addActionListener(manejador);
		b_hash.addKeyListener(this);
		b_memoria.addMouseListener(manejador);
		b_memoria.addMouseMotionListener(manejador);
		b_memoria.addActionListener(manejador);
		b_memoria.addKeyListener(this);
		b_setN.addMouseListener(manejador);
		b_setN.addMouseMotionListener(manejador);
		b_setN.addActionListener(manejador);
		b_setN.addKeyListener(this);
		b_salir.addMouseListener(manejador);
		b_salir.addMouseMotionListener(manejador);
		b_salir.addActionListener(manejador);
		b_salir.addKeyListener(this);
		b_secuencias.addMouseListener(manejador);
		b_secuencias.addMouseMotionListener(manejador);
		b_secuencias.addActionListener(manejador);
		b_secuencias.addKeyListener(this);
	}// Fin del constructor General

	/*
	 * Metodo privado para crear los botones numericos y de aperadores
	 */
	private void crear_botones_numericos(int tamanho) {

		for (int i = 0; i < tamanho * tamanho - 1; i++) {
			partida[i] = i + 1;
			botones_numericos[i] = new JButton(String.valueOf(i + 1));
			botones_numericos[i].setToolTipText(String.valueOf(i + 1));
			botones_numericos[i].setForeground(new Color(0, 0, 255));
			botones_numericos[i].setFont(new Font("Arial", Font.BOLD, 80));
		}
		partida[tamanho * tamanho - 1] = 0;
		memoria = partida.clone();
		botones_numericos[tamanho * tamanho - 1] = new JButton(String
				.valueOf(" "));
		botones_numericos[tamanho * tamanho - 1].setToolTipText(String
				.valueOf(" "));
		botones_numericos[tamanho * tamanho - 1].setForeground(new Color(0, 0,
				255));
		botones_numericos[tamanho * tamanho - 1].setFont(new Font("Arial",
				Font.BOLD, 80));
	}

	private void crear_botones_principales() {

		b_hash = new JButton("Dispersar");
		b_hash.setToolTipText("Dispersa los valores del Puzzle");
		b_hash.setFont(new Font("Arial", Font.BOLD, 12));
		b_hash.setMnemonic(8);
		b_hash.setForeground(new Color(255, 0, 0));

		b_memoria = new JButton("MR");
		b_memoria
				.setToolTipText("Carga el tablero con estado inicial memorizado");
		b_memoria.setFont(new Font("Arial", Font.BOLD, 14));
		b_memoria.setMnemonic(127);
		b_memoria.setForeground(new Color(255, 0, 0));

		b_evaluarPI = new JButton("Resolver IDS");
		b_evaluarPI
				.setToolTipText("Resuelve el Puzzle por metodo de Busqueda de Profundidad Iterativa");
		b_evaluarPI.setFont(new Font("Arial", Font.BOLD, 12));
		b_evaluarPI.setMnemonic('R');
		b_evaluarPI.setForeground(new Color(255, 0, 0));

		b_evaluarAstar = new JButton("Resolver A*");
		b_evaluarAstar
				.setToolTipText("Resuelve el Puzzle por el metodo de A* con numeor de piezas mal colocadas");
		b_evaluarAstar.setFont(new Font("Arial", Font.BOLD, 12));
		b_evaluarAstar.setMnemonic('y');
		b_evaluarAstar.setForeground(new Color(255, 0, 0));

		panelMain.add(b_evaluarPI);
		panelMain.add(b_evaluarAstar);
		panelMain.add(b_hash);
		panelMain.add(b_memoria);

	}

	// Funcion para randomizar un vector
	public static int[] randomizar(final int[] ints) {
		final Random rd = new Random();
		for (int i = 0; i < ints.length; i++) {
			final int ci = ints[i];
			final int ni = Math.abs(rd.nextInt() % ints.length);
			ints[i] = ints[ni];
			ints[ni] = ci;
		}
		return ints;
	}

	// Quitar

	/*
	 * Metodos para la implementar la interface KeyListener en esta clase
	 * (Calculadora)
	 */
	public void keyPressed(KeyEvent evento) {
		;
	}// No se define este metodo

	/*
	 * Metodo para la interface KeyListener de esta clase, este metodo ocurre
	 * cuando se deja de presionar una tecla, ejecuta la accion del boton
	 * definido
	 */
	public void keyReleased(KeyEvent evento) {
	}// Fin metodo keyReleased

	/*
	 * Metodo para la interface KeyListener de esta clase, este metodo ocurre
	 * cuando se presiona una tecla de accion, como Enter, ESC, SUP, etc,
	 * ejecuta la accion del boton definido
	 */
	public void keyTyped(KeyEvent evento) {
		String KeyCode = new String("" + evento.getKeyChar());
	}// Fin metodo keyTyped

	/*
	 * Implementacion de los metodos de la interface ComponentListener, se
	 * ultiliza solo el metodo componetShown, necesario para que el boton
	 * "Cerar" obtenga el enfoque.
	 */
	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}

	/*
	 * Esta es una clase privada que se encarga de manejar los eventos generados
	 * en la aplicacion cuando ocurre sobre alguno de los botones del panel
	 * numerico, principal, y de operadores
	 * 
	 * @author Silvano Christian Gomez, Juan Marcelo Ferreira Aranda, Arturo
	 * Molinas
	 * 
	 * TODO To change the template for this generated type comment go to Window -
	 * Preferences - Java - Code Style - Code Templates
	 */
	private class ManejadorEventoBotones extends MouseAdapter implements
			ActionListener, MouseMotionListener {

		nPuzzleWindows principal;

		private ManejadorEventoBotones(nPuzzleWindows principal) {
			this.principal = principal;
		}

		public void actionPerformed(ActionEvent evento) {

			if (evento.getActionCommand().compareTo("Resolver A*") == 0) {

				System.out.println("Estoy Resolviendo A*");

				NPuzzle npuzzle = new NPuzzle();

				// Pone el tamanho
				NPuzzle.tam = tamanho;
				NPuzzle.resolverAstar_Misplaced(new NPuzzleBoard(partida,
						tamanho), tamanho);

				// Pone el tiempo
				display_time.setText(String.valueOf(NPuzzle.getTiempo()));

				// Pone la profundidad
				display_profundidad.setText("" + NPuzzle.getProfundidad());

				// Pone la cantidad de nodos expandidos
				display_nodos.setText("" + NPuzzle.getNodosExpandidos());

				// Para poder poner en el texto de Secuencias
				String[] movimientos = NPuzzle.getMovimientos();
				principal.secuencias = NPuzzle.getMovimientos();
				posiciones = xycoordinatesFromAbsoluteCoordinate(getPositionOf(0));
				xpos = posiciones[0];
				ypos = posiciones[1];
				white_array_pos_prv = absoluteCoordinatesFromXYCoordinates(
						xpos, ypos);
				paso = principal.secuencias.length;
				progreso = 0;
				String movimientosString = "";
				for (int i = 0; i < movimientos.length; i++) {
					movimientosString = movimientosString + movimientos[i]
							+ " ";
				}
				display_secuencias.setText(movimientosString);

			} else if (evento.getSource() == b_evaluarPI) {// Boton Evaluar
				// Expresion
				NPuzzle mypuzzle = new NPuzzle();
				mypuzzle
						.resolverPI(new NPuzzleBoard(partida, tamanho), tamanho);
				display_time.setText(mypuzzle.getTiempo() + "");
				display_profundidad.setText(mypuzzle.getProfundidad());
				display_nodos.setText(mypuzzle.getNodosExpandidos());
				principal.secuencias = mypuzzle.getMovimientos();
				posiciones = xycoordinatesFromAbsoluteCoordinate(getPositionOf(0));
				xpos = posiciones[0];
				ypos = posiciones[1];
				white_array_pos_prv = absoluteCoordinatesFromXYCoordinates(
						xpos, ypos);
				paso = principal.secuencias.length;
				progreso = 0;
				String mov = "";
				for (int i = 0; i < mypuzzle.getMovimientos().length; i++) {
					mov = mov + " " + mypuzzle.getMovimientos()[i];
				}
				display_secuencias.setText(mov);
			}// Fin del Bloque "EvaluarPI"
			else if (evento.getSource() == b_memoria) {
				dispersarTableroMemorizado();
				resp_resultado = false;
			} else if (evento.getSource() == b_hash) {
				partida = randomizar(partida);
				memoria = partida.clone();
				dispersarTablero();
				principal.secuencias = null;
				display_time.setText("0");
				display_profundidad.setText("0");
				display_nodos.setText("0");
				display_secuencias.setText("");
				resp_resultado = false;
			} else if (evento.getSource() == b_setN) {
				JTextArea areadeSalida = new JTextArea(2, 40);// Para mensajes

				try {
					tamanho = Integer.parseInt(display_n.getText());
					if ((tamanho < 3) || (tamanho > 10)) {
						String salida = "El tamaño debe estar comprendido entre 3 y 10";
						areadeSalida.setText(salida);
						JOptionPane.showMessageDialog(areadeSalida,
								areadeSalida);
					} else {
						redimensionartablero();
						panelCentro.add(panelMain, BorderLayout.SOUTH);
						panelCentro.add(panelNumerico, BorderLayout.CENTER);
						principal.add(panelCentro, BorderLayout.CENTER);
						principal.add(panelDisplay, BorderLayout.SOUTH);
						principal.revalidate();
						principal.repaint();
						principal.secuencias = null;
						display_time.setText("0");
						display_profundidad.setText("0");
						display_nodos.setText("0");
						display_secuencias.setText("");
					}
				} catch (Exception e) {
					String salida = "La entrada debe ser numerico";
					areadeSalida.setText(salida);
					JOptionPane.showMessageDialog(areadeSalida, areadeSalida);
				}
				resp_resultado = false;
			} else if (evento.getSource() == b_salir) {
				System.exit(0);
			} else if (evento.getSource() == b_secuencias) {
				realizar_transiciones_secuencias(principal.secuencias);
			}
		}// Fin del metodo actionPerformed

		// Este metodo realiza las transiciones entre los movimientos del
		// espacio en blanco en el tablero del PUZZLE
		private void realizar_transiciones_secuencias(String[] secuencias) {
			int aux;
			if ((progreso < paso) && secuencias != null) {
				if (secuencias[progreso].compareTo("Izquierda") == 0) {
					ypos = ypos - 1;
				} else if (secuencias[progreso].compareTo("Derecha") == 0) {
					ypos = ypos + 1;
				} else if (secuencias[progreso].compareTo("Arriba") == 0) {
					xpos = xpos - 1;
				} else if (secuencias[progreso].compareTo("Abajo") == 0) {
					xpos = xpos + 1;
				}
				int white_array_pos_nxt = absoluteCoordinatesFromXYCoordinates(
						xpos, ypos);
				aux = partida[white_array_pos_prv];
				partida[white_array_pos_prv] = partida[white_array_pos_nxt];
				partida[white_array_pos_nxt] = aux;
				white_array_pos_prv = white_array_pos_nxt;
				dispersarTablero();
			}
			progreso = progreso + 1;
		}

		// Metodo que obtiene las coordenadas (x,y) de un numero dado en el
		// vector
		private int[] xycoordinatesFromAbsoluteCoordinate(int x) {
			int[] retVal = null;
			int xcoord = (x / tamanho);
			int c = (xcoord * tamanho) + 1;
			int ycoord = 0;
			while (true) {
				if (c++ > x) {
					break;
				}
				ycoord++;
			}
			retVal = new int[] { xcoord, ycoord };

			return retVal;
		}

		// Metodo que obtiene la posicion x de un numero dado en el vector
		private int getPositionOf(int val) {
			int retVal = -1;
			for (int i = 0; i < tamanho * tamanho; i++) {
				if (partida[i] == val) {
					retVal = i;
				}
			}
			if (retVal == -1) {
				int c = 1;
			}

			return retVal;
		}

		// Metodo que obtiene la posicion x en el vector partida dada las
		// coordenadas (x,y)
		private int absoluteCoordinatesFromXYCoordinates(int x, int y) {
			return x * tamanho + y;
		}

		// Metodo para dibujar el tablero randomizado
		private void dispersarTablero() {
			for (int i = 0; i < tamanho * tamanho; i++) {
				if (partida[i] == 0) {
					botones_numericos[i].setText("");
					principal.panelCentro.revalidate();
					principal.panelCentro.repaint();
				} else {
					botones_numericos[i].setText(String.valueOf(partida[i]));
					principal.panelCentro.revalidate();
					principal.panelCentro.repaint();
				}
			}
		}

		// Metodo para dibujar el tablero randomizado memorizado previamente
		private void dispersarTableroMemorizado() {
			for (int i = 0; i < tamanho * tamanho; i++) {
				partida[i] = memoria[i];
				if (memoria[i] == 0) {
					botones_numericos[i].setText("");
					principal.panelCentro.revalidate();
					principal.panelCentro.repaint();
				} else {
					botones_numericos[i].setText(String.valueOf(memoria[i]));
					principal.panelCentro.revalidate();
					principal.panelCentro.repaint();
				}
			}
			posiciones = xycoordinatesFromAbsoluteCoordinate(getPositionOf(0));
			xpos = posiciones[0];
			ypos = posiciones[1];
			white_array_pos_prv = absoluteCoordinatesFromXYCoordinates(xpos,
					ypos);
			progreso = 0;
		}

		// Metodo para redimensionar el tablero del Puzzle
		private void redimensionartablero() {
			int tamanhoFuente;
			switch (tamanho) {
			case 3:
				tamanhoFuente = 80;
				break;
			case 4:
				tamanhoFuente = 55;
				break;
			case 5:
				tamanhoFuente = 50;
				break;
			case 6:
				tamanhoFuente = 45;
				break;
			case 7:
				tamanhoFuente = 40;
				break;
			case 8:
				tamanhoFuente = 35;
				break;
			case 9:
				tamanhoFuente = 30;
				break;
			case 10:
				tamanhoFuente = 25;
				break;
			default:
				tamanhoFuente = 20;
			}
			panelNumerico.removeAll();
			panelNumerico.setLayout(new GridLayout(tamanho, tamanho, 2, 2));
			botones_numericos = new JButton[tamanho * tamanho];
			partida = new int[tamanho * tamanho];
			for (int i = 0; i < tamanho * tamanho - 1; i++) {
				partida[i] = i + 1;
				botones_numericos[i] = new JButton(String.valueOf(i + 1));
				botones_numericos[i].setToolTipText(String.valueOf(i + 1));
				botones_numericos[i].setForeground(new Color(0, 0, 255));
				botones_numericos[i].setFont(new Font("Arial", Font.BOLD,
						tamanhoFuente));
			}
			partida[tamanho * tamanho - 1] = 0;
			memoria = partida.clone();
			botones_numericos[tamanho * tamanho - 1] = new JButton(String
					.valueOf(" "));
			botones_numericos[tamanho * tamanho - 1].setToolTipText(String
					.valueOf(" "));
			botones_numericos[tamanho * tamanho - 1].setForeground(new Color(0,
					0, 255));
			botones_numericos[tamanho * tamanho - 1].setFont(new Font("Arial",
					Font.BOLD, tamanhoFuente));
			for (int i = 0; i < tamanho * tamanho; i++) {
				panelNumerico.add(botones_numericos[i]);
			}

		}

		@Override
		public void mouseDragged(MouseEvent evento) {
			;
		}// No se implementa

		// Metodo que maneja el evento de mover el mouse sobre los botones
		// numericos y principales para
		// obtener el enfoque en el boton sobre el que se posiciona el mouse
		@Override
		public void mouseMoved(MouseEvent evento) {

			if (evento.getSource() == b_evaluarPI) {
				b_evaluarPI.requestFocus();
			} else if (evento.getSource() == b_evaluarAstar) {
				b_evaluarAstar.requestFocus();
			} else if (evento.getSource() == b_hash) {
				b_hash.requestFocus();
			}
		}// fin metodo mouseMoved
	}// Fin de la clase privada

	// Metodo main de la clase Calculadora, no se implementa
	public static void main(String args[]) {
	}

}// Fin clase Calculadora
