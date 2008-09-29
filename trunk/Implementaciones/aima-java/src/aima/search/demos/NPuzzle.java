/*
 * Created on Sep 12, 2004
 *
 */
package aima.search.demos;

import aima.datastructures.LIFOQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.npuzzle.ManhattanHeuristicFunction;
import aima.search.npuzzle.MisplacedTilleHeuristicFunction;
import aima.search.npuzzle.NPuzzleBoard;
import aima.search.npuzzle.NPuzzleGoalTest;
import aima.search.npuzzle.NPuzzleSuccessorFunction;
import aima.search.uninformed.IterativeDeepeningSearch;

/**
 * @author Ravi Mohan
 * 
 */
public class NPuzzle {

    public static int tam = 3;
    public static double tiempo = 0;

    public double getTiempoDeEjecucion() {
        return tiempo;
    }

    public void setTiempoDeEjecucion(double tiempo) {
        this.tiempo = tiempo;
    }
    // static NPuzzleBoard random1 = new NPuzzleBoard(new int[] { 0, 2, 3, 4, 5,
    // 6, 7, 8, 9, 10, 12, 1, 13, 14, 11, 15 }, tam);

    private static NPuzzleBoard TableroAzar(int tam) {
        int[] miarray = new int[tam * tam];
        for (int i = 0; i < tam * tam; i++) {
            miarray[i] = i;
        }

        NPuzzleBoard random1 = new NPuzzleBoard(randomizar(miarray), tam);
        return random1;
    }

    public static int[] randomizar(final int[] ints) {
        final Random rd = new Random();
        for (int i = 0; i < ints.length; i++) {
            final int ci = ints[i];
            final int ni = Math.abs(rd.nextInt() % ints.length);
            ints[i] = ints[ni];
            ints[ni] = ci;
        }
        return ints;
    }

    /**
     * Resuelve el problema A* pasandole un vector
     * de la partida que se tiene que resolver.
     * 
     * @autor Guido Casco
     * @param partida
     */
    public List resolverAAsterisco(int[] partida, List cola) {
        long inicio, fin;
        NPuzzleBoard random1 = TableroAzar(tam);
        random1.setBoard(partida);

        System.out.println("--------------------------------------------");

        inicio = System.nanoTime();
        cola = npuzzlePuzzleAStarDemo(random1, cola);
        fin = System.nanoTime();

        tiempo = (fin - inicio) / 1000000.0;

        System.out.println("Y ha tardado: " + ((fin - inicio) / 1000000.0) + " segundos");

        return cola;
    }

    private List npuzzlePuzzleAStarDemo(NPuzzleBoard random1, List cola) {

        System.out.println("Tablero Problema");
        System.out.println(random1.toString());
        System.out.println("Tablero Meta");
        NPuzzleGoalTest meta = new NPuzzleGoalTest();
        meta.generarMeta(tam);
        System.out.println(meta.getGoalBoard().toString());
        
        System.out.println("\nNPuzzle AStar Search (MisplacedTileHeursitic)-->");
        GraphSearch grafoSearch = new GraphSearch();
        try {
            Problem problem = new Problem(random1,
                    new NPuzzleSuccessorFunction(), new NPuzzleGoalTest(),
                    new MisplacedTilleHeuristicFunction());
            
            
            
            Search search = new AStarSearch(grafoSearch);
            SearchAgent agent = new SearchAgent(problem, search);
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cola = grafoSearch.getMovimientos();
        
        /*while(!cola.isEmpty()) {
            System.out.println("" + cola.get().toString());
        }*/
        
        
        return cola;
    }

    /**
     * Resuelve el problema A* pasandole un vector
     * de la partida que se tiene que resolver.
     * 
     * @autor Guido Casco
     * @param partida
     */
    public List resolverIDL(int[] partida, List cola) {
        long inicio,  fin;
        NPuzzleBoard random1 = TableroAzar(tam);
        random1.setBoard(partida);

        System.out.println("--------------------------------------------");

        inicio = System.nanoTime();
        npuzzlePuzzleIDLSDemo(random1, cola);
        fin = System.nanoTime();

        tiempo = (fin - inicio) / 1000000.0;

        System.out.println("Y ha tardado: " + ((fin - inicio) / 1000000.0) + " segundos");

        return cola;

    }

    private List npuzzlePuzzleIDLSDemo(NPuzzleBoard random1, List cola) {
        System.out.println("Tablero Problema");
        System.out.println(random1.toString());
        System.out.println("Tablero Meta");
        NPuzzleGoalTest meta = new NPuzzleGoalTest();
        meta.generarMeta(tam);
        System.out.println(meta.getGoalBoard().toString());

        System.out.println("\nEightPuzzleDemo Iterative DLS -->");
        Search search = null;
        SearchAgent agent = null;
        Problem problem = null;
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch();
        try {
            problem = new Problem(random1,
                    new NPuzzleSuccessorFunction(), new NPuzzleGoalTest());
            search = ids;
            agent = new SearchAgent(problem, search);
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cola = ids.getMovimientos();
        
        return cola;
    }
    
    
    private String cantNodosExpandidos = ""; //Cantidad de Nodos Expandidos
    private String profundidad = "";         //Profundidad Maxima


    public String getCantidadNodosExpandidos() {
        return cantNodosExpandidos;
    }

    public void setCantidadNodosExpantidos(String cantNodosExpandidos) {
        this.cantNodosExpandidos = cantNodosExpandidos;
    }

    public String getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(String profundidad) {
        this.profundidad = profundidad;
    }

    private void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);

            if (key.compareTo("nodesExpanded") == 0) {
                setCantidadNodosExpantidos(property);
            } else if (key.compareTo("maxQueueSize") == 0) {
                this.setProfundidad(property);
            }
        }

    }

    private void printActions(List actions) {
        System.out.println("Tamanho de la Lista de Action: " + actions.size());
        
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}