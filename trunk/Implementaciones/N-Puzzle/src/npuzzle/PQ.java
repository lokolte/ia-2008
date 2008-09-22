package npuzzle;

/*
 * Assignment: Homework 04
 * Subject: Priority Queue ADT
 * Author(s): Charm Gil, Hong
 * Creation Date: 5 November 2007
 * Completion Date: 8 October 2007
 * Completion Hours: 3 hr (research) + 4 hr (spec analysis and design) + 4 hr (coding)
 * 
 * Reference: Priority Queue, Chapter 6, Introduction to Algorithm (MIT)
*/


import java.util.ArrayList;

public class PQ{
	private ArrayList<Path> queue;
	
	///////
	// Constructor
	public PQ(){
		queue = new ArrayList<Path>();
	}
	
	
	///////
	// Method: insert()
	//        - add action of priority queue 
	//          INPUT: Path
	public void insert( Path newElement ){
		queue.add( new Path() );
		
		decreaseKey( queue.size() - 1, newElement );	// call decreaseKey() in order to maintain PQ
	}
	

	///////
	// Method: extractMin()
	//        - remove action of priority queue 
	//          Output: Path
	public Path extractMin(){
		if( queue.size() < 1 ){
			System.out.println( "Heap underflow" );
			System.exit( 1 );
		}
		
		Path min = queue.get( 0 );
		queue.set( 0, queue.get( queue.size() - 1 ) );
		queue.remove( queue.size() - 1 );
		
		minHeapify( 0 );
		
		return min;
	}
	
	
	///////
	// Method: buildMinHeap()
	//        - make normal queue be priority queue (min heap)
	//		  - Never used in this project
	public void buildMinHeap(){
		for( int i = queue.size() / 2 ; i >= 0 ; i-- )
			minHeapify( i );
	}
	
	
	///////
	// Method: decreaseKey()
	//		  - a method which maintains priority queue when add was happened
	private void decreaseKey( int i, Path key ){
		if( key.getEstimated() < queue.get(i).getEstimated() ){
			queue.set( i, key );
			
			while( i > 0 && queue.get( parent( i ) ).getEstimated() > queue.get( i ).getEstimated() ){
				swap( i, parent( i ) );
				i = parent( i );
			}
		}
	}
	
	
	///////
	// Method: minHeapify()
	//		  - a method which builds priority queue
	private void minHeapify( int i ){
		int l = 2 * i + 1;
		int r = 2 * i + 2;
		
		if( l < queue.size() ){
			int smallest = i;
						
			if( queue.get( l ).getEstimated() < queue.get( i ).getEstimated() ) smallest = l;
			else smallest = i;
			
			if( r < queue.size() ){
				if( queue.get( r ).getEstimated() < queue.get( smallest ).getEstimated() ) smallest = r;
			}
			
			if( smallest != i ){
				swap( i, smallest );
				minHeapify( smallest );
			}
		}
	}
	
	
	// some accessors
	public int size(){
		return queue.size();
	}
	
	
	public int get( int i ){
		return queue.get( i ).getEstimated();
	}
	
	public boolean isEmpty(){
		if( queue.size() == 0 )
			return true;
		
		return false;
	}
	
	
	// some helping methods
	private void swap( int i, int j ){
		Path temp = queue.get( i );
		queue.set( i, queue.get( j ));
		queue.set( j, temp );
	}
		
	private int parent( int i ){
		return ( i - 1 ) / 2;
	}
}
