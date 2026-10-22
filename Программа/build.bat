@echo off
chcp 65001 >nul
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: javac not found.
    echo Install JDK 17 or newer and add its bin folder to PATH.
    pause
    exit /b 1
)

if not exist out mkdir out

javac -encoding UTF-8 -d out src\levenshtein\*.java
if errorlevel 1 (
    echo.
    echo Build failed.
    pause
    exit /b 1
)

echo Build OK. Classes are in the out folder.
pause
