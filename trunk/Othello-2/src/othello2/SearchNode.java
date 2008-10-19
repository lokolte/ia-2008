package othello2;

/**
 ******************************************************************************
 * The SearchNode class defines the basic node of searching for the best
 * Board to move to, to maximize the outcome. These are not binary nodes, as 
 * many moves can be played at a given level.
 * 
 * TODO: Implement the Alpha-Beta pruning.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
import java.util.*;

public class SearchNode extends ArrayList {
    //private ArrayList children;

    private Evaluatable entity;
    private int nodeType;
    private int player;
    private Evaluatable bestChild;
    public final static int MIN_NODE = 1;
    public final static int MAX_NODE = 2;
    public final static int NOT_DEFINED = Integer.MAX_VALUE;
    private boolean generated;
    private int currentMax;
    private int currentMin;
    private static int nodesTraversed = 0;
    private static int nodesTotal = 0;
    private int nodeValue;
    private final static String miniMaxAlgoritm = "miniMax";
    private final static String alphaBetaAlgoritm = "alphaBeta";

    public SearchNode(int player, int nodeType) {
        super();
        //children = new ArrayList();
        this.nodeType = nodeType;
        this.player = player;
        generated = false;
        nodeValue = NOT_DEFINED;
        currentMax = -Integer.MAX_VALUE;
        currentMin = Integer.MAX_VALUE;
    }

    public SearchNode(int player, int nodeType, Evaluatable entity) {
        this(player, nodeType);
        setEntity(entity);

    }

    public boolean isGenerated() {
        return generated;
    }

    public ArrayList getChildren() {
        return this;
    }

    public boolean hasChildren() {
        if (size() > 0 && isGenerated()) {
            return true;
        }
        return false;
    }

    public boolean addChild(Evaluatable o) {
        // TODO: warning: [unchecked] unchecked call to add(E) 
        // as a member of the raw type java.util.ArrayList
        return super.add(new SearchNode(player, opposite(nodeType), o));
    }

    private boolean addChild(SearchNode o) {
        // TODO: warning: [unchecked] unchecked call to add(E) 
        // as a member of the raw type java.util.ArrayList
        return super.add(o);
    }

    private static int opposite(int nodeType) {
        if (nodeType == MIN_NODE) {
            return MAX_NODE;
        } else {
            return MIN_NODE;
        }
    }

    public Evaluatable getEntity() {
        return entity;
    }

    public void setEntity(Evaluatable o) {
        this.entity = o;
    }

    public int childrenValue(int depth, String algoritmo) {
        if (algoritmo.compareTo(miniMaxAlgoritm)==0) {
            return this.miniMax(
                    depth,
                    -Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        else{
            return this.alphaBeta(
                    depth,
                    -Integer.MAX_VALUE, Integer.MAX_VALUE);
            
        }
    }

    public int miniMax(int depth, int currentMax, int currentMin) {
        //MyDebug.println( "childrenValue()");

        if (!hasChildren() && isGenerated()) {
            return 0;
        }
        if (!isGenerated()) {
            generateChildrenMiniMax(depth);

        //MyDebug.println( "childrenValue()::hasChildren && generated() ");
        }
        int value = (nodeType == MAX_NODE ? -Integer.MAX_VALUE : Integer.MAX_VALUE);
        if (depth == 0) {
            return value;
        }
        for (int i = 0; i < size(); i++) {
            SearchNode e = (SearchNode) get(i);
            int v = 1;
            if (depth > 1) {
                v = e.miniMax(depth - 1, currentMax, currentMin);
                nodesTraversed++;
            } else if (depth == 1) {
                v = e.getEntity().staticEvaluation(
                        (nodeType == MAX_NODE ? player : Board.opposite(player)));
            /*
            if( nodeType == MAX_NODE && v < currentMax )
            {
            //MyDebug.println( "alpha cut ");
            return v;
            }
            
            if( nodeType == MIN_NODE && v > currentMin )
            {
            //MyDebug.println( "beta cut ");
            return v;
            }
            
             */
            }
            if (nodeType == MIN_NODE && v < value) {
                value = v;
                bestChild = e.getEntity();
            } else if (nodeType == MAX_NODE && v > value) {
                value = v;
                bestChild = e.getEntity();
            }
        }
        return value;
    }

    public int alphaBeta(int depth, int currentMax, int currentMin) {
        //MyDebug.println( "childrenValue()");

        if (!hasChildren() && isGenerated()) {
            return 0;
        }
        if (!isGenerated()) {
            generateChildrenAlphaBeta(depth);

        //MyDebug.println( "childrenValue()::hasChildren && generated() ");
        }
        int value = (nodeType == MAX_NODE ? -Integer.MAX_VALUE : Integer.MAX_VALUE);
        if (depth == 0) {
            return value;
        }
        for (int i = 0; i < size(); i++) {
            SearchNode e = (SearchNode) get(i);
            int v = 1;
            if (depth > 1) {
                v = e.alphaBeta(depth - 1, currentMax, currentMin);
                nodesTraversed++;
            } else if (depth == 1) {
                v = e.getEntity().staticEvaluation(
                        (nodeType == MAX_NODE ? player : Board.opposite(player)));
            }
            if (nodeType == MAX_NODE && v < currentMax) {
                //MyDebug.println( "alpha cut ");
                return v;
            }

            if (nodeType == MIN_NODE && v > currentMin) {
                //MyDebug.println( "beta cut ");
                return v;
            }

            if (nodeType == MIN_NODE && v < value) {
                value = v;
                bestChild = e.getEntity();
            } else if (nodeType == MAX_NODE && v > value) {
                value = v;
                bestChild = e.getEntity();
            }
        }
        return value;
    }

    public Evaluatable getBestEvaluatable() {
        return bestChild;
    }

    public void generateChildrenMiniMax(int depth) {
        if (depth == 0) {
            generated = true;
            return;
        }
        // Generate the children nodes.
        Evaluatable e = getEntity();
        ArrayList children = e.generate(
                (nodeType == MAX_NODE ? player : Board.opposite(player)));
        Collections.shuffle(children);

        for (int i = 0; i < children.size(); i++) {
            // Get the next children.
            SearchNode child = new SearchNode(
                    player,
                    opposite(nodeType),
                    (Evaluatable) children.get(i));
            addChild(child);
            // Generate that childs children.
            ((SearchNode) this.get(i)).generateChildrenMiniMax(depth - 1);
            // Compute the value for the next child.
            int value = child.miniMax(depth - 1, currentMax, currentMin);
            // Update the curren$t choice for the node.
            if (nodeType == MAX_NODE && value > currentMax) {
                currentMax = value;
            }
            if (nodeType == MIN_NODE && value < currentMin) {
                currentMin = value;
            }
        }
        generated = true;
    }

    public void generateChildrenAlphaBeta(int depth) {
        if (depth == 0) {
            generated = true;
            return;
        }
        // Generate the children nodes.
        Evaluatable e = getEntity();
        ArrayList children = e.generate(
                (nodeType == MAX_NODE ? player : Board.opposite(player)));
        Collections.shuffle(children);

        for (int i = 0; i < children.size(); i++) {
            // Get the next children.
            SearchNode child = new SearchNode(
                    player,
                    opposite(nodeType),
                    (Evaluatable) children.get(i));
            addChild(child);
            // Generate that childs children.
            ((SearchNode) this.get(i)).generateChildrenAlphaBeta(depth - 1);
            // Compute the value for the next child.
            int value = child.miniMax(depth - 1, currentMax, currentMin);
            // Update the curren$t choice for the node.
            if (nodeType == MAX_NODE && value > currentMax) {
                currentMax = value;
            }
            if (nodeType == MIN_NODE && value < currentMin) {
                currentMin = value;
            }
        }
        generated = true;
    }
}