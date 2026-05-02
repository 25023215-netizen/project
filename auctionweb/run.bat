@echo off
title AuctionWeb Launcher
echo ========================================
echo   Starting AuctionWeb Application...
echo ========================================
echo.

REM Start backend server in a new command window
echo [1/2] Starting Backend Server...
start "AuctionWeb Backend" cmd /c "mvnw.cmd spring-boot:run"

REM Wait for backend to initialize (approx 10 seconds)
echo Waiting 10 seconds for backend to initialize...
timeout /t 10 /nobreak >nul

REM Start frontend client
echo [2/2] Starting JavaFX Client...
mvnw.cmd javafx:run

echo.
echo ========================================
echo   Application Closed.
echo   Note: Backend server is still running in the other window.
echo   You can close it manually if you are done.
echo ========================================
pause
