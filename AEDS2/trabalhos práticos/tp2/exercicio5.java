public class exercicio5 {
    public static boolean isFim(String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public static String trim (String str) { // método para remover o separador "-" e espaços em branco de uma string
        String nova = ""; // declaração de uma nova string para armazenar a nova string
        for (int i = 0; i < str.length(); i++) { // loop para percorrer todos os caracteres da string
            if (str.charAt(i) != '-' && str.charAt(i) != ' ') nova += str.charAt(i); // se o caractere não for "-", concatena o caractere à nova string
        }
        return nova; // retorna a nova string sem espaços e o separador "-"
    }

    public static String minusculo (String str) { // método para converter todos os caracteres de uma string para minúsculo
        String nova = ""; // declaração de uma nova string para armazenar a string em minúsculo
        for (int i = 0; i < str.length(); i++) { // loop para percorrer todos os caracteres da string
            if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') nova += (char)(str.charAt(i) + 32); // se o caractere for maiúsculo, converte o caractere para minúsculo
            else nova += str.charAt(i); // se não, mantém o caractere
        }
        return nova; // retorna a nova string em minúsculo
    }

    public static String swap (String str, int i, int j) { // método para trocar dois caracteres de posição em um array
        String nova = ""; // declaração de uma nova string para armazenar a string com os caracteres trocados
        for (int k = 0; k < str.length(); k++) { // loop para percorrer todos os caracteres da string
            if (k == i) nova += str.charAt(j); // se o índice for igual a i, concatena o caractere na posição j
            else if (k == j) nova += str.charAt(i); // se o índice for igual a j, concatena o caractere na posição i
            else nova += str.charAt(k); // se não, mantém o caractere
        }
        return nova; // retorna a nova string com os caracteres trocados
    }

    public static String ordenar (String str) { // método para ordenar a string em ordem alfabética para assim facilitar a comparação
        String nova = str; // declaração de uma nova string para armazenar a string ordenada
        for (int i = 0; i < nova.length() - 1; i++) { // loop até o penúltimo caractere
            for (int j = 0; j < nova.length() - i - 1; j++) { // loop até o penúltimo caractere menos o número de iterações já feitas
                if (nova.charAt(j) > nova.charAt(j + 1)) nova = swap(nova, j, j + 1); // se o caractere na posição j for maior que o próximo, chama o método para trocar os caracteres de posição
            }
        }
        return nova; // retorna a string ordenada
    }

    public static String[] separar (String str) {
        String str1 = ""; String str2 = ""; // declaração de duas novas strings para armazenar as duas metades da string original
        boolean separou = false; // variável para indicar se as strings foram separadas
        int i = 0;
        while (i < str.length()) { // loop para percorrer todos os caracteres da string
            if (!separou && i + 2 < str.length() && str.charAt(i) == ' ' && str.charAt(i + 1) == '-' && str.charAt(i + 2) == ' ') { // se ainda não foram separadas e encontrar o separador "-"
                separou = true; // indica que as strings foram separadas
                i += 3; // pula o separador
            } else { // se não, concatena o caractere à string correspondente
                if (!separou) str1 += str.charAt(i); // se ainda não foram separadas, concatena o caractere à primeira string
                else str2 += str.charAt(i); // se já foram separadas, concatena o caractere à segunda string
                i++; // incrementa o índice
            }
        }
        String[] resp = new String[2]; // declaração de um array para armazenar as duas strings
        resp[0] = str1; resp[1] = str2; // atribuição das strings ao array
        return resp; // retorna o array com as duas strings
    }

    public static boolean anagrama (String str1, String str2) { // método para verificar se é anagrama
        if (str1.length() != str2.length()) return false; // se o tamanho das strings forem diferentes, já retorna falso
        str1 = ordenar(str1); str2 = ordenar(str2); // chama o método para ordenar as strings
        for (int i = 0; i < str1.length(); i++) { // loop para percorrer todos os caracteres da string (que devem ser iguais)
            if (str1.charAt(i) != str2.charAt(i)) return false; // se algum caractere for diferente, retorna falso
        }
        return true; // se todos os caracteres forem iguais, retorna verdadeiro
    }

    public static void main (String[] args) { // main do programa
        String str = MyIO.readLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler duas strings e verifiar se essas são anagramas enquanto a string seja diferente de "FIM"
            String[] strs = separar(str); // declara uma array de strings e chama o método para separar em duas
            String str1 = trim(minusculo(strs[0])); String str2 = trim(minusculo(strs[1])); // chama os métodos para remover os separadores e espaços de cada string e convertê-las para minúsculo
            if (anagrama(str1, str2)) MyIO.println("SIM"); // chamada do método e validação e se forem anagramas, imprime "SIM"
            else MyIO.println("NÃO"); // se não, imprime "NAO"
            str = MyIO.readLine(); // leitura da próxima string
        }
    }
}
