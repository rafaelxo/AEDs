import java.io.*;
import java.util.*;

public class BuscaEmGrafo {
    static int time = 0;
    static int[] color;
    static int[] d;
    static int[] f;
    static List<Integer>[] adj;
    static int targetVertex;
    static List<String> targetClassifications = new ArrayList<>();

    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Insira o nome do arquivo: ");
        String nomeArquivo = sc.nextLine().trim();
        System.out.print("Insira o número do vértice alvo: ");
        targetVertex = sc.nextInt();

        try {
            FastScanner scanner = new FastScanner(nomeArquivo);

            String firstToken = scanner.next();
            if (firstToken == null) {
                System.out.println("Arquivo vazio.");
                return;
            }

            int n = Integer.parseInt(firstToken);
            int m = scanner.nextInt();

            adj = new ArrayList[n + 1];
            for (int i = 1; i <= n; i++) {
                adj[i] = new ArrayList<>();
            }

            for (int i = 0; i < m; i++) {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                adj[u].add(v);
            }

            for (int i = 1; i <= n; i++) {
                Collections.sort(adj[i]);
            }

            color = new int[n + 1];
            d = new int[n + 1];
            f = new int[n + 1];

            System.out.println("\n Arestas de Arvore Encontradas (Grafo Completo)");

            for (int i = 1; i <= n; i++) {
                if (color[i] == 0) dfsVisit(i);
            }

            System.out.println("\n Classificacao das Arestas Saindo do Vertice " + targetVertex);
            if (targetClassifications.isEmpty()) {
                System.out.println("Nenhuma aresta sai do vertice " + targetVertex + ".");
            } else {
                for (String edge : targetClassifications) {
                    System.out.println(edge);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erro: O arquivo especificado nao foi encontrado.");
        } catch (Exception e) {
            System.out.println("Erro durante a execucao: " + e.getMessage());
        }
    }

    static void dfsVisit(int u) {
        color[u] = 1;
        time++;
        d[u] = time;

        for (int v : adj[u]) {
            if (u == targetVertex) {
                if (color[v] == 0) {
                    targetClassifications.add(u + " -> " + v + " : Aresta de Arvore");
                } else if (color[v] == 1) {
                    targetClassifications.add(u + " -> " + v + " : Aresta de Retorno");
                } else if (color[v] == 2) {
                    if (d[u] < d[v]) {
                        targetClassifications.add(u + " -> " + v + " : Aresta de Avanco");
                    } else {
                        targetClassifications.add(u + " -> " + v + " : Aresta de Cruzamento");
                    }
                }
            }

            if (color[v] == 0) {
                System.out.println(u + " -> " + v);
                dfsVisit(v);
            }
        }

        color[u] = 2;
        time++;
        f[u] = time;
    }

    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        public FastScanner(String nomeArquivo) throws FileNotFoundException {
            br = new BufferedReader(new FileReader(nomeArquivo));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
