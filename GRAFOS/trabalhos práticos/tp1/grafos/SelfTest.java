package grafos;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Random;

/**
 * Testes de corretude (sem dependências externas). Execute com:
 * <pre>  java -cp out grafos.SelfTest</pre>
 * Verifica, entre outros: (1) pontes (naïve, naïve com early exit e Tarjan) contra um oráculo independente
 * baseado em union-find; (2) remoção/restauração exata de arestas; (3) classificação euleriana contra
 * força bruta; (4) caminhos de Fleury validados por um verificador independente, em grafos das três
 * classes, e idênticos entre as 4 estratégias; (5) a Figura 1 do enunciado; (6) casos de borda.
 */
public final class SelfTest {
    private static int checks = 0, failures = 0;

    private static void check(boolean ok, String msg) {
        checks++;
        if (!ok) { failures++; System.out.println("  FALHOU: " + msg); }
    }

    public static void main(String[] args) throws Exception {
        long t0 = System.nanoTime();
        testFigure1();
        testRemoveRestore();
        testBridgesVsOracle();
        testClassificationVsBruteForce();
        testFleuryGeneratedClasses();
        testFleuryLarger();
        testEdgeCases();
        testIOAndValidator();
        System.out.printf("%n%d verificações, %d falhas (%.1f s)%n", checks, failures, (System.nanoTime() - t0) / 1e9);
        if (failures > 0) { System.out.println("RESULTADO: FALHOU"); System.exit(1); }
        System.out.println("RESULTADO: TODOS OS TESTES PASSARAM");
    }

    // ------------------------------------------------------------------------------------------ Figura 1

    private static void testFigure1() {
        System.out.println("[1] Figura 1 do enunciado");
        Graph g = Examples.figure1();
        check(g.n == 9 && g.m == 15, "9 vértices e 15 arestas");
        int[] expDeg = {3, 4, 4, 4, 4, 4, 3, 2, 2};
        for (int v = 0; v < 9; v++) check(g.degree(v) == expDeg[v], "grau de " + Examples.LABELS.charAt(v));
        EulerChecker.Info info = EulerChecker.analyze(g);
        check(info.type == EulerChecker.Type.SEMI_EULERIAN, "Figura 1 é semi-euleriana");
        check(info.startVertex == 0 || info.startVertex == 6, "início em A ou G");
        int fg = -1;
        for (int e = 0; e < g.m; e++)
            if ((g.edgeU(e) == 5 && g.edgeV(e) == 6) || (g.edgeU(e) == 6 && g.edgeV(e) == 5)) fg = e;
        BridgeFinder[] finders = {new NaiveBridgeFinder(false), new NaiveBridgeFinder(true), new TarjanBridgeFinder(g)};
        for (BridgeFinder f : finders) {
            int[] br = f.findBridges(g);
            check(Arrays.equals(br, new int[]{fg}), f.name() + ": única ponte é F–G, obtido " + Arrays.toString(br));
        }
        int[] ref = null;
        for (String s : Strategies.NAMES) {
            Fleury.Result r = Fleury.findEulerPath(g, Strategies.create(s, g));
            check(r.found(), s + ": encontrou caminho");
            check(EulerPathValidator.validate(g, r.path) == null, s + ": caminho válido");
            check(r.path[0] == 0 || r.path[0] == 6, s + ": começa em vértice ímpar");
            check(r.path[r.path.length - 1] != r.path[0], s + ": termina no outro vértice ímpar");
            if (ref == null) ref = r.path; else check(Arrays.equals(ref, r.path), s + ": mesmo caminho das demais estratégias");
        }
        check(g.edgeCount() == 15, "Fleury.findEulerPath(g, ...) preserva o grafo original");
    }

    // ------------------------------------------------------------------------------------------ remoção/restauração

    private static void testRemoveRestore() {
        System.out.println("[2] Remoção e restauração exata de arestas");
        Random rnd = new Random(1);
        for (int it = 0; it < 200; it++) {
            int n = 3 + rnd.nextInt(30);
            Graph g = randomGraph(n, 0.1 + rnd.nextDouble() * 0.6, rnd);
            if (g.m == 0) continue;
            int[] a1 = g.adjE.clone(), a2 = g.adjTo.clone(), d = g.deg.clone(), p1 = g.posU.clone(), p2 = g.posV.clone();
            for (int k = 0; k < 20; k++) {
                int e = rnd.nextInt(g.m);
                g.removeEdge(e);
                check(!g.isAlive(e), "aresta removida não está viva");
                g.restoreLastRemoved();
            }
            check(Arrays.equals(a1, g.adjE) && Arrays.equals(a2, g.adjTo) && Arrays.equals(d, g.deg)
                    && Arrays.equals(p1, g.posU) && Arrays.equals(p2, g.posV), "estado idêntico após remover/restaurar");
            // remoções permanentes: grau consistente e listas só com arestas vivas
            int removed = 0;
            for (int e = 0; e < g.m; e++) if (rnd.nextInt(3) == 0) { g.removeEdge(e); removed++; }
            check(g.edgeCount() == g.m - removed, "contagem de arestas vivas");
            boolean consistent = true;
            int sumDeg = 0;
            for (int v = 0; v < g.n; v++) {
                sumDeg += g.degree(v);
                for (int i = g.start[v]; i < g.start[v] + g.deg[v]; i++) {
                    int e = g.adjE[i];
                    if (!g.isAlive(e) || g.other(e, v) != g.adjTo[i]) consistent = false;
                }
            }
            check(consistent && sumDeg == 2 * g.edgeCount(), "listas de adjacência consistentes após remoções");
        }
    }

    // ------------------------------------------------------------------------------------------ pontes

    private static void testBridgesVsOracle() {
        System.out.println("[3] Pontes: naïve, naïve-early e Tarjan vs. oráculo (union-find)");
        Random rnd = new Random(2);
        int graphs = 0;
        for (int it = 0; it < 600; it++) {
            int n = 2 + rnd.nextInt(40);
            Graph g = (it % 3 == 0) ? connectedRandom(n, rnd) : randomGraph(n, rnd.nextDouble() * 0.5, rnd);
            int[] want = bridgesOracle(g);
            for (BridgeFinder f : new BridgeFinder[]{new NaiveBridgeFinder(false), new NaiveBridgeFinder(true), new TarjanBridgeFinder(g)}) {
                int[] got = f.findBridges(g);
                check(Arrays.equals(got, want), f.name() + " n=" + n + " m=" + g.m + " esperado " + Arrays.toString(want) + " obtido " + Arrays.toString(got));
            }
            graphs++;
        }
        // grafos maiores gerados (com muitas pontes): compara Tarjan x naïve
        for (int s = 0; s < 20; s++) {
            Graph g = GraphGenerator.randomConnected(300, 300 + 20 * s, 100 + s);
            int[] t = new TarjanBridgeFinder(g).findBridges(g);
            check(Arrays.equals(t, new NaiveBridgeFinder(false).findBridges(g)), "Tarjan == naïve em randomConnected 300 vértices (s=" + s + ")");
            check(Arrays.equals(t, bridgesOracle(g)), "Tarjan == oráculo em randomConnected 300 vértices (s=" + s + ")");
        }
        // Euleriano não tem pontes
        for (int s = 0; s < 10; s++) {
            Graph g = GraphGenerator.eulerian(500, 4, s);
            check(new TarjanBridgeFinder(g).findBridges(g).length == 0, "grafo euleriano gerado não tem pontes");
        }
        // vértice de corte sem ser ponte; caminho; ciclo; árvore
        Graph path = fromPairs(5, 0, 1, 1, 2, 2, 3, 3, 4);
        check(new TarjanBridgeFinder(path).findBridges(path).length == 4, "em um caminho todas as arestas são pontes");
        Graph cyc = fromPairs(5, 0, 1, 1, 2, 2, 3, 3, 4, 4, 0);
        check(new TarjanBridgeFinder(cyc).findBridges(cyc).length == 0, "ciclo sem pontes");
        Graph bow = fromPairs(5, 0, 1, 1, 2, 2, 0, 2, 3, 3, 4, 4, 2); // gravata: 2 é vértice de corte, sem pontes
        check(new TarjanBridgeFinder(bow).findBridges(bow).length == 0, "gravata (vértice de corte) sem pontes");
        System.out.println("    (" + graphs + " grafos aleatórios pequenos comparados)");
    }

    // ------------------------------------------------------------------------------------------ classificação

    private static void testClassificationVsBruteForce() {
        System.out.println("[4] Classificação euleriana vs. força bruta (busca exaustiva de trilha euleriana)");
        Random rnd = new Random(3);
        int[] counts = new int[3];
        for (int it = 0; it < 2500; it++) {
            int n = 2 + rnd.nextInt(7);
            Graph g = randomGraph(n, rnd.nextDouble() * 0.7, rnd);
            if (g.m > 13) continue;
            boolean exists = bruteForceEulerTrail(g);
            EulerChecker.Info info = EulerChecker.analyze(g);
            check(exists == (info.type != EulerChecker.Type.NON_EULERIAN), "existência de caminho (n=" + n + ", m=" + g.m + ")");
            counts[info.type.ordinal()]++;
            for (String s : Strategies.NAMES) {
                Fleury.Result r = Fleury.findEulerPath(g, Strategies.create(s, g));
                check(r.found() == exists, s + ": Fleury acha caminho sse existe");
                if (r.found()) check(EulerPathValidator.validate(g, r.path) == null, s + ": caminho válido (grafo pequeno)");
            }
        }
        System.out.println("    (euleriano=" + counts[0] + ", semi=" + counts[1] + ", não=" + counts[2] + ")");
        check(counts[0] > 20 && counts[1] > 20 && counts[2] > 20, "amostra cobre as três classes");
    }

    // ------------------------------------------------------------------------------------------ Fleury em grafos gerados

    private static void testFleuryGeneratedClasses() {
        System.out.println("[5] Fleury em grafos aleatórios gerados (3 classes × 4 estratégias)");
        double[] degs = {2, 3, 4, 6, 10};
        int total = 0;
        for (int n : new int[]{4, 5, 8, 13, 30, 60, 120}) {
            for (double d : degs) {
                for (int s = 0; s < 6; s++) {
                    long seed = 1000L * n + 10 * s + (long) d;
                    Graph ge = GraphGenerator.eulerian(n, d, seed);
                    Graph gs = GraphGenerator.semiEulerian(n, d, seed);
                    Graph gn = GraphGenerator.nonEulerian(n, d, seed);
                    check(oddCount(ge) == 0 && EulerChecker.analyze(ge).type == EulerChecker.Type.EULERIAN, "gerador euler n=" + n);
                    check(oddCount(gs) == 2 && EulerChecker.analyze(gs).type == EulerChecker.Type.SEMI_EULERIAN, "gerador semi n=" + n);
                    check(oddCount(gn) == 4 && EulerChecker.analyze(gn).type == EulerChecker.Type.NON_EULERIAN, "gerador non n=" + n);
                    for (Graph g : new Graph[]{ge, gs}) {
                        int[] ref = null;
                        for (String st : Strategies.NAMES) {
                            Fleury.Result r = Fleury.findEulerPath(g, Strategies.create(st, g));
                            check(r.found(), st + ": acha caminho n=" + n);
                            if (!r.found()) continue;
                            String err = EulerPathValidator.validate(g, r.path);
                            check(err == null, st + " n=" + n + " d=" + d + ": " + err);
                            if (ref == null) ref = r.path;
                            else check(Arrays.equals(ref, r.path), st + ": caminho idêntico às demais estratégias n=" + n);
                        }
                        total++;
                    }
                    for (String st : Strategies.NAMES)
                        check(!Fleury.findEulerPath(gn, Strategies.create(st, gn)).found(), st + ": não euleriano => sem caminho");
                }
            }
        }
        System.out.println("    (" + total + " grafos euleriano/semi-euleriano resolvidos por 4 estratégias)");
    }

    private static void testFleuryLarger() {
        System.out.println("[6] Fleury em grafos maiores (n = 2.000): Tarjan e naïve-early idênticos e válidos");
        for (String cls : new String[]{"euler", "semi"}) {
            for (int s = 0; s < 3; s++) {
                Graph g = GraphGenerator.byClass(cls, 2000, 4, 77 + s);
                Fleury.Result a = Fleury.findEulerPath(g, Strategies.create("tarjan", g));
                Fleury.Result b = Fleury.findEulerPath(g, Strategies.create("naive-early", g));
                Fleury.Result c = Fleury.findEulerPath(g, Strategies.create("naive", g));
                check(a.found() && b.found() && c.found(), cls + ": encontrou caminho");
                check(EulerPathValidator.validate(g, a.path) == null, cls + ": tarjan válido");
                check(Arrays.equals(a.path, b.path) && Arrays.equals(a.path, c.path), cls + ": caminhos idênticos");
            }
        }
    }

    // ------------------------------------------------------------------------------------------ bordas

    private static void testEdgeCases() {
        System.out.println("[7] Casos de borda");
        // sem arestas
        Graph e0 = Graph.fromEdges(3, new int[0], new int[0]);
        Fleury.Result r = Fleury.findEulerPath(e0, Strategies.create("tarjan", e0));
        check(r.found() && r.path.length == 1 && r.type == EulerChecker.Type.EULERIAN, "grafo sem arestas: caminho trivial");
        // uma aresta
        Graph k2 = fromPairs(2, 0, 1);
        for (String s : Strategies.NAMES) {
            r = Fleury.findEulerPath(k2, Strategies.create(s, k2));
            check(r.type == EulerChecker.Type.SEMI_EULERIAN && Arrays.equals(r.path, new int[]{0, 1}), s + ": K2");
        }
        // triângulo (euleriano) e triângulo + vértices isolados
        Graph tri = fromPairs(5, 0, 1, 1, 2, 2, 0);
        for (String s : Strategies.NAMES) {
            r = Fleury.findEulerPath(tri, Strategies.create(s, tri));
            check(r.type == EulerChecker.Type.EULERIAN && EulerPathValidator.validate(tri, r.path) == null, s + ": triângulo com isolados");
        }
        // dois triângulos disjuntos: graus pares mas desconexo
        Graph two = fromPairs(6, 0, 1, 1, 2, 2, 0, 3, 4, 4, 5, 5, 3);
        for (String s : Strategies.NAMES)
            check(!Fleury.findEulerPath(two, Strategies.create(s, two)).found(), s + ": dois triângulos disjuntos => não existe");
        // K4: 4 vértices ímpares; K5: euleriano
        Graph k4 = fromPairs(4, 0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3);
        check(EulerChecker.analyze(k4).type == EulerChecker.Type.NON_EULERIAN, "K4 é não euleriano");
        Graph k5 = fromPairs(5, 0, 1, 0, 2, 0, 3, 0, 4, 1, 2, 1, 3, 1, 4, 2, 3, 2, 4, 3, 4);
        for (String s : Strategies.NAMES) {
            r = Fleury.findEulerPath(k5, Strategies.create(s, k5));
            check(r.type == EulerChecker.Type.EULERIAN && EulerPathValidator.validate(k5, r.path) == null, s + ": K5");
        }
        // halteres: dois triângulos ligados por uma ponte, ambas as pontas ímpares => semi-euleriano com a ponte no meio
        Graph dumb = fromPairs(6, 0, 1, 1, 2, 2, 0, 2, 3, 3, 4, 4, 5, 5, 3);
        for (String s : Strategies.NAMES) {
            r = Fleury.findEulerPath(dumb, Strategies.create(s, dumb));
            check(r.type == EulerChecker.Type.SEMI_EULERIAN && EulerPathValidator.validate(dumb, r.path) == null, s + ": halteres");
        }
        // limite de tempo
        Graph big = GraphGenerator.eulerian(3000, 4, 5);
        boolean timedOut = false;
        try { Fleury.run(big.copy(), Strategies.create("naive-all", big), Deadline.afterSeconds(0.05)); }
        catch (Deadline.TimeLimitExceeded ex) { timedOut = true; }
        check(timedOut, "Deadline interrompe naive-all em grafo grande");
    }

    private static void testIOAndValidator() throws IOException {
        System.out.println("[8] Entrada/saída e validador");
        Graph g = GraphGenerator.semiEulerian(50, 4, 9);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GraphIO.write(g, new PrintStream(bos, true, "UTF-8"));
        Graph h = GraphIO.read(new BufferedReader(new StringReader(bos.toString("UTF-8"))));
        check(h.n == g.n && h.m == g.m && Arrays.equals(h.eu, g.eu) && Arrays.equals(h.ev, g.ev), "ida e volta de arquivo");
        boolean rejected = false;
        try { GraphIO.read(new BufferedReader(new StringReader("3 2\n0 1\n1 0\n"))); } catch (IOException ex) { rejected = true; }
        check(rejected, "aresta paralela rejeitada na leitura");
        rejected = false;
        try { GraphIO.read(new BufferedReader(new StringReader("3 1\n1 1\n"))); } catch (IllegalArgumentException ex) { rejected = true; }
        check(rejected, "laço rejeitado");
        // o validador rejeita caminhos errados
        Graph tri = fromPairs(3, 0, 1, 1, 2, 2, 0);
        check(EulerPathValidator.validate(tri, new int[]{0, 1, 2, 0}) == null, "validador aceita caminho correto");
        check(EulerPathValidator.validate(tri, new int[]{0, 1, 0, 1}) != null, "validador rejeita aresta repetida");
        check(EulerPathValidator.validate(tri, new int[]{0, 1, 2}) != null, "validador rejeita caminho curto");
        Graph sq = fromPairs(4, 0, 1, 1, 2, 2, 3, 3, 0);
        check(EulerPathValidator.validate(sq, new int[]{0, 1, 2, 0, 3}) != null, "validador rejeita aresta inexistente");
    }

    // ------------------------------------------------------------------------------------------ utilidades

    private static Graph fromPairs(int n, int... p) {
        int m = p.length / 2;
        int[] eu = new int[m], ev = new int[m];
        for (int i = 0; i < m; i++) { eu[i] = p[2 * i]; ev[i] = p[2 * i + 1]; }
        return Graph.fromEdges(n, eu, ev);
    }

    private static Graph randomGraph(int n, double p, Random rnd) {
        int[] eu = new int[n * n], ev = new int[n * n];
        int m = 0;
        for (int a = 0; a < n; a++)
            for (int b = a + 1; b < n; b++)
                if (rnd.nextDouble() < p) { if (rnd.nextBoolean()) { eu[m] = a; ev[m] = b; } else { eu[m] = b; ev[m] = a; } m++; }
        return Graph.fromEdges(n, Arrays.copyOf(eu, m), Arrays.copyOf(ev, m));
    }

    /** Conexo e esparso (árvore + poucas arestas): bom para ter pontes. */
    private static Graph connectedRandom(int n, Random rnd) {
        int maxM = n * (n - 1) / 2;
        int m = Math.min(maxM, n - 1 + rnd.nextInt(Math.max(1, n / 2)));
        return GraphGenerator.randomConnected(n, m, rnd.nextLong());
    }

    private static int oddCount(Graph g) {
        int c = 0;
        for (int v = 0; v < g.n; v++) if ((g.degree(v) & 1) == 1) c++;
        return c;
    }

    /** Oráculo: e é ponte sse o número de componentes (union-find) aumenta ao removê-la. */
    private static int[] bridgesOracle(Graph g) {
        int base = components(g, -1);
        int[] tmp = new int[g.m];
        int c = 0;
        for (int e = 0; e < g.m; e++) if (components(g, e) > base) tmp[c++] = e;
        return Arrays.copyOf(tmp, c);
    }

    private static int components(Graph g, int skip) {
        int[] p = new int[g.n];
        for (int i = 0; i < g.n; i++) p[i] = i;
        int comps = g.n;
        for (int e = 0; e < g.m; e++) {
            if (e == skip) continue;
            int a = find(p, g.edgeU(e)), b = find(p, g.edgeV(e));
            if (a != b) { p[a] = b; comps--; }
        }
        return comps;
    }

    private static int find(int[] p, int x) {
        while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; }
        return x;
    }

    /** Busca exaustiva: existe trilha que usa todas as arestas exatamente uma vez? (m pequeno) */
    private static boolean bruteForceEulerTrail(Graph g) {
        if (g.m == 0) return true;
        for (int s = 0; s < g.n; s++)
            if (g.degree(s) > 0 && trail(g, s, 0L, 0)) return true;
        return false;
    }

    private static boolean trail(Graph g, int v, long usedMask, int used) {
        if (used == g.m) return true;
        for (int e = 0; e < g.m; e++) {
            if ((usedMask >> e & 1L) != 0) continue;
            int u = g.edgeU(e), w = g.edgeV(e);
            if (u == v && trail(g, w, usedMask | 1L << e, used + 1)) return true;
            if (w == v && trail(g, u, usedMask | 1L << e, used + 1)) return true;
        }
        return false;
    }
}
