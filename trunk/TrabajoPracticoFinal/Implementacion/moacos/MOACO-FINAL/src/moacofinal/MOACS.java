/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

/**
 *
 * @author jmferreira
 */
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
