package npuzzle;

/*
 * Assignment: Homework 04
 * Subject: Definition of Puzzle
 * Author(s): Charm Gil, Hong
 * Creation Date: 5 November 2007
 * Completion Date: 8 October 2007
 * Completion Hours: 3 hr (research) + 4 hr (spec analysis and design) + 4 hr (coding)
 * 
 * Reference: Priority Queue, Chapter 6, Introduction to Algorithm (MIT)
*/


import java.util.*;

public class Puzzle{
	private static final int UP = -4;
	private static final int DOWN = -3;
	private static final int LEFT = -2;
	private static final int RIGHT = -1;
	
	private int edge;
	private int blank;
	private int tiles[][];
	
	///////
	// Constructor
	public Puzzle( String newTiles ){
		StringTokenizer tokens = new StringTokenizer( newTiles );
		
		edge = ( int )Math.sqrt( tokens.countTokens() );
		if( edge * edge != tokens.countTokens() )		// if illegal input,
			System.exit( 1 );							// stop program
		
		blank = edge * edge;				// define a number, edge^2 as blank 
		tiles = new int[ edge ][ edge ];
		
		for( int i = 0 ; i < edge ; i++ ){
			for( int j = 0 ; j < edge ; j++ ){
				String temp = tokens.nextToken();
				if( temp.equals( "_" ) )
					tiles[ i ][ j ] = blank;
				else
					tiles[ i ][ j ] = Integer.parseInt( temp );
			}
		}
	}
	
	
	///////
	// Method: getAllPossibleMoves()()
	//        - It gives all the possible next positions of the tiles
	public ArrayList<Puzzle> getAllPossibleMoves(){
		ArrayList<Puzzle> possibleMoves = new ArrayList<Puzzle>();
		
		for( int direction = UP ; direction <= RIGHT ; direction++ )
			if( isMovable( direction ) )
				possibleMoves.add( move( direction ) );
		
		return possibleMoves;
	}
	
	
	///////
	// Method: estimate()
	//        - It gives heuristic value
	public int estimate( Puzzle otherPuzzle ){
		if( edge != otherPuzzle.getEdge() )		// exception - different size of puzzle
			return -1;
		
		int n = 0, sumDiff = 0;
		int diff[] = new int[ edge * edge ];
		
		for( int i = 0 ; i < edge ; i++)
			for( int j = 0 ; j < edge ; j++ )
				diff[ n++ ] = Math.abs( otherPuzzle.get( i, j ) - tiles[ i ][ j ] );
		
		for( int i = 0 ; i < n ; i++ )
			sumDiff += diff[ i ] % edge + diff[ i ] / edge;
		
		return sumDiff;
	}
	
	
	// some accessor
	public int getEdge(){
		return edge;
	}
	
	
	public int get( int i, int j ){
		return tiles[ i ][ j ];
	}
	
	
	public String getSequence(){
		String str = "";
		
		for( int i = 0 ; i < edge ; i++ ){
			for( int j = 0 ; j < edge ; j++ ){
				if( tiles [ i ][ j ] == blank )
					str += "_ ";
				else
					str += tiles[ i ][ j ] + " ";
			}
		}
		
		return str;
	}

	
	
	public void print(){
		System.out.println( this.toString() );
	}
	
	
	public String toString(){
		int maxDigit = (new Integer( blank )).toString().length();	// blank is edge * edge
		
		String frame = "%" + maxDigit + "d";	// number format
		String str = "";
		
		for( int i = 0 ; i < edge ; i++ ){
			for( int j = 0 ; j < edge ; j++ ){
				if( tiles [ i ][ j ] == blank )
					for( int k = -1 ; k < maxDigit ; k++ )
						str += " ";
				else
					str += new Formatter().format( frame, tiles[ i ][ j ] ) + " ";
			}
			str += "\n";
		}
		
		return str;
	}
	
	
	public boolean equals( Puzzle otherPuzzle ){
		if( edge != otherPuzzle.getEdge() )
			return false;
		
		for( int i = 0 ; i < edge ; i++ )
			for( int j = 0 ; j < edge ; j++ )
				if( tiles[ i ][ j ] != otherPuzzle.tiles[ i ][ j ] )
					return false;
		
		return true;
	}
	
	
	// some helping method
	private Puzzle move( int direction ){
		Puzzle nextState = new Puzzle( getSequence() );
		
		int i = whereIs( blank ) / edge;
		int j = whereIs( blank ) % edge;
		
		if( direction == UP )			nextState.swap( i, j, i - 1, j );
		else if( direction == DOWN )	nextState.swap( i, j, i + 1, j );
		else if( direction == LEFT )	nextState.swap( i, j, i, j - 1 );
		else if( direction == RIGHT )	nextState.swap( i, j, i, j + 1 );
		
		return nextState;
	}
	
	
	private void swap( int x_i, int x_j, int y_i, int y_j  ){
		int temp = tiles[ x_i ][ x_j ];
		tiles[ x_i ][ x_j ] = tiles[ y_i ][ y_j ];
		tiles[ y_i ][ y_j ] = temp;
	}
	
	
	private boolean isMovable( int direction ){
		int i = whereIs( blank ) / edge;
		int j = whereIs( blank ) % edge;
		
		if( direction == UP )			return ( i == 0 ) ? false : true; 
		else if( direction == DOWN )	return ( i == edge - 1 ) ? false : true;
		else if( direction == LEFT )	return ( j == 0 ) ? false : true;
		else if( direction == RIGHT )	return ( j == edge - 1 ) ? false : true;
		else							return false;
	}
	
	
	private int whereIs( int tile ){
		int i = 0;
		
		while( tiles[ i / edge ][ i % edge ] != tile && i < blank )	i++;
		
		return i;
	}
}	// end of class
