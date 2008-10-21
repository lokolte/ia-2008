package othello;
import java.awt.*;
import java.util.*;
import java.io.*;

/**This is a class for reading and writing textfiles.
 * basic use : <ul>
 * <li>make a file object
 * <li>do readopen
 * <li>do some read
 *  <li>do close
 *  <li>or:
 *  <li>make an object
 *  <li>do writeopen
 *  <li>do some writing
 *  <li>do close 
 * </ul>
 */
class file {

    BufferedReader in;
    PrintWriter out;
    boolean OK;

    /**
     * open a file for reading.
     */
    public void readopen(String f1) {
        readopen(new File(f1));
    }

    /**
     * open a file for reading.
     */
    public void readopen(File f1) {
        try {
            in = new BufferedReader(new FileReader(f1));
            OK = true;
        } catch (Exception e) {
            e.printStackTrace();
            OK = false;
        }
    }

    /**
     * read one line of the input file.
     */
    public String read() {
        if (!OK) {
            return null;
        }
        try {
            return in.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * close the file opened for reading.
     */
    public void readclose() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open a file for writing. Note that it is allowed to have both a file to
     * read and a file to write with the same file object.
     */
    public void writeopen(String f1) {
        writeopen(new File(f1));
    }

    /**
     * open a file for writing. Note that it is allowed to have both a file to
     * read and a file to write with the same file object.
     */
    public void writeopen(File f1) {
        try {
            out = new PrintWriter(new FileOutputStream(f1));
            OK = true;
        } catch (Exception e) {
            e.printStackTrace();
            OK = false;
        }
    }

    /**
     * Write out the given string as a separate line.
     */
    public void write(String s) {
        if (OK) {
            out.println(s);
        }
    }

    /**
     * close the writefile
     */
    void writeclose() {
        if (out != null) {
            out.close();
        }
    }

    /**
     * close both the file opened for reading and the one for writing (if any)
     */
    public void close() {
        readclose();
        writeclose();
    }

    /**
     * converts a String to an int.
     */
    public static int num(String s) {
        return Integer.parseInt(s.trim());
    }

    /**
     * converts a String to a double.
     */
    public static double dnum(String s) {
        return new Double(s).doubleValue();
    }
}