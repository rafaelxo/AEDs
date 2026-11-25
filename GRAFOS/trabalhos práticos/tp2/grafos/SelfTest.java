package grafos;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Testes independentes incluindo enumeracao exaustiva para instancias pequenas. */
public final class SelfTest {
    private SelfTest() {}
    static int tests=0;
    static void check(boolean condition,String message){tests++;if(!condition)throw new AssertionError(message);}
    public static void main(String[] args) throws Exception {
        Path temp=Files.createTempDirectory("kcentros-teste-");
        try {
            Path p=temp.resolve("duplicada.txt");
            Files.writeString(p,"3 4 1\n1 2 2\n2 3 3\n1 3 20\n1 2 7\n");
            GraphIO.Instance small=GraphIO.read(p);
            GraphIO.allPairsShortestPaths(small);
            check(small.distances[0][1]==7,"ultima aresta paralela");
            check(small.distances[0][2]==10,"Floyd e arestas paralelas");
            check(small.distances[1][2]==3,"terceira distancia");
            for(int n=5;n<=12;n++)for(int k=1;k<=Math.min(4,n);k++){
                Path f=GraphGenerator.generate(temp.resolve("teste-"+n+"-"+k+".txt"),n,k,729L+n*17+k);
                GraphIO.Instance inst=GraphIO.read(f);
                GraphIO.allPairsShortestPaths(inst);
                int brute=brute(inst.distances,k);
                Solution ap=ApproxKCenter.solve(inst.distances,k,true);
                check(ap.centers.length<=k && ap.radius>=brute,"solucao aproximada valida n="+n);
                check(ap.radius<=2*brute,"garantia 2-approx n="+n);
                ExactKCenter.Result ex=ExactKCenter.solve(inst.distances,k,ap,5000);
                check(ex.status==ExactKCenter.Status.OPTIMO_COMPROVADO,"certificacao n="+n+" k="+k);
                check(ex.solution.radius==brute,"exato vs enumeracao n="+n+" k="+k+" esperado="+brute+" obtido="+ex.solution.radius);
            }
            System.out.println("OK: "+tests+" assercoes; duplicatas, Floyd, solucao, 2-aproximacao e exato vs enumeracao.");
        }finally{
            try(java.util.stream.Stream<Path> s=Files.walk(temp)){
                s.sorted(Comparator.reverseOrder()).forEach(f->{try{Files.deleteIfExists(f);}catch(IOException ignored){}});
            }
        }
    }
    static int brute(int[][] d,int k){
        int[] centers=new int[k];
        return enumerate(d,centers,0,0,k,Integer.MAX_VALUE);
    }
    static int enumerate(int[][] d,int[] c,int used,int start,int k,int best){
        if(used==k)return Math.min(best,new Solution(c,d).radius);
        for(int v=start;v<=d.length-(k-used);v++){
            c[used]=v;
            best=enumerate(d,c,used+1,v+1,k,best);
        }
        return best;
    }
}
