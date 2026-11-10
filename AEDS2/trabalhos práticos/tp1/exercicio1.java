import java.util.*;

public class exercicio1 {
    public static Scanner sc = new Scanner (System.in);

    public static boolean isFim (String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public static boolean palindromo (String str) { // método para verificar se a string é palíndromo
        boolean resp = false; // declaração do retorno booleano do método
        int i = 0, j = str.length() - 1; // declaração dos índices i e j (primeiro e último caracter da string)
        while (i < j) {
            if (str.charAt(i) != str.charAt(j)) {
                resp = false; // se os caracteres forem diferentes, a string não é palíndromo
                i = j; // atribuição de i igual à j para sair do loop
            }
            else {
                resp = true; // se os caracteres forem iguais, a string pode ser palíndromo
                i++; j--; // incrementa i e decrementa j para verificar os próximos caracteres
            }
        }
        return resp; // retorno do método (verdadeiro ou falso)
    }

    public static void main (String[] args) { // main do programa
        String str = sc.nextLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler strings e verifiar se essa é um palíndromo enquanto a string seja diferente de "FIM"
            if (palindromo(str)) System.out.println("SIM"); // se for palíndromo, imprime "SIM"
            else System.out.println("NAO"); //se não, imprime "NAO"
            str = sc.nextLine(); // leitura da próxima string
        }
    }
}
