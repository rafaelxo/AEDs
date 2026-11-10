#include <stdio.h>
#include <stdbool.h>

bool isFim (char str[]) { // método auxiliar para verificar se a string é igual a "FIM"
    return (str[0] == 'F' && str[1] == 'I' && str[2] == 'M' && str[3] == '\0'); // retorna true ou false ao realizar a comparação
}

int length (char str[]) { // método para calcular o tamanho da string
    int i = 0; // contador para o tamanho da string
    while (str[i] != '\0') i++; // incrementa o contador até encontrar o fim da string
    return i; //retorno do tamanho da string
}

bool palindromoReal (char str[], int i, int j) { // método para verificar se a string é um palíndromo
    if (i >= j) return true; // condição de parada (quando todos os caracteres já forem verificados)
    else if (str[i] != str[j]) return false; // se a primeira letra for diferente da última, retorna falso
    else return palindromoReal(str, i + 1, j - 1); // chama o método recursivo com os índices incrementados e decrementados
}

int palindromoBase (char str[]) { // método base para o recursivo
    return palindromoReal(str, 0, length(str) - 1); // chama o método recursivo com os índices inicial e final da string
}

int main () { // main do programa
    char str[500]; scanf(" %[^\n]", str); //declaração e leitura da string
    while (!isFim(str)) { // loop para ler string e verificar se essa é um palíndromo enquanto a string seja diferente de "FIM"
        if (palindromoBase(str)) printf("SIM\n"); // se for palíndromo, imprime "SIM"
        else printf("NAO\n"); // se não, imprime "NAO"
        scanf(" %[^\n]", str); // leitura da próxima string
    }
    return 0;
}
