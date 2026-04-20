@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

echo ============================================================
echo   AUCTION WEB - CLIENT STARTUP SCRIPT
echo   Nhom 4 ^| Lap trinh nang cao
echo ============================================================
echo.

:: ============================================================
:: BUOC 1: Kiem tra Java
:: ============================================================
echo [1/3] Dang kiem tra Java...

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
:: BUOC 2: Kiem tra Maven Wrapper
:: ============================================================
echo [2/3] Dang kiem tra Maven Wrapper...

cd /d "%~dp0"

if not exist "mvnw.cmd" (
    echo [LOI] Khong tim thay file mvnw.cmd!
    echo       File nay can thiet de build va chay project.
    echo       Hay dam bao ban da tai day du source code.
    pause
    exit /b 1
)

if not exist ".mvn\wrapper\maven-wrapper.properties" (
    echo [LOI] Khong tim thay .mvn\wrapper\maven-wrapper.properties!
    echo       Cau truc project bi thieu. Hay tai lai source code.
    pause
    exit /b 1
)

echo       [OK] Maven Wrapper da san sang.
echo.

:: ============================================================
:: BUOC 3: Kiem tra Server dang chay
:: ============================================================
echo [3/3] Dang kiem tra Server...

:: Kiem tra xem server co dang chay tren port 8080 khong
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if %ERRORLEVEL% neq 0 (
echo [CANH BAO] Server Spring Boot chua chay tren port 8080!
    echo       Vui long chay file run-server.bat truoc khi chay Client.
    echo.
    set /p "CONTINUE=  Ban co muon tiep tuc chay Client khong? (y/N): "
    if /i not "!CONTINUE!"=="y" (
        echo       Da huy. Hay chay run-server.bat truoc.
        pause
        exit /b 0
    )
)

echo.
echo ============================================================
echo   Dang khoi dong Client (JavaFX App)...
echo   Luu y: Server phai chay truoc o http://localhost:8080
echo   Nhan Ctrl+C de dung Client.
echo ============================================================
echo.

:: Chay JavaFX Client bang Maven Wrapper
cmd /k mvnw.cmd -Djavafx.mainClass=frontend.Signup.Signup javafx:run
