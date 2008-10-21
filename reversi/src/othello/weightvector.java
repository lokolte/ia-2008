package othello;
import java.awt.*;
import java.util.*;
import java.io.*;
/**
 * this is a data-holding object that mainly just contains numbers
 * that are used by the evaluation function. You could reuse this class
 * for solving other problems with genetic algorithms. Note that the
 * static members of this class determine what the weight vectors look like,
 * so you would have to edit this part and recompile.
 */
class weightvector {

    /**
     *the most important thing is a: it contains
     * the weights we are looking for. name is just a comment. At the moment 
     * it stores the operators used to et this weight vector: M for mutation,
     * avg for average of the rest of the population, 
     */
    static int n = 2 * board.evalvectorlength + 1;
    int[] a = new int[n];
    String name;
    static int[] min = new int[n];
    static int[] max = new int[n];
    

    static {
        for (int i = 0; i < n - 1; i++) {
            min[i] = -10;
            max[i] = 10;
        }
        min[n - 1] = 5;
        max[n - 1] = 60;
    }

    /**
     * create a new weigth vector filled with random numbers.
     */
    public weightvector() {/*random initialisation*/
        for (int i = 0; i < n; i++) {
            a[i] = rand(min[i], max[i] + 1);
        }
        name = randomname();
    }

    /**
     * create a random name for the comment
     */
    String randomname() {
        String r = "_";
        for (int i = 0; i < 2; i++) {
            r += (char) (rand(26) + 'a');
        }
        return r;
    }

    /**
     * make a copy of the given vector with small changes.
     */
    public weightvector(weightvector w) {/*mutated copy*/
        for (int i = 0; i < n; i++) {
            a[i] = bound(w.a[i] - 1 + rand(0, 3), min[i], max[i]);
        }
        name = "M" + w.name;
    }

    /**
     * creates a new vector with the average of the given population.
     */
    public weightvector(weightvector[] pop) {
        for (int i = 0; i < n; i++) {
            a[i] = 0;
        }
        for (int p = 0; p < pop.length; p++) {
            for (int i = 0; i < n; i++) {
                a[i] += pop[p].a[i];
            }
        }
        for (int i = 0; i < n; i++) {
            a[i] = a[i] / pop.length;
        }
        name = "avg";
    }

    /**
     *create a new weight vector from the given string. This constructor is
     * compatible with the toString() method.
     */
    public weightvector(String s) {
        StringTokenizer st = new StringTokenizer(s);
        for (int i = 0; i < n; i++) {
            a[i] = file.num(st.nextToken());
        }
        name = st.nextToken();
    }

    public String toString() {/*convert to string*/
        String s = "" + a[0];
        for (int i = 1; i < n; i++) {
            s += " " + a[i];
        }
        s += " " + name;
        return s;
    }

    /**
     *crosses this weight vector with the given weight vector. It returns
     * an array with two new weight vectors, that are random mixtures of the
     * two parents.
     */
    public weightvector[] crossover(weightvector w) {
        weightvector[] r = {new weightvector(), new weightvector()};
        for (int i = 0; i < n; i++) {
            int x = rand(0, 2);
            r[x].a[i] = a[i];
            r[1 - x].a[i] = w.a[i];
        }
        String s = randomname();
        r[0].name = "X" + s;
        r[1].name = "x" + s;
        return r;
    }

    int rand(int a, int b)//return random number r:a<=r<b
    {
        return a + rand(b - a);
    }

    int rand(int r)//return random number r:0<=x<r
    {
        return (int) (Math.random() * r);
    }

    int bound(int x, int min, int max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }
}