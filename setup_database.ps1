# =====================================================================
# College ERP System - Automated Database Setup (PowerShell)
# =====================================================================

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "       COLLEGE ERP SYSTEM - AUTOMATED DATABASE SETUP" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Detect MySQL executable
$mysqlExe = $null
if (Test-Path "C:\xampp\mysql\bin\mysql.exe") {
    $mysqlExe = "C:\xampp\mysql\bin\mysql.exe"
} elseif (Get-Command mysql -ErrorAction SilentlyContinue) {
    $mysqlExe = "mysql"
}

if (-not $mysqlExe) {
    Write-Host "[ERROR] MySQL was not found at C:\xampp\mysql\bin\mysql.exe or in system PATH." -ForegroundColor Red
    Write-Host "Please ensure XAMPP is installed in C:\xampp." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit..."
    exit 1
}

Write-Host "[1/4] Found MySQL at: $mysqlExe" -ForegroundColor Green
Write-Host ""

# 2. Check if MySQL server is running
Write-Host "[2/4] Testing connection to MySQL Server on localhost:3306..." -ForegroundColor Yellow
$testOutput = & $mysqlExe -u root -e "SELECT 1;" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ATTENTION] MySQL is NOT currently running!" -ForegroundColor Yellow
    Write-Host "  1. Open the 'XAMPP Control Panel'" -ForegroundColor Cyan
    Write-Host "  2. Click the 'Start' button next to 'MySQL'" -ForegroundColor Cyan
    Write-Host "  3. Once MySQL shows a green PID and port (3306), press Enter here..." -ForegroundColor Cyan
    Write-Host ""
    Read-Host "Press Enter after starting MySQL..."
    
    $testOutput = & $mysqlExe -u root -e "SELECT 1;" 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Still unable to connect to MySQL. Please verify MySQL is started in XAMPP." -ForegroundColor Red
        Read-Host "Press Enter to exit..."
        exit 1
    }
}

Write-Host "      [SUCCESS] MySQL server is active and reachable!" -ForegroundColor Green
Write-Host ""

# 3. Import schema.sql
Write-Host "[3/4] Importing database schema and sample records from sql\schema.sql..." -ForegroundColor Yellow
$schemaPath = Join-Path $PSScriptRoot "sql\schema.sql"
if (-not (Test-Path $schemaPath)) {
    Write-Host "[ERROR] sql\schema.sql not found at $schemaPath" -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

Get-Content $schemaPath -Raw | & $mysqlExe -u root
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Failed to import schema.sql into MySQL." -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

Write-Host "      [SUCCESS] Database 'college_erp' and all tables imported successfully!" -ForegroundColor Green
Write-Host ""

# 4. Verify created tables
Write-Host "[4/4] Verifying database tables in 'college_erp':" -ForegroundColor Cyan
Write-Host "------------------------------------------------" -ForegroundColor DarkGray
& $mysqlExe -u root -e "USE college_erp; SHOW TABLES;"
Write-Host "------------------------------------------------" -ForegroundColor DarkGray
Write-Host ""
Write-Host "====================================================================" -ForegroundColor Green
Write-Host "  DATABASE SETUP COMPLETE! YOUR PROJECT IS READY FOR LIVE WORK." -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Initial Login Credentials:" -ForegroundColor Yellow
Write-Host "  - Administrator : admin    / admin123"
Write-Host "  - Faculty       : faculty1 / faculty123"
Write-Host "  - Student       : student1 / student123"
Write-Host ""
Write-Host "You can now run .\run.ps1 or run.bat to start the College ERP System!" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to exit..."
