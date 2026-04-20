@echo off
title AUCTION WEB - ENVIRONMENT CHECK
echo ============================================================
echo   AUCTION WEB - DIAGNOSTIC TOOL (REPAIRED)
echo ============================================================
echo.

:: 1. Check Project Path
echo [1/5] Checking Project Path...
set "TEMP_DIR=%~dp0"
echo       Path: "%TEMP_DIR%"

:: Check for spaces or special characters using a safer method
echo "%TEMP_DIR%" | findstr /r "[ &%%^()]" >nul
if %ERRORLEVEL% equ 0 (
    echo [WARNING] Your path contains spaces or special characters.
    echo           This often causes Maven Wrapper to fail.
    echo           Try moving the project to C:\BTL\auctionweb
) else (
    echo       [OK] Path is clean.
)
echo.

:: 2. Check Java
echo [2/5] Checking Java...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java not found in your system PATH.
) else (
    echo       [OK] Java is installed.
    java -version 2>&1 | findstr "version"
)
echo.

:: 3. Check Important Files
echo [3/5] Checking System Files...
set "MISSING=0"
if not exist "%~dp0.mvn" (
    echo [ERROR] Folder ".mvn" is MISSING.
    set "MISSING=1"
)
if not exist "%~dp0mvnw.cmd" (
    echo [ERROR] File "mvnw.cmd" is MISSING.
    set "MISSING=1"
)
if "%MISSING%"=="0" (
    echo       [OK] All system files are present.
)
echo.

:: 4. Check Internet
echo [4/5] Checking Internet...
ping -n 1 google.com >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [WARNING] No internet connection. Maven cannot download libraries.
) else (
    echo       [OK] Internet is connected.
)
echo.

echo ============================================================
echo   CHECK COMPLETE. 
echo   If you see any [ERROR] above, please fix it.
echo ============================================================
echo.
pause
