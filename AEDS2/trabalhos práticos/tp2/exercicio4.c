#include <stdio.h>
#include <bool.h>

bool isFim (char str[]) {
    return (str[0] == 'F' && str[1] == 'I' && str[2] == 'M' && str[3] == '\0');
}

int somaDigitos (char n[], int i) { // método recursivo para somar cada caracter do número inteiro
    if (n[i] == '\0') return 0; // condição de parada como '\0' qando a string acabar, retornando 0
    else return (n[i] - '0') + somaDigitos(n, i + 1); // soma o caracter convertido para inteiro e chama o método recursivo com o índice incrementado
}

int main () { // main do programa
    char n[100]; scanf("%s", n); // declaração e leitura do número como string
    while (!isFim(n)) { // loop para ler números como string enquanto a string seja diferente de "FIM"
        printf("%d\n", somaDigitos(n, 0)); // saída do resultado da soma dos caracteres
        scanf("%s", n); // leitura do próximo número como string
    }
    return 0;
}
