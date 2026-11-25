package grafos;

import java.nio.file.*;
import java.util.*;

public final class Main {
    private Main(){}
    private static String option(String[] args,String name,String fallback){
        for(int i=0;i<args.length-1;i++)if(args[i].equals(name))return args[i+1];
        return fallback;
    }
    private static boolean flag(String[] args,String name){return Arrays.asList(args).contains(name);}
    private static void help(){
        System.out.println("Uso: java -cp out grafos.Main <comando> [opcoes]\n"
            +"  test\n"
            +"  resolver <arquivo.txt> [--limite-ms 5000] [--sem-trocas]\n"
            +"  bench [--dir instancias] [--csv resultados/orlib.csv] [--limite-ms 3000] [--exato-max-n 10000] [--primeiro 1] [--ultimo 40] [--acrescentar]\n"
            +"  sinteticos [--limite-ms 10000]\n"
            +"  gerar <arquivo.txt> <n> <k> <semente>");
    }
    public static void main(String[] args) throws Exception {
        if(args.length==0){help();return;}
        switch(args[0]){
            case "test":SelfTest.main(new String[0]);break;
            case "resolver": {
                if(args.length<2){help();return;}
                GraphIO.Instance inst=GraphIO.read(Paths.get(args[1]));
                long prep=GraphIO.allPairsShortestPaths(inst), start=System.nanoTime();
                Solution ap=ApproxKCenter.solve(inst.distances,inst.k,!flag(args,"--sem-trocas"));
                long time=System.nanoTime()-start;
                long limit=Long.parseLong(option(args,"--limite-ms","5000"));
                ExactKCenter.Result ex=ExactKCenter.solve(inst.distances,inst.k,ap,limit);
                System.out.printf(Locale.ROOT,"Instancia=%s n=%d m=%d k=%d; preparo=%.3f ms%n",inst.name,inst.n,inst.m,inst.k,prep/1e6);
                System.out.printf(Locale.ROOT,"Aproximado: raio=%d tempo=%.3f ms centros=%s%n",ap.radius,time/1e6,ap.centerList());
                System.out.printf(Locale.ROOT,"Exato: raio=%d status=%s limite_inferior=%d tempo=%.3f ms centros=%s%n",
                    ex.solution.radius,ex.status,ex.lowerBound,ex.elapsedNanos/1e6,ex.solution.centerList());
                break;
            }
            case "bench":Benchmark.officialRange(
                Paths.get(option(args,"--dir","instancias")),
                Paths.get(option(args,"--csv","resultados/orlib.csv")),
                Paths.get(option(args,"--centros","resultados/centros.txt")),
                Long.parseLong(option(args,"--limite-ms","3000")),
                Integer.parseInt(option(args,"--exato-max-n","10000")),
                Integer.parseInt(option(args,"--primeiro","1")),
                Integer.parseInt(option(args,"--ultimo","40")),
                flag(args,"--acrescentar"));
                break;
            case "sinteticos":Benchmark.synthetic(Paths.get("instancias/sinteticas"),Paths.get("resultados/sinteticos.csv"),
                Long.parseLong(option(args,"--limite-ms","10000")));
                break;
            case "gerar":if(args.length!=5){help();return;}GraphGenerator.generate(Paths.get(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Long.parseLong(args[4]));break;
            default:help();
        }
    }
}
