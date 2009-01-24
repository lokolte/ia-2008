//********** Clase Solucion *************
public class Solucion
{
	protected int array;
	protected int size;
	public Solucion(int tam)
	{
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
		array =(int)calloc(sizeof(int),tam);
		for(int i =0;i<tam;i++)
			array[i]=-1;
		size =tam;
	}

	public void resetear()
	{
		for(int i =0;i<size;i++)
			array[i]=-1;
	}

	public void set(int pos,int valor)
	{
		array[pos]=valor;
	}
	public int get(int pos)
	{
		return array[pos];
	}
	public int getSize()
	{
		return size;
	}
	public void imprimir(RefObject<FILE> f)
	{
		for (int i =0;i<size-1;i++)
			fprintf(f.argvalue,"%d-",array[i]);

		fprintf(f.argvalue,"%d\n",array[size-1]);
	}
	public void imprimir()
	{
		for (int i =0;i<size-1;i++)
			System.out.printf("%d-",array[i]);

		System.out.printf("%d\n",array[size-1]);
	}
	public void destruir()
	{
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(array);
	}
//    void cargarSol(void){
//		array[0]=1;
//		array[1]=0;
//	};
	public void solcpy(RefObject<Solucion> sol)
	{
		for(int i =0;i<sol.argvalue.getSize();i++)
			array[i]=sol.argvalue.get(i);
	}
}


//********** Clase SolucionVRP ***********
public class SolucionVRP extends Solucion
{
	private int camiones;
	private int sizeActual;
	private void duplicar_size()
	{
		int arrayAnterior =array;
	
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
		array =(int)malloc(sizeof(int)*size *2);
		for(int i =0;i<size;i++)
			array[i]=arrayAnterior[i];
		for(int i =size;i<size *2;i++)
			array[i]=-1;
		size*=2;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(arrayAnterior);
	}
	public SolucionVRP(int tam)
	{
		super(tam);
		camiones =1;
		sizeActual =0;
	}
	public void setCamiones(int nro)
	{
		camiones =nro;
	}
	public void incCamiones()
	{
		camiones++;
	}
	public int getCamiones()
	{
		return camiones;
	}
	public void add(int valor)
	{
		if(sizeActual+1 >= size)
			duplicar_size();
		array[sizeActual]=valor;
		sizeActual++;
	}
	public int getSizeActual()
	{
		return sizeActual;
	}
	public void imprimir(RefObject<FILE> f)
	{
//        int ruta=1;
//		printf("Ruta 1:0");
//		for (int i=1;i<sizeActual-1;i++)
//		{
//			printf("-%d",array[i]);
//			if(array[i]==0)
//				printf("\nRuta %d:0",++ruta);
//		}
//		printf("-%d\n",array[sizeActual-1]);

		for (int i =0;i<sizeActual-1;i++)
			fprintf(f.argvalue,"%d-",array[i]);

		fprintf(f.argvalue,"%d\n",array[sizeActual-1]);
	}
	public void resetear()
	{
		for(int i =0;i<size;i++)
			array[i]=-1;
		sizeActual =0;
		camiones =0;
	}
	public void solcpy(RefObject<SolucionVRP> sol)
	{
		for(int i =0;i<sol.argvalue.getSizeActual();i++)
			array[i]=sol.argvalue.get(i);
		for(int i =sol.argvalue.getSizeActual();i<size;i++)
			array[i]=-1;
		sizeActual =sol.argvalue.getSizeActual();
		camiones =sol.argvalue.getCamiones();
	}
}

//*********Clase Auxiliar para el VRPTW***********
public class customerVRP
{
	private double x;
	private double y;
	private double serviceTime;
	private double demanda;
	private double timeStart;
	private double timeEnd;
	public customerVRP()
	{
		}
	public void setCoord(double x,double y)
	{
		this.x =x;
		this.y =y;
	}
	public double getCoordX()
	{
		return x;
	}
	public double getCoordY()
	{
		return y;
	}
	public void setServiceTime(double servTime)
	{
		serviceTime =servTime;
	}
	public double getServiceTime()
	{
		return serviceTime;
	}
	public void setDemanda(double dem)
	{
		demanda =dem;
	}
	public double getDemanda()
	{
		return demanda;
	}
	public void setWindow(double begin,double end)
	{
		timeStart =begin;
		timeEnd =end;
	}
	public double getTimeStart()
	{
		return timeStart;
	}
	public double getTimeEnd()
	{
		return timeEnd;
	}
}

//***********Clase Tabla Feromonas***************
public class TablaFeromona
{
	private int size;
	private double[][] tabla;
	public TablaFeromona(int tam)
	{
		size =tam;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
		tabla =(double)malloc(tam *sizeof(double));
		for (int i =0;i<tam;i++)
		{
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
			tabla[i] =(double)malloc(tam *sizeof(double));
		}
	}
	public double obtenerValor(int estOrigen, int estDestino)
	{
		return tabla[estOrigen][estDestino];
	}
	public void actualizar(int estOrigen, int estDestino, double tau)
	{
		tabla[estOrigen][estDestino] =tau;
	}
	public void reiniciar(double tau0)
	{
		for (int i =0; i<size; i++)
		{
			for (int j =0; j<size; j++)
			{
				tabla[i][j] =tau0;
			}
		}
	}
	public void imprimir()
	{
		for (int i =0; i<size; i++)
		{
			for (int j =0; j<size; j++)
			{
				System.out.printf("%lf ",tabla[i][j]);
			}
			System.out.print("\n");
		}
	}
	public void destruir()
	{
		for (int i =0;i<size;i++)
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
			free(tabla[i]);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
		free(tabla);
	}
}


//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2008 Tangible Software Solutions Inc.
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