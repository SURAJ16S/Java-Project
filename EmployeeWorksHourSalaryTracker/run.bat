@echo off
echo Building the project...
call mvn clean install

echo Copying dependencies...
call mvn dependency:copy-dependencies -DoutputDirectory=lib

echo Running the application...
java -cp "target/classes;lib/*" com.company.main.Main

pause 