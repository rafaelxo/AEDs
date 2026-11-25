package grafos;

import java.util.*;

/** Gonzalez: escolha iterativa do ponto mais distante; opcional refinamento 1-swap. */
public final class ApproxKCenter {
    private ApproxKCenter() {}
    public static Solution solve(int[][] d, int k, boolean swaps) {
        if (k<1 || k>d.length) throw new IllegalArgumentException("k fora dos limites");
        int n=d.length;
        int[] seeds={0,n/3,(2*n)/3,n-1};
        Solution best=null;
        for(int seed:seeds){
            Solution candidate=gonzalez(d,k,seed);
            if(best==null || candidate.radius<best.radius)best=candidate;
        }
        if(swaps) best=improve(d,best,2);
        return best;
    }
    private static Solution gonzalez(int[][] d,int k,int initial) {
        int n=d.length;
        int[] centers=new int[k], nearest=new int[n];
        boolean[] chosen=new boolean[n];
        Arrays.fill(nearest,Integer.MAX_VALUE);
        for(int step=0;step<k;step++){
            int farthest=step==0 ? initial : -1, max=-1;
            if(step>0) for(int v=0;v<n;v++) {
                if(!chosen[v] && nearest[v]>max){max=nearest[v];farthest=v;}
            }
            centers[step]=farthest;
            chosen[farthest]=true;
            for(int v=0;v<n;v++)nearest[v]=Math.min(nearest[v],d[v][farthest]);
        }
        return new Solution(centers,d);
    }
    private static Solution improve(int[][] d,Solution initial,int passes) {
        int n=d.length, k=initial.centers.length;
        int[] centers=initial.centers.clone();
        boolean[] chosen=new boolean[n];
        for(int c:centers)chosen[c]=true;
        int current=initial.radius;
        for(int pass=0;pass<passes;pass++) {
            int[] first=new int[n], second=new int[n], assigned=new int[n];
            Arrays.fill(first,Integer.MAX_VALUE);
            Arrays.fill(second,Integer.MAX_VALUE);
            Arrays.fill(assigned,-1);
            for(int v=0;v<n;v++){
                for(int i=0;i<k;i++) {
                    int dist=d[v][centers[i]];
                    if(dist<first[v]) {second[v]=first[v];first[v]=dist;assigned[v]=i;}
                    else if(dist<second[v])second[v]=dist;
                }
            }
            int bestRadius=current, remove=-1, add=-1;
            for(int i=0;i<k;i++) for(int c=0;c<n;c++) {
                if(chosen[c])continue;
                int candidateRadius=0;
                for(int v=0;v<n;v++){
                    int without=assigned[v]==i ? second[v] : first[v];
                    int dist=Math.min(without,d[v][c]);
                    if(dist>bestRadius){candidateRadius=dist;break;}
                    if(dist>candidateRadius)candidateRadius=dist;
                }
                if(candidateRadius<bestRadius){bestRadius=candidateRadius;remove=i;add=c;}
            }
            if(remove<0)break;
            chosen[centers[remove]]=false;
            centers[remove]=add;
            chosen[add]=true;
            current=bestRadius;
        }
        return new Solution(centers,d);
    }
}
