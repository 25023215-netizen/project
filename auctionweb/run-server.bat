@echo off
title AUCTION WEB - SERVER
echo ============================================================
echo   STARTING AUCTION SERVER (Spring Boot)
echo ============================================================
echo.

cd /d "%~dp0"

:: [1/2] Check Java
echo [1/2] Checking Java version...
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java not found! Please install JDK 17 or newer.
    echo       Add Java to your PATH environment variable.
    pause
    exit /b 1
)

:: [2/2] Check Maven Wrapper
if not exist "mvnw.cmd" (
    echo [ERROR] File mvnw.cmd not found! 
    echo       Please make sure you copied all files correctly.
    pause
    exit /b 1
)

echo.
echo ============================================================
echo   Running Server... 
echo   (First time may take a few minutes to download libraries)
echo ============================================================
echo.

:: Run Spring Boot using Maven Wrapper
call mvnw.cmd spring-boot:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Server stopped with error code %ERRORLEVEL%
    echo       Check the logs above for details.
    pause
)

pause
