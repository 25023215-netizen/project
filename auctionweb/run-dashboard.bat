@echo off
cd /d "%~dp0"
mvnw.cmd "-Djavafx.mainClass=frontend.dashboard.Dashboard" javafx:run
