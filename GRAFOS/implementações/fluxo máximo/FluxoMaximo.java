import java.io.*;
import java.util.*;

public class FluxoMaximo {
    private static class Aresta {
        final int destino;
        final int indiceReversa;
        final boolean original;
        int capacidade;

        Aresta(int destino, int indiceReversa, int capacidade, boolean original) {
            this.destino = destino;
            this.indiceReversa = indiceReversa;
            this.capacidade = capacidade;
            this.original = original;
        }
    }

    private static class Grafo {
        final int vertices;
        final int quantidadeArestas;
        final List<Aresta>[] adjacencias;
        final int[] nivel;
        final int[] proximaAresta;

        @SuppressWarnings({"unchecked", "rawtypes"})
        Grafo(int vertices, int quantidadeArestas) {
            this.vertices = vertices;
            this.quantidadeArestas = quantidadeArestas;
            this.adjacencias = (List<Aresta>[]) new List[vertices + 1];
            for (int v = 1; v <= vertices; v++) {
                adjacencias[v] = new ArrayList<>();
            }
            this.nivel = new int[vertices + 1];
            this.proximaAresta = new int[vertices + 1];
        }

        void adicionarAresta(int origem, int destino) {
            if (origem == destino) {
                return;
            }

            Aresta ida = new Aresta(destino, adjacencias[destino].size(), 1, true);
            Aresta volta = new Aresta(origem, adjacencias[origem].size(), 0, false);
            adjacencias[origem].add(ida);
            adjacencias[destino].add(volta);
        }

        boolean construirNiveis(int origem, int destino) {
            Arrays.fill(nivel, -1);
            int[] fila = new int[vertices + 1];
            int inicio = 0;
            int fim = 0;

            fila[fim++] = origem;
            nivel[origem] = 0;

            while (inicio < fim) {
                int u = fila[inicio++];
                for (Aresta a : adjacencias[u]) {
                    if (a.capacidade > 0 && nivel[a.destino] == -1) {
                        nivel[a.destino] = nivel[u] + 1;
                        fila[fim++] = a.destino;
                    }
                }
            }
            return nivel[destino] != -1;
        }

        int enviarFluxo(int u, int destino, int limite) {
            if (u == destino) {
                return limite;
            }

            List<Aresta> saidas = adjacencias[u];
            while (proximaAresta[u] < saidas.size()) {
                Aresta a = saidas.get(proximaAresta[u]);
                if (a.capacidade > 0 && nivel[a.destino] == nivel[u] + 1) {
                    int enviado = enviarFluxo(a.destino, destino, Math.min(limite, a.capacidade));
                    if (enviado > 0) {
                        a.capacidade -= enviado;
                        adjacencias[a.destino].get(a.indiceReversa).capacidade += enviado;
                        return enviado;
                    }
                }
                proximaAresta[u]++;
            }
            return 0;
        }

        int calcularFluxoMaximo(int origem, int destino) {
            int fluxo = 0;
            while (construirNiveis(origem, destino)) {
                Arrays.fill(proximaAresta, 0);
                int enviado;
                while ((enviado = enviarFluxo(origem, destino, Integer.MAX_VALUE)) > 0) {
                    fluxo += enviado;
                }
            }
            return fluxo;
        }

        List<List<Integer>> extrairCaminhos(int origem, int destino, int fluxo) {
            List<List<Integer>> caminhos = new ArrayList<>();

            for (int k = 0; k < fluxo; k++) {
                boolean[] visitado = new boolean[vertices + 1];
                int[] pai = new int[vertices + 1];
                Aresta[] arestaPai = new Aresta[vertices + 1];
                int[] fila = new int[vertices + 1];
                int inicio = 0;
                int fim = 0;

                visitado[origem] = true;
                fila[fim++] = origem;

                while (inicio < fim && !visitado[destino]) {
                    int u = fila[inicio++];
                    for (Aresta a : adjacencias[u]) {
                        if (!a.original || visitado[a.destino]) {
                            continue;
                        }

                        int fluxoNaAresta = adjacencias[a.destino].get(a.indiceReversa).capacidade;
                        if (fluxoNaAresta > 0) {
                            visitado[a.destino] = true;
                            pai[a.destino] = u;
                            arestaPai[a.destino] = a;
                            fila[fim++] = a.destino;
                            if (a.destino == destino) {
                                break;
                            }
                        }
                    }
                }

                if (!visitado[destino]) {
                    throw new IllegalStateException("Falha ao decompor o fluxo maximo em caminhos.");
                }

                List<Integer> caminho = new ArrayList<>();
                for (int v = destino; v != origem; v = pai[v]) {
                    caminho.add(v);
                    Aresta ida = arestaPai[v];
                    adjacencias[v].get(ida.indiceReversa).capacidade--;
                    ida.capacidade++;
                }
                caminho.add(origem);
                Collections.reverse(caminho);
                caminhos.add(caminho);
            }
            return caminhos;
        }
    }

    private static class Resultado {
        final int quantidadeCaminhos;
        final int totalArestasUsadas;
        final List<List<Integer>> caminhos;

        Resultado(int quantidadeCaminhos, List<List<Integer>> caminhos) {
            this.quantidadeCaminhos = quantidadeCaminhos;
            this.caminhos = caminhos;
            int soma = 0;
            for (List<Integer> caminho : caminhos) {
                soma += caminho.size() - 1;
            }
            this.totalArestasUsadas = soma;
        }
    }

    public static void main(String[] args) {
        String arquivo;
        int origem;
        int destino;

        try {
            if (args.length == 3) {
                arquivo = args[0];
                origem = Integer.parseInt(args[1]);
                destino = Integer.parseInt(args[2]);
            } else if (args.length == 0) {
                Scanner teclado = new Scanner(System.in);
                System.out.print("Insira o nome do arquivo: ");
                arquivo = teclado.nextLine().trim();
                System.out.print("Vertice de origem: ");
                origem = Integer.parseInt(teclado.nextLine().trim());
                System.out.print("Vertice de destino: ");
                destino = Integer.parseInt(teclado.nextLine().trim());
            } else {
                throw new IllegalArgumentException("Uso: java FluxoMaximo <arquivo.txt> <origem> <destino>");
            }

            long inicioTotal = System.nanoTime();
            Grafo grafo = lerGrafo(arquivo);
            long fimLeitura = System.nanoTime();

            if (origem < 1 || origem > grafo.vertices ||
                destino < 1 || destino > grafo.vertices || origem == destino) {
                throw new IllegalArgumentException("Origem e destino devem ser distintos e estar entre 1 e " + grafo.vertices + ".");
            }

            int fluxo = grafo.calcularFluxoMaximo(origem, destino);
            long fimFluxo = System.nanoTime();
            List<List<Integer>> caminhos = grafo.extrairCaminhos(origem, destino, fluxo);
            Resultado resultado = new Resultado(fluxo, caminhos);
            long fimAlgoritmo = System.nanoTime();

            System.out.println("\nArquivo: " + arquivo);
            System.out.println("Vertices: " + grafo.vertices);
            System.out.println("Arestas do grafo: " + grafo.quantidadeArestas);
            System.out.println("\nOrigem: " + origem + " | Destino: " + destino);
            exibirResultado(resultado);
            System.out.printf(Locale.US, "\nTempo de leitura: %.3f ms%n", (fimLeitura - inicioTotal) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo do fluxo maximo: %.3f ms%n", (fimFluxo - fimLeitura) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo de extracao dos caminhos: %.3f ms%n", (fimAlgoritmo - fimFluxo) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo do algoritmo: %.3f ms%n", (fimAlgoritmo - fimLeitura) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo total (leitura + algoritmo): %.3f ms%n", (fimAlgoritmo - inicioTotal) / 1_000_000.0);
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Grafo lerGrafo(String nomeArquivo) throws IOException {
        try (LeitorRapido leitor = new LeitorRapido(nomeArquivo)) {
            int n = leitor.proximoInt();
            int m = leitor.proximoInt();

            if (n < 2 || m < 0) {
                throw new IllegalArgumentException("A quantidade de vertices deve ser pelo menos 2 e a de arestas nao negativa.");
            }

            Grafo grafo = new Grafo(n, m);
            for (int i = 0; i < m; i++) {
                int u = leitor.proximoInt();
                int v = leitor.proximoInt();
                if (u < 1 || u > n || v < 1 || v > n) {
                    throw new IllegalArgumentException("Aresta " + (i + 1) + ": vertices fora do intervalo de 1 a " + n + ".");
                }
                grafo.adicionarAresta(u, v);
            }

            if (leitor.proximoIntOpcional() != null) {
                throw new IOException("Arquivo contem dados excedentes apos as " + m + " arestas declaradas.");
            }
            return grafo;
        }
    }

    private static void exibirResultado(Resultado resultado) {
        System.out.println("Quantidade maxima de caminhos disjuntos em arestas: " + resultado.quantidadeCaminhos);
        if (resultado.quantidadeCaminhos == 0) {
            System.out.println("Nao existe caminho direcionado entre a origem e o destino.");
            System.out.println("Total de arestas utilizadas nos caminhos: 0");
            return;
        }

        for (int i = 0; i < resultado.caminhos.size(); i++) {
            List<Integer> caminho = resultado.caminhos.get(i);
            System.out.print("Caminho " + (i + 1) + " (" + (caminho.size() - 1) + " arestas): ");
            for (int j = 0; j < caminho.size(); j++) {
                if (j > 0) {
                    System.out.print(" -> ");
                }
                System.out.print(caminho.get(j));
            }
            System.out.println();
        }
        System.out.println("Total de arestas utilizadas nos caminhos: " + resultado.totalArestasUsadas);
    }

    private static class LeitorRapido implements Closeable {
        private final InputStream entrada;
        private final byte[] buffer = new byte[65536];
        private int posicao = 0;
        private int tamanho = 0;

        LeitorRapido(String nomeArquivo) throws IOException {
            entrada = new BufferedInputStream(new FileInputStream(nomeArquivo), 65536);
        }

        private int proximoByte() throws IOException {
            if (posicao >= tamanho) {
                tamanho = entrada.read(buffer);
                posicao = 0;
                if (tamanho < 0) {
                    return -1;
                }
            }
            return buffer[posicao++] & 0xff;
        }

        Integer proximoIntOpcional() throws IOException {
            int c;
            do {
                c = proximoByte();
            } while (c >= 0 && c <= ' ');

            if (c == -1) {
                return null;
            }
            boolean negativo = c == '-';
            if (negativo) {
                c = proximoByte();
            }
            if (c < '0' || c > '9') {
                throw new IOException("Valor inteiro invalido no arquivo.");
            }

            long numero = 0;
            while (c >= '0' && c <= '9') {
                numero = numero * 10 + c - '0';
                if (numero > 2147483648L) {
                    throw new IOException("Valor inteiro fora do intervalo permitido.");
                }
                c = proximoByte();
            }
            if (c > ' ') {
                throw new IOException("Caractere invalido no arquivo.");
            }

            long valor = negativo ? -numero : numero;
            if (valor < Integer.MIN_VALUE || valor > Integer.MAX_VALUE) {
                throw new IOException("Valor inteiro fora do intervalo permitido.");
            }
            return (int) valor;
        }

        int proximoInt() throws IOException {
            Integer valor = proximoIntOpcional();
            if (valor == null) {
                throw new EOFException("Arquivo incompleto: faltam valores do grafo.");
            }
            return valor;
        }

        @Override
        public void close() throws IOException {
            entrada.close();
        }
    }
}
