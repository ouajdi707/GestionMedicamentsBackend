@echo off
echo Starting Gestion Medicaments Backend...
echo.
echo Prerequisites:
echo 1. MySQL should be running on localhost:3306
echo 2. Make sure Java is installed and configured
echo.

echo Checking if MySQL is accessible...
echo To continue, make sure MySQL is running and press any key...
pause

echo.
echo Trying to compile the Spring Boot application...
echo.

cd /d "%~dp0"

if exist target\medicaments-0.0.1-SNAPSHOT.jar (
    echo Found compiled JAR. Running the application...
    java -jar target\medicaments-0.0.1-SNAPSHOT.jar
) else (
    echo No compiled JAR found. Please make sure to build the project first.
    echo You can build it using: mvn clean package
    echo.
    echo Press any key to exit...
    pause
) 