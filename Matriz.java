import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
/**
 * Clase matriz de enteros con operaciones especializadas
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Matriz
{
    // La matriz
    private int[][] arreglo;
    int numFilas;
    int numCols;

    /**
     * Crea matriz de enteros a partir de arreglo de enteros. 
     * La matriz se llena por filas.
     * @param numCols Cantidad de columnas.
     */
    public Matriz(int numCols, int [] datos)
    {
        if (numCols < 1) {
            System.err.println("Número de columnas inválido. Se usó numCols = 1.");
            numCols = 1;
        }
        if (datos.length < 1) {
            System.err.println("No hay datos. Se creó una matriz 1x1 nula.");
            numCols = 1;
            datos = new int[]{0};
        }
        numFilas = datos.length / numCols;
        if (datos.length % numCols != 0) {
            System.err.println("El tamaño de los datos no es múltiplo de la cantidad de columnas. La última fila se rellenó con ceros");
            numFilas++;
        } 
        this.numCols = numCols;
        arreglo = new int[numFilas][numCols];
        for (int i = 0; i < datos.length; i++) {
            arreglo[i / numCols][i % numCols] = datos[i];
        }
    }

    /**
     * Convierte la matriz a formato de hilera.
     */
    public String toString()
    {
        String hilera = Arrays.toString(arreglo[0]);
        for(int i = 1; i < this.numFilas; i++) {
            hilera += "\n" + Arrays.toString(arreglo[i]);
        }
        return hilera;
    }

    /**
     * Suma valores de subcolumnas
     *
     * @param  filaInicial  fila donde inicia la suma
     * @param  filaFinal  fila donde termina la suma
     * @return    arreglo con los valores de las sumas
     */
    public int [] sumaSobreColumnas(int filaInicial, int filaFinal)
    {
        if (filaInicial < 0 || filaFinal >= numFilas || filaFinal < filaInicial) {
            System.err.println("Rango de filas mal formado.");
            return null;
        }
        
        int [] res = new int[numCols];
        for (int col = 0; col < numCols; col++) {
            for (int row = filaInicial; row <= filaFinal; row++) {
                res[col] += arreglo[row][col];
            }
        }
        return res;
    }

    /**
     * Suma periódica de valores en subcolumnas
     *
     * @param  filaInicial  fila donde inicia la suma
     * @param  filaFinal  fila donde termina la suma
     * @param  periodo  periodo de la suma
     * @return    arreglo con los valores de las sumas periódicas
     */
    public int [] sumaPeriodicaSobreColumnas(int filaInicial, int filaFinal, int periodo)
    {
        if (filaInicial < 0 || filaFinal >= numFilas || filaFinal < filaInicial) {
            System.err.println("Rango de filas mal formado.");
            return null;
        }
        if (periodo < 1) {
            System.err.println("El periodo debe ser positivo.");
            return null;
        }
        int [] sumCols = this.sumaSobreColumnas(filaInicial, filaFinal);
        int [] res = new int[periodo > numCols ? numCols: periodo];
        for (int col = 0; col<numCols; col++) {
            res[col % periodo] += sumCols[col];
        }

        return res;
    }  
  
  
    /**
     * Centroides de periodos de subcolumnas
     *
     * @param  filaInicial  fila donde inicia la suma
     * @param  filaFinal  fila donde termina la suma
     * @param  periodo  periodo de la suma
     * @return    centroides de las subcolumnas
     */
    public double [] centroidesDePeriodosDeSubcolumnas(int filaInicial, int filaFinal, int periodo)
    {
        if (filaInicial < 0 || filaFinal >= numFilas || filaFinal < filaInicial) {
            System.err.println("Rango de filas mal formado.");
            return null;
        }
        if (periodo < 1) {
            System.err.println("El periodo debe ser positivo.");
            return null;
        }
        double [] centroides = new double[periodo];
        for (int i = 0; i < centroides.length; i++) {
            double sumaTotal = 0;
            for (int col = i; col < numCols; col += periodo) {
                double sumaColumna = 0;
                for (int row = filaInicial; row <= filaFinal; row++) {
                    sumaColumna += arreglo[row][col];
                    sumaTotal += arreglo[row][col];
                }
                centroides[i] += col * sumaColumna;
            }
            centroides[i] = sumaTotal == 0 ? Integer.MIN_VALUE : centroides[i]/sumaTotal; // se utiliza Integer.MIN_VALUE como simbolo de que se indefine.
        }
        return centroides;
    }

     /**
     * Centroides de periodos de subcolumnas cuantizados (redondeados) al elemento más cercano de la clase.
     *
     * @param  filaInicial  fila donde inicia la suma
     * @param  filaFinal  fila donde termina la suma
     * @param  periodo  periodo de la suma
     * @return    centroides de las subcolumnas cuantizadas
     */
    public int [] centroidesDePeriodosDeSubcolumnasCuantizados(int filaInicial, int filaFinal, int periodo)
    {
        if (filaInicial < 0 || filaFinal >= numFilas || filaFinal < filaInicial) {
            System.err.println("Rango de filas mal formado.");
            return null;
        }
        if (periodo < 1) {
            System.err.println("El periodo debe ser positivo.");
            return null;
        }
        int [] centroidesCuantizados = new int[periodo];
        double [] centroides = centroidesDePeriodosDeSubcolumnas(filaInicial, filaFinal, periodo);
        for (int clase = 0; clase < centroidesCuantizados.length; clase++) {
            double distanciaCentroideAClase = ((centroides[clase] % periodo - clase) + periodo) % periodo;
            centroidesCuantizados[clase] = distanciaCentroideAClase < 0.5*periodo ?
                                       (int)(centroides[clase] - distanciaCentroideAClase + 0.00001) : //El 0.00001 para evitar que por redondeo quede como (int) x.99999 -> x, cuando fue que la resta queria quedar en x+1 exacto
                                       (int)(centroides[clase] + periodo - distanciaCentroideAClase + 0.00001);
        }
        return centroidesCuantizados;
    }

    public static Matriz matrizDeCancionCSV(String filename) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            String line = bf.readLine();
            String[] datosHilera = line.split(",", -1);
            int[] datos = new int[datosHilera.length];
            for (int i = 0; i < datosHilera.length ; i++) {
                datos[i] = datosHilera[i].isEmpty() ? 0 : Integer.parseInt(datosHilera[i]);
            }
            bf.close();
            return new Matriz(88, datos);

        } catch (IOException e) {
            System.out.println("Error al abrir el archivo");
            return null;
        }
    }
}