@echo off
setlocal enabledelayedexpansion
title College ERP - Database Auto-Setup
color 0b

echo ====================================================================
echo        COLLEGE ERP SYSTEM - AUTOMATED DATABASE SETUP
echo ====================================================================
echo.

REM 1. Detect MySQL executable in common locations (XAMPP default: C:\xampp\mysql\bin\mysql.exe)
set "MYSQL_EXE="
if exist "C:\xampp\mysql\bin\mysql.exe" (
    set "MYSQL_EXE=C:\xampp\mysql\bin\mysql.exe"
) else (
    where mysql >nul 2>&1
    if !errorlevel! equ 0 (
        set "MYSQL_EXE=mysql"
    )
)

if "%MYSQL_EXE%"=="" (
    color 0c
    echo [ERROR] MySQL was not found at C:\xampp\mysql\bin\mysql.exe or in system PATH.
    echo.
    echo Please make sure XAMPP is installed in C:\xampp.
    echo If installed in another drive, please edit this script or add mysql to PATH.
    echo.
    pause
    exit /b 1
)

echo [1/4] Found MySQL at: %MYSQL_EXE%
echo.

REM 2. Check if MySQL server is running on localhost port 3306
echo [2/4] Testing connection to MySQL Server on localhost:3306...
"%MYSQL_EXE%" -u root -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    color 0e
    echo.
    echo [ATTENTION] Could not connect to MySQL!
    echo.
    echo Looks like MySQL is NOT currently running.
    echo   1. Open the "XAMPP Control Panel"
    echo   2. Click the "Start" button next to "MySQL" (and "Apache" if desired)
    echo   3. Once MySQL shows a green PID/Port, press any key here to retry...
    echo.
    pause
    "%MYSQL_EXE%" -u root -e "SELECT 1;" >nul 2>&1
    if !errorlevel! neq 0 (
        color 0c
        echo.
        echo [ERROR] Still unable to connect to MySQL. Please ensure MySQL is started in XAMPP.
        pause
        exit /b 1
    )
)

color 0a
echo       [SUCCESS] MySQL server is active and reachable!
echo.

REM 3. Import schema.sql into MySQL
echo [3/4] Importing database schema and sample records from sql\schema.sql...
if not exist "sql\schema.sql" (
    color 0c
    echo [ERROR] sql\schema.sql not found in project directory.
    pause
    exit /b 1
)

"%MYSQL_EXE%" -u root < "sql\schema.sql"
if %errorlevel% neq 0 (
    color 0c
    echo.
    echo [ERROR] Failed to import sql\schema.sql. Check if root has a password.
    pause
    exit /b 1
)

echo       [SUCCESS] Database 'college_erp' and all tables imported successfully!
echo.

REM 4. Verify created tables
echo [4/4] Verifying database tables in 'college_erp':
echo ------------------------------------------------
"%MYSQL_EXE%" -u root -e "USE college_erp; SHOW TABLES;"
echo ------------------------------------------------
echo.
echo ====================================================================
echo   DATABASE SETUP COMPLETE! YOUR PROJECT IS READY FOR LIVE WORK.
echo ====================================================================
echo.
echo Initial Login Credentials:
echo   - Administrator : admin    / admin123
echo   - Faculty       : faculty1 / faculty123
echo   - Student       : student1 / student123
echo.
echo You can now run "run.bat" to start the College ERP System in LIVE mode!
echo.
pause
