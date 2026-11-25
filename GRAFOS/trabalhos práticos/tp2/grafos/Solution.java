package grafos;

import java.util.*;

public final class Solution {
    public final int[] centers;
    public final int radius;
    public Solution(int[] centers, int[][] distances) {
        this.centers=centers.clone();
        if(centers.length==0) throw new IllegalArgumentException("Sem centros");
        BitSet seen = new BitSet(distances.length);
        for(int center: centers) {
            if(center<0||center>=distances.length||seen.get(center))
                throw new IllegalArgumentException("Centro duplicado ou invalido");
            seen.set(center);
        }
        int r=0;
        for(int[] row:distances) {
            int best=Integer.MAX_VALUE;
            for(int center:centers) best=Math.min(best,row[center]);
            r=Math.max(r,best);
        }
        radius=r;
    }
    /** Centros numerados a partir de 1, como na OR-Library. */
    public String centerList() {
        int[] c=centers.clone();
        Arrays.sort(c);
        for(int i=0;i<c.length;i++)c[i]++;
        return Arrays.toString(c);
    }
}
