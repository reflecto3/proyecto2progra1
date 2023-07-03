import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class Trie {
    private Nodo raiz;

    public Trie() {
        raiz = new Nodo(null);
    }

    public void insertar(String palabra, String[][] elemento) {
        palabra = palabra.trim();
        if (palabra.isEmpty()) {
            return;
        }

        insertar(palabra, elemento, raiz);
    }

    
    private void insertar(String subpalabra, String[][] elemento, Nodo inicio) {
        if (subpalabra.isEmpty()) {
            inicio.elemento = elemento;
        }

        if (inicio.conexiones.containsKey(subpalabra.charAt(0))) {
            insertar(subpalabra.substring(1), elemento, inicio.conexiones.get(subpalabra.charAt(0)));
            return;
        }

        completarPalabra(subpalabra, elemento, inicio);
    }
    
    private void completarPalabra(String subpalabra, String[][] elemento, Nodo inicio) {
        for (int i = 0; i < subpalabra.length(); i++) {
            inicio.conexiones.put(subpalabra.charAt(i), new Nodo());
            inicio = inicio.conexiones.get(subpalabra.charAt(i));
        }
        inicio.elemento = elemento;
    }

    public String[][] getElement(String palabra) {
        Nodo actual = raiz;
        for (int i = 0; i < palabra.length(); i++) {
            actual = actual.conexiones.get(palabra.charAt(i));
            if (actual == null) {
                return null;
            }
        }
        return actual.elemento;
    }

        public static Trie crearDiccionario(String filename) {
        try {
            BufferedReader bfReader = new BufferedReader(new FileReader(filename));
            String line = bfReader.readLine();
            Trie dct = new Trie();
            while (line != null) {
                String palabra = line.substring(0,line.indexOf('.'));
                String[] acepcionesEnHilera = line.substring(line.indexOf('.')+2).split(";");
                String[][] acepciones = new String[acepcionesEnHilera.length][];
                for (int i = 0; i < acepcionesEnHilera.length; i++) {
                    acepciones[i] = acepcionesEnHilera[i].trim().split(",");
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

    private class Nodo {
        private String[][] elemento;
        private HashMap<Character, Nodo> conexiones;

        public Nodo() {
            elemento = null;
            conexiones = new HashMap<>();
        }

        public Nodo(String[][] elemento) {
            this.elemento = elemento;
            conexiones = new HashMap<>();
        }
    }
}