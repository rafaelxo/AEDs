import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Gera 8 instancias reproduziveis de grafos direcionados com capacidade 1.
 * Tipo 1 (esparso): m = 4*n
 * Tipo 2 (denso):   m = n*(n-1)/8
 *
 * Garantias:
 * - vertices numerados de 1 a n;
 * - nenhuma aresta u->u nem aresta direcionada duplicada;
 * - grafos fortemente conexos, pelo ciclo 1->2->...->n->1;
 * - semente fixa para reproduzir os arquivos de teste.
 */
public class GeradorDeGrafos {
    private static final long SEMENTE_BASE = 20261103L;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso: java GeradorDeGrafos [diretorio_de_saida]");
            System.exit(1);
        }

        Path diretorio = Paths.get(args.length == 1 ? args[0] : ".");
        int[] tamanhosTipo1 = {200, 500, 1000, 2000};
        int[] tamanhosTipo2 = {100, 200, 400, 800};

        try {
            Files.createDirectories(diretorio);
            System.out.println("Gerando grafos direcionados com capacidade unitaria...");

            for (int n : tamanhosTipo1) {
                int m = 4 * n;
                String arquivo = "grafo_esparso_" + n + ".txt";
                gerarGrafo(n, m, diretorio.resolve(arquivo).toString(), SEMENTE_BASE + 1_000_003L + n);
                System.out.println("Tipo 1 (esparso): " + arquivo + " | V=" + n + " | A=" + m);
            }

            for (int n : tamanhosTipo2) {
                int m = Math.toIntExact((long) n * (n - 1) / 8);
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

    public static void gerarGrafo(int n, int m, String nomeArquivo, long semente) throws IOException {
        if (n < 2) {
            throw new IllegalArgumentException("O numero de vertices deve ser pelo menos 2.");
        }
        long maximoArestas = (long) n * (n - 1);
        if (m < n || m > maximoArestas) {
            throw new IllegalArgumentException("Para garantir conectividade forte e arestas " + "unicas, m deve estar entre n e " + maximoArestas + ".");
        }

        Random aleatorio = new Random(semente);
        Set<Long> arestasUsadas = new HashSet<>();

        try (BufferedWriter saida = new BufferedWriter(Files.newBufferedWriter(Paths.get(nomeArquivo), StandardCharsets.UTF_8), 65536)) {
            saida.write(n + " " + m);
            saida.newLine();

            for (int u = 1; u <= n; u++) {
                int v = (u == n) ? 1 : u + 1;
                registrarAresta(saida, arestasUsadas, n, u, v);
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

                registrarAresta(saida, arestasUsadas, n, u, v);
                quantidadeCriada++;
            }
        }
    }

    private static void registrarAresta(BufferedWriter saida, Set<Long> usadas, int n, int u, int v) throws IOException {
        usadas.add(chaveAresta(n, u, v));
        saida.write(u + " " + v);
        saida.newLine();
    }

    private static long chaveAresta(int n, int u, int v) {
        return (long) u * (n + 1L) + v;
    }
}
