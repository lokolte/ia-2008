package jmetal.solutions;

import jmetal.metaheuristics.nsgaII.*;
import jmetal.base.*;
import jmetal.base.operator.crossover.*;
import jmetal.base.operator.mutation.*;
import jmetal.base.operator.selection.*;
import jmetal.problems.*;

import jmetal.util.JMException;
import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.problems.BiObjective.*;
import jmetal.qualityIndicator.QualityIndicator;

public class VrptwNsga_main {

    public static Logger logger_;      // Logger object

    public static FileHandler fileHandler_; // FileHandler object


    /**
     * @param args Command line arguments.
     * @throws JMException 
     * @throws IOException 
     * @throws SecurityException 
     * Usage: three options
     *      - jmetal.metaheuristics.nsgaII.NSGAII_main
     *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
     *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
     */
    public static void main(String[] args) throws JMException, SecurityException, IOException {

        for (int i = 0; i < 10; i++) {


            Problem problem;         // The problem to solve
            Algorithm algorithm;         // The algorithm to use
            Operator crossover;         // Crossover operator
            Operator mutation;         // Mutation operator
            Operator selection;         // Selection operator
            
            //String name = "rc101.txt";
            String name = "c101.txt";
            String problemName = "src/files/vrptw/";
            QualityIndicator indicators; // Object to get quality indicators

            // Logger object and file to store log messages
            logger_ = Configuration.logger_;
            fileHandler_ = new FileHandler("NSGA/VRPTW/VRPTW-NSGA.log");
            logger_.addHandler(fileHandler_);

            indicators = null;
            if (args.length == 1) {
                Object[] params = {"Permutation"};
                problem = (new ProblemFactory()).getProblem(args[0], params);
            } // if
            else if (args.length == 2) {
                Object[] params = {"Permutation"};
                problem = (new ProblemFactory()).getProblem(args[0], params);
                indicators = new QualityIndicator(problem, args[1]);
            } else {
                problem = new VRPTW(problemName + name);
            }

            algorithm = new NSGAII(problem);

            // Algorithm parameters
            algorithm.setInputParameter("populationSize", 100);
            algorithm.setInputParameter("maxEvaluations", 25000);

            // Mutation and Crossover for Real codification 
            crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover");
            crossover.setParameter("probability", 0.9);
            crossover.setParameter("distributionIndex", 20.0);

            mutation = MutationFactory.getMutationOperator("SwapMutation");
            mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());
            mutation.setParameter("distributionIndex", 20.0);

            // Selection Operator 
            selection = SelectionFactory.getSelectionOperator("BinaryTournament2");

            // Add the operators to the algorithm
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            // Add the indicator object to the algorithm
            algorithm.setInputParameter("indicators", indicators);

            // Execute the Algorithm
            long initTime = System.currentTimeMillis();
            SolutionSet population = algorithm.execute();
            long estimatedTime = System.currentTimeMillis() - initTime;

            // Result messages 
            logger_.info("Total execution time: " + estimatedTime + "ms");
            logger_.info("Variables values have been writen to file VAR");
            population.printVariablesToFile("NSGA/VRPTW/VAR.txt");
            logger_.info("Objectives values have been writen to file FUN");
            population.printObjectivesToFile("NSGA/VRPTW/FUN.txt");
            population.printObjectivesToFile("c:\\instancias-parametros\\generado\\" + name + "-NSGA.txt");

            if (indicators != null) {
                logger_.info("Quality indicators");
                logger_.info("Hypervolume: " + indicators.getHypervolume(population));
                logger_.info("GD         : " + indicators.getGD(population));
                logger_.info("IGD        : " + indicators.getIGD(population));
                logger_.info("Spread     : " + indicators.getSpread(population));
                logger_.info("Epsilon    : " + indicators.getEpsilon(population));

                int evaluations = ((Integer) algorithm.getOutputParameter("evaluations")).intValue();
                logger_.info("Speed      : " + evaluations + " evaluations");
            }
        }
    }
} 
