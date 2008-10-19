package othello2;
/**
 ******************************************************************************
 * The SearchTree class defines the basic process of searching for the best
 * Board to move to, to maximize the outcome.
 * 
 * TODO: Implement the Alpha-Beta pruning.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
import java.util.*;

public class SearchTree implements OthelloConstants
{
	SearchNode root;
	private int player;
	public SearchTree(  )
	{

	}
	public SearchTree( int player, SearchNode entity )
	{
		setRoot( entity );
		this.player = player;
	}
	public SearchTree( int player, Evaluatable entity )
	{
		setRoot( new SearchNode( player, SearchNode.MAX_NODE, entity ) );
		this.player = player;
	}

	public void setRoot( SearchNode o )
	{
		root = o;
	}

	public SearchNode getRoot()
	{
		return root;
	}

	
	/**
	 * Generates the child nodes for the tree, and stops after reaching depth.
	 */
	public void generateTree( int depth, String algoritmo )
	{
            if (algoritmo.compareTo(miniMaxAlgoritm)==0){
		this.root.generateChildrenMiniMax(depth);
            }
            else{
		this.root.generateChildrenAlphaBeta(depth);
            }
	}

	/**
	 * Searches the generated tree for the best move (and returns it) 
	 */
	public int getBestMove( int depth, String algoritmo )
	{
		int v = this.root.childrenValue( depth, algoritmo );
		if( Math.abs(v) == Integer.MAX_VALUE ) 
			return -1;
		Board best = (Board) this.root.getBestEvaluatable();
		if( best == null )
			return -1;
		int m = best.getLastMove();
		return m;		
	}
}