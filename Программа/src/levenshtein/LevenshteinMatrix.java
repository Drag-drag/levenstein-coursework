package levenshtein;

/**
 * Классическая реализация алгоритма Вагнера — Фишера.
 * Строит полную матрицу расстояний размером (n + 1) x (m + 1).
 *
 * Время:  O(n * m)
 * Память: O(n * m)
 *
 * Используется как эталон корректности для остальных реализаций.
 */
public final class LevenshteinMatrix implements DistanceAlgorithm {

    @Override
    public int distance(String a, String b) {
        int n = a.length();
        int m = b.length();

        int[][] d = new int[n + 1][m + 1];

        // Граничные условия: расстояние от пустой строки.
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Основной цикл динамического программирования.
        for (int i = 1; i <= n; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;

                int deletion = d[i - 1][j] + 1;
                int insertion = d[i][j - 1] + 1;
                int substitution = d[i - 1][j - 1] + cost;

                d[i][j] = Math.min(Math.min(deletion, insertion), substitution);
            }
        }

        return d[n][m];
    }

    @Override
    public String name() {
        return "Полная матрица";
    }
}
