package levenshtein;

/**
 * Мини-программа для демонстрации работы алгоритма
 * и проверки на нескольких характерных примерах.
 *
 * Запуск без аргументов:  java -cp out levenshtein.Main
 * Запуск с аргументами:   java -cp out levenshtein.Main "кот" "код"
 */
public final class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            DistanceAlgorithm algorithm = new LevenshteinTwoRows();
            int result = algorithm.distance(args[0], args[1]);
            System.out.println("Расстояние Левенштейна: " + result);
            return;
        }

        System.out.println("Демонстрация работы алгоритма Левенштейна");
        System.out.println("----------------------------------------");

        DistanceAlgorithm matrix = new LevenshteinMatrix();
        DistanceAlgorithm twoRows = new LevenshteinTwoRows();

        String[][] examples = {
                {"кот", "код"},
                {"кот", "скот"},
                {"дом", "дым"},
                {"hello", "yellow"},
                {"", "abc"},
                {"abc", "abc"}
        };

        for (String[] pair : examples) {
            int a = matrix.distance(pair[0], pair[1]);
            int b = twoRows.distance(pair[0], pair[1]);
            System.out.printf("%-10s -> %-10s : %d (матрица) / %d (две строки)%n",
                    "\"" + pair[0] + "\"", "\"" + pair[1] + "\"", a, b);
        }

        System.out.println();
        System.out.println("Для проверки всех реализаций запустите:");
        System.out.println("  java -cp out levenshtein.TestRunner");
        System.out.println("Для замеров времени запустите:");
        System.out.println("  java -Xmx1g -cp out levenshtein.Benchmark");
    }
}
