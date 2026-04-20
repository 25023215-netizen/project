@echo off
title AUCTION WEB - CLIENT
echo ============================================================
echo   STARTING AUCTION CLIENT (JavaFX App)
echo ============================================================
echo.

cd /d "%~dp0"

:: [1/2] Check Server Port (Optional)
echo [1/2] Checking if Server is running on port 8080...
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [WARNING] Server might not be running! 
    echo           Please run 'run-server.bat' first.
    echo.
)

:: [2/2] Run Client
echo [2/2] Launching JavaFX Client...
echo.

:: Run JavaFX using Maven Wrapper
call mvnw.cmd javafx:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Client stopped with error code %ERRORLEVEL%
    echo       Make sure you have JDK 17+ and the server is UP.
    pause
)

pause
