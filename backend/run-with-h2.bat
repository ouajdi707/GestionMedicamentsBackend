@echo off
echo Starting Gestion Medicaments Backend with H2 Database...
echo.
echo This script will start the application with an in-memory H2 database.
echo.

echo Make sure you have Java installed (version 17+)
echo.

set H2_PROPS=-Dspring.datasource.url=jdbc:h2:mem:medicamentsdb 
set H2_PROPS=%H2_PROPS% -Dspring.datasource.driverClassName=org.h2.Driver 
set H2_PROPS=%H2_PROPS% -Dspring.datasource.username=sa 
set H2_PROPS=%H2_PROPS% -Dspring.datasource.password=password 
set H2_PROPS=%H2_PROPS% -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect 
set H2_PROPS=%H2_PROPS% -Dspring.h2.console.enabled=true 
set H2_PROPS=%H2_PROPS% -Dspring.h2.console.path=/h2-console
set H2_PROPS=%H2_PROPS% -Dspring.jpa.hibernate.ddl-auto=update
set H2_PROPS=%H2_PROPS% -Dspring.mail.host=localhost

set CLASSPATH=target\classes
set MAIN_CLASS=com.gestion.medicaments.MedicamentsApplication

echo Trying to run the Spring Boot application with H2...
echo.

java %H2_PROPS% -cp %CLASSPATH% %MAIN_CLASS%

if %ERRORLEVEL% NEQ 0 (
  echo.
  echo Error running application. Please check Java installation and classpath.
  pause
) else (
  echo.
  echo Application is running. Press Ctrl+C to stop.
) 