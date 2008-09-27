package aima.search.demos;

/*
 * Created on 13/05/2005
 *
 * TODO Clase generadora de los paneles de Calculadoras
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Silvano Christian Gomez, Juan Marcelo Ferreira Aranda, Arturo Molinas
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class nPuzzleWindows extends JPanel implements KeyListener,
		ComponentListener {
	private int partida[];
	private int tamanho = 3;// Maneja el tamaño del problema Puzzle
	private final BorderLayout esquema_principal;
	private final JPanel panelCentro, panelDisplay, JPsetN, JPpath, JPnodes;// Paneles
	// del
	// contenedor principal
	private final JPanel panelNumerico, panelMain; // Subpaneles del
	// panelCentro
	private JButton botones_numericos[];// Botones
	private JButton b_hash, b_cerrar, b_evaluar, b_ayuda;
	final JButton b_setN;
	// para el Subpanel panelCentro
	private final JLabel etiqueta;
	private final JTextField display_profundidad, display_n, display_nodos;
	private JLabel JLsetN, JLpath, JLnodes, JLempty;
	private boolean resp_resultado;
	final boolean display_push = false;
	private final String memoria = new String("0");

	// Constructos General
	public nPuzzleWindows() {
		super();

		esquema_principal = new BorderLayout(5, 10);// Este es el equema
		// principal donde se dibuja
		// todos los components
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
		panelCentro.add(panelMain, BorderLayout.SOUTH);
		panelCentro.add(panelNumerico, BorderLayout.NORTH);

		// Se agraga la etiqueta del dato Profundidad
		JPpath = new JPanel();
		JPpath.setLayout(new GridLayout(1, 2));
		JLpath = new JLabel();
		JLpath = new JLabel("Profundidad");
		JLpath.setHorizontalAlignment(SwingConstants.LEFT);
		JLpath.setFont(new Font("Serif", Font.PLAIN, 18));
		display_profundidad = new JTextField("", 60);
		display_profundidad.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_profundidad.setHorizontalAlignment(SwingConstants.RIGHT);
		display_profundidad.setEditable(false);
		display_profundidad.setBackground(new Color(255, 255, 255));
		display_profundidad.addKeyListener(this);
		JPpath.add(JLpath);
		JPpath.add(display_profundidad);

		// Se agraga la etiqueta del dato Nodos Expandidos
		JPnodes = new JPanel();
		JPnodes.setLayout(new GridLayout(1, 2));
		JLnodes = new JLabel();
		JLnodes = new JLabel("Nodos expandidos");
		JLnodes.setHorizontalAlignment(SwingConstants.LEFT);
		JLnodes.setFont(new Font("Serif", Font.PLAIN, 18));
		display_nodos = new JTextField("0", 60);
		display_nodos.setFont(new Font("Serif", Font.PLAIN, 24));
		display_nodos.setHorizontalAlignment(SwingConstants.RIGHT);
		display_nodos.setEditable(false);
		display_nodos.setBackground(new Color(255, 255, 255));
		display_nodos.setForeground(new Color(255, 0, 0));
		display_nodos.addKeyListener(this);
		JPnodes.add(JLnodes);
		JPnodes.add(display_nodos);

		// Armamos el panel donde se insertara el tamaño de N
		JPsetN = new JPanel();
		JPsetN.setLayout(new GridLayout(1, 3));
		JLsetN = new JLabel();
		JLsetN = new JLabel("Tamaño");
		JLsetN.setHorizontalAlignment(SwingConstants.LEFT);
		JLsetN.setFont(new Font("Serif", Font.PLAIN, 18));
		display_n = new JTextField("", 60);
		display_n.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_n.setHorizontalAlignment(SwingConstants.RIGHT);
		display_n.setEditable(true);
		display_n.setBackground(new Color(255, 255, 255));
		display_n.addKeyListener(this);
		b_setN = new JButton("Cambiar");
		b_setN.setToolTipText("Fija el valor de N y recalcula la matriz");
		b_setN.setFont(new Font("Arial", Font.BOLD, 12));
		b_setN.setMnemonic(8);
		b_setN.setForeground(new Color(255, 0, 0));
		JPsetN.add(JLsetN);
		JPsetN.add(display_n);
		JPsetN.add(b_setN);

		// Crear Panel de Display
		panelDisplay = new JPanel();
		panelDisplay.setLayout(new GridLayout(3, 1, -1, -1));
		panelDisplay.setBackground(new Color(255, 255, 255));
		panelDisplay.add(JPsetN);
		panelDisplay.add(JPpath);
		panelDisplay.add(JPnodes);

		/*
		 * Agregamos el panel central, panel izquierdo y Caja de Texto del
		 * display_profundidad al Contenedor principal
		 */
		this.add(panelCentro, BorderLayout.CENTER);
		// this.add(panelIzq, BorderLayout.WEST);
		this.add(panelDisplay, BorderLayout.SOUTH);

		/*
		 * Agregamos los manejadores de eventos para todos los botones definidos
		 */
		/**
		 * for (int i = 0; i < this.tamanho * this.tamanho; i++) { // Manejador
		 * de // los botones // numericos
		 * botones_numericos[i].addMouseMotionListener(manejador);
		 * botones_numericos[i].addMouseListener(manejador);
		 * botones_numericos[i].addActionListener(manejador);
		 * botones_numericos[i].addKeyListener(this); }
		 * 
		 * b_evaluar.addMouseListener(manejador);
		 * b_evaluar.addMouseMotionListener(manejador);
		 * b_evaluar.addActionListener(manejador);
		 * b_evaluar.addKeyListener(this); b_ayuda.addMouseListener(manejador);
		 * b_ayuda.addMouseMotionListener(manejador);
		 * b_ayuda.addActionListener(manejador); b_ayuda.addKeyListener(this);
		 * b_hash.addMouseListener(manejador);
		 * b_hash.addMouseMotionListener(manejador);
		 * b_hash.addActionListener(manejador); b_hash.addKeyListener(this);
		 * b_cerrar.addMouseListener(manejador);
		 * b_cerrar.addMouseMotionListener(manejador);
		 * b_cerrar.addActionListener(manejador); b_cerrar.addKeyListener(this); //
		 * *********************************************************************************** //
		 * Fijar los valores por defecto del Frame
		 */
		b_cerrar.requestFocus();// Dejamos esta sentencia que es util cuando el
		// panel se carga en un
		// Frame.
	}

	// Constructor Generar con tamaño del Puzzle como parametro
	public nPuzzleWindows(int tamanho) {
		super();
		this.tamanho = tamanho;

		esquema_principal = new BorderLayout(5, 10);// Este es el equema
		// principal donde se dibuja
		// todos los components
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

		JLempty = new JLabel();
		JLabel JLempty2 = new JLabel();
		JLempty = new JLabel(" ");
		// Se agraga la etiqueta del dato Profundidad
		JPpath = new JPanel();
		JPpath.setLayout(new GridLayout(1, 3));
		JLpath = new JLabel();
		JLpath = new JLabel("Profundidad");
		JLpath.setHorizontalAlignment(SwingConstants.LEFT);
		JLpath.setFont(new Font("Serif", Font.PLAIN, 18));
		display_profundidad = new JTextField("", 60);
		display_profundidad.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_profundidad.setHorizontalAlignment(SwingConstants.RIGHT);
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
		display_nodos.setFont(new Font("Serif", Font.PLAIN, 24));
		display_nodos.setHorizontalAlignment(SwingConstants.RIGHT);
		display_nodos.setEditable(false);
		display_nodos.setBackground(new Color(255, 255, 255));
		display_nodos.setForeground(new Color(255, 0, 0));
		display_nodos.addKeyListener(this);
		JPnodes.add(JLnodes);
		JPnodes.add(display_nodos);
		JPnodes.add(JLempty2);
		// Armamos el panel donde se insertara el tamaño de N
		JPsetN = new JPanel();
		JPsetN.setLayout(new GridLayout(1, 3));
		JLsetN = new JLabel();
		JLsetN = new JLabel("Tamaño");
		JLsetN.setHorizontalAlignment(SwingConstants.LEFT);
		JLsetN.setFont(new Font("Serif", Font.PLAIN, 18));
		display_n = new JTextField("", 60);
		display_n.setFont(new Font("Monospaced", Font.PLAIN, 24));
		display_n.setHorizontalAlignment(SwingConstants.RIGHT);
		display_n.setEditable(true);
		display_n.setBackground(new Color(255, 255, 255));
		display_n.addKeyListener(this);
		b_setN = new JButton("Cambiar");
		b_setN.setToolTipText("Fija el valor de N y recalcula la matriz");
		b_setN.setFont(new Font("Arial", Font.BOLD, 12));
		b_setN.setMnemonic(8);
		b_setN.setForeground(new Color(255, 0, 0));
		JPsetN.add(JLsetN);
		JPsetN.add(display_n);
		JPsetN.add(b_setN);

		// Crear Panel de Display
		panelDisplay = new JPanel();
		panelDisplay.setLayout(new GridLayout(3, 1, -1, -1));
		panelDisplay.setBackground(new Color(255, 255, 255));
		panelDisplay.add(JPsetN);
		panelDisplay.add(JPpath);
		panelDisplay.add(JPnodes);
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

		b_evaluar.addMouseListener(manejador);
		b_evaluar.addMouseMotionListener(manejador);
		b_evaluar.addActionListener(manejador);
		b_evaluar.addKeyListener(this);
		b_ayuda.addMouseListener(manejador);
		b_ayuda.addMouseMotionListener(manejador);
		b_ayuda.addActionListener(manejador);
		b_ayuda.addKeyListener(this);
		b_hash.addMouseListener(manejador);
		b_hash.addMouseMotionListener(manejador);
		b_hash.addActionListener(manejador);
		b_hash.addKeyListener(this);
		b_cerrar.addMouseListener(manejador);
		b_cerrar.addMouseMotionListener(manejador);
		b_cerrar.addActionListener(manejador);
		b_cerrar.addKeyListener(this);
		b_setN.addMouseListener(manejador);
		b_setN.addMouseMotionListener(manejador);
		b_setN.addActionListener(manejador);
		b_setN.addKeyListener(this);

		/***********************************************************************
		 * // Fijar los valores por defecto del Frame
		 * 
		 */
		b_cerrar.requestFocus();// Dejamos esta sentencia que es util cuando el
		// panel se carga en un
		// Frame.

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
			botones_numericos[i].setFont(new Font("Arial", Font.BOLD, 20));
		}
		partida[tamanho * tamanho - 1] = 0;
		botones_numericos[tamanho * tamanho - 1] = new JButton(String
				.valueOf(" "));
		botones_numericos[tamanho * tamanho - 1].setToolTipText(String
				.valueOf(" "));
		botones_numericos[tamanho * tamanho - 1].setForeground(new Color(0, 0,
				255));
		botones_numericos[tamanho * tamanho - 1].setFont(new Font("Arial",
				Font.BOLD, 20));
	}

	private void crear_botones_principales() {

		b_hash = new JButton("Dispersar");
		b_hash.setToolTipText("Dispersa los valores del Puzzle");
		b_hash.setFont(new Font("Arial", Font.BOLD, 12));
		b_hash.setMnemonic(8);
		b_hash.setForeground(new Color(255, 0, 0));

		b_cerrar = new JButton("Salir");
		b_cerrar.setToolTipText("Cierra el Juego");
		b_cerrar.setFont(new Font("Arial", Font.BOLD, 14));
		b_cerrar.setMnemonic(127);
		b_cerrar.setForeground(new Color(255, 0, 0));

		b_evaluar = new JButton("Resolver PI");
		b_evaluar.setToolTipText("Resuelve el Puzzle");
		b_evaluar.setFont(new Font("Arial", Font.BOLD, 12));
		b_evaluar.setMnemonic('R');
		b_evaluar.setForeground(new Color(255, 0, 0));

		b_ayuda = new JButton("Ayuda");
		b_ayuda.setToolTipText("Abre Ventana de Ayuda");
		b_ayuda.setFont(new Font("Arial", Font.BOLD, 12));
		b_ayuda.setMnemonic('y');
		b_ayuda.setForeground(new Color(255, 0, 0));

		panelMain.add(b_evaluar);
		panelMain.add(b_hash);
		panelMain.add(b_cerrar);

	}

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

	@Override
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < tamanho * tamanho - 1; i++) {
			int position = this.getPositionOf(i);
			result = 37 * result + position;
		}
		return result;
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
		b_cerrar.requestFocus();// Se pasa el enfoque el boton "Cerar"
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

			if (evento.getSource() == b_ayuda)
				;// Aqui debe llamar al menu de ayuda

			else if (evento.getSource() == b_evaluar) {// Boton Evaluar
				// Expresion
				JTextArea areadeSalida = new JTextArea(2, 40);
				areadeSalida.setBackground(new Color(255, 0, 0));
				areadeSalida.setFont(new Font("Serif", Font.BOLD, 12));
				areadeSalida.setEditable(false);
				String salida;

			}// Fin del Bloque "Evaluar"
			else if (evento.getSource() == b_cerrar) {
				display_profundidad.setText("");
				display_nodos.setText("0");
				resp_resultado = false;
			} else if (evento.getSource() == b_hash) {
				if (display_profundidad.getText().length() > 1)
					display_profundidad
							.setText(display_profundidad.getText().substring(0,
									display_profundidad.getText().length() - 1));
				else
					display_profundidad.setText("");
				resp_resultado = false;
			} else if (evento.getSource() == b_setN) {
				JTextArea areadeSalida = new JTextArea(2, 40);// Para mensajes
				// de Error
				// emergentes
				// Validar numero
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
					}
				} catch (Exception e) {
					String salida = "La entrada debe ser numerico";
					areadeSalida.setText(salida);
					JOptionPane.showMessageDialog(areadeSalida, areadeSalida);
				}
				resp_resultado = false;
			} else if (evento.getSource() == display_n) {
				display_profundidad.setText("");
				display_n.selectAll();
				// redimensionar la tabla
				resp_resultado = false;
			}

		}// Fin del metodo actionPerformed

		// Metodo para redimensionar el tablero del Puzzle
		private void redimensionartablero() {
			panelNumerico.removeAll();
			panelNumerico.setLayout(new GridLayout(tamanho, tamanho, 2, 2));
			botones_numericos = new JButton[tamanho * tamanho];
			partida = new int[tamanho * tamanho];
			for (int i = 0; i < tamanho * tamanho - 1; i++) {
				partida[i] = i + 1;
				botones_numericos[i] = new JButton(String.valueOf(i + 1));
				botones_numericos[i].setToolTipText(String.valueOf(i + 1));
				botones_numericos[i].setForeground(new Color(0, 0, 255));
				botones_numericos[i].setFont(new Font("Arial", Font.BOLD, 20));
			}
			partida[tamanho * tamanho - 1] = 0;
			botones_numericos[tamanho * tamanho - 1] = new JButton(String
					.valueOf(" "));
			botones_numericos[tamanho * tamanho - 1].setToolTipText(String
					.valueOf(" "));
			botones_numericos[tamanho * tamanho - 1].setForeground(new Color(0,
					0, 255));
			botones_numericos[tamanho * tamanho - 1].setFont(new Font("Arial",
					Font.BOLD, 20));
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

			if (evento.getSource() == b_evaluar)
				b_evaluar.requestFocus();
			else if (evento.getSource() == b_ayuda)
				b_ayuda.requestFocus();
			else if (evento.getSource() == b_hash)
				b_hash.requestFocus();
			else if (evento.getSource() == b_cerrar)
				b_cerrar.requestFocus();

		}// fin metodo mouseMoved

	}// Fin de la clase privada

	// Metodo main de la clase Calculadora, no se implementa
	public static void main(String args[]) {
	}

}// Fin clase Calculadora
