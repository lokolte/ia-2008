package othello2;
/**
 ******************************************************************************
 * The MyDebug class
 * 
 * @author Michael Farrelly <michael.farrelly@uleth.ca>
 * @version 0.2006.04.04
 ****************************************************************************** 
 */
public class MyDebug
{
	public static boolean DEBUG	= false;

	public static void println( String context, String string )
	{
		if( DEBUG )
			System.err.println( "[" + context + "]: " + string );
	}
	public static void println( String string )
	{
		if( DEBUG )
			println( "debug", string );
	}

	public static void print( String string )
	{
		if( DEBUG )
			System.err.print( string );
	}

}
