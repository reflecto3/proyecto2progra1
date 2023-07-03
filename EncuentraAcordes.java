import java.util.Arrays;

public final class EncuentraAcordes {
    private static final Trie diccionario = Trie.crearDiccionario("ChordsDictBasic.txt");

    public static void main(String[] args) {
        String[][] e = diccionario.getElement("39A5");
        for (String[] a : e) {
            System.out.println(Arrays.toString(a));
        }
    }

}