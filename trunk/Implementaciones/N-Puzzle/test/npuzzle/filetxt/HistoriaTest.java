/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package npuzzle.filetxt;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guido Diego y Bruno
 */
public class HistoriaTest {

    public HistoriaTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getNombreArchivo method, of class Historia.
     */
    /*@Test
    public void testGetNombreArchivo() {
        System.out.println("getNombreArchivo");
        Historia instance = new Historia("HISTORIA.TXT");
        String expResult = "";
        String result = instance.getNombreArchivo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNombreArchivo method, of class Historia.
     */
    /*@Test
    public void testSetNombreArchivo() {
        System.out.println("setNombreArchivo");
        String nombreArchivo = "";
        Historia instance = new Historia();
        instance.setNombreArchivo(nombreArchivo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFile method, of class Historia.
     */
    @Test
    public void testReadFile() {
        System.out.println("readFile");
        Historia instance = new Historia("../N-Puzzle/src/npuzzle/filetxt/HISTORIA.TXT");
        
        String result = instance.readFile();
        
        System.out.println(result);
    }

}