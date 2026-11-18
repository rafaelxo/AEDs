package grafos;

/** Grafos de exemplo. */
public final class Examples {
    private Examples() { }

    /** Rótulos A..I dos vértices 0..8 do grafo da Figura 1 do enunciado. */
    public static final String LABELS = "ABCDEFGHI";

    /**
     * Grafo da Figura 1 do enunciado (9 vértices, 15 arestas). Vértices A..I = 0..8.
     * Graus: A=3, B=4, C=4, D=4, E=4, F=4, G=3, H=2, I=2 &rArr; semi-euleriano (A e G ímpares);
     * a única ponte é F–G.
     */
    public static Graph figure1() {
        String[] edges = {"AD", "AB", "AE", "BD", "BC", "BE", "CD", "CE", "CF", "DF", "EF", "FG", "GH", "GI", "HI"};
        int[] eu = new int[edges.length], ev = new int[edges.length];
        for (int i = 0; i < edges.length; i++) {
            eu[i] = LABELS.indexOf(edges[i].charAt(0));
            ev[i] = LABELS.indexOf(edges[i].charAt(1));
        }
        return Graph.fromEdges(LABELS.length(), eu, ev);
    }
}
