import java.util.*;
//#include "pvm3.h"
//#define PARETO 50

//************* Clase Conjunto Pareto *****************
public class ConjuntoPareto
{
	private int cantSoluciones; //cantidad actual de soluciones
	private int tamano; //tamano del array de soluciones
	// array que contiene las soluciones del frente pareto
	protected Solucion[][] lista;
	protected SolucionVRP[][] listaVRP;
	public ConjuntoPareto(int numSoluciones)
	{
		cantSoluciones =0;
		tamano =numSoluciones;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
		lista = (Solucion)malloc(sizeof(Solucion)*numSoluciones);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
		listaVRP = (SolucionVRP)malloc(sizeof(SolucionVRP)*numSoluciones);
		for(int i =0;i<numSoluciones;i++)
		{
			lista[i] =null;
			listaVRP[i] =null;
		}
	}
	public int agregarNoDominado(RefObject<Solucion> sol, RefObject<Problem> prob)
	{
		double solfuncion1 =prob.argvalue.funcion_obj_1(sol.argvalue); // Evaluacion de la solucion respecto
		double solfuncion2 =prob.argvalue.funcion_obj_2(sol.argvalue); // a las funciones obetivo del problema
		double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto
		double solauxfuncion2;
	
		for (int i =0; i<cantSoluciones; i++)
		{
			solauxfuncion1 =prob.argvalue.funcion_obj_1(lista[i]);
			solauxfuncion2 =prob.argvalue.funcion_obj_2(lista[i]);
			// ambas funciones objetivo siempre se minimizan
			if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2)
				return 0; //sol es dominada por una solucion del conjunto
		}
		//Aumentar el tamaño del conjunto Pareto si éste está lleno
		if (cantSoluciones ==tamano)
		{
			Solucion[][] listaAux =lista;
			tamano =tamano *2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
			lista =(Solucion)malloc(tamano *sizeof(Solucion));
			for (int i =0;i<cantSoluciones;i++)
			{
				lista[i] =listaAux[i];
			}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
			free(listaAux); //liberar el array anterior
		}
		if(lista[cantSoluciones] ==null)
			lista[cantSoluciones] =new Solucionsol.argvalue.getSize();
		RefObject<SolucionVRP> TempRefObject = new RefObject<SolucionVRP>(sol);
		lista[cantSoluciones].solcpy(TempRefObject);
		sol = TempRefObject.argvalue;
		cantSoluciones++;
		return 1;
	}
	public void eliminarDominados(RefObject<Solucion> sol, RefObject<Problem> prob)
	{
		double solfuncion1 =prob.argvalue.funcion_obj_1(sol.argvalue); // Evaluacion de la solucion respecto
		double solfuncion2 =prob.argvalue.funcion_obj_2(sol.argvalue); // a las funciones obetivo del problema
		double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto
		double solauxfuncion2;
		//Solucion *elim;
	
		for (int i =0; i<cantSoluciones; i++)
		{
			solauxfuncion1 =prob.argvalue.funcion_obj_1(lista[i]);
			solauxfuncion2 =prob.argvalue.funcion_obj_2(lista[i]);
			// ambas funciones objetivo siempre se minimizan
			if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2))
			{
				//elim=lista[i];
				lista[i].destruir();
				lista[i] =lista[cantSoluciones-1];
				lista[cantSoluciones-1] =null; //liberar puntero
				cantSoluciones--;
				i--;
				//elim->destruir();
			}
		}
	}
	public int agregarNoDominado(RefObject<SolucionVRP> sol, RefObject<Problem> prob)
	{
		double solfuncion1 =prob.argvalue.funcion_obj_1(sol.argvalue); // Evaluacion de la solucion respecto
		double solfuncion2 =prob.argvalue.funcion_obj_2(sol.argvalue); // a las funciones obetivo del problema
		double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto
		double solauxfuncion2;
	
		for (int i =0; i<cantSoluciones; i++)
		{
			solauxfuncion1 =prob.argvalue.funcion_obj_1(listaVRP[i]);
			solauxfuncion2 =prob.argvalue.funcion_obj_2(listaVRP[i]);
			// ambas funciones objetivo siempre se minimizan
			if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2)
				return 0; //sol es dominada por una solucion del conjunto
		}
		//Aumentar el tamaño del conjunto Pareto si éste está lleno
		if (cantSoluciones ==tamano)
		{
			SolucionVRP[][] listaAux =listaVRP;
			tamano =tamano *2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
			listaVRP =(SolucionVRP)malloc(tamano *sizeof(SolucionVRP));
			for (int i =0;i<cantSoluciones;i++)
			{
				listaVRP[i] =listaAux[i];
			}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
			free(listaAux);
		}
		if(listaVRP[cantSoluciones] ==null)
			listaVRP[cantSoluciones] =new SolucionVRPsol.argvalue.getSize();
		RefObject<SolucionVRP> TempRefObject = new RefObject<SolucionVRP>(sol);
		listaVRP[cantSoluciones].solcpy(TempRefObject);
		sol = TempRefObject.argvalue;
		cantSoluciones++;
		return 1;
	}
	public void eliminarDominados(RefObject<SolucionVRP> sol, RefObject<Problem> prob)
	{
		double solfuncion1 =prob.argvalue.funcion_obj_1(sol.argvalue); // Evaluacion de la solucion respecto
		double solfuncion2 =prob.argvalue.funcion_obj_2(sol.argvalue); // a las funciones obetivo del problema
		double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto
		double solauxfuncion2;
		//SolucionVRP *elim;
	
		for (int i =0; i<cantSoluciones; i++)
		{
			solauxfuncion1 =prob.argvalue.funcion_obj_1(listaVRP[i]);
			solauxfuncion2 =prob.argvalue.funcion_obj_2(listaVRP[i]);
			// ambas funciones objetivo siempre se minimizan
			if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2))
			{
				//elim=listaVRP[i];
				listaVRP[i].destruir();
				listaVRP[i] =listaVRP[cantSoluciones-1];
				listaVRP[cantSoluciones-1] =null; //liberar puntero
				cantSoluciones--;
				i--;
				//elim->destruir();
			}
		}
	}
	public void listarSoluciones(RefObject<Problem> prob, RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"w");
		fprintf(f,"%d\n",cantSoluciones);
		for (int i =0;i<cantSoluciones;i++)
		{
			fprintf(f,"%lf\t%lf\n",prob.argvalue.funcion_obj_1(lista[i]),prob.argvalue.funcion_obj_2(lista[i]));
	//		lista[i]->imprimir(f);
		}
		fclose(f);
	}
	public void listarSolucionesVRP(RefObject<Problem> prob, RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"w");
		fprintf(f,"%d\n",cantSoluciones);
		for (int i =0;i<cantSoluciones;i++)
		{
			fprintf(f,"%lf\t%lf\n",prob.argvalue.funcion_obj_1(listaVRP[i]),prob.argvalue.funcion_obj_2(listaVRP[i]));
			//listaVRP[i]->imprimir(f);
		}
		fclose(f);
	}
	public int getSize()
	{
		return cantSoluciones;
	}
	public Solucion getSolucion(int i)
	{
		return lista[i];
	}
	public SolucionVRP getSolucionVRP(int i)
	{
		return listaVRP[i];
	}
	public void enviar(int tid)
	{
		}
	public void recibir(int tid)
	{
		}
}

//void ConjuntoPareto::enviar(int tid)
//{
//	int msgtype=PARETO;
//	int aux;
//	pvm_initsend(PvmDataDefault);
//	pvm_pkint(&cantSoluciones,1,1);
//	aux=lista[0]->getSize();
//	pvm_pkint(&aux,1,1);
//	for (int i=0; i<cantSoluciones; i++)
//	{
//		for (int j=0;j<lista[i]->getSize();j++)
//		{
//			aux=lista[i]->get(j);
//			pvm_pkint(&aux,1,1);
//		}
//	}
//	pvm_send(tid,msgtype);
//}
//
//void ConjuntoPareto::recibir(int tid)
//{
//	int msgtype=PARETO;
//	int tamsol=0,nodo=0;
//	pvm_recv(tid,msgtype);
//	pvm_upkint(&cantSoluciones,1,1);
//	pvm_upkint(&tamsol,1,1);
//	for (int i=0; i<cantSoluciones; i++)
//	{
//		if(lista[i]==NULL)
//			lista[i]=new Solucion(tamsol);
//		for (int j=0;j<tamsol;j++)
//		{
//			pvm_upkint(&nodo,1,1);
//			lista[i]->set(j,nodo);
//		}
//	}
//}

public class MOACO
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