package grafos;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Gera grafo nao direcionado conectado com pesos positivos, formato OR-Library. */
public final class GraphGenerator {
    private GraphGenerator() {}
    public static Path generate(Path file,int n,int k,long seed) throws IOException {
        if(n<2||k<1||k>n)throw new IllegalArgumentException();
        Random r=new Random(seed);
        int m=2*n; // anel + n cordas aleatorias
        Files.createDirectories(file.toAbsolutePath().getParent());
        try(BufferedWriter out=Files.newBufferedWriter(file)){
            out.write(n+" "+m+" "+k+"\n");
            for(int i=1;i<=n;i++){
                int j=i==n?1:i+1;
                out.write(i+" "+j+" "+(1+r.nextInt(100))+"\n");
            }
            for(int i=0;i<n;i++){
                int u=1+r.nextInt(n),v=1+r.nextInt(n-1);
                if(v>=u)v++;
                out.write(u+" "+v+" "+(1+r.nextInt(100))+"\n");
            }
        }
        return file;
    }
}
