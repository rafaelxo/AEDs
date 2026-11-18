#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>

int comparacoes = 0; // contador de comparações
int movimentacoes = 0; // contador de movimentações

typedef struct { // estrutura para armazenar a data com dia, mês e ano
    int dia, mes, ano;
} Data;

void setData(Data *data, const char *str) { // método para setar a data a partir de uma string
    data->dia = 1;
    data->mes = 1;
    data->ano = 0; // inicializa a data com valores padrão

    if (!str || strlen(str) == 0) return; // se a string for nula ou muito curta, retorna

    char meses[12][4] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // array com os meses abreviados

    char mes[4] = {0}; // array para armazenar o mês
    strncpy(mes, str, 3); // copia os primeiros 3 caracteres para o mês
    mes[3] = '\0'; // finaliza a string do mês

    int mesNum = 1; // variável para o número do mês
    for (int j = 0; j < 12; j++) { // loop para encontrar o número do mês correspondente
        if (strcmp(mes, meses[j]) == 0) { // se encontrar o mês
            mesNum = j + 1; // atribui o número do mês (1-12)
            break;
        }
    }
    data->mes = mesNum; // atribui o mês à estrutura

    char dia[3] = "01"; // array para armazenar o dia
    char ano[5] = {0}; // array para armazenar o ano
    if (strlen(str) == 8 && str[3] == ' ') { // se o formato for "Mmm d yyyy"
        strncpy(ano, str + 4, 4); // copia os 4 caracteres do ano
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

typedef struct { // estrutura para armazenar os dados do jogo com seus atributos
    int id, estimatedOwners, metacriticScore, achievements;
    char name[200];
    Data releaseDate;
    float price, userScore;
    char supportedLanguages[50][50], publishers[50][50], developers[50][50], categories[50][50], genres[50][50], tags[50][50];
} Game;

// setters para Game
void setId(Game *g, int id) { g->id = id; }
void setName(Game *g, const char *name) { strcpy(g->name, name ? name : ""); }
void setReleaseDate(Game *g, const char *releaseDate) { setData(&g->releaseDate, releaseDate); }
void setEstimatedOwners(Game *g, int estimatedOwners) { g->estimatedOwners = estimatedOwners; }
void setPrice(Game *g, float price) { g->price = price; }
void setMetacriticScore(Game *g, int metacriticScore) { g->metacriticScore = metacriticScore; }
void setUserScore(Game *g, float userScore) { g->userScore = userScore; }
void setAchievements(Game *g, int achievements) { g->achievements = achievements; }
void setSupportedLanguages(Game *g, char languages[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->supportedLanguages[i], languages[i]);
}
void setPublishers(Game *g, char pubs[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->publishers[i], pubs[i]);
}
void setDevelopers(Game *g, char devs[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->developers[i], devs[i]);
}
void setCategories(Game *g, char cats[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->categories[i], cats[i]);
}
void setGenres(Game *g, char gens[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->genres[i], gens[i]);
}
void setTags(Game *g, char tgs[50][50]) {
    for (int i = 0; i < 50; i++) strcpy(g->tags[i], tgs[i]);
}

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

bool espaco (char c) { // método para verificar se um caractere é espaço em branco
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == '\v'; // verifica os tipos de espaço em branco
}

void trim (char *str) { // método para remover espaços em branco do início e fim da string
    if (str == NULL) return; // se a string for nula, retorna

    int ini = 0, fim = strlen(str) - 1; // índices para o início e fim da string
    while (ini <= fim && espaco(str[ini])) ini++; // remove espaços em branco do início
    while (fim >= ini && espaco(str[fim])) fim--; // remove espaços em branco do fim

    int k = 0; // índice para a nova posição na string
    for (int t = ini; t <= fim; t++) str[k++] = str[t]; // desloca os caracteres para o início
    str[k] = '\0'; // finaliza a string removendo os espaços em branco do fim
}

void limparAspas (char *str) { // método para remover aspas do início e fim da string
    if (!str) return; // se a string for nula, retorna

    trim(str); // remove espaços em branco do início e fim
    int n = strlen(str); // obtém o tamanho da string
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

    int i = 0, fim = strlen(str) - 1; // inicializa os índices para percorrer a string
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

    int i = 0, fim = strlen(str) - 1; // inicializa os índices para percorrer a string
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

void processarLinha (char *linha, Game *game) { // método para processar a linha lida do arquivo
    inicializar(game); // inicializar os campos do jogo
    char campos[15][1000]; // array para armazenar os campos separados
    int ncampos = separaCampos(linha, campos); // separar os campos da linha

    if (ncampos > 0) setId(game, inteiro(campos[0])); // converter o primeiro campo para inteiro e armazenar no id

    if (ncampos > 1) { // processar o segundo campo para nome
        setName(game, campos[1]); // copiar o segundo campo para o nome
        trim(game->name);
    }

    if (ncampos > 2) { // processar o terceiro campo para data de lançamento
        setReleaseDate(game, campos[2]); // copiar o terceiro campo para a data de lançamento
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
        setEstimatedOwners(game, inteiro(nums)); // converter a string de dígitos para inteiro e armazenar na estimativa de donos
    }

    if (ncampos > 4) { // processar o quinto campo para preço
        char preco[100]; // array para armazenar o preço
        strcpy(preco, campos[4]);
        trim(preco);
        if (strcmp(preco, "Free to Play") == 0 || strlen(preco) == 0) setPrice(game, 0.0f); // se for "Free to Play" ou estiver vazio, o preço é 0.0
        else setPrice(game, real(preco)); // caso contrário, converter para float e armazenar no preço
    }

    if (ncampos > 5) { // processar o sexto campo para idiomas suportados
        char temp[50][50] = {{0}};
        separaArray(campos[5], temp); // separar os idiomas e armazenar no array
        for (int i = 0; i < 50 && temp[i][0] != '\0'; i++) limparAspas(temp[i]); // remover aspas de cada idioma
        setSupportedLanguages(game, temp);
    }

    if (ncampos > 6) { // processar o sétimo campo para pontuação do Metacritic
        char meta[100]; // array para armazenar a pontuação do Metacritic
        strcpy(meta, campos[6]);
        trim(meta);
        if (strlen(meta) == 0) setMetacriticScore(game, -1); // se estiver vazio, a pontuação é -1
        else setMetacriticScore(game, inteiro(meta)); // caso contrário, converter para inteiro e armazenar na pontuação do Metacritic
    }

    if (ncampos > 7) { // processar o oitavo campo para pontuação do usuário
        char user[100]; // array para armazenar a pontuação do usuário
        strcpy(user, campos[7]);
        trim(user);
        if (strlen(user) == 0 || strcmp(user, "tbd") == 0) setUserScore(game, -1.0f); // se estiver vazio ou for "tbd", a pontuação é -1.0
        else setUserScore(game, real(user)); // caso contrário, converter para float e armazenar na pontuação do usuário
    }

    if (ncampos > 8) { // processar o nono campo para conquistas
        char ach[100]; // array para armazenar as conquistas
        strcpy(ach, campos[8]); // copiar o campo para uma variável temporária
        trim(ach);
        if (strlen(ach) == 0) setAchievements(game, 0); // se estiver vazio, as conquistas são 0
        else setAchievements(game, inteiro(ach)); // caso contrário, converter para inteiro e armazenar nas conquistas
    }

    if (ncampos > 9) { // processar o décimo campo para editores
        char temp[50][50] = {{0}};
        separaArray(campos[9], temp);
        setPublishers(game, temp);
    }
    if (ncampos > 10) { // processar o décimo primeiro campo para desenvolvedores
        char temp[50][50] = {{0}};
        separaArray(campos[10], temp);
        setDevelopers(game, temp);
    }
    if (ncampos > 11) { // processar o décimo segundo campo para categorias
        char temp[50][50] = {{0}};
        separaArray(campos[11], temp);
        setCategories(game, temp);
    }
    if (ncampos > 12) { // processar o décimo terceiro campo para gêneros
        char temp[50][50] = {{0}};
        separaArray(campos[12], temp);
        setGenres(game, temp);
    }
    if (ncampos > 13) { // processar o décimo quarto campo para tags
        char temp[50][50] = {{0}};
        separaArray(campos[13], temp);
        setTags(game, temp);
    }
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

void swap (Game *a, Game *b) { // método para trocar dois jogos de posição
    Game temp = *a; // guarda o valor de a em uma variável temporária
    *a = *b; // copia o valor de b para a
    *b = temp; // copia o valor da variável temporária para b
    movimentacoes += 3; // incrementa o contador de movimentações
}

void selecao (Game *games, int n) { // método para ordenar um array de jogos pelo nome através do seleção
    for (int i = 0; i < n - 1; i++) {
        int menor = i; // índice do menor elemento
        for (int j = i + 1; j < n; j++) {
            comparacoes++;
            if (strcmp(games[j].name, games[menor].name) < 0) menor = j; // se encontrar um nome menor, atualiza o índice
        }
        if (menor != i) swap(&games[i], &games[menor]); // troca o menor elemento com o primeiro elemento da parte não ordenada
    }
}

int main() { // main do programa
    Game *games = (Game*)malloc(1000*sizeof(Game)); // array para armazenar os jogos
    int qtd = 0; // contador de jogos armazenados

    char entrada[100]; // declaração e leitura da entrada
    scanf("%s", entrada);
    while (strcmp(entrada, "FIM") != 0) { // lê entrada até EOF
        entrada[strcspn(entrada, "\n")] = '\0'; // remove nova linha
        int cod = inteiro(entrada); // converte a entrada para inteiro
        FILE *arq = fopen("/tmp/games.csv", "r"); // abertura do arquivo para leitura

        char linha[2000]; // buffer para ler cada linha do arquivo
        fgets(linha, sizeof(linha), arq); // ler a primeira linha (cabeçalho) e ignorar

        bool encontrado = false;
        while (!encontrado && fgets(linha, sizeof(linha), arq) != NULL) { // ler cada linha do arquivo
            linha[strcspn(linha, "\n")] = '\0'; // remove nova linha
            int iLin = 0, iId = 0; // índice para percorrer as linhas
            char idGame[10]; // buffer para armazenar o id como string
            while (linha[iLin] != '\0' && linha[iLin] != ',' && iId < 9) idGame[iId++] = linha[iLin++]; // extrair o id
            idGame[iId] = '\0'; // finalizar a string do id montado

            if (cod == inteiro(idGame)) { // se o id da linha for igual ao código procurado
                processarLinha(linha, &games[qtd++]); // processar a linha e preencher os dados do jogo
                encontrado = true; // marcar como encontrado e sair do loop
            }
        }

        fclose(arq);
        scanf("%s", entrada); // ler a próxima entrada
    }

    clock_t inicio = clock(); // marca o tempo de início da execução
    selecao(games, qtd); // ordenar os jogos pelo nome
    clock_t fim = clock(); // marca o tempo de fim da execução
    double tempo = (double)(fim - inicio) / CLOCKS_PER_SEC * 1000.0; // calcula o tempo total de execução
    for (int i = 0; i < qtd; i++) mostrar(&games[i]); // mostrar os jogos ordenados

    FILE *log = fopen("893046_selecao.txt", "w"); // abre o arquivo de log para escrita
    if (log) { // se o arquivo foi aberto com sucesso
        fprintf(log, "Matrícula: 893046\tTempo de execução: %.0fms\tNúmero de comparações: %d\tNúmero de movimentações: %d\n", tempo, comparacoes, movimentacoes); // escreve os dados no arquivo de log
        fclose(log);
    }

    free(games);
    return 0;
}
