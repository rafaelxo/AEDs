package grafos;

/**
 * Estratégia de identificação de pontes usada pelo algoritmo de Fleury. A cada passo em que o vértice
 * corrente tem grau ≥ 2, Fleury chama {@link #prepare} uma vez e depois {@link #isBridge} para as arestas
 * candidatas incidentes ao vértice corrente.
 *
 * <p>Estratégias disponíveis (ver {@link Strategies}):
 * <ul>
 *   <li>{@code naive} – testa apenas as arestas candidatas: remove a aresta e verifica a conectividade
 *       (travessia completa da componente);</li>
 *   <li>{@code naive-early} – idem, mas a travessia para ao encontrar a outra extremidade;</li>
 *   <li>{@code naive-all} – a cada passo identifica <i>todas</i> as pontes do grafo corrente pelo método naïve
 *       (remove cada aresta e testa a conectividade) – leitura literal do enunciado;</li>
 *   <li>{@code tarjan} – a cada passo identifica todas as pontes do grafo corrente com o algoritmo de Tarjan.</li>
 * </ul>
 */
public interface BridgeStrategy {
    String name();

    /** Chamado uma vez por passo de Fleury (quando deg(cur) ≥ 2), antes das consultas {@link #isBridge}. */
    void prepare(Graph g, int cur);

    /** A aresta {@code e} (presente no grafo corrente) é uma ponte do grafo corrente? */
    boolean isBridge(Graph g, int e);

    /** Define o limite de tempo (usado pelas estratégias cujo {@code prepare} é caro). */
    default void setDeadline(Deadline d) { }
}
