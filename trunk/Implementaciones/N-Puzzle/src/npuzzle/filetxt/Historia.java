/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package npuzzle.filetxt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Guido Diego y Bruno
 */
public class Historia extends JFrame {

    String nombreArchivo = "";

    public Historia() {
    }

    public Historia(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String readFile() {
        File nombre = new File(this.getNombreArchivo());
        String resultado = "";
        //StringBuffer bufer = "";

        // si nombre existe, mostrar informacion sobre el  
        if (nombre.isFile()) {
                // anexar el contenido del archivo a areaSalida  
                try {
                    BufferedReader entrada = new BufferedReader(
                            new FileReader(nombre));
                    //bufer = new StringBuffer();  
                    String texto;


                    while ((texto = entrada.readLine()) != null) {
                        resultado = resultado + texto + "\n";

                    //bufer.append( texto + "\n" );  
                    }

                } // procesar los problemas en el procesamiento del archivo  
                catch (IOException excepcionES) {
                    JOptionPane.showMessageDialog(this, "ERROR EN ARCHIVO",
                            "ERROR EN ARCHIVO", JOptionPane.ERROR_MESSAGE);
                }
        
        } else {
            resultado = "Error: No se pudo leer el archivo";
        }

        return resultado;
    }
    
    /*public String writeFile( ) {
        File nombre = new File(this.getNombreArchivo());
        String resultado = "";
        //StringBuffer bufer = "";

        // si nombre existe, mostrar informacion sobre el  
        if (nombre.exists()) {
            if (nombre.isFile()) {
                // anexar el contenido del archivo a areaSalida  
                try {
                     FileOutputStream fos = new FileOutputStream(nombre);

                    //bufer = new StringBuffer();  
                    String texto;


                    while ((texto = entrada.readLine()) != null) {
                        resultado = resultado + texto + "\n";

                    //bufer.append( texto + "\n" );  
                    }

                } // procesar los problemas en el procesamiento del archivo  
                catch (IOException excepcionES) {
                    JOptionPane.showMessageDialog(this, "ERROR EN ARCHIVO",
                            "ERROR EN ARCHIVO", JOptionPane.ERROR_MESSAGE);
                }
            } // fin de instruccion if  

        }

        return resultado;
    }*/
         
}
