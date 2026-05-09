@echo off
echo ============================================
echo  University Automation System - Build
echo ============================================

if not exist out mkdir out

echo Compiling Java sources...
javac --release 11 -d out src\*.java

if errorlevel 1 (
    echo.
    echo BUILD FAILED. Please check errors above.
    pause
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL!
echo.
echo Running application...
java -cp out UniversityAutomationApp
