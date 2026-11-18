import java.util.*;

public class exercicio3 {
    public static Scanner sc = new Scanner (System.in);

    public static boolean isFim(String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }
    public static int somaDigitos(String str, int i) { // método recursivo para somar cada caracter do número inteiro
        if (i >= str.length()) return 0; // condição de parada quando a quantidade de caracteres for maior que o tamanho da string
        else return (str.charAt(i) - '0') + somaDigitos(str, i + 1); // soma o caracter convertido para inteiro e chama o método recursivo com o índice incrementado
    }

    public static void main (String[] args) { // main do programa
        String num = sc.nextLine(); // declaração e leitura do número como string
        while (!isFim(num)) { // loop para ler números como string enquanto a string seja diferente de "FIM"
            System.out.println(somaDigitos(num, 0)); // saída do resultado da soma dos caracteres
            num = sc.nextLine(); // leitura do próximo número como string
        }
    }
}
