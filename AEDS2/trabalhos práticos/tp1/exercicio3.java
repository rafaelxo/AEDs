public class exercicio3 {
    public static boolean isFim (String str) { // método auxiliar para verificar se a string é igual a "FIM"
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M'); // retorna true ou false ao realizar a comparação
    }

    public static String ciframento (String str) { // método para o Ciframento de César
        String cod = ""; // declaração da string que armazenará o ciframento
        for (int i = 0; i < str.length(); i++) cod += (char)(str.charAt(i) + 3); // loop para percorrer cada caractere da string, realizando o ciframento ao somar 3 ao respectivo valor ASCII e concatenando ao resultado
        return cod; // retorno do método com a string cifrada
    }

    public static void main (String[] args) { // main do programa
        String str = MyIO.readLine(); // declaração e leitura da string
        while (!isFim(str)) { // loop para ler strings e verifiar se essa é um palíndromo enquanto a string seja diferente de "FIM"
            MyIO.println(ciframento(str)); // imprime o resultado do ciframento ao chamar o método
            str = MyIO.readLine(); // leitura da próxima string
        }
    }
}
