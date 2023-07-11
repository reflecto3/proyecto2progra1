public class Ordenar {
    private Ordenar(){}

    public static void ordenDescendente(int[] lista1, int[] lista2){
        if(lista1.length != lista2.length){
            System.err.println("Estos arreglos no tienen el mismo tamaño.");
            return;
        }
        
        for (int i = lista2.length - 1; i > 0; i--){
            int posMin = i;
            for (int j = i; j >= 0; j--){
                if(lista2[j] < lista2[posMin]){
                    posMin = j;
                }
            }

            int temp = lista2[i];
            lista2[i] = lista2[posMin];
            lista2[posMin] = temp;

            int temp2 = lista1[i];
            lista1[i] = lista1[posMin];
            lista1[posMin] = temp2;
        }    
    }
    
    public static void ordenAscendenteString(String[] hileras, int[] numeros){
        if(hileras.length != numeros.length){
            System.err.println("Estos arreglos no tienen el mismo tamaño.");
            return;
        }
        
        for (int i = 0; i < numeros.length; i++){
            int posMin = i;
            for (int j = i + 1; j < numeros.length; j++){
                if(numeros[j] < numeros[posMin]){
                    posMin = j;
                }
            }

            int temp1 = numeros[i];
            numeros[i] = numeros[posMin];
            numeros[posMin] = temp1;

            String temp2 = hileras[i];
            hileras[i] = hileras[posMin];
            hileras[posMin] = temp2;
        }    
    }
}