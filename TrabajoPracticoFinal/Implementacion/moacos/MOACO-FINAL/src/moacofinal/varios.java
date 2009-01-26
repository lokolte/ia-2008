/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class varios
{
    	public static final int PARETO = 50;
	public static final String SLAVENAME = "slave";
	public static final int CBIANT = 1;
	public static final int CBIMC = 2;
	public static final int CCOMP = 3;
	public static final int CMAS = 4;
	public static final int CM3AS = 5;
	public static final int CMOACS = 6;
	public static final int CMOAQ = 7;
	public static final int CMOA = 8;
	public static final int CPACO = 9;
	public static final int KROAB = 10;
	public static final int KROAC = 11;
	public static final int QAP1 = 12;
	public static final int QAP2 = 13;
	public static final int C101 = 14;
	public static final int RC101 = 15;
	public static final int NTASKS = 2;
	public static final int MSGINI = 20;
	public static final int MSGDATA = 30;
	public static final int MSGCONT = 40;
	public static final int TERMINAR = 50;
        
	public static void debug(String s)
	{

                try {
                    PrintStream output = new PrintStream(
                    new FileOutputStream("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\debug.txt"));
                    output.print(s);
                    output.close();
                } catch (java.io.IOException e) {
                    System.out.println("Error al leer archivo");
                }
// simple
	}
        
        int max (int i, int j){
            if (i>=j) return i;
            else return j;
        }

	public static String outFile = new String(new char[255]);

	public static String set_outfile(String out, String prob, String inst, String alg)
	{
		out = prob;
		out += ".";
		out += inst;
		out += alg;
                
                return out;
	}

	// Comparador de cadenas no sensible a may�sculas ni min�sculas,
	//   retorna: -1 si cad es mayor que cad2
	//			 0 si son iguales
	//			 1 si cad2 es mayor que cad 
	public static int stricmp(String cad, String cad2)
	{
		int comp;
		String c2 = new String(new char[cad2.length()]);
		c2 = cad2;

                cad = String.valueOf(cad).toLowerCase();
                c2 = String.valueOf(c2).toLowerCase();
                comp = c2.compareTo(cad);
		return comp;
	}
}
