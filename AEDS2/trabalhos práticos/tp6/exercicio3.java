import java.util.*;
import java.io.*;

public class exercicio3 {
    public static Scanner sc = new Scanner(System.in); // scanner para leitura de entrada

    static class Celula { // classe célula para a pilha
        private Game game; // atributo para armazenar o jogo
        private Celula prox; // referência para a próxima célula

        public Celula () { // construtor padrão
            this.game = null; // inicializa o jogo como nulo
            this.prox = null; // inicializa a próxima célula como nula
        }

        public Celula (Game game) { // construtor com parâmetro
            this.game = game; // inicializa o jogo com o valor fornecido
            this.prox = null; // inicializa a próxima célula como nula
        }
    }

    static class Pilha { // classe pilha
        private Celula topo; // referência para o topo da pilha

        public Pilha() { // construtor padrão
            this.topo = null; // inicializa o topo como nulo
        }

        public void inserir (Game game) { // método para inserir um jogo na pilha
            Celula tmp = new Celula(game); // cria uma nova célula com o jogo
            tmp.prox = topo; // aponta a próxima célula para o topo atual
            topo = tmp; // atualiza o topo para a nova célula
        }

        public Game remover () { // método para remover um jogo da pilha
            if (topo == null) throw new IllegalArgumentException("Erro!"); // se a pilha estiver vazia, lança exceção
            Game resp = topo.game; // armazena o jogo do topo
            Celula tmp = topo; // variável temporária para o topo
            topo = tmp.prox; // atualiza o topo para a próxima célula
            tmp.prox = null; // desvincula a célula removida
            return resp; // retorna o jogo removido
        }

        public int tamanho () { // método para calcular o tamanho da pilha
            int tamanho = 0; // variável para armazenar o tamanho
            for (Celula i = topo; i != null; i = i.prox) tamanho++; // percorre a pilha e conta as células
            return tamanho; // retorna o tamanho calculado
        }

        public void mostrarInv (Celula i, int j) { // método para mostrar a pilha de forma inversa
            if (i != null) { // se a célula não for nula
                mostrarInv(i.prox, j - 1); // chamada recursiva para a próxima célula
                System.out.println("[" + j + "] " + i.game.mostrar()); // imprime o jogo da célula atual
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

    public static String[] separarComando (String info) { // método para separar a informação e seus parâmetros
        if (info == null || info.length() == 0) return new String[0]; // tratamento para string nula ou vazia

        String[] partes = new String[3]; // array para armazenar as partes da informação
        int n = 0; // contador de partes
        String parte = ""; // variável temporária para armazenar a parte atual

        int len = info.length(); // tamanho da string
        for (int i = 0; i < len; i++) { // loop para percorrer os caracteres da string
            char c = info.charAt(i);
            if (c == ' ') { // se encontrar um espaço, finaliza a parte
                if (parte.length() > 0) partes[n++] = parte; // armazena a parte no array se não estiver vazia
                parte = ""; // reseta a variável temporária
            } else parte += c; // adiciona o caractere à parte atual
        }
        if (parte.length() > 0) partes[n++] = parte; // armazena a última parte se não estiver vazia

        return partes; // retorna o array com as partes da informação
    }

    public static void main (String[] args) { // main do programa
        Pilha pilha = new Pilha(); // criação da pilha para armazenar os jogos
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
                        pilha.inserir(game); // inserir o jogo na pilha
                        encontrado = true; // marcar como encontrado e sair do loop
                    }
                }
                leitor.close();
            } catch (FileNotFoundException e) { System.err.println(e.getMessage()); }

            entrada = sc.nextLine(); // lê a próxima entrada
        }

        int n = sc.nextInt(); // lê o número de operações
        sc.nextLine(); // consome a quebra de linha após o número
        for (int i = 0; i < n; i++) {
            String info = sc.nextLine(); // lê o comando
            String[] partes = separarComando(info); // separa o comando em partes

            String comando = partes[0]; // obtém o comando
            switch (comando) {
                case "I":
                    File arq = new File("/tmp/games.csv"); // abertura do arquivo para leitura
                    int cod = inteiro(partes[1]); // obtém o código do jogo

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
                                pilha.inserir(game); // inserir o jogo no início da pilha
                                encontrado = true; // marcar como encontrado e sair do loop
                            }
                        }
                        leitor.close();
                    } catch (FileNotFoundException e) { System.err.println(e.getMessage()); }
                    break;

                case "R": System.out.println("(R) " + pilha.remover().getName()); // remove da pilha e imprime o nome do jogo removido
                    break;
            }
        }
        int j = pilha.tamanho() - 1; // índice para exibir os jogos em ordem inversa
        pilha.mostrarInv(pilha.topo, j); // mostra a pilha de forma inversa
    }
}
