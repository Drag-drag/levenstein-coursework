@echo off
chcp 65001 >nul
cd /d "%~dp0"
java -Xmx1g -cp out levenshtein.Benchmark
pause
