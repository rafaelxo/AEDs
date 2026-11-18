#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

typedef struct Data { // estrutura para armazenar a data com dia, mês e ano
    int dia, mes, ano;
} Data;

void setData (Data *data, char *str) { // método para setar a data a partir de uma string
    data->dia = 1;
    data->mes = 1;
    data->ano = 0; // inicializa a data com valores padrão

    if (!str || strlen(str) < 8) return; // se a string for nula ou vazia, mantém valores padrão

    char meses[12][4] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // array com os meses abreviados

    char mes[4] = {0}; // array para armazenar o mês
    int i = 0; // índice para percorrer a string de origem
    for (i = 0; i < 3 && i < (int)strlen(str); i++) mes[i] = str[i]; // copia os três primeiros caracteres (mês)
    mes[3] = '\0'; // finaliza a string do mês

    for (int j = 0; j < 12; j++) { // loop para encontrar o número do mês correspondente
        if (strcmp(mes, meses[j]) == 0) { // se encontrar o mês
            data->mes = j + 1; // atribui o número do mês (1-12)
            break;
        }
    }

    char dia[3] = "01"; // array para armazenar o dia
    char ano[5] = {0}; // array para armazenar o ano
    if (strlen(str) == 8 && str[3] == ' ') { // se o formato for "Mmm d yyyy"
        strncpy(ano, str + 4, 4);
        ano[4] = '\0';
    } else if (str[5] == ',') { // se o formato for "Mmm d, yyyy"
        dia[0] = '0';
        dia[1] = str[4]; // copia o dia com zero à esquerda
        dia[2] = '\0';
        strncpy(ano, str + 7, 4); // copia os 4 caracteres do ano
        ano[4] = '\0';
    } else { // se o formato for "Mmm dd, yyyy"
        dia[0] = str[4];
        dia[1] = str[5]; // copia o dia
        dia[2] = '\0';
        strncpy(ano, str + 8, 4); // copia os 4 caracteres do ano
        ano[4] = '\0';
    }

    data->dia = atoi(dia); // converte string dia para inteiro
    data->ano = atoi(ano); // converte string ano para inteiro
}

typedef struct Game { // estrutura para armazenar os dados do jogo
    int id, estimatedOwners, metacriticScore, achievements;
    char name[200];
    struct Data releaseDate;
    float price, userScore;
    char supportedLanguages[50][50], publishers[50][50], developers[50][50], categories[50][50], genres[50][50], tags[50][50];
} Game;

void inicializar (Game *g) { // método para inicializar os campos do jogo (construtor)
    g->id = 0;
    g->name[0] = '\0';
    g->releaseDate.dia = 1;
    g->releaseDate.mes = 1;
    g->releaseDate.ano = 1;
    g->estimatedOwners = 0;
    g->price = 0.0f;
    g->metacriticScore = -1;
    g->userScore = -1.0f;
    g->achievements = 0;
    for (int i = 0; i < 50; i++) {
        g->supportedLanguages[i][0] = '\0';
        g->publishers[i][0] = '\0';
        g->developers[i][0] = '\0';
        g->categories[i][0] = '\0';
        g->genres[i][0] = '\0';
        g->tags[i][0] = '\0';
    }
}

typedef struct Celula { // estrutura para célula da pilha encadeada
    struct Game *game; // ponteiro para o jogo
    struct Celula *prox; // ponteiro para a próxima célula
} Celula;

Celula *newCelula (Game *x) { // método para criar uma nova célula
    Celula *nova = (Celula*)malloc(sizeof(Celula)); // aloca memória para a nova célula
    nova->game = x; // atribui o jogo à célula
    nova->prox = NULL; // inicializa o próximo como NULL
    return nova; // retorna a nova célula
}

Celula *topo; // ponteiros para o topo da pilha

void Pilha () { // método para inicializar a pilha
    topo = NULL; // inicializa o topo como NULL
}

int tamanho () { // método para obter o tamanho da pilha
    int tamanho = 0; // inicializa o contador de tamanho
    for (Celula *i = topo; i != NULL; i = i->prox) tamanho++; // percorre a pilha contando as células
    return tamanho; // retorna o tamanho total
}

void inserir (Game *x) { // método para inserir no fim da pilha
    Celula *tmp = newCelula(x); // cria uma nova célula com o jogo
    tmp->prox = topo; // aponta o próximo da nova célula para o topo
    topo = tmp; // atualiza o topo para a nova célula
    tmp = NULL; // anula a variável temporária
}

Game *remover () { // método para remover de uma posição específica
    if (topo == NULL) exit(1); // verifica se a pilha está vazia
    Celula *tmp = topo; // guarda a célula a ser removida
    Game *resp = tmp->game; // obtém o jogo da célula a ser removida
    topo = topo->prox; // atualiza o topo para a próxima célula
    tmp->prox = NULL; // desvincula a célula removida
    free(tmp); // libera a célula removida
    tmp = NULL; // anula a variável temporária
    return resp; // retorna o jogo removido
}

void mostrarInv (Celula *i, int j) { // método para mostrar a pilha de forma inversa
    if (i != NULL) { // se a célula não for nula
        mostrarInv(i->prox, j - 1); // chamada recursiva para a próxima célula
        printf("[%d] ", j); // imprime o índice
        mostrar(i->game); // mostra os dados do jogo
    }
}

bool espaco (char c) { // método para verificar se um caractere é espaço em branco
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == '\v'; // verifica os tipos de espaço em branco
}

void trim (char *str) { // método para remover espaços em branco do início e fim da string
    if (str == NULL) return; // se a string for nula, retorna

    int ini = 0, fim = (int)strlen(str) - 1; // índices para o início e fim da string
    while (ini <= fim && espaco(str[ini])) ini++; // remove espaços em branco do início
    while (fim >= ini && espaco(str[fim])) fim--; // remove espaços em branco do fim

    int k = 0; // índice para a nova posição na string
    for (int t = ini; t <= fim; t++) str[k++] = str[t]; // desloca os caracteres para o início
    str[k] = '\0'; // finaliza a string removendo os espaços em branco do fim
}

void limparAspas (char *str) { // método para remover aspas do início e fim da string
    if (!str) return; // se a string for nula, retorna

    trim(str); // remove espaços em branco do início e fim
    int n = (int)strlen(str); // obtém o tamanho da string
    if (n >= 2) { // se a string tiver pelo menos 2 caracteres
        char ini = str[0], fim = str[n - 1]; // obtém o primeiro e o último caractere
        if ((ini == '\"' && fim == '\"') || (ini == '\'' && fim == '\'')) { // se ambos forem aspas simples ou duplas
            for (int i = 1; i < n - 1; i++) str[i - 1] = str[i]; // desloca os caracteres para a esquerda
            str[n - 2] = '\0'; // finaliza a string removendo o último caractere
        }
    }

    trim(str); // remove espaços em branco novamente
}

int inteiro (char *str) { // método para converter string em int
    if (!str) return 0; // se a string for nula, retorna 0

    int i = 0, fim = (int)strlen(str) - 1; // inicializa os índices para percorrer a string
    while (i <= fim && espaco(str[i])) i++; // remove espaços em branco do início
    while (fim >= i && espaco(str[fim])) fim--; // remove espaços em branco do fim

    int num = 0; // inicializa o número
    bool achou = false, negat = false; // flags para indicar se encontrou dígitos e se é negativo
    if (i <= fim && str[i] == '-') { // verifica se o número é negativo
        negat = true; // marca como negativo
        i++;
    }

    for (; i <= fim; i++) { // percorre cada caractere da string
        char c = str[i];
        if (c >= '0' && c <= '9') { // se for um dígito
            num = num * 10 + (c - '0'); // acumula o número
            achou = true; // marca que encontrou um dígito
        }
    }

    return achou ? (negat ? -num : num) : 0; // retorna o número final, considerando o sinal
}

float real (char *str) { // método para converter string em float
    if (!str) return 0.0f; // se a string for nula, retorna 0.0

    int i = 0, fim = (int)strlen(str) - 1; // inicializa os índices para percorrer a string
    while (i <= fim && espaco(str[i])) i++; // remove espaços em branco do início
    while (fim >= i && espaco(str[fim])) fim--; // remove espaços em branco do fim

    float num = 0.0f, div = 1.0f; // inicializa o número e o divisor para a parte decimal
    bool achou = false, negat = false, decimal = false; // flags para indicar se encontrou dígitos, se é negativo e se está na parte decimal
    if (i <= fim && str[i] == '-') { // verifica se o número é negativo
        negat = true; // marca como negativo
        i++;
    }

    for (; i <= fim; i++) { // percorre cada caractere da string
        char c = str[i];
        if (c == '.') decimal = true; // se encontrar um ponto, marca que está na parte decimal
        else if (c >= '0' && c <= '9') { // se for um dígito
            achou = true; // marca que encontrou um dígito
            if (!decimal) num = num * 10.0f + (float)(c - '0'); // se não estiver na parte decimal, acumula o número inteiro
            else { // se estiver na parte decimal
                div *= 10.0f; // aumenta o divisor
                num = num + (float)(c - '0') / div; // acumula a parte decimal
            }
        }
    }

    return achou ? (negat ? -num : num) : 0.0f; // retorna o número final, considerando o sinal
}

int separaCampos (char *str, char campos[][1000]) { // método para separar os campos de uma linha CSV
    int n = 0, x = 0; // n é o contador de campos, x é o índice dentro do campo atual
    bool aspas = false; // flag para indicar se estamos dentro de aspas
    for (int i = 0; str[i] != '\0'; i++) { // percorrer cada caractere da string
        char c = str[i];
        if (c == '\"') aspas = !aspas; // alternar a flag de aspas ao encontrar uma aspa
        else if (c == ',' && !aspas) { // se encontrar uma vírgula fora de aspas, é o fim de um campo
            if (n < 15) { // se não exceder o máximo de campos
                campos[n][x] = '\0'; // finalizar a string do campo atual
                limparAspas(campos[n]); // remover aspas do campo
                n++; x = 0;
            }
        }
        else { // caso contrário, adicionar o caractere ao campo atual
            if (n < 15 && x < 999) campos[n][x++] = c; // garantir que não exceda o tamanho do campo
        }
    }

    if (n < 15) { // adicionar o último campo se houver caracteres
        campos[n][x] = '\0'; // finalizar a string do campo atual
        limparAspas(campos[n]); // remover aspas do campo
        n++; x = 0;
    }

    return n; // retornar o número de campos separados
}

int separaArray (char *str, char dest[][50]) { // método para separar os elementos de um array representado como string
    int n = 0, x = 0; // n é o contador de elementos, x é o índice dentro do elemento atual
    bool aspas = false; // flag para indicar se estamos dentro de aspas
    for (int i = 0; str[i] != '\0'; i++) { // percorrer cada caractere da string
        char c = str[i];
        if (c == '\"') aspas = !aspas; // alternar a flag de aspas ao encontrar uma aspa
        else if (c == ',' && !aspas) { // se encontrar uma vírgula fora de aspas, é o fim de um elemento
            if (x > 0 && n < 50) { // se houver caracteres no elemento atual e não exceder o máximo
                dest[n][x] = '\0'; // finalizar a string do elemento atual
                trim(dest[n]); // remover espaços em branco do início e fim
                limparAspas(dest[n]); // remover aspas do elemento
                n++; x = 0;
            }
            else x = 0; // se não houver caracteres, apenas resetar o índice
        }
        else if (c == '[' || c == ']') { // ignorar colchetes
        } else { // caso contrário, adicionar o caractere ao elemento atual
            if (n < 50 && x < 49) dest[n][x++] = c; // garantir que não exceda o tamanho do array
        }
    }

    if (x > 0 && n < 50) { // adicionar o último elemento se houver caracteres
        dest[n][x] = '\0'; // finalizar a string do elemento atual
        trim(dest[n]); // remover espaços em branco do início e fim
        limparAspas(dest[n]); // remover aspas do elemento
        n++; x = 0;
    }
    return n; // retornar o número de elementos separados
}

void processarLinha (char *linha, Game *game) { // método para processar a linha lida do arquivo
    inicializar(game); // inicializar os campos do jogo
    char campos[15][1000]; // array para armazenar os campos separados
    int ncampos = separaCampos(linha, campos); // separar os campos da linha

    if (ncampos > 0) game->id = inteiro(campos[0]); // converter o primeiro campo para inteiro e armazenar no id

    if (ncampos > 1) { // processar o segundo campo para nome
        strcpy(game->name, campos[1]); // copiar o segundo campo para o nome
        trim(game->name);
    }

    if (ncampos > 2) { // processar o terceiro campo para data de lançamento
        setData(&game->releaseDate, campos[2]); // copiar o terceiro campo para a data de lançamento
    }

    if (ncampos > 3) { // processar o quarto campo para estimativa de donos
        char nums[100]; // array para armazenar apenas os dígitos
        int n = 0; // contador de dígitos
        for (int i = 0; campos[3][i] != '\0'; i++) { // percorrer cada caractere do campo
            char c = campos[3][i]; // obter o caractere atual
            if (c >= '0' && c <= '9') { // se for um dígito, armazenar no array de dígitos
                if (n < 99) nums[n++] = c; // evitar overflow
            }
        }
        nums[n] = '\0'; // finalizar a string de dígitos
        game->estimatedOwners = inteiro(nums); // converter a string de dígitos para inteiro e armazenar na estimativa de donos
    }

    if (ncampos > 4) { // processar o quinto campo para preço
        char preco[100]; // array para armazenar o preço
        strcpy(preco, campos[4]);
        trim(preco);
        if (strcmp(preco, "Free to Play") == 0 || strlen(preco) == 0) game->price = 0.0f; // se for "Free to Play" ou estiver vazio, o preço é 0.0
        else game->price = real(preco); // caso contrário, converter para float e armazenar no preço
    }

    if (ncampos > 5) { // processar o sexto campo para idiomas suportados
        separaArray(campos[5], game->supportedLanguages); // separar os idiomas e armazenar no array
        for (int i = 0; i < 50 && game->supportedLanguages[i][0] != '\0'; i++) limparAspas(game->supportedLanguages[i]); // remover aspas de cada idioma
    }

    if (ncampos > 6) { // processar o sétimo campo para pontuação do Metacritic
        char meta[100]; // array para armazenar a pontuação do Metacritic
        strcpy(meta, campos[6]);
        trim(meta);
        if (strlen(meta) == 0) game->metacriticScore = -1; // se estiver vazio, a pontuação é -1
        else game->metacriticScore = inteiro(meta); // caso contrário, converter para inteiro e armazenar na pontuação do Metacritic
    }

    if (ncampos > 7) { // processar o oitavo campo para pontuação do usuário
        char user[100]; // array para armazenar a pontuação do usuário
        strcpy(user, campos[7]);
        trim(user);
        if (strlen(user) == 0 || strcmp(user, "tbd") == 0) game->userScore = -1.0f; // se estiver vazio ou for "tbd", a pontuação é -1.0
        else game->userScore = real(user); // caso contrário, converter para float e armazenar na pontuação do usuário
    }

    if (ncampos > 8) { // processar o nono campo para conquistas
        char ach[100]; // array para armazenar as conquistas
        strcpy(ach, campos[8]); // copiar o campo para uma variável temporária
        trim(ach);
        if (strlen(ach) == 0) game->achievements = 0; // se estiver vazio, as conquistas são 0
        else game->achievements = inteiro(ach); // caso contrário, converter para inteiro e armazenar nas conquistas
    }

    if (ncampos > 9) separaArray(campos[9], game->publishers); // processar o décimo campo para editores
    if (ncampos > 10) separaArray(campos[10], game->developers); // processar o décimo primeiro campo para desenvolvedores
    if (ncampos > 11) separaArray(campos[11], game->categories); // processar o décimo segundo campo para categorias
    if (ncampos > 12) separaArray(campos[12], game->genres); // processar o décimo terceiro campo para gêneros
    if (ncampos > 13) separaArray(campos[13], game->tags); // processar o décimo quarto campo para tags
}

void formatarData (Data *data, char dest[]) { // método para formatar a data conforme o enunciado
    dest[0] = '\0'; // inicializa a string de destino como vazia

    if (data == NULL) { // se o ponteiro for nulo
        strcpy(dest, "01/01/0001"); // copia a data padrão
        return;
    }

    char diaNum[3] = "01"; // array para armazenar o número do dia
    if (data->dia >= 1 && data->dia <= 31) { // se o dia for válido
        if (data->dia < 10) { // se for menor que 10, adiciona zero à esquerda
            diaNum[0] = '0';
            diaNum[1] = '0' + data->dia;
            diaNum[2] = '\0';
        } else { // se for >= 10, converte normalmente
            diaNum[0] = '0' + (data->dia / 10);
            diaNum[1] = '0' + (data->dia % 10);
            diaNum[2] = '\0';
        }
    }

    char mesNum[3] = "01"; // array para armazenar o número do mês
    if (data->mes >= 1 && data->mes <= 12) { // se o mês for válido
        if (data->mes < 10) { // se for menor que 10, adiciona zero à esquerda
            mesNum[0] = '0';
            mesNum[1] = '0' + data->mes;
            mesNum[2] = '\0';
        } else { // se for >= 10, converte normalmente
            mesNum[0] = '0' + (data->mes / 10);
            mesNum[1] = '0' + (data->mes % 10);
            mesNum[2] = '\0';
        }
    }

    char ano[5] = "0001"; // array para armazenar o ano
    if (data->ano >= 1000 && data->ano <= 9999) { // se o ano for válido (4 dígitos)
        ano[0] = '0' + (data->ano / 1000);
        ano[1] = '0' + ((data->ano / 100) % 10);
        ano[2] = '0' + ((data->ano / 10) % 10);
        ano[3] = '0' + (data->ano % 10);
        ano[4] = '\0';
    }

    strcat(dest, diaNum);
    strcat(dest, "/");
    strcat(dest, mesNum);
    strcat(dest, "/");
    strcat(dest, ano); // concatena dia, mês e ano no formato dd/mm/aaaa
}

void formatarPreco (float preco) { // método para formatar o preço conforme o enunciado
    if (preco == 0.0f) printf("0.0"); // se o preço for 0.0, imprime "0.0"
    else if (preco == (int) preco) printf("%d", (int) preco); // se o preço for um número inteiro, imprime como inteiro
    else if (((int) (preco * 10)) == (preco * 10)) printf("%.1f", preco); // se o preço tiver uma casa decimal, imprime com uma casa decimal
    else printf("%.2f", preco); // caso contrário, imprime com duas casas decimais
}

void mostrar (Game *g) { // método para mostrar os dados do jogo para cada atributo e suas devidas formatações
    char data[11];
    formatarData(&g->releaseDate, data);

    printf("=> %d ## %s ## %s ## %d ## ", g->id, g->name, data, g->estimatedOwners); // imprimir id, nome, data formatada e estimativa de donos
    formatarPreco(g->price); // imprimir o preço formatado
    printf(" ## [");

    for (int i = 0; i < 50 && g->supportedLanguages[i][0] != '\0'; i++) { // imprimir os idiomas suportados
        if (i > 0) printf(", ");
        printf("%s", g->supportedLanguages[i]);
    }

    printf("] ## %d ## %.1f ## %d ## [", g->metacriticScore, g->userScore, g->achievements); // imprimir pontuação do Metacritic, pontuação do usuário e conquistas

    for (int i = 0; i < 50 && g->publishers[i][0] != '\0'; i++) { // imprimir os editores
        if (i > 0) printf(", ");
        printf("%s", g->publishers[i]);
    }
    printf("] ## [");

    for (int i = 0; i < 50 && g->developers[i][0] != '\0'; i++) { // imprimir os desenvolvedores
        if (i > 0) printf(", ");
        printf("%s", g->developers[i]);
    }
    printf("] ## [");

    for (int i = 0; i < 50 && g->categories[i][0] != '\0'; i++) { // imprimir as categorias
        if (i > 0) printf(", ");
        printf("%s", g->categories[i]);
    }
    printf("] ## [");

    for (int i = 0; i < 50 && g->genres[i][0] != '\0'; i++) { // imprimir os gêneros
        if (i > 0) printf(", ");
        printf("%s", g->genres[i]);
    }
    printf("] ## [");

    for (int i = 0; i < 50 && g->tags[i][0] != '\0'; i++) { // imprimir as tags
        if (i > 0) printf(", ");
        printf("%s", g->tags[i]);
    }
    printf("] ##\n"); // finalizar a linha
}

int separarComando (char *info, char partes[2][10]) { // método para separar o comando em partes
    if (info == NULL) return 0; // se a informação for nulo, retorna 0

    int n = 0; // contador de partes
    int i = 0;
    int len = (int)strlen(info);
    while (i < len) {
        while (i < len && info[i] == ' ') i++; // pular espaços em branco
        int j = 0;
        while (i < len && info[i] != ' ' && j < 9) partes[n][j++] = info[i++]; // copiar caracteres para a parte atual
        partes[n][j] = '\0'; // finalizar a string da parte atual
        n++; // incrementar o contador de partes
    }

    return n; // retornar o número de partes separadas
}

int main() { // main do programa
    Pilha(); // inicializa a pilha encadeada
    char entrada[10]; scanf("%s", entrada); // declaração e leitura da entrada
    while (strcmp(entrada, "FIM") != 0) { // enquanto a entrada não for "FIM"
        int cod = inteiro(entrada); // converte a entrada para inteiro
        FILE *arq = fopen("/tmp/games.csv", "r"); // abertura do arquivo para leitura

        char linha[2000]; // buffer para ler cada linha do arquivo
        fgets(linha, sizeof(linha), arq); // ler a primeira linha (cabeçalho) e ignorar

        bool encontrado = false; // variável para controlar se o jogo foi encontrado
        while (!encontrado && fgets(linha, sizeof(linha), arq) != NULL) { // ler cada linha do arquivo
            int len = (int)strlen(linha); // obter o tamanho da linha lida
            if (len > 0 && linha[len - 1] == '\n') linha[len - 1] = '\0'; // remove o caractere de nova linha, se presente

            int iLin = 0, iId = 0; // índice para percorrer as linhas
            char idGame[10]; // buffer para armazenar o id como string
            while (linha[iLin] != '\0' && linha[iLin] != ',' && iId < 9) idGame[iId++] = linha[iLin++]; // extrair o id
            idGame[iId] = '\0'; // finalizar a string do id montado

            if (cod == inteiro(idGame)) { // se o id da linha for igual ao código procurado
                Game *g = (Game*)malloc(sizeof(Game)); // criar uma variável do tipo Game
                processarLinha(linha, g); // processar a linha e preencher os dados do jogo
                inserir(g); // inserir o jogo na pilha encadeada
                encontrado = true; // marcar como encontrado e sair do loop
            }
        }
        fclose(arq);

        scanf("%s", entrada); // lê a próxima entrada
    }

    int n; scanf("%d", &n); // lê o número de operações a serem realizadas
    getchar(); // consumir o caractere de nova linha após o número
    for (int i = 0; i < n; i++) { // loop para processar cada operação
        char info[20]; // buffer para armazenar a linha de comando
        fgets(info, sizeof(info), stdin); // lê a linha de comando
        int len = (int)strlen(info); // obtém o tamanho da linha lida
        if (len > 0 && info[len - 1] == '\n') info[len - 1] = '\0';  // remove o caractere de nova linha, se presente

        char partes[2][10]; // array para armazenar as partes do comando
        separarComando(info, partes); // separa o comando em partes

        if (strcmp(partes[0], "I") == 0) { // se o comando for inserir no início
            FILE *arq = fopen("/tmp/games.csv", "r"); // abertura do arquivo para leitura
            int cod = inteiro(partes[1]); // obtém o código do jogo

            char linha[2000]; // buffer para ler cada linha do arquivo
            fgets(linha, sizeof(linha), arq); // ler a primeira linha (cabeçalho) e ignorar

            bool encontrado = false; // variável para controlar se o jogo foi encontrado
            while (!encontrado && fgets(linha, sizeof(linha), arq) != NULL) { // ler cada linha do arquivo
                int len = (int)strlen(linha); // obter o tamanho da linha lida
                if (len > 0 && linha[len - 1] == '\n') linha[len - 1] = '\0'; // remove o caractere de nova linha, se presente

                int iLin = 0, iId = 0; // índice para percorrer as linhas
                char idGame[10]; // buffer para armazenar o id como string
                while (linha[iLin] != '\0' && linha[iLin] != ',' && iId < 9) idGame[iId++] = linha[iLin++]; // extrair o id
                idGame[iId] = '\0'; // finalizar a string do id montado

                if (cod == inteiro(idGame)) { // se o id da linha for igual ao código procurado
                    Game *g = (Game*)malloc(sizeof(Game)); // criar uma variável do tipo Game
                    processarLinha(linha, g); // processar a linha e preencher os dados do jogo
                    inserir(g); // inserir o jogo na pilha encadeada
                    encontrado = true; // marcar como encontrado e sair do loop
                }
            }
            fclose(arq);

        } else if (strcmp(partes[0], "R") == 0) { // se o comando for remover do fim
            Game *aux = remover(); // remove do fim da pilha
            printf("(R) %s\n", aux->name); // imprime o nome do jogo removido
            free(aux); // libera a memória do jogo removido
        }
    }

    int j = tamanho() - 1; // obtém o tamanho da pilha menos um
    mostrarInv(topo, j); // mostra a pilha de forma inversa

    return 0;
}
