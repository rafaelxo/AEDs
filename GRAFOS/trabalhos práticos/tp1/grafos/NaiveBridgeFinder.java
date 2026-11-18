package grafos;

import java.util.Arrays;

/**
 * Método <b>naïve</b> para identificar pontes: para cada aresta {@code e = (u,v)}, remove-se {@code e},
 * testa-se a conectividade (v ainda é alcançável a partir de u?) e devolve-se {@code e} ao grafo.
 * A aresta é ponte se, e somente se, a remoção a desconecta.
 *
 * <p>Custo: m testes de conectividade, cada um Θ(n+m) &rArr; <b>O(m·(n+m))</b>.
 *
 * <p>Parâmetro {@code earlyExit}: se {@code false} (padrão do "naïve puro") cada teste percorre toda a
 * componente de u; se {@code true} a busca para assim que v é encontrado (otimização que não altera o
 * resultado, apenas reduz o custo dos testes em que a aresta <i>não</i> é ponte).
 */
public final class NaiveBridgeFinder implements BridgeFinder {
    private final boolean earlyExit;

    public NaiveBridgeFinder() { this(false); }
    public NaiveBridgeFinder(boolean earlyExit) { this.earlyExit = earlyExit; }

    @Override public String name() { return earlyExit ? "naive-early" : "naive"; }

    @Override
    public int[] findBridges(Graph g, Deadline dl) {
        Reach reach = new Reach(g.n);
        int[] tmp = new int[16];
        int cnt = 0;
        for (int e = 0; e < g.m; e++) {
            if (!g.isAlive(e)) continue;
            dl.check(e);
            if (isBridge(g, reach, e, earlyExit)) {
                if (cnt == tmp.length) tmp = Arrays.copyOf(tmp, cnt * 2);
                tmp[cnt++] = e;
            }
        }
        return Arrays.copyOf(tmp, cnt);
    }

    /** Teste naïve para uma única aresta: remove, testa conectividade, restaura. */
    static boolean isBridge(Graph g, Reach reach, int e, boolean earlyExit) {
        int u = g.eu[e], v = g.ev[e];
        g.removeEdge(e);
        boolean connected = reach.reaches(g, u, v, earlyExit);
        g.restoreLastRemoved();
        return !connected;
    }
}
