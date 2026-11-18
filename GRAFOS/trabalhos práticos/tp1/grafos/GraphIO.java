package grafos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Leitura e escrita de grafos em formato texto simples:
 * <pre>
 *   # comentários começam com '#'
 *   n m            (número de vértices e de arestas)
 *   u v            (m linhas, vértices numerados de 0 a n-1)
 * </pre>
 */
public final class GraphIO {
    private GraphIO() { }

    public static Graph read(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return read(br);
        }
    }

    public static Graph read(BufferedReader br) throws IOException {
        int n = -1, m = -1, k = 0;
        int[] eu = null, ev = null;
        String line;
        while ((line = br.readLine()) != null) {
            int hash = line.indexOf('#');
            if (hash >= 0) line = line.substring(0, hash);
            line = line.trim();
            if (line.isEmpty()) continue;
            StringTokenizer st = new StringTokenizer(line);
            if (n < 0) {
                n = Integer.parseInt(st.nextToken());
                m = Integer.parseInt(st.nextToken());
                eu = new int[m]; ev = new int[m];
            } else {
                if (k >= m) throw new IOException("mais arestas do que o declarado (" + m + ")");
                eu[k] = Integer.parseInt(st.nextToken());
                ev[k] = Integer.parseInt(st.nextToken());
                k++;
            }
        }
        if (n < 0) throw new IOException("arquivo vazio");
        if (k != m) throw new IOException("esperava " + m + " arestas, encontrei " + k);
        checkSimple(n, eu, ev);
        return Graph.fromEdges(n, eu, ev);
    }

    /** Rejeita arestas paralelas (o grafo deve ser simples). Laços são rejeitados por {@link Graph#fromEdges}. */
    public static void checkSimple(int n, int[] eu, int[] ev) throws IOException {
        long[] keys = new long[eu.length];
        for (int e = 0; e < eu.length; e++) {
            long a = Math.min(eu[e], ev[e]), b = Math.max(eu[e], ev[e]);
            keys[e] = a * n + b;
        }
        Arrays.sort(keys);
        for (int i = 1; i < keys.length; i++)
            if (keys[i] == keys[i - 1])
                throw new IOException("aresta paralela encontrada (o grafo deve ser simples): "
                        + (keys[i] / n) + " " + (keys[i] % n));
    }

    public static void write(Graph g, PrintStream out) {
        out.println(g.n + " " + g.m);
        StringBuilder sb = new StringBuilder();
        for (int e = 0; e < g.m; e++) {
            sb.append(g.eu[e]).append(' ').append(g.ev[e]).append('\n');
            if (sb.length() > 1 << 16) { out.print(sb); sb.setLength(0); }
        }
        out.print(sb);
        out.flush();
    }
}
