//#include "pvm3.h"
#define PARETO 50

/************** Clase Conjunto Pareto ******************/
class ConjuntoPareto
{
	int cantSoluciones; //cantidad actual de soluciones
	int tamano;			//tamano del array de soluciones
protected:
	// array que contiene las soluciones del frente pareto
	Solucion ** lista;
	SolucionVRP ** listaVRP;
public:
	ConjuntoPareto(int numSoluciones);
	int agregarNoDominado(Solucion &sol, Problem *prob);
	void eliminarDominados(Solucion &sol, Problem *prob);
	int agregarNoDominado(SolucionVRP &sol, Problem *prob);
	void eliminarDominados(SolucionVRP &sol, Problem *prob);
	void listarSoluciones(Problem *p,char file[]);
	void listarSolucionesVRP(Problem *prob,char file[]);
	int getSize(){return cantSoluciones;};
	Solucion * getSolucion(int i){return lista[i];};
	SolucionVRP * getSolucionVRP(int i){return listaVRP[i];};
	void enviar(int tid){};
	void recibir(int tid){};
};

ConjuntoPareto::ConjuntoPareto(int numSoluciones)
{
	cantSoluciones=0;
	tamano=numSoluciones;
	lista = (Solucion **)malloc(sizeof(Solucion *)*numSoluciones);
	listaVRP = (SolucionVRP **)malloc(sizeof(SolucionVRP *)*numSoluciones);
	for(int i=0;i<numSoluciones;i++)
	{
		lista[i]=NULL;
		listaVRP[i]=NULL;
	}
}

int ConjuntoPareto::agregarNoDominado(Solucion &sol, Problem *prob)
{
	double solfuncion1=prob->funcion_obj_1(sol);	// Evaluacion de la solucion respecto
	double solfuncion2=prob->funcion_obj_2(sol);	// a las funciones obetivo del problema
	double solauxfuncion1, solauxfuncion2; // Evaluacion de los objetivos de alguna solucion del conjunto

	for (int i=0; i<cantSoluciones; i++)
	{
		solauxfuncion1=prob->funcion_obj_1(*lista[i]);
		solauxfuncion2=prob->funcion_obj_2(*lista[i]);
		// ambas funciones objetivo siempre se minimizan
		if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2)
			return 0; //sol es dominada por una solucion del conjunto
	}
	//Aumentar el tamaño del conjunto Pareto si éste está lleno
	if (cantSoluciones==tamano)
	{
		Solucion ** listaAux=lista;
		tamano=tamano*2;
		lista=(Solucion **)malloc(tamano*sizeof(Solucion *));
		for (int i=0;i<cantSoluciones;i++)
		{
			lista[i]=listaAux[i];
		}
		free(listaAux); //liberar el array anterior
	}
	if(lista[cantSoluciones]==NULL)
		lista[cantSoluciones]=new Solucion(sol.getSize());
	lista[cantSoluciones]->solcpy(sol);
	cantSoluciones++;
	return 1;
}

void ConjuntoPareto::eliminarDominados(Solucion &sol, Problem *prob)
{
	double solfuncion1=prob->funcion_obj_1(sol);	// Evaluacion de la solucion respecto
	double solfuncion2=prob->funcion_obj_2(sol);	// a las funciones obetivo del problema
	double solauxfuncion1, solauxfuncion2; // Evaluacion de los objetivos de alguna solucion del conjunto
	//Solucion *elim;

	for (int i=0; i<cantSoluciones; i++)
	{
		solauxfuncion1=prob->funcion_obj_1(*lista[i]);
		solauxfuncion2=prob->funcion_obj_2(*lista[i]);
		// ambas funciones objetivo siempre se minimizan
		if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2))
		{
			//elim=lista[i];
			lista[i]->destruir();
			lista[i]=lista[cantSoluciones-1];
			lista[cantSoluciones-1]=NULL; //liberar puntero
			cantSoluciones--;
			i--;
			//elim->destruir();
		}
	}
}

int ConjuntoPareto::agregarNoDominado(SolucionVRP &sol, Problem *prob)
{
	double solfuncion1=prob->funcion_obj_1(sol);	// Evaluacion de la solucion respecto
	double solfuncion2=prob->funcion_obj_2(sol);	// a las funciones obetivo del problema
	double solauxfuncion1, solauxfuncion2; // Evaluacion de los objetivos de alguna solucion del conjunto

	for (int i=0; i<cantSoluciones; i++)
	{
		solauxfuncion1=prob->funcion_obj_1(*listaVRP[i]);
		solauxfuncion2=prob->funcion_obj_2(*listaVRP[i]);
		// ambas funciones objetivo siempre se minimizan
		if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2)
			return 0; //sol es dominada por una solucion del conjunto
	}
	//Aumentar el tamaño del conjunto Pareto si éste está lleno
	if (cantSoluciones==tamano)
	{
		SolucionVRP ** listaAux=listaVRP;
		tamano=tamano*2;
		listaVRP=(SolucionVRP **)malloc(tamano*sizeof(SolucionVRP *));
		for (int i=0;i<cantSoluciones;i++)
		{
			listaVRP[i]=listaAux[i];
		}
		free(listaAux);
	}
	if(listaVRP[cantSoluciones]==NULL)
		listaVRP[cantSoluciones]=new SolucionVRP(sol.getSize());
	listaVRP[cantSoluciones]->solcpy(sol);
	cantSoluciones++;
	return 1;
}

void ConjuntoPareto::eliminarDominados(SolucionVRP &sol, Problem *prob)
{
	double solfuncion1=prob->funcion_obj_1(sol);	// Evaluacion de la solucion respecto
	double solfuncion2=prob->funcion_obj_2(sol);	// a las funciones obetivo del problema
	double solauxfuncion1, solauxfuncion2; // Evaluacion de los objetivos de alguna solucion del conjunto
	//SolucionVRP *elim;

	for (int i=0; i<cantSoluciones; i++)
	{
		solauxfuncion1=prob->funcion_obj_1(*listaVRP[i]);
		solauxfuncion2=prob->funcion_obj_2(*listaVRP[i]);
		// ambas funciones objetivo siempre se minimizan
		if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2))
		{
			//elim=listaVRP[i];
			listaVRP[i]->destruir();
			listaVRP[i]=listaVRP[cantSoluciones-1];
			listaVRP[cantSoluciones-1]=NULL; //liberar puntero
			cantSoluciones--;
			i--;
			//elim->destruir();
		}
	}
}

void ConjuntoPareto::listarSoluciones(Problem *prob,char file[])
{
	FILE *f=fopen(file,"w");
	fprintf(f,"%d\n",cantSoluciones);
	for (int i=0;i<cantSoluciones;i++)
	{
		fprintf(f,"%lf\t%lf\n",prob->funcion_obj_1(*lista[i]),prob->funcion_obj_2(*lista[i]));
//		lista[i]->imprimir(f);
	}
	fclose(f);
}

void ConjuntoPareto::listarSolucionesVRP(Problem *prob,char file[])
{
	FILE *f=fopen(file,"w");
	fprintf(f,"%d\n",cantSoluciones);
	for (int i=0;i<cantSoluciones;i++)
	{
		fprintf(f,"%lf\t%lf\n",prob->funcion_obj_1(*listaVRP[i]),prob->funcion_obj_2(*listaVRP[i]));
		//listaVRP[i]->imprimir(f);
	}
	fclose(f);
}

/*void ConjuntoPareto::enviar(int tid)
{
	int msgtype=PARETO;
	int aux;
	pvm_initsend(PvmDataDefault);
	pvm_pkint(&cantSoluciones,1,1);
	aux=lista[0]->getSize();
	pvm_pkint(&aux,1,1);
	for (int i=0; i<cantSoluciones; i++)
	{
		for (int j=0;j<lista[i]->getSize();j++)
		{
			aux=lista[i]->get(j);
			pvm_pkint(&aux,1,1);
		}
	}
	pvm_send(tid,msgtype);
}

void ConjuntoPareto::recibir(int tid)
{
	int msgtype=PARETO;
	int tamsol=0,nodo=0;
	pvm_recv(tid,msgtype);
	pvm_upkint(&cantSoluciones,1,1);
	pvm_upkint(&tamsol,1,1);
	for (int i=0; i<cantSoluciones; i++)
	{
		if(lista[i]==NULL)
			lista[i]=new Solucion(tamsol);
		for (int j=0;j<tamsol;j++)
		{
			pvm_upkint(&nodo,1,1);
			lista[i]->set(j,nodo);
		}
	}
}*/

class MOACO
{
protected:
	Problem * prob;
	int criterio,tiempoTotal,maxIteraciones,hormigas;
public:
	ConjuntoPareto * pareto;
	MOACO(Problem *p){
		prob=p;
		pareto=new ConjuntoPareto(500);
		srand(time(NULL));
	};
	virtual int seleccionar_siguiente_estadoTSP(int estOrigen, Solucion &sol)=0;
	virtual int seleccionar_siguiente_estadoQAP(int estOrigen, Solucion &sol)=0;
	virtual int seleccionar_siguiente_estadoVRP(int estOrigen, Solucion &sol, double currentTime, double cargaActual)=0;
	virtual void ejecutarTSP(void)=0;
	virtual void ejecutarQAP(void)=0;
	virtual void ejecutarVRP(void)=0;
	int condicion_parada(int generacion,clock_t start,clock_t end)
	{
		if(criterio==1)
		{
			if((end - start) / CLOCKS_PER_SEC < tiempoTotal)
				return 0;
		}
		else if(generacion<maxIteraciones)
			return 0;
		
		return 1;
	};
	Problem * getProblema(void){ return prob;};
	virtual void online_update(int orig,int dest){};
};
