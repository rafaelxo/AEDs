package grafos;

/** Implementações de {@link BridgeStrategy} e fábrica por nome. */
public final class Strategies {
    private Strategies() { }

    /** Nomes aceitos pela linha de comando. */
    public static final String[] NAMES = {"naive", "naive-early", "naive-all", "tarjan"};

    public static BridgeStrategy create(String name, Graph g) {
        switch (name) {
            case "naive":       return new NaiveLazy(g.n, false);
            case "naive-early": return new NaiveLazy(g.n, true);
            case "naive-all":   return new NaiveAll(g.n, g.m);
            case "tarjan":      return new TarjanPerStep(g.n, g.m);
            default: throw new IllegalArgumentException("estratégia desconhecida: " + name
                    + " (use: naive, naive-early, naive-all, tarjan)");
        }
    }

    /**
     * Naïve "sob demanda": só testa as arestas candidatas (incidentes ao vértice corrente) –
     * remove a aresta, testa a conectividade, restaura. Custo por teste: Θ(n+m) (ou menos, com early exit).
     */
    static final class NaiveLazy implements BridgeStrategy {
        private final Reach reach;
        private final boolean early;
        NaiveLazy(int n, boolean early) { this.reach = new Reach(n); this.early = early; }
        @Override public String name() { return early ? "naive-early" : "naive"; }
        @Override public void prepare(Graph g, int cur) { }
        @Override public boolean isBridge(Graph g, int e) {
            return NaiveBridgeFinder.isBridge(g, reach, e, early);
        }
    }

    /**
     * Naïve "completo": a cada passo identifica TODAS as pontes da componente do vértice corrente
     * removendo uma a uma cada aresta e testando a conectividade: O(m·(n+m)) por passo.
     */
    static final class NaiveAll implements BridgeStrategy {
        private final Reach enumReach, testReach;
        private final int[] verts, mark;
        private int stamp;
        private Deadline dl = Deadline.NONE;
        NaiveAll(int n, int m) {
            enumReach = new Reach(n); testReach = new Reach(n);
            verts = new int[n]; mark = new int[m];
        }
        @Override public String name() { return "naive-all"; }
        @Override public void setDeadline(Deadline d) { dl = d; }
        @Override public void prepare(Graph g, int cur) {
            stamp++;
            int cnt = enumReach.component(g, cur, verts);
            for (int k = 0; k < cnt; k++) {
                int x = verts[k];
                int a = g.start[x];
                int d = g.deg[x];
                for (int i = a; i < a + d; i++) {
                    int e = g.adjE[i];
                    if (g.eu[e] != x) continue;           // cada aresta é testada uma única vez
                    dl.check(0);
                    if (NaiveBridgeFinder.isBridge(g, testReach, e, false)) mark[e] = stamp;
                }
            }
        }
        @Override public boolean isBridge(Graph g, int e) { return mark[e] == stamp; }
    }

    /**
     * Tarjan "a cada passo": executa o algoritmo de Tarjan a partir do vértice corrente, obtendo todas as
     * pontes do grafo corrente em O(n+m).
     */
    static final class TarjanPerStep implements BridgeStrategy {
        private final TarjanBridgeFinder core;
        TarjanPerStep(int n, int m) { core = new TarjanBridgeFinder(n, m); }
        @Override public String name() { return "tarjan"; }
        @Override public void prepare(Graph g, int cur) { core.beginSession(); core.dfs(g, cur); }
        @Override public boolean isBridge(Graph g, int e) { return core.isMarked(e); }
    }
}
