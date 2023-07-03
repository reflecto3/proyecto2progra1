import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class EncuentraAcordes {
    private static final Trie DICCIONARIO = Trie.crearDiccionario("ChordsDictBasicV2.txt");
    private Matriz cancion;
    private int[][] filasAcordes;
    private static final double TIEMPO_POR_FILA = 256.0/11025;

    public static void main(String[] args) {
        EncuentraAcordes e = new EncuentraAcordes("Luis Miguel - El día que me quieras (intro).csv", "Luis Miguel - El día que me quieras (intro).begin.txt", "Luis Miguel - El día que me quieras (intro).end.txt");
        System.out.println(e.cancion.numFilas);
        for (int[] ac : e.filasAcordes) {
            System.out.println(Arrays.toString(ac));
        }
    }

    EncuentraAcordes(String csvFile, String beginFile, String endFile) {
        cancion = Matriz.matrizDeCancionCSV(csvFile);
        filasAcordes = filasDeAcordes(beginFile, endFile);

    }

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
        catch (IOException e) {
            System.out.println("Error al abrir el archivo.");
            return null;
        }
    }
}