#!/bin/bash
set -e

cd "$(dirname "$0")"

MODE="${1:-client}"

case "$MODE" in
  server)
    ./mvnw.cmd spring-boot:run
    ;;
  signin|client)
    ./mvnw.cmd -Djavafx.mainClass=frontend.signin.Signin javafx:run
    ;;
  dashboard)
    ./mvnw.cmd -Djavafx.mainClass=frontend.dashboard.Dashboard javafx:run
    ;;
  *)
    echo "Usage: ./run.sh [server|signin|dashboard]"
    exit 1
    ;;
esac
