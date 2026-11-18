package grafos;

import java.io.IOException;

/**
 * Testes adicionais das instâncias grandes. Não faz parte das medições do relatório:
 * verifica que a implementação não estoura a pilha, gera grafos simples e
 * encontra caminhos eulerianos válidos também em grafos maiores.
 */
public final class StressTest {
    private StressTest() { }

    private static int checks = 0;
    private static void check(boolean value, String reason) {
        checks++;
        if (!value) throw new AssertionError(reason);
    }

    private static void validateSimple(Graph g) {
        try {
            GraphIO.checkSimple(g.n, g.eu, g.ev);
        } catch (IOException e) {
            throw new AssertionError("O gerador criou grafo não simples: " + e.getMessage(), e);
        }
        checks++;
    }

    private static void checkEuler(int n, String cls, long seed) {
        Graph g = GraphGenerator.byClass(cls, n, 4.0, seed);
        validateSimple(g);
        EulerChecker.Info info = EulerChecker.analyze(g);
        EulerChecker.Type expect = cls.equals("euler") ? EulerChecker.Type.EULERIAN
                : cls.equals("semi") ? EulerChecker.Type.SEMI_EULERIAN
                : EulerChecker.Type.NON_EULERIAN;
        check(info.type == expect, "Classificação errada em " + cls + " n=" + n);
        check(g.n == n, "n incompatível");
        check(g.m >= n - 2, "Número de arestas inesperado");
        System.out.printf("Grafo %s n=%d m=%d: classificação e simplicidade OK%n", cls, n, g.m);
        if (cls.equals("non")) {
            Fleury.Result r = Fleury.findEulerPath(g, Strategies.create("tarjan", g));
            check(!r.found(), "Fleury deveria rejeitar o grafo não euleriano");
            return;
        }
        // n=10.000: executa os dois caminhos completos e compara resultados.
        if (n <= 10_000) {
            Fleury.Result fast = Fleury.findEulerPath(g, Strategies.create("naive-early", g));
            Fleury.Result tarjan = Fleury.findEulerPath(g, Strategies.create("tarjan", g));
            check(fast.found() && tarjan.found(), "Fleury não encontrou caminho existente");
            check(java.util.Arrays.equals(fast.path, tarjan.path), "Estratégias produziram trajetos diferentes");
            check(EulerPathValidator.validate(g, fast.path) == null, "Caminho naive inválido");
            check(EulerPathValidator.validate(g, tarjan.path) == null, "Caminho Tarjan inválido");
            System.out.printf("Fleury (%s) em n=%d: %d arestas, caminho validado pelas duas estratégias%n",
                    cls, n, g.m);
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        for (String cls : new String[]{"euler", "semi", "non"}) checkEuler(10_000, cls, 3301L);
        for (String cls : new String[]{"euler", "semi", "non"}) checkEuler(100_000, cls, 3301L);

        Graph large = GraphGenerator.randomConnected(100_000, 150_000, 3301L);
        validateSimple(large);
        int[] bridges = new TarjanBridgeFinder(large).findBridges(large);
        check(bridges.length > 0 && bridges.length < large.m, "Pontes fora do esperado");
        System.out.printf("Tarjan n=%d m=%d: %d pontes, DFS iterativa OK%n", large.n, large.m, bridges.length);

        Graph small = GraphGenerator.randomConnected(1000, 1500, 42L);
        check(java.util.Arrays.equals(new TarjanBridgeFinder(small).findBridges(small),
                new NaiveBridgeFinder(false).findBridges(small)), "Naive x Tarjan divergem");
        System.out.printf("STRESS TEST PASSOU: %d checagens, %.2f segundos%n",
                checks, (System.nanoTime() - start) / 1e9);
    }
}
