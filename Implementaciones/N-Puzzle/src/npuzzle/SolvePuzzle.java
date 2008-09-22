package npuzzle;

/*
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

public class SolvePuzzle {
	///////
	// Method: solve()
	//        - A* solver
	public static void solve( Puzzle scrambled, Puzzle goal ){
		Path detonator = new Path( scrambled );
		detonator.computeFx( goal );
		
		PQ pq = new PQ();
		pq.insert( detonator );
		
		//int counter = 0;
		
		while( !pq.isEmpty() ){
			//System.out.print( "Q length: " + pq.size() );
			//System.out.println( " / Iteration: " + ++counter );
			
			Path currentPath = pq.extractMin();
			
			if( currentPath.equals( goal ) ){
				currentPath.print();
				System.out.println( " => total " + ( currentPath.size() - 1 ) + " moves." );
				
				return;
			}
			
			ArrayList<Path> nextPaths = currentPath.nextPaths();
			
			for( int i = 0 ; i < nextPaths.size() ; i++ ){
				nextPaths.get( i ).computeFx( goal );
				pq.insert( nextPaths.get( i ) );
			}
		}
		// failed
		System.out.println( " => A* failure: impossible array is given." );
	}
} // end of class
