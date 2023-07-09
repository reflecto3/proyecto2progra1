import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase principal del Proyecto 2 del curso de Programacion 1 de la UCR.
 * Consiste en un 'EncuentraAcordes,' que toma como entrada un .csv con los volumenes de las 88
 * notas del piano cada 256/11025 segundos, un .txt que detalla el tiempo en segundos en el que 
 * inicia cada acorde y otro que detalla cuando termina cada acorde.
 * @author Jose Eduardo Lopez Corella 
 * @author Jose Javier Alvarez Pacheco
 * @version 03/07/2023
 */
public class EncuentraAcordes {
    private static final Trie DICCIONARIO = Trie.crearDiccionario("ChordsDictBasicV2.txt");
    private static final int NUMERO_DE_NOTAS = 12;
    private static final int NUMERO_DE_TECLAS = 88;
    private Matriz cancion;
    private int[][] filasAcordes;
    private static final double TIEMPO_POR_FILA = 256.0/11025;

    // public static void main(String[] args) {
    //     EncuentraAcordes arc = new EncuentraAcordes("LuisMiguel-ElDiaQueMeQuieras(intro).csv", "LuisMiguel-ElDiaQueMeQuieras(intro).begin.txt", "LuisMiguel-ElDiaQueMeQuieras(intro).end.txt");
    //     arc.execute();
    // }

    public static void main(String[] args) {
        new Environment(44, NUMERO_DE_TECLAS, "ArcticMonkeys505.csv");
    }

    public void execute() {
        for (int numAcorde = 0; numAcorde < filasAcordes.length; numAcorde++) {
            System.out.println("ACORDE " + (numAcorde+1));
            int[] volumenes = cancion.sumaPeriodicaSobreColumnas(filasAcordes[numAcorde][0], filasAcordes[numAcorde][1], NUMERO_DE_NOTAS);
            int[] clases = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            ordenarDecendienteSegunCriterio(clases, volumenes);
            // System.out.println(Arrays.toString(volumenes) + "\n" + Arrays.toString(clases));
            volumenes = eliminarNotasConVolumenMenorAl(volumenes);
            // System.out.println(Arrays.toString(volumenes));

            int[] clasesRelativizadas = clasesRelativizadas(clases);
            // System.out.println(Arrays.toString(clases) + "\n" + Arrays.toString(clasesRelativizadas));

            String palabra = formarPalabra(clasesRelativizadas, volumenes);
            String[][] acepciones = DICCIONARIO.getClosestElement(palabra);
            for (String[] a: acepciones) {
                System.out.print(Arrays.toString(a));
            }
            desrelativizarAcepciones(acepciones, clases[0]);

            System.out.println("\n" + Arrays.toString(clases));
            for (String[] a: acepciones) {
                System.out.print(Arrays.toString(a));
            }
            System.out.println();

        }
    }
    
    /**
     * Constructor de la clase EncuentraAcordes
     * @param csvFile Archivo con los volumenes de las 88 notas cada 256/11025 segundos.
     * @param beginFile Archivo con los tiempos en segundos en que inicia cada acorde.
     * @param endFile Archivo con los tiempos en segundos en que termina cada acorde.
     */
    EncuentraAcordes(String csvFile, String beginFile, String endFile) {
        cancion = Matriz.matrizDeCancionCSV(NUMERO_DE_TECLAS, csvFile);
        filasAcordes = filasDeAcordes(beginFile, endFile);

    }

    /**
     * A partir de los archivos que detallan los tiempos en que inicia y termina cada acorde,
     * obtiene un int[][] en el cual cada entrada representa un acorde y tiene un int[] de tamaño
     * 2 donde se dice en que fila de la matriz inicia el acorde y en cual termina.
     * @param beginFile
     * @param endFile
     * @return int[][] con las filas de inicio y de finalizacion de cada acorde.
     */
    public static int[][] filasDeAcordes(String beginFile, String endFile) {
        try {
            BufferedReader bfBegin = new BufferedReader(new FileReader(beginFile));
            BufferedReader bfEnd = new BufferedReader(new FileReader(endFile));
            ArrayList<Double[]> inicioYFinalDeAcordes = new ArrayList<>();
            String lineBegin = bfBegin.readLine();
            String lineEnd = bfEnd.readLine();
            while (lineBegin != null && lineEnd != null) {
                inicioYFinalDeAcordes.add(new Double[] {Double.parseDouble(lineBegin), Double.parseDouble(lineEnd)});
                lineBegin = bfBegin.readLine();
                lineEnd = bfEnd.readLine();
            }
            bfBegin.close();
            bfEnd.close();
            int[][] acordes = new int[inicioYFinalDeAcordes.size()][];
            for (int i = 0; i < acordes.length; i++) {
                acordes[i] = new int[] {(int)(inicioYFinalDeAcordes.get(i)[0]/TIEMPO_POR_FILA), (int)(inicioYFinalDeAcordes.get(i)[1]/TIEMPO_POR_FILA)};
            }
            return acordes;
        }
        catch (IOException ex) {
            System.out.println("Error al abrir el archivo." + ex.getMessage());
            return null;
        }
    }

    public static int[] eliminarNotasConVolumenMenorAl(int[] lista){
        double suma = 0;
        int[] listaActualizada = lista.clone();
        for(int i = 0; i < lista.length; i++){
            suma = suma + lista[i];
        }
        double sumaTotal = suma;
        double porcentajeDelTotal = 0.035 * sumaTotal;
        for(int j = 0; j < listaActualizada.length; j++){
            if(listaActualizada[j] < porcentajeDelTotal){
                listaActualizada[j] = 0;
            }
        }
        return listaActualizada;
    }
    
    private static void ordenarDecendienteSegunCriterio(int[] arr, int[] criterio) {
        if (arr.length != criterio.length) {
            System.err.println("La longitud del arreglo y del criterio tienen que ser la misma.");
            return;
        }
        
        for (int i = 1; i < arr.length; i++) {
            int insertArr = arr[i];
            int insertCriterio = criterio[i];
            int j = i - 1;
            while (j >= 0 && criterio[j] < insertCriterio) {
                criterio[j+1] = criterio[j];
                arr[j+1] = arr[j];
                j--;
            }
            criterio[j+1] = insertCriterio;
            arr[j+1] = insertArr;
        }
    }
    
    
    public static String CambiarClaseANotaMusical(int clase){
        HashMap<Integer, String> claseToString = new HashMap<Integer, String>();
       
        claseToString.put(0, "A");
        claseToString.put(1, "A#");
        claseToString.put(2, "B");
        claseToString.put(3, "C");
        claseToString.put(4, "C#");
        claseToString.put(5, "D");
        claseToString.put(6, "D#");
        claseToString.put(7, "E");
        claseToString.put(8, "F");
        claseToString.put(9, "F#");
        claseToString.put(10, "G");
        claseToString.put(11, "G#");
        String notaMusical = claseToString.get(clase);
        return notaMusical;
    }

    private static int[] clasesRelativizadas(int[] clases) {
        int[] clasesRel = clases.clone();
        for (int i = 0; i < clasesRel.length; i++) {
            clasesRel[i] = (clases[i] - clases[0] + clases.length) % clases.length;
        }
        
        return clasesRel;
    }

    private String formarPalabra(int[] clasesRelativizadas, int[] volumenes) {
        if (clasesRelativizadas.length != volumenes.length) {
            System.out.println("ERROR: EN FORMAR PALABRA");
            return null;
        }

        String palabra = "";
        for (int i = 1; i < clasesRelativizadas.length; i++) { // i = 1 para que la palabra no inicie en 0
            if (volumenes[i] == 0) {
                break;
            }
            if (clasesRelativizadas[i] < 10) {
                palabra = palabra + clasesRelativizadas[i];
            }
            else {
                palabra = palabra + (clasesRelativizadas[i] == 10 ? "A" : "B");
            }
        }

        return palabra;
    }

    private int claseDuodecimalADecimal(String clase) {
        switch (clase) {
            case "A": return 10;
            case "B": return 11;
            default: return Integer.parseInt(clase); 
        }
    }

    private void desrelativizarAcepciones(String[][] acepciones, int clasePrincipal) {
        for (int i = 0; i < acepciones.length; i++) {
            acepciones[i][0] = "" + (claseDuodecimalADecimal(acepciones[i][0]) + clasePrincipal) % NUMERO_DE_NOTAS;
            acepciones[i][1] = "" + (claseDuodecimalADecimal(acepciones[i][1]) + clasePrincipal) % NUMERO_DE_NOTAS;
        }
    }
}

