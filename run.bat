@echo off
REM =====================================================================
REM College ERP System - Windows Launch Script
REM Strictly executes Java Swing GUI with Native MySQL JDBC Driver
REM =====================================================================

echo [1/3] Checking Java Development Kit...
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo System PATH javac not detected. Checking standard Microsoft JDK path...
    if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe" (
        set "PATH=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin;%PATH%"
        echo Found Microsoft OpenJDK 17.
    ) else (
        echo ERROR: javac was not found. Please ensure JDK 17+ is installed and configured in PATH.
        pause
        exit /b 1
    )
)

echo [2/3] Compiling Project Sources...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin -cp "lib/mysql-connector-j-8.3.0.jar;src" src/com/college/*.java src/com/college/db/*.java src/com/college/model/*.java src/com/college/dao/*.java src/com/college/gui/*.java src/com/college/util/*.java
if %errorlevel% neq 0 (
    echo Compilation failed. Please inspect errors above.
    pause
    exit /b %errorlevel%
)
echo Compilation successful.

echo [3/3] Launching College ERP Desktop Application...
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.college.Main
pause
