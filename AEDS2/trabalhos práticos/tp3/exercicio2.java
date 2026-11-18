import java.util.*;

public class exercicio2 {
    public static Scanner sc = new Scanner (System.in);

    public static boolean isFim(String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public static String inversao (String str, int i) { // método para realizar a inversao de uma string
        if (i < 0) return ""; // condição de parada da recursão, quando já se verificou todos os caracteres
        else return str.charAt(i) + inversao(str, i - 1); // printa o caractere da última posição e faz a chamada recursiva para o próximo caractere
    }

    public static void main (String[] args) { // main do programa
        String str = sc.nextLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler strings e fazer sua inversão
            System.out.println(inversao(str, str.length() - 1)); // saída da string invertida ao chamar o método
            str = sc.nextLine(); // leitura da próxima string
        }
    }
}
