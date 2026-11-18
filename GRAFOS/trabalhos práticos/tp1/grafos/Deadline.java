package grafos;

/**
 * Limite de tempo cooperativo: os algoritmos chamam {@link #check(long)} periodicamente e, se o prazo
 * estourou, uma {@link TimeLimitExceeded} é lançada (usada nos experimentos para interromper execuções
 * inviáveis, p. ex. o método naïve em grafos grandes).
 */
public final class Deadline {
    /** Sem limite. */
    public static final Deadline NONE = new Deadline(Long.MAX_VALUE);

    private final long limit;
    private int tick;

    private Deadline(long limitNanos) { this.limit = limitNanos; }

    public static Deadline afterSeconds(double seconds) {
        return new Deadline(System.nanoTime() + (long) (seconds * 1e9));
    }

    /** Verifica o prazo a cada 16 chamadas (para o custo ser desprezível). */
    public void check(long progress) {
        if (limit != Long.MAX_VALUE && ((++tick & 15) == 0) && System.nanoTime() > limit)
            throw new TimeLimitExceeded(progress);
    }

    /** Lançada quando o limite de tempo é excedido; {@code progress} = passos já concluídos. */
    public static final class TimeLimitExceeded extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public final long progress;
        public TimeLimitExceeded(long progress) {
            super("limite de tempo excedido (progresso: " + progress + ")", null, false, false);
            this.progress = progress;
        }
    }
}
