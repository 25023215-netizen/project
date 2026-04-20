@echo off
title AUCTION WEB - SERVER
echo ============================================================
echo   STARTING AUCTION SERVER (Spring Boot)
echo ============================================================
echo.

cd /d "%~dp0"

:: [1/3] Check Java
echo [1/3] Checking Java version...
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java not found! Please install JDK 17 or newer.
    pause
    exit /b 1
)

:: [2/3] Check Requirements
if not exist ".mvn" (
    echo [ERROR] Missing ".mvn" folder! Please copy all hidden files.
    pause
    exit /b 1
)

:: [3/3] Running
echo [3/3] Launching Spring Boot...
echo.

call mvnw.cmd spring-boot:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo ============================================================
    echo   [ERROR] Server failed to start (Code %ERRORLEVEL%).
    echo.
    echo   Try these steps:
    echo   1. Run 'check-env.bat' to verify your Java version (Must be 17+).
    echo   2. Delete the 'target' folder and try again.
    echo   3. Make sure your folder path has NO spaces or special chars.
    echo ============================================================
    pause
)

pause
