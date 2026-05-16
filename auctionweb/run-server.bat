@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

echo ============================================================
echo   AUCTION WEB - SERVER STARTUP SCRIPT
echo   Nhom 4 ^| Lap trinh nang cao
echo ============================================================
echo.

:: ============================================================
:: BUOC 1: Kiem tra Java
:: ============================================================
echo [1/2] Dang kiem tra Java...

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [LOI] Khong tim thay Java tren may!
    echo       Vui long tai va cai dat Java JDK 17 tai:
    echo       https://adoptium.net/temurin/releases/?version=17
    echo       Sau do them Java vao bien moi truong PATH.
    pause
    exit /b 1
)

for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JAVA_VER=%%~v"
)
echo       Java version: %JAVA_VER%
echo       [OK] Java da duoc cai dat.
echo.

:: ============================================================
:: BUOC 2: Kiem tra port 8080
:: ============================================================
echo [2/2] Dang kiem tra port 8080...

netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [CANH BAO] Port 8080 dang bi chiem boi ung dung khac!
    echo.
    for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
        echo       PID: %%p
    )
    echo.
    set /p "KILL_PORT=  Ban co muon tat process do de chay server? (y/N): "
    if /i "!KILL_PORT!"=="y" (
        for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
            taskkill /F /PID %%p >nul 2>&1
        )
        echo       [OK] Da giai phong port 8080.
    )
) else (
    echo       [OK] Port 8080 san sang.
)

echo.
echo ============================================================
echo   Dang khoi dong Spring Boot Server...
echo   Database: H2 Embedded (tu dong, khong can cai them)
echo   Du lieu luu tai: ./data/auction_db
echo   Xem du lieu: http://localhost:8080/h2-console
echo   API Server:  http://localhost:8080
echo   Nhan Ctrl+C de dung server.
echo ============================================================
echo.

cd /d "%~dp0"
cmd /k mvnw.cmd spring-boot:run
