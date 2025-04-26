@echo off
echo Starting Gestion Medicaments Backend with MySQL...
echo.
echo Prerequisites:
echo 1. MySQL should be running on localhost:3306
echo 2. Database 'wajj' should exist or be allowed to be created
echo 3. Make sure Java is installed (version 17+)
echo.

echo This script will try to run the application using MySQL.
echo.

echo Press any key to continue...
pause

echo Trying to run the Spring Boot application...
echo.

set CLASSPATH=target\classes;src\main\resources
set MAIN_CLASS=com.gestion.medicaments.MedicamentsApplication

java -cp %CLASSPATH% %MAIN_CLASS%

if %ERRORLEVEL% NEQ 0 (
  echo.
  echo Error running application. Please check Java installation, MySQL connection, and classpath.
  pause
) else (
  echo.
  echo Application is running successfully. Press Ctrl+C to stop.
) 