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
    private String beginFile;
    private String endFile;

    public static void main(String[] args) {
        // new Environment("ArcticMonkeys505.csv");
        EncuentraAcordes arc = new EncuentraAcordes("ArcticMonkeys505.csv", "ArcticMonkeys505.begin.txt", "ArcticMonkeys505.end.txt");

        for (int i=0; i < arc.filasAcordes.length; i++) {
            arc.execute(i);
        }
    }

    public void execute(int numAcorde) {
        if (numAcorde < 0 || numAcorde > filasAcordes.length) {
            System.out.println("ACORDE INVALIDO.");
            return;
        }
        int[] volumenes = cancion.sumaPeriodicaSobreColumnas(filasAcordes[numAcorde][0], filasAcordes[numAcorde][1], NUMERO_DE_NOTAS);
        int[] clases = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Ordenar.ordenDescendente(clases, volumenes);
        volumenes = eliminarNotasConVolumenMenorAl(volumenes);

        int[] clasesRelativizadas = clasesRelativizadas(clases);

        String palabra = formarPalabra(clasesRelativizadas, volumenes);
        if (numAcorde == 70){
            System.out.println();
        }
        String[][] acepciones = DICCIONARIO.getClosestElement(palabra);
        desrelativizarAcepciones(acepciones, clases[0]);

        int[] notasCaracteristicas = notasCaracteristicas(numAcorde);
        String[][] acepcionesConCaracteristicas = matrizCaracteristicasNotas(acepciones, notasCaracteristicas);
    
        ArrayList<Double[]> inicioYFinalDeAcordes = leerTiemposDeAcordes(beginFile, endFile);
        System.out.printf("%d: %.2f%n", numAcorde+1, inicioYFinalDeAcordes.get(numAcorde)[0]);
        acordes(acepcionesConCaracteristicas);
        System.out.println();
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
        this.beginFile = beginFile;
        this.endFile = endFile;
    }

    /**
     * A partir de los archivos que detallan los tiempos en que inicia y termina cada acorde,
     * obtiene un int[][] en el cual cada entrada representa un acorde y tiene un int[] de tamaño
     * 2 donde se dice en que fila de la matriz inicia el acorde y en cual termina.
     * @param beginFile
     * @param endFile
     * @return int[][] con las filas de inicio y de finalizacion de cada acorde.
     */
    private static int[][] filasDeAcordes(String beginFile, String endFile) {
        ArrayList<Double[]> inicioYFinalDeAcordes = leerTiemposDeAcordes(beginFile, endFile);
        int[][] acordes = new int[inicioYFinalDeAcordes.size()][];
        for (int i = 0; i < acordes.length; i++) {
            acordes[i] = new int[] {(int)(inicioYFinalDeAcordes.get(i)[0]/TIEMPO_POR_FILA), (int)(inicioYFinalDeAcordes.get(i)[1]/TIEMPO_POR_FILA)};
        }
        return acordes;
    }

        private static ArrayList<Double[]> leerTiemposDeAcordes(String beginFile, String endFile) {
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
            return inicioYFinalDeAcordes;
        }
        catch (IOException ex) {
            System.out.println("Error al abrir el archivo." + ex.getMessage());
            return null;
        }
    }

    public int[] notasCaracteristicas(int numAcorde) {
        return cancion.centroidesDePeriodosDeSubcolumnasCuantizados(filasAcordes[numAcorde][0], filasAcordes[numAcorde][1], NUMERO_DE_NOTAS);
    }

    private static int[] eliminarNotasConVolumenMenorAl(int[] lista){
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

    private static String[][] matrizCaracteristicasNotas(String[][] matrizAcepcionesInterpretadas, int[] notasCaracteristicas){
        int[] clases = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        String[][] matrizNueva = matrizAcepcionesInterpretadas.clone();
        //Se asocian las clases con las notas musicales (Usamos la nomenclatura estadounidense)
        HashMap<String, String> claseToString = new HashMap<String, String>();
        claseToString.put("0", "A");
        claseToString.put("1", "A#");
        claseToString.put("2", "B");
        claseToString.put("3", "C");
        claseToString.put("4", "C#");
        claseToString.put("5", "D");
        claseToString.put("6", "D#");
        claseToString.put("7", "E");
        claseToString.put("8", "F");
        claseToString.put("9", "F#");
        claseToString.put("10", "G");
        claseToString.put("11", "G#");

        //Se asignan las clases y notas
        HashMap<Integer, Integer> caracteristicaToclase = new HashMap<Integer, Integer>();
        for(int k = 0; k < 12; k++){
            caracteristicaToclase.put(clases[k], notasCaracteristicas[k]);
        }

        for(int i = 0; i < matrizNueva.length; i++){
            matrizNueva[i][0] = Integer.toString(caracteristicaToclase.get(Integer.parseInt(matrizAcepcionesInterpretadas[i][0])));
        }

        for(int i = 0; i < matrizNueva.length; i++){
            matrizNueva[i][1] = claseToString.get(matrizAcepcionesInterpretadas[i][1]);
        }
        return matrizNueva;
    }

    // Metodo para ordenar y obtener los acordes de matrizCaracteristicasNotas
    public static void acordes(String[][] matrizCaracteristicasNotas){
        int[] columnaDeNotasCaracteristicas = new int[matrizCaracteristicasNotas.length];
        String[] acordesAsociados = new String[matrizCaracteristicasNotas.length];

        for(int i = 0; i < matrizCaracteristicasNotas.length; i++){
            columnaDeNotasCaracteristicas[i] = Integer.parseInt(matrizCaracteristicasNotas[i][0]);
            acordesAsociados[i] = matrizCaracteristicasNotas[i][1] + matrizCaracteristicasNotas[i][2];
        }

        //Se ordenan los acordes asociados con base en las notas características
        Ordenar.ordenAscendenteString(acordesAsociados, columnaDeNotasCaracteristicas);

        // Se eliminan los acordes repetidos si es que los hay
        acordesAsociados = Arrays.stream(acordesAsociados).distinct().toArray(String[]::new);

        // Se imprimen los acordes sugeridos
        // Imprimimos los acordes si la cantidad de acordes es menor o igual que tres 
        if(acordesAsociados.length < 3){
            for(int k = 0; k < acordesAsociados.length; k++){
                System.out.println(acordesAsociados[k]);
            }
        }

        // Si hay mas de 3 solo se reportan los primeros tres.
        else{
            for(int k = 0; k < 3; k++){
                System.out.println(acordesAsociados[k]);    
            }
        }
    }
}

