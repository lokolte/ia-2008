/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

/**
 *
 * @author Christian Gomez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int problema; // indica el codigo del problema
        String parametros = new String(new char[100]); // indica el archivo de parametros a utilizar
        int algoritmo; // algoritmo a ejecutar
        MOACO alg; // instancia del algoritmo
        Problem prob; // instancia del problema a resolver
        String cad = new String(new char[300]);
        String pr = new String(new char[60]);
        String instancia = new String(new char[100]);
        

        problema = DefineConstantsSlave1.RC101;
        prob = new VRPTW("d:\\instancias-parametros\\rc101.txt");
        parametros = "d:\\instancias-parametros\\parametros_tsp.txt";
        pr = "rc101.txt";
        alg = new M3AS(prob, parametros);

        // Ejecutar el algoritmo indicado
        if (problema == DefineConstantsSlave1.KROAB || problema == DefineConstantsSlave1.KROAC) {
            alg.ejecutarTSP();
        } else if (problema == DefineConstantsSlave1.QAP1 || problema == DefineConstantsSlave1.QAP2) {
            alg.ejecutarQAP();
        } else if (problema == DefineConstantsSlave1.C101 || problema == DefineConstantsSlave1.RC101) {
            alg.ejecutarVRP();
        }
        cad="d:\\instancias-parametros\\"+pr+"."+".pareto";
        alg.pareto.listarSolucionesVRP(prob, cad);
        
    }

}
final class DefineConstantsSlave1 {

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
}
