import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Gera 8 instancias reproduziveis de grafos direcionados e ponderados.
 * Esparso: m = 3*n
 * Denso:   m = n*n/4
 *
 * Garantias:
 * - vertices de 1 a n;
 * - pesos inteiros positivos de 1 a 20;
 * - nenhuma aresta u->u e nenhuma aresta direcionada duplicada;
 * - fortemente conexos: o ciclo 1->2->...->n->1 garante um caminho entre quaisquer dois vertices;
 * - a semente fixa permite gerar exatamente os mesmos arquivos.
 */
public class GeradorDeGrafos {
    private static final long SEMENTE_BASE = 20261008L;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso: java GeradorDeGrafos [diretorio_de_saida]");
            System.exit(1);
        }
        Path diretorio = Path.of(args.length == 1 ? args[0] : ".");
        int[] tamanhosTipo1 = {1000, 5000, 10000, 50000};
        int[] tamanhosTipo2 = {100, 500, 1000, 2000};

        try {
            Files.createDirectories(diretorio);
            System.out.println("Gerando grafos direcionados com pesos positivos...");

            for (int n : tamanhosTipo1) {
                int m = 3 * n;
                String arquivo = "grafo_esparso_" + n + ".txt";
                gerarGrafo(n, m, diretorio.resolve(arquivo).toString(), SEMENTE_BASE + 1_000_003L + n);
                System.out.println("Tipo 1 (esparso): " + arquivo + " | V=" + n + " | A=" + m);
            }

            for (int n : tamanhosTipo2) {
                int m = Math.toIntExact((long) n * n / 4);
                String arquivo = "grafo_denso_" + n + ".txt";
                gerarGrafo(n, m, diretorio.resolve(arquivo).toString(), SEMENTE_BASE + 2_000_003L + n);
                System.out.println("Tipo 2 (denso): " + arquivo + " | V=" + n + " | A=" + m);
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
            throw new IllegalArgumentException("Para garantir conectividade forte e arestas " + "unicas, m deve estar entre n e " + maximoArestas + ".");
        }

        Random aleatorio = new Random(semente);
        Set<Long> arestasUsadas = new HashSet<>();

        try (BufferedWriter saida = new BufferedWriter(Files.newBufferedWriter(Path.of(nomeArquivo), StandardCharsets.UTF_8), 65536)) {
            saida.write(n + " " + m);
            saida.newLine();

            for (int u = 1; u <= n; u++) {
                int v = (u == n) ? 1 : u + 1;
                registrarAresta(saida, arestasUsadas, n, u, v, aleatorio.nextInt(20) + 1);
            }

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

                registrarAresta(saida, arestasUsadas, n, u, v, aleatorio.nextInt(20) + 1);
                quantidadeCriada++;
            }
        }
    }

    private static void registrarAresta(BufferedWriter saida, Set<Long> usadas, int n, int u, int v, int peso) throws IOException {
        usadas.add(chaveAresta(n, u, v));
        saida.write(u + " " + v + " " + peso);
        saida.newLine();
    }

    private static long chaveAresta(int n, int u, int v) {
        return (long) u * (n + 1L) + v;
    }
}
