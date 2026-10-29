@echo off
title Spring Boot Backend - 8080
echo ===================================================
echo             KHOI DONG BACKEND SPRING BOOT
echo ===================================================

set "JAVA_HOME=D:\download\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;D:\download\apache-maven-3.9.12-bin\apache-maven-3.9.12\bin;%PATH%"

echo [1/2] Tat tien trinh Java / Spring Boot cu...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /F /PID %%a >nul 2>&1
timeout /t 1 >nul

echo [2/2] Khoi dong Spring Boot...
echo ===================================================
echo.
mvn.cmd spring-boot:run
pause
