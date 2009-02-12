/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmetal.problems.BiObjective;

/**
 *
 * @author Alida
 */
import jmetal.base.*;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Permutation;
import java.io.*;

public class QAP extends Problem {

    public int numberOfCities_;
    public double[][] distanceMatrix_;
    public double[][] flujo1;
    public double[][] flujo2;

   
    public QAP(String filename) throws FileNotFoundException, IOException {
        numberOfVariables_ = 1;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "QAP";

        solutionType_ = SolutionType_.Permutation;

        variableType_ = new VariableType_[numberOfVariables_];
        length_ = new int[numberOfVariables_];

        variableType_[0] = Enum.valueOf(VariableType_.class, "Permutation");

        readProblem(filename);
        System.out.println(numberOfCities_);
        length_[0] = numberOfCities_;
        
      /* 
       System.out.println();
       System.out.println("Matriz de flujo1");
          for (int k = 0; k < numberOfCities_; k++) {
                    for (int j =0; j < numberOfCities_; j++) {
                       System.out.print(flujo1[k][j] + "\t");
                    } // for
                    System.out.println();
            } // for
       
       System.out.println();
       System.out.println("Matriz de flujo2");
          for (int k = 0; k < numberOfCities_; k++) {
                    for (int j =0; j < numberOfCities_; j++) {
                       System.out.print(flujo2[k][j] + "\t");
                    } // for
                    System.out.println();
            } // for
         
         System.out.println();
         System.out.println("Matriz de distancias");
         for (int k = 0; k < numberOfCities_; k++) {
                for (int j =0; j < numberOfCities_; j++) {
                   System.out.print(distanceMatrix_[k][j] + "\t");
                } 
                System.out.println();
          }*/
    } 


    /** 
     * Evaluates a solution 
     * @param solution The solution to evaluate
     */
    public void evaluate(Solution solution) {
        double fitness1, fitness2;
        int x, y;
        fitness1 = 0.0;
        fitness2 = 0.0;
        
      for (int i = 0; i < (numberOfCities_ ); i++) {
           for (int j = 0; j < (numberOfCities_ ); j++) {
                x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
                y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[j];
  
            fitness1 += distanceMatrix_[i][j] * flujo1[x][y];
           }
      }
        
         for (int i = 0; i < (numberOfCities_ ); i++) {
           for (int j = 0; j < (numberOfCities_ ); j++) {
                x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
                y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[j];
  
            fitness2 += distanceMatrix_[i][j] * flujo2[x][y];
           }
      }
            
            solution.setObjective(0, fitness1);
            solution.setObjective(1, fitness2);
      
    } // evaluate

    public void readProblem(String fileName) throws FileNotFoundException,
            IOException {

        Reader inputFile = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(fileName)));

        StreamTokenizer token = new StreamTokenizer(inputFile);
        try {
            token.nextToken();
           
            numberOfCities_ = (int) token.nval;

            distanceMatrix_ = new double[numberOfCities_][numberOfCities_];
            flujo1 = new double[numberOfCities_][numberOfCities_];
            flujo2 = new double[numberOfCities_][numberOfCities_];

            // Cargar objetivo 1
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                   flujo1[k][j] = token.nval;
                } 
            } 
            
             // Cargar objetivo 2
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    flujo2[k][j] = token.nval;
                } 
            } 
            
            // Carga de distancias
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    distanceMatrix_[k][j] = token.nval;
                } 
            }
            
        } // try
        catch (Exception e) {
            System.err.println("QAP.readProblem(): error when reading data file " + e);
            System.exit(1);
        } // catch

    } // readProblem

}
