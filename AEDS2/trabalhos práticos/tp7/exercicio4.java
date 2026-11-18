import java.util.*;
import java.io.*;

public class exercicio4 {
    public static Scanner sc = new Scanner(System.in); // scanner para leitura de entrada

    public static int comparacoes = 0; // contador de comparações

    public static class NoAN { // classe nó para árvore

        // atributos
        private Game game;
        private NoAN esq, dir;
        private boolean cor;

        // construtor
        public NoAN (Game x) {
            this.game = x;
            this.esq = this.dir = null;
            this.cor = true;
        }
    }

    public static class Alvinegra { // classe árvore

        private NoAN raiz; // astributo raiz

        public Alvinegra() { this.raiz = null; } // construtor

        public void inserirMan (Game x, NoAN i) { // método recursivo de inserção
            if (raiz == null) raiz = new NoAN(x); // se a raiz for nula, cria o nó raiz
            else if (raiz.esq == null && raiz.dir == null) { // se a raiz não tiver filhos
                if (menorNome(x.name, raiz.game.name) < 0) raiz.esq = new NoAN(x); // se for menor, insere à esquerda
                else raiz.dir = new NoAN(x); // se for maior, insere à direita
            } else if (raiz.esq == null) { // se a raiz não tiver o filho da esquerda
                if (menorNome(x.name, raiz.game.name) < 0) raiz.esq = new NoAN(x); // se for menor, insere à esquerda
                else if (menorNome(x.name, raiz.dir.game.name) < 0) { // se for maior que a raiz e menor que o filho à direita
                    raiz.esq = new NoAN(raiz.game); // move a raiz para a esquerda
                    raiz.game = x; // insere o novo nó na raiz
                } else { // se for maior que o filho à direita
                    raiz.esq = new NoAN(raiz.game); // move a raiz para a esquerda
                    raiz.game = raiz.dir.game; // move o filho direito para a raiz
                    raiz.dir.game = x; // insere o novo nó no filho direito
                }
                raiz.esq.cor = raiz.dir.cor = false; // define a cor dos filhos como preto
            } else if (raiz.dir == null) {
                if (menorNome(x.name, raiz.game.name) > 0) raiz.dir = new NoAN(x); // se for maior, insere à direita
                else if (menorNome(x.name, raiz.esq.game.name) > 0) { // se for menor que a raiz e maior que o filho à esquerda
                    raiz.dir = new NoAN(raiz.game); // move a raiz para a direita
                    raiz.game = x; // insere o novo nó na raiz
                } else { // se for menor que o filho à esquerda
                    raiz.dir = new NoAN(raiz.game); // move a raiz para a direita
                    raiz.game = raiz.esq.game; // move o filho esquerdo para a raiz
                    raiz.esq.game = x; // insere o novo nó no filho esquerdo
                }
                raiz.esq.cor = raiz.dir.cor = false; // define a cor do filho direito como preto
            } else inserir(x, null, null, null, raiz); // chama o método recursivo
            raiz.cor = false; // define a cor da raiz como branca
        }

        private void inserir (Game x, NoAN bis, NoAN avo, NoAN pai, NoAN i) {
            if (i == null) {
                if (menorNome(x.name, pai.game.name) < 0) i = pai.esq = new NoAN(x); // se for menor, insere à esquerda
                else i = pai.dir = new NoAN(x); // se for maior, insere à direita
                if (pai.cor == true) balancear(bis, avo, pai, i); // se o pai for vermelho, balanceia a árvore
            } else {
                if (i.esq != null && i.dir != null && i.esq.cor == true && i.dir.cor == true) { // se os dois filhos forem vermelhos
                    i.cor = true; // pinta o nó atual de vermelho
                    i.esq.cor = i.dir.cor = false; // pinta os filhos de preto
                    if (i == raiz) i.cor = false; // se for a raiz, pinta de preto
                    else if (pai.cor == true) balancear(bis, avo, pai, i); // se o pai for vermelho, balanceia a árvore
                }
                if (menorNome(x.name, i.game.name) < 0) inserir(x, avo, pai, i, i.esq); // se for menor, chama recursivamente à esquerda
                else if (menorNome(x.name, i.game.name) > 0) inserir(x, avo, pai, i, i.dir); // se for maior, chama recursivamente à direita
                else;
            }
        }

        private void balancear (NoAN bis, NoAN avo, NoAN pai, NoAN i) {
            if (pai.cor == true) { // se o pai for vermelho
                if (menorNome(pai.game.name, avo.game.name) > 0) { // se o pai for maior que o avo
                    if (menorNome(i.game.name, pai.game.name) > 0) avo = rotacaoEsq(avo); // se o nó for maior que o pai, rotação simples à esquerda
                    else avo = rotacaoDirEsq(avo); // se for menor, rotação dupla à direita-esquerda
                } else { // se o pai for menor que o avo
                    if (menorNome(i.game.name, pai.game.name) < 0) avo = rotacaoDir(avo); // se o nó for menor que o pai, rotação simples à direita
                    else avo = rotacaoEsqDir(avo); // se for maior, rotação dupla à esquerda-direita
                }
                if (bis == null) raiz = avo; // se bis for nulo, atualiza a raiz
                else if (menorNome(avo.game.name, bis.game.name) < 0) bis.esq = avo; // se avo for menor que bis, atualiza o filho esquerdo de bis
                else bis.dir = avo; // se avo for maior que bis, atualiza o filho direito de bis
                avo.cor = false; // pinta avo de preto
                avo.esq.cor = avo.dir.cor = true; // pinta os filhos de avo de vermelho
            }
        }

        private NoAN rotacaoDir (NoAN i) { // método de rotação simples à direita
            NoAN noEsq = i.esq; // pega o nó esquerdo
            NoAN noEsqDir = noEsq.dir; // pega o filho direito do nó esquerdo
            noEsq.dir = i; // faz a rotação
            i.esq = noEsqDir; // atualiza o filho esquerdo do nó i
            return noEsq; // retorna o novo nó raiz
        }
        private NoAN rotacaoEsq (NoAN i) { // método de rotação simples à esquerda
            NoAN noDir = i.dir; // pega o nó direito
            NoAN noDirEsq = noDir.esq; // pega o filho esquerdo do nó direito
            noDir.esq = i; // faz a rotação
            i.dir = noDirEsq; // atualiza o filho direito do nó i
            return noDir; // retorna o novo nó raiz
        }
        private NoAN rotacaoDirEsq (NoAN i) { // método de rotação dupla direita-esquerda
            i.dir = rotacaoDir(i.dir); // faz a rotação direita no filho direito
            return rotacaoEsq(i); // faz a rotação esquerda no nó i
        }
        private NoAN rotacaoEsqDir (NoAN i) { // método de rotação dupla esquerda-direita
            i.esq = rotacaoEsq(i.esq); // faz a rotação esquerda no filho esquerdo
            return rotacaoDir(i); // faz a rotação direita no nó i
        }

        public void pesquisar (String nome, NoAN i) { // método recursivo de pesquisa
            if (i == null) System.out.print(" NAO"); // se o nó for nulo, não encontrou
            else if (nome.equals(i.game.name)) { // se o nome for igual ao do nó atual
                comparacoes++;
                System.out.print(" SIM"); // imprime que encontrou
            } else if (menorNome(nome, i.game.name) < 0) { // se o nome for menor, chama a pesquisa na subárvore esquerda
                comparacoes++;
                System.out.print(" esq"); // indica que vai para esquerda
                pesquisar(nome, i.esq); // chamada recursiva à subárvore esquerda
            } else if (menorNome(nome, i.game.name) > 0) { // se for maior, chama a pesquisa na subárvore direita
                comparacoes++;
                System.out.print(" dir"); // indica que vai para direita
                pesquisar(nome, i.dir); // chamada recursiva à subárvore direita
            }
        }
    }

    static class Data { // classe data

        // atributos
        private int dia, mes, ano;

        public Data() { // construtor padrão
            this.dia = 1;
            this.mes = 1;
            this.ano = 0;
        }
        public Data (int dia, int mes, int ano) { // construtor com parâmetros
            setDia(dia);
            setMes(mes);
            setAno(ano);
        }
        public Data (String dataStr) { // construtor que recebe string no formato "Mon dd, yyyy"
            setData(dataStr);
        }

        // getters e setters para cada atributo
        public int getDia() { return dia; }
        public void setDia (int dia) { this.dia = dia; }
        public int getMes() { return mes; }
        public void setMes (int mes) { this.mes = mes; }
        public int getAno() { return ano; }
        public void setAno (int ano) { this.ano = ano; }

        public void setData (String str) { // método para setar a data a partir de uma string no formato "Mon dd, yyyy"
            if (str == null || str.length() == 0) { // tratamento para string nula ou vazia
                this.dia = 1;
                this.mes = 1;
                this.ano = 0;
                return; // seta data padrão e retorna
            }

            String[] meses = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // array com as abreviações dos meses

            int len = str.length(); // cache length para todo método
            String mesStr = "", diaStr = "", anoStr = ""; // strings temporárias para armazenar mês, dia e ano
            int i = 0;
            int fase = 0; // 0: mes (primeiros 3), 1: dia (digitos apos espaco), 2: ano (digitos apos virgula/espaco)
            while (i < len) { // loop para percorrer a string
                char c = str.charAt(i); // caractere atual
                if (fase == 0 && i >= 3) { fase = 1; i++; continue; } // sai de mes apos 3 caracteres
                if (fase == 1 && c == ',') { fase = 2; i++; continue; } // sai de dia na virgula
                if (fase == 2 && (c == ' ' || c == ',')) { i++; continue; } // pula espacos/comas em ano

                if (fase == 0) mesStr += c; // coleta as iniciais do mês
                else if (fase == 1 && c >= '0' && c <= '9') diaStr += c; // só digitos em dia
                else if (fase == 2 && c >= '0' && c <= '9') anoStr += c; // só digitos em ano
                i++;
            }

            this.mes = 1; // padrão
            for (int j = 0; j < 12; j++) { // loop para encontrar o mês correspondente
                if (mesStr.equals(meses[j])) { // se encontrar o mês (usa equals)
                    this.mes = j + 1; // atribui o número do mês correspondente
                    break;
                }
            }

            this.dia = diaStr.length() > 0 ? inteiro(diaStr) : 1; // converte o dia ou define como 1 se vazio

            this.ano = anoStr.length() == 4 ? inteiro(anoStr) : 1; // converte o ano ou define como 1 se inválido (4 digitos exatos)
        }

        public String formatarData() { // método para formatar a data no formato dd/mm/yyyy
            String diaStr = (dia < 10 ? "0" : "") + dia; // formata o dia com dois dígitos
            String mesStr = (mes < 10 ? "0" : "") + mes; // formata o mês com dois dígitos
            return diaStr + "/" + mesStr + "/" + ano; // retorna a data formatada
        }
    }

    static class Game { // classe game

        // atributos
        private int id, estimatedOwners, metacriticScore, achievements;
        private String name;
        private Data releaseDate;
        private float price, userScore;
        private String[] supportedLanguages, publishers, developers, categories, genres, tags;

        public Game() { // construtor padrão
            this.id = 0;
            this.name = "";
            this.releaseDate = new Data();
            this.estimatedOwners = 0;
            this.price = 0;
            this.supportedLanguages = new String[0];
            this.metacriticScore = -1;
            this.userScore = -1.0f;
            this.achievements = 0;
            this.publishers = new String[0];
            this.developers = new String[0];
            this.categories = new String[0];
            this.genres = new String[0];
            this.tags = new String[0];
        }
        public Game (int id, String name, String releaseDate, int estimatedOwners, float price, String[] supportedLanguages, int metacriticScore, float userScore, int achievements, String[] publishers, String[] developers, String[] categories, String[] genres, String[] tags) { // construtor com parâmetros
            setId(id);
            setName(name);
            setReleaseDate(releaseDate);
            setEstimatedOwners(estimatedOwners);
            setPrice(price);
            setSupportedLanguages(supportedLanguages);
            setMetacriticScore(metacriticScore);
            setUserScore(userScore);
            setAchievements(achievements);
            setPublishers(publishers);
            setDevelopers(developers);
            setCategories(categories);
            setGenres(genres);
            setTags(tags);
        }

        // getters e setters para cada atributo
        public int getId() { return id; }
        public void setId (int id) { this.id = id; }

        public String getName() { return name; }
        public void setName (String name) { this.name = name; }

        public Data getReleaseDate() { return releaseDate; }
        public void setReleaseDate (String releaseDate) { this.releaseDate = new Data(releaseDate); }

        public int getEstimatedOwners() { return estimatedOwners; }
        public void setEstimatedOwners (int estimatedOwners) { this.estimatedOwners = estimatedOwners; }

        public float getPrice() { return price; }
        public void setPrice (float price) { this.price = price; }

        public String[] getSupportedLanguages() { return supportedLanguages; }
        public void setSupportedLanguages (String[] supportedLanguages) { this.supportedLanguages = supportedLanguages; }

        public int getMetacriticScore() { return metacriticScore; }
        public void setMetacriticScore (int metacriticScore) {  this.metacriticScore = metacriticScore;  }

        public float getUserScore() { return userScore; }
        public void setUserScore (float userScore) { this.userScore = userScore; }

        public int getAchievements() { return achievements; }
        public void setAchievements (int achievements) { this.achievements = achievements; }

        public String[] getPublishers() { return publishers; }
        public void setPublishers (String[] publishers) { this.publishers = publishers; }

        public String[] getDevelopers() { return developers; }
        public void setDevelopers (String[] developers) { this.developers = developers; }

        public String[] getCategories() { return categories; }
        public void setCategories (String[] categories) { this.categories = categories; }

        public String[] getGenres() { return genres; }
        public void setGenres (String[] genres) { this.genres = genres; }

        public String[] getTags() { return tags; }
        public void setTags(String[] tags) { this.tags = tags; }

        public static String formatarData (Data data) { // método para formatar objeto Data
            if (data == null) return "01/01/0001"; // retorna data padrão se o objeto for nulo

            String dia = data.getDia() < 10 ? "0" + data.getDia() : "" + data.getDia(); // formata o dia com dois dígitos
            String mes = data.getMes() < 10 ? "0" + data.getMes() : "" + data.getMes(); // formata o mês com dois dígitos
            String ano = "" + data.getAno(); // converte o ano para string

            return dia + "/" + mes + "/" + ano; // retorna a data formatada
        }

        public static String formatarFloat (float n, int casas) { // método para formatar float com 1 ou 2 casas decimais
            if (n == 0.0f || (casas == 1 && n == -1.0f)) return "0.0"; // tratamento especial para 0.0 e -1.0 com 1 casa decimal

            int inteiro = (int) n; // parte inteira
            String result = "" + inteiro + "."; // inicia com int + "."
            float decimalF = n - inteiro; // parte decimal
            if (casas == 2) { // se for para formatar com 2 casas decimais
                int decimal = (int) (decimalF * 100 + 0.5f); // parte decimal arredondada
                int decInt = decimal / 10; // primeiro dígito decimal
                if (decimal % 10 == 0) result += decInt; // se o segundo dígito for 0, adiciona só o primeiro
                else if (decimal < 10) result += "0" + decimal; // se for menor que 10, adiciona 0 antes
                else result += "" + decimal; // caso contrário, adiciona os dois dígitos
            } else { // se for para formatar com 1 casa decimal
                int decimal = (int) (decimalF * 10 + 0.5f); // parte decimal arredondada
                result += "" + decimal; // adiciona o dígito decimal
            }

            return result; // retorna a string formatada
        }

        public static String juntar (String[] arr) { // método para juntar os elementos
            if (arr == null || arr.length == 0) return "[]"; // se o array for nulo ou vazio, retorna string vazia

            String result = "["; // string para armazenar o resultado
            int lenArr = arr.length; // tamanho do array
            for (int i = 0; i < lenArr; i++) { // loop para percorrer o array
                result += arr[i]; // adiciona o elemento atual
                if (i < lenArr - 1) result += ", "; // se não for o último elemento, adiciona vírgula e espaço
            }
            result += "]"; // fecha a lista

            return result; // retorna a string resultante
        }

        public String mostrar() { // método para mostrar o game
            String saida = "=> " + getId() + " ## " + getName() + " ## " + formatarData(getReleaseDate()) + " ## " + getEstimatedOwners() + " ## ";
            saida += formatarFloat(getPrice(), 2) + " ## ";
            saida += juntar(getSupportedLanguages()) + " ## ";
            saida += getMetacriticScore() + " ## ";
            saida += formatarFloat(getUserScore(), 1) + " ## ";
            saida += getAchievements() + " ## ";
            saida += juntar(getPublishers()) + " ## ";
            saida += juntar(getDevelopers()) + " ## ";
            saida += juntar(getCategories()) + " ## ";
            saida += juntar(getGenres()) + " ## ";
            saida += juntar(getTags()) + " ##";
            return saida; // retorna a string formatada com todos os atributos
        }

        public int menorNome (Game jogo) { // método de instância para comparar nomes
            String a = this.name; // atribui o nome do jogo atual
            String b = jogo.getName(); // atribui o nome do jogo passado como parâmetro

            int lenA = a.length(), lenB = b.length(); // tamanho das strings
            int i = 0; // índice para percorrer as strings
            while (i < lenA && i < lenB) { // loop para comparar caractere por caractere
                char c1 = a.charAt(i), c2 = b.charAt(i);
                if (c1 != c2) return c1 - c2; // se encontrar um caractere diferente, retorna a diferença
                i++;
            }

            return lenA - lenB; // se passar por todos os caracteres e não encontrar diferenças, retorna a diferença de tamanho
        }
    }

    public static int inteiro (String str) { // método para converter string em inteiro
        if (str == null || str.length() == 0) return 0; // tratamento para string nula ou vazia

        int len = str.length(); // tamanho da string
        String tempStr = ""; // string temporária para armazenar a string sem espaços em branco
        for (int i = 0; i < len; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (c != ' ' && c != '\t') tempStr += c; // coleta só não-brancos (equiv. skips iniciais/finais)
        }
        if (tempStr.length() == 0) return 0; // se a string temporária estiver vazia, retorna 0
        str = tempStr; // atualiza a string original

        int i = 0, fim = str.length() - 1; // índices para início e fim da string (fim agora exato)

        int num = 0; // variável para o número resultante
        boolean achou = false, negat = false; // flags para marcar se encontrou pelo menos um dígito e se é negativo
        if (i <= fim && str.charAt(i) == '-') { // se o primeiro caractere for '-', marca como negativo
            negat = true; // marca como negativo
            i++;
        }

        for (; i <= fim; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') { // se for um dígito
                num = num * 10 + (c - '0'); // constrói o número
                achou = true; // marca que encontrou pelo menos um dígito
            }
        }

        return achou ? (negat ? -num : num) : 0; // retorna o número ou 0 se não encontrou dígitos
    }

    public static float real (String str) { // método para converter string em float
        if (str == null || str.length() == 0) return 0; // tratamento para string nula ou vazia

        int len = str.length(); // tamanho da string
        String tempStr = ""; // string temporária para armazenar a string sem espaços em branco
        for (int i = 0; i < len; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (c != ' ' && c != '\t') tempStr += c; // coleta só não-brancos (equiv. skips iniciais/finais)
        }
        if (tempStr.length() == 0) return 0; // se a string temporária estiver vazia, retorna 0
        str = tempStr; // atualiza a string original

        int i = 0, fim = str.length() - 1; // índices para início e fim da string

        float num = 0, div = 1; // variáveis para o número e divisor decimal
        boolean achou = false, negat = false, decimal = false; // flags para controle de parte decimal, se encontrou dígitos e se é negativo
        if (i <= fim && str.charAt(i) == '-') { // se o primeiro caractere for '-', marca como negativo
            negat = true; // marca como negativo
            i++;
        }

        for (; i <= fim; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (c == '.') decimal = true; // se encontrar um ponto, ativa a flag de parte decimal
            else if (c >= '0' && c <= '9') { // se for um dígito
                achou = true; // marca que encontrou pelo menos um dígito
                if (!decimal) num = num * 10 + (c - '0'); // se não for parte decimal, constrói a parte inteira
                else {
                    div *= 10; // aumenta o divisor decimal
                    num = num + (c - '0') / div; // parte decimal
                }
            }
        }

        return achou ? (negat ? -num : num) : 0; // retorna o número ou 0 se não encontrou dígitos
    }

    public static void trim (String[] array, int n) { // método para remover espaços em branco do início e fim dos elementos de um array
        for (int i = 0; i < n; i++) { // loop para percorrer os primeiros n elementos do array
            if (array[i] == null) array[i] = ""; // tratamento para elementos nulos

            int ini = 0, fim = array[i].length() - 1; // índices para início e fim do elemento
            int lenElem = array[i].length(); // tamanho do elemento
            while (ini < lenElem && (array[i].charAt(ini) == ' '|| array[i].charAt(ini) == '\t')) ini++; // move o índice inicial para o primeiro caractere não branco
            while (fim >= ini && (array[i].charAt(fim) == ' '|| array[i].charAt(fim) == '\t')) fim--; // move o índice final para o último caractere não branco

            String novo = ""; // variável para armazenar o novo elemento sem espaços em branco
            for (int j = ini; j <= fim; j++) novo += array[i].charAt(j); // constrói o novo elemento
            array[i] = novo; // atribui o novo elemento ao array
        }
    }

    public static int separaArray (String str, String[] array) { // método para separar os elementos de um array representado como string
        if (str == null || str.length() == 0) return 0; // tratamento para string nula ou vazia

        boolean eArray = str.length() > 0 && str.charAt(0) == '['; // verifica se a string representa um array
        int n = 0; // contador de elementos
        String campo = ""; // variável temporária para armazenar o elemento atual
        boolean aspas = false; // variável para controle de aspas

        int len = str.length(); // tamanho da string
        for (int i = 0; i < len; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (eArray && c == '\'') aspas = !aspas; // alterna o estado de aspas
            else if ((c == ',' || c == ';') && !aspas) { // se encontrar uma vírgula ou ponto e vírgula fora de aspas, finaliza o elemento
                if (eArray || (i + 1 >= len || str.charAt(i + 1) != ' ')) { // se for um array ou não houver espaço após a vírgula
                    if (campo.length() > 0) array[n++] = campo; // armazena o elemento no array se não estiver vazio
                    campo = ""; // reseta a variável temporária
                } else campo += c; // adiciona o caractere ao elemento atual
            } else if (c != '[' && c != ']' && c != '"') campo += c; // adiciona o caractere ao elemento atual, ignorando colchetes e aspas
            else if (c == '\'' && !eArray) campo += c; // adiciona aspas simples se não for um array
        }

        if (campo.length() > 0) array[n++] = campo; // armazena o último elemento se não estiver vazio
        trim(array, n); // remove espaços em branco dos elementos do array

        return n; // retorna a quantidade de elementos separados
    }

    public static String limparAspas (String str) { // método para remover aspas iniciais e finais de uma string
        if (str == null || str.length() == 0) return ""; // tratamento para string nula ou vazia

        int ini = 0, fim = str.length() - 1; // índices para início e fim da string
        int len = str.length(); // tamanho da string
        while (ini < len && (str.charAt(ini) == ' ' || str.charAt(ini) == '\t')) ini++; // ignora espaços iniciais
        while (fim >= ini && (str.charAt(fim) == ' ' || str.charAt(fim) == '\t')) fim--; // ignora espaços finais

        if (fim >= ini + 1 && str.charAt(ini) == '"' && str.charAt(fim) == '"') { // verifica se há aspas nas extremidades
            ini++; fim--; // ajusta os índices para ignorar as aspas
        }

        String result = ""; // variável para armazenar a nova string sem aspas
        for (int i = ini; i <= fim; i++) result += str.charAt(i); // constrói a nova string

        return result; // retorna a string sem aspas
    }

    public static int separaCampos (String str, String[] campos) { // método para separar os campos da linha CSV
        if (str == null || str.length() == 0) return 0; // tratamento para string nula ou vazia

        int n = 0; // contador de campos
        String campo = ""; // variável temporária para armazenar o campo atual
        boolean aspas = false; // variável para controle de aspas

        int len = str.length(); // tamanho da string
        for (int i = 0; i < len; i++) { // loop para percorrer os caracteres da string
            char c = str.charAt(i);
            if (c == '"') aspas = !aspas; // alterna o estado de aspas
            else if (c == ',' && !aspas) { // se encontrar uma vírgula fora de aspas, finaliza o campo
                campos[n++] = limparAspas(campo); // armazena o campo no array, removendo aspas
                campo = ""; // reseta a variável temporária
            } else campo += c; // adiciona o caractere ao campo atual
        }
        campos[n++] = limparAspas(campo); // armazena o último campo

        return n; // retorna a quantidade de campos separados
    }

    public static String[] processarLista (String campo) { // método para processar uma lista de campos
        String[] tmp = new String[100]; // array temporário para armazenar os campos separados
        int qtd = separaArray(campo == null ? "" : campo, tmp); // separa os campos e obtém a quantidade
        String[] result = new String[qtd]; // array final com o tamanho exato
        for (int i = 0; i < qtd; i++) result[i] = tmp[i]; // copia os campos para o array final

        return result; // retorna o array final
    }

    public static void processar (String str, Game game) { // método para processar a linha e preencher o objeto Game
        String[] campos = new String[15]; // array para armazenar os campos separados
        separaCampos(str, campos); // separa os campos da linha

        game.setId(inteiro(campos[0] == null ? "" : campos[0])); // setagem do identificador único
        game.setName(campos[1] == null ? "" : campos[1]); // setagem do nome
        game.setReleaseDate(campos[2] == null ? "" : campos[2]); // setagem da data de lançamento
        game.setEstimatedOwners(inteiro(campos[3] == null ? "" : campos[3])); // setagem da estimativa de compradores

        // setagem do preço
        String precoStr = campos[4] == null ? "" : campos[4]; // pega o campo de preço com tratamento para nulo
        if (precoStr.equals("Free to Play")) game.setPrice(0.0f); // tratamento especial para "Free to Play"
        else game.setPrice(real(precoStr));

        game.setSupportedLanguages(processarLista(campos[5])); // setagem dos idiomas suportados

        // setagem da nota atribuída pelo Metacritic
        String campoMeta = campos[6] == null ? "" : campos[6];
        if (campoMeta.length() == 0) game.setMetacriticScore(-1); // tratamento para campo vazio
        else game.setMetacriticScore(inteiro(campoMeta));

        // setagem da nota atribuída pelos usuários
        String campoUser = campos[7] == null ? "" : campos[7];
        if (campoUser.length() == 0 || campoUser.equals("tbd")) game.setUserScore(-1.0f); // tratamento para "tbd"
        else game.setUserScore(real(campoUser));

        game.setAchievements(inteiro(campos[8] == null ? "" : campos[8])); // setagem de conquistas disponíveis
        game.setPublishers(processarLista(campos[9])); // setagem dos responsáveis pela publicação
        game.setDevelopers(processarLista(campos[10])); // setagem dos responsáveis pelo desenvolvimento
        game.setCategories(processarLista(campos[11])); // setagem das categorias associadas
        game.setGenres(processarLista(campos[12])); // setagem dos gêneros
        game.setTags(processarLista(campos[13])); // setagem das tags
    }

    public static int menorNome (String a, String b) { // método static para comparar duas strings (nomes)
        if (a == null || b == null) return 0; // se algum dos nomes for nulo, retorna 0

        int lenA = a.length(), lenB = b.length(); // tamanho das strings
        int i = 0;
        while (i < lenA && i < lenB) { // loop para comparar caractere por caractere
            char c1 = a.charAt(i), c2 = b.charAt(i);
            if (c1 != c2) return c1 - c2; // se encontrar um caractere diferente, retorna a diferença
            i++;
        }

        return lenA - lenB; // se passar por todos os caracteres e não encontrar diferenças, retorna a diferença de tamanho
    }

    public static void main (String[] args) { // main do programa
        Alvinegra arvore = new Alvinegra(); // criação da árvore binária de busca
        String entrada = sc.nextLine(); // declaração e leitura da entrada
        while (!entrada.equals("FIM")) { // enquanto a entrada não for "FIM"
            int cod = inteiro(entrada); // converte a entrada para inteiro
            File arq = new File("/tmp/games.csv"); // abertura do arquivo para leitura

            try {
                Scanner leitor = new Scanner(arq, "UTF-8"); // leitura do arquivo
                leitor.nextLine(); // ler a primeira linha (cabeçalho) e ignorar

                boolean encontrado = false; // flag para indicar se o jogo foi encontrado
                while (!encontrado && leitor.hasNextLine()) { // ler cada linha do arquivo
                    String linha = leitor.nextLine(); // declaração e leitura da linha

                    int lenLinha = linha.length(); // tamanho da linha
                    int iLin = 0; // índice para percorrer a linha
                    String idGame = ""; // variável para armazenar o id do jogo
                    boolean virgula = false; // flag para indicar se encontrou a vírgula
                    while (iLin < lenLinha && !virgula) { // loop para extrair o id do jogo até a vírgula
                        char c = linha.charAt(iLin);
                        if (c == ',') virgula = true; // sai antes na vírgula
                        else idGame += c; // atribui o caractere ao id do jogo
                        iLin++;
                    }

                    if (cod == inteiro(idGame)) { // se o id da linha for igual ao código procurado
                        Game game = new Game(); // criar uma variável do tipo Game
                        processar(linha, game); // processar a linha e preencher os dados do jogo
                        arvore.inserirMan(game, arvore.raiz); // inserir o jogo na árvore
                        encontrado = true; // marcar como encontrado e sair do loop
                    }
                }
                leitor.close();
            } catch (FileNotFoundException e) { System.err.println(e.getMessage()); }

            entrada = sc.nextLine(); // lê a próxima entrada
        }

        long tempo = 0; // variável para armazenar o tempo de execução
        entrada = sc.nextLine(); // leitura da próxima entrada para pesquisa
        while (!entrada.equals("FIM")) { // enquanto a entrada não for "FIM"
            System.out.print(entrada + ": =>raiz "); // imprime o nome do jogo a ser pesquisado

            long inicio = System.currentTimeMillis(); // marca o tempo de início da execução
            arvore.pesquisar(entrada, arvore.raiz); // chama o método de pesquisa na árvore
            long fim = System.currentTimeMillis(); // marca o tempo de fim da execução
            tempo += fim - inicio; // acumula o tempo de execução

            System.out.println(); // nova linha após a pesquisa
            entrada = sc.nextLine(); // lê a próxima entrada
        }

        // gravação dos dados no arquivo de saída
        try {
            FileWriter fw = new FileWriter("893046_arvoreAlvinegra.txt"); // cria o arquivo de saída
            PrintWriter pw = new PrintWriter(fw); // cria o escritor para o arquivo
            pw.println("Matrícula: 893046\tTempo de execução: " + tempo + "ms\tNúmero de comparações: " + comparacoes); // escreve os dados no arquivo
            pw.close();
        } catch (IOException e) { System.err.println(e.getMessage()); }
    }
}
