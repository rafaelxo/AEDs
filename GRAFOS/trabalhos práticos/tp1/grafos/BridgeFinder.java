package grafos;

/**
 * Método de identificação de <b>todas</b> as pontes de um grafo (problema descrito no enunciado).
 * Implementações: {@link NaiveBridgeFinder} (remove cada aresta e testa a conectividade, O(m·(n+m)))
 * e {@link TarjanBridgeFinder} (busca em profundidade com low-link, O(n+m)).
 */
public interface BridgeFinder {
    String name();

    /**
     * Retorna os identificadores das pontes do grafo corrente (considerando todas as componentes
     * conexas), em ordem crescente. O grafo é devolvido ao mesmo estado em que estava.
     */
    int[] findBridges(Graph g, Deadline deadline);

    default int[] findBridges(Graph g) { return findBridges(g, Deadline.NONE); }
}
