/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

public class GlobalMembersSlave1
{

	static int Main(int argc, RefObject<String[]> args)
	{
		int problema; // indica el codigo del problema
		String parametros = new String(new char[100]); // indica el archivo de parametros a utilizar
		int algoritmo; // algoritmo a ejecutar
		MOACO alg; // instancia del algoritmo
		Problem prob; // instancia del problema a resolver
		String cad = new String(new char[300]);
		String pr = new String(new char[60]);
		String instancia = new String(new char[100]);

		// leer parametros de usuario 
		if(argc>=4)
		{
			if(stricmp(args.argvalue.charAt(1),"kroab")==0)
			{
				problema =DefineConstantsSlave1.KROAB;
				prob =new TSP("/home/fuentes/KROAB100.TXT.tsp");
				parametros = "/home/fuentes/parametros_tsp.txt";
				pr = "KROAB100.TXT.tsp";
			}
			else if(stricmp(args.argvalue.charAt(1),"kroac")==0)
			{
				problema =DefineConstantsSlave1.KROAC;
				prob =new TSP("/home/fuentes/kroac100.txt.tsp");
				parametros = "/home/fuentes/parametros_tsp.txt";
				pr = "kroac100.txt.tsp";
			}
			else if(stricmp(args.argvalue.charAt(1),"qap1")==0)
			{
				problema =DefineConstantsSlave1.QAP1;
				prob =new QAP("/home/fuentes/qapUni.75.0.1.qap");
				parametros = "/home/fuentes/parametros_qap.txt";
				pr = "qapUni.75.0.1.qap";
			}
			else if(stricmp(args.argvalue.charAt(1),"qap2")==0)
			{
				problema =DefineConstantsSlave1.QAP2;
				prob =new QAP("/home/fuentes/qapUni.75.p75.1.qap");
				parametros = "/home/fuentes/parametros_qap.txt";
				pr = "qapUni.75.p75.1.qap";
			}
			else if(stricmp(args.argvalue.charAt(1),"c101")==0)
			{
				problema =DefineConstantsSlave1.C101;
				prob =new VRPTW("/home/fuentes/c101.txt");
				parametros = "/home/fuentes/parametros_vrp.txt";
				pr = "c101.txt";
			}
			else if(stricmp(args.argvalue.charAt(1),"rc101")==0)
			{
				problema =DefineConstantsSlave1.RC101;
				prob =new VRPTW("/home/fuentes/rc101.txt");
				parametros = "/home/fuentes/parametros_vrp.txt";
				pr = "rc101.txt";
			}
			else
			{
				System.out.print("\nError, problema no valido. Opciones: kroab,kroac,qap1,qap2,c101,rc101.\n");
				exit(1);
			}
			if(stricmp(args.argvalue.charAt(2),"biant")==0)
			{
				alg =new BicriterionAnt(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"bimc")==0)
			{
				alg =new BicriterionMC(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"moacs")==0)
			{
				alg =new MOACS(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"moaq")==0)
			{
				alg =new MOAQ(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"m3as")==0)
			{
				alg =new M3AS(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"moa")==0)
			{
				alg =new MOA(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"comp")==0)
			{
				alg =new CompeteAnts(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"paco")==0)
			{
				alg =new PACO(prob,parametros);
			}
			else if(stricmp(args.argvalue.charAt(2),"mas")==0)
			{
				alg =new MAS(prob,parametros);
			}
			else
			{
				System.out.print("\nError, algoritmo no valido. Opciones: biant,bimc,comp,moacs,m3as,moa,moaq,paco.\n");
				exit(1);
			}
			instancia = args.argvalue.charAt(3);
		}
		else
		{
		System.out.print("\nError, faltan argumentos...problema, algoritmo, instancia\n");
		exit(1);
		}

		// Ejecutar el algoritmo indicado
		if (problema ==DefineConstantsSlave1.KROAB || problema ==DefineConstantsSlave1.KROAC)
			alg.ejecutarTSP();
		else if(problema ==DefineConstantsSlave1.QAP1 || problema ==DefineConstantsSlave1.QAP2)
			alg.ejecutarQAP();
		else if(problema ==DefineConstantsSlave1.C101 || problema ==DefineConstantsSlave1.RC101)
			alg.ejecutarVRP();

		String.format(cad,"/home/fuentes/%s.%s.%s.pareto",pr,args.argvalue.charAt(2),instancia);
		if(problema ==DefineConstantsSlave1.C101 || problema ==DefineConstantsSlave1.RC101)
		{
			RefObject<Problem> TempRefObject = new RefObject<Problem>(prob);
			RefObject<String> TempRefObject2 = new RefObject<String>(cad);
			alg.pareto.listarSolucionesVRP(TempRefObject, TempRefObject2);
			prob = TempRefObject.argvalue;
			cad = TempRefObject2.argvalue;
		}
		else
		{
			RefObject<Problem> TempRefObject3 = new RefObject<Problem>(prob);
			RefObject<String> TempRefObject4 = new RefObject<String>(cad);
			alg.pareto.listarSoluciones(TempRefObject3, TempRefObject4);
			prob = TempRefObject3.argvalue;
			cad = TempRefObject4.argvalue;
		}

		return 0;
	}
}


final class DefineConstantsSlave1
{
	public static final int PARETO = 50;
	public static final String SLAVENAME = "slave";
	public static final int CBIANT = 1;
	public static final int CBIMC = 2;
	public static final int CCOMP = 3;
	public static final int CMAS = 4;
	public static final int CM3AS = 5;
	public static final int CMOACS = 6;
	public static final int CMOAQ = 7;
	public static final int CMOA = 8;
	public static final int CPACO = 9;
	public static final int KROAB = 10;
	public static final int KROAC = 11;
	public static final int QAP1 = 12;
	public static final int QAP2 = 13;
	public static final int C101 = 14;
	public static final int RC101 = 15;
	public static final int NTASKS = 2;
	public static final int MSGINI = 20;
	public static final int MSGDATA = 30;
	public static final int MSGCONT = 40;
	public static final int TERMINAR = 50;
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