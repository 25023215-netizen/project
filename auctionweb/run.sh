#!/bin/bash

# AuctionWeb - Startup Script for Linux/macOS
# Script nay tu dong chay Backend Server va Giao dien JavaFX

echo "========================================"
echo "  AuctionWeb - He thong Dau gia Online"
echo "========================================"
echo ""

# Cap quyen thuc thi cho mvnw neu can
chmod +x mvnw

# Tao file cau hinh mac dinh neu chua co
if [ ! -f config.properties ]; then
    echo "[INFO] Tao file cau hinh mac dinh..."
    echo "server.host=localhost" > config.properties
    echo "server.port=8080" >> config.properties
fi

echo "[1/2] Dang khoi dong Backend Server (Trong nen)..."
./mvnw spring-boot:run -DskipTests > server_log.txt 2>&1 &
SERVER_PID=$!

echo "Dang doi Backend khoi tao (10 giay)..."
sleep 10

echo ""
echo "[2/2] Dang khoi dong Giao dien..."
./mvnw javafx:run -DskipTests

# Khi tat giao dien, tat luon server
echo "Dang dung Backend Server..."
kill $SERVER_PID


echo ""
echo "========================================"
echo "  Ung dung da dong."
echo "========================================"
