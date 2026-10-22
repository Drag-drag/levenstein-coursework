package levenshtein;

/**
 * Реализация с оптимизацией памяти.
 *
 * Для вычисления очередной строки матрицы достаточно знать
 * только предыдущую строку, поэтому хранятся два массива длины m + 1.
 * Более короткая строка всегда используется как "столбцы",
 * поэтому памяти нужно O(min(n, m)).
 *
 * Время:  O(n * m)
 * Память: O(min(n, m))
 */
public final class LevenshteinTwoRows implements DistanceAlgorithm {

    @Override
    public int distance(String a, String b) {
        // Для экономии памяти внешним циклом идём по более длинной строке.
        if (a.length() < b.length()) {
            String tmp = a;
            a = b;
            b = tmp;
        }

        int n = a.length();
        int m = b.length();

        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            char ca = a.charAt(i - 1);

            for (int j = 1; j <= m; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;

                int deletion = prev[j] + 1;
                int insertion = cur[j - 1] + 1;
                int substitution = prev[j - 1] + cost;

                cur[j] = Math.min(Math.min(deletion, insertion), substitution);
            }

            // Меняем строки местами, чтобы не копировать массивы.
            int[] tmp = prev;
            prev = cur;
            cur = tmp;
        }

        return prev[m];
    }

    @Override
    public String name() {
        return "Две строки O(min(n,m))";
    }
}
