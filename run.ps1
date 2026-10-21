# =====================================================================
# College ERP System - PowerShell Launch Script
# =====================================================================

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host ">>> [1/3] Detecting Java JDK..." -ForegroundColor Cyan

$javacCmd = "javac"
if (-not (Get-Command "javac" -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe") {
        $env:PATH = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin;" + $env:PATH
        $javacCmd = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe"
        Write-Host "Loaded Microsoft OpenJDK 17." -ForegroundColor Green
    } else {
        Write-Error "JDK compiler (javac) not found. Please install JDK 17+ or configure PATH."
        exit 1
    }
}

Write-Host ">>> [2/3] Compiling Project Sources..." -ForegroundColor Cyan
if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }

$sources = (Get-ChildItem -Recurse -Path "src\*.java").FullName
& $javacCmd -encoding UTF-8 -d "bin" -cp "lib/mysql-connector-j-8.3.0.jar;src" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation encountered errors."
    exit $LASTEXITCODE
}
Write-Host "Compilation complete (0 errors)." -ForegroundColor Green

Write-Host ">>> [3/3] Starting College ERP Application..." -ForegroundColor Cyan
& java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.college.Main
