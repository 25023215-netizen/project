@echo off
title AuctionWeb
setlocal enabledelayedexpansion

echo ========================================
echo   AuctionWeb - He thong Dau gia Online
echo ========================================
echo.

if not exist config.properties (
    echo [INFO] Tao file cau hinh mac dinh...
    echo server.host=localhost > config.properties
    echo server.port=8080 >> config.properties
)

echo Lua chon che do chay:
echo [1] CHAY TOAN BO (Server + Client) - May chu
echo [2] CHI CHAY GIAO DIEN (Client) - May khach
echo [3] CAU HINH IP SERVER
echo.

set /p choice="Nhap lua chon (1-3): "

if "%choice%"=="2" goto client_only
if "%choice%"=="3" goto config_ip
goto full_start

:config_ip
echo.
set /p new_ip="Nhap IP hoac Link Ngrok (VD: 192.168.1.5 hoặc abc.ngrok-free.app): "
echo server.host=%new_ip% > config.properties
echo server.port=8080 >> config.properties
echo [OK] Da luu cau hinh vao config.properties!
echo.
pause
cls
goto :full_start

:client_only
echo.
echo [1/1] Dang khoi dong Giao dien...
mvnw.cmd javafx:run
goto :end

:full_start
echo.
echo [1/2] Dang khoi dong Backend Server (Cua so moi)...
start "AuctionWeb Backend" cmd /c "mvnw.cmd spring-boot:run"

echo.
echo Dang doi Backend khoi tao (10 giay)...
timeout /t 10 /nobreak >nul

echo.
echo [2/2] Dang khoi dong Giao dien...
mvnw.cmd javafx:run
goto :end

:end
echo.
echo ========================================
echo   Ung dung da dong.
echo ========================================
pause
