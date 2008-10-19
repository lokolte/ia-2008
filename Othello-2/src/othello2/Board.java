package othello2;
/**
 ******************************************************************************
 * The Board class stores the board related information, such as what positions
 * are occupied, etc. It also calculates what positions are available for the 
 * next player to play.
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.06
 ****************************************************************************** 
 */
import java.util.*;

public class Board implements OthelloConstants, Cloneable, Evaluatable
{
    private int [] positions;
	private int [] sums;
	private int lastMove = -1;
	
    
	/** 
	 * Creates an empty board.
      */
	public Board()
	{
        this( false );
	}

    
	/** 
	 * Creates the starting board, both players having two 
	 * pieces played.
     */
    public Board( boolean startingBoard )
    {
        positions = new int[BOARD_POSITIONS];
		sums = new int[3];
		sums[FREE_SPACE] = 64;
        if( startingBoard )
        {
			

            setPosition( 3,3, LIGHT_PIECE );
            setPosition( 4,4, LIGHT_PIECE );

            setPosition( 4,3, DARK_PIECE );
            setPosition( 3,4, DARK_PIECE );

			//calcPositions();
        }
    }

	
	/**
	 * Sets the value at position (x,y)
	 * @param x the X (column)
	 * @param y the Y (row)
	 * @param value the player to set at (x,y)
	 */
	public void setPosition( int x, int y, int value )
	{	
		setPosition( convertPosition(x,y), value );
	}
	
	/**
	 * Sets the value at position (n)
	 * @param n the actual position in the list (0-63)
	 * @param value the player to set at (n)
	 */
    public void setPosition( int n, int value )
    {
		int oldValue = getPosition( n );
		if( value != oldValue )
		{
			sums[oldValue] --;
			sums[value] ++;
		}
		
        positions[n] = value;
    }


	/**
	 * Gets the value at position (n)
	 * @param n the actual position in the list (0-63)
	 * @return the player that is set at (n)
	 */
	public int getPosition( int n )
	{
		if( n >= 0 && n < BOARD_POSITIONS )
			return positions[n];
		return NOT_POSSIBLE;
	}


	/**
	 * Gets the value at position (x,y)
	 * @param x the X (column)
	 * @param y the Y (row)
	 * @return the player that is set at (x,y)
	 */
    public int getPosition( int x, int y )
    {
        return getPosition( convertPosition(x,y) );
    }


	/**
	 * Evaluates the given board.
	 * required for Evaluatable interface
	 */	
	public int staticEvaluation( int player )
	{
		return (BOARD_POSITIONS - getCount(FREE_SPACE)) 
			+ getCount(player);
	}

	/**
	 * Generates the boards if <b>player</b> were to move in
	 * all of the possible board pieces.
	 * 
	 * Required for Evaluatable interface.
	 * 
	 * @param player the player to make the move
	 * @return a list of the boards with the move performed.
	 */
	public ArrayList generate( int player )
	{
		int [] moves = findMoves( this, player );
		ArrayList list = new ArrayList(moves.length);
		
		for( int i=0;i<moves.length&&moves[i] != NOT_POSSIBLE;i++ )
		{
			Board clone = (Board) this.clone();
			
			clone.move( moves[i] % 8, moves[i] / 8, player );
			list.add( clone );
		}
		return list;
	}

	/**
	 * Gets the number of pieces a player has,
	 * @param player LIGHT_PIECE, DARK_PIECE or FREE_SPACE
	 * @return the number of pieces
	 */
	public int getCount( int player )
	{
		return sums[player];
	}

	/**
	 * Gets the opposite of the given player
	 * @param player LIGHT_PIECE, or DARK_PIECE
	 * @return the opposite value, LIGHT_PIECE for DARK_PIECE and DARK_PIECE for LIGHT_PIECE. 
	 */
	public static int opposite( int player )
	{
		if( player == DARK_PIECE ) 
			return LIGHT_PIECE;
		return DARK_PIECE;
	}

	/**
	 * Moves the player to (x,y) and flips the pieces.
	 * 
	 * @param x the X (column)
	 * @param y the Y (row)
	 * @param player the player to set at (x,y)
	 */
	public boolean move( int x, int y, int player )
	{
		return move(x,y,player,true);
	}

	/**
	 * Moves the player to (x,y) and flips the pieces (in all directions
	 * if specified in the last argument).
	 * 
	 * @param x the X (column)
	 * @param y the Y (row)
	 * @param player the player to set at (x,y)
	 * @param flipPieces whether to flip pieces or not.
	 */
	public boolean move( int x, int y, int player, boolean flipPieces )
	{
		
		int oplayer = opposite( player );
		boolean moved = false;
		
		int n = convertPosition(x,y);
		if( n >= BOARD_POSITIONS && n < 0 )
			return false;
		int j = 0;
		int i = 0;
		int last = -1;
		for( int k=n-9; k<=n+9;k++ )
		{
			i = k % 8;
			j = k / 8;
			// investigate possible chain
			
			if( getPosition( i,j ) == oplayer )
			{
				int iinc = i - x;
				int jinc = j - y;
				int istart = i;
				int jstart = j;
				boolean chain = false;
				while( istart >= 0 && istart < BOARD_LENGTH 
						&& jstart >= 0 && jstart < BOARD_LENGTH 
						&& getPosition( istart, jstart ) == oplayer )
				{
					istart += iinc;
					jstart += jinc;
				}
				if( istart >= 0 && istart < BOARD_LENGTH 
					&& jstart >= 0 && jstart < BOARD_LENGTH 
					&& getPosition( istart, jstart ) == player)
				{
					chain = true;							
					if( flipPieces )
					{
						setPosition( x, y, player );
						setPosition( i, j, player );
						flipPieces( i, j, istart, jstart, iinc, jinc, player );
					}
					moved = true;
				}
			}
			if( k == n-7 || k == n+1 )
			{
				k+=5;
			}
		
		
		}
	//		}
	//	}
		if( moved )
			lastMove = convertPosition(x, y);
		//MyDebug.println("ai::move = done.");
		return moved;
	}

	/**
	 * Flips the pieces in the direction specified
	 */
	public void flipPieces( int x, int y, int x1, int y1, 
							int xinc, int yinc, int player )
	{		
		while( x != x1 || y != y1 )
		{			
			setPosition( x, y, player );
			x+= xinc;
			y+= yinc;
		}
	}

	/**
	 * Sets the positions based on an array.
	 */
	protected void setPositions( int [] positions )
	{
		System.arraycopy(positions,0,this.positions,0,positions.length);
		sums[0] = 0;
		sums[1] = 0;
		sums[2] = 0;
		for( int i = 0; i < BOARD_POSITIONS; i++ )
		{
			sums[positions[i]] ++;
		}
	}

	/**
	 * Gets the positions in an array.
	 */
	protected int [] getPositions(  )
	{
		return positions;
	}

	/**
	 * Clones the Board.
	 * @return a copy or clone of the board.
	 */
	public Object clone()
	{
		Board b = new Board();
		b.setPositions( (int[]) this.getPositions().clone() );
		b.setLastMove( this.getLastMove() );
		return (Object) b;
	}

	/**
	 * Convert the position of (x,y) to a n-value.
	 */
	public static int convertPosition( int x, int y )
	{
		return x + (y * BOARD_LENGTH);
	}

	/**
	 * Convert the position of a n-value to (x,y).
	 */
	public static int [] convertPosition( int n )
	{
		return new int[]{n%BOARD_LENGTH,n/BOARD_LENGTH};
	}

	/**
	 * Finds the possible moves for a player on a board.
	 */
	public static int [] findMoves( Board board, int player )
	{
		int opposite = opposite(player);
		int [] moves = new int[BOARD_POSITIONS];
		initializeList(moves);
		int m = 0;
		
		for( int i=0;i<BOARD_POSITIONS;i++ )
		{
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i-1) == NO_PIECE 
					&& (i-1) / BOARD_LENGTH == i / BOARD_LENGTH))
				
			{
				// Free to the right?
				int j = i;
				while( j / BOARD_LENGTH == i / BOARD_LENGTH 
						&& board.getPosition(j) == opposite )
				{
					j++;
				}
				if( board.getPosition(j) == player && ! inList( moves, i-1))
				{
					MyDebug.println( "[move-R : "+i + ": "+(i-1)+"]" );
					moves[m] = i-1;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i+1) == NO_PIECE 
					&& (i+1) / BOARD_LENGTH == i / BOARD_LENGTH))
			{
				// Free to the left?
				int j = i;
				while( j / BOARD_LENGTH == i / BOARD_LENGTH 
						&& board.getPosition(j) == opposite )
				{
					j--;
				}

				if( board.getPosition(j) == player && ! inList( moves, i+1))
				{
					MyDebug.println( "[move-L : "+i + ": "+(i+1)+"]" );
					moves[m] = i+1;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i-BOARD_LENGTH) == NO_PIECE 
				)) //&& (i-BOARD_LENGTH) / BOARD_LENGTH == i / BOARD_LENGTH - 1)
			{
				// Free to the up?
				int j = i;
				while( j % BOARD_LENGTH == i % BOARD_LENGTH 
					&& j < BOARD_POSITIONS && board.getPosition(j) == opposite )
				{
					j+=BOARD_LENGTH;
				}
				if( board.getPosition(j) == player 
					&& ! inList( moves, i-BOARD_LENGTH))
				{
					MyDebug.println( "[move-U : "+i + ": "+(i-BOARD_LENGTH)+"]" );
					moves[m] = i-BOARD_LENGTH;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i-BOARD_LENGTH+1) == NO_PIECE 
				&& (i-BOARD_LENGTH+1) / BOARD_LENGTH == i / BOARD_LENGTH - 1))
			{
				// Free to the up-right?
				int j = i+BOARD_LENGTH-1;;
				while( j % BOARD_LENGTH <= i % BOARD_LENGTH 
					&& j / BOARD_LENGTH <= i / BOARD_LENGTH 
					&& j < BOARD_POSITIONS && board.getPosition(j) == opposite )
				{
					j+=BOARD_LENGTH-1;
				}
				if( board.getPosition(j) == player 
					&& ! inList( moves, i-BOARD_LENGTH+1 ))
				{
					MyDebug.println( "[move-UR: "+i + ": "+(i-BOARD_LENGTH+1)+"]" );
					moves[m] = i-BOARD_LENGTH+1;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i-BOARD_LENGTH-1) == NO_PIECE 
				&& (i-BOARD_LENGTH-1) / BOARD_LENGTH == i / BOARD_LENGTH - 1))
			{
				// Free to the up-left?
				int j = i;
				while( j % BOARD_LENGTH >= i % BOARD_LENGTH 
					&& j < BOARD_POSITIONS && board.getPosition(j) == opposite )
				{
					j+=BOARD_LENGTH+1;
				}
				if( board.getPosition(j) == player 
					&& ! inList( moves, i-BOARD_LENGTH-1 ))
				{
					MyDebug.println( "[move-UL: "+i + ": "+(i-BOARD_LENGTH-1)+"]" );
					moves[m] = i-BOARD_LENGTH-1;
					m++;
				}
			}

			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i+BOARD_LENGTH) == NO_PIECE 
				)) //&& (i+BOARD_LENGTH) / BOARD_LENGTH == i / BOARD_LENGTH + 1)
			{
				// Free to the down?
				int j = i;
				while( j % BOARD_LENGTH == i % BOARD_LENGTH 
					&& j > 0 && board.getPosition(j) == opposite )
				{
					j-=BOARD_LENGTH;
				}
				if( board.getPosition(j) == player 
					&& ! inList( moves, i+BOARD_LENGTH ) )
				{
					MyDebug.println( "[move-D : "+i + ": "+(i+BOARD_LENGTH)+"]" );
					moves[m] = i+BOARD_LENGTH;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i+BOARD_LENGTH+1) == NO_PIECE 
				&& (i+BOARD_LENGTH+1) / BOARD_LENGTH == i / BOARD_LENGTH + 1))
			{
				// Free to the down-right?
				int j = i;
				while( j % BOARD_LENGTH <= i % BOARD_LENGTH 
					&& j > 0 && board.getPosition(j) == opposite )
				{
					j-=BOARD_LENGTH+1;
				}
				
				if( board.getPosition(j) == player 
					&& ! inList( moves, i+BOARD_LENGTH+1 ) )
				{
					MyDebug.println( "[move-DR: "+i + ": "+(i-+BOARD_LENGTH+1)+"]" );
					moves[m] = i+BOARD_LENGTH+1;
					m++;
				}
			}
			if( board.getPosition(i) == opposite 
				&& (board.getPosition(i+BOARD_LENGTH-1) == NO_PIECE 
				&& (i+BOARD_LENGTH-1) / BOARD_LENGTH == i / BOARD_LENGTH + 1))
			{
				// Free to the down-left?
				int j = i;
				while( j % BOARD_LENGTH >= i % BOARD_LENGTH 
					&& j > 0 && board.getPosition(j) == opposite )
				{
					j-=BOARD_LENGTH-1;
				}
				
				if( board.getPosition(j) == player 
					&& ! inList( moves, i+BOARD_LENGTH-1 ) )
				{
					MyDebug.println( "[move-DL: "+i + ": "+(i+BOARD_LENGTH-1)+"]" );
					moves[m] = i+BOARD_LENGTH-1;
					m++;
				}
			}
		}
		moves[m] = NOT_POSSIBLE;
		Arrays.sort( moves, 0, m );
		return moves;
	}

	
	private static void initializeList( int [] list )
	{
		for( int i=0;i<list.length;i++)
		{
			list[i] = NOT_POSSIBLE;
		}
	}
	/**
	 * Checks if item is in a list.
	 */
	private static boolean inList( int [] list, int item )
	{
		for( int i=0;i<list.length;i++ )
		{
			if( list[i] == item )
				return true;
		}
		return false;
	}

	/**
	 * Gets the last move played.
	 */
	public int getLastMove()
	{ 
		return lastMove; 
	}

	/**
	 * Sets the last move played.
	 */
	public void setLastMove( int last ) 
	{ 
		lastMove = last; 
	}

	public String toString()
	{
		String s = "\n\t";
		for( int i=0;i<BOARD_POSITIONS;i++ )
		{
			s+= getPosition(i)+", ";
			if( (i+1) % 8 == 0 )
				s += "\n\t";
		}
		return s;
	}
}
