package grafos;

import java.util.*;

/** Busca binaria no raio; decisao via cobertura de conjuntos e branch-and-bound. */
public final class ExactKCenter {
    private ExactKCenter(){}
    public enum Status { OPTIMO_COMPROVADO, TEMPO_ESGOTADO }
    public static final class Result {
        public final Solution solution;
        public final Status status;
        public final int lowerBound;
        public final long nodes, elapsedNanos;
        Result(Solution s, Status st,int lower,long nodes,long elapsed){
            solution=s; status=st; lowerBound=lower; this.nodes=nodes;elapsedNanos=elapsed;
        }
    }

    public static Result solve(int[][] d, int k, Solution upper, long limitMs) {
        long begin=System.nanoTime();
        long deadline=limitMs<=0 ? Long.MAX_VALUE : begin+limitMs*1_000_000L;
        int n=d.length;
        if(k>=n){
            int[] c=new int[n];for(int i=0;i<n;i++)c[i]=i;
            return new Result(new Solution(c,d),Status.OPTIMO_COMPROVADO,0,0,System.nanoTime()-begin);
        }
        if(upper==null)upper=ApproxKCenter.solve(d,k,false);
        Solution best=upper;
        int lowerGuarantee=(upper.radius+1)/2; // 2-aproximacao (metricas)
        int[] values=new int[n*n];
        int size=0;
        for(int i=0;i<n;i++) for(int j=i;j<n;j++){
            int value=d[i][j];
            if(value>=lowerGuarantee&&value<=best.radius)values[size++]=value;
        }
        Arrays.sort(values,0,size);
        int distinct=0;
        for(int i=0;i<size;i++)if(distinct==0||values[i]!=values[distinct-1])values[distinct++]=values[i];
        if(distinct==0) throw new IllegalStateException("Nenhum raio candidato");
        int lo=0,hi=distinct-1;
        long visited=0;
        boolean timedOut=false;
        while(lo<=hi){
            if(System.nanoTime()>deadline){timedOut=true;break;}
            int mid=(lo+hi)>>>1;
            CoverSearch decider=null;
            try {
                decider=new CoverSearch(d,k,values[mid],deadline);
                int[] selected=decider.find();
                visited+=decider.nodes;
                if(selected!=null) {
                    Solution got=new Solution(extendToK(selected,k,n),d);
                    if(got.radius<best.radius)best=got;
                    hi=mid-1;
                }else lo=mid+1;
            }catch(LimitException e){if(decider!=null) visited+=decider.nodes;timedOut=true;break;}
        }
        int lb=lo<distinct ? values[lo] : best.radius;
        // 'lo' e a primeira faixa ainda potencialmente viavel.
        if(timedOut) return new Result(best,Status.TEMPO_ESGOTADO,Math.min(lb,best.radius),visited,System.nanoTime()-begin);
        return new Result(best,Status.OPTIMO_COMPROVADO,best.radius,visited,System.nanoTime()-begin);
    }
    private static int[] extendToK(int[] selected,int k,int n) {
        if(selected.length==k)return selected;
        boolean[] used=new boolean[n];
        for(int c:selected)used[c]=true;
        int[] full=Arrays.copyOf(selected,k);
        int next=selected.length;
        for(int c=0;c<n && next<k;c++)if(!used[c])full[next++]=c;
        return full;
    }
    static final class LimitException extends RuntimeException {}
    private static final class CoverSearch {
        final int n,k,radius;
        final int[][] distances;
        final long deadline;
        final BitSet[] covers;
        final int[][] options;
        long nodes;
        int[] solution;
        CoverSearch(int[][] d,int k,int radius,long deadline){
            this.n=d.length;this.k=k;this.radius=radius;this.distances=d;this.deadline=deadline;
            covers=new BitSet[n];
            for(int c=0;c<n;c++){
                if((c&15)==0 && System.nanoTime()>=deadline)throw new LimitException();
                BitSet b=new BitSet(n);
                for(int v=0;v<n;v++)if(d[v][c]<=radius)b.set(v);
                covers[c]=b;
            }
            options=new int[n][];
            for(int v=0;v<n;v++){
                if((v&15)==0 && System.nanoTime()>=deadline)throw new LimitException();
                int[] tmp=new int[n];int sz=0;
                for(int c=0;c<n;c++)if(covers[c].get(v))tmp[sz++]=c;
                options[v]=Arrays.copyOf(tmp,sz);
            }
        }
        int[] find(){
            BitSet rem=new BitSet(n);rem.set(0,n);
            int[] selected=new int[k];
            if(dfs(rem,k,selected,0))return solution;
            return null;
        }
        boolean dfs(BitSet remaining,int left,int[] chosen,int depth){
            nodes++;
            if((nodes&7)==0 && System.nanoTime()>=deadline)throw new LimitException();
            if(remaining.isEmpty()){
                solution=Arrays.copyOf(chosen,depth);
                return true;
            }
            if(left==0)return false;
            int count=remaining.cardinality(),maxCover=0;
            for(int c=0;c<n;c++){
                BitSet s=(BitSet)covers[c].clone();
                s.and(remaining);
                maxCover=Math.max(maxCover,s.cardinality());
            }
            if(maxCover==0 || (count+maxCover-1)/maxCover>left)return false;
            // Empacotamento: pontos separados por distancia > 2r exigem centros diferentes.
            int[] packing=new int[left+1];int num=0;
            for(int v=remaining.nextSetBit(0);v>=0;v=remaining.nextSetBit(v+1)){
                boolean far=true;
                for(int i=0;i<num;i++)if(distances[v][packing[i]]<=2L*radius){far=false;break;}
                if(far){packing[num++]=v;if(num>left)return false;}
            }
            int pivot=-1,minOptions=Integer.MAX_VALUE;
            for(int v=remaining.nextSetBit(0);v>=0;v=remaining.nextSetBit(v+1)){
                // Todas as opcoes cobrem v, mas as menos promissoras sao exploradas por ultimo.
                int len=options[v].length;
                if(len<minOptions){minOptions=len;pivot=v;}
            }
            int[] possibilities=options[pivot].clone();
            int[] gains=new int[possibilities.length];
            for(int i=0;i<possibilities.length;i++){
                BitSet gain=(BitSet)covers[possibilities[i]].clone();
                gain.and(remaining);gains[i]=gain.cardinality();
            }
            // Ordena candidatos por pontos descobertos em ordem decrescente.
            for(int i=0;i<possibilities.length;i++){
                int imax=i;
                for(int j=i+1;j<possibilities.length;j++)if(gains[j]>gains[imax])imax=j;
                int t=possibilities[i];possibilities[i]=possibilities[imax];possibilities[imax]=t;
                t=gains[i];gains[i]=gains[imax];gains[imax]=t;
            }
            ArrayList<BitSet> explored=new ArrayList<>();
            for(int c:possibilities){
                BitSet remainder=(BitSet)remaining.clone();
                remainder.andNot(covers[c]);
                boolean duplicate=false;
                for(BitSet x:explored)if(x.equals(remainder)){duplicate=true;break;}
                if(duplicate)continue;
                explored.add(remainder);
                chosen[depth]=c;
                if(dfs(remainder,left-1,chosen,depth+1))return true;
            }
            return false;
        }
    }
}
