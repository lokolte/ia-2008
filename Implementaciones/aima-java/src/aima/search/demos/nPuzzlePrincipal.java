package aima.search.demos;

import javax.swing.JApplet;

public class nPuzzlePrincipal extends JApplet {

	@Override
	public void init() {
		nPuzzleWindows nPuzzle = new nPuzzleWindows();
		getContentPane().add(nPuzzle);
		setSize(700, 680);
		this.addComponentListener(nPuzzle);// Escucha los Eventos del Componete
		// Applet
	}
}