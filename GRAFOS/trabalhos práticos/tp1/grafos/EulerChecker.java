package grafos;

/**
 * Condições de existência de caminho/ciclo euleriano (Euler, 1736), em tempo O(n+m):
 * um grafo (ignorando vértices isolados) possui caminho euleriano se, e somente se, é conexo e tem 0 ou 2
 * vértices de grau ímpar.
 * <ul>
 *   <li>0 vértices ímpares &rArr; <b>euleriano</b> (existe ciclo euleriano; Fleury começa em qualquer vértice com grau &gt; 0);</li>
 *   <li>2 vértices ímpares &rArr; <b>semi-euleriano</b> (existe caminho, mas não ciclo; deve começar em um dos ímpares);</li>
 *   <li>caso contrário &rArr; <b>não euleriano</b>.</li>
 * </ul>
 */
public final class EulerChecker {
    private EulerChecker() { }

    public enum Type {
        EULERIAN("euleriano"), SEMI_EULERIAN("semi-euleriano"), NON_EULERIAN("não euleriano");
        public final String label;
        Type(String label) { this.label = label; }
    }

    /** Resultado da análise: tipo, vértice inicial recomendado para Fleury e contagem de vértices ímpares. */
    public static final class Info {
        public final Type type;
        public final int startVertex;
        public final int oddCount;
        public final boolean connected;
        Info(Type type, int startVertex, int oddCount, boolean connected) {
            this.type = type; this.startVertex = startVertex; this.oddCount = oddCount; this.connected = connected;
        }
    }

    public static Info analyze(Graph g) {
        int odd = 0, firstOdd = -1, firstNonIsolated = -1, nonIsolated = 0;
        for (int v = 0; v < g.n; v++) {
            int d = g.deg[v];
            if (d > 0) {
                nonIsolated++;
                if (firstNonIsolated < 0) firstNonIsolated = v;
                if ((d & 1) == 1) { odd++; if (firstOdd < 0) firstOdd = v; }
            }
        }
        if (nonIsolated == 0) // grafo sem arestas: caminho trivial
            return new Info(Type.EULERIAN, g.n > 0 ? 0 : -1, 0, true);
        if (odd != 0 && odd != 2) // barato: descarta pela paridade antes de testar conectividade
            return new Info(Type.NON_EULERIAN, -1, odd, false /* não verificado */);
        boolean connected = new Reach(g.n).countReachable(g, firstNonIsolated) == nonIsolated;
        if (!connected) return new Info(Type.NON_EULERIAN, -1, odd, false);
        return odd == 0
                ? new Info(Type.EULERIAN, firstNonIsolated, 0, true)
                : new Info(Type.SEMI_EULERIAN, firstOdd, 2, true);
    }
}
