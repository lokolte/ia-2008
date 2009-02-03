/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.qualityIndicator.util;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;

/**
 *
 * @author Internet Designer
 */
public class Metricas {

    ConjuntoPareto pareto1;
    ConjuntoPareto pareto2;

    public static void main(String[] args) {

        Metricas miPrueba = new Metricas();

        //1RA. Metrica: Distancia al Ytrue
        String fileYtrue = "src/files/algorithms/frontpareto/resultado_frente_tsp_spea.txt"; //N ejecuciones de los 4 algoritmos
        String fileYprima = "src/files/algorithms/frontparetoprima/frontprima.txt";          //N ejecuciones de un solo algoritmo
        
        double distanciaFinal = miPrueba.getDistanciaYTrue(fileYtrue, fileYprima);
        System.out.println("Distancia Final: " + distanciaFinal);

        //2DA. Metrica: Distancia al Ytrue
        fileYprima = "src/files/algorithms/frontparetoprima/frontprima.txt";
        

        double extension = miPrueba.getExtension(fileYprima);
        System.out.println("Extension: " + extension);

         //3RA. Metrica: Distancia al Ytrue
        fileYprima = "src/files/algorithms/frontparetoprima/frontprima.txt";


        double distribucion = miPrueba.getDistribucion(fileYprima);
        System.out.println("Distribucion: " + distribucion);

    }

    public double getDistribucion (String fileYTrue){
        MetricsUtil metricsUtils = new MetricsUtil();

        pareto1 = GenerarYTrue(fileYTrue);

        SolutionSet yTrueSolutionSet = pareto1.getSolutionSet();

        double sumaCantidadMaxima = 0.0;
        double rho = 0.1 * getExtension(fileYTrue);
        for (int i = 0; i < pareto1.solutionSet.size(); i++) {

            Solution solutionYtrue = yTrueSolutionSet.get(i);
            double[] yTruePunto = {solutionYtrue.getObjective(0),solutionYtrue.getObjective(1)};

            double cantidadMaxima = 0.0;
            for (int j = 0; j < pareto1.solutionSet.size(); j++) {

                Solution solutionYPrima = yTrueSolutionSet.get(j);
                double[] yPrimaPunto = {solutionYPrima.getObjective(0),solutionYPrima.getObjective(1)};


                Double aux = metricsUtils.distance(yTruePunto, yPrimaPunto);

                if(aux > rho) {
                    cantidadMaxima++;
                }


            }
            sumaCantidadMaxima = sumaCantidadMaxima + cantidadMaxima;

        }

        double distanciaFinal = (1.0/yTrueSolutionSet.size()) * sumaCantidadMaxima;

        return distanciaFinal;

    }

    public double getExtension (String fileYTrue){
        MetricsUtil metricsUtils = new MetricsUtil();

        pareto1 = GenerarYTrue(fileYTrue);

        SolutionSet yTrueSolutionSet = pareto1.getSolutionSet();

        double sumaDistanciaMaxima = 0.0;
        for (int i = 0; i < pareto1.solutionSet.size(); i++) {

            Solution solutionYtrue = yTrueSolutionSet.get(i);
            double[] yTruePunto = {solutionYtrue.getObjective(0),solutionYtrue.getObjective(1)};

            double distanciaMaxima = Double.MIN_VALUE;
            for (int j = 0; j < pareto1.solutionSet.size(); j++) {

                Solution solutionYPrima = yTrueSolutionSet.get(j);
                double[] yPrimaPunto = {solutionYPrima.getObjective(0),solutionYPrima.getObjective(1)};


                Double aux = metricsUtils.distance(yTruePunto, yPrimaPunto);

                if(distanciaMaxima < aux) {
                    distanciaMaxima = aux;
                }
                

            }
            sumaDistanciaMaxima = sumaDistanciaMaxima + distanciaMaxima;

        }

        double distanciaFinal = Math.sqrt(sumaDistanciaMaxima);

        return distanciaFinal;

    }


    public double getDistanciaYTrue (String fileYTrue, String fileYPrima){
        MetricsUtil metricsUtils = new MetricsUtil();

        pareto1 = GenerarYTrue(fileYTrue);
        pareto2 = GenerarYTrue(fileYPrima);
        //pareto1.solutionSet.printObjectivesToFile("src/files/algorithms/frontparetopromedio/frontparetopromediospeatsp.txt");

        SolutionSet yTrueSolutionSet = pareto1.getSolutionSet();
        SolutionSet yPrimaSolutionSet = pareto2.getSolutionSet();

        double sumaDistanciaMinima = 0.0;
        for (int i = 0; i < pareto1.solutionSet.size(); i++) {

            Solution solutionYtrue = yTrueSolutionSet.get(i);
            double[] yTruePunto = {solutionYtrue.getObjective(0),solutionYtrue.getObjective(1)};

            double distanciaMinima = Double.MAX_VALUE;
            for (int j = 0; j < pareto2.solutionSet.size(); j++) {

                Solution solutionYPrima = yPrimaSolutionSet.get(j);
                double[] yPrimaPunto = {solutionYPrima.getObjective(0),solutionYPrima.getObjective(1)};


                Double aux = metricsUtils.distance(yTruePunto, yPrimaPunto);

                if(distanciaMinima > aux) {
                    distanciaMinima = aux;
                }

            }
            sumaDistanciaMinima = sumaDistanciaMinima + distanciaMinima;

        }

        double distanciaFinal = (1.0/(yTrueSolutionSet.size())) * sumaDistanciaMinima;

        return distanciaFinal;

    }


    public ConjuntoPareto GenerarYTrue(String file) {
        MetricsUtil metricsUtils = new MetricsUtil();

        SolutionSet solutionSet = metricsUtils.readNonDominatedSolutionSet(file);


        ConjuntoPareto pareto = new ConjuntoPareto();

        for (int i = 0; i < solutionSet.size(); i++) {

            double solObjetivo1 = ((Solution) solutionSet.get(i)).getObjective(0);
            double solObjetivo2 = ((Solution) solutionSet.get(i)).getObjective(1);

            if (pareto.agregarNoDominado(solObjetivo1, solObjetivo2) == 1) {
                pareto.eliminarDominados(solObjetivo1, solObjetivo2);
            }

        }
        return pareto;
    }
}
