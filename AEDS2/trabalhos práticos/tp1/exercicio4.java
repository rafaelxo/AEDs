import java.util.*;

public class exercicio4 {
    private static Random rand = new Random(4);

    public static boolean isFim (String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public String alteracao (String str) { // método para realizar a alteração de caracteres
        char c1 = (char)('a' + (Math.abs(rand.nextInt()) % 26)); char c2 = (char)('a' + (Math.abs(rand.nextInt()) % 26)); // gera dois caracteres aleatórios entre 'a' e 'z'
        String nova = ""; // declaração da string como char que armazenará a nova string com os caracteres alterados
        for (int i = 0; i < str.length(); i++) { // loop para percorrer cada caractere da string
            if (str.charAt(i) == c1) nova += c2; // se o caractere for igual ao primeiro gerado, substitui por c2
            else nova += str.charAt(i); // se não, mantém o caractere original
        }
        return nova; // retorno do método contendo a string
    }

    public static void main (String[] args) { // main do programa
        String str = MyIO.readLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler strings e realizar a alteração enquanto a string seja diferente de "FIM"
            MyIO.println(new exercicio4().alteracao(str)); // imprime o resultado da alteração ao chamar o método
            str = MyIO.readLine(); // leitura da próxima string
        }
    }
}
