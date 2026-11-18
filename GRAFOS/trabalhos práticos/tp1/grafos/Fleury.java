package grafos;

/**
 * Algoritmo de Fleury (1883) para caminho/ciclo euleriano.
 *
 * <pre>
 *   1. Verifique as condições de Euler (conexo + 0 ou 2 vértices ímpares); senão, não existe caminho.
 *   2. cur ← vértice inicial (um dos dois ímpares, se houver; senão qualquer vértice com grau &gt; 0).
 *   3. Enquanto restarem arestas:
 *        - se cur tem uma única aresta incidente, use-a;
 *        - senão, escolha uma aresta incidente que NÃO seja ponte do grafo corrente
 *          (a identificação de pontes é delegada a uma {@link BridgeStrategy});
 *        - remova a aresta escolhida e mova cur para a outra extremidade.
 * </pre>
 *
 * Com {@code m} arestas há {@code m} passos; cada passo custa uma identificação de pontes, O(n+m)
 * (Tarjan) ou mais (naïve), de modo que Fleury é, no melhor caso, O(m·(n+m)) – muito mais lento que o
 * algoritmo de Hierholzer (O(m)), mas é o método pedido no enunciado.
 */
public final class Fleury {
    private Fleury() { }

    /**
     * Resultado: tipo do grafo, caminho (sequência de m+1 vértices) ou {@code null} se não existe, e
     * contadores da execução: passos forçados (grau 1, sem consulta a pontes), passos de decisão (grau ≥ 2),
     * testes {@code isBridge} realizados e quantas vezes uma ponte foi evitada.
     */
    public static final class Result {
        public final EulerChecker.Type type;
        public final int[] path;
        public final long forcedSteps, decisionSteps, bridgeTests, bridgesAvoided;
        Result(EulerChecker.Type type, int[] path, long forced, long decisions, long tests, long avoided) {
            this.type = type; this.path = path;
            this.forcedSteps = forced; this.decisionSteps = decisions; this.bridgeTests = tests; this.bridgesAvoided = avoided;
        }
        Result(EulerChecker.Type type, int[] path) { this(type, path, 0, 0, 0, 0); }
        public boolean found() { return path != null; }
    }

    /** Versão conveniente: trabalha sobre uma cópia, preservando {@code g}. */
    public static Result findEulerPath(Graph g, BridgeStrategy strategy) {
        return run(g.copy(), strategy, Deadline.NONE);
    }

    /**
     * Executa Fleury CONSUMINDO as arestas de {@code g} (o grafo fica sem arestas ao final).
     * Passe uma cópia ({@link Graph#copy()}) se precisar do grafo depois.
     *
     * @throws Deadline.TimeLimitExceeded se o prazo estourar (progress = passos concluídos)
     */
    public static Result run(Graph g, BridgeStrategy strat, Deadline dl) {
        EulerChecker.Info info = EulerChecker.analyze(g);
        if (info.type == EulerChecker.Type.NON_EULERIAN) return new Result(info.type, null);

        final int m = g.edgeCount();
        int[] path = new int[m + 1];
        int cur = info.startVertex;
        if (cur < 0) return new Result(info.type, new int[0]); // grafo sem vértices
        path[0] = cur;
        strat.setDeadline(dl);
        long forced = 0, decisions = 0, tests = 0, avoided = 0;

        int k = 1; // passo corrente (1..m)
        try {
            for (; k <= m; k++) {
                final int a = g.start[cur];
                final int d = g.deg[cur];
                if (d == 0) throw new IllegalStateException("Fleury: vértice corrente sem arestas no passo " + k);
                int slot;
                if (d == 1) {
                    slot = a;                          // única opção: atravessa (mesmo que seja ponte)
                    forced++;
                } else {
                    strat.prepare(g, cur);             // ex.: Tarjan roda uma vez sobre o grafo corrente
                    decisions++;
                    slot = -1;
                    for (int i = a; i < a + d; i++) {  // primeira aresta incidente que não seja ponte
                        tests++;
                        if (!strat.isBridge(g, g.adjE[i])) { slot = i; break; }
                        avoided++;
                    }
                    if (slot < 0)
                        throw new IllegalStateException("Fleury: todas as arestas incidentes são pontes no passo " + k);
                }
                int e = g.adjE[slot];
                int next = g.adjTo[slot];
                g.removeEdge(e);
                path[k] = next;
                cur = next;
                dl.check(k);
            }
        } catch (Deadline.TimeLimitExceeded ex) {
            // a exceção pode vir de dentro da estratégia: informa quantos passos já estavam concluídos
            throw new Deadline.TimeLimitExceeded(k - 1);
        }
        return new Result(info.type, path, forced, decisions, tests, avoided);
    }
}
