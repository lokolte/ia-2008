#include "varios.h"
#include "algoritmos.h"

int main(int argc,char *args[])
{
    int problema;					// indica el codigo del problema 
    char parametros[100];			// indica el archivo de parametros a utilizar 
    int algoritmo;					// algoritmo a ejecutar
    MOACO *alg;						// instancia del algoritmo
    Problem *prob;					// instancia del problema a resolver
    char cad[300],pr[60];
    char instancia[100];
    
    /* leer parametros de usuario */
    if(argc>=4)
    {
		if(strlcmp(args[1],"kroab")==0)
		{
			problema=KROAB;
			prob=new TSP("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias\\KROAB100.TSP.TXT");
			strcpy(parametros,"E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\parametros_tsp.txt");
			strcpy(pr,"KROAB100.TXT.tsp");
		}
		else if(strlcmp(args[1],"kroac")==0)
		{
			problema=KROAC;
			prob=new TSP("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\kroac100.tsp.txt");
			strcpy(parametros,"E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\parametros_tsp.txt");
			strcpy(pr,"kroac100.txt.tsp");
		}
		else if(strlcmp(args[1],"qap1")==0)
		{
			problema=QAP1;
			prob=new QAP("/home/fuentes/qapUni.75.0.1.qap");
			strcpy(parametros,"/home/fuentes/parametros_qap.txt");
			strcpy(pr,"qapUni.75.0.1.qap");
		}
		else if(strlcmp(args[1],"qap2")==0)
		{
			problema=QAP2;
			prob=new QAP("/home/fuentes/qapUni.75.p75.1.qap");
			strcpy(parametros,"/home/fuentes/parametros_qap.txt");
			strcpy(pr,"qapUni.75.p75.1.qap");
		}
		else if(strlcmp(args[1],"c101")==0)
		{
			problema=C101;
			prob=new VRPTW("/home/fuentes/c101.txt");
			strcpy(parametros,"/home/fuentes/parametros_vrp.txt");
			strcpy(pr,"c101.txt");
		}
		else if(strlcmp(args[1],"rc101")==0)
		{
			problema=RC101;
			prob=new VRPTW("/home/fuentes/rc101.txt");
			strcpy(parametros,"/home/fuentes/parametros_vrp.txt");
			strcpy(pr,"rc101.txt");
		}
		else
		{
			printf("\nError, problema no valido. Opciones: kroab,kroac,qap1,qap2,c101,rc101.\n");
			exit(1);
		}

		if(strlcmp(args[2],"biant")==0)
		{
			printf("biant");

			alg=new BicriterionAnt(prob,parametros);
		}
		else if(strlcmp(args[2],"bimc")==0)
		{
printf("binct");
			alg=new BicriterionMC(prob,parametros);
		}
		else if(strlcmp(args[2],"moacs")==0)
		{
printf("moacs");
			alg=new MOACS(prob,parametros);
		}
		else if(strlcmp(args[2],"moaq")==0)
		{
printf("moaq");
			alg=new MOAQ(prob,parametros);
		}
		else if(strlcmp(args[2],"m3as")==0)
		{
printf("m3as");
			alg=new M3AS(prob,parametros);
		}
		else if(strlcmp(args[2],"moa")==0)
		{
printf("moa");
			alg=new MOA(prob,parametros);
		}
		else if(strlcmp(args[2],"comp")==0)
		{
printf("comp");
			alg=new CompeteAnts(prob,parametros);
		}
		else if(strlcmp(args[2],"paco")==0)
		{
printf("paco");
			alg=new PACO(prob,parametros);
		}
		else if(strlcmp(args[2],"mas")==0)
		{
printf("mas");
			alg=new MAS(prob,parametros);
		}
		else
		{
			printf("\nError, algoritmo no valido. Opciones: biant,bimc,comp,moacs,m3as,moa,moaq,paco.\n");
			exit(1);
		}
		strcpy(instancia,args[3]);
    }
    else
    {
	printf("\nError, faltan argumentos...problema, algoritmo, instancia\n");
	exit(1);
    }
    
	// Ejecutar el algoritmo indicado
	if (problema==KROAB || problema==KROAC)
		alg->ejecutarTSP();
	else if(problema==QAP1 || problema==QAP2)
		alg->ejecutarQAP();
	else if(problema==C101 || problema==RC101)
		alg->ejecutarVRP();
printf("termino solucion");

	sprintf(cad,"C:\\cygwin\\home\\Christian Gomez\\moacos\\%s.%s.%s.pareto",pr,args[2],instancia);
	if(problema==C101 || problema==RC101)
		alg->pareto->listarSolucionesVRP(prob,cad);
	else
		alg->pareto->listarSoluciones(prob,cad);
		
	return 0;
}
