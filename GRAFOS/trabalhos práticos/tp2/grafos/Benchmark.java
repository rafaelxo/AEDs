package grafos;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Executa todos os arquivos pmed1..40 e registra resultados sem inventar dados faltantes. */
public final class Benchmark {
    private Benchmark(){}
    public static void official(Path dir,Path csv,Path centersPath,long maxExactMs,int maxExactN) throws IOException {
        officialRange(dir, csv, centersPath, maxExactMs, maxExactN, 1, 40, false);
    }

    /** Permite dividir a campanha experimental em lotes, preservando os CSVs acumulados. */
    public static void officialRange(Path dir,Path csv,Path centersPath,long maxExactMs,int maxExactN,
                                     int first,int last,boolean append) throws IOException {
        if(first<1 || last>40 || first>last) throw new IllegalArgumentException("Faixa de instancias invalida");
        if(append && (!Files.isRegularFile(csv) || !Files.isRegularFile(centersPath)))
            throw new IOException("CSV e centros precisam existir para continuar o experimento");
        if(maxExactMs<=0) throw new IllegalArgumentException("Limite exato deve ser positivo para benchmark");
        Files.createDirectories(csv.toAbsolutePath().getParent());
        Files.createDirectories(centersPath.toAbsolutePath().getParent());
        int done=0;
        try(BufferedWriter out=Files.newBufferedWriter(csv, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                 append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
            BufferedWriter centers=Files.newBufferedWriter(centersPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                 append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {
            if(!append)out.write("instancia;n;m;k;raio_referencia;preparo_ms;aprox_raio;aprox_ms;gap_percentual;exato_raio;exato_ms;status_exato;limite_inferior_exato\n");
            for(int number=first;number<=last;number++){
                String name="pmed"+number;
                Path file=dir.resolve(name+".txt");
                int ref=ReferenceTable.RADIUS[number];
                if(!Files.exists(file)){
                    out.write(String.join(";", name, String.valueOf(ReferenceTable.N[number]), "", String.valueOf(ReferenceTable.K[number]), String.valueOf(ref), "", "", "", "", "", "", "INSTANCIA_AUSENTE", "")+"\n");
                    continue;
                }
                GraphIO.Instance inst=GraphIO.read(file);
                if(inst.n!=ReferenceTable.N[number]||inst.k!=ReferenceTable.K[number])
                    throw new IOException("Tamanho ou k diferente do enunciado em "+file);
                long prep=GraphIO.allPairsShortestPaths(inst);
                long t=System.nanoTime();
                Solution approximate=ApproxKCenter.solve(inst.distances,inst.k,true);
                long apMs=System.nanoTime()-t;
                // Divergencias com valores publicados sao registradas e auditadas; nao impedem o benchmark.
                double gap=100.0*(approximate.radius-ref)/ref;
                String exactR="",exactTime="",status="NAO_EXECUTADO_POR_TAMANHO",lower="";
                centers.write(name+";aproximado;"+approximate.radius+";"+approximate.centerList()+"\n");
                if(inst.n<=maxExactN){
                    ExactKCenter.Result ex=ExactKCenter.solve(inst.distances,inst.k,approximate,maxExactMs);
                    exactR=""+ex.solution.radius;
                    exactTime=String.format(Locale.ROOT,"%.3f",ex.elapsedNanos/1e6);
                    status=ex.status.name();lower=""+ex.lowerBound;
                    centers.write(name+";exato;"+ex.solution.radius+";"+ex.solution.centerList()+"\n");
                    if(ex.status==ExactKCenter.Status.OPTIMO_COMPROVADO && ex.solution.radius!=ref) {
                        status="OTIMO_COMPROVADO_DIVERGENTE";
                        System.err.printf("ATENCAO: %s referencia=%d, otimo calculado=%d. Conferir literatura e dados.%n", name, ref, ex.solution.radius);
                    }
                }
                out.write(String.format(Locale.ROOT,
                    "%s;%d;%d;%d;%d;%.3f;%d;%.3f;%.2f;%s;%s;%s;%s%n",
                    name,inst.n,inst.m,inst.k,ref,prep/1e6,approximate.radius,apMs/1e6,gap,exactR,exactTime,status,lower));
                out.flush();centers.flush();done++;
                System.out.printf(Locale.ROOT,"%s: n=%d k=%d raio aprox=%d (ref=%d, gap=%.1f%%), exato=%s raio=%s%n",
                    name,inst.n,inst.k,approximate.radius,ref,gap,status,exactR);
            }
        }
        System.out.println("Processadas "+done+" instancias oficiais nesta etapa ["+first+".."+last+"]. Resultados: "+csv);
    }

    public static void synthetic(Path dir,Path csv,long maxExactMs) throws IOException {
        Files.createDirectories(dir);
        Files.createDirectories(csv.toAbsolutePath().getParent());
        int[] sizes={10,12,16,20,40,100,200,400,600,900};
        int[] kValues={2,3,3,4,5,10,20,40,60,90};
        try(BufferedWriter out=Files.newBufferedWriter(csv)){
            out.write("n;m;k;semente;preparo_ms;aprox_raio;aprox_ms;exato_raio;exato_ms;status_exato;limite_inferior\n");
            for(int i=0;i<sizes.length;i++){
                int n=sizes[i],k=kValues[i];
                long seed=20261116L+i;
                Path file=GraphGenerator.generate(dir.resolve("sintetico-n"+n+".txt"),n,k,seed);
                GraphIO.Instance inst=GraphIO.read(file);
                long prep=GraphIO.allPairsShortestPaths(inst);
                long t=System.nanoTime();
                Solution ap=ApproxKCenter.solve(inst.distances,k,true);
                long aproxTime=System.nanoTime()-t;
                String exR="",exTime="",exStatus="NAO_EXECUTADO_POR_TAMANHO",bound="";
                {
                    ExactKCenter.Result ex=ExactKCenter.solve(inst.distances,k,ap,maxExactMs);
                    exR=""+ex.solution.radius;
                    exTime=String.format(Locale.ROOT,"%.3f",ex.elapsedNanos/1e6);
                    exStatus=ex.status.name();bound=""+ex.lowerBound;
                }
                out.write(String.format(Locale.ROOT,"%d;%d;%d;%d;%.3f;%d;%.3f;%s;%s;%s;%s%n",
                    n,inst.m,k,seed,prep/1e6,ap.radius,aproxTime/1e6,exR,exTime,exStatus,bound));
                out.flush();
                System.out.printf(Locale.ROOT,"Sintetico n=%d k=%d: raio=%d aprox=%.3f ms exato=%s%n",n,k,ap.radius,aproxTime/1e6,exStatus);
            }
        }
    }
}
