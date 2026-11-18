package grafos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Experimentos de desempenho. Dois modos:
 *
 * <pre>
 *   fleury  : tempo de Fleury (inclui a verificação das condições de Euler) por estratégia, em grafos
 *             aleatórios eulerianos, semi-eulerianos e não eulerianos.
 *   bridges : tempo da identificação de TODAS as pontes (naïve x Tarjan) em grafos aleatórios conexos.
 * </pre>
 *
 * Opções comuns: {@code --sizes 100,1000,10000,100000}, {@code --reps 30,20,5,3} (uma por tamanho),
 * {@code --avgdeg 4}, {@code --timeout 600} (segundos por execução), {@code --seed 12345},
 * {@code --out resultados/arquivo.csv} (acrescenta linhas se o arquivo existir), {@code --label texto}.
 * Modo fleury: {@code --classes euler,semi,non}, {@code --strategies tarjan,naive,naive-early,naive-all}.
 * Modo bridges: {@code --methods tarjan,naive,naive-early}.
 *
 * <p>Metodologia: para cada (tamanho, classe, repetição) gera-se UM grafo (semente determinística) e cada
 * estratégia o resolve a partir de uma cópia nova; o tempo medido (System.nanoTime) cobre somente a chamada
 * ao algoritmo (classificação + passos de Fleury), não a geração, a cópia, a criação dos vetores auxiliares
 * nem a validação do resultado (feita depois, fora da medição, por um verificador independente).
 * Antes das medições há uma fase de aquecimento da JIT.
 */
public final class Benchmark {
    private Benchmark() { }

    static final String HEADER = "experimento,rotulo,classe,n,m,rep,semente,estrategia,tempo_ms,status,passos,passos_total,valido,forcados,decisoes,testes,evitadas";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { System.out.println("uso: bench <fleury|bridges> [opções] (ver README)"); return; }
        String mode = args[0];
        String[] a = java.util.Arrays.copyOfRange(args, 1, args.length);
        int[] sizes = ints(opt(a, "--sizes", "100,1000,10000,100000"));
        int[] reps = ints(opt(a, "--reps", "30,20,5,3"));
        double avgDeg = Double.parseDouble(opt(a, "--avgdeg", mode.equals("bridges") ? "3" : "4"));
        double timeout = Double.parseDouble(opt(a, "--timeout", "600"));
        long baseSeed = Long.parseLong(opt(a, "--seed", "12345"));
        String out = opt(a, "--out", "resultados/" + mode + ".csv");
        String label = opt(a, "--label", mode);
        int warm = Integer.parseInt(opt(a, "--warmup", "6"));
        int repStart = Integer.parseInt(opt(a, "--rep-start", "0"));
        if (reps.length < sizes.length) throw new IllegalArgumentException("--reps precisa de um valor por tamanho");

        File f = new File(out);
        boolean fresh = !f.exists();
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
            if (fresh) pw.println(HEADER);
            if (mode.equals("fleury")) {
                String[] classes = opt(a, "--classes", "euler,semi,non").split(",");
                String[] strategies = opt(a, "--strategies", "tarjan,naive,naive-early").split(",");
                fleury(pw, label, sizes, reps, repStart, classes, strategies, avgDeg, timeout, baseSeed, warm);
            } else if (mode.equals("bridges")) {
                String[] methods = opt(a, "--methods", "tarjan,naive").split(",");
                bridges(pw, label, sizes, reps, repStart, methods, avgDeg, timeout, baseSeed, warm);
            } else {
                throw new IllegalArgumentException("modo desconhecido: " + mode);
            }
        }
    }

    static long seedFor(long base, int n, int rep) { return base * 1_000_003L + n * 31L + rep; }

    // ------------------------------------------------------------------------------------------ Fleury

    private static void fleury(PrintWriter pw, String label, int[] sizes, int[] reps, int repStart, String[] classes,
                               String[] strategies, double avgDeg, double timeout, long baseSeed, int warm) {
        info("Aquecimento da JIT...");
        for (String s : strategies) {
            if (s.equals("naive-all")) continue;
            for (int i = 0; i < warm; i++) {
                Graph g = GraphGenerator.eulerian(1000, avgDeg, 9000 + i);
                Fleury.run(g.copy(), Strategies.create(s, g), Deadline.NONE);
                Graph h = GraphGenerator.semiEulerian(200, avgDeg, 9100 + i);
                Fleury.run(h.copy(), Strategies.create(s, h), Deadline.NONE);
            }
        }
        for (int si = 0; si < sizes.length; si++) {
            int n = sizes[si];
            if (n <= 1000 && warm > 0) { // aquecimento específico do tamanho (execuções não registradas)
                for (int i = 0; i < 5 * warm; i++) {
                    for (String cls : classes) {
                        Graph w = GraphGenerator.byClass(cls, n, avgDeg, 7000 + i);
                        for (String s : strategies) {
                            if (s.equals("naive-all")) continue;
                            Fleury.run(w.copy(), Strategies.create(s, w), Deadline.NONE);
                        }
                    }
                }
            }
            for (int rep = repStart; rep < repStart + reps[si]; rep++) {
                long seed = seedFor(baseSeed, n, rep);
                for (String cls : classes) {
                    Graph base = GraphGenerator.byClass(cls, n, avgDeg, seed);
                    for (String s : strategies) {
                        Graph g = base.copy();
                        BridgeStrategy st = Strategies.create(s, g);
                        if (n >= 10000) System.gc();
                        Deadline dl = timeout > 0 ? Deadline.afterSeconds(timeout) : Deadline.NONE;
                        long t0 = System.nanoTime();
                        Fleury.Result r = null;
                        long progress = base.m;
                        String status = "ok";
                        try {
                            r = Fleury.run(g, st, dl);
                        } catch (Deadline.TimeLimitExceeded ex) {
                            status = "timeout";
                            progress = ex.progress;
                        }
                        long dt = System.nanoTime() - t0;
                        int valid = 1;
                        if (r != null) {
                            boolean expectPath = !cls.equals("non");
                            if (r.found() != expectPath) valid = 0;
                            else if (r.found() && EulerPathValidator.validate(base, r.path) != null) valid = 0;
                        }
                        if (valid == 0) System.err.println("!!! RESULTADO INVÁLIDO: " + cls + " n=" + n + " rep=" + rep + " " + s);
                        long[] stats = r == null ? new long[]{-1, -1, -1, -1}
                                : new long[]{r.forcedSteps, r.decisionSteps, r.bridgeTests, r.bridgesAvoided};
                        row(pw, "fleury", label, cls, n, base.m, rep, seed, s, dt, status, progress, base.m, valid, stats);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------------ pontes

    private static void bridges(PrintWriter pw, String label, int[] sizes, int[] reps, int repStart, String[] methods,
                                double avgDeg, double timeout, long baseSeed, int warm) {
        info("Aquecimento da JIT...");
        for (int i = 0; i < warm; i++) {
            Graph g = GraphGenerator.randomConnected(500, 750, 9000 + i);
            for (String mth : methods) finder(mth, g).findBridges(g);
        }
        for (int si = 0; si < sizes.length; si++) {
            int n = sizes[si];
            int m = (int) Math.round(n * avgDeg / 2.0);
            if (n <= 1000 && warm > 0) { // aquecimento específico do tamanho (execuções não registradas)
                for (int i = 0; i < 5 * warm; i++) {
                    Graph w = GraphGenerator.randomConnected(n, m, 7000 + i);
                    for (String mth : methods) finder(mth, w).findBridges(w.copy());
                }
            }
            for (int rep = repStart; rep < repStart + reps[si]; rep++) {
                long seed = seedFor(baseSeed, n, rep);
                Graph base = GraphGenerator.randomConnected(n, m, seed);
                int[] ref = null;
                for (String mth : methods) {
                    Graph g = base.copy();
                    BridgeFinder bf = finder(mth, g);
                    if (n >= 10000) System.gc();
                    Deadline dl = timeout > 0 ? Deadline.afterSeconds(timeout) : Deadline.NONE;
                    long t0 = System.nanoTime();
                    String status = "ok";
                    long progress = m;
                    int[] br = null;
                    try { br = bf.findBridges(g, dl); }
                    catch (Deadline.TimeLimitExceeded ex) { status = "timeout"; progress = ex.progress; }
                    long dt = System.nanoTime() - t0;
                    int valid = 1;
                    if (br != null) {
                        if (ref == null) ref = br;
                        else if (!java.util.Arrays.equals(ref, br)) valid = 0;
                        // o resultado precisa ser o mesmo do Tarjan; confere também a contagem de componentes via Tarjan
                    }
                    if (valid == 0) System.err.println("!!! PONTES DIVERGENTES: n=" + n + " rep=" + rep + " " + mth);
                    row(pw, "pontes", label, "conexo", n, m, rep, seed, mth, dt, status, progress, m, valid,
                            br == null ? new long[]{-1, -1, -1, -1} : new long[]{-1, -1, -1, br.length});
                }
            }
        }
    }

    private static BridgeFinder finder(String name, Graph g) {
        switch (name) {
            case "tarjan": return new TarjanBridgeFinder(g);
            case "naive": return new NaiveBridgeFinder(false);
            case "naive-early": return new NaiveBridgeFinder(true);
            default: throw new IllegalArgumentException("método desconhecido: " + name);
        }
    }

    // ------------------------------------------------------------------------------------------ util

    private static void row(PrintWriter pw, String exp, String label, String cls, int n, int m, int rep, long seed,
                            String strat, long nanos, String status, long progress, long total, int valid,
                            long[] stats) {
        String line = String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%d,%d,%s,%.6f,%s,%d,%d,%d,%d,%d,%d,%d",
                exp, label, cls, n, m, rep, seed, strat, nanos / 1e6, status, progress, total, valid,
                stats[0], stats[1], stats[2], stats[3]);
        pw.println(line);
        pw.flush();
        info(String.format(Locale.ROOT, "%-7s %-6s n=%-6d rep=%-2d %-12s %12.3f ms  %s %s", exp, cls, n, rep, strat,
                nanos / 1e6, status, valid == 1 ? "" : "INVALIDO"));
    }

    private static void info(String s) { System.err.println(s); }

    private static String opt(String[] a, String name, String def) {
        for (int i = 0; i < a.length - 1; i++) if (a[i].equals(name)) return a[i + 1];
        return def;
    }

    private static int[] ints(String csv) {
        List<Integer> l = new ArrayList<>();
        for (String s : csv.split(",")) l.add(Integer.parseInt(s.trim()));
        int[] r = new int[l.size()];
        for (int i = 0; i < r.length; i++) r[i] = l.get(i);
        return r;
    }
}
