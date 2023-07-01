import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public final class Name {
    public static Trie createDictionary(String filename) {
        try {
            BufferedReader bfReader = new BufferedReader(new FileReader(filename));
            String line = bfReader.readLine();
            Trie dct = new Trie();
            while (line != null) {
                String palabra = line.substring(0,line.indexOf('.'));
                String[] acepciones = line.substring(line.indexOf('.')+2).split(";");
                for (String acepcion: acepciones) {
                    acepcion.trim();
                }
                dct.insertar(palabra, acepciones);
                line = bfReader.readLine();
            }
            bfReader.close();
            return dct;
        } catch (IOException e) {
            System.out.println("Error al abrir el archivo");
            return null;
        }
    }
}