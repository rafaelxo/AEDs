package grafos;

/**
 * Travessia em largura (BFS) reutilizável, sem realocar memória a cada chamada
 * (marcação por "carimbo" inteiro em vez de limpar um vetor de visitados).
 *
 * <p>É a primitiva de <i>teste de conectividade</i> usada pelo método naïve: remove-se a aresta
 * {@code (u,v)} e verifica-se se {@code v} ainda é alcançável a partir de {@code u}.
 */
public final class Reach {
    private final int[] mark;
    private final int[] queue;
    private int stamp;

    public Reach(int n) {
        mark = new int[n];
        queue = new int[n];
    }

    private int nextStamp() {
        if (++stamp == Integer.MAX_VALUE) { java.util.Arrays.fill(mark, 0); stamp = 1; }
        return stamp;
    }

    /**
     * BFS a partir de {@code s} no grafo corrente. Retorna {@code true} se {@code t} foi alcançado.
     *
     * @param earlyExit se {@code true}, interrompe a busca assim que {@code t} é descoberto
     *                  (otimização); se {@code false}, percorre TODA a componente de {@code s}
     *                  (teste de conectividade "puro", custo Θ(n+m) da componente).
     */
    public boolean reaches(Graph g, int s, int t, boolean earlyExit) {
        final int st = nextStamp();
        final int[] start = g.start, deg = g.deg, adjTo = g.adjTo, mark = this.mark, queue = this.queue;
        int head = 0, tail = 0;
        queue[tail++] = s;
        mark[s] = st;
        if (earlyExit && s == t) return true;
        while (head < tail) {
            int x = queue[head++];
            int a = start[x], b = a + deg[x];
            for (int i = a; i < b; i++) {
                int y = adjTo[i];
                if (mark[y] != st) {
                    mark[y] = st;
                    queue[tail++] = y;
                    if (earlyExit && y == t) return true;
                }
            }
        }
        return mark[t] == st;
    }

    /**
     * Coleta os vértices da componente conexa de {@code s} (no grafo corrente) em {@code out}
     * (que deve ter capacidade ≥ n) e retorna quantos são. {@code s} é o primeiro.
     */
    public int component(Graph g, int s, int[] out) {
        final int st = nextStamp();
        int head = 0, tail = 0;
        out[tail++] = s;
        mark[s] = st;
        while (head < tail) {
            int x = out[head++];
            int a = g.start[x], b = a + g.deg[x];
            for (int i = a; i < b; i++) {
                int y = g.adjTo[i];
                if (mark[y] != st) { mark[y] = st; out[tail++] = y; }
            }
        }
        return tail;
    }

    /** Número de vértices alcançáveis a partir de {@code s}. */
    public int countReachable(Graph g, int s) {
        return component(g, s, queue);
    }
}
