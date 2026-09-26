import java.util.*;
import java.io.*;

public class RepresentacaoDeGrafo {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Insira o nome do arquivo: ");
        String nomeArquivo = sc.nextLine();
        System.out.print("Insira o número do vértice alvo: ");
        int verticeAlvo = sc.nextInt();

        int numVertices = 0;
        int numArestas = 0;
        List<List<Integer>> arestasSaida = new ArrayList<>();
        List<List<Integer>> arestasEntrada = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha = br.readLine();
            if (linha == null) {
                System.out.println("Arquivo vazio.");
                return;
            }

            StringTokenizer st = new StringTokenizer(linha);
            numVertices = Integer.parseInt(st.nextToken());
            numArestas = Integer.parseInt(st.nextToken());
            for (int i = 0; i <= numVertices; i++) {
                arestasSaida.add(new ArrayList<>());
                arestasEntrada.add(new ArrayList<>());
            }
            for (int i = 0; i < numArestas; i++) {
                linha = br.readLine();
                if (linha == null) break;

                st = new StringTokenizer(linha);
                int origem = Integer.parseInt(st.nextToken());
                int destino = Integer.parseInt(st.nextToken());
                arestasSaida.get(origem).add(destino);
                arestasEntrada.get(destino).add(origem);
            }

            System.out.println("Leitura concluída com sucesso!\n");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        if (verticeAlvo < 1 || verticeAlvo > numVertices) {
            System.out.println("Erro: O vértice informado (" + verticeAlvo + ") não existe no grafo. Os vértices vão de 1 a " + numVertices + ".");
            return;
        }

        List<Integer> sucessores = arestasSaida.get(verticeAlvo);
        List<Integer> predecessores = arestasEntrada.get(verticeAlvo);
        Collections.sort(sucessores);
        Collections.sort(predecessores);
        int grauSaida = sucessores.size();
        int grauEntrada = predecessores.size();

        System.out.println("--- Informações do Vértice " + verticeAlvo + " ---");
        System.out.println("Grau de saída: " + grauSaida);
        System.out.println("Grau de entrada: " + grauEntrada);
        System.out.println("Conjunto de sucessores: " + (grauSaida == 0 ? "Nenhum" : sucessores));
        System.out.println("Conjunto de predecessores: " + (grauEntrada == 0 ? "Nenhum" : predecessores));
    }
}
