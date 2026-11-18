@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out grafos\*.java
if errorlevel 1 exit /b 1
java -cp out grafos.SelfTest
if errorlevel 1 exit /b 1
java -cp out grafos.StressTest
if errorlevel 1 exit /b 1
