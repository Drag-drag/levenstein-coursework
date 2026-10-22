@echo off
chcp 65001 >nul
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo ОШИБКА: компилятор javac не найден.
    echo Установите JDK 17 или новее и добавьте его папку bin в PATH.
    pause
    exit /b 1
)

if not exist out mkdir out

javac -encoding UTF-8 -d out src\levenshtein\*.java
if errorlevel 1 (
    echo.
    echo Сборка завершена с ошибками.
    pause
    exit /b 1
)

echo Сборка завершена успешно. Классы находятся в папке out.
pause
