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

    @SuppressWarnings("unchecked")
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

            adj = (List<Integer>[]) new ArrayList[n + 1];
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

            System.out.println("\nArestas de Árvore Encontradas (Grafo Completo):");

            for (int i = 1; i <= n; i++) {
                if (color[i] == 0)
                    dfsVisit(i);
            }

            System.out.println("\nClassificação das Arestas Saindo do Vértice " + targetVertex + ": ");
            if (targetClassifications.isEmpty()) {
                System.out.println("Nenhuma aresta sai do vértice " + targetVertex + ".");
            } else {
                for (String edge : targetClassifications) {
                    System.out.println(edge);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erro: O arquivo especificado não foi encontrado.");
        } catch (Exception e) {
            System.out.println("Erro durante a execução: " + e.getMessage());
        }
    }

    static void dfsVisit(int startNode) {
        Stack<Integer> stack = new Stack<>();
        int[] edgeIndex = new int[color.length];

        color[startNode] = 1;
        time++;
        d[startNode] = time;
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int u = stack.peek();

            if (edgeIndex[u] < adj[u].size()) {
                int v = adj[u].get(edgeIndex[u]);
                edgeIndex[u]++;

                if (u == targetVertex) {
                    if (color[v] == 0) {
                        targetClassifications.add(u + " -> " + v + " = Aresta de Árvore");
                    } else if (color[v] == 1) {
                        targetClassifications.add(u + " -> " + v + " = Aresta de Retorno");
                    } else if (color[v] == 2) {
                        if (d[u] < d[v]) {
                            targetClassifications.add(u + " -> " + v + " = Aresta de Avanço");
                        } else {
                            targetClassifications.add(u + " -> " + v + " = Aresta de Cruzamento");
                        }
                    }
                }

                if (color[v] == 0) {
                    System.out.println(u + " -> " + v);
                    color[v] = 1;
                    time++;
                    d[v] = time;
                    stack.push(v);
                }
            } else {
                stack.pop();
                color[u] = 2;
                time++;
                f[u] = time;
            }
        }
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
                    if (line == null)
                        return null;
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
