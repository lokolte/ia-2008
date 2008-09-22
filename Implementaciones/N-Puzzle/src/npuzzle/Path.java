package npuzzle;

/**
 * 
 * Assignment: Homework 04
 * Subject: A* n-Puzzle solving implementation
 * Author(s): Charm Gil, Hong
 * Creation Date: 5 November 2007
 * Completion Date: 8 October 2007
 * Completion Hours: 3 hr (research) + 4 hr (spec analysis and design) + 4 hr (coding)
 * 
 * Reference: Priority Queue, Chapter 6, Introduction to Algorithm (MIT)
*/


import java.util.*;

///////
// Class: Path
//        - A class definition of solving progress
//          This will be nodes of priority queue
public class Path{
	private ArrayList<Puzzle> path;
	private int estimated;
	
	///////
	// Constructors
	public Path( Puzzle newPuzzle ){
		this();
		path.add( newPuzzle );
	}
	
	public Path(){
		path = new ArrayList<Puzzle>();
		estimated = Integer.MAX_VALUE;
	}
	
	
	///////
	// Method: nextPaths()
	//        - Generating next possible tile positions and return them
	public ArrayList<Path> nextPaths(){
		ArrayList<Puzzle> allPossibleMoves = path.get( path.size() - 1 ).getAllPossibleMoves();
					
		for( int i = 0 ; i < allPossibleMoves.size() ; i++ )
			if( this.has( allPossibleMoves.get( i ) ) )
				allPossibleMoves.remove( i );
		
		ArrayList<Path> nextPaths = new ArrayList<Path>();
		for( int i = 0 ; i < allPossibleMoves.size() ; i++ )
			nextPaths.add( mergeOnCurrentPath( allPossibleMoves.get( i ) ) );
		
		return nextPaths;
	}
	
	
	///////
	// Method: mergeOnCurrentPath()
	//        - Gets next movements, build next paths by adding them up to current path and return it. 
	public Path mergeOnCurrentPath( Puzzle nextPuzzle ){
		Path nextPath = new Path();
		
		for( int i = 0 ; i < path.size() ; i++ )
			nextPath.path.add( path.get( i ) );
		nextPath.path.add( nextPuzzle );
		
		return nextPath;
	}
	
	
	///////
	// Method: computeFx()
	//        - Evaluate the sum of the distance so far and current estimate to the goal 
	public void computeFx( Puzzle goal ){
		estimated = path.size() + path.get( path.size() - 1 ).estimate( goal );
	}
	

	// some accessors and helping methods
	public int getEstimated(){
		return estimated;
	}
	

	public void print(){
		for( int i = 0 ; i < path.size() ; i++ ){
			path.get( i ).print();
		}
	}
	
	
	public int size(){
		return path.size();
	}
	
	
	public boolean equals( Puzzle otherPuzzle ){
		return path.get( path.size() - 1 ).equals( otherPuzzle );
	}
	
	
	private boolean has( Puzzle otherPuzzle ){
		for( int i = 0 ; i < path.size() ; i++ )
			if( path.get( i ).equals( otherPuzzle ) )
				return true;
		
		return false;
	}
} // end of class
