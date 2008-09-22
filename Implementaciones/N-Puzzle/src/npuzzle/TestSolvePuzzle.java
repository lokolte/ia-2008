package npuzzle;

/*
 * Assignment: Homework 04
 * Subject: SolvePuzzle Driver program
 * Author(s): Charm Gil, Hong
 * Creation Date: 5 November 2007
 * Completion Date: 8 October 2007
 * Completion Hours: 3 hr (research) + 4 hr (spec analysis and design) + 4 hr (coding)
 * 
 * Reference: Priority Queue, Chapter 6, Introduction to Algorithm (MIT)
*/


import java.util.*;

public class TestSolvePuzzle {
	public static void main(String[] args) {
		Scanner keyboard = new Scanner( System.in );
		
		// demo 1
		System.out.println( "--------------------" );
		System.out.println( "Demo Play #1 : n = 4" );
		System.out.println( "--------------------" );
		Puzzle scrambled = new Puzzle("_ 6 8 12 10 2 9 4 1 3 14 15 5 13 7 11") ;
		Puzzle solved = new Puzzle("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 _") ;
		
		scrambled.print();
		System.out.println( " (press Enter to see how it is solved)" );
		keyboard.nextLine();
		
		SolvePuzzle.solve(scrambled,solved) ;
		
		// demo 2
		System.out.println( "\n\n--------------------" );
		System.out.println( "Demo Play #2 : n = 3" );
		System.out.println( "--------------------" );
		scrambled = new Puzzle("1 5 _ 2 4 7 8 3 6");
		solved = new Puzzle("1 2 3 4 5 6 7 8 _");
		
		scrambled.print();
		System.out.println( " (press Enter to see how it is solved)" );
		keyboard.nextLine();
		
                SolvePuzzle.solve(scrambled,solved) ;
                
                // demo 3
		System.out.println( "\n\n--------------------" );
		System.out.println( "Demo Play #2 : n = 2" );
		System.out.println( "--------------------" );
		scrambled = new Puzzle("_ 2 1 3");
		solved = new Puzzle("1 2 3 _");
		
		scrambled.print();
		System.out.println( " (press Enter to see how it is solved)" );
		keyboard.nextLine();
		
                SolvePuzzle.solve(scrambled,solved) ;
                
                // demo 3
		/*System.out.println( "\n\n--------------------" );
		System.out.println( "Demo Play #1 : n = 5" );
		System.out.println( "--------------------" );
		scrambled = new Puzzle("1 2 3 4 5 6 7 8 9 10 11 12 13 14 16 15 17 18 19 21 20 22 23 24 _") ;
		solved = new Puzzle("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 _") ;
		
                scrambled.print();
		System.out.println( " (press Enter to see how it is solved)" );
		keyboard.nextLine();
		
		SolvePuzzle.solve(scrambled,solved) ;*/
                
		
	}
}
