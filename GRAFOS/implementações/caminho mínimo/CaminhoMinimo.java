import java.io.*;
import java.util.*;

public class CaminhoMinimo {
    private static class Aresta {
        final int destino;
        final int peso;

        Aresta(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    private static class Grafo {
        final int vertices;
        final int quantidadeArestas;
        final List<Aresta>[] adjacencias;

        Grafo(int vertices, int quantidadeArestas, List<Aresta>[] adjacencias) {
            this.vertices = vertices;
            this.quantidadeArestas = quantidadeArestas;
            this.adjacencias = adjacencias;
        }
    }

    private static class Estado implements Comparable<Estado> {
        final int vertice;
        final long distancia;
        final int quantidadeArestas;

        Estado(int vertice, long distancia, int quantidadeArestas) {
            this.vertice = vertice;
            this.distancia = distancia;
            this.quantidadeArestas = quantidadeArestas;
        }

        @Override
        public int compareTo(Estado outro) {
            int comparacao = Long.compare(this.distancia, outro.distancia);
            if (comparacao != 0) {
                return comparacao;
            }
            return Integer.compare(this.quantidadeArestas, outro.quantidadeArestas);
        }
    }

    private static class Resultado {
        final long custo;
        final int quantidadeArestas;
        final List<Integer> caminho;

        Resultado(long custo, int quantidadeArestas, List<Integer> caminho) {
            this.custo = custo;
            this.quantidadeArestas = quantidadeArestas;
            this.caminho = caminho;
        }

        boolean existeCaminho() {
            return !caminho.isEmpty();
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
                throw new IllegalArgumentException(
                    "Uso: java CaminhoMinimo <arquivo.txt> <origem> <destino>");
            }

            long inicioTotal = System.nanoTime();
            Grafo grafo = lerGrafo(arquivo);
            long fimLeitura = System.nanoTime();

            if (origem < 1 || origem > grafo.vertices ||
                destino < 1 || destino > grafo.vertices) {
                throw new IllegalArgumentException(
                    "Origem e destino devem estar entre 1 e " + grafo.vertices + ".");
            }

            Resultado resultado = encontrarCaminhoMinimo(grafo, origem, destino);
            long fimAlgoritmo = System.nanoTime();

            System.out.println("\nArquivo: " + arquivo);
            System.out.println("Vertices: " + grafo.vertices);
            System.out.println("Arestas do grafo: " + grafo.quantidadeArestas);
            System.out.println("\nOrigem: " + origem + " | Destino: " + destino);
            exibirResultado(resultado);
            System.out.printf(Locale.US, "\nTempo de leitura: %.3f ms%n",
                    (fimLeitura - inicioTotal) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo do algoritmo: %.3f ms%n",
                    (fimAlgoritmo - fimLeitura) / 1_000_000.0);
            System.out.printf(Locale.US, "Tempo total (leitura + algoritmo): %.3f ms%n",
                    (fimAlgoritmo - inicioTotal) / 1_000_000.0);

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Grafo lerGrafo(String nomeArquivo) throws IOException {
        try (LeitorRapido leitor = new LeitorRapido(nomeArquivo)) {
            int n = leitor.proximoInt();
            int m = leitor.proximoInt();

            if (n < 1 || m < 0) {
                throw new IllegalArgumentException(
                    "A quantidade de vertices deve ser positiva e a de arestas nao negativa.");
            }

            List<Aresta>[] adj = (List<Aresta>[]) new List[n + 1];
            for (int i = 1; i <= n; i++) {
                adj[i] = new ArrayList<>();
            }

            for (int i = 0; i < m; i++) {
                int u = leitor.proximoInt();
                int v = leitor.proximoInt();
                int peso = leitor.proximoInt();

                if (u < 1 || u > n || v < 1 || v > n) {
                    throw new IllegalArgumentException(
                        "Aresta " + (i + 1) + ": vertices fora do intervalo de 1 a " + n + ".");
                }
                if (peso <= 0) {
                    throw new IllegalArgumentException(
                        "Aresta " + (i + 1) + ": o peso deve ser estritamente positivo.");
                }

                // Grafo direcionado: adiciona somente u -> v.
                adj[u].add(new Aresta(v, peso));
            }
            return new Grafo(n, m, adj);
        }
    }

    /** Dijkstra com prioridade lexicografica (distancia, numero de arestas). */
    private static Resultado encontrarCaminhoMinimo(Grafo grafo, int origem, int destino) {
        int n = grafo.vertices;
        long[] dist = new long[n + 1];
        int[] arestas = new int[n + 1];
        int[] pai = new int[n + 1];

        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(arestas, Integer.MAX_VALUE);
        Arrays.fill(pai, -1);

        PriorityQueue<Estado> fila = new PriorityQueue<>();
        dist[origem] = 0;
        arestas[origem] = 0;
        fila.add(new Estado(origem, 0, 0));

        while (!fila.isEmpty()) {
            Estado atual = fila.poll();
            int u = atual.vertice;

            // Descarta estados antigos, inclusive quando houve desempate por arestas.
            if (atual.distancia != dist[u] ||
                atual.quantidadeArestas != arestas[u]) {
                continue;
            }

            // Com pesos positivos, a primeira retirada valida do destino e otima.
            if (u == destino) {
                break;
            }

            for (Aresta a : grafo.adjacencias[u]) {
                int v = a.destino;
                long novaDistancia = atual.distancia + a.peso;
                int novasArestas = atual.quantidadeArestas + 1;

                if (novaDistancia < dist[v] ||
                    (novaDistancia == dist[v] && novasArestas < arestas[v])) {
                    dist[v] = novaDistancia;
                    arestas[v] = novasArestas;
                    pai[v] = u;
                    fila.add(new Estado(v, novaDistancia, novasArestas));
                }
            }
        }

        if (dist[destino] == Long.MAX_VALUE) {
            return new Resultado(-1, -1, Collections.emptyList());
        }

        List<Integer> caminho = new ArrayList<>();
        for (int v = destino; v != -1; v = pai[v]) {
            caminho.add(v);
        }
        Collections.reverse(caminho);
        return new Resultado(dist[destino], arestas[destino], caminho);
    }

    private static void exibirResultado(Resultado resultado) {
        if (!resultado.existeCaminho()) {
            System.out.println("Nao existe caminho direcionado entre a origem e o destino.");
            return;
        }

        System.out.println("Custo total (distancia): " + resultado.custo);
        System.out.println("Quantidade de arestas no caminho: " + resultado.quantidadeArestas);
        System.out.print("Caminho: ");
        for (int i = 0; i < resultado.caminho.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print(resultado.caminho.get(i));
        }
        System.out.println();
    }

    private static class LeitorRapido implements Closeable {
        private final BufferedReader leitor;
        private StringTokenizer tokens;

        LeitorRapido(String nomeArquivo) throws IOException {
            leitor = new BufferedReader(new FileReader(nomeArquivo), 65536);
        }

        String proximo() throws IOException {
            while (tokens == null || !tokens.hasMoreTokens()) {
                String linha = leitor.readLine();
                if (linha == null) {
                    return null;
                }
                tokens = new StringTokenizer(linha);
            }
            return tokens.nextToken();
        }

        int proximoInt() throws IOException {
            String token = proximo();
            if (token == null) {
                throw new EOFException("Arquivo incompleto: faltam valores do grafo.");
            }
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new IOException("Valor inteiro invalido no arquivo: " + token, e);
            }
        }

        @Override
        public void close() throws IOException {
            leitor.close();
        }
    }
}
