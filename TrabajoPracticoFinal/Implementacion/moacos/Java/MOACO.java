/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

/**
 *
 * @author Christian Gomez
 */
public abstract class MOACO
{
	protected Problem prob;
	protected int criterio;
	protected int tiempoTotal;
	protected int maxIteraciones;
	protected int hormigas;
	public ConjuntoPareto pareto;
	public MOACO(RefObject<Problem> p)
	{
		prob =p.argvalue;
		pareto =new ConjuntoPareto(500);
		RandomNumbers.seed(time(null));
	}
	public abstract int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol);
	public abstract int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol);
	public abstract int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual);
	public abstract void ejecutarTSP();
	public abstract void ejecutarQAP();
	public abstract void ejecutarVRP();
	public int condicion_parada(int generacion,clock_t start,clock_t end)
	{
		if(criterio ==1)
		{
			if((end - start) / CLOCKS_PER_SEC < tiempoTotal)
				return 0;
		}
		else if(generacion<maxIteraciones)
			return 0;

		return 1;
	}
	public Problem getProblema()
	{
		return prob;
	}
	public void online_update(int orig,int dest)
	{
		}
}


final class DefineConstantsClases2
{
	public static final int PARETO = 50;
}
//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2008 Tangible Software Solutions Inc.
//
//	This class provides the ability to simulate the behavior of the C/C++ functions for 
//	generating random numbers.
//	'rand' converts to the parameterless overload of NextNumber
//	'random' converts to the single-parameter overload of NextNumber
//	'randomize' converts to the parameterless overload of Seed
//	'srand' converts to the single-parameter overload of Seed
//----------------------------------------------------------------------------------------
final class RandomNumbers
{
	private static Random r;

	static int nextNumber()
	{
		if (r == null)
			Seed();

		return r.nextInt();
	}

	static int nextNumber(int ceiling)
	{
		if (r == null)
			Seed();

		return r.nextInt(ceiling);
	}

	static void seed()
	{
		r = new Random();
	}

	static void seed(int seed)
	{
		r = new Random(seed);
	}
}
//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2008 Tangible Software Solutions Inc.
//
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
final class RefObject<T>
{
	T argvalue;
	RefObject(T refarg)
	{
		argvalue = refarg;
	}
}
