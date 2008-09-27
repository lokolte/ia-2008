package aima.search.demos;

import javax.swing.JApplet;

public class nPuzzlePrincipal extends JApplet {

	@Override
	public void init() {
		nPuzzleWindows nPuzzle = new nPuzzleWindows(4);
		getContentPane().add(nPuzzle);
		setSize(608, 350);
		this.addComponentListener(nPuzzle);// Escucha los Eventos del Componete
		// Applet
	}
}