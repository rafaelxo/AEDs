package grafos;

import java.util.Arrays;

/**
 * Validador independente: confere se uma sequência de vértices é de fato um caminho euleriano do grafo
 * ORIGINAL, isto é, se cada par de vértices consecutivos é uma aresta do grafo e se todas as m arestas
 * aparecem exatamente uma vez. (Não usa nada do Fleury nem das estruturas de remoção: compara
 * conjuntos ordenados de chaves de arestas.)
 */
public final class EulerPathValidator {
    private EulerPathValidator() { }

    /** Retorna {@code null} se válido; senão, uma mensagem descrevendo o problema. */
    public static String validate(Graph g, int[] path) {
        int m = g.m;
        if (m == 0) return path.length <= 1 ? null : "grafo sem arestas, mas o caminho tem passos";
        if (path.length != m + 1) return "tamanho do caminho " + path.length + " != m+1 = " + (m + 1);
        long n = g.n;
        long[] orig = new long[m];
        for (int e = 0; e < m; e++) orig[e] = key(g.eu[e], g.ev[e], n);
        long[] used = new long[m];
        for (int k = 0; k < m; k++) {
            int a = path[k], b = path[k + 1];
            if (a < 0 || a >= n || b < 0 || b >= n) return "vértice fora do intervalo no passo " + k;
            used[k] = key(a, b, n);
        }
        Arrays.sort(orig);
        Arrays.sort(used);
        for (int i = 0; i < m; i++) {
            if (orig[i] != used[i]) {
                return "o multiconjunto de arestas do caminho difere do grafo (aresta repetida ou inexistente), "
                        + "primeira diferença na posição ordenada " + i;
            }
        }
        return null;
    }

    private static long key(int a, int b, long n) {
        return a < b ? a * n + b : b * n + a;
    }
}
