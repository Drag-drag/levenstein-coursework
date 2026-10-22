package levenshtein;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

/**
 * Замеры времени работы реализаций.
 *
 * Таблица 1 - время вычисления расстояния для одной пары строк.
 * Таблица 2 - время обработки набора из 32 пар строк в 1, 2, 4 и 8 потоков
 *             и ускорение относительно одного потока.
 * Таблица 3 - теоретический объём памяти.
 *
 * Результаты печатаются в консоль и сохраняются в results.md / results.csv.
 *
 * Запуск: java -Xmx1g -cp out levenshtein.Benchmark
 */
public final class Benchmark {

    private static final int[] SIZES = {100, 250, 500, 1000, 2000, 4000};
    private static final int[] BATCH_SIZES = {500, 1000, 2000};
    private static final int[] THREADS = {1, 2, 4, 8};
    private static final int PAIRS = 48;

    private static final int WARMUP = 2;
    private static final int RUNS = 7;
    private static final int BATCH_WARMUP = 1;
    private static final int BATCH_RUNS = 5;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /** Защита от удаления "ненужных" вычислений оптимизатором JIT. */
    private static volatile long sink;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Random random = new Random(42);

        warmUp(random);

        StringBuilder md = new StringBuilder();
        StringBuilder csv = new StringBuilder();

        md.append("# Результаты замеров\n\n");
        md.append("Время в миллисекундах: минимум из ").append(RUNS)
          .append(" запусков после ").append(WARMUP).append(" прогревочных.\n\n");

        md.append("## Таблица 1. Одна пара строк\n\n");
        md.append("| Длина | Полная матрица | Две строки |\n");
        md.append("|---:|---:|---:|\n");

        csv.append("n,matrix,twoRows\n");

        System.out.println("Таблица 1. Одна пара строк, время (мс)");
        System.out.printf("%6s %18s %14s%n", "n", "Полная матрица", "Две строки");

        for (int n : SIZES) {
            String a = randomString(random, n);
            String b = randomString(random, n);

            double matrix = measure(new LevenshteinMatrix(), a, b);
            double twoRows = measure(new LevenshteinTwoRows(), a, b);

            System.out.printf("%6d %18.3f %14.3f%n", n, matrix, twoRows);
            md.append(String.format(Locale.US, "| %d | %.3f | %.3f |%n", n, matrix, twoRows));
            csv.append(String.format(Locale.US, "%d,%.3f,%.3f%n", n, matrix, twoRows));
        }

        md.append("\n## Таблица 2. Набор из ").append(PAIRS).append(" пар строк\n\n");
        md.append("| Длина | 1 поток | 2 потока | 4 потока | 8 потоков | Ускорение (8 потоков) |\n");
        md.append("|---:|---:|---:|---:|---:|---:|\n");

        csv.append("\nbatch_n,p1,p2,p4,p8\n");

        System.out.println();
        System.out.println("Таблица 2. Набор из " + PAIRS + " пар строк, время (мс)");
        System.out.printf("%6s %10s %10s %10s %10s %20s%n",
                "n", "1 поток", "2 потока", "4 потока", "8 потоков", "Ускорение (8)");

        for (int n : BATCH_SIZES) {
            String[] as = new String[PAIRS];
            String[] bs = new String[PAIRS];
            for (int i = 0; i < PAIRS; i++) {
                as[i] = randomString(random, n);
                bs[i] = randomString(random, n);
            }

            double[] time = new double[THREADS.length];
            for (int t = 0; t < THREADS.length; t++) {
                try (LevenshteinParallel parallel = new LevenshteinParallel(THREADS[t])) {
                    time[t] = measureBatch(parallel, as, bs);
                }
            }

            double speedup = time[0] / time[time.length - 1];

            System.out.printf("%6d %10.3f %10.3f %10.3f %10.3f %20.2f%n",
                    n, time[0], time[1], time[2], time[3], speedup);

            md.append(String.format(Locale.US,
                    "| %d | %.3f | %.3f | %.3f | %.3f | %.2f |%n",
                    n, time[0], time[1], time[2], time[3], speedup));

            csv.append(String.format(Locale.US, "%d,%.3f,%.3f,%.3f,%.3f%n",
                    n, time[0], time[1], time[2], time[3]));
        }

        md.append("\n## Таблица 3. Теоретический объём памяти\n\n");
        md.append("| Длина | Полная матрица, байт | Две строки, байт |\n");
        md.append("|---:|---:|---:|\n");

        System.out.println();
        System.out.println("Таблица 3. Теоретический объём памяти (байт)");
        System.out.printf("%6s %20s %20s%n", "n", "Полная матрица", "Две строки");

        for (int n : SIZES) {
            long matrixBytes = (long) (n + 1) * (n + 1) * Integer.BYTES;
            long rowsBytes = 2L * (n + 1) * Integer.BYTES;

            System.out.printf("%6d %20d %20d%n", n, matrixBytes, rowsBytes);
            md.append(String.format("| %d | %d | %d |%n", n, matrixBytes, rowsBytes));
        }

        try (PrintWriter out = new PrintWriter("results.md", "UTF-8")) {
            out.print(md);
        }
        try (PrintWriter out = new PrintWriter("results.csv", "UTF-8")) {
            out.print(csv);
        }

        System.out.println();
        System.out.println("Результаты сохранены в results.md и results.csv");
    }

    /** Прогрев JIT-компилятора перед основными замерами. */
    private static void warmUp(Random random) {
        String a = randomString(random, 300);
        String b = randomString(random, 300);

        DistanceAlgorithm matrix = new LevenshteinMatrix();
        DistanceAlgorithm twoRows = new LevenshteinTwoRows();

        for (int i = 0; i < 30; i++) {
            sink += matrix.distance(a, b);
            sink += twoRows.distance(a, b);
        }

        String[] as = {a, b, a, b};
        String[] bs = {b, a, b, a};
        try (LevenshteinParallel parallel = new LevenshteinParallel(4)) {
            for (int i = 0; i < 10; i++) {
                sink += parallel.distances(as, bs)[0];
            }
        }
    }

    /** Минимальное время работы алгоритма для одной пары, мс. */
    private static double measure(DistanceAlgorithm algorithm, String a, String b) {
        for (int i = 0; i < WARMUP; i++) {
            sink += algorithm.distance(a, b);
        }

        double best = Double.MAX_VALUE;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            int result = algorithm.distance(a, b);
            long elapsed = System.nanoTime() - start;

            sink += result;
            best = Math.min(best, elapsed / 1_000_000.0);
        }
        return best;
    }

    /** Минимальное время обработки набора пар, мс. */
    private static double measureBatch(LevenshteinParallel algorithm,
                                       String[] as, String[] bs) {
        for (int i = 0; i < BATCH_WARMUP; i++) {
            sink += algorithm.distances(as, bs)[0];
        }

        double best = Double.MAX_VALUE;
        for (int i = 0; i < BATCH_RUNS; i++) {
            long start = System.nanoTime();
            int[] result = algorithm.distances(as, bs);
            long elapsed = System.nanoTime() - start;

            sink += result[0] + result[result.length - 1];
            best = Math.min(best, elapsed / 1_000_000.0);
        }
        return best;
    }

    private static String randomString(Random random, int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        }
        return new String(chars);
    }
}
