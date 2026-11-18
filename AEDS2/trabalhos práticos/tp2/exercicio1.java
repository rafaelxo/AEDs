import java.util.*;

public class exercicio1 {
    public static Scanner sc = new Scanner (System.in);
    
    public static String trim (String str, boolean[] vals) { // método para retirar espaços e substituir variáveis pelos seus valores
        String nova = ""; // string que receberá a nova expressão
        for (int i = 0; i < str.length(); i++) { // loop para percorrer a string original
            if (str.charAt(i) != ' ') { // se o caracter não for espaço
                if (str.charAt(i) > 64 && str.charAt(i) < 91) nova += vals[str.charAt(i) - 65] ? "1" : "0"; // se for variável, substitui pelo valor correspondente
                else nova += str.charAt(i); // se não, mantém o caracter
            }
        }
        return nova; // retorna a string tratada
    }

    public static String operadores (String str, int i) { // método para extrair o operador da expressão (and, or, not)
        String temp = ""; // string temporária para armazenar o operador
        do {
            i--;
            temp += str.charAt(i);
        } while (i > 0 && str.charAt(i - 1) > 96 && str.charAt(i - 1) < 123); // loop para percorrer a string até encontrar um caracter que não seja letra minúscula (iniciador do operador)
        String oper = ""; // string que armazenará o operador na ordem correta
        for (int j = temp.length() - 1; j >= 0; j--) oper += temp.charAt(j); // loop para inverter a string temporária e armazenar na string do operador
        return oper; // retorno do operador
    }

    public static boolean[] parametros (String str, int i) { // método para extrair os parâmetros da expressão (valores booleanos)
        String temp = ""; // string temporária para armazenar os parâmetros
        while (i < str.length() - 1 && str.charAt(i + 1) != ')') { // loop para percorrer a string até encontrar o fechamento do parêntese
            i++;
            if (str.charAt(i) == '0' || str.charAt(i) == '1') temp += str.charAt(i); // se o caracter for 0 ou 1, adiciona na string temporária
        }
        boolean[] valores = new boolean[temp.length()]; // vetor que armazenará os valores booleanos
        for (int j = 0; j < temp.length(); j++) valores[j] = temp.charAt(j) == '1'; // loop para converter os caracteres em booleanos e armazenar no vetor
        return valores; // retorno do vetor de valores booleanos
    }

    public static String substituicao (String str, int i, String att) { // método para substituir a expressão pelo resultado
        String nova = ""; // string que armazenará a nova expressão
        for (int j = 0; j < i; j++) nova += str.charAt(j); // loop para copiar a parte da string antes do operador
        for (int j = i; j < str.length(); j++) { // loop para percorrer a string a partir do operador
            if (str.charAt(j) == ')') { // se encontrar o fechamento do parêntese
                nova += att; // adiciona o resultado da expressão
                j++; // avança o índice para copiar o fechamento do parêntese
                while (j < str.length()) { // loop para copiar o restante da string após o fechamento do parêntese
                    nova += str.charAt(j); // copia o caracter
                    j++; // avança o índice
                }
            }
        }
        return nova; // retorno da nova expressão
    }

    public static boolean resultado (String str, boolean[] vals) { // método para calcular o resultado da expressão
        boolean result = false; // variável que armazenará o resultado
        switch (str) { // switch para identificar o operador e calcular o resultado
            case "not": // se for not, inverte o valor o único parâmetro
                result = !vals[0];
                break;
            case "or": // se for or, verifica se algum dos parâmetros é true
                for (int i = 0; i < vals.length; i++) { // loop para percorrer os valores booleanos
                    if (vals[i]) { // se encontrar um valor true, a resposta é verdadeiro
                        result = true;
                        i = vals.length;
                    }
                }
                break;
            case "and": // se for and, verifica se todos os parâmetros são true
                result = true;
                for (int i = 0; i < vals.length; i++) { // loop para percorrer os valores booleanos
                    if (!vals[i]) { // se encontrar um valor false, a resposta é falso
                        result = false;
                        i = vals.length;
                    }
                }
                break;
        }
        return result; // retorno do resultado
    }

    public static void main (String[] args) { // main do programa
        int n = sc.nextInt(); // declaração e leitura da quantidade de bits
        while (n != 0) { // loop para testar o resultado de cada expressão enquanto n for maior que 0
            boolean[] vals = new boolean[n]; // declaração do vetor de valores booleanos
            for (int i = 0; i < n; i++) vals[i] = sc.nextInt() != 0; // leitura dos valores booleanos
            String str = sc.nextLine(); // lê a linha com os valores booleanos
            String expr = trim(str, vals); // tratamento da expressão (remoção de espaços e substituição de variáveis pelos seus valores)
            for (int i = expr.length() - 1; i > 0; i--) { // loop para percorrer a expressão de trás para frente
                if (expr.charAt(i) == '(') { // se encontrar um parêntese de abertura
                    String oper = operadores(expr, i); // chama o método para extrair o operador
                    boolean[] param = parametros(expr, i); // chama o método para extrair os parâmetros
                    i -= oper.length(); // ajusta o índice para a posição do operador
                    expr = substituicao(expr, i, resultado(oper, param) ? "1" : "0"); // chama o método para substituir a expressão pelo resultado, retornando 1 ou 0
                }
            }
            System.out.println(expr); // saída do resultado final da expressão
            n = sc.nextInt(); // leitura da próxima quantidade de bits
        }
    }
}
