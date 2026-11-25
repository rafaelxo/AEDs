package grafos;

/** Raios de k-centros declarados na Tabela 1 do enunciado (nao sao p-mediana). */
public final class ReferenceTable {
    private ReferenceTable() {}
    public static final int[] N={0,
        100,100,100,100,100,200,200,200,200,200,
        300,300,300,300,300,400,400,400,400,400,
        500,500,500,500,500,600,600,600,600,600,
        700,700,700,700,800,800,800,900,900,900};
    public static final int[] K={0,
        5,10,10,20,33,5,10,20,40,67,
        5,10,30,60,100,5,10,40,80,133,
        5,10,50,100,167,5,10,60,120,200,
        5,10,70,140,5,10,80,5,10,90};
    public static final int[] RADIUS={0,
        127,98,93,74,48,84,64,55,37,20,
        59,51,35,26,18,47,39,28,18,13,
        40,38,22,15,11,38,32,18,13,9,
        30,29,15,11,30,27,15,29,23,13};
}
