/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

public class GlobalMembersConstruir_solucion
{
	public static void construir_solucionTSP(int estOrig, MOACO aco, int onlineUpdate, RefObject<Solucion> sol)
	{
		int estVisitados =0;
		int sgteEstado =0;
		int estActual =estOrig;
		sol.argvalue.set(estVisitados, estOrig);
		estVisitados++;
		while(estVisitados<(aco.getProblema().getSize()))
			{
			sgteEstado = aco.seleccionar_siguiente_estadoTSP(estActual, sol);
			if (onlineUpdate != 0)
				aco.online_update(estActual, sgteEstado);
			estActual = sgteEstado;
			sol.argvalue.set(estVisitados, sgteEstado);
			estVisitados++;
		}
		sol.argvalue.set(estVisitados, estOrig);
	}

	public static void construir_solucionQAP(int estOrigen, MOACO aco, int onlineUpdate, RefObject<Solucion> sol)
	{
		int estVisitados =0;
		int sgteEstado;
		int estActual =estOrigen;

		sol.argvalue.set(estVisitados, estOrigen);
		estVisitados++;
		while(estVisitados<aco.getProblema().getSize())
		{
			sgteEstado = aco.seleccionar_siguiente_estadoQAP(estActual, sol);
			if (onlineUpdate != 0)
				aco.online_update(estActual, sgteEstado);
			estActual = sgteEstado;
			sol.argvalue.set(estVisitados, sgteEstado);
			estVisitados++;
		}

	}

	public static void construir_solucionVRP(int estOrigen, MOACO aco, int onlineUpdate, RefObject<SolucionVRP> sol)
	{
		int estVisitados =0;
		int sgteEstado;
		int estActual =estOrigen;
		double cargaActual;
		double currentTime;

		VRPTW vrp =(VRPTW)aco.getProblema();

		sol.argvalue.add(estOrigen);
		estVisitados++;
		currentTime =0;
		cargaActual =0;
		while(estVisitados<vrp.getSize())
		{
			sgteEstado = aco.seleccionar_siguiente_estadoVRP(estActual, sol, currentTime, cargaActual);
			sol.argvalue.add(sgteEstado);
			if(sgteEstado!=0) //0 representa el deposito, no ir al deposito
			{
				estVisitados++;
				currentTime =max(currentTime+vrp.getDistancia(estActual,sgteEstado),vrp.getTimeStart(sgteEstado));
				cargaActual+=vrp.getDemanda(sgteEstado);
				if (onlineUpdate != 0)
					aco.online_update(estActual, sgteEstado);
			}
			else // ir al deposito
			{
				currentTime =0;
				cargaActual =0;
				sol.argvalue.incCamiones();
			}
			estActual = sgteEstado;
		}
		sol.argvalue.add(estOrigen); // volver al deposito
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