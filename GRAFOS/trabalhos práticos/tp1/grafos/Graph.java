package grafos;

import java.util.Arrays;

/**
 * Grafo simples não-direcionado com suporte a remoção e restauração de arestas em O(1).
 *
 * <p><b>Representação.</b> Listas de adjacência em formato CSR (arrays contíguos): os vizinhos do vértice
 * {@code v} ocupam os slots {@code start[v] .. start[v]+deg[v]-1} de {@code adjTo}/{@code adjE}
 * ({@code adjTo[s]} é o vizinho e {@code adjE[s]} o identificador da aresta). Os slots a partir de
 * {@code start[v]+deg[v]} (até {@code start[v+1]-1}) guardam as arestas já removidas de {@code v}.
 *
 * <p><b>Remoção em O(1).</b> Para remover a aresta {@code e} da lista de {@code x}, ela é trocada de
 * lugar com o último slot "vivo" e {@code deg[x]} é decrementado. Assim, percorrer a lista de um
 * vértice custa sempre O(grau <i>atual</i>) e uma travessia do grafo custa O(n + m) do grafo
 * <i>restante</i> (e não do grafo original) – o que importa em Fleury, onde as arestas vão sendo
 * consumidas.
 *
 * <p><b>Restauração exata.</b> {@link #restoreLastRemoved()} desfaz a <i>última</i> remoção desfazendo
 * também a troca de slots, de modo que a ordem das listas de adjacência volta exatamente ao que era.
 * Isso permite que os testes "remove a aresta, verifica conectividade, devolve a aresta" do método naïve
 * não alterem o estado do grafo – e, consequentemente, que todas as estratégias de identificação de
 * pontes façam exatamente as mesmas escolhas no algoritmo de Fleury (mesmo caminho de saída).
 */
public final class Graph {
    /** Número de vértices (identificados por 0..n-1). */
    public final int n;
    /** Número de arestas do grafo original. */
    public final int m;

    /** Extremidades originais da aresta e (nunca mudam). */
    final int[] eu, ev;
    final int[] start, adjTo, adjE, deg;
    /** Posição (slot) da aresta e na lista de eu[e] e na lista de ev[e]. */
    final int[] posU, posV;

    private int mAlive;
    private int undoEdge = -1, undoPU, undoPV;

    private Graph(int n, int m, int[] eu, int[] ev, int[] start, int[] adjTo, int[] adjE,
                  int[] deg, int[] posU, int[] posV) {
        this.n = n; this.m = m; this.eu = eu; this.ev = ev; this.start = start;
        this.adjTo = adjTo; this.adjE = adjE; this.deg = deg; this.posU = posU; this.posV = posV;
        this.mAlive = m;
    }

    /**
     * Constrói o grafo a partir da lista de arestas ({@code eu[i]}, {@code ev[i]}).
     * Laços são rejeitados; arestas paralelas NÃO são verificadas aqui (ver {@link GraphIO}).
     */
    public static Graph fromEdges(int n, int[] eu, int[] ev) {
        int m = eu.length;
        if (ev.length != m) throw new IllegalArgumentException("vetores de tamanhos diferentes");
        int[] deg = new int[n];
        for (int e = 0; e < m; e++) {
            if (eu[e] < 0 || eu[e] >= n || ev[e] < 0 || ev[e] >= n)
                throw new IllegalArgumentException("vértice fora do intervalo na aresta " + e);
            if (eu[e] == ev[e]) throw new IllegalArgumentException("laço na aresta " + e + " (grafo simples)");
            deg[eu[e]]++; deg[ev[e]]++;
        }
        int[] start = new int[n + 1];
        for (int v = 0; v < n; v++) start[v + 1] = start[v] + deg[v];
        int[] fill = new int[n];
        int[] adjTo = new int[2 * m], adjE = new int[2 * m];
        int[] posU = new int[m], posV = new int[m];
        for (int e = 0; e < m; e++) {
            int u = eu[e], v = ev[e];
            int su = start[u] + fill[u]++;
            adjTo[su] = v; adjE[su] = e; posU[e] = su;
            int sv = start[v] + fill[v]++;
            adjTo[sv] = u; adjE[sv] = e; posV[e] = sv;
        }
        return new Graph(n, m, eu.clone(), ev.clone(), start, adjTo, adjE, deg, posU, posV);
    }

    /** Cópia independente do estado atual (arestas removidas permanecem removidas). */
    public Graph copy() {
        Graph g = new Graph(n, m, eu, ev, start, adjTo.clone(), adjE.clone(), deg.clone(),
                posU.clone(), posV.clone());
        g.mAlive = mAlive;
        return g;
    }

    // ------------------------------------------------------------------ consultas

    /** Grau atual (considerando apenas arestas ainda presentes). */
    public int degree(int v) { return deg[v]; }

    /** Número de arestas ainda presentes. */
    public int edgeCount() { return mAlive; }

    public int edgeU(int e) { return eu[e]; }
    public int edgeV(int e) { return ev[e]; }

    /** Extremidade de {@code e} diferente de {@code x}. */
    public int other(int e, int x) { return eu[e] == x ? ev[e] : eu[e]; }

    public boolean isAlive(int e) {
        return posU[e] < start[eu[e]] + deg[eu[e]];
    }

    /** Vizinhos atuais de v (cópia; usado em testes e na impressão, não nos algoritmos). */
    public int[] neighbors(int v) {
        return Arrays.copyOfRange(adjTo, start[v], start[v] + deg[v]);
    }

    // ------------------------------------------------------------------ remoção / restauração

    /** Remove a aresta e (deve estar presente) em O(1). */
    public void removeEdge(int e) {
        undoEdge = e;
        undoPU = posU[e];
        undoPV = posV[e];
        detach(eu[e], e);
        detach(ev[e], e);
        mAlive--;
    }

    /**
     * Desfaz a ÚLTIMA chamada de {@link #removeEdge(int)}, restaurando também a ordem exata das listas.
     * Só é válida se nenhuma outra modificação ocorreu entre a remoção e a restauração.
     */
    public void restoreLastRemoved() {
        int e = undoEdge;
        if (e < 0) throw new IllegalStateException("nada a restaurar");
        attach(eu[e], e, undoPU);
        attach(ev[e], e, undoPV);
        mAlive++;
        undoEdge = -1;
    }

    private void detach(int x, int e) {
        boolean atU = (eu[e] == x);
        int p = atU ? posU[e] : posV[e];
        int last = start[x] + deg[x] - 1;
        if (p != last) {
            int e2 = adjE[last], t2 = adjTo[last];
            adjE[p] = e2; adjTo[p] = t2;                  // o último ocupa o lugar de e
            if (eu[e2] == x) posU[e2] = p; else posV[e2] = p;
            adjE[last] = e; adjTo[last] = atU ? ev[e] : eu[e]; // e vai para o último slot "vivo"
            if (atU) posU[e] = last; else posV[e] = last;
        }
        deg[x]--;
    }

    private void attach(int x, int e, int origPos) {
        boolean atU = (eu[e] == x);
        int s = start[x] + deg[x]; // slot onde e está agora (primeiro slot "morto")
        if (origPos != s) {
            int e2 = adjE[origPos], t2 = adjTo[origPos];
            adjE[s] = e2; adjTo[s] = t2;
            if (eu[e2] == x) posU[e2] = s; else posV[e2] = s;
            adjE[origPos] = e; adjTo[origPos] = atU ? ev[e] : eu[e];
            if (atU) posU[e] = origPos; else posV[e] = origPos;
        }
        deg[x]++;
    }
}
