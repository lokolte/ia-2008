/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;


public class construir_solucion
{
	public static void construir_solucionTSP(int estOrig, MOACO aco, int onlineUpdate, Solucion sol)
	{
		int estVisitados =0;
		int sgteEstado =0;
		int estActual =estOrig;
		sol.set(estVisitados, estOrig);
		estVisitados++;
		while(estVisitados<(aco.getProblema().getSize()))
			{
			sgteEstado = aco.seleccionar_siguiente_estadoTSP(estActual, sol);
			if (onlineUpdate != 0)
				aco.online_update(estActual, sgteEstado);
			estActual = sgteEstado;
			sol.set(estVisitados, sgteEstado);
			estVisitados++;
		}
		sol.set(estVisitados, estOrig);
	}

	public static void construir_solucionQAP(int estOrigen, MOACO aco, int onlineUpdate, Solucion sol)
	{
		int estVisitados =0;
		int sgteEstado;
		int estActual =estOrigen;

		sol.set(estVisitados, estOrigen);
		estVisitados++;
		while(estVisitados<aco.getProblema().getSize())
		{
			sgteEstado = aco.seleccionar_siguiente_estadoQAP(estActual, sol);
			if (onlineUpdate != 0)
				aco.online_update(estActual, sgteEstado);
			estActual = sgteEstado;
			sol.set(estVisitados, sgteEstado);
			estVisitados++;
		}

	}

	public static void construir_solucionVRP(int estOrigen, MOACO aco, int onlineUpdate, SolucionVRP sol)
	{
		int estVisitados =0;
		int sgteEstado;
		int estActual =estOrigen;
		double cargaActual;
		double currentTime;

		VRPTW vrp =(VRPTW)aco.getProblema();

		sol.add(estOrigen);
		estVisitados++;
		currentTime =0;
		cargaActual =0;
		while(estVisitados<vrp.getSize())
		{
			sgteEstado = aco.seleccionar_siguiente_estadoVRP(estActual, sol, currentTime, cargaActual);
			sol.add(sgteEstado);
			if(sgteEstado!=0) //0 representa el deposito, no ir al deposito
			{
				estVisitados++;
				currentTime =Math.max(currentTime+vrp.getDistancia(estActual,sgteEstado),vrp.getTimeStart(sgteEstado));
				cargaActual+=vrp.getDemanda(sgteEstado);
				if (onlineUpdate != 0)
					aco.online_update(estActual, sgteEstado);
			}
			else // ir al deposito
			{
				currentTime =0;
				cargaActual =0;
				sol.incCamiones();
			}
			estActual = sgteEstado;
		}
		sol.add(estOrigen); // volver al deposito
	}
}

