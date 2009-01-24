#include <stdio.h>
void debug(char s[])
{
	FILE* f=fopen("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\debug.txt","a");
	fprintf(f,"%s\n",s);
	fclose(f);
}
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <string.h>
#include <ctype.h>
#include "clases.cc"
#include "Problema.cc"
#include "clases2.cc"
#define max(i,j) (i>=j)?i:j
#include "construir_solucion.cc"
#define SLAVENAME	"slave"
#define CBIANT		1
#define CBIMC		2
#define CCOMP		3
#define CMAS		4
#define CM3AS		5
#define CMOACS		6
#define CMOAQ		7
#define CMOA		8
#define CPACO		9
#define KROAB		10
#define KROAC		11
#define QAP1		12
#define QAP2		13
#define C101		14
#define RC101		15
#define NTASKS		2
#define MSGINI		20
#define MSGDATA		30
#define MSGCONT		40
#define TERMINAR	50

char outFile[255];

void set_outfile(char out[],char prob[],char inst[],char alg[])
{
	strcpy(out,prob);
	strcat(out,".");
	strcat(out,inst);
	strcat(out,alg);
}

/* Comparador de cadenas no sensible a mayúsculas ni minúsculas,
   retorna: -1 si cad es mayor que cad2
			 0 si son iguales
			 1 si cad2 es mayor que cad */
int strlcmp(char cad[],char cad2[])
{
	int comp;
	int i;
	char c2[strlen(cad2)];
	strcpy(c2,cad2);
	
	for(i=0;i<strlen(cad);i++)
		cad[i]=tolower(cad[i]);
	for(i=0;i<strlen(c2);i++)
		c2[i]=tolower(c2[i]);
	comp=strcmp(cad,c2);
	
	return comp;
}
