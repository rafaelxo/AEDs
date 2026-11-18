package grafos;

import java.util.HashSet;
import java.util.Random;

/**
 * Geradores de grafos simples aleatórios (reprodutíveis pela semente).
 *
 * <p><b>Euleriano.</b> Constrói-se um ciclo hamiltoniano sobre uma permutação aleatória dos vértices
 * (garante conexidade e grau 2 em todos) e, em seguida, somam-se ciclos simples aleatórios de comprimento
 * 3..{@value #MAX_CYCLE} sobre vértices sorteados, sem repetir arestas, até atingir o número de arestas
 * desejado ({@code n·grauMédio/2}). Cada ciclo adiciona 2 ao grau de seus vértices, portanto todos os
 * graus permanecem pares e o grafo é conexo &rArr; euleriano (e sem pontes).
 *
 * <p><b>Semi-euleriano.</b> Remove-se uma aresta aleatória de um grafo euleriano. Como todo grafo
 * euleriano é livre de pontes, o grafo continua conexo e exatamente as duas extremidades da aresta
 * removida passam a ter grau ímpar.
 *
 * <p><b>Não euleriano.</b> Removem-se duas arestas aleatórias sem extremidade comum de um grafo
 * euleriano: ficam exatamente 4 vértices de grau ímpar (&gt; 2), logo não há caminho euleriano.
 *
 * <p>A ordem das arestas (e a orientação de cada uma) é embaralhada, para que a ordem das listas de
 * adjacência – e portanto as escolhas de Fleury – não seja enviesada pela construção.
 */
public final class GraphGenerator {
    private GraphGenerator() { }

    public static final int MAX_CYCLE = 8;

    /** Lista de arestas (vetores paralelos). */
    private static final class Edges {
        int[] u, v; int size;
        Edges(int cap) { u = new int[cap]; v = new int[cap]; }
        void add(int a, int b) {
            if (size == u.length) { u = java.util.Arrays.copyOf(u, size * 2); v = java.util.Arrays.copyOf(v, size * 2); }
            u[size] = a; v[size] = b; size++;
        }
    }

    private static long key(int a, int b, long n) { return a < b ? a * n + b : b * n + a; }

    private static void shuffle(int[] a, Random rnd) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
    }

    /** Arestas de um grafo euleriano aleatório com ~ n·avgDeg/2 arestas. */
    private static Edges eulerianEdges(int n, double avgDeg, Random rnd) {
        if (n < 3) throw new IllegalArgumentException("n deve ser >= 3");
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        shuffle(perm, rnd);
        HashSet<Long> set = new HashSet<>(Math.max(16, (int) (n * avgDeg)));
        Edges E = new Edges((int) Math.max(16, n * avgDeg / 2 + MAX_CYCLE));
        for (int i = 0; i < n; i++) {
            int a = perm[i], b = perm[(i + 1) % n];
            set.add(key(a, b, n)); E.add(a, b);
        }
        long target = Math.round(n * avgDeg / 2.0);
        long maxAttempts = 200 + 50 * target;
        int[] cyc = new int[MAX_CYCLE];
        int maxLen = Math.min(MAX_CYCLE, n);
        for (long attempt = 0; E.size < target && attempt < maxAttempts; attempt++) {
            int len = 3 + rnd.nextInt(maxLen - 2);
            int c = 0;
            while (c < len) {                      // len vértices distintos
                int x = rnd.nextInt(n);
                boolean dup = false;
                for (int j = 0; j < c; j++) if (cyc[j] == x) { dup = true; break; }
                if (!dup) cyc[c++] = x;
            }
            boolean ok = true;
            for (int j = 0; j < len && ok; j++)
                if (set.contains(key(cyc[j], cyc[(j + 1) % len], n))) ok = false;
            if (!ok) continue;
            for (int j = 0; j < len; j++) {
                int a = cyc[j], b = cyc[(j + 1) % len];
                set.add(key(a, b, n)); E.add(a, b);
            }
        }
        return E;
    }

    /** Constrói o grafo com as arestas embaralhadas, ignorando os índices em {@code skip}. */
    private static Graph build(int n, Edges E, Random rnd, int... skip) {
        int m = E.size - skip.length;
        int[] order = new int[E.size];
        for (int i = 0; i < order.length; i++) order[i] = i;
        shuffle(order, rnd);
        int[] eu = new int[m], ev = new int[m];
        int k = 0;
        outer:
        for (int idx : order) {
            for (int s : skip) if (s == idx) continue outer;
            if (rnd.nextBoolean()) { eu[k] = E.u[idx]; ev[k] = E.v[idx]; }
            else { eu[k] = E.v[idx]; ev[k] = E.u[idx]; }
            k++;
        }
        return Graph.fromEdges(n, eu, ev);
    }

    public static Graph eulerian(int n, double avgDeg, long seed) {
        Random rnd = new Random(seed);
        return build(n, eulerianEdges(n, avgDeg, rnd), rnd);
    }

    public static Graph semiEulerian(int n, double avgDeg, long seed) {
        Random rnd = new Random(seed);
        Edges E = eulerianEdges(n, avgDeg, rnd);
        int e = rnd.nextInt(E.size);
        return build(n, E, rnd, e);
    }

    public static Graph nonEulerian(int n, double avgDeg, long seed) {
        if (n < 4) throw new IllegalArgumentException("n deve ser >= 4");
        Random rnd = new Random(seed);
        Edges E = eulerianEdges(n, avgDeg, rnd);
        int e1 = rnd.nextInt(E.size), e2;
        int guard = 0;
        do {
            e2 = rnd.nextInt(E.size);
            guard++;
        } while ((e2 == e1 || E.u[e2] == E.u[e1] || E.u[e2] == E.v[e1] || E.v[e2] == E.u[e1] || E.v[e2] == E.v[e1])
                && guard < 100000);
        if (guard >= 100000) throw new IllegalStateException("não achou duas arestas disjuntas");
        return build(n, E, rnd, e1, e2);
    }

    /**
     * Grafo aleatório conexo com exatamente {@code m} arestas (m ≥ n-1): árvore geradora aleatória
     * (vértice i liga-se a um anterior sorteado) + arestas extras aleatórias. Costuma ter pontes
     * (folhas e ramos); usado para testar/medir a identificação de pontes.
     */
    public static Graph randomConnected(int n, int m, long seed) {
        if (n < 2 || m < n - 1 || m > (long) n * (n - 1) / 2) throw new IllegalArgumentException("parâmetros inválidos");
        Random rnd = new Random(seed);
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        shuffle(perm, rnd);
        HashSet<Long> set = new HashSet<>(2 * m);
        Edges E = new Edges(m);
        for (int i = 1; i < n; i++) {
            int a = perm[i], b = perm[rnd.nextInt(i)];
            set.add(key(a, b, n)); E.add(a, b);
        }
        while (E.size < m) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            if (a == b || !set.add(key(a, b, n))) continue;
            E.add(a, b);
        }
        return build(n, E, rnd);
    }

    /** Gerador por nome de classe: "euler", "semi" ou "non". */
    public static Graph byClass(String cls, int n, double avgDeg, long seed) {
        switch (cls) {
            case "euler": return eulerian(n, avgDeg, seed);
            case "semi":  return semiEulerian(n, avgDeg, seed);
            case "non":   return nonEulerian(n, avgDeg, seed);
            default: throw new IllegalArgumentException("classe desconhecida: " + cls + " (use euler, semi ou non)");
        }
    }
}
