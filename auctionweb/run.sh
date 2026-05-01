#!/bin/bash
set -e

cd "$(dirname "$0")"

MODE="${1:-client}"

case "$MODE" in
  server)
    ./mvnw.cmd spring-boot:run
    ;;
  signin|client)
    ./mvnw.cmd -Psignin javafx:run
    ;;
  dashboard)
    ./mvnw.cmd -Pdashboard javafx:run
    ;;
  signup)
    ./mvnw.cmd -Psignup javafx:run
    ;;
  auction-list)
    ./mvnw.cmd -Pauction-list javafx:run
    ;;
  *)
    echo "Usage: ./run.sh [server|signin|dashboard|signup|auction-list]"
    exit 1
    ;;
esac
