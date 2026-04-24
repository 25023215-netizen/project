@echo off
echo ====================================================
echo   DANG KHOI DONG HE THONG DAU GIA (BACKEND + FRONTEND)
echo ====================================================

:: Khoi dong Server trong cua so moi
echo 1. Dang khoi dong Backend (Spring Boot)...
start "Auction Backend" cmd /c "mvnw.cmd spring-boot:run"

:: Cho mot lat de Server khoi dong
echo 2. Dang cho Server san sang (khoang 15 giay)...
timeout /t 15 /nobreak > nul

:: Khoi dong Client trong cua so moi
echo 3. Dang khoi dong Frontend (JavaFX)...
start "Auction Frontend" cmd /c "mvnw.cmd javafx:run"

echo ====================================================
echo   HE THONG DANG CHAY. VUI LONG KIEM TRA CAC CUA SO MOI.
echo ====================================================
pause
