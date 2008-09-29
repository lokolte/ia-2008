package aima.search.npuzzle;

import java.util.ArrayList;
import java.util.List;

import aima.basic.XYLocation;

/**
 * @author Ravi Mohan
 * 
 */
public class NPuzzleBoard {

    public static String LEFT = "Left";
    public static String RIGHT = "Right";
    public static String UP = "Up";
    public static String DOWN = "Down";
    public List movimientos = new ArrayList();

    public List getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List movimientos) {
        this.movimientos = movimientos;
    }
    
    
    
    int[] board;
    int tam;

    public int[] getBoard() {
        return board;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }

    public int getTam() {
        return tam;
    }

    public void setTam(int tam) {
        this.tam = tam;
    }

    public NPuzzleBoard(int atam) {
        board = new int[]{5, 4, 0, 6, 1, 8, 7, 3, 2, 9, 10, 11, 12, 13, 14,
                    15
                };
        tam = atam;

    }

    public NPuzzleBoard() {
        board = new int[]{5, 4, 0, 6, 1, 8, 7, 3, 2, 9, 10, 11, 12, 13, 14,
                    15
                };
    }

    public NPuzzleBoard(int[] aBoard, int atam) {
        board = aBoard;
        tam = atam;
    }

    private int[] xycoordinatesFromAbsoluteCoordinate(int x) {
        int[] retVal = null;
        int xcoord = (x / this.tam);
        int c = (xcoord * this.tam) + 1;
        int ycoord = 0;
        while (true) {
            if (c++ > x) {
                break;
            }
            ycoord++;
        }
        retVal = new int[]{xcoord, ycoord};

        return retVal;
    }

    private int absoluteCoordinatesFromXYCoordinates(int x, int y) {
        return x * this.tam + y;
    }

    private int getValueAt(int x, int y) {
        // refactor this use either case or a div/mod soln
        return board[absoluteCoordinatesFromXYCoordinates(x, y)];
    }

    private int getGapPosition() {
        return getPositionOf(0);
    }

    private int getPositionOf(int val) {
        int retVal = -1;
        for (int i = 0; i < this.tam * this.tam; i++) {
            if (board[i] == val) {
                retVal = i;
            }
        }
        if (retVal == -1) {
            int c = 1;
        }

        return retVal;
    }

    public XYLocation getLocationOf(int val) {
        int abspos = getPositionOf(val);
        int xpos = xycoordinatesFromAbsoluteCoordinate(abspos)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(abspos)[1];
        return new XYLocation(xpos, ypos);
    }

    private void setValue(int xPos, int yPos, int val) {
        int abscoord = absoluteCoordinatesFromXYCoordinates(xPos, yPos);
        board[abscoord] = val;

    }

    public int getValueAt(XYLocation loc) {
        return getValueAt(loc.getXCoOrdinate(), loc.getYCoOrdinate());
    }

    public void moveGapRight() {
        
        movimientos.add(RIGHT);
        
        int gapPosition = getGapPosition();
        int xpos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[1];
        if (!(ypos == 2)) {
            int valueOnRight = getValueAt(xpos, ypos + 1);
            setValue(xpos, ypos, valueOnRight);
            setValue(xpos, ypos + 1, 0);
        }

    }

    public void moveGapLeft() {
        
        movimientos.add(LEFT);
        
        int gapPosition = getGapPosition();
        int xpos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(getGapPosition())[1];
        if (!(ypos == 0)) {
            int valueOnLeft = getValueAt(xpos, ypos - 1);
            setValue(xpos, ypos, valueOnLeft);
            setValue(xpos, ypos - 1, 0);
        }

    }

    public void moveGapDown() {
        
        movimientos.add(DOWN);
        
        int gapPosition = getGapPosition();
        int xpos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[1];
        if (!(xpos == 2)) {
            int valueOnBottom = getValueAt(xpos + 1, ypos);
            setValue(xpos, ypos, valueOnBottom);
            setValue(xpos + 1, ypos, 0);
        }
    }

    public void moveGapUp() {
        
        movimientos.add(UP);
        
        int gapPosition = getGapPosition();
        int xpos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(gapPosition)[1];
        if (!(xpos == 0)) {
            int valueOnTop = getValueAt(xpos - 1, ypos);
            setValue(xpos, ypos, valueOnTop);
            setValue(xpos - 1, ypos, 0);
        }

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        }
        NPuzzleBoard aBoard = (NPuzzleBoard) o;

        for (int i = 0; i < this.tam * this.tam - 1; i++) {
            if (this.getPositionOf(i) != aBoard.getPositionOf(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (int i = 0; i < this.tam * this.tam - 1; i++) {
            int position = this.getPositionOf(i);
            result = 37 * result + position;
        }
        return result;
    }

    public List<XYLocation> getPositions() {
        ArrayList<XYLocation> retVal = new ArrayList<XYLocation>();
        for (int i = 0; i < this.tam * this.tam; i++) {
            int[] res = xycoordinatesFromAbsoluteCoordinate(getPositionOf(i));
            XYLocation loc = new XYLocation(res[0], res[1]);
            retVal.add(loc);

        }
        return retVal;
    }

    public void setBoard(List<XYLocation> locs) {

        int count = 0;

        for (int i = 0; i < locs.size(); i++) {
            XYLocation loc = locs.get(i);
            this.setValue(loc.getXCoOrdinate(), loc.getYCoOrdinate(), count);
            count = count + 1;
        }
    }

    public boolean canMoveGap(String where) {
        boolean retVal = true;
        int abspos = getPositionOf(0);
        int xpos = xycoordinatesFromAbsoluteCoordinate(abspos)[0];
        int ypos = xycoordinatesFromAbsoluteCoordinate(abspos)[1];
        if (where.equals(LEFT)) {
            if (ypos == 0) {
                retVal = false;
            }
        }
        if (where.equals(RIGHT)) {
            if (ypos == this.tam - 1) {
                retVal = false;
            }
        }
        if (where.equals(UP)) {
            if (xpos == 0) {
                retVal = false;
            }
        }
        if (where.equals(DOWN)) {
            if (xpos == this.tam - 1) {
                retVal = false;
            }
        }

        return retVal;
    }

    @Override
    public String toString() {
        String retVal = "";
        int resto = 1;
        for (int i = 0; i < this.tam * this.tam; i++) {
            if (resto * this.tam == i + 1) {
                resto++;
                retVal = retVal + "\t" + board[i] + "\n";
            } else {
                retVal = retVal + "\t" + board[i];
            }
        }
        return retVal;
    }
}