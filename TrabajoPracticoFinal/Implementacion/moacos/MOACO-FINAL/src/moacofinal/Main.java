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
        String parametros = new String(new char[100]); // indica el archivo de parametros a utilizar
        MOACO alg; // instancia del algoritmo
        Problem prob; // instancia del problema a resolver
        String cad = new String(new char[300]);
        String pr = new String(new char[60]);

/*
        String archivoProblema = "kroac100.tsp.txt";
        String ruta = "d:\\instancias-parametros\\";
        String archivoParametros = "parametros_tsp.txt";
        String algoritmoEjecucion = "MOACS"; //Valores: MOACS, M3AS
        String problemaEjecucion = "TSP"; //Valores: TSP, QAP, VRP
*/
        String archivoProblema = "kroac100.tsp.txt";
        String ruta = "d:\\instancias-parametros\\";
        String archivoParametros = "parametros_tsp.txt";
        String algoritmoEjecucion = "MOACS"; //Valores: MOACS, M3AS
        String problemaEjecucion = "TSP"; //Valores: TSP, QAP, VRP


        parametros = ruta + archivoParametros;
        pr = archivoProblema;

        System.out.println();
        System.out.println("*********Ejecutandose con los sgtes parametros*********");
        System.out.println();
        System.out.println("archivoProblema = " + archivoProblema);
        System.out.println("ruta = " + ruta);
        System.out.println("archivoParametros = " + archivoParametros);
        System.out.println("algoritmoEjecucion = " + algoritmoEjecucion);
        System.out.println("problemaEjecucion = " + problemaEjecucion);
        System.out.println();

        cad = "d:\\instancias-parametros\\PARETO-" + pr + "." + algoritmoEjecucion + "." + problemaEjecucion + ".pareto-" + System.currentTimeMillis();

        // Ejecutar el algoritmo indicado
        if (problemaEjecucion.compareTo("TSP") == 0) {
            prob = new TSP(ruta + archivoProblema);
            if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                alg = new MOACS(prob, parametros);
            } else {
                alg = new M3AS(prob, parametros);
            }
            alg.ejecutarTSP();
            alg.pareto.listarSoluciones(prob, cad);

        } else if (problemaEjecucion.compareTo("QAP") == 0) {
            prob = new TSP(ruta + archivoProblema);
            if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                alg = new MOACS(prob, parametros);
            } else {
                alg = new M3AS(prob, parametros);
            }
            alg.ejecutarQAP();
            alg.pareto.listarSoluciones(prob, cad);

        } else if (problemaEjecucion.compareTo("VRP") == 0) {
            prob = new TSP(ruta + archivoProblema);
            if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                alg = new MOACS(prob, parametros);
            } else {
                alg = new M3AS(prob, parametros);
            }
            alg.ejecutarVRP();
            alg.pareto.listarSolucionesVRP(prob, cad);

        }
        System.out.println("Pareto Generado: " + cad);
        System.out.println("Ejecución Finalizada...");

    }
}

