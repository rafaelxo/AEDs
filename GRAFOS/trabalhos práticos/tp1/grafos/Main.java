package grafos;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Interface de linha de comando.
 * <pre>
 *   demo                                  executa o exemplo da Figura 1 (pontes + caminho euleriano, com trace)
 *   euler  &lt;arquivo&gt; [--strategy S] [--full] [--letters]
 *                                         encontra um caminho euleriano com Fleury (S = tarjan | naive | naive-early | naive-all)
 *   bridges &lt;arquivo&gt; [--method M] [--letters]
 *                                         lista as pontes (M = tarjan | naive | naive-early)
 *   gen &lt;euler|semi|non|connected&gt; &lt;n&gt; [--seed S] [--avgdeg D] [--m M]
 *                                         gera um grafo aleatório na saída padrão (mesmo formato de entrada)
 *   bench ...                             experimentos (ver Benchmark)
 *   test                                  testes de corretude (SelfTest)
 * </pre>
 */
public final class Main {
    private Main() { }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { usage(); return; }
        String[] rest = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "demo":    demo(); break;
            case "euler":   euler(rest); break;
            case "bridges": bridges(rest); break;
            case "gen":     gen(rest); break;
            case "bench":   Benchmark.main(rest); break;
            case "test":    SelfTest.main(rest); break;
            default:        usage();
        }
    }

    private static void usage() {
        System.out.println("Uso: java -cp out grafos.Main <comando> [opções]\n"
                + "  demo                                    exemplo da Figura 1 do enunciado\n"
                + "  euler <arquivo> [--strategy S] [--full] [--letters]\n"
                + "                                          caminho euleriano por Fleury; S = tarjan|naive|naive-early|naive-all\n"
                + "  bridges <arquivo> [--method M] [--letters]\n"
                + "                                          pontes; M = tarjan|naive|naive-early\n"
                + "  gen <euler|semi|non|connected> <n> [--seed S] [--avgdeg D] [--m M]\n"
                + "                                          gera grafo aleatório (stdout)\n"
                + "  bench <fleury|bridges> [opções]         experimentos com medição de tempo\n"
                + "  test                                    testes de corretude");
    }

    // ------------------------------------------------------------------------------------------ utilidades

    private static String opt(String[] a, String name, String def) {
        for (int i = 0; i < a.length - 1; i++) if (a[i].equals(name)) return a[i + 1];
        return def;
    }

    private static boolean flag(String[] a, String name) {
        for (String s : a) if (s.equals(name)) return true;
        return false;
    }

    private static String label(int v, boolean letters) {
        return letters && v < 26 ? String.valueOf((char) ('A' + v)) : String.valueOf(v);
    }

    private static String edgeStr(Graph g, int e, boolean letters) {
        return label(g.edgeU(e), letters) + "–" + label(g.edgeV(e), letters);
    }

    private static String pathStr(int[] path, boolean letters, boolean full) {
        StringBuilder sb = new StringBuilder();
        int n = path.length;
        int limit = full ? n : Math.min(n, 40);
        for (int i = 0; i < limit; i++) { if (i > 0) sb.append(" → "); sb.append(label(path[i], letters)); }
        if (limit < n) {
            sb.append(" → … (").append(n - limit - 3).append(" vértices omitidos; use --full) … → ");
            for (int i = n - 3; i < n; i++) { sb.append(label(path[i], letters)); if (i < n - 1) sb.append(" → "); }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------------------------ comandos

    private static void demo() {
        Graph g = Examples.figure1();
        System.out.println("=== Figura 1 do enunciado: " + g.n + " vértices, " + g.m + " arestas ===");
        StringBuilder deg = new StringBuilder();
        for (int v = 0; v < g.n; v++) deg.append(label(v, true)).append('=').append(g.degree(v)).append(' ');
        System.out.println("Graus: " + deg.toString().trim());
        EulerChecker.Info info = EulerChecker.analyze(g);
        System.out.println("Classificação: " + info.type.label + " (vértices de grau ímpar: " + info.oddCount
                + "; início recomendado: " + label(info.startVertex, true) + ")");

        System.out.println("\n--- Pontes (todas as arestas do grafo) ---");
        for (BridgeFinder f : new BridgeFinder[]{new NaiveBridgeFinder(false), new TarjanBridgeFinder(g)}) {
            long t0 = System.nanoTime();
            int[] br = f.findBridges(g);
            long dt = System.nanoTime() - t0;
            StringBuilder sb = new StringBuilder();
            for (int e : br) sb.append(edgeStr(g, e, true)).append(' ');
            System.out.printf("  %-6s -> %d ponte(s): %s (%.3f ms)%n", f.name(), br.length, sb.toString().trim(), dt / 1e6);
        }

        for (String s : new String[]{"naive", "tarjan"}) {
            System.out.println("\n--- Fleury com estratégia '" + s + "' (trace) ---");
            Tracing tr = new Tracing(Strategies.create(s, g), true);
            Fleury.Result r = Fleury.findEulerPath(g, tr);
            System.out.println("  Caminho: " + pathStr(r.path, true, true));
            String err = EulerPathValidator.validate(g, r.path);
            System.out.println("  Validação independente: " + (err == null ? "OK (todas as 15 arestas exatamente uma vez)" : "ERRO: " + err));
        }
    }

    /** Estratégia decoradora que imprime as decisões de Fleury (usada no demo). */
    private static final class Tracing implements BridgeStrategy {
        private final BridgeStrategy inner;
        private final boolean letters;
        private int cur;
        private int step;
        Tracing(BridgeStrategy inner, boolean letters) { this.inner = inner; this.letters = letters; }
        @Override public String name() { return inner.name(); }
        @Override public void prepare(Graph g, int cur) { this.cur = cur; step++; inner.prepare(g, cur); }
        @Override public boolean isBridge(Graph g, int e) {
            boolean b = inner.isBridge(g, e);
            System.out.println("  [decisão em " + label(cur, letters) + "] aresta " + edgeStr(g, e, letters)
                    + (b ? " é PONTE -> evitada" : " não é ponte -> escolhida"));
            return b;
        }
    }

    private static void euler(String[] a) throws IOException {
        if (a.length < 1) { usage(); return; }
        Graph g = GraphIO.read(Paths.get(a[0]));
        boolean letters = flag(a, "--letters");
        String sname = opt(a, "--strategy", "tarjan");
        BridgeStrategy st = Strategies.create(sname, g);
        System.out.println("Grafo: n=" + g.n + ", m=" + g.m + "; estratégia: " + sname);
        long t0 = System.nanoTime();
        Fleury.Result r = Fleury.run(g.copy(), st, Deadline.NONE);
        long dt = System.nanoTime() - t0;
        System.out.println("Classificação: " + r.type.label);
        if (!r.found()) {
            EulerChecker.Info info = EulerChecker.analyze(g);
            String motivo = info.oddCount > 2
                    ? info.oddCount + " vértices de grau ímpar (só 0 ou 2 são permitidos)"
                    : "o grafo é desconexo (há arestas em mais de uma componente conexa)";
            System.out.println("Não existe caminho euleriano: " + motivo + ".");
        } else {
            System.out.println("Caminho euleriano (" + (r.path.length - 1) + " arestas): " + pathStr(r.path, letters, flag(a, "--full")));
            String err = EulerPathValidator.validate(g, r.path);
            System.out.println("Validação independente: " + (err == null ? "OK" : "ERRO: " + err));
        }
        System.out.printf("Tempo: %.3f ms%n", dt / 1e6);
    }

    private static void bridges(String[] a) throws IOException {
        if (a.length < 1) { usage(); return; }
        Graph g = GraphIO.read(Paths.get(a[0]));
        boolean letters = flag(a, "--letters");
        String method = opt(a, "--method", "tarjan");
        BridgeFinder f;
        switch (method) {
            case "naive": f = new NaiveBridgeFinder(false); break;
            case "naive-early": f = new NaiveBridgeFinder(true); break;
            case "tarjan": f = new TarjanBridgeFinder(g); break;
            default: throw new IllegalArgumentException("método desconhecido: " + method);
        }
        long t0 = System.nanoTime();
        int[] br = f.findBridges(g);
        long dt = System.nanoTime() - t0;
        System.out.println("Grafo: n=" + g.n + ", m=" + g.m + "; método: " + f.name());
        System.out.println(br.length + " ponte(s):");
        for (int e : br) System.out.println("  " + edgeStr(g, e, letters));
        System.out.printf("Tempo: %.3f ms%n", dt / 1e6);
    }

    private static void gen(String[] a) {
        if (a.length < 2) { usage(); return; }
        String cls = a[0];
        int n = Integer.parseInt(a[1]);
        long seed = Long.parseLong(opt(a, "--seed", "1"));
        double avg = Double.parseDouble(opt(a, "--avgdeg", "4"));
        Graph g;
        if (cls.equals("connected")) {
            int m = Integer.parseInt(opt(a, "--m", String.valueOf(Math.round(n * 1.5))));
            g = GraphGenerator.randomConnected(n, m, seed);
        } else {
            g = GraphGenerator.byClass(cls, n, avg, seed);
        }
        GraphIO.write(g, System.out);
    }
}
