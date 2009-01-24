/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacos;

/**
 *
 * @author Christian Gomez
 */
//*********** Clase VRPTW *************
public class VRPTW extends Problem
{
	private int capacity;
	private customerVRP customers;
	private void cargar_estado(RefObject<String> file)
	{
		// El archivo file posee: la cantidad de customers, la capacidad de los camiones
		// y los datos de cada customer: coordenadas, demanda, ventana y tiempo de servicio
		FILE f;
		double x;
		double y;
		double dem;
		double begin;
		double end;
		double servTime;
		int i;
	
		f =fopen(file.argvalue,"r");
		fscanf(f, "%d\n", i);
		fscanf(f, "%d\n", capacity);
		for(i =0;i<size;i++)
		{
			fscanf(f, "%lf", x);
			fscanf(f, "%lf", x);
			fscanf(f, "%lf", y);
			fscanf(f, "%lf", dem);
			fscanf(f, "%lf", begin);
			fscanf(f, "%lf", end);
			fscanf(f, "%lf", servTime);
			customers[i].setCoord(x, y);
			customers[i].setDemanda(dem);
			customers[i].setWindow(begin, end);
			customers[i].setServiceTime(servTime);
		}
		fclose(f);
		generar_matriz_ady();
	}
	private void generar_matriz_ady()
	{
		// a partir de las coordenadas de los customers se genera la matriz simetrica
		// de adyacencia con las dinstancias euclideas entre cada par de customers
		double aux;
	
		for(int i =0;i<size;i++)
		{
			for(int j =i+1;j<size;j++)
			{
				aux =Math.pow(customers[i].getCoordX()-customers[j].getCoordX(),2);
				aux+=Math.pow(customers[i].getCoordY()-customers[j].getCoordY(),2);
				matrizAdy[i][j] =Math.sqrt(aux);
				matrizAdy[j][i] =matrizAdy[i][j];
			}
			matrizAdy[i][i] =0;
		}
	}
	public VRPTW(RefObject<String> file)
	{
		super(file);
		customers =new customerVRP[size];
		cargar_estado(file);
	}
	public double funcion_obj_1(RefObject<Solucion> sol)
	{
		return ((SolucionVRP) sol.argvalue).getCamiones(); // devuelve la cantidad camiones
	}
	public double funcion_obj_2(RefObject<Solucion> sol)
	{
		int i;
		double suma =0;
		SolucionVRP s =(SolucionVRP) sol.argvalue;
		for(i =0;i<s.getSizeActual()-1;i++)
			suma+=matrizAdy[s.get(i)][s.get(i+1)];
		suma+=matrizAdy[s.get(s.getSizeActual()-1)][0];
	
		return suma; // devolver el "Total Travel Distance"
	}
	public double heuristica_1(int estOrigen,int estDest)
	{
		return 1;
	}
	public double heuristica_2(int estOrigen,int estDest)
	{
		return 1.0/matrizAdy[estOrigen][estDest];
	}
	public int getCapacity()
	{
		return capacity;
	}
	public double getDemanda(int customer)
	{
		return customers[customer].getDemanda();
	}
	public double getTimeStart(int customer)
	{
		return customers[customer].getTimeStart();
	}
	public double getTimeEnd(int customer)
	{
		return customers[customer].getTimeEnd();
	}
	public double getServiceTime(int customer)
	{
		return customers[customer].getServiceTime();
	}
	public void imprimir()
	{
		System.out.print("Matriz Adyacencia:\n");
		for(int i =0;i<1;i++)
		{
			for(int j =0;j<size;j++)
				System.out.printf("%lf ",matrizAdy[i][j]);
			System.out.print("\n");
		}
		System.out.printf("Size: %d\n",size);
		System.out.printf("capacity: %d\n",capacity);
		for(int i =0;i<20;i++)
		{
			System.out.printf("Customer %d\n",i);
			System.out.printf("Demanda: %lf\n",customers[i].getDemanda());
			System.out.printf("Service Time: %lf\n",customers[i].getServiceTime());
			System.out.printf("Begin: %lf\n",customers[i].getTimeStart());
			System.out.printf("End: %lf\n",customers[i].getTimeEnd());
		}
	}
}



