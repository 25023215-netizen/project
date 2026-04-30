@echo off
cd /d "%~dp0"
mvnw.cmd "-Djavafx.mainClass=frontend.signin.Signin" javafx:run
