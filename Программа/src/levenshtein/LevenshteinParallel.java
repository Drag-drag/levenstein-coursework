package levenshtein;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Параллельная версия.
 *
 * В реальных задачах расстояние Левенштейна обычно нужно вычислить
 * не для одной пары строк, а для большого набора пар (поиск дубликатов,
 * проверка орфографии, сравнение результатов распознавания и т.п.).
 * Пары строк независимы друг от друга, поэтому их обработку можно
 * распределить между потоками: каждая задача считает расстояние
 * обычным последовательным алгоритмом с оптимизацией памяти.
 *
 * Внутри одной пары строк распараллеливание затруднено: клетки матрицы
 * зависят друг от друга по диагоналям, и на каждую диагональ пришлось бы
 * ставить барьер (см. раздел 1.4 пояснительной записки).
 *
 * Память: O(p * min(n, m)), где p - число потоков.
 */
public final class LevenshteinParallel implements DistanceAlgorithm, AutoCloseable {

    private final int threads;
    private final ExecutorService pool;
    private final LevenshteinTwoRows sequential = new LevenshteinTwoRows();

    public LevenshteinParallel(int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("Число потоков должно быть >= 1");
        }
        this.threads = threads;
        this.pool = Executors.newFixedThreadPool(threads);
    }

    /** Расстояние для одной пары строк (выполняется последовательно). */
    @Override
    public int distance(String a, String b) {
        return sequential.distance(a, b);
    }

    /**
     * Вычисляет расстояния для набора пар строк параллельно.
     *
     * @param a массив первых строк
     * @param b массив вторых строк
     * @return массив расстояний, result[i] = distance(a[i], b[i])
     */
    public int[] distances(final String[] a, final String[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Массивы строк должны быть одинаковой длины");
        }

        List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>(a.length);
        for (int i = 0; i < a.length; i++) {
            final int index = i;
            tasks.add(new Callable<Integer>() {
                @Override
                public Integer call() {
                    return sequential.distance(a[index], b[index]);
                }
            });
        }

        try {
            List<Future<Integer>> futures = pool.invokeAll(tasks);
            int[] result = new int[a.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = futures.get(i).get();
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Вычисление прервано", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Ошибка при вычислении", e.getCause());
        }
    }

    public int getThreads() {
        return threads;
    }

    @Override
    public String name() {
        return "Параллельная (" + threads + " потоков)";
    }

    @Override
    public void close() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
