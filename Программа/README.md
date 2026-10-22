# Расстояние Левенштейна: оптимизация памяти и параллельные вычисления

Курсовая работа. Реализация на Java трёх версий алгоритма вычисления
расстояния редактирования (Левенштейна):

| Класс | Идея | Время | Память |
|---|---|---|---|
| `LevenshteinMatrix` | классическая полная матрица | O(n·m) | O(n·m) |
| `LevenshteinTwoRows` | хранение двух строк | O(n·m) | O(min(n, m)) |
| `LevenshteinParallel` | пул потоков, обработка набора пар | O(n·m / p) | O(p·min(n, m)) |

## Требования

JDK 17 или новее (проверено на JDK 21). Проверка: `javac -version`.

## Сборка

```
javac -encoding UTF-8 -d out src\levenshtein\*.java
```

или запустить `build.bat`.

## Запуск

```
java -cp out levenshtein.Main
java -cp out levenshtein.Main "кот" "код"
java -cp out levenshtein.TestRunner
java -Xmx1g -cp out levenshtein.Benchmark
```

Соответствующие bat-файлы: `run.bat`, `test.bat`, `bench.bat`.

## Проверка памяти

```
java -Xmx256m -cp out levenshtein.MemoryProbe matrix 8000
java -Xmx256m -cp out levenshtein.MemoryProbe rows 1000000
```

## Результаты

Файлы `results.md` и `results.csv` содержат результаты последнего запуска
замеров: время для одной пары строк, время и ускорение для набора из 48 пар,
теоретический объём памяти.

Кратко (AMD Ryzen 5 PRO 3500U, 4 ядра / 8 потоков, JDK 21):

- память при n = 4000: 61 МБ (матрица) против 32 КБ (две строки);
- ускорение пакетной обработки на 8 потоках: 6,29 (n = 500), 5,20 (n = 1000),
  3,83 (n = 2000);
- при `-Xmx256m` матричная версия работает до n = 8000, версия с двумя
  строками — при n = 1 000 000.

## Структура

```
src/levenshtein/
  DistanceAlgorithm.java   интерфейс
  LevenshteinMatrix.java   классический алгоритм
  LevenshteinTwoRows.java  оптимизация памяти
  LevenshteinParallel.java параллельная обработка пар
  Main.java                демонстрация
  TestRunner.java          тесты корректности
  Benchmark.java           замеры времени
  MemoryProbe.java         проверка объёма памяти
```
