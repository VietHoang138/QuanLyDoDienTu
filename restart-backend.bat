@echo off
title Spring Boot Backend - Shop Do Dien Tu
echo ===================================================
echo             KHOI DONG BACKEND SPRING BOOT
echo ===================================================

set "JAVA_HOME=D:\download\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;D:\download\apache-maven-3.9.12-bin\apache-maven-3.9.12\bin;%PATH%"

echo.
echo [1/3] Giai phong port 8080 va tat tien trinh Java cu...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /F /PID %%a >nul 2>&1
taskkill /F /IM java.exe >nul 2>&1
timeout /t 1 >nul

echo [2/3] Chuyen vao thu muc java...
cd /d "%~dp0java"

echo [3/3] Dang khoi dong Spring Boot (mvn spring-boot:run)...
echo       Toan bo log khoi dong se hien ben duoi:
echo ===================================================
echo.
call mvn.cmd spring-boot:run
pause