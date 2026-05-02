#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "========================================"
echo "  Starting AuctionWeb Application..."
echo "========================================"
echo ""

echo "[1/2] Starting Backend Server in background..."
./mvnw spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

echo "Waiting 10 seconds for backend to initialize..."
sleep 10

echo "[2/2] Starting JavaFX Client..."
./mvnw javafx:run

echo ""
echo "========================================"
echo "  Client closed. Terminating backend..."
echo "========================================"
kill $BACKEND_PID
