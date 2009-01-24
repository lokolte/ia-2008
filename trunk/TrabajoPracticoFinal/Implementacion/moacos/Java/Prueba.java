/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Gomez
 */
public class Prueba
{
	protected static double[][] matrizAdy;
        private static double[][] matrizAdy2;
	protected static int size;
        static int ciudades;
        static int objetivos;
	public Prueba(String file)
	{
            int i;
            BufferedReader reader  =null; 
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            String linea = null;
            boolean numerico = false;
            while (!numerico) {
                try{
                    linea = reader.readLine();
                    size = Integer.parseInt(linea);
                    numerico = true;
                }catch(IOException e){
                    numerico = true;
                }catch(NullPointerException e){
                    numerico = true;
                }catch(NumberFormatException ex){
                    numerico = false;
                }
            }
        }
        private static int getSize(){
            return size;
        }


        public void cargar_estado(String file) {
        // en la primera linea posee el tamaño del problema

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // El archivo file posee las dos matrices de adyacencia separadas por '\n'
        String linea;
        try {
            ciudades = Integer.parseInt(reader.readLine());
            objetivos = Integer.parseInt(reader.readLine());
            matrizAdy = new double[ciudades][ciudades];
            matrizAdy2 = new double[ciudades][ciudades];
            int count = 0;
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                count = count + 1;
                if (count <= ciudades) {
                    // hacer token
                    subCadena = linea.split("\\s");
                    for (int i = 0; i < subCadena.length; i++) {
                        matrizAdy[count - 1][i] = Double.valueOf(subCadena[i]).doubleValue();
                    }
                } else if (count > 101) {
                    subCadena = linea.split("\\s");
                    for (int i = 0; i < subCadena.length; i++) {
                        matrizAdy2[count - 102][i] = Double.valueOf(subCadena[i]).doubleValue();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error de conversion");
        }
    }
        public static void imprimirMatriz(double matriz[][]){
         for(int i=0;i<ciudades;i++){
             for(int j=0;j<ciudades;j++){
                 System.out.print(matriz[i][j]+" ");
             }
             System.out.println();
         }
        }
        
        public static void main(String[] args) {
            Prueba proft = new Prueba ("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias\\KROAB100.TSP.TXT");
            System.out.println(getSize());
            proft.cargar_estado("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias\\KROAB100.TSP.TXT");
            imprimirMatriz(matrizAdy);
            imprimirMatriz(matrizAdy2);
        }

        
}
