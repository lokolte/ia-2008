/************ Clase Problema *************/
class Problem
{
protected:
	double **matrizAdy;
	int size;
	virtual void cargar_estado(char * file) = 0;
public:
	Problem(char * file);
	virtual double funcion_obj_1(Solucion &sol) = 0;
	virtual double funcion_obj_2(Solucion &sol) = 0;
	virtual double heuristica_1(int estOrigen,int estDest) = 0;
	virtual double heuristica_2(int estOrigen,int estDest) = 0;
	int getSize(void){ return size; };
	double getDistancia(int i,int j){
		return matrizAdy[i][j];
	};
	void destruir(void){
		for(int i=0;i<size;i++)
			free(matrizAdy[i]);
		free(matrizAdy);
	};
};

Problem::Problem(char * file){
	int i;
	FILE *f=fopen(file,"r");
	// obtener el tamaño del problema
	fscanf(f,"%d\n",&size);
	fclose(f);
	// inicializar matriz de adyacencia
	matrizAdy = (double **)malloc(sizeof(double *)*size);
	for(i=0;i<size;i++)
		matrizAdy[i]=(double *)malloc(sizeof(double)*size);
}

/************ Clase TSP **************/
class TSP:public Problem
{
	double **matrizAdy2;
	void cargar_estado(char * file);
public:
	TSP(char * file);
	double funcion_obj_1(Solucion &sol);
	double funcion_obj_2(Solucion &sol);
	double heuristica_1(int estOrigen,int estDest){
		return 1.0/matrizAdy[estOrigen][estDest];
	};
	double heuristica_2(int estOrigen,int estDest){
		return 1.0/matrizAdy2[estOrigen][estDest];
	};
	void imprimir_matrices(void);
	void destruir(void){
		for(int i=0;i<size;i++)
			free(matrizAdy2[i]);
		free(matrizAdy2);
		
		((Problem *)this)->destruir();
	};
};

TSP::TSP(char * file):Problem(file)
{
	int i;
	matrizAdy2 = (double **)malloc(sizeof(double *)*size);
	for(i=0;i<size;i++)
		matrizAdy2[i]=(double *)malloc(sizeof(double)*size);
	
	cargar_estado(file);
}

void TSP::cargar_estado(char * file)
{
	// El archivo file posee las dos matrices de adyacencia separadas por '\n'
	// en la primera linea posee el tamaño del problema
	FILE *f;
	int i,j;
	f=fopen(file,"r");
	fscanf(f,"%d\n",&i);
	fscanf(f,"%d\n",&i);
	for(i=0;i<size;i++)
		for(j=0;j<size;j++)
			fscanf(f,"%lf",&matrizAdy[i][j]);
	for(i=0;i<size;i++)
		for(j=0;j<size;j++)
			fscanf(f,"%lf",&matrizAdy2[i][j]);
	fclose(f);
}

double TSP::funcion_obj_1(Solucion &sol)
{
	int i;
	double suma=0;
	for(i=0;i<sol.getSize()-1;i++)
		suma+=matrizAdy[sol.get(i)][sol.get(i+1)];
	suma+=matrizAdy[sol.get(sol.getSize()-1)][0];
	return suma;
}

double TSP::funcion_obj_2(Solucion &sol)
{
	int i;
	double suma=0;
	for(i=0;i<sol.getSize()-1;i++)
		suma+=matrizAdy2[sol.get(i)][sol.get(i+1)];
	suma+=matrizAdy2[sol.get(sol.getSize()-1)][0];
	return suma;
}

void TSP::imprimir_matrices(void)
{
	int i,j;
	printf("Matriz Adyacencia 1:\n");
	for(i=0;i<size;i++)
	{
		for(j=0;j<size;j++)
			printf("%lf ",matrizAdy[i][j]);
		printf("\n");
	}
	printf("Matriz Adyacencia 2:\n");
	for(i=0;i<size;i++)
	{
		for(j=0;j<size;j++)
			printf("%lf ",matrizAdy2[i][j]);
		printf("\n");
	}
}

/************ Clase QAP **************/
class QAP:public Problem
{
	double **matrizFlujo1;
	double **matrizFlujo2;
	void cargar_estado(char * file);
public:
	QAP(char * file);
	double funcion_obj_1(Solucion &sol);
	double funcion_obj_2(Solucion &sol);
	double heuristica_1(int estOrigen,int estDest){return 1;}; // No se utilizan
	double heuristica_2(int estOrigen,int estDest){return 1;}; // heurísticas
	void imprimir_matrices(void);
	void destruir(void)
	{
		for(int i=0;i<size;i++)
			free(matrizFlujo1[i]);
		free(matrizFlujo1);
		for(int i=0;i<size;i++)
			free(matrizFlujo2[i]);
		free(matrizFlujo2);
		
		((Problem *)this)->destruir();
	};
};

QAP::QAP(char * file):Problem(file)
{
	int i;
	matrizFlujo1 = (double **)malloc(sizeof(double *)*size);
	for(i=0;i<size;i++)
		matrizFlujo1[i]=(double *)malloc(sizeof(double)*size);
	matrizFlujo2 = (double **)malloc(sizeof(double *)*size);
	for(i=0;i<size;i++)
		matrizFlujo2[i]=(double *)malloc(sizeof(double)*size);
	cargar_estado(file);
}

void QAP::cargar_estado(char * file)
{
	// El archivo file posee las tres matrices: adyacencia,flujo1,flujo2 separadas por '\n'
	// en la primera linea posee informacion adicional
	FILE *f;
	int i,j;
	f=fopen(file,"r");
	fscanf(f,"%d\n",&i);
	for(i=0;i<this->size;i++)
		for(j=0;j<this->size;j++)
			fscanf(f,"%lf",&matrizAdy[i][j]);
	for(i=0;i<size;i++)
		for(j=0;j<size;j++)
			fscanf(f,"%lf",&matrizFlujo1[i][j]);
	for(i=0;i<this->size;i++)
		for(j=0;j<this->size;j++)
			fscanf(f,"%lf",&matrizFlujo2[i][j]);
	fclose(f);
}

double QAP::funcion_obj_1(Solucion &sol)
{
	int i,j;
	double suma=0;
	for(i=0;i<sol.getSize();i++)
		for(j=0;j<sol.getSize();j++)
			suma+=matrizAdy[i][j]*matrizFlujo1[sol.get(i)][sol.get(j)];
			
	return suma;
}

double QAP::funcion_obj_2(Solucion &sol)
{
	int i,j;
	double suma=0;
	for(i=0;i<sol.getSize();i++)
		for(j=0;j<sol.getSize();j++)
			suma+=matrizAdy[i][j]*matrizFlujo2[sol.get(i)][sol.get(j)];
			
	return suma;
}

void QAP::imprimir_matrices(void)
{
	int i,j;
	
	printf("Matriz Adyacencia:\n");
	for(i=0;i<size;i++)
	{
		for(j=0;j<size;j++)
			printf("%lf ",matrizAdy[i][j]);
		printf("\n");
	}
	printf("Matriz flujo 1:\n");
	for(i=0;i<size;i++)
	{
		for(j=0;j<size;j++)
			printf("%lf ",matrizFlujo1[i][j]);
		printf("\n");
	}
	printf("Matriz flujo 2:\n");
	for(i=0;i<size;i++)
	{
		for(j=0;j<size;j++)
			printf("%lf ",matrizFlujo2[i][j]);
		printf("\n");
	}
}

/************ Clase VRPTW **************/
class VRPTW:public Problem
{
	int capacity;
	customerVRP *customers;
	void cargar_estado(char * file);
	void generar_matriz_ady(void);
public:
	VRPTW(char * file);
	double funcion_obj_1(Solucion &sol){
		return ((SolucionVRP *)&sol)->getCamiones(); // devuelve la cantidad camiones
	};
	double funcion_obj_2(Solucion &sol);
	double heuristica_1(int estOrigen,int estDest){return 1;};
	double heuristica_2(int estOrigen,int estDest){
		return 1.0/matrizAdy[estOrigen][estDest];
	};
	int getCapacity(void){return capacity;};
	double getDemanda(int customer){
		return customers[customer].getDemanda();
	};
	double getTimeStart(int customer){
		return customers[customer].getTimeStart();
	};
	double getTimeEnd(int customer){
		return customers[customer].getTimeEnd();
	};
	double getServiceTime(int customer){
		return customers[customer].getServiceTime();
	};
	void imprimir(void);
};

VRPTW::VRPTW(char * file):Problem(file)
{
	customers=new customerVRP[size];
	cargar_estado(file);
}

void VRPTW::cargar_estado(char * file)
{
	// El archivo file posee: la cantidad de customers, la capacidad de los camiones
	// y los datos de cada customer: coordenadas, demanda, ventana y tiempo de servicio
	FILE *f;
	double x,y,dem,begin,end,servTime;
	int i;
	
	f=fopen(file,"r");
	fscanf(f,"%d\n",&i);
	fscanf(f,"%d\n",&capacity);
	for(i=0;i<size;i++)
	{
		fscanf(f,"%lf",&x);
		fscanf(f,"%lf",&x);
		fscanf(f,"%lf",&y);
		fscanf(f,"%lf",&dem);
		fscanf(f,"%lf",&begin);
		fscanf(f,"%lf",&end);
		fscanf(f,"%lf",&servTime);
		customers[i].setCoord(x,y);
		customers[i].setDemanda(dem);
		customers[i].setWindow(begin,end);
		customers[i].setServiceTime(servTime);
	}
	fclose(f);
	generar_matriz_ady();
}

void VRPTW::generar_matriz_ady(void)
{
	// a partir de las coordenadas de los customers se genera la matriz simetrica
	// de adyacencia con las dinstancias euclideas entre cada par de customers
	double aux;
	
	for(int i=0;i<size;i++)
	{
		for(int j=i+1;j<size;j++)
		{
			aux=pow(customers[i].getCoordX()-customers[j].getCoordX(),2);
			aux+=pow(customers[i].getCoordY()-customers[j].getCoordY(),2);
			matrizAdy[i][j]=sqrt(aux);
			matrizAdy[j][i]=matrizAdy[i][j];
		}
		matrizAdy[i][i]=0;
	}
}

double VRPTW::funcion_obj_2(Solucion &sol)
{
	int i;
	double suma=0;
	SolucionVRP *s=(SolucionVRP *)&sol;
	for(i=0;i<s->getSizeActual()-1;i++)
		suma+=matrizAdy[s->get(i)][s->get(i+1)];
	suma+=matrizAdy[s->get(s->getSizeActual()-1)][0];

	return suma; // devolver el "Total Travel Distance"
}

void VRPTW::imprimir(void)
{
	printf("Matriz Adyacencia:\n");
	for(int i=0;i<1;i++)
	{
		for(int j=0;j<size;j++)
			printf("%lf ",matrizAdy[i][j]);
		printf("\n");
	}
	printf("Size: %d\n",size);
	printf("capacity: %d\n",capacity);
	for(int i=0;i<20;i++)
	{
		printf("Customer %d\n",i);
		printf("Demanda: %lf\n",customers[i].getDemanda());
		printf("Service Time: %lf\n",customers[i].getServiceTime());
		printf("Begin: %lf\n",customers[i].getTimeStart());
		printf("End: %lf\n",customers[i].getTimeEnd());
	}
}
/*
int main()
{
	Problem *p=new VRPTW("c101.txt");
	SolucionVRP sol(p->getSize()+100);
	
	for(int i=0;i<p->getSize();i++)
		sol.add(i);
	double f1=p->funcion_obj_1(sol);
	double f2=p->funcion_obj_2(sol);
	printf("%f*%f\n",f1,f2);
	((VRPTW *)p)->imprimir();
}*/

