#include <stdio.h>
#include <stdbool.h>

bool isFim(char str[]) { // metódo para verificar se uma string é igual a "FIM"
    return (str[0] == 'F' && str[1] == 'I' && str[2] == 'M' && str[3] == '\0'); // retorna true ou false ao realizar a comparação
}

bool vogais (char str[]) { // método para verificar se a string recebida é formada apenas por vogais
    int i = 0;
    while (str[i] != '\0') { // loop para verificação até que atinja a condição de parada da string
        char c = str[i]; // atribuição de cada caracter para realizar a comparação a cada posição da string
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) return false; // verificação individual para validar somente letras, retornando falso caso seja símbolo ou número
        if (c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u' && c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U') return false; // mais uma verificação para validar se o caracter é diferente de uma vogal, retornando false
        i++;
    }
    return true;
}

bool consoantes (char str[]) { // método para verificar se a string recebida é formada apenas por consoantes
    int i = 0;
    while (str[i] != '\0') { // loop para verificação até que atinja a condição de parada da string
        char c = str[i]; //atribuição de cada caracter para realizar a comparação a cada posição da string
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) return false; // verificação individual para validar somente letras, retornando falso caso seja símbolo ou número
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') return false; // mais uma verificação para validar se o caracter é uma vogal, retornando false
        i++;
    }
    return true; // se não cair no retorno da estrutura condicional, retorna true
}

bool inteiros (char str[]) { // método para verificar se a string recebida é um número inteiro
    int i = 0;
    while (str[i] != '\0') { // loop para verificação até que atinja a condição de parada da string
        if (str[i] < '0' || str[i] > '9') return false; // verificação individual de cada caracter, comparando-o com numeros inteiros e retornando false caso a condição for verdadeira
        i++;
    }
    return true; // se não cair no retorno da estrutura condicional, retorna true
}

bool reais (char str[]) { // método para verificar se a string recebida é um número real
    int i = 0, virgula = 0; // declaração de variável para contar a quantidade de vírgulas na string
    while (str[i] != '\0') { // loop para verificação até que atinja a condição de parada da string
        if (str[i] == '.' || str[i] == ',') { // verificação individual para validar a vírgula de um número real
            virgula++;
            if (virgula > 1) return false; // se houver mais de uma vírgula, já retorna falso
        }
        else if (str[i] < '0' || str[i] > '9') return false; // mais uma verificação para validar somente números, retornando falso caso seja símbolo ou letra (com exceção do '.' e ',', já verificados anteriormente)
        i++;
    }
    return true; // retorno true caso não caia em nenhuma das condições de retorno false
}

int main () { // main do programa
    char str[500]; scanf(" %[^\n]", str); // declaração e leitura da string
    while (!isFim(str)) { // loop para ler strings e verificar seus métodos enquanto a string seja diferente de "FIM"
        printf("%s ", vogais(str) ? "SIM" : "NAO");
        printf("%s ", consoantes(str) ? "SIM" : "NAO");
        printf("%s ", inteiros(str) ? "SIM" : "NAO");
        printf("%s\n", reais(str) ? "SIM" : "NAO");
        scanf(" %[^\n]", str); // leitura da próxima string
    }
    return 0;
}
