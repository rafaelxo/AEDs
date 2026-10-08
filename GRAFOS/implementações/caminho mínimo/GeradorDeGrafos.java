import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Gera 8 instancias reproduziveis de grafos direcionados e ponderados.
 * Tipo 1 (esparso): m = 3*n
 * Tipo 2 (denso):   m = n*n/4
 *
 * Garantias:
 * - vertices de 1 a n;
 * - pesos inteiros positivos de 1 a 20;
 * - nenhuma aresta u->u e nenhuma aresta direcionada duplicada;
 * - fortemente conexos: o ciclo 1->2->...->n->1 garante um
 *   caminho entre quaisquer dois vertices;
 * - a semente fixa permite gerar exatamente os mesmos arquivos.
 *
 * Execucao: java GeradorDeGrafos
 * ATENCAO: sobrescreve arquivos de mesmo nome na pasta atual.
 */
public class GeradorDeGrafos {
    private static final long SEMENTE_BASE = 20261008L;

    public static void main(String[] args) {
        int[] tamanhosTipo1 = {1000, 5000, 10000, 50000};
        int[] tamanhosTipo2 = {100, 500, 1000, 2000};

        try {
            System.out.println("Gerando grafos direcionados com pesos positivos...");

            for (int n : tamanhosTipo1) {
                int m = 3 * n;
                String arquivo = "grafo_tipo1_" + n + ".txt";
                gerarGrafo(n, m, arquivo, SEMENTE_BASE + 1_000_003L + n);
                System.out.println("Tipo 1 (esparso): " + arquivo +
                        " | V=" + n + " | A=" + m);
            }

            for (int n : tamanhosTipo2) {
                int m = (n * n) / 4;
                String arquivo = "grafo_tipo2_" + n + ".txt";
                gerarGrafo(n, m, arquivo, SEMENTE_BASE + 2_000_003L + n);
                System.out.println("Tipo 2 (denso): " + arquivo +
                        " | V=" + n + " | A=" + m);
            }

            System.out.println("\nOs 8 grafos foram gerados com sucesso!");
            System.out.println("Semente base: " + SEMENTE_BASE);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro ao gerar os grafos: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void gerarGrafo(int n, int m, String nomeArquivo, long semente)
            throws IOException {
        if (n < 2) {
            throw new IllegalArgumentException("O numero de vertices deve ser pelo menos 2.");
        }
        long maximoArestas = (long) n * (n - 1);
        if (m < n || m > maximoArestas) {
            throw new IllegalArgumentException("Para garantir conectividade forte e arestas " +
                "unicas, m deve estar entre n e " + maximoArestas + ".");
        }

        Random aleatorio = new Random(semente);
        // A codificacao (u, v) -> u*(n+1)+v identifica unicamente cada arco.
        Set<Long> arestasUsadas = new HashSet<>();

        try (BufferedWriter saida = new BufferedWriter(new FileWriter(nomeArquivo), 65536)) {
            saida.write(n + " " + m);
            saida.newLine();

            // Reserva n arestas que formam um ciclo direcionado e garantem
            // um caminho entre qualquer par de vertices.
            for (int u = 1; u <= n; u++) {
                int v = (u == n) ? 1 : u + 1;
                registrarAresta(saida, arestasUsadas, n, u, v,
                        aleatorio.nextInt(20) + 1);
            }

            // Completa a quantidade de arestas sem lacos nem duplicatas.
            int quantidadeCriada = n;
            while (quantidadeCriada < m) {
                int u = aleatorio.nextInt(n) + 1;
                int v = aleatorio.nextInt(n) + 1;
                if (u == v) {
                    continue;
                }

                long chave = chaveAresta(n, u, v);
                if (arestasUsadas.contains(chave)) {
                    continue;
                }

                registrarAresta(saida, arestasUsadas, n, u, v,
                        aleatorio.nextInt(20) + 1);
                quantidadeCriada++;
            }
        }
    }

    private static void registrarAresta(BufferedWriter saida, Set<Long> usadas,
                                       int n, int u, int v, int peso) throws IOException {
        usadas.add(chaveAresta(n, u, v));
        saida.write(u + " " + v + " " + peso);
        saida.newLine();
    }

    private static long chaveAresta(int n, int u, int v) {
        return (long) u * (n + 1L) + v;
    }
}
