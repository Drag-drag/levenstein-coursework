package levenshtein;

/**
 * Проверка того, сколько памяти требует каждая реализация.
 *
 * Программа запускается с ограничением кучи, например:
 *   java -Xmx256m -cp out levenshtein.MemoryProbe matrix 8000
 *
 * Если памяти хватает, печатается "OK", иначе "OutOfMemoryError".
 * Это позволяет найти максимальную длину строк, при которой
 * реализация ещё работает при заданном объёме памяти.
 */
public final class MemoryProbe {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Использование: java -Xmx256m -cp out levenshtein.MemoryProbe <matrix|rows> <n>");
            return;
        }

        String mode = args[0];
        int n = Integer.parseInt(args[1]);

        try {
            if ("matrix".equals(mode)) {
                // Полная матрица (n + 1) x (n + 1) 32-битных чисел.
                int[][] matrix = new int[n + 1][n + 1];
                matrix[n][n] = 1; // обращение, чтобы массив не удалили
                System.out.println("OK: матрица " + (n + 1) + "x" + (n + 1) + " выделена");
                System.out.println("Теоретический объём: "
                        + ((long) (n + 1) * (n + 1) * Integer.BYTES / 1024 / 1024) + " МБ");
            } else if ("rows".equals(mode)) {
                // Два массива длины n + 1.
                int[] prev = new int[n + 1];
                int[] cur = new int[n + 1];
                prev[n] = 1;
                cur[n] = 1;
                System.out.println("OK: два массива длины " + (n + 1) + " выделены");
                System.out.println("Теоретический объём: "
                        + (2L * (n + 1) * Integer.BYTES / 1024) + " КБ");
            } else {
                System.out.println("Неизвестный режим: " + mode);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("OutOfMemoryError при n = " + n + " (режим " + mode + ")");
        }
    }
}
