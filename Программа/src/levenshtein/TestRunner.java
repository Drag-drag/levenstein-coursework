package levenshtein;

import java.util.Random;

/**
 * Проверка корректности всех реализаций.
 *
 * 1. Фиксированные примеры с заранее известным ответом.
 * 2. Случайные пары строк: результат каждой реализации сравнивается
 *    с эталонной матричной версией.
 * 3. Пакетный параллельный режим: набор пар обрабатывается пулом потоков.
 *
 * Запуск: java -cp out levenshtein.TestRunner
 */
public final class TestRunner {

    private static final String ALPHABET = "абвгдежзийклмнопрстуфхцчшщыэюя";

    public static void main(String[] args) {
        int failed = 0;

        failed += checkFixedExamples();
        failed += checkRandomExamples(300, 200);
        failed += checkBatchMode(40, 200, 400);

        System.out.println();
        if (failed == 0) {
            System.out.println("ИТОГ: все тесты пройдены успешно.");
        } else {
            System.out.println("ИТОГ: обнаружено ошибок: " + failed);
            System.exit(1);
        }
    }

    /** Проверка на примерах, ответ для которых известен заранее. */
    private static int checkFixedExamples() {
        System.out.println("Таблица 1. Проверка на фиксированных примерах");
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-20s %-20s %-10s %-10s %-10s %-10s%n",
                "Строка A", "Строка B", "Ожидается", "Матрица", "2 строки", "Параллельно");
        System.out.println("--------------------------------------------------------------");

        String[][] cases = {
                {"кот", "код", "1"},
                {"кот", "скот", "1"},
                {"дом", "дым", "1"},
                {"hello", "yellow", "2"},
                {"", "", "0"},
                {"", "abc", "3"},
                {"abc", "", "3"},
                {"abc", "abc", "0"},
                {"abcdef", "azced", "3"},
                {"расстояние", "рост", "7"}
        };

        int failed = 0;
        LevenshteinParallel parallel = new LevenshteinParallel(4);
        DistanceAlgorithm[] algorithms = {
                new LevenshteinMatrix(),
                new LevenshteinTwoRows(),
                parallel
        };
        String[] names = {"Матрица", "2 строки", "Параллельно"};

        for (String[] c : cases) {
            int expected = Integer.parseInt(c[2]);
            System.out.printf("%-20s %-20s %-10d", quote(c[0]), quote(c[1]), expected);

            for (int k = 0; k < algorithms.length; k++) {
                int actual = algorithms[k].distance(c[0], c[1]);
                System.out.printf(" %-10d", actual);
                if (actual != expected) {
                    System.out.print(" <-- ОШИБКА (" + names[k] + ")");
                    failed++;
                }
            }
            System.out.println();
        }
        parallel.close();
        return failed;
    }

    /** Сравнение всех реализаций с эталоном на случайных строках. */
    private static int checkRandomExamples(int pairs, int maxLength) {
        Random random = new Random(42);
        int failed = 0;

        LevenshteinMatrix reference = new LevenshteinMatrix();
        LevenshteinTwoRows twoRows = new LevenshteinTwoRows();
        LevenshteinParallel parallel = new LevenshteinParallel(4);

        for (int t = 0; t < pairs; t++) {
            String a = randomString(random, random.nextInt(maxLength + 1));
            String b = randomString(random, random.nextInt(maxLength + 1));

            int expected = reference.distance(a, b);
            int actualTwoRows = twoRows.distance(a, b);
            int actualParallel = parallel.distance(a, b);

            if (expected != actualTwoRows || expected != actualParallel) {
                failed++;
                System.out.println("Несовпадение: \"" + a + "\" / \"" + b + "\""
                        + " эталон=" + expected
                        + " две строки=" + actualTwoRows
                        + " параллельно=" + actualParallel);
            }
        }

        System.out.println();
        System.out.println("Таблица 2. Случайные строки: " + pairs + " пар, "
                + "длина 0..." + maxLength + ", алфавит " + ALPHABET.length() + " букв");
        System.out.println("Несовпадений с эталоном: " + failed);

        parallel.close();
        return failed;
    }

    /** Проверка пакетного режима: пул потоков считает набор пар. */
    private static int checkBatchMode(int pairs, int minLength, int maxLength) {
        Random random = new Random(7);
        LevenshteinMatrix reference = new LevenshteinMatrix();

        String[] as = new String[pairs];
        String[] bs = new String[pairs];
        int[] expected = new int[pairs];

        for (int i = 0; i < pairs; i++) {
            int length = minLength + random.nextInt(maxLength - minLength + 1);
            as[i] = randomString(random, length);
            bs[i] = randomString(random, length);
            expected[i] = reference.distance(as[i], bs[i]);
        }

        int failed = 0;
        try (LevenshteinParallel parallel = new LevenshteinParallel(4)) {
            int[] actual = parallel.distances(as, bs);
            for (int i = 0; i < pairs; i++) {
                if (actual[i] != expected[i]) {
                    failed++;
                    System.out.println("Несовпадение в пакетном режиме, пара " + i
                            + ": эталон=" + expected[i] + ", получено=" + actual[i]);
                }
            }
        }

        System.out.println();
        System.out.println("Таблица 3. Пакетный параллельный режим: " + pairs
                + " пар, длина " + minLength + "..." + maxLength);
        System.out.println("Несовпадений с эталоном: " + failed);

        return failed;
    }

    private static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static String quote(String s) {
        return "\"" + s + "\"";
    }
}
