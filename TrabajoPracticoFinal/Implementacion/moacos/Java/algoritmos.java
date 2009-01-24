package moacos;

import java.util.*;
//*********************BICRITERION ANT************************************
public class BicriterionAnt extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private TablaFeromona tabla2;
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private int hormigaActual; // utilizado para calcular los pesos lambda
	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla2 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
		tabla2.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del BicriterionAnt
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, int cantNoDominados)
	{
		double valorTabla;
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar ambas tablas en una cantidad 1/cantNoDominados
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			valorTabla =tabla1.obtenerValor(j,k);
			tabla1.actualizar(j,k,valorTabla+1.0/cantNoDominados);
			valorTabla =tabla2.obtenerValor(j,k);
			tabla2.actualizar(j,k,valorTabla+1.0/cantNoDominados);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		double lambda;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
	
		lambda =hormigaActual/(hormigas-1); // peso de la hormiga actual
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),lambda *alfa)*Math.pow(tabla2.obtenerValor(estOrigen,i),(1-lambda)*alfa) *Math.pow(heuristica1,lambda *beta)*Math.pow(heuristica2,(1-lambda)*beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas()
	{
		for(int i =0;i<prob.getSize();i++)
		{
			for(int j =0;j<prob.getSize();j++)
			{
				tabla1.actualizar(i,j,tabla1.obtenerValor(i,j)*(1-rho));
				tabla2.actualizar(i,j,tabla2.obtenerValor(i,j)*(1-rho));
			}
		}
	}
	public BicriterionAnt(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tablas();
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas];
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i; // utilizado en seleccionar_sgte_estado
				construir_solucionTSP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados;i++)
			{
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados);
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/tsp.biant.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas];
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i; // utilizado en seleccionar_sgte_estado
				construir_solucionQAP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados;i++)
			{
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados);
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/qap.biant.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
			int[] indice = new int[hormigas];
		clock_t start;
		clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize()*2);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i; // utilizado en seleccionar_sgte_estado
				construir_solucionVRP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados;i++)
			{
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSizeActual(), cantNoDominados);
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/qap.biant.pareto");
	}
}

//*********************BICRITERION MC************************************
public class BicriterionMC extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private TablaFeromona tabla2;
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private double lambdaActual; // utilizado para calcular los pesos lambda actual
	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla2 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
		tabla2.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del BicriterionMC
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, int cantNoDominados, RefObject<TablaFeromona> tab)
	{
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar ambas tablas en una cantidad 1/cantNoDominados
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			tabla1.actualizar(j,k,tab.argvalue.obtenerValor(j,k)+1.0/cantNoDominados);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
	
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),lambdaActual *alfa)*Math.pow(tabla2.obtenerValor(estOrigen,i),(1-lambdaActual)*alfa) *Math.pow(heuristica1,lambdaActual *beta)*Math.pow(heuristica2,(1-lambdaActual)*beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas()
	{
		for(int i =0;i<prob.getSize();i++)
		{
			for(int j =0;j<prob.getSize();j++)
			{
				tabla1.actualizar(i,j,tabla1.obtenerValor(i,j)*(1-rho));
				tabla2.actualizar(i,j,tabla2.obtenerValor(i,j)*(1-rho));
			}
		}
	}
	private int insertar(RefObject<Double> array, double costoActual, int cantNoDominados)
	{
		for(int i =cantNoDominados-1;i>=0;i--)
			if(array.argvalue[i]<=costoActual)
				return i+1;
	
		return 0;
	}
	public BicriterionMC(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tablas();
		if(hormigas%2!=0) // se utiliza un numero par de hormigas
			hormigas++;
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas]; // array que contiene el indice en sols de una solucion actual no dominada
		double[] f1 = new double[hormigas]; // array donde se ordenan las soluciones no dominadas de la generacion por la funcion obj. 1
		double incrementoLambda =2.0/3.0/(hormigas/2 - 1); // para problemas bi-objetivos
		double lambda1;
		double lambda2;
		double costoActual;
		int pos;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			lambda1 =-incrementoLambda;
			lambda2 =1.0/3.0 - incrementoLambda;
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				lambda1+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda1;
				construir_solucionTSP(estOrigen,this,0,sols[i]);
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				lambda2+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda2;
				construir_solucionTSP(estOrigen,this,0,sols[i+hormigas/2]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					costoActual =prob.funcion_obj_1(sols[i]);
					RefObject<Double> TempRefObject = new RefObject<Double>(f1);
					pos =insertar(TempRefObject, costoActual, cantNoDominados); // devuelve su lugar en orden creciente
					f1 = TempRefObject.argvalue;
					for(int j =cantNoDominados;j>pos;j--)
					{
						f1[j] =f1[j-1];
						indice[j] =indice[j-1];
					}
					f1[pos] =costoActual;
					indice[pos] =i;
					cantNoDominados++;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados/2;i++) // la primera region actualiza la tabla 1
			{
				RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados, TempRefObject2);
				tabla1 = TempRefObject2.argvalue;
				sols[indice[i]].resetear();
			}
			for(int i =cantNoDominados/2;i<cantNoDominados;i++) // la segunda region actualiza la tabla 2
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados, TempRefObject3);
				tabla2 = TempRefObject3.argvalue;
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/tsp.bimc.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas]; // array que contiene el indice en sols de una solucion actual no dominada
		double[] f1 = new double[hormigas]; // array donde se ordenan las soluciones no dominadas de la generacion por la funcion obj. 1
		double incrementoLambda =2.0/3.0/(hormigas/2 - 1); // para problemas bi-objetivos
		double lambda1;
		double lambda2;
		double costoActual;
		int pos;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			lambda1 =-incrementoLambda;
			lambda2 =1.0/3.0 - incrementoLambda;
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				lambda1+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda1;
				construir_solucionQAP(estOrigen,this,0,sols[i]);
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				lambda2+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda2;
				construir_solucionQAP(estOrigen,this,0,sols[i+hormigas/2]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					costoActual =prob.funcion_obj_1(sols[i]);
					RefObject<Double> TempRefObject = new RefObject<Double>(f1);
					pos =insertar(TempRefObject, costoActual, cantNoDominados); // devuelve su lugar en orden creciente
					f1 = TempRefObject.argvalue;
					for(int j =cantNoDominados;j>pos;j--)
					{
						f1[j] =f1[j-1];
						indice[j] =indice[j-1];
					}
					f1[pos] =costoActual;
					indice[pos] =i;
					cantNoDominados++;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados/2;i++) // la primera region actualiza la tabla 1
			{
				RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados, TempRefObject2);
				tabla1 = TempRefObject2.argvalue;
				sols[indice[i]].resetear();
			}
			for(int i =cantNoDominados/2;i<cantNoDominados;i++) // la segunda region actualiza la tabla 2
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), cantNoDominados, TempRefObject3);
				tabla2 = TempRefObject3.argvalue;
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/qap.bimc.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
			int[] indice = new int[hormigas]; // array que contiene el indice en sols de una solucion actual no dominada
			double[] f1 = new double[hormigas]; // array donde se ordenan las soluciones no dominadas de la generacion por la funcion obj. 1
		double incrementoLambda =2.0/3.0/(hormigas/2 - 1); // para problemas bi-objetivos
		double lambda1;
		double lambda2;
		double costoActual;
		int pos;
			clock_t start;
			clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			lambda1 =-incrementoLambda;
			lambda2 =1.0/3.0 - incrementoLambda;
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				lambda1+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda1;
				construir_solucionVRP(estOrigen,this,0,sols[i]);
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				lambda2+=incrementoLambda; // utilizado en seleccionar_sgte_estado
				lambdaActual =lambda2;
				construir_solucionVRP(estOrigen,this,0,sols[i+hormigas/2]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					costoActual =prob.funcion_obj_1(sols[i]);
					RefObject<Double> TempRefObject = new RefObject<Double>(f1);
					pos =insertar(TempRefObject, costoActual, cantNoDominados); // devuelve su lugar en orden creciente
					f1 = TempRefObject.argvalue;
					for(int j =cantNoDominados;j>pos;j--)
					{
						f1[j] =f1[j-1];
						indice[j] =indice[j-1];
					}
					f1[pos] =costoActual;
					indice[pos] =i;
					cantNoDominados++;
				}
				else
					sols[i].resetear();
			}
	
			for(int i =0;i<cantNoDominados/2;i++) // la primera region actualiza la tabla 1
			{
				RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSizeActual(), cantNoDominados, TempRefObject2);
				tabla1 = TempRefObject2.argvalue;
				sols[indice[i]].resetear();
			}
			for(int i =cantNoDominados/2;i<cantNoDominados;i++) // la segunda region actualiza la tabla 2
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSizeActual(), cantNoDominados, TempRefObject3);
				tabla2 = TempRefObject3.argvalue;
				sols[indice[i]].resetear();
			}
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/qap.bimc.pareto");
	}
}

//*********************COMPET ANTS************************************
public class CompeteAnts extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private TablaFeromona tabla2;
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private double AVR1;
	private double AVR2;
	private int esEspia; // indica si la hormiga actual es espia o no
	private int coloniaActual; // indica la colonia actual, 1 o 2
	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla2 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
		tabla2.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del CompeteAnts
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else if(stricmp(cad,"AVR1")==0)
				fscanf(f, "%lf", AVR1);
			else if(stricmp(cad,"AVR2")==0)
				fscanf(f, "%lf", AVR2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, RefObject<TablaFeromona> tab, double deltaTao)
	{
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar ambas tablas en una cantidad 1/cantNoDominados
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			tab.argvalue.actualizar(j,k,tab.argvalue.obtenerValor(j,k)+deltaTao);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		TablaFeromona tab;
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				if(coloniaActual ==1)
				{
					heuristica =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
					tab =tabla1;
				}
				else
				{
					heuristica =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
					tab =tabla2;
				}
				if (esEspia == 0)
					productos[i]=Math.pow(tab.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica,beta);
				else
					productos[i]=Math.pow(0.5 tabla1.obtenerValor(estOrigen,i) + 0.5 tabla2.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica,beta);
	
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas(RefObject<TablaFeromona> tab)
	{
		for(int i =0;i<prob.getSize();i++)
			for(int j =0;j<prob.getSize();j++)
				tab.argvalue.actualizar(i,j,tab.argvalue.obtenerValor(i,j)*(1-rho));
	}
	private double calcular_media(double[] array, int size)
	{
		double media =0.0;
		for(int i =0;i<size;i++)
			media+=array[i];
	
		return media / size;
	}
	private int adaptar_numero_hormigas(double media1, double media2)
	{
		double random;
		double prob;
		int hormigas1 =0;
		prob = media2 / AVR2 / (media1/AVR1 + media2/AVR2); // normalizado
		for(int i =0;i<hormigas;i++)
		{
			random = (double)(RandomNumbers.nextNumber()%10000) / 10000.0;
			if(random < prob)
				hormigas1++;
		}
	
		return hormigas1;
	}
	private int adaptar_numero_espias(double best, double bestPrima, int popSize, int colonia)
	{
		double random;
		double prob;
		int espias =0;
	
		if(colonia ==1)
			prob = best/F1MAX / (best/F1MAX + 4.0 * bestPrima / F2MAX); // normalizado
		else
			prob = best/F2MAX / (best/F2MAX + 4.0 * bestPrima / F1MAX); // normalizado
		for(int i =0;i<popSize;i++)
		{
			random = (double)(RandomNumbers.nextNumber()%10000) / 10000.0;
			if(random < prob)
				espias++;
		}
	
		return espias;
	}
	private int buscar_pos(RefObject<Double> array, int size, double costo)
	{
		for(int i =size;i>=0;i--)
		{
			if(array.argvalue[i]<=costo)
				return (i+1);
		}
	
		return 0;
	}
	public CompeteAnts(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		AVR1 =1; //por defecto
		AVR2 =1; //por defecto
		inicializar_parametros(file);
		inicializar_tablas();
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		int hormigas1;
		int hormigas2;
		int espias1;
		int espias2;
		int auxEspias;
		int pos;
		double cantBest1;
		double cantBest2;
		double evalAux;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		Solucion sols2 =new Solucion[hormigas](prob.getSize()+1);
		Solucion solAux = new Solucion(prob.getSize()+1);
		double[] evaluaciones1 = new double[hormigas];
		double[] evaluaciones2 = new double[hormigas];
		start =clock();
		end =start;
		hormigas1 =hormigas/2;
		hormigas2 =hormigas-hormigas1;
		espias1 =0;
		espias2 =0;
	
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantBest1 =hormigas1 * 0.0625; //valor tomado de los autores originales del algoritmo
			cantBest2 =hormigas2 * 0.0625; //valor tomado de los autores originales del algoritmo
			auxEspias =espias1;
			esEspia =1;
			coloniaActual =1;
			for(int i =0;i<hormigas1;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionTSP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_1(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject = new RefObject<Double>(evaluaciones1);
				pos =buscar_pos(TempRefObject, i-1, evalAux);
				evaluaciones1 = TempRefObject.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols[k].solcpy(sols[k-1]);
					evaluaciones1[k] =evaluaciones1[k-1];
				}
				sols[pos].solcpy(solAux);
				evaluaciones1[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
			evaporar_feromonas(TempRefObject2);
			tabla1 = TempRefObject2.argvalue;
			for(int i =0;i<cantBest1;i++)
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[i], sols[i].getSize(), TempRefObject3, 1.0 - (double)i/(double)cantBest1);
				tabla1 = TempRefObject3.argvalue;
			}
	
			auxEspias =espias2;
			esEspia =1;
			coloniaActual =2;
			for(int i =0;i<hormigas2;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionTSP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_2(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject4 = new RefObject<Double>(evaluaciones2);
				pos =buscar_pos(TempRefObject4, i-1, evalAux);
				evaluaciones2 = TempRefObject4.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols2[k].solcpy(sols2[k-1]);
					evaluaciones2[k] =evaluaciones2[k-1];
				}
				sols2[pos].solcpy(solAux);
				evaluaciones2[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject5 = new RefObject<TablaFeromona>(tabla2);
			evaporar_feromonas(TempRefObject5);
			tabla2 = TempRefObject5.argvalue;
			for(int i =0;i<cantBest2;i++)
			{
				RefObject<TablaFeromona> TempRefObject6 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols2[i], sols2[i].getSize(), TempRefObject6, 1.0 - (double)i/(double)cantBest2);
				tabla2 = TempRefObject6.argvalue;
			}
	
			// actualizar frente pareto con soluciones de la primera colonia
			for(int i =0;i<hormigas1;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
					//sols[i].destruir();
			}
			// actualizar frente pareto con soluciones de la segunda colonia
			for(int i =0;i<hormigas2;i++)
			{
				if (pareto.agregarNoDominado(sols2[i],prob))
				{
					pareto.eliminarDominados(sols2[i],prob);
				}
				//else
					//sols2[i].destruir();
			}
	
			// adaptar numero de hormigas y homigas espias
			hormigas1 =adaptar_numero_hormigas(calcular_media(evaluaciones1, hormigas1), calcular_media(evaluaciones2, hormigas2));
			hormigas2 =hormigas - hormigas1;
			espias1 =adaptar_numero_espias(evaluaciones1[0], evaluaciones2[0], hormigas1, 1);
			espias2 =adaptar_numero_espias(evaluaciones2[0], evaluaciones1[0], hormigas2, 2);
	
			end =clock();
		}
		//pareto->listarSoluciones(prob,"/home/fuentes/tsp.comp.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		int hormigas1;
		int hormigas2;
		int espias1;
		int espias2;
		int auxEspias;
		int pos;
		double cantBest1;
		double cantBest2;
		double evalAux;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		Solucion sols2 =new Solucion[hormigas](prob.getSize());
		Solucion solAux = new Solucion(prob.getSize());
		double[] evaluaciones1 = new double[hormigas];
		double[] evaluaciones2 = new double[hormigas];
		start =clock();
		end =start;
		hormigas1 =hormigas/2;
		hormigas2 =hormigas-hormigas1;
		espias1 =0;
		espias2 =0;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantBest1 =hormigas1 * 0.0625; //valor tomado de los autores originales del algoritmo
			cantBest2 =hormigas2 * 0.0625; //valor tomado de los autores originales del algoritmo
			auxEspias =espias1;
			esEspia =1;
			for(int i =0;i<hormigas1;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionQAP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_1(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject = new RefObject<Double>(evaluaciones1);
				pos =buscar_pos(TempRefObject, i-1, evalAux);
				evaluaciones1 = TempRefObject.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols[k].solcpy(sols[k-1]);
					evaluaciones1[k] =evaluaciones1[k-1];
				}
				sols[pos].solcpy(solAux);
				evaluaciones1[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
			evaporar_feromonas(TempRefObject2);
			tabla1 = TempRefObject2.argvalue;
			for(int i =0;i<cantBest1;i++)
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[i], sols[i].getSize(), TempRefObject3, 1.0 - (double)i/(double)cantBest1);
				tabla1 = TempRefObject3.argvalue;
			}
	
			auxEspias =espias2;
			esEspia =1;
			for(int i =0;i<hormigas2;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionQAP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_2(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject4 = new RefObject<Double>(evaluaciones2);
				pos =buscar_pos(TempRefObject4, i-1, evalAux);
				evaluaciones2 = TempRefObject4.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols2[k].solcpy(sols2[k-1]);
					evaluaciones2[k] =evaluaciones2[k-1];
				}
				sols2[pos].solcpy(solAux);
				evaluaciones2[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject5 = new RefObject<TablaFeromona>(tabla2);
			evaporar_feromonas(TempRefObject5);
			tabla2 = TempRefObject5.argvalue;
			for(int i =0;i<cantBest2;i++)
			{
				RefObject<TablaFeromona> TempRefObject6 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols2[i], sols2[i].getSize(), TempRefObject6, 1.0 - (double)i/(double)cantBest2);
				tabla2 = TempRefObject6.argvalue;
			}
	
			// actualizar frente pareto con soluciones de la primera colonia
			for(int i =0;i<hormigas1;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
					//sols[i].destruir();
			}
			// actualizar frente pareto con soluciones de la segunda colonia
			for(int i =0;i<hormigas2;i++)
			{
				if (pareto.agregarNoDominado(sols2[i],prob))
				{
					pareto.eliminarDominados(sols2[i],prob);
				}
				//else
					//sols2[i].destruir();
			}
	
			// adaptar numero de hormigas y homigas espias
			hormigas1 =adaptar_numero_hormigas(calcular_media(evaluaciones1, hormigas1), calcular_media(evaluaciones2, hormigas2));
			hormigas2 =hormigas - hormigas1;
			espias1 =adaptar_numero_espias(evaluaciones1[0], evaluaciones2[0], hormigas1, 1);
			espias2 =adaptar_numero_espias(evaluaciones2[0], evaluaciones1[0], hormigas2, 2);
	
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.comp.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		int hormigas1;
		int hormigas2;
		   int espias1;
		   int espias2;
		   int auxEspias;
		   int pos;
			double cantBest1;
			double cantBest2;
			double evalAux;
			clock_t start;
			clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize()*2);
		SolucionVRP sols2 =new SolucionVRP[hormigas](prob.getSize()*2);
		SolucionVRP solAux = new SolucionVRP(prob.getSize()*2);
		double[] evaluaciones1 = new double[hormigas];
		double[] evaluaciones2 = new double[hormigas];
		start =clock();
		end =start;
		hormigas1 =hormigas/2;
		hormigas2 =hormigas-hormigas1;
		espias1 =0;
		espias2 =0;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantBest1 =hormigas1 * 0.0625; //valor tomado de los autores originales del algoritmo
			cantBest2 =hormigas2 * 0.0625; //valor tomado de los autores originales del algoritmo
			auxEspias =espias1;
			esEspia =1;
			for(int i =0;i<hormigas1;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionVRP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_1(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject = new RefObject<Double>(evaluaciones1);
				pos =buscar_pos(TempRefObject, i-1, evalAux);
				evaluaciones1 = TempRefObject.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols[k].solcpy(sols[k-1]);
					evaluaciones1[k] =evaluaciones1[k-1];
				}
				sols[pos].solcpy(solAux);
				evaluaciones1[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
			evaporar_feromonas(TempRefObject2);
			tabla1 = TempRefObject2.argvalue;
			for(int i =0;i<cantBest1;i++)
			{
				RefObject<TablaFeromona> TempRefObject3 = new RefObject<TablaFeromona>(tabla1);
				actualizar_feromonas(sols[i], sols[i].getSizeActual(), TempRefObject3, 1.0 - (double)i/(double)cantBest1);
				tabla1 = TempRefObject3.argvalue;
			}
	
			auxEspias =espias2;
			esEspia =1;
			for(int i =0;i<hormigas2;i++)
			{
				if(auxEspias<=0)
					esEspia =0;
				else
					auxEspias--;
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionVRP(estOrigen,this,0,solAux);
				evalAux =prob.funcion_obj_2(solAux);
				// insertar en sols ordenado
				RefObject<Double> TempRefObject4 = new RefObject<Double>(evaluaciones2);
				pos =buscar_pos(TempRefObject4, i-1, evalAux);
				evaluaciones2 = TempRefObject4.argvalue;
				for(int k =i;k>pos;k--)
				{
					sols2[k].solcpy(sols2[k-1]);
					evaluaciones2[k] =evaluaciones2[k-1];
				}
				sols2[pos].solcpy(solAux);
				evaluaciones2[pos] =evalAux;
				solAux.resetear();
			}
			RefObject<TablaFeromona> TempRefObject5 = new RefObject<TablaFeromona>(tabla2);
			evaporar_feromonas(TempRefObject5);
			tabla2 = TempRefObject5.argvalue;
			for(int i =0;i<cantBest2;i++)
			{
				RefObject<TablaFeromona> TempRefObject6 = new RefObject<TablaFeromona>(tabla2);
				actualizar_feromonas(sols2[i], sols2[i].getSizeActual(), TempRefObject6, 1.0 - (double)i/(double)cantBest2);
				tabla2 = TempRefObject6.argvalue;
			}
	
			// actualizar frente pareto con soluciones de la primera colonia
			for(int i =0;i<hormigas1;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
					//sols[i].destruir();
			}
			// actualizar frente pareto con soluciones de la segunda colonia
			for(int i =0;i<hormigas2;i++)
			{
				if (pareto.agregarNoDominado(sols2[i],prob))
				{
					pareto.eliminarDominados(sols2[i],prob);
				}
				//else
					//sols2[i].destruir();
			}
	
			// adaptar numero de hormigas y homigas espias
			hormigas1 =adaptar_numero_hormigas(calcular_media(evaluaciones1, hormigas1), calcular_media(evaluaciones2, hormigas2));
			hormigas2 =hormigas - hormigas1;
			espias1 =adaptar_numero_espias(evaluaciones1[0], evaluaciones2[0], hormigas1, 1);
			espias2 =adaptar_numero_espias(evaluaciones2[0], evaluaciones1[0], hormigas2, 2);
	
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.comp.pareto");
	}
}

//*********************MAS************************************

public class MAS extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private int hormigaActual; // utilizado para calcular los pesos lambda
	private int noLambdas;
	private void inicializar_tabla()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del MAS
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, double deltaTau)
	{
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar ambas tablas en una cantidad 1/cantNoDominados
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			tabla1.actualizar(j,k,tabla1.obtenerValor(j,k)+deltaTau);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		double lambda1;
		double lambda2;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		if (noLambdas != 0)
			lambda1 =lambda2 =1;
		else
		{
			lambda1 =hormigaActual;
			lambda2 =hormigas-hormigaActual+1;
		}
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
	
	productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica1,lambda1 *beta)*Math.pow(heuristica2,lambda2 *beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas()
	{
		for(int i =0;i<prob.getSize();i++)
			for(int j =0;j<prob.getSize();j++)
				tabla1.actualizar(i,j,tabla1.obtenerValor(i,j)*(1-rho));
	}
	private double calcular_delta_tao(RefObject<Solucion> sol)
	{
		double delta;
	
		delta =1.0/(prob.funcion_obj_1(sol.argvalue)/F1MAX + prob.funcion_obj_2(sol.argvalue)/F2MAX); //normalizados
		return delta;
	}
	public MAS(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tabla();
		noLambdas =0;
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
	
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas];
		int cambio;
		int noCambio =0;
		double deltaTao;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionTSP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
			cambio =0;
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
					cambio =1;
				}
				else
					sols[i].resetear();
			}
			// reiniciar la tabla si es necesario
			if (cambio == 0)
				noCambio++;
			else
				noCambio =0;
	
			if(noCambio >= 500) // no hubo cambios en el frente Pareto por 500 generaciones
			{
				tabla1.reiniciar(taoInicial);
				noCambio =0;
			}
			// actualizan la tabla los no dominados de la iteracion
			for(int i =0;i<cantNoDominados;i++)
			{
				deltaTao =calcular_delta_tao(sols[indice[i]]);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), deltaTao);
				sols[indice[i]].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.mas.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		int[] indice = new int[hormigas];
		int cambio;
		int noCambio =0;
		double deltaTao;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionQAP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
			cambio =0;
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
					cambio =1;
				}
				else
					sols[i].resetear();
			}
			// reiniciar la tabla si es necesario
			if (cambio == 0)
				noCambio++;
			else
				noCambio =0;
	
			if(noCambio >= 500) // no hubo cambios en el frente Pareto por 500 generaciones
			{
				tabla1.reiniciar(taoInicial);
				noCambio =0;
			}
			// actualizan la tabla los no dominados de la iteracion
			for(int i =0;i<cantNoDominados;i++)
			{
				deltaTao =calcular_delta_tao(sols[indice[i]]);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSize(), deltaTao);
				sols[indice[i]].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.mas.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
			int[] indice = new int[hormigas];
			int cambio;
			int noCambio =0;
			double deltaTao;
			clock_t start;
			clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize()*2);
		start =clock();
		end =start;
		noLambdas =1;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionVRP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
			cambio =0;
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					indice[cantNoDominados++] =i;
					cambio =1;
				}
				else
					sols[i].resetear();
			}
			// reiniciar la tabla si es necesario
			if (cambio == 0)
				noCambio++;
			else
				noCambio =0;
	
			if(noCambio >= 500) // no hubo cambios en el frente Pareto por 500 generaciones
			{
				tabla1.reiniciar(taoInicial);
				noCambio =0;
			}
			// actualizan la tabla los no dominados de la iteracion
			for(int i =0;i<cantNoDominados;i++)
			{
				deltaTao =calcular_delta_tao(sols[indice[i]]);
				actualizar_feromonas(sols[indice[i]], sols[indice[i]].getSizeActual(), deltaTao);
				sols[indice[i]].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.mas.pareto");
	}
}

//*********************MOA************************************

public class MOA extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private TablaFeromona tabla2;
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double omicron; // Limite superior impuesto a las feromonas
	private double k; // cantidad de iteraciones entre actualizaciones de la tabla
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private int hormigaActual;
	private int noLambdas;
	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del MOA
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"OMICRON")==0)
				fscanf(f, "%lf", omicron);
			else if(stricmp(cad,"K")==0)
				fscanf(f, "%lf", k);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, int m)
	{
		double valorTabla;
		double deltatau =omicron/m;
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar cada arco utilizado con el valor Omicron/|pareto|
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			valorTabla =tabla1.obtenerValor(j,k);
			tabla1.actualizar(j,k,valorTabla+deltatau);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		double lambda1;
		double lambda2;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		if (noLambdas != 0)
			lambda1 =lambda2 =1;
		else
		{
			lambda1 =hormigaActual; // peso de la hormiga actual para el objetivo 1
			lambda2 =hormigas - lambda1 + 1; // peso para el objetivo 2
		}
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica1,beta *lambda1)*Math.pow(heuristica2,beta *lambda2);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	public MOA(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		noLambdas =0;
		inicializar_parametros(file);
		inicializar_tablas();
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int m;
		int tInicio;
		int tFin;
		Solucion sol = new Solucion(prob.getSize());
	
		for (int i =0; i<hormigas;i++)
		{
			construir_solucionTSP(RandomNumbers.nextNumber()%prob.getSize(),this,0,sol);
			if (pareto.agregarNoDominado(sol,prob))
				pareto.eliminarDominados(sol,prob);
			//else
			sol.resetear();
		}
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion+=(int)k/hormigas;
			m =pareto.getSize();
			tabla1.reiniciar(taoInicial);
			for (int i =0;i<pareto.getSize();i++)
				actualizar_feromonas(*(pareto.getSolucion(i)), pareto.getSolucion(i).getSize(), m);
			for (int i =0; i<k;i++)
			{
				hormigaActual =i%hormigas + 1;
				construir_solucionTSP(RandomNumbers.nextNumber()%prob.getSize(),this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.moa.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int m;
		int tInicio;
		int tFin;
		Solucion sol = new Solucion(prob.getSize());
	
		for (int i =0; i<hormigas;i++)
		{
			construir_solucionQAP(RandomNumbers.nextNumber()%prob.getSize(),this,0,sol);
			if (pareto.agregarNoDominado(sol,prob))
				pareto.eliminarDominados(sol,prob);
			//else
			sol.resetear();
		}
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion++;
			m =pareto.getSize();
			tabla1.reiniciar(taoInicial);
			for (int i =0;i<pareto.getSize();i++)
				actualizar_feromonas(*(pareto.getSolucion(i)), pareto.getSolucion(i).getSize(), m);
			for (int i =0; i<k;i++)
			{
				hormigaActual =i%hormigas + 1;
				construir_solucionQAP(RandomNumbers.nextNumber()%prob.getSize(),this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moa.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int m;
		int tInicio;
		int tFin;
		SolucionVRP sol = new SolucionVRP(prob.getSize()*2);
		noLambdas =1;
		for (int i =0; i<hormigas;i++)
		{
			construir_solucionVRP(0,this,0,sol);
			if (pareto.agregarNoDominado(sol,prob))
				pareto.eliminarDominados(sol,prob);
			//else
			sol.resetear();
		}
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion++;
			m =pareto.getSize();
			tabla1.reiniciar(taoInicial);
			for (int i =0;i<pareto.getSize();i++)
				actualizar_feromonas(*(pareto.getSolucionVRP(i)), pareto.getSolucionVRP(i).getSizeActual(), m);
			for (int i =0; i<k;i++)
			{
				hormigaActual =i%hormigas + 1;
				construir_solucionVRP(0,this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moa.pareto");
	}
}

//*********************MOAQ************************************

public class MOAQ extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private double tmax; // prob. utilizada en el pseudo-random rule
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double gama; // Factor de descuento
	private double lambda; // constante utilizada en el pseudo-random rule
	private double t; // valor estocastico utilizado en el pseudo-random rule
	private int familiaActual; // indica que objetivo actualiza la colonia actual
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;

	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del MOAQ
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"GAMA")==0)
				fscanf(f, "%lf", gama);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"LAMBDA")==0)
				fscanf(f, "%lf", lambda);
			else if(stricmp(cad,"TMAX")==0)
				fscanf(f, "%lf", tmax);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize)
	{
		double reward;
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // aplicar reward al arco j,k de ambas tablas
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			reward =1.0/(prob.funcion_obj_1(sol.argvalue)/F1MAX + prob.funcion_obj_2(sol.argvalue)/F2MAX); // normalizado
			tabla1.actualizar(j,k,tabla1.obtenerValor(j,k) + rho * reward);
		}
	}
	private double calcular_max_q(int estOrig, RefObject<TablaFeromona> tab)
	{
		double max =0;
		double valorTabla;
		for(int i =0;i<prob.getSize();i++)
		{
			valorTabla =tab.argvalue.obtenerValor(estOrig,i);
			if(valorTabla>max)
				max =valorTabla;
		}
	
		return max;
	}
	private int seleccionar_mayor(int estOrigen, int[] visitados, RefObject<TablaFeromona> tab)
	{
		int sgteEstado;
		double mayorValor =-1; // inicializar a un valor peque�o
		double heuristica;
		double valorActual;
		double random;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		for (int i =0;i<prob.getSize();i++)
		{
			if (visitados[i]!=1) // estado i no visitado
			{
				if(familiaActual ==1)
					heuristica =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				else
					heuristica =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				valorActual =Math.pow(tab.argvalue.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica,beta);
				if(valorActual>mayorValor)
				{
					mayorValor =valorActual;
					sgteEstado =i;
				}
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(mayorValor ==-1)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		return sgteEstado;
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados, RefObject<TablaFeromona> tab)
	{
		int sgteEstado;
		double heuristica;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				if(familiaActual ==1)
					heuristica =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				else
					heuristica =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tab.argvalue.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica,beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =0;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas()
	{
		double maxQ;
		for(int i =0;i<prob.getSize();i++)
		{
			for(int j =0;j<prob.getSize();j++)
			{
				RefObject<TablaFeromona> TempRefObject = new RefObject<TablaFeromona>(tabla1);
				maxQ =calcular_max_q(j, TempRefObject);
				tabla1 = TempRefObject.argvalue;
				tabla1.actualizar(i,j,tabla1.obtenerValor(i,j)*(1-rho)+rho *gama *maxQ);
			}
		}
	}
	public MOAQ(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tablas();
		t =Math.pow(tmax,2)/lambda;
		if(hormigas%2!=0)
			hormigas++;
	}
	public void setFamiliaActual(int obj)
	{
		familiaActual =obj;
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		double randomValue =RandomNumbers.nextNumber()/(double)RAND_MAX; // valor aleatorio entre 0 y 1
	
		// marcar estados ya visitados
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		if (randomValue<=t)
		{
			RefObject<TablaFeromona> TempRefObject = new RefObject<TablaFeromona>(tabla1);
			sgteEstado =seleccionar_mayor(estOrigen, visitados, TempRefObject);
			tabla1 = TempRefObject.argvalue;
		}
		else
		{
			RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados, TempRefObject2);
			tabla1 = TempRefObject2.argvalue;
		}
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		double randomValue =RandomNumbers.nextNumber()/(double)RAND_MAX; // valor aleatorio entre 0 y 1
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
		{
			if (randomValue<=t)
			{
				RefObject<TablaFeromona> TempRefObject = new RefObject<TablaFeromona>(tabla1);
				sgteEstado =seleccionar_mayor(estOrigen, visitados, TempRefObject);
				tabla1 = TempRefObject.argvalue;
			}
			else
			{
				RefObject<TablaFeromona> TempRefObject2 = new RefObject<TablaFeromona>(tabla1);
				sgteEstado =seleccionar_probabilistico(estOrigen, visitados, TempRefObject2);
				tabla1 = TempRefObject2.argvalue;
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			t =t *lambda/tmax; // actualizar para la regla pseudo-aleatoria
			// familia 1
			familiaActual =1; //optimizar de acuerdo al objetivo 1
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionTSP(estOrigen,this,0,sols[i]);
			}
			// familia 2
			familiaActual =2; //optimizar de acuerdo al objetivo 2
			for(int i =hormigas/2;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionTSP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					actualizar_feromonas(sols[i], sols[i].getSize());
				}
				//else
				sols[i].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.moaq.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			t =t *lambda/tmax; // actualizar para la regla pseudo-aleatoria
			// familia 1
			familiaActual =1; //optimizar de acuerdo al objetivo 1
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionQAP(estOrigen,this,0,sols[i]);
			}
			// familia 2
			familiaActual =2; //optimizar de acuerdo al objetivo 2
			for(int i =hormigas/2;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionQAP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					actualizar_feromonas(sols[i], sols[i].getSize());
				}
				//else
				sols[i].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moaq.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
			clock_t start;
			clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize()*2);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			t =t *lambda/tmax; // actualizar para la regla pseudo-aleatoria
			// familia 1
			familiaActual =1; //optimizar de acuerdo al objetivo 1
			for(int i =0;i<hormigas/2;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionVRP(estOrigen,this,0,sols[i]);
			}
			// familia 2
			familiaActual =2; //optimizar de acuerdo al objetivo 2
			for(int i =hormigas/2;i<hormigas;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				construir_solucionVRP(estOrigen,this,0,sols[i]);
			}
	
			evaporar_feromonas();
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
					actualizar_feromonas(sols[i], sols[i].getSizeActual());
				}
				//else
				sols[i].resetear();
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moaq.pareto");
	}
}

//*********************PACO************************************

public class PACO extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private TablaFeromona tabla2;

	private double peso1; // Necesarios para el calculo de las probabilidades
	private double peso2;
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Coeficiente de evaporacion
	private double q0; // Probabilidad de pseudoaleatoriedad
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private Solucion mejorObj1;
	private Solucion segundoObj1;
	private Solucion mejorObj2;
	private Solucion segundoObj2;
	private SolucionVRP mejorObj1VRP;
	private SolucionVRP segundoObj1VRP;
	private SolucionVRP mejorObj2VRP;
	private SolucionVRP segundoObj2VRP;
	private void inicializar_tablas()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
		tabla2 =new TablaFeromona(prob.getSize());
		tabla2.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del PACO
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"Q0")==0)
				fscanf(f, "%lf", q0);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void online_update(int origen, int destino)
	{
		double tau;
		tau =(1-rho) tabla1.obtenerValor(origen,destino)+rho *taoInicial;
		tabla1.actualizar(origen,destino,tau);
		tau =(1-rho) tabla2.obtenerValor(origen,destino)+rho *taoInicial;
		tabla2.actualizar(origen,destino,tau);
	}
	private void actualizar_feromona_tabla1(RefObject<Solucion> sol)
	{
		double tau;
		double deltatau;
		deltatau =F1MAX/prob.funcion_obj_1( sol.argvalue);
	
		for (int i =0;i<sol.argvalue.getSize()-1;i++)
		{
			tau =tabla1.obtenerValor(sol.argvalue.get(i),sol.argvalue.get(i+1))+rho *deltatau;
			tabla1.actualizar(sol.argvalue.get(i),sol.argvalue.get(i+1),tau);
		}
	}
	private void actualizar_feromona_tabla2(RefObject<Solucion> sol)
	{
		double tau;
		double deltatau;
		deltatau =F2MAX/prob.funcion_obj_2( sol.argvalue);
	
		for (int i =0;i<sol.argvalue.getSize()-1;i++)
		{
			tau =tabla2.obtenerValor(sol.argvalue.get(i),sol.argvalue.get(i+1))+rho *deltatau;
			tabla2.actualizar(sol.argvalue.get(i),sol.argvalue.get(i+1),tau);
		}
	}
	private void actualizar_feromona_tabla1(RefObject<SolucionVRP> sol)
	{
		double tau;
		double deltatau;
		deltatau =F1MAX/prob.funcion_obj_1( sol.argvalue);
	
		for (int i =0;i<sol.argvalue.getSizeActual()-1;i++)
		{
			tau =tabla1.obtenerValor(sol.argvalue.get(i),sol.argvalue.get(i+1))+rho *deltatau;
			tabla1.actualizar(sol.argvalue.get(i),sol.argvalue.get(i+1),tau);
		}
	}
	private void actualizar_feromona_tabla2(RefObject<SolucionVRP> sol)
	{
		double tau;
		double deltatau;
		deltatau =F2MAX/prob.funcion_obj_2( sol.argvalue);
	
		for (int i =0;i<sol.argvalue.getSizeActual()-1;i++)
		{
			tau =tabla2.obtenerValor(sol.argvalue.get(i),sol.argvalue.get(i+1))+rho *deltatau;
			tabla2.actualizar(sol.argvalue.get(i),sol.argvalue.get(i+1),tau);
		}
	}
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//	void actualizar_feromona(SolucionVRP NamelessParameter1, TablaFeromona NamelessParameter2, int objetivo);
	private void evaporar_feromonas()
	{
		double tau1;
		double tau2;
		for (int i =0;i<prob.getSize();i++)
		{
			for (int j =0;j<prob.getSize();j++)
			{
				tau1 =tabla1.obtenerValor(i,j);
				tabla1.actualizar(i,j,(1-rho)*tau1);
				tau2 =tabla2.obtenerValor(i,j);
				tabla2.actualizar(i,j,(1-rho)*tau2);
			}
		}
	}
	private int seleccionar_mayor(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double mayorValor =-1; // inicializar a un valor peque�o
		double heuristica1;
		double heuristica2;
		double valorActual;
		double tau1;
		double tau2;
		double random;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		for (int i =0;i<prob.getSize();i++)
		{
			if (visitados[i]!=1) // estado i no visitado
			{
				heuristica1 =1.0/prob.heuristica_1(estOrigen,i); // normalizado
				heuristica2 =1.0/prob.heuristica_2(estOrigen,i); // normalizado
				tau1 =tabla1.obtenerValor(estOrigen,i)*peso1; // valor de tabla con la preferencia de la hormiga
				tau2 =tabla2.obtenerValor(estOrigen,i)*peso2; // valor de tabla con la preferencia de la hormiga
				valorActual =Math.pow(tau1+tau2,alfa)*Math.pow(2/(heuristica1/NORM1+heuristica2/NORM2),beta);
				sinPorcion[cantSinPorcion++] =i;
				if(valorActual>mayorValor)
				{
					mayorValor =valorActual;
					sgteEstado =i;
				}
			}
		}
		if(mayorValor ==-1)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		return sgteEstado;
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
		double tau1;
		double tau2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
	
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =1.0/prob.heuristica_1(estOrigen,i); // normalizado
				heuristica2 =1.0/prob.heuristica_2(estOrigen,i); // normalizado
				tau1 =tabla1.obtenerValor(estOrigen,i)*peso1; // valor de tabla con la preferencia de la hormiga
				tau2 =tabla2.obtenerValor(estOrigen,i)*peso2; // valor de tabla con la preferencia de la hormiga
				productos[i]=Math.pow(tau1+tau2,alfa)*Math.pow(2/(heuristica1/NORM1+heuristica2/NORM2),beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
	
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void encontrar_mejores(int objetivo)
	{
		double evMejor;
		double evActual;
		double evSeg =RAND_MAX;
		if (objetivo ==1)
		{
			mejorObj1.solcpy(*(pareto.getSolucion(0)));
			evMejor =prob.funcion_obj_1(*(pareto.getSolucion(0)));
		}
		else
		{
			mejorObj2.solcpy(*(pareto.getSolucion(0)));
			evMejor =prob.funcion_obj_2(*(pareto.getSolucion(0)));
		}
	
		for (int i =1;i<pareto.getSize();i++)
		{
			if (objetivo ==1)
			{
				evActual =prob.funcion_obj_1(*(pareto.getSolucion(i)));
				if (evActual<evMejor)
				{
					evSeg =evMejor;
					segundoObj1.solcpy( mejorObj1);
	
					evMejor =evActual;
					mejorObj1.solcpy(*(pareto.getSolucion(i)));
				}
				else if(evActual<evSeg)
				{
					evSeg =evActual;
					segundoObj1.solcpy(*(pareto.getSolucion(i)));
				}
			}
			else
			{
				evActual =prob.funcion_obj_2(*(pareto.getSolucion(i)));
				if (evActual<evMejor)
				{
					evSeg =evMejor;
					segundoObj2.solcpy( mejorObj2);
	
					evMejor =evActual;
					mejorObj2.solcpy(*(pareto.getSolucion(i)));
				}
				else if(evActual<evSeg)
				{
					evSeg =evActual;
					segundoObj2.solcpy(*(pareto.getSolucion(i)));
				}
			}
		}
	
	}
	private void encontrar_mejoresVRP(int objetivo)
	{
		double evMejor;
		double evActual;
		double evSeg =RAND_MAX;
		if (objetivo ==1)
		{
			mejorObj1VRP.solcpy(*(pareto.getSolucionVRP(0)));
			evMejor =prob.funcion_obj_1(*(pareto.getSolucionVRP(0)));
		}
		else
		{
			mejorObj2VRP.solcpy(*(pareto.getSolucionVRP(0)));
			evMejor =prob.funcion_obj_2(*(pareto.getSolucionVRP(0)));
		}
	
		for (int i =1;i<pareto.getSize();i++)
		{
			if (objetivo ==1)
			{
				evActual =prob.funcion_obj_1(*(pareto.getSolucionVRP(i)));
				if (evActual<evMejor)
				{
					evSeg =evMejor;
					segundoObj1VRP.solcpy( mejorObj1VRP);
	
					evMejor =evActual;
					mejorObj1VRP.solcpy(*(pareto.getSolucionVRP(i)));
				}
				else if(evActual<evSeg)
				{
					evSeg =evActual;
					segundoObj1VRP.solcpy(*(pareto.getSolucionVRP(i)));
				}
			}
			else
			{
				evActual =prob.funcion_obj_2(*(pareto.getSolucionVRP(i)));
				if (evActual<evMejor)
				{
					evSeg =evMejor;
					segundoObj2VRP.solcpy( mejorObj2VRP);
	
					evMejor =evActual;
					mejorObj2VRP.solcpy(*(pareto.getSolucionVRP(i)));
				}
				else if(evActual<evSeg)
				{
					evSeg =evActual;
					segundoObj2VRP.solcpy(*(pareto.getSolucionVRP(i)));
				}
			}
		}
	
	}
	public PACO(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tablas();
		if(F1MAX>10000000)
		{ //es QAP
			mejorObj1 = new Solucion(prob.getSize());
			segundoObj1 = new Solucion(prob.getSize());
			mejorObj2 = new Solucion(prob.getSize());
			segundoObj2 = new Solucion(prob.getSize());
		}
		else
		{ //no es QAP
			mejorObj1 = new Solucion(prob.getSize()+1);
			segundoObj1 = new Solucion(prob.getSize()+1);
			mejorObj2 = new Solucion(prob.getSize()+1);
			segundoObj2 = new Solucion(prob.getSize()+1);
		}
		mejorObj1VRP = new SolucionVRP(prob.getSize()*2);
		segundoObj1VRP = new SolucionVRP(prob.getSize()*2);
		mejorObj2VRP = new SolucionVRP(prob.getSize()*2);
		segundoObj2VRP = new SolucionVRP(prob.getSize()*2);

	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		double q;
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		//Pseudo-random proportional rule
		q =RandomNumbers.nextNumber()/(double)RAND_MAX;
		if (q<=q0)
			sgteEstado =seleccionar_mayor(estOrigen, visitados);
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		double q;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
		{
			//Pseudo-random proportional rule
			q =RandomNumbers.nextNumber()/(double)RAND_MAX;
			if (q<=q0)
				sgteEstado =seleccionar_mayor(estOrigen, visitados);
			else
				sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
		}
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int tInicio;
		int tFin;
		Solucion sol = new Solucion(prob.getSize()+1);
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion++;
			for (int i =0;i<hormigas;i++)
			{
				peso1 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				peso2 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				construir_solucionTSP((int)(RandomNumbers.nextNumber()%prob.getSize()),this,1,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			evaporar_feromonas();
			encontrar_mejores(1);
			encontrar_mejores(2);
			RefObject<Solucion> TempRefObject = new RefObject<Solucion>(mejorObj1);
			actualizar_feromona_tabla1(TempRefObject);
			mejorObj1 = TempRefObject.argvalue;
			RefObject<Solucion> TempRefObject2 = new RefObject<Solucion>(mejorObj2);
			actualizar_feromona_tabla2(TempRefObject2);
			mejorObj2 = TempRefObject2.argvalue;
			if (pareto.getSize()>1)
			{
				RefObject<Solucion> TempRefObject3 = new RefObject<Solucion>(segundoObj1);
				actualizar_feromona_tabla1(TempRefObject3);
				segundoObj1 = TempRefObject3.argvalue;
				RefObject<Solucion> TempRefObject4 = new RefObject<Solucion>(segundoObj2);
				actualizar_feromona_tabla2(TempRefObject4);
				segundoObj2 = TempRefObject4.argvalue;
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.paco.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int tInicio;
		int tFin;
		Solucion sol = new Solucion(prob.getSize());
	
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion++;
			for (int i =0;i<hormigas;i++)
			{
				peso1 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				peso2 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				construir_solucionQAP((int)(RandomNumbers.nextNumber()%prob.getSize()),this,1,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			evaporar_feromonas();
			encontrar_mejores(1);
			encontrar_mejores(2);
			RefObject<Solucion> TempRefObject = new RefObject<Solucion>(mejorObj1);
			actualizar_feromona_tabla1(TempRefObject);
			mejorObj1 = TempRefObject.argvalue;
			RefObject<Solucion> TempRefObject2 = new RefObject<Solucion>(mejorObj2);
			actualizar_feromona_tabla2(TempRefObject2);
			mejorObj2 = TempRefObject2.argvalue;
			if (pareto.getSize()>1)
			{
				RefObject<Solucion> TempRefObject3 = new RefObject<Solucion>(segundoObj1);
				actualizar_feromona_tabla1(TempRefObject3);
				segundoObj1 = TempRefObject3.argvalue;
				RefObject<Solucion> TempRefObject4 = new RefObject<Solucion>(segundoObj2);
				actualizar_feromona_tabla2(TempRefObject4);
				segundoObj2 = TempRefObject4.argvalue;
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.paco.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int tInicio;
		int tFin;
		SolucionVRP sol = new SolucionVRP(prob.getSize()*2);
	
		tInicio =clock();
		tFin =tInicio;
		while(!condicion_parada(generacion, tInicio, tFin))
		{
			generacion++;
			for (int i =0;i<hormigas;i++)
			{
				peso1 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				peso2 =RandomNumbers.nextNumber()/(double)RAND_MAX;
				construir_solucionVRP(0,this,1,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
			evaporar_feromonas();
			encontrar_mejoresVRP(1);
			encontrar_mejoresVRP(2);
			RefObject<Solucion> TempRefObject = new RefObject<Solucion>(mejorObj1VRP);
			actualizar_feromona_tabla1(TempRefObject);
			mejorObj1VRP = TempRefObject.argvalue;
			RefObject<Solucion> TempRefObject2 = new RefObject<Solucion>(mejorObj2VRP);
			actualizar_feromona_tabla2(TempRefObject2);
			mejorObj2VRP = TempRefObject2.argvalue;
			if (pareto.getSize()>1)
			{
				RefObject<Solucion> TempRefObject3 = new RefObject<Solucion>(segundoObj1VRP);
				actualizar_feromona_tabla1(TempRefObject3);
				segundoObj1VRP = TempRefObject3.argvalue;
				RefObject<Solucion> TempRefObject4 = new RefObject<Solucion>(segundoObj2VRP);
				actualizar_feromona_tabla2(TempRefObject4);
				segundoObj2VRP = TempRefObject4.argvalue;
			}
			tFin =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.paco.pareto");
	}
}

//***********M3AS****************
public class M3AS extends MOACO
{
	private TablaFeromona tabla1; //Matriz de feromonas
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private int hormigaActual; // utilizado para calcular los pesos lambda
	private int noLambdas;
	private void inicializar_tabla()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del MAS
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, double deltaTau, double taumin, double taumax)
	{
		int j;
		int k;
		double tjk;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar la tabla de feromonas con el valor indicado por deltaTau
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			tjk =tabla1.obtenerValor(j,k);
			if (tjk+deltaTau < taumin)
				tabla1.actualizar(j,k,taumin);
			else if (tjk+deltaTau > taumax)
				tabla1.actualizar(j,k,taumax);
			else
				tabla1.actualizar(j,k,tjk+deltaTau);
		}
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		double lambda1;
		double lambda2;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		if (noLambdas != 0)
			lambda1 =lambda2 =1;
		else
		{
			lambda1 =hormigaActual; // lambda para heuristica 1
			lambda2 =hormigas-hormigaActual+1; // lambda para heuristica 2
		}
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica1,lambda1 *beta)*Math.pow(heuristica2,lambda2 *beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private void evaporar_feromonas()
	{
		for(int i =0;i<prob.getSize();i++)
			for(int j =0;j<prob.getSize();j++)
				tabla1.actualizar(i,j,tabla1.obtenerValor(i,j)*(1-rho));
	}
	private double calcular_delta_tao(RefObject<Solucion> sol)
	{
		double delta;
	
		delta =1.0/(prob.funcion_obj_1( sol.argvalue)/F1MAX + prob.funcion_obj_2( sol.argvalue)/F2MAX); //normalizados
		return delta;
	}
	private double calcular_delta_tao(RefObject<SolucionVRP> sol)
	{
		double delta;
	
		delta =1.0/(prob.funcion_obj_1( sol.argvalue)/F1MAX + prob.funcion_obj_2( sol.argvalue)/F2MAX); //normalizados
		return delta;
	}
	public M3AS(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tabla();
		noLambdas =0;
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
	
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		double deltaTao;
		   double taumin;
		   double taumax;
		   double f;
	
		clock_t start;
		clock_t end;
		Solucion sol = new Solucion(prob.getSize()+1);
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionTSP(estOrigen,this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
	
			}
	
			evaporar_feromonas();
			// actualizan la tabla las soluciones no dominadas
			for(int i =0;i<pareto.getSize();i++)
			{
				deltaTao =calcular_delta_tao(pareto.getSolucion(i));
				taumax =deltaTao/(1-rho);
				taumin =deltaTao/(2 *hormigas*(1-rho));
				actualizar_feromonas(pareto.getSolucion(i), pareto.getSolucion(i).getSize(), deltaTao, taumin, taumax);
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.m3as.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
		double deltaTao;
		double taumin;
		double taumax;
		double f;
	
		clock_t start;
		clock_t end;
		Solucion sol = new Solucion(prob.getSize());
		start =clock();
		end =start;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i; // utilizado en seleccionar_sgte_estado
				construir_solucionQAP(estOrigen,this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
	
			evaporar_feromonas();
			// actualizan la tabla los no dominados de la iteracion
			for(int i =0;i<pareto.getSize();i++)
			{
				deltaTao =calcular_delta_tao(pareto.getSolucion(i));
				taumax =deltaTao/(1-rho);
				taumin =deltaTao/(2 *hormigas*(1-rho));
				actualizar_feromonas(pareto.getSolucion(i), pareto.getSolucion(i).getSize(), deltaTao, taumin, taumax);
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.m3as.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		int cantNoDominados;
			double deltaTao;
		double taumin;
		double taumax;
		double f;
	
			clock_t start;
			clock_t end;
		SolucionVRP sol = new SolucionVRP(prob.getSize()*2);
		start =clock();
		end =start;
		noLambdas =1;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			cantNoDominados =0;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i; // utilizado en seleccionar_sgte_estado
				construir_solucionVRP(estOrigen,this,0,sol);
				if (pareto.agregarNoDominado(sol,prob))
					pareto.eliminarDominados(sol,prob);
				//else
				sol.resetear();
			}
	
			evaporar_feromonas();
			// actualizan la tabla los no dominados de la iteracion
			for(int i =0;i<pareto.getSize();i++)
			{
				deltaTao =calcular_delta_tao(pareto.getSolucionVRP(i));
				taumax =deltaTao/(1-rho);
				taumin =deltaTao/(2 *hormigas*(1-rho));
	
	actualizar_feromonas(pareto.getSolucionVRP(i), pareto.getSolucionVRP(i).getSizeActual(), deltaTao, taumin, taumax);
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.m3as.pareto");
	}
}
//************MOACS***********

public class MOACS extends MOACO
{
	private TablaFeromona tabla1; //Matrices de feromonas
	private double alfa; // exponente para las feromonas
	private double beta; // exponente para la visibilidad
	private double rho; // Learning step (coeficiente de evaporacion)
	private double taoInicial; // valor inicial para las tablas de feromonas
	private double tao;
	private double q0;
	private double F1MAX; // utilizados para normalizacion
	private double F2MAX;
	private double NORM1;
	private double NORM2;
	private int hormigaActual; // utilizado para calcular los pesos lambda
	private int noLambdas;
	private void inicializar_tabla()
	{
		tabla1 =new TablaFeromona(prob.getSize());
		tabla1.reiniciar(taoInicial);
	}
	private void online_update(int origen, int destino)
	{
		double tau;
		tau =(1-rho) tabla1.obtenerValor(origen,destino)+rho *taoInicial;
		tabla1.actualizar(origen,destino,tau);
	}
	private void inicializar_parametros(RefObject<String> file)
	{
		FILE f =fopen(file.argvalue,"r");
		String cad = new String(new char[200]);
		int valor;
		fgets(cad,199,f); // leer comentario
		fscanf(f, "%s = %d\n", cad, criterio); // leer criterio de parada
		fscanf(f, "%s = %d\n", cad, valor); // leer limite de parada
		if(criterio==1)
			tiempoTotal=valor;
		else
			maxIteraciones=valor;
		// leer parametros especificos del MOACS
		while(!feof(f))
		{
			fscanf(f,"%s",cad);
			fscanf(f," =");
			if(stricmp(cad,"HORMIGAS")==0)
				fscanf(f, "%d", hormigas);
			else if(stricmp(cad,"ALFA")==0)
				fscanf(f, "%lf", alfa);
			else if(stricmp(cad,"BETA")==0)
				fscanf(f, "%lf", beta);
			else if(stricmp(cad,"RHO")==0)
				fscanf(f, "%lf", rho);
			else if(stricmp(cad,"TAU0")==0)
				fscanf(f, "%lf", taoInicial);
			else if(stricmp(cad,"Q0")==0)
				fscanf(f, "%lf", q0);
			else if(stricmp(cad,"F1MAX")==0)
				fscanf(f, "%lf", F1MAX);
			else if(stricmp(cad,"F2MAX")==0)
				fscanf(f, "%lf", F2MAX);
			else if(stricmp(cad,"D1MAX")==0)
				fscanf(f, "%lf", NORM1);
			else if(stricmp(cad,"D2MAX")==0)
				fscanf(f, "%lf", NORM2);
			else
				fscanf(f,"%s",cad);
		}
	}
	private void actualizar_feromonas(RefObject<Solucion> sol, int solSize, double deltaTau)
	{
		int j;
		int k;
		for(int i =0;i<solSize-1;i++)
		{ // actualizar ambas tablas en una cantidad 1/cantNoDominados
			j =sol.argvalue.get(i);
			k =sol.argvalue.get(i+1);
			tabla1.actualizar(j,k,tabla1.obtenerValor(j,k)+deltaTau);
		}
	}
	private int seleccionar_mayor(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double mayorValor =-1; // inicializar a un valor peque�o
		double heuristica1;
		double heuristica2;
		double valorActual;
		double tau1;
		double tau2;
		double lambda1;
		double lambda2;
		double random;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		if (noLambdas != 0)
			lambda1 =lambda2 =1;
		else
		{
			lambda1 =hormigaActual; // peso de la hormiga actual para el objetivo 1
			lambda2 =hormigas - lambda1 + 1; // peso para el objetivo 2
		}
		for (int i =0;i<prob.getSize();i++)
		{
			if (visitados[i]!=1) // estado i no visitado
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				valorActual =Math.pow(tabla1.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica1,lambda1 *beta)*Math.pow(heuristica2,lambda2 *beta);
				if(valorActual>mayorValor)
				{
					mayorValor =valorActual;
					sgteEstado =i;
				}
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(mayorValor ==-1)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		return sgteEstado;
	}
	private int seleccionar_probabilistico(int estOrigen, int[] visitados)
	{
		int sgteEstado;
		double heuristica1;
		double heuristica2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		double productos =(double)calloc(prob.getSize(),sizeof(double));
		double random;
		double suma =0;
		double acum =0;
		double lambda1;
		double lambda2;
		int[] sinPorcion = new int[prob.getSize()];
		int cantSinPorcion =0;
		if (noLambdas != 0)
			lambda1 =lambda2 =1;
		else
		{
			lambda1 =hormigaActual; // peso de la hormiga actual para el objetivo 1
			lambda2 =hormigas - lambda1 + 1; // peso para el objetivo 2
		}
		random =RandomNumbers.nextNumber()/(double)RAND_MAX; // escoger un valor entre 0 y 1
		// hallar la suma y los productos
		for(int i =0;i<prob.getSize();i++)
		{
			if(!visitados[i])
			{
				heuristica1 =prob.heuristica_1(estOrigen,i)*NORM1; // normalizado
				heuristica2 =prob.heuristica_2(estOrigen,i)*NORM2; // normalizado
				productos[i]=Math.pow(tabla1.obtenerValor(estOrigen,i),alfa)*Math.pow(heuristica1,lambda1 *beta)*Math.pow(heuristica2,lambda2 *beta);
				suma+=productos[i];
				sinPorcion[cantSinPorcion++] =i;
			}
		}
		if(suma ==0)
		{
			random =RandomNumbers.nextNumber()%cantSinPorcion;
			sgteEstado =sinPorcion[(int)random];
		}
		else
		{
			// aplicar ruleta
			for (int i =0;i<prob.getSize();i++)
			{
				if (!visitados[i]) // estado i no visitado
				{
					acum+=productos[i]/suma;
					if(acum>=random)
					{
						sgteEstado =i;
						break;
					}
				}
			}
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(productos);
		return sgteEstado;
	}
	private double calcular_delta_tao(RefObject<Solucion> sol)
	{
		double delta;
	
		delta =1.0/(prob.funcion_obj_1(sol.argvalue)/F1MAX + prob.funcion_obj_2(sol.argvalue)/F2MAX); //normalizados
		return delta;
	}
	private double calcular_delta_tao(RefObject<SolucionVRP> sol)
	{
		double delta;
	
		delta =1.0/(prob.funcion_obj_1(sol.argvalue)/F1MAX + prob.funcion_obj_2(sol.argvalue)/F2MAX); //normalizados
		return delta;
	}
	private double calcular_tao_prima(double avr1, double avr2)
	{
		return (1.0/(avr1 *avr2));
	}
	private double calcular_average(int obj)
	{
		double avr =0;
		for(int i =0;i<pareto.getSize();i++)
		{
			if(obj ==1)
				avr+=prob.funcion_obj_1(*(pareto.getSolucion(i)));
			else
				avr+=prob.funcion_obj_2(*(pareto.getSolucion(i)));
		}
	
		return (avr/(double)pareto.getSize());
	}
	private double calcular_averageVRP(int obj)
	{
		double avr =0;
		for(int i =0;i<pareto.getSize();i++)
		{
			if(obj ==1)
				avr+=prob.funcion_obj_1(*(pareto.getSolucionVRP(i)));
			else
				avr+=prob.funcion_obj_2(*(pareto.getSolucionVRP(i)));
		}
	
		return (avr/(double)pareto.getSize());
	}
	public MOACS(RefObject<Problem> p, RefObject<String> file)
	{
		super(p.argvalue);
		NORM1 =1; //por defecto
		NORM2 =1; //por defecto
		F1MAX =1; //por defecto
		F2MAX =1; //por defecto
		inicializar_parametros(file);
		inicializar_tabla();
		noLambdas =0;
	}
	public int seleccionar_siguiente_estadoTSP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		double q;
		// marcar estados ya visitados, hallar el vecindario
		for (int i =0;sol.argvalue.get(i)!=-1;i++)
			visitados[sol.argvalue.get(i)]=1;
	
		//Pseudo-random proportional rule
		q =RandomNumbers.nextNumber()/(double)RAND_MAX;
		if (q<=q0)
			sgteEstado =seleccionar_mayor(estOrigen, visitados);
		else
			sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
		return sgteEstado;
	}
	public int seleccionar_siguiente_estadoQAP(int estOrigen, RefObject<Solucion> sol)
	{
		int sgte =seleccionar_siguiente_estadoTSP(estOrigen, sol);
		return sgte;
	}
	public int seleccionar_siguiente_estadoVRP(int estOrigen, RefObject<Solucion> sol, double currentTime, double cargaActual)
	{
		int sgteEstado;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		int visitados =(int)calloc(prob.getSize(),sizeof(int));
		SolucionVRP soluc =(SolucionVRP) sol.argvalue;
		VRPTW problem =(VRPTW)prob;
		int totalVisitados =1; // necesariamente se visito el deposito 1 vez
		double distancia;
		double q;
		// hallar el vecindario
		for(int i =0;i<soluc.getSizeActual();i++)
		{
			visitados[soluc.get(i)]=1;
			if(soluc.get(i)!=0) // estado 0 ya se contabilizo
				totalVisitados++;
		}
		for (int i =0;i<problem.getSize();i++)
		{
			if(!visitados[i])
			{ // controlar si se cumplira la ventana, la capacidad
				// y si se podra volver a tiempo al deposito si fuera necesario
				distancia =max(currentTime+problem.getDistancia(estOrigen,i),problem.getTimeStart(i));
				if(cargaActual+problem.getDemanda(i)>problem.getCapacity() || currentTime+problem.getDistancia(estOrigen,i)>problem.getTimeEnd(i) || distancia+problem.getDistancia(i,0)>problem.getTimeEnd(0))
				{
					visitados[i]=1; // marcar como no vecino
					totalVisitados++;
				}
			}
		}
	
		if(totalVisitados>=problem.getSize())
			sgteEstado =0; // ir al deposito
		else
		{
			//Pseudo-random proportional rule
			q =RandomNumbers.nextNumber()/(double)RAND_MAX;
			if (q<=q0)
				sgteEstado =seleccionar_mayor(estOrigen, visitados);
			else
				sgteEstado =seleccionar_probabilistico(estOrigen, visitados);
		}
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(visitados);
	
		return sgteEstado;
	}
	public void ejecutarTSP()
	{
		int generacion =0;
		int estOrigen;
		double deltaTao;
		double taoPrima;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize()+1);
		start =clock();
		end =start;
		tao =-1;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionTSP(estOrigen,this,1,sols[i]);
			}
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
				sols[i].resetear();
			}
	
			taoPrima =calcular_tao_prima(calcular_average(1), calcular_average(2));
	
			if(taoPrima > tao)
			{
				// reiniciar tabla de feromonas
				tao =taoPrima;
				tabla1.reiniciar(tao);
			}
			else
			{
				// actualizan la tabla las soluciones del frente Pareto
				for(int i =0;i<pareto.getSize();i++)
				{
					deltaTao =calcular_delta_tao(*(pareto.getSolucion(i)));
					actualizar_feromonas(*(pareto.getSolucion(i)), pareto.getSolucion(i).getSize(), deltaTao);
				}
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/tsp.moacs.pareto");
	}
	public void ejecutarQAP()
	{
		int generacion =0;
		int estOrigen;
		double deltaTao;
		double taoPrima;
		clock_t start;
		clock_t end;
		Solucion sols =new Solucion[hormigas](prob.getSize());
		start =clock();
		end =start;
		tao =-1;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =RandomNumbers.nextNumber()%(prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionQAP(estOrigen,this,1,sols[i]);
			}
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
				sols[i].resetear();
			}
	
			taoPrima =calcular_tao_prima(calcular_average(1), calcular_average(2));
	
			if(taoPrima > tao)
			{
				// reiniciar tabla de feromonas
				tao =taoPrima;
				tabla1.reiniciar(tao);
			}
			else
			{
				// actualizan la tabla las soluciones del frente Pareto
				for(int i =0;i<pareto.getSize();i++)
				{
					deltaTao =calcular_delta_tao(*(pareto.getSolucion(i)));
					actualizar_feromonas(*(pareto.getSolucion(i)), pareto.getSolucion(i).getSize(), deltaTao);
				}
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moacs.pareto");
	}
	public void ejecutarVRP()
	{
		int generacion =0;
		int estOrigen;
		double deltaTao;
		double taoPrima;
		clock_t start;
		clock_t end;
		SolucionVRP sols =new SolucionVRP[hormigas](prob.getSize()*2);
		start =clock();
		end =start;
		tao =-1;
		noLambdas =1;
		while(!condicion_parada(generacion,start,end))
		{
			generacion++;
			for(int i =0;i<hormigas;i++)
			{
				estOrigen =0; // colocar a la hormiga en un estado inicial aleatorio
				hormigaActual =i+1; // utilizado en seleccionar_sgte_estado
				construir_solucionVRP(estOrigen,this,1,sols[i]);
			}
	
			for(int i =0;i<hormigas;i++)
			{
				if (pareto.agregarNoDominado(sols[i],prob))
				{
					pareto.eliminarDominados(sols[i],prob);
				}
				//else
				sols[i].resetear();
			}
	
			taoPrima =calcular_tao_prima(calcular_averageVRP(1), calcular_averageVRP(2));
	
			if(taoPrima > tao)
			{
				// reiniciar tabla de feromonas
				tao =taoPrima;
				tabla1.reiniciar(tao);
			}
			else
			{
				// actualizan la tabla las soluciones del frente Pareto
				for(int i =0;i<pareto.getSize();i++)
				{
					deltaTao =calcular_delta_tao(*(pareto.getSolucionVRP(i)));
	
	actualizar_feromonas(*(pareto.getSolucionVRP(i)), pareto.getSolucionVRP(i).getSizeActual(), deltaTao);
				}
			}
			end =clock();
		}
	//	pareto->listarSoluciones(prob,"/home/fuentes/qap.moacs.pareto");
	}
}

//----------------------------------------------------------------------------------------
//	Copyright � 2006 - 2008 Tangible Software Solutions Inc.
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
//	Copyright � 2006 - 2008 Tangible Software Solutions Inc.
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