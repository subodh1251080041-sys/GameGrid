@echo off
chcp 65001 > nul
echo ========================================================
echo   Launching Game Grid - Game Purchase Store
echo ========================================================

if not exist bin\com\gamegrid\Main.class (
    echo [INFO] Binaries not found. Compiling first...
    call compile.bat
)

java -Dfile.encoding=UTF-8 -cp bin com.gamegrid.Main
pause
