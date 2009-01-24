/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

/**
 *
 * @author Christian Gomez
 */
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
		lista = new Solucion[numSoluciones][numSoluciones];
		listaVRP = new SolucionVRP[numSoluciones][numSoluciones];
		for(int i =0;i<numSoluciones;i++)
                {
                    for(int j =0;j<numSoluciones;j++)
                    {
			lista[i][j] =null;
			listaVRP[i][j] =null;
                    }
                }
	}
	public int agregarNoDominado(Solucion sol, Problem prob)
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
		//Aumentar el tama�o del conjunto Pareto si �ste est� lleno
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
		//Aumentar el tama�o del conjunto Pareto si �ste est� lleno
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

