#include <stdio.h>
#include <stdbool.h>

bool isFim (char str[]) { // metódo para verificar se uma string é igual a "FIM"
    return (str[0] == 'F' && str[1] == 'I' && str[2] == 'M' && str[3] == '\0'); // retorna true ou false ao realizar a comparação
}

bool vogaisRec (char str[], int i) { // método para verificar se a string recebida é formada apenas por vogais
    if (str[i] == '\0') return true; // condição de parada da recursão, quando atingiu o fim da string
    char c = str[i]; // atribuição do caractere atual da string a um char para facilitar as comparações
    if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) return false; // verificação individual para validar somente letras, retornando falso caso seja símbolo ou número
    if (c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u' && c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U') return false; // mais uma verificação para validar se o caractere é diferente de uma vogal, retornando falso
    else return vogaisRec(str, i + 1); // se não cair em nenhuma condicional, chama a recursão passando a string e o próximo caractere
}

bool vogaisRecBase (char str[]) { // método para fazer a primeira chamada à recursão
    return vogaisRec(str, 0);
}

bool consoantesRec (char str[], int i) { // método para verificar se a string recebida é formada apenas por consoantes
    if (str[i] == '\0') return true; // condição de parada da recursão, quando atingiu o fim da string
    char c = str[i]; // atribuição do caractere atual da string a um char para facilitar as comparações
    if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) return false; // verificação individual para validar somente letras, retornando falso caso seja símbolo ou número
    if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') return false; // mais uma verificação para validar se o caractere é uma vogal, retornando falso
    else return consoantesRec(str, i + 1); // se não cair em nenhuma condicional, chama a recursão passando a string e o próximo caractere
}

bool consoantesRecBase (char str[]) { // método para fazer a primeira chamada à recursão
    return consoantesRec(str, 0);
}

bool inteirosRec (char str[], int i) { // método para verificar se a string recebida é um número inteiro
    if (str[i] == '\0') return true; // condição de parada da recursão, quando atingiu o fim da string
    if (str[i] < '0' || str[i] > '9') return false; // verificação individual de cada caractere, retornando falso se o caractere for diferente de um número
    else return inteirosRec(str, i + 1); // se não cair em nenhuma condiciona, chama a recursão passando a string e o próximo caractere
}

bool inteirosRecBase(char str[]) { // método para fazer a primeira chamada à recursão
    return inteirosRec(str, 0);
}

bool reaisRec (char str[], int i, int virgula) { // método para verificar se a string recebida é um número real
    if (str[i] == '\0') return true; // condição de parada da recursão, quando atingiu o fim da string
    if (str[i] == '.' || str[i] == ',') { // condição para verificar se o caractere é um ponto ou uma vírgula
        if (virgula >= 1) return false; // se a quantidade de vírgula for maior ou igual a 1, retorna falso
        return reaisRec(str, i + 1, virgula + 1); // se não, chama a recursão para o próximo caractere e passando a quantidade de vírgulas
    }
    if (str[i] < '0' || str[i] > '9') return false; // mais uma verificação para validar somente números, retornando falso caso seja símbolo ou letra (com exceção do '.' e ',', já verificados anteriormente)
    return reaisRec(str, i + 1, virgula); // se não cair nos testes do método, chama novamente a recursão passando o próximo caractere
}

bool reaisRecBase (char str[]) { // método para fazer a primeira chamada à recursão
    return reaisRec(str, 0, 0);
}

int main() { // main do programa
    char str[500]; scanf(" %[^\n]", str); // declaração e leitura da string
    while (!isFim(str)) { // loop para ler strings e verificar seus métodos enquanto a string seja diferente de "FIM"
        printf("%s ", vogaisRecBase(str) ? "SIM" : "NAO");
        printf("%s ", consoantesRecBase(str) ? "SIM" : "NAO");
        printf("%s ", inteirosRecBase(str) ? "SIM" : "NAO");
        printf("%s\n", reaisRecBase(str) ? "SIM" : "NAO"); // saídas utilizando operador ternário, SIM para retorno verdadeiro da função, e NAO para falso
        scanf(" %[^\n]", str); // leitura da próxima string
    }
    return 0;
}
