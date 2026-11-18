import java.util.*;

public class exercicio3 {
    public static Scanner sc = new Scanner (System.in);

    public static boolean isFim(String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public static String inversao (String str) { // método para realizar a inversao de uma string
        String nova = ""; // declaração da string invertida como vazia
        for (int i = str.length() - 1; i >= 0; i--) nova += str.charAt(i); // atribuição de cada caracter individualmente à cada posição da nova string
        return nova; // retorno da nova string invertida
    }

    public static void main (String[] args) { // main do programa
        String str = sc.nextLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler strings e fazer sua inversão
            System.out.println(inversao(str)); // saída da string invertida ao chamar o método
            str = sc.nextLine(); // leitura da próxima string
        }
    }
}
