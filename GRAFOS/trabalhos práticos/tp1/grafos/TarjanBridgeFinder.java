package grafos;

import java.util.Arrays;

/**
 * Algoritmo de Tarjan (1974) para pontes: uma única busca em profundidade que calcula, para cada vértice
 * v, o tempo de descoberta {@code disc[v]} e o menor tempo de descoberta alcançável a partir da subárvore
 * de v usando no máximo uma aresta de retorno, {@code low[v]}. A aresta de árvore (p, v) é ponte
 * se, e somente se, {@code low[v] > disc[p]} (nenhuma aresta de retorno sai da subárvore de v para
 * p ou seus ancestrais). Custo: <b>O(n+m)</b>.
 *
 * <p>A DFS é <b>iterativa</b> (pilha explícita), pois em grafos com 100.000 vértices a recursão estouraria a
 * pilha da JVM. A aresta pela qual se chegou ao vértice é ignorada por <i>identificador</i> (e não pelo
 * vizinho), o que também seria correto com arestas paralelas.
 *
 * <p>Os vetores são alocados uma única vez e reaproveitados entre execuções por "carimbo" inteiro.
 */
public final class TarjanBridgeFinder implements BridgeFinder {
    private final int[] disc, low, pe, it, stack, vis, bridgeMark;
    private int stamp, timer;

    public TarjanBridgeFinder(int n, int m) {
        disc = new int[n]; low = new int[n]; pe = new int[n]; it = new int[n];
        stack = new int[n]; vis = new int[n];
        bridgeMark = new int[m];
    }

    public TarjanBridgeFinder(Graph g) { this(g.n, g.m); }

    @Override public String name() { return "tarjan"; }

    /** Inicia uma nova "sessão" (invalida visitados e marcas de pontes anteriores). */
    void beginSession() {
        if (++stamp == Integer.MAX_VALUE) {
            Arrays.fill(vis, 0); Arrays.fill(bridgeMark, 0); stamp = 1;
        }
        timer = 0;
    }

    /** A aresta e foi marcada como ponte na sessão corrente? */
    boolean isMarked(int e) { return bridgeMark[e] == stamp; }

    /** DFS (de Tarjan) a partir de {@code root}, marcando as pontes da componente de {@code root}. */
    void dfs(Graph g, int root) {
        final int[] start = g.start, deg = g.deg, adjTo = g.adjTo, adjE = g.adjE;
        final int[] disc = this.disc, low = this.low, pe = this.pe, it = this.it, stack = this.stack, vis = this.vis;
        final int st = stamp;
        int sp = 0;
        stack[sp++] = root;
        vis[root] = st; disc[root] = low[root] = timer++; pe[root] = -1; it[root] = start[root];
        while (sp > 0) {
            int v = stack[sp - 1];
            int i = it[v];
            if (i < start[v] + deg[v]) {
                it[v] = i + 1;
                int e = adjE[i];
                if (e == pe[v]) continue;           // não volta pela aresta de chegada
                int w = adjTo[i];
                if (vis[w] != st) {                  // aresta de árvore
                    vis[w] = st; disc[w] = low[w] = timer++; pe[w] = e; it[w] = start[w];
                    stack[sp++] = w;
                } else if (disc[w] < low[v]) {       // aresta de retorno
                    low[v] = disc[w];
                }
            } else {                                 // v terminou
                sp--;
                if (sp > 0) {
                    int p = stack[sp - 1];
                    if (low[v] < low[p]) low[p] = low[v];
                    if (low[v] > disc[p]) bridgeMark[pe[v]] = st; // (p,v) é ponte
                }
            }
        }
    }

    @Override
    public int[] findBridges(Graph g, Deadline dl) {
        beginSession();
        for (int v = 0; v < g.n; v++)
            if (g.deg[v] > 0 && vis[v] != stamp) dfs(g, v);
        int[] tmp = new int[16];
        int cnt = 0;
        for (int e = 0; e < g.m; e++) {
            if (bridgeMark[e] == stamp) {
                if (cnt == tmp.length) tmp = Arrays.copyOf(tmp, cnt * 2);
                tmp[cnt++] = e;
            }
        }
        return Arrays.copyOf(tmp, cnt);
    }
}
