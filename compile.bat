@echo off
chcp 65001 > nul
echo ========================================================
echo   Compiling Game Grid (Java OOPS Course Project)
echo ========================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin -sourcepath src src\com\gamegrid\model\*.java src\com\gamegrid\exception\*.java src\com\gamegrid\payment\*.java src\com\gamegrid\repository\*.java src\com\gamegrid\service\*.java src\com\gamegrid\ui\*.java src\com\gamegrid\Main.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation successful! Class files generated in 'bin/'.
) else (
    echo [ERROR] Compilation failed. Please inspect errors above.
)
pause
