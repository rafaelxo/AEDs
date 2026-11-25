package grafos;

import java.io.*;
import java.nio.file.*;

/** Le o formato OR-Library: n m k, seguidos de m triplas (u,v,peso). */
public final class GraphIO {
    private GraphIO() {}
    public static final int INF = 1_000_000_000;

    public static final class Instance {
        public final int n, m, k;
        public final int[][] distances;
        public final String name;
        Instance(int n, int m, int k, int[][] d, String name) {
            this.n=n; this.m=m; this.k=k; this.distances=d; this.name=name;
        }
    }

    public static Instance read(Path file) throws IOException {
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(file))) {
            FastIntReader reader = new FastIntReader(stream);
            int n = reader.next(), m = reader.next(), k = reader.next();
            if (n <= 0 || n > 10000 || m < 0 || k <= 0 || k > n)
                throw new IOException("Cabecalho invalido em " + file);
            int[][] d = new int[n][n];
            for (int i=0;i<n;i++) for (int j=0;j<n;j++) d[i][j]= i==j ? 0 : INF;
            for (int a=0; a<m; a++) {
                int u=reader.next()-1, v=reader.next()-1, w=reader.next();
                if (u<0 || v<0 || u>=n || v>=n || u==v || w<0 || w>=INF)
                    throw new IOException("Aresta invalida: " + (a+1) + " em " + file);
                // IMPORTANTE: conforme especificacao da OR-Library, ultima aresta paralela prevalece.
                d[u][v]=w;
                d[v][u]=w;
            }
            if (reader.hasNext()) throw new IOException("Dados extras depois das " + m + " arestas: " + file);
            return new Instance(n,m,k,d,file.getFileName().toString());
        } catch (EOFException e) {
            throw new IOException("Arquivo incompleto: " + file,e);
        }
    }

    public static long allPairsShortestPaths(Instance inst) {
        long start=System.nanoTime();
        int[][] d=inst.distances;
        for (int mid=0;mid<inst.n;mid++) {
            int[] dm = d[mid];
            for (int i=0;i<inst.n;i++) {
                int via = d[i][mid];
                if (via==INF) continue;
                int[] di = d[i];
                for (int j=0;j<inst.n;j++) {
                    if (dm[j]==INF) continue;
                    int alt=via+dm[j];
                    if (alt < di[j]) di[j]=alt;
                }
            }
        }
        for (int i=0;i<inst.n;i++) for (int j=0;j<inst.n;j++)
            if(d[i][j]==INF) throw new IllegalArgumentException("Grafo desconexo: "+inst.name);
        return System.nanoTime()-start;
    }

    private static final class FastIntReader {
        final InputStream stream;
        final byte[] buf=new byte[65536];
        int at=0, length=0, cached=-2;
        FastIntReader(InputStream s){stream=s;}
        private int read() throws IOException {
            if (at>=length) {length=stream.read(buf);at=0;if(length<0)return -1;}
            return buf[at++]&255;
        }
        boolean hasNext() throws IOException {
            if(cached==-2) {
                do {cached=read();} while(cached>=0 && cached<=32);
            }
            return cached>=0;
        }
        int next() throws IOException {
            int c;
            if(cached!=-2){c=cached;cached=-2;} else {do{c=read();}while(c>=0 && c<=32);}
            if(c<0)throw new EOFException();
            int value=0, sign=1;
            if(c=='-'){sign=-1;c=read();}
            if(c<'0'||c>'9') throw new IOException("Token nao numerico");
            do {value=value*10+c-'0';c=read();} while(c>='0'&&c<='9');
            if(c>32) throw new IOException("Token numerico invalido");
            return sign*value;
        }
    }
}
